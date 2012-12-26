/*
 * AuthorizationService.java
 * 
 * Copyright (c) 2001-2002 Labfire, Inc. All rights reserved.
 * Visit http://labfire.com/ for more information. 
 * 
 * This software is the confidential and proprietary information of
 * Labfire, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Labfire.
 */


package com.labfire.fe.auth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import com.labfire.fe.FireEngineComponent;
import com.labfire.fe.FireEngineConfig;
import com.labfire.fe.cache.CacheException;
import com.labfire.fe.cache.CacheService;
import com.labfire.fe.cache.CachedObject;
import com.labfire.fe.common.InitializeException;
import com.labfire.fe.common.Organization;
import com.labfire.fe.common.TransientUser;
import com.labfire.fe.common.User;
import com.labfire.fe.log.LogService;
import com.labfire.fe.util.Connections;
import com.labfire.fe.util.Strings;

/**
 * AuthorizationService
 * The AuthorizationService's primary function is to determine
 * whether or not a particular user is allowed access to a particular
 * resource. The AuthorizationService holds a reference to a ConstraintTree,
 * which mirrors parts of the filesystem which have access constraints placed
 * upon them. Constraints are created based on information found in the system
 * database, and added to the tree at startup. Constraints may also be dynamically
 * added or cleared at runtime. 
 * 
 * @author <a href = "mailto:ian@labfire.com">Ian Wehrman</a>
 * @see com.labfire.fe.auth.Constraint
 */
public final class AuthorizationService extends FireEngineComponent {
	private static ConstraintTree ct;
	private static int secondsToLive = 1800;
	private static RequestAuthFactory raFactory = new RequestAuthFactory();
	private static ConstraintFactory cFactory = new ConstraintFactory();
	
	static AuthToken serviceAuthToken;

	/**
	 * AuthorizationService
	 * Create and initialize the AuthorizationService, largely consisting
	 * of reading constraints from the system database and adding them to
	 * the ConstraintTree.
	 * 
	 * @param inConfigFileName properties file for AuthorizationService class
	 * @throws InitializeException if there is a problem initializing the class and populating the ConstraintTree
	 */
 	public AuthorizationService(FireEngineConfig config) throws InitializeException {
  		super(config);
		serviceAuthToken = config.getServiceAuthToken();
		try {
			Constraint c;
			ConstraintNode cn = new ConstraintNode("/");
			ct = new ConstraintTree(cn);
			ResultSet rs = Connections.select(serviceAuthToken, 
				"SELECT * FROM Constraints,Requests WHERE ConstraintID=RequestConstraint", false);
			if (rs != null) {
				while (rs.next()){
					c = new Constraint();
					if (rs.getInt("cID") != 0) {
						c.setID(new Integer(rs.getInt("cID")));
					}
					if (rs.getInt("cOID") != 0) {
						c.setOID(new Integer(rs.getInt("cOID")));
					}
					if (rs.getInt("cAID") != 0) {
						c.setAID(new Integer(rs.getInt("cAID")));
					}
					cn = new ConstraintNode(rs.getString("RequestURI"));
					cn.addConstraint(c);
					ct.add(cn);
				}
				rs.close();
			}
		} catch (SQLException se) {
			InitializeException ie = new InitializeException("An SQLException has occurred.", se);
			LogService.logError(ie.getMessage(), se);
			throw ie;
		} catch (AuthException ae) {
			InitializeException ie = new InitializeException("An SQLException has occurred.", ae);
			LogService.logError(ie.getMessage(), ae);
			throw ie;
		}
	}
	
