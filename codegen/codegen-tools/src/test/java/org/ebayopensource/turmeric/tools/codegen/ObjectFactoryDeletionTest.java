package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ObjectFactoryDeletionTest extends AbstractServiceGeneratorTestCase {

@Rule public TestName name = new TestName();
	
	File destDir = null;
	File binDir = null;
	final String INTF_PROPERTIES = "service_intf_project.properties";
	final String IMPL_PROPERTIES = "service_impl_project.properties";
	ServiceGenerator gen = null;
	File intfProperty = null;
	File implProperty = null;
	Properties intfProps = new Properties();
	Properties implProps = new Properties();
	
@Before
	
	public void init() throws Exception{
		
	testingdir.ensureEmpty();
	destDir = testingdir.getDir();
	binDir = testingdir.getFile("bin");
	intfProperty =	createPropertyFile(destDir.getAbsolutePath(), INTF_PROPERTIES);
		
	implProperty =	createPropertyFile(destDir.getAbsolutePath(), IMPL_PROPERTIES);
	
		

		//enter values to property file
		
		intfProps.put("sipp_version","1.1");
		intfProps.put("service_interface_class_name","org.ebayopensource.test.soaframework.tools.codegen.A");
		intfProps.put("service_layer","COMMON");
		intfProps.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\AccountService.wsdl");
		intfProps.put("service_version","1.0.0");
		intfProps.put("admin_name","AccountService");
		intfProps.put("service_namespace_part","billing");
		intfProps.put("domainName","Billing");
		
		
		
		implProps.put("useExternalServiceFactory", "true");
		
		
		fillProperties(implProps, implProperty);
		

		
	}	

/*
 * Test deletion of ObjectFactory.java and package-info.java in the common type namespace during 
 * service build.
 */

@Test
public void testObjectFactoryDeleteOnServiceBuild() throws Exception{

	fillProperties(intfProps, intfProperty);
	File path  = getCodegenQEDataFileInput("AccountService1.wsdl");
	
	String [] testArgs1 = {"-serviceName","AccountService",
			  "-genType","ServiceFromWSDLIntf",	
			  "-wsdl",path.getAbsolutePath(),
			  "-gip","com.ebay.test.soaframework.tools.codegen",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-slayer","INTERMEDIATE",
			  "-bin",binDir.getAbsolutePath(),
			  "-scv","1.0.0",
			  "-pr",destDir.getAbsolutePath()};
	
	performDirectCodeGen(testArgs1,binDir);
	
	codegenAssertFileNotExists(destDir.getAbsolutePath() +"/gen-src","org/ebayopensource/turmeric/common/v1/types/ObjectFactory.java"); 
	codegenAssertFileNotExists(destDir.getAbsolutePath()+ "/gen-src","org/ebayopensource/turmeric/common/v1/types/package-info.java");
	
	
	
	
}


/*
 * Test deletion of ObjectFactory.java and package-info.java in the common type namespace using prebuild gentype 
 * .
 */

@Test
public void testObjectFactoryDeleteOnServiceV3Build() throws Exception{

	fillProperties(intfProps, intfProperty);	
	File path  = getCodegenQEDataFileInput("AccountService1.wsdl");
	
	String [] testArgs1 = {"-serviceName","NewService",
			  "-genType","ClientNoConfig",	
			  "-wsdl",path.getAbsolutePath(),
			  "-gip","com.ebay.test.soaframework.tools.codegen",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-bin",binDir.getAbsolutePath(),
			  "-slayer","INTERMEDIATE",
			  "-scv","1.0.0",
			  "-pr",destDir.getAbsolutePath()};
	
	performDirectCodeGen(testArgs1,binDir);
	
	codegenAssertFileNotExists(destDir.getAbsolutePath()+ "/gen-src","org/ebayopensource/turmeric/common/v1/types/ObjectFactory.java"); 
	codegenAssertFileNotExists(destDir.getAbsolutePath()+ "/gen-src","org/ebayopensource/turmeric/common/v1/types/package-info.java");
	
	
	
	
}


/*
 * Test deletion of ObjectFactory.java and package-info.java in the common type namespace 
 * when ns2pkg points to common type namespace.
 */

@Test
public void testObjectFactoryDelete() throws Exception{
	
	
	intfProps.put("ns2pkg","http://www.ebayopensource.org/turmeric/blogs/v1/services|org.ebayopensource.turmeric.common.v1.types");
	fillProperties(intfProps, intfProperty);
	File path  = getCodegenQEDataFileInput("JunitEndTest.wsdl");
	
	String [] testArgs1 = {"-serviceName","NewService",
			  "-genType","ClientNoConfig",	
			  "-wsdl",path.getAbsolutePath(),
			  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-bin",binDir.getAbsolutePath(),
			  "-slayer","INTERMEDIATE",
			  "-scv","1.0.0",
			  "-pr",destDir.getAbsolutePath()};
	
	performDirectCodeGen(testArgs1,binDir);
	
	codegenAssertFileNotExists(destDir.getAbsolutePath()+ "/gen-src","org/ebayopensource/turmeric/common/v1/types/ObjectFactory.java"); 
	codegenAssertFileNotExists(destDir.getAbsolutePath()+"/gen-src","org/ebayopensource/turmeric/common/v1/types/package-info.java");
	
	
	
	
}


/*
 * Test deletion of ObjectFactory.java and package-info.java in the common namespace 
 * in single namespace wsdl pointing  to common namespace.
 */

@Test
public void testObjectFactoryDelete2() throws Exception{
	
	fillProperties(intfProps, intfProperty);

	File path  = getCodegenQEDataFileInput("AccountService2.wsdl");
	
	String [] testArgs1 = {"-serviceName","NewService",
			  "-genType","ServiceFromWSDLIntf",	
			  "-wsdl",path.getAbsolutePath(),
			  "-gip","org.ebayopensource.turmeric.common.v1.types",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-bin",binDir.getAbsolutePath(),
			  "-slayer","INTERMEDIATE",
			  "-scv","1.0.0",
			  "-pr",destDir.getAbsolutePath()};
	
	performDirectCodeGen(testArgs1,binDir);
	
	codegenAssertFileNotExists(destDir.getAbsolutePath() +"/gen-src","org/ebayopensource/turmeric/common/v1/types/ObjectFactory.java"); 
	codegenAssertFileNotExists(destDir.getAbsolutePath() +"/gen-src","org/ebayopensource/turmeric/common/v1/types/package-info.java");
	
	
	
	
}
	
	
}
