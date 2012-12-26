/*
 * BaseException.java
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


package com.labfire.fe.common;

import java.io.Serializable;

/**
 * BaseException
 * The BaseException class is used across all core framework services and is also
 * suitable for use by developers extending the framework.
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class BaseException extends Exception implements Serializable {
	
	/**
	 * BaseException
	 */
	public BaseException() {}
	
	/**
	 * BaseException
	 */
	public BaseException(String inMessage) {
		super(inMessage);
	}
	
	/**
	 * BaseException
	 */
	public BaseException(Throwable inThrowable) {
		super(inThrowable);
	}

	/**
	 * BaseException
	 */
	public BaseException(String inMessage, Throwable inThrowable) {
		super(inMessage, inThrowable);
	}

	/**
	 * getNextException
	 * 
	 * @return Throwable
	 */
	public Throwable getNextException() {
		return super.getCause();
	}
}