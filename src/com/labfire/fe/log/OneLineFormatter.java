/*
 * OneLineFormatter.java
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

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.labfire.fe.util.Strings;

/**
 * OneLineFormatter
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class OneLineFormatter extends Formatter {

	/**
	 * format
	 *
	 * @return String
	 */
	public String format(LogRecord record) {
		String time = new java.sql.Timestamp(record.getMillis()).toString();
		
		StringBuffer sb = new StringBuffer(record.getLevel().toString());
		sb.append(" - ");
		//sb.append(new java.util.Date(record.getMillis()));
		
		sb.append(time);
		if (time.length() < 23) {
			if (time.length() == 22) {
				sb.append('0');
			} else if (time.length() == 21) {
				sb.append("00");
			}
		}
		sb.append(" - ");
		if (record.getSourceClassName() != null && record.getSourceMethodName() != null) {
			sb.append(record.getSourceClassName());
			sb.append('.');
			sb.append(record.getSourceMethodName());
			sb.append(": ");
		}
		sb.append(record.getMessage());
		if (record.getThrown() != null) {
			sb.append(" [");
			sb.append(record.getThrown());
			sb.append(']');
			StackTraceElement[] elements = record.getThrown().getStackTrace();
			for (int i = 0; i < elements.length; i++) {
				sb.append('\n');
				sb.append("\tat ");
				sb.append(Strings.escapeSQL(elements[i].toString()));
			}
		}
		sb.append('\n');
		return sb.toString();
	}
}