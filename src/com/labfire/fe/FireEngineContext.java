/*
 * FireEngineContext.java
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


package com.labfire.fe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import org.jdom.Element;

import com.labfire.fe.auth.AuthToken;

/**
 * FireEngineContext
 * 
 * @author <a href = "http://labfire.com/">Labfire, Inc.</a>
 */


public class FireEngineContext {
	private String virtualRoot;
	private String errorPage;
	private String loginPage;
	private String admin;
	private String hostName;
	private ClassLoader classLoader;
	private Properties properties;
	private Map elements;
	private AuthToken at;
	
	private Map repositories = new HashMap();
	private LinkedList rlist = new LinkedList();
	
	public String getVirtualRoot() {
		return this.virtualRoot;
	}
	
	public String getErrorPage() {
		return this.errorPage;
	}
	
	/**
	 * @return String
	 */
	public String getLoginPage() {
		return loginPage;
	}
	
	public String getAdmin() {
		return this.admin;
	}
	
	public String getHostName() {
		return this.hostName;
	}
	
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}
	
	public String getProperty(String parameter) {
		return properties.getProperty(parameter);
	}
	
	Element getElement(String className) {
		return (Element)elements.get(className);
	}
	
	AuthToken getAuthToken() {
		return this.at;
	}
	
	public Iterator iterator() {
		return rlist.iterator();
	}
	
	public FireEngineRepository getRepository(String filename) {
		return (FireEngineRepository)repositories.get(filename);
	}
	
	public Map getRepositories() {
		return repositories;
	}
	
	void setVirtualRoot(String virtualRoot) {
		this.virtualRoot = virtualRoot;
	}
	
	void setErrorPage(String errorPage) {
		this.errorPage = errorPage;
	}
	
	/**
	 * Sets the loginPage.
	 * @param loginPage The loginPage to set
	 */
	void setLoginPage(String loginPage) {
		this.loginPage = loginPage;
	}
	
	void setAdmin(String admin) {
		this.admin = admin;
	}
	
	void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	void setElements(Map elements) {
		this.elements = elements;
	}
	
	void setAuthToken(AuthToken at) {
		this.at = at;
	}
	
	void addRepository(FireEngineRepository repository) {
		repositories.put(repository.getFilename(), repository);
		rlist.addFirst(repository);
	}
}
