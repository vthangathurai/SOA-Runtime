package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @author skale
 *
 */
public class ClientConfigQETest extends AbstractServiceGeneratorTestCase {

	/**
	 * @param name
	 */
	public ClientConfigQETest() {
	
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
	public  void clientConfig() throws Exception {
		
		String testArgs[] =  new String[] {
				"-servicename","HelloWorldService",
				"-genType", "ClientConfig",
				"-interface","org/ebayopensource/qaservices/helloworld/intf/IHelloWorld.java",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
	
			};	
		
		performDirectCodeGen(testArgs, binDir);
		
		String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/client/config/HelloWorldService/ClientConfig.xml";
		
		String goldPath = getTestResrcDir() + "/HelloWorldConsumer/gen-meta-src/META-INF/soa/client/config/HelloWorldService/ClientConfig.xml";
			
		assertFileExists(genPath);
		Assert.assertTrue(compareTwoFiles(genPath, goldPath));
		

	}
}
