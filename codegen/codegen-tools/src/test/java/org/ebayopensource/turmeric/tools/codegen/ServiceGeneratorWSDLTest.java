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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.XmlAdjuster;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.junit.Assert;
import org.junit.Test;


public class ServiceGeneratorWSDLTest extends AbstractServiceGeneratorTestCase {
	
	@Test
	public void accountServiceFaultTagWSDL() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/AccountService-New.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "AccountService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", SOAConstants.DEFAULT_SERVICE_NAMESPACE,
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.service",
			"-bin", binDir.getAbsolutePath(),
			"-cn", "AccountService",
			"-icsi", "-gin", "AccountService" 
		};
		// @formatter:on

		performDirectCodeGen(args, binDir);
	}

	@Test
	public void serviceGeneratorWSDL1() throws Exception {
		// Initialize testing paths
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/CalcService.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = testingdir.getFile("tmp");
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "CalcService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.org.ebayopensource/soaframework/service/calc",
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.tools.codegen",
			"-bin", binDir.getAbsolutePath(),
			"-cn", "CalcService",
			"-icsi", 
			"-gin", "CalculatorSvcIntf" 
		};
		// @formatter:on

		performDirectCodeGen(args);
	}

//	@Test
//	public void serviceGeneratorWSDL2() throws Exception {
//		// Initialize testing paths
//		MavenTestingUtils.ensureEmpty(testingdir.getDir());
//		File srcDir = getTestSrcDir();
//		File destDir = testingdir.getDir();
//		File binDir = testingdir.getFile("bin");
//		String xmlPath = "org/ebayopensource/turmeric/test/tools/codegen/data/TestService4.xml";
//
//		// Copy resources into testing dirs
//		File xmlActual = TestResourceUtil.copyResource(xmlPath, testingdir, "meta-src");
//		File wsdl = TestResourceUtil.copyResource("org/ebayopensource/turmeric/test/tools/codegen/data/StockQuote.wsdl", testingdir, "meta-src");
//
//		// Adjust path entries in XML 
//		Map<String, String> ns = new HashMap<String,String>();
//		ns.put("c", "http://www.ebayopensource.org/turmeric/runtime/codegen/common");
//		Map<String, String> entries = new HashMap<String, String>();
//		entries.put("//c:service-code-gen/c:interface-info/c:wsdl-def/c:wsdl-file", wsdl.getAbsolutePath());
//		entries.put("//c:service-code-gen/c:tool-input-info/c:src-location", srcDir.getAbsolutePath());
//		entries.put("//c:service-code-gen/c:tool-input-info/c:dest-location", destDir.getAbsolutePath());
//		entries.put("//c:service-code-gen/c:tool-input-info/c:bin-location", binDir.getAbsolutePath());
//		XmlAdjuster.correct(xmlActual, ns, entries);
//
//		// Setup arguments
//		// @formatter:off
//		String testArgs[] =  new String[] {
//			"-xml", xmlActual.getAbsolutePath(),
//			"-namespace","http://stock.app.org.ebayopensource"
//		};
//		// @formatter:on
//
//		// Execute
//		performDirectCodeGen(testArgs);
//	}


	@Test
	public void serviceGeneratorWSDL3() throws Exception {
		// Initialize testing paths
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/CalcService.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String testArgs[] =  new String[] {
			"-servicename", "MyCalcService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "TypeMappings",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.org.ebayopensource/soaframework/service/calc",
			"-gip", "org.ebayopensource.test.soaframework.services.calc",
			"-bin", binDir.getAbsolutePath() 
		};
		// @formatter:on

		// Execute
		performDirectCodeGen(testArgs);
	}

	@Test
	public void serviceGeneratorWSDL3WOInftPkgWONS() throws Exception {
		// Initialize testing paths
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/CalcService.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String testArgs[] =  new String[] {
			"-servicename", "CalculatorService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-bin", binDir.getAbsolutePath() 
		};
		// @formatter:on

		// Execute
		performDirectCodeGen(testArgs);
	}

	@Test
	public void serviceGeneratorWSDL4() throws Exception {
		// Initialize testing paths
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/REService.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		
		// Setup arguments
		// @formatter:off
		String testArgs[] =  new String[] {
			"-servicename", "REService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype",  "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.org.ebayopensource/soaframework/service/ratingengine",
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.tools.codegen",
			"-bin", binDir.getAbsolutePath(),
			"-cn", "REService", "-icsi" 
		};
		// @formatter:on
		
		// Execute
		performDirectCodeGen(testArgs);
	}

