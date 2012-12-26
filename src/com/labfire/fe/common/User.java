/*
 * User.java
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

import com.labfire.fe.auth.AuthToken;

/**
 * User
 * The User class represents a user of a webapp built with the FireEngine
 * framework, and a single row in a User table of a database. The User class is not a trusted class inside of FireEngine,
 * and is not trusted with it's own ID, organization ID, or access ID. 
 * Instead, the User carries encrypted IDs, which trusted portions of the
 * FireEngine will translate into real IDs before using them to query against
 * a database, or verify access restrictions.
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 * @see com.labfire.fe.auth.AuthenticationService
 */
public class User implements Serializable {
	private boolean locked = false;
	private boolean userLoginAllowed = false;
	private AuthToken userToken;
	private AuthToken userAccessToken;
	private List userOrgTokens;
	private String userName;
	private String userPass;
	private String userEmail;
	private String userFirstName;
	private String userLastName;
	private java.util.Date userDateCreated;
	private java.util.Date userDateModified;
	private java.util.Date userLastLoggedIn;
	
	/**
	 * isUserLoginAllowed
	 *
	 * @return boolean
	 */
	public boolean isUserLoginAllowed() {
		return userLoginAllowed;
	}
	
	/**
	 * getUserToken
	 * Get the user's encrypted ID
	 * 
	 * @return long
	 */
	public AuthToken getUserToken() {
		return userToken;
	}
	
	/**
	 * getUserOrgTokens
	 * Get the user's encrypted organization ID
	 *
	 * @return List
	 */
	public List getUserOrgTokens() {
		return userOrgTokens;
	}
	
	/**
	 * getUserAccessToken
	 * Get the user's encrypted access ID
	 *
	 * @return long
	 */
	public AuthToken getUserAccessToken() {
		return userAccessToken;
	}
	
	/**
	 * User
	 * Get the username
	 * 
	 * @return String
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * getUserPass
	 * Get the user's password
	 * 
	 * @return String
	 */	
	public String getUserPass() {
		return userPass;
	}

	/**
	 * getUserEmail
	 * Get the user's email address
	 *
	 * @return String
	 */
	public String getUserEmail() {
		return userEmail;
	}
	
	/**
	 * getUserLinkedEmail
	 * Get the user's email address, formatted as an anchor tag
	 *
	 * @return String
	 */
	public String getUserLinkedEmail() {
		return "<a href = \"mailto:" + getUserEmail() + "\">" + getUserWholeName() + " &lt;" + getUserEmail() + "&gt;</a>";
	}

	/**
	 * getUserFirstName
	 * Get the user's first name.
	 * 
	 * @return String
	 */
	public String getUserFirstName() {
		return userFirstName;
	}
	
	/**
	 * getUserLastName
	 * Get the user's last name.
	 *
	 * @return String
	 */
	public String getUserLastName() {
		return userLastName;
	}

	/**
	 * getUserWholeName
	 * Get the user's first and last name.
	 * 
	 * @return String
	 */
	public String getUserWholeName() {
		return userFirstName + " " + userLastName;
	}
	
	/**
	 * getUserDateCreated
	 * Get the date the user was created.
	 * 
	 * @return Date
	 */
	public java.util.Date getUserDateCreated() {
		return userDateCreated;
	}
	
	/**
	 * getUserDateModified
	 * Get the date the user was last modified.
	 *
	 * @return Date
	 */
	public java.util.Date getUserDateModified() {
		return userDateModified;
	}
	
	/**
	 * getUserLastLoggedIn
	 * Get the date the user was last logged in.
	 *
	 * @return Date
	 */
	public java.util.Date getUserLastLoggedIn() {
		return userLastLoggedIn;
	}	
	
	/**
	 * setUserLoginAllowed
	 * Set whether or not the user is allowed to log in
	 *
	 * @param boolean
	 */
	public void setUserLoginAllowed(boolean userLoginAllowed) {
		if (!locked) {
			this.userLoginAllowed = userLoginAllowed;
		}
	}
	
	/**
	 * setUserEID
	 * Set the user's encrypted ID.
	 *
	 * @param userEID new encrypted ID
	 */
	public void setUserToken(AuthToken userToken) {
		if (!locked) {
			this.userToken = userToken;
		}
	}
	
	/**
	 * setUserEOIDs
	 * Set the user's encrypted organization IDs.
	 *
	 * @param userEOIDs new encrypted organization IDs
	 */
	public void setUserOrgTokens(List userOrgTokens) {
		if (!locked) {
			this.userOrgTokens = userOrgTokens;
		}
	}
	
	/**
	 * setUserEAID
	 * Set the user's encrypted access ID.
	 * 
	 * @param userEAID new encrypted access ID
	 */
	public void setUserAccessToken(AuthToken userAccessToken) {
		if (!locked) {
			this.userAccessToken = userAccessToken;
		}
	}
	
	/**
	 * setUserName
	 */
	public void setUserName(String userName) {
		if (!locked) {
			this.userName = userName;
		}
	}
	
	/**
	 * setUserPass
	 */
	public void setUserPass(String userPass) {
		if (!locked) {
			this.userPass = userPass;
		}
	}
	
	/**
	 * setUserEmail
	 */
	public void setUserEmail(String userEmail) {
		if (!locked) {
			this.userEmail = userEmail;
		}
	}
	
	/**
	 * setUserFirstName
	 */
	public void setUserFirstName(String userFirstName) {
		if (!locked) {
			this.userFirstName = userFirstName;
		}
	}
	
	/**
	 * setUserLastName
	 */
	public void setUserLastName(String userLastName) {
		if (!locked) {
			this.userLastName = userLastName;
		}
	}
	
	/**
	 * setUserDateCreated
	 */
	public void setUserDateCreated(java.util.Date userDateCreated) {
		if (!locked) {
			this.userDateCreated = userDateCreated;
		}
	}
	
	/**
	 * setUserDateModified
	 */
	public void setUserDateModified(java.util.Date userDateModified) {
		if (!locked) {
			this.userDateModified = userDateModified;
		}
	}
	
	/**
	 * setUserLastLoggedIn
	 */
	public void setUserLastLoggedIn(java.util.Date userLastLoggedIn) {
		if (!locked) {
			this.userLastLoggedIn = userLastLoggedIn;
		}
	}

	/**
	 * lock
	 */
	public void lock() {
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
		if (!(o instanceof User)) {
			return false;
		} else {
			User a = (User)o;
			if (userName.equals(a.getUserName()) 
				&& userEmail.equals(a.getUserEmail())
				&& userFirstName.equals(a.getUserFirstName())
				&& userLastName.equals(a.getUserLastName())
				&& userDateCreated.equals(a.getUserDateCreated())
				&& userDateModified.equals(a.getUserDateModified())) {
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
		return "User: " + getUserName();
	}
}
