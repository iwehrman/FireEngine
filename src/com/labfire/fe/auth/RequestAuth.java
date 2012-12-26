/*
 * RequestAuth
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

import java.util.HashSet;
import java.util.Set;

/**
 * RequestAuth
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class RequestAuth {
	private String URI;
	private Set constraints = new HashSet();

	/**
	 * getConstraints
	 *
	 * @return Set
	 */
	public Set getConstraints() {
		return constraints;
	}
	
	/**
	 * getURI
	 * 
	 * @return String
	 */
	public String getURI() {
		return URI;
	}
		
	/**
	 * setConstraint
	 */
	public void setConstraints(Set constraints) {
		this.constraints = constraints;
	}
	/**
	 * setURI
	 * Set the URI for this constraint
	 *
	 * @param URI URI for which this constraint applies
	 */
	public void setURI(String URI) {
		if ((URI.lastIndexOf("/") == (URI.length() - 1)) && !URI.equals("/") && URI.length() > 0) {
			URI = URI.substring(0, URI.length() - 1);
		}
		this.URI = URI;
	}
	
	/**
	 * equals
	 * 
	 * @return boolean
	 */
	public boolean equals(RequestAuth ra) {
		if (!getURI().equals(ra.getURI()) || !constraints.equals(ra.getConstraints())) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * toString
	 * 
	 * @return String
	 */
	public String toString() {
		return "RequestAuth: " + getURI();
	}
}