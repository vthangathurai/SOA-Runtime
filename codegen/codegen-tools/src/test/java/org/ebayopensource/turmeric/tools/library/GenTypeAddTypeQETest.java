package org.ebayopensource.turmeric.tools.library;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.ServiceGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This JUnit class is covering all the testcases for gentype : genTypeAddType. This gentype is used for 
 * adding type to a specified TypeLibrary. A type can be 
 * SimpleType
 * ComplexType
 * ComplexType dependent on Simple type in same library
 * ComplexType dependent on Simple type in different library
 * ComplexType dependent on Complex type in same library
 * ComplexType dependent on Complex type in different library
 * 
 * @author vganjare
 *
 */
public class GenTypeAddTypeQETest extends AbstractServiceGeneratorTestCase {

	File prCategoryRoot = null;
	File prProductRoot = null;
	private String PRODUCT_TYPE_LIBRARY = "ProductTypeLibrary";
	private String CATEGORY_TYPE_LIBRARY = "CategoryTypeLibrary";
	private String PROJECT_ROOT_CATEGORY = "";
	private String PROJECT_ROOT_PRODUCT = "";
	private String GOLD_COPY_ROOT_CATEGORY = "";
	private TypeLibraryUtility utility = new TypeLibraryUtility();
	
	final String TYPE_INFO = "TypeInformation.xml";
	final String SUN_EPISODE = "sun-jaxb.episode";
	
	@Before
	public void init(){
		
		mavenTestingRules.setFailOnViolation(false);
		
		prCategoryRoot = testingdir.getFile("CategoryTypeLibrary");
		prProductRoot = testingdir.getFile("ProductTypeLibrary");
		PROJECT_ROOT_CATEGORY =  prCategoryRoot.getAbsolutePath();
		PROJECT_ROOT_PRODUCT = prProductRoot.getAbsolutePath();
		
		
	}
	

	/**
	 * Positive Testcase for SimpleType xsd.
	 * CategoryName.xsd is a Simple Type xml schema defination.
	 * CategoryName.xsd locations are:
	 * Codegen copy location: \AntTests\CategoryTypeLibrary\meta-src\types\CategoryName.xsd
	 * Vanilla copy location: \Vanilla-Codegen\TypeLibraryCodegen\CategoryTypeLibrary\meta-src\types
	 * 
	 * Artifacts Validated:
	 * Codegen copy location: \AntTests\CategoryTypeLibrary\gen-meta-src\META-INF\CategoryTypeLibrary\
	 * Vanilla copy location: \Vanilla-Codegen\TypeLibraryCodegen\CategoryTypeLibrary\gen-meta-src\META-INF\CategoryTypeLibrary\addTypeFromSimpleType\
	 * 1.TypeInformation.xml
	 * 2.CategoryName.episode
	 * 3.sun-jaxb.episode
	 * 
	 * Codegen copy location: \AntTests\CategoryTypeLibrary\gen-src\com\ebay\soaframework\examples\config\
	 * Vanilla copy location: \Vanilla-Codegen\TypeLibraryCodegen\CategoryTypeLibrary\gen-src\com\ebay\soaframework\examples\config\addTypeFromSimpleType\
	 * 1.CategoryName.java
	 *
	 *
	 *Quick Bug 660,579,581 
	 * @throws IOException 
	 */
	@Test
	public void testGenTypeAddTypeFromSimpleType() throws IOException {
		String folderConstant = "addTypeFromSimpleType";


		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		
		TestResourceUtil.copyResource("types/CategoryName.xsd", testingdir, "CategoryTypeLibrary/meta-src/");

		//createTypeLibrary();

		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"CategoryName.xsd",
				"-lcf",
				"default"
				};
		try {
			sGenerator.startCodeGen(pluginParameter);
			
			//validate the content of TypeInformation.xml
			String TIXmlCodegenPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanillaPath = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeFromSimpleType/"+ TYPE_INFO;
			boolean typeInformationXmlCompare = utility.compareFiles(TIXmlCodegenPath, TIXmlVanillaPath);
			assertTrue("TypeInformation.xml content did not match", typeInformationXmlCompare);

			//Validate the contents of CategoryName.java
			String javaFileCodegenCopy = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryName.java",
					null);
			String javaFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-src/org/ebayopensource/soaframework/examples/config/addTypeFromSimpleType/CategoryName.java";
			boolean javaFileCompare = utility.compareFiles(javaFileCodegenCopy, javaFileVanillaCopy);
			assertTrue("CategoryName.java file content does not match", javaFileCompare);

