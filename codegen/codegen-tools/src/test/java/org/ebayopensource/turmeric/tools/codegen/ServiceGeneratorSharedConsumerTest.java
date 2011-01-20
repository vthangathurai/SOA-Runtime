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
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.commons.lang.StringUtils;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.GeneratedAssert;
import org.ebayopensource.turmeric.tools.codegen.external.WSDLUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.junit.Test;

public class ServiceGeneratorSharedConsumerTest extends AbstractServiceGeneratorTestCase{
    
    public Properties getDefaultConsumerProps() {
        Properties props = new Properties();
        
        props.put("scpp_version", "1.1");
        props.put("client_name","Somename");

        return props;
    }
    
	public Properties getDefaultInterfaceProps() {
	    Properties props = new Properties();
	    
        props.put("service_interface_class_name", "org.ebayopensource.turmeric.runtime.types.newadminname.NewAdminName");
        props.put("service_layer","COMMON");
        props.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\AccountService.wsdl");
        props.put("service_version","1.0.0");
        props.put("admin_name","NewAdminName");
        props.put("sipp_version","1.1");
        props.put("service_namespace_part","Billing");
        props.put("domainName","ebay");
        props.put("enabledNamespaceFolding","true");
        
        // Since this test case has no ServiceConfig.xml to work off of, we need to specify this value in the properties file.
        props.put(CodeGenConstants.PROPERTY_SHARED_CONSUMER_SHORTER_PATH, "org.ebayopensource.turmeric.runtime.types.newadminname.gen");
        
        return props;
	}
	
	/**
	 * Test the creation of the SharedConsumer during a interface generation
	 * using the standard layout and generation techniques found within
	 * the turmeric-maven-plugin
     * 
     * @throws Exception
	 */
	@Test
	public void testStandard() throws Exception {
	    /* Default Codegen looks for the files:
	     *   "./service_consumer_project.properties" and
	     *   "./service_intf_project.properties"
	     * which is in violation of the maven testing rules 
	     * (READ outside of testing dir)
	     */
	    mavenTestingRules.setFailOnViolation(false);
	    
        testingdir.ensureEmpty();
        File mdestDir = testingdir.getFile("target/generated-resources/codegen");
        File jdestDir = testingdir.getFile("target/generated-sources/codegen");
        File binDir = testingdir.getFile("target/classes");
        File srcDir = getTestSrcDir();
        
        File wsdl = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV1.wsdl");

        MavenTestingUtils.ensureDirExists(jdestDir);
        MavenTestingUtils.ensureDirExists(mdestDir);
        MavenTestingUtils.ensureDirExists(binDir);

        String serviceName = "NewService";
        String adminName = "NewAdminName";
        String packageName = "org.ebayopensource.turmeric.runtime.types.newadminname";

        // @formatter:off
	    String args[] = {
            "-gentype", "ClientNoConfig", 
            "-servicename", serviceName, 
	        "-adminname", adminName,
	        "-mdest", mdestDir.getAbsolutePath(),
            "-jdest", jdestDir.getAbsolutePath(),
	        "-bin", binDir.getAbsolutePath(),
	        "-src", srcDir.getAbsolutePath(),
	        "-wsdl", wsdl.getAbsolutePath(),
	        InputOptions.OPT_GEN_SHARED_CONSUMER, 
	        InputOptions.OPT_PACKAGE_SHARED_CONSUMER, packageName + ".gen", 
	        "-gip", packageName,
	        "-noObjectFactoryGeneration", "false",
	        "-enablednamespacefolding"
	    };
        // @formatter:on

        performDirectCodeGen(args, binDir);

        // Should be generated off of AdminName
        GeneratedAssert.assertFileExists(mdestDir, getServiceMetadataPath(adminName));
        GeneratedAssert.assertFileExists(mdestDir, ("META-INF/soa/common/config/" + adminName + "/TypeMappings.xml"));
        GeneratedAssert.assertFileExists(mdestDir, ("META-INF/soa/services/wsdl/" + adminName + "_mns.wsdl"));

        // Should NOT be generated off ServiceName
        GeneratedAssert.assertPathNotExists(mdestDir, getServiceMetadataPath(serviceName));

        // Validate Generated Code
        assertGeneratedJavaFiles(jdestDir, packageName, adminName);
	}
	
