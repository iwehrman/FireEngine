/*
 * ConnectionPool.java
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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import com.labfire.fe.log.BootLogger;

/** 
 * ConnectionPool
 * A class for preallocating, recycling, and managing JDBC connections.
 *  
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 * @author Marty Hall
 */
public class ConnectionPool implements Runnable {
	private boolean waitIfBusy;
	private boolean connectionPending = false;
	private int maxConnections;
	private int initialConnections;
	private int count = 0;
	private String driver;
	private String url;
	private String username;
	private String password;
	private Vector availableConnections;
	private Vector busyConnections;
	
	/**
	 * ConnectionPool
	 */
	public ConnectionPool(String driver, String url, String username, 
			String password, int initialConnections, int maxConnections,
			boolean waitIfBusy) throws SQLException {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		this.maxConnections = maxConnections;
		this.initialConnections = initialConnections;
		this.waitIfBusy = waitIfBusy;
		if (initialConnections > maxConnections) {
			initialConnections = maxConnections;
		}
		availableConnections = new Vector(initialConnections);
		busyConnections = new Vector();
		for (int i = 0; i < initialConnections; i++) {
			availableConnections.addElement(makeNewConnection());
		}
		Thread reclaimConnectionThread = new Thread(new ConnectionReclaimer(this), "reclaimConnectionThread[" + url + "," + username + "]");
		reclaimConnectionThread.setPriority(Thread.MIN_PRIORITY);
		reclaimConnectionThread.start();
	}

	/**
	 * getURL
	 *
	 * @return String
	 */
	public String getURL() {
		return url;
	}
	
	/**
	 * getUserName
	 *
	 * @return String
	 */
	public String getUserName() {
		return username;
	}
	
	/**
	 * getDriver
	 *
	 * @return String
	 */
	public String getDriver() {
		return driver;
	}
	
	
	/**
	 * getInitialConnections
	 *
	 * @return int
	 */
	public int getInitialConnections() {
		return initialConnections;
	}
	
	/**
	 * getMaxConnections
	 *
	 * @return int
	 */
	public int getMaxConnections() {
		return maxConnections;
	}
	
	/**
	 * getAvailableConnections
	 *
	 * @return int
	 */
	public synchronized int getAvailableConnections() {
		return availableConnections.size();
	}
	
	/**
	 * getBusyConnections
	 *
	 * @return int
	 */
	public synchronized int getBusyConnections() {
		return busyConnections.size();
	}
	
	/**
	 * totalConnections
	 *
	 * @return int
	 */
	public synchronized int totalConnections() {
		return(availableConnections.size() + busyConnections.size());
	}
	
	/**
	 * getCount
	 *
	 * @return int
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * getWaitIfBusy
	 *
	 * @return boolean
	 */
	public boolean getWaitIfBusy() {
		return waitIfBusy;
	}

	/**
	 * getconnection
	 * 
	 * @returns Connection an initialized Connection object
	 * @throws SQLException if there are problems finding a Connection to return
	 */
	public synchronized Connection getConnection() throws SQLException {
		if (!availableConnections.isEmpty()) {
			Connection existingConnection = (Connection)availableConnections.lastElement();
			int lastIndex = availableConnections.size() - 1;
			availableConnections.removeElementAt(lastIndex);
			// If connection on available list is closed (e.g.,
			// it timed out), then remove it from available list
			// and repeat the process of obtaining a connection.
			// Also wake up threads that were waiting for a
			// connection because maxConnection limit was reached.
			if (existingConnection.isClosed()) {
				notifyAll(); // Freed up a spot for anybody waiting
				return getConnection();
			} else {
				busyConnections.addElement(existingConnection);
				existingConnection.setAutoCommit(true);
				existingConnection.clearWarnings();
				count++;
				return existingConnection;
			}
		} else {
			// Three possible cases:
			// 1) You haven't reached maxConnections limit. So
			//    establish one in the background if there isn't
			//    already one pending, then wait for
			//    the next available connection (whether or not
			//    it was the newly established one).
			// 2) You reached maxConnections limit and waitIfBusy
			//    flag is false. Throw SQLException in such a case.
			// 3) You reached maxConnections limit and waitIfBusy
			//    flag is true. Then do the same thing as in second
			//    part of step 1: wait for next available connection.
	
			if ((totalConnections() < maxConnections) && !connectionPending) {
				makeBackgroundConnection();
			} else if (!waitIfBusy) {
				throw new SQLException("Connection limit reached: " + this.toString());
			}
			// Wait for either a new connection to be established
			// (if you called makeBackgroundConnection) or for
			// an existing connection to be freed up.
			try {
				wait();
			} catch(InterruptedException ie) {}
			// Someone freed up a connection, so try again.
			return getConnection();
		}
	}
	
