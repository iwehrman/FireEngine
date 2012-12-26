/*
 * Constraint.java
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


/**
 * Constraint
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public final class Constraint {
	private int constraintID;
	private Integer ID = null;
	private Integer OID = null;
	private Integer AID = null;

	/**
	 * getConstraintID
	 *
	 * @return int
	 */
	public int getConstraintID() {
		return constraintID;
	}
	
	/**
	 * getID
	 * 
	 * @return Integer
	 */
	public Integer getID() {
		return ID;
	}
	
	/**
	 * getOID
	 * 
	 * @return Integer
	 */
	public Integer getOID() {
		return OID;
	}
	
	/**
	 * getAID
	 * 
	 * @return Integer
	 */
	public Integer getAID() {
		return AID;
	}
	
	/**
	 * setConstraintID
	 */
	public void setConstraintID(int constraintID) {
		this.constraintID = constraintID;
	}
	
	/**
	 * setID
	 */
	public void setID(Integer ID) {
		this.ID = ID;
	}
	
	/**
	 * setOID
	 */
	public void setOID(Integer OID) {
		this.OID = OID;
	}
	
	/**
	 * setAID
	 */
	public void setAID(Integer AID) {
		this.AID = AID;
	}
	
	public int hashCode() {
		return (ID == null ? 0 : ID.hashCode()) ^ (OID == null ? 11 : OID.hashCode()) ^ (AID == null ? 22 : AID.hashCode());
	}
	
	/**
	 * equals
	 * 
	 * @return boolean
	 */
	public boolean equals(Object o) {
		if (o instanceof Constraint) {
			Constraint c = (Constraint)o;
			if ((ID == null ? (c.getID() == null) : ID.equals(c.getID())) &&
				(OID == null ? (c.getOID() == null) : OID.equals(c.getOID())) &&
				(AID == null ? (c.getAID() == null) : AID.equals(c.getAID()))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * isNull
	 *
	 * @return boolean
	 */
	public boolean isNull() {
		if ((getID() != null) || (getOID() != null) || (getAID() != null)) {
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
		return "Constraint: " + "[id=" + getID() + ",oid=" + getOID() + ",aid=" + getAID() + "]";
	}
}