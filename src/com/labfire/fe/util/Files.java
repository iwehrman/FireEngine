/*
 * Files.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Files
 * 
 * @author <a href="http://labfire.com/">Labfire, Inc.</a>
 */
public class Files {
	private static final int BYTES_KB = 1024;
	private static final int BYTES_MB = BYTES_KB * 1024;
	private static final int BYTES_GB = BYTES_MB * 1024;

	/**
	 * move inFile to outFile on the filesystem
	 * @param inFile
	 * @param outFile
	 * @return boolean
	 * @throws IOException
	 */
	public static boolean move(File inFile, File outFile) throws IOException {
		if (inFile.getCanonicalPath().equals(outFile.getCanonicalPath())) {
			// inFile and outFile are the same,
			// hence no copying is required
			return false;
		}
	
		FileInputStream fin = new FileInputStream(inFile);  
		FileOutputStream fout = new FileOutputStream(outFile);
		byte[] buffer = new byte[256];
        while (true) {
          int bytesRead = fin.read(buffer);
          if (bytesRead == -1) break;
          fout.write(buffer, 0, bytesRead);
        }
		fin.close();
		fout.close();
		return inFile.delete();
	}
	
	/**
	 * Recursively removes files starting at a path
	 * @param path
	 * @return boolean whether or not the files were successfully removed
	 */
	public static boolean removeFiles(File path) {
		File[] files = path.listFiles();
		if (files != null) {
			boolean success = true;
			for (int i = 0; i < files.length; i++) {
				success &= removeFiles(files[i]);
			}
			return success &= path.delete();
		} else {
			return path.delete();
		}
	}
	
	/**
	 * compute the SHA-1 hash of a file
	 * @param file
	 * @return String
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static String computeHash(File file) throws IOException, NoSuchAlgorithmException {
		byte[] buffer = new byte[2048];
		byte[] hex = new byte[32];
		FileInputStream input = new FileInputStream(file);
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		while (input.read(buffer) != -1) {
			md.update(buffer);
		}
		Strings.bytesToHex(md.digest(), hex);
		return new String(hex);
	}
	
	/**
	 * format a filesize in bytes to be human-readable
	 * @param bytes
	 * @return String
	 */
	public static String formatSize(long bytes) {
		int amt;
		if (bytes >= BYTES_GB) {
			amt = (int) (bytes / BYTES_GB);
			return new StringBuffer(new Integer(amt).toString()).append(" GB").toString();
		} else if (bytes >= BYTES_MB) {
			amt = (int) (bytes / BYTES_MB);
			return new StringBuffer(new Integer(amt).toString()).append(" MB").toString();
		} else if (bytes >= BYTES_KB) {
			amt = (int) (bytes / BYTES_KB);
			return new StringBuffer(new Integer(amt).toString()).append(" KB").toString();
		} else {
			return new StringBuffer(new Long(bytes).toString()).append(" bytes").toString();
		}
	}
}