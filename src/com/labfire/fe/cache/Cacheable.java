/*
 * Cacheable.java
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

/**
 * Cacheable
 * This interface defines the methods, which must be implemented by
 * all objects wishing to be placed in the cache.
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public interface Cacheable {
	/* 
	 * By requiring all objects to determine their own expirations, the
	 * algorithm is abstracted from the caching service, thereby providing maximum
	 * flexibility since each object can adopt a different expiration strategy.
	 */
	
	/**
	 * isExpired
	 * 
	 * @return boolean which tells if the object has expired, as far as the cache is concerned.
	 */
	public boolean isExpired();

	/**
	 * getIdentifier
	 * 
	 * @return Object identifier of the object in the cache.
	 */
	public Object getIdentifier();
}
