package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @author rpallikonda
 *
 */
public class ServerConfigTest  extends AbstractServiceGeneratorTestCase{

	/**
	 * @param name
	 */
	public ServerConfigTest(){}


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
	public  void serverConfig() throws Exception {
		String testArgs1[] =  new String[] {	
				"-genType","ServerConfig",
				"-interface","org.ebayopensource.turmeric.tools.codegen.IHelloWorld",
				"-serviceName","HelloWorldService", 
				"-sicn","org.ebayopensource.qaservices.helloworld.intf.gen.HelloWorldServiceImplSkeleton",
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				};
				
			
			
		 performDirectCodeGen(testArgs1, binDir);
		 
		
		 String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/services/config/HelloWorldService/ServiceConfig.xml";
			
		 String goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-meta-src/META-INF/soa/services/config/HelloWorldService/ServiceConfig.xml";
				
		 assertFileExists(genPath);
		 Assert.assertTrue(compareTwoFiles(genPath, goldPath));	
			
	}
}
