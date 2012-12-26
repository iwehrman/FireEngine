/*
 * MailServlet.java
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


package com.labfire.fe.mail;

import javax.servlet.http.HttpServletRequest;

import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.util.UpdateException;
import com.labfire.fe.util.UpdateServlet;

/**
 * MailServlet
 * 
 * MailTo<br />
 * MailSubject<br />
 * MailBody<br />
 * @author <a href = "http://labfire.com/">Labfire, Inc.</a>
 */
public class MailServlet extends UpdateServlet {

	/**
	 * processForm
	 * 
	 * @return int
	 */
	public int processForm(AuthToken at, HttpServletRequest request) throws UpdateException {
		try {
			String to = request.getParameter("MailTo");
			String subject = request.getParameter("MailSubject");
			String body = request.getParameter("MailBody");
			MailService.sendMessage(at, to, subject, body);
			return 1;
		} catch (Exception e) {
			throw new UpdateException("Caught Exception while sending message.", e);
		}
	}
}
