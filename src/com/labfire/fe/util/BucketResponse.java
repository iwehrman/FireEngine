/*
 * BucketResponse.java
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


package com.labfire.fe.util;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * BucketResponse
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class BucketResponse extends HttpServletResponseWrapper {
	private CharArrayWriter bucket = new CharArrayWriter();
	
	/**
	 * BucketResponse
	 */
	public BucketResponse(HttpServletResponse response) {
		super(response);
	}
	
	/**
	 * getWriter
	 * 
	 * @return PrintWriter
	 */
	public PrintWriter getWriter() {
		return new PrintWriter(bucket);
	}
	
	/**
	 * flushBuffer
	 */
	public void flushBuffer() {}
	
	/**
	 * toCharArray
	 *
	 * @return char[]
	 */
	public char[] toCharArray() {
		return bucket.toCharArray();
	}
	
	/**
	 * toString
	 *
	 * @return String
	 */
	public String toString() {
		return bucket.toString();
	}
}