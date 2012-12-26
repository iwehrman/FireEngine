/*
 * CronScheduler.java
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


package com.labfire.fe.cron;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import com.labfire.fe.log.LogService;

/**
 * CronScheduler
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class CronScheduler implements Runnable {
	private int sleepTime;
	private List jobs;
	
	public CronScheduler(List jobs, int sleepTime) {
		this.jobs = jobs;
		this.sleepTime = sleepTime;
	}
	
	/**
	 * run
	 */
	public void run() {
		Iterator elements;
		Chronological c;
		try {
			while (true) {
				Thread.sleep(sleepTime);
				synchronized (jobs) {
					elements = jobs.iterator();
					start_over: while(elements.hasNext()) {
						// must use the iterator's remove() to avoid
						// ConcurrentModificationExceptions
						try {
							c = (Chronological)elements.next();
							if (c.isTime()) {
								if (c.execute()) {
									elements.remove();
									LogService.logDebug("Object " + c + " removed");
								}
							}
						} catch (ConcurrentModificationException cme) {
							LogService.logWarn("ConcurrentModification of jobs attempted");
							break start_over;
						} catch (Exception e) {
							LogService.logWarn("Unable to sweep cron jobs.", e);
						}
						jobs.wait(1);
					}
				}
			}
		} catch (InterruptedException ie) {
			LogService.logInfo("Halting cron sweep...");
		}
	}
}