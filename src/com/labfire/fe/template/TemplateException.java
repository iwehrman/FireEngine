/*
 * TemplateException.java
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

import java.io.Serializable;

import com.labfire.fe.common.BaseException;

/**
 * TemplateException
 * The TemplateException is thrown by classes in the Template package.  Since this 
 * exception extends the framework base exception, the underlying cause can be saved and 
 * re-inspected by the calling code.
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class TemplateException extends BaseException implements Serializable {

	/**
	 * TemplateException
	 * 
	 * @param inMessage - descriptive error message indicating the source of the problem
	 * @param inThrowable - the caught exception to be wrapped
	 */
	public TemplateException(String inMessage, Throwable inThrowable) {
		super(inMessage, inThrowable);
	}

	/**
	 * TemplateException
	 * 
	 * @param inMessage - descriptive error message indicating the source of the problem
	 */
	public TemplateException(String inMessage) {
		super(inMessage);
	}
}