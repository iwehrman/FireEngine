/*
 * AuthenticationDeleteServlet.java
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

import javax.servlet.http.HttpServletRequest;

import com.labfire.fe.util.UpdateException;
import com.labfire.fe.util.UpdateServlet;

/**
 * AuthenticationDeleteServlet
 * 
 * @author <a href="http://labfire.com">Labfire, Inc.</a>
 */
public class AuthenticationDeleteServlet extends UpdateServlet {

	/**
	 * processForm
	 * 
	 * @return int
	 */
	public int processForm(AuthToken at, HttpServletRequest request) throws UpdateException {
		try {
			return AuthenticationService.delete(at, Integer.parseInt(request.getParameter("id")));
		} catch (Exception e) {
			throw new UpdateException("Unable to delete user", e);
		}
	}
}
