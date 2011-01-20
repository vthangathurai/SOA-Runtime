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
public class TestClientTest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	public TestClientTest(){}


	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	

	@Before
	public void init() throws Exception{
	
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		
		
		}
	/*Sep- 10,2009 The vanilla copy of base consumer updated for MCC feature.The constructor of the base consumers takes in environment and client name as argument
	 * and alsoservice creation takes in these two arguments.Two instance variables are created for the environment and client name.
	 */

	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void testClient() throws Exception {
		
		
		String testArgs1[] =  new String[] {	
				"-genType","Consumer",
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
		
		 
			
		 String genPath = destDir.getAbsolutePath() + "/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseHelloWorldServiceConsumer.java";
			
		 String goldPath = getTestResrcDir() + "/HelloWorldConsumer/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/BaseHelloWorldServiceConsumer.java";
				
		 assertFileExists(genPath);
		 Assert.assertTrue(compareTwoFiles(genPath, goldPath));
		 
		
	}
}
