/*
 * CronService.java
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


package com.labfire.fe.cron;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import com.labfire.fe.FireEngineComponent;
import com.labfire.fe.FireEngineConfig;
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
import com.labfire.fe.log.LogService;

/**
 * CronService
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public final class CronService extends FireEngineComponent {
	private static List jobs = Collections.synchronizedList(new LinkedList());
	private Thread cronThread;

	/**
	 * CronService
	 */
	public CronService(FireEngineConfig config) throws InitializeException {
		super(config);
		int sleepTime;
		
		try {
			sleepTime = Integer.parseInt(config.getConfigProps().getProperty("sleepTime"));
		} catch (NumberFormatException nfe) {
			sleepTime = 60000;
		}
		
		try {
			cronThread = new Thread(new CronScheduler(jobs, sleepTime), "cronThread");
			cronThread.setPriority(Thread.MIN_PRIORITY);
			cronThread.setDaemon(true);
			cronThread.start();
		} catch(Exception e) {
			  LogService.logWarn("Unable to start cronThread.", e);
		}
	}
	
	protected void unload() {
		cronThread.interrupt();
		jobs.clear();
	}
	
	/**
	 * putCron
	 */
	public static void putCron(Chronological c) {
		jobs.add(c);
	}
	
	/** 
	 * select
	 * 
	 * @return List
	 */
	public static List select(AuthToken at) throws AuthException {
		User u = null;
		
		try {
			u = (User)((CachedObject)CacheService.getCache(at)).object;
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		
		int accessLevel = AccessAuthenticationService.findAID(u.getUserAccessToken());
		if (accessLevel == AuthLevel.ACCESS_ADMIN) {
			// admins can view all cron objects
			return Collections.unmodifiableList(jobs);
		} else {
			// no one else can view anything
			return null;
		}
	}
	
	/**
	 * delete
	 * 
	 * @return int
	 */
	public static int delete(AuthToken at) throws AuthException {
		User u = null;
		int deleted = -1;
		
		try {
			u = (User)((CachedObject)CacheService.getCache(at)).object;
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		
		int accessLevel = AccessAuthenticationService.findAID(u.getUserAccessToken());
		if (accessLevel == AuthLevel.ACCESS_ADMIN) {
			synchronized (jobs) {
				try {
					deleted = jobs.size();
					jobs.clear();
				} catch (ConcurrentModificationException cme) {
					LogService.logError("Unable to remove cron entry.", cme);
				}
			}
		}
		return deleted;
	}
	
	/**
	 * getStatus
	 * 
	 * @return String
	 */
	public String getStatus() {
		return "CronService: entries=" + jobs.size();
	}
}
