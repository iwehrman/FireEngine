/*
 * Connections.java
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.labfire.fe.auth.AuthException;
import com.labfire.fe.auth.AuthToken;
import com.labfire.fe.cache.DependentCacheable;
import com.labfire.fe.db.ConnectionService;
import com.labfire.fe.db.Dependency;
import com.labfire.fe.db.StaticResultSet;
import com.labfire.fe.log.LogService;

/**
 * Connections
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class Connections {
	private static final int CACHE_SIZE = Math.round(Runtime.getRuntime().freeMemory() / (1024*1024));
	private static final String UPDATE = "UPDATE ";
	private static final String INSERT = "INSERT INTO ";
	private static final String DELETE = "DELETE FROM ";
	private static final String SELECT = "SELECT * FROM ";
	private static final String WHERE = " WHERE ";
	private static final String OID_EQ = WHERE + "oid=";
	private static final Pattern queryPattern = Pattern.compile("FROM\\s+?([^\\(]+?\\w)(\\s+?\\w|\\s*?$)");
	private static final Pattern tablePattern = Pattern.compile("(\\w+?)\\b");
	private static int cacheHit = 0;
	private static int cacheMiss = 0;
	private static Map sqlCache = new HashMap();
	
	
	/**
	 * Connections
	 */
	private Connections() {}
	
	/**
	 * getDepsFromQuery
	 */
	public static Set getDepsFromQuery(String update, String connection) {
		Set deps = new HashSet();
		Matcher queryMatcher = queryPattern.matcher(update);
		while (queryMatcher.find()) {
			Matcher tableMatcher = tablePattern.matcher(queryMatcher.group(1));
			while (tableMatcher.find()) {
				deps.add(new Dependency(connection, tableMatcher.group(1)));
			}
		}
		return deps;
	}
	
	/**
	 * getTableFromUpdate
	 */
	public static String getTableFromUpdate(String update) {
		String snip;
		
		if (update != null) {
			update = update.trim().toUpperCase();
		} else {
			return null;
		}
		if (update.startsWith(UPDATE)) {
			snip = update.substring(UPDATE.length());
			return snip.substring(0, snip.indexOf(' '));
		} else if (update.startsWith(INSERT)) {
			snip = update.substring(INSERT.length());
			return snip.substring(0, snip.indexOf(' '));
		} else if (update.startsWith(DELETE)) {
			snip = update.substring(DELETE.length());
			return snip.substring(0, snip.indexOf(' '));
		} else {
			return null;
		}
	}
	
	/**
	 * logSQLWarnings
	 */
	public static void logSQLWarnings(Connection cn) {
		try {
			SQLWarning sqlw = cn.getWarnings();
			while (sqlw != null) {
				LogService.logWarn(sqlw.getMessage());
				sqlw = sqlw.getNextWarning();
			}
		} catch (SQLException sqle) {
			LogService.logError("Error logging SQL warnings.", sqle);
		}
	}
	
	/**
	 * logSQLWarnings
	 */
	public static void logSQLWarnings(ResultSet rs) {
		try {
			SQLWarning sqlw = rs.getWarnings();
			while (sqlw != null) {
				LogService.logWarn(sqlw.getMessage());
				sqlw = sqlw.getNextWarning();
			}
		} catch (SQLException sqle) {
			LogService.logError("Error logging SQL warnings.", sqle);
		}
	}
	
	/**
	 * logSQLWarnings
	 */
	public static void logSQLWarnings(Statement stmt) {
		try {
			SQLWarning sqlw = stmt.getWarnings();
			while (sqlw != null) {
				LogService.logWarn(sqlw.getMessage());
				sqlw = sqlw.getNextWarning();
			}
		} catch (SQLException sqle) {
			LogService.logError("Error logging SQL warnings.", sqle);
		}
	}
	
	/**
	 * select
	 * 
	 * @return ResultSet
	 */
	public static ResultSet select(String query, Connection cn) throws SQLException {
		return _select(query, cn);
	}
	
	/**
	 * select
	 * 
	 * @return ResultSet
	 */
	public static ResultSet select(String query, Connection cn, boolean useCache) throws SQLException {
		ResultSet rs;
		if (useCache) {
			DatabaseMetaData md = cn.getMetaData();
			String url = md.getURL();
			LRUCache lru = (LRUCache)sqlCache.get(url);
			if (lru == null) {
				lru = new LRUCache(CACHE_SIZE);
				sqlCache.put(url, lru);
			}
			DependentCacheable dc = (DependentCacheable)lru.get(query);
			if (dc == null || dc.isExpired()) {
				String user = md.getUserName();
				rs = new StaticResultSet(_select(query, cn));
				Set deps = getDepsFromQuery(query, ConnectionService.getConnectionName(url, user));
				dc = new DependentCacheable(rs, query, deps);
				lru.put(query, dc);
				cacheMiss++;
			} else {
				rs = (StaticResultSet)dc.object;
				cacheHit++;
			}
			rs = (StaticResultSet)((StaticResultSet)rs).clone();
		} else {
			rs = _select(query, cn);
		}
		return rs;
	}

	/**
	 * select
	 * 
	 * @return ResultSet
	 */
	public static ResultSet select(AuthToken at, String query, String name, boolean useCache) throws SQLException,AuthException {
		ResultSet rs;
		if (useCache) {
			String url = ConnectionService.getConnectionUrl(name);
			LRUCache lru = (LRUCache)sqlCache.get(url);
			if (lru == null) {
				lru = new LRUCache(CACHE_SIZE);
				sqlCache.put(url, lru);
			}
			DependentCacheable dc = (DependentCacheable)lru.get(query);
			if (dc == null || dc.isExpired()) {
				rs = new StaticResultSet(_select(at, query, name));
				Set deps = getDepsFromQuery(query, name);
				dc = new DependentCacheable(rs, query, deps);
				lru.put(query, dc);
				cacheMiss++;
			} else {
				rs = (StaticResultSet)dc.object;
				cacheHit++;
			}
			rs = (StaticResultSet)((StaticResultSet)rs).clone();
		} else {
			rs = _select(at, query, name);
		}
		return rs;
	}

	/**
	 * select
	 * 
	 * @return ResultSet
	 */
	public static ResultSet select(AuthToken at, String query, String name) throws SQLException,AuthException {
		return _select(at, query, name);
	}
	
	/**
	 * select
	 * 
	 * @return ResultSet
	 */
	public static ResultSet select(AuthToken at, String query) throws SQLException,AuthException {
		return _select(at, query, ConnectionService.getSystemConnectionName());
	}
	
	/**
	 * select
	 * 
	 * @return ResultSet
	 */
	public static ResultSet select(AuthToken at, String query, boolean useCache) throws SQLException,AuthException {
		return select(at, query, ConnectionService.getSystemConnectionName(), useCache);
	}
	
	/**
	 * _select
	 * 
	 * @return ResultSet
	 */
	private static ResultSet _select(String query, Connection cn) throws SQLException {
		Statement stmt = cn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		logSQLWarnings(rs);
		return rs;
	}
	
	/**
	 * select
	 * 
	 * @return ResultSet
	 */
	private static ResultSet _select(AuthToken at, String query, String name) throws SQLException,AuthException {
		ResultSet rs;
		Connection cn = null;
		try {
			cn = ConnectionService.getConnection(at, name);
			if (cn != null) {
				Statement stmt = cn.createStatement();
				rs = stmt.executeQuery(query);
				logSQLWarnings(rs);
			} else {
				throw new SQLException("Unable to retrieve connection for " + name);
			}				
		} catch (SQLException sqle) {
			LogService.logError("Caught SQLException.", sqle);
			LogService.logDebug(query);
			throw sqle;
		} finally {
			if (cn != null) {
				ConnectionService.returnConnection(cn);
			}
		}
		return rs;
	}
	
	/**
	 * update
	 * 
	 * @return int
	 */
	public static ResultSet update(String update, Connection cn) throws SQLException {
		return _update(update, cn);
	}

	/**
	 * update
	 * 
	 * @return int
	 */
	public static ResultSet update(AuthToken at, String update, String name) throws SQLException,AuthException {
		ResultSet rs = null;
		Connection cn = null;
		
		try {
			cn = ConnectionService.getConnection(at, name);
			if (cn != null) {
				rs = _update(update, cn);
			} else {
				throw new SQLException("Unable to retrieve connection for " + name);
			}
		} catch (SQLException sqle) {
			LogService.logError("Caught SQLException.", sqle);
			LogService.logDebug(update);
			throw sqle;
		} finally {
			if (cn != null) {
				ConnectionService.returnConnection(cn);
			}
		}
		return rs;
	}
	
	/**
	 * update
	 * 
	 * @return int
	 */
	public static ResultSet update(AuthToken at, String update) throws SQLException,AuthException {
		return update(at, update, ConnectionService.getSystemConnectionName());	
	}
	
	/**
	 * _update
	 * 
	 * @return int
	 */
	private static ResultSet _update(String update, Connection cn) throws SQLException {
		ResultSet rs = null;
		String tableName = getTableFromUpdate(update);
		ConnectionService.setLastModifiedFromUrl(cn.getMetaData().getURL(), tableName);
		Statement stmt = cn.createStatement();
		stmt.executeUpdate(update);
		logSQLWarnings(stmt);
		if (stmt instanceof org.postgresql.PGStatement) {
			long oid = ((org.postgresql.PGStatement)stmt).getLastOID();
			if (oid > 0) { // oid generated from insert
				StringBuffer query = new StringBuffer(SELECT);
				query.append(tableName);
				query.append(OID_EQ);
				query.append(oid);
				rs = stmt.executeQuery(query.toString());
			} else {
				rs = StaticResultSet.EMPTY_RS;
			}
		} else {
			LogService.logError("Unable to return updates ResultSet");
		}
		return rs;
	}
}
