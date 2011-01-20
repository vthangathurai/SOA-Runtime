/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.ebayopensource.turmeric.junit.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.junit.logging.UKernelLoggingUtils;
import org.ebayopensource.turmeric.junit.rules.TestingDir;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.codegen.NonInteractiveCodeGen;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;


public class AbstractCodegenTestCase extends AbstractTurmericTestCase {
	@Rule
	public TestingDir testingdir = new TestingDir();

	@Before
	public void initLogs() {
		UKernelLoggingUtils.initTesting(MavenTestingUtils.getTargetDir());
	}

	/**
	 * Load {@link Properties} from disk.
	 * 
	 * @param propFile
	 *            the properties file to load.
	 * @return the loaded properties (or empty properties if file does not exist)
	 * @throws IOException
	 *             if unable to read existing file
	 */
	protected Properties loadProperties(File propFile) throws IOException {
		Properties props = new Properties();
		if (!propFile.exists()) {
			return props;
		}
		FileReader reader = null;
		try {
			reader = new FileReader(propFile);
			props.load(reader);
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return props;
	}
	
	/**
	 * Load {@link Properties} from classpath.
	 * 
	 * @param resource
	 *            the properties resource to load.
	 * @return the loaded properties (or empty properties if resource not found)
	 * @throws IOException
	 *             if unable to read resource
	 */
	protected Properties loadProperties(String resource) throws IOException {
		Properties props = new Properties();
		Enumeration<URL> enurls = this.getClass().getClassLoader()
				.getResources(resource);
		if (!enurls.hasMoreElements()) {
			return props;
		}
		List<URL> urls = Collections.list(enurls);
		Assert.assertThat(
				"Encountered multiple hits for resource: " + resource,
				urls.size(), is(1));
		InputStream stream = null;
		try {
			stream = urls.get(0).openStream();
			props.load(stream);
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return props;
	}

	/**
	 * Write {@link Properties} to disk.
	 * 
	 * @param propFile
	 *            the file to write to
	 * @param props
	 *            the properties to write.
	 * @throws IOException
	 *             if unable to create the file
	 */
	protected void writeProperties(File propFile, Properties props)
			throws IOException {
				FileWriter writer = null;
				try {
					writer = new FileWriter(propFile);
					props.store(writer, "Created by testcase");
				} finally {
					IOUtils.closeQuietly(writer);
				}
			}

	@Before
	public final void preventBadTests() {
		mavenTestingRules.setStrictReadPaths(true);
		mavenTestingRules.setStrictWritePaths(true);
		mavenTestingRules.setFailOnViolation(true);
	}

	public void performDirectCodeGen(String args[], File binDir) throws Exception {
		NonInteractiveCodeGen gen = new NonInteractiveCodeGen();
		MavenTestingUtils.ensureDirExists(binDir);
		gen.addExtraClassPath(binDir);
		gen.addJavaToolsClassPath();
		gen.execute(args);
	}
	
	public void performDirectCodeGen(String args[]) throws Exception {
		performDirectCodeGen(args, testingdir.getFile("bin"));
	}
}
