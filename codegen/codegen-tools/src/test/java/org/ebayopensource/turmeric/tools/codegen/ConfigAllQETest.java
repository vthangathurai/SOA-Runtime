package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.ebayopensource.turmeric.junit.asserts.XmlAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author svaddi
 *
 */

public class ConfigAllQETest extends AbstractServiceGeneratorTestCase {
	/**
	 * @param name
	 */
	public ConfigAllQETest(){
		
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
	public  void configAll() throws Exception {
		
		String testArgs[] =  new String[] {
				"-servicename","HelloWorldService",
				"-genType", "ConfigAll",
				"-interface","org/ebayopensource/turmeric/tools/codegen/IHelloWorld.java",
				"-sicn","org.ebayopensource.qaservices.helloworld.intf.gen.HelloWorldServiceImplSkeleton",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
	
			};	
		
		performDirectCodeGen(testArgs, binDir);
		
		String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/services/config/HelloWorldService/ServiceConfig.xml";
		
		String goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-meta-src/META-INF/soa/services/config/HelloWorldService/ServiceConfig.xml";
			
		assertFileExists(genPath);
		Assert.assertTrue(compareTwoFiles(genPath, goldPath));
		
		
		 genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/services/config/GlobalServiceConfig.xml";
		
		 goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-meta-src/META-INF/soa/services/config/GlobalServiceConfig.xml";
			
		assertFileExists(genPath);
		XmlAssert.assertEquals(readFileAsString(goldPath), readFileAsString(genPath));

		
		
		 genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/client/config/HelloWorldService/ClientConfig.xml";
		
		 goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-meta-src/META-INF/soa/client/config/HelloWorldService/ClientConfig.xml";
			
		assertFileExists(genPath);
		Assert.assertTrue(compareTwoFiles(genPath, goldPath));
		
		
		genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/client/config/GlobalClientConfig.xml";
		
		 goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-meta-src/META-INF/soa/client/config/GlobalClientConfig.xml";
			
		assertFileExists(genPath);
		Assert.assertTrue(compareTwoFiles(genPath, goldPath));
			
	}
}
