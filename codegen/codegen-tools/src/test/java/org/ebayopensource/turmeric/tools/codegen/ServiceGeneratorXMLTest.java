///*******************************************************************************
// * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *******************************************************************************/
//package org.ebayopensource.turmeric.tools.codegen;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
//import org.ebayopensource.turmeric.tools.TestResourceUtil;
//import org.ebayopensource.turmeric.tools.XmlAdjuster;
//import org.junit.Test;
//
//
//public class ServiceGeneratorXMLTest extends AbstractServiceGeneratorTestCase {
////	@Test
////	public void serviceGeneratorXml1() throws Exception {
////		MavenTestingUtils.ensureEmpty(testingdir.getDir());
////		File srcDir = getTestSrcDir();
////		File destDir = testingdir.getDir();
////		String xmlPath = "org/ebayopensource/turmeric/test/tools/codegen/data/TestService1.xml";
////		
////		// Copy resources into testing dirs
////		File xmlActual = TestResourceUtil.copyResource(xmlPath, testingdir, "meta-src");
////		
////		// Adjust path entries in XML 
////		Map<String, String> ns = new HashMap<String,String>();
////		ns.put("c", "http://www.ebay.com/soaframework/tools/codegen/common");
////		Map<String, String> entries = new HashMap<String, String>();
////		entries.put("//c:service-code-gen/c:tool-input-info/c:src-location", srcDir.getAbsolutePath());
////		entries.put("//c:service-code-gen/c:tool-input-info/c:dest-location", destDir.getAbsolutePath());
////		XmlAdjuster.correct(xmlActual, ns, entries);
////
////		// @formatter:off
////		String args[] = {
////			"-xml", xmlActual.getAbsolutePath() 
////		};
////		// @formatter:on
////		
////		performDirectCodeGen(args);
////	}
////
////	@Test
////	public void serviceGeneratorXml2() throws Exception {
////		MavenTestingUtils.ensureEmpty(testingdir.getDir());
////		File srcDir = getTestSrcDir();
////		File destDir = testingdir.getDir();
////		String xmlPath = "org/ebayopensource/turmeric/test/tools/codegen/data/TestService2.xml";
////		
////		// Copy resources into testing dirs
////		File xmlActual = TestResourceUtil.copyResource(xmlPath, testingdir, "meta-src");
////		
////		// Adjust path entries in XML 
////		Map<String, String> ns = new HashMap<String,String>();
////		ns.put("c", "http://www.ebay.com/soaframework/tools/codegen/common");
////		Map<String, String> entries = new HashMap<String, String>();
////		entries.put("//c:service-code-gen/c:tool-input-info/c:src-location", srcDir.getAbsolutePath());
////		entries.put("//c:service-code-gen/c:tool-input-info/c:dest-location", destDir.getAbsolutePath());
////		XmlAdjuster.correct(xmlActual, ns, entries);
////
////		// @formatter:off
////		String args[] = {
////			"-xml", xmlActual.getAbsolutePath() 
////		};
////		// @formatter:on
////		
////		performDirectCodeGen(args);
////	}
////
////	@Test
////	public void serviceGeneratorXml3() throws Exception {
////		MavenTestingUtils.ensureEmpty(testingdir.getDir());
////		File srcDir = getTestSrcDir();
////		File destDir = testingdir.getDir();
////		String xmlPath = "org/ebayopensource/turmeric/test/tools/codegen/data/TestService3.xml";
////		
////		// Copy resources into testing dirs
////		File xmlActual = TestResourceUtil.copyResource(xmlPath, testingdir, "meta-src");
////		
////		// Adjust path entries in XML 
////		Map<String, String> ns = new HashMap<String,String>();
////		ns.put("c", "http://www.ebay.com/soaframework/tools/codegen/common");
////		Map<String, String> entries = new HashMap<String, String>();
////		entries.put("//c:service-code-gen/c:tool-input-info/c:src-location", srcDir.getAbsolutePath());
////		entries.put("//c:service-code-gen/c:tool-input-info/c:dest-location", destDir.getAbsolutePath());
////		XmlAdjuster.correct(xmlActual, ns, entries);
////		
////		// @formatter:off
////		String args[] = {
////			"-xml", xmlActual.getAbsolutePath() 
////		};
////		// @formatter:on
////		
////		performDirectCodeGen(args);
////	}
//}
