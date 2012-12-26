/*
 * BlockParameter.java
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


/**
 * BlockParameter
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class BlockParameter {
	private boolean required = true;
	private boolean multiple = false;
	private String className;
	
	public BlockParameter() {}
	
	public BlockParameter(String className) {
		this.className = className;
	}
	
	public BlockParameter(String className, boolean required, boolean multiple) {
		this.className = className;
		this.required = required;
		this.multiple = multiple;
	}
	
	public String getClassName() {
		return className;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public boolean isMultiple() {
		return multiple;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
}