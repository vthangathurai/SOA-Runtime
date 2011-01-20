/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;

import org.junit.Test;

public class ServiceGenerator1Test extends AbstractServiceGeneratorTestCase {

	@Test
	public void testBaseConsumerWithInitMethodCase1() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File wsdl = getCodegenDataFileInput("CalcService.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] = new String[] {
			"-servicename", "TestService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "Consumer", 
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(), 
			"-scv", "1.0.0", 
			"-bin", binDir.getAbsolutePath() 
		};
		// @formatter:on
		
		performDirectCodeGen(args, binDir);
		
		assertGeneratedContainsSnippet("gen-src/org/ebayopensource/turmeric/common/v1/services/gen/BaseTestServiceConsumer.java", 
				"ConsumerWithInitMethod.txt", 
				null, null, null);
	}

	@Test
	public void testBaseConsumerWithInitMethodCase2() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File wsdl = getCodegenDataFileInput("CalcService.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] = new String[] {
			"-servicename", "TestService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "SharedConsumer", 
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(), 
			"-scv", "1.0.0", 
			"-bin", binDir.getAbsolutePath() 
		};
		// @formatter:on
		
		performDirectCodeGen(args, binDir);
		
		assertGeneratedContainsSnippet(
				"gen-src/org/ebayopensource/turmeric/common/v1/services/testservice/gen/SharedTestServiceConsumer.java", 
				"ConsumerWithInitMethod.txt", 
				null, null, null);
	}
}
