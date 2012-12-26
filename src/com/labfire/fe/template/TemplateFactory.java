/*
 * TemplateFactory.java
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


package com.labfire.fe.template;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.common.InitializeException;

/**
 * TemplateFactory
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public interface TemplateFactory {
	public Object getInstance(HttpServletRequest request, HttpServletResponse response) throws InitializeException;
}
