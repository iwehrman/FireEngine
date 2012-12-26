/*
 * ResponseCompressor.java
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.labfire.fe.log.LogService;
import com.labfire.fe.util.WorkQueue;

/**
 * ResponseCompressor
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class ResponseCompressor implements Runnable {
	private WorkQueue queue;
	
	/**
	 * ResponseCompressor
	 */
	public ResponseCompressor(WorkQueue queue) {
		this.queue = queue;
	}
	
	/**
	 * run
	 */
	public void run() {
		byte[] in;
		CachedResponse response;
		ByteArrayOutputStream bytes;
		GZIPOutputStream out;
		
		while (true) {
			try {
				response = (CachedResponse)queue.getWork();
				if (response != null && response.response != null) {
					bytes = new ByteArrayOutputStream();
					out = new GZIPOutputStream(bytes);
					in = response.response.getBytes();
					out.write(in, 0, in.length);
					out.finish();
					out.close();
					response.putGzipResponse(bytes.toString());
				}
			} catch (IOException ioe) {
				LogService.logWarn("Unable to compress response.", ioe);
			} catch (InterruptedException ie) {
				LogService.logWarn("Unable to get a response to compress.", ie);
			}
		}
	}
}