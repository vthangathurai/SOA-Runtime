package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @author shrao
 *
 */
public class ServerNoConfigTest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	public ServerNoConfigTest(){}


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
	public  void serverNoConfigTest() throws Exception {
		
		String testArgs1[] =  new String[] {	
				"-genType","ServerNoConfig",
				"-interface","org.ebayopensource.turmeric.tools.codegen.IHelloWorld",
				"-serviceName","HelloWorldService", 
				"-sicn","org.ebayopensource.qaservices.helloworld.intf.gen.HelloWorldServiceImplSkeleton",
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				};
				
			
			
		 performDirectCodeGen(testArgs1, binDir);
		 
		
		 String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/common/config/HelloWorldService/TypeMappings.xml";
			
		 String goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-meta-src/META-INF/soa/common/config/HelloWorldService/TypeMappings.xml";
				
		 assertFileExists(genPath);
		 Assert.assertTrue(compareTwoFiles(genPath, goldPath));	
		 
		 
		  genPath = destDir.getAbsolutePath() + "/gen-src/service/org/ebayopensource/qaservices/helloworld/intf/gen/HelloWorldServiceImplSkeleton.java";
			
		  goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-src/service/org/ebayopensource/qaservices/helloworld/intf/gen/HelloWorldServiceImplSkeleton.java";
				
		 assertFileExists(genPath);
		 Assert.assertTrue(compareTwoFiles(genPath, goldPath));	
		 
		 
		 genPath = destDir.getAbsolutePath() + "/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/HelloWorldServiceTypeDefsBuilder.java";
			
		  goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-src/org/ebayopensource/turmeric/tools/codegen/gen/HelloWorldServiceTypeDefsBuilder.java";
				
		 assertFileExists(genPath);
		 Assert.assertTrue(compareTwoFiles(genPath, goldPath));	
		 
		 
		 genPath = destDir.getAbsolutePath() + "/gen-web-content/WEB-INF/web.xml";
			
		  goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-meta-src/WEB-INF/web.xml";
				
		 assertFileExists(genPath);
		 Assert.assertTrue(compareTwoFiles(genPath, goldPath));	
			
	}
}
