/*
 * ConstraintTree.java
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


package com.labfire.fe.auth;

import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import com.labfire.fe.log.LogService;

/** 
 * ConstraintTree
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class ConstraintTree {
	private static final String SEPARATOR = "/";
	private ConstraintNode root = null;
	
	/**
	 * ConstraintTree
	 * 
	 * @param root The root node of the ConstraintTree
	*/
	public ConstraintTree(ConstraintNode root) {
		this.root = root;
	}
	
	/**
	 * getRoot
	 *
	 * @return ConstraintNode
	 */
	public ConstraintNode getRoot() {
		return root;
	}
	
	/**
	 * setRoot
	 */
	public void setRoot(ConstraintNode root) {
		this.root = root;
	}
	
	/**
	 * add
	 * Add and create nodes in the constraint tree representing
	 * files and directories which have constraint objects associated with
	 * them. Directories which do not explicitly have constraints associated
	 * with them will have ConstraintNodes created for them (with their parent
	 * directory's constraint attributes) if necessary.
	 * 
	 * @param cn ConstraintNode to add to the ConstraintTree
	 */
	public void add(ConstraintNode cn) {
		ConstraintNode last = root;
		ConstraintNode temp = null;
		StringTokenizer st = new StringTokenizer(cn.getPath(), SEPARATOR);
		StringBuffer next = new StringBuffer();
		String nextToken;
		
		for (int size = st.countTokens(); size > 0; size--) {
			nextToken = st.nextToken();
			if (nextToken.equals(".")) {
				continue;
			} else {
				next.append(SEPARATOR);
				next.append(nextToken);
				temp = (ConstraintNode)last.getChildren().get(next.toString());
				if (temp == null) { // add node if necessary
					temp = new ConstraintNode(next.toString());
					temp.setParent(last);
					last.getChildren().put(next.toString(), temp);
				} 
				if (!st.hasMoreTokens()) { // the last node, add constraints
					if (cn.getConstraints().size() > 0) {
						temp.addAllConstraints(cn.getConstraints());
					} else {
						temp.getConstraints().clear();
						LogService.logDebug("Clearing Constraints for " + last);
					}
				}
				last = temp;
			}
		}
	}
	
	/**
	 * match
	 * Match a Constraint to a request.
	 * 
	 * @param request URI string which Constraint is matched against
	 */
	public Set match(String request) {
		return find(request).getConstraints();
	}
	
	/**
	 * find
	 * Create a ConstraintNode which matches a request,
	 * that is a composite of all the nodes beneath it
	 * (in other words, inherit the attributes of parent nodes
	 * dynamically here, not when we insert them).
	 * 
	 * @param request URI string which ConstraintNode is matched against
	 */
	ConstraintNode find(String request) {
		StringTokenizer st = new StringTokenizer(request, SEPARATOR);
		StringBuffer next = new StringBuffer();
		ConstraintNode last = root;
		ConstraintNode temp;
		ConstraintNode retVal = new ConstraintNode(request);
		String nextToken;
		
		for (int size = st.countTokens(); size > 0; size--) {
			nextToken = st.nextToken();
			if (!nextToken.equals(".")) {
				next.append(SEPARATOR);
				next.append(nextToken);
				temp = (ConstraintNode)last.getChildren().get(next.toString());
				if (temp == null) {
					retVal.addAllConstraints(last.getConstraints());
					break;
				} else {
					last = temp;
					if (size == 1) {
						retVal.addAllConstraints(last.getConstraints());
						break;
					}
				}
			}
		}
		
		return retVal;
	}

	/**
	 * clear
	 * Clear the constraints (but don't actually delete them) which match
	 * (and those under the one that matches) a request.
	 * 
	 * @param request URI string which Constraints are matched against and cleared.
	 */
	public void clear(String pattern) {
		ConstraintNode cn = find(pattern);
		if (cn != null) {
			cn.getConstraints().clear();
			for (Iterator i = cn.getChildren().values().iterator(); i.hasNext(); ) {
				clear(((ConstraintNode)i.next()).getPath());
			}
		}
	}
	
	/** 
	 * size
	 *
	 * @return int
	 */
	public int size() {
		return countNodes(root);
	}
	
	/**
	 * toString
	 *
	 * @return String
	 */
	public String toString(ConstraintNode root) {
		String s = root.getPath() + '\n';
		for (Iterator i = root.getChildren().values().iterator(); i.hasNext(); ) {
			s += '\t' + toString((ConstraintNode)i.next()) + '\n';
		}
		return s;
	}
	
	public String toString() {
		return toString(this.root);
	}
	
	/**
	 * countNodes
	 * 
	 * @return int
	 */
	private int countNodes(ConstraintNode root) {
		int nodes = root.getChildren().size();
		for (Iterator i = root.getChildren().values().iterator(); i.hasNext(); ) {
			nodes += countNodes((ConstraintNode)i.next());
		}
		return nodes;
	}
}