	/**
	 * run
	 */
	public void run() {
		try {
			Connection connection = makeNewConnection();
			synchronized(this) {
				availableConnections.addElement(connection);
				connectionPending = false;
				notifyAll();
			}
		} catch (Exception e) { 
			// SQLException or OutOfMemory
			// Give up on new connection and wait for existing one
			// to free up.
		}
	}

	/**
	 * free
	 */
	public synchronized void free(Connection connection) {
		// should probably do this
		//connection.commit();
		busyConnections.removeElement(connection);
		availableConnections.addElement(connection);
		// Wake up threads that are waiting for a connection
		notifyAll(); 
	}

	/**
	 * closeAllConnections
	 * Close all the connections. Use with caution:
	 * be sure no connections are in use before
	 * calling. Note that you are not <I>required</I> to
	 * call this when done with a ConnectionPool, since
	 * connections are guaranteed to be closed when
	 * garbage collected. But this method gives more control
	 * regarding when the connections are closed.
	 */
	public synchronized void closeAllConnections() {
		closeConnections(availableConnections);
		availableConnections.clear();
		closeConnections(busyConnections);
		busyConnections.clear();
	}

	/**
	 * reduceAvailableConnections
	 */
	void reduceAvailableConnections(long amount) {
		Connection cn;
		if (availableConnections != null) {
			for (int i = 0; (i < amount) && (i < availableConnections.size()); i++) {
				synchronized (availableConnections) {
					cn = (Connection)availableConnections.remove(0);
				}
				try {
					if (cn != null && !cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException sqle) {
					// ignore any SQL exceptions while closing the connection
				}
			}
		}
	}

	/**
	 * makeBackgroundConnection
	 * You can't just make a new connection in the foreground
	 * when none are available, since this can take several
	 * seconds with a slow network connection. Instead,
	 * start a thread that establishes a new connection,
	 * then wait. You get woken up either when the new connection
	 * is established or if someone finishes with an existing
	 * connection.
	 */
	private void makeBackgroundConnection() {
		connectionPending = true;
		BootLogger.logDebug("ConnectionPool.makeBackgroundConnection: Adding new connection to pool " + this);
		try {
			Thread connectThread = new Thread(this);
			connectThread.start();
		} catch(OutOfMemoryError oome) {
			// Give up on new connection
		}
	}
  
	/**
	 * makeNewConnection
	 * This explicitly makes a new connection. Called in
	 * the foreground when initializing the ConnectionPool,
	 * and called in the background when running.
	 *
	 * @return Connection
	 */
	private Connection makeNewConnection() throws SQLException {
		try {
			// Load database driver if not already loaded
			Class.forName(driver);
			// Establish network connection to database
			Connection connection = DriverManager.getConnection(url, username, password);
			return connection;
		} catch(ClassNotFoundException cnfe) {
			// Simplify try/catch blocks of people using this by
			// throwing only one exception type.
			throw new SQLException("Can't find class for driver: " + driver);
		}
	}

	/**
	 * closeConnections
	 */
	private void closeConnections(Vector connections) {
		try {
			for (int i = 0; i < connections.size(); i++) {
				Connection connection = (Connection)connections.elementAt(i);
				if (!connection.isClosed()) {
					connection.close();
				}
			}
		} catch(SQLException sqle) {
			// Ignore errors; garbage collect anyhow
		}
	}

	/**
	 * toString
	 *
	 * @return String
	 */
	public String toString() {
		return(url + "," + username + " (" + availableConnections.size() + ":" + busyConnections.size() + ":" + maxConnections + ")");
	}
}
