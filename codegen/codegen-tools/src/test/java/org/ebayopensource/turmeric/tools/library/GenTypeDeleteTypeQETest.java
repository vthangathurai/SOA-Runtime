package org.ebayopensource.turmeric.tools.library;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.ServiceGenerator;
import org.junit.Before;
import org.junit.Test;

/**
 * This class will automate all the testcases related to gentype: genTypeDeleteType.
 * After call to this gentype, following are validation scenarios:
 * 1. TypeName.xsd will get deleted.
 * 2. TypeInformation.xml will get updated.
 * 3. TypeName.java will get deleted.
 * 4. TypeName.episode file will get deleted
 * 5. sun-jaxb.episode will get updated. 
 * @author vganjare
 *
 */
public class GenTypeDeleteTypeQETest extends AbstractServiceGeneratorTestCase {

	File prCategoryRoot = null;
	File prProductRoot = null;
		

	private String PRODUCT_TYPE_LIBRARY = "ProductTypeLibrary";
	private String CATEGORY_TYPE_LIBRARY = "CategoryTypeLibrary";

	private String PROJECT_ROOT = null;
	private String PROJECT_ROOT_CATEGORY = "";
	private String PROJECT_ROOT_PRODUCT = "";
	private TypeLibraryUtility utility = new TypeLibraryUtility();
	final String TYPE_INFO  ="TypeInformation.xml";
	final String SUN_EPISODE = "sun-jaxb.episode";

	@Before
	public void init() throws IOException{
		PROJECT_ROOT= testingdir.getDir().getAbsolutePath();
		testingdir.ensureEmpty();
		
		mavenTestingRules.setFailOnViolation(false);
		
		
		
		prCategoryRoot = testingdir.getFile("CategoryTypeLibrary");
		prProductRoot = testingdir.getFile("ProductTypeLibrary");
		PROJECT_ROOT_CATEGORY =  prCategoryRoot.getAbsolutePath();
		PROJECT_ROOT_PRODUCT = prProductRoot.getAbsolutePath();
		
		createCategoryTypeLibrary();
	}
	
	public void createCategoryTypeLibrary() throws IOException{
		String xsdFileName = "CategoryName.xsd";
			
		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		System.out.println("Copy the xsd file to meta-srctypes folder");
		
		
		TestResourceUtil.copyResource("types/CategoryTypeLibrary/CategoryName.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		
		//Add type to library.
		boolean typeFlag = addTypeToLibrary(xsdFileName);
		assertTrue("Addition of type failed.", typeFlag);
	}
	
	
	/**
	 * This testcase will validate the deletion of a simple type.
	 * e.g. CategoryName.xsd
	 * @throws IOException 
	 *
	 */
	

	
	/**
	 * This testcase will validate the deletion of a complex type.
	 * e.g. CategoryName.xsd
	 * @throws IOException 
	 *
	 */
	@Test
	public void testGenTypeDeleteTypeComplexType() throws IOException {
		String folderConstant = "deleteTypeComplexType";
		final String xsdFileName = "CategoryProduct.xsd";
		final String episodeFileName = "CategoryProduct.episode";
		final String javaFileName = "CategoryProduct.java";
	
		System.out.println("testGenTypeDeleteTypeComplexType");
	
		
		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		System.out.println("Copy the xsd file to");
		//Copy the xsd file to \meta-src\types\ folder
		//boolean copyXsd = utility.copyXSDFileToTypesFolder(CATEGORY_TYPE_LIBRARY, xsdFileName);
		//assertTrue("Xsd file is not copied: " + copyXsd, copyXsd);
		//System.out.println("Add type to library.");
		
		
		TestResourceUtil.copyResource("types/CategoryProduct.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		
		//Add type to library.
		boolean typeFlag = addTypeToLibrary(xsdFileName);
		assertTrue("Addition of type failed.", typeFlag);


		final ServiceGenerator sGenerator = new ServiceGenerator();
		final String[] pluginParameter = { "-gentype",
				"genTypeDeleteType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				CATEGORY_TYPE_LIBRARY,
				"-type",
				xsdFileName };
		try {
			
			Runnable run = new Runnable(){

				@Override
				public void run() {
					
					try {
						sGenerator.startCodeGen(pluginParameter);
						
						
						//Validate the deletion of the TypeName.xsd file.
						String xsdPath = utility.getXsdFilePath1(PROJECT_ROOT_CATEGORY,CATEGORY_TYPE_LIBRARY, xsdFileName);
						boolean xsdFileExistFlag = utility.checkFileExistance(xsdPath);
						assertFalse(xsdFileName+" is not deleted properly.", xsdFileExistFlag);
						
						//Validate the deletion of the individual episode file.
						String episodePath = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, episodeFileName, null);
						boolean episodeFileExists = utility.checkFileExistance(episodePath);
						assertFalse(episodeFileName+" is not deleted properly.", episodeFileExists);
						
						//Validate the deletion of the TypeName.java file.
						String javaFilePath = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, javaFileName, null);
						boolean javaFileExists = utility.checkFileExistance(javaFilePath);
						assertFalse(episodeFileName+" is not deleted properly.", javaFileExists);
						
						//Validate the updation of the TypeInformation.xml
						String TIXmlCodegen = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
						String TIXmlVanilla = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/deleteTypeComplexType/"+ TYPE_INFO;
						boolean TIXmlCompare = utility.compareFiles(TIXmlCodegen, TIXmlVanilla);
						assertTrue("TypeInformation.xml content does not match.", TIXmlCompare);
						
						
						//Validate the updation of the sun-jaxb.episode file
						String masterEpisodeCodegen = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, "sun-jaxb.episode", null);
						String masterEpisodeVanilla = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/deleteTypeComplexType/"+ SUN_EPISODE;
						boolean masterEpisodeCompare = utility.compareFiles(masterEpisodeCodegen, masterEpisodeVanilla);
						//assertTrue("Sun-jaxb.episode file content does not match.", masterEpisodeCompare);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			};
			Thread t = new Thread(run);
			t.start();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:"+e.getMessage(), false);
		}
	}
	
	
	
	/**
	 * This testcase will validate the deletion of a complex type which is dependent on Simple type from 
	 * same library.
	 * @throws IOException 
	 *
	 */
	@Test
	public void testGenTypeDeleteTypeDepOnSimpleTypeSameLib() throws IOException {
		String folderConstant = "deleteTypeDepOnSimpleTypeSameLib";
		final String xsdFileName = "CategoryRelease.xsd";
		final String dependingSimpleXsd = "CategoryName.xsd";
		final String episodeFileName = "CategoryRelease.episode";
		final String javaFileName = "CategoryRelease.java";
	
		System.out.println("testGenTypeDeleteTypeDepOnSimpleTypeSameLib");
		

		System.out.println("Create TypeLibrary CategoryTypeLibrary");
		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		
		TestResourceUtil.copyResource("types/CategoryTypeLibrary/CategoryRelease.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		
		
		TestResourceUtil.copyResource("types/CategoryTypeLibrary/CategoryName.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		
		System.out.println("Add type to library.");
		//Add type to library.
		boolean typeFlag1 = addTypeToLibrary(dependingSimpleXsd);
		assertTrue("Addition of type failed.", typeFlag1);
		
		System.out.println("Add type to library.2");
		//Add type to library.
		boolean typeFlag = addTypeToLibrary(xsdFileName);
		assertTrue("Addition of type failed.", typeFlag);


		final ServiceGenerator sGenerator = new ServiceGenerator();
		final String[] pluginParameter = { "-gentype",
				"genTypeDeleteType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				CATEGORY_TYPE_LIBRARY,
				"-type",
				xsdFileName };
		try {
			
			Runnable run = new Runnable(){

				@Override
				public void run() {
					
					try {
			sGenerator.startCodeGen(pluginParameter);
			//Validate the deletion of the TypeName.xsd file.
			String xsdPath = utility.getXsdFilePath1(PROJECT_ROOT_CATEGORY,CATEGORY_TYPE_LIBRARY, xsdFileName);
			boolean xsdFileExistFlag = utility.checkFileExistance(xsdPath);
			assertFalse(xsdFileName+" is not deleted properly.", xsdFileExistFlag);
			
			//Validate the deletion of the individual episode file.
			String episodePath = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, episodeFileName, null);
			boolean episodeFileExists = utility.checkFileExistance(episodePath);
			assertFalse(episodeFileName+" is not deleted properly.", episodeFileExists);
			
			//Validate the deletion of the TypeName.java file.
			String javaFilePath = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, javaFileName, null);
			boolean javaFileExists = utility.checkFileExistance(javaFilePath);
			assertFalse(javaFileName+" is not deleted properly.", javaFileExists);
			
			//Validate the CategoryName.episode exists
			String episodePath1 = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, "CategoryName.episode", null);
			boolean episodeFileExists1 = utility.checkFileExistance(episodePath1);
			assertTrue("CategoryName.episode got deleted.", episodeFileExists1);
			
			//Validate the CategoryName.xsd exists
			String xsdPath1 = utility.getXsdFilePath1(PROJECT_ROOT_CATEGORY,CATEGORY_TYPE_LIBRARY, dependingSimpleXsd);
			boolean xsdFileExistFlag1 = utility.checkFileExistance(xsdPath1);
			assertTrue("CategoryName.xsd got deleted.", xsdFileExistFlag1);
			
			//Validate the CategoryName.java exists
			String javaFilePath1 = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, "CategoryName.java", null);
			boolean javaFileExists1 = utility.checkFileExistance(javaFilePath1);
			assertTrue("CategoryName.java got deleted.", javaFileExists1);
			
			//Validate the updation of the TypeInformation.xml
			String TIXmlCodegen = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanilla =  getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/deleteTypeDepOnSimpleTypeSameLib/"+ TYPE_INFO;
			boolean TIXmlCompare = utility.compareFiles(TIXmlCodegen, TIXmlVanilla);
			assertTrue("TypeInformation.xml content does not match.", TIXmlCompare);
			
			
			//Validate the updation of the sun-jaxb.episode file
			String masterEpisodeCodegen = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, "sun-jaxb.episode", null);
			String masterEpisodeVanilla = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/deleteTypeDepOnSimpleTypeSameLib/"+ SUN_EPISODE;
			boolean masterEpisodeCompare = utility.compareFiles(masterEpisodeCodegen, masterEpisodeVanilla);
			//assertTrue("Sun-jaxb.episode file content does not match.", masterEpisodeCompare);
			
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			};
			Thread t = new Thread(run);
			t.start();

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:"+e.getMessage(), false);
		}
	}
	
	
	@Test
	public void testGenTypeDeleteTypeSimpleType() throws IOException {
		String folderConstant = "deleteTypeSimpleType";
		final String xsdFileName = "CategoryName.xsd";
		final String episodeFileName = "CategoryName.episode";
		final String javaFileName = "CategoryName.java";
	
		
			
		createCategoryTypeLibrary();
		
		
     
		final String[] pluginParameter = { "-gentype",
				"genTypeDeleteType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				CATEGORY_TYPE_LIBRARY,
				"-type",
				xsdFileName };
		try {
			
			Runnable run = new Runnable(){

				@Override
				public void run() {
					
					try {
						
						
			performDirectCodeGen(pluginParameter);
			//Validate the deletion of the TypeName.xsd file.
			String xsdPath = utility.getXsdFilePath1(PROJECT_ROOT_CATEGORY,CATEGORY_TYPE_LIBRARY, xsdFileName);
			boolean xsdFileExistFlag = utility.checkFileExistance(xsdPath);
			assertFalse(xsdFileName+" is not deleted properly.", xsdFileExistFlag);
			
			//Validate the deletion of the individual episode file.
			String episodePath = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, episodeFileName, null);
			boolean episodeFileExists = utility.checkFileExistance(episodePath);
			assertFalse(episodeFileName+" is not deleted properly.", episodeFileExists);
			
			//Validate the deletion of the TypeName.java file.
			String javaFilePath = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, javaFileName, null);
			boolean javaFileExists = utility.checkFileExistance(javaFilePath);
			assertFalse(episodeFileName+" is not deleted properly.", javaFileExists);
			
			//Validate the updation of the TypeInformation.xml
			String TIXmlCodegen = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanilla = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/deleteTypeSimpleType/"+ TYPE_INFO;
			assertXML(TIXmlCodegen, TIXmlVanilla,null);
			
			
			//Validate the updation of the sun-jaxb.episode file
			String masterEpisodeCodegen = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, "sun-jaxb.episode", null);
			String masterEpisodeVanilla = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/deleteTypeSimpleType/"+ SUN_EPISODE;
			assertXML(masterEpisodeCodegen, masterEpisodeVanilla,null);
			
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			};
			Thread t = new Thread(run);
			t.start();
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:"+e.getMessage(), false);
		}
	}
	/**
	 * This testcase will validate the deletion of a complex type which is dependent on complex type from 
	 * same library.
	 * @throws IOException 
	 *
	 */
	@Test
	public void testGenTypeDeleteTypeDepOnComplexTypeSameLib() throws IOException {
		String folderConstant = "deleteTypeDepOnComplexTypeSameLib";
		final String xsdFileName = "CategorySales.xsd";
		final String dependingSimpleXsd = "CategoryProduct.xsd";
		final String episodeFileName = "CategorySales.episode";
		final String javaFileName = "CategorySales.java";
	
		
		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		
		
		
		TestResourceUtil.copyResource("types/CategoryTypeLibrary/CategorySales.xsd", testingdir, "CategoryTypeLibrary/meta-src");
	
		TestResourceUtil.copyResource("types/CategoryTypeLibrary/CategoryProduct.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		
		System.out.println("Add type to library.");
		//Add type to library.
		boolean typeFlag1 = addTypeToLibrary(dependingSimpleXsd);
		assertTrue("Addition of type failed.", typeFlag1);
		System.out.println("Add type to library.2");
		//Add type to library.
		boolean typeFlag = addTypeToLibrary(xsdFileName);
		assertTrue("Addition of type failed.", typeFlag);
		

		

			
		final ServiceGenerator sGenerator = new ServiceGenerator();
		final String[] pluginParameter = { "-gentype",
				"genTypeDeleteType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				CATEGORY_TYPE_LIBRARY,
				"-type",
				xsdFileName };
		try {
			Runnable run = new Runnable(){
			
			@Override
			public void run() {
				
				try {
			
			sGenerator.startCodeGen(pluginParameter);
			//Validate the deletion of the TypeName.xsd file.
			String xsdPath = utility.getXsdFilePath1(PROJECT_ROOT_CATEGORY,CATEGORY_TYPE_LIBRARY, xsdFileName);
			boolean xsdFileExistFlag = utility.checkFileExistance(xsdPath);
			assertFalse(xsdFileName+" is not deleted properly.", xsdFileExistFlag);
			
			//Validate the deletion of the individual episode file.
			String episodePath = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, episodeFileName, null);
			boolean episodeFileExists = utility.checkFileExistance(episodePath);
			assertFalse(episodeFileName+" is not deleted properly.", episodeFileExists);
			
			//Validate the deletion of the TypeName.java file.
			String javaFilePath = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, javaFileName, null);
			boolean javaFileExists = utility.checkFileExistance(javaFilePath);
			assertFalse(episodeFileName+" is not deleted properly.", javaFileExists);
			
			//Validate the CategoryProduct.episode exists
			String episodePath1 = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, "CategoryProduct.episode", null);
			boolean episodeFileExists1 = utility.checkFileExistance(episodePath1);
			assertTrue("CategoryProduct.episode got deleted.", episodeFileExists1);
			
			//Validate the CategoryProduct.xsd exists
			String xsdPath1 = utility.getXsdFilePath1(PROJECT_ROOT_CATEGORY,CATEGORY_TYPE_LIBRARY, dependingSimpleXsd);
			boolean xsdFileExistFlag1 = utility.checkFileExistance(xsdPath1);
			assertTrue("CategoryProduct.xsd got deleted.", xsdFileExistFlag1);
			
			//Validate the CategoryProduct.java exists
		/*	String javaFilePath1 = utility.getGeneratedJavaFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, "CategoryProduct.java", null);
			boolean javaFileExists1 = utility.checkFileExistance(javaFilePath1);
			assertTrue("CategoryProduct.java got deleted.", javaFileExists1);
			*/
			//Validate the updation of the TypeInformation.xml
			String TIXmlCodegen = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanilla = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/deleteTypeDepOnComplexTypeSameLib/"+ TYPE_INFO;
			boolean TIXmlCompare = utility.compareFiles(TIXmlCodegen, TIXmlVanilla);
			assertTrue("TypeInformation.xml content does not match.", TIXmlCompare);
			
			
			//Validate the updation of the sun-jaxb.episode file
			String masterEpisodeCodegen = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, "sun-jaxb.episode", null);
			String masterEpisodeVanilla = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/deleteTypeDepOnComplexTypeSameLib/"+ SUN_EPISODE;
			boolean masterEpisodeCompare = utility.compareFiles(masterEpisodeCodegen, masterEpisodeVanilla);
			//assertTrue("Sun-jaxb.episode file content does not match.", masterEpisodeCompare);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
				
				};
			Thread t = new Thread(run);
			t.start();
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:"+e.getMessage(), false);
		}
	}
	
	
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

			TestResourceUtil.copyResource("types/ProductTypeLibrary/ProductName.xsd", testingdir, "ProductTypeLibrary/meta-src");
			TestResourceUtil.copyResource("types/ProductTypeLibrary/ProductSale.xsd", testingdir, "ProductTypeLibrary/meta-src");
			TestResourceUtil.copyResource("types/ProductTypeLibrary/ProductPrice.xsd", testingdir, "ProductTypeLibrary/meta-src");
			TestResourceUtil.copyResource("types/ProductTypeLibrary/ProductRelease.xsd", testingdir, "ProductTypeLibrary/meta-src");
			TestResourceUtil.copyResource("types/ProductTypeLibrary/ProductInformation.xsd", testingdir, "ProductTypeLibrary/meta-src");

			String[] typesParameter = { "-gentype",
					"genTypeAddType",
					"-pr",
					PROJECT_ROOT_PRODUCT,
					"-libname",
					PRODUCT_TYPE_LIBRARY,
					"-type",
					"ProductName.xsd",
					"-type",
					"ProductSale.xsd",
					"-type",
					"ProductPrice.xsd",
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
	 * This testcase will validate the deletion of a complex type which is dependent on simple type from 
	 * different library.
	 * @throws IOException 
	 *
	 */
	@Test
	public void testGenTypeDeleteTypeDepOnSimpleTypeDiffLib() throws IOException {
		String folderConstant = "deleteTypeDepOnSimpleTypeDiffLib";
		final String xsdFileName = "CategoryInformation.xsd";
		final String dependingSimpleXsd = "ProductName.xsd";
		final String episodeFileName = "CategoryInformation.episode";
		final String javaFileName = "CategoryInformation.java";
		System.out.println("inside testGenTypeDeleteTypeDepOnSimpleTypeDiffLib");
		
		
		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		System.out.println("set ProductTypeLibrary.");
		//set ProductTypeLibrary.
		testSetProductTypeLibrary();
		//boolean classPath = utility.setClassPath();
		//System.out.println("Classpath status:"+classPath);
		
		TestResourceUtil.copyResource("types/CategoryTypeLibrary/CategoryInformation.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		TestResourceUtil.copyResource("types/ProductTypeLibrary/ProductName.xsd", testingdir, "ProductTypeLibrary/meta-src");
		//Add type to library.
		boolean typeFlag1 = addTypeToLibrary(PRODUCT_TYPE_LIBRARY,dependingSimpleXsd);
		assertTrue("Addition of type failed.", typeFlag1);
		System.out.println("Add type to library.2");
		//Add type to library.
		boolean typeFlag = addTypeToLibrary(xsdFileName);
		assertTrue("Addition of type failed.", typeFlag);


		final ServiceGenerator sGenerator = new ServiceGenerator();
		final String[] pluginParameter = { "-gentype",
				"genTypeDeleteType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				CATEGORY_TYPE_LIBRARY,
				"-type",
				xsdFileName };
		try {
			
			Runnable run = new Runnable(){
				
				@Override
				public void run() {
					
					try {
			sGenerator.startCodeGen(pluginParameter);
			//Validate the deletion of the TypeName.xsd file.
			String xsdPath = utility.getXsdFilePath1(PROJECT_ROOT_CATEGORY,CATEGORY_TYPE_LIBRARY, xsdFileName);
			boolean xsdFileExistFlag = utility.checkFileExistance(xsdPath);
			assertFalse(xsdFileName+" is not deleted properly.", xsdFileExistFlag);
			
			//Validate the deletion of the individual episode file.
			String episodePath = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, episodeFileName, null);
			boolean episodeFileExists = utility.checkFileExistance(episodePath);
			assertFalse(episodeFileName+" is not deleted properly.", episodeFileExists);
			
			//Validate the deletion of the TypeName.java file.
			String javaFilePath = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, javaFileName, null);
			boolean javaFileExists = utility.checkFileExistance(javaFilePath);
			assertFalse(episodeFileName+" is not deleted properly.", javaFileExists);
			
			//Validate the ProductName.episode exists
			String episodePath1 = utility.getEpisodeFilePath(PROJECT_ROOT_PRODUCT, PRODUCT_TYPE_LIBRARY, "ProductName.episode", null);
			boolean episodeFileExists1 = utility.checkFileExistance(episodePath1);
			assertTrue("CategoryProduct.episode got deleted.", episodeFileExists1);
			
			//Validate the ProductName.xsd exists
			String xsdPath1 = utility.getXsdFilePath1(PROJECT_ROOT_PRODUCT, PRODUCT_TYPE_LIBRARY,dependingSimpleXsd);
			boolean xsdFileExistFlag1 = utility.checkFileExistance(xsdPath1);
			assertTrue("CategoryProduct.xsd got deleted.", xsdFileExistFlag1);
			
			//Validate the ProductName.java exists
			String javaFilePath1 = utility.getGeneratedJavaFilePath(PROJECT_ROOT_PRODUCT, PRODUCT_TYPE_LIBRARY, "ProductName.java", null);
			boolean javaFileExists1 = utility.checkFileExistance(javaFilePath1);
			assertTrue("CategoryName.java got deleted.", javaFileExists1);
			
			//Validate the updation of the TypeInformation.xml
			String TIXmlCodegen = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanilla = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/deleteTypeDepOnSimpleTypeDiffLib/"+ TYPE_INFO;
			boolean TIXmlCompare = utility.compareFiles(TIXmlCodegen, TIXmlVanilla);
			assertTrue("TypeInformation.xml content does not match.", TIXmlCompare);
			
			
			//Validate the updation of the sun-jaxb.episode file
			String masterEpisodeCodegen = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, "sun-jaxb.episode", null);
			String masterEpisodeVanilla = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/deleteTypeDepOnSimpleTypeDiffLib/"+ SUN_EPISODE;
			boolean masterEpisodeCompare = utility.compareFiles(masterEpisodeCodegen, masterEpisodeVanilla);
			//assertTrue("Sun-jaxb.episode file content does not match.", masterEpisodeCompare);
			
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
				
				};
			Thread t = new Thread(run);
			t.start();

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:"+e.getMessage(), false);
		}
	}
	
	/**
	 * This testcase will validate the deletion of a complex type which is dependent on complex type from 
	 * different library.
	 * @throws IOException 
	 *
	 */
	@Test
	public void testGenTypeDeleteTypeDepOnComplexTypeDiffLib() throws IOException {
		String folderConstant = "deleteTypeDepOnSimpleTypeDiffLib";
		final String xsdFileName = "CategoryInformation.xsd";
		final String dependingSimpleXsd = "ProductName.xsd";
		final String episodeFileName = "CategoryInformation.episode";
		final String javaFileName = "CategoryInformation.java";
		System.out.println("inside testGenTypeDeleteTypeDepOnComplexTypeDiffLib");
		
		
		System.out.println("Create TypeLibrary CategoryTypeLibrary.");
		//Create TypeLibrary CategoryTypeLibrary.
		boolean createLibraryFlag = createTypeLibrary();
		System.out.println("Library Creation status:" + createLibraryFlag);
		assertTrue("CategoryLibrary is not created", createLibraryFlag);
		System.out.println("set ProductTypeLibrary.");
		//set ProductTypeLibrary.
		testSetProductTypeLibrary();
		/*boolean classPath = utility.setClassPath();
		System.out.println("Classpath status:"+classPath);
		System.out.println("Copy the xsd file to");*/
		//Copy the xsd file to \meta-src\types\ folder
		TestResourceUtil.copyResource("types/CategoryTypeLibrary/CategoryInformation.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		TestResourceUtil.copyResource("types/ProductTypeLibrary/ProductName.xsd", testingdir, "ProductTypeLibrary/meta-src");
		
		//Add type to library.
		boolean typeFlag1 = addTypeToLibrary(PRODUCT_TYPE_LIBRARY,dependingSimpleXsd);
		assertTrue("Addition of type failed.", typeFlag1);
		System.out.println("Add type to library.2");
		//Add type to library.
		boolean typeFlag = addTypeToLibrary(xsdFileName);
		assertTrue("Addition of type failed.", typeFlag);


		final ServiceGenerator sGenerator = new ServiceGenerator();
		final String[] pluginParameter = { "-gentype",
				"genTypeDeleteType",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname",
				CATEGORY_TYPE_LIBRARY,
				"-type",
				xsdFileName };
		try {
			
			Runnable run = new Runnable(){
				
				@Override
				public void run() {
					
					try {
			sGenerator.startCodeGen(pluginParameter);
			//Validate the deletion of the TypeName.xsd file.
			String xsdPath = utility.getXsdFilePath1(PROJECT_ROOT_CATEGORY,CATEGORY_TYPE_LIBRARY, xsdFileName);
			boolean xsdFileExistFlag = utility.checkFileExistance(xsdPath);
			assertFalse(xsdFileName+" is not deleted properly.", xsdFileExistFlag);
			
			//Validate the deletion of the individual episode file.
			String episodePath = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, episodeFileName, null);
			boolean episodeFileExists = utility.checkFileExistance(episodePath);
			assertFalse(episodeFileName+" is not deleted properly.", episodeFileExists);
			
			//Validate the deletion of the TypeName.java file.
			String javaFilePath = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, javaFileName, null);
			boolean javaFileExists = utility.checkFileExistance(javaFilePath);
			assertFalse(episodeFileName+" is not deleted properly.", javaFileExists);
			
			//Validate the ProductName.episode exists
			String episodePath1 = utility.getEpisodeFilePath(PROJECT_ROOT_PRODUCT, PRODUCT_TYPE_LIBRARY, "ProductName.episode", null);
			boolean episodeFileExists1 = utility.checkFileExistance(episodePath1);
			assertTrue("CategoryProduct.episode got deleted.", episodeFileExists1);
			
			//Validate the ProductName.xsd exists
			String xsdPath1 = utility.getXsdFilePath1(PROJECT_ROOT_PRODUCT,PRODUCT_TYPE_LIBRARY, dependingSimpleXsd);
			boolean xsdFileExistFlag1 = utility.checkFileExistance(xsdPath1);
			assertTrue("CategoryProduct.xsd got deleted.", xsdFileExistFlag1);
			
			//Validate the ProductName.java exists
			String javaFilePath1 = utility.getGeneratedJavaFilePath(PROJECT_ROOT_PRODUCT, PRODUCT_TYPE_LIBRARY, "ProductName.java", null);
			boolean javaFileExists1 = utility.checkFileExistance(javaFilePath1);
			assertTrue("CategoryName.java got deleted.", javaFileExists1);
			
			//Validate the updation of the TypeInformation.xml
			String TIXmlCodegen = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, null);
			String TIXmlVanilla = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/deleteTypeDepOnSimpleTypeDiffLib/"+ TYPE_INFO;
			boolean TIXmlCompare = utility.compareFiles(TIXmlCodegen, TIXmlVanilla);
			assertTrue("TypeInformation.xml content does not match.", TIXmlCompare);
			
			
			//Validate the updation of the sun-jaxb.episode file
			String masterEpisodeCodegen = utility.getEpisodeFilePath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, "sun-jaxb.episode", null);
			String masterEpisodeVanilla = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/deleteTypeDepOnSimpleTypeDiffLib/"+ SUN_EPISODE;
			boolean masterEpisodeCompare = utility.compareFiles(masterEpisodeCodegen, masterEpisodeVanilla);
			//assertTrue("Sun-jaxb.episode file content does not match.", masterEpisodeCompare);
			
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
				
				};
			Thread t = new Thread(run);
			t.start();

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:"+e.getMessage(), false);
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
			assertTrue("Creation of typelibrary failed because of following exception : "+e.getMessage(), false);
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
			assertTrue("Addition of type failed because of following exception : "+e.getMessage(), false);
		}
		return flag;		
	}
	
	private boolean addTypeToLibrary(String libraryName, String xsdName){
		boolean flag = false;
		String projectRoot = PROJECT_ROOT+File.separator +libraryName;
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeAddType",
				"-pr",
				projectRoot,
				"-libname",
				libraryName,
				"-type",
				xsdName };
		try {
			sGenerator.startCodeGen(pluginParameter);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
			assertTrue("Addition of type failed because of following exception : "+e.getMessage(), false);
		}
		return flag;		
	}
	
}
