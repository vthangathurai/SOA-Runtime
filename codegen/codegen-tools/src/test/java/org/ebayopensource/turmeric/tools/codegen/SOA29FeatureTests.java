package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.ebayopensource.turmeric.tools.AbstractCodegenTestCase;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.ebay.kernel.util.FileUtils;

public class SOA29FeatureTests extends AbstractServiceGeneratorTestCase{
	
	ServiceGenerator gen = null;
	

	
	String destDir = null;
	Properties implProps = new Properties();
	final String IMPL_PROPERTIES = "service_impl_project.properties";
	final String INTF_PROPERTIES = "service_intf_project.properties";
	File implProperty = null;
	File intfProperty = null;
	Properties intfProps = new Properties();
	@Before
	public void init() throws Exception{
		
			
		
		gen = new ServiceGenerator();
		
		destDir = getTestDestDir().getAbsolutePath();
		File bin = new File(destDir);
		URL [] urls = {bin.toURI().toURL()};
		URLClassLoader loader = new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());
		Thread.currentThread().setContextClassLoader(loader);
		
		testingdir.ensureEmpty();
		
		try{
			intfProperty = createPropertyFile(destDir, INTF_PROPERTIES);
			implProperty =createPropertyFile(destDir, IMPL_PROPERTIES);
		}catch(IOException e){
			
			throw(e);
		}
		
		
		//enter values to property file
		implProps = new Properties();
		
		
		intfProps.put("sipp_version","1.1");
		
