package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class ToolsUpdateNsGenTypeServerClientConfig extends AbstractServiceGeneratorTestCase {

	String namespace;
	String marketplace_ns;
	File destDir = null;
	File prDir = null;
	File binDir = null;
	


	@Before
	public void init() throws Exception{
		
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		
		namespace = "http://www.ebayopensource.org/new/namespace";
		marketplace_ns = "http://www.ebayopensource.org/turmeric/common/v1/services";
		}
	
	

	
	

@Test
// after 2.4 service name and namespace is removed.
public void generateServiceConfig() throws Exception{
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	String testArgs[] =  new String[] {	
			"-genType","ServiceMetadataProps",
			"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorServiceSkeletonInterface",
			"-serviceName","AccountService", 
			"-namespace","http://www.ebayopensource.org/new/namespace",
			"-scv","1.0.0", 
			"-dest",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(), 
			"-bin",binDir.getAbsolutePath(),
			};
			
		
		
	 performDirectCodeGen(testArgs, binDir);

	 String testArgs1[] =  new String[] {	
				"-genType","ServerConfig",
				"-wsdl",wsdl.getAbsolutePath(),
				"-serviceName","AccountService", 
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				};
	 performDirectCodeGen(testArgs1, binDir);
	 boolean check = false;
		performDirectCodeGen(testArgs1,binDir);
		String serviceConfigpath = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/config/AccountService/ServiceConfig.xml";
		File file = new File(serviceConfigpath);
		assertTrue(file.exists());
		List<String> firstFile = FileUtils.readLines(file);
		for(String s: firstFile){
			if(s.contains(namespace))
				check = true;
		}
		assertFalse(check);
}






@Test

public void generateClientConfig() throws Exception{
	
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	String testArgs[] =  new String[] {	
			"-genType","ServiceMetadataProps",
			"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorServiceSkeletonInterface",
			"-serviceName","AccountService", 
			"-namespace","http://www.ebayopensource.org/new/namespace",
			"-scv","1.0.0", 
			"-dest",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(), 
			"-bin",binDir.getAbsolutePath(),
			};
			
		
		
	 performDirectCodeGen(testArgs, binDir);

	 String testArgs1[] =  new String[] {	
				"-genType","ClientConfig",
				"-wsdl",wsdl.getAbsolutePath(),
				"-serviceName","AccountService", 
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				};
	 performDirectCodeGen(testArgs1, binDir);
	 boolean check = false;
		performDirectCodeGen(testArgs1,binDir);
		String clientConfigpath = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/AccountService/ClientConfig.xml";
		File file = new File(clientConfigpath);
		assertTrue(file.exists());
		List<String> firstFile = FileUtils.readLines(file);
		for(String s: firstFile){
			if(s.contains(marketplace_ns))
				check = true;
		}
		assertFalse(check);
}


@Test
//after 2.4 service name and namespace is removed.
public void generateServerConfigWithNs()throws Exception{
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	String testArgs[] =  new String[] {	
			"-genType","ServiceMetadataProps",
			"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorServiceSkeletonInterface",
			"-serviceName","AccountService", 
			"-namespace","http://www.ebayopensource.org/new/namespace",
			"-scv","1.0.0", 
			"-dest",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(), 
			"-bin",binDir.getAbsolutePath(),
			};
			
		
		
	 performDirectCodeGen(testArgs, binDir);

	 String testArgs1[] =  new String[] {	
				"-genType","ServerConfig",
				"-wsdl",wsdl.getAbsolutePath(),
				"-serviceName","AccountService",
				"-namespace","http://www.ebayopensource.org/new/namespace",
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				};
	 performDirectCodeGen(testArgs1, binDir);

	 boolean check = false;
		performDirectCodeGen(testArgs1,binDir);
		String serviceConfigpath = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/config/AccountService/ServiceConfig.xml";
		File file = new File(serviceConfigpath);
		assertTrue(file.exists());
		List<String> firstFile = FileUtils.readLines(file);
		for(String s: firstFile){
			if(s.contains(namespace))
				check = true;
		}
		assertFalse(check);
}


@Test
public void generateClientConfigWithNs()throws Exception{
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	String testArgs[] =  new String[] {	
			"-genType","ServiceMetadataProps",
			"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorServiceSkeletonInterface",
			"-serviceName","AccountService", 
			"-namespace","http://www.ebayopensource.org/new/namespace",
			"-scv","1.0.0", 
			"-dest",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(), 
			"-bin",binDir.getAbsolutePath(),
			};
			
		
		
	 performDirectCodeGen(testArgs, binDir);

	 String testArgs1[] =  new String[] {	
				"-genType","ClientConfig",
				"-wsdl",wsdl.getAbsolutePath(),
				"-serviceName","AccountService",
				"-namespace","http://www.ebayopensource.org/new/namespace",
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				};
	 performDirectCodeGen(testArgs1, binDir);
	 
	 boolean check = false;
		performDirectCodeGen(testArgs1,binDir);
		String clientConfigpath = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/AccountService/ClientConfig.xml";
		File file = new File(clientConfigpath);
		assertTrue(file.exists());
		List<String> firstFile = FileUtils.readLines(file);
		for(String s: firstFile){
			if(s.contains(namespace))
				check = true;
		}
		assertTrue(check);
}

/*@Test
//after 2.4 service name and namespace is removed.
public void generateServerConfigWithNsInCodegenAndMetadata()throws Exception{
    input1.setNamespace("http://www.ebay.com/new/namespace");
	sgen.startCodeGen(this.setInputParameters(input));
	sgen.startCodeGen(this.setInputParameters(input1));
	
	File file = new File(CodegenUtils.getPath("ServiceConfig","NewService","AntTests/output/"));
	assertTrue(file.exists());
	
	assertFalse(Utils.searchStringInFile(CodegenUtils.getPath("ServiceConfig","NewService","AntTests/output/"),namespace));
	
	file.delete();
}*/


/*@Test
public void generateClientConfigWithNsInCodegenAndMetadata()throws Exception{
	
    input2.setNamespace("http://www.ebay.com/new/namespace");
	sgen.startCodeGen(this.setInputParameters(input));
	sgen.startCodeGen(this.setInputParameters(input2));
	
	File file = new File(CodegenUtils.getPath("ClientConfig","NewService","AntTests/output/"));
	assertTrue(file.exists());
	
	assertTrue(Utils.searchStringInFile(CodegenUtils.getPath("ClientConfig","NewService","AntTests/output/"),namespace));
	
	file.delete();
	
}
*/


@Test
//after 2.4 service name and namespace is removed.
public void generateServerConfigNoNs()throws Exception{

	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	String testArgs1[] =  new String[] {	
			"-genType","ServerConfig",
			"-wsdl",wsdl.getAbsolutePath(),
			"-serviceName","AccountService",
			"-scv","1.0.0", 
			"-dest",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(), 
			"-bin",binDir.getAbsolutePath(),
			};
 performDirectCodeGen(testArgs1, binDir);
	
 boolean check = false;
	performDirectCodeGen(testArgs1,binDir);
	String serviceConfigpath = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/config/AccountService/ServiceConfig.xml";
	File file = new File(serviceConfigpath);
	assertTrue(file.exists());
	List<String> firstFile = FileUtils.readLines(file);
	for(String s: firstFile){
		if(s.contains(namespace))
			check = true;
	}
	assertFalse(check);
}


@Test
public void generateClientConfigNoNs()throws Exception{
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	String testArgs1[] =  new String[] {	
			"-genType","ClientConfig",
			"-wsdl",wsdl.getAbsolutePath(),
			"-serviceName","AccountService", 
			"-scv","1.0.0", 
			"-dest",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(), 
			"-bin",binDir.getAbsolutePath(),
			};
 performDirectCodeGen(testArgs1, binDir);
	
 boolean check = false;
	performDirectCodeGen(testArgs1,binDir);
	String clientConfigpath = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/AccountService/ClientConfig.xml";
	File file = new File(clientConfigpath);
	assertTrue(file.exists());
	List<String> firstFile = FileUtils.readLines(file);
	for(String s: firstFile){
		if(s.contains(marketplace_ns))
			check = true;
	}
	assertTrue(check);
	
}


@Test(expected = Exception.class)

public void generateServerConfigNullStringNs()throws Exception{
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	String testArgs1[] =  new String[] {	
			"-genType","ServerConfig",
			"-wsdl",wsdl.getAbsolutePath(),
			"-serviceName","AccountService",
			"-namespace","",
			"-scv","1.0.0", 
			"-dest",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(), 
			"-bin",binDir.getAbsolutePath(),
			};
	
	performDirectCodeGen(testArgs1, binDir);
	
}


@Test(expected = Exception.class)

public void generateClientConfigNullStringNsNs()throws Exception{
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	String testArgs1[] =  new String[] {	
			"-genType","ClientConfig",
			"-wsdl",wsdl.getAbsolutePath(),
			"-serviceName","AccountService",
			"-namespace","",
			"-scv","1.0.0", 
			"-dest",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(), 
			"-bin",binDir.getAbsolutePath(),
			};
	
	performDirectCodeGen(testArgs1, binDir);
	
	
}




}
