/*
 * Servlets.java
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


package com.labfire.fe.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.labfire.fe.FireEngineContext;
import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.auth.AuthenticationService;

/**
 * Servlets
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class Servlets {

	/**
	 * Pull an AuthToken out from a request. Actually, the AuthToken
	 * is stored in the session, but in some cases it's easier on the user to
	 * just pass in the request object, which holds a reference to / can create
	 * the session. 
	 * 
	 * @param HttpServletRequest servlet request
	 * @return AuthToken not guaranteed to be non-null or valid
	 * @see com.labfire.fe.auth.AuthToken
	 * @see com.labfire.fe.auth.AuthenticationService#isValidToken(AuthToken)
	 */
	public static AuthToken getAuthToken(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		return (AuthToken)session.getAttribute(AuthenticationService.getSessionAttributeName());
	}
	
	public static FireEngineContext getFireEngineContext(ServletContext context) {
		return (FireEngineContext)context.getAttribute("FireEngineContext");
	}
	
	public static FireEngineContext getFireEngineContext(HttpServletRequest request) {
		return getFireEngineContext(request.getSession().getServletContext());
	}
}