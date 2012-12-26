/*
 * CacheCollector.java
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

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.labfire.fe.log.LogService;

/**
 * CacheCollector
 * 
 * @author <a href = "http://labfire.com/">Labfire, Inc.</a>
 */
public class CacheCollector implements Runnable {
	private int sleepTime;
	private Map cache;
	
	
	public CacheCollector(Map cache, int sleepTime) {
		this.cache = cache;
		this.sleepTime = sleepTime;
	}

	public void run() {
		Set keySet;
		Iterator keys = null;
		Cacheable o;
		Object k;
		try {
			while (true) {
				Thread.sleep(sleepTime);
				try {
					synchronized (cache) {
						// Get the set of all keys that are in cache.  These are the unique identifiers
						keySet = cache.keySet();
						/* An iterator is used to move through the Keyset */
						keys = keySet.iterator();
						/* Sets up a loop that will iterate through each key in the KeySet */
						while (keys.hasNext()) {
							k = keys.next();
							o = (Cacheable)cache.get(k);
							if ((o == null) || (o.isExpired())) {
								keys.remove();
								// LogService.logDebug("Object " + k.toString() + " expired");
							}
							cache.wait(100);
						}
					}
				} catch (ConcurrentModificationException cme) {
					LogService.logWarn("ConcurrentModification of cache attempted", cme);
				} catch (Exception e) {
					LogService.logWarn("Unable to sweep cache", e);
					if (keys != null) {
						keys.remove();
						LogService.logWarn("Forced removal of last key");
					}
				}
			}
		} catch (InterruptedException ie) {
			LogService.logInfo("Halting cache sweep...");
		}
	}
	
	public String toString() {
		return "CacheCollector: " + cache;
	}
}