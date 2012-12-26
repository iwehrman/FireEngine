/*
 * AuthenticationEditServlet.java
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

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.labfire.fe.FireEngineContext;
import com.labfire.fe.common.TransientUser;
import com.labfire.fe.mail.MailException;
import com.labfire.fe.mail.MailService;
import com.labfire.fe.util.Servlets;
import com.labfire.fe.util.Strings;
import com.labfire.fe.util.UpdateException;
import com.labfire.fe.util.UpdateServlet;

/**
 * AuthenticationEditServlet
 * 
 * @author <a href="http://labfire.com">Labfire, Inc.</a>
 */
public class AuthenticationEditServlet extends UpdateServlet {
	
	/**
	 * processForm
	 * 
	 * @return int
	 */
	public int processForm(AuthToken at, HttpServletRequest request) throws UpdateException {
		int records = -1;
		TransientUser u;
		boolean notify = false;
		try {
			u = new TransientUser();
			u.setUserLoginAllowed(Boolean.valueOf(request.getParameter("userLoginAllowed")).booleanValue());
			u.setUserAID(Integer.parseInt(request.getParameter("userAccess")));
			u.setUserName(request.getParameter("userName"));
			u.setUserEmail(request.getParameter("userEmail"));
			u.setUserFirstName(request.getParameter("userFirstName"));
			u.setUserLastName(request.getParameter("userLastName"));
			List oids = new LinkedList();
				String[] userOrgs = request.getParameterValues("userOrgs");
				for (int i = 0; userOrgs != null && i < userOrgs.length; i++) {
					oids.add(new Integer(Integer.parseInt(userOrgs[i])));
				}
				u.setUserOIDs(oids);
			if (!request.getParameter("userID").toLowerCase().equals("new")) {
				if (!request.getParameter("userPass").equals(request.getParameter("userPass2"))) {
					throw new AuthException("Supplied passwords do not match");
				} else if (request.getParameter("userPass").length() < 6) {
					throw new AuthException("Supplied password must be at least 6 characters long");
				} else {
					u.setUserPass(request.getParameter("userPass"));
				}
				u.setUserID(Integer.parseInt(request.getParameter("userID")));
				u.setUserLastLoggedIn(new java.util.Date(Long.parseLong(request.getParameter("userLastLoggedIn"))));
				records = AuthenticationService.update(at, u);
			} else {
				if (request.getParameter("userPass").length() == 0 &&
						request.getParameter("userPass2").length() == 0) {
					String pass = Strings.encode(new Long(System.currentTimeMillis()).toString() + u.getUserName());
					u.setUserPass(pass.substring(0, 10));
					notify = true;
				} else if (!request.getParameter("userPass").equals(request.getParameter("userPass2"))) {
					throw new AuthException("Supplied passwords do not match");
				} else if (request.getParameter("userPass").length() < 6) {
					throw new AuthException("Supplied password must be at least 6 characters long");
				} else {
					u.setUserPass(request.getParameter("userPass"));
				}
				records = AuthenticationService.insert(at, u);
				if (notify) {
					notifyUser(at, u);
				}
			}
			return records;
		} catch (Exception e) {
			throw new UpdateException("Unable to edit user", e);
		}
	}
	
	private void notifyUser(AuthToken at, TransientUser user) throws AuthException, MailException {
		FireEngineContext context = Servlets.getFireEngineContext(getServletContext());
		String from = "FireEngine <noreply@labfire.com>";
		String to = user.getUserEmail();
		String subject = "User account notification";
		String body = "Hello,\nA new user account has been created for you at " +
			context.getHostName() + ".\nYour login information is:\nUsername: " + user.getUserName() +
			"\nPassword: " + user.getUserPass() + "\n";
		MailService.sendMessage(at, to, from, subject, body);
	}
}
