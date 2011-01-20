package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;

import org.ebayopensource.turmeric.tools.codegen.exception.PreValidationFailedException;
import org.junit.Before;
import org.junit.Test;

public class PreValidationFailedExceptionTest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	public PreValidationFailedExceptionTest(){}


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
	@Test(expected= PreValidationFailedException.class)
	public  void preValidationFailedException() throws Exception {
		String testArgs1[] =  new String[] {	
				"-genType","All",
				"-class","org.ebayopensource.qaservices.helloworld.intf.gen.HelloWorldServiceImplSkeleton.class",
				"-sicn","org.ebayopensource.qaservices.helloworld.intf.gen.",
				"-gin","HelloWorldInterface",
				"-serviceName","HelloWorldService", 
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				"-dontprompt"};
				
			
			
		 performDirectCodeGen(testArgs1, binDir);
	}
}
