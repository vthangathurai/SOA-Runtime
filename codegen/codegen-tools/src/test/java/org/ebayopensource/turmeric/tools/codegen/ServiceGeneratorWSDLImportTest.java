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

import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.junit.Test;


public class ServiceGeneratorWSDLImportTest extends AbstractServiceGeneratorTestCase {

	@Test
	public void serviceGeneratorWSDLWithImportRelativeDirectory() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/CalcServiceWithImportRelative.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "CalcService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soaframework/service/calc",
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.tools.codegen",
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on

		performDirectCodeGen(args);
	}

	@Test
	public void serviceGeneratorWSDLWithImportSameDirectory() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/CalcServiceWithImport.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "CalcService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soaframework/service/calc",
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.tools.codegen",
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on

		performDirectCodeGen(args);
	}
}
