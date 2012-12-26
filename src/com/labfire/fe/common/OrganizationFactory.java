/*
 * OrganizationFactory.java
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

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.auth.OrgAuthenticationService;
import com.labfire.fe.template.TemplateFactory;
import com.labfire.fe.util.Servlets;

/**
 * OrganizationFactory
 * The Organization class represents a logical group of FireEngine users, and a 
 * single row in a Organization table of a database.
 * 
 * @author <a href = "http://labfire.com/">Labfire, Inc.</a>
 * @see com.labfire.fe.auth.OrgAuthenticationService
 */
public class OrganizationFactory implements TemplateFactory {
	/**
	 * getInstance
	 * 
	 * create organization object
	 */
	public Organization getInstance(AuthToken at, int orgID) throws InitializeException {
		Organization o = new Organization();
		try {
			ResultSet rs = OrgAuthenticationService.select(at, orgID);
			rs.next();
			o.setOrgID(rs.getInt("OrgID"));
			o.setOrgName(rs.getString("OrgName"));
			o.setOrgShortName(rs.getString("OrgShortName"));
			o.setOrgURL(rs.getString("OrgURL"));
			o.setOrgAddress1(rs.getString("OrgAddress1"));
			o.setOrgAddress2(rs.getString("OrgAddress2"));
			o.setOrgCity(rs.getString("OrgCity"));
			o.setOrgState(rs.getString("OrgState"));
			o.setOrgZip(rs.getString("OrgZip"));
			o.setOrgCountry(rs.getString("OrgCountry"));
			o.setOrgPhone(rs.getString("OrgPhone"));
			o.setOrgDefaultAID(rs.getInt("OrgDefaultAID"));
			o.setOrgDateCreated(rs.getTimestamp("OrgDateCreated"));
			o.setOrgDateModified(rs.getTimestamp("OrgDateModified"));
			o.lock();
			rs.close();
			return o;
		} catch (Exception e) {
			throw new InitializeException("Unable to initialize Organization", e);
		}
	}
	
	/**
	 * getInstance
	 * 
	 * create organization object
	 */
	public Organization getInstance(AuthToken at, String shortName) throws InitializeException {
		Organization o = new Organization();
		try {
			ResultSet rs = OrgAuthenticationService.select(at, shortName);
			rs.next();
			o.setOrgID(rs.getInt("OrgID"));
			o.setOrgName(rs.getString("OrgName"));
			o.setOrgShortName(rs.getString("OrgShortName"));
			o.setOrgURL(rs.getString("OrgURL"));
			o.setOrgAddress1(rs.getString("OrgAddress1"));
			o.setOrgAddress2(rs.getString("OrgAddress2"));
			o.setOrgCity(rs.getString("OrgCity"));
			o.setOrgState(rs.getString("OrgState"));
			o.setOrgZip(rs.getString("OrgZip"));
			o.setOrgCountry(rs.getString("OrgCountry"));
			o.setOrgPhone(rs.getString("OrgPhone"));
			o.setOrgDefaultAID(rs.getInt("OrgDefaultAID"));
			o.setOrgDateCreated(rs.getTimestamp("OrgDateCreated"));
			o.setOrgDateModified(rs.getTimestamp("OrgDateModified"));
			o.lock();
			rs.close();
			return o;
		} catch (Exception e) {
			throw new InitializeException("Unable to initialize Organization", e);
		}
	}
	
	/**
	 * getInstance
	 * 
	 * create organization object
	 */
	public Object getInstance(HttpServletRequest request, HttpServletResponse response) throws InitializeException {
		try {
			try {
				int id = Integer.parseInt(request.getParameter(OrganizationFactory.class.getName()));
				return getInstance(Servlets.getAuthToken(request), id);
			} catch (NumberFormatException nfe) {
				return getInstance(Servlets.getAuthToken(request), request.getParameter(OrganizationFactory.class.getName()));		
			}
		} catch (Exception e) {
			throw new InitializeException("Unable to initialize Organization", e);
		}
	}
}