			//Validate the content of CategoryName.episode file
			String episodeFileCodegenCopy = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryName.episode",
					null);
			String episodeFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeFromSimpleType/CategoryName.episode";
			boolean episodeFileCompare = utility.compareFiles(episodeFileCodegenCopy, episodeFileVanillaCopy);
		
			//Validate the contents of Sun-jaxb.episode
			String masterEpisodeFileCodegenCopy = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"sun-jaxb.episode",
					null);
			String masterEpisodeFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeFromSimpleType/"+ SUN_EPISODE;
			boolean masterEpisodeCompare = utility.compareFiles(masterEpisodeFileCodegenCopy, masterEpisodeFileVanillaCopy);
			assertTrue("Sun-jaxb.episode content does not match", masterEpisodeCompare);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown." + e.getMessage(), false);
		}
	}

	
	/**
	 * Positive Testcase for ComplexType xsd.
	 * CategoryProduct.xsd is a Complex Type xml schema defination.
	 * CategoryProduct.xsd locations are:
	 * Codegen copy location: \AntTests\CategoryTypeLibrary\meta-src\types\CategoryProduct.xsd
	 * Vanilla copy location: \Vanilla-Codegen\TypeLibraryCodegen\CategoryTypeLibrary\meta-src\types\CategoryProduct.xsd
	 * 
	 * Artifacts Validated:
	 * Codegen copy location: \AntTests\CategoryTypeLibrary\gen-meta-src\META-INF\CategoryTypeLibrary\
	 * Vanilla copy location: \Vanilla-Codegen\TypeLibraryCodegen\CategoryTypeLibrary\gen-meta-src\META-INF\CategoryTypeLibrary\addTypeFromComplexType\
	 * 1.TypeInformation.xml
	 * 2.CategoryProduct.episode
	 * 3.sun-jaxb.episode
	 * 
	 * Codegen copy location: \AntTests\CategoryTypeLibrary\gen-src\com\ebay\soaframework\examples\config\
	 * Vanilla copy location: \Vanilla-Codegen\TypeLibraryCodegen\CategoryTypeLibrary\gen-src\com\ebay\soaframework\examples\config\addTypeFromComplexType\
	 * 1.CategoryProduct.java
	 *
	 *Quick bug 706
	 * @throws IOException 
	 */
	@Test
	public void testGenTypeAddTypeFromComplexType() throws IOException {

		String folderConstant = "addTypeFromComplexType";


		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		
		//Copy the xsd file to \meta-src\types\ folder
		//boolean copyXsd = utility.copyXSDFileToTypesFolder(CATEGORY_TYPE_LIBRARY, "CategoryProduct.xsd");
		//assertTrue("Xsd file is not copied", copyXsd);
		
		TestResourceUtil.copyResource("types/CategoryProduct.xsd", testingdir, "CategoryTypeLibrary/meta-src");

		//createTypeLibrary();

		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"CategoryProduct.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
			//validate the content of TypeInformation.xml
			String TIXmlCodegenPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanillaPath = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeFromComplexType/"+ TYPE_INFO;
			boolean typeInformationXmlCompare = utility.compareFiles(TIXmlCodegenPath, TIXmlVanillaPath);
			assertTrue("TypeInformation.xml content did not match", typeInformationXmlCompare);

			//Validate the contents of CategoryName.java
			String javaFileCodegenCopy = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryProduct.java",
					null);
			String javaFileVanillaCopy =  getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-src/org/ebayopensource/soaframework/examples/config/addTypeFromComplexType/CategoryProduct.java";
			boolean javaFileCompare = utility.compareFiles(javaFileCodegenCopy, javaFileVanillaCopy);
			assertTrue("CategoryProduct.java file content does not match", javaFileCompare);

			//Validate the content of CategoryName.episode file
			String episodeFileCodegenCopy = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryProduct.episode",
					null);
			String episodeFileVanillaCopy =  getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeFromComplexType/CategoryProduct.episode";
			boolean episodeFileCompare = utility.compareFiles(episodeFileCodegenCopy, episodeFileVanillaCopy);
			assertTrue("CategoryProduct.episode content does not match.", episodeFileCompare);

			//Validate the contents of Sun-jaxb.episode
			String masterEpisodeFileCodegenCopy = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"sun-jaxb.episode",
					null);
			String masterEpisodeFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeFromComplexType/"+ SUN_EPISODE;
			boolean masterEpisodeCompare = utility.compareFiles(masterEpisodeFileCodegenCopy, masterEpisodeFileVanillaCopy);
			//assertTrue("Sun-jaxb.episode content does not match", masterEpisodeCompare);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown.Exception:"+e.getMessage(), false);
		}
	}

	
	/**
	 * Positive Testcase for ComplexType xsd which is dependent on Complex type from same Library.
	 * CategorySales.xsd is a Complex Type xsd which is dependent on CategoryProduct.xsd.
	 * Both the xsd are part of Library CategoryTypeLibrary.
	 * 
	 * Artifacts Validated:
	 * Codegen copy location: \AntTests\CategoryTypeLibrary\gen-meta-src\META-INF\CategoryTypeLibrary\
	 * Vanilla copy location: \Vanilla-Codegen\TypeLibraryCodegen\CategoryTypeLibrary\gen-meta-src\META-INF\CategoryTypeLibrary\addTypeDepOnComplexTypeSameLib\
	 * 1.TypeInformation.xml
	 * 3.CategorySales.episode
	 * 3.sun-jaxb.episode
	 * 
	 * Codegen copy location: \AntTests\CategoryTypeLibrary\gen-src\com\ebay\soaframework\examples\config\
	 * Vanilla copy location: \Vanilla-Codegen\TypeLibraryCodegen\CategoryTypeLibrary\gen-src\com\ebay\soaframework\examples\config\addTypeDepOnComplexTypeSameLib\
	 * 1.CategoryProduct.java
	 * 2.CategorySales.java
	 * 
	 * Pre-Requisite: Type CategoryProduct.java is not present
	 * 
	 * Quick bug 669
	 * @throws IOException 
	 */
	@Test
	public void testGenTypeAddTypeDepOnComplexTypeSameLib() throws IOException {
		String VANILLA_INFO = "addTypeDepOnComplexTypeSameLib";
		XMLUnit.setIgnoreComments(true);

		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		
		//Copy the xsd file to \meta-src\types\ folder
		//boolean copyXsd = utility.copyXSDFileToTypesFolder1(CATEGORY_TYPE_LIBRARY,CATEGORY_TYPE_LIBRARY, "CategoryProduct.xsd");
		//assertTrue("Xsd file is not copied", copyXsd);

		//boolean copyXsd1 = utility.copyXSDFileToTypesFolder(CATEGORY_TYPE_LIBRARY, "CategorySales.xsd");
		//assertTrue("Xsd file is not copied", copyXsd1);
		
		TestResourceUtil.copyResource("types/CategoryProduct.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		
		TestResourceUtil.copyResource("types/CategorySales.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		

		//createTypeLibrary();

		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"CategorySales.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
			//validate the content of TypeInformation.xml
			String TIXmlCodegenPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanillaPath = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeDepOnComplexTypeSameLib/"+ TYPE_INFO;
			boolean typeInformationXmlCompare = utility.compareFiles(TIXmlCodegenPath, TIXmlVanillaPath);
			assertTrue("TypeInformation.xml content did not match", typeInformationXmlCompare);

			//Validate the contents of CategorySales.java
			String javaFileCodegenCopy = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategorySales.java",
					null);
			String javaFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-src/org/ebayopensource/soaframework/examples/config/addTypeDepOnComplexTypeSameLib/CategorySales.java";
			;
			boolean javaFileCompare = utility.compareFiles(javaFileCodegenCopy, javaFileVanillaCopy);
			assertTrue("CategorySales.java file content does not match", javaFileCompare);

			//Validate the contents of CategoryProduct.java
			String javaFileCodegenCopy1 = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryProduct.java",
					null);
			String javaFileVanillaCopy1 = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-src/org/ebayopensource/soaframework/examples/config/addTypeDepOnComplexTypeSameLib/CategoryProduct.java";
			;
			boolean javaFileCompare1 = utility.compareFiles(javaFileCodegenCopy1, javaFileVanillaCopy1);
			assertTrue("CategoryProduct.java file content does not match", javaFileCompare1);

			//Validate the content of CategorySales.episode file
			String episodeFileCodegenCopy = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategorySales.episode",
					null);
			String episodeFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeDepOnComplexTypeSameLib/CategorySales.episode";
			//boolean episodeFileCompare = utility.compareFiles(episodeFileCodegenCopy, episodeFileVanillaCopy);
			//assertTrue("CategorySales.episode content does not match.", episodeFileCompare);
			
			assertXML(episodeFileVanillaCopy, episodeFileCodegenCopy,null);
			
			//Validate the contents of Sun-jaxb.episode
			String masterEpisodeFileCodegenCopy = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"sun-jaxb.episode",
					null);
			String masterEpisodeFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeDepOnComplexTypeSameLib/"+SUN_EPISODE;
			
			assertXML(masterEpisodeFileVanillaCopy, masterEpisodeFileCodegenCopy,null);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:"+e.getMessage(), false);
		}
	}


	/**
	 *Testcase for xs:any issue.
	 * @throws IOException 
	 */
	@Test
	public void testGenTypeAddTypeXsAnyType1() throws IOException {
		String VANILLA_INFO = "addTypeXsAnyType1";



		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		
		//Copy the xsd file to \meta-src\types\ folder
		//boolean copyXsd = utility.copyXSDFileToTypesFolder(CATEGORY_TYPE_LIBRARY, "XsAnyType1.xsd");
		//assertTrue("Xsd file is not copied", copyXsd);
		
		TestResourceUtil.copyResource("types/XsAnyType1.xsd", testingdir, "CategoryTypeLibrary/meta-src");

		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"XsAnyType1.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
			//validate the content of TypeInformation.xml
			String TIXmlCodegenPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanillaPath =  getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeXsAnyType1/"+ TYPE_INFO;
			boolean typeInformationXmlCompare = utility.compareFiles(TIXmlCodegenPath, TIXmlVanillaPath);
			assertTrue("TypeInformation.xml content did not match", typeInformationXmlCompare);
			
			String javaFileCodegenCopy = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"XsAnyType1.java",
					null);
			String javaFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-src/org/ebayopensource/soaframework/examples/config/addTypeXsAnyType1/XsAnyType1.java";
			;
			boolean javaFileCompare = compareFiles(new File(javaFileCodegenCopy), new File(javaFileVanillaCopy));
			assertTrue("XsAnyType1.java file content does not match", javaFileCompare);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:"+e.getMessage(), false);
		}
	}
	
	
	
	/**
	 *Test case for xs:any issue.
	 * @throws IOException 
	 */
	@Test
	public void testGenTypeAddTypeXsAnyType2() throws IOException {
		String VANILLA_INFO = "addTypeXsAnyType2";



		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		
		//Copy the xsd file to \meta-src\types\ folder
		//boolean copyXsd = utility.copyXSDFileToTypesFolder(CATEGORY_TYPE_LIBRARY, "XsAnyType2.xsd");
		//assertTrue("Xsd file is not copied", copyXsd);
		
		TestResourceUtil.copyResource("types/XsAnyType2.xsd", testingdir, "CategoryTypeLibrary/meta-src");

		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"XsAnyType2.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
			//validate the content of TypeInformation.xml
			String TIXmlCodegenPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanillaPath = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeXsAnyType2/"+ TYPE_INFO;
			boolean typeInformationXmlCompare = utility.compareFiles(TIXmlCodegenPath, TIXmlVanillaPath);
			assertTrue("TypeInformation.xml content did not match", typeInformationXmlCompare);
			
			String javaFileCodegenCopy = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"XsAnyType2.java",
					null);
			String javaFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-src/org/ebayopensource/soaframework/examples/config/addTypeXsAnyType2/XsAnyType2.java";
			;
			boolean javaFileCompare = compareFiles(new File(javaFileCodegenCopy), new File(javaFileVanillaCopy));
			assertTrue("XsAnyType2.java file content does not match", javaFileCompare);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:"+e.getMessage(), false);
		}
	}

	
	/**
	 * Positive Testcase for ComplexType xsd which is dependent on Simple type from same Library.
	 * CategoryRelease.xsd is a Complex Type xsd which is dependent on CategoryName.xsd which is Simple type.
	 * Both the xsd are part of Library CategoryTypeLibrary.
	 * 
	 * Artifacts Validated:
	 * Codegen copy location: \AntTests\CategoryTypeLibrary\gen-meta-src\META-INF\CategoryTypeLibrary\
	 * Vanilla copy location: \Vanilla-Codegen\TypeLibraryCodegen\CategoryTypeLibrary\gen-meta-src\META-INF\CategoryTypeLibrary\addTypeDepOnSimpleTypeSameLib\
	 * 1.TypeInformation.xml
	 * 2.CategoryRelease.episode
	 * 3.sun-jaxb.episode
	 * 
	 * Codegen copy location: \AntTests\CategoryTypeLibrary\gen-src\com\ebay\soaframework\examples\config\
	 * Vanilla copy location: \Vanilla-Codegen\TypeLibraryCodegen\CategoryTypeLibrary\gen-src\com\ebay\soaframework\examples\config\addTypeDepOnSimpleTypeSameLib\
	 * 1.CategoryRelease.java
	 * 2.CategoryName.java
	 * @throws IOException 
	 * 
	 */	
	@Test
	public void testGenTypeAddTypeDepOnSimpleTypeSameLib() throws IOException {
		String VANILLA_INFO = "addTypeDepOnSimpleTypeSameLib";


		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		
		//Copy the xsd file to \meta-src\types\ folder
		//boolean copyXsd = utility.copyXSDFileToTypesFolder(CATEGORY_TYPE_LIBRARY, "CategoryRelease.xsd");
		//assertTrue("Xsd file is not copied", copyXsd);

		//boolean copyXsd1 = utility.copyXSDFileToTypesFolder1(CATEGORY_TYPE_LIBRARY,CATEGORY_TYPE_LIBRARY, "CategoryName.xsd");
		//assertTrue("Xsd file is not copied", copyXsd1);
		
		TestResourceUtil.copyResource("types/CategoryRelease.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		
		TestResourceUtil.copyResource("types/CategoryName.xsd", testingdir, "CategoryTypeLibrary/meta-src");

		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"CategoryRelease.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
			//validate the content of TypeInformation.xml
			String TIXmlCodegenPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanillaPath = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeDepOnSimpleTypeSameLib/"+ TYPE_INFO;
			boolean typeInformationXmlCompare = utility.compareFiles(TIXmlCodegenPath, TIXmlVanillaPath);
			assertTrue("TypeInformation.xml content did not match", typeInformationXmlCompare);

			//Validate the contents of CategorySales.java
			String javaFileCodegenCopy = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryRelease.java",
					null);
			String javaFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-src/org/ebayopensource/soaframework/examples/config/addTypeDepOnSimpleTypeSameLib/CategoryRelease.java";
			;
			boolean javaFileCompare = utility.compareFiles(javaFileCodegenCopy, javaFileVanillaCopy);
			assertTrue("CategoryRelease.java file content does not match", javaFileCompare);

			//Validate the contents of CategoryProduct.java
			String javaFileCodegenCopy1 = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryName.java",
					null);
			String javaFileVanillaCopy1 = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-src/org/ebayopensource/soaframework/examples/config/addTypeDepOnSimpleTypeSameLib/CategoryName.java";
			;
			boolean javaFileCompare1 = utility.compareFiles(javaFileCodegenCopy1, javaFileVanillaCopy1);
			assertTrue("CategoryName.java file content does not match", javaFileCompare1);

			//Validate the content of CategorySales.episode file
			String episodeFileCodegenCopy = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryRelease.episode",
					null);
			String episodeFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeDepOnSimpleTypeSameLib/CategoryRelease.episode";
			boolean episodeFileCompare = utility.compareFiles(episodeFileCodegenCopy, episodeFileVanillaCopy);
			assertTrue("CategoryRelease.episode content does not match.", episodeFileCompare);

			//Validate the contents of Sun-jaxb.episode
			String masterEpisodeFileCodegenCopy = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"sun-jaxb.episode",
					null);
			String masterEpisodeFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeDepOnSimpleTypeSameLib/"+ SUN_EPISODE;
			boolean masterEpisodeCompare = utility.compareFiles(masterEpisodeFileCodegenCopy, masterEpisodeFileVanillaCopy);
			//assertTrue("Sun-jaxb.episode content does not match", masterEpisodeCompare);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown." + e.getMessage(), false);
		}
	}
	
	/**
	 * This will check for following scenario.
	 * If Type CategoryName is already is existance, then addition of any type dependent
	 * on CategoryName should not regenerate the CategoryName.java.
	 * @throws IOException 
	 *
	 */
			
	@Test
	public void testGenTypeAddTypeDepOnSimpleTypeSameLibNeg() throws IOException {
		
		

		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		
		//Copy the xsd file to \meta-src\types\ folder
		//boolean copyXsd = utility.copyXSDFileToTypesFolder(CATEGORY_TYPE_LIBRARY, "CategoryRelease.xsd");
		//assertTrue("Xsd file is not copied", copyXsd);
		
		TestResourceUtil.copyResource("types/CategoryRelease.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		
		TestResourceUtil.copyResource("types/CategoryName.xsd", testingdir, "CategoryTypeLibrary/meta-src");

		//boolean copyXsd1 = utility.copyXSDFileToTypesFolder1(CATEGORY_TYPE_LIBRARY,CATEGORY_TYPE_LIBRARY, "CategoryName.xsd");
		//assertTrue("Xsd file is not copied", copyXsd1);

		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"CategoryName.xsd" };
		
		String[] pluginParameter1 = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"CategoryRelease.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
			utility.waitForExecution(2);
			sGenerator.startCodeGen(pluginParameter1);

			//Validate the generation time of CategoryName.java
			String pathCategoryNameJava = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryName.java",
					null);
			String pathCategoryReleaseJava = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryRelease.java",
					null);
			long categoryNameJavaCreationTime = utility.getFileCreationTime(pathCategoryNameJava);
			long categoryReleaseJavaCreationTime = utility.getFileCreationTime(pathCategoryReleaseJava);
			
			if((categoryNameJavaCreationTime - categoryReleaseJavaCreationTime)>=(2*1000)){
				assertTrue("CategoryName.java is not getting regenerated.", false);
			}			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown." + e.getMessage(), false);
		}
	}


	@Test
	public void testSetProductTypeLibrary() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_PRODUCT,
				"-libname",
				PRODUCT_TYPE_LIBRARY,
				"-libVersion",
				"1.2.3",
				"-libNamespace",
				"http://www.ebayopensource.org/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);

			//boolean copyProductNameXsd = utility.copyXSDFileToTypesFolder1(PRODUCT_TYPE_LIBRARY,PRODUCT_TYPE_LIBRARY,  "ProductName.xsd");

			//boolean copyProductSaleXsd = utility.copyXSDFileToTypesFolder1(PRODUCT_TYPE_LIBRARY,PRODUCT_TYPE_LIBRARY,  "ProductSale.xsd");

			//boolean copyProductPriceXsd = utility.copyXSDFileToTypesFolder1(PRODUCT_TYPE_LIBRARY,PRODUCT_TYPE_LIBRARY,  "ProductPrice.xsd");

			//boolean copyProductReleaseXsd = utility.copyXSDFileToTypesFolder1(PRODUCT_TYPE_LIBRARY,PRODUCT_TYPE_LIBRARY,  "ProductRelease.xsd");

			//boolean copyProductInformationXsd = utility.copyXSDFileToTypesFolder1(PRODUCT_TYPE_LIBRARY,PRODUCT_TYPE_LIBRARY, 
			//		"ProductInformation.xsd");
			
			TestResourceUtil.copyResource("types/ProductName.xsd", testingdir, "ProductTypeLibrary/meta-src");
			TestResourceUtil.copyResource("types/ProductSale.xsd", testingdir, "ProductTypeLibrary/meta-src");
			TestResourceUtil.copyResource("types/ProductPrice.xsd", testingdir, "ProductTypeLibrary/meta-src");
			TestResourceUtil.copyResource("types/ProductRelease.xsd", testingdir, "ProductTypeLibrary/meta-src");
			TestResourceUtil.copyResource("types/ProductInformation.xsd", testingdir, "ProductTypeLibrary/meta-src");
			//utility.setClassPath();
			String[] typesParameter = { "-gentype",
					"genTypeAddType",
					"-pr",
					PROJECT_ROOT_PRODUCT,
					"-libname",
					PRODUCT_TYPE_LIBRARY,
					"-type",
					"ProductName.xsd",
					"-type",
					"ProductPrice.xsd",
					"-type",
					"ProductSale.xsd",
					"-type",
					"ProductRelease.xsd",
					"-type",
					"ProductInformation.xsd" };
			sGenerator.startCodeGen(typesParameter);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown."+ e.getMessage(), false);
		}
	}

	/**
	 * In this Test case, the CategoryInformation.xsd is dependent on ProductName.xsd
	 * ProductName.xsd is a simple type and is present in Library ProductTypeLibrary.
	 * CategoryInformation.xsd is a complex type and is dpresent in Library CategoryTypeLibrary.
	 * @throws IOException 
	 *
	 */
	@Test
	public void testGenTypeAddTypeDepOnSimpleTypeDiffLib() throws IOException {
		String VANILLA_INFO = "addTypeDepOnSimpleTypeDiffLib";


		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		
		
		boolean createLibraryFlag1 = createTypeLibrary1();
		System.out.println("Library Creation status:" + createLibraryFlag1);
		assertTrue("ProductLibrary is not created", createLibraryFlag1);
		
		
		
		//Copy the xsd file to \meta-src\types\CategoryTypeLibrary\ folder
		//boolean copyXsd = utility.copyXSDFileToTypesFolder(CATEGORY_TYPE_LIBRARY, "CategoryInformation.xsd");
		//assertTrue("Xsd file is not copied", copyXsd);

		//Copy the xsd file to \meta-src\types\ folder
		//boolean copyXsd1 = utility.copyXSDFileToTypesFolder1(PRODUCT_TYPE_LIBRARY,CATEGORY_TYPE_LIBRARY, "ProductName.xsd");
		//assertTrue("Xsd file is not copied" , copyXsd1);	
		TestResourceUtil.copyResource("types/CategoryInformation.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		TestResourceUtil.copyResource("types/ProductName.xsd", testingdir, "ProductTypeLibrary/meta-src");
		
		//boolean classPathFlag = utility.setClassPath();

		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				CATEGORY_TYPE_LIBRARY,
				"-type",
				"CategoryInformation.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
			//validate the content of TypeInformation.xml
			String TIXmlCodegenPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanillaPath = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeDepOnSimpleTypeDiffLib/"+ TYPE_INFO;
			boolean typeInformationXmlCompare = utility.compareFiles(TIXmlCodegenPath, TIXmlVanillaPath);
			assertTrue("TypeInformation.xml content did not match", typeInformationXmlCompare);

			//Validate the contents of CategorySales.java
			String javaFileCodegenCopy = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryInformation.java",
					null);
			String javaFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-src/org/ebayopensource/soaframework/examples/config/addTypeDepOnSimpleTypeDiffLib/CategoryInformation.java";
			
			boolean javaFileCompare = utility.compareFiles(javaFileCodegenCopy, javaFileVanillaCopy);
			assertTrue("CategoryInformation.java file content does not match", javaFileCompare);

			//Validate the content of CategorySales.episode file
			String episodeFileCodegenCopy = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryInformation.episode",
					null);
			String episodeFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeDepOnSimpleTypeDiffLib/CategoryInformation.episode";
			boolean episodeFileCompare = utility.compareFiles(episodeFileCodegenCopy, episodeFileVanillaCopy);
			assertTrue("CategorySales.episode content does not match.", episodeFileCompare);

			//Validate the contents of Sun-jaxb.episode
			String masterEpisodeFileCodegenCopy = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"sun-jaxb.episode",
					null);
			String masterEpisodeFileVanillaCopy = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/addTypeDepOnSimpleTypeDiffLib/"+SUN_EPISODE;
			boolean masterEpisodeCompare = utility.compareFiles(masterEpisodeFileCodegenCopy, masterEpisodeFileVanillaCopy);
			assertTrue("Sun-jaxb.episode content does not match", masterEpisodeCompare);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Following exception is thrown: "+e, false);
		}
	}

	/*@Test
	public void testGenTypeAddTypeDepOnComplexTypeDiffLib() {
		String VANILLA_INFO = "addTypeFromComplexTypeDiffLib";

		//Copy the xsd file to \meta-src\types\ folder
		boolean copyXsd = utility.copyXSDFileToTypesFolder(CATEGORY_TYPE_LIBRARY, "CategoryProduct.xsd");
		assertTrue("Xsd file is not copied", copyXsd);

		boolean copyXsd1 = utility.copyXSDFileToTypesFolder(CATEGORY_TYPE_LIBRARY, "CategorySales.xsd");
		assertTrue("Xsd file is not copied", copyXsd1);

		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"CategorySales.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
			//validate the content of TypeInformation.xml
			String TIXmlCodegenPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanillaPath = utility.getTypeInformationXMLPath(GOLD_COPY_ROOT + "\\" + CATEGORY_TYPE_LIBRARY,
					CATEGORY_TYPE_LIBRARY,
					VANILLA_INFO);
			boolean typeInformationXmlCompare = utility.compareFiles(TIXmlCodegenPath, TIXmlVanillaPath);
			assertTrue("TypeInformation.xml content did not match", typeInformationXmlCompare);

			//Validate the contents of CategorySales.java
			String javaFileCodegenCopy = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategorySales.java",
					null);
			String javaFileVanillaCopy = utility.getGeneratedJavaFilePath(GOLD_COPY_ROOT + "\\" + CATEGORY_TYPE_LIBRARY,
					CATEGORY_TYPE_LIBRARY,
					"CategorySales.java",
					VANILLA_INFO);
			;
			boolean javaFileCompare = utility.compareFiles(javaFileCodegenCopy, javaFileVanillaCopy);
			assertTrue("CategorySales.java file content does not match", javaFileCompare);

			//Validate the contents of CategoryProduct.java
			String javaFileCodegenCopy1 = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryProduct.java",
					null);
			String javaFileVanillaCopy1 = utility.getGeneratedJavaFilePath(GOLD_COPY_ROOT + "\\" + CATEGORY_TYPE_LIBRARY,
					CATEGORY_TYPE_LIBRARY,
					"CategoryProduct.java",
					VANILLA_INFO);
			;
			boolean javaFileCompare1 = utility.compareFiles(javaFileCodegenCopy1, javaFileVanillaCopy1);
			assertTrue("CategorySales.java file content does not match", javaFileCompare1);

			//Validate the content of CategorySales.episode file
			String episodeFileCodegenCopy = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"CategorySales.episode",
					null);
			String episodeFileVanillaCopy = utility.getEpisodeFilePath(GOLD_COPY_ROOT + "\\" + CATEGORY_TYPE_LIBRARY,
					CATEGORY_TYPE_LIBRARY,
					"CategorySales.episode",
					VANILLA_INFO);
			boolean episodeFileCompare = utility.compareFiles(episodeFileCodegenCopy, episodeFileVanillaCopy);
			assertTrue("CategorySales.episode content does not match.", episodeFileCompare);

			//Validate the contents of Sun-jaxb.episode
			String masterEpisodeFileCodegenCopy = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY,
					CATEGORY_TYPE_LIBRARY,
					"sun-jaxb.episode",
					null);
			String masterEpisodeFileVanillaCopy = utility.getEpisodeFilePath(GOLD_COPY_ROOT + "\\" + CATEGORY_TYPE_LIBRARY,
					CATEGORY_TYPE_LIBRARY,
					"sun-jaxb.episode",
					VANILLA_INFO);
			boolean masterEpisodeCompare = utility.compareFiles(masterEpisodeFileCodegenCopy, masterEpisodeFileVanillaCopy);
			assertTrue("Sun-jaxb.episode content does not match", masterEpisodeCompare);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown.", false);
		}
	}*/

	/**
	 * This testcase is for input options without value for '-pr'
	 *
	 */
	@Test
	public void testGenTypeAddTypePrValueMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"CategoryName.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Please provide a value for the option -pr";
			assertTrue("Expected Exception message: " + exceptionMessage + " || Actual Exception message:" + e.getMessage(),
					e.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class: " + exceptionClass, e.getClass().getName().equals(exceptionClass));
		}
	}

	/**
	 * This testcase for input options without '-pr' option.
	 *
	 */
	@Test
	public void testGenTypeAddTypePrIsMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"CategoryName.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException";
			String exceptionMessage = "Project Root is missing.";
			assertTrue("Expected Exception message:" + exceptionMessage + " || Actual Exception message:" + e.getMessage(),
					e.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass + ", Actual:" + e.getClass().getName(), e.getClass()
					.getName()
					.equals(exceptionClass));
		}
	}

	/**
	 * This testcase is for "" value of option '-pr'.
	 *
	 */
	@Test
	public void testGenTypeAddTypePrEmptyString() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				"",
				"-libname",
				"CategoryTypeLibrary",
				"-type",
				"CategoryName.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Please provide a proper value for the option -pr";
			assertTrue("Expected Exception message:" + exceptionMessage + " || Actual Exception message:" + e.getMessage(),
					e.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass + ", Actual:" + e.getClass().getName(), e.getClass()
					.getName()
					.equals(exceptionClass));
		}
	}

	/**
	 * This testcase is for missing option value for '-libname'.
	 *
	 */
	@Test
	public void testGenTypeAddTypeLibNameValueMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype", "genTypeAddType", "-pr", PROJECT_ROOT_CATEGORY, "-libname" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Missing parameter for '-libname' option.";
			assertTrue("Expected Exception message:" + exceptionMessage + " || Actual Exception message:" + e.getMessage(),
					e.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass + ", Actual:" + e.getClass().getName(), e.getClass()
					.getName()
					.equals(exceptionClass));
		}
	}

	/**
	 * This testcase is for missing input option '-libname'
	 *
	 */
	@Test
	public void testGenTypeAddTypeLibNameIsMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype", "genTypeAddType", "-pr", PROJECT_ROOT_CATEGORY, "CategoryTypeLibrary" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException";
			String exceptionMessage = "Invalid option categorytypelibrary specified. This option is not recognized.";
			assertTrue("Expected Exception message:" + exceptionMessage + " || Actual Exception message:" + e.getMessage(),
					e.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass + ", Actual:" + e.getClass().getName(), e.getClass()
					.getName()
					.equals(exceptionClass));
		}
	}

	/**
	 * This testcase if for "" value for option '-libname'
	 *
	 */
	@Test
	public void testGenTypeAddTypeLibNameEmptyString() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype", "genTypeAddType", "-pr", PROJECT_ROOT_CATEGORY, "-libname", "" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Please provide a proper value for the option -libname";
			assertTrue("Expected Exception message:" + exceptionMessage + " || Actual Exception message:" + e.getMessage(),
					e.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass + ", Actual:" + e.getClass().getName(), e.getClass()
					.getName()
					.equals(exceptionClass));
		}
	}

	/**
	 * This testcase is for missing '-type' value.
	 *
	 */
	@Test
	public void testGenTypeAddTypeTypeValueMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				CATEGORY_TYPE_LIBRARY,
				"-type" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Missing parameter for '-type' option.";
			assertTrue("Expected Exception message:" + exceptionMessage + " || Actual Exception message:" + e.getMessage(),
					e.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass + ", Actual:" + e.getClass().getName(), e.getClass()
					.getName()
					.equals(exceptionClass));
		}
	}

	/**
	 * This testcase is for missing option '-type'.
	 *
	 */
	@Test
	public void testGenTypeAddTypeTypeIsMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libName",
				"CategoryTypeLibrary",
				"CategoryName.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException";
			String exceptionMessage = "Invalid option categoryname.xsd specified. This option is not recognized.";
			assertTrue("Expected Exception message:" + exceptionMessage + " || Actual Exception message:" + e.getMessage(),
					e.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass + ", Actual:" + e.getClass().getName(), e.getClass()
					.getName()
					.equals(exceptionClass));
		}
	}

	/**
	 * This testcase is for "" value for option '-type'.
	 *
	 */
	@Test
	public void testGenTypeAddTypeTypeEmptyString() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				CATEGORY_TYPE_LIBRARY,
				"-type",
				"" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Please provide a proper value for the option -type";
			assertTrue("Expected Exception message:" + exceptionMessage + " || Actual Exception message:" + e.getMessage(),
					e.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass + ", Actual:" + e.getClass().getName(), e.getClass()
					.getName()
					.equals(exceptionClass));
		}
	}

	/**
	 * This tescase is for invalid xsd.
	 *
	 */
	@Test
	public void testGenTypeAddTypeTypeInvalidXsd() {
		
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				CATEGORY_TYPE_LIBRARY,
				"-type",
				"Type.xsd" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException";
			String exceptionMessage = "Type.xsd does not exist";
			assertTrue("Expected Exception message:" + exceptionMessage + " || Actual Exception message:" + e.getMessage(),
					e.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass + ", Actual:" + e.getClass().getName(), e.getClass()
					.getName()
					.equals(exceptionClass));
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
				"1.2.3",
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
	
	
	private boolean createTypeLibrary1() {
		boolean flag = false;
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				PRODUCT_TYPE_LIBRARY,
				"-libVersion",
				"1.2.3",
				"-libNamespace",
				"http://www.ebayopensource.org/diff/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public static void main(String[] args) {
		GenTypeAddTypeQETest test = new GenTypeAddTypeQETest();
		//test.testGenTypeAddTypeFromSimpleType();
		//test.testGenTypeAddTypeFromComplexType();
		//test.testGenTypeAddTypeDepOnComplexTypeSameLib();
		//test.testGenTypeAddTypeDepOnSimpleTypeSameLib();
		//test.testGenTypeAddTypeDepOnSimpleTypeSameLibNeg();
		//test.testGenTypeAddTypeDepOnSimpleTypeDiffLib();
		//test.testGenTypeAddTypeDepOnComplexTypeDiffLib();
		test.testGenTypeAddTypeTypeInvalidXsd();
	}

}
