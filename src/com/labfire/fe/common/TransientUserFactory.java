/*
 * TransientUserFactory.java
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


package com.labfire.fe.common;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.auth.AuthenticationService;
import com.labfire.fe.auth.OrgAuthenticationService;
import com.labfire.fe.template.TemplateFactory;
import com.labfire.fe.util.Servlets;

/**
 * TransientUserFactory
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class TransientUserFactory implements TemplateFactory {

	/**
	 * getInstance
	 */
	public TransientUser getInstance(AuthToken at, int userID) throws InitializeException {
		TransientUser u = new TransientUser();
		try {
			Organization o;
			ResultSet rs = AuthenticationService.select(at, userID);
			rs.next();
			u.setUserLoginAllowed(rs.getBoolean("UserLoginAllowed"));
			u.setUserID(rs.getInt("UserID"));
			u.setUserAID(rs.getInt("UserAccess"));
			u.setUserName(rs.getString("UserName"));
			u.setUserPass(AuthenticationService.getUserPass(at, userID));
			u.setUserEmail(rs.getString("UserEmail"));
			u.setUserFirstName(rs.getString("UserFirstName"));
			u.setUserLastName(rs.getString("UserLastName"));
			u.setUserDateCreated(rs.getTimestamp("UserDateCreated"));
			u.setUserDateModified(rs.getTimestamp("UserDateModified"));
			u.setUserLastLoggedIn(rs.getTimestamp("UserLastLoggedIn"));
			List oids = new LinkedList();
			ResultSet rs0 = OrgAuthenticationService.selectByUser(at, userID);
			if (rs0 != null) {
				while (rs0.next()) {
					o = OrgAuthenticationService.getOrganization(rs0.getInt("OrgID"));
					if (o != null) {
						oids.add(new Integer(o.getOrgID()));
					}
				}
				rs0.close();
			}
			u.setUserOIDs(oids);
			u.lock();
			rs.close();
			return u;
		} catch (Exception e) {
			throw new InitializeException("Unable to initialize TransientUser", e);
		}
	}
	
	/**
	 * getInstance
	 */
	public TransientUser getInstance(AuthToken at, String userName) throws InitializeException {
		TransientUser u = new TransientUser();
		try {
			Organization o;
			ResultSet rs = AuthenticationService.select(at, userName);
			rs.next();
			u.setUserLoginAllowed(rs.getBoolean("UserLoginAllowed"));
			u.setUserID(rs.getInt("UserID"));
			u.setUserAID(rs.getInt("UserAccess"));
			u.setUserName(rs.getString("UserName"));
			u.setUserPass(AuthenticationService.getUserPass(at, u.getUserID()));
			u.setUserEmail(rs.getString("UserEmail"));
			u.setUserFirstName(rs.getString("UserFirstName"));
			u.setUserLastName(rs.getString("UserLastName"));
			u.setUserDateCreated(rs.getTimestamp("UserDateCreated"));
			u.setUserDateModified(rs.getTimestamp("UserDateModified"));
			u.setUserLastLoggedIn(rs.getTimestamp("UserLastLoggedIn"));
			List oids = new LinkedList();
			ResultSet rs0 = OrgAuthenticationService.selectByUser(at, u.getUserID());
			if (rs0 != null) {
				while (rs0.next()) {
					o = OrgAuthenticationService.getOrganization(rs0.getInt("OrgID"));
					if (o != null) {
						oids.add(new Integer(o.getOrgID()));
					}
				}
				rs0.close();
			}
			u.setUserOIDs(oids);
			u.lock();
			rs.close();
			return u;
		} catch (Exception e) {
			throw new InitializeException("Unable to initialize TransientUser", e);
		}
	}
	
	/**
	 * getInstance
	 */
	public Object getInstance(HttpServletRequest request, HttpServletResponse response) throws InitializeException {
		try {
			try {
				int id = Integer.parseInt(request.getParameter(TransientUserFactory.class.getName()));
				return getInstance(Servlets.getAuthToken(request), id);
			} catch (NumberFormatException nfe) {
				return getInstance(Servlets.getAuthToken(request), request.getParameter(TransientUserFactory.class.getName()));		
			}
		} catch (Exception e) {
			throw new InitializeException("Unable to initialize TransientUser due to missing request parameter", e);
		}
		
	}
}
