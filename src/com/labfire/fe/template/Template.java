/*
 * Template.java
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Template
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class Template {
	private String name;
	private String file;
	private Map blocks = new HashMap();
	private Set dependencies = new HashSet();
	private Map properties = new HashMap();
	
	/**
	 * Template
	 */
	private Template() {}
	
	/**
	 * Template
	 */
	public Template(String name, String file) {
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
	 * getBlocks
	 *
	 * @return Map
	 */
	public Map getBlocks() {
		return blocks;
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
	 * setBlocks
	 */
	public void setBlocks(Map blocks) {
		this.blocks = blocks;
	}
	
	/**
	 * addBlock
	 */
	public void addBlock(Block b) {
		this.blocks.put(b.getName(), b);
		Iterator i = b.getDependencies().iterator();
		while (i.hasNext()) {
			dependencies.add(i.next());
		}
	}
	
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	public String getProperty(String key) {
		return (String)properties.get(key);
	}
	
	public Map getProperties() {
		return properties;
	}
	
	/**
	 * toString
	 * 
	 * @return String
	 */
	public String toString() {
		return "Template[" + name + "]: " + blocks;
	}
}