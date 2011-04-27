package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ServiceFactoryImplTest extends AbstractServiceGeneratorTestCase {
	
	@Rule public TestName name = new TestName();
	
	File destDir = null;
	File binDir = null;
	final String INTF_PROPERTIES = "service_intf_project.properties";
	final String IMPL_PROPERTIES = "service_impl_project.properties";

	ServiceGenerator gen = null;
	
	Properties implProps = new Properties();
	File intfProperty = null;
	File implProperty = null;
	File metaDataProps = null;
	Properties intfProps = null;
	
	Properties metaData = null;
	
	@Before
	
	public void init() throws Exception{
		
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		intfProperty =	createPropertyFile(destDir.getAbsolutePath(), INTF_PROPERTIES);
			
			
	

		//enter values to property file
		intfProps = new Properties();
		intfProps.put("sipp_version","1.1");
		intfProps.put("service_interface_class_name","org.ebayopensource.test.soaframework.tools.codegen.AccountService");
		intfProps.put("service_layer","COMMON");
		intfProps.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\AccountService.wsdl");
		intfProps.put("service_version","1.0.0");
		intfProps.put("admin_name","AccountService");
		intfProps.put("interface_source_type","wsdl");
		intfProps.put("service_namespace_part","billing");
		intfProps.put("domainName","Billing");
		
		metaData = new Properties();
		metaData.put("interface_source_type","interface");
		
		fillProperties(intfProps, intfProperty);
		
		

		
	}
	
	/*
	 * Test current functionality where dispatcher package is derived from the impl package
	 * present in serviceconfig.xml
	 */
	@Test
	public void testCurrentFunctionality() throws Exception{
		
		implProperty =	createPropertyFile(destDir.getAbsolutePath(), IMPL_PROPERTIES);
		File path  = getCodegenQEDataFileInput("AccountService.wsdl");
		
		
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
		
		File bin = new File(destDir,"bin");
		
		String [] testArgs2 = {"-serviceName","NewService",
				  "-genType","ServiceFromWSDLImpl",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-bin",binDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",bin.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
		performDirectCodeGen(testArgs2,binDir);
		
		
		codegenAssertFileExists(destDir.getAbsolutePath(),"gen-src/service/org/ebayopensource/test/soaframework/tools/codegen/impl/gen/AccountServiceRequestDispatcher.java");
		
		
	}
	
	
	@Test
	public void testCurrentFunctionalityWithProperty() throws Exception{
		
		implProperty =	createPropertyFile(destDir.getAbsolutePath(), IMPL_PROPERTIES);
		implProps.put("useExternalServiceFactory", "false");
		fillProperties(implProps, implProperty);
		File path  = getCodegenQEDataFileInput("AccountService.wsdl");
		
		
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
		
		
		
		String [] testArgs2 = {"-serviceName","NewService",
				  "-genType","ServiceFromWSDLImpl",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
		performDirectCodeGen(testArgs2,binDir);
		
		
		codegenAssertFileExists(destDir.getAbsolutePath(),"gen-src/service/org/ebayopensource/test/soaframework/tools/codegen/impl/gen/AccountServiceRequestDispatcher.java");
		

		
	}
	
	
	/*
	 * Test functionality where developer has put property "useServiceFactoryImpl = true" and has replaced the impl class tag and
	 * replaced with factory class tag.
	 */
	@Test
	public void testCase1() throws Exception{
		
		implProperty =	createPropertyFile(destDir.getAbsolutePath(), IMPL_PROPERTIES);
		implProps.put("useExternalServiceFactory", "TruE ");
		fillProperties(implProps, implProperty);
		
		
		File path  = getCodegenQEDataFileInput("AccountService.wsdl");
		
		
		String [] testArgs1 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path.getAbsolutePath(),
				  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-bin",binDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir.getAbsolutePath()};
		
	
		
		String [] testArgs2 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLImpl",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		
		String [] testArgs4 = {"-serviceName","AccountService",
				  "-genType","ServerConfig",	
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
		performDirectCodeGen(testArgs2,binDir);
		performDirectCodeGen(testArgs4,binDir);
		
		
		
		XmlUtility.removeElementFromXml(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"), "service-impl-class-name");
		XmlUtility.addElementToXml(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"), "service-impl-factory","org.ebayopensource.factory.Factory");
		
		ensureClean(new File(destDir.getAbsolutePath(),"gen-src/service").getAbsolutePath());
		
		//XmlUtility.addElementToXml(getServiceConfigFile(destDir,"AccountService"),"service-impl-factory","org.ebayopensource.factory.Factory");
		
		String [] testArgs3 = {"-serviceName","AccountService",
				  "-genType","dispatcher",	
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs3,binDir);
		codegenAssertFileExists(destDir.getAbsolutePath(),"gen-src/service/org/ebayopensource/test/soaframework/tools/codegen/gen/AccountServiceRequestDispatcher.java");
		
		
	}
	
	
	/*
	 * Test functionality where developer has put property "useServiceFactoryImpl = true" and has added factory class tag and not removed the
	 * impl class tag.
	 */
	@Test
	@Ignore("case handled at runtime")
	public void testCase2() throws Exception{
		
		implProperty =	createPropertyFile(destDir.getAbsolutePath(), IMPL_PROPERTIES);
		implProps.put("useExternalServiceFactory", "  true   ");
		fillProperties(implProps, implProperty);
	
		File path  = getCodegenQEDataFileInput("AccountService.wsdl");
		
		
		String [] testArgs1 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path.getAbsolutePath(),
				  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-bin",binDir.getAbsolutePath(),
				  "-scv","1.0.0",
				  "-pr",destDir.getAbsolutePath()};
		
		
		String [] testArgs2 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLImpl",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		
		String [] testArgs4 = {"-serviceName","AccountService",
				  "-genType","ServerConfig",	
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
		performDirectCodeGen(testArgs2,binDir);
		performDirectCodeGen(testArgs4,binDir);
		
		
		
		
		XmlUtility.addElementToXml(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"), "service-impl-factory","org.ebayopensource.factory.Factory");
		
		ensureClean(new File(destDir.getAbsolutePath(),"gen-src/service").getAbsolutePath());
		

		
		String [] testArgs3 = {"-serviceName","AccountService",
				  "-genType","dispatcher",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs3,binDir);
		codegenAssertFileExists(destDir.getAbsolutePath(),"gen-src/service/org/ebayopensource/test/soaframework/tools/codegen/gen/AccountServiceRequestDispatcher.java");
		
	}
	/*
	 * negative case where property useExternalServiceFactory=false and only factory class is present in service config.xml
	 */
	
	@Test
	public void testCase3() throws Exception{
		
		implProperty =	createPropertyFile(destDir.getAbsolutePath(), IMPL_PROPERTIES);
		implProps.put("useExternalServiceFactory", "false");
		fillProperties(implProps, implProperty);
		
		
		File path  = getCodegenQEDataFileInput("AccountService.wsdl");
		
		
		String [] testArgs1 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path.getAbsolutePath(),
				  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-bin",binDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir.getAbsolutePath()};
		
	
		String [] testArgs2 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLImpl",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		
		String [] testArgs4 = {"-serviceName","AccountService",
				  "-genType","ServerConfig",	
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
		performDirectCodeGen(testArgs2,binDir);
		performDirectCodeGen(testArgs4,binDir);
		
		
		
		XmlUtility.removeElementFromXml(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"), "service-impl-class-name");
		XmlUtility.addElementToXml(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"), "service-impl-factory","org.ebayopensource.factory.Factory");
		
		ensureClean(new File(destDir.getAbsolutePath(),"gen-src/service").getAbsolutePath());
		
		//XmlUtility.addElementToXml(getServiceConfigFile(destDir.getAbsolutePath(),"AccountService"),"service-impl-factory","org.ebayopensource.factory.Factory");
		
		String [] testArgs3 = {"-serviceName","AccountService",
				  "-genType","dispatcher",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs3,binDir);
		codegenAssertFileExists(destDir.getAbsolutePath(),"gen-src/service/org/ebayopensource/test/soaframework/tools/codegen/impl/gen/AccountServiceRequestDispatcher.java");
		
		
	}
	
	/*
	 * negative case where property useExternalServiceFactory=true and only factory class is present in service config.xml
	 */
	
	@Test
	public void testCase4() throws Exception{
		
		implProperty =	createPropertyFile(destDir.getAbsolutePath(), IMPL_PROPERTIES);
		implProps.put("useExternalServiceFactory", "true");
		fillProperties(implProps, implProperty);
		
		
		File path  = getCodegenQEDataFileInput("AccountService.wsdl");
		
		
		String [] testArgs1 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path.getAbsolutePath(),
				  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-bin",binDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir.getAbsolutePath()};
		
	
		
		String [] testArgs2 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLImpl",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		
		String [] testArgs4 = {"-serviceName","AccountService",
				  "-genType","ServerConfig",	
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
		performDirectCodeGen(testArgs2,binDir);
		performDirectCodeGen(testArgs4,binDir);
		
		
	
		
		ensureClean(new File(destDir.getAbsolutePath(),"gen-src/service").getAbsolutePath());
		
		
		String [] testArgs3 = {"-serviceName","AccountService",
				  "-genType","dispatcher",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs3,binDir);
		codegenAssertFileExists(destDir.getAbsolutePath(),"gen-src/service/org/ebayopensource/test/soaframework/tools/codegen/gen/AccountServiceRequestDispatcher.java");
		
		
	}
	
	@Test
	@Ignore("handled the case in runtime")
	public void testCase5() throws Exception{
		
		implProperty =	createPropertyFile(destDir.getAbsolutePath(), IMPL_PROPERTIES);
		implProps.put("useExternalServiceFactory", "  false   ");
		fillProperties(implProps, implProperty);
	
		File path  = getCodegenQEDataFileInput("AccountService.wsdl");
		
		
		String [] testArgs1 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path.getAbsolutePath(),
				  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-bin",binDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir.getAbsolutePath()};
		
	
		
		String [] testArgs2 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLImpl",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		
		String [] testArgs4 = {"-serviceName","AccountService",
				  "-genType","ServerConfig",	
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
		performDirectCodeGen(testArgs2,binDir);
		performDirectCodeGen(testArgs4,binDir);
		
		
		
		
		XmlUtility.addElementToXml(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"), "service-impl-factory","org.ebayopensource.factory.Factory");
		
		ensureClean(new File(destDir.getAbsolutePath(),"gen-src/service").getAbsolutePath());
		

		
		String [] testArgs3 = {"-serviceName","AccountService",
				  "-genType","dispatcher",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs3,binDir);
		codegenAssertFileExists(destDir.getAbsolutePath(),"gen-src/service/org/ebayopensource/test/soaframework/tools/codegen/gen/AccountServiceRequestDispatcher.java");
		
	}
	
	/*
	 * negative case where property useExternalServiceFactory= [value other than true or false] and only factory class is present in service config.xml
	 */
	
	
	@Test
	public void testCase6() throws Exception{
		
		implProperty =	createPropertyFile(destDir.getAbsolutePath(), IMPL_PROPERTIES);
		implProps.put("useExternalServiceFactory", "sadsa");
		fillProperties(implProps, implProperty);
		
		
		File path  = getCodegenQEDataFileInput("AccountService.wsdl");
		
		
		String [] testArgs1 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path.getAbsolutePath(),
				  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-bin",binDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir.getAbsolutePath()};
		
		
		
		String [] testArgs2 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLImpl",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		
		String [] testArgs4 = {"-serviceName","AccountService",
				  "-genType","ServerConfig",	
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
		performDirectCodeGen(testArgs2,binDir);
		performDirectCodeGen(testArgs4,binDir);
		
		
		
		XmlUtility.removeElementFromXml(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"), "service-impl-class-name");
		XmlUtility.addElementToXml(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"), "service-impl-factory","org.ebayopensource.factory.Factory");
		
		ensureClean(new File(destDir.getAbsolutePath(),"gen-src/service").getAbsolutePath());
		
		//XmlUtility.addElementToXml(getServiceConfigFile(destDir.getAbsolutePath(),"AccountService"),"service-impl-factory","org.ebayopensource.factory.Factory");
		
		String [] testArgs3 = {"-serviceName","AccountService",
				  "-genType","dispatcher",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs3,binDir);
		codegenAssertFileExists(destDir.getAbsolutePath(),"gen-src/service/org/ebayopensource/test/soaframework/tools/codegen/impl/gen/AccountServiceRequestDispatcher.java");
		
		
	}
	/*
	 * case where property useExternalServiceFactory=true and only factory class is present in service config.xml
	 * for dispatcher pre build.
	 */
	
	@Test
	public void testCase7() throws Exception{
		
		implProperty =	createPropertyFile(destDir.getAbsolutePath(), IMPL_PROPERTIES);
		implProps.put("useExternalServiceFactory", "TruE ");
		fillProperties(implProps, implProperty);
		
		fillProperties(intfProps, intfProperty);
		File path  = getCodegenQEDataFileInput("AccountService1.wsdl");
		
		
		String [] testArgs1 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path.getAbsolutePath(),
				  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-bin",binDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir.getAbsolutePath()};
		
		
		
		String [] testArgs2 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLImpl",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		
		String [] testArgs4 = {"-serviceName","AccountService",
				  "-genType","ServerConfig",	
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
		performDirectCodeGen(testArgs2,binDir);
		performDirectCodeGen(testArgs4,binDir);
		
		
		
		XmlUtility.removeElementFromXml(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"), "service-impl-class-name");
		XmlUtility.addElementToXml(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"), "service-impl-factory","org.ebayopensource.factory.Factory");
		
		ensureClean(new File(destDir.getAbsolutePath(),"gen-src/service").getAbsolutePath());
		
		//XmlUtility.addElementToXml(getServiceConfigFile(destDir.getAbsolutePath(),"AccountService"),"service-impl-factory","org.ebayopensource.factory.Factory");
		
		String [] testArgs3 = {"-serviceName","AccountService",
				  "-genType","DispatcherForBuild",	
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs3,binDir);
		codegenAssertFileExists(destDir.getAbsolutePath(),"gen-src/service/org/ebayopensource/test/soaframework/tools/codegen/gen/AccountServiceRequestDispatcher.java");
		
		
	}
	
	/*
	 * case where property useExternalServiceFactory=true and only factory class is present in service config.xml
	 * for dispatcher maven pre build.
	 */
	@Test
	public void testCase8() throws Exception{
		
		implProperty =	createPropertyFile(destDir.getAbsolutePath(), IMPL_PROPERTIES);
		implProps.put("useExternalServiceFactory", "TruE ");
		fillProperties(implProps, implProperty);
		
		fillProperties(intfProps, intfProperty);
		File path  = getCodegenQEDataFileInput("AccountService1.wsdl");
		
		
		String [] testArgs1 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path.getAbsolutePath(),
				  "-gip","org.ebayopensource.test.soaframework.tools.codegen",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-bin",binDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir.getAbsolutePath()};
		
	
		
		String [] testArgs2 = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLImpl",	
				  "-wsdl",path.getAbsolutePath(),
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		
		String [] testArgs4 = {"-serviceName","AccountService",
				  "-genType","ServerConfig",	
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-bin",binDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs1,binDir);
		performDirectCodeGen(testArgs2,binDir);
		performDirectCodeGen(testArgs4,binDir);
		
		
		
		XmlUtility.removeElementFromXml(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"), "service-impl-class-name");
		XmlUtility.addElementToXml(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"), "service-impl-factory","org.ebayopensource.factory.Factory");
		
		ensureClean(new File(destDir.getAbsolutePath(),"gen-src/service").getAbsolutePath());
		
		//XmlUtility.addElementToXml(getServiceConfigFile(destDir.getAbsolutePath(),"AccountService"),"service-impl-factory","org.ebayopensource.factory.Factory");
		
		String [] testArgs3 = {"-serviceName","AccountService",
				  "-genType","DispatcherForMaven",	
				  "-interface","org.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","org.ebayopensource.test.soaframework.tools.codegen.impl.AccountServiceImpl",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-jdest",destDir.getAbsolutePath() + "/gen-src/service",
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-bin",binDir.getAbsolutePath(),
				  "-pr",destDir.getAbsolutePath()};
		
		performDirectCodeGen(testArgs3,binDir);
		codegenAssertFileExists(destDir.getAbsolutePath(),"gen-src/service/org/ebayopensource/test/soaframework/tools/codegen/gen/AccountServiceRequestDispatcher.java");
		
		
	}
	
	public void codegenAssertFileExists(String destDir,String path){
		 
		 File file = new File(destDir+File.separator+path);
		 Assert.assertTrue("file " + path+ "does not exist in directory" + destDir, file.exists());
	 }
	
	public String getServiceConfigFile(String destDir,String serviceName){
		 
		 return destDir + File.separator +"gen-meta-src/META-INF/soa/services/config/"+serviceName+"/ServiceConfig.xml";
		 
	 }
	
	 public void ensureClean(String dir){
		 
		 File testDir = new File(dir);
		 if(testDir.isDirectory()){
			 
			File [] fileList = testDir.listFiles();
			for(File file:fileList){
				
				if(file.isDirectory()){
					ensureClean(file.getAbsolutePath());
					file.delete();
				}
				file.delete();
			}
			 
		 }
		 
	 }

}
