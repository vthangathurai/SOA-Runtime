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
import java.util.ArrayList;

import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.WSDLConversionToSingleNamespace;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author aupadhay
 */
public class WSDLConversionToSingleNamespaceTest extends AbstractServiceGeneratorTestCase {
	@Test
	public void testConvertWSDLwithMultipleNamespaceScenario1() throws Exception {
		testingdir.ensureEmpty();
		
		File wsdl = TestResourceUtil.copyResource("org/ebayopensource/turmeric/test/tools/codegen/data/Testing.wsdl", testingdir, "meta-src");
		File wsdlout = new File(wsdl.getParentFile(), "Test.wsdl");
		
		WSDLConversionToSingleNamespace wsdlconv = new WSDLConversionToSingleNamespace();
		wsdlconv.convertWSDL(wsdl.getAbsolutePath(), wsdlout.getAbsolutePath());

		int totalSchemas = WSDLConversionTestHelper.getNumberOfschemaFromWSDL(wsdlout);
		Assert.assertEquals(5, totalSchemas);
	}

	@Test
	public void testIfProperImportsAreAdded() throws Exception {
		testingdir.ensureEmpty();

		File wsdl = TestResourceUtil.copyResource("org/ebayopensource/turmeric/test/tools/codegen/data/Testing.wsdl", testingdir, "meta-src");
		File wsdlout = new File(wsdl.getParentFile(), "Test.wsdl");

		WSDLConversionToSingleNamespace wsdlconv = new WSDLConversionToSingleNamespace();
		wsdlconv.convertWSDL(wsdl.getAbsolutePath(), wsdlout.getAbsolutePath());

		ArrayList<String> allImports = WSDLConversionTestHelper
				.getAllNewNamespaceAddedInImports(wsdlout);
		Assert.assertThat(allImports, hasItem("http://www.testing.com/lib1"));
		Assert.assertThat(allImports, hasItem("http://www.testing.com/lib2"));
		Assert.assertThat(allImports, hasItem("http://www.testing.com/lib4"));
	}

	@Test
	public void testWsdlWithMultipleSourceTagInvalidCase() throws Exception {
		testingdir.ensureEmpty();

		File wsdl = TestResourceUtil.copyResource("org/ebayopensource/turmeric/test/tools/codegen/data/InvalidTest.wsdl", testingdir, "meta-src");
		File wsdlout = new File(wsdl.getParentFile(), "Test.wsdl");

		WSDLConversionToSingleNamespace wsdlconv = new WSDLConversionToSingleNamespace();
		wsdlconv.convertWSDL(wsdl.getAbsolutePath(),
				wsdlout.getAbsolutePath());
	}
}