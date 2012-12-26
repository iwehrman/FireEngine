/*
 * SMTPHandler.java
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


package com.labfire.fe.log;

import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.mail.MailService;

/**
 * SMTPHandler
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class SMTPHandler extends Handler {
	private AuthToken at;
	private String to;

	/**
	 * SMTPHandler
	 */
	public SMTPHandler(AuthToken at, String to) {
		super();
		this.at = at;
		this.to = to;
	}
	
	/**
	 * publish
	 */
	public void publish(LogRecord record) {
		try {
			StringBuffer subject = new StringBuffer(record.getLevel().toString());
			subject.append(": ");
			subject.append(record.getSourceClassName());
			subject.append('.');
			subject.append(record.getSourceMethodName());
			StringBuffer body = new StringBuffer(subject.toString());
			body.append('\n');
			body.append('\n');
			body.append(new java.sql.Timestamp(record.getMillis()));
			body.append('\n');
			body.append('\n');
			body.append(record.getMessage());
			if (record.getThrown() != null) {
				body.append('\n');
				body.append('\n');
				body.append(record.getThrown());
				body.append('\n');
				StackTraceElement[] trace = record.getThrown().getStackTrace();
				for (int i = 0; i < trace.length; i++) {
					body.append(trace[i]);
					body.append('\n');
				}
			}
			MailService.sendMessage(at, to, subject.toString(), body.toString());
		} catch (Exception e) {
			reportError("Unable to send log message", e, ErrorManager.WRITE_FAILURE);
		}
	}
	
	/**
	 * flush
	 */
	public void flush() {}
	
	/**
	 * close
	 */
	public void close() {}
}