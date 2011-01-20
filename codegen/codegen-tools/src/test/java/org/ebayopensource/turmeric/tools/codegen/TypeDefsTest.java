package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.fail;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author svaddi
 *
 */
public class TypeDefsTest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	public TypeDefsTest(){}


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
	
	public  void typeDefs() throws Exception {
		
		String testArgs1[] =  new String[] {	
				"-genType","TypeDefs",
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
		
		 
			
		 String genPath = destDir.getAbsolutePath() + "/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/HelloWorldServiceTypeDefsBuilder.java";
			
		 String goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/HelloWorldServiceTypeDefsBuilder.java";
				
		 assertFileExists(genPath);
		 Assert.assertTrue(compareTwoFiles(genPath, goldPath));
		
			
		}



}
