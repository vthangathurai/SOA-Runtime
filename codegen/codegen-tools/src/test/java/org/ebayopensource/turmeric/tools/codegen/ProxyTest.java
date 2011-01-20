package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author shrao
 *
 */
public class ProxyTest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	public ProxyTest(){}

	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	

	@Before
	public void init() throws Exception{
	
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		
		
		}

	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void proxy() throws Exception {
		
		String testArgs1[] =  new String[] {	
				"-genType","proxy",
				"-interface","org.ebayopensource.turmeric.tools.codegen.IHelloWorld",
				"-serviceName","HelloWorldService", 
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				"-dontprompt"};
				
			
			
		 performDirectCodeGen(testArgs1, binDir);
		 
		
			
		 String genPath = destDir.getAbsolutePath() + "/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/HelloWorldServiceProxy.java";
			
			String goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/HelloWorldServiceProxy.java";
				
			assertFileExists(genPath);
			Assert.assertTrue(compareTwoFiles(genPath, goldPath));	
		
	}
}
