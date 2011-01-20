package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class CodegenAsyncQETest extends AbstractServiceGeneratorTestCase{
		
	File destDir = null;
	File prDir = null;
	File binDir = null;

	Properties intfProper = new Properties();
	@Before
	public void init() throws Exception{
		
		mavenTestingRules.setFailOnViolation(false);
		testingdir.ensureEmpty();
		destDir = getTestDestDir();
		binDir = testingdir.getFile("bin");
		prDir = getTestDestDir();
		
		
		}
	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void asycGenAllFromWsdl() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
				
		"-genType","All",
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf",
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService",
		"-serviceName","CalculatorService",
		"-wsdl",wsdl.getAbsolutePath(),			
		"-slayer","INTERMEDIATE",
		"-scv","1.0.0",
		"-jdest",destDir.getAbsolutePath()+"/gen-src/client",
		"-dest",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath()
		
		};
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/AsyncCalculatorService.java";
		String goldPath = getTestResrcDir() +"/CalculatorService/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/AsyncCalculatorService.java";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));
		String path2 = destDir.getAbsolutePath()+ "/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/gen/CalculatorServiceProxy.java";
		
		String goldPath2 = getTestResrcDir() +"/CalculatorService/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/gen/CalculatorServiceProxy.java";
		assertFileExists(path2);
		Assert.assertTrue(compareTwoFiles(path2, goldPath2));
		String path3 = destDir.getAbsolutePath()+  "/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/gen/BaseCalculatorServiceConsumer.java";
		String goldPath3 = getTestResrcDir() +"/CalculatorService/gen-src/org/ebayopensource/qaservices/calculatorservice/intf/gen/BaseCalculatorServiceConsumer.java";
		
		assertFileExists(path3);
		
		Assert.assertTrue(compareTwoFiles(path3, goldPath3));
	}
	
	
	
	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void asycGenClientFromlWsdl() throws Exception{
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
				
		"-genType","Client",
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf",
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService",
		"-serviceName","CalculatorService",
		"-wsdl",wsdl.getAbsolutePath(),			
		"-slayer","INTERMEDIATE",
		"-scv","1.0.0",
		"-jdest",destDir.getAbsolutePath()+"/gen-src/client",
		"-dest",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath()
		
		};
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/AsyncCalculatorService.java";
		String goldPath = getTestResrcDir() +"/CalculatorService/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/AsyncCalculatorService.java";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));
		String path2 = destDir.getAbsolutePath()+ "/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/gen/CalculatorServiceProxy.java";
		
		String goldPath2 = getTestResrcDir() +"/CalculatorService/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/gen/CalculatorServiceProxy.java";
		assertFileExists(path2);
		Assert.assertTrue(compareTwoFiles(path2, goldPath2));
		
		
		
		
	}
		
	
	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void asycGenClientNoConfigFromlWsdl() throws Exception{
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
				
		"-genType","ClientNoConfig",
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf",
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService",
		"-serviceName","CalculatorService",
		"-wsdl",wsdl.getAbsolutePath(),			
		"-slayer","INTERMEDIATE",
		"-scv","1.0.0",
		"-jdest",destDir.getAbsolutePath()+"/gen-src/client",
		"-dest",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath()
		
		};
		performDirectCodeGen(testArgs, binDir);
		String path = destDir.getAbsolutePath()+ "/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/AsyncCalculatorService.java";
		String goldPath = getTestResrcDir() +"/CalculatorService/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/AsyncCalculatorService.java";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));
		String path2 = destDir.getAbsolutePath()+ "/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/gen/CalculatorServiceProxy.java";
		
		String goldPath2 = getTestResrcDir() +"/CalculatorService/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/gen/CalculatorServiceProxy.java";
		assertFileExists(path2);
		Assert.assertTrue(compareTwoFiles(path2, goldPath2));
	}
	
	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void asycGenProxyFromlWsdl() throws Exception{
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
				
		"-genType","proxy",
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf",
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService",
		"-serviceName","CalculatorService",
		"-wsdl",wsdl.getAbsolutePath(),			
		"-slayer","INTERMEDIATE",
		"-scv","1.0.0",
		"-jdest",destDir.getAbsolutePath()+"/gen-src/client",
		"-dest",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath()
		
		};
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/AsyncCalculatorService.java";
		String goldPath = getTestResrcDir() +"/CalculatorService/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/AsyncCalculatorService.java";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));
		String path2 = destDir.getAbsolutePath()+ "/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/gen/CalculatorServiceProxy.java";
		
		String goldPath2 = getTestResrcDir() +"/CalculatorService/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/gen/CalculatorServiceProxy.java";
		assertFileExists(path2);
		Assert.assertTrue(compareTwoFiles(path2, goldPath2));	
	}
	
	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void asycGenServiceFromWSDLIntfFromlWsdl() throws Exception{
		File wsdl = getCodegenQEDataFileInput("Calc.wsdl");
		String testArgs[] =  new String[] {	
				
		"-genType","ServiceFromWSDLIntf",
		"-gip","org.ebayopensource.qaservices.calculatorservice.intf",
		"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService",
		"-serviceName","CalculatorService",
		"-wsdl",wsdl.getAbsolutePath(),			
		"-slayer","INTERMEDIATE",
		"-scv","1.0.0",
		"-jdest",destDir.getAbsolutePath()+"/gen-src/client",
		"-dest",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath()
		
		};
		
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/AsyncCalculatorService.java";
		String goldPath = getTestResrcDir() +"/CalculatorService/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/AsyncCalculatorService.java";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));
		String path2 = destDir.getAbsolutePath()+ "/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/gen/CalculatorServiceProxy.java";
		
		String goldPath2 = getTestResrcDir() +"/CalculatorService/gen-src/client/org/ebayopensource/qaservices/calculatorservice/intf/gen/CalculatorServiceProxy.java";
		assertFileExists(path2);
		Assert.assertTrue(compareTwoFiles(path2, goldPath2));
	}
	


}
