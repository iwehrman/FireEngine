/*
 * LogService.java
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

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;
import java.util.logging.XMLFormatter;

import com.labfire.fe.FireEngine;
import com.labfire.fe.FireEngineComponent;
import com.labfire.fe.FireEngineConfig;
import com.labfire.fe.FireEngineContext;
import com.labfire.fe.auth.AccessAuthenticationService;
import com.labfire.fe.auth.AuthException;
import com.labfire.fe.auth.AuthLevel;
import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.auth.AuthenticationException;
import com.labfire.fe.cache.CacheException;
import com.labfire.fe.cache.CacheService;
import com.labfire.fe.cache.CachedObject;
import com.labfire.fe.common.InitializeException;
import com.labfire.fe.common.User;
import com.labfire.fe.cron.CronService;
import com.labfire.fe.cron.SimpleCronEntry;
import com.labfire.fe.util.Connections;

/**
 * LogService
 * The LogService class hides the complexities of the underlying logging
 * implementation from the FireEngine user.  The API is deliberately kept as simple
 * as possible to minimize both the effort in using it and also the structural impact it
 * has on code.
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public final class LogService extends FireEngineComponent {
	private static boolean debug = false;
	private static String mailHandler = null;
	private static AuthToken serviceAuthToken;
	private static Logger logger;
	private static Set handlers = new HashSet();
	private static SimpleCronEntry sweeper = null;
	
	 /**
	  * LogService
	  *
	  * @param inConfigFileName - the location of the config. file for this service
	  * @throws InitializeException if the service could not be initialized.
	  */
	 public LogService(FireEngineConfig config) throws InitializeException {
	 	super(config);
		FireEngineContext context = config.getFireEngineContext();
        debug = FireEngine.getDebug();
		serviceAuthToken = config.getServiceAuthToken();
		logger = Logger.getLogger("com.labfire.fe." + context.getHostName() + context.getVirtualRoot());
		if (debug) {
			logger.setLevel(Level.FINE);
		} else {
			logger.setLevel(Level.INFO);
		}
		logger.setUseParentHandlers(false);
		Handler[] oldHandlers = logger.getHandlers();
		for (int i = 0; i < oldHandlers.length; i++) {
			logger.removeHandler(oldHandlers[i]);
		}
		
		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new OneLineFormatter());
		ch.setLevel(Level.FINE);
		logger.addHandler(ch);
		handlers.add(ch);
		try {
			String logDir = "%t";
			if (super.getProperty("logDir") != null) {
				logDir = super.getProperty("logDir");
				new File(logDir).mkdirs();
			}
			String pattern = logDir + "/FireEngine.%g.log";
			FileHandler fh = new FileHandler(pattern, 1000000, 4, true);
			if (super.getProperty("format") != null && super.getProperty("format").equalsIgnoreCase("xml")) {
				fh.setFormatter(new XMLFormatter());
			} else {
				fh.setFormatter(new OneLineFormatter());
			}
			fh.setLevel(Level.ALL);
			logger.addHandler(fh);
			handlers.add(fh);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Error creating FileHandler", ioe);
		}
		
		JDBCHandler jdbch = new JDBCHandler(serviceAuthToken);
		jdbch.setFormatter(new SQLFormatter());
		jdbch.setLevel(Level.FINE);
		logger.addHandler(jdbch);
		handlers.add(jdbch);
		
		if (super.getProperty("mailto") != null) {
			mailHandler = super.getProperty("mailto");
		}
	}

	protected void unload() {
		if (handlers != null) {
			Handler handler;
			Iterator i = handlers.iterator();
			while (i.hasNext()) {
				handler = (Handler) i.next();
				handler.close();
				logger.removeHandler(handler);
			}
			handlers.clear();
		}
	}
	
	protected void postCreate() throws InitializeException {
		if (mailHandler != null) {
			SMTPHandler smtph = new SMTPHandler(serviceAuthToken, mailHandler);
			smtph.setLevel(Level.SEVERE);
			MemoryHandler mh = new MemoryHandler(smtph, 100, Level.SEVERE);
			mh.setLevel(Level.SEVERE);
			logger.addHandler(mh);
			handlers.add(mh);
		}
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new java.util.Date());
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.roll(Calendar.DAY_OF_YEAR, 1);
			sweeper = new SimpleCronEntry(new LogSweeper(serviceAuthToken), cal.getTime(), SimpleCronEntry.DAY);
			CronService.putCron(sweeper);
		} catch (Exception e) {
			throw new InitializeException("Unable to schedule log sweeper", e);
		}
	}

	/**
	 * logDebug
	 * Logs a debug message.  The action taken will depend on how log4j has been set up.
	 * By default, debug messages are printed to both stdout and to file.
	 * 
	 * @param inMessage - the String debug message to log
	 */
	public static void logDebug(String inMessage) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		StackTraceElement caller = elements[1];
		logger.logp(Level.FINE, caller.getClassName(), caller.getMethodName(), inMessage);
	}
	
	/**
	 * logDebug
	 * @param msg
	 * @param thrown
	 */
	public static void logDebug(String inMessage, Throwable thrown) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		StackTraceElement caller = elements[1];
		logger.logp(Level.FINE, caller.getClassName(), caller.getMethodName(), inMessage, thrown);
	}
	
	/**
	 * logDebug
	 * @param clazz
	 * @param method
	 * @param inMessage
	 */
	public static void logDebug(String clazz, String method, String inMessage) {
		logger.logp(Level.FINE, clazz, method, inMessage);
	}
	
	/**
	 * logDebug
	 * @param clazz
	 * @param method
	 * @param inMessage
	 * @param thrown
	 */
	public static void logDebug(String clazz, String method, String inMessage, Throwable thrown) {
		logger.logp(Level.FINE, clazz, method, inMessage, thrown);
	}

	/**
	 * logInfo
	 * Logs an info message.
	 * 
	 * @param inMessage - the String debug message to log
	 */
	public static void logInfo(String inMessage) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		StackTraceElement caller = elements[1];
		logger.logp(Level.INFO, caller.getClassName(), caller.getMethodName(), inMessage);
	}
	
	/**
	 * logInfo
	 * @param inMessage
	 * @param thrown
	 */
	public static void logInfo(String inMessage, Throwable thrown) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		StackTraceElement caller = elements[1];
		logger.logp(Level.INFO, caller.getClassName(), caller.getMethodName(), inMessage, thrown);
	}
	
	/**
	 * logInfo
	 * @param clazz
	 * @param method
	 * @param inMessage
	 */
	public static void logInfo(String clazz, String method, String inMessage) {
		logger.logp(Level.INFO, clazz, method, inMessage);
	}
	
	/**
	 * logInfo
	 * @param clazz
	 * @param method
	 * @param inMessage
	 * @param thrown
	 */
	public static void logInfo(String clazz, String method, String inMessage, Throwable thrown) {
		logger.logp(Level.INFO, clazz, method, inMessage, thrown);
	}

	/**
	 * logWarn
	 * Logs a warning message.  The action taken will depend on how log4j has been set up.
	 * By default, warn messages are printed to both stdout and to file.
	 *
	 * @param inMessage - the String warn message to log
	 */
	public static void logWarn(String inMessage) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		StackTraceElement caller = elements[1];
		logger.logp(Level.WARNING, caller.getClassName(), caller.getMethodName(), inMessage);
	}
	
	/**
	 * logWarn
	 * @param inMessage
	 * @param thrown
	 */
	public static void logWarn(String inMessage, Throwable thrown) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		StackTraceElement caller = elements[1];
		logger.logp(Level.WARNING, caller.getClassName(), caller.getMethodName(), inMessage, thrown);
	}
	
	/**
	 * logWarn
	 * @param clazz
	 * @param method
	 * @param inMessage
	 */
	public static void logWarn(String clazz, String method, String inMessage) {
		logger.logp(Level.WARNING, clazz, method, inMessage);
	}
	
	/**
	 * logWarn
	 * @param clazz
	 * @param method
	 * @param inMessage
	 * @param thrown
	 */
	public static void logWarn(String clazz, String method, String inMessage, Throwable thrown) {
		logger.logp(Level.WARNING, clazz, method, inMessage, thrown);
	}


	/**
	 * logError
	 * Logs an error message.  The action taken will depend on how log4j has been set up.
	 * By default, error messages are printed to both stdout and to file.
	 *
	 * @param inMessage - the String error message to log
	 */
	public static void logError(String inMessage) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		StackTraceElement caller = elements[1];
		logger.logp(Level.SEVERE, caller.getClassName(), caller.getMethodName(), inMessage);
	}
	
	/**
	 * logError
	 * @param inMessage
	 * @param thrown
	 */
	public static void logError(String inMessage, Throwable thrown) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		StackTraceElement caller = elements[1];
		logger.logp(Level.SEVERE, caller.getClassName(), caller.getMethodName(), inMessage, thrown);
	}
	
	/**
	 * logError
	 * @param clazz
	 * @param method
	 * @param inMessage
	 */
	public static void logError(String clazz, String method, String inMessage) {
		logger.logp(Level.SEVERE, clazz, method, inMessage);
	}
	
	/**
	 * logError
	 * @param clazz
	 * @param method
	 * @param inMessage
	 * @param thrown
	 */
	public static void logError(String clazz, String method, String inMessage, Throwable thrown) {
		logger.logp(Level.SEVERE, clazz, method, inMessage, thrown);
	}

	/**
	 * select
	 * Select all rows from the Log table, such that this user is allowed to view them.
	 * The userEID supplied is used to check the user's access level and organization,
	 * and only rows that the user is allowed to view are returned. For the log table,
	 * Admins can view all log entries, trusted users and users can only view entries
	 * with category INFO.
	 * 
	 * @param eid int representing a userEID
	 * @param limit
	 * @param offset
	 * @return Rows selected from the log table. If no rows were selected, the ResultSet may be null.
	 * @throws AuthenticationException - if unable to determine access level for the given userEID
	 */
	public static ResultSet select(AuthToken at, int limit, int offset) throws SQLException, AuthException {
		String query = null;
		User u = null;
		
		try {
			u = (User)((CachedObject)CacheService.getCache(at)).object;
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError("LogService.select: " + ae.getMessage());
			LogService.logError("LogService.select: " + ce.getMessage());
			throw ae;
		}
		
		int accessLevel = AccessAuthenticationService.findAID(u.getUserAccessToken());
		if (accessLevel == AuthLevel.ACCESS_ADMIN) {
			// admins can view all log entries
			// _lots_
			query = "SELECT * FROM Log ORDER BY LogMillis DESC LIMIT " + limit + " OFFSET " + offset;
		} else {
			// trusted users can only view "info" messages.
			query = "SELECT * FROM Log WHERE LogLevel='INFO' ORDER BY LogMillis DESC LIMIT " + limit + " OFFSET " + offset;
		}
		return Connections.select(serviceAuthToken, query, false);
	}

	/**
	 * select
	 * Select a specific row from the Log table, such that this user is allowed to view it.
	 * The userEID supplied is used to check the user's access level and organization,
	 * and only rows that the user is allowed to view are returned. For the log table,
	 * Admins can view any log entry, trusted users and users can only view entries
	 * with category INFO.
	 * 
	 * @param eid int representing a userEID
	 * @param key primary key for the log table
	 * @return Rows selected from the log table. If no rows were selected, the ResultSet may be null.
	 * @throws AuthenticationException - if unable to determine access level for the given userEID
	 */
	public static ResultSet select(AuthToken at, int key) throws SQLException, AuthException {
		String query = null;
		User u = null;
		
		try {
			u = (User)((CachedObject)CacheService.getCache(at)).object;
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError("LogService.select: " + ae.getMessage());
			LogService.logError("LogService.select: " + ce.getMessage());
			throw ae;
		}
		
		int accessLevel = AccessAuthenticationService.findAID(u.getUserAccessToken());
		if (accessLevel == AuthLevel.ACCESS_ADMIN) {
			// admins can view all log entries
			// _lots_
			query = "SELECT * FROM Log WHERE LogID=" + key;
		} else {
			// trusted users can only view "info" messages.
			query = "SELECT * FROM Log WHERE LogLevel='INFO' AND LogId=" + key + " ORDER BY LogMillis DESC";
		}	
		return Connections.select(serviceAuthToken, query, false);
	}
	
	/**
	 * getStatus
	 * 
	 * @return String
	 */
	public String getStatus() {
		return "LogService: debug=" + debug;
	}
}
