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
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.junit.Test;


public class ServiceGeneratorNetworkTest extends AbstractServiceGeneratorTestCase {
	
	@Test
	public void amazonWSDL() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String args[] = {
			"-servicename", "AWSECommerceService",
			"-wsdl", "http://webservices.amazon.com/AWSECommerceService/AWSECommerceService.wsdl",
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", SOAConstants.DEFAULT_SERVICE_NAMESPACE,
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.service",
			"-bin", binDir.getAbsolutePath(),
			"-cn", "AWSECommerceService",
			"-icsi", 
			"-gin", "AWSECommerceService" 
		};
		// @formatter:on
		
		performDirectCodeGen(args);
	}

	@Test
	public void ebayCSSvcCSUpdateMACActivityAddAttachmentsWSDL() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/ebayCSSvc-CSUpdateMACActivityAddAttachments.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "eBayCSAPIInterfaceService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", SOAConstants.DEFAULT_SERVICE_NAMESPACE,
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.service",
			"-bin", binDir.getAbsolutePath(),
			"-cn", "eBayCSAPIInterfaceService",
			"-icsi", "-gin", "eBayCSAPIInterfaceService" };
		// @formatter:on

		performDirectCodeGen(args);
	}

	@Test
	public void ebayCSSvcWSDL() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/ebayCSSvc.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "eBayCSAPIInterfaceService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", SOAConstants.DEFAULT_SERVICE_NAMESPACE,
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.service",
			"-bin", binDir.getAbsolutePath(),
			"-cn", "eBayCSAPIInterfaceService",
			"-icsi", "-gin", "eBayCSAPIInterfaceService" };
		// @formatter:on

		performDirectCodeGen(args);
	}

	@Test
	public void serviceGeneratorWSDLWithImportReferringToHttpLink() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/WsdlWith_HTTP_Import.wsdl");
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
			"-namespace", "http://www.ebay.com/soaframework/service/calc",
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.tools.codegen",
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on

		performDirectCodeGen(args);
	}
}
