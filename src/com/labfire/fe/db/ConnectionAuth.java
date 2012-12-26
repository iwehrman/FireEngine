/*
 * ConnectionAuth.java
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

import com.labfire.fe.auth.Constraint;

/**
 * ConnectionAuth
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class ConnectionAuth {
	private Constraint constraint;
	private ConnectionPool pool;

	public ConnectionAuth(ConnectionPool pool) {
		this.pool = pool;
	}

	public Constraint getConstraint() {
		return constraint;
	}
	
	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
	
	/**
	 * getPool
	 * 
	 * @return ConnectionPool
	 */
	public ConnectionPool getPool() {
		return pool;
	}
	
	/**
	 * setPool
	 */
	public void setPool(ConnectionPool pool) {
		this.pool = pool;
	}
	
	/**
	 * equals
	 * 
	 * @return boolean
	 */
	public boolean equals(ConnectionAuth ca) {
		if (!getPool().equals(ca.getPool()) || !constraint.equals(ca.getConstraint()))
			return false;
		else
			return true;		
	}
}