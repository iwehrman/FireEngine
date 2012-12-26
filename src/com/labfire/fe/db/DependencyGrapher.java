/*
 * DependencyGrapher.java
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


package com.labfire.fe.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Date;

import com.labfire.fe.log.LogService;
import com.labfire.fe.util.DirectedGraph;


/**
 * DependencyGrapher
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class DependencyGrapher implements Runnable {
	private static final String[] TYPES = { "TABLE" };
	private static final String TABLE_NAME = "TABLE_NAME";
	private static final String FKTABLE_NAME = "FKTABLE_NAME";
	private ConnectionPool pool;
	private DirectedGraph graph;
	
	public DependencyGrapher(ConnectionPool pool, DirectedGraph graph) {
		this.graph = graph;
		this.pool = pool;
	}
	
	/**
	 * run
	 */
	public void run() {
		synchronized (graph) {
			Connection cn = null;
			try {
				Object v1Value;
				Object v2Value;
				Object edge = new Object();
				String tableName;
				String v1Key;
				String v2Key;
				Date now = new Date(System.currentTimeMillis());
				ResultSet rs;
				ResultSet rs0;
				DatabaseMetaData dbmd;
				cn = pool.getConnection();
						
				dbmd = cn.getMetaData();
				rs = dbmd.getTables(null, null, null, TYPES);
				if (rs != null) {
					while (rs.next()) {
						tableName = rs.getString(TABLE_NAME);
						v1Key = tableName.toUpperCase();
						v1Value = (Date) now.clone();
						if (!graph.isVertex(v1Key)) {
							graph.addVertex(v1Key, v1Value);
						}
						rs0 = dbmd.getExportedKeys("", "", tableName);
						if (rs0 != null) {
							while (rs0.next()) {
								v2Key = rs0.getString(FKTABLE_NAME).toUpperCase();
								if (!v1Key.equals(v2Key)) { // no loops
									v2Value = (Date) now.clone();
									if (!graph.isVertex(v2Key)) {
										graph.addVertex(v2Key, v2Value);
									}
									graph.addEdge(v1Key, v2Key, edge);
								}
							}
							rs0.close();
						}
					}
					rs.close();
					LogService.logDebug("Built dependency graph for " + pool.getURL());
				}
			} catch (Exception e) {
				LogService.logError("Failed to graph dependencies", e);
			} finally {
				if (pool != null && cn != null) {
					pool.free(cn);
				}
			}
		}
	}
}
