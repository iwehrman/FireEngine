/*
 * DependentCacheable.java
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

import java.util.Set;

import com.labfire.fe.db.ConnectionService;

/**
 * DependentCacheable
 * A Cache Object wrapper whose expiration is determined by a
 * set of database dependencies.
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class DependentCacheable implements Cacheable {
	private long creation = System.currentTimeMillis();
	private Object identifier;
	private Set deps;
	public Object object;
    
	/**
	 * DependentCacheable
	 */
	private DependentCacheable() {}
	
	/**
	 * DependentCacheable
	 */
    public DependentCacheable(Object obj, Object id, Set deps) {
		this.object = obj;
		this.identifier = id;
		this.deps = deps;
	}

	/**
	 * isExpired
	 *
	 * @return boolean
	 */
	public boolean isExpired() {
        return (ConnectionService.getLastModified(deps) > creation);
    }

	/**
	 * getDependencies
	 * 
	 * @return Set
	 */
	public Set getDependencies() {
		return deps;
	}
	
	/**
	 * getCreationTime
	 * 
	 * @return long
	 */
	public long getCreationTime() {
		return creation;
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
	 * determines if this object equals another
	 *
	 * @param o another Object
	 * @return boolean
	 */
	public boolean equals(Object o) {
		if (!(o instanceof DependentCacheable)) {
			return false;
		} else {
			DependentCacheable a = (DependentCacheable)o;
			if (object.equals(a.object)
				&& deps.equals(a.getDependencies())) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * toString
	 *
	 * @return toString
	 */
	public String toString() {
		return "DependentCacheable: " + object.toString();
	}
}

