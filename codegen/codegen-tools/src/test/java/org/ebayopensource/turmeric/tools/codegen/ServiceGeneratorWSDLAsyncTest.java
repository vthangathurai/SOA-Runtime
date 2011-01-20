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

import org.apache.axis2.wsdl.WSDL2Java;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.junit.Ignore;
import org.junit.Test;


public class ServiceGeneratorWSDLAsyncTest extends AbstractServiceGeneratorTestCase {

	@Test
	public void asyncWSDL() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/CalcService1.wsdl");
		File destDir = getTestDestDir();
		File jdestDir = new File(destDir, "client");
		File binDir = testingdir.getFile("bin");
		
		MavenTestingUtils.ensureDirExists(getTestDestPath("meta-src"));

		// Setup arguments
		// @formatter:off
		String args[] =  new String[] {
			"-wsdl", wsdl.getAbsolutePath(),
			"-namespace", SOAConstants.DEFAULT_SERVICE_NAMESPACE,
			"-servicename", "CalculatorIntf",
			"-scv", "1.0.0",
			"-dest", destDir.getAbsolutePath(),
			"-bin", binDir.getAbsolutePath(),
			"-gentype", "All",
			"-jdest", jdestDir.getAbsolutePath()
		};
		// @formatter:on

		performDirectCodeGen(args);
	}

	@Test
	public void asyncWSDLVoidOperationNoArguments() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/SampleServiceAsync.wsdl");
		File destDir = getTestDestDir();
		File jdestDir = new File(destDir, "client");
		File binDir = testingdir.getFile("bin");
		
		MavenTestingUtils.ensureDirExists(getTestDestPath("meta-src"));

		// Setup arguments
		// @formatter:off
		String args[] =  new String[] {
			"-wsdl", wsdl.getAbsolutePath(),
			"-namespace", SOAConstants.DEFAULT_SERVICE_NAMESPACE,
			"-servicename", "SampleIntf",
			"-scv", "1.0.0",
			"-dest", destDir.getAbsolutePath(),
			"-bin", binDir.getAbsolutePath(),
			"-gentype", "All",
			"-jdest", jdestDir.getAbsolutePath()
		};
		// @formatter:on

		performDirectCodeGen(args);
	}
}
