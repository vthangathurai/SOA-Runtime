/**
 *
 */
package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author skale
 *
 */
public class SvcOpPropTest extends AbstractServiceGeneratorTestCase {

	/**
	 * @param name
	 */
	public SvcOpPropTest(){}


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
	public  void svcOpProperty() throws Exception {
		String testArgs1[] =  new String[] {	
				"-genType","ServiceOpProps",
				"-interface","org.ebayopensource.turmeric.tools.codegen.IHelloWorld",
				"-serviceName","HelloWorldService", 
				"-sicn","org.ebayopensource.qaservices.helloworld.intf.gen.HelloWorldServiceImplSkeleton",
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				};
				
			
			
		 performDirectCodeGen(testArgs1, binDir);
		 
		 String path = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/services/config/HelloWorldService/service_operations.properties";
		 
		 File svcProps = new File(path);
		 Assert.assertFalse(svcProps.exists());
		 
		
		}
}
