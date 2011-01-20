package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;



public class BaseConsumerChangesQETest extends AbstractServiceGeneratorTestCase{
	
	final String INTF_PROPERTIES = "service_intf_project.properties";
	final String CONSUMER_PROPERTIES = "service_consumer_project.properties";
	
	Properties consumerProper = new Properties();
	boolean haveProperty,haveScpp;

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
	public void testNotGeneratingBaseConusmerScenario() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");	
		String [] testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest",destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				"-adminname","Admin3",
			
	
			};	

	
	
		performDirectCodeGen(testArgs, binDir);
		
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		fillProperties(consumerProper, consumerProps);
		
	
	
		//change to package of BC - the consumer name is removed from pckg.
		String baseConsumer = destDir.getAbsolutePath() + "/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseAdminV1Consumer.java";
		File baseConsumerClass = new File(baseConsumer);
		assertFalse(baseConsumerClass.exists());
		
		
		String testArgs2[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-interface","org/ebayopensource/turmeric/tools/codegen/Admin2.java",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin",binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-adminname","Admin2",
				"-cn","cname"
				
			};	

		
		performDirectCodeGen(testArgs2, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() +"/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseAdmin2Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
		
		
		String testArgs3[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-interface","org/ebayopensource/turmeric/tools/codegen/Admin3.java",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-adminname","Admin3",
				"-cn","cname"
				
			};	

	
		performDirectCodeGen(testArgs3, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() +"/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseAdmin3Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
		
		
	}

	@Test
	//@Ignore("failing")
	public void testGeneratingBaseConusmerScenario() throws Exception{
	
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		consumerProper.put("scpp_version", "1.0");
		fillProperties(consumerProper, consumerProps);	
		
		
	

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-interface","org/ebayopensource/turmeric/tools/codegen/Admin1.java",
				"-src", destDir.getAbsolutePath(),
				"-dest",destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-adminname","Admin1",
				"-cn","cname"
				
			};	
		
		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		String baseConsumer = destDir.getAbsolutePath() +"/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseAdmin1Consumer.java";
		File baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
		
		String baseCon = destDir.getAbsolutePath() + "/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseAdmin1Consumer.java";
		File baseConFile = new File(baseCon);

		if(baseConFile.exists()){

			if(baseConFile.delete()){
			
				performDirectCodeGen(testArgs1, binDir);

			}
			else
				throw new Exception("File Could not be deleted");
				
			}	
			
			baseConsumer = destDir.getAbsolutePath() +"/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseAdmin1Consumer.java";
			baseConsumerClass = new File(baseConsumer);
			assertTrue(baseConsumerClass.exists());
			
			assertTrue(FileUtils.readFileToString(baseConsumerClass).contains("private final static String SVC_ADMIN_NAME = \"Admin1\";"));
			assertTrue(FileUtils.readFileToString(baseConsumerClass).contains("private String m_environment;"));
			assertTrue(FileUtils.readFileToString(baseConsumerClass).contains(" private String m_clientName = \"Admin1\";"));
	}
	
	
	@Test
	//@Ignore("Failing")
	public void testGeneratingBaseConusmerScenario2() throws Exception{
		
		

		
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		consumerProper.put("not_generate_base_consumer","");
		fillProperties(consumerProper, consumerProps);	
		
	

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-interface","org/ebayopensource/turmeric/tools/codegen/AdminV1.java",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-adminname","AdminV1",
				"-cn","cname"
				
			};	


		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() +"/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseAdminV1Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
	}
	
	@Test
	//@Ignore("failing")
	public void testGeneratingBaseConusmerScenario3() throws Exception{
		
		
		haveProperty = false;
		File consumerProps = createPropertyFile(destDir.getAbsolutePath(),CONSUMER_PROPERTIES);
		consumerProper.put("not_generate_base_consumer","");
		fillProperties(consumerProper, consumerProps);	
		
	

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-interface","org/ebayopensource/turmeric/tools/codegen/AdminV1.java",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin",binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-adminname","AdminV1",
				"-cn","cname"
				
			};	

		
		performDirectCodeGen(testArgs1, binDir);
		//change to package of BC - the consumer name is removed from pckg.
		baseConsumer = destDir.getAbsolutePath() +"/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseAdminV1Consumer.java";
		baseConsumerClass = new File(baseConsumer);
		assertTrue(baseConsumerClass.exists());
	}
	
	
	
	
	
	

public void deinitialize(){
	
	 baseConsumer = null;
     baseConsumerClass = null;
     in = null;
 	 out = null;
 	 pro = null;
 	 file = null;
	
}
}
