/*
 * CompressFilter.java
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.log.LogService;

/**
 * CompressFilter
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class CompressFilter implements Filter {
	private static Map gzipCache = Collections.synchronizedMap(new LRUCache(50));
	private FilterConfig filterConfig;

	/**
	 * init
	 * 
	 * @throws ServletException - if the Filter could not be initialized
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	/**
	 * doFilter
	 * 
	 * @param request ServletRequest object associated with a request
	 * @param response ServletResponse object associated with a request
	 * @param chain FilterCHain object holding the webapp's list of Filters
	 */
	public void doFilter (ServletRequest request,
		ServletResponse response, FilterChain chain) {
		try {
			HttpServletRequest req = (HttpServletRequest)request;
			HttpServletResponse res = (HttpServletResponse)response;
			String encodings = req.getHeader("TE");
			if (encodings != null && encodings.indexOf("gzip") != -1) {
				String responseString;
				BucketResponse responseWrapper = new BucketResponse(res);
				chain.doFilter(request, responseWrapper);
				String wrapperString = responseWrapper.toString();
				Integer key = new Integer(wrapperString.hashCode());
				responseString = (String)gzipCache.get(key);
				if (responseString == null) {
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					GZIPOutputStream out = new GZIPOutputStream(bytes);
					byte[] in = wrapperString.getBytes();
					out.write(in, 0, in.length);
					out.finish();
					out.close();
					responseString = bytes.toString();
					gzipCache.put(key, responseString);
				}
				res.addHeader("Transfer-Encoding", "gzip");
				response.setContentType("text/html");
				response.getWriter().write(responseString);
			} else {
				chain.doFilter(request, response);
				return;	
			}
    	} catch (IOException io) {
			LogService.logError("Caught IOException", io);
			Errors.handleException(io, request, response);
		} catch (ServletException se) {
			LogService.logError("Caught ServletException", se);
			Errors.handleException(se, request, response);
		}
	}
	
	/**
	 * getFilterConfig
	 * 
	 * @return FilterConfig
	 */
	public FilterConfig getFilterConfig() {
		return this.filterConfig;
	}

	/**
	 * setFilterConfig
	 */
	public void setFilterConfig (FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}
  
	/**
	 * destroy
	 */
	public void destroy() {
		this.filterConfig = null;
	}
}
