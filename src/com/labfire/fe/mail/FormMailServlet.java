/*
 * FormMailServlet.java
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

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.util.Errors;

/**
 * FormMailServlet
 *
	required fields:<br />
	<code>_redirect</code> - String - URI to redirect to after sending mail<br />
	<code>_to</code> - field name - name of the field in the form which contains the address to send this mail to<br />
	<code>_from</code> - field name - name of the field in the form which contains the address this mail will be sent from<br />
	<code>_subject</code> - field name - name of the field in the form which contains the subject of the message<br /><br />
	optional fields:<br />
	<code>_hide</code> - comma-separated list of field names - names of the fields in the form which shouldn't be included in the email. 
		note: fields starting with underscore are already hidden, so it isn't necessary to add those to this list.<br />
	<code>_required</code> - comma-separated list of field names - names of the fields in the form which are required. the user will be shown
		an error page if a required field is blank, or has length == 0.
	
	for example, the following form...:<br /><br />
	
	<pre>
&lt;form method = "post" action = "servlet/com.labfire.fe.mail.FormMailServlet"&gt;
	&lt;input type = "text" name = "SomeName" /&gt;
	&lt;input type = "text" name = "SomeEmail" /&gt;
	&lt;input type = "text" name = "FavAnimal" /&gt;
	&lt;input type = "text" name = "LastZooVisit" /&gt;
	&lt;input type = "hidden" name = "ZooInfo" value = "Some info about a zoo visit." /&gt;
	&lt;input type = "hidden" name = "MyEmailAddr" = "Ian Wehrman <ian@labfire.com>" /&gt;
	&lt;input type = "hidden" name = "_redirect" value = "/thanks.html" /&gt;
	&lt;input type = "hidden" name = "_to" value = "MyEmailAddr" /&gt;
	&lt;input type = "hidden" name = "_from" value = "SomeEmail" /&gt;
	&lt;input type = "hidden" name = "_subject" value = "ZooInfo" /&gt;
	&lt;input type = "submit" value = "Send My Zoo Info" /&gt;
&lt;/form>
	</pre>
	
	will generate the following email:<br /><br />
	
	To: (whatever was in the <code>MyEmailAddr</code> field)<br />
	From: (whatever was in the <code>SomeEmail</code> field<br />
	Subject: Some info about a zoo visit.<br />
	Body: <br /><blockquote>
		SomeName: joe schmoe<br />
		SomeEmail: joe@schmoe.com<br />
		FavAnimal: bear<br />
		LastZooVisit: yesterday</blockquote>
	
 * @author <a href = "mailto:ian@labfire.com">Ian Wehrman</a>
 * @see com.labfire.fe.mail.MailService
 */
public class FormMailServlet extends HttpServlet {
	/**
	 * doGet
	 * 
	 * @param request HttpServletRequest object associated with a request
	 * @param response HttpServletResponse object associated with a request
	 * @throws IOException - if an input or output error is detected when the servlet handles the GET request
	 * @throws ServletException - if the request for the GET could not be handled
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AuthToken at = MailService.getServiceAuthToken();
		String to = request.getParameter(request.getParameter("_to"));
		String from = request.getParameter(request.getParameter("_from"));
		String subject = request.getParameter(request.getParameter("_subject"));
		String redirect = request.getParameter("_redirect");
		String body = "";
		String name;
		String[] values;
		LinkedList parameters = new LinkedList();
		LinkedList hidden = new LinkedList();
		StringTokenizer st;
		String token;
		
		try {
			if (request.getParameter("_required") != null) {
				st = new StringTokenizer(request.getParameter("_required"), ",");
				while (st.hasMoreTokens()) {
					token = st.nextToken().trim();
					if ((request.getParameter(token) == null) || (request.getParameter(token).length() == 0)) {
						throw new MailException("The following field requires user input: " + token + ".");
					}
				}
			}
			
			if (request.getParameter("_hide") != null) {
				st = new StringTokenizer(request.getParameter("_hide"), ",");
				while (st.hasMoreTokens()) {
					hidden.add(st.nextToken());
				}
			}
			
			for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
				parameters.add(e.nextElement());
			}
			sortParameters(parameters);
			
			for (ListIterator li = parameters.listIterator(); li.hasNext(); ) {
				name = (String)li.next();
				if ((name.charAt(0) != '_') && !(hidden.contains(name))) {
					body = name + ": ";
					values = request.getParameterValues(name);
					body += values[0];
					for (int i = 1; i < values.length; i++) {
						body += ", " + values[i];
					}
					body += "\n";
				}
			}
			MailService.sendMessage(at, to, from, subject, body);
			((HttpServletResponse)response).sendRedirect(redirect);
		} catch (Exception e) {
			Errors.handleException(e, request, response);
		}
	}

	/**
	 * sortParameters
	 *
	 * by default, this returns the same Collection that is passed in.
	 * subclass and override this method to sort the parameters in a special way. 
	 */
	public void sortParameters(List parameters) {
		Collections.sort(parameters);
	}

	/**
	 * doPost
	 * 
	 * @param request HttpServletRequest object associated with a request
	 * @param response HttpServletResponse object associated with a request
	 * @throws IOException - if an input or output error is detected when the servlet handles the POST request
	 * @throws ServletException - if the request for the POST could not be handled
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);		
	}
}
