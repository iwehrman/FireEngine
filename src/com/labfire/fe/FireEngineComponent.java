/*
 * FireEngineComponent.java
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

import java.util.Properties;

import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.common.InitializeException;

/**
 * FireEngineComponent
 * 
 * This class  holds the minimal functionality that all framework components (services)
 * are expected to provide.  Developers who want to add their own custom services must
 * extend this class to meet the framework SPI requirements.  See the documentation for
 * more details on how to add a new service to the framework.
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public abstract class FireEngineComponent {	
	protected static AuthToken serviceAuthToken;
	protected Properties serviceProperties;
	
	/**
	 * default no-args constructor is made private to make sure that service providers
	 * call the correct Constructor (protected FrameworkComponent(String inPropertiesFileName)).
	 */
	private FireEngineComponent() {}
	
	/**
	 * @throws InitializeException - if the service could not be initialized.  Per-service
	 * error information is wrapped inside this exception.
	 */
	protected FireEngineComponent(FireEngineConfig config) throws InitializeException {
		serviceProperties = config.getConfigProps();
		serviceAuthToken = config.getServiceAuthToken();
	}
	
	protected void unload() {}
	
	protected void postCreate() throws InitializeException {}
	
	/**
	 * Provides the subclass with access to it's FireEngine configuration environment.
	 * @param inPropertyKey - the key of the property to retrieve
	 * @return String - the value of the requested key
	 */
	protected String getProperty(String inPropertyKey) {
		return serviceProperties.getProperty(inPropertyKey);
	}
	
	/**
	 * This method should be overridden to return status information pertinent to each 
	 * particular framework service.
	 */
	public abstract String getStatus();
}