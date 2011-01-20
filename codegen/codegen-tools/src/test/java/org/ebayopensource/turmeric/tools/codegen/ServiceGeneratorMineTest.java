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
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.GeneratedAssert;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.junit.Assert;
import org.junit.Test;

public class ServiceGeneratorMineTest extends AbstractServiceGeneratorTestCase {
    @Test
    public void checkObjectFactoryClassGenerationCase3() throws Exception {
        // Initialize testing paths
        testingdir.ensureEmpty();
        File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/CalcService.wsdl");
        File destDir = testingdir.getDir();
        File binDir = testingdir.getFile("bin");
        File metaDir = testingdir.getFile("meta-src");
        
        MavenTestingUtils.ensureDirExists(binDir);
        MavenTestingUtils.ensureDirExists(metaDir);

        // @formatter:off
		String args[] = new String[] {
			"-servicename", "CalculatorService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-noObjectFactoryGeneration", "trueeeee",
			"-dest", destDir.getAbsolutePath(), 
			"-scv", "1.0.0", 
			"-bin", binDir.getAbsolutePath() 
		};
		// @formatter:on

        performDirectCodeGen(args, binDir);

        Assert.assertThat("Testing boolean parse logic", Boolean.parseBoolean("trueeee"), is(false));

        File genSrcDir = testingdir.getFile("gen-src");

        GeneratedAssert.assertJavaExists(genSrcDir, "org.ebayopensource.turmeric.common.v1.services.ObjectFactory");
    }

    @Test(expected = BadInputValueException.class)
    public void commonTypesNSInputOptionFailureCase_BadNS() throws Exception {
        // Initialize testing paths
        testingdir.ensureEmpty();
        File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/AccountService.wsdl");
        File destDir = getTestDestDir();
        File binDir = testingdir.getFile("bin");

        // @formatter:off
		String args[] =  new String[] {
			"-servicename", "AccountService",
			"-cn","AccountService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "TypeMappings",
			"-dest", destDir.getAbsolutePath(),
			"-bin", binDir.getAbsolutePath(),
			"-ctns", "/fi|e/abc"
		};
		// @formatter:on

        performDirectCodeGen(args);
    }

    @Test
    public void createServiceMetaDataPropsWithNewStructure() throws Exception {
        // Initialize testing paths
        testingdir.ensureEmpty();
        File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/Testing.wsdl");
        File rootDir = testingdir.getDir();

        // @formatter:off
		String[] args = {
			"-servicename", "MyServiceV1",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "ServiceMetadataProps",
			"-publicservicename","MyService",
			"-adminname","xyz",
			"-pr", rootDir.getAbsolutePath() 
		};
		// @formatter:on

        performDirectCodeGen(args);

        GeneratedAssert.assertDirExists(rootDir, "gen-meta-src/META-INF/soa/common/config/xyz");
        // Generates in "gen-meta-src/META-INF/soa/services/wsdl/xyz" instead?
    }

    @Test
    public void createServiceMetaDataPropsWithNewStructureWithIntfPropertiesFilePresent() throws Exception {
        // Initialize testing paths
        testingdir.ensureEmpty();
        File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/Testing.wsdl");
        File rootDir = testingdir.getDir();

        File intfFile = new File(rootDir, "service_intf_project.properties");
        TestResourceUtil.copyResource("intfs/TestService/service_intf_project.properties", intfFile);

        // @formatter:off
		String[] args = new String[] {
			"-servicename", "MyServiceV1",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "ServiceMetadataProps",
			"-publicservicename","MyService",
			"-adminname","xyz",
			"-pr", rootDir.getAbsolutePath() 
		};
		// @formatter:on

        performDirectCodeGen(args);

        // adminname is Testing and not xyz
        File propFile = GeneratedAssert.assertFileExists(rootDir,
                        "gen-meta-src/META-INF/soa/common/config/Testing/service_metadata.properties");

        FileInputStream in = null;
        try {
            in = new FileInputStream(propFile);
            Properties props = new Properties();
            props.load(in);
            assertEquals("Testing", props.getProperty("admin_name"));
            assertEquals("1.1.1", props.getProperty("service_version"));
            assertEquals("TestService", props.getProperty("service_name"));
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }
}
