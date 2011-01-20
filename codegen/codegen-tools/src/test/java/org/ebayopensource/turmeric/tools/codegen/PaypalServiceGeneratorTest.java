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
import org.junit.Test;

public class PaypalServiceGeneratorTest extends AbstractServiceGeneratorTestCase {

	@Test
	public void testRemotePayPalWSDL() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File srcDir = getTestSrcDir();
		File destDir = testingdir.getFile("tmp");
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "PayPalAPIInterfaceService",
			"-wsdl", "https://www.paypalobjects.com/wsdl/PayPalSvc.wsdl", 
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", SOAConstants.DEFAULT_SERVICE_NAMESPACE,
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.service",
			"-bin", binDir.getAbsolutePath(),
			"-cn", "PayPalAPIInterfaceService",
			"-icsi", 
			"-gin", "PayPalAPIInterfaceService" };
		// @formatter:on

		performDirectCodeGen(args, binDir);
	}
}
