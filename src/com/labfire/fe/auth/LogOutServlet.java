/*
 * LogOutServlet.java
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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.FireEngineContext;
import com.labfire.fe.util.Servlets;

/**
 * LogOutServlet
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class LogOutServlet extends HttpServlet {

	/**
	 * doGet
	 * 
	 * @param request HttpServletRequest object associated with a request
	 * @param response HttpServletResponse object associated with a request
	 * @throws IOException - if an input or output error is detected when the servlet handles the GET request
	 * @throws ServletException - if the request for the GET could not be handled
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean purgeCookies = Boolean.valueOf(request.getParameter("purgeCookies")).booleanValue();
		if (purgeCookies) {
			Cookie c = new Cookie("FireEngineUser", null);
			c.setMaxAge(0);
			c.setPath("/");
			response.addCookie(c);
			c = new Cookie("FireEnginePass", null);
			c.setMaxAge(0);
			c.setPath("/");
			response.addCookie(c);
		}
		try {
			AuthToken at = Servlets.getAuthToken(request);
			int id = AuthenticationService.findID(at);
			AuthenticationService.purgeUser(id);
		} catch (Exception e) {}
		request.getSession(true).invalidate();
		FireEngineContext context = Servlets.getFireEngineContext(getServletContext());
		response.sendRedirect(context.getVirtualRoot());
	}
	
	/**
	 * doPost
	 * 
	 * @param request HttpServletRequest object associated with a request
	 * @param response HttpServletResponse object associated with a request
	 * @throws IOException - if an input or output error is detected when the servlet handles the POST request
	 * @throws ServletException - if the request for the POST could not be handled
	 */	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);		
	}
}
