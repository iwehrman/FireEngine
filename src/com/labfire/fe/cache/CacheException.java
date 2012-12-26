/*
 * CacheException.java
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

import java.io.Serializable;

import com.labfire.fe.common.BaseException;

/**
 * CacheException
 * The CacheException is thrown by objects in the cache package.  Since this 
 * exception extends the framework base exception, the underlying cause can be saved and 
 * re-inspected by the calling code.
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class CacheException extends BaseException implements Serializable {

	/**
	 * CacheException
	 * 
	 * @param inMessage - descriptive error message indicating the source of the problem
	 * @param inThrowable - the caught exception to be wrapped
	 */
	public CacheException(String inMessage, Throwable inThrowable) {
		super(inMessage, inThrowable);
	}

	/**
	 * CacheException
	 * 
	 * @param inMessage - descriptive error message indicating the source of the problem
	 */
	public CacheException(String inMessage) {
		super(inMessage);
	}
}