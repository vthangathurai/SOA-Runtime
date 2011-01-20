/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.standard;

import java.io.File;

import org.codehaus.plexus.logging.Logger;
import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.ebayopensource.turmeric.plugins.maven.BaseTurmericMojoTestCase;
import org.ebayopensource.turmeric.plugins.maven.GenErrorLibraryMojo;
import org.junit.Test;

/**
 * Test basic generation of the error library.
 */
public class GenStandardSampleErrorLibraryTest extends
		BaseTurmericMojoTestCase<GenErrorLibraryMojo> {
	@Override
	public String getTestMojoDirName() {
		return "standard/sampleErrorLibrary";
	}

	@Override
	public String getTestMojoGoal() {
		return "gen-errorlibrary";
	}

	@Test
	public void testStandardGenErrorLibrary() throws Exception {
		setPlexusLoggingLevel(Logger.LEVEL_DEBUG);
		setMojoLoggingDebug(true);

		GenErrorLibraryMojo mojo = createMojo();
		testProjectExecuteMojo(mojo);

		// Validate that something got created.
		File genDir = mojo.getOutputDirectory();
		PathAssert.assertDirExists(genDir);
		PathAssert.assertSubdirNotExists(genDir, "gen-src"); // Bug with -dest

		PathAssert.assertFileCount("Generated Java File Count", 2, genDir, ".java");

		String expectedPackagePath = "org/ebayopensource/turmeric/errorlibrary/sampleerrordomain";
		PathAssert.assertFileExists(genDir, (expectedPackagePath + "/ErrorConstants.java"));
		PathAssert.assertFileExists(genDir, (expectedPackagePath + "/ErrorDataCollection.java"));

		// Validate that gen-src, and gen-meta-src dirs are _NOT_ created
		File basedir = mojo.getProject().getBasedir();
		PathAssert.assertSubdirNotExists(basedir, "gen-src");
		PathAssert.assertSubdirNotExists(basedir, "gen-meta-src");
	}

}
