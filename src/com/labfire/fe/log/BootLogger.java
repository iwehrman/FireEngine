/*
 * BootLogger.java
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

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.labfire.fe.FireEngine;

/**
 * BootLogger
 * The BootLogger is designed to be used internally by the framework manager and
 * components at start-up time, i.e. when the main logging service has not yet been
 * initialized.
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class BootLogger {
	private static Logger logger;
	private static boolean debug = true;

	static {
		debug = FireEngine.getDebug();
		logger = Logger.getLogger("com.labfire.fe.boot");
		if (debug) {
			logger.setLevel(Level.FINE);
		} else {
			logger.setLevel(Level.INFO);
		}
		logger.setUseParentHandlers(false);
		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new OneLineFormatter());
		ch.setLevel(Level.FINE);
		logger.addHandler(ch);
	}

	/**
	 * logDebug
	 * @param inMessage
	 */
	public static void logDebug(String inMessage) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		StackTraceElement caller = elements[1];
		logger.logp(Level.FINE, caller.getClassName(), caller.getMethodName(), inMessage);
	}
	
	/**
	 * logDebug
	 * @param inMessage
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
	 * @deprecated
	 */
	public static void logDebug(String clazz, String method, String inMessage) {
		logger.logp(Level.FINE, clazz, method, inMessage);
	}
	
	/**
	 * logDebug
	 * @deprecated
	 */
	public static void logDebug(String clazz, String method, String inMessage, Throwable thrown) {
		logger.logp(Level.FINE, clazz, method, inMessage, thrown);
	}

	/**
	 * log
	 * @param inMessage
	 */
	public static void log(String inMessage) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		StackTraceElement caller = elements[1];
		logger.logp(Level.INFO, caller.getClassName(), caller.getMethodName(), inMessage);
	}
	
	/**
	 * log
	 * @param inMessage
	 * @param thrown
	 */
	public static void log(String inMessage, Throwable thrown) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		StackTraceElement caller = elements[1];
		logger.logp(Level.INFO, caller.getClassName(), caller.getMethodName(), inMessage, thrown);
	}
	
	/**
	 * log
	 * @deprecated
	 */
	public static void log(String clazz, String method, String inMessage) {
		logger.logp(Level.INFO, clazz, method, inMessage);
	}
	
	/**
	 * log
	 * @deprecated
	 */
	public static void log(String clazz, String method, String inMessage, Throwable thrown) {
		logger.logp(Level.INFO, clazz, method, inMessage, thrown);
	}
}
