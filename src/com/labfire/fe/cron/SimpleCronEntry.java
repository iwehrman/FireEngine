/*
 * SimpleCronEntry.java
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

import java.util.Calendar;
import java.util.Date;

/**
 * SimpleCronEntry
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class SimpleCronEntry implements Chronological {
	public static final int SHORT = 0;
	public static final int QUARTER_HOUR = 1;
	public static final int HALF_HOUR = 2;
	public static final int HOUR = 3;
	public static final int DAY = 4;
	public static final int WEEK = 5;
	public static final int MONTH = 6;
	public static final int YEAR = 7;

	private int repeatType = -1;
	private String identifier;
	private Calendar start;
	private Calendar stop;
	private Runnable job;
	
	/**
	 * SimpleCronEntry
	 */
	public SimpleCronEntry(Runnable job) {
		start = Calendar.getInstance();
		stop = Calendar.getInstance();
		setJob(job);
	}
	
	/**
	 * SimpleCronEntry
	 */
	public SimpleCronEntry(Runnable job, Date start) {
		this(job);
		setDate(start);
	}

	/**
	 * SimpleCronEntry
	 */
	public SimpleCronEntry(Runnable job, Date start, int rtype) {
		this(job);
		setDate(start, rtype);
	}

	/**
	 * SimpleCronEntry
	 */
	public SimpleCronEntry(Runnable job, Date start, int rtype, Date stop) {
		this(job);
		setDate(start, rtype, stop);
	}

	/**
	 * setJob
	 */
	public void setJob(Runnable r) {
		this.job = r;
	}
	
	/**
	 * setIdentifier
	 */
	public void setIdentifier(String id) {
		this.identifier = id;
	}
	
	/**
	 * getIdentifier
	 * 
	 * @return String
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/** 
	 * getJob
	 * 
	 * @return Runnable
	 */
	public Runnable getJob() {
		return job;
	}

	/**
	 * setDate
	 */
	public void setDate(Date start) {
		this.start.setTime(start);
		this.stop.clear();
	}
	
	/**
	 * setDate
	 */
	public void setDate(Date start, int rtype) {
		this.start.setTime(start);
		this.repeatType = rtype;
		this.stop.clear();
	}
	
	/**
	 * setDate
	 */
	public void setDate(Date start, int rtype, Date stop) {
		this.start.setTime(start);
		this.repeatType = rtype;
		this.stop.setTime(stop);
	}

	/**
	 * getStartDate
	 * 
	 * @return Date
	 */
	public Date getStartDate() {
		return start.getTime();
	}
	
	/**
	 * getStopDate
	 * 
	 * @return Date
	 */
	public Date getStopDate() {
		return stop.getTime();
	}
	
	/**
	 * getRepeatType
	 *
	 * @return int
	 */
	public int getRepeatType() {
		return repeatType;
	}
	
	/**
	 * getRepeatName
	 *
	 * @return String
	 */
	public String getRepeatName() {
		switch (repeatType) {
			case SHORT:
				return "Shortly";
			case QUARTER_HOUR:
				return "Quarter Hour";
			case HALF_HOUR:
				return "Half Hour"; 
			case HOUR:
				return "Hour";
			case DAY:
				return "Day";
			case WEEK:
				return "Week";
			case MONTH:
				return "Month";
			case YEAR: 
				return "Year";
			default:
				return "Unknown";
		}
	}

	/**
	 * execute
	 * 
	 * @return boolean
	 * @throws CronException if the execute method is unable to fire
	*/
	public boolean execute() throws CronException {
		boolean finished = true;
		new Thread(job).start();
		
		if ((repeatType >= 0) && (!stop.isSet(Calendar.YEAR) || start.before(stop))) {
			switch (repeatType) {
				case SHORT:
					start.add(Calendar.MINUTE, 5);
					break;
				case QUARTER_HOUR:
					start.add(Calendar.MINUTE, 15);
					break;
				case HALF_HOUR:
					start.add(Calendar.MINUTE, 30);
					break; 
				case HOUR: 
					start.add(Calendar.HOUR, 1); 
					break;
				case DAY: 
					start.add(Calendar.DAY_OF_WEEK, 1); 
					break;
				case WEEK: 
					start.add(Calendar.WEEK_OF_YEAR, 1); 
					break;
				case MONTH: 
					start.add(Calendar.MONTH, 1); 
					break;
				case YEAR: 
					start.add(Calendar.YEAR, 1); 
					break;
			}
			if (start.before(stop) || !stop.isSet(Calendar.YEAR)) {
				finished = false;
			}
		}
		return finished;
	}
	
	/**
	 * isTime
	 *
	 * @return boolean
	 */
	public boolean isTime() {
		Calendar today = Calendar.getInstance();
		
		today.setTime(new Date());
		if (today.after(start)) {
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "SimpleCronEntry: " + job.getClass().getName();
	}
}