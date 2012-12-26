/*
 * FireEngine.java
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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.labfire.fe.auth.LongAuthToken;
import com.labfire.fe.common.InitializeException;
import com.labfire.fe.log.BootLogger;


/**
 * FireEngine
 *
 * @see org.apache.tools.ant.taskdefs.MatchingTask
 * @see org.apache.tools.ant.DirectoryScanner
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
 
public class FireEngine extends MatchingTask {
	private static boolean feDebug = true;
	private static boolean isBootStrapped = false;
	private static FireEngine singletonRef = null;
	
	private boolean validate = Boolean.getBoolean("com.labfire.fe.validate");
	private int majorVersion = 1;
	private int minorVersion = 4;
	private int subVersion = 10;
	private long startTime;
	private Map contexts = new HashMap();
	private SecureRandom rand = null;

	/**
	 * Constructor visibility is private to ensure that the only way to instantiate
	 * the FireEngine is through the public <code>getInstance</code> method.
	 */
	private FireEngine() {
		FireEngine.isBootStrapped = true;
		startTime = System.currentTimeMillis();
		BootLogger.log("FireEngine Version " + majorVersion + "." + minorVersion + "." + subVersion);
		BootLogger.log("Copyright 2001-2003 Labfire, Inc. All rights reserved.");
		BootLogger.log("Visit http://labfire.com/ for more information.");
		try {
			rand = SecureRandom.getInstance("SHA1PRNG");
			rand.setSeed(new java.util.Date().getTime());
		} catch (NoSuchAlgorithmException nsae) {
			BootLogger.log("Unable to initialize SecureRandom (SHA1PRNG) object", nsae);
			System.exit(1);
		}
	}
	
	/**
	 * @return FireEngine - there can only be one
	 */
	public static synchronized FireEngine getInstance() {
		if (FireEngine.singletonRef == null) {
			FireEngine.singletonRef = new FireEngine();
		}
		return FireEngine.singletonRef;
	}
	
	/**
	 * @return boolean - true if FireEngine has already been bootstrapped
	 * and initialized, false otherwise
	 * @synchronized
	 */
	public static synchronized boolean isBootStrapped() {
		return FireEngine.isBootStrapped;
	}
	
	/**
	 * getDebug
	 * 
	 * @return boolean
	 */
	public static boolean getDebug() {
		return feDebug;
	}
	
	/**
	 * getStartTime
	 * 
	 * @return long
	 */
	public long getStartTime() {
		return startTime;
	}
	
	/**
	 * getMajorVersion
	 *
	 * @return int
	 */
	public int getMajorVersion() {
		return majorVersion;
	}
	
	/**
	 * getMinorVersion
	 *
	 * @return int
	 */
	public int getMinorVersion() {
		return minorVersion;
	}
	
	/**
	 * getSubVersion
	 * 
	 * @return int
	 */
	public int getSubVersion() {
		return subVersion;
	}
	
	/**
	 * getVersion
	 *
	 * @return String
	 */
	public String getVersion() {
		return majorVersion + "." + minorVersion + "." + subVersion;
	}
	
	public FireEngineContext findContext(String appRoot) {
		return (FireEngineContext)contexts.get(appRoot);
	}
	
	synchronized FireEngineContext configure(String configFile) throws InitializeException {
		String E_MISSING_PROP = "Missing configuration property: ";
		String virtualRoot = "virtualRoot";
		String errorPage = "errorPage";
		String loginPage = "loginPage";
		String admin = "admin";
		String hostName = "hostName";
		
		// load main FireEngine proprties file
		try {
			BootLogger.log("Config file: " + configFile);
			FireEngineContext context = new FireEngineContext();
			FileReader reader = new FileReader(new File(configFile));
			SAXBuilder builder = new SAXBuilder(this.validate);
			Document configDoc = builder.build(reader);
			Properties props = FireEngineConfig.XMLtoProperties(configDoc.getRootElement());
			context.setProperties(props);
			
			String prop = props.getProperty(virtualRoot);
			if (prop == null) {
				throw new InitializeException(E_MISSING_PROP + virtualRoot);
			} else {
				context.setVirtualRoot(prop);
			}
			
			prop = props.getProperty(errorPage);
			if (prop == null) {
				throw new InitializeException(E_MISSING_PROP + errorPage);
			} else {
				context.setErrorPage(prop);
			}
			
			prop = props.getProperty(loginPage);
			if (prop == null) {
				throw new InitializeException(E_MISSING_PROP + loginPage);
			} else {
				context.setLoginPage(prop);
			}
			
			prop = props.getProperty(admin);
			if (prop == null) {
				throw new InitializeException(E_MISSING_PROP + admin);
			} else {
				context.setAdmin(prop);
			}
			
			prop = props.getProperty(hostName);	
			if (prop == null) {
				throw new InitializeException(E_MISSING_PROP + hostName);
			} else {
				context.setHostName(prop);
			}
			
			context.setAuthToken(new LongAuthToken(rand.nextLong()));
			Element current;
			String elementClass;
			List serviceElementList = configDoc.getRootElement().getChildren("Service");
			Map elements = new HashMap();
			for (Iterator i = serviceElementList.iterator(); i.hasNext(); ) {
				current = (Element)i.next();
				if (!"false".equals(current.getAttributeValue("load"))) {
					elementClass = current.getAttributeValue("class");
					if (elementClass != null) {
						elements.put(elementClass, current.clone());
					}
				}
			}
			context.setElements(elements);
			return context;
		} catch (JDOMException je) {
			BootLogger.log("Parse error on document " + configFile, je);
			throw new InitializeException("Unable to configure FireEngine", je);
		} catch (IOException ie) {
			BootLogger.log("Unable to open config file " + configFile, ie);
			BootLogger.log("Check the value of the system property com.labfire.fe.config");
			throw new InitializeException("Unable to configure FireEngine", ie);
		} catch (Exception e) {
			BootLogger.log("An unknown error has occurred", e);
			throw new InitializeException("Unable to configure FireEngine", e);
		}
	}
	
	/**
	 * register
	 *
	 * @synchronized
	 */
	synchronized FireEngineContext register(String appRoot, ClassLoader loader, List repositories) throws InitializeException {
		String configFile = appRoot + "/WEB-INF/FireEngine.xml";
		FireEngineContext context = configure(configFile);
		context.setClassLoader(loader);
		contexts.put(appRoot, context);
		FireEngineRepository repository;
		for (Iterator i = repositories.iterator(); i.hasNext(); ) {
			repository = (FireEngineRepository)i.next();
			List l;
			Object[] files = findServices(repository.getFilename()).toArray();
			// process the file names to their class equivalents and also sort them
			// according to priority
			processFrameworkNames(files, repository.getComparator());
			BootLogger.log("Loading " + files.length + " files from repository " + repository.getFilename());
			try {
				l = load(files, context);
				repository.setRegisteredServices(l);
				context.addRepository(repository);
				Iterator j = l.iterator();
				while (j.hasNext()) {
					((FireEngineComponent)j.next()).postCreate();
				}
				BootLogger.log("Successfully loaded repository " + repository.getFilename());
			} catch (InitializeException ie) {
				BootLogger.log(ie.getMessage(), ie.getNextException());
			}
		}
		return context;
	}
	
	/**
	 * deregister
	 *
	 * @synchronized
	 */
	synchronized void deregister(String appRoot) {
		FireEngineContext context = (FireEngineContext)contexts.get(appRoot);
		for (Iterator i = context.iterator(); i.hasNext(); ) {
			unload((FireEngineRepository)i.next());
		}
		contexts.remove(appRoot);
	}
	
	/**
	 * findServices
	 * 
	 * @return List
	 */
	private List findServices(String filename) {
		List classes = new Vector();
		if (filename.toLowerCase().endsWith(".jar")) {
			try {
				JarFile jf = new JarFile(filename);
				for (Enumeration e = jf.entries(); e.hasMoreElements();) {
					JarEntry je = (JarEntry)e.nextElement();
					if (je.getName().endsWith("Service.class")) {
						classes.add(je.getName());
					}
				}
			} catch (IOException ioe) {
				BootLogger.log("Caught IOException while opening JarFile " + filename, ioe);
			} catch (SecurityException se) {
				BootLogger.log("Caught SecurityException while opening JarFile " + filename, se);
			}
		} else { // this code might go away sometime
			// add classes that meet the framework SPI spec.
			super.setIncludes("**/*Service.class");
			DirectoryScanner ds = super.getDirectoryScanner(new File(filename));
			ds.scan();
			String f[] = ds.getIncludedFiles();
			for (int fi = 0; fi<f.length; fi++) {
				classes.add(f[fi]);
			}
		}
		return classes;
	}
	
	/**
	 * load
	 * 
	 * @return List
	 */
	private List load(Object[] files, FireEngineContext context) throws InitializeException {
		FireEngineComponent fc;
		List services = new ArrayList(files.length);
		Class feComponent = FireEngineComponent.class;
		Class[] parTypes = { FireEngineConfig.class };
		Object[] args  = new Object[1];

		// now iterate through each candidate service found and initialize it
		for (int i = 0; i< files.length; i++) {
			try {
				BootLogger.log("Initializing [" + (i+1) + "]: " + (String)files[i] + "...");
				Class tempClass = context.getClassLoader().loadClass((String)files[i]);
				Constructor tempCons = tempClass.getConstructor(parTypes);
				args[0] = new FireEngineConfig(context, tempClass.getName());
				// now call the introspected Constructor
				// if the service does not implement the required SPI, an exception will be thrown here
				if (feComponent.isAssignableFrom(tempClass)) {
					fc = (FireEngineComponent)tempCons.newInstance(args);
					// Now add the newly initialized FireEngineComponent to an internal list
					services.add(fc);
				} else {
					throw new Exception("Class " + (String)files[i] + "has Superclass of type " 
							+ tempClass.getSuperclass() + ", not FireEngineComponent.");
				}
			} catch (Exception e) {
				BootLogger.log("Unable to load " + files[i], e.getCause());
				throw new InitializeException("Unable to load " + files[i], e);
			}
		}
		return services;
	}
	
	private void unload(FireEngineRepository repository) {
		FireEngineComponent fc;
		List services = repository.getRegisteredServices();
		if (services != null) {
			Collections.reverse(services);
			Iterator i = services.iterator();
			while (i.hasNext()) {
				fc = (FireEngineComponent)i.next();
				try {
					fc.unload();
				} catch (Exception e) {
					BootLogger.logDebug("Error unloading Service " 
						+ fc.getClass().getName() + " from repository " + repository.getFilename(), e);
				}
			}
			services.clear();
		}
	}
	
	/**
	 * Converts an OS file name to the corresponding fully-qualified class name.
	 * Strips the '.class' extension and converts the path separator character (/ or \)
	 * to the '.' package separator.
	 *
	 * @param inFullClassName - the system-dependent file name to convert, e.g. /usr/home/hsheil/framework/logging/LogService.class
	 * @return String the corresponding class name, e.g. framework.logging.LogService
	 */
	private Object[] processFrameworkNames(Object[] inFileList, Comparator comparator) {
		String temp;
		
		for (int i = 0; i < inFileList.length; i++) {
			// strip off the '.class' extension
			int j = ((String)inFileList[i]).indexOf(".class");
			temp = ((String)inFileList[i]).substring(0,j);
			// now replace the platform dependent file separator with "."
			inFileList[i] = temp.replace(File.separatorChar, '.');
		}
		// resort the list according to the repository's comparator
		if (comparator != null) {
			Arrays.sort(inFileList, comparator);
		}
		return inFileList;
	}
	
	/**
	 * Converts a fully-qualified class name (i.e. with package) to just the class name.
	 *
	 * @param inFullyQualifiedClass the class name to convert
	 * @return the class name minus it's package prefix
	 */
	private String getClassName(Class inFullyQualifiedClass) {
		String fullClassName = inFullyQualifiedClass.getName();
		
		//now filter out the package prefix
		return fullClassName.substring(fullClassName.lastIndexOf(".") + 1, fullClassName.length());
	}
}
