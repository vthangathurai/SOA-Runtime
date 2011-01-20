/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;

import org.ebayopensource.turmeric.tools.AbstractCodegenTestCase;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.util.ClassPathUtil;
import org.junit.Assert;
import org.junit.Test;


public class ClassPathUtilTest extends AbstractCodegenTestCase {
	@Test
	public void testCyclicManifest() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();

		File jarA = TestResourceUtil.copyResource(
				"cyclic-manifest-classpath/a.jar", testingdir, "lib");
		File jarB = TestResourceUtil.copyResource(
				"cyclic-manifest-classpath/b.jar", testingdir, "lib");
		File jarC = TestResourceUtil.copyResource(
				"cyclic-manifest-classpath/c.jar", testingdir, "lib");

		// Only mention a.jar in URLClassLoader (the rest should be found
		// via the "Class-Path" attribute in the a.jar META-INF/MANIFEST.MF
		URL urls[] = { jarA.toURI().toURL() };

		ClassLoader original = Thread.currentThread().getContextClassLoader();
		URLClassLoader ucl = new URLClassLoader(urls, original);
		try {
			Thread.currentThread().setContextClassLoader(ucl);
			LinkedList<File> classpath = ClassPathUtil.getClassPath();

			Assert.assertThat(classpath, hasItem(jarA));
			Assert.assertThat(classpath, hasItem(jarB));
			Assert.assertThat(classpath, hasItem(jarC));
		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}

	@Test
	public void testFindSoaClient() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();

		ClassLoader original = Thread.currentThread().getContextClassLoader();
		URLClassLoader ucl = new URLClassLoader(new URL[0], original);
		try {
			Thread.currentThread().setContextClassLoader(ucl);
			LinkedList<File> classpath = ClassPathUtil.getClassPath();

			String expected = File.separator + "soa-client";
			boolean found = false;
			for (File file : classpath) {
				System.out.println("Class-Path Entry:: " + file.getAbsolutePath());
				if (file.getAbsolutePath().contains(expected)) {
					found = true;
				}
			}

			Assert.assertTrue("Should have found soa-client within classpath",
					found);
		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}
}
