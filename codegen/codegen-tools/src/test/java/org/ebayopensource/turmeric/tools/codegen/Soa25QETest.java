package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class Soa25QETest extends AbstractServiceGeneratorTestCase{
	
	File destDir = null;
	File prDir = null;
	File binDir = null;
	Properties intfProper = new Properties();
	@Before
	public void init() throws Exception{
		
		
		testingdir.ensureEmpty();
		destDir = getTestDestDir();
		binDir = testingdir.getFile("bin");
		prDir = getTestDestDir();
		
		
		
		intfProper.put("service_interface_class_name", "org.ebayopensource.turmeric.runtime.types.AccountService");
		intfProper.put("service_layer","COMMON");
		intfProper.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\AccountService.wsdl");
		intfProper.put("service_version","1.0.0");
		intfProper.put("admin_name","newadminname");
		intfProper.put("sipp_version","1.1");
		intfProper.put("service_namespace_part","Billing");
		intfProper.put("domainName","ebay");
		intfProper.put("noObjectFactoryGeneration","false");
	}
	
	
	@Test	
//	@Ignore("failing")
	/*FIXME
	 * fix failing tests
	 */
	public void deleteObjectFactoryCase1() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "All",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/runtime/types",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","true"

			};

		intfProper.put("noObjectFactoryGeneration","true");
		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
		
		File file = new File(destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/turmeric/services/ObjectFactory.java");
		assertFalse(file.exists());
		
		file= new File(destDir.getAbsolutePath()+"/gen-src/com/ebay/soaframework/common/types/ObjectFactory.java");
		assertFalse(file.exists());
	}
	
	
	
	@Test
//	@Ignore("failing")
	public void deleteObjectFactoryCase2() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "All",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/services",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","true",
				"-ns2pkg","http://www.ebayopensource.com/turmeric/services=com.ebayopensource.marketplace.something.one,http://www.ebayopensource.org/turmeric/common/v1/types=com.ebayopensource.mypackage"
				

			};

		intfProper.put("noObjectFactoryGeneration","true");
		
		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
		
		File file = new File(destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/marketplace/something/one/ObjectFactory.java");
		assertFalse(file.exists());
		
		file= new File(destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/mypackage/ObjectFactory.java");
		assertFalse(file.exists());
	}
	
	@Test
//	@Ignore("failing")
	public void deleteObjectFactoryCase3() throws Exception{
		
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		String testArgs[] =  new String[] {
				"-servicename","AccountService",
				"-genType", "All",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.com/turmeric/services",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","true",
				"-ns2pkg","http://www.ebayopensource.com/turmeric/services=com.ebayopensource.marketplace.something.one"
				

			};

		intfProper.put("noObjectFactoryGeneration","true");
		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
		
		File file = new File(destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/marketplace/something/one/ObjectFactory.java");
		assertFalse(file.exists());
		
		
	}
	
	
	@Test
//	@Ignore("failing")
	public void deleteObjectFactoryCase4() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "All",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.com/turmeric/services",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","true",
				

			};

		intfProper.put("noObjectFactoryGeneration","true");
		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
		
		File file = new File(destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/turmeric/services/ObjectFactory.java");
		assertFalse(file.exists());
		
		
	}
	
	
	@Test
//	@Ignore("failing")
	public void deleteObjectFactoryCase5() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV1.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "All",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.com/turmeric/services",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath(),
				"-mdest",destDir.getAbsolutePath()+"/meta-src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin",binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","true",
				"-ns2pkg","http://www.ebayopensource.com/turmeric/services=com.ebayopensource.marketplace.something.one"
				

			};

		intfProper.put("noObjectFactoryGeneration","true");
		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
		
		File file = new File(destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/marketplace/something/one/ObjectFactory.java");
		assertFalse(file.exists());
		
		
	}
	
	
	
		
		

	
	
	@Test
	public void deleteObjectFactoryCase7() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("PaypalSvc.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "All",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/runtime/types",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath(),
				"-mdest",destDir.getAbsolutePath()+"/meta-src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","true"
				

			};

		intfProper.put("noObjectFactoryGeneration","true");
		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);		
		File file = new File(destDir.getAbsolutePath()+"/gen-src/fr/virtuoz/ObjectFactory.java");
		assertFalse(file.exists());
		
		file = new File(destDir.getAbsolutePath()+"/gen-src/com/virtuoz/ObjectFactory.java");
		assertFalse(file.exists());
		
		
	}
	
	
	
	@Test
