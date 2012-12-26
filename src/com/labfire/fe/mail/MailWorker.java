/*
 * MailWorker.java
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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.labfire.fe.log.LogService;
import com.labfire.fe.util.WorkQueue;

/**
 * MailWorker
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class MailWorker implements Runnable, TransportListener {
	private static final String REPLY_TO = "no-reply@labfire.com";
	private WorkQueue queue;
	private Properties mailProps;
	
	/**
	 * MailWorker
	 */
	public MailWorker(WorkQueue queue, Properties mailProps) {
		this.queue = queue;
		this.mailProps = mailProps;
	}
	
	/**
	 * run
	 */
	public void run() {
		Mailable mail;
		Session session;
		Transport transport;
		MimeMessage msg;
		MimeBodyPart mbp;
		Multipart mp;
		InternetAddress[] address = new InternetAddress[1];
		InternetAddress[] replyTo = new InternetAddress[1];
		
		List work = queue.getAllWork();
		Iterator messages = work.iterator();
		while (messages.hasNext()) {
			try {
				mail = (Mailable) messages.next();
				session = Session.getInstance(mailProps, new MailAuthenticator(mail.getAuthToken()));
				msg = new MimeMessage(session); // create a message 
				LogService.logDebug("Preparing mail to: " + mail.getTo());
				address[0] = new InternetAddress(mail.getTo());
				mbp = new MimeBodyPart();
				mp = new MimeMultipart();
				replyTo[0] = new InternetAddress(REPLY_TO);
				msg.setReplyTo(replyTo);
				msg.setFrom(new InternetAddress(mail.getFrom()));
				msg.setRecipients(Message.RecipientType.TO, address);
				msg.setSubject(mail.getSubject());
				msg.setSentDate(new Date());
				mbp.setText(mail.getBody());
				mp.addBodyPart(mbp);
				msg.setContent(mp); // add the Multipart to the message
				transport = session.getTransport();
				transport.addTransportListener(this);
				transport.connect();
				transport.sendMessage(msg, address);
				transport.close();
				LogService.logDebug("Message processed from " + mail.getFrom());
			} catch (Exception e) {
				LogService.logError("Unable to process mail", e);
			}
		}
	}
	
	public void messageDelivered(TransportEvent e) {
		Address[] addresses = e.getValidSentAddresses();
		StringBuffer message = new StringBuffer("Message delivered to: ");
		for (int i = 0; i < addresses.length; i++) {
			message.append(addresses[i]);
			if (i < (addresses.length - 1)) {
				message.append(',');
			}
		}
		LogService.logDebug(message.toString());
	}
	
	public void messageNotDelivered(TransportEvent e) {
		StringBuffer message = new StringBuffer("Message not delivered.");
		Address[] addresses = e.getValidUnsentAddresses();
		if (addresses.length > 0) {
			message.append(" Valid, unsent addresses: ");
			for (int i = 0; i < addresses.length; i++) {
				message.append(addresses[i]);
				if (i < (addresses.length - 1)) {
					message.append(',');
				}
			}
		}
		addresses = e.getInvalidAddresses();
		if (addresses.length > 0) {
			message.append(", Invalid addresses: ");
			for (int i = 0; i < addresses.length; i++) {
				message.append(addresses[i]);
				if (i < (addresses.length - 1)) {
					message.append(',');
				}
			}
		}
		LogService.logError(message.toString());
	}
	
	public void messagePartiallyDelivered(TransportEvent e) {
		StringBuffer message = new StringBuffer("Message delivered to: ");
		Address[] addresses = e.getValidSentAddresses();
		for (int i = 0; i < addresses.length; i++) {
			message.append(addresses[i]);
			if (i < (addresses.length - 1)) {
				message.append(',');
			}
		}
		addresses = e.getValidUnsentAddresses();
		if (addresses.length > 0) {
			message.append(", Valid, unsent addresses: ");
			for (int i = 0; i < addresses.length; i++) {
				message.append(addresses[i]);
				if (i < (addresses.length - 1)) {
					message.append(',');
				}
			}
		}
		addresses = e.getInvalidAddresses();
		if (addresses.length > 0) {
			message.append(", Invalid addresses: ");
			for (int i = 0; i < addresses.length; i++) {
				message.append(addresses[i]);
				if (i < (addresses.length - 1)) {
					message.append(',');
				}
			}
		}
		LogService.logError(message.toString());
	}
}
