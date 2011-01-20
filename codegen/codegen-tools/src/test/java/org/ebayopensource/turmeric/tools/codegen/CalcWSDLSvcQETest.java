package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author shrao
 *
 */
public class CalcWSDLSvcQETest extends AbstractServiceGeneratorTestCase {
	/**
	 * @param name
	 */
	
	
	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	NamespaceContextImpl nsc;

	@Before
	public void init() throws Exception{
	
		mavenTestingRules.setFailOnViolation(false);
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		prDir = testingdir.getFile("tmp");
		nsc = new NamespaceContextImpl();
		
		
		}
	
	public CalcWSDLSvcQETest(){}


	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allFromCalcSvcWSDL() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","All", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-slayer","INTERMEDIATE", 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		try{
		performDirectCodeGen(testArgs, binDir); }
		
		catch(Exception e){
			
			Assert.fail("Exception was thrown with message " + e.getMessage() + " and cause "+ e.getCause() );
		}
		
	}
	@Test
	public  void interfaceCalcSvc() {
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-slayer","INTERMEDIATE", 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		try{
		performDirectCodeGen(testArgs, binDir);
		
		String genPath = destDir.getCanonicalPath() + "/gen-src/org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java";
		
		String goldPath = getTestResrcDir() + "/CalculatorServiceImpl/gen-src/org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java";
			
		assertFileExists(genPath);
		Assert.assertTrue(compareFiles(new File(genPath), new File(goldPath)));
		
		
		String genPathType = destDir.getCanonicalPath() + "/gen-src/com/ebayopensource/test/soaframework/tools/codegen/Add.java";
		
		String goldPath1 = getTestResrcDir() + "/CalculatorServiceImpl/gen-src/com/ebayopensource/test/soaframework/tools/codegen/Add.java";
			
		assertFileExists(genPathType);
		Assert.assertTrue(compareTwoFiles(genPathType, goldPath1));
		
		 genPathType = destDir.getCanonicalPath() + "/gen-src/com/ebayopensource/test/soaframework/tools/codegen/AddResponse.java";
		
		 goldPath1 = getTestResrcDir() + "/CalculatorServiceImpl/gen-src/com/ebayopensource/test/soaframework/tools/codegen/AddResponse.java";
		
		}
		
		catch(Exception e){
			
			Assert.fail("Exception was thrown with message " + e.getMessage() + " and cause "+ e.getCause() );
		}
		
		
	}


	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allCalcSvc() throws Exception {
		
		FileInputStream in = null;
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-slayer","COMMON", 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		
		String testArgs1[] =  new String[] {	
				"-genType","All", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-sicn","org.ebayopensource.qaservices.calculatorservice.intf.gen.CalculatorServiceImplSkeleton",
				"-serviceName","CalculatorService", 
				"-slayer","COMMON", 
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				
				String genPath = destDir.getAbsolutePath() +"/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties";
				in = new FileInputStream(new File(genPath));
				Properties pro = new Properties();
				pro.load(in);
				Assert.assertTrue(pro.get("service_layer").equals("COMMON")); }
				catch(Exception e){
					e.printStackTrace();
				}finally{
					
					in.close();
				}
				
		
	}


	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allCalcSvc2() throws Exception {
		
		FileInputStream in = null;
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		String testArgs1[] =  new String[] {	
				"-genType","All", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService", 
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				
				String genPath = destDir.getAbsolutePath() +"/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties";
				in = new FileInputStream(new File(genPath));
				Properties pro = new Properties();
				pro.load(in);
				Assert.assertTrue(pro.get("service_layer").equals("BUSINESS")); }
				catch(Exception e){
					e.printStackTrace();
				}finally{
					
					in.close();
				}
		

	}


	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allCalcSvc3() throws Exception {
		FileInputStream in = null;
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		File layers = getCodegenQEDataFileInput("service_layers.txt");
		
		String testArgs1[] =  new String[] {	
				"-genType","All", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService", 
				"-asl",layers.getAbsolutePath(),
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				
				String genPath = destDir.getAbsolutePath() +"/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties";
				in = new FileInputStream(new File(genPath));
				Properties pro = new Properties();
				pro.load(in);
				Assert.assertTrue(pro.get("service_layer").equals("BUSINESS")); }
				catch(Exception e){
					e.printStackTrace();
				}finally{
					
					in.close();
				}
		
	

	}


	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allCalcSvc4() throws Exception {
		FileInputStream in = null;
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		File layers = getCodegenQEDataFileInput("service_layers2.txt");
		
		String testArgs1[] =  new String[] {	
				"-genType","All", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService", 
				"-asl",layers.getAbsolutePath(),
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				
				String genPath = destDir.getAbsolutePath() +"/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties";
				in = new FileInputStream(new File(genPath));
				Properties pro = new Properties();
				pro.load(in);
				Assert.assertTrue(pro.get("service_layer").equals("UD_BUSINESS")); }
				catch(Exception e){
					e.printStackTrace();
				}finally{
					
					in.close();
				}
		

	}

	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allCalcSvc41() throws Exception {
		
FileInputStream in = null;
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		File layers = getCodegenQEDataFileInput("service_layers3.txt");
		
		String testArgs1[] =  new String[] {	
				"-genType","All", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService", 
				"-asl",layers.getAbsolutePath(),
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				
				String genPath = destDir.getAbsolutePath() +"/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties";
				in = new FileInputStream(new File(genPath));
				Properties pro = new Properties();
				pro.load(in);
				Assert.assertTrue(pro.get("service_layer").equals("UD_COMMON")); }
				catch(Exception e){
					e.printStackTrace();
				}finally{
					
					in.close();
				}
		
	}	
	
	
	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allCalcSvc42() throws Exception {
		
		FileInputStream in = null;
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		File layers = getCodegenQEDataFileInput("service_layers2.txt");
		
		String testArgs1[] =  new String[] {	
				"-genType","All", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService", 
				"-asl",layers.getAbsolutePath(),
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				
				String genPath = destDir.getAbsolutePath() +"/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties";
				in = new FileInputStream(new File(genPath));
				Properties pro = new Properties();
				pro.load(in);
				Assert.assertTrue(pro.get("service_layer").equals("UD_BUSINESS")); }
				catch(Exception e){
					e.printStackTrace();
				}finally{
					
					in.close();
				}
		

	}
	
	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allCalcSvc43() throws Exception {
		
		FileInputStream in = null;
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		File layers = getCodegenQEDataFileInput("service_layers4.txt");
		
		String testArgs1[] =  new String[] {	
				"-genType","All", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService", 
				"-asl",layers.getAbsolutePath(),
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				
				String genPath = destDir.getAbsolutePath() +"/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties";
				in = new FileInputStream(new File(genPath));
				Properties pro = new Properties();
				pro.load(in);
				Assert.assertTrue(pro.get("service_layer").equals("BUSINESS")); }
				catch(Exception e){
					e.printStackTrace();
				}finally{
					
					in.close();
				}
		
	}
	
	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allCalcSvc5() throws Exception {
		
		FileInputStream in = null;
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		File layers = getCodegenQEDataFileInput("service_layers.txt");
		
		String testArgs1[] =  new String[] {	
				"-genType","All", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService",
				"-slayer","COMMON",
				"-asl",layers.getAbsolutePath(),
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				
				String genPath = destDir.getAbsolutePath() +"/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties";
				in = new FileInputStream(new File(genPath));
				Properties pro = new Properties();
				pro.load(in);
				Assert.assertTrue(pro.get("service_layer").equals("COMMON")); }
				catch(Exception e){
					e.printStackTrace();
				}finally{
					
					in.close();
				}
		
	}


	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allCalcSvc6() throws Exception {
		
		FileInputStream in = null;
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		File layers = getCodegenQEDataFileInput("service_layers2.txt");
		
		String testArgs1[] =  new String[] {	
				"-genType","All", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService",
				"-slayer","UD_INTERMEDIATE",
				"-asl",layers.getAbsolutePath(),
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				
				String genPath = destDir.getAbsolutePath() +"/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties";
				in = new FileInputStream(new File(genPath));
				Properties pro = new Properties();
				pro.load(in);
				Assert.assertTrue(pro.get("service_layer").equals("UD_INTERMEDIATE")); }
				catch(Exception e){
					e.printStackTrace();
				}finally{
					
					in.close();
				}
		
	}


	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allCalcSvc7() throws Exception {
		
		
		
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		File layers = getCodegenQEDataFileInput("corrupted_service_layers.txt");
		
		String testArgs1[] =  new String[] {	
				"-genType","All", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService",
				"-asl",layers.getAbsolutePath(),
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				Assert.fail("Expected a exception but did not get one");
				
				 }
				catch(Exception e){
					Assert.assertTrue(true);
				}
		
		
	}



	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void clientCalcSvc() throws Exception {
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
	
		
		String testArgs1[] =  new String[] {	
				"-genType","Client", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService",
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				}catch(Exception e){
					
					e.printStackTrace();
				}
				
		
	}



	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void clientNoConfigCalcSvc() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
	
		
		String testArgs1[] =  new String[] {	
				"-genType","Client", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService",
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				String genPath = destDir.getCanonicalPath() + "/gen-src/org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java";
				
				String goldPath = getTestResrcDir() + "/CalculatorServiceImpl/gen-src/org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java";
					
				assertFileExists(genPath);
				Assert.assertTrue(compareFiles(new File(genPath),new File(goldPath)));
				
				}catch(Exception e){
					
					e.printStackTrace();
				}
	}


	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void typeMappingsFromCalcWSDL() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","TypeMappings", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		
		String goldPath = getTestResrcDir() + "/CalculatorServiceImpl/gen-meta-src/META-INF/soa/common/config/CalculatorService/TypeMappings.xml";
		
		String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/common/config/CalculatorService/TypeMappings.xml";
		
		assertFileExists(genPath);
		Assert.assertTrue(compareTwoFiles(genPath, goldPath));
		
	}

	/**
	 * @throws  
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allFromCalcWSDL(){
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","All", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		try {
			performDirectCodeGen(testArgs, binDir);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void svcMetadataProps() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
	
		
		String testArgs1[] =  new String[] {	
				"-genType","ServiceMetadataProps", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService",
				"-scv","1.0.0", 
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath()};
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				String genPath = prDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties";
				
				
					
				assertFileExists(genPath);

				
				}catch(Exception e){
					
					e.printStackTrace();
				}
		
	}	
		
	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void svcMetadataPropsWithoutPR() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","Interface", 
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf", 
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService", 
		"-serviceName","CalculatorService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
	
		
		String testArgs1[] =  new String[] {	
				"-genType","ServiceMetadataProps", 
				"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorService.java",
				"-serviceName","CalculatorService",
				"-scv","1.0.0", 
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				"-dest",destDir.getAbsolutePath()};
				
				try{
				performDirectCodeGen(testArgs1, binDir);
				String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/common/config/CalculatorService/service_metadata.properties";
				
				
					
				assertFileExists(genPath);

				
				}catch(Exception e){
					
					e.printStackTrace();
				}
		
	}	
	
	/**
	 * @check  Exceptions need to be handled
	 */
	
	
public Node getNodeDetails(NamespaceContext nsc,String exprString,String filePath) throws XPathExpressionException{
		
		List<String> list = new ArrayList<String>();
		XPathFactory factory = XPathFactory.newInstance();

		// 2. Use the XPathFactory to create a new XPath object
		XPath xpath = factory.newXPath();
		
		xpath.setNamespaceContext(nsc);

		// 3. Compile an XPath string into an XPathExpression
		XPathExpression expression = xpath.compile(exprString);
 
		// 4. Evaluate the XPath expression on an input document
		Node result = (Node)expression.evaluate(new org.xml.sax.InputSource(filePath),XPathConstants.NODE );
		
	
		return result;
	}
	
}
