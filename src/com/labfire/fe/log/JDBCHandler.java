/*
 * JDBCHandler.java
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

import java.util.Iterator;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.util.WorkQueue;

/**
 * JDBCHandler
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class JDBCHandler extends Handler {
	private AuthToken at;
	private WorkQueue work = new WorkQueue();
	Thread writeThread;

	/**
	 * JDBCHandler
	 */
	public JDBCHandler(AuthToken at) {
		super();
		this.at = at;
		try {
			writeThread = new Thread(new LogWriter(at, this, work));
			writeThread.setPriority(Thread.MIN_PRIORITY);
			writeThread.start();
		} catch (Exception e) {
			LogService.logError("Unable to start writeThread.", e);
		}
	}
	
	/**
	 * publish
	 */
	public void publish(LogRecord record) {
		work.addWork(record);
	}
	
	/**
	 * flush
	 */
	public void flush() {}
	
	/**
	 * close
	 */
	public void close() {
		LogService.logInfo("Flushing log queue...");
		writeThread.interrupt();
		LogRecord record;
		Iterator records = work.getAllWork().iterator();
		while (records.hasNext()) {
			record = (LogRecord) records.next();
			LogWriter.writeLogRecord(at, this, record);
		}
	}
	
	/**
	 * reportError
	 */
	protected void reportError(String msg, Exception ex, int code) {
		super.reportError(msg, ex, code);
	}
}
