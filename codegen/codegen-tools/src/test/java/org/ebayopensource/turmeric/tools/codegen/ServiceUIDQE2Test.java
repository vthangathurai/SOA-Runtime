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
import java.io.IOException;
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


public class ServiceUIDQE2Test extends AbstractServiceGeneratorTestCase{
	File destDir = null;
	File prDir = null;
	File binDir = null;
	NamespaceContextImpl nsc;
	final String IMPL_PROPERTIES = "service_impl_project.properties";
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
	
	//Test 1 -Validate the existence if cc.xml in the impl project in correct folder structure.
	//Service name attribute is removed from cc.xml after 2.4 soa version.
	//Note: simp_version is a new property added by plugin to service_impl_project.properties file,  based on this property if => 1.1 the
	//service name in cc.xml will be removed. => 1.1 means  2.4 or post 2.4
	@Test
	//@Ignore("need to be verified")
	public void testForClientConfigInImpl() throws Exception{
			
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	
			File src= new File(destDir.getAbsolutePath()+"/src");
			if(!src.exists())
				src.mkdirs();
			createImplPropsFile();
			String testArgs1[] =  new String[] {
					"-servicename","NewService",
					"-wsdl", wsdl.getAbsolutePath(),		
					"-genType", "ServiceFromWSDLImpl",
					"-src", destDir.getAbsolutePath()+"/src",
					"-dest", destDir.getAbsolutePath(),
					"-scv", "1.2.3",
					"-slayer","COMMON",
					"-bin", binDir.getAbsolutePath(),
					"-pr",prDir.getAbsolutePath(),
					"-cn","ClientName",
					"-adminname","somename",
					"-ccgn","sdsaf",
					"-gt"

				};

			performDirectCodeGen(testArgs1, binDir);

			String clientConfigFile = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/ClientName/ClientConfig.xml";
			File clientConfig = new File(clientConfigFile);
			assertTrue(clientConfig.exists());
			
			
			
			List<String> svcNameNode = getNodeDetails(nsc,"//ns2:client-config",clientConfigFile);
		
			String svcName = null;
		
			if(svcNameNode.size() > 0)
				svcName = svcNameNode.get(2);
		
			assertEquals(null,svcName);
		
 
		}
	//Test 2 - Validate the the sc.xml is generated in correct path and service name is removed from the the sc.xml
	//Service name attribute is removed from sc.xml after 2.4 soa version.
	//Note: sipp_version is a new property added by plugin to service_intf_project.properties file,  based on this property if => 1.1 the
	//service name in sc.xml will be removed. => 1.1 means  2.4 or post 2.4
	@Test 
	public void generateServiceConfigWithoutPassingAdminname() throws Exception{
	
		
	

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-interface", "org.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface",
				"-sicn", "org.ebayopensource.test.soaframework.tools.codegen.SimpleServiceImpl",
				"-genType", "ServerConfig",
				//"-src", "AntTests/out",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
		

			};

		performDirectCodeGen(testArgs1, binDir);

		String serviceConfigFile = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/config/NewService/ServiceConfig.xml";
		File scvConfig = new File(serviceConfigFile);
		assertTrue(scvConfig.exists());
		
		
		List<String> svcNameNode = getNodeDetails(nsc,"//ns2:service-config",serviceConfigFile);
	
		String svcName = null;
	
		if(svcNameNode.size() > 0)
			svcName = svcNameNode.get(2);
	
