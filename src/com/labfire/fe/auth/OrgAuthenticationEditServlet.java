/*
 * OrgAuthenticationEditServlet.java
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

import javax.servlet.http.HttpServletRequest;

import com.labfire.fe.common.Organization;
import com.labfire.fe.util.UpdateException;
import com.labfire.fe.util.UpdateServlet;

/**
 * OrgAuthenticationEditServlet
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class OrgAuthenticationEditServlet extends UpdateServlet {

	/**
	 * processForm
	 * 
	 * @return int
	 */
	public int processForm(AuthToken at, HttpServletRequest request) throws UpdateException {
		try {
			Organization o = new Organization();
			o.setOrgName(request.getParameter("orgName"));
			o.setOrgShortName(request.getParameter("orgShortName"));
			o.setOrgURL(request.getParameter("orgURL"));
			o.setOrgAddress1(request.getParameter("orgAddress1"));
			o.setOrgAddress2(request.getParameter("orgAddress2"));
			o.setOrgCity(request.getParameter("orgCity"));
			o.setOrgState(request.getParameter("orgState"));
			o.setOrgZip(request.getParameter("orgZip"));
			o.setOrgCountry(request.getParameter("orgCountry"));
			o.setOrgPhone(request.getParameter("orgPhone"));
			o.setOrgDefaultAID(Integer.parseInt(request.getParameter("orgDefaultAID")));
			if (!request.getParameter("orgID").toLowerCase().equals("new")) {
				o.setOrgID(Integer.parseInt(request.getParameter("orgID")));
				return OrgAuthenticationService.update(at, o);
			} else
				return OrgAuthenticationService.insert(at, o);
		} catch (Exception e) {
			throw new UpdateException("Unable to update Organization.", e);
		}
	}
}
