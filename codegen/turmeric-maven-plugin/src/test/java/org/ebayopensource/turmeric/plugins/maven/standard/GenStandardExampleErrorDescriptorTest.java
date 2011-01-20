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

import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.ebayopensource.turmeric.plugins.maven.BaseTurmericMojoTestCase;
import org.ebayopensource.turmeric.plugins.maven.GenErrorDescriptorMojo;
import org.junit.Test;

public class GenStandardExampleErrorDescriptorTest extends
		BaseTurmericMojoTestCase<GenErrorDescriptorMojo> {

	@Override
	public String getTestMojoDirName() {
		return "standard/exampleErrorDescriptor";
	}

	@Override
	public String getTestMojoGoal() {
		return "gen-errordescriptor";
	}

	@Test
	public void testGenerateErrorDescriptor() throws Exception {
		GenErrorDescriptorMojo mojo = createMojo();
		mojo.execute();

		// Validate that something got created.
		File genDir = mojo.getOutputDirectory();
		PathAssert.assertDirExists(genDir);

		// Make sure specific file is generated
		String sourceFilename = "com.ebay.domain.repositoryservice.common.error"
				.replace('.', File.separatorChar)
				+ File.separatorChar
				+ "RepositoryServiceErrorDescriptor.java";
		File sourceFile = new File(genDir, sourceFilename);
		PathAssert.assertFileExists(sourceFile);

		// Make sure only 1 file is generated
		PathAssert.assertFileCount("Generated Java File Count", 1, genDir, ".java");
	}

}
