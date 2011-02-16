package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nagnihotri
 * 
 */
public class WsdlToMnsQETest extends AbstractServiceGeneratorTestCase {
	/**
	 * @param name
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */

	/*
	 * protected void setUp() throws Exception { super.setUp(); }
	 * 
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 * 
	 * @After protected void tearDown() throws Exception { super.tearDown(); }
	 */
	/*
	 * public void testDispatcher() { String antBinPath =
	 * System.getenv("ANT_HOME") + File.separator + "bin" + File.separator +
	 * "ant.bat"; String output = Utils.runCommand(antBinPath + " -buildfile
	 * .//AntTests/build.xml testDispatcher"); assertTrue(output.contains("BUILD
	 * SUCCESSFUL")); assertFalse(output.contains("Exception"));
	 * assertFalse(output.contains("class is not found"));
	 * assertFalse(output.contains("Could not find file")); }
	 */
	/**
	 * @check Exceptions need to be handled
	 */
	
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
		
		
		}
	
	
	
	/* Replaced the vanilla copy with the generated copy.Ordering was incorrect*/
	@Test
	public void wsdlToMns() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("WSDLwithdiffnamespaces.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","WsdlConversionToMns",
		"-serviceName","TestService1",
		"-wsdl",wsdl.getAbsolutePath(),
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath() };
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/services/wsdl/TestService1_mns.wsdl";
		String goldPath = getTestResrcDir() +"/testservice1/gen-meta-src/soa/services/wsdl/TestService1_mns.wsdl";
		
		assertFileExists(path);
		
		
		
	}
	/* Updated the  vanilla copy with the generated copy for changes in TM el*/
	@Test
	public void mnsTest() throws Exception {
		File wsdl = getCodegenQEDataFileInput("TestService1_mns.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","All",
		"-serviceName","TestService1",
		"-wsdl",wsdl.getAbsolutePath(),
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath() };
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/common/config/TestService1/TypeMappings.xml";
		String goldPath = getTestResrcDir() +"/testservice1//gen-meta-src/META-INF/soa/common/config/TestService1/TypeMappings.xml";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));
	}

	// for testing wsdl second one
	/* Replaced the vanilla copy with the generated copy.Ordering was incorrect*/
	@Test
	public void testingwsdlbyTestingWsdl() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("Testing.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","WsdlConversionToMns",
		"-serviceName","TestService",
		"-wsdl",wsdl.getAbsolutePath(),
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath() };
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/services/wsdl/TestService_mns.wsdl";
		String goldPath = getTestResrcDir() +"/testservice1/gen-meta-src/soa/services/wsdl/TestService_mns.wsdl";
		
		assertFileExists(path);
		
		
	}
	/* Updated the  vanilla copy with the generated copy for changes in TM el*/
	@Test
	public void mnsTestByTestingWsdl() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("TestService_mns.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","All",
		"-serviceName","TestService",
		"-wsdl",wsdl.getAbsolutePath(),
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath() };
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/common/config/TestService/TypeMappings.xml";
		String goldPath = getTestResrcDir() +"/testservice1//gen-meta-src/META-INF/soa/common/config/TestService/TypeMappings.xml";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));	
		
	
	}

	// TEST FOR WSDLwithmanyannotations.wsdl
	@Test
	public void testingwsdlbyWSDLwithotherPrefix() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("WSDLwithotherPrefix.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","WsdlConversionToMns",
		"-serviceName","TestService3",
		"-wsdl",wsdl.getAbsolutePath(),
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath() };
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/services/wsdl/TestService3_mns.wsdl";
		String goldPath = getTestResrcDir() +"/testservice1/gen-meta-src/soa/services/wsdl/TestService3_mns.wsdl";
		
		assertFileExists(path);
		
		
	}

	@Test
	/* Updated the  vanilla copy with the generated copy for changes in TM el*/
	public void mnsToTypeLibraryByWSDLwithotherPrefix() throws Exception {
		File wsdl = getCodegenQEDataFileInput("TestService3_mns.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","All",
		"-serviceName","TestService3",
		"-wsdl",wsdl.getAbsolutePath(),
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath() };
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/common/config/TestService3/TypeMappings.xml";
		String goldPath = getTestResrcDir() +"/testservice1//gen-meta-src/META-INF/soa/common/config/TestService3/TypeMappings.xml";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));	
		
		
	}

	// TEST FOR WSDLwithmanyannotations.wsdl
	
	@Test
	public void testingwsdlbyWSDLwithmanyannotations() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("WSDLwithmanyannotations.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","WsdlConversionToMns",
		"-serviceName","TestService2",
		"-wsdl",wsdl.getAbsolutePath(),
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath() };
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/services/wsdl/TestService2_mns.wsdl";
		String goldPath = getTestResrcDir() +"/testservice1/gen-meta-src/soa/services/wsdl/TestService2_mns.wsdl";
		
		assertFileExists(path);
		
		
	}

	@Test
	/* Updated the  vanilla copy with the generated copy for changes in TM el*/
	public void mnsToTypeLibraryByWSDLwithmanyannotations() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("TestService2_mns.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","All",
		"-serviceName","TestService2",
		"-wsdl",wsdl.getAbsolutePath(),
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath() };
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/common/config/TestService2/TypeMappings.xml";
		String goldPath = getTestResrcDir() +"/testservice1//gen-meta-src/META-INF/soa/common/config/TestService2/TypeMappings.xml";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));
		
		

	}

	// TEST FOR TestService4_mns.wsdl
	@Test
	/* Updated the  vanilla copy with the generated copy for changes in TM el*/
	public void mnsToTypeLibraryByWSDLTestService4() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("TestService4_mns.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","All",
		"-serviceName","TestService4",
		"-wsdl",wsdl.getAbsolutePath(),
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath() };
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/common/config/TestService4/TypeMappings.xml";
		String goldPath = getTestResrcDir() +"/testservice1//gen-meta-src/META-INF/soa/common/config/TestService4/TypeMappings.xml";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));
		
		
	}

	// TEST FOR TestService5_mns.wsdl
	@Test
	/* Updated the  vanilla copy with the generated copy for changes in TM el*/
	public void mnsToTypeLibraryByWSDLTestService5() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("TestService5_mns.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","All",
		"-serviceName","TestService5",
		"-wsdl",wsdl.getAbsolutePath(),
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath() };
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/common/config/TestService5/TypeMappings.xml";
		String goldPath = getTestResrcDir() +"/testservice1//gen-meta-src/META-INF/soa/common/config/TestService5/TypeMappings.xml";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));
		
		
	}

	// TEST FOR TestService6_mns.wsdl
	@Test
	/* Updated the  vanilla copy with the generated copy for changes in TM el*/
	public void mnsToTypeLibraryByWSDLTestService6() throws Exception {
		File wsdl = getCodegenQEDataFileInput("TestService6_mns.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","All",
		"-serviceName","TestService6",
		"-wsdl",wsdl.getAbsolutePath(),
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath() };
		
		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+ "/gen-meta-src/META-INF/soa/common/config/TestService6/TypeMappings.xml";
		String goldPath = getTestResrcDir() +"/testservice1//gen-meta-src/META-INF/soa/common/config/TestService6/TypeMappings.xml";
		
		assertFileExists(path);
		Assert.assertTrue(compareTwoFiles(path, goldPath));
		
		
	}

}
