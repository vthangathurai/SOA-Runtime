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

import org.ebayopensource.turmeric.tools.GeneratedAssert;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException;
import org.junit.Assert;
import org.junit.Test;


public class ServiceGeneratorConfigTest extends AbstractServiceGeneratorTestCase {
	
	@Test
	public void createClientConfigwithnewstructure() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		
		// @formatter:off
		String args[] = {
			"-servicename", "ClientConfig",
			"-interface", "NotRequired",
			"-gentype", "ClientConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebay.com/soa/MyGlobalConfig",
			"-cn", "MyClient1",
			"-environment","Myenv"};
		// @formatter:on
		
		performDirectCodeGen(args);
		
		GeneratedAssert.assertFileExists(destDir, "gen-meta-src/META-INF/soa/client/config/MyClient1/Myenv/ClientConfig/ClientConfig.xml");
	}

	@Test
	public void createGlobalClientConfig1() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();

		// @formatter:off
		String args[] = {
			"-servicename", "MyGlobalConfig",
			"-interface", "NotRequired",
			"-gentype", "GlobalClientConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebay.com/soa/MyGlobalConfig",
			"-cn", "MyClient1" 
		};
		// @formatter:on
		
		performDirectCodeGen(args);
	}

	@Test
	public void createGlobalClientConfig2() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();

		// @formatter:off
		String args[] = {
			"-servicename", "MyGlobalConfig",
			"-interface", "NotRequired",
			"-gentype", "GlobalClientConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebay.com/soa/MyGlobalConfig",
			"-ccgn", "MyClientGroup",
			"-cn", 	"MyClient2" 
		};
		// @formatter:on
		
		performDirectCodeGen(args);
	}

	@Test
	public void createGlobalServerConfig1() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "MyGlobalConfig1",
			"-interface", "NotRequired",
			"-gentype", "GlobalServerConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebay.com/soa/MyGlobalConfig" 
		};
		// @formatter:on
		
		performDirectCodeGen(args);
	}

	@Test
	public void createGlobalServerConfig2() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();

		// @formatter:off
		String args[] = {
			"-servicename", "MyGlobalConfig2",
			"-interface", "NotRequired",
			"-gentype", "GlobalServerConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebay.com/soa/MyGlobalConfig",
			"-scgn", "MyServiceGroup" 
		};
		// @formatter:on
		
		performDirectCodeGen(args);
	}

	@Test
	public void createGlobalServerConfigForASLInput() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File asl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/util/service_layers.txt");

		// Setup arguments
		// @formatter:off
		String testArgs[] = {
			"-servicename", "MyGlobalConfig1",
			"-interface", "NotRequired",
			"-gentype", "GlobalServerConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-asl", asl.getAbsolutePath(),
			"-namespace", "http://www.ebay.com/soa/MyGlobalConfig" 
		};
		// @formatter:on
		
		// Execute
		performDirectCodeGen(testArgs);
	}


	@SuppressWarnings("unchecked")
	@Test
	public void createGlobalServerConfigForAslInvalidSlayer() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File asl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/util/service_layers.txt");

		// @formatter:off
		String args[] = {
			"-servicename", "MyGlobalConfig1",
			"-interface", "NotRequired",
			"-gentype", "GlobalServerConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-asl", asl.getAbsolutePath(),
			"-slayer", "my-paypal",
			"-namespace", "http://www.ebay.com/soa/MyGlobalConfig" 
		};
		// @formatter:on
		
		try {
			performDirectCodeGen(args);
			Assert.fail("Should have thrown a " + BadInputOptionException.class.getName());
		} catch(BadInputOptionException e){
			Assert.assertThat(e.getMessage(), allOf( 
					containsString("Invalid service layer"),
					containsString("my-paypal")));
		} 
	}


	@Test
	public void createGlobalServerConfigForAslValidSlayer() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File asl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/util/service_layers.txt");

		// Setup arguments
		// @formatter:off
		String testArgs[] =  new String[] {
			"-servicename", "MyGlobalConfig1",
			"-interface", "NotRequired",
			"-gentype", "GlobalServerConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-asl", asl.getAbsolutePath(),
			"-slayer", "BUSINESS",
			"-namespace", "http://www.ebay.com/soa/MyGlobalConfig" 
		};
		// @formatter:on

		// Execute
		performDirectCodeGen(testArgs);
	}

	@Test
	public void createGlobalServerConfigForNoASLInput() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();

		// @formatter:off
		String args[] = {
			"-servicename", "MyGlobalConfig1",
			"-interface", "NotRequired",
			"-gentype", "GlobalServerConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-namespace", "http://www.ebay.com/soa/MyGlobalConfig" 
		};
		// @formatter:on

		performDirectCodeGen(args);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void createGlobalServerConfigForNoAslInValidSlayer() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();

		// @formatter:off
		String args[] = {
			"-servicename", "MyGlobalConfig1",
			"-interface", "NotRequired",
			"-gentype", "GlobalServerConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-slayer","BUSINESS_OWN" ,
			"-namespace", "http://www.ebay.com/soa/MyGlobalConfig" 
		};
		// @formatter:on
		
		try {
			performDirectCodeGen(args);
			Assert.fail("Should have thrown a " + BadInputOptionException.class.getName());
		} catch(BadInputOptionException e){
			Assert.assertThat(e.getMessage(), allOf( 
					containsString("Invalid service layer"),
					containsString("BUSINESS_OWN")));
		} 
	}

	@Test
	public void createGlobalServerConfigForNoAslValidSlayer() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();

		// @formatter:off
		String args[] = {
			"-servicename", "MyGlobalConfig1",
			"-interface", "NotRequired",
			"-gentype", "GlobalServerConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-slayer","BUSINESS" ,
			"-namespace", "http://www.ebay.com/soa/MyGlobalConfig" 
		};
		// @formatter:on
		
		performDirectCodeGen(args);
	}


	@Test
	public void createGlobalServerConfigForWrongASLInput() throws Exception {

		// Initialize testing paths
		testingdir.ensureEmpty();
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		
		File asl = new File(TestResourceUtil.getDir(), "service_layers.txt");
		Assert.assertFalse("asl should not exist: " + asl.getAbsolutePath(), asl.exists());

		// @formatter:off
		String args[] = {
			"-servicename", "MyGlobalConfig1",
			"-interface", "NotRequired",
			"-gentype", "GlobalServerConfig",
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-asl", asl.getAbsolutePath(),
			"-namespace", "http://www.ebay.com/soa/MyGlobalConfig" 
		};
		// @formatter:on
		
		try {
			performDirectCodeGen(args);
			Assert.fail("Should have thrown a " + BadInputOptionException.class.getName());
		} catch(BadInputOptionException e){
			Assert.assertThat(e.getMessage(), containsString("File doesn't exist at location"));
		} 
	}


}
