/*
 * SQLFormatter.java
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
 * SQLFormatter
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class SQLFormatter extends Formatter {
	private static final int MAX_ELEMENTS = 6;
	private static final String PREFIX = 
		"INSERT INTO Log (LogLevel,LogMillis,LogMessage,LogThrown) VALUES (";

	private static final String PREFIXCMT = 
		"INSERT INTO Log (LogLevel,LogMillis,LogMessage,LogClass,LogMethod,LogThrown) VALUES (";
	private static final String AC = "',";
	private static final String CA = ",'";
	private static final String ACA = "','";
	private static final String AP = "')";

	/**
	 * format
	 *
	 * @return String
	 */
	public String format(LogRecord record) {
		StringBuffer sb;
		if (record.getSourceClassName() != null) {
			sb = new StringBuffer(PREFIXCMT);
		} else {
			sb = new StringBuffer(PREFIX);
		}
		sb.append('\'');
		sb.append(record.getLevel());
		sb.append(AC);
		sb.append(record.getMillis());
		sb.append(CA);
		sb.append(Strings.escapeSQL(record.getMessage()));
		if (record.getSourceClassName() != null) {
			sb.append(ACA);
			sb.append(record.getSourceClassName());
			sb.append(ACA);
			sb.append(record.getSourceMethodName());
			sb.append(ACA);
			Throwable t = record.getThrown();
			while (t != null) {
				sb.append(Strings.escapeSQL(t.toString()));
				StackTraceElement[] elements = t.getStackTrace();
				int i = 0;
				for (; i < elements.length && i < MAX_ELEMENTS; i++) {
					sb.append('\n');
					sb.append("\tat ");
					sb.append(Strings.escapeSQL(elements[i].toString()));
				}
				if (i == MAX_ELEMENTS) {
					sb.append("\n\t... ");
					sb.append(elements.length - MAX_ELEMENTS);
					sb.append(" more");
				}
				t = t.getCause();
				if (t != null) {
					sb.append("\n\n");
				}
			}
		}
		sb.append(AP);
		return sb.toString();
	}
}