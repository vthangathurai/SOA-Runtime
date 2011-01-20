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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
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

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;






public class ServiceUIDQE3Test extends AbstractServiceGeneratorTestCase{
	File destDir = null;
	File prDir = null;
	File binDir = null;

	NamespaceContextImpl nsc;

	Properties pro = new Properties();
	@Before
	public void initialize() throws Exception{
		
		testingdir.ensureEmpty();
		destDir = getTestDestDir();
		binDir = testingdir.getFile("bin");
		prDir = getTestDestDir();
		 nsc = new NamespaceContextImpl();	
	
		
		pro.put("service_interface_class_name", "com.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface");
		pro.put("service_layer","COMMON");
		pro.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\AccountService.wsdl");
		pro.put("service_version","1.0.0");
		pro.put("admin_name","newadminname");
		pro.put("sipp_version","1.1");
		pro.put("service_namespace_part","Billing");
		pro.put("domainName","ebayopen");
		

	}
	
	
	

	// create a service n build.
	//Test 12 - Validate that when service project is created metdata properties file is generated with contents copied
	//from intf.properties file except namespace and service name which is got from
	//wsdl.And also validate the typemappings,java files path.
	@Test
	public void buildingServiceMetadataPropertiesGotFromIntfProps() throws Exception{
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),


			};

		
		createInterfacePropsFile(pro,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
		
		String str = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
		File metadata = new File(str);

		assertTrue(metadata.exists());

		FileInputStream in = new FileInputStream(metadata);
		Properties pro = new Properties();
		pro.load(in);
		in.close();

		assertEquals("newadminname",pro.getProperty("admin_name"));
		assertEquals("AccountService",pro.getProperty("service_name"));
		assertEquals("http://www.ebayopensource.com/turmeric/services",pro.getProperty("service_namespace"));
		assertEquals("Billing",pro.getProperty("service_namespace_part"));
		assertEquals("ebayopen",pro.getProperty("domainName"));
		
		String typemappingspath = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/TypeMappings.xml";
		File typeMapping = new File(typemappingspath);
		assertTrue(typeMapping.exists());

		String intfJava = destDir.getAbsolutePath()+"/gen-src/client/com/ebayopensource/test/soaframework/tools/codegen/SimpleServiceInterface.java";
		File intfFile = new File(intfJava);
		assertTrue(intfFile.exists());

		String asyncIntfJava = destDir.getAbsolutePath()+"/gen-src/client/com/ebayopensource/test/soaframework/tools/codegen/AsyncSimpleServiceInterface.java";
		File asyncIntfFile = new File(asyncIntfJava);
		assertTrue(asyncIntfFile.exists());

		String proxyJava = destDir.getAbsolutePath()+"/gen-src/client/com/ebayopensource/test/soaframework/tools/codegen/gen/NewadminnameProxy.java";
		File proxyFile = new File(proxyJava);
		assertTrue(proxyFile.exists());
		
		String sharedConsumer = destDir.getAbsolutePath()+"/gen-src/client/com/ebayopensource/test/soaframework/tools/codegen/newadminname/gen/SharedNewadminnameConsumer.java";
		File sharedConsumerFile = new File(sharedConsumer);
		assertTrue(sharedConsumerFile.exists());
		
		String typeDef = destDir.getAbsolutePath()+"/gen-src/client/com/ebayopensource/test/soaframework/tools/codegen/gen/NewadminnameTypeDefsBuilder.java";
		File typeDefFile = new File(typeDef);
		assertTrue(typeDefFile.exists());





	}
	// testing for metadata regeneration when the  metadata is removed
	//Test 13 -Validate that the metadata properties file is regenerated, after the file is removed and rebuilt.
	//Validate the metadata properties content are from the intf.props file and wsdl.
	@Test
	public void metadataRegerationWhenFileRemoved() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath()

			};

		String testArgs2[] =  new String[] {
				"-servicename", "NewService",
				"-wsdl", wsdl.getAbsolutePath(),
				"-genType", "ServiceMetadataProps",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),


			};
		createInterfacePropsFile(pro,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs2, binDir);
		performDirectCodeGen(testArgs, binDir);

		String serviceMetadata = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
		File scvmetaprops = new File(serviceMetadata);

		if(scvmetaprops.exists()){

			if(scvmetaprops.delete()){
				performDirectCodeGen(testArgs, binDir);

			}
			else
				throw new Exception("File Could not be deleted");

		}

		assertTrue(scvmetaprops.exists());
		FileInputStream in = new FileInputStream(serviceMetadata);
		Properties pro = new Properties();
		pro.load(in);
		in.close();
		//added for new requirement - Domain name and namespace part.
		assertEquals("Billing",pro.getProperty("service_namespace_part"));
		assertEquals("ebayopen",pro.getProperty("domainName"));
		//------------------------------
		serviceMetadata =null;
		scvmetaprops=null;

	}


	// testing for metadata regeneration when the folder where metadata file generated is removed.
	//Test 14 -Validate that the metadata properties file is regenerated, after folder containing file is removed and rebuilt.
	//Validate the metadata properties content are from the intf.props file and wsdl.
	@Test
	public void metadataRegenerationWhenFolderContainingFileRemoved() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath()

			};

		String testArgs2[] =  new String[] {
				"-servicename", "NewService",
				"-wsdl", wsdl.getAbsolutePath(),
				"-genType", "ServiceMetadataProps",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),


			};
		createInterfacePropsFile(pro,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
		performDirectCodeGen(testArgs2, binDir);

		String serviceMetadata = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
		File scvmetaprops = new File(serviceMetadata);

		
		if(scvmetaprops.exists()){

			testingdir.ensureEmpty();
			performDirectCodeGen(testArgs, binDir);

			

		}

		assertTrue(scvmetaprops.exists());

	
	}

	// testing for service name updated in metadata as wsdl is updated
	//Test 15 - Validate if wsdl is updated manually for service name and the interface project
	//is rebuilt, the updated service name should appear in the regenerated metadata properties file.
	@Test
	//@Ignore
	public void changingWSDLSvcNameShouldUpdateMetadataOnRegeneration() throws Exception{
		
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
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
				"-wsdl", wsdl.getAbsolutePath(),
				"-genType", "ServiceMetadataProps",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),


			};
		createInterfacePropsFile(pro,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs2, binDir);
		performDirectCodeGen(testArgs, binDir);
		
		String str = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
		File metadata = new File(str);
		assertTrue(metadata.exists());
		FileInputStream in = new FileInputStream(metadata);
		Properties pro = new Properties();
		pro.load(in);
		assertEquals("AccountService",pro.getProperty("service_name"));
		in.close();
		
		wsdl = getCodegenQEDataFileInput("AccountService_ChangedServiceName.wsdl");
		
		String testArgs3[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
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
		assertEquals("AccountServiceChangedName",pro.getProperty("service_name"));
		
		
		in.close();

	}

	
	// testing for service namespace updated in metadata as wsdl is updated
	//Test 16 - Validate if wsdl is updated manually for service name and the interface project
	//is rebuilt in v3 mode, the updated service name should appear in the regenerated metadata properties file.
	
	@Test
	//@Ignore
	public void changingWSDLSvcNameShouldUpdateMetadataOnRegenerationV3() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
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
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),

			};
		createInterfacePropsFile(pro,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs2, binDir);
		performDirectCodeGen(testArgs, binDir);
		
		String str = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
		File metadata = new File(str);
		assertTrue(metadata.exists());
		FileInputStream in = new FileInputStream(metadata);
		Properties pro = new Properties();
		pro.load(in);
		assertEquals("AccountService",pro.getProperty("service_name"));
		in.close();
		 wsdl = getCodegenQEDataFileInput("AccountService_ChangedServiceName.wsdl");
		
		String testArgs3[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ClientNoConfig",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-adminname","newadminname"


			};

		performDirectCodeGen(testArgs3, binDir);
		metadata = new File(str);
		in = new FileInputStream(metadata);
		pro.load(in);
		assertEquals("AccountServiceChangedName",pro.getProperty("service_name"));
		assertEquals("AccountServiceChangedName",pro.getProperty("service_name"));
		//	//added for new requirement - Domain name and namespace part.
		assertEquals("ebayopen",pro.getProperty("domainName"));
		assertEquals("Billing",pro.getProperty("service_namespace_part"));
		//----------------------------------------------------------
		in.close();
		str =null;
		pro= null;
		metadata=null;

	}

	
	// generate service config with admin name
	//Test 17 - Validate the path of generation of sc.xml by passing admin name.
	// also validating service name is not present in sc.xml
	@Test
	public void generateServiceConfigPassingAdminname() throws Exception{
		
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
				"-adminname","newadminname"

			};
		createInterfacePropsFile(pro,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-interface", "com.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface",
				"-sicn", "com.ebayopensource.test.soaframework.tools.codegen.SimpleServiceImpl",
				"-genType", "ServerConfig",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-adminname","newadminname"

			};

		performDirectCodeGen(testArgs1, binDir);

		String serviceConfigFile = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/config/newadminname/ServiceConfig.xml";
		File scvConfig = new File(serviceConfigFile);
		assertTrue(scvConfig.exists());
		
	
		
		List<String> svcNameNode = getNodeDetails(nsc,"//ns2:service-config",serviceConfigFile);
	
		String svcName = null;
	
		if(svcNameNode.size() > 0)
			svcName = svcNameNode.get(2);
	
		assertEquals(null,svcName);
		serviceConfigFile= null;

	}
	
	
	// generate client config with admin name
	//Test 18 - Validate the path of generation of cc.xml by passing admin name.
	// also validating service name is not present in cc.xml

	@Test
	
	public void generateClientConfigPassingAdminname() throws Exception{
		
		CreateConsumerPropsFile();


		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-sl","http://localhost:8080/calculator",
				"-mdest",destDir.getAbsolutePath()+"/meta-src",
				"-interface", "com.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface",
				"-genType", "ClientConfig",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.com/test/soaframework/tools/codegen",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-cn","NewClient",
				"-ccgn","MarketplaceClientGroup",
				"-adminname","newadminname",
				"-environment","production"

			};

		performDirectCodeGen(testArgs1, binDir);

		String clientConfigFile = destDir.getAbsolutePath()+"/meta-src/META-INF/soa/client/config/NewClient/production/newadminname/ClientConfig.xml";
		File clientConfig = new File(clientConfigFile);
		assertTrue(clientConfig.exists());
		

		
		List<String> svcNameNode = getNodeDetails(nsc,"//ns2:client-config",clientConfigFile);
	
		String svcName = null;
	
		if(svcNameNode.size() > 0)
			svcName = svcNameNode.get(2);
	
		assertEquals(null,svcName);

	}
	
	


	

	//generate web.xml with admin name
	//Test 19 -Validate web.xml by passing admin name
	@Test
	public void generateWebXml() throws Exception{
		
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
				"-adminname","newadminname"

			};
		createInterfacePropsFile(pro,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-genType", "webxml",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-adminname","newadminname"

			};
		performDirectCodeGen(testArgs1, binDir);
		String webXmlPath = destDir.getAbsolutePath()+"/gen-web-content/WEB-INF/web.xml";

