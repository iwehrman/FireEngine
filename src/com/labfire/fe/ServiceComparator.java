/*
 * ServiceComparator.java
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

/**
 * ServiceComparator
 * 
 * @author <a href = "http://labfire.com/">Labfire, Inc.</a>
 */


public class ServiceComparator implements Comparator {
	String[] firstClassNames;
	String[] lastClassNames;
	
	protected ServiceComparator() {}
	
	protected ServiceComparator(String[] firstClassNames, String[] lastClassNames) {
		this.firstClassNames = firstClassNames;
		this.lastClassNames = lastClassNames;
	}
	
	protected void setFirstClassNames(String[] names) {
		this.firstClassNames = names;
	}
	
	protected void setLastClassNames(String[] names) {
		this.lastClassNames = names;
	}
	
	public int compare(Object o1, Object o2) {
		if (o1.equals(o2)) {
			return 0;
		} else {
			String s1 = (String)o1;
			String s2 = (String)o2;
			int i1 = search(firstClassNames, s1);
			if (i1 > -1) {
				// i1 in firstNames list, so if i2 isn't
				// then it must be greater
				int i2 = search(firstClassNames, s2);
				if (i2 > -1) {
					if (i1 > i2) {
						return 1;
					} else {
						return -1;
					}
				} else { 
					return -1;
				}
			} else {
				i1 = search(lastClassNames, s1);
				if (i1 > -1) {
					// i1 is in the lastNames list, so if i2 isn't
					// it must be less
					int i2 = search(lastClassNames, s2);
					if (i2 > -1) {
						if (i1 > i2) {
							return 1;
						} else {
							return -1;
						}
					} else {
						return 1;
					}
				} else {
					// i1 is in the middle, so if i2 is in the lastNames list,
					// then it must be greater. if it is in the firstNames list
					// it must be less
					int i2 = search(firstClassNames, s2);
					if (i2 > -1) {
						return 1;
					} else {
						i2 = search (lastClassNames, s2);
						if (i2 > -1) {
							return -1;
						}
					}
					return 0;
				}
			}
		}
	}
	
	// Arrays.binarySearch just plain doesn't work for strings
	private static final int search(String[] strings, String file) {
		for (int i = 0; i < strings.length; i++) {
			if (strings[i].equals(file)) {
				return i;
			}
		}
		return -1;
	}
}