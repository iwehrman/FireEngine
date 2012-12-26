/*
 * FireEngineContextListenerImpl.java
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

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.labfire.fe.common.InitializeException;
import com.labfire.fe.log.BootLogger;

/**
 * FireEngineContextListenerImpl
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public abstract class FireEngineContextListenerImpl implements ServletContextListener {
	FireEngine fe;

	public FireEngineContextListenerImpl() {
		fe = FireEngine.getInstance();
	}

	/**
	 * contextDestroyed
	 */
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		deregister(event);
		context.removeAttribute("FireEngineContext");
		BootLogger.log("Deregistered context " + context.getRealPath("/"));
	}

	/**
	 * contextInitialized
	 */
	public void contextInitialized(ServletContextEvent event) {
		List repositories = new LinkedList();
		String repository = "FireEngine-libs.jar";
		String comparatorName = "com.labfire.fe.FireEngineComparator";
		ServletContext context = event.getServletContext();
		ClassLoader cl = this.getClass().getClassLoader();
		repositories.add(buildRepository(context, repository, comparatorName));
		
		repository = context.getInitParameter("repository");
		if (repository != null) {
			comparatorName = context.getInitParameter("comparator");
			repositories.add(buildRepository(context, repository, comparatorName));
		}
		
		try {
			FireEngineContext feContext = register(event, cl, repositories);
			context.setAttribute("FireEngineContext", feContext);
			BootLogger.log("Registered context " + context.getRealPath("/"));
		} catch (Exception e) {
			BootLogger.log("Unable to register context " + context.getRealPath("/"), e);
		}
	}
	
	protected FireEngineRepository buildRepository(ServletContext context, String repository, String comparatorName) {
		StringBuffer filename = new StringBuffer(context.getRealPath("/"));
		if (repository.endsWith(".jar")) {
			filename.append("/WEB-INF/lib/");
		} else {
			filename.append("/WEB-INF/classes/");
		}
		filename.append(repository);
		FireEngineRepository fr = new FireEngineRepository(filename.toString());
		if (comparatorName != null) {
			try {
				Class comparatorClass = this.getClass().getClassLoader().loadClass(comparatorName);
				fr.setComparator((Comparator)comparatorClass.newInstance());
			} catch (Exception e) {
				BootLogger.log("Unable to instantiate comparator " + comparatorName, e);
			}
		}
		return fr;
	}
  
	/**
	 * register
	 * 
	 * @synchronized
	 */ 
	protected synchronized final FireEngineContext register(ServletContextEvent event, ClassLoader cl, List repositories) throws InitializeException {
		ServletContext context = event.getServletContext();
		if (context == event.getSource()) {
			return fe.register(context.getRealPath("/"), cl, repositories);
		} else {
			throw new InitializeException("Registration failed, event from source other than ServletContext");
		}
	}
	
	/**
	 * deregister
	 * 
	 * @synchronized
	 */
	protected synchronized final void deregister(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		if (context == event.getSource()) {
			fe.deregister(context.getRealPath("/"));
		} else {
			BootLogger.log("Registration failed, event from source other than ServletContext");
		}
	}
}
