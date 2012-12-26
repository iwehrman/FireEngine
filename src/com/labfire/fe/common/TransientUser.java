/*
 * TransientUser.java
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


package com.labfire.fe.common;

import java.io.Serializable;
import java.util.List;

/**
 * TransientUser
 * The TransientUser class represents a "light" user who may
 * or may not be currently logged in. TransientUser is a subclass
 * of user, and simply adds the UserID, UserOID, and UserAID fields,
 * (which are not used by most interfaces). typically, the UserEID,
 * UserEOID, and UserEAID fields will be null (as the user is likely
 * not actually logged in).
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 * @see com.labfire.fe.auth.AuthenticationService
 */
public class TransientUser extends User implements Serializable {
	private boolean locked = false;
	private int userID;
	private int userAID;
	private List userOIDs;

	/**
	 * getUserID
	 * 
	 * @return int
	 */
	public int getUserID() {
		return userID;
	}
	
	/**
	 * getUserOIDs
	 * 
	 * @return List
	 */
	public List getUserOIDs() {
		return userOIDs;
	}
	
	/**
	 * getUserAID
	 * 
	 * @return int
	 */
	public int getUserAID() {
		return userAID;
	}
	
	/**
	 * setUserID
	 */
	public void setUserID(int id) {
		if (!locked) {
			userID = id;
		}
	}
	
	/**
	 * setUserOIDs
	 */
	public void setUserOIDs(List id) {
		if (!locked) {
			userOIDs = id;
		}
	}
	
	/**
	 * setUserAID
	 */
	public void setUserAID(int id) {
		if (!locked) {
			userAID = id;
		}
	}
	
	/**
	 * lock
	 */
	public void lock() {
		super.lock();
		this.locked = true;
	}
	
	/**
	 * isLocked
	 *
	 * @return boolean
	 */
	public boolean isLocked() {
		return locked;
	}
	
	/**
	 * determines if this object equals another
	 *
	 * @param o another Object
	 * @return boolean
	 */
	public boolean equals(Object o) {
		if (!(o instanceof TransientUser)) {
			return false;
		} else {
			TransientUser a = (TransientUser)o;
			if (userID == a.getUserID() && super.equals(a)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * toString
	 * 
	 * @return String
	 */
	public String toString() {
		return "TransientUser: " + getUserName();
	}
}
