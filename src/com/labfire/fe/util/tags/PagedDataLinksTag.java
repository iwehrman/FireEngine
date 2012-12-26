/*
 * PagedDataLinksTag.java
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


package com.labfire.fe.util.tags;

import java.io.IOException;

import javax.servlet.jsp.tagext.TagSupport;

import com.labfire.fe.log.LogService;

/** 
 * PagedDataLinksTag
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */

public class PagedDataLinksTag extends TagSupport {
	private static final int BUFFER_SIZE = 255;
	private static int counter = 1;
	private int prevPage = 0;
	private int nextPage = 1;
	private int currentPage = 1;
	private int pageIteratorSize = 1;
	private String link;
	private String imageId;
	private String href = null;
 	private String prevSrcOff = null;
	private String prevSrcOn = null;
	private String nextSrcOff = null;
	private String nextSrcOn = null;
	private String pageAttributeName = null;
	private String imageClass = null;
	private String highlightClass = null;
	private String textClass = null;
	private String linkClass = null;
	private String blankSrc = null;
	
	/**
	 * doStartTag
	 *
	 * @return int
	 */
	public int doStartTag() {
		try {
			StringBuffer sb = new StringBuffer(BUFFER_SIZE);
			prevPage = currentPage - 1;
			imageId = "prev_page" + counter;
			if (prevPage > 0) {
				//There is a previous page, so put a link there
				link = href + "&amp;" + pageAttributeName + '=' + prevPage;
				sb.append("\n\t\t<a href=\""); 
				sb.append(link);
				sb.append("\" title=\"Page ");
				sb.append(prevPage);
				sb.append(" of ");
				sb.append(pageIteratorSize);
				sb.append("\" class=\"");
				sb.append(linkClass);
				sb.append("\" onmouseover=\"javascript:setImage('");
				sb.append(imageId);
				sb.append("','");
				sb.append(prevSrcOn);
				sb.append("')\"");
				sb.append(" onmouseout=\"javascript:setImage('");
				sb.append(imageId);
				sb.append("','");
				sb.append(prevSrcOff);
				sb.append("')\" >");	  
				sb.append("<img class=\"");
				sb.append(imageClass);
				sb.append("\" id=\"");
				sb.append(imageId);
				sb.append("\" src=\"");
				sb.append(prevSrcOff);
				sb.append("\" alt=\"Previous\" />");
				sb.append("</a>");
			} else {
				//No previous page, just but a blank image
				sb.append("<a href=\"#\" "); 
				sb.append("class=\"");
				sb.append(linkClass);
				sb.append("\" >"); 			
				sb.append("<img class=\"");
				sb.append(imageClass);
				sb.append("\" id=\"");
				sb.append(imageId);
				sb.append("\" src=\"");
				sb.append(blankSrc);
				sb.append("\" /></a>");			
			}

			sb.append("<span class=\"");
			sb.append(textClass);
			sb.append("\" >");
			for (int i = 1; i <= pageIteratorSize; i++) {
				link = href + "&amp;" + pageAttributeName + '=' + i;
				if (i != currentPage) {
					sb.append("<a class=\"");
					sb.append(linkClass);
					sb.append("\" href=\"");
					sb.append(link);
					sb.append("\">");
					sb.append(i);
					sb.append("</a>");
				} else { 
					sb.append("<span class=\"");
					sb.append(highlightClass);
					sb.append("\">");
					sb.append(i);
					sb.append("</span>");
				}
			}
			sb.append("</span>");		

			nextPage = currentPage + 1;
			imageId = "next_page" + counter;
			if (nextPage <= pageIteratorSize) {
				link = href + "&amp;" + pageAttributeName + '=' + nextPage;
				sb.append("<a href=\"");
				sb.append(link);
				sb.append("\" title=\"Page ");
				sb.append(nextPage);
				sb.append(" of ");
				sb.append(pageIteratorSize);
				sb.append("\" class=\"");
				sb.append(linkClass);
				sb.append("\" onmouseover=\"javascript:setImage('");
				sb.append(imageId);
				sb.append("','");
				sb.append(nextSrcOn);
				sb.append("')\"");
				sb.append(" onmouseout=\"javascript:setImage('");
				sb.append(imageId);
				sb.append("','");
				sb.append(nextSrcOff);
				sb.append("')\" >");	  
				sb.append("<img class=\"");
				sb.append(imageClass);
				sb.append("\" id=\"");
				sb.append(imageId);
				sb.append("\" src=\"");
				sb.append(nextSrcOff);
				sb.append("\" alt=\"Next\" />");
				sb.append("</a>");
			} else {
				//No Next page, just put a blank image
				sb.append("<a href=\"#\" "); 
				sb.append("class=\"");
				sb.append(linkClass);
				sb.append("\" >"); 			
				sb.append("<img class=\"");
				sb.append(imageClass);
				sb.append("\" id=\"");
				sb.append(imageId);
				sb.append("\" src=\"");
				sb.append(blankSrc);
				sb.append("\" /></a>");			
			}
			
			pageContext.getOut().print(sb.toString());
			counter++;
		} catch (IOException ioe) {
			LogService.logError("Unable to create tag", ioe);
		}
		return(SKIP_BODY);
	}
  
	/**
	 * setCurrentPage
	 */
	public void setCurrentPage(int i) {
		try {
			this.currentPage = i;
		} catch (NumberFormatException nfe){
			this.currentPage = 1;
		} 
	}

	/**
	 * setPageIteratorSize
	 */
	public void setPageIteratorSize(int i) {
		try{
			this.pageIteratorSize = i;
		} catch (NumberFormatException nfe){
			this.pageIteratorSize = 1;
		}
	}  

	/**
	 * setPrevSrcOff
	 */
	public void setPrevSrcOff(String s) {
		this.prevSrcOff = s;
	}

	/**
	 * setPrevSrcOn
	 */
	public void setPrevSrcOn(String s) {
		this.prevSrcOn = s;
	}

	/**
	 * setNextSrcOff
	 */
	public void setNextSrcOff(String s) {
		this.nextSrcOff = s;
	}

	/**
	 * setNextSrcOn
	 */
	public void setNextSrcOn(String s) {
		this.nextSrcOn = s;
	}

	/**
	 * setBlankSrc
	 */
	public void setBlankSrc(String s) {
		this.blankSrc = s;
	}

	/**
	 * setHref
	 */
	public void setHref(String s) {
		this.href = s;
	}

	/**
	 * setPageAttributeName
	 */
	public void setPageAttributeName(String s) {
		this.pageAttributeName = s;
	}

	/**
	 * setImageClass
	 */
	public void setImageClass(String s) {
		this.imageClass = s;
	}

	/**
	 * setHighlightClass
	 */
	public void setHighlightClass(String s) {
		this.highlightClass = s;
	}

	/**
	 * setLinkClass
	 */
	public void setLinkClass(String s) {
		this.linkClass = s;
	} 

	/**
	 * setTextClass
	 */
	public void setTextClass(String s) {
		this.textClass = s;
	} 
}
