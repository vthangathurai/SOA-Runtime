package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;


/**
 * @author shrao
 *
 */
public class HelloWorldWSDLSvcQETest extends AbstractServiceGeneratorTestCase{
	
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
	 * @param name
	 */
	public HelloWorldWSDLSvcQETest(){}

	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void interfaceHelloWSDLSvc(){
		
		File wsdl = getCodegenQEDataFileInput("HelloWorldService3.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","HelloWorldService",
				"-genType", "Interface",
				"-gip","org.ebayopensource.qaservices.helloworldwsdlservice.intf",
				"-namespace","http://www.ebayopensource.org/soaframework/service/HelloWorldWSDLService",
				"-wsdl",wsdl.getAbsolutePath(),
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
	
			};	
		
		try {
			performDirectCodeGen(testArgs, binDir);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allHelloWSDLSvc() {
		
		
		File wsdl = getCodegenQEDataFileInput("HelloWorldService3.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","HelloWorldService",
				"-genType", "Interface",
				"-gip","org.ebayopensource.qaservices.helloworldwsdlservice.intf",
				"-namespace","http://www.ebayopensource.org/soaframework/service/HelloWorldWSDLService",
				"-wsdl",wsdl.getAbsolutePath(),
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
	
			};	
		
		
		String testArgs1[] =  new String[] {
				"-servicename","HelloWorldService",
				"-genType", "All",
				"-interface","org.ebayopensource.qaservices.helloworldwsdlservice.intf.HelloWorldService",
				"-wsdl",wsdl.getAbsolutePath(),
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
	
			};	
		
		try {
			performDirectCodeGen(testArgs, binDir);
			performDirectCodeGen(testArgs1, binDir);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	
	}




}