	/**
	 * Test the creation of the SharedConsumer during interface generation
	 * using the legacy layout and generation techniques found within the
	 * turmeric-maven-plugin
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLegacy() throws Exception
	{
        testingdir.ensureEmpty();
        File rootDir = testingdir.getDir();
        File mdestDir = testingdir.getFile("gen-meta-src");
        File jdestDir = testingdir.getFile("gen-src");
        File binDir = testingdir.getFile("bin");
        File srcDir = getTestSrcDir();
        
        File wsdl = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV1.wsdl");

        MavenTestingUtils.ensureDirExists(jdestDir);
        MavenTestingUtils.ensureDirExists(mdestDir);
        MavenTestingUtils.ensureDirExists(binDir);
        
        Properties props = getDefaultInterfaceProps();
        createInterfacePropsFile(rootDir, props);
        
        String serviceName = "NewService";
        String adminName = props.getProperty("admin_name");

        // @formatter:off
	    String args[] = {
	      "-gentype", "ClientNoConfig",
          "-servicename", serviceName,
          "-pr", rootDir.getAbsolutePath(), 
	      "-mdest", mdestDir.getAbsolutePath(),
	      "-jdest", jdestDir.getAbsolutePath(),
	      "-bin", binDir.getAbsolutePath(),
	      "-wsdl", wsdl.getAbsolutePath(),
	      "-src", srcDir.getAbsolutePath()
	    };
        // @formatter:on

        performDirectCodeGen(args, binDir);

        // Should be generated off of AdminName
        GeneratedAssert.assertFileExists(mdestDir, getServiceMetadataPath(adminName));
        GeneratedAssert.assertFileExists(mdestDir, ("META-INF/soa/common/config/" + adminName + "/TypeMappings.xml"));
        GeneratedAssert.assertFileExists(mdestDir, ("META-INF/soa/services/wsdl/" + adminName + "_mns.wsdl"));
        
        // Should NOT be generated off ServiceName
        GeneratedAssert.assertPathNotExists(mdestDir, getServiceMetadataPath(serviceName));

        // Validate Generated Code
        String packageName = getPackage(props, wsdl);
        assertGeneratedJavaFiles(jdestDir, packageName, adminName);
	}

    protected void assertGeneratedJavaFiles(File jdestDir, String packageName, String name) {
        // Shared Consumer File
        GeneratedAssert.assertJavaExists(jdestDir, (packageName + ".gen.Shared" + name + "Consumer"));
        // Async Service File
        GeneratedAssert.assertJavaExists(jdestDir, (packageName + ".Async" + name));
        // Service File
        GeneratedAssert.assertJavaExists(jdestDir, (packageName + "." + name));
        // Type Def File
        GeneratedAssert.assertJavaExists(jdestDir, (packageName + ".gen." + name + "TypeDefsBuilder"));
        // Proxy File File
        GeneratedAssert.assertJavaExists(jdestDir, (packageName + ".gen." + name + "Proxy"));
    }

    protected String getServiceMetadataPath(String name) {
        return "META-INF/soa/common/config/" + name + "/service_metadata.properties";
    }
	
    @Test
	public void testPreBuild() throws Exception {
        testingdir.ensureEmpty();
        File destDir = testingdir.getDir();
        File jdestDir = testingdir.getFile("dest/gen-src/client");
        File mdestDir = testingdir.getFile("gen-meta-src");
        File binDir = testingdir.getFile("bin");
        File metaDir = testingdir.getFile("meta-src");
        
        MavenTestingUtils.ensureDirExists(jdestDir);
        MavenTestingUtils.ensureDirExists(mdestDir);
        MavenTestingUtils.ensureDirExists(metaDir);
		
        Properties props = getDefaultInterfaceProps();
		createInterfacePropsFile(destDir, props);
		
		File wsdl = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV1.wsdl");
		
		String serviceName = "NewService";
		String adminName = props.getProperty("admin_name");
		
        // @formatter:off
		String testArgs1[] = {
			"-servicename", serviceName,
			"-namespace","http://www.ebayopensource.com/turmeric/services",
			"-gentype", "ClientNoConfig",
			"-wsdl", wsdl.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-metasrc", metaDir.getAbsolutePath(),
			"-jdest", jdestDir.getAbsolutePath(),
			"-scv", "1.0.0",
			"-slayer","COMMON",
			"-bin", binDir.getAbsolutePath(),
		};	
        // @formatter:on
		
		performDirectCodeGen(testArgs1, binDir);

		// Should be generated off of AdminName
		GeneratedAssert.assertFileExists(mdestDir, getServiceMetadataPath(adminName));
		GeneratedAssert.assertFileExists(mdestDir, ("META-INF/soa/common/config/" + adminName + "/TypeMappings.xml"));
		GeneratedAssert.assertFileExists(mdestDir, ("META-INF/soa/services/wsdl/" + adminName + "_mns.wsdl"));
		
		// Should NOT be generated off ServiceName
		GeneratedAssert.assertPathNotExists(mdestDir, getServiceMetadataPath(serviceName));

		// Validate Generated Code
		String packageName = getPackage(props, wsdl);
		assertGeneratedJavaFiles(jdestDir, packageName, adminName);
	}
	
	private String getPackage(Properties props, File wsdl) throws WSDLException
	{
	    String intfClass = null;
	    String intfPackage = null;
	    
	    if(!props.isEmpty()) {
	        intfClass = props.getProperty("service_interface_class_name");
	        if(StringUtils.isNotBlank(intfClass)) {
	            int idx = intfClass.lastIndexOf('.');
	            if(idx>=0) {
	                intfPackage = intfClass.substring(0,idx);
	            }
	        }
	    }
	    if(StringUtils.isBlank(intfPackage)) {
	        String namespace = getTargetNamespace(wsdl);
	        return WSDLUtil.getPackageFromNamespace(namespace);
	    }
	    
	    return intfPackage;
	}
	
	private String getTargetNamespace(File wsdlFile) throws WSDLException {
        Definition def = null;
        WSDLFactory wsdl = WSDLFactory.newInstance();
        WSDLReader reader = wsdl.newWSDLReader();
        def = reader.readWSDL(wsdlFile.toURI().toASCIIString());
        return def.getTargetNamespace();
    }

    @Test
	public void testConsumer() throws Exception
	{
        testingdir.ensureEmpty();

        File wsdl = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV1.wsdl");
		
        File rootDir = testingdir.getDir();
        File destDir = testingdir.getFile("dest");
        File mdestDir = testingdir.getFile("meta-src");
        File jdestDir = testingdir.getFile("dest/gen-src/client");
        File binDir = testingdir.getFile("bin");
        File metaDir = testingdir.getFile("meta-src");
        
        MavenTestingUtils.ensureDirExists(jdestDir);
        MavenTestingUtils.ensureDirExists(mdestDir);
        MavenTestingUtils.ensureDirExists(binDir);
        MavenTestingUtils.ensureDirExists(metaDir);
        
        Properties props = getDefaultInterfaceProps();
        createInterfacePropsFile(rootDir, props);

		String serviceName = "NewService";
		String packageName = "org.ebayopensource.turmeric.runtime.types.newadminname";
	
        // @formatter:off
		String testArgs1[] =  new String[] {
			"-servicename",serviceName,
			"-namespace","http://www.ebayopensource.com/turmeric/services",
			"-genType", "ServiceFromWSDLIntf",
			"-wsdl", wsdl.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-jdest", jdestDir.getAbsolutePath(),
			"-mdest", mdestDir.getAbsolutePath(),
			"-scv", "1.0.0",
			"-slayer","COMMON",
			"-bin", binDir.getAbsolutePath(),
			"-pr", rootDir.getAbsolutePath()
	
		};	
        // @formatter:on
		
		performDirectCodeGen(testArgs1, binDir);
		
        // @formatter:off
		String testArgs2[] =  new String[] {
			"-servicename", "NewService",
			"-namespace", "http://www.ebayopensource.com/turmeric/services",
			"-genType", "ClientConfig",
			"-interface", packageName + ".NewAdminName",
			"-mdest", mdestDir.getAbsolutePath(),
			"-sl", "http://ebay.com/service",
			"-environment","production",
			"-ccgn","SOAClientGroup",
			"-dest", destDir.getAbsolutePath(),
			"-jdest", jdestDir.getAbsolutePath(),
			"-cn", "NewAdminNameConsumer",
			"-scv", "1.0.0",
			"-slayer","COMMON",
			"-bin", binDir.getAbsolutePath(),
			"-pr", rootDir.getAbsolutePath()
		};	
        // @formatter:on
		
		performDirectCodeGen(testArgs2, binDir);
		
        createConsumerPropsFile(rootDir, getDefaultConsumerProps());
        
        // @formatter:off
		String testArgs3[] =  new String[] {
			"-servicename", "NewService",
			"-namespace", "http://www.ebayopensource.com/turmeric/services",
			"-genType", "Consumer",
			"-interface", packageName + ".NewAdminName",
			"-sl", "http://ebay.com/service",
			"-environment", "production",
			"-ccgn", "SOAClientGroup",
			"-dest", destDir.getAbsolutePath(),
			"-jdest", jdestDir.getAbsolutePath(),
			"-cn", "NewAdminNameConsumer",
			"-scv", "1.0.0",
			"-slayer","COMMON",
			"-bin", binDir.getAbsolutePath(),
			"-pr", rootDir.getAbsolutePath()
		};	
        // @formatter:on
		
		performDirectCodeGen(testArgs3, binDir);
		
		GeneratedAssert.assertJavaNotExists(jdestDir, (packageName + ".newadminname.gen.SharedNewAdminNameConsumer"));
	}
	
	private void createInterfacePropsFile(File basedir, Properties props) throws IOException
	{
		File file = new File(basedir, "service_intf_project.properties");
		Properties pro = loadProperties(file);
		Enumeration<?> names = props.propertyNames();
		while(names.hasMoreElements()) {
		    String key = (String) names.nextElement();
		    String value = props.getProperty(key);
		    pro.setProperty(key, value);
		}
        writeProperties(file, pro);
	}
	
	private void createConsumerPropsFile(File basedir, Properties props) throws Exception {
		File file = new File(basedir, "service_consumer_project.properties");
		Properties pro = loadProperties(file);
        Enumeration<?> names = props.propertyNames();
        while(names.hasMoreElements()) {
            String key = (String) names.nextElement();
            String value = props.getProperty(key);
            pro.setProperty(key, value);
        }
	    writeProperties(file, pro);
	}
}