//		NamespaceContextImpl nsc = new NamespaceContextImpl();
//		nsc.setNs2("http://java.sun.com/xml/ns/j2ee");
//		List<String> nodeInitParam = getNodeDetails(nsc,"ns2//servlet",webXmlPath);
//	
//		String initparam = null;
//		
//			if(nodeInitParam.size() > 0)
//				initparam = nodeInitParam.get(1);
		
	
		//assertEquals("newadminname",initparam);


	}
	
	

	@Test
	//Test 20 -Validate gentype All to check all artifacts.
	public void genAllWithAdminname() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");

		createInterfacePropsFile(pro,destDir.getAbsolutePath());

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-genType", "All",
				"-wsdl", wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/gen-src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-adminname","newadminname"

			};
		performDirectCodeGen(testArgs1, binDir);

		String serviceConfigFile = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/config/newadminname/ServiceConfig.xml";
		File scvConfig = new File(serviceConfigFile);
		assertTrue(scvConfig.exists());

		String metaPropsFile = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
		File metaProperties = new File(metaPropsFile);
		assertTrue(metaProperties.exists());

		String implClass = destDir.getAbsolutePath()+"/gen-src/service/com/ebayopensource/test/soaframework/tools/codegen/gen/NewadminnameImplSkeleton.java";
		File implClassFile = new File(implClass);
		assertTrue(implClassFile.exists());

		String dispatcherClass = destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/test/soaframework/tools/codegen/gen/gen/NewadminnameRequestDispatcher.java";
		File dispatcherClassFile = new File(dispatcherClass);
		assertTrue(dispatcherClassFile.exists());

		String testClass = destDir.getAbsolutePath()+"/gen-test/com/ebayopensource/test/soaframework/tools/codegen/test/NewadminnameTest.java";
		File testClassFile = new File(testClass);
		assertTrue(testClassFile.exists());
		
		String clientConfig = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/newadminname/ClientConfig.xml";
		File clientConfigFile = new File(clientConfig);
		assertTrue(clientConfigFile.exists());

		String sharedConsumerClass = destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/test/soaframework/tools/codegen/newadminname/gen/SharedNewadminnameConsumer.java";
		File sharedConsumerClassFile = new File(sharedConsumerClass);
		assertTrue(sharedConsumerClassFile.exists());
		
	

	}
	
	