	/**
	 * isAuthorized
	 *
	 * @return boolean
	 */
	public static boolean isAuthorized(AuthToken at, Set s) {
		Object c;
		Iterator i = s.iterator();
		while (i.hasNext()) {
			c = i.next();
			if (c instanceof Constraint) {
				if (isAuthorized(at, (Constraint)c)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * isAuthorized
	 * Determines whether or not a particular eid allowed access to
	 * the resource they have requested. 
	 * 
	 * @param eid eid of the user making the request
	 * @param request URI string requested
	 * @return boolean
	 */
	public static boolean isAuthorized(AuthToken at, Constraint c) {
		boolean authorized = true;
	
		if (c == null) {
			return false;
		} else if (c.isNull()) {
			return authorized; // we have a constraint, but it's all nulls
		}
			
		try {
			User u = AuthenticationService.getUser(at);
			if (AccessAuthenticationService.findAID(u.getUserAccessToken()) == AuthLevel.ACCESS_ADMIN) {
				return true; // you're in
			} else if (u != null) {
				try {
					if (c.getAID() != null) {
						if (AccessAuthenticationService.findAID(u.getUserAccessToken()) < c.getAID().intValue()) {
							return false;
						}
					}
					if (c.getID() != null) {
						if (AuthenticationService.findID(u.getUserToken()) != c.getID().intValue()) {
							TransientUser tu = AuthenticationService.getTransientUser(at, c.getID().intValue());
							try {
								if (tu == null || AccessAuthenticationService.findAID(u.getUserAccessToken()) <= tu.getUserAID()) {
									return false;
								}
							} catch (AuthException ae) {
								LogService.logError("Caught AuthException while creating TransientUser.", ae);
								return false;								
							}
						}
					}
					if (c.getOID() != null) {
						Iterator i = u.getUserOrgTokens().iterator();
						Object orgAt;
						boolean matched = false;
						while (i.hasNext() && !matched) {
							orgAt = i.next();
							if (orgAt instanceof AuthToken && OrgAuthenticationService.findOID((AuthToken)orgAt) != c.getOID().intValue()) {
								try {
									Organization o = OrgAuthenticationService.getOrganization(c.getOID().intValue());
									if (o == null || AccessAuthenticationService.findAID(u.getUserAccessToken()) <= o.getOrgDefaultAID()) {
										authorized = false;
									} else {
										matched = true;
									}
								} catch (Exception e) {
									authorized = false;
								}
							} else {
								matched = true;
							}
						}
					}
				} catch (AuthenticationException ae) {
					return false;
				}
			}
		} catch (AuthenticationException ae) {
			return false;
		}
		return authorized;
	}
	
	/**
	 * authorizeRequest
	 * 
	 * @return boolean
	 */
	public static boolean authorizeRequest(AuthToken at, String request) {
		if (request == null) {
			return false;
		} else {
			return isAuthorized(at, ct.match(request));
		}
	}
	
	/**
	 * hasNullConstraint
	 *
	 * @return boolean
	 */
	public static boolean hasNullConstraint(String request) {
		if (request != null) {
			Set s = ct.match(request);
			if (s != null) {
				if (s.size() == 0) {
					return true;
				} else {
					Iterator i = s.iterator();
					while (i.hasNext()) {
						if (!((Constraint)i.next()).isNull()) {
							return false;
						}
					}
					return true;
				}
			} else {
				LogService.logError("Match returned a null constraint");
			}
		}
		return false;
	}
	
	/**
	 * select
	 * Select all constraints from the Constraint table, such that this user is allowed to view them.
	 * The userEID supplied is used to check the user's access level and organization,
	 * and only rows that the user is allowed to view are returned. For the Constraint table,
	 * admins can view all constraints, trusted users can view constraints for users with access level
	 * strictly less than their own and organizations with defaultaid less than their own, and 
	 * constraints for their userid and organization, and users can only view constraints in their own 
	 * organization, and for themselves.
	 * 
	 * @param eid int representing a userEID
	 * @return Rows selected from the Constraints table. If no rows were selected, the ResultSet may be null.
	 * @throws AuthenticationException - if unable to determine access level for the given userEID
	 */	
	public static ResultSet select(AuthToken at) throws SQLException,AuthException {
		String query = "SELECT Constraints.*,Users.UserName,Organizations.Orgname,Requests.* FROM " +
			"Requests,Constraints LEFT OUTER JOIN Organizations on (Constraints.COID = Organizations.OrgID) " +
			"LEFT OUTER JOIN Users on (Constraints.CID = Users.UserID) WHERE RequestConstraint=ConstraintID " +
			"ORDER BY RequestURI";
		
		try {
			CacheService.getCache(at);
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		return Connections.select(serviceAuthToken, query, false);
	}
	
	/**
	 * select
	 * Select a constraint from the Constraint table, such that this user is allowed to view it.
	 * The userEID supplied is used to check the user's access level and organization,
	 * and only rows that the user is allowed to view are returned. For the Constraint table,
	 * admins can view any constraint, trusted users can view constraints for users with access level
	 * strictly less than their own and organizations with defaultaid less than their own, and 
	 * constraints for their userid and organization, and users can only view constraints in their own 
	 * organization, and for themselves.
	 * 
	 * @param eid int representing a userEID
	 * @return Rows selected from the Constraints table. If no rows were selected, the ResultSet may be null.
	 * @throws AuthenticationException - if unable to determine access level for the given userEID
	 */		
	public static ResultSet select(AuthToken at, int key) throws SQLException,AuthException {
		String query = "SELECT Constraints.*,Users.UserName,Organizations.Orgname,Requests.* FROM " +
			"Requests,Constraints LEFT OUTER JOIN Organizations on (Constraints.COID = Organizations.OrgID) " +
			"LEFT OUTER JOIN Users on (Constraints.CID = Users.UserID) WHERE RequestConstraint=ConstraintID " +
			"AND ConstraintID=" + key + " ORDER BY RequestURI";
		
		try {
			CacheService.getCache(at);
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		return Connections.select(serviceAuthToken, query, false);
	}
	
	/**
	 * selectConstraints
	 *
	 * @throws AuthenticationException - if unable to determine access level for the given userEID
	 */		
	public static ResultSet selectConstraints(AuthToken at) throws SQLException,AuthException {
		String query = "SELECT * FROM Constraints ORDER BY ConstraintID";
		
		try {
			CacheService.getCache(at);
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		return Connections.select(serviceAuthToken, query, false);
	}
	
	/**
	 * selectConstraints
	 *
	 * @throws AuthenticationException - if unable to determine access level for the given userEID
	 */		
	public static ResultSet selectConstraints(AuthToken at, int key) throws SQLException,AuthException {
		String query = "SELECT * FROM Constraints WHERE ConstraintID=" + key + " ORDER BY ConstraintID";
		
		try {
			CacheService.getCache(at);
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		return Connections.select(serviceAuthToken, query, false);
	}
	
	/**
	 * selectRequests
	 *
	 * @throws AuthenticationException - if unable to determine access level for the given userEID
	 */		
	public static ResultSet selectRequests(AuthToken at) throws SQLException,AuthException {
		String query = "SELECT DISTINCT RequestURI FROM Requests ORDER BY RequestURI";
		
		try {
			CacheService.getCache(at);
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		return Connections.select(serviceAuthToken, query, false);
	}
	
	/**
	 * selectRequests
	 *
	 * @throws AuthenticationException - if unable to determine access level for the given userEID
	 */
	public static ResultSet selectRequests(AuthToken at, String key) throws SQLException,AuthException {
		String query = "SELECT Constraints.*,Users.UserName,Organizations.Orgname,Requests.* FROM " +
			"Requests,Constraints LEFT OUTER JOIN Organizations on (Constraints.COID = Organizations.OrgID) " +
			"LEFT OUTER JOIN Users on (Constraints.CID = Users.UserID) WHERE RequestConstraint=ConstraintID " +
			"AND RequestURI='" + key + "' ORDER BY RequestURI,RequestConstraint";
		
		try {
			CacheService.getCache(at);
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		return Connections.select(serviceAuthToken, query, false);
	}

	/**
	 * insertRequest
	 */	
	public static int insertRequest(AuthToken at, RequestAuth ra) throws SQLException,AuthException {
		User u = null;
		Constraint c;
		Iterator i;
		
		try {
			u = (User)((CachedObject)CacheService.getCache(at)).object;
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		
		int accessLevel = AccessAuthenticationService.findAID(u.getUserAccessToken());
		int id = AuthenticationService.findID(u.getUserToken());
		if (accessLevel == AuthLevel.ACCESS_ADMIN || id == AuthenticationService.SYSTEM_ID) {
			ra.setURI(Strings.escapeSQL(ra.getURI()));
			ConstraintNode cn = new ConstraintNode(ra.getURI());
			i = ra.getConstraints().iterator();
			while (i.hasNext()) {
				c = (Constraint)i.next();
				c.setConstraintID(addConstraint(at, c, ra.getURI()));
				cn.addConstraint(c);
			}
			ct.add(cn);
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * deleteRequest
	 */
	public static int deleteRequest(AuthToken at, String key) throws SQLException,AuthException {
		User u = null;
		Iterator i;
		Constraint c;
		RequestAuth ra;
		
		try {
			u = (User)((CachedObject)CacheService.getCache(at)).object;
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		
		int accessLevel = AccessAuthenticationService.findAID(u.getUserAccessToken());
		if (accessLevel == AuthLevel.ACCESS_ADMIN) {
			try {
				ra = raFactory.getInstance(at, key);
			} catch (InitializeException ie) {
				throw new AuthException("Unable to initialize RequestAuth", ie.getCause());
			}
			i = ra.getConstraints().iterator();
			while (i.hasNext()) {
				c = (Constraint)i.next();
				deleteConstraint(at, c.getConstraintID(), key);
			}
			ConstraintNode cn = new ConstraintNode(ra.getURI());
			ct.add(cn);
			return 1;
		} else {
			// users cannot delete constraints
			return 0;
		}
	}

	/**
	 * insertConstraint
	 */	
	private static int insertConstraint(AuthToken at, Constraint c) throws SQLException,AuthException {
		StringBuffer update = new StringBuffer();
		User u = null;
		
		try {
			u = (User)((CachedObject)CacheService.getCache(at)).object;
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		
		int accessLevel = AccessAuthenticationService.findAID(u.getUserAccessToken());
		int id = AuthenticationService.findID(u.getUserToken());
		if (accessLevel == AuthLevel.ACCESS_ADMIN || id == AuthenticationService.SYSTEM_ID) {
			// admins can update any constraint
			update.append("INSERT INTO Constraints (cID,cOID,cAID) VALUES (");
			if (c.getID() != null) {
				update.append(c.getID());
			} else { 
				update.append("null");
			}
			
			if (c.getOID() != null) {
				update.append(',');
				update.append(c.getOID());
			} else {
				update.append(",null");
			}
			
			if (c.getAID() != null) {
				update.append(',');
				update.append(c.getAID());
			} else {
				update.append(",null");
			}
			update.append(')');
			ResultSet rs = Connections.update(serviceAuthToken, update.toString());
			int retVal = 0;
			if (rs != null) {
				if (rs.next()) {
					retVal = rs.getInt("ConstraintID");
				}
				rs.close();
			}
			return retVal;
		} else {
			return 0;
		}
	}
	
	/**
	 * addConstraint
	 */
	public static int addConstraint(AuthToken at, Constraint c, String uri) throws SQLException,AuthException {
		String update;
		User u = null;
		
		try {
			u = (User)((CachedObject)CacheService.getCache(at)).object;
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		
		int accessLevel = AccessAuthenticationService.findAID(u.getUserAccessToken());
		int id = AuthenticationService.findID(u.getUserToken());
		if (accessLevel == AuthLevel.ACCESS_ADMIN || id == AuthenticationService.SYSTEM_ID) {
			c.setConstraintID(insertConstraint(at, c));
			update = "INSERT INTO Requests (RequestURI,RequestConstraint) VALUES ('" +
				uri + "'," + c.getConstraintID() + ")";
			ResultSet rs = Connections.update(serviceAuthToken, update);
			if (rs != null) {
				rs.close();
			}
			ConstraintNode cn = ct.find(uri);
			cn.getConstraints().add(c);
			return c.getConstraintID();
		} else {
			return 0;
		}
	}
	
	/**
	 * deleteConstraint
	 */
	public static int deleteConstraint(AuthToken at, int key, String uri) throws SQLException,AuthException {
		String update;
		User u = null;
		
		try {
			u = (User)((CachedObject)CacheService.getCache(at)).object;
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		
		int accessLevel = AccessAuthenticationService.findAID(u.getUserAccessToken());
		if (accessLevel == AuthLevel.ACCESS_ADMIN) {
			try {
				cFactory.getInstance(at, key);
			} catch (InitializeException ie) {
				throw new AuthException("Unable to initialize Constraint", ie.getCause());
			}
			update = "DELETE FROM Requests WHERE RequestConstraint=" + key;
			ResultSet rs = Connections.update(serviceAuthToken, update);
			if (rs != null) {
				rs.close();
			}
			update = "DELETE FROM Constraints WHERE ConstraintID=" + key;
			ct.add(new ConstraintNode(uri));
			rs = Connections.update(serviceAuthToken, update);
			if (rs != null) {
				rs.close();
			}
			return 1;
		} else {
			// users cannot delete constraints
			return 0;
		}
	}

	/**
	 * getStatus
	 * 
	 * @return String
	 */
	public String getStatus() {
		return "AuthorizationService: nodes=" + ct.size();
	}
}
