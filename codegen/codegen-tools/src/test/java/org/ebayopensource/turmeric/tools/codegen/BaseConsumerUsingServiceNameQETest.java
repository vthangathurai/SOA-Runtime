package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class BaseConsumerUsingServiceNameQETest extends  AbstractServiceGeneratorTestCase{
	final String INTF_PROPERTIES = "service_intf_project.properties";
	final String CONSUMER_PROPERTIES = "service_consumer_project.properties";
	boolean haveProperty,haveScpp;

	String baseConsumer;
	File baseConsumerClass;
	FileInputStream in;
	FileOutputStream out;
	Properties pro;
	File file;
	File binDir= null;
	File destDir = null;
	Properties consumerProper = new Properties();
	@Before
	public void initialize(){
		
	
		haveProperty = true;
		haveScpp= true;
		destDir = getTestDestDir();
		binDir = testingdir.getFile("bin");
		consumerProper.put("scpp_version","1.1");
		consumerProper.put("client_name","Somename");
		consumerProper.put("not_generate_base_consumer","newservice,newService1");

	}
	
	@Test
	public void testNotGeneratingBaseConusmerScenario() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		fillProperties(consumerProper, consumerProps);
		

		String testArgs1[] =  new String[] {
				"-servicename","newService",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath() +"/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-cn","cname"

				
			};	

		
		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() +"/src/org/ebayopensource/turmeric/common/v1/service/gen/BaseNewServiceConsumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertFalse(baseConsumerClass.exists());
		
		
		String testArgs2[] =  new String[] {
				"-servicename","newService1",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-cn","cname"
		
				
			};	

			
		performDirectCodeGen(testArgs2, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath()+ "/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseNewService4Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertFalse(baseConsumerClass.exists());
		
		
		String testArgs3[] =  new String[] {
				"-servicename","NewService24",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", "Vanilla-Codegen/ServiceInputFiles",
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath() + "/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-cn","cname"
	
				
			};	

			
		performDirectCodeGen(testArgs3, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() + "/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseNewService1Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertFalse(baseConsumerClass.exists());
	}

	@Test
	public void testGeneratingBaseConusmerScenario() throws Exception{
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		fillProperties(consumerProper, consumerProps);
		
	

		String testArgs1[] =  new String[] {
				"-servicename","NewService123",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", "Vanilla-Codegen/ServiceInputFiles",
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath() +"/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-cn","cname"

				
			};	

		
		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() + "/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseNewService123Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
	}
	
	
	@Test
	public void testGeneratingBaseConusmerScenario2() throws Exception{
		// not generate with no value.

		
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		consumerProper.put("not_generate_base_consumer","");
		fillProperties(consumerProper, consumerProps);
		
		
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");

		String testArgs1[] =  new String[] {
				"-servicename","AccountService",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+ "/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-cn","cname"
				
			};	

		
		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() + "/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseAccountServiceConsumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
	}
	
	@Test
	public void testGeneratingBaseConusmerScenario3() throws Exception{
		haveProperty = false;
		
		// no not_generate_base...
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		consumerProper.remove("not_generate_base_consumer");
		fillProperties(consumerProper, consumerProps);
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");

		String testArgs1[] =  new String[] {
				"-servicename","AccountService",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath() +"/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-cn","cname"
				
			};	

		
		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() +"/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseAccountServiceConsumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
	}
	
	
	
	
	
	
	

	
	@After
	public void deinitialize(){
		
	
		baseConsumer = null;
		baseConsumerClass = null;
		 in = null;
		 out = null;
		 pro = null;
		 file = null;
		
		
		
	}

}
