/*
 * LRUCache.java
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


package com.labfire.fe.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * LRUCache
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class LRUCache extends LinkedHashMap {
	protected int maxsize;
	// private static String CLASS = "com.labfire.fe.util.LRUCache";
	// private static String METHOD = "removeEldestEntry";
	// private static String MESSAGE = "Removing entry ";
	
	/**
	 * LRUCache
	 */
    public LRUCache(int maxsize) {
		super(maxsize*4/3 + 1, 0.75f, true);
		this.maxsize = maxsize;
    }
    
	/**
	 * removeEldestEntry
	 * 
	 * @return boolean
	 */
    protected boolean removeEldestEntry(Entry eldest) {
		if (size() > maxsize) {
			//LogService.logDebug(CLASS, METHOD, MESSAGE + eldest);
			return true;
		} else {
			return false;
		}
	}
}
