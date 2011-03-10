package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
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
	
		mavenTestingRules.setFailOnViolation(false);	
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		prDir = testingdir.getDir();
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreWhitespace(true);
		
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
		
		String genstr = readFileAsString(path);
		String goldstr = readFileAsString(goldPath);
		
		assertFileExists(path);
		XMLAssert.assertXMLEqual(genstr, goldstr);
		
		
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
				
				String genstr = readFileAsString(path);
				String goldstr = readFileAsString(goldPath);
				
				assertFileExists(path);
				
				Diff diff = new Diff(goldstr, genstr);
				
				diff.overrideElementQualifier(new ElementNameAndAttributeQualifier("xml-namespace"));
				Assert.assertTrue(diff.similar());
		
		
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
				
				String genstr = readFileAsString(path);
				String goldstr = readFileAsString(goldPath);
				
				assertFileExists(path);
				Diff diff = new Diff(goldstr, genstr);
				
				diff.overrideElementQualifier(new ElementNameAndAttributeQualifier("xml-namespace"));
				Assert.assertTrue(diff.similar());
		
	}
}
