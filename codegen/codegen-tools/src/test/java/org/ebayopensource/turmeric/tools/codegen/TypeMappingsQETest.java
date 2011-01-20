package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author skale
 * 
 */
public class TypeMappingsQETest extends AbstractServiceGeneratorTestCase {
	/**
	 * @param name
	 */
	public TypeMappingsQETest(){}
	
	File destDir = null;
	File prDir = null;
	File binDir = null;

	@Before
	public void init() throws Exception{
	
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		prDir = testingdir.getDir();
		
		
		}


	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	/*Sep -16: Updated the vanilla copy for TM el feature change*/
	@Test
	public  void typeMappings() throws Exception {
		
		
		String testArgs[] =  new String[] {	
		"-genType","ClientConfig",
		"-interface","org.ebayopensource.turmeric.tools.codegen.IHelloWorld.java",
		"-serviceName","HelloWorldService",
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath() };
		
		performDirectCodeGen(testArgs, binDir);
		
		String testArgs1[] =  new String[] {	
				"-genType","TypeMappings",
				"-interface","org.ebayopensource.turmeric.tools.codegen.IHelloWorld.java",
				"-serviceName","HelloWorldService",
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath() };
				
		performDirectCodeGen(testArgs1, binDir);
		
		
		String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/common/config/HelloWorldService/TypeMappings.xml";
		String goldPath = getTestResrcDir() +"/typemappings//gen-meta-src/META-INF/soa/common/config/HelloWorldService/TypeMappings.xml";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));
		
		
	}
	
	/**
	 * Negative test case for -ctns option: codgen 211
	 * @throws Exception 
	 */
	@Test
	public  void typeMappings_ctnsInvalidValue() throws Exception {
		
		
		
		File wsdl = getCodegenQEDataFileInput("ShippingCalculatorService.wsdl");
		
		String testArgs[] =  new String[] {	
				"-genType","All",
				"-gip","org.ebayopensource.turmeric.services",
				"-namespace","http://www.ebayopensource.org/soaframework/service/ShippingCalculatorService",
				"-wsdl",wsdl.getAbsolutePath(),
				"-serviceName","ShippingCalculatorService",
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				"-ctns","ht:/22/s"};
				
				performDirectCodeGen(testArgs, binDir);
				
				String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/common/config/ShippingCalculatorService/TypeMappings.xml";
				String goldPath = getTestResrcDir() +"/typemappings//gen-meta-src/META-INF/soa/common/config/ShippingCalculatorService/TypeMappings.xml";
				
				assertFileExists(path);
				Assert.assertTrue(compareTwoFiles(path, goldPath));
		
		
	}
	
	/**
	 * Positive test case for -ctns option: codgen 211
	 * @throws Exception 
	 */
	/*Sep -16: Updated the vanilla copy for TM el feature change*/
	@Test
	public  void typeMappings_ctns() throws Exception {
		
File wsdl = getCodegenQEDataFileInput("ShippingCalculatorService.wsdl");
		
		String testArgs[] =  new String[] {	
				"-genType","All",
				"-gip","org.ebayopensource.turmeric.services",
				"-namespace","http://www.ebayopensource.org/soaframework/service/ShippingCalculatorService",
				"-wsdl",wsdl.getAbsolutePath(),
				"-serviceName","ShippingCalculatorService",
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				"-ctns","https://www.play.com/types"};
				
				performDirectCodeGen(testArgs, binDir);
				
				String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/common/config/ShippingCalculatorService/TypeMappings.xml";
				String goldPath = getTestResrcDir() +"/typemappings//gen-meta-src/META-INF/soa/common/config/ShippingCalculatorService/TypeMappings2.xml";
				
				assertFileExists(path);
				Assert.assertTrue(compareTwoFiles(path, goldPath));
		
	}
}
