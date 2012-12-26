/*
 * LoginReminder.java
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.FireEngineContext;
import com.labfire.fe.util.Errors;
import com.labfire.fe.util.Servlets;

/**
 * LoginReminder
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class LoginReminder extends HttpServlet {

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
		try {
			FireEngineContext context = Servlets.getFireEngineContext(getServletContext());
			AuthenticationService.sendReminder(request);
			response.sendRedirect(context.getVirtualRoot());
		} catch (Exception e) {
			Errors.handleException(e, request, response);
		}
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
