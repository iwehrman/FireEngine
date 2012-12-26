/*
 * ConnectionService.java
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


package com.labfire.fe.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jdom.Element;

import com.labfire.fe.FireEngineComponent;
import com.labfire.fe.FireEngineConfig;
import com.labfire.fe.auth.AccessAuthenticationService;
import com.labfire.fe.auth.AuthException;
import com.labfire.fe.auth.AuthLevel;
import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.auth.AuthenticationException;
import com.labfire.fe.auth.AuthenticationService;
import com.labfire.fe.auth.AuthorizationService;
import com.labfire.fe.auth.Constraint;
import com.labfire.fe.auth.OrgAuthenticationService;
import com.labfire.fe.cache.CacheException;
import com.labfire.fe.cache.CacheService;
import com.labfire.fe.cache.CachedObject;
import com.labfire.fe.common.InitializeException;
import com.labfire.fe.common.User;
import com.labfire.fe.cron.CronService;
import com.labfire.fe.cron.SimpleCronEntry;
import com.labfire.fe.log.BootLogger;
import com.labfire.fe.log.LogService;
import com.labfire.fe.util.DirectedGraph;
import com.labfire.fe.util.NoSuchVertexException;

/**
 * ConnectionService
 * The ConnectionService is used by business-level code to set up and get database
 * connections.
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public final class ConnectionService extends FireEngineComponent {
	private static final String DB_PGSQL = "PostgreSQL";
	private static final String DB_SAPDB = "SAP DB";
	
	private static String systemUrl;
	private static String systemUser;
	private static String systemConnectionName;
	private static Map constraints = new HashMap();
	private static Map nameToUrl = new HashMap();
	private static Map urlUserToName = new HashMap();
	private static Map nameToUser = new HashMap();
	private static Map nameToType = new HashMap();
	private static Map urlToGraph = new HashMap();
	private static Set sweepers = new HashSet();
	private static AuthToken serviceAuthToken;
	
	/**
	 * ConnectionService
	 */
	public ConnectionService(FireEngineConfig config) throws InitializeException {
		super(config);
		serviceAuthToken = config.getServiceAuthToken();
		Element current;
		Element serviceElement = config.getConfigElement();
		Element sysConnElement = serviceElement.getChild("SystemConnection");
		
		if (sysConnElement == null) {
			throw new InitializeException("SystemConnection element not found");
		}
		
		Properties systemConnProps = buildConnProperties(sysConnElement);
		
		// setup the constraint for the system database connection
		Constraint constraint = new Constraint();
		constraint.setOID(new Integer(OrgAuthenticationService.SYSTEM_OID));
		constraint.setID(new Integer(AuthenticationService.SYSTEM_ID));

		try {
			register(systemConnProps, constraint);
			systemConnectionName = systemConnProps.getProperty("db.name");
		} catch(SQLException sqle) {
			throw new InitializeException("Unable to create initial system connection pool", sqle);
		}
		
		try {
			Properties p;
			List connectionsList = serviceElement.getChildren("Connection");
			for (Iterator i = connectionsList.iterator(); i.hasNext(); ){
				current = (Element)i.next();
				p = buildConnProperties(current);
				constraint = buildConnConstraint(current);
				register(p, constraint);
			}
		} catch (SQLException sqle) {
			throw new InitializeException("Unable to create connection pool", sqle);
		} catch (NumberFormatException nfe){
			throw new InitializeException("Error reading Constraint", nfe);
		}
	}
	
	/**
	 * executed after a FireEngineRepository's Services are
	 * initialized. ConnectionService adds cron entries to sweep
	 * each database nightly
	 *
	 * @throws InitializeException if an error is encountered during postCreate
	 */
	protected void postCreate() throws InitializeException {
		try {
			String name;
			SimpleCronEntry sce;
			Calendar cal = Calendar.getInstance();
			cal.setTime(new java.util.Date());
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.roll(Calendar.DAY_OF_YEAR, 1);
			Iterator i = urlUserToName.values().iterator();
			while (i.hasNext()) {
				name = (String)i.next();
				Connection cn = getConnection(name);
				DatabaseMetaData dbmd = cn.getMetaData();
				if (dbmd.getDatabaseProductName().equals(DB_PGSQL)) {
					sce = new SimpleCronEntry(new PgSweeper(name), cal.getTime(), SimpleCronEntry.DAY);
					CronService.putCron(sce);
					sweepers.add(sce);
				}
			}
		} catch (Exception e) {
			throw new InitializeException("Unable to schedule database sweepers", e);
		}
	}
	
	protected void unload() {
		Map.Entry entry;
		ConnectionAuth ca;
		ConnectionPool pool;
		Iterator i = constraints.entrySet().iterator();
		while (i.hasNext()) {
			entry = (Map.Entry) i.next();
			ca = (ConnectionAuth) entry.getValue();
			pool = ca.getPool();
			pool.closeAllConnections();
		}
		constraints.clear();
		sweepers.clear();
		nameToUrl.clear();
		nameToUser.clear();
		urlToGraph.clear();
		urlUserToName.clear();
	}
	
	/**
	 * register
	 */
	private void register(Properties p, Constraint c) throws SQLException {
		boolean waitIfBusy = Boolean.valueOf(p.getProperty("db.wait")).booleanValue();
		int maxConnections = 10;
		int initConnections = 5;
		String name = p.getProperty("db.name");
		String user = p.getProperty("db.user");
		String password = p.getProperty("db.password");
		String url = p.getProperty("db.url");
		String driver = p.getProperty("db.driver");
		ConnectionPool pool = null;
		Connection cn = null;
		DatabaseMetaData dbmd;
		
		try {
			maxConnections = Integer.parseInt(p.getProperty("db.max"));
			initConnections = Integer.parseInt(p.getProperty("db.init"));
		} catch (NumberFormatException nfe) {
			BootLogger.log("Unable to set init and max connection properties for resource " + name, nfe);
		}
		
		try {
			if ((name != null) && (url != null) && (driver != null) && (user != null)) {
				pool = new ConnectionPool(driver, url, user, password, initConnections, maxConnections, waitIfBusy);
				ConnectionAuth ca = new ConnectionAuth(pool);
				ca.setConstraint(c);
				if (nameToUrl.get(name) == null) {
					if (urlToGraph.get(url) == null) {
						DirectedGraph deps = new DirectedGraph();
						new Thread(new DependencyGrapher(pool, deps)).start();
						urlToGraph.put(url, deps);
						cn = pool.getConnection();
						dbmd = cn.getMetaData();
						nameToType.put(name, dbmd.getDatabaseProductName());
					}
					urlUserToName.put(url+user, name);
					nameToUrl.put(name, url);
					nameToUser.put(name, user);
					constraints.put(url+user, ca);
					//BootLogger.logDebug("ConnectionService.register: Registered connection for url " + url);
				}
			}
		} catch (SQLException sqle) {
			BootLogger.log("Unable to create connection pool for resource " + name, sqle);
			throw sqle;
		} finally {
			if (pool != null && cn != null) {
				pool.free(cn);
			}
		}
	}

	/**
	 * buildConnProperties
	 *
	 * @return Properties
	 */
	private static Properties buildConnProperties(Element e) {
		try {
			Properties p = new Properties();
			p.setProperty("db.name", e.getAttributeValue("name"));
			p.setProperty("db.user",e.getAttributeValue("user"));
			p.setProperty("db.password", e.getAttributeValue("password"));
			p.setProperty("db.url", e.getAttributeValue("url"));
			p.setProperty("db.driver", e.getAttributeValue("driver"));
			p.setProperty("db.max", e.getAttributeValue("maxConnections"));
			p.setProperty("db.init", e.getAttributeValue("initConnections"));
			p.setProperty("db.wait", e.getAttributeValue("waitIfBusy"));
			return p;
		} catch (NullPointerException npe) {
			return null;
		}
	}
	
	/**
	 * buildConnConstraint
	 *
	 * @return ConnectionConstraint
	 */
	private static Constraint buildConnConstraint(Element e) throws NumberFormatException {
		Constraint cc = new Constraint();
		String oid;
		String aid;
		String id;
		Element constElement = e.getChild("Constraint");
		if (constElement != null) {
			oid = e.getAttributeValue("oid");
			aid = e.getAttributeValue("aid");
			id  = e.getAttributeValue("id");
			if (oid != null) {
				cc.setOID(new Integer(oid));
			}
			if (aid != null) {
				cc.setAID(new Integer(aid));
			}
			if (id != null) {
				cc.setID(new Integer(id));
			}
		}
		return cc;
	}

	/**
	 * getConnection
	 * 
	 * @return Connection
	 * @throws SQLException if a connection could not be created
	 */
	public static Connection getConnection(AuthToken at, String name) throws SQLException, AuthException {
		String key = (String)nameToUrl.get(name) + (String)nameToUser.get(name);
		ConnectionAuth ca = (ConnectionAuth)constraints.get(key);
		if (ca == null) {
			throw new AuthException("No ConnectionAuth found for: " + name);
		}
		if (AuthorizationService.isAuthorized(at, ca.getConstraint()) ||
			(serviceAuthToken.equals(at) && systemConnectionName.equals(name))) {
			ConnectionPool pool = ca.getPool();
			if (pool == null) {
				throw new AuthException("No ConnectionPool found for: " + name);
			}
			return pool.getConnection();
		} else {
			throw new AuthException("Failed to acquire connection for: " + name);
		}
	}
	
	/**
	 * getConnection
	 * 
	 * @return Connection
	 */
	public static Connection getConnection(AuthToken at) throws SQLException, AuthException {
		return getConnection(at, systemConnectionName);
	}
	
	/**
	 * getConnection
	 *
	 * @return Connection
	 */
	static Connection getConnection(String name) throws SQLException, AuthException {
		String key = (String)nameToUrl.get(name) + (String)nameToUser.get(name);
		ConnectionAuth ca = (ConnectionAuth)constraints.get(key);
		if (ca == null) {
			throw new AuthException("No ConnectionAuth found for: " + name);
		}
		ConnectionPool pool = ca.getPool();
		if (pool == null) {
			throw new AuthException("No ConnectionPool found for: " + name);
		}
		return pool.getConnection();
	}

	/**
	 * returnConnection
	 *
	 * @param inConnection - the Connection to close
	 */
	public static void returnConnection(Connection inConnection) throws SQLException {
		((ConnectionAuth)constraints.get(inConnection.getMetaData().getURL()
				+ inConnection.getMetaData().getUserName())).getPool().free(inConnection);
	}
	
	public static long getLastModified(Dependency dep) {
		return getLastModified(dep.getConnectionName(), dep.getTableName());
	}
	
	public static long getLastModified(String name, String table) {
		Object url = nameToUrl.get(name);
		DirectedGraph deps = (DirectedGraph)urlToGraph.get(url);
		long time = System.currentTimeMillis();
		if (deps != null) {
			try {
				time = getLastModified(deps, table.toUpperCase());
			} catch (NoSuchVertexException nsve) {
				LogService.logError("Unable to find dependency for Connection " + name, nsve);
			}
		} else {
			LogService.logWarn("Unable to find dependency graph for Connection " + name);
		}
		return time;
	}
	
	public static long getLastModified(Set deps) {
		Dependency d;
		if (deps != null) {
			Iterator i = deps.iterator();
			long time = 0;
			while (i.hasNext()) {
				d = (Dependency)i.next();
				time = Math.max(time, getLastModifiedFromUrl(d.getConnectionUrl(), d.getTableName()));
			}
			if (time == 0) {
				return System.currentTimeMillis();
			} else {
				return time;
			}
		}
		return System.currentTimeMillis();
	}
	
	public static long getLastModifiedFromUrl(String url, String table) {
		DirectedGraph deps = (DirectedGraph)urlToGraph.get(url);
		long time = System.currentTimeMillis();
		if (deps != null) {
			try {
				time = getLastModified(deps, table.toUpperCase());
			} catch (NoSuchVertexException nsve) {
				LogService.logError("Unable to find dependency for Connection " + url, nsve);
			}
		} else {
			LogService.logWarn("Unable to find dependency graph for Connection " + url);
		}
		return time;
	}
	
	private static long getLastModified(DirectedGraph deps, Object key) throws NoSuchVertexException {
		synchronized (deps) {
			return ((java.util.Date)deps.getVertexData(key)).getTime();
		}
	}
	
	public static void setLastModified(String name, String table) {
		Object url = nameToUrl.get(name);
		DirectedGraph deps = (DirectedGraph)urlToGraph.get(url);
		if (deps != null) {
			try {
				setLastModified(deps, table.toUpperCase(), System.currentTimeMillis());
			} catch (NoSuchVertexException nsve) {
				LogService.logError("Unable to update dependency graph for Connection " + url, nsve);
			}
		} else {
			LogService.logWarn("Unable to find dependency graph for Connection " + url);
		}
	}
	
	public static void setLastModifiedFromUrl(String url, String table) {
		DirectedGraph deps = (DirectedGraph)urlToGraph.get(url);
		if (deps != null) {
			try {
				setLastModified(deps, table.toUpperCase(), System.currentTimeMillis());
			} catch (NoSuchVertexException nsve) {
				LogService.logError("Unable to update dependency graph for Connection " + url, nsve);
			}
		} else {
			LogService.logWarn("Unable to find dependency graph for Connection " + url);
		}
	}
	
	private static void setLastModified(DirectedGraph deps, Object key, long time) throws NoSuchVertexException {
		synchronized (deps) {
			((java.util.Date)deps.getVertexData(key)).setTime(time);
			Iterator i = deps.neighborKeys(key).iterator();
			while (i.hasNext()) {
				setLastModified(deps, i.next(), time);
			}
		}
	}
	
	/**
	 * getSystemConnectionName
	 *
	 * @return String Connection name for the FireEngine database
	 */
	public static String getSystemConnectionName() {
		return systemConnectionName;
	}
	
	/**
	 * getConnectionUrl
	 *
	 * @return String
	 */
	public static String getConnectionUrl(String name) {
		return (String)nameToUrl.get(name);
	}
	
	/**
	 * getConnectionUser
	 *
	 * @return String
	 */
	public static String getConnectionUser(String name) {
		return (String)nameToUser.get(name);
	}
	
	/**
	 * getConnectionType
	 *
	 * @return String
	 */
	public static String getConnectionType(String name) {
		return (String)nameToType.get(name);
	}
	
	/**
	 * getConnectionUrl
	 *
	 * @return String
	 */
	public static String getConnectionName(String url, String user) {
		return (String)urlUserToName.get(url+user);
	}

	/**
	 * select
	 * 
	 * @return Map
	 */
	public static Map select(AuthToken at) throws AuthException {
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
			// admins can view all cache objects
			return Collections.unmodifiableMap(constraints);
		} else {
			// no one else can view anything
			return null;
		}
	}
	
	/**
	 * getStatus
	 *
	 * @return String
	 */
	public String getStatus() {
		return "ConnectionService: pools=" + constraints.size();
	}
	
	/**
	 * finalize
	 */
	protected void finalize() {
		Iterator itr = constraints.values().iterator();
		while (itr.hasNext()) {
			((ConnectionAuth)itr.next()).getPool().closeAllConnections();
		}
		constraints.clear();
	}
}