//	@Test
//	public void serviceGeneratorWSDL5() throws Exception {
//		// Initialize testing paths
//		MavenTestingUtils.ensureEmpty(testingdir.getDir());
//		File destDir = testingdir.getDir();
//		File binDir = testingdir.getFile("bin");
//		String xmlPath = "org/ebayopensource/turmeric/test/tools/codegen/data/TestService5.xml";
//		
//		// Copy resources into testing dirs
//		File xmlActual = TestResourceUtil.copyResource(xmlPath, testingdir, "meta-src");
//		File wsdl = TestResourceUtil.copyResource("org/ebayopensource/turmeric/test/tools/codegen/data/ShoppingService.wsdl", testingdir, "meta-src");
//		
//		// Adjust path entries in XML 
//		Map<String, String> ns = new HashMap<String,String>();
//		ns.put("c", "http://www.ebayopensource.org/turmeric/runtime/codegen/common");
//		Map<String, String> entries = new HashMap<String, String>();
//		entries.put("//c:service-code-gen/c:interface-info/c:wsdl-def/c:wsdl-file", wsdl.getAbsolutePath());
//		entries.put("//c:service-code-gen/c:tool-input-info/c:dest-location", destDir.getAbsolutePath());
//		entries.put("//c:service-code-gen/c:tool-input-info/c:bin-location", binDir.getAbsolutePath());
//		XmlAdjuster.correct(xmlActual, ns, entries);
//		
//		// Setup arguments
//		// @formatter:off
//		String testArgs[] =  new String[] {
//			"-xml", xmlActual.getAbsolutePath(),
//			"-namespace", "urn:ebay:apis:eBLBaseComponents"
//		};
//		// @formatter:on
//
//		// Execute
//		performDirectCodeGen(testArgs);
//	}

	@Test
	public void serviceGeneratorWSDLAcctSvc() throws Exception {
		// Initialize testing paths
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/AccountService.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = testingdir.getFile("tmp");
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] = {
			"-servicename", "AcctSvc",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.org.ebayopensource/soaframework/service/acct",
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.service",
			"-bin", binDir.getAbsolutePath(),
			"-cn", "AcctService",
			"-icsi", 
			"-gin", "AcctSvcIntf" 
		};
		// @formatter:on

		performDirectCodeGen(args, binDir);
	}

	@Test
	public void serviceGeneratorWSDLServiceWithSoapHeaders() throws Exception {
		// Initialize testing paths
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/util/TrackingApi.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String testArgs[] = {
			"-servicename", "TrackingApi",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.org.ebayopensource/soaframework/service/trackingApi",
			"-gip", "org.ebayopensource.test.soaframework.services.trackingApi",
			"-bin", binDir.getAbsolutePath() 
		};
		// @formatter:on
		
		// Execute
		performDirectCodeGen(testArgs);
	}


	@Test
	public void serviceGeneratorWSDLServiceWithSoapHeaders2() throws Exception {
		// Initialize testing paths
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/util/TrackingApi2.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String testArgs[] =  new String[] {
			"-servicename", "TrackingApi2",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.org.ebayopensource/soaframework/service/trackingApi2",
			"-gip", "org.ebayopensource.test.soaframework.services.trackingApi2",
			"-bin", binDir.getAbsolutePath() 
		};
		// @formatter:on
		
		// Execute
		performDirectCodeGen(testArgs);
	}

	@Test
	public void serviceGeneratorWSDLWithoutWSDLValueWithMDestinationNull() throws Exception {
		// Initialize testing paths
		File srcDir = getTestSrcDir();
		File destDir = testingdir.getDir(); // Simulates passing "."
		File binDir = testingdir.getFile("bin");

		MavenTestingUtils.ensureEmpty(testingdir.getFile("meta-src"));
		TestResourceUtil.copyResource("META-INF/soa/services/wsdl/CalcService/CalcService.wsdl", testingdir, "meta-src");

		// Setup arguments
		// @formatter:off
		String testArgs[] = {
			"-servicename", "CalcService",
			"-wsdl", /* null */
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.org.ebayopensource/soaframework/service/calc",
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.tools.codegen",
			"-bin", binDir.getAbsolutePath(),
			"-cn", "CalcService",
			"-icsi", "-gin", "CalculatorSvcIntf",
			"-mdest", ""
		};
		// @formatter:on
		
		// Execute
		performDirectCodeGen(testArgs);
	}

	@Test
	public void serviceGeneratorWSDLWithoutWSDLWithMDestination() throws Exception {
		// Initialize testing paths
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File mdestDir = testingdir.getFile("meta-src");
		File binDir = testingdir.getFile("bin");
		
		MavenTestingUtils.ensureEmpty(mdestDir);
		TestResourceUtil.copyResource("META-INF/soa/services/wsdl/CalcService/CalcService.wsdl", testingdir, "meta-src");
		
		// Setup arguments
		// @formatter:off
		String testArgs[] =  new String[] {
			"-servicename", "CalcService",
			"-wsdl",
			"-gentype", "ClientNoConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-mdest",  mdestDir.getAbsolutePath(),
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.tools.codegen",
			"-bin", binDir.getAbsolutePath(),
			"-cn", "CalcService",
			"-icsi", 
			"-gin", "CalculatorSvcIntf" 
		};
		// @formatter:on

		// Execute
		performDirectCodeGen(testArgs);
	}

	@Test
	public void serviceGeneratorWSDLWithoutWSDLWithoutMDestination() throws Exception {
		// Initialize testing paths
		File srcDir = getTestSrcDir();
		File destDir = testingdir.getDir(); // Simulates passing "."
		File mdestDir = testingdir.getFile("meta-src");
		File binDir = testingdir.getFile("bin");

		MavenTestingUtils.ensureEmpty(mdestDir);
		TestResourceUtil.copyResource("META-INF/soa/services/wsdl/CalcService/CalcService.wsdl", testingdir, "meta-src");

		// Setup arguments
		// @formatter:off
		String testArgs[] =  new String[] {
			"-servicename", "CalcService",
			"-wsdl",
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.org.ebayopensource/soaframework/service/calc",
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.tools.codegen",
			"-bin", binDir.getAbsolutePath(),
			"-cn", "CalcService",
			"-icsi", "-gin", "CalculatorSvcIntf" 
		};
		// @formatter:on
		
		// Execute
		performDirectCodeGen(testArgs);
	}

	@Test
	public void testDefaultingInputTypeWSDL() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		File rootDir = testingdir.getDir();
		String wsdlToBeCopied = "org/ebayopensource/turmeric/test/tools/codegen/data/CalcService.wsdl";
		
		File wsdl = TestResourceUtil.copyResource(wsdlToBeCopied, testingdir, "meta-src");
		
		// generate the service_metadata.properties
		// @formatter:off
		String args1[] = { // this is a WSDL based service
			"-servicename",	"MyCalcService9031",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "ServiceMetadataProps",
			"-pr",rootDir.getAbsolutePath(),
			"-scv","1.2.0",
			"-slayer","COMMON"
		}; 
		// @formatter:on
		
		performDirectCodeGen(args1);
		
		//Copying the WSDL to the required folder where codegen will pick it
		File destBaseDir = testingdir.getFile(FilenameUtils
				.separatorsToSystem("meta-src/META-INF/soa/services/wsdl/MyCalcService9031/MyCalcService9031.wsdl"));
		TestResourceUtil.copyResource(wsdlToBeCopied, destBaseDir);
		
		// generate all the other artifacts
		// @formatter:off
		String args2[] = { // not providing the inputType, the code should default to WSDL based service as SMP would contain wsdluri
			"-servicename",	"MyCalcService9031",
			"-gentype", "All",
			"-pr",rootDir.getAbsolutePath(),
			"-dest",destDir.getAbsolutePath(),
			"-bin",binDir.getAbsolutePath()
		}; 
		// @formatter:on

		performDirectCodeGen(args2);
	}

	@Test
	public void testDefaultingInputTypeWSDLThruServIntfPropsFile() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		File rootDir = testingdir.getDir();
		
		TestResourceUtil.copyResource("META-INF/soa/services/wsdl/MyCalcService15001/MyCalcService15001.wsdl", testingdir, "meta-src");

		// generate the service_metadata.properties
		// @formatter:off
		String args1[] = new String[] { // this is a WSDL based service
			"-servicename",	"MyCalcService15001",
			"-wsdl", /* null */
			"-gentype", "ServiceMetadataProps",
			"-pr",rootDir.getAbsolutePath(),
			"-scv","1.2.0",
			"-slayer","COMMON"
		}; 
		// @formatter:off
		
		performDirectCodeGen(args1);
		
		// generate the service_intf_project.properties file
		// @formatter:off
		String args2[] = new String[] {
			"-servicename",	"MyCalcService15001",
			"-wsdl", /* null */
			"-gentype","ServiceIntfProjectProps",
			"-sl","www.amazon.com:9089/getAllTracking",
			"-pr", rootDir.getAbsolutePath()
		}; 
		// @formatter:on
		
		performDirectCodeGen(args2);

		// generate all the other artifacts
		// @formatter:off
		String args3[] = new String[] { // not providing the inputType, the code should default to WSDL based service
			"-servicename",	"MyCalcService15001",
			"-gentype", "All",
			"-pr", rootDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-bin", binDir.getAbsolutePath()
		}; 
		// @formatter:on

		performDirectCodeGen(args3);
	}
	
	@Test
	public void testWSDLServiceForMetadata() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/CalcService.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		File rootDir = testingdir.getDir(); // was "./tmp/myPrFolder403"

		// Setup arguments
		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "MyCalcService403",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "ServiceMetadataProps",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-gip", "org.ebayopensource.test.soaframework.services.calc",
			"-gin","MyCalInterface",
			"-slayer","COMMON",
			"-bin", binDir.getAbsolutePath() ,
			"-pr", rootDir.getAbsolutePath()
		};
		// @formatter:on
		
		// Execute
		performDirectCodeGen(args);
	}

	@Test
	public void testWSDLServiceForMetadataAllArtifacts() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/CalcService.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		File rootDir = testingdir.getDir(); // was "./tmp/myPrFolder404"

		// Setup arguments
		// @formatter:off
		String args1[] = {
			"-servicename", "MyCalcServiceAll404",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "ServiceMetadataProps",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-gip", "org.ebayopensource.test.soaframework.services.calc",
			"-gin","MyCalInterfaceAll",
			"-slayer","COMMON",
			"-bin", binDir.getAbsolutePath(),
			"-pr", rootDir.getAbsolutePath()
		};
		// @formatter:on

		// Execute
		performDirectCodeGen(args1);

		// Setup arguments
		// @formatter:off
		String args2[] =  new String[] {
			"-servicename", "MyCalcServiceAll404",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-bin", binDir.getAbsolutePath(),
			"-pr", rootDir.getAbsolutePath()
		};
		// @formatter:on
		
		// Execute
		performDirectCodeGen(args2);
	}

	@Test
	public void testWSDLServiceForSvcIntfProp() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/CalcService.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		File rootDir = testingdir.getDir(); // was "./tmp/myPrFolder9001"

		// generate the service_metadata.properties
		// @formatter:off
		String args1[] = new String[] {
			"-servicename",	"MyCalcService9001",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "ServiceMetadataProps",
			"-gip","org.ebayopensource.testing.properties.testcases",
			"-gin","UserGivenNameForMyCalcService9001",
			"-scv","1.0.1",
			"-slayer","COMMON",
			"-pr",rootDir.getAbsolutePath() 
		};
		// @formatter:on

		performDirectCodeGen(args1);
		
		// generate the service_intf_project.properties
		// @formatter:off
		String args2[] = new String[] {
			"-servicename",	"MyCalcService9001",
			"-wsdl",
			"-gentype","ServiceIntfProjectProps",
			"-sl","www.amazon.com:9089/getAllTracking",
			"-pr", rootDir.getAbsolutePath(),
			"-ctns","www.org.ebayopensource/test/ctns/option"
		}; 
		// @formatter:on
		
		performDirectCodeGen(args2);
		
		// generate all the other artifacts
		// @formatter:off
		String args3[] = new String[] {
			"-servicename",	"MyCalcService9001",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest",destDir.getAbsolutePath(),
			"-bin", binDir.getAbsolutePath(),
			"-pr", rootDir.getAbsolutePath() 
		}; 
		// @formatter:on
		
		performDirectCodeGen(args3);
	}

	@Test
	public void testWSDLwithCyclicDependency() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/PayPalAPIInterfaceService.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "PayPalAPIInterfaceService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.0.0",
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on
		
		// Execute
		performDirectCodeGen(args);
	}

	@Test
	public void testWSDLwithMultiNamespaceOption() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File wsdl = getCodegenDataFileInput("Testing.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = testingdir.getFile("tmp");
		File binDir = testingdir.getFile("bin");
		
		// @formatter:off
		String args[] = {
			"-servicename", "TestService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "WsdlConversionToMns",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.0.0",
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on
		
		performDirectCodeGen(args);
	}

	@Test
	public void testWSDLwithMultiNamespaceoptionsInvalidCase() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File wsdl = getCodegenDataFileInput("WsdlWithInvalidSourceTag.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = testingdir.getFile("tmp");
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] = {
			"-servicename", "TestService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "WsdlConversionToMns",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.0.0",
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on

		try {
			performDirectCodeGen(args);
			Assert.fail("Expected exception of type: " + CodeGenFailedException.class.getName());
		} catch (CodeGenFailedException ex) {
			Assert.assertThat(ex.getMessage(), containsString("Attributes for the source Tag are Invalid"));
		}
	}
	
	@Test
	public void testWSDLwithNamespaceNameHavingclass() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File wsdl = getCodegenDataFileInput("TestData.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = testingdir.getFile("tmp");
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] = {
			"-servicename", "ClassifedAdSearchService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.0.0",
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on
		
		performDirectCodeGen(args);
	}

	@Test
	public void trackerWSDL() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/Tracker-extschema.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "Tracker2",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://soa.ebayopensource.org/Tracker/",
			"-scv", "1.0.0",
			"-gip", "org.ebayopensource.test.soaframework.service",
			"-bin", binDir.getAbsolutePath(),
			"-cn", "Tracker2",
			"-icsi", "-gin", "Tracker2" 
		};
		// @formatter:on

		performDirectCodeGen(args);
	}
	
}
