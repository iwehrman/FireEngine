/*
 * ConstraintFactory.java
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
 * ConstraintFactory
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class ConstraintFactory implements TemplateFactory {
	/**
	 * Constraint
	 */
	public Constraint getInstance(AuthToken at, int key) throws InitializeException {
		Constraint c = new Constraint();
		try {
			ResultSet rs = AuthorizationService.selectConstraints(at, key);
			rs.next();
			c.setConstraintID(rs.getInt("ConstraintID"));
			if (rs.getInt("cID") != 0) {
				c.setID(new Integer(rs.getInt("cID")));
			}
			if (rs.getInt("cOID") != 0) {
				c.setOID(new Integer(rs.getInt("cOID")));
			}
			if (rs.getInt("cAID") != 0) {
				c.setAID(new Integer(rs.getInt("cAID")));
			}
			rs.close();
			return c;
		} catch (Exception e) {
			throw new InitializeException("Unable to initialize Constraint", e);
		}
	}


	/**
	 * Constraint
	 */
	public Object getInstance(HttpServletRequest request, HttpServletResponse response) throws InitializeException {
		int id;
		try {
			id = Integer.parseInt(request.getParameter(ConstraintFactory.class.getName()));
		} catch (Exception e) {
			throw new InitializeException("Unable to initialize Constraint");
		}
		return getInstance(Servlets.getAuthToken(request), id);
	}
}
