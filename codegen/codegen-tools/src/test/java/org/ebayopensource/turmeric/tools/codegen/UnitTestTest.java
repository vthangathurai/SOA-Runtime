package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author svaddi
 *
 */
public class UnitTestTest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	public UnitTestTest(){}


	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	

	@Before
	public void init() throws Exception{
		
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		
		
		}
	@Test
	public  void unitTest() throws Exception {
		
		
		String testArgs1[] =  new String[] {	
				"-genType","UnitTest",
				"-interface","org.ebayopensource.turmeric.tools.codegen.IHelloWorld",
				"-serviceName","HelloWorldService", 
		        "-cn","HelloWorld",
		        "-environment","production",
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				};
				
			
			
		 performDirectCodeGen(testArgs1, binDir);
		 
		 
		 
		 String genPath = destDir.getAbsolutePath() + "/gen-test/org/ebayopensource/turmeric/tools/codegen/test/HelloWorldServiceTest.java";
			
		 String goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-src/org/ebayopensource/turmeric/tools/codegen/test/HelloWorldServiceTest.java";
				
		 assertFileExists(genPath);
		 Assert.assertTrue(compareTwoFiles(genPath, goldPath));
		
	}
}
