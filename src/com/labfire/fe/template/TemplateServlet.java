/*
 * TemplateServlet.java
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

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.FireEngineContext;
import com.labfire.fe.auth.AuthorizationException;
import com.labfire.fe.cache.CachedResponse;
import com.labfire.fe.cache.CachedResponseBundle;
import com.labfire.fe.cache.ResponseCompressor;
import com.labfire.fe.db.ConnectionService;
import com.labfire.fe.log.LogService;
import com.labfire.fe.util.BucketResponse;
import com.labfire.fe.util.Errors;
import com.labfire.fe.util.Servlets;
import com.labfire.fe.util.WorkQueue;

/**
 * TemplateServlet
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class TemplateServlet extends HttpServlet {
	private static final String CACHE_IGNORE = "FireEngine.IgnoreCache";
	private static final String METHOD_NAME = "getInstance";
	private static WorkQueue work = new WorkQueue();
	private static Map bundles;
	private static String requestParameter;

	/**
	 * init
	 * 
	 * @throws ServletException - if initialization fails
	 */
	public void init() throws ServletException {
		try {
			Thread compressThread = new Thread(new ResponseCompressor(work));
			compressThread.setPriority(Thread.MIN_PRIORITY);
			compressThread.start();
			bundles = TemplateService.bundles;
			requestParameter = TemplateService.getRequestParameter();
		} catch (Exception e) {
			LogService.logError("Unable to start compressThread.", e);
		}
	}

	/**
	 * doGet
	 * 
	 * 
	 * @param request HttpServletRequest object associated with a request
	 * @param response HttpServletResponse object associated with a request
	 * @throws IOException - if an input or output error is detected when the servlet handles the GET request
	 * @throws ServletException - if the request for the GET could not be handled
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String tempName;
		try {
			tempName = request.getParameter(requestParameter);
			if (tempName == null || tempName.length() == 0) {
				tempName = TemplateService.defaultTemplate;
			}
			Template template = TemplateService.select(tempName);
			if (template == null) {
				Errors.handleException(new TemplateException("No template found by name: " + tempName), request, response);
			} else {
				Block block;
				if (!TemplateService.isAuthorized(request, response, template)) {
					throw new AuthorizationException("Authorization failed, possibly due to insufficient permissions, for template " + template.getFile());
				}
				Iterator l = template.getBlocks().keySet().iterator();
				while (l.hasNext()) {
					block = (Block)template.getBlocks().get((String)l.next());
					if (!TemplateService.isAuthorized(request, response, block)) {
						throw new AuthorizationException("Authorization failed, possibly due to insufficient permissions, for content block " + block.getFile());
					}
				}
				
				response.addHeader("Cache-Control", "must-revalidate");
				response.addDateHeader("Expires", System.currentTimeMillis());
				String bundleKey = requestParameter + '=' + template.getName();
				CachedResponseBundle responseBundle = (CachedResponseBundle)bundles.get(bundleKey);
				if (responseBundle != null) {
					String responseString;
					if (request.getParameter(CACHE_IGNORE) != null) {
						LogService.logDebug("Cache IGNORE for " + bundleKey);
						prepareTemplate(request, response, template);
						RequestDispatcher rd = getServletContext().getRequestDispatcher(template.getFile());
						if (rd != null) {
							rd.forward(request, response);
						}
						return;
					}
					String cacheKey = '?' + request.getQueryString();
					CachedResponse cr = (CachedResponse)responseBundle.get(cacheKey);
					long lastModified = ConnectionService.getLastModified(responseBundle.getDependencies());
					if ((cr != null) && (responseBundle.getDependencies().size() == 0 || cr.timeGenerated > lastModified) 
							&& ((responseBundle.getExpiration() == 0) 
							|| ((System.currentTimeMillis() - cr.timeGenerated)/1000) < responseBundle.getExpiration())) {
						if (cr.getGzipResponse() != null) {
							String encodings = request.getHeader("Accept-Encoding");
							if (encodings != null && encodings.indexOf("gzip") != -1) {
								response.setHeader("Content-Encoding", "gzip");
								responseString = cr.getGzipResponse();
							} else {
								responseString = cr.getResponse();
							}
						} else {
							responseString = cr.getResponse();
						}
						response.setContentType(responseBundle.getContentType());
						response.setContentLength(responseString.length());
						response.setDateHeader("Last-Modified", lastModified);
						if (responseBundle.getExpiration() > 0) {
							response.setHeader("Cache-Control", "max-age=" + responseBundle.getExpiration());
							response.setDateHeader("Expires", System.currentTimeMillis() + (responseBundle.getExpiration()*1000));
						}
					} else {
						prepareTemplate(request, response, template);
						responseString = processTemplate(request, response, template, cacheKey);
					}
					PrintWriter out = response.getWriter();
					out.write(responseString);
				} else {
					prepareTemplate(request, response, template);
					RequestDispatcher rd = getServletContext().getRequestDispatcher(template.getFile());
					if (rd != null) {
						rd.forward(request, response);
					}
				}
			}
		} catch (Exception e) {
			Errors.handleException(e, request, response);
		}
	}
	
	private void prepareTemplate(HttpServletRequest request, HttpServletResponse response, Template template) throws TemplateException {
		String cName = null;
		Object bean;
		Block block;
		BlockParameter parameter;
		Iterator i = template.getProperties().entrySet().iterator();
		Map.Entry entry;
		while (i.hasNext()) {
			entry = (Map.Entry)i.next();
			request.setAttribute((String)entry.getKey(), entry.getValue());
		}
		Iterator l = template.getBlocks().keySet().iterator();
		while (l.hasNext()) {
			block = (Block)template.getBlocks().get((String)l.next());
			i = block.getParameters().iterator();
			while (i.hasNext()) {
				parameter = (BlockParameter)i.next();
				cName = parameter.getClassName();
				if (request.getAttribute(cName) == null) {
					try {
						bean = createBean(parameter, request, response);
						request.setAttribute(cName, bean);
					} catch (TemplateException te) {
						if (parameter.isRequired()) {
							throw te;
						}
					}
				}
			}
		}
	}
	
	private Object createBean(BlockParameter parameter, HttpServletRequest request, HttpServletResponse response) throws TemplateException {
		Class parameterClass;
		Object[] args = { request, response };
		Class[] parTypes = { HttpServletRequest.class, HttpServletResponse.class };
		TemplateFactory factory;
		Method factoryMethod;
		Object bean;
		
		try {
			FireEngineContext context = Servlets.getFireEngineContext(request);
			ClassLoader beanLoader = context.getClassLoader();
			parameterClass = beanLoader.loadClass(parameter.getClassName());
			factory = (TemplateFactory)parameterClass.newInstance();
			factoryMethod = parameterClass.getMethod(METHOD_NAME, parTypes);
			bean = factoryMethod.invoke(factory, args);
			return bean;
		} catch (Exception e) {
			TemplateException te = new TemplateException("Error preparing " + parameter.getClassName() + " object, possibly due to missing request parameter", e.getCause());
			LogService.logWarn(te.getMessage(), e.getCause());
			throw te;
		}
	}
	
	private String processTemplate(HttpServletRequest request, HttpServletResponse response, Template template, String cacheKey) throws TemplateException {
		LogService.logDebug("Cache MISS for " + cacheKey);
		String bundleKey = TemplateService.getRequestParameter() + "=" + template.getName();
		String responseString;
		CachedResponse cr;
		CachedResponseBundle responseBundle = null;
		BucketResponse bucket = new BucketResponse(response);
		RequestDispatcher rd = getServletContext().getRequestDispatcher(template.getFile());
		if (rd != null) {
			try {
				rd.forward(request, bucket);
			} catch (Exception e) {
				throw new TemplateException("Unable to forward to " + template.getFile(), e);
			}
		} else {
			throw new TemplateException("Unable to retrieve RequestDispatcher for " + template.getFile());
		}
		responseString = bucket.toString();
		responseBundle = (CachedResponseBundle)bundles.get(bundleKey);
		if (responseBundle == null) {
			TemplateException te = new TemplateException("Unable to find CachedResponseBundle for " + bundleKey);
			LogService.logWarn(te.getMessage());
			throw te;
		}
		cr = new CachedResponse(responseString);
		responseBundle.put(cacheKey, cr);
		work.addWork(cr);
		return responseString;
	}

	/**
	 * getLastModified
	 *
	 * @return long
	 */
	public long getLastModified(HttpServletRequest request) {
		String tempName = request.getParameter(TemplateService.getRequestParameter());
		if (tempName == null) {
			tempName = TemplateService.defaultTemplate;
		}
		Template template = TemplateService.select(tempName);
		if (template != null) {
			return ConnectionService.getLastModified(template.getDependencies())/1000*1000;
		} else {
			return System.currentTimeMillis();
		}
	}

	/**
	 * doPost
	 * 
	 * @param request HttpServletRequest object associated with a request
	 * @param response HttpServletResponse object associated with a request
	 * @throws IOException - if an input or output error is detected when the servlet handles the POST request
	 * @throws ServletException - if the request for the POST could not be handled
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
