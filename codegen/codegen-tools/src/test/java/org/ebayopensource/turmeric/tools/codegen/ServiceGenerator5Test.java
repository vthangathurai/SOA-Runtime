/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import static org.hamcrest.Matchers.*;

import java.io.File;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.GeneratedAssert;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.external.WSDLUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class ServiceGenerator5Test extends AbstractServiceGeneratorTestCase {

	// commented because adcommerce changes are on f22575_soafwk23_637
	@Test
	public void serviceGenerationWithPublicServiceName() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = testingdir.getDir();
		
		File wsdl = TestResourceUtil.copyResource("org/ebayopensource/turmeric/test/tools/codegen/data/Testing.wsdl", testingdir, "meta-src");
		String publicServiceName = "MyService";
		
		// @formatter:off
		String args[] = {
			"-servicename", "MyServiceV1",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "ServiceMetadataProps",
			"-publicservicename", publicServiceName,
			"-pr", rootDir.getAbsolutePath()
		};
		// @formatter:on
		
		performDirectCodeGen(args);
		
		File publicServiceWsdl = GeneratedAssert.assertFileExists(rootDir, "gen-meta-src/META-INF/soa/services/wsdl/MyServiceV1/MyServiceV1_public.wsdl");
			
		QName qname = WSDLUtil.getFirstServiceQName(publicServiceWsdl.getAbsolutePath());
		Assert.assertNotNull("QName", qname);
		
		String actualServiceName = qname.getLocalPart();

		Assert.assertEquals(publicServiceName, actualServiceName);
	}

	@Test
	// Enable "-tlx" option when it is implemented
	public void testingTypeLibraryOptionSuccessCase() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/CalcServiceWithImport.wsdl");
		File tlx = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/CalculatorServiceTypeInformation.xml");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] = {
			"-servicename", "CalcService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soaframework/service/calc",
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.tools.codegen",
			"-bin", binDir.getAbsolutePath(),
//			"-tlx", tlx.getAbsolutePath(),
		};
		// @formatter:on
		
		performDirectCodeGen(args);

		GeneratedAssert.assertFileExists(destDir, "gen-src/org/ebayopensource/turmeric/services/Add.java");
		GeneratedAssert.assertFileExists(destDir, "gen-src/org/ebayopensource/turmeric/services/AddResponse.java");
	}

	@Test
	public void testAnnotationJava2WSDL() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] = {
			"-servicename", "AnnServiceJava2WSDL",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.InterfaceForAnnotation.java",
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.0.0",
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on
		
		performDirectCodeGen(args);
	}

	//This was a commented testcase earlier
	@Test
	public void testAnnotationWSDL2Java() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] = {
			"-servicename", "SimpleService",
			"-wsdl", "",
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.0.0",
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on
		
		try {
			performDirectCodeGen(args);
		} catch (MissingInputOptionException exception) {
			Assert.assertThat(exception.getMessage(), containsString("Input file is missing"));
			
		}
	}

}
