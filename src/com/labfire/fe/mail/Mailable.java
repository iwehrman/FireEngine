/*
 * Mailable.java
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

import com.labfire.fe.auth.AuthToken;

/**
 * Mailable
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class Mailable {
	private AuthToken at;
	private String to;
	private String from;
	private String subject;
	private String body;
	
	public Mailable(AuthToken at, String to, String from, String subject, String body) {
		this.at = at;
		this.to = to;
		this.from = from;
		this.subject = subject;
		this.body = body;
	}
	
	public AuthToken getAuthToken() {
		return this.at;
	}
	
	public String getTo() {
		return this.to;
	}
	
	public String getFrom() {
		return this.from;
	}
	
	public String getSubject() {
		return this.subject;
	}
	
	public String getBody() {
		return this.body;
	}
}