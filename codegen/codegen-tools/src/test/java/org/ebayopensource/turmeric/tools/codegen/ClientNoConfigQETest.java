package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.fail;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rpallikonda
 *
 */
public class ClientNoConfigQETest  extends AbstractServiceGeneratorTestCase{

	/**
	 * @param name
	 */
	public ClientNoConfigQETest() {
		
	}


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
	public  void clientNoConfig() throws Exception {
		
		String testArgs[] =  new String[] {
				"-servicename","HelloWorldService",
				"-genType", "ClientNoConfig",
				"-interface","org/ebayopensource/turmeric/tools/codegen/IHelloWorld.java",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
	
			};	
		
		performDirectCodeGen(testArgs, binDir);
		
		String genPath = destDir.getAbsolutePath() + "/gen-src/org/ebayopensource/turmeric/tools/codegen/AsyncIHelloWorld.java";
		
		String goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-src/org/ebayopensource/turmeric/tools/codegen/AsyncIHelloWorld.java";
			
		assertFileExists(genPath);
		Assert.assertTrue(compareTwoFiles(genPath, goldPath));
		
			
	}
}
