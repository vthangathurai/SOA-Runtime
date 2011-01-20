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
public class ServerTest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	public ServerTest(){}


	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	

	@Before
	public void init() throws Exception{
		
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		
		
		}
	
	/*Sep- 10 2009,Changed the vanilla copy to adapt to the changes for "Remove version for Service Config.xml.Removed the current version element from vanilla copy"*/

	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void server() throws Exception {
		
		String testArgs1[] =  new String[] {	
				"-genType","Server",
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
		 
		  genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/common/config/HelloWorldService/TypeMappings.xml";
			
		  goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-meta-src/META-INF/soa/common/config/HelloWorldService/TypeMappings.xml";
				
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
		 
		 
		 genPath = destDir.getAbsolutePath() + "/gen-meta-src/WEB-INF/web.xml";
			
	}
}
