/*
 * Strings.java
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

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.labfire.fe.log.BootLogger;

/**
 * Strings
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class Strings {
	private static final String ENCODING = "UTF-8";
	private static MessageDigest md;
	private static final Pattern linkPattern = Pattern.compile("((http|ftp)://(\\S+))");
	private static final char lookup[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
								'a', 'b', 'c', 'd', 'e', 'f' };
	
	public static final String EMPTY = "";

	static {
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException nsae) {
			BootLogger.log("Unable to initialize MessageDigest.", nsae);
		}
	}
	
	/**
	 * Strings
	 */
	private Strings() {}
	
	/**
	 * replaceString
	 * 
	 * @return String
	 */
	public static String replaceString(String aSearch, String aFind, String aReplace) {
		String result = aSearch;
		if ((result != null) && (result.length() > 0)) {
			int a = 0;
			int b = 0;
			while (true) {
				a = result.indexOf(aFind, b);
				if (a != -1) {
					result = result.substring(0, a) + aReplace + result.substring(a + aFind.length());
					b = a + aReplace.length();
				} else {
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * cleanString
	 *
	 * @return String
	 */
	public static String cleanString(String inMessage) {
		if (inMessage.lastIndexOf('\'') != -1) {
			return replaceString(inMessage, "'", "''");
		} else {
			return inMessage;
		}
	}
	
	/**
	 * escapeHTML
	 *
	 * @return String
	 */
	public static String escapeHTML(String html) {
		if (html == null) {
			return null;
		}
		if (html.lastIndexOf('&') != -1) {
			html = replaceString(html, "&", "&amp;");
		}
		if (html.lastIndexOf('<') != -1) {
			html = replaceString(html, "<", "&lt;");
		}
		if (html.lastIndexOf('>') != -1) {
			html = replaceString(html, ">", "&gt;");
		}
		if (html.lastIndexOf('"') != -1) {
			html = replaceString(html, "\"", "&quot;");
		}
		if (html.lastIndexOf('\n') != -1) {
			html = replaceString(html, "\n", "<br />");
		}
		
		Matcher linkMatcher = linkPattern.matcher(html);
		html = linkMatcher.replaceAll("<a href=\"$1\">$1</a>");
		return html;
	}
	
	/**
	 * escapeHTML
	 *
	 * @return String
	 */
	public static String escapeHTML(Object html) {
		if (html == null) {
			return "";
		}
		return escapeHTML(html.toString());
	}
	
	/**
	 * escapeSQL
	 *
	 * @return String
	 */
	public static String escapeSQL(String sql) {
		if (sql == null) {
			return "";
		}
		if (sql.lastIndexOf('\'') != -1) {
			return replaceString(sql, "'", "''");
		} else {
			return sql;
		}
	}
	
	/**
	 * escapeSQL
	 *
	 * @return String
	 */
	public static String escapeSQL(Object sql) {
		if (sql == null) {
			return "";
		}
		return escapeSQL(sql.toString());
	}
	
	/**
	 * hashCode
	 *
	 * @return long
	 */
	public static long hashCode(String s) {
		int length;
		long hash;
		long h1;
		long h2;
		String s1;
		String s2;
	
		if (s == null) {
			return 0;
		} else {
			length = s.length();
			s1 = s.substring(0, (length - (length % 2))/2);
			s2 = s.substring(((length + (length % 2))/2) - (length % 2));
			h1 = (new Integer(s1.hashCode())).longValue();
			h2 = (new Integer(s2.hashCode())).longValue();
			hash = h1;
			hash <<= 31;
			//hash |= h2; ?
			return hash + h2;
		}
	}
	
	/**
	 * encode
	 *
	 * @return String
	 */
	public static String encode(String string) {
		byte[] temp_digest;
		byte[] hex_digest = new byte[32];

		synchronized (md) {
			md.update(string.getBytes());
			temp_digest = md.digest();
		}
		bytesToHex(temp_digest, hex_digest);
		return new String(hex_digest);
	}
	
	/**
	 * encode
	 *
	 * @return String
	 */
	public static String encode(Object o) {
		return encode(o.toString());
	}


	/*
	 * bytesToHex
	 * Turn 16-byte stream into a human-readable 32-byte hex string
	 */
	public static final void bytesToHex(byte[] bytes, byte[] hex) {
		int i, c, j, pos = 0;

		for (i = 0; i < 16; i++) {
			c = bytes[i] & 0xFF;
			j = c >> 4;
			hex[pos++] = (byte)lookup[j];
			j = (c & 0xF);
			hex[pos++] = (byte)lookup[j];
		}
	}
	
	/**
	 * addRequestParameter
	 *
	 * @return String
	 */
	public static String addRequestParameter(String requestURI, String parameter, Object value) {
		try {
			int pPos;
			int qPos = requestURI.indexOf('?');
			if (qPos == -1) {
				return requestURI + '?' + parameter + '=' + URLEncoder.encode(value.toString(), ENCODING);
			}
			pPos = requestURI.lastIndexOf(parameter);
			if (pPos < qPos) {
				return requestURI + '&' + parameter + '=' + URLEncoder.encode(value.toString(), ENCODING);
			}
			else {
				String head = requestURI.substring(0, pPos);
				String tail = requestURI.substring(pPos, requestURI.length());
				int ioa = tail.indexOf('&');
				if (ioa < 0) {
					return head + parameter + '=' + URLEncoder.encode(value.toString(), ENCODING);
				} else {
					return head + parameter + '=' + URLEncoder.encode(value.toString(), ENCODING) + tail.substring(ioa, tail.length());
				}
			}
		} catch (Exception e) {
			BootLogger.log("Caught exception while adding request parameter.", e);
			return requestURI;
		}
	}
	
	/**
	 * removeRequestParameter
	 * 
	 * @return String
	 */
	public static String removeRequestParameter(String requestURI, String parameter) {
		try {
			int qPos = requestURI.indexOf('?');
			if (qPos > -1) {
				int pPos = requestURI.lastIndexOf(parameter);
				if (pPos > qPos) {
					String head = requestURI.substring(0, pPos - 1);
					String tail = requestURI.substring(pPos, requestURI.length());
					int ioa = tail.indexOf('&');
					if (ioa == -1) {
						return head;
					} else if (head.indexOf('?') == -1) {
						return head + '?' + tail.substring(ioa + 1, tail.length());
					} else {
						return head + tail.substring(ioa, tail.length());
					}
				}
			}
			return requestURI;
		} catch (Exception e) {
			BootLogger.log("Caught exception while removing request parameter.", e);
			return requestURI;
		}
	}
}
