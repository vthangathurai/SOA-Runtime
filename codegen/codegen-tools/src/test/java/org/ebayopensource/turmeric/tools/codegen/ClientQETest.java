package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.fail;

import java.io.File;

import junit.framework.Assert;

import org.ebayopensource.turmeric.junit.asserts.XmlAssert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author shrao
 *
 */

public class ClientQETest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	public ClientQETest()
	{
		
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
	public  void client() throws Exception {
		
		String testArgs[] =  new String[] {
				"-servicename","HelloWorldService",
				"-genType", "Client",
				"-interface","org/ebayopensource/turmeric/tools/codegen/IHelloWorld.java",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
	
			};	
		
		performDirectCodeGen(testArgs, binDir);
		
		String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/client/config/GlobalClientConfig.xml";
		
		String goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-meta-src/META-INF/soa/client/config/GlobalClientConfig.xml";
		
		assertFileExists(genPath);
		XmlAssert.assertEquals(readFileAsString(goldPath), readFileAsString(genPath));
		
	}
}
