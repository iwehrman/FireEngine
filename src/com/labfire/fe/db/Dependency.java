/*
 * Dependency.java
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


package com.labfire.fe.db;

/**
 * Dependency on a table in a database
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class Dependency {
	private String tableName;
	private String connectionName;
	
	public Dependency(String connectionName, String tableName) {
		this.tableName = tableName;
		this.connectionName = connectionName;
	}
	
	public String getConnectionName() {
		return this.connectionName;
	}
	
	public String getConnectionUrl() {
		return ConnectionService.getConnectionUrl(connectionName);
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	/**
	 * determines if this object equals another
	 *
	 * @param o another Object
	 * @return boolean
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Dependency)) {
			return false;
		} else {
			Dependency a = (Dependency)o;
			if (connectionName.equals(a.getConnectionName())
				&& tableName.equals(a.getTableName())) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * returns a hashcode for this object
	 *
	 * @return int
	 */
	public int hashCode() {
		return connectionName.hashCode() ^ tableName.hashCode();
	}
	
	/**
	 * return a String representation of this Project
	 *
	 * @return String
	 */
	public String toString() {
		return "Dependency: " + connectionName + "/" + tableName;
	}
}
