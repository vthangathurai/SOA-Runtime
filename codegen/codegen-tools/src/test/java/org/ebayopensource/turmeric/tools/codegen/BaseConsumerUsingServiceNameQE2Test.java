package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BaseConsumerUsingServiceNameQE2Test extends AbstractServiceGeneratorTestCase{
	
	HashMap<String,String> propertiesFileMap;
	boolean haveProperty,haveScpp;
	final String CONSUMER_PROPERTIES = "service_consumer_project.properties";

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
	
		destDir = getTestDestDir();
		binDir = testingdir.getFile("bin");

		haveProperty = true;
		haveScpp= true;
	
		consumerProper.put("scpp_version","1.1");
		consumerProper.put("client_name","Somename");
		consumerProper.put("not_generate_base_consumer","newservice,newService1");

	}
	
	
	
	@Test
	public void testNotGeneratingBaseConusmerScenario2() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		fillProperties(consumerProper, consumerProps);
		
	

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", "Vanilla-Codegen/ServiceInputFiles",
				"-dest", destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath()+"/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-cn","cname"
		
				
			};	
		// no scpp_version 
		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath()+ "/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseNewServiceConsumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertFalse(baseConsumerClass.exists());
	}
	
	
	@Test
	public void testGeneratingBaseConusmerScenario4() throws Exception{
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		haveScpp = false;
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		fillProperties(consumerProper, consumerProps);
		
	

		String testArgs1[] =  new String[] {
				"-servicename","NewService12",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath()+ "/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-cn","cname"
	
				
			};	
		
		
		performDirectCodeGen(testArgs1, binDir);
		// no scpp_version 
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() +"/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseNewService12Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
	}
	
	@Test
	public void testGeneratingBaseConusmerScenario5() throws Exception{
		

		// no scpp_version or not_generate...
		haveProperty = false;
		haveScpp = false;
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		fillProperties(consumerProper, consumerProps);
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");

		String testArgs1[] =  new String[] {
				"-servicename","NewService11",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", "Vanilla-Codegen/ServiceInputFiles",
				"-dest", destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath() + "/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-cn","cname"
	
				
			};	

	
		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() + "/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseNewService11Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
	}
	
	@Test
	public void noPropsFile() throws Exception{
		
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest",destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath() +"/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-cn","cname"
				
			};	

		
		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath()+ "/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseNewServiceConsumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
	}
	
	
	
	
	
	@After
	public void deinitialize(){
		
	
		baseConsumer = null;
		baseConsumerClass = null;
		propertiesFileMap = null;
		 in = null;
		 out = null;
		 pro = null;
		 file = null;
		
		
		
	}

}
