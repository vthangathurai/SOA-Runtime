/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.monitoring.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;


public class Utils {
	private static final boolean DEBUG = false;

	public static void lanunchBrower(String browerPath, String fileToDisplay) {

		String[] cmd = new String[] { browerPath, fileToDisplay };
		try {
			Runtime.getRuntime().exec(cmd);
			// System.out.println(p.getErrorStream());
			// don't wait for the brower to exit
			// p.waitFor();
			// printDebugMessage(p.exitValue() +"");
		} catch (IOException e) {
			System.out.println("Error: Please check Internet Browser path. "
					+ e.getMessage() + "\n"); //KEEPME
			System.exit(0);
		}

	}

	public static void copyFile(File in, File out) throws Exception {
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		FileChannel sourceChannel = null;
		FileChannel destinationChannel = null;
		
		try {
			outStream = new FileOutputStream(out);
			inStream = new FileInputStream(in);
			destinationChannel = outStream.getChannel();
			sourceChannel = inStream.getChannel();
			sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		} finally {
			CodeGenUtil.closeQuietly(inStream);
			CodeGenUtil.closeQuietly(outStream);
			CodeGenUtil.closeQuietly(sourceChannel);
			CodeGenUtil.closeQuietly(destinationChannel);
		}
	}

	public static List<String> tokenizeLine(String line) {
		List<String> tokenList = new ArrayList<String>();
		StringTokenizer tokens = new StringTokenizer(line, ";");
		while (tokens.hasMoreTokens()) {
			tokenList.add(tokens.nextToken());
		}

		return tokenList;
	}

	public static String addDoubles(String value1, String value2) {
		Double result = Double.parseDouble(value1) + Double.parseDouble(value2);
		return String.valueOf(result);
	}
	
	public static String addBigDecimals(String value1, String value2) {
		BigDecimal result = (new BigDecimal(value1.trim())).add(new BigDecimal(value2.trim()));
		return String.valueOf(result);
	}

	public static String addLongs(String value1, String value2) {
		Long result = Long.valueOf(value1) + Long.valueOf(value2);
		return String.valueOf(result);
	}
	
	public static void printMessage(String... args) {
		for (String msg : args) {
			System.out.print(msg); //KEEPME
		}
		System.out.println(""); //KEEPME
	}

	public static void printDebugMessage(String... args) {
		if (DEBUG) {
			printMessage(args);
		}
	}
}
