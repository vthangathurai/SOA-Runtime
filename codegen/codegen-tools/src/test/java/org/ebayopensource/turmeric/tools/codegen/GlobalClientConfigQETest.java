package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.ebayopensource.turmeric.junit.asserts.XmlAssert;
import org.junit.Before;
import org.junit.Test;


/**
 * @author skale
 *
 */
public class GlobalClientConfigQETest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	public GlobalClientConfigQETest(){
		
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
	public  void globalClientConfig() throws Exception {
		
		
		String testArgs1[] =  new String[] {	
				"-genType","GlobalClientConfig", 
				"-interface","org/ebayopensource/turmeric/tools/codegen/IHelloWorld.java",
				"-serviceName","HelloWorldService", 
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath() };
				
			
				performDirectCodeGen(testArgs1, binDir);
				
				String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/client/config/GlobalClientConfig.xml";
				
				String goldPath = getTestResrcDir() + "/HelloWorldIntf/gen-meta-src/META-INF/soa/client/config/GlobalClientConfig.xml";
					
				assertFileExists(genPath);
				XmlAssert.assertEquals(readFileAsString(goldPath), readFileAsString(genPath));
	
	
	}


}
