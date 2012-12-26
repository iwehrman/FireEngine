/*
 * MailAuthenticator.java
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

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import com.labfire.fe.auth.AuthToken;

/**
 * MailAuthenticator
 * 
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class MailAuthenticator extends Authenticator {
	private static PasswordAuthentication auth = null;
	
	public MailAuthenticator(AuthToken at) throws MailException {
		auth = new PasswordAuthentication(MailService.getDefaultUser(), MailService.getDefaultPass());
	}
	
	public PasswordAuthentication getPasswordAuthentication() {
		return auth;
	}
}
