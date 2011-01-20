package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class ToolsUpdateNsGenAll extends AbstractServiceGeneratorTestCase {


	String namespace;
	String namespace_metadata;
	File destDir = null;
	File prDir = null;
	File binDir = null;
	


	@Before
	public void init() throws Exception{
		
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		
		 namespace_metadata="http://www.ebayopensource.com/turmeric/services";
		}

@Test
//after 2.4 service name and namespace is removed.
public void checkServiceConfigForAll() throws Exception{
	
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	String testArgs1[] =  new String[] {	
			"-genType","All",
			"-wsdl",wsdl.getAbsolutePath(),
			"-serviceName","NewService", 
			"-namespace","http://www.ebayopensource.org/turmeric/services",
			"-scv","1.0.0", 
			"-dest",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(), 
			"-bin",binDir.getAbsolutePath(),
			};
			
		
		
	 performDirectCodeGen(testArgs1, binDir);
	
	boolean check = false;
	performDirectCodeGen(testArgs1,binDir);
	String serviceConfigpath = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/config/NewService/ServiceConfig.xml";
	File file = new File(serviceConfigpath);
	assertTrue(file.exists());
	List<String> firstFile = FileUtils.readLines(file);
	for(String s: firstFile){
		if(s.contains(namespace_metadata))
			check = true;
	}
	assertFalse(check);
	String clientConfigpath = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/NewService/ClientConfig.xml";
	File file1 = new File(clientConfigpath);
	assertTrue(file.exists());
	List<String> firstFile1 = FileUtils.readLines(file1);
	for(String s: firstFile1){
		if(s.contains(namespace_metadata))
			check = true;
	}
	assertTrue(check);
}

	
	
	
	









	
	
	
}
