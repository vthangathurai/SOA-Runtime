package org.ebayopensource.turmeric.tools.codegen.protobuf;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.ebay.qa.junit.two.eight.AbstractTestCase;
import com.ebay.soaframework.tools.codegen.ServiceGenerator;
import com.ebay.soaframework.tools.codegen.fastserformat.validator.FastSerFormatNotSupportedException;

public class TestTypeLibraryProtobuf extends AbstractServiceGeneratorTestCase {
	

String destDir = null;
Properties libProps = new Properties();
final String LIB_PROPERTIES = "service_intf_project.properties";
File libProperty = null;
@Before
public void init() throws Exception{

		
		destDir = getTestingDir(this.getClass().getSimpleName()+"."+name.getMethodName());
		
		
		CodeGenUtil.deleteContentsOfDir(new File(destDir));
		try {
			libProperty =	createPropertyFile(destDir,LIB_PROPERTIES);
			
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		libProps.put("nonXSDFormats","protobuf");
		//libProps.put("TYPE_LIBRARY_VERSION","1.0.0");
		//libProps.put("TYPE_LIBRARY_NAMESPACE","http://www.ebay.com/soaframework/examples/config");
		//libProps.put("TYPE_LIBRARY_NAME","TestTypeLibrary");
		
	}
	@Test(expected=FastSerFormatNotSupportedException.class)
	public void testCase1() throws Exception{
		
		File xsd1 = new File("wsdlorxsd/TestXsd.xsd");
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		

		
		String [] testArgs = {
				  "-servicename","ShippingService",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-genType","ValidateXSDsForNonXSDFormats",
				  "-xsdPathsForValidation","xsd/TestXsd.xsd",
				  "-nonXSDFormats","protobuf"
				  
				 };
		
		performDirectCodeGen(testArgs);
	}
	
	@Test(expected=FastSerFormatNotSupportedException.class)
	public void testCase2() throws Exception{
		
		fillProperties(libProps, libProperty);
		File xsd1 = new File("wsdlorxsd/TestXsd.xsd");
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		

		
		String [] testArgs = {
				  "-servicename","ShippingService",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-genType","ValidateXSDsForNonXSDFormats",
				  "-xsdPathsForValidation","xsd/TestXsd.xsd",
				  "-pr",destDir,
				  "-dest",destDir
				  
				 };
		
		performDirectCodeGen(testArgs);
		
		
	}	
	
	
	@Test(expected=FastSerFormatNotSupportedException.class)
	public void testCase3() throws Exception{
		
		File xsd1 = new File("wsdlorxsd/TestXsd.xsd");
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		

		
		String [] testArgs = {
				  "-servicename","ShippingService",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-genType","ValidateXSDsForNonXSDFormats",
				  "-xsdPathsForValidation","xsd/TestXsd.xsd,xsd/TestXsd1.xsd",
				  "-nonXSDFormats","protobuf"
				  
				 };
		
		performDirectCodeGen(testArgs); 
	
		
	}
	
	
	@Test(expected=FastSerFormatNotSupportedException.class)
	public void testCase4() throws Exception{
		
		File xsd1 = new File("wsdlorxsd/TestXsd.xsd");
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		

		
		String [] testArgs = {
				  "-servicename","ShippingService",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-genType","ValidateXSDsForNonXSDFormats",
				  "-xsdPathsForValidation","xsd/TestXsd2.xsd",
				  "-nonXSDFormats","protobuf"
				  
				 };
		
		performDirectCodeGen(testArgs);
		
		
		
	}
	
	@Test(expected=FastSerFormatNotSupportedException.class)
	public void testCase5() throws Exception{
		
		File xsd1 = new File("wsdlorxsd/TestXsd.xsd");
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		

		
		String [] testArgs = {
				  "-servicename","ShippingService",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-genType","ValidateXSDsForNonXSDFormats",
				  "-xsdPathsForValidation","xsd/TestXsd3.xsd",
				  "-nonXSDFormats","protobuf"
				  
				 };
		
		performDirectCodeGen(testArgs);
		
		
	}
	
	@Test
	public void testCase6() throws Exception{
		
		File xsd1 = new File("wsdlorxsd/TestXsd.xsd");
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		

		
		String [] testArgs = {
				  "-servicename","ShippingService",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-genType","ValidateXSDsForNonXSDFormats",
				  "-xsdPathsForValidation","xsd/SupportedXsd.xsd",
				  "-nonXSDFormats","protobuf"
				  
				 };
		
		performDirectCodeGen(testArgs);
		
	}
	
	@Test
	public void testCase7() throws Exception{
		
		File xsd1 = new File("wsdlorxsd/SupportedXsd.xsd");
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		

		
		String [] testArgs = {
				  "-servicename","ShippingService",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-genType","ValidateXSDsForNonXSDFormats",
				  "-xsdPathsForValidation",xsd1.getAbsolutePath(),
				  "-nonXSDFormats","protobuf"
				  
				 };
		
		performDirectCodeGen(testArgs);
		
	}
	
	@Test
	public void testCase8() throws Exception{
		
		//SOATestUserTypeLibrary.jar
		File xsd1 = new File("wsdlorxsd");
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		

		
		String [] testArgs = {
				  "-servicename","ShippingService",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-genType","ValidateXSDsForNonXSDFormats",
				  "-xsdPathsForValidation","jar:file:\\"+ xsd1.getAbsolutePath() +"/SOATestUserTypeLibrary.jar!/types/RegistrationInfoType.xsd,jar:file:\\"+ xsd1.getAbsolutePath() +"/SOATestUserTypeLibrary.jar!/types/UserInfoType.xsd",
				  "-nonXSDFormats","protobuf"
				  
				 };
		
		performDirectCodeGen(testArgs);
		
	}
	
	
	@Test
	public void testCase9() throws Exception{
		
	
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		

		
		String [] testArgs = {
				  "-servicename","ShippingService",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-genType","ValidateXSDsForNonXSDFormats",
				  "-xsdPathsForValidation","types/RegistrationInfoType.xsd,types/UserInfoType.xsd",
				  "-nonXSDFormats","protobuf"
				  
				 };
		
		performDirectCodeGen(testArgs);
		
	}
	
	// URL url = new URL("jar:file://+xsd1.getAbsolutePath() +"/SOATestUserTypeLibrary.jar!/UserInfoType.xsd);
	//RegistrationInfoType
	//jar:file://+xsd1.getAbsolutePath() +"/SOATestUserTypeLibrary.jar!/RegistrationInfoType.xsd
	
}
