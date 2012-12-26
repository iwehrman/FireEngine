/*
 * AuthorizationEditServlet.java
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
 * AuthorizationEditServlet
 * 
 * @author <a href = "mailto:ian@labfire.com">Labfire, Inc.</a>
 */
public class AuthorizationEditServlet extends UpdateServlet {
	public int processForm(AuthToken at, HttpServletRequest request) throws UpdateException {
		RequestAuth ra = new RequestAuth();
		Constraint c = new Constraint();
		try {
			ra.setURI(request.getParameter("RequestURI"));
			if (request.getParameter("cID") != null && request.getParameter("cID").length() > 0) {
				c.setID(new Integer(Integer.parseInt(request.getParameter("cID"))));
			}
			if (request.getParameter("cOID") != null && request.getParameter("cOID").length() > 0) {
				c.setOID(new Integer(Integer.parseInt(request.getParameter("cOID"))));
			}
			if (request.getParameter("cAID") != null && request.getParameter("cAID").length() > 0) {
				c.setAID(new Integer(Integer.parseInt(request.getParameter("cAID"))));
			}
			ra.getConstraints().add(c);
			AuthorizationService.insertRequest(at, ra);
			return 1;
		} catch (Exception e) {
			throw new UpdateException("Unable to insert authorization constraint", e);
		}
	}
}
