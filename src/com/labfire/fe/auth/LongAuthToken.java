/*
 * LongAuthToken.java
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

import com.labfire.fe.util.Strings;

/**
 * LongAuthToken
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class LongAuthToken implements Serializable, AuthToken {
	private String token;
	
	/**
	 * LongAuthToken
	 */
	public LongAuthToken(Long token) {
		this.token = Strings.encode(token.toString());
	}
	
	/**
	 * LongAuthToken
	 */
	public LongAuthToken(long token) {
		this.token = Strings.encode(new Long(token).toString());
	}
	
	/**
	 * LongAuthToken
	 */
	public LongAuthToken(String token) {
		this.token = token;
	}

	/**
	 * getToken
	 *
	 * @return Object
	 */
	public Object getToken() {
		return token;
	}
	
	public void setToken(Object token) {
		this.token = Strings.encode(token.toString());
	}
	
	/**
	 * equals
	 *
	 * @return boolean
	 */
	public boolean equals(Object at) {
		if (token != null && at != null) {
			return token.equals(((AuthToken)at).getToken());
		} else {
			return false;
		}
	}
	
	/**
	 * hashCode
	 *
	 * @return int
	 */
	public int hashCode() {
		return token.hashCode();
	}
	
	/**
	 * clone
	 *
	 * @return Object
	 */
	public Object clone() {
		return new LongAuthToken(this.token);
	}
	
	/**
	 * toString
	 *
	 * @return String
	 */
	public String toString() {
		return token;
	}
}