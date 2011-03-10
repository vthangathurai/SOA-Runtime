package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class CommonTypenNSTMTest extends AbstractServiceGeneratorTestCase{
@Rule public TestName name = new TestName();
	
	File destDir = null;
	File binDir = null;
	final String INTF_PROPERTIES = "service_intf_project.properties";
	final String IMPL_PROPERTIES = "service_impl_project.properties";
	ServiceGenerator gen = null;
	Properties intfProps = new Properties();
	File intfProperty = null;
	
@Before
	
	public void init() throws Exception{
		
	testingdir.ensureEmpty();
	destDir = testingdir.getDir();
	binDir = testingdir.getFile("bin");
			intfProperty =	createPropertyFile(destDir.getAbsolutePath(), INTF_PROPERTIES);
			
		
	

		//enter values to property file
		
		intfProps.put("sipp_version","1.1");
		intfProps.put("service_interface_class_name","org.ebayopensource.test.soaframework.tools.codegen.AdcommerceConfigGroupMarketV2");
		intfProps.put("service_layer","COMMON");
		intfProps.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\AdcommerceConfigGroupMarketV2.wsdl");
		intfProps.put("service_version","1.0.0");
		intfProps.put("admin_name","AdcommerceConfigGroupMarketV2");
		intfProps.put("service_namespace_part","billing");
		intfProps.put("domainName","Billing");
		intfProps.put("enabledNamespaceFolding","true");
		
		fillProperties(intfProps, intfProperty);


		
	}	

/*
 * Single namespace wsdl with namespace folding = true. The Error type to have wsdl namespace.
 */

@Test
public void testTypeMappingCommonNamespace() throws Exception{

	File path  = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV2.wsdl");
	
	String [] testArgs1 = {"-serviceName","ConfigGroupMarket",
			  "-genType","ServiceFromWSDLIntf",	
			  "-wsdl",path.getAbsolutePath(),
			  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-slayer","INTERMEDIATE",
			  "-scv","1.0.0",
			  "-bin",binDir.getAbsolutePath(),
			  "-pr",destDir.getAbsolutePath()};
	
	performDirectCodeGen(testArgs1,binDir);
	
	File typeMappingFile = new File(getTypeMappingsFile(destDir.getAbsolutePath(),"AdcommerceConfigGroupMarketV2"));
	
	Document typeDoc = XmlUtility.getXmlDoc(typeMappingFile.getAbsolutePath());
	
	NodeList nodeList =  typeDoc.getElementsByTagName("xml-element-name");
	
	for(int i =0; i < nodeList.getLength();i++){
		Assert.assertTrue(nodeList.item(i).getFirstChild().getNodeValue().contains("{http://www.ebayopensource.com/marketplace/services}"));
	}
		
	
	
		
	
}
/*
 * Single namespace wsdl with namespace folding = false. The Error type to have wsdl namespace.
 */


@Test
public void testTypeMappingNSFoldingFalse() throws Exception{
	
	intfProps.put("enabledNamespaceFolding","false");
	
	fillProperties(intfProps, intfProperty);

	File path  = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV2.wsdl");

	String [] testArgs1 = {"-serviceName","ConfigGroupMarket",
			  "-genType","ServiceFromWSDLIntf",	
			  "-wsdl",path.getAbsolutePath(),
			  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-slayer","INTERMEDIATE",
			  "-bin",binDir.getAbsolutePath(),
			  "-scv","1.0.0",
			  "-pr",destDir.getAbsolutePath()};
	
	performDirectCodeGen(testArgs1,binDir);
	
	File typeMappingFile = new File(getTypeMappingsFile(destDir.getAbsolutePath(),"AdcommerceConfigGroupMarketV2"));
	
	Document typeDoc = XmlUtility.getXmlDoc(typeMappingFile.getAbsolutePath());
	
	NodeList nodeList =  typeDoc.getElementsByTagName("error-message");
	
	for(int i =0; i < nodeList.getLength();i++){
		NodeList childNodes = nodeList.item(i).getChildNodes();
		for(int j= 0;j < childNodes.getLength();j++) {
			
			if(childNodes.item(j).getNodeName().equals("xml-element-name")){
				Assert.assertTrue(childNodes.item(j).getFirstChild().getNodeValue().contains("{http://www.ebayopensource.org/turmeric/common/v1/types}"));
			}
			
		}
	}
	
}

/*
 * multi namespace wsdl with namespace folding = false. The Error type to have wsdl namespace.
 */
	
	@Test
	public void testTypeMappingNSFoldingFalseForMnsWSDL() throws Exception{
		
		intfProps.put("enabledNamespaceFolding","false");
		
		fillProperties(intfProps, intfProperty);

		File path  = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String [] testArgs1 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path.getAbsolutePath(),
				  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
		
		File typeMappingFile = new File(getTypeMappingsFile(destDir.getAbsolutePath(),"AdcommerceConfigGroupMarketV2"));
		
		Document typeDoc = XmlUtility.getXmlDoc(typeMappingFile.getAbsolutePath());
		
		NodeList nodeList =  typeDoc.getElementsByTagName("error-message");
		
		for(int i =0; i < nodeList.getLength();i++){
			
			NodeList childNodes = nodeList.item(i).getChildNodes();
			for(int j= 0;j < childNodes.getLength();j++) {
				
				if(childNodes.item(j).getNodeName().equals("xml-element-name")){
					Assert.assertTrue(childNodes.item(j).getFirstChild().getNodeValue().contains("{http://www.ebayopensource.org/turmeric/common/v1/types}"));
				}
				
			}
			
		}
		
	
	
	
		
	
	}
	
	
	@Test
	public void testMnsWsdlWithFault() throws Exception{
		
		intfProps.put("enabledNamespaceFolding","false");
		
		fillProperties(intfProps, intfProperty);

		File path  = getCodegenQEDataFileInput("AccountService1.wsdl");
		
		String [] testArgs1 = {"-serviceName","NewService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path.getAbsolutePath(),
				  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
	}
	
	@Test
	public void testSingleNSWsdlWithFault() throws Exception{
		
		intfProps.put("enabledNamespaceFolding","true");
		
		fillProperties(intfProps, intfProperty);

		File path  = getCodegenQEDataFileInput("BlogsServiceV1.wsdl");
		
		String [] testArgs1 = {"-serviceName","NewService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path.getAbsolutePath(),
				  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-bin",binDir.getAbsolutePath(),
				  "-scv","1.0.0",
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
	}
	
	public String getTypeMappingsFile(String destDir,String serviceName){
		 
		 return destDir + File.separator +"gen-meta-src/META-INF/soa/common/config/"+serviceName+"/TypeMappings.xml";
		 
	 }
	 



}
