/*
 * CachedResponseBundle.java
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

import java.util.HashSet;
import java.util.Set;

import com.labfire.fe.db.Dependency;
import com.labfire.fe.util.LRUCache;

/**
 * CachedResponseBundle
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class CachedResponseBundle extends LRUCache implements Cacheable {
	private static final int DEFAULT_SIZE = 25;
	private int size;
	private int expires = 0;
	private Object identifier;
	private String requestURI;
	private String contentType = "text/html";
	private Set dependencies = new HashSet();
	
	/**
	 * CachedResponseBundle
	 */
	private CachedResponseBundle() {
		super(DEFAULT_SIZE); // only for compilation
	}
	
	/**
	 * CachedResponseBundle
	 */
	public CachedResponseBundle(String requestURI, int size) {
		super(size);
		this.requestURI = requestURI;
		this.identifier = requestURI;
	}
	
	/**
	 * CachedResponseBundle
	 */
	public CachedResponseBundle(String requestURI) {
		this(requestURI, DEFAULT_SIZE);
	}
	
	/**
	 * getRequestURI
	 *
	 * @return String
	 */
	public String getRequestURI() {
		return requestURI;
	}
	
	/**
	 * getExpiration
	 * 
	 * @return int
	 */
	public int getExpiration() {
		return expires;
	}
	
	/**
	 * getContentType
	 *
	 * @return String
	 */
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * getDependencies
	 *
	 * @return Set
	 */
	public Set getDependencies() {
		return dependencies;
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
	 * isExpired
	 * 
	 * @return boolean
	 */
	public boolean isExpired() {
		return false;
	}
	
	/** 
	 * setExpiration
	 */
	public void setExpiration(int expires) {
		this.expires = expires;
	}
	
	/**
	 * setContentType
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * setDependencies
	 */
	public void setDependencies(Set deps) {
		this.dependencies = deps;
	}

	/**
	 * addDependency
	 */
	public void addDependency(Dependency dep) {
		dependencies.add(dep);
	}
		
	/**
	 * toString
	 *
	 * @return String
	 */
	public String toString() {
		return "CachedResponseBundle:" + requestURI;
	}
}