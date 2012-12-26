/*
 * CachedResponse.java
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


package com.labfire.fe.cache;

/**
 * CachedResponse
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class CachedResponse {
	public String response = null;
	public String gzipResponse;
	public long timeGenerated;
	
	/**
	 * CachedResponse
	 */
	public CachedResponse(String response) {
		this.response = response;
		timeGenerated = System.currentTimeMillis();
	}
	
	/**
	 * getResponse
	 *
	 * @return String
	 */
	public String getResponse() {
		return response;
	}
	
	/**
	 * getGzipResponse
	 *
	 * @return String
	 */
	public String getGzipResponse() {
		synchronized (gzipResponse) {
			return gzipResponse;
		}
	}
	
	/**
	 * putGzipResponse
	 */
	public void putGzipResponse(String gzipResponse) {
		synchronized (gzipResponse) {
			this.gzipResponse = gzipResponse;
		}
	}	
}