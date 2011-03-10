package org.ebayopensource.turmeric.tools.library;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.NonInteractiveCodeGen;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.ebay.kernel.util.FileUtils;


public class BugTest extends AbstractServiceGeneratorTestCase {
	
	final String INTF_PROPERTIES = "service_intf_project.properties";

	private String TEST_TYPE_LIBRARY = "TestTypeLibrary";
	private String TEST_TYPE_LIBRARY2 = "TestTypeLibrary2";

	private String TEST_NAMESPACE = "http://www.ebayopensource.org/turmeric/services/v1";
	private String TEST_NAMESPACE2 = "http://www.ebayopensource.org/turmeric/services/v2";

	
@Rule public TestName name = new TestName();
	
	File destDir = null;
	File binDirectory = null;

	Properties intfProps = new Properties();


	
@Before
	
	public void init() throws Exception{
		

		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDirectory = testingdir.getFile("bin");
		
		
		

		//enter values to property file
		
		intfProps.put("sipp_version","1.1");
		intfProps.put("service_interface_class_name","org.ebayopensource.test.soaframework.tools.codegen.TurnTheServiceV1");
		intfProps.put("service_layer","COMMON");
		intfProps.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\TurnTheServiceV1.wsdl");
		intfProps.put("service_version","1.0.0");
		intfProps.put("admin_name","TurnTheServiceV1");
		intfProps.put("service_namespace_part","billing");
		intfProps.put("domainName","Billing");
		intfProps.put("enabledNamespaceFolding","true");
		
		
		
		File service = new File(destDir.getAbsolutePath() + File.separator +"meta-src");
	
		URL [] urls = {service.toURI().toURL()};
		URLClassLoader urlClassLoader = new URLClassLoader(urls);
		Thread.currentThread().setContextClassLoader(urlClassLoader);
		
}	


@Test

public void testBug() throws Exception{
	
	File intfaceProps = createPropertyFile(destDir.getAbsolutePath(),INTF_PROPERTIES);
	fillProperties(intfProps, intfaceProps);
	
	createTypeLibrary(destDir.getAbsolutePath() +"/TestTypeLibrary",TEST_TYPE_LIBRARY,TEST_NAMESPACE);
	
	setClassPath(destDir.getAbsolutePath());
	
	TestResourceUtil.copyResource("META-INF/"+TEST_TYPE_LIBRARY+"/TypeDependencies.xml", testingdir,TEST_TYPE_LIBRARY +"/meta-src");
	
	
	TestResourceUtil.copyResource("types/C1Type.xsd", testingdir, TEST_TYPE_LIBRARY+"/meta-src" );
	
	createType(destDir.getAbsolutePath() +"/TestTypeLibrary", TEST_TYPE_LIBRARY, "C1Type.xsd");
	
	TestResourceUtil.copyResource("types/C2Type.xsd", testingdir,TEST_TYPE_LIBRARY + "/meta-src");
	
	createType(destDir.getAbsolutePath() +"/TestTypeLibrary", TEST_TYPE_LIBRARY, "C2Type.xsd");
	
	createTypeLibrary(destDir.getAbsolutePath() +"/TestTypeLibrary2",TEST_TYPE_LIBRARY2,TEST_NAMESPACE2);
	setClassPath(destDir.getAbsolutePath());
	
	TestResourceUtil.copyResource("META-INF/"+TEST_TYPE_LIBRARY2+"/TypeDependencies.xml", testingdir,TEST_TYPE_LIBRARY2 + "/meta-src");
	
	TestResourceUtil.copyResource("types/AType.xsd", testingdir, TEST_TYPE_LIBRARY2+"/meta-src");
	
	createType(destDir.getAbsolutePath() + "/TestTypeLibrary2", TEST_TYPE_LIBRARY2, "AType.xsd");
	
	
	TestResourceUtil.copyResource("types/C3Type.xsd", testingdir, TEST_TYPE_LIBRARY+"/meta-src");
	
	
	
	createType(destDir.getAbsolutePath() +"/TestTypeLibrary", TEST_TYPE_LIBRARY, "C3Type.xsd");
	
	TestResourceUtil.copyResource("META-INF/TurnTheServiceV1/TypeDependencies.xml", testingdir, "meta-src");
	
	File binDir = new File(destDir.getAbsolutePath() +"/bin") ;
	
	File dest = createFolders(binDir,"/org/ebayopensource/turmeric/services/v1/");
	
	String javaFilePath = destDir.getAbsolutePath() +"/TestTypeLibrary/gen-src/org/ebayopensource/turmeric/services/v1/";
	
	File type = new File(dest +"/C1Type.java");
	if(!type.exists())
	type.createNewFile();
	
	File type2 = new File(dest +"/C2Type.java");
	if(!type.exists())
	type.createNewFile();
	
	FileUtils.copyFile(javaFilePath +"/C1Type.java", dest +"/C1Type.java");
	FileUtils.copyFile(javaFilePath +"/C2Type.java", dest +"/C2Type.java");
	
	compileJavaFile(type.getAbsolutePath());
	compileJavaFile(type2.getAbsolutePath());
	
	fillProperties(intfProps, intfaceProps);

	 File wsdl = getCodegenQEDataFileInput("TurnTheServiceV1.wsdl");
	
	
	String [] testArgs1 = {"-serviceName","NewService",
			  "-genType","ServiceFromWSDLIntf",	
			  "-wsdl",wsdl.getAbsolutePath(),
			  "-gip","com.ebay.test.soaframework.tools.codegen",
			  "-dest",destDir.getAbsolutePath(),
			  "-src",destDir.getAbsolutePath(),
			  "-slayer","INTERMEDIATE",
			  "-scv","1.0.0",
			  "-bin",binDirectory.getAbsolutePath(),
			  "-pr",destDir.getAbsolutePath()};
	
	performDirectCodeGen(testArgs1, binDirectory);
	
	
	
	FileFilter fileDirectories = new FileFilter() { 
		public boolean accept(File file) { return file.isDirectory(); } 
	};
	
	FileFilter genJavaFile = new FileFilter() { 
		public boolean accept(File file) { return (file.getName().equals("ObjectFactory.java") || file.getName().equals("package-info.java")); } 
	};
	File javaTypeLibDir = new File(javaFilePath);
	if(javaTypeLibDir.isDirectory()){
		
		File [] file = javaTypeLibDir.listFiles();
		file = javaTypeLibDir.listFiles(fileDirectories);
		
		file = javaTypeLibDir.listFiles(genJavaFile);
		
		Assert.assertTrue(file.length == 0);
		
	}
	
	
	
}

public File createFolders(File dest,String dirs) throws Exception{
	File dir = new File(dest.getAbsolutePath() + "/"+dirs);
	if (dir.mkdirs()){
		
	}
	else {
		throw new Exception("Could not create folders");
	}
	
	return dir;
}


private void setClassPath(String PROJECT_ROOT){
	
	String gen_meta_src_ProductTypeLibrary = PROJECT_ROOT +"/TestTypeLibrary/gen-meta-src/";
	String gen_src_ProductTypeLibrary = PROJECT_ROOT +"/TestTypeLibrary/gen-src/";
	String meta_src_ProductTypeLibrary = PROJECT_ROOT +"/TestTypeLibrary/meta-src/";

	String gen_meta_src_CategoryTypeLibrary = PROJECT_ROOT +"/TestTypeLibrary2/gen-meta-src/";
	String gen_src_CategoryTypeLibrary= PROJECT_ROOT +"/TestTypeLibrary2/gen-src/";
	String meta_src_CategoryTypeLibrary = PROJECT_ROOT +"/TestTypeLibrary2/meta-src/";

	
	
	File file_gen_meta_src_ProductTypeLibrary = new File(gen_meta_src_ProductTypeLibrary);
	File file_gen_src_ProductTypeLibrary = new File(gen_src_ProductTypeLibrary);
	File file_meta_src_ProductTypeLibrary = new File(meta_src_ProductTypeLibrary);

	File file_gen_meta_src_CategoryTypeLibrary = new File(gen_meta_src_CategoryTypeLibrary);
	File file_gen_src_CategoryTypeLibrary = new File(gen_src_CategoryTypeLibrary);
	File file_meta_src_CategoryTypeLibrary = new File(meta_src_CategoryTypeLibrary);

	
	    try{
		URL[] urls = { file_gen_meta_src_ProductTypeLibrary.toURI().toURL(),
				file_gen_src_ProductTypeLibrary.toURI().toURL(),
				file_meta_src_ProductTypeLibrary.toURI().toURL(),
				file_gen_meta_src_CategoryTypeLibrary.toURI().toURL(),
				file_gen_src_CategoryTypeLibrary.toURI().toURL(),
				file_meta_src_CategoryTypeLibrary.toURI().toURL(),
				};
	
		URLClassLoader urlClassLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
		Thread.currentThread().setContextClassLoader(urlClassLoader);
	    }catch(MalformedURLException e){
	    	e.printStackTrace();
	    }
	
	
}
	
	
}	
