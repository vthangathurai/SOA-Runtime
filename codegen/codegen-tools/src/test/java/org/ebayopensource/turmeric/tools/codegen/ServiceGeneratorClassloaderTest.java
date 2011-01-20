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


public class ServiceGeneratorClassloaderTest extends AbstractServiceGeneratorTestCase {
	
	/**
	 * A version of Test1Service started to fail with a ClassCastException.
	 * <p>
	 * Caused by: java.lang.ClassCastException: 
	 * org.apache.axis2.receivers.RawXMLINOnlyMessageReceiver cannot be 
	 * cast to org.apache.axis2.engine.MessageReceiver
	 * @throws Exception 
	 */
	@Test
	public void testAxisServiceGenIssue() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		
		File rootDir = testingdir.getDir();
		File srcDir = testingdir.getFile("src");
		File mdestDir = testingdir.getFile("target/generated-resources/codegen");
		File jdestDir = testingdir.getFile("target/generated-sources/codegen");
		File binDir = testingdir.getFile("target/classes");
		
		TestResourceUtil.copyResourceRootDir("sample-services/Test1Service", testingdir);
		
		MavenTestingUtils.ensureDirExists(mdestDir);
		MavenTestingUtils.ensureDirExists(jdestDir);
		MavenTestingUtils.ensureDirExists(binDir);

		// @formatter:off
		String args[] = {
			"-gentype", "ClientNoConfig",
			"-pr", rootDir.getAbsolutePath(),
			"-mdest", mdestDir.getAbsolutePath(),
			"-jdest", jdestDir.getAbsolutePath(),
			"-gip", "org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message",
			"-interface", "org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.Test1Service",
			"-servicename", "Test1Service",
			"-bin", binDir.getAbsolutePath(),
			"-src", srcDir.getAbsolutePath(),
		};
		// @formatter:on

		performDirectCodeGen(args, binDir);
	}
}