		assertEquals(null,svcName);
		

	}
	
	// generate client config with admin name
	//Test 3 -Validate the existence if cc.xml in the consumer project is in correct folder structure.
	//Service name attribute is removed from cc.xml after 2.4 soa version.
	//Note: scpp_version is a new property added by plugin to service_consumer_project.properties file,  based on this property if => 1.1 the
	//service name in cc.xml will be removed.  => 1.1 means  2.4 or post 2.4
	@Test

	public void generateClientConfigWithoutPassingAdminname() throws Exception{
		
		//CreateConsumerPropsFile();


		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-interface", "com.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface",
				//"-wsdl", wsdl.getAbsolutePath(),
				"-sicn", "com.ebayopensource.test.soaframework.tools.codegen.SimpleServiceImpl",
				"-genType", "ClientConfig",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),

			};

		performDirectCodeGen(testArgs1, binDir);

		String clientConfigFile = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/NewService/ClientConfig.xml";
		File clientConfig = new File(clientConfigFile);
		assertTrue(clientConfig.exists());
		

		
		List<String> svcNameNode = getNodeDetails(nsc,"//ns2:client-config",clientConfigFile);
	
		String svcName = null;
	
		if(svcNameNode.size() > 0)
			svcName = svcNameNode.get(2);
	
		assertEquals("{http://www.ebayopensource.org/turmeric/common/v1/services}NewService",svcName);
		

	}

	//testing metadata props gentype with admin name
	//Test 4 - Validate metadata properties file is generated in proper path with service name,namespace,namespace part and domain name
	//passed through the options.
	@Test
	public void metadataGentypePassingAdminName() throws Exception{
		String testArgs[] =  new String[] {
				"-servicename", "SimpleService401",
				"-interface", "org.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface.java",
				"-genType", "ServiceMetadataProps",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.com/marketplace",
				"-pr",prDir.getAbsolutePath(),
				"-adminname","newadmin"
			};

		performDirectCodeGen(testArgs, binDir);

		File metadata = new File(destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadmin/service_metadata.properties");

		assertTrue(metadata.exists());
		FileInputStream in = new FileInputStream(metadata);
		Properties pro = new Properties();
		pro.load(in);
		assertEquals(pro.getProperty("service_name"),"SimpleService401");
		assertEquals(pro.getProperty("service_namespace"),"http://www.ebayopensource.com/marketplace");
		assertEquals(null,pro.getProperty("service_namespace_part"));
		assertEquals(null,pro.getProperty("domainName"));
		in.close();
        
    

	}

	//testing metadata props gentype without admin name
	//Test 5 - Validate metadata properties file is generated in proper path with service name,namespace passed through the options.
	@Test
	public void metadataGentypeNotPassingAdminName() throws Exception{
		String testArgs[] =  new String[] {
				"-servicename", "SimpleService401",
				"-interface", "org.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface.java",
				"-genType", "ServiceMetadataProps",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.com/marketplace",
				"-pr",prDir.getAbsolutePath()
			};
	
		performDirectCodeGen(testArgs, binDir);
		String metaString = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/SimpleService401/service_metadata.properties";
		File metadata = new File(metaString);

		assertTrue(metadata.exists());
		FileInputStream in = new FileInputStream(metadata);
		Properties pro = new Properties();
		pro.load(in);
		assertEquals(pro.getProperty("service_name"),"SimpleService401");
		assertEquals(pro.getProperty("service_namespace"),"http://www.ebayopensource.com/marketplace");
		in.close();
	

	}

	//admin name,service layer, version passed thru' gen type and interface properties file.
	//Test 6 - Validate that if service_intf_project.properties file is present and gentype options are used for passing values like,
	//the service layer,version,service name,admin name,namespace part,domain and interface class name, all the values must be copied to
	// metadta properties.
	@Test
	public void propertiesOfMetadataGotFromIntfPropsNotFromPassesOption() throws Exception{
		String testArgs[] =  new String[] {
				"-servicename", "SimpleService401", 
				"-interface","org.ebayopensource.test.soaframework.tools.codegen.Simple",
				"-genType", "ServiceMetadataProps",
				//"-src", destDir.getAbsolutePath(),
				"-dest",destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","INTERMEDIATE",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-adminname","gentypeadmin"
			};
		createPropsFile();
		performDirectCodeGen(testArgs, binDir);
		String str= destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
		File metadata = new File(str);

		assertTrue(metadata.exists());
		FileInputStream in = new FileInputStream(metadata);
		Properties pro = new Properties();
		pro.load(in);
		assertEquals("SimpleService401",pro.getProperty("service_name"));
		assertEquals("newadminname",pro.getProperty("admin_name"));
		assertEquals("COMMON",pro.getProperty("service_layer"));
		assertEquals("1.0.0",pro.getProperty("service_version"));
		assertEquals("com.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface",pro.getProperty("service_interface_class_name"));
		assertEquals("Billing",pro.getProperty("service_namespace_part"));
		assertEquals("ebayopen",pro.getProperty("domainName"));
		in.close();
		str= null;
		metadata =null;
		pro =null;

	}

	//admin name,service layer, version passed thru' interface properties file, not thru gentype .both inputs should be same as plugin passes them.a p4 bug for this?
	//Test 7 - Validate that if service_intf_project.properties file is present with properties
	//the service layer,version,service name,admin name,namespace part,domain and interface class name, metadata properties will have all the values copied..
	@Test
	public void propertiesOfMetadataGotFromIntfPropsEvenIfNotPassed() throws Exception{
		String testArgs[] =  new String[] {
				"-servicename", "SimpleService401",
				"-interface", "org.ebayopensource.test.soaframework.tools.codegen.Simple",
				"-genType", "ServiceMetadataProps",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-bin", binDir.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/marketplace/gentype",
				"-pr",prDir.getAbsolutePath(),
			};
		createPropsFile();
		performDirectCodeGen(testArgs, binDir);
		String str= destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
		File metadata = new File(str);

		assertTrue(metadata.exists());
		FileInputStream in = new FileInputStream(metadata);
		Properties pro = new Properties();
		pro.load(in);

		assertEquals("newadminname",pro.getProperty("admin_name"));
		assertEquals("COMMON",pro.getProperty("service_layer"));
		assertEquals("1.0.0",pro.getProperty("service_version"));
		assertEquals("http://www.ebayopensource.org/marketplace/gentype",pro.getProperty("service_namespace"));
		assertEquals("com.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface",pro.getProperty("service_interface_class_name"));
		assertEquals("Billing",pro.getProperty("service_namespace_part"));
		assertEquals("ebayopen",pro.getProperty("domainName"));
        in.close();


	}

	// diff sevice name & namespace given in gen type and wsdl.
	//Test 8 - Validate that namespace and service name should be got from wsdl while generating the metadata properties.Here values are
	//passed from option also.
	@Test
	public void testForSvcNameAndNamespaceInMetadataFromWsdl1() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		String testArgs[] =  new String[] {
				"-servicename", "SimpleService401",
				"-wsdl", wsdl.getAbsolutePath(),
				"-genType", "ServiceMetadataProps",
				"-namespace","http://www.ebayopensource.com/somenamespace",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath()

			};
		createPropsFile();
		performDirectCodeGen(testArgs, binDir);
		String str = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
		File metadata = new File(str);

		assertTrue(metadata.exists());
		FileInputStream in = new FileInputStream(metadata);
		Properties pro = new Properties();
		pro.load(in);
		in.close();
		assertEquals("AccountService",pro.getProperty("service_name"));
		assertEquals("http://www.ebayopensource.com/turmeric/services",pro.getProperty("service_namespace"));


	}

	// diff sevice name & namespace from wsdl only no thru gentype options.
	//Test 9 - Validate that namespace and service name should be got from wsdl while generating the metadata properties.Here naemspace is not 
	//passed from option.
	@Test
	public void testForSvcNameAndNamespaceInMetadataFromWsdl2() throws Exception{
		
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
		createPropsFile();
		performDirectCodeGen(testArgs, binDir);
		String str = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/newadminname/service_metadata.properties";
		File metadata = new File(str);

		assertTrue(metadata.exists());
		FileInputStream in = new FileInputStream(metadata);
		Properties pro = new Properties();
		pro.load(in);
		in.close();

		assertEquals("AccountService",pro.getProperty("service_name"));
		assertEquals("http://www.ebayopensource.com/turmeric/services",pro.getProperty("service_namespace"));


	}
	
	
		
		
		

		private void CreateInterfacePropsFile(Properties pro,String path) throws IOException{
		
			File file = new File(path+File.separator + "service_intf_project.properties");
			FileOutputStream out  =null;
			File destDir = new File(path);
			
			try{
					if(!destDir.exists())
						destDir.mkdir();
						
					if(!file.exists())
					file.createNewFile();
			       
			        out = new FileOutputStream(file);
			        pro.store(out,null);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
	        out.close();
		
			}
		}

	private  void createPropsFile() throws Exception{

		CreateInterfacePropsFile(pro,destDir.getAbsolutePath());

	}




private  void createImplPropsFile() throws IOException{
	FileInputStream in = null;
	FileOutputStream out = null;
	File file = new File(destDir.getAbsolutePath()+File.separator +IMPL_PROPERTIES);
	
	if(!destDir.exists())
		destDir.mkdir();
	if(!file.exists())
	try{	
		file.createNewFile();
		Properties pro = new Properties();
	
		in = new FileInputStream(file);
	    pro.load(in);
	    pro.setProperty("simp_version","1.1");
	    out  = new FileOutputStream(destDir.getAbsolutePath()+File.separator +IMPL_PROPERTIES);
	    pro.store(out,null);
	}catch(Exception e){
		e.printStackTrace();
	}finally{
    out.close();
    in.close(); }

}
//testing for service name updated in metadata as wsdl is updated



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
	return list;
}
@After
public void deinitialize(){

	 nsc= null;
	 pro=null;

	
}
}
