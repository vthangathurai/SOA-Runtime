package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
public class ToolsUpdateNsGenMetadataProps  extends AbstractServiceGeneratorTestCase{
	

	
	String namespace;
	File destDir = null;
	File prDir = null;
	File binDir = null;
	


	@Before
	public void init() throws Exception{
	
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		
		 namespace="http\\://www.ebayopensource.org/new/namespace";

}

	
	
@Test

public void generateMetadataPropsWithNs() throws Exception{
	
	FileInputStream in = null;
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	String testArgs1[] =  new String[] {	
			"-genType","ServiceMetadataProps",
			"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorServiceSkeletonInterface",
			"-serviceName","AccountService", 
			"-namespace","http://www.ebayopensource.org/new/namespace",
			"-scv","1.0.0", 
			"-dest",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(), 
			"-bin",binDir.getAbsolutePath(),
			};
	
	try{
	 performDirectCodeGen(testArgs1, binDir);
	String path = destDir.getAbsolutePath() +"/gen-meta-src/META-INF/soa/common/config/AccountService/service_metadata.properties"; 
	File metadataFile = new File(path);
	assertTrue(metadataFile.exists());
	
	in = new FileInputStream(new File(path));
	Properties pro = new Properties();
	pro.load(in);
	Assert.assertTrue(pro.get("namespace").equals(namespace)); }
	catch(Exception e){
		e.printStackTrace();
	}finally{
		if(in !=null)
		in.close();
	}
	
}

@Test

public void generateMetadataPropsWithoutNs() throws Exception{
	
	FileInputStream in = null;
	
	String testArgs1[] =  new String[] {	
			"-genType","ServiceMetadataProps",
			"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorServiceSkeletonInterface",
			"-serviceName","AccountService", 
			"-scv","1.0.0", 
			"-dest",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(), 
			"-bin",binDir.getAbsolutePath(),
			};
	try{
	 performDirectCodeGen(testArgs1, binDir);
		String path = destDir.getAbsolutePath() +"/gen-meta-src/META-INF/soa/common/config/AccountService/service_metadata.properties"; 
		File metadataFile = new File(path);
		assertTrue(metadataFile.exists());
		
		in = new FileInputStream(new File(path));
		Properties pro = new Properties();
		pro.load(in);
		Assert.assertTrue(pro.get("namespace").equals(null)); }
		catch(Exception e){
			e.printStackTrace();
		}finally{
			if(in != null)
			in.close();
		}
	
}

@Test(expected =Exception.class)

public void generateMetadataPropsWithNullStringForNs() throws Exception{
	
	
	String testArgs1[] =  new String[] {	
			"-genType","ServiceMetadataProps",
			"-interface","org/ebayopensource/qaservices/calculatorservice/intf/CalculatorServiceSkeletonInterface",
			"-serviceName","NewService", 
			"-namespace","",
			"-scv","1.0.0", 
			"-dest",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(), 
			"-bin",binDir.getAbsolutePath(),
			};
	performDirectCodeGen(testArgs1, binDir);
	
	
}

@After
public void deinitialize() throws IOException{

	 
	 testingdir.ensureEmpty();	
	 
}
}






