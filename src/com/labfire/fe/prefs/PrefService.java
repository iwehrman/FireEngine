/*
 * PrefService.java
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


package com.labfire.fe.prefs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.labfire.fe.FireEngineComponent;
import com.labfire.fe.FireEngineConfig;
import com.labfire.fe.auth.AccessAuthenticationService;
import com.labfire.fe.auth.AuthException;
import com.labfire.fe.auth.AuthLevel;
import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.auth.AuthenticationException;
import com.labfire.fe.auth.AuthenticationService;
import com.labfire.fe.cache.CacheException;
import com.labfire.fe.cache.CacheService;
import com.labfire.fe.cache.CachedObject;
import com.labfire.fe.common.InitializeException;
import com.labfire.fe.common.User;
import com.labfire.fe.db.ConnectionService;
import com.labfire.fe.log.LogService;
import com.labfire.fe.util.Connections;
import com.labfire.fe.util.Strings;

/**
 * PrefService
 *
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public final class PrefService extends FireEngineComponent {
	private static final int TTL = 1800;
	private static final String PREF_SALT = "clf_prefs_";
	private static AuthToken serviceAuthToken;
	private static Map systemPrefs = new HashMap();
	
	public PrefService(FireEngineConfig config) throws InitializeException {
		super(config);
		String key;
		String value;
		String query = "SELECT * FROM Prefs WHERE PrefUser=0";
		serviceAuthToken = config.getServiceAuthToken();
		try {
			ResultSet rs = Connections.select(serviceAuthToken, query, ConnectionService.getSystemConnectionName());
			if (rs != null) {
				while (rs.next()) {
					key = rs.getString("PrefKey");
					value = rs.getString("PrefValue");
					if ((key != null) && (value != null)) {
						systemPrefs.put(key, value);
					}
				}
				rs.close();
			}
		} catch (Exception e) {
			throw new InitializeException("Unable to initialize system preferences", e);
		}
	}
	
	protected void unload() {
		systemPrefs.clear();
	}
	
	public static boolean prefExists(String key) {
		return systemPrefs.containsKey(key);
	}
	
	public static Map getSystemPrefs() {
		return Collections.unmodifiableMap(systemPrefs);
	}
	
	public static String getSystemPref(String key, String defaultValue) {
		String value = (String)systemPrefs.get(key);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}
	
	public static synchronized void setSystemPref(AuthToken at, String key, String value) throws SQLException, AuthException {
		String update;
		User u = null;
		
		try {
			u = (User)((CachedObject)CacheService.getCache(at)).object;
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		
		int accessLevel = AccessAuthenticationService.findAID(u.getUserAccessToken());
		if (accessLevel == AuthLevel.ACCESS_ADMIN) {
			if (getSystemPref(key, null) == null) {
				update = "INSERT INTO Prefs (PrefUser,PrefKey,PrefValue) VALUES (0,'" + Strings.escapeSQL(key)
						+ "','" + Strings.escapeSQL(value) + "')";
			} else {
				update = "UPDATE Prefs SET PrefValue='" + Strings.escapeSQL(value) + "' WHERE PrefUser=0 AND PrefKey='" 
						+ Strings.escapeSQL(key) + "'";
			}
			ResultSet rs = Connections.update(serviceAuthToken, update, ConnectionService.getSystemConnectionName());
			if (rs != null) {
				rs.close();
			}
			systemPrefs.put(key, value);
		}
	}
	
	/**
	 * Method removeSystemPref.
	 * @param at
	 * @param key
	 */
	public static void removeSystemPref(AuthToken at, String key) throws SQLException, AuthException {
		String update;
		User u = null;
		
		
		try {
			u = (User)((CachedObject)CacheService.getCache(at)).object;
		} catch (CacheException ce) {
			AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
			LogService.logError(ae.getMessage(), ce);
			throw ae;
		}
		
		int accessLevel = AccessAuthenticationService.findAID(u.getUserAccessToken());
		if (accessLevel == AuthLevel.ACCESS_ADMIN) {
			update = "DELETE FROM prefs WHERE PrefKey='" + Strings.escapeSQL(key) + "'";
			systemPrefs.remove(key);
			ResultSet rs = Connections.update(serviceAuthToken, update, ConnectionService.getSystemConnectionName());
			if (rs != null) {
				rs.close();
			}
		}
	}
	
	public static String getUserPref(AuthToken at, String key, String defaultValue) {
		String value;
		Map userPrefs;
		if (AuthenticationService.isValidToken(at)) {
			userPrefs = getUserPrefs(at);
			if (userPrefs != null) {
				value = (String)userPrefs.get(key);
				if (value != null) {
					return value;
				}
			} 
		}
		return getSystemPref(key, defaultValue);
	}
	
	public static synchronized void setUserPref(AuthToken at, String key, String value) throws SQLException, AuthException {
		if (getSystemPref(key, null) != null) {
			Map userPrefs;
			String update;
			
			try {
				CacheService.getCache(at);
			} catch (CacheException ce) {
				AuthenticationException ae = new AuthenticationException("A CacheException has occurred.", ce);
				LogService.logError(ae.getMessage(), ce);
				throw ae;
			}
			
			if (getUserPref(at, key, null) == null) {
				update = "INSERT INTO Prefs (PrefUser,PrefKey,PrefValue) VALUES ("
						+ AuthenticationService.findID(at) + ",'" + Strings.escapeSQL(key)
						+ "','" + Strings.escapeSQL(value) + "')";
			} else {
				update = "UPDATE Prefs SET PrefValue='" + Strings.escapeSQL(value)
						+ "' WHERE PrefUser=" + AuthenticationService.findID(at)
						+ " AND PrefKey='" + Strings.escapeSQL(key) + "'";
			}
			ResultSet rs = Connections.update(serviceAuthToken, update, ConnectionService.getSystemConnectionName());
			if (rs != null) {
				rs.close();
			}
			userPrefs = getUserPrefs(at);
			if (userPrefs != null) {
				userPrefs.put(key, value);
			}
		}
	}
	
	private static Map getUserPrefs(AuthToken at) {
		CachedObject co;
		Map userPrefs = null;
		try {
			co = (CachedObject)CacheService.getCache(Strings.hashCode(PREF_SALT + at));
			if (co != null) {
				co.setExpiration(TTL);
				if (co.object == null) {
					try {
						co.object = initUserPrefs(at);
					} catch (Exception e) {
						LogService.logError("Unable to initialize preferences for " + at, e);
					}
				}
				userPrefs = (Map)co.object;
			}
		} catch (CacheException ce) {
			try {
				userPrefs = initUserPrefs(at);
				co = new CachedObject(userPrefs, Strings.hashCode(PREF_SALT + at), TTL);
				CacheService.putCache(co);
			} catch (Exception e) {
				LogService.logError("Unable to initialize preferences for  " + at, e);
			}
		}
		return userPrefs;
	}
	
	private static Map initUserPrefs(AuthToken at) throws SQLException, AuthException {
		String inValue;
		String inKey;
		Map userPrefs = new HashMap();
		String query = "SELECT * FROM Prefs WHERE PrefUser=" + AuthenticationService.findID(at);
		ResultSet rs = Connections.select(serviceAuthToken, query, ConnectionService.getSystemConnectionName());
		if (rs != null) {
			while (rs.next()) {
				inKey = rs.getString("PrefKey");
				inValue = rs.getString("PrefValue");
				if ((inKey != null) && (inValue != null)) {
					userPrefs.put(inKey, inValue);
				}
			}
			rs.close();
		}
		return userPrefs;
	}
	
	public String getStatus() {
		return "PrefService: entries=" + systemPrefs.size();
	}
}
