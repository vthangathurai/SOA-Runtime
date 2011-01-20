package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
public class Bug561133Test  extends AbstractServiceGeneratorTestCase{
	
	
	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	NamespaceContextImpl nsc;

	@Before
	public void init() throws Exception{
		
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		nsc = new NamespaceContextImpl();
		
		
		}
	
	@Test
	public void testBug561133() throws Exception
	{
		
		File wsdl = getCodegenQEDataFileInput("GoogleNewServiceV1.wsdl");
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
	
			};	
		
		
		try{
			performDirectCodeGen(testArgs, binDir);
		
		assertTrue(false);}
		catch(Exception e){
			
			assertTrue(true);
			(e.getMessage()).contains("Duplicate operation");
		}
		
		
		
	}
	
	

}
