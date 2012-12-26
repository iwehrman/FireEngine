/*
 * AuthException.java
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


package com.labfire.fe.auth;

import java.io.Serializable;

import com.labfire.fe.common.BaseException;

/**
 * AuthException
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class AuthException extends BaseException implements Serializable {

	/**
	 * AuthException
	 * 
	 * @param inMessage - descriptive error message indicating the source of the problem
	 * @param inThrowable - the caught exception to be wrapped
	 */
	public AuthException(String inMessage, Throwable inThrowable) {
		super(inMessage, inThrowable);
	}

	/**
	 * AuthException
	 * 
	 * @param inMessage - descriptive error message indicating the source of the problem
	 */
	public AuthException(String inMessage) {
		super(inMessage);
	}
}