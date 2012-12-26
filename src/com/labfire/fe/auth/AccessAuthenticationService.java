/*
 * AccessAuthenticationService.java
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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import com.labfire.fe.FireEngineComponent;
import com.labfire.fe.FireEngineConfig;
import com.labfire.fe.common.InitializeException;
import com.labfire.fe.log.LogService;

/**
 AccessAuthenticationService keeps track of AID/EAID pairs
 for access levels.

 @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class AccessAuthenticationService extends FireEngineComponent {
	private static Map tokens = new HashMap(); //these refer to each other
	private static Map aids = new HashMap();
	private static SecureRandom rand;

	/**
	 * AccessAuthenticationService
	 */
 	public AccessAuthenticationService(FireEngineConfig config) throws InitializeException {
  		super(config);
		try {
			rand = SecureRandom.getInstance("SHA1PRNG");
			rand.setSeed(new java.util.Date().getTime());
		} catch (NoSuchAlgorithmException nsae) {
			InitializeException ie = new InitializeException("Cannot get SecureRandom instance.", nsae);
			LogService.logError(ie.getMessage(), nsae);
			throw ie;
		}
		
		// init the eaids once.
		for (int i = 1; i <= 3; i++) {
			addToken(i);
		}
	}
	
	protected void unload() {
		tokens.clear();
		aids.clear();
	}

	/**
	 * isValidToken
	 *
	 * @return long
	 */
	public static boolean isValidToken(AuthToken at) {
		try {
			findAID(at);
			return true;
		} catch (AuthenticationException ae) {
			return false;
		}
	}
	
	public static AuthLevel getAuthLevel(int level) throws AuthenticationException {
		switch (level) {
			case AuthLevel.ACCESS_USER:
				return AuthLevel.USER;
			case AuthLevel.ACCESS_TRUSTED_USER:
				return AuthLevel.TRUSTED_USER;
			case AuthLevel.ACCESS_ADMIN:
				return AuthLevel.ADMIN;
			default:
				throw new AuthenticationException("No corresponding AuthLevel");
		}
	}
	
	/**
	 * findAID
	 * Translates an EAID into an AID.
	 *
	 * @param eid int representing a EAID
	 * @return The corresponding AID
	 * @throws AuthenticationException - if EAID doesn't match an AID
	 */
	public static synchronized int findAID(AuthToken at) throws AuthenticationException {
		Integer aid = (Integer)aids.get(at);
		if (aid == null) {
			AuthenticationException ae = new AuthenticationException("No corresponding AID");
			throw ae;
		}
		return aid.intValue();
	}
	
	/**
	 * findToken
	 *
	 * @return AuthToken
	 */
	static synchronized AuthToken findToken(int aid) throws AuthenticationException {
		AuthToken at = (AuthToken)tokens.get(new Integer(aid));
		if (at == null) {
			AuthenticationException ae = new AuthenticationException("No corresponding Token");
			throw ae;
		}
		return at;
	}
	
	/**
	 * addToken
	 *
	 * @return AuthToken
	 */
	static synchronized AuthToken addToken(int aid) {
		Integer i = new Integer(aid);
		AuthToken at;
		
		try {
			// return the eoid already in the list
			at = findToken(aid);
		} catch (AuthenticationException ae) {
			// eoid isn't in the list, add a new one and return it
			at = new LongAuthToken(rand.nextLong());
			tokens.put(i, at);
			aids.put(at, i);
		}
		return at;
	}
	
	public static String getAIDName(int i) {
		switch (i) {
			case AuthLevel.ACCESS_ADMIN: 
				return "Admin";
			case AuthLevel.ACCESS_TRUSTED_USER:
				return "Trusted User";
			case AuthLevel.ACCESS_USER:
				return "User";
			default: 
				return "";
		}
	}
	
	public String getStatus() {
		return "AccessAuthenticationService: tokens=" + tokens.size();
	}
}
