package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;

import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author shrao
 *
 */

public class MissingInputExcepnTest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	public MissingInputExcepnTest(){}


	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	

	@Before
	public void init() throws Exception{
		mavenTestingRules.setFailOnViolation(false);
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		
		
		}

	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test(expected= MissingInputOptionException.class)
	
	public  void missingInputExcepn() throws Exception {
		
		String testArgs1[] =  new String[] {	
				"-genType","All",
				"-class","org.ebayopensource.qaservices.helloworld.intf.gen.HelloWorldServiceImplSkeleton.class",
				"-sicn","org.ebayopensource.qaservices.helloworld.intf.gen",
				"-serviceName","HelloWorldService", 
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				"-dontprompt"};
				
			
			
					performDirectCodeGen(testArgs1, binDir);
			
	}
}
