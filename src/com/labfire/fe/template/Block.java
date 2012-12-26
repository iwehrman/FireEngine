/*
 * Block.java
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


package com.labfire.fe.template;

import java.util.HashSet;
import java.util.Set;

import com.labfire.fe.db.Dependency;

/**
 * Block
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class Block {
	private String name;
	private String file;
	private Set parameters = new HashSet();
	private Set dependencies = new HashSet();
	
	/**
	 * Block
	 */
	private Block() {}
	
	/**
	 * Block
	 */
	public Block(String name, String file) {
		this.name = name;
		this.file = file;
	}
	
	/**
	 * getName
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * getFile
	 * 
	 * @return String
	 */
	public String getFile() {
		return file;
	}
	
	/**
	 * getParameters
	 * 
	 * @return Map
	 */
	public Set getParameters() {
		return parameters;
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
	 * setName
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * setFile
	 */
	public void setFile(String file) {
		this.file = file;
	}
	
	/**
	 * addParameter
	 */
	public void addParameter(BlockParameter parameter) {
		parameters.add(parameter);
	}
	
	/**
	 * addDependency
	 */
	public void addDependency(Dependency dependency) {
		dependencies.add(dependency);
	}
	
	/**
	 * toString
	 * 
	 * @return String
	 */
	public String toString() {
		return "Block: " + name + ":" + file;
	}
}