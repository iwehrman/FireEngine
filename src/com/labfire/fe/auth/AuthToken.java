/*
 * AuthToken.java
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
 * AuthToken
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public interface AuthToken extends Cloneable,Serializable {
	/**
	 * getToken
	 *
	 * @return Object
	 */
	public Object getToken();
	
	/**
	 * getToken
	 *
	 * @return Object
	 */
	public void setToken(Object token);
	
	/**
	 * equals
	 *
	 * @return boolean
	 */
	public boolean equals(Object at);
	
	/**
	 * clone
	 *
	 * @return Object
	 */
	public Object clone();
	
	/**
	 * toString
	 *
	 * @return String
	 */
	public String toString();
}