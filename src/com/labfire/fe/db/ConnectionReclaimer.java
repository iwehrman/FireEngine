/*
 * ConnectionReclaimer.java
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

/**
 * ConnectionReclaimer
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class ConnectionReclaimer implements Runnable {
	private ConnectionPool pool;
	private final int SLEEP_TIME = 1000*60*10; // run every 10 minutes
	
	/**
	 * ConnectionReclaimer
	 */
	ConnectionReclaimer(ConnectionPool pool) {
		this.pool = pool;
	}
	
	/**
	 * run
	 */
	public void run() {
		int initialConnections;
		int availableConnections;
		long connectionsToBeReclaimed;
		
		try {
			while (true) {
				initialConnections = pool.getInitialConnections();
				availableConnections = pool.getAvailableConnections();
				if (availableConnections > initialConnections) {
					connectionsToBeReclaimed = Math.round(Math.floor(((availableConnections - initialConnections) / initialConnections)));
					pool.reduceAvailableConnections(connectionsToBeReclaimed);
				}
				Thread.sleep(SLEEP_TIME);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}