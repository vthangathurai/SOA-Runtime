/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.errorlibrary.CodeGenAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class ServiceUIDQETest extends AbstractServiceGeneratorTestCase{

	//NamespaceContextImpl nsc;

	
	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	
	HashMap<String,String> propertiesFileMap;
	@Before
	public void initialize() throws Exception{
		testingdir.ensureEmpty();
		destDir = getTestDestDir();
		binDir = testingdir.getFile("bin");
		prDir = getTestDestDir();
			
		 
		 File src= new File(destDir.getAbsolutePath()+"/src");
		 if(!src.exists())
			 src.mkdir();
			
		
		propertiesFileMap = new HashMap<String,String>();
		
		propertiesFileMap.put("service_interface_class_name", "com.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface");
		propertiesFileMap.put("service_layer","COMMON");
		propertiesFileMap.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\AccountService.wsdl");
		propertiesFileMap.put("service_version","1.0.0");
		propertiesFileMap.put("admin_name","newadminname");
		propertiesFileMap.put("sipp_version","1.1");
		propertiesFileMap.put("service_namespace_part","Billing");
		propertiesFileMap.put("domainName","ebayopen");
		
	}
	
	
	
	// create a unit test with admin name
	@Test
	//@Ignore("result to be verified")
	public void generateUnitTestWithAdminname() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV1.wsdl");
		
		String testArgs1[] =  new String[] {
				"-servicename","ConfigGroupMarketV1",
				"-wsdl", wsdl.getAbsolutePath(),
				"-genType", "UnitTest",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-adminname","ConfigGroupMarketV1"

			};
		performDirectCodeGen(testArgs1, binDir);
		File testClassFile = new File(destDir,"gen-test");
		CodeGenAssert.assertJavaSourceExists(testClassFile,"org.ebayopensource.turmeric.common.v1.services.test.ConfigGroupMarketV1Test");



	}

	
	// generate skeleton, test class and dispatcher class

	@Test
	public void generateSkeletonAndDispatcherWithAdminname() throws Exception{
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");

		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-wsdl", wsdl.getAbsolutePath(),
				"-genType", "ServiceFromWSDLIntf",
				//"-src", "AntTests/out",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-adminname","newadminname"

			};

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-interface", "com/ebayopensource/test/soaframework/tools/codegen/SimpleServiceInterface.java",
				"-genType", "ServiceFromWSDLImpl",
				"-src", destDir.getAbsolutePath()+"/gen-src",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-adminname","newadminname",
				"-gt"

			};

		String testArgs2[] =  new String[] {
				"-servicename","NewService",
				"-sicn","com/ebayopensource/test/soaframework/tools/codegen/gen/NewadminnameImplSkeleton",
				"-wsdl",wsdl.getAbsolutePath(),
				"-genType", "Dispatcher",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-adminname","newadminname"


			};
		createPropsFile(destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
		performDirectCodeGen(testArgs1, binDir);
		performDirectCodeGen(testArgs2, binDir);
	
		String implClass = destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/test/soaframework/tools/codegen/gen/NewadminnameImplSkeleton.java";
		File implementationClass = new File(implClass);
		assertTrue(implementationClass.exists());

		String dispatcherClass = destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/test/soaframework/tools/codegen/gen/gen/NewadminnameRequestDispatcher.java";
		File dispatcClass = new File(dispatcherClass);
		assertTrue(dispatcClass.exists());



	}



// testing for namespace updated in metadata as wsdl is updated- v3 call

	@Test
	public void changingWSDLNamespaceShouldUpdateMetadataOnRegeneration() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AdvertisingNewService3V1.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", "AntTests/out",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),


			};

		String testArgs2[] =  new String[] {
				"-servicename", "NewService",
				"-wsdl", wsdl.getAbsolutePath(),
				"-genType", "ServiceMetadataProps",
				//"-src", "AntTests/out",
				"-dest",  destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin",  binDir.getAbsolutePath(),
				"-pr", prDir.getAbsolutePath(),


			};
		createPropsFile(destDir.getAbsolutePath());
		performDirectCodeGen(testArgs2, binDir);
		performDirectCodeGen(testArgs, binDir);
		
		String str =  destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
		File metadata = new File(str);
		assertTrue(metadata.exists());
		FileInputStream in = new FileInputStream(metadata);
		Properties pro = new Properties();
		pro.load(in);
		in.close();;
		assertEquals("http://www.ebayopensource.org/turmeric/advertising/v1/services",pro.getProperty("service_namespace"));
		
		wsdl = getCodegenQEDataFileInput("BillingNewService3V1.wsdl");
		
		String testArgs3[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ClientNoConfig",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", "AntTests/out",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath()



			};

		performDirectCodeGen(testArgs3, binDir);
		
		
		metadata = new File(str);
		in = new FileInputStream(metadata);
		pro.load(in);
		assertEquals("http://www.ebayopensource.org/turmeric/billing/v1/services",pro.getProperty("service_namespace"));
		assertEquals("NewService3New",pro.getProperty("service_name"));
		in.close();

	}

	// testing for namespace updated in metadata as wsdl is updated- plugin call

	@Test
	public void changingWSDLNamespaceShouldUpdateMetadataOnRegenerationV3() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AdvertisingNewService3V1.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-gip","org.ebayopensource.src",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),


			};

		String testArgs2[] =  new String[] {
				"-servicename", "NewService",
				"-wsdl", wsdl.getAbsolutePath(),
				"-genType", "ServiceMetadataProps",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),


			};
		createPropsFile(destDir.getAbsolutePath());

		performDirectCodeGen(testArgs2, binDir);
		performDirectCodeGen(testArgs, binDir);
		
		String str = destDir.getAbsolutePath() +"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
		File metadata = new File(str);
		assertTrue(metadata.exists());
		FileInputStream in = new FileInputStream(metadata);
		Properties pro = new Properties();
		pro.load(in);
		in.close();
		
		assertEquals("http://www.ebayopensource.org/turmeric/advertising/v1/services",pro.getProperty("service_namespace"));
		
		wsdl = getCodegenQEDataFileInput("BillingNewService3V1.wsdl");
		
		String testArgs3[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-gip","org.ebayopensource.src",
				"-wsdl",wsdl.getAbsolutePath(),
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-namespace","http://namespace"



			};

		performDirectCodeGen(testArgs3, binDir);
		metadata = new File(str);
		in = new FileInputStream(metadata);
		pro.load(in);
		assertEquals("http://www.ebayopensource.org/turmeric/billing/v1/services",pro.getProperty("service_namespace"));
		assertEquals("NewService3New",pro.getProperty("service_name"));
		in.close();
	}


		//testing for ServiceOpProps
		@Test
		public void testingServiceOperationOpsWithAdminname() throws Exception{
			
			File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
			
			String testArgs[] =  new String[] {
					"-servicename","NewService",
					"-genType", "ServiceOpProps",
					"-wsdl",wsdl.getAbsolutePath(),
					//"-src", binDir.getAbsolutePath(),
					"-dest", destDir.getAbsolutePath(),
					"-scv", "1.2.3",
					"-slayer","COMMON",
					"-bin", binDir.getAbsolutePath(),
					"-pr",prDir.getAbsolutePath(),

				};

			createPropsFile(destDir.getAbsolutePath());
			performDirectCodeGen(testArgs, binDir);

			String serviceOps = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/config/newadminname/service_operations.properties";
			File serviceOpsProps = new File(serviceOps);
			assertTrue(!serviceOpsProps.exists());



		}



		@Test
		public void testForsmp_version1_1() throws Exception{
			
			File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
			
			String testArgs[] =  new String[] {
					"-servicename","NewService",
					"-wsdl", wsdl.getAbsolutePath(),
					"-genType", "ServiceMetadataProps",
					//"-src", destDir.getAbsolutePath(),
					"-dest", destDir.getAbsolutePath(),
					"-scv", "1.2.3",
					"-slayer","COMMON",
					"-bin", binDir.getAbsolutePath(),
					"-pr",prDir.getAbsolutePath(),

				};
			createPropsFile(destDir.getAbsolutePath());
			performDirectCodeGen(testArgs, binDir);
			String str = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
			File metadata = new File(str);

			assertTrue(metadata.exists());
			FileInputStream in = new FileInputStream(metadata);
			Properties pro = new Properties();
			pro.load(in);

			in.close();
			assertEquals("1.1",pro.getProperty("smp_version"));
		}
		@Test
		public void testForsmp_version1_0() throws Exception{
			
			File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
			
			String testArgs[] =  new String[] {
					"-servicename","NewService",
					"-wsdl", wsdl.getAbsolutePath(),
					"-genType", "ServiceMetadataProps",
					//"-src", destDir.getAbsolutePath(),
					"-dest", destDir.getAbsolutePath(),
					"-scv", "1.2.3",
					"-slayer","COMMON",
					"-bin", binDir.getAbsolutePath(),
					"-pr",prDir.getAbsolutePath(),

				};
			propertiesFileMap.put("sipp_version","1.0");
			createPropsFile(destDir.getAbsolutePath());
			performDirectCodeGen(testArgs, binDir);
			String str = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
			File metadata = new File(str);

			assertTrue(metadata.exists());
			FileInputStream in = new FileInputStream(metadata);
			Properties pro = new Properties();
			pro.load(in);
			in.close();

			assertEquals("1.0",pro.getProperty("smp_version"));
		}
		@Test
		public void testForsmp_version_noValue() throws Exception{
			
			File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
			
			String testArgs[] =  new String[] {
					"-servicename","NewService",
					"-wsdl", wsdl.getAbsolutePath(),
					"-genType", "ServiceMetadataProps",
					//"-src", destDir.getAbsolutePath(),
					"-dest", destDir.getAbsolutePath(),
					"-scv", "1.2.3",
					"-slayer","COMMON",
					"-bin", binDir.getAbsolutePath(),
					"-pr",prDir.getAbsolutePath(),

				};
			propertiesFileMap.put("sipp_version","");
			createPropsFile(destDir.getAbsolutePath());

			performDirectCodeGen(testArgs, binDir);
			String str = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
			File metadata = new File(str);

			assertTrue(metadata.exists());
			FileInputStream in = new FileInputStream(metadata);
			Properties pro = new Properties();
			pro.load(in);
			in.close();

			assertEquals(null,pro.getProperty("smp_version"));
		}
		
		

		private void CreateInterfacePropsFile(HashMap<String,String> map,String path) throws Exception{
		
			File file = new File(path+File.separator +"service_intf_project.properties");
			File destDir = new File(path);
			if(!destDir.exists())
				destDir.mkdirs();
				
			if(!file.exists())
			file.createNewFile();
			Properties pro = new Properties();

			FileInputStream in = new FileInputStream(file);
	        pro.load(in);
	        pro.setProperty("service_interface_class_name",map.get("service_interface_class_name"));
	        pro.setProperty("service_layer",map.get("service_layer"));
	        pro.setProperty("original_wsdl_uri",map.get("original_wsdl_uri"));
	        pro.setProperty("service_version",map.get("service_version"));
	        pro.setProperty("admin_name",map.get("admin_name"));
	        pro.setProperty("sipp_version",map.get("sipp_version"));
	        pro.setProperty("service_namespace_part",map.get("service_namespace_part"));
	        pro.setProperty("domainName",map.get("domainName"));
	        //pro.setProperty("envMapper",map.get("envMapper"));  
	        FileOutputStream out = new FileOutputStream(path +File.separator +"service_intf_project.properties");
	        pro.store(out,null);
	        out.close();
			in.close();
		}

	private  void createPropsFile(String destDir) throws Exception{

		CreateInterfacePropsFile(propertiesFileMap,destDir);

	}


private static void CreateConsumerPropsFile(String destDir) throws Exception{
	File dest = new File(destDir);
	File file = new File(destDir +File.separator +"service_consumer_project.properties");
	if(!dest.exists())
		dest.mkdirs();
	if(!file.exists())
	file.createNewFile();
	Properties pro = new Properties();

	FileInputStream in = new FileInputStream(file);
    pro.load(in);

    pro.setProperty("client_name","clientname");
    pro.setProperty("scpp_version","1.1");
    FileOutputStream out= new FileOutputStream(destDir +File.separator+"service_consumer_project.properties");
    pro.store(out,null);
    out.close();
    in.close();

}


//testing for service name updated in metadata as wsdl is updated

@Test
public void testIntfPropsValuesUsedWhenBuilding() throws Exception{
	
	File wsdl = getCodegenQEDataFileInput("AdvertisingNewService3V1.wsdl");
	String testArgs[] =  new String[] {
			"-servicename","NewService",
			"-genType", "ServiceFromWSDLIntf",
			"-wsdl",wsdl.getAbsolutePath(),
			//"-src", destDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.2.3",
			"-slayer","COMMON",
			"-bin", binDir.getAbsolutePath(),
			"-pr", prDir.getAbsolutePath(),




		};

	String testArgs2[] =  new String[] {
			"-servicename", "NewService",
			"-wsdl",wsdl.getAbsolutePath(),
			"-genType", "ServiceMetadataProps",
			//"-src", destDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.2.3",
			"-slayer","COMMON",
			"-bin", binDir.getAbsolutePath(),
			"-pr",prDir.getAbsolutePath(),


		};
	createPropsFile(destDir.getAbsolutePath());

	performDirectCodeGen(testArgs2, binDir);
	performDirectCodeGen(testArgs, binDir);
	
	propertiesFileMap.put("service_version","9.9.9");
	propertiesFileMap.put("service_layer","somelayer");
	
	CreateInterfacePropsFile(propertiesFileMap,destDir.getAbsolutePath());

	 wsdl = getCodegenQEDataFileInput("BillingNewService3V1.wsdl");	
	String testArgs3[] =  new String[] {
			"-servicename","NewService",
			"-genType", "ClientNoConfig",
			"-wsdl",wsdl.getAbsolutePath(),
			//"-src", "AntTests/out",
			"-dest",destDir.getAbsolutePath(),
			"-scv", "1.2.3",
			"-slayer","COMMON",
			"-bin",binDir.getAbsolutePath(),
			"-pr",prDir.getAbsolutePath()


		};

	performDirectCodeGen(testArgs3, binDir);
	
	String str = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
	File metadata = new File(str);

	assertTrue(metadata.exists());
	FileInputStream in = new FileInputStream(metadata);
	Properties pro = new Properties();
	pro.load(in);
	in.close();

	assertEquals("9.9.9",pro.getProperty("service_version"));
	assertEquals("somelayer",pro.getProperty("service_layer"));

}


@Test
public void Bug9780() throws Exception{
	
	File src = new File(destDir.getAbsolutePath()+"/src");
	if(!src.exists())
		src.mkdirs();
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	
	String testArgs[] =  new String[] {
			"-servicename","NewService",
			"-genType", "ServiceFromWSDLImpl",
			"-gip","org.ebayopensource.src",
			"-wsdl",wsdl.getAbsolutePath(),
			"-src", destDir.getAbsolutePath()+"/src",
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.2.3",
			"-slayer","COMMON",
			"-bin",binDir.getAbsolutePath(),
			"-pr",prDir.getAbsolutePath(),
			"-namespace","http://namespace",
			"-adminname","newadminname"


		};
	
	wsdl = getCodegenQEDataFileInput("AdvertisingNewService3V1.wsdl");

	String testArgs2[] =  new String[] {
			"-servicename", "NewService",
			"-wsdl",wsdl.getAbsolutePath(),
			"-genType", "ServiceMetadataProps",
			//"-src", destDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.2.3",
			"-slayer","COMMON",
			"-bin", binDir.getAbsolutePath(),
			"-pr",prDir.getAbsolutePath(),


		};
	createPropsFile(destDir.getAbsolutePath());
	
	try{

	performDirectCodeGen(testArgs2, binDir);
	performDirectCodeGen(testArgs, binDir); }
	catch(Exception e){
		
		assertTrue(false);
	}
	
}


@After
public void deinitialize(){

	 propertiesFileMap=null;
	
	
}
}
