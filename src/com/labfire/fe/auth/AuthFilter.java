/*
 * AuthFilter.java
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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.labfire.fe.log.LogService;
import com.labfire.fe.util.Errors;
import com.labfire.fe.util.Servlets;

/**
 * AuthFilter
 * 
 * @author <a href = "http://labfire.com">Labfire, Inc.</a>
 */
public class AuthFilter implements Filter {
	private FilterConfig filterConfig;

	/**
	 * doFilter
	 * 
	 * @param request ServletRequest object associated with a request
	 * @param response ServletResponse object associated with a request
	 * @param chain FilterCHain object holding the webapp's list of Filters
	 */
	public void doFilter(ServletRequest request, 
			ServletResponse response, FilterChain chain) {
		try {
			HttpServletRequest hsr = (HttpServletRequest)request;
			AuthToken at = Servlets.getAuthToken(hsr);
			StringBuffer requestURI;
			if (hsr.getParameter("requestURI") != null) {
				requestURI = new StringBuffer(hsr.getParameter("requestURI"));
			} else {
				requestURI = new StringBuffer(hsr.getRequestURI());
			}
			if (AuthorizationService.hasNullConstraint(requestURI.toString())) {
				chain.doFilter(request, response);
			} else {
				String qs = hsr.getQueryString();
				if (qs != null) {
					requestURI.append('?');
					requestURI.append(qs);
				}
				if (request.getAttribute("requestURI") == null) {
					request.setAttribute("requestURI", requestURI.toString());
				}
				if ((at == null) || (!AuthenticationService.isValidToken(at))) {
					hsr.getSession().invalidate();
					RequestDispatcher rd = request.getRequestDispatcher("/servlet/com.labfire.fe.auth.AuthenticationServlet");
					if (rd != null) {
						rd.forward(request, response);
					}
				} else if (!AuthorizationService.authorizeRequest(at, hsr.getRequestURI())) {
					AuthorizationException ae = new AuthorizationException("Authorization failed, possibly due to insufficient permissions, for resource: " + requestURI);
					Errors.handleException(ae, request, response);	
				} else {
					AuthenticationService.setCurrentToken(at);
					chain.doFilter (request, response);
				}
			}
    	} catch (IOException io) {
			LogService.logError("Caught IOException", io);
			Errors.handleException(io, request, response);
		} catch (ServletException se) {
			LogService.logError("Caught ServletException", se);
			Errors.handleException(se, request, response);
		} finally {
			AuthenticationService.unsetCurrentToken();
		}
	}

	/**
	 * getFilterConfig
	 */
	public FilterConfig getFilterConfig() {
		return this.filterConfig;
	}

	/**
	 * setFilterConfig
	 */
	public void setFilterConfig (FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}
  
	/**
	 * destroy
	 */
	public void destroy() {
		this.filterConfig = null;
	}

	/**
	 * init
	 * 
	 * @throws ServletException - if the Filter could not be initialized
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}
}