private  void CreateConsumerPropsFile() throws Exception{

	File file = new File(destDir.getAbsolutePath()+File.separator +"service_consumer_project.properties");
	
	File dDir = new File(destDir.getAbsolutePath());
	if(!dDir.exists())
		dDir.mkdir();
	
	if(!file.exists())
	file.createNewFile();
	Properties pro = new Properties();

	FileInputStream in = new FileInputStream(file);
    pro.load(in);

    pro.setProperty("client_name","clientname");
    pro.setProperty("scpp_version","1.1");
    FileOutputStream out= new FileOutputStream(destDir.getAbsolutePath()+ File.separator + "service_consumer_project.properties");
    pro.store(out,null);
    out.close();
    in.close();

}



public List<String> getNodeDetails(NamespaceContext nsc,String exprString,String filePath) throws XPathExpressionException{
	
	List<String> list = new ArrayList<String>();
	XPathFactory factory = XPathFactory.newInstance();

	// 2. Use the XPathFactory to create a new XPath object
	XPath xpath = factory.newXPath();
	
	xpath.setNamespaceContext(nsc);

	// 3. Compile an XPath string into an XPathExpression
	XPathExpression expression = xpath.compile(exprString);

	// 4. Evaluate the XPath expression on an input document
	Node result = (Node)expression.evaluate(new org.xml.sax.InputSource(filePath),XPathConstants.NODE );
	
	String svcName = null;
	NamedNodeMap attMap = result.getAttributes();
    Node att =attMap.getNamedItem("service-name");
    if(att != null)
    svcName = att.getNodeValue();
	if(result != null){
    list.add(result.getNodeName());
    list.add(result.getTextContent());
    list.add(svcName);
	}
	factory = null;
	xpath = null;
	expression = null;
	result=null;
	attMap = null;
	att = null;
	return list;
}
@After
public void deinitialize(){

	 nsc= null;	
}
}
