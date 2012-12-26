/*
 * Errors.java
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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.labfire.fe.FireEngineContext;
import com.labfire.fe.log.LogService;

/**
 * Errors
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class Errors {

	/**
	 * handleException
	 */
	public static void handleException(Exception e, ServletRequest request, ServletResponse response) {
		RequestDispatcher rd;
		ServletContext context = ((HttpServletRequest)request).getSession().getServletContext();
		FireEngineContext feContext = ((FireEngineContext)context.getAttribute("FireEngineContext"));
		String errorPage = feContext.getErrorPage();
		LogService.logWarn("Handled " + e, e);
		try {
			HttpSession session = ((HttpServletRequest)request).getSession(true);
			session.setAttribute("lastException", e);
			rd = request.getRequestDispatcher(errorPage);
			if (rd != null) {
				if (!response.isCommitted()) {
					((HttpServletResponse)response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					rd.forward(request, response);
				} else {
					rd.include(request, response);
				}
			} else {
				LogService.logError("Unable to acquire RequestDispatcher for error page");
			}
		} catch (IllegalStateException ise) {
			try {
				rd = ((HttpServletRequest)request).getSession(true).getServletContext().getRequestDispatcher(errorPage);
				if (rd != null) {
					rd.include(request, response);
				} else {
					LogService.logError("Unable to acquire RequestDispatcher for error page, on second try");
				}
			} catch (Exception e2) {
				LogService.logError("Unable to handle error for resource " + ((HttpServletRequest)request).getRequestURI() + ", on second try", e2);
			}
		} catch (Exception e1) {
			LogService.logError("Unable to handle error for resource " + ((HttpServletRequest)request).getRequestURI(), e1);
		}
	}
}
