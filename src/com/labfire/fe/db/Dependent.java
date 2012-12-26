/*
 * Dependent.java
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


package com.labfire.fe.db;

import java.util.Iterator;
import java.util.Set;

/**
 * Dependent
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public abstract class Dependent {
	private boolean expired = false;
	
	/**
	 * getDependents
	 *
	 * @return Set
	 */
	public abstract Set getDependents();
	
	/**
	 * isExpired
	 *
	 * @return boolean
	 */
	public synchronized boolean isExpired() {
		if (expired) {
			return true;
		} else {
			Dependent child;
			Set dependents = getDependents();
			Iterator i = dependents.iterator();
			for (int size = dependents.size(); size > 0; size--) {
				child = (Dependent)i.next();
				if (child.isExpired()) {
					// LogService.logDebug("com.labfire.fe.db.Dependent", "isExpired", this + " Found an expired child - " + child);
					expired = true;
					return true;
				}
			}
			return false;
		}
	}
	
	protected void setExpired() {
		expired = true;
	}
}
