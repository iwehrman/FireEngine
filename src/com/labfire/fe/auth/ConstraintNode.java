/*
 * ConstraintNode.java
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ConstraintNode
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class ConstraintNode {
	private String path;
	private ConstraintNode parent = null;
	private Set constraints = new HashSet();
	private Map children = new HashMap();
	
	/**
	 * ConstraintNode
	 */
	public ConstraintNode(String path) {
		this.path = path;
	}
	
	public void addConstraint(Constraint constraint) {
		this.constraints.add(constraint);
	}
	
	public void addAllConstraints(Set constraints) {
		this.constraints.addAll(constraints);
	}	
	
	public Set getConstraints() {
		return constraints;
	}
	
	public String getPath() {
		return path;
	}
	
	public ConstraintNode getParent() {
		return parent;
	}
	
	public void setParent(ConstraintNode parent) {
		this.parent = parent;
	}
	
	public Map getChildren() {
		return children;
	}
	
	public void setChildren(Map children) {
		this.children = children;
	}
	
	public String toString() {
		return "ConstraintNode: " + path;
	}
}