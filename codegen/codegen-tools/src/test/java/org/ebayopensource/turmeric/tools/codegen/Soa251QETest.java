package org.ebayopensource.turmeric.tools.codegen;


import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;



public class Soa251QETest  extends AbstractServiceGeneratorTestCase {
	
	File destDir = null;
	File prDir = null;
	File binDir = null;

	Properties intfProper = new Properties();
	@Before
	public void init() throws Exception{
		
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		prDir = testingdir.getDir();
		


		intfProper.put("service_interface_class_name", "org.ebayopensource.turmeric.runtime.types.NewService");
		intfProper.put("service_layer","COMMON");
		intfProper.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\AccountService.wsdl");
		intfProper.put("service_version","1.0.0");
		intfProper.put("admin_name","NewAdminName");
		intfProper.put("sipp_version","1.1");
		intfProper.put("service_namespace_part","Billing");
		intfProper.put("domainName","ebay");
		intfProper.put("enabledNamespaceFolding","false");
		
		File intfProps = createPropertyFile(destDir.getAbsolutePath(),"service_intf_project.properties");
		fillProperties(intfProper, intfProps);
		
		
		
	}
	
	@Test
	public void addingInitMethodToConsumerClass() throws Exception
	{
		
		mavenTestingRules.setFailOnViolation(false);
		File wsdl = getCodegenQEDataFileInput("BillingNewService3V1.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", "AntTests/out",
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-cn","ConsumerName",
				"-pr",prDir.getAbsolutePath(),
	
			};	
		
		performDirectCodeGen(testArgs, binDir);
		
		
		String generatedCopy =destDir.getAbsolutePath()+"/src/org/ebayopensource/turmeric/runtime/types/newadminname/gen/SharedNewAdminNameConsumer.java";
		String baseCopy = getCodegenQEDataFileInput("SharedNewAdminNameConsumer.java").getAbsolutePath();
	    assertTrue("The generated copy"+ generatedCopy + "and base copy" + baseCopy + "are not identical.Please check the files for mismatches",compareTwoFiles(generatedCopy, baseCopy));
		
		
	}
	
	
	
	
	
	
	
	
	
}
	

	

	
		

		
		

	
	

