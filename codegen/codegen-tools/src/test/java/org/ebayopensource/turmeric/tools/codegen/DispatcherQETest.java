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
public class DispatcherQETest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	public DispatcherQETest(){}


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
	public  void dispatcher() throws Exception {
		
		String testArgs1[] =  new String[] {	
				"-genType","ClientConfig", 
				"-interface","org/ebayopensource/turmeric/tools/codegen/IHelloWorld.java",
				"-serviceName","HelloWorldService", 
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
			
				performDirectCodeGen(testArgs1, binDir);
				
				
				String testArgs[] =  new String[] {	
						"-genType","dispatcher", 
						"-interface","org/ebayopensource/turmeric/tools/codegen/IHelloWorld.java",
						"-sicn","org.ebayopensource.qaservices.helloworld.intf.gen.HelloWorldServiceImplSkeleton",
						"-serviceName","HelloWorldService", 
						"-scv","1.0.0", 
						"-dest",destDir.getAbsolutePath(),
						"-src",destDir.getAbsolutePath(), 
						"-bin",binDir.getAbsolutePath() };
						
					
						performDirectCodeGen(testArgs, binDir);
						
						String genPath = destDir.getAbsolutePath() + "/gen-src/org/ebayopensource/qaservices/helloworld/intf/gen/gen/HelloWorldServiceRequestDispatcher.java";
						
						String goldPath = getTestResrcDir() + "/HelloWorldConsumer/gen-src/org/ebayopensource/qaservices/helloworld/intf/gen/gen/HelloWorldServiceRequestDispatcher.java";
							
						assertFileExists(genPath);
						Assert.assertTrue(compareTwoFiles(genPath, goldPath));		
						
						
				
					
	}
}
