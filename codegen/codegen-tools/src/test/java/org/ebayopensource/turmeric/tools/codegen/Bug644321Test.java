package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class Bug644321Test  extends AbstractServiceGeneratorTestCase{

	File destDir = null;
	File prDir = null;
	File binDir = null;
	File Dir = null;
	File tmpDir = null;
	
	NamespaceContextImpl nsc;

	@Before
	public void init() throws Exception{
		
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		tmpDir = testingdir.getFile("tmp");
		nsc = new NamespaceContextImpl();
		
		
		}
	
	@Test
	public void testBug644321() throws Exception
	{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath()
	
			};	
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-interface","org/ebayopensource/turmeric/common/v1/services/NewService.java",
				"-src", destDir.getAbsolutePath() + "/gen-src/client",
				"-dest", tmpDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-jdest",tmpDir.getAbsolutePath()+"/src",
				"-cn","ClientTest"
	
			};	
		
		
		performDirectCodeGen(testArgs1, binDir);
		performDirectCodeGen(testArgs, binDir);
		
		String path = tmpDir.getAbsolutePath() +"/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseNewServiceConsumer.java";
		
	 	File baseConsumer = new File(path);
		assertTrue(baseConsumer.exists());
		
		
		
	}
	
	

	
}
