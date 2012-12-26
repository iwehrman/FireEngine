/*
 * TemplateService.java
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jdom.Element;

import com.labfire.fe.FireEngineComponent;
import com.labfire.fe.FireEngineConfig;
import com.labfire.fe.FireEngineContext;
import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.auth.AuthenticationService;
import com.labfire.fe.auth.AuthorizationService;
import com.labfire.fe.cache.CachedResponseBundle;
import com.labfire.fe.common.InitializeException;
import com.labfire.fe.db.Dependency;
import com.labfire.fe.log.LogService;
import com.labfire.fe.util.Servlets;

/**
 * TemplateService
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public final class TemplateService extends FireEngineComponent {
	private static final String IF_TEMPLATE_NAME = "com.labfire.fe.template.TemplateFactory";
	private static Class ifTemplateClass;
	private static String requestParameter;
	private static String servletName;
	private static Map templates = new HashMap();
	static Map bundles = new HashMap();
	private Element serviceElement;
	
	static String defaultTemplate;
	
	/**
	 * TemplateService
	 * 
	 * @param inConfigFileName String filename of the MailService properties file.
	 * @throws InitializeException - if the constructor is unable to initialize SecureRandom.
	 */
	public TemplateService(FireEngineConfig config) throws InitializeException {
		super(config);
		serviceElement = config.getConfigElement();
		Properties props = super.serviceProperties;
		
		if (props.getProperty("servletName") != null) {
			servletName = props.getProperty("servletName");
		} else {
			servletName = "servlet/" + TemplateServlet.class.getName();
		}
		
		if (props.getProperty("parameter") != null) {
			requestParameter = props.getProperty("parameter").trim();
		} else {
			requestParameter = "page";
		}
		
		if (props.getProperty("default") != null) {
			defaultTemplate = props.getProperty("default").trim();
		} else {
			defaultTemplate = null;
			LogService.logWarn("No default template specified");
		}

		try {
			ifTemplateClass = Class.forName(IF_TEMPLATE_NAME);
			addTemplates(serviceElement);
		} catch (ClassNotFoundException cnfe) {
			throw new InitializeException("Unable to find TemplateFactory class", cnfe);
		}
	}
	
	protected void unload() {
		templates.clear();
		bundles.clear();
	}
	
	/** 
	 * addTemplates
	 *
	 * @return int
	 */
	private int addTemplates(Element e) {
		int counter = 0;
		String templateName;
		String templateFile;
		String blockName;
		String blockFile;
		String className;
		List templateList;
		List blockList;
		List propList;
		List parameterList;
		Element templateElement;
		Element blockElement;
		Element parameterElement;
		Element propElement;
		Template template;
		Block block;
		BlockParameter parameter;
		CachedResponseBundle bundle;
		Dependency dep;
	
		templateList = e.getChildren("Template");
		for (Iterator i = templateList.iterator();i.hasNext(); ) {
			templateElement = (Element)i.next();
			try {
				templateName = templateElement.getAttributeValue("name");
				templateFile = templateElement.getAttributeValue("file");
				if (templateFile == null) {
					throw new TemplateException("No filename specified for template " + templateName);
				}
				template = new Template(templateName, templateFile);
				propList = templateElement.getChildren("Property");
				for (Iterator j = propList.iterator(); j.hasNext(); ) {
					propElement = (Element)j.next();
					template.addProperty(propElement.getAttributeValue("name"), propElement.getAttributeValue("value"));
				}
				blockList = templateElement.getChildren("Block");
				//if (blockList.isEmpty()) {
				//	throw new TemplateException("No content blocks specified for template " + templateName);
				//}
				for (Iterator j = blockList.iterator(); j.hasNext(); ) {
					blockElement = (Element)j.next();
					blockName = blockElement.getAttributeValue("name");
					blockFile = blockElement.getAttributeValue("file");
					if (blockFile == null) {
						throw new TemplateException("No filename for content block " + templateName + "/" + blockName);
					}
					propList = blockElement.getChildren("Property");
					for (Iterator h = propList.iterator(); h.hasNext(); ) {
						propElement = (Element)h.next();
						template.addProperty(propElement.getAttributeValue("name"), propElement.getAttributeValue("value"));
					}
					block = new Block(blockName, blockFile);
					parameterList = blockElement.getChildren("Parameter");
					for (Iterator k = parameterList.iterator(); k.hasNext(); ) {
						parameterElement = (Element)k.next();
						className = parameterElement.getAttributeValue("class");
						if (className == null) {
							throw new TemplateException("No class specified for parameter in block " + templateName + "/" + blockName);
						}
						if (!isTemplateFactory(className)) {
							throw new TemplateException("The class " + className + " specified for block " + templateName 
								+ "/" + blockName + " does not properly implement the interface " + IF_TEMPLATE_NAME + ".");
						}
						parameter = new BlockParameter(className);
						if (parameterElement.getAttributeValue("multiple") != null) {
							parameter.setMultiple(Boolean.valueOf(parameterElement.getAttributeValue("multiple")).booleanValue());
						}
						if (parameterElement.getAttributeValue("required") != null) {
							parameter.setRequired(Boolean.valueOf(parameterElement.getAttributeValue("required")).booleanValue());
						}
						block.addParameter(parameter);
					}
					parameterList = blockElement.getChildren("Dependency");
					for (Iterator k = parameterList.iterator(); k.hasNext(); ) {
						parameterElement = (Element)k.next();
						dep = new Dependency(parameterElement.getAttributeValue("connection"), parameterElement.getAttributeValue("table"));
						block.addDependency(dep);
					}
					template.addBlock(block);
				}
				if (Boolean.valueOf(templateElement.getAttributeValue("cache")).booleanValue()) {
					String key = requestParameter + "=" + templateName;
					try {
						if (templateElement.getAttributeValue("size") != null) {
							bundle = new CachedResponseBundle(key, Integer.parseInt(templateElement.getAttributeValue("size")));
						} else {
							bundle = new CachedResponseBundle(key);
						}
					} catch (NumberFormatException nfe) {
						bundle = new CachedResponseBundle(key);
					}
					if (templateElement.getAttributeValue("expires") != null) {
						try {
							bundle.setExpiration(Integer.parseInt(templateElement.getAttributeValue("expires")));
						} catch (NumberFormatException nfe) {}
					}
					if (templateElement.getAttributeValue("contentType") != null) {
						bundle.setContentType(templateElement.getAttributeValue("contentType"));
					}
					bundle.setDependencies(template.getDependencies());
					bundles.put(key, bundle);
				}
				templates.put(templateName, template);
				counter++;
			} catch (TemplateException te) {
				LogService.logWarn("Error adding Template", te);
			}
		}
		return counter;
	}
	
	/**
	 * select
	 *
	 * @return Template
	 */
	public static Template select(String template) {
		if (template == null) {
			return (Template)templates.get(defaultTemplate);
		} else {
			return (Template)templates.get(template);
		}
	}
	
	/** 
	 * select
	 * 
	 * @return Template
	 */
	public static Template select(HttpServletRequest request) {
		return select(request.getParameter(requestParameter));
	}

	/**
	 * isTemplateData
	 * 
	 * @return boolean
	 */
	private static boolean isTemplateFactory(String className) {
		try {
			// make sure it implements the proper interface
			Class tempClass = Class.forName(className);
			Class[] interfaces = tempClass.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				if (interfaces[i].equals(ifTemplateClass)) {
					return true;
				}
			}
		} catch (ClassNotFoundException cnfe) {
			LogService.logError("Caught ClassNotFoundException.", cnfe);
		}
		return false;
	}

	/**
	 * isAuthorized
	 *
	 * @return boolean
	 */
	public static boolean isAuthorized(HttpServletRequest request, HttpServletResponse response, Block block) {
		FireEngineContext context = Servlets.getFireEngineContext(request);
		return isAuthorized(request, response, context.getVirtualRoot() + block.getFile());
	}
	
	/**
	 * isAuthorized
	 *
	 * @return boolean
	 */
	public static boolean isAuthorized(HttpServletRequest request, HttpServletResponse response, Template template) {
		FireEngineContext context = Servlets.getFireEngineContext(request);
		return isAuthorized(request, response, context.getVirtualRoot() + template.getFile());
	}
	
	/**
	 * isAuthorized
	 *
	 * @return boolean
	 */
	private static boolean isAuthorized(HttpServletRequest request, HttpServletResponse response, String path) {
		boolean authorized = false;
		// LogService.logDebug("TemplateService.isAuthorized: Authorizing path " + path);
		if (AuthorizationService.hasNullConstraint(path)) {
			authorized = true;
		} else {
			try {
				AuthToken at = Servlets.getAuthToken(request);
				if (at == null || !AuthenticationService.isValidToken(at)) {
					String requestURI = request.getRequestURI();
					String qs = request.getQueryString();
					if (qs != null) {
						requestURI = requestURI + "?" + qs;
					}
					request.setAttribute("requestURI", requestURI);
					HttpSession session = request.getSession(true);
					RequestDispatcher rd = session.getServletContext().getRequestDispatcher("/servlet/com.labfire.fe.auth.AuthenticationServlet");
					if (rd != null) {
						rd.forward(request, response);
					}
					authorized = false;
				} else {
					authorized = AuthorizationService.authorizeRequest(at, path);
				}
			} catch (Exception e) {
				LogService.logWarn("Error checking authorization.", e);
			}
		}
		return authorized;	
	}
	
	/**
	 * getRequestParameter
	 * 
	 * @return String
	 */
	public static String getRequestParameter() {
		return requestParameter;
	}

	/**
	 * getStatus
	 *
	 * @return String
	 */
	public String getStatus() {
		return "TemplateService: templates=" + templates.size();
	}
	
	/**
	 * Returns the defaultTemplate.
	 * @return String
	 */
	public static String getDefaultTemplate() {
		return defaultTemplate;
	}

	/**
	 * @return String
	 */
	public static String getServletName() {
		return servletName;
	}
}
