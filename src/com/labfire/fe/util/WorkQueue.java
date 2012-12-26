/*
 * WorkQueue.java
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


package com.labfire.fe.util;

import java.util.List;
import java.util.LinkedList;

public class WorkQueue {
	LinkedList queue = new LinkedList();
    
	/**
	 * addWork
	 */
	public synchronized void addWork(Object o) {
		queue.addLast(o);
		notify();
	}
    
	/**
	 * getWork
	 * 
	 * @return Object
	 */
	public synchronized Object getWork() throws InterruptedException {
		while (queue.isEmpty()) {
			wait();
		}
		return queue.removeFirst();
	}
	
	public synchronized List getAllWork() {
		List work = new LinkedList(queue);
		queue.clear();
		return work;
	}
	
	public int getApproximateQueueSize() {
		return queue.size();
	}
}