		fillProperties(intfProps, intfProperty);
		
	}
	
	@Test
	public void testKeepAliveInConfig()throws Exception{
		
		String [] testArgs=  new String[] {
				"-servicename","BillingSuService",
				"-genType", "ClientConfig",
				"-interface","org.ebayopensource.services.interface.BillingSuService",
				"-dest", destDir,
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",destDir,
				"-consumerid","123",
				"-cn","SampleConsumer",
				"-environment","production",
				"-adminname","BillingSuService"
				
	
			};	
		
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		
		 String path = destDir + "/gen-meta-src/META-INF/soa/client/config/SampleConsumer/production/BillingSuService/ClientConfig.xml";
		 String xml = readFileAsString(path);
		 Assert.assertTrue(xml.contains("<option name=\"KEEP_ALIVE\">true</option>"));
		 
	}
	
	
	
	
	@Test
	public void testAddedSettersChangedAccessModifiers() throws Exception{
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		intfProps.put("sipp_version","1.2");
		fillProperties(intfProps, intfProperty);
		
		
		String [] testArgs = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","org.ebayopensource.marketplace.shipping.v1.services",
				  "-adminname","CalculatorService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-namespace","http://www.ebay.com/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		
		Constructor<?> constr = null;
	    Class<?> cls = loadClass("org.ebayopensource.marketplace.shipping.v1.services.calculatorservice.gen.SharedCalculatorServiceConsumer");
		Method []  mtds = cls.getDeclaredMethods();
		boolean present = false;
		int count =0;
		
		for(Constructor<?> c: cls.getDeclaredConstructors() ){
		 if(c.getParameterTypes().length ==1){
			 constr = c;
		 }
		}
		Object constructorObj =null;
		for(Method m : mtds){
			
			if(m.getName().equals("setServiceLocation") || m.getName().equals("setAuthToken") || m.getName().equals("setCookies") || m.getName().equals("getServiceLocation") ||
					 m.getName().equals("getAuthToken") ||  m.getName().equals("getCookies") ||   m.getName().equals("setHostName") || m.getName().equals("getHostName")){
				count = count+ 1;
			}
			
			if(m.getName().equals("setServiceLocation")){
			
				Assert.assertTrue(Modifier.isPublic(m.getModifiers()));
			}
			if(m.getName().equals("setAuthToken")){
			
				Assert.assertTrue(Modifier.isPublic(m.getModifiers()));
			}
			if(m.getName().equals("setCookies")){
			
				Assert.assertTrue(Modifier.isPublic(m.getModifiers()));
			}
			
			if(m.getName().equals("setHostName") && m.getParameterTypes().length==1){
				
				constructorObj = constr.newInstance(new String("dummy"));
				m.invoke(constructorObj,"d-sjc-00507487");
				
			}
			
			if(m.getName().equals("getHostName")){
				
				String host = (String) m.invoke(constructorObj);
				Assert.assertEquals("d-sjc-00507487",host);
			}
			
			
			
		}
		for(Method m : mtds){
		if(m.getName().equals("getServiceLocation")){
			
			URL serviceLocation = (URL) m.invoke(constructorObj);
			Assert.assertEquals("http://d-sjc-00507487/services/shipping/v1/ShippingService",serviceLocation.toString());
		}
		}
		
		Assert.assertTrue("one of the method is missing(setServiceLocation,setAuthToken,setCookies,getServiceLocation,getAuthToken,getCookies,setHostName,getHostName)",count == 9);
		
	}
	
	@Test
	public void testServiceLocationFromConfigFile() throws Exception{
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		intfProps.put("sipp_version","1.2");
		fillProperties(intfProps, intfProperty);
		
		
		String [] testArgs = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","org.ebayopensource.marketplace.shipping.v1.services",
				  "-adminname","CalculatorService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-namespace","http://wwwebayopensource.org/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		
		Constructor<?> constr = null;
	    Class<?> cls = loadClass("org.ebayopensource.marketplace.shipping.v1.services.calculatorservice.gen.SharedCalculatorServiceConsumer");
		Method []  mtds = cls.getDeclaredMethods();
		boolean present = false;
		int count =0;
		
		for(Constructor<?> c: cls.getDeclaredConstructors() ){
		 if(c.getParameterTypes().length ==1){
			 constr = c;
		 }
		}
		Object constructorObj =null;
		constructorObj = constr.newInstance(new String("dummy"));	
			
		
		for(Method m : mtds){
		if(m.getName().equals("getServiceLocation")){
			
			URL serviceLocation = (URL) m.invoke(constructorObj);
			Assert.assertEquals("http://d-sjc-00507487/services/shipping/v1/ShippingService",serviceLocation.toString());
		}
		}
		
		
	}
	@Test
	public void testServiceLocationFromSharedConsumer() throws Exception{
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		intfProps.put("sipp_version","1.2");
		fillProperties(intfProps, intfProperty);
		
		
		String [] testArgs = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","org.ebayopensource.marketplace.shipping.v1.services",
				  "-adminname","CalculatorService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-namespace","http://www.ebayopensource.org/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		
		Constructor<?> constr = null;
	    Class<?> cls = loadClass("org.ebayopensource.marketplace.shipping.v1.services.calculatorservice.gen.SharedCalculatorServiceConsumer");
		Method []  mtds = cls.getDeclaredMethods();

		for(Constructor<?> c: cls.getDeclaredConstructors() ){
		 if(c.getParameterTypes().length ==1){
			 constr = c;
		 }
		}
		Object constructorObj =null;
		constructorObj = constr.newInstance(new String("dummy"));	
			
		for(Method m : mtds){
			if(m.getName().equals("setServiceLocation")){
				
				 m.invoke(constructorObj,"http://localhost:8080/service/test");
			}
			}
		for(Method m : mtds){
		if(m.getName().equals("getServiceLocation")){
			
			URL serviceLocation = (URL) m.invoke(constructorObj);
			Assert.assertEquals("http://localhost:8080/service/test",serviceLocation.toString());
		}
		}
		
		
	}
	
	
	@Test
	public void testServiceLocationFromInvoker() throws Exception{
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		intfProps.put("sipp_version","1.2");
		fillProperties(intfProps, intfProperty);
		
		
		String [] testArgs = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","org.ebayopensource.marketplace.shipping.v1.services",
				  "-adminname","CalculatorService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-namespace","http://www.ebayopensource.org/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		
		Constructor<?> constr = null;
	    Class<?> cls = loadClass("org.ebayopensource.marketplace.shipping.v1.services.calculatorservice.gen.SharedCalculatorServiceConsumer");
		Method []  mtds = cls.getDeclaredMethods();

		for(Constructor<?> c: cls.getDeclaredConstructors() ){
		 if(c.getParameterTypes().length ==1){
			 constr = c;
		 }
		}
		Object constructorObj =null;
		constructorObj = constr.newInstance(new String("dummy"));	
		Object obj = null;	
		for(Method m : mtds){
			if(m.getName().equals("getService")){
				
				 obj = m.invoke(constructorObj);
			}
		}
		Class<?> cl = obj.getClass();
		Method [] mtd = cl.getDeclaredMethods();
		for(Method mt : mtd){
			if(mt.getName().equals("getInvokerOptions")){
				
				 obj = mt.invoke(constructorObj);
			}
		}
		for(Method m : mtds){
		if(m.getName().equals("getServiceLocation")){
			
			URL serviceLocation = (URL) m.invoke(constructorObj);
			Assert.assertEquals("http://localhost:8080/service/test",serviceLocation.toString());
		}
		}
		
		
	}
	
	@Test
	public void testChangesBefore12() throws Exception{
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		intfProps.put("sipp_version","1.1");
		fillProperties(intfProps, intfProperty);
		
		
		String [] testArgs = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","org.ebayopensource.marketplace.shipping.v1.services",
				  "-adminname","CalculatorService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-namespace","http://www.ebayopensource.org/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		

	    Class<?> cls = loadClass("org.ebayopensource.marketplace.shipping.v1.services.calculatorservice.gen.SharedCalculatorServiceConsumer");
		Method []  mtds = cls.getDeclaredMethods();
		boolean present = true;
		
	
		for(Method m : mtds){
			
			
			if(m.getName().equals("setServiceLocation")){
				
				present = false;
			
			}
			if(m.getName().equals("setAuthToken")){
			
				present = false;
				
			}
			if(m.getName().equals("setCookies")){
			
				present = false;
			}
			
			if(m.getName().equals("setHostName")){
				
				present = false;
				
			}
			
			
		}
		
		Assert.assertFalse("Methods not present before 1.2 version is available",false);
		
	}	
	@Test
	public void testProtoEprotofilepath() throws Exception{
		File wsdlFile = new File("wsdlorxsd/TestWsdl.wsdl");
		intfProps.put("nonXSDFormats","protobuf");
		fillProperties(intfProps, intfProperty);
		
		
		String [] testArgs = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","org.ebayopensource.marketplace.shipping.v1.services",
				  "-adminname","CalculatorService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-namespace","http://www.ebayopensource.org/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-enablednamespacefolding",
				  "-pr",destDir
				 };
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		
		File protoFile = new File(destDir+"/meta-src/META-INF/soa/services/proto/CalculatorService/CalculatorService.proto");
		Assert.assertTrue("File does not exist in " + protoFile.getAbsolutePath(), protoFile.exists());
		//codegen.tools.soaframework.test.ebay.com
		File jProtoFile = new File(destDir + "/gen-src/client/com/ebay/test/soaframework/tools/codegen/proto/CalculatorService.java");
		Assert.assertTrue("File does not exist in " + jProtoFile.getAbsolutePath(), jProtoFile.exists());
	
		File eProtoFile = new File(destDir + "/gen-src/client/com/ebay/test/soaframework/tools/codegen/proto/extended/CalculatorService.java");
		Assert.assertTrue("File does not exist in " + eProtoFile.getAbsolutePath(), jProtoFile.exists());
	}
	// admin name different from the service name
	@Test
	public void testProtoEprotofilepath2() throws Exception{
		File wsdlFile = new File("wsdlorxsd/TestWsdl.wsdl");
		intfProps.put("nonXSDFormats","protobuf");
		fillProperties(intfProps, intfProperty);
		
		
		String [] testArgs = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","org.ebayopensource.marketplace.shipping.v1.services",
				  "-adminname","CalculatorServiceV1",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-namespace","http://www.ebayopensource.org/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-enablednamespacefolding",
				  "-pr",destDir
				 };
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		
		File protoFile = new File(destDir+"/meta-src/META-INF/soa/services/proto/CalculatorServiceV1/CalculatorServiceV1.proto");
		Assert.assertTrue("File does not exist in " + protoFile.getAbsolutePath(), protoFile.exists());
		
		File jProtoFile = new File(destDir + "/gen-src/client/com/ebay/test/soaframework/tools/codegen/proto/CalculatorServiceV1.java");
		Assert.assertTrue("File does not exist in " + jProtoFile.getAbsolutePath(), jProtoFile.exists());
	
		File eProtoFile = new File(destDir + "/gen-src/client/com/ebay/test/soaframework/tools/codegen/proto/extended/CalculatorServiceV1.java");
		Assert.assertTrue("File does not exist in " + eProtoFile.getAbsolutePath(), jProtoFile.exists());
	}
	
	
	// change the package with ns2pkg option
	@Test
	public void testProtoEprotofilepath3() throws Exception{
		File wsdlFile = new File("wsdlorxsd/TestWsdl.wsdl");
		intfProps.put("nonXSDFormats","protobuf");
		intfProps.put("ns2pkg","http://codegen.tools.soaframework.test.ebay.com=com.ebayopensource.test.protobuf");
		fillProperties(intfProps, intfProperty);
		
		
		String [] testArgs = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","org.ebayopensource.marketplace.shipping.v1.services",
				  "-adminname","CalculatorServiceV1",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-namespace","http://www.ebayopensource.org/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-enablednamespacefolding",
				  "-pr",destDir
				 };
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		
		File protoFile = new File(destDir+"/meta-src/META-INF/soa/services/proto/CalculatorServiceV1/CalculatorServiceV1.proto");
		Assert.assertTrue("File does not exist in " + protoFile.getAbsolutePath(), protoFile.exists());
		
		File jProtoFile = new File(destDir + "/gen-src/client/com/ebayopensource/test/protobuf/proto/CalculatorServiceV1.java");
		Assert.assertTrue("File does not exist in " + jProtoFile.getAbsolutePath(), jProtoFile.exists());
	
		File eProtoFile = new File(destDir + "/gen-src/client/com/ebayopensource/test/protobuf/proto/extended/CalculatorServiceV1.java");
		Assert.assertTrue("File does not exist in " + eProtoFile.getAbsolutePath(), jProtoFile.exists());
	}
	
	
	@Test
	public void testSupportZeroConfigProperty() throws Exception{
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		intfProps.put("support_zero_config","true");
		fillProperties(intfProps, intfProperty);
		
		
		String [] testArgs = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","org.ebayopensource.marketplace.shipping.v1.services",
				  "-adminname","CalculatorService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-namespace","http://www.ebayopensource.org/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		
		File metadata = new File(destDir+"/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties");
		Properties prop = loadProperties(metadata);
		Assert.assertEquals("true",prop.getProperty("support_zero_config"));
		
		
	}	
	
	//test service config uses impl tag, if no intf property file is present. 
	@Test
	public void testImplFactory() throws Exception{
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		//File destDir = new File("generated");
		
		
		
		String [] testArgs = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServerConfig",
				  "-interface","org.ebayopensource.marketplace.blogs.v1.services.suchservice.BlogsSuchServiceV1",
				  "-scgn","MarketplaceServiceGroup",
				  "-sicn","com.ebayopensource.test.ServiceImpl",
				  "-namespace","http://www.ebayopensource.org/marketplace/blogs/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0"
				 };
		
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		
		
		String path = destDir + "/meta-src/META-INF/soa/services/config/CalculatorService/ServiceConfig.xml";
		 String xml = readFileAsString(path);
		 Assert.assertTrue(xml.contains("<service-impl-class-name>com.ebayopensource.test.ServiceImpl</service-impl-class-name>"));
		
		
	}	
	
	//test service config uses impl factory tag, useExternalServiceFactory = true. 
	@Test
	public void testImplFactory2() throws Exception{
		
		String svc = "https://svcs.ebay.com/services/shipping/v1/ShippingService";
		implProps.put("useExternalServiceFactory","true");
		implProps.put("serviceImplFactoryClassName","dummyvalue");
		
		fillProperties(implProps, implProperty);
		
		
		fillProperties(intfProps, intfProperty);
		
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		//File destDir = new File("generated");
		

			String [] testArgs = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","org.ebayopensource.marketplace.shipping.v1.services",
				  "-adminname","CalculatorService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-namespace","http://www.ebayopensource.org/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
			
			
			String [] testArgs1 = {"-serviceName","CalculatorService",
					  "-mdest",destDir +"/meta-src",
					  "-genType","ServerConfig",
					  "-interface","org.ebayopensource.marketplace.shipping.v1.services.CalculatorService",
					  "-scgn","MarketplaceServiceGroup",
					 "-sicn","org.ebayopensource.test.ServiceImpl",
					  "-namespace","http://www.ebayopensource.org/marketplace/blogs/v1/services",
					  "-dest",destDir,
					  "-bin",destDir,
					  "-slayer","INTERMEDIATE",
					  "-scv","1.0.0",
					  "-pr",destDir
					 };
			
			
			
			String [] testArgs2 = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLImpl",
				  "-interface","org.ebayopensource.marketplace.shipping.v1.services.CalculatorService",
				  "-scgn","MarketplaceServiceGroup",
				  "-adminname","CalculatorService",
				  "-environment","production",
				  "-src",destDir,
				  "-gt",
				  "-jdest",destDir +"/gen-src",
				  "-cn","BlogsSuchServiceV1_Test",
				  "-sicn","org.ebayopensource.test.ServiceImpl",
				  "-namespace","http://www.ebayopensource.org/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		sgen.startCodeGen(testArgs1);
		sgen.startCodeGen(testArgs2);
		
		
		
		String path = destDir + "/meta-src/META-INF/soa/services/config/CalculatorService/ServiceConfig.xml";
		 String xml = readFileAsString(path);
		 Assert.assertTrue(xml.contains("<service-impl-factory-class-name>dummyvalue</service-impl-factory-class-name>"));
		
		Properties metadata = new Properties();
		File metaProps = new File(destDir +"/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties");
		FileInputStream in = new FileInputStream(metaProps);
		metadata.load(in);
		String svcLoc = (String) metadata.get("service_location");
		
		Assert.assertTrue("Service location is not what is present in wsdl",svc.equals(svcLoc));
		
	}
	
	public File copyToDir(File source,File dir){
		
		File dest = new File(dir,source.getName());
		
		try {
			FileUtils.copyFile(source.getAbsolutePath(),dest.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dest;
		
	}
	
	@Test
	public void testBug18551() throws Exception{
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		//File destDir = new File("generated");
		
		intfProps.put("interface_source_type","WSDL");
		fillProperties(intfProps,intfProperty);
		File wsdlPath = new File(destDir+"/meta-src/META-INF/soa/services/wsdl/ShippingService");
		wsdlPath.mkdirs();
		copyToDir(wsdlFile, wsdlPath);
		
		String [] testArgs = {"-serviceName","ShippingService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWsdlIntf",
				  "-gip","com.ebay.intf.shipping",
				  "-namespace","http://www.ebayopensource.orgmarketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		
		ServiceGenerator sgen = new ServiceGenerator();
		try{
		sgen.startCodeGen(testArgs);
		}catch(MissingInputOptionException  e){
		 Assert.assertTrue(e.getMessage().contains("Pls check whether the service_intf_project.properties file exists in the project root and make sure the file contains the property interface_source_type"));
		}
	}	
	
	@Test
	public void testImplFactory3() throws Exception{
		
		implProps.put("useExternalServiceFactory","falase");
		implProps.put("serviceImplFactoryClassName","");
		
		fillProperties(implProps, implProperty);
		
		File wsdlFile = new File("wsdlorxsd/ShippingService.wsdl");
		//File destDir = new File("generated");
		

			String [] testArgs = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLIntf",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","org.ebayopensource.marketplace.shipping.v1.services",
				  "-adminname","CalculatorService",
				  "-slayer","BUSINESS",
				  "-jdest",destDir +"/gen-src",
				  "-sicn","com.ebay.test.ServiceImpl",
				  "-namespace","http://www.ebayopensource.org/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
			
			
			String [] testArgs1 = {"-serviceName","CalculatorService",
					  "-mdest",destDir +"/meta-src",
					  "-genType","ServerConfig",
					  "-interface","org.ebayopensource.marketplace.shipping.v1.services.CalculatorService",
					  "-scgn","MarketplaceServiceGroup",
					  "-sicn","org.ebayopensource.test.ServiceImpl",
					  "-namespace","http://www.ebayopensource.org/marketplace/blogs/v1/services",
					  "-dest",destDir,
					  "-bin",destDir,
					  "-slayer","INTERMEDIATE",
					  "-scv","1.0.0",
					  "-pr",destDir
					 };
			
			
			
			String [] testArgs2 = {"-serviceName","CalculatorService",
				  "-mdest",destDir +"/meta-src",
				  "-genType","ServiceFromWSDLImpl",
				  "-interface","org.ebayopensource.marketplace.shipping.v1.services.CalculatorService",
				  "-scgn","MarketplaceServiceGroup",
				  "-adminname","CalculatorService",
				  "-environment","production",
				  "-gt",
				  "-jdest",destDir +"/gen-src",
				  "-cn","BlogsSuchServiceV1_Test",
				  "-sicn","org.ebayopensource.test.ServiceImpl",
				  "-namespace","http://www.ebayopensource.org/marketplace/shipping/v1/services",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir
				 };
		
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		sgen.startCodeGen(testArgs1);
		sgen.startCodeGen(testArgs2);
		
		String path = destDir + "/meta-src/META-INF/soa/services/config/CalculatorService/ServiceConfig.xml";
		 String xml = readFileAsString(path);
		 Assert.assertTrue(xml.contains("<service-impl-class-name>org.ebayopensource.test.ServiceImpl</service-impl-class-name>"));
		
		
	}
	
	@Test
	public void testBaseConsumerForNoClientConfigChange() throws Exception{
		intfProps.put("sipp_version","1.0");
		fillProperties(intfProps,intfProperty);
		File wsdlFile = new File("wsdlorxsd/TestWsdlComplexType.wsdl");
		
		String [] testArgs = {"-serviceName","FindingService",
				  "-genType","Consumer",
				  "-wsdl",wsdlFile.getAbsolutePath(),
				  "-gip","org.ebayopensource.marketplace.shipping.v1.services",
				  "-adminname","FindingService",
				  "-dest",destDir,
				  "-bin",destDir,
				  "-pr",destDir
				 };
		
		ServiceGenerator sgen = new ServiceGenerator();
		try{
		sgen.startCodeGen(testArgs);}
		catch(Exception e){
			e.printStackTrace();
			
		}
			
	}
	
	       
	

}
