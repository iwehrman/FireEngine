/*
 * NoSuchVertexException.java
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

import com.labfire.fe.common.BaseException;

/**
 * NoSuchVertexException
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class NoSuchVertexException extends BaseException {

	/**
	 * NoSuchVertexException
	 * 
	 * @param inMessage - descriptive error message indicating the source of the problem
	 * @param inThrowable - the caught exception to be wrapped
	 */
	public NoSuchVertexException(String inMessage, Throwable inThrowable) {
		super(inMessage, inThrowable);
	}

	/**
	 * NoSuchVertexException
	 * 
	 * @param inMessage - descriptive error message indicating the source of the problem
	 */
	public NoSuchVertexException(String inMessage) {
		super(inMessage);
	}
}