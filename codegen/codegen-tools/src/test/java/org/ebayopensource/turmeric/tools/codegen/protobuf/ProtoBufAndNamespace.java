package org.ebayopensource.turmeric.tools.codegen.protobuf;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import junit.framework.Assert;

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ProtoBufAndNamespace extends AbstractServiceGeneratorTestCase {

	@Rule public TestName name = new TestName();	

	String destDir = null;
	Properties libProps = new Properties();
	final String INTF_PROPERTIES = "service_intf_project.properties";
	File libProperty = null;
	@Before
	public void init() throws Exception{
			
			
			
			destDir = getTestDestDir().getAbsolutePath();
			
			
			testingdir.ensureEmpty();
			try {
				libProperty =	createPropertyFile(destDir,INTF_PROPERTIES);
				
		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			
			libProps.put("nonXSDFormats","protobuf");
			libProps.put("sipp_version","1.1");
			
		}	
	
	@Test
	public void testWithMultiNS() throws Exception{
		libProps.put("enableNamespaceFolding","true");
		fillProperties(libProps,libProperty);
		File wsdlFile = new File("Vanilla-Codegen/ServiceInputFiles/FindingService.wsdl");
		
		String [] testArgs = {"-serviceName","FindingService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","com.ebay.marketplace.shipping.v1.services",
				  "-adminname","FindingService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		
		
		try{
			performDirectCodeGen(testArgs);}
		catch(Exception e){
			Assert.assertTrue("Multiple namespace is supported by protobuf",e.getMessage().contains("Multiple Namespace wsdl is not supported"));
		}
			
	}
	
	@Test
	public void testWithMultiNSAndNSFfalse() throws Exception{
		libProps.put("enableNamespaceFolding","false");
		fillProperties(libProps,libProperty);
		File wsdlFile = new File("Vanilla-Codegen/ServiceInputFiles/FindingService.wsdl");
		
		String [] testArgs = {"-serviceName","FindingService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","com.ebay.marketplace.shipping.v1.services",
				  "-adminname","FindingService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		
		
		try{
			performDirectCodeGen(testArgs);}
		catch(Exception e){
			Assert.assertTrue("Multiple namespace is supported by protobuf",e.getMessage().contains("Multiple Namespace wsdl is not supported"));
		}
			
	}
	
	
	
	@Test
	public void testWithSingleNSAndNSFfalse() throws Exception{
		libProps.put("enableNamespaceFolding","false");
		fillProperties(libProps,libProperty);
		File wsdlFile = new File("wsdlorxsd/TestWsdlComplexType.wsdl");
		
		String [] testArgs = {"-serviceName","FindingService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","com.ebay.marketplace.shipping.v1.services",
				  "-adminname","FindingService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		
		
		try{
			performDirectCodeGen(testArgs);}
		catch(Exception e){
			e.printStackTrace();
			Assert.assertTrue("Single namespace is not supported by protobuf",false);
		}
			
	}
	
	
	@Test
	public void testWithSingleNSAndNSFtrue() throws Exception{
		libProps.put("enableNamespaceFolding","false");
		fillProperties(libProps,libProperty);
		File wsdlFile = new File("wsdlorxsd/TestWsdlComplexType.wsdl");
		
		String [] testArgs = {"-serviceName","FindingService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","com.ebay.marketplace.shipping.v1.services",
				  "-adminname","FindingService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		
		
		try{
			performDirectCodeGen(testArgs);}
		catch(Exception e){
			e.printStackTrace();
			Assert.assertTrue("Single namespace is not supported by protobuf",false);
		}
			
	}
	
	
	
	
	
	
}
