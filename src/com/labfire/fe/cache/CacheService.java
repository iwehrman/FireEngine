/*
 * CacheService.java
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


package com.labfire.fe.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jdom.Element;

import com.labfire.fe.FireEngineComponent;
import com.labfire.fe.FireEngineConfig;
import com.labfire.fe.auth.AccessAuthenticationService;
import com.labfire.fe.auth.AuthException;
import com.labfire.fe.auth.AuthLevel;
import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.auth.AuthenticationException;
import com.labfire.fe.common.InitializeException;
import com.labfire.fe.common.User;
import com.labfire.fe.db.Dependency;
import com.labfire.fe.log.LogService;

/**
 * CacheService
 * CacheService keeps holds a HashMap of Cacheable objects, for
 * the system to use. The property intervalms
 * determines how long to sleep inbetween sweeps of the HashMap to
 * look for expired items to remove. 
 * 
 * @author <a href = "http://labfire.com/">Labfire, Inc.</a>
 * @see com.labfire.fe.cache.Cacheable
 * @see com.labfire.fe.cache.CachedObject
 */
public final class CacheService extends FireEngineComponent {
	private static Map lockedCache = Collections.synchronizedMap(new HashMap());
	private Thread cacheThread;
	static HashMap bundles = new HashMap();
	
	/**
	 * CacheService
	 */
 	public CacheService(FireEngineConfig config) throws InitializeException {
  		super(config);
		CachedResponseBundle responseBundle;
		Element parent,current,child;
		Dependency dep;
		int size;
		int sleepTime;
		
		try {
			sleepTime = Integer.parseInt(config.getConfigProps().getProperty("sleepTime"));
		} catch (NumberFormatException nfe) {
			sleepTime = 30000;
		}
				
		parent = config.getConfigElement();
		List children = parent.getChildren("CachedResponseBundle");
		ListIterator deps;
		ListIterator li = children.listIterator();
		
		while (li.hasNext()) {
			current = (Element)li.next();
			try {
				size = Integer.parseInt(current.getAttributeValue("size"));
			} catch (NumberFormatException nfe) {
				size = 25;
			}
			responseBundle = new CachedResponseBundle(current.getAttributeValue("uri"), size);
			try {
				responseBundle.setExpiration(Integer.parseInt(current.getAttributeValue("expires")));
			} catch (NumberFormatException nfe) {
				responseBundle.setExpiration(0);
			}
			if (current.getAttributeValue("contentType") != null) {
				responseBundle.setContentType(current.getAttributeValue("contentType"));
			}
			deps = current.getChildren("Dependency").listIterator();
			while (deps.hasNext()) {
				child = (Element)deps.next();
				dep = new Dependency(child.getAttributeValue("connection"), child.getAttributeValue("table"));
				responseBundle.addDependency(dep);
			}
			bundles.put(responseBundle.getRequestURI(), responseBundle);
		}
		
		try {
			// Create background thread responsible for purging expired items.
			cacheThread = new Thread(new CacheCollector(lockedCache, sleepTime), "cacheThread");
			cacheThread.setPriority(Thread.MIN_PRIORITY);
			cacheThread.setDaemon(true);
			cacheThread.start();
		} catch (Exception e) {
			LogService.logError("Unable to start cacheThread", e);
		}
	}
	
	protected void unload() {
		lockedCache.clear();
		cacheThread.interrupt();
		bundles.clear();
	}

	/**
	 * putCache
	 */
  	public static void putCache(Cacheable object) {
		lockedCache.put(object.getIdentifier(), object);
	}
	
	/**
	 * getCache
	 * Retrieves a Cacheable item from the cache. The item is not removed from the cache
	 * (that only happens when the item expires). 
	 * 
	 * @return a Cacheable object from the cache
	 * @param key for the item in the cache
	 * @throws CacheException if there is no item in the cache with the specified key
	 */
	public static Cacheable getCache(Object identifier) throws CacheException {
		Cacheable object = (Cacheable)lockedCache.get(identifier);

    	if (object == null) {
			throw new CacheException("Object not found in cache.");		
		}
    	if (object.isExpired()) {
			lockedCache.remove(identifier);
			LogService.logDebug("Object " + identifier + " expired at " + new java.util.Date());
			throw new CacheException("Object found in cache, but has expired.");
    	} else {
			return object;
    	}
	}
	
	public static Cacheable getCache(long identifier) throws CacheException {
		return getCache(new Long(identifier));
	}
	
	/**
	 * select
	 * 
	 * @return Map
	 */
	public static Map select(AuthToken at) throws AuthException {
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
			// admins can view all cache objects
			return Collections.unmodifiableMap(lockedCache);
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
	public static int delete(AuthToken at, long key) throws AuthException {
		User u = null;
		int deleted = 0;
		
		try {
			u = (User)((CachedObject)CacheService.getCache(at)).object;
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		
		int accessLevel = AccessAuthenticationService.findAID(u.getUserAccessToken());
		if (accessLevel == AuthLevel.ACCESS_ADMIN) {
			Object o;
			// admins can delete any cache objects
			o = lockedCache.remove(new Long(key));
			if (o != null)
				deleted = 1;
		}
		return deleted;
	}

	/**
	 * getStatus
	 * 
	 * @return String
	 */
	public String getStatus() {
		return "CacheService: entries=" + lockedCache.size();
	}
}
