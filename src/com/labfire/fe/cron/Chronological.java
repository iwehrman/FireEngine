/*
 * Chronological.java
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

package com.labfire.fe.cron;

/**
 * Chronological
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public interface Chronological {
	/**
	 * execute
	 * execute returns true if the CronService should remove this
	 * Chronological interface from it's list after execution, and false
	 * if it should leave it in the list (ie: if isTime may still return true,
	 * ie: if this Chronological event is a repeating one).
	 * 
	 * @throws CronException if the execute method is unable to fire
	 */
	public boolean execute() throws CronException;
	
	/**
	 * isTime
	 * isTime returns true if it is time to execute this Chronological object
	 * (or if time has passed).
	 */
	public boolean isTime();
}