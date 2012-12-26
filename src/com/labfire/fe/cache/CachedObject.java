/*
 * CachedObject.java
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


package com.labfire.fe.cache;

import java.util.Date;

/**
 * CachedObject
 * A Generic Cache Object wrapper.  Implements the Cacheable
 * interface
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class CachedObject implements Cacheable {
	public Object object;
	
	private final Date dateCreated = new Date();
	private Object identifier;
    private Date dateofExpiration;
    
	/**
	 * CachedObject
	 */
	private CachedObject() {}
	
	/**
	 * CachedObject
	 */
    public CachedObject(Object obj, Object id, int secondsToLive) {
		dateofExpiration = null;
		this.object = obj;
		this.identifier = id;
		setExpiration(secondsToLive);
	}
	
	/**
	 * CachedObject
	 */
    public CachedObject(Object obj, long id, int secondsToLive) {
		this(obj, new Long(id), secondsToLive);
	}

	/**
	 * isExpired
	 *
	 * @return boolean
	 */
	public boolean isExpired() {
        if (dateofExpiration != null) {
			// date of expiration is compared.
			return (dateofExpiration.getTime() < System.currentTimeMillis());
        } else { // it lives forever
          return false;
	  }
    }
	
	/**
	 * setExpiration
	 */
	public void setExpiration(int secondsToLive) {
		// secondsToLive of 0 means it lives forever
		if (secondsToLive != 0) {
			if (dateofExpiration == null) {
				dateofExpiration = new Date(System.currentTimeMillis() + (1000*secondsToLive));
			} else {
				dateofExpiration.setTime(System.currentTimeMillis() + (1000*secondsToLive));
			}
		} else {
			dateofExpiration = null;
		}
	}
	
	/**
	 * getExpiration
	 * 
	 * @return Date
	 */
	public Date getExpiration() {
		return dateofExpiration;
	}
	
	/**
	 * getCreation
	 * 
	 * @return Date
	 */
	public Date getCreation() {
		return dateCreated;
	}

	/**
	 * getIdentifier
	 *
	 * @return Object
	 */
    public Object getIdentifier() {
      return identifier;
    }
	
	/**
	 * toString
	 *
	 * @return toString
	 */
	public String toString() {
		return object.toString();
	}
}

