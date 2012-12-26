/*
 * FireEngineComparator.java
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


package com.labfire.fe;


/**
 * FireEngineComparator
 * 
 * @author <a href = "http://labfire.com/">Labfire, Inc.</a>
 */


public class FireEngineComparator extends ServiceComparator {
	private String[] firstClassNames = { "com.labfire.fe.db.ConnectionService", 
		"com.labfire.fe.log.LogService", 
		"com.labfire.fe.cache.CacheService",
		"com.labfire.fe.auth.AccessAuthenticationService",
		"com.labfire.fe.auth.OrgAuthenticationService",
		"com.labfire.fe.auth.AuthenticationService" };
	private String[] lastClassNames = new String[0];
	
	public FireEngineComparator() {
		super();
		super.setFirstClassNames(firstClassNames);
		super.setLastClassNames(lastClassNames);
	}
}