/*
 * LogWriter.java
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.ErrorManager;
import java.util.logging.LogRecord;

import com.labfire.fe.auth.AuthException;
import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.util.Connections;
import com.labfire.fe.util.WorkQueue;

/**
 * LogWriter
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class LogWriter implements Runnable {
	private WorkQueue queue;
	private AuthToken at;
	private JDBCHandler parent;
	
	/**
	 * LogWriter
	 */
	public LogWriter(AuthToken at, JDBCHandler parent, WorkQueue queue) {
		this.at = at;
		this.parent = parent;
		this.queue = queue;
	}
	
	/**
	 * run
	 */
	public void run() {
		LogRecord record;
		try {
			while (true) {
				record = (LogRecord)queue.getWork();
				writeLogRecord(at, parent, record);
			}
		} catch (InterruptedException ie) {
			LogService.logInfo("Halting log writer...");
		}
	}
	
	static void writeLogRecord(AuthToken at, JDBCHandler parent, LogRecord record) {
		ResultSet rs = null;
		try {
			rs = Connections.update(at, parent.getFormatter().format(record));
		} catch (SQLException sqle) {
			parent.reportError("Unable to write log record to database", sqle, ErrorManager.WRITE_FAILURE);
		} catch (AuthException ae) {
			parent.reportError("Authorization failure while writing log record to database", ae, ErrorManager.WRITE_FAILURE);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException sqle) {}
		} 
	}
}
