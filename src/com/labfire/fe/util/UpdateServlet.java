/*
 * UpdateServlet.java
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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.FireEngineContext;
import com.labfire.fe.auth.AuthException;
import com.labfire.fe.auth.AuthToken;

/**
 * UpdateServlet
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public abstract class UpdateServlet extends HttpServlet {
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
		AuthToken at = Servlets.getAuthToken(request);
		String required = request.getParameter("_required");
		int records = -1;
		
		if (at == null) {
			AuthException ae = new AuthException("No user currently logged in.");
			Errors.handleException(ae, request, response);	
			return;
		}
		
		if (required != null) {
			String value;
			String field;
			StringBuffer missing = new StringBuffer();
			String[] fields = required.split(",");
			if (fields != null) {
				for (int i = 0; i < fields.length; i++) {
					field = fields[i].trim();
					value = request.getParameter(field);
					if (value == null || value.trim().equals("")) {
						if (missing.length() > 0) {
							missing.append(", ");		
						}
						missing.append(field);
					}
				}
				if (missing.length() > 0) {
					UpdateException ue = new UpdateException("The following fields require information: " + missing.toString());
					Errors.handleException(ue, request, response);
					return;
				}
			}
		}
		
		try {
			records = processForm(at, request);
		} catch (Exception e) {
			Errors.handleException(e, request, response);
			return;
		}
		
		if (records > 0) {
			String redirect = getRedirect(at, request);
			redirect = response.encodeRedirectURL(redirect);
			response.sendRedirect(redirect);
		} else {
			UpdateException e = new UpdateException("Update unsuccessful, possibly due to insufficient priveledges, missing information, or an invalid request.");
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
	
	/**
	 * processForm
	 * 
	 * @return int
	 */
	protected abstract int processForm(AuthToken at, HttpServletRequest request) throws UpdateException;
	
	protected String getRedirect(AuthToken at, HttpServletRequest request) {
		String redirect = request.getParameter("_redirect");
		if (redirect == null) {
			redirect = request.getHeader("REFERER");
			if (redirect == null) {
				FireEngineContext context = Servlets.getFireEngineContext(getServletContext());
				redirect = context.getVirtualRoot();
			}
		}
		return redirect;
	}
}
