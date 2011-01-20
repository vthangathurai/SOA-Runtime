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

import org.ebayopensource.turmeric.tools.AbstractCodegenTestCase;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.util.JavacHelper;
import org.junit.Assert;
import org.junit.Test;


public class JavacHelperTest extends AbstractCodegenTestCase {
	
	@Test
	public void testCyclicManifest() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		
		File jarA = TestResourceUtil.copyResource("cyclic-manifest-classpath/a.jar", testingdir, "lib");
		File jarB = TestResourceUtil.copyResource("cyclic-manifest-classpath/b.jar", testingdir, "lib");
		File jarC = TestResourceUtil.copyResource("cyclic-manifest-classpath/c.jar", testingdir, "lib");

		// Only mention a.jar in URLClassLoader (the rest should be found
		// via the "Class-Path" attribute in the a.jar META-INF/MANIFEST.MF
		URL urls[] = {
			jarA.toURI().toURL()
		};
		
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		URLClassLoader ucl = new URLClassLoader(urls, original);
		try {
			Thread.currentThread().setContextClassLoader(ucl);
			String classpath = JavacHelper.buildClasspath(testingdir.getDir().getAbsolutePath());
			Assert.assertThat(classpath, containsString(jarA.getAbsolutePath()));
			Assert.assertThat(classpath, containsString(jarB.getAbsolutePath()));
			Assert.assertThat(classpath, containsString(jarC.getAbsolutePath()));
		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}
}
