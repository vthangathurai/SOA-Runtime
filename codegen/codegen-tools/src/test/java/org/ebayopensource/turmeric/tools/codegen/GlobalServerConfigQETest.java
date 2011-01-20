package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author svaddi
 *
 */
public class GlobalServerConfigQETest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	public GlobalServerConfigQETest(){}

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
	public  void globalServerConfig() throws Exception {
		
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
		
		String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/services/config/GlobalServiceConfig.xml";
		
		String goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-meta-src/META-INF/soa/services/config/GlobalServiceConfig.xml";
			
		assertFileExists(genPath);
		Assert.assertTrue(compareTwoFiles(genPath, goldPath));
		
	
	}
}
