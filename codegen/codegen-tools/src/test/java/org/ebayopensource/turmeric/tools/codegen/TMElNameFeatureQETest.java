package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.fail;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


public class TMElNameFeatureQETest extends AbstractServiceGeneratorTestCase {
	
	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	NamespaceContextImpl nsc;

	@Before
	public void init() throws Exception{
	
		mavenTestingRules.setFailOnViolation(false);
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		nsc = new NamespaceContextImpl();
		
		
		}
	
	@Test
	public void testTmElNameFeatureByAcc() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","TypeMappings", 
		"-serviceName","AccountService", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-slayer","COMMON", 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/common/config/AccountService/TypeMappings.xml";
		
		String goldPath = getTestResrcDir() + "/TMELImpl/gen-meta-src/META-INF/soa/common/config/AccountService/TypeMappings.xml";
			
		assertFileExists(genPath);
		Assert.assertTrue(compareTwoFiles(genPath, goldPath));
		
	
	}
	@Test
	public void testTmElNameFeatureByAccOP() throws Exception {
		
		File wsdl = getCodegenQEDataFileInput("AccountServiceOP.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","TypeMappings", 
		"-serviceName","AccountServiceOP", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-slayer","COMMON", 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/common/config/AccountServiceOP/TypeMappings.xml";
		
		String goldPath = getTestResrcDir() + "/TMELImpl/gen-meta-src/META-INF/soa/common/config/AccountServiceOP/TypeMappings.xml";
			
		assertFileExists(genPath);
		Assert.assertTrue(compareTwoFiles(genPath, goldPath));
	
	}
	@Test
	public void testTmElNameFeatureByWsdlWithAnnotations() throws Exception {
		
		
		
		File wsdl = getCodegenQEDataFileInput("WSDLwithmanyannotations.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","TypeMappings", 
		"-serviceName","TestService2", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-slayer","COMMON", 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/common/config/TestService2/TypeMappings.xml";
		
		String goldPath = getTestResrcDir() + "/TMELImpl/gen-meta-src/META-INF/soa/common/config/TestService2/TypeMappings.xml";
			
		assertFileExists(genPath);
		Assert.assertTrue(compareTwoFiles(genPath, goldPath));

	}
	@Test
	public void testTmElNameFeatureByWsdlWithPrefix() throws Exception {
		File wsdl = getCodegenQEDataFileInput("WSDLwithotherPrefix.wsdl");
		String testArgs[] =  new String[] {	
		"-genType","TypeMappings", 
		"-serviceName","TestService3", 
		"-wsdl",wsdl.getAbsolutePath(), 
		"-slayer","COMMON", 
		"-scv","1.0.0", 
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(), 
		"-bin",binDir.getAbsolutePath() };
		
		
		performDirectCodeGen(testArgs, binDir);
		
		String genPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/common/config/TestService3/TypeMappings.xml";
		
		String goldPath = getTestResrcDir() + "/TMELImpl/gen-meta-src/META-INF/soa/common/config/TestService3/TypeMappings.xml";
			
		assertFileExists(genPath);
		Assert.assertTrue(compareTwoFiles(genPath, goldPath));
		
	
	}
}
