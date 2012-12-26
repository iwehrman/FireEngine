/*
 * Organization.java
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


package com.labfire.fe.common;

import java.io.Serializable;

/**
 * Organization
 * The Organization class represents a logical group of FireEngine users, and a 
 * single row in a Organization table of a database.
 * 
 * @author <a href = "http://labfire.com/">Labfire, Inc.</a>
 * @see com.labfire.fe.auth.OrgAuthenticationService
 */
public class Organization implements Serializable {
	private boolean locked = false;
	private int orgID;
	private String orgName;
	private String orgShortName;
	private String orgURL;
	private String orgAddress1;
	private String orgAddress2;
	private String orgCity;
	private String orgState;
	private String orgZip;
	private String orgCountry;
	private String orgPhone;
	private int orgDefaultAID;
	private java.util.Date orgDateCreated;
	private java.util.Date orgDateModified;
	
	/**
	 * getOrgID
	 * Get the org's ID
	 * 
	 * @return int
	 */
	public int getOrgID() {
		return orgID;
	}
	
	/**
	 * getOrgName
	 *
	 * @return String
	 */
	public String getOrgName() {
		return orgName;
	}
	
	/**
	 * getOrgName
	 *
	 * @return String
	 */
	public String getOrgShortName() {
		return orgShortName;
	}
	
	/**
	 * getOrgURL
	 * 
	 * @return String
	 */
	public String getOrgURL() {
		return orgURL;
	}
	
	/**
	 * getOrgAddress1
	 * 
	 * @return String
	 */
	public String getOrgAddress1() {
		return orgAddress1;
	}
	
	/**
	 * getOrgAddress2
	 * 
	 * @return String
	 */
	public String getOrgAddress2() {
		return orgAddress2;
	}
	
	/**
	 * getOrgCity
	 *
	 * @return String
	 */
	public String getOrgCity() {
		return orgCity;
	}
	
	/**
	 * getOrgState
	 *
	 * @return String
	 */
	public String getOrgState() {
		return orgState;
	}
	
	/**
	 * getOrgZip
	 *
	 * @return String
	 */
	public String getOrgZip() {
		return orgZip;
	}
	
	/**
	 * getOrgCountry
	 *
	 * @return String
	 */
	public String getOrgCountry() {
		return orgCountry;
	}

	/**
	 * getOrgPhone
	 *
	 * @return String
	 */
	public String getOrgPhone() {
		return orgPhone;
	}
	
	/**
	 * getOrgDefaultAID
	 * 
	 * @return int
	 */
	public int getOrgDefaultAID() {
		return orgDefaultAID;
	}

	/**
	 * getOrgDateCreated
	 *
	 * @return Date
	 */
	public java.util.Date getOrgDateCreated() {
		return orgDateCreated;
	}
	
	/**
	 * getOrgDateModified
	 *
	 * @return Date
	 */
	public java.util.Date getOrgDateModified() {
		return orgDateModified;
	}

	/* modifiers */
	
	/**
	 * setOrgID
	 */
	public void setOrgID(int orgID) {
		if (!locked) {
			this.orgID = orgID;
		}
	}
	
	/**
	 * setOrgName
	 */
	public void setOrgName(String orgName) {
		if (!locked) {
			this.orgName = orgName;
		}
	}
	
	/**
	 * setOrgShortName
	 */
	public void setOrgShortName(String orgShortName) {
		if (!locked) {
			this.orgShortName = orgShortName.toLowerCase();
		}
	}
	
	/**
	 * setOrgURL
	 */
	public void setOrgURL(String orgURL) {
		if (!locked) {
			this.orgURL = orgURL;
		}
	}
	
	/**
	 * setOrgAddress1
	 */
	public void setOrgAddress1(String orgAddress1) {
		if (!locked) {
			this.orgAddress1 = orgAddress1;
		}
	}
	
	/**
	 * setOrgAddress2
	 */
	public void setOrgAddress2(String orgAddress2) {
		if (!locked) {
			this.orgAddress2 = orgAddress2;
		}
	}
	
	/**
	 * setOrgCity
	 */
	public void setOrgCity(String orgCity) {
		if (!locked) {
			this.orgCity = orgCity;
		}
	}
	
	/**
	 * setOrgState
	 */
	public void setOrgState(String orgState) {
		if (!locked) {
			this.orgState = orgState;
		}
	}
	
	/**
	 * setOrgZip
	 */
	public void setOrgZip(String orgZip) {
		if (!locked) {
			this.orgZip = orgZip;
		}
	}
	
	/**
	 * setOrgCountry
	 */
	public void setOrgCountry(String orgCountry) {
		if (!locked) {
			this.orgCountry = orgCountry;
		}
	}
	
	/**
	 * setOrgPhone
	 */
	public void setOrgPhone(String orgPhone) {
		if (!locked) {
			this.orgPhone = orgPhone;
		}
	}
	
	/**
	 * setOrgDefaultAID
	 */
	public void setOrgDefaultAID(int orgDefaultAID) {
		if (!locked) {
			this.orgDefaultAID = orgDefaultAID;
		}
	}

	/**
	 * setOrgDateCreated
	 */
	public void setOrgDateCreated(java.util.Date orgDateCreated) {
		if (!locked) {
			this.orgDateCreated = orgDateCreated;
		}
	}
	
	/**
	 * setOrgDateModified
	 */
	public void setOrgDateModified(java.util.Date orgDateModified) {
		if (!locked) {
			this.orgDateModified = orgDateModified;
		}
	}

	/* methods */
	
	/**
	 * lock
	 */
	public void lock() {
		this.locked = true;
	}
	
	/**
	 * isLocked
	 *
	 * @return boolean
	 */
	public boolean isLocked() {
		return locked;
	}
	
	/**
	 * determines if this object equals another
	 *
	 * @param o another Object
	 * @return boolean
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Organization)) {
			return false;
		} else {
			Organization a = (Organization)o;
			if (orgID == a.getOrgID()
				&& orgName.equals(a.getOrgName())
				&& orgShortName.equals(a.getOrgShortName())
				&& orgURL.equals(a.getOrgURL())
				&& orgAddress1.equals(a.getOrgAddress1())
				&& orgAddress2.equals(a.getOrgAddress2())
				&& orgCity.equals(a.getOrgCity())
				&& orgState.equals(a.getOrgState())
				&& orgZip.equals(a.getOrgZip())
				&& orgCountry.equals(a.getOrgCountry())
				&& orgPhone.equals(a.getOrgPhone())
				&& orgDateCreated.equals(a.getOrgDateCreated())
				&& orgDateModified.equals(a.getOrgDateModified())) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * toString
	 * 
	 * @return String
	 */
	public String toString() {
		return "Organization: " + getOrgName();
	}
}
