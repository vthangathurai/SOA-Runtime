package org.ebayopensource.turmeric.tools.library;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.ServiceGenerator;
import org.junit.Before;
import org.junit.Test;

/**
 * This class will have all the testcases for gentype genTypeCleanBuildTypeLibrary
 * @author vganjare
 *
 */
public class GenTypeCleanBuildTypeLibraryQETest extends AbstractServiceGeneratorTestCase{
	
	File prCategoryRoot = null;
	File prProductRoot = null;

	private String CATEGORY_TYPE_LIBRARY = "CategoryTypeLibrary";
	private String PROJECT_ROOT_CATEGORY = "";
	private String PROJECT_ROOT_PRODUCT = "";

	private TypeLibraryUtility utility = new TypeLibraryUtility();
	
	final String TYPE_INFO = "TypeInformation.xml";
	
	@Before
	public void init(){
		
		mavenTestingRules.setFailOnViolation(false);
		
		prCategoryRoot = testingdir.getFile("CategoryTypeLibrary");
		prProductRoot = testingdir.getFile("ProductTypeLibrary");
		PROJECT_ROOT_CATEGORY =  prCategoryRoot.getAbsolutePath();
		PROJECT_ROOT_PRODUCT = prProductRoot.getAbsolutePath();
		
		
	}
	
	/**
	 * Test for modification of TypeIformation.xml
	 * 
	 * Quick bug 638
	 * @throws IOException 
	 *
	 */
	@Test
	public void testGenTypeCleanBuildTypeLibrarySimpleType() throws IOException{
		String folderConstant = "cleanBuildTypeLibrarySimpleType";
		String xsdFileName = "CategoryName.xsd";
	
		

		
		//create library
		boolean createLibraryFlag = createTypeLibrary();
		assertTrue("Problem occured during Library creation.", createLibraryFlag);
		
		TestResourceUtil.copyResource("types/CategoryName.xsd", testingdir, "CategoryTypeLibrary/meta-src/");
		
		//add types
		boolean addType = addTypeToLibrary(xsdFileName);
		assertTrue("Problem occured during Type addition.", addType);
		
		//modify TypeInformation.xml
		String newTIXmlPath = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/cleanBuildTypeLibrarySimpleType/"+ TYPE_INFO;
		String oldTIXmlPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
	
		
		
		MavenTestingUtils.ensureEmpty(testingdir.getFile("gen-meta-src"));
		MavenTestingUtils.ensureEmpty(testingdir.getFile("gen-src"));
		
		//Validate the changes.
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCleanBuildTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname", CATEGORY_TYPE_LIBRARY };
			try {
				
				sGenerator.startCodeGen(pluginParameter);
				
				boolean compareTIXml = utility.compareFiles(oldTIXmlPath, newTIXmlPath);
				assertTrue("TypeInformation.xml content does not match.", compareTIXml);
				
				File typeInformation = new File(oldTIXmlPath);
				Assert.assertTrue(typeInformation.exists());
				
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue("No exception should be thrown. Exception: "+e.getMessage(), false);
		}
	}
	
	
	/**
	 * Test for modification of TypeIformation.xml
	 * @throws IOException 
	 *
	 */
	@Test
	public void testGenTypeCleanBuildTypeLibraryModifyXsdWithValidContent() throws IOException{
		String folderConstant = "cleanBuildTypeLibraryModifyXsdWithValidContent";
		String xsdFileName = "CategoryProduct.xsd";
		String javaFileName = "CategoryProduct1.java";
		String episodeFileName = "CategoryProduct.episode";
		
		
		//create library
		boolean createLibraryFlag = createTypeLibrary();
		assertTrue("Problem occured during Library creation.", createLibraryFlag);
		
		TestResourceUtil.copyResource("types/CategoryProduct.xsd", testingdir, "CategoryTypeLibrary/meta-src/");
			
		//add types
		boolean addType = addTypeToLibrary(xsdFileName);
		assertTrue("Problem occured during Type addition.", addType);
		
		//Update the xsd file
		/*String oldXsdPath = utility.getXsdFilePath(PROJECT_ROOT_CATEGORY, xsdFileName);
		String newXsdPath = utility.getXsdFilePath(GOLD_COPY_ROOT_CATEGORY, "CategoryProduct1.xsd");
		boolean updateXsd = utility.updateSourceFile(newXsdPath, oldXsdPath);
		assertTrue("Problem occured during updating CategoryName.xsd",updateXsd);*/
		
		TestResourceUtil.copyResource("types/CategoryProduct1.xsd", testingdir, "CategoryTypeLibrary/meta-src/");
						
		
		
		//Validate the changes.
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCleanBuildTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname", CATEGORY_TYPE_LIBRARY };
			try {
			
				sGenerator.startCodeGen(pluginParameter);
				
				//Validate the contents of TypeInformation.xml
				String newTIXmlPath = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/cleanBuildTypeLibraryModifyXsdWithValidContent/"+ TYPE_INFO;
				String oldTIXmlPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
				assertXML(oldTIXmlPath,newTIXmlPath,new String []{"xml-type-name"});
				//assertTrue("TypeInformation.xml content does not match.", compareTIXml);
				
				
				//Validate the contents of the CategoryProduct.java file.
				String javaFilePathVanilla =getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-src/org/ebayopensource/soaframework/examples/config/cleanBuildTypeLibraryModifyXsdWithValidContent/CategoryProduct1.java";
				String javaFileCodegenCopy = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
						CATEGORY_TYPE_LIBRARY,
						"CategoryProduct1.java",
						null);
				boolean javaFileCompare = utility.compareFiles(javaFileCodegenCopy, javaFilePathVanilla);
				assertTrue(javaFileName+" content does not match.", javaFileCompare);
				
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue("No exception should be thrown. Exception: "+e.getMessage(), false);
		}
	}
	
	
	/**
	 * Test for modification of TypeName.xsd with invalid content.
	 * @throws IOException 
	 *
	 */
	@Test
	public void testGenTypeCleanBuildTypeLibraryModifyXsdWithInvalidContent() throws IOException{
		String xsdFileName = "CategoryProduct.xsd";
		
		
		//create library
		boolean createLibraryFlag = createTypeLibrary();
		assertTrue("Problem occured during Library creation.", createLibraryFlag);
		
		TestResourceUtil.copyResource("types/CategoryProduct.xsd", testingdir, "CategoryTypeLibrary/meta-src/");
		
		//add types
		boolean addType = addTypeToLibrary(xsdFileName);
		assertTrue("Problem occured during Type addition.", addType);
		
		TestResourceUtil.copyResource("types/CategoryProductInvalid.xsd", testingdir, "CategoryTypeLibrary/meta-src/");
		
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCleanBuildTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname", CATEGORY_TYPE_LIBRARY };
			try{
				sGenerator.startCodeGen(pluginParameter);
				assertTrue("Exception should be thrown. Exception: ", false);
				
			} catch (Exception e) {
				e.printStackTrace();	
		}
	}
	
	
	
	
	private boolean createTypeLibrary() {
		boolean flag = false;
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				CATEGORY_TYPE_LIBRARY,
				"-libVersion",
				"3.2.1",
				"-libNamespace",
				"http://www.ebayopensource.org/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	
	private boolean addTypeToLibrary(String xsdName){
		boolean flag = false;
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				xsdName };
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
