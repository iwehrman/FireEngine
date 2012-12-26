/*
 * AuthenticationServlet.java
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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.labfire.fe.FireEngine;
import com.labfire.fe.FireEngineContext;
import com.labfire.fe.common.User;
import com.labfire.fe.log.LogService;
import com.labfire.fe.util.Dates;
import com.labfire.fe.util.Errors;
import com.labfire.fe.util.Servlets;

/**
 * AuthenticationServlet
 * The AuthenticationServlet is a controller which (typically) stands
 * between the AuthenticationFilter and the AuthenticationService. This
 * servlet retrieves login information (either from postdata or cookies)
 * and attempts to retrive a user EID from the Authentication service,
 * based on the users's login information. If it receives a valid EID,
 * it inserts it into the user's session, and forwards the request to
 * it's original URI. If it does not receive a valid URI, the user is sent
 * back to the login page.
 * 
 * @author <a href="http://labfire.com">Labfire, Inc.</a>
 */
public class AuthenticationServlet extends HttpServlet {
	
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
		doPost(request, response);
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
		FireEngineContext context = Servlets.getFireEngineContext(getServletContext());
		// look for user/pass in postdata first
		String userName = (String)request.getParameter("FireEngineUser");
		String userPass = (String)request.getParameter("FireEnginePass");
		String requestURI = (String)request.getAttribute("requestURI");
		
		// if no requestURI, (like if someone just typed in login.jsp)
		// then forward a successful request to the front page
		if ((requestURI == null) || (requestURI.length() == 0)) {
			requestURI = request.getParameter("requestURI");
			if (requestURI == null || (requestURI.length() == 0)) {
				requestURI = context.getVirtualRoot();
			}
		}
		
		// if not in postdata, try cookies
		if ((userName == null) || (userPass == null)) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					if (cookies[i].getName().equals("FireEngineUser")) {
						userName = cookies[i].getValue();
					}
					if (cookies[i].getName().equals("FireEnginePass")) {
						userPass = cookies[i].getValue();
					}
				}
			}
		} else if ("on".equals(request.getParameter("FireEngineCookie"))) {
			// if user info was in postdata, and they want info stored in a cookie
			Cookie c = new Cookie("FireEngineUser", userName);
			c.setMaxAge(Dates.SECONDS_PER_YEAR);
			c.setPath("/");
			response.addCookie(c);
			c = new Cookie("FireEnginePass", userPass);
			c.setMaxAge(Dates.SECONDS_PER_YEAR);
			c.setPath("/");
			response.addCookie(c);
			c = new Cookie("FireEngineMajorVersion", String.valueOf(FireEngine.getInstance().getMajorVersion()));
			c.setMaxAge(Dates.SECONDS_PER_YEAR);
			c.setPath("/");
			response.addCookie(c);
			c = new Cookie("FireEngineMinorVersion", String.valueOf(FireEngine.getInstance().getMinorVersion()));
			c.setMaxAge(Dates.SECONDS_PER_YEAR);
			c.setPath("/");
			response.addCookie(c);
			c = new Cookie("FireEngineSubVersion", String.valueOf(FireEngine.getInstance().getSubVersion()));
			c.setMaxAge(Dates.SECONDS_PER_YEAR);
			c.setPath("/");
			response.addCookie(c);
		}
		
		if ((userName == null) || (userPass == null)) {
			// incomplete user information, send to the login page
			try {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				RequestDispatcher rd = getServletContext().getRequestDispatcher(context.getLoginPage());
				rd.forward(request, response);
			} catch (Exception e) {
				Errors.handleException(e, request, response);
			}
		} else {
			// see if AuthenticationService will give us a valid user object.
			try {
				User u = AuthenticationService.login(userName, userPass);
				if (u != null) {
					HttpSession session = request.getSession();
					AuthToken at = Servlets.getAuthToken(request);
					if ((at == null) || !AuthenticationService.isValidToken(at)) {
						session.setAttribute(AuthenticationService.getSessionAttributeName(), u.getUserToken());
						LogService.logInfo("User " + userName + " logged in successfully from " + request.getRemoteAddr());
					}
					response.sendRedirect(response.encodeRedirectURL(requestURI));
				} else {
					LogService.logError("Failed login by user " + userName + " from " + request.getRemoteAddr());
					try {
						response.setStatus(HttpServletResponse.SC_FORBIDDEN);
						request.setAttribute("errorMessage", "Authentication failed: Invalid login information");
						RequestDispatcher rd = getServletContext().getRequestDispatcher(context.getLoginPage());
						rd.forward(request, response);
					} catch (Exception e) {
						Errors.handleException(e, request, response);
					}
				}
			} catch (AuthException ae) {
				LogService.logWarn("Failed login by user " + userName + " from " + request.getRemoteAddr());
				try {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					request.setAttribute("errorMessage", "Authentication failed: Invalid login information");
					RequestDispatcher rd = getServletContext().getRequestDispatcher(context.getLoginPage());
					rd.forward(request, response);
				} catch (Exception e) {
					Errors.handleException(e, request, response);
				}
			} catch (Exception e) {
				LogService.logError("An unknown exception has occurred while logging in", e);
				try {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					request.setAttribute("errorMessage", "Authentication failed: Invalid login information");
					RequestDispatcher rd = getServletContext().getRequestDispatcher(context.getLoginPage());
					rd.forward(request, response);
				} catch (Exception ee) {
					Errors.handleException(ee, request, response);
				}
			}
		}		
	}
}
