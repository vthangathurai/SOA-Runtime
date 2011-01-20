package org.ebayopensource.turmeric.tools.codegen;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.ServiceGenerator;
import org.ebayopensource.turmeric.tools.codegen.TestUserResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.handler.UserResponseHandler;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
/**
 * @author rmohagaonkar
 *
 */



public class TestMultipleClientSupportQE extends AbstractServiceGeneratorTestCase {
	
	File destDir = null;
	File prDir = null;
	File binDir = null;
	String envValue = "";
	String clientName="";


	@Before
	public void init() throws Exception{

		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		prDir = testingdir.getDir();
		
		
		}
	
	
	
	/*Validate the client config path with given environment and client name*/
	@Test
	public void CheckMCCwithValidateInput() {
			System.out.println("*****************CheckWsdlWithPublicServiceName Starts*************************");
			boolean isException = false;
			String clientConfigpath=destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/client/config/MyClient/Myenviroment/MyServiceV1/ClientConfig.xml";
			ServiceGenerator servicegenerator = createServiceGenerator();
			try {
				String[] testArgs1 = setInputParameters("ClientConfig",true,true,"MyServiceV1");
				servicegenerator. startCodeGen(testArgs1);
		    } catch (Exception e) {
				isException = true;
				assertFalse(true);
			}
			assertFalse(isException);
			File modifiedWsdlPath = new File(clientConfigpath);
		assertTrue(modifiedWsdlPath.exists());				
			System.out.println("*************************CheckWsdlWithPublicServiceName Ends*****************************");
}
	
	/*Validate the client config path with no environment name*/
	@Test
	public void CheckMCCwithonlyClientName() {
			System.out.println("*****************CheckMCCwithonlyClientName Starts*************************");
			boolean isException = false;
			String clientConfigpath=destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/client/config/MyClient/ClientConfig.xml";
			ServiceGenerator servicegenerator = createServiceGenerator();
			try {
				String[] testArgs1 = setInputParameters("ClientConfig",true,false,"MyServiceV1");
				servicegenerator. startCodeGen(testArgs1);
		    } catch (Exception e) {
				isException = true;
				assertFalse(true);
			}
			assertFalse(isException);
			File modifiedWsdlPath = new File(clientConfigpath);
		assertTrue(modifiedWsdlPath.exists());				
			System.out.println("*************************CheckMCCwithonlyClientName Ends*****************************");
}
	
	/*Validate the client config path with no environment name*/
	@Test
	public void CheckMCCwithonlyEnvironmentName() {
			System.out.println("*****************CheckMCCwithonlyEnvironmentName Starts*************************");
			boolean isException = false;
			String clientConfigpath=destDir.getAbsolutePath()  +"/gen-meta-src/META-INF/soa/client/config/MyServiceV1/Myenviroment/MyServiceV1/ClientConfig.xml";
			ServiceGenerator servicegenerator = createServiceGenerator();
			try {
				String[] testArgs1 = setInputParameters("ClientConfig",false,true,"MyServiceV1");
				servicegenerator. startCodeGen(testArgs1);
		    } catch (Exception e) {
				isException = true;
				assertFalse(true);
			}
			assertFalse(isException);
			File modifiedWsdlPath = new File(clientConfigpath);
		assertTrue(modifiedWsdlPath.exists());				
			System.out.println("*************************CheckMCCwithonlyEnvironmentName Ends*****************************");
}
	/*Validate the  Base consumer.java file configuration whether added with the argument constructor*/
	@Test
	public void CheckMCCForBaseConsumer() {
			System.out.println("*****************CheckMCCForBaseConsumer Starts*************************");
			boolean isException = false;
			String clientConfigpath=destDir.getAbsolutePath() +"/gen-src/org/ebayopensource/turmeric/common/v1/services/gen/BaseMyServiceV1Consumer.java";
	
			try {
				String[] testArgs1 = setInputParameters("Consumer",true,true,"MyServiceV1");
				performDirectCodeGen(testArgs1, binDir);
		    } catch (Exception e) {
		    	e.printStackTrace();
				isException = true;
				assertFalse(true);
			}
			assertFalse(isException);
			File modifiedWsdlPath = new File(clientConfigpath);
		assertTrue(modifiedWsdlPath.exists());		
		boolean status = validateContents(modifiedWsdlPath,getCodegenQEDataFileInput("BaseMyServiceV1Consumer.java").getAbsolutePath());
		assertTrue("Unit test content does not match as expected", status);	
		System.out.println("*************************CheckMCCForBaseConsumer Ends*****************************");
}
	/*Validate the  ServiceUnitTest.java file configuration present in the impl project whether added with the argument constructor*/
	@Test
	public void CheckMCCForUnitTest() {
			System.out.println("*****************CheckMCCForBaseConsumer Starts*************************");
			boolean isException = false;
			String clientConfigpath=destDir.getAbsolutePath()  + "/gen-test/org/ebayopensource/turmeric/common/v1/services/test/MyServiceV1Test.java";
			
			try {
				String[] testArgs1 = setInputParameters("UnitTest",true,true,"MyServiceV1");
				performDirectCodeGen(testArgs1, binDir);
		    } catch (Exception e) {
				isException = true;
				assertFalse(true);
			}
			assertFalse(isException);
			File modifiedWsdlPath = new File(clientConfigpath);
		assertTrue(modifiedWsdlPath.exists());		
		boolean status =validateContents(modifiedWsdlPath, getCodegenQEDataFileInput("MyServiceV1Test.java").getAbsolutePath());
		assertTrue("Unit test content does not match as expected", status);	
		System.out.println("*************************CheckMCCForBaseConsumer Ends*****************************");
}
	private ServiceGenerator createServiceGenerator() {
		UserResponseHandler testResponseHandler = new TestUserResponseHandler();
		ServiceGenerator serviceGenerator = new ServiceGenerator(testResponseHandler);
		return serviceGenerator;
	}
	public String[] setInputParameters(String genType,boolean cn,boolean env,String svcName){
		if (env==true){envValue="Myenviroment";}
		if(cn==true){clientName="MyClient";}
		File wsdl = getCodegenQEDataFileInput("Testing1.wsdl");
		String[] testArgs1 = new String[] {"-servicename",
				svcName,
				"-wsdl",wsdl.getAbsolutePath(),
				"-genType", genType,
				"-dest",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				"-cn", clientName,
				"-src",destDir.getAbsolutePath(),
				"-environment",envValue,		
				"-pr", prDir.getAbsolutePath()};
		     return testArgs1;
	}
	private boolean validateContents(File generatedFile, String lineContent){
		boolean returnValue = false;
		String tmpStr,cmpStr = null;
		try {
			if(generatedFile != null){
				File validFile = new File(lineContent);
				BufferedReader cr = new BufferedReader(new FileReader(validFile));
				BufferedReader br = new BufferedReader(new FileReader(generatedFile));
				while((( tmpStr = br.readLine()) != null)&&((cmpStr = cr.readLine()) != null)) {
					if(tmpStr.contains(cmpStr)){
						returnValue = true;
						continue;
						
						
					}else{
						returnValue = false;
						break;}
					
				}
			}
		} catch (Exception e) {
			returnValue = false;
		}
		return returnValue;


	}
}