//	@Ignore("failing")
	public void deleteObjectFactoryCase8() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/runtime/types",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/src/test/java/",
				"-mdest",destDir.getAbsolutePath()+"/meta-src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","false"

			};
		intfProper.put("noObjectFactoryGeneration","true");
		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
		
		File file = new File(destDir.getAbsolutePath()+"/src/test/java/org/ebayopensource/turmeric/common/v1/types/ObjectFactory.java");
		assertTrue(file.exists());
	}
	
	
	@Test
//	@Ignore("failing")
	public void donotDeleteObjectFactoryNSOtherThanMP2() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		
	
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/runtime/types",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath(),
				"-mdest",destDir.getAbsolutePath()+"/meta-src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","true"

			};

		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
		
		File file = new File(destDir.getAbsolutePath()+"/gen-src/org/ebayopensource/turmeric/common/v1/types/ObjectFactory.java");
		assertFalse(file.exists());
	}
	
	
	@Test
//	@Ignore("failing")
	public void donotDeleteObjectFactory() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/runtime/types",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/src/test/java/",
				"-mdest",destDir.getAbsolutePath()+"/meta-src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","false"

			};
		

		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
		
		File file = new File(destDir.getAbsolutePath()+"/src/test/java/org/ebayopensource/turmeric/common/v1/types/ObjectFactory.java");
		assertTrue(file.exists());
	}
	
	
	@Test
//	@Ignore("failing")
	public void invalidInputForNoObjectFactoryGeneration() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV1.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/runtime/types",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","True   "
				//Invalid input is taken as false or for any other input other than "true"
			};

		intfProper.put("noObjectFactoryGeneration","klk");
		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
	
		File file = new File(destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/turmeric/common/v1/types/ObjectFactory.java");
		assertFalse(file.exists());
	}
	
	
	
	@Test
//	@Ignore("failing")
	public void invalidInputForNoObjectFactoryGenerationCase2() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV1.wsdl");
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/runtime/types",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","drfg   "
				//Invalid input is taken as false or for any other input other than "true"
			};

		intfProper.put("noObjectFactoryGeneration","True");
		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
	
		File file = new File(destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/turmeric/services/ObjectFactory.java");
		assertTrue(file.exists());
	}
	
	
	@Test
//	@Ignore("failing")
	public void deleteObjectFactoryIntfPropsNotPresent() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV1.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/runtime/types",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","True"

			};

		performDirectCodeGen(testArgs, binDir);
		
		File file = new File(destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/turmeric/services/ObjectFactory.java");
		assertFalse(file.exists());
	}
	
	
	
	
	
	
	
	@Test
//	@Ignore("failing")
	public void donotDeleteObjectFactoryNSOtherThanMP2IntfPropsNotPresent() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/runtime/types",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","False   "

			};

		performDirectCodeGen(testArgs, binDir);
		
		File file = new File(destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/turmeric/services/ObjectFactory.java");
		assertTrue(file.exists());
	}
	
	
	@Test
//	@Ignore("failing")
	public void invalidInputForNoObjectFactoryGenerationIntfPropsNotPresent() throws Exception{
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/runtime/types",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","klk"
				//Invalid input is taken as false or for any other input other than "true"
			};

	
		performDirectCodeGen(testArgs, binDir);
		File file = new File(destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/turmeric/services/ObjectFactory.java");
		assertTrue(file.exists());
	}
	
	
	
	@Test
//	@Ignore("failing")
	public void noInput() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService1",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/runtime/types",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
		
			};

	
		performDirectCodeGen(testArgs, binDir); 
	
		File file = new File(destDir.getAbsolutePath()+"/gen-src/com/ebayopensource/turmeric/services/ObjectFactory.java");
		assertTrue(file.exists());
	}

 
}
