package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.ServiceGenerator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;



public class BaseConsumerChangesQE2Test extends AbstractServiceGeneratorTestCase {
	
	final String INTF_PROPERTIES = "service_intf_project.properties";
	final String CONSUMER_PROPERTIES = "service_consumer_project.properties";
	
	
	Properties consumerProper = new Properties();
	boolean haveProperty,haveScpp;
	ServiceGenerator sgen;
	String baseConsumer;
	File baseConsumerClass;
	FileInputStream in;
	FileOutputStream out;
	Properties pro;
	File file;
	File binDir = null;
	File destDir = null;
	@Before
	public void initialize() throws IOException{


		haveProperty = true;
		haveScpp = true;
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		
		consumerProper.put("scpp_version", "1.1");
		consumerProper.put("client_name","Somename");
		consumerProper.put("not_generate_base_consumer","AdminV1");

	}
	
	
	
	@Test
	//@Ignore("failing")
	public void testNotGeneratingBaseConusmerScenario2() throws Exception{
		
	
		haveScpp = false;
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		fillProperties(consumerProper, consumerProps);
		
	

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-interface","org/ebayopensource/turmeric/tools/codegen/Admin1.java",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-adminname","Admin3",
				"-cn","canme"
				
			};	

		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() +"/org/ebayopensource/turmeric/tools/codegen/testNotGeneratingBaseConusmerScenario2/gen/BaseAdmin1Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertFalse(baseConsumerClass.exists());
	}
	
	
	@Test
	//@Ignore("failing")
	public void testGeneratingBaseConusmerScenario4() throws Exception{
		
	
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		fillProperties(consumerProper, consumerProps);
		
	

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-interface","org/ebayopensource/turmeric/tools/codegen/Admin1.java",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-adminname","Admin1",
				"-cn","cname"
				
			};	

		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() +"/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseAdmin1Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
	}
	
	@Test
	//@Ignore("failing")
	public void testGeneratingBaseConusmerScenario5() throws Exception{
		

		haveProperty = false;
		haveScpp = false;
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		fillProperties(consumerProper, consumerProps);
		
	

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-interface","org/ebayopensource/turmeric/tools/codegen/Admin2.java",
				"-dest",destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-adminname","Admin2",
				"-cn","cname"
				
			};	

		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() +"/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseAdmin2Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
	}
	
	@Test
	//@Ignore("failing")
	public void noPropsFile() throws Exception{
	
		
		
	

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-interface","org/ebayopensource/turmeric/tools/codegen/Admin3.java",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-adminname","Admin3",
				"-cn","cname"
				
			};	

		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() +"/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseAdmin3Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
	}
	
	
	
	

public void deinitialize(){
	 sgen = null;

	 baseConsumer = null;
     baseConsumerClass = null;
     in = null;
 	 out = null;
 	 pro = null;
 	 file = null;
	
}
}
