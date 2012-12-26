/*
 * MailService.java
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


package com.labfire.fe.mail;

import java.util.Date;
import java.util.Properties;

import com.labfire.fe.FireEngineComponent;
import com.labfire.fe.FireEngineConfig;
import com.labfire.fe.auth.AuthException;
import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.cache.CacheException;
import com.labfire.fe.cache.CacheService;
import com.labfire.fe.cache.CachedObject;
import com.labfire.fe.common.InitializeException;
import com.labfire.fe.common.User;
import com.labfire.fe.cron.CronService;
import com.labfire.fe.cron.SimpleCronEntry;
import com.labfire.fe.log.LogService;
import com.labfire.fe.util.WorkQueue;

/**
 * MailService
 * The MailService allows authenticated FireEngine users to send email.
 * The mailservice.properties file should at least specify mail.smtp.host
 * and possibly mail.smtp.user if necessary.
 * 
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public final class MailService extends FireEngineComponent {
	private static AuthToken serviceAuthToken;
	private static Properties mailProps;
	private static String defaultUser;
	private static String defaultPass;
	private static WorkQueue work = new WorkQueue();
	private static SimpleCronEntry sweeper = null;
	
	/**
	 * MailService
	 * Initializes the MailService component. Called at FireEngine startup.
	 * Gets a Session instance using config data.
	 * 
	 */
	public MailService(FireEngineConfig config) throws InitializeException {
		super(config);
		serviceAuthToken = config.getServiceAuthToken();
		defaultUser = super.serviceProperties.getProperty("defaultUser");
		defaultPass = super.serviceProperties.getProperty("defaultPass");
		mailProps = new Properties();
		mailProps.setProperty("mail.transport.protocol", "smtp");
		mailProps.setProperty("mail.smtp.host", super.serviceProperties.getProperty("host"));
		mailProps.setProperty("mail.smtp.user", defaultUser);
		// mailProps.setProperty("mail.debug", "true");
		mailProps.setProperty("mail.smtp.auth", "true");
		sweeper = new SimpleCronEntry(
			new MailWorker(work, mailProps), 
			new Date(), 
			SimpleCronEntry.SHORT);
		CronService.putCron(sweeper);
	}
	
	/**
	 * reset statically-assigned resources when this Service's parent
	 * FireEngineRepository is unloaded
	 */
	protected void unload() { 
		// clear the queues
		LogService.logInfo("Flushing mail queue...");
		new MailWorker(work, mailProps).run();
	}
	
	static String getDefaultUser() {
		return defaultUser;
	}
	
	static String getDefaultPass() {
		return defaultPass;
	}
	
	/**
	 * sendMessage
	 * 
	 * @param eid userEID
	 * @param to Who the message is addressed to
	 * @param subject The subject of the message to
	 * @param body The text body of the message
	 * @throws MailException if an error occurs while sending the message
	 * @throws AuthenticationException if the userEID could not be matched to a valid user
	 */
	public static void sendMessage(AuthToken at, String to, String subject, String body) throws MailException, AuthException {
		String from;
		try {
			User u = (User)((CachedObject)CacheService.getCache(at)).object;
			if ((u.getUserFirstName() == null) || (u.getUserLastName() == null)) {
				from = u.getUserName() + " <" + u.getUserEmail() + ">";
			} else {
				from = u.getUserFirstName() + " " + u.getUserLastName() + " <" + u.getUserEmail() + ">";
			}
			sendMessage(at, to, from, subject, body);
		} catch (CacheException ce) {
			AuthException ae = new AuthException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;		
		}
	}
	
	/**
	 * sendMessage
	 */
	public static void sendMessage(AuthToken at, String to, String from, String subject, String body) throws MailException, AuthException {
		Mailable mail = new Mailable(at, to, from, subject, body);
		work.addWork(mail);
	}
	
	/** 
	 * getMailEID
	 * 
	 * @return long
	 */
	static AuthToken getServiceAuthToken() {
		return serviceAuthToken;
	}
	
	/**
	 * getStatus
	 * 
	 * @return String
	 */
	public String getStatus() {
		return "MailService: properties=" + mailProps;
	}
}
