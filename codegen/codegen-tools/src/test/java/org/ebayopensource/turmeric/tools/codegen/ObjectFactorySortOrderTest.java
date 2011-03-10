package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import junit.framework.Assert;

import org.ebayopensource.turmeric.tools.codegen.external.WSDLUtil;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.WsdlParserUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.ebay.kernel.util.FileUtils;

public class ObjectFactorySortOrderTest extends AbstractServiceGeneratorTestCase {
	
	@Rule public TestName name = new TestName();
	
	File destDir = null;
	File binDir = null;
	final String INTF_PROPERTIES = "service_intf_project.properties";
	final String IMPL_PROPERTIES = "service_impl_project.properties";
	ServiceGenerator gen = null;
	File intfProperty = null;
	
	File objFactory = null;
	
	@Before
	public void initialize() throws Exception{
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		
		
		File objFactFolder = new File(destDir.getAbsolutePath()+"/obFjactory");
		if(!objFactFolder.exists()){
			
			objFactFolder.createNewFile();
		}
		try {
			intfProperty =	createPropertyFile(destDir.getAbsolutePath(), INTF_PROPERTIES);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		//enter values to property file
		Properties intfProps = new Properties();
		intfProps.put("sipp_version","1.1");
		intfProps.put("service_interface_class_name","org.ebayopensource.test.soaframework.tools.codegen.AccountService");
		intfProps.put("service_layer","COMMON");
		intfProps.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\ebaySvc.wsdl");
		intfProps.put("service_version","1.0.0");
		intfProps.put("admin_name","AccountService");
		intfProps.put("service_namespace_part","billing");
		intfProps.put("domainName","Billing");
	
		
		fillProperties(intfProps, intfProperty);


	}
	
	@Test
	public void testObjectFactoryCheckSum() throws Exception {
		File path  = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String [] testArgs = {"-serviceName","AccountService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path.getAbsolutePath(),
				  "-namespace","http://www.ebayopensource.org/marketplace/service",
				  "-dest",destDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-jdest",destDir.getAbsolutePath() +"/gen-src/client",
				  "-bin",binDir.getAbsolutePath(),
				  "-scv","1.0.0",
				  "-pr",destDir.getAbsolutePath() };

		
		Set<String> targetNS = WsdlParserUtil.getAllTargetNamespces(path.getAbsolutePath());
		List<String> packageList = new ArrayList<String>();
		Iterator<String> it = targetNS.iterator();
		while(it.hasNext()){
			packageList.add(WSDLUtil.getPackageFromNamespace(it.next()));
					
		}
		for(int i=0;i<5;i++){
		
			performDirectCodeGen(testArgs,binDir);
		
			File objectFactory = null;
			for(String pkg:packageList){
				
					String p = pkg.replace(".","/");
					objectFactory = new File(destDir.getAbsolutePath()+"/gen-src/client/"+p+ "/ObjectFactory.java");
					FileUtils.copyFile(objectFactory.getAbsolutePath(),getObjectFactoryFile(p).getAbsolutePath());
		
			
			}
		}
		
		
		for(String pkg:packageList){
			String p = pkg.replace(".","/");			
				File file = new File(destDir.getAbsolutePath()+"/objFactory/"+p);	
				Assert.assertTrue(fileCompare(file));
	
		}
	
		

	}
	
	public File getObjectFactoryFile(String pkg){
		long time = System.currentTimeMillis();
		
		try {
			(new File(destDir.getAbsolutePath()+"/objFactory/"+pkg)).mkdirs();
			objFactory = new File(destDir.getAbsolutePath()+"/objFactory/"+pkg+"/ObjectFactory"+time+".java");
			objFactory.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return objFactory;
	}
	
	public boolean fileCompare(File dir) throws IOException{
		boolean compare = true;
		File file1 = null;
		File file2 = null;
		File [] files = dir.listFiles();
		int i;
		for(i=0;i < files.length;i++){
			
		  
			file1 = files[i];
			i = i +1;
			if(i==files.length){
				  break;
			  }
			file2 = files[i];
			i = i - 1;
			compare = compareTwoFiles(file1, file2);
		}
		
		return compare;
	}
	
	
	

}
