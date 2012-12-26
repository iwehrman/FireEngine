/*
 * TreeNode.java
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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A lightweight treenode data structure
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class TreeNode {
	Object data;
	Collection children = new LinkedList();
	
	/**
	 * Tree
	 */
	public TreeNode() {
		data = null;
	}
	
	/**
	 * Tree
	 * @param data
	 */
	public TreeNode(Object data) {
		this.data = data;
	}
	/**
	 * @return Object
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Sets the data.
	 * @param data The data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	/**
	 * add a child to the tree
	 * @param node
	 */
	public void addChild(TreeNode node) {
		children.add(node);
	}
	
	/**
	 * iterator over child treenodes
	 * @return Iterator
	 */
	public Iterator iterator() {
		return children.iterator();
	}

	/**
	 * @return LinkedList
	 */
	public Collection getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 * @param children The children to set
	 */
	public void setChildren(Collection children) {
		this.children = children;
	}
	
	/**
	 * toString
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new StringBuffer()
			.append(data)
			.append('\n')
			.append(children)
			.toString();
	}

}
