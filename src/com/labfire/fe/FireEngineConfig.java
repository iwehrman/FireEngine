/*
 * FireEngineConfig.java
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


package com.labfire.fe;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jdom.Element;

import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.log.BootLogger;
import com.labfire.fe.util.Strings;

public class FireEngineConfig	{
	private Element configElement;
	private Properties configProps;
	private AuthToken serviceAuthToken;
	private FireEngineContext fireEngineContext;
	
	/**
	 * FireEngineConfig
	 * 
	 */
	public FireEngineConfig(FireEngineContext context, String className) {
		configElement = context.getElement(className);
		configProps = XMLtoProperties(configElement);
		serviceAuthToken = (AuthToken)context.getAuthToken().clone();
		fireEngineContext = context;
	}
	
	/**
	 * XMLtoProperties
	 * 
	 * @return Properties
	 */
	public static Properties XMLtoProperties(Element e) {
		Properties p = new Properties();
		Element current;
		String key;
		String value;
		
		// If a null Element is passed in, just return an empty Properties object
		if (e == null) {
			return p;
		}
			
		List props = e.getChildren("Property");
		
		for (Iterator i = props.iterator(); i.hasNext(); ) {
			current = (Element)i.next();
			key = current.getAttributeValue("name");
			value = current.getAttributeValue("value");
			
			// for some reason getAttributeValue is escaping backslashes...
			value = Strings.replaceString(value, "\\n", "\n"); //XXX
			if ((key != null) && (value != null)) {
				p.setProperty(key, value);
			} else {
				BootLogger.logDebug("Incorrectly formatted Property element: " + key + " : " + value);
			}
		}
		return p;
	}

	/**
	 * getServiceEID
	 *
	 * @return Long
	 */
	public AuthToken getServiceAuthToken() {
		return serviceAuthToken;
	}
	
	/**
	 * getConfigElement
	 *
	 * @return Element
	 */
	public Element getConfigElement() {
		return configElement;
	}
	
	/**
	 * getConfigProps
	 *
	 * @return Properties
	 */	
	public Properties getConfigProps() {
		return configProps;
	}
	
	public FireEngineContext getFireEngineContext() {
		return fireEngineContext;
	}
}
