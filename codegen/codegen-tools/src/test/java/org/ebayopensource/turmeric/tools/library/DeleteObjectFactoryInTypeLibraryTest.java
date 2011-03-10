package org.ebayopensource.turmeric.tools.library;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.ServiceGenerator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;



public class DeleteObjectFactoryInTypeLibraryTest  extends AbstractServiceGeneratorTestCase{
	
	private String CATEGORY_TYPE_LIBRARY = "CategoryTypeLibrary";


	
@Rule public TestName name = new TestName();
	
	File destDir = null;
	File binDir = null;

	
@Before
	
	public void init() throws Exception{
		

	testingdir.ensureEmpty();
	destDir = testingdir.getDir();
	binDir = testingdir.getFile("bin");
		
}

	
	
	@Test
	public void testGenTypeAddTypeDepOnComplexTypeSameLib() throws IOException {
		

		
		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary(destDir.getAbsolutePath());
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		
		
		
		
		TestResourceUtil.copyResource("types/CategoryProduct2.xsd", testingdir,"meta-src" );

		//createTypeLibrary();

	
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				destDir.getAbsolutePath(),
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"CategoryProduct.xsd" };
		try {
			performDirectCodeGen(pluginParameter);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		String[] pluginParameter1 = {
				"-gentype",
				"genTypeCleanBuildTypeLibrary",
				"-pr",
				destDir.getAbsolutePath(),
				"-libname", CATEGORY_TYPE_LIBRARY };
		
		try {
			performDirectCodeGen(pluginParameter1);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		codegenAssertFileNotExists(destDir +File.separator+"gen-src","org/ebayopensource/turmeric/common/v1/types/ObjectFactory.java");
		
		codegenAssertFileNotExists(destDir +File.separator+"gen-src","org/ebayopensource/turmeric/common/v1/types/package-info.java");
		
		
	}
		
		
		private boolean createTypeLibrary(String pr) {
			boolean flag = false;
			ServiceGenerator sGenerator = new ServiceGenerator();
			String[] pluginParameter = { "-gentype",
					"genTypeCreateTypeLibrary",
					"-pr",
					pr,
					"-libname",
					CATEGORY_TYPE_LIBRARY,
					"-libVersion",
					"1.2.3",
					"-libNamespace",
					"http://www.ebayopensource.org/turmeric/common/v1/types" };
			try {
				sGenerator.startCodeGen(pluginParameter);
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
				flag = false;
			}
			return flag;
		}

}
