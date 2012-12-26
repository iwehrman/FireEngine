/*
 * RequestAuthFactory
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

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.common.InitializeException;
import com.labfire.fe.template.TemplateFactory;
import com.labfire.fe.util.Servlets;

/**
 * RequestAuthFactory
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class RequestAuthFactory implements TemplateFactory {

	/**
	 * RequestAuth
	 */
	public RequestAuth getInstance(AuthToken at, String key) throws InitializeException {
		RequestAuth ra = new RequestAuth();
		try {
			ra.setURI(key);
			Constraint constraint;
			ResultSet rs = AuthorizationService.selectRequests(at, key);
			while (rs.next()) {
				constraint = new Constraint();
				constraint.setConstraintID(rs.getInt("ConstraintID"));
				if (rs.getInt("cID") != 0) {
					constraint.setID(new Integer(rs.getInt("cID")));
				}
				if (rs.getInt("cOID") != 0) {
					constraint.setOID(new Integer(rs.getInt("cOID")));
				}
				if (rs.getInt("cAID") != 0) {
					constraint.setAID(new Integer(rs.getInt("cAID")));
				}
				ra.getConstraints().add(constraint);
			}
			rs.close();
			return ra;
		} catch (Exception e) {
			throw new InitializeException("Unable to initialize RequestAuth", e);
		}
	}


	/**
	 * RequestAuth
	 */
	public Object getInstance(HttpServletRequest request, HttpServletResponse response) throws InitializeException {
		return getInstance(Servlets.getAuthToken(request),
			request.getParameter(RequestAuthFactory.class.getName()));
	}
}
