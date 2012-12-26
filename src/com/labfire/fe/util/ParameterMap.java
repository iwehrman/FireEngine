/*
 * ParameterMap.java
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
 
package com.labfire.fe.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * ParameterMap
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class ParameterMap extends HashMap implements Map {

	/**
	 * Constructor for ParameterMap.
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public ParameterMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructor for ParameterMap.
	 * @param initialCapacity
	 */
	public ParameterMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructor for ParameterMap.
	 */
	public ParameterMap() {
		super();
	}

	/**
	 * Constructor for ParameterMap.
	 * @param m
	 */
	public ParameterMap(Map m) {
		super(m);
	}
	
	/**
	 * get a parameter from the Map, following the semantics of Request.
	 * getParameter
	 * @param name
	 * @return String
	 */
	public String getParameter(String name) {
		try {
			Vector values = (Vector)get(name);
			if (values == null || values.size() == 0) {
				return null;
			}
			return (String)values.elementAt(values.size() - 1);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * add a parameter to the Map
	 * @param name
	 * @param value
	 */
	public void addParameter(String name, String value) {
		Vector existingValues = (Vector)get(name);
		if (existingValues == null) {
			existingValues = new Vector();
			put(name, existingValues);
		}
		existingValues.addElement(value);
	}

}
