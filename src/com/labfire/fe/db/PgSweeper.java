/*
 * PgSweeper.java
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


package com.labfire.fe.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.labfire.fe.log.LogService;
import com.labfire.fe.util.Connections;


/**
 * PgSweeper
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class PgSweeper implements Runnable {
	private String connectionName;
	
	public PgSweeper(String connectionName) {
		this.connectionName = connectionName;
	}
	
	/**
	 * run
	 */
	public void run() {
		Connection cn = null;
		try {
			String update = "vacuum";
			cn = ConnectionService.getConnection(connectionName);
			Statement stmt = cn.createStatement();
			stmt.executeUpdate(update);
			Connections.logSQLWarnings(stmt);
			LogService.logInfo("Vacuumed database " + connectionName);
			update = "analyze";
			stmt.executeUpdate(update);
			Connections.logSQLWarnings(stmt);
			LogService.logInfo("Analyzed database " + connectionName);
			stmt.close();
		} catch (Exception e) {
			LogService.logError("Unable to start database sweep", e);
		} finally {
			if (cn != null) {
				try {
					ConnectionService.returnConnection(cn);
				} catch (SQLException sqle) {
					LogService.logError("Unable to return connection after database sweep", sqle);
				}
			}
		}
	}
}
