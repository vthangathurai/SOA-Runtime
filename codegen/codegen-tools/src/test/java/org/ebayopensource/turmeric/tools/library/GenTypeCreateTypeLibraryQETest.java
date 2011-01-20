package org.ebayopensource.turmeric.tools.library;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.ServiceGenerator;
import org.ebayopensource.turmeric.tools.library.SOATypeRegistry;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class GenTypeCreateTypeLibraryQETest extends AbstractServiceGeneratorTestCase{

	File destDir = null;
	File binDir = null;
	private String CATEGORY_TYPE_LIBRARY = "CategoryTypeLibrary";
	private String PROJECT_ROOT_CATEGORY= null;


	private TypeLibraryUtility utility = new TypeLibraryUtility();
	
	@Before
	public void init() throws IOException{
		
		mavenTestingRules.setFailOnViolation(false);
		PROJECT_ROOT_CATEGORY = getTestDestDir().getAbsolutePath();
		testingdir.ensureEmpty();
		destDir = getTestDestDir();
		binDir = testingdir.getFile("bin");
	}
	
	
	@Test
	//@Ignore("failing")
	public void testGenTypeCreateTypeLibrary() {
		
	
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname", CATEGORY_TYPE_LIBRARY, "-libVersion", "1.2.3" ,"-libNamespace","http://www.ebayopensource.org/soaframework/examples/config"};
			try {
				//sGenerator.startCodeGen(pluginParameter);
				performDirectCodeGen(pluginParameter, binDir);
				// Validate the content of the TypeInformation.xml
				String typeInformationXmlCodegenPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY,null);
				String typeInformationXmlVanillaPath = TestResourceUtil.copyResource("TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/TypeInformation.xml", testingdir, "gen-meta-src").getAbsolutePath();
				assertXML(typeInformationXmlVanillaPath, typeInformationXmlCodegenPath,new String [] {"xml-type-name"});
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue("No exception should be thrown",false);
		}
	}

	
	
		
	@Test
	public void testGenTypeCreateTypeLibraryPrValueMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				"-libname", "CategoryTypeLibrary", "-libVersion", "1.2.3" ,"-libNamespace","http://www.ebayopensource.org/soaframework/examples/config"};
			try {
				sGenerator.startCodeGen(pluginParameter);
			} catch (Exception e) {
				e.printStackTrace();
				String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
				String exceptionMessage = "Please provide a value for the option -pr";
				assertTrue(
					"Expected Exception message: "+exceptionMessage+" || Actual Exception message:"
							+ e.getMessage(), e.getMessage().contains(exceptionMessage));
				assertTrue("Expected Exception Class: " + exceptionClass, e
					.getClass().getName().equals(exceptionClass));
		}
	}
	
	@Test
	public void testGenTypeCreateTypeLibraryPrIsMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-libname", "CategoryTypeLibrary", "-libVersion", "1.2.3" ,"-libNamespace","http://www.ebayopensource.org/soaframework/examples/config"};
			try {
				sGenerator.startCodeGen(pluginParameter);
			} catch (Exception e) {
				e.printStackTrace();
				String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException";
				String exceptionMessage = "Project Root is missing.";
				assertTrue(
					"Expected Exception message:"+exceptionMessage+" || Actual Exception message:"
							+ e.getMessage(), e.getMessage().contains(exceptionMessage));
				assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));				
		}
	}
	
	@Test
	public void testGenTypeCreateTypeLibraryPrEmptyString() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype", "genTypeCreateTypeLibrary",
				"-pr", "", "-libname", "CategoryTypeLibrary", "-libVersion",
				"1.2.3", "-libNamespace",
				"http://www.ebayopensource.org/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Please provide a proper value for the option -pr";
			assertTrue("Expected Exception message:" + exceptionMessage
					+ " || Actual Exception message:" + e.getMessage(), e
					.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));
		}
	}
	
	
	@Test
	public void testGenTypeCreateTypeLibraryLibNameValueMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname", "-libVersion", "1.2.3",
				"-libNamespace",
				"http://www.ebayopensource.org/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Please provide a value for the option -libname";
			assertTrue("Expected Exception message:" + exceptionMessage
					+ " || Actual Exception message:" + e.getMessage(), e
					.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));
		}
	}
	
	
	@Test
	public void testGenTypeCreateTypeLibraryLibNameIsMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"CategoryTypeLibrary", "-libVersion", "1.2.3",
				"-libNamespace",
				"http://www.ebayopensource.org/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException";
			String exceptionMessage = "Invalid option categorytypelibrary specified. This option is not recognized.";
			assertTrue("Expected Exception message:" + exceptionMessage
					+ " || Actual Exception message:" + e.getMessage(), e
					.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));
		}
	}
	
	
	@Test
	public void testGenTypeCreateTypeLibraryLibNameEmptyString() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname","","-libVersion", "1.2.3",
				"-libNamespace",
				"http://www.ebayopensource.org/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Please provide a proper value for the option -libname";
			assertTrue("Expected Exception message:" + exceptionMessage
					+ " || Actual Exception message:" + e.getMessage(), e
					.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));
		}
	}
	
	
	@Test
	public void testGenTypeCreateTypeLibraryLibVersionMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname","CategoryTypeLibrary","-libVersion",
				"-libNamespace",
				"http://www.ebayopensource.org/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Please provide a value for the option -libversion";
			assertTrue("Expected Exception message:" + exceptionMessage
					+ " || Actual Exception message:" + e.getMessage(), e
					.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));
		}
	}
	
	@Test
	public void testGenTypeCreateTypeLibraryLibVersionIsMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname","CategoryTypeLibrary","1.2.3",
				"-libNamespace",
				"http://www.ebayopensource.org/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException";
			String exceptionMessage = "Invalid option 1.2.3 specified. This option is not recognized.";
			assertTrue("Expected Exception message:" + exceptionMessage
					+ " || Actual Exception message:" + e.getMessage(), e
					.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));
		}
	}
	
	
	@Test
	public void testGenTypeCreateTypeLibraryLibVersionEmptyString() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname","CategoryTypeLibrary","-libVersion","",
				"-libNamespace",
				"http://www.ebayopensource.org/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Please provide a proper value for the option -libversion";
			assertTrue("Expected Exception message:" + exceptionMessage
					+ " || Actual Exception message:" + e.getMessage(), e
					.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));
		}
	}
	
	
	@Test
	public void testGenTypeCreateTypeLibraryLibVersionInvalidValueInt() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname","CategoryTypeLibrary","-libVersion","1.2.3.4",
				"-libNamespace",
				"http://www.ebayopensource.org/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "The Library version should be in the format X.Y.Z where X,Y and Z are integers.";
			assertTrue("Expected Exception message:" + exceptionMessage
					+ " || Actual Exception message:" + e.getMessage(), e
					.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));
		}
	}
	
	
	@Test
	public void testGenTypeCreateTypeLibraryLibVersionInvalidValueStr() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname","CategoryTypeLibrary","-libVersion","a.b.c",
				"-libNamespace",
				"http://www.ebayopensource.org/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "The Library version should be in the format X.Y.Z where X,Y and Z are integers.";
			assertTrue("Expected Exception message:" + exceptionMessage
					+ " || Actual Exception message:" + e.getMessage(), e
					.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));
		}
	}
	
	
	@Test
	public void testGenTypeCreateTypeLibraryLibNameSpaceValueMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname","CategoryTypeLibrary","-libVersion","1.2.3",
				"-libNamespace" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Missing parameter for '-libNamespace' option.";
			assertTrue("Expected Exception message:" + exceptionMessage
					+ " || Actual Exception message:" + e.getMessage(), e
					.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));
		}
	}
	
	@Test
	public void testGenTypeCreateTypeLibraryLibNameSpaceIsMissing() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname","CategoryTypeLibrary","-libVersion","1.2.3",
				"http://www.ebayopensource.org/soaframework/examples/config" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException";
			String exceptionMessage = "Invalid option http://www.ebayopensource.org/soaframework/examples/config specified. This option is not recognized.";
			assertTrue("Expected Exception message:" + exceptionMessage
					+ " || Actual Exception message:" + e.getMessage(), e
					.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));
		}
	}
	
	
	@Test
	public void testGenTypeCreateTypeLibraryLibNameSpaceValueEmptyString() {
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				PROJECT_ROOT_CATEGORY,
				"-libname","CategoryTypeLibrary","-libVersion","1.2.3",
				"-libNamespace","" };
		try {
			sGenerator.startCodeGen(pluginParameter);
		} catch (Exception e) {
			e.printStackTrace();
			String exceptionClass = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			String exceptionMessage = "Please provide a proper value for the option -libnamespace";
			assertTrue("Expected Exception message:" + exceptionMessage
					+ " || Actual Exception message:" + e.getMessage(), e
					.getMessage().contains(exceptionMessage));
			assertTrue("Expected Exception Class:" + exceptionClass
					+ ", Actual:" + e.getClass().getName(), e.getClass()
					.getName().equals(exceptionClass));
		}
	}
	
	
	
}
