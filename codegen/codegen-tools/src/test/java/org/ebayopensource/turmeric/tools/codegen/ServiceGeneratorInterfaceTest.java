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

import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.GeneratedAssert;
import org.ebayopensource.turmeric.tools.codegen.exception.PreValidationFailedException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class ServiceGeneratorInterfaceTest extends AbstractServiceGeneratorTestCase {
	@Test
	public void generatedInterfaceForNoGipAndNoGin() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "ComplexService_1001",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.InterfaceWithMultipleParameters",
			"-gentype", "All",
			"-dest", destDir.getAbsolutePath(),
			"-src", srcDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soaframework/service/ComplexService",
			"-scv", "1.0.0" ,
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on

		performDirectCodeGen(args, binDir);
	}

	public void generatedInterfaceForGipAndNoGin() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "ComplexService_1002",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.InterfaceWithMultipleParameters",
			"-gentype", "All",
			"-gip","org.ebayopensource.test.newpath.newpackage",
			"-dest", destDir.getAbsolutePath(),
			"-src", srcDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soaframework/service/ComplexService",
			"-scv", "1.0.0",
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on

		performDirectCodeGen(args, binDir);
	}

	@Test
	public void generatedInterfaceForGinAndNoGip() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "ComplexService_1003",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.InterfaceWithMultipleParameters",
			"-gentype", "All",
			"-gin","OwnInterface_1003",
			"-dest", destDir.getAbsolutePath(),
			"-src", srcDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soaframework/service/ComplexService",
			"-scv", "1.0.0",
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on

		performDirectCodeGen(args, binDir);
	}

	@Test
	public void generatedInterfaceForGipAndGin() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "ComplexService_1004",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.InterfaceWithMultipleParameters",
			"-gentype", "All",
			"-gip","org.ebayopensource.test.newpath.newpackage",
			"-gin","OwnInterface_1004",
			"-dest", destDir.getAbsolutePath(),
			"-src", srcDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soaframework/service/ComplexService",
			"-scv", "1.0.0" ,
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on

		performDirectCodeGen(args, binDir);
	}

	@Test
	public void serviceGeneratorInterface1() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		String serviceName = "SimpleService";
		
		// @formatter:off
		String args[] =  new String[] {
			"-servicename", serviceName,
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soa/sampleservices/",
			"-scv", "1.0.0",
			"-cn", "SimpleService",
			"-bin", binDir.getAbsolutePath(),
			"-pkg2ns", "org.ebayopensource.test.soaframework.tools.codegen=http://www.ebayopensource.org/soa/sampleservices/"
		};
		// @formatter:on

		performDirectCodeGen(args);
		
		GeneratedAssert.assertFileExists(destDir, ("gen-meta-src/META-INF/soa/common/config/"
        + serviceName + "/service_metadata.properties"));
		GeneratedAssert.assertPathNotExists(destDir, ("meta-src/META-INF/soa/common/config/"
        + serviceName + "/service_metadata.properties"));
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void serviceGeneratorInterface1MetadataPropMigrated() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir(); 
		File binDir = testingdir.getFile("bin");

		String serviceName = "SimpleService";

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", serviceName,
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soa/sampleservices/",
			"-scv", "1.0.0",
			"-cn", "SimpleService",
			"-bin", binDir.getAbsolutePath(),
			"-pkg2ns", "org.ebayopensource.test.soaframework.tools.codegen=http://www.ebayopensource.org/soa/sampleservices/"
		};
		// @formatter:on

		performDirectCodeGen(args);

		GeneratedAssert.assertFileExists(destDir, ("gen-meta-src/META-INF/soa/common/config/"
        + serviceName + "/service_metadata.properties"));
		GeneratedAssert.assertPathNotExists(destDir, ("meta-src/META-INF/soa/common/config/"
        + serviceName + "/service_metadata.properties"));
	}


	@Test
	public void serviceGeneratorInterface2() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File jdestDir = testingdir.getFile("gen-src");
		File mdestDir = testingdir.getFile("gen-meta-src");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "ItemService",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.ItemServiceInterface",
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soa/ItemService",
			"-scv", "1.0.0",
			"-cn", "ItemService",
			"-sl", "http://ebayopensource.org/soaframework/services/ItemService",
			"-wl", "http://ebayopensource.org/soaframework/services/ItemService?wsdl",
			"-jdest", jdestDir.getAbsolutePath(),
			"-mdest", mdestDir.getAbsolutePath(),
			"-icsi", "-nc", "-ce"
		};
		// @formatter:on

		performDirectCodeGen(args);
	}

	@Test
	public void serviceGeneratorInterface3() throws Exception {
		assumeTest1ServicePresent();
		File srcDir = getTestSrcDir();
		File destDir = testingdir.getFile("tmp");
		
		// @formatter:off
		String args[] = {
			"-servicename", "test1gen",
			"-interface", "org.ebayopensource.test.soaframework.sample.services.message.Test1Service.class",
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace",  "http://www.ebayopensource.org/turmeric/common/config",
			"-ccgn", "SOAWebService",
			"-scgn", "SOAWebService",
			"-scv", "1.0.0",
			"-cn", "Test1Gen",
			"-slayer", "BUSINESS"
		};
		// @formatter:on
		
		performDirectCodeGen(args);
	}

	/**
	 * Using junit's {@link Assume} class to skip tests that rely 
	 * on Test1Service if Test1Service is unavailable for some reason.
	 * (Not the tests fault)
	 * 
	 * We should move these tests out to SOATests (the integration tests)
	 * as that's a more appropriate place for this kind of test.
	 */
	public static void assumeTest1ServicePresent() {
		try {
			Class<?> clazz = Class.forName("org.ebayopensource.test.soaframework.sample.services.message.Test1Service",
					false, Thread.currentThread().getContextClassLoader());
			Assume.assumeNotNull(clazz);
		} catch (Throwable t) {
			Assume.assumeNoException(t);
		}
	}

	@Test
	public void serviceGeneratorInterface4() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "MathService",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.MathServiceInterface",
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/turmeric/common/config",
			"-sicn", "org.ebayopensource.turmeric.tools.codegen.MathServiceImpl",
			"-scv", "1.0.0",
			"-cn", "MathServiceClient",
			"-bin", binDir.getAbsolutePath(),
			"-dontprompt", "-op2cemc",
			"all=org.ebayopensource.turmeric.tools.codegen.CustomErrorMessage"
		};
		// @formatter:on

		performDirectCodeGen(args, binDir);
	}

	@Test
	public void serviceGeneratorInterface5() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "ComplexService",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.ComplexServiceInterface",
			"-gentype", "All",
			"-dest", destDir.getAbsolutePath(),
			"-src", srcDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soaframework/service/ComplexService",
			"-cn", "ComplexServiceClient",
			"-scv", "1.0.0",
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on

		performDirectCodeGen(args, binDir);
	}

	@Test
	public void serviceGeneratorInterface6() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String clientArgs[] =  new String[] {
			"-servicename", "Simple1Service",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype", "Client",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soa/sampleservices/",
			"-scv", "1.0.0",
			"-cn", "SimpleService",
			"-bin", binDir.getAbsolutePath(),
			"-pkg2ns", "org.ebayopensource.test.soaframework.tools.codegen=http://www.ebayopensource.org/soa/sampleservices/"
		};
		// @formatter:on

		// @formatter:off
		String serverArgs[] =  new String[] {
			"-servicename", "Simple1Service",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype", "Server",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soa/sampleservices/",
			"-scv", "1.0.0",
			"-cn", "SimpleService",
			"-bin", binDir.getAbsolutePath(), "-gt",
			"-pkg2ns", "org.ebayopensource.test.soaframework.tools.codegen=http://www.ebayopensource.org/soa/sampleservices/"
		};
		// @formatter:on

		performDirectCodeGen(clientArgs);
		performDirectCodeGen(serverArgs);
	}

	@Test
	public void serviceGeneratorInterface7() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String clientArgs[] =  new String[] {
			"-servicename", "Simple2Service",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype", "Client",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soa/sampleservices/",
			"-scv", "1.0.0",
			"-cn", "SimpleService",
			"-bin", binDir.getAbsolutePath(),
			"-pkg2ns", "org.ebayopensource.test.soaframework.tools.codegen=http://www.ebayopensource.org/soa/sampleservices/"
		};
		// @formatter:on

		// @formatter:off
		String serverArgs[] =  new String[] {
			"-servicename", "Simple2Service",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype", "ServerNoConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soa/sampleservices/",
			"-scv", "1.0.0",
			"-cn", "SimpleService",
			"-bin", binDir.getAbsolutePath(), "-gt",
			"-pkg2ns", "org.ebayopensource.test.soaframework.tools.codegen=http://www.ebayopensource.org/soa/sampleservices/",
			"-sicn", "org.ebayopensource.test.soaframework.tools.codegen.impl.SimpleServiceImpl"
		};
		// @formatter:on

		performDirectCodeGen(clientArgs);
		performDirectCodeGen(serverArgs);
	}

	@Test
	public void serviceGeneratorInterface8() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "Simple3Service",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype", "Client",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soa/sampleservices/",
			"-scv", "1.0.0",
			"-cn", "SimpleService",
			"-bin", binDir.getAbsolutePath(), "-gt",
			"-pkg2ns", "org.ebayopensource.test.soaframework.tools.codegen=http://www.ebayopensource.org/soa/sampleservices/"
		};
		// @formatter:on

		performDirectCodeGen(args);
	}

	@Test
	public void serviceGeneratorInterface9() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "Item1Service",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.ItemServiceInterface",
			"-gentype", "ClientNoConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soa/sampleservices/",
			"-scv", "1.0.0",
			"-cn", "Item1Service",
			"-bin", binDir.getAbsolutePath(), "-gt",
			"-pkg2ns", "org.ebayopensource.test.soaframework.tools.codegen=http://www.ebayopensource.org/soa/itemservice/"
		};
		// @formatter:on

		performDirectCodeGen(args);
	}

	@Test
	public void serviceGeneratorInterface10() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "Simple5Service",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype", "ServiceOpProps",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soa/sampleservices/",
			"-scv", "1.0.0",
			"-cn", "SimpleService",
			"-bin", binDir.getAbsolutePath(), "-gt",
			"-pkg2ns", "org.ebayopensource.test.soaframework.tools.codegen=http://www.ebayopensource.org/soa/sampleservices/"
		};
		// @formatter:on

		performDirectCodeGen(args);
	}

	@Test
	public void serviceGeneratorInterface11() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir(); 
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "Simple6Service",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype", "SecurityPolicyConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebayopensource.org/soa/sampleservices/",
			"-scv", "1.0.0",
			"-cn", "SimpleService",
			"-bin", binDir.getAbsolutePath(), "-gt",
			"-pkg2ns", "org.ebayopensource.test.soaframework.tools.codegen=http://www.ebayopensource.org/soa/sampleservices/"
		};
		// @formatter:on

		performDirectCodeGen(args);
	}

	@Test
	public void interfaceServiceForSvcIntfProp() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		File rootDir = testingdir.getDir(); // was "./tmp/myPrFolder9011"

		//generate the service_metadata.properties
		// @formatter:off
		String args1[] = new String[] {
			"-servicename",	"MyCalcService9011",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype", "ServiceMetadataProps",
			"-pr",rootDir.getAbsolutePath(),
			"-scv","1.2.0",
			"-slayer","COMMON"
		};
		// @formatter:on
		
		performDirectCodeGen(args1);

		//generate the service_intf_project.properties
		// @formatter:off
		String args2[] = new String[] {
			"-servicename",	"MyCalcService9011",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype","ServiceIntfProjectProps",
			"-sl","http://amazon.com/getAllTracking",
			"-pr", rootDir.getAbsolutePath()
		};
		// @formatter:on
		
		performDirectCodeGen(args2);
		
		//generate all the other artifacts
		// @formatter:off
		String args3[] = new String[] {
			"-servicename",	"MyCalcService9011",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype", "All",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-bin", binDir.getAbsolutePath(),
			"-pr", rootDir.getAbsolutePath() 
		}; 
		// @formatter:on
		
		performDirectCodeGen(args3);
	}
	
	@Test
	public void interfaceServiceForMetadata() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		File rootDir = testingdir.getDir(); // was "./tmp/myPrFolder401"
		
		// Setup arguments
		// @formatter:off
		String testArgs[] =  new String[] {
			"-servicename", "SimpleService401",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.SimpleServiceInterface.java",
			"-gentype", "ServiceMetadataProps",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.2.3",
			"-slayer","COMMON",
			"-bin", binDir.getAbsolutePath(),
			"-pr", rootDir.getAbsolutePath()
		};
		// @formatter:on

		// Execute
		performDirectCodeGen(testArgs);
	}

	@Test
	public void interfaceServiceForMetadataAllArtifacts() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		File rootDir = testingdir.getDir(); // was "./tmp/myPrFolder402"

		// Setup arguments
		// @formatter:off
		String args1[] =  new String[] {
			"-servicename", "ItemService402",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.ItemServiceInterface",
			"-gentype", "ServiceMetadataProps",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "2.0.0",
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
				"-servicename", "ItemService402",
				"-interface", "org.ebayopensource.turmeric.tools.codegen.ItemServiceInterface",
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

	@SuppressWarnings("unchecked")
	@Test
	public void overloadedInterfaceServiceGen() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		
		StringBuilder srcPath = new StringBuilder();
		srcPath.append(testingdir.getFile("FunctionalTests").getAbsolutePath());
		srcPath.append(";"); // TODO: support File.pathSeparator
		srcPath.append(srcDir.getAbsolutePath());

		// Setup arguments
		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "MathService",
			"-interface", "org.ebayopensource.turmeric.tools.codegen.MathServiceOverloadedInterface",
			"-gentype", "All",
			"-src", srcPath.toString(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.0.0",
			"-bin", binDir.getAbsolutePath(),
		};
		// @formatter:on
		
		try {
			performDirectCodeGen(args);
			Assert.fail("Should have thrown a " + PreValidationFailedException.class.getName());
		} catch (PreValidationFailedException ex) {
			Assert.assertThat(ex.getMessage(), allOf( 
					containsString("Method Name : add"),
					containsString("Method overloading is not allowed") ));
		}
	}
}
