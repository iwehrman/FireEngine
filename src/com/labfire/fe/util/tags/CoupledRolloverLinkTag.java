/*
 * CoupledRolloverLinkTag.java
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
 * CoupleRolloverLinkTag
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */

public class CoupledRolloverLinkTag extends TagSupport {
	private static final int BUFFER_SIZE = 255;
	private String href = null;
	private String onclick = null;
	private String title = null;
	private String srcOff = null;
	private String srcOn = null;
	private String imageClass = null;
	private String imageId = null;
	private String textClass = null;
	private String linkClass = null;
	private String value = null;
	

	/**
	 * doStartTag
	 *
	 * @return int
	 */
	public int doStartTag() {
		try {
			StringBuffer buffer = new StringBuffer(BUFFER_SIZE);
			buffer.append("<a");
			if (href != null) {
				buffer.append(" href=\"");
				buffer.append(href);
				buffer.append('"');
			}
			if (onclick != null) {
				buffer.append(" onclick=\"");
				buffer.append(onclick);
				buffer.append('"');
			}
			if (title != null) {
				buffer.append(" title=\"");
				buffer.append(title);
				buffer.append('"');
			}
			if (linkClass != null) {
				buffer.append(" class=\"");
				buffer.append(linkClass);
				buffer.append('"');
			}
			if (imageId != null) {
				if (srcOn != null) {
					buffer.append(" onmouseover=\"setImage('");
					buffer.append(imageId);
					buffer.append("','");
					buffer.append(srcOn);
					buffer.append("')\"");
				}
				if (srcOff != null) {
					buffer.append(" onmouseout=\"setImage('");
					buffer.append(imageId);
					buffer.append("','");
					buffer.append(srcOff);
					buffer.append("')\"");
				}
			}
			buffer.append("><img");
			if (imageClass != null) {
				buffer.append(" class=\"");
				buffer.append(imageClass);
				buffer.append('"');
			}
			if (imageId != null) {
				buffer.append(" id=\"");
				buffer.append(imageId);
				buffer.append('"');
			}
			if (srcOff != null) {
				buffer.append(" src=\"");
				buffer.append(srcOff);
				buffer.append('"');
			}
			buffer.append(" alt=\"");
			buffer.append(title);
			buffer.append("\" />");
			if (value != null) {
				buffer.append("<span");
				if (textClass != null) {
					buffer.append(" class=\"");
					buffer.append(textClass);
					buffer.append('"');
				}
				buffer.append('>');
				buffer.append(value);
				buffer.append("</span>");
			}
			buffer.append("</a>");
			pageContext.getOut().print(buffer.toString());
		} catch (IOException ioe) {
			LogService.logError("Unable to create tag", ioe);
		}
		return SKIP_BODY;
	}

	/**
	 * setSrcOff
	 */
	public void setSrcOff(String s) {
		this.srcOff = s;
	}
	
	/**
	 * setSrcOn
	 */
	public void setSrcOn(String s) {
		this.srcOn = s;
	}

	/**
	 * setImageClass
	 */
	public void setImageClass(String s) {
		this.imageClass = s;
	}

	/**
	 * setImageId
	 */
	public void setImageId(String s) {
		this.imageId = s;
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

	/**
	 * setHref
	 */
	public void setHref(String s) {
		this.href = s;
	}  

	/**
	 * setValue
	 */
	public void setValue(String s) {
		this.value = s;
	}  

	/**
	 * setTitle
	 */
	public void setTitle(String s) {
		this.title = s;
	}
	
	/**
	 * Sets the onclick.
	 * @param onclick The onclick to set
	 */
	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

}
