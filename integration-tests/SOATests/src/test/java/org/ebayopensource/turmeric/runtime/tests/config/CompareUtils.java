/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.config;

import static org.hamcrest.CoreMatchers.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import com.ebay.kernel.util.StringUtils;

public class CompareUtils {

	public static String writeOutputFile(Class<?> clazz, StringBuffer output, String name) throws Exception {
		String compareFilename = name + ".compare.txt";
		String outputFilename = name + ".output.txt";
		URL compareURL = clazz.getResource(compareFilename);
		if(compareURL == null) {
			// Skip writeOutputFile as its not possible to find the file.
			System.out.println("## Resource Not Found: " + compareFilename);
			return null;
		}
		if(compareURL.getProtocol().equals("file") == false) {
			// Can't write to non-file protocols anyway.
			System.out.println("## Not file protocol: " +  compareURL.toExternalForm());
			return null;
		}
		BufferedWriter bw = null;
		try {
			File compareFile = new File(compareURL.toURI());
			File outputDir = compareFile.getParentFile();
			File outputFile = new File(outputDir, outputFilename);
			bw = new BufferedWriter(new FileWriter(outputFile));
			List<String> lines = StringUtils.splitStr(output.toString(), '\n', false);
			for (String line : lines) {
				bw.write(line);
				bw.newLine();
			}
			bw.close();
			return outputFile.getAbsolutePath();
		} catch(IllegalArgumentException e) {
			// We write the *.output.txt file for convenience in the local (Eclipse)
			// testing case.  When doing testing from a jar file under ICE, we do not need
			// this capability and it won't work to try to write output data, so skip this
			// logic.
		} finally {
			IOUtils.closeQuietly(bw);
		}
		return null;
	}
	
	public static String getCompareString(Class<?> clazz, String compareFilename) throws Exception {
		StringBuffer compare = new StringBuffer();
		String fullResourcePath = getClassRelativeResourceName(clazz, compareFilename);
		URL compareURL = clazz.getClassLoader().getResource(fullResourcePath);
		Assert.assertThat("Attempting to find compare string resource: " + fullResourcePath, compareURL, is(notNullValue()));
		InputStream stream = null;
		BufferedReader br =  null;
		try {
			stream = compareURL.openStream();
			br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				compare.append(line);
				compare.append('\n');
			}
			br.close();
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(stream);
		}

		return compare.toString();
	}
	
	public static String getClassRelativeResourceName(Class<?> clazz,
			String resourceName) {
		String baseName = clazz.getName();
		int index = baseName.lastIndexOf('.');
		if (index != -1) {
			return baseName.substring(0, index).replace('.', '/') + "/"
					+ resourceName;
		}

		return resourceName;
	}
}
