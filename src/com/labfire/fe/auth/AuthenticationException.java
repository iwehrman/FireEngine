/*
 * AuthenticationException.java
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

/**
 * AuthenticationException
 *
 * @author <a href="http://labfire.com">Labfire, Inc.</a>
 */
public class AuthenticationException extends AuthException implements Serializable {

	/**
	 * AuthenticationException
	 * 
	 * @param inMessage - descriptive error message indicating the source of the problem
	 * @param inThrowable - the caught exception to be wrapped
	 */
	public AuthenticationException(String inMessage, Throwable inThrowable) {
		super(inMessage, inThrowable);
	}

	/**
	 * AuthenticationException 
	 * 
	 * @param inMessage - descriptive error message indicating the source of the problem
	 */
	public AuthenticationException(String inMessage) {
		super(inMessage);
	}
}