package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.util.Properties;

import org.custommonkey.xmlunit.DifferenceEngine;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class CommentsAddedToConfigTest extends AbstractServiceGeneratorTestCase{

@Rule public TestName name = new TestName();
	
	File destDir = null;
	File binDir = null;
	final String CONSUMER_PROPERTIES = "service_consumer_project.properties";


	
@Before
	
	public void init() throws Exception{
		
	testingdir.ensureEmpty();
	destDir = testingdir.getDir();
	binDir = testingdir.getFile("bin");
	File consumerProperty = null;
		consumerProperty =	createPropertyFile(destDir.getAbsolutePath(), CONSUMER_PROPERTIES);

		Properties consumerProps = new Properties();
		consumerProps.put("scpp_version","1.1");
		fillProperties(consumerProps, consumerProperty);
}


@Test
public void testCommentedElementsClientConfig() throws Exception{
	
	File path  = getCodegenQEDataFileInput("AccountService1.wsdl");
	
	
	String [] testArgs1 = {"-serviceName","AccountService",
			  "-genType","ClientConfig",	
			  "-wsdl",path.getAbsolutePath(),
			  "-consumerid","123",
			  "-interface","com.ebay.AccountService",
			  "-ccgn","marketplace",
			  "-sl","http://www.ebayopensource.com/services",
			  "-wl","http://www.ebayopensource.com/services?wsdl",
			  "-cn","AccountServiceConsumer",
			  "-environment","production",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-adminname","AccountService",
			  "-slayer","INTERMEDIATE",
			  "-bin",binDir.getAbsolutePath(),
			  "-scv","1.0.0",
			  "-pr",destDir.getAbsolutePath()};
	

	
	performDirectCodeGen(testArgs1, binDir);
	
	File genFile = new File(getClientConfigFile(destDir.getAbsolutePath(), "AccountService", "AccountServiceConsumer"));
	
	
	File goldFile = getCodegenQEDataFileInput("ClientConfig.xml");
	
	String genString = readFileAsString(genFile.getAbsolutePath());
	String goldString = readFileAsString(goldFile.getAbsolutePath());
	
	XMLAssert.assertXMLEqual(genString, goldString);
	
}


@Test
public void testCommentedElementsServiceConfig() throws Exception{
	
	File path  = getCodegenQEDataFileInput("AccountService1.wsdl");
	
	
	String [] testArgs1 = {"-serviceName","AccountService",
			  "-genType","ServerConfig",	
			  "-wsdl",path.getAbsolutePath(),
			  "-interface","com.ebayopensource.AccountService",
			  "-sicn","com.ebayopensource.impl.AccountServicesImpl",
			  "-scgn","marketplace",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-adminname","AccountService",
			  "-slayer","INTERMEDIATE",
			  "-bin",binDir.getAbsolutePath(),
			  "-scv","1.0.0",
			  "-pr",destDir.getAbsolutePath()};
	

	
	performDirectCodeGen(testArgs1, binDir);
	
	File genFile = new File(getServiceConfigFile(destDir.getAbsolutePath(), "AccountService"));
	
	
	File goldFile = getCodegenQEDataFileInput("ServiceConfig.xml");
	
	String genString = readFileAsString(genFile.getAbsolutePath());
	String goldString = readFileAsString(goldFile.getAbsolutePath());
	
	XMLAssert.assertXMLEqual(genString, goldString);
	
}



@Test
public void testCommentedElementsConfigAll() throws Exception{
	
	File path  = getCodegenQEDataFileInput("AccountService1.wsdl");
	
	
	String [] testArgs1 = {"-serviceName","AccountService",
			  "-genType","ConfigAll",	
			  "-wsdl",path.getAbsolutePath(),
			  //"-interface","org.ebayopensource.turmeric.common.v1.services.AccountService",
			  "-sl","http://www.ebayopensource.com/services",
			  "-wl","http://www.ebayopensource.com/services?wsdl",
			  "-cn","AccountServiceConsumer",
			  "-sicn","com.ebayopensource.impl.AccountServicesImpl",
			  "-environment","production",
			  "-ccgn","marketplace",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-adminname","AccountService",
			  "-slayer","INTERMEDIATE",
			  "-bin",binDir.getAbsolutePath(),
			  "-scv","1.0.0",
			  "-pr",destDir.getAbsolutePath()};
	

	
	performDirectCodeGen(testArgs1,binDir);
	
	File genFile = new File(getClientConfigFile(destDir.getAbsolutePath(), "AccountService", "AccountServiceConsumer"));
	
	
	File goldFile = getCodegenQEDataFileInput("ClientConfig2.xml");
	
	String genString = readFileAsString(genFile.getAbsolutePath());
	String goldString = readFileAsString(goldFile.getAbsolutePath());
	
	XMLAssert.assertXMLEqual(genString, goldString);
	
}

public String getClientConfigFile(String destDir,String serviceName,String consumerName){
	 
	 return destDir + File.separator +"gen-meta-src/META-INF/soa/client/config/"+consumerName+File.separator+ "production"+File.separator+serviceName+File.separator+"ClientConfig.xml";
	 
}

public String getServiceConfigFile(String destDir,String serviceName){
	 
	 return destDir + File.separator +"gen-meta-src/META-INF/soa/services/config/"+serviceName+"/ServiceConfig.xml";
	 
}
	
	
}
