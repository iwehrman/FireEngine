/*
 * LogSweeper.java
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

import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.util.Connections;
import com.labfire.fe.util.Dates;


/**
 * LogSweeper
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class LogSweeper implements Runnable {
	private final long MILLIS_IN_MONTH = Dates.DAYS * 31;
	private AuthToken at;
		
	/**
 	 * LogSweeper
	 */
	public LogSweeper(AuthToken at) {
		this.at = at;
	}
	
	/**
	 * run
	 */
	public void run() {
		try {
			long time = System.currentTimeMillis() - MILLIS_IN_MONTH;
			String query = "SELECT count(*) FROM log WHERE LogMillis < " + time;
			ResultSet rs = Connections.select(at, query);
			if (rs.next()) {
				int count = rs.getInt(1);
				rs.close();
				String update = "DELETE FROM log WHERE LogMillis < " + time;
				Connections.update(at, update);
				LogService.logInfo("Deleted " + count + " old log messages");
			}
		} catch (Exception e) {
			LogService.logError("Unable to start log sweep", e);
		}
	}
}
