/*
 * CacheFilter.java
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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.db.ConnectionService;
import com.labfire.fe.log.LogService;
import com.labfire.fe.util.BucketResponse;
import com.labfire.fe.util.Errors;
import com.labfire.fe.util.WorkQueue;

/**
 * CacheFilter
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class CacheFilter implements Filter {
	private WorkQueue work = new WorkQueue();
	private FilterConfig filterConfig;
	private static final int TTL = 60*5;
	private static final int BUNDLE_SIZE = 25;
	private static final String CACHE_IGNORE = "FireEngine.IgnoreCache";
	private static Map bundles;

	/**
	 * init
	 * 
	 * @throws ServletException - if the Filter could not be initialized
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		bundles = CacheService.bundles;
		try {
			Thread compressThread = new Thread(new ResponseCompressor(work));
			compressThread.setPriority(Thread.MIN_PRIORITY);
			compressThread.start();
		} catch (Exception e) {
			LogService.logError("Unable to start compressThread.", e);
		}
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
			if (!req.getMethod().equalsIgnoreCase("GET")) {
				chain.doFilter(request, response);
				return;
			}
			String responseString = null;
			StringBuffer requestURI = new StringBuffer(req.getRequestURI());
			String queryString = req.getQueryString();
			if (queryString != null) {
				requestURI.append('?');
				requestURI.append(queryString);
			}
			String cacheKey = requestURI.toString();
			CachedResponseBundle responseBundle = (CachedResponseBundle)bundles.get(req.getRequestURI());
			if (responseBundle != null) {
				if (req.getParameter(CACHE_IGNORE) != null) {
					LogService.logDebug("Cache IGNORE for " + cacheKey);
					chain.doFilter(request, response);
					return;
				}
				CachedResponse cr = (CachedResponse)responseBundle.get(cacheKey);
				long lastModified = ConnectionService.getLastModified(responseBundle.getDependencies());
				if ((cr != null) && (responseBundle.getDependencies().size() == 0 || cr.timeGenerated > lastModified) 
							&& ((responseBundle.getExpiration() == 0) 
							|| ((System.currentTimeMillis() - cr.timeGenerated)/1000) < responseBundle.getExpiration())) {
					if (cr.getGzipResponse() != null) {
						String encodings = ((HttpServletRequest)request).getHeader("Accept-Encoding");
						if (encodings != null && encodings.indexOf("gzip") != -1) {
							((HttpServletResponse)response).setHeader("Content-Encoding", "gzip");
							responseString = cr.getGzipResponse();
						} else {
							responseString = cr.getResponse();
						}
					} else {
						responseString = cr.getResponse();
					}
					response.setContentType(responseBundle.getContentType());
					response.setContentLength(responseString.length());
					((HttpServletResponse)response).setDateHeader("Last-Modified", lastModified);
					if (responseBundle.getExpiration() > 0) {
						((HttpServletResponse)response).setHeader("Cache-Control", "max-age=" + responseBundle.getExpiration());
						((HttpServletResponse)response).setDateHeader("Expires", System.currentTimeMillis() + (responseBundle.getExpiration()*1000));
					}
				} else {
					responseString = cacheResponse(chain, (HttpServletRequest)request, (HttpServletResponse)response, cacheKey);
				}
				PrintWriter out = response.getWriter();
				out.write(responseString);
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

	/**
	 * cacheResponse
	 *
	 * @return String
	 */
	private String cacheResponse(FilterChain chain, HttpServletRequest request, HttpServletResponse response, String cacheKey) 
			throws ServletException, IOException {
		LogService.logDebug("Cache MISS for " + cacheKey);
		BucketResponse responseWrapper = new BucketResponse(response);
		chain.doFilter(request, responseWrapper);
		String responseString = responseWrapper.toString();
		CachedResponseBundle responseBundle = (CachedResponseBundle)bundles.get(request.getRequestURI());
		if (responseBundle == null) {
			LogService.logWarn("Unable to find CachedResponseBundle for " + request.getRequestURI());
			return null;
		} else {
			CachedResponse cr = new CachedResponse(responseString);
			responseBundle.put(cacheKey, cr);
			work.addWork(cr);
			return responseString;
		}
	}
}
