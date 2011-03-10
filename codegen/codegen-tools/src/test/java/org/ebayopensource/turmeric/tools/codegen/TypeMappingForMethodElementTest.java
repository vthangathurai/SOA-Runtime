package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.util.Properties;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class TypeMappingForMethodElementTest extends AbstractServiceGeneratorTestCase{

@Rule public TestName name = new TestName();
	
	File destDir = null;
	File binDir = null;
	final String INTF_PROPERTIES = "service_intf_project.properties";

	ServiceGenerator gen = null;
	
@Before
	
	public void init() throws Exception{
		
	testingdir.ensureEmpty();
	destDir = testingdir.getDir();
	binDir = testingdir.getFile("bin");
		File intfProperty = null;
	
			intfProperty =	createPropertyFile(destDir.getAbsolutePath(), INTF_PROPERTIES);
			


		//enter values to property file
		Properties intfProps = new Properties();
		intfProps.put("sipp_version","1.1");
		intfProps.put("service_interface_class_name","org.ebayopensource.test.soaframework.tools.codegen.CalcService");
		intfProps.put("service_layer","COMMON");
		intfProps.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\CalcService2.wsdl");
		intfProps.put("service_version","1.0.0");
		intfProps.put("admin_name","CalcService");
		intfProps.put("service_namespace_part","billing");
		intfProps.put("domainName","Billing");
		
		
		
		fillProperties(intfProps, intfProperty);
	
	}		

@Test
public void testTypeMappinsForMethodName() throws Exception{
	File path  = getCodegenQEDataFileInput("CalcService2.wsdl");
	
	
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
	
	File typeMappingFile = new File(getTypeMappingsFile(destDir.getAbsolutePath(),"CalcService"));
	
	Document typeDoc = XmlUtility.getXmlDoc(typeMappingFile.getAbsolutePath());
	NodeList nodeList =  typeDoc.getElementsByTagName("operation-list");
	NodeList childNodes = nodeList.item(0).getChildNodes();

	for(int i=0; i < childNodes.getLength();i++){
		
		if((childNodes.item(i).getNodeName().equals("operation"))){
			
			NamedNodeMap nodeMap = childNodes.item(i).getAttributes();
			String methodName = nodeMap.getNamedItem("methodName").getNodeValue();
			assertFirstLetterIsLowerCase(methodName);
			
		}
	}
}

@Test
public void testTypeMappinsForMethodName2() throws Exception{
	File path  = getCodegenQEDataFileInput("CalcService2.wsdl");
	
	
	String [] testArgs1 = {"-serviceName","NewService",
			  "-genType","TypeMappings",	
			  "-wsdl",path.getAbsolutePath(),
			  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-slayer","INTERMEDIATE",
			  "-bin",binDir.getAbsolutePath(),
			  "-scv","1.0.0",
			  "-pr",destDir.getAbsolutePath()};
	
	performDirectCodeGen(testArgs1,binDir);
	
	File typeMappingFile = new File(getTypeMappingsFile(destDir.getAbsolutePath(),"CalcService"));
	
	Document typeDoc = XmlUtility.getXmlDoc(typeMappingFile.getAbsolutePath());
	NodeList nodeList =  typeDoc.getElementsByTagName("operation-list");
	NodeList childNodes = nodeList.item(0).getChildNodes();

	for(int i=0; i < childNodes.getLength();i++){
		
		if((childNodes.item(i).getNodeName().equals("operation"))){
			
			NamedNodeMap nodeMap = childNodes.item(i).getAttributes();
			String methodName = nodeMap.getNamedItem("methodName").getNodeValue();
			assertFirstLetterIsLowerCase(methodName);
			
		}
	}
}


@Test
public void testTypeMappinsForMethodName3() throws Exception{
	File path  = getCodegenQEDataFileInput("CalcService1.wsdl");
	
	
	String [] testArgs1 = {"-serviceName","NewService",
			  "-genType","ServiceFromWSDLIntf",	
			  "-wsdl",path.getAbsolutePath(),
			  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-bin",binDir.getAbsolutePath(),
			  "-slayer","INTERMEDIATE",
			  "-scv","1.0.0",
			  "-pr",destDir.getAbsolutePath()};
	
	performDirectCodeGen(testArgs1,binDir);
	
	String [] testArgs2 = {"-serviceName","NewService",
			  "-genType","Dispatcher",	
			  "-interface","org.ebayopensource.test.soaframework.tools.codegen.CalcService",
			  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.CalcServiceImpl",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-bin",binDir.getAbsolutePath(),
			  "-slayer","INTERMEDIATE",
			  "-scv","1.0.0",
			  "-pr",destDir.getAbsolutePath()};
	
	performDirectCodeGen(testArgs2,binDir);
	
	File typeMappingFile = new File(getTypeMappingsFile(destDir.getAbsolutePath(),"CalcService"));
	
	Document typeDoc = XmlUtility.getXmlDoc(typeMappingFile.getAbsolutePath());
	NodeList nodeList =  typeDoc.getElementsByTagName("operation-list");
	NodeList childNodes = nodeList.item(0).getChildNodes();

	for(int i=0; i < childNodes.getLength();i++){
		
		if((childNodes.item(i).getNodeName().equals("operation"))){
			
			NamedNodeMap nodeMap = childNodes.item(i).getAttributes();
			String methodName = nodeMap.getNamedItem("methodName").getNodeValue();
			assertFirstLetterIsLowerCase(methodName);
			
		}
	}
}

protected void assertFirstLetterIsLowerCase(String value){
	char ch = value.charAt(0);
	String  firstLetter =Character.toString(ch);
	Assert.assertTrue(Pattern.matches("[a-z]",firstLetter));
	
	
	
}

public String getTypeMappingsFile(String destDir,String serviceName){
	 
	 return destDir + File.separator +"gen-meta-src/META-INF/soa/common/config/"+serviceName+"/TypeMappings.xml";
	 
}
	
}
