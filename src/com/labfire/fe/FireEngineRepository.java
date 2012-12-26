/*
 * FireEngineRepository.java
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

import java.util.Comparator;
import java.util.List;

/**
 * FireEngineRepository
 * 
 * @author <a href = "http://labfire.com/">Labfire, Inc.</a>
 */


public class FireEngineRepository {
	private String filename;
	private List registeredServices = null;
	private Comparator comparator = null;
	
	public FireEngineRepository(String filename) {
		this.filename = filename;
	}
	
	public FireEngineRepository(String filename, Comparator comparator) {
		this.filename = filename;
		this.comparator = comparator;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public List getRegisteredServices() {
		return this.registeredServices;
	}
	
	public void setRegisteredServices(List services) {
		this.registeredServices = services;
	}
	
	public Comparator getComparator() {
		return this.comparator;
	}
	
	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}
	
	public boolean equals(Object o) {
		if (o instanceof FireEngineRepository
			&& ((FireEngineRepository)o).getFilename().equals(getFilename())) {
			return true;
		} else {
			return false;
		}
	}
}