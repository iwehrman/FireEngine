/*
 * AuthLevel.java
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

/**
 * AuthLevel
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class AuthLevel {
	public static final int ACCESS_USER = 1;
	public static final int ACCESS_TRUSTED_USER = 2;
	public static final int ACCESS_ADMIN = 3;
	public static final AuthLevel USER = new AuthLevel(ACCESS_USER);
	public static final AuthLevel TRUSTED_USER = new AuthLevel(ACCESS_TRUSTED_USER);
	public static final AuthLevel ADMIN = new AuthLevel(ACCESS_ADMIN);
	
	private int level;
	
	/**
	 * AuthLevel
	 */
	private AuthLevel(int level) {
		this.level = level;
	}
	
	/**
	 * equals
	 * 
	 * @return boolean
	 */
	public boolean equals(AuthLevel at) {
		return (this.level == at.getLevel());
	}
	
	/**
	 * getLevel
	 *
	 * @return int
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * toString
	 *
	 * @return String
	 */
	public String toString() {
		switch (level) {
			case ACCESS_ADMIN: 
				return "Admin";
			case ACCESS_TRUSTED_USER:
				return "Trusted User";
			case ACCESS_USER:
				return "User";
			default: 
				return "";
		}
	}
}