/*
 * UserFactory.java
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
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.auth.AuthenticationService;
import com.labfire.fe.template.TemplateFactory;
import com.labfire.fe.util.Servlets;

/**
 * UserFactory
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class UserFactory implements TemplateFactory {
	
	/**
	 * getInstance
	 *
	 * @return Object
	 */
	public User getInstance(AuthToken at, int userID) throws InitializeException {
		User u = new User();
		try {
			ResultSet rs = AuthenticationService.select(at, userID);
			rs.next();
			u.setUserLoginAllowed(rs.getBoolean("UserLoginAllowed"));
			u.setUserName(rs.getString("UserName"));
			u.setUserPass(AuthenticationService.getUserPass(at, userID));
			u.setUserEmail(rs.getString("UserEmail"));
			u.setUserFirstName(rs.getString("UserFirstName"));
			u.setUserLastName(rs.getString("UserLastName"));
			u.setUserDateCreated(rs.getTimestamp("UserDateCreated"));
			u.setUserDateModified(rs.getTimestamp("UserDateModified"));
			u.setUserLastLoggedIn(new Timestamp((new java.util.Date()).getTime()));
			u.lock();
			rs.close();
			return u;
		} catch (Exception e) {
			throw new InitializeException("Unable to initialize User", e);
		}
	}
	
	/**
	 * getInstance
	 *
	 * @return Object
	 */
	public Object getInstance(HttpServletRequest request, HttpServletResponse response) throws InitializeException {
		int id;
		try {
			id = Integer.parseInt(request.getParameter(UserFactory.class.getName()));
		} catch (Exception e) {
			throw new InitializeException("Unable to initialize User due to missing request parameter");
		}
		return getInstance(Servlets.getAuthToken(request), id);
	}

}
