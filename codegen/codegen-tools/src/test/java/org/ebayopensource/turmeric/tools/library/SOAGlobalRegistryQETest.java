package org.ebayopensource.turmeric.tools.library;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.common.config.LibraryType;
import org.ebayopensource.turmeric.common.config.TypeDependencyType;
import org.ebayopensource.turmeric.common.config.TypeInformationType;
import org.ebayopensource.turmeric.common.config.TypeLibraryDependencyType;
import org.ebayopensource.turmeric.common.config.TypeLibraryType;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.ServiceGenerator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class will validate all the testcases related to SOATypeRegistry.java interface.
 * @author vganjare
 *
 */
public class SOAGlobalRegistryQETest extends AbstractServiceGeneratorTestCase {
	
	File prCategoryRoot = null;
	File prProductRoot = null;

	private SOATypeRegistry m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
	private String PRODUCT_TYPE_LIBRARY = "ProductTypeLibrary";
	private String CATEGORY_TYPE_LIBRARY = "CategoryTypeLibrary";
	private String TYPE_LIBRARY_TYPE = "TypeLibraryType";
	private String PROJECT_ROOT_CATEGORY = "";
	private String PROJECT_ROOT_PRODUCT = "";
	private String PROJECT_ROOT_TEST = "";
	private TypeLibraryUtility utility = new TypeLibraryUtility();
	private String PROJECT_ROOT = null;
	private String NAMESPACE ="http://www.ebayopensource.org/soaframework/examples/config";

	
	@Before
	public void init() throws IOException{
		
		PROJECT_ROOT = testingdir.getDir().getAbsolutePath();
		PROJECT_ROOT_TEST = testingdir.getFile("Test").getAbsolutePath();
		PROJECT_ROOT_CATEGORY = getTestDestDir().getAbsolutePath();
		testingdir.ensureEmpty();
		
		mavenTestingRules.setFailOnViolation(false);
		
		prCategoryRoot = testingdir.getFile("CategoryTypeLibrary");
		prProductRoot = testingdir.getFile("ProductTypeLibrary");
		PROJECT_ROOT_CATEGORY =  prCategoryRoot.getAbsolutePath();
		PROJECT_ROOT_PRODUCT = prProductRoot.getAbsolutePath();
	}
	
	//*********--- boolean populateRegistryWithTypeLibraries(List<String> typeLibraryNames) ---************//

	/**
	 * Validate the working of populateRegistryWithTypeLibraries() for valid 
	 * TypeLibraries.
	 */
	@Test
	public void testPopulateRegistryWithTypeLibraries() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";

		//Set the environment.
		setEnvironment(folderConstant);

		List<String> list = new ArrayList<String>();
		list.add(PRODUCT_TYPE_LIBRARY);
		list.add(CATEGORY_TYPE_LIBRARY);
		try {
			boolean flag = m_soaTypeRegistry.populateRegistryWithTypeLibraries(list);
			assertTrue("SOAGlobalRegistry is not updated properly.", flag);
			List libraryList = m_soaTypeRegistry.getAllTypeLibraries();
			boolean libFlag = (libraryList.size() == list.size());
			assertTrue("SOAGlobalRegistry is not updated properly.", libFlag);
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue("No Exception should be thrown. Following Exception is thrown: " + ex.getMessage(), false);
		}
	}

	/**
	 * This testcase will validate the working for Invalid TypeLibraryName.
	 *
	 */
	@Test
	public void testPopulateRegistryWithTypeLibrariesInvalidInputs() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";

		//Set the environment.
		setEnvironment(folderConstant);

		List<String> list = new ArrayList<String>();
		CATEGORY_TYPE_LIBRARY = "InvalidTypeLibrary";
		list.add(CATEGORY_TYPE_LIBRARY);
		list.add(PRODUCT_TYPE_LIBRARY);
		try {
			boolean flag = m_soaTypeRegistry.populateRegistryWithTypeLibraries(list);
			assertFalse("Valid Exception should be thrown.", flag);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionClassName = "org.ebayopensource.turmeric.tools.library.exception.PopulateRegistryException";
			String exceptionMessage = "Could not find the TypeInformation.xml file for library InvalidTypeLibrary  at location : META-INF/InvalidTypeLibrary/TypeInformation.xml";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	/**
	 * Duplicate names should not increase the count of libraries in 
	 * GlobalRegistry.
	 *
	 */
	@Test
	public void testPopulateRegistryWithTypeLibrariesDuplicateNames() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";

		//Set the environment.
		setEnvironment(folderConstant);

		List<String> list = new ArrayList<String>();
		list.add(PRODUCT_TYPE_LIBRARY);
		list.add(PRODUCT_TYPE_LIBRARY);
		list.add(CATEGORY_TYPE_LIBRARY);
		list.add(CATEGORY_TYPE_LIBRARY);
		try {
			boolean flag = m_soaTypeRegistry.populateRegistryWithTypeLibraries(list);
			assertTrue("SOAGlobalRegistry is not updated properly.", flag);
			List libraryList = m_soaTypeRegistry.getAllTypeLibraries();
			boolean sizeFlag = (libraryList.size() == 2);
			assertTrue("The Global Registry should not be updated for duplicate libraries.", sizeFlag);
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue("No Exception should be thrown. Following Exception is thrown: " + ex.getMessage(), false);
		}

	}

	/**
	 * This method will validate the working for the response if 
	 * input is 'null'.
	 */
	@Test
	public void testPopulateRegistryWithTypeLibrariesNullValue() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";

		//Set the environment.
		setEnvironment(folderConstant);

		List<String> list = new ArrayList<String>();
		list.add(PRODUCT_TYPE_LIBRARY);
		list.add(null);
		list.add(PRODUCT_TYPE_LIBRARY);
		list.add(CATEGORY_TYPE_LIBRARY);
		try {
			boolean flag = m_soaTypeRegistry.populateRegistryWithTypeLibraries(list);
			assertFalse("Exception should be thrown.", flag);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Could not find the TypeInformation.xml file for library null  at location : META-INF/null/TypeInformation.xml";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.library.exception.PopulateRegistryException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
			try {
				List libraries = m_soaTypeRegistry.getAllTypeLibraries();
				boolean libraryCheck = (libraries.size() == 2);
				assertTrue("Global Registry is updated incorrectly.", libraryCheck);
			} catch (Exception e) {
				assertFalse("Exception should be thrown.", true);
			}
		}
	}

	//*********--- boolean addTypeLibraryToRegistry(TypeLibraryType library) ---************//
	/**
	 * Validates the working of boolean addTypeLibraryToRegistry(TypeLibraryType library)
	 * for valid inputs.
	 */
	@Test
	public void testAddTypeLibraryToRegistryTLT() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			//before call to addTypeLibraryToRegistry();
			int initialCount = getAllLibrariesFromWorkspace();

			TypeLibraryType productTypeLibrary = (TypeLibraryType) updateGlobalTableWithNewLibrary(TYPE_LIBRARY_TYPE,
					PRODUCT_TYPE_LIBRARY);
			TypeLibraryType typeLib = new TypeLibraryType();
			typeLib.setLibraryName("LibraryTest");
			typeLib.setLibraryNamespace("http://www.ebayopensource.org");
			typeLib.setVersion("1.2.3");
			boolean flag = m_soaTypeRegistry.addTypeLibraryToRegistry(typeLib);

			//After call to addTypeLibraryToRegistry(productTypeLibrary)
			int finalCount = getAllLibrariesFromWorkspace();
			boolean checkFlag = (flag == (initialCount + 1 == finalCount));
			assertTrue("Invalid working of addTypeLibraryToRegistry(TypeLibraryType type).", checkFlag);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:" + e.getMessage(), false);
		}
	}
	
	
//	*********--- boolean addTypeLibraryToRegistry(TypeLibraryType library) ---************//
	/**
	 * Validates the working of boolean addTypeLibraryToRegistry(TypeLibraryType library)
	 * for invalid version.
	 */
	@Test
	public void testAddTypeLibraryToRegistryTLTInvalidVersion() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			//before call to addTypeLibraryToRegistry();
			int initialCount = getAllLibrariesFromWorkspace();

			TypeLibraryType productTypeLibrary = (TypeLibraryType) updateGlobalTableWithNewLibrary(TYPE_LIBRARY_TYPE,
					PRODUCT_TYPE_LIBRARY);
			productTypeLibrary.setVersion("1.2.3.4");
			boolean flag = m_soaTypeRegistry.addTypeLibraryToRegistry(productTypeLibrary);
			assertTrue("Invalid working of addTypeLibraryToRegistry(TypeLibraryType type).",flag);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input param TypeLibraryType's version is not in the format X.Y.Z where X,Y,Z are integers.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().equals(exceptionMessage));
		}
	}
	

	/**
	 * Validates the working of boolean addTypeLibraryToRegistry(TypeLibraryType library)
	 * for duplicate inputs.
	 */
	@Test
	//@Ignore("failing")
	public void testAddTypeLibraryToRegistryTLTDuplicateInputs() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			TypeLibraryType productTypeLibrary = (TypeLibraryType) updateGlobalTableWithNewLibrary(TYPE_LIBRARY_TYPE,
					PRODUCT_TYPE_LIBRARY);
			int initialCount = getAllLibrariesFromWorkspace();
			boolean flag = m_soaTypeRegistry.addTypeLibraryToRegistry(productTypeLibrary);
			boolean flag1 = m_soaTypeRegistry.addTypeLibraryToRegistry(productTypeLibrary);
			int finalCount = getAllLibrariesFromWorkspace();
			assertTrue("Invalid working of addTypeLibraryToRegistry(TypeLibraryType type).",
					(flag || flag1) || (initialCount == finalCount));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:" + e.getMessage(), false);
		}
	}

	/**
	 * Validates the working of boolean addTypeLibraryToRegistry(TypeLibraryType library)
	 * for new TypeLibraryType() inputs.
	 */
	@Test
	public void testAddTypeLibraryToRegistryTLTNewObject() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			TypeLibraryType productTypeLibrary = new TypeLibraryType();
			boolean flag = m_soaTypeRegistry.addTypeLibraryToRegistry(productTypeLibrary);
			assertFalse("Invalid working of addTypeLibraryToRegistry(TypeLibraryType type).", true);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input param TypeLibraryType's library name is either null or void.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().equals(exceptionMessage));
		}
	}

	/**
	 * Validates the working of boolean addTypeLibraryToRegistry(TypeLibraryType library)
	 * for Null inputs.
	 */
	@Test
	public void testAddTypeLibraryToRegistryTLNull() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			TypeLibraryType productTypeLibrary = null;
			boolean flag = m_soaTypeRegistry.addTypeLibraryToRegistry(productTypeLibrary);
			assertFalse("Invalid working of addTypeLibraryToRegistry(TypeLibraryType type).", true);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input param \"TypeLibraryType\" cannot be null.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().equals(exceptionMessage));
		}
	}

	//*********--- boolean addTypeLibraryToRegistry(TypeLibraryType library) ---************//
	/**
	 * Validate the working of boolean addTypeLibraryToRegistry(String library)
	 * for valid LibraryName.
	 */
	@Test
	//@Ignore("failing")
	public void testAddTypeLibraryToRegistryST() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			//boolean tFlag = utility.deleteTypeLibrary(PROJECT_ROOT_PRODUCT);
			//before call to addTypeLibraryToRegistry();
			int initialCount = getAllLibrariesFromWorkspace();
			boolean flag = m_soaTypeRegistry.addTypeLibraryToRegistry(PRODUCT_TYPE_LIBRARY);
			//After call to addTypeLibraryToRegistry(productTypeLibrary)

			
			createTempLibrary(PROJECT_ROOT_TEST,"Test");
			setEnvironment(folderConstant);
			m_soaTypeRegistry.addTypeLibraryToRegistry("Test");
			int finalCount = getAllLibrariesFromWorkspace();
			boolean checkFlag = (flag == (initialCount != finalCount));
			assertTrue("Invalid working of addTypeLibraryToRegistry(TypeLibraryType type).", checkFlag);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:" + e.getMessage(), false);
		}
	}

	/**
	 * Validates the working of boolean addTypeLibraryToRegistry(String library)
	 * for duplicate inputs.
	 */
	@Test
	public void testAddTypeLibraryToRegistrySLTDuplicateInputs() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			int initialCount = getAllLibrariesFromWorkspace();
			boolean flag = m_soaTypeRegistry.addTypeLibraryToRegistry(PRODUCT_TYPE_LIBRARY);
			boolean flag1 = m_soaTypeRegistry.addTypeLibraryToRegistry(PRODUCT_TYPE_LIBRARY);
			int finalCount = getAllLibrariesFromWorkspace();
			assertTrue("Invalid working of addTypeLibraryToRegistry(TypeLibraryType type).",
					(flag || flag1) || (initialCount == finalCount));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:" + e.getMessage(), false);
		}
	}

	/**
	 * Validates the working of boolean addTypeLibraryToRegistry(String library)
	 * for new String() inputs.
	 */
	@Test
	public void testAddTypeLibraryToRegistrySLTNewObject() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			String productTypeLibrary = new String();
			boolean flag = m_soaTypeRegistry.addTypeLibraryToRegistry(productTypeLibrary);
			assertFalse("Invalid working of addTypeLibraryToRegistry(TypeLibraryType type).", true);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Could not find the TypeInformation.xml file for library   at location : META-INF//TypeInformation.xml";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.library.exception.PopulateRegistryException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	/**
	 * Validates the working of boolean addTypeLibraryToRegistry(String library)
	 * for "" inputs.
	 */
	@Test
	public void testAddTypeLibraryToRegistrySLTNEmptyStr() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			String productTypeLibrary = "";
			boolean flag = m_soaTypeRegistry.addTypeLibraryToRegistry(productTypeLibrary);
			assertFalse("Invalid working of addTypeLibraryToRegistry(TypeLibraryType type).", true);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Could not find the TypeInformation.xml file for library   at location : META-INF//TypeInformation.xml";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.library.exception.PopulateRegistryException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	/**
	 * Validates the working of boolean addTypeLibraryToRegistry(TypeLibraryType library)
	 * for Null inputs.
	 */
	@Test
	public void testAddTypeLibraryToRegistrySLNull() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			String productTypeLibrary = null;
			boolean flag = m_soaTypeRegistry.addTypeLibraryToRegistry(productTypeLibrary);
			assertFalse("Invalid working of addTypeLibraryToRegistry(TypeLibraryType type).", true);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input param \"library\" cannot be null.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().equals(exceptionMessage));
		}
	}

	//*********--- boolean addTypeToRegistry(LibraryType libraryType) ---************//
	/**
	 * Validate the working of addTypeToRegistry(LibraryType libraryType) for 
	 * valid inputs.
	 */
	@Test
	public void testAddTypeToRegistryLT() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			
			LibraryType type = new LibraryType();
			type.setName("AddType1");
			type.setLibraryInfo((TypeLibraryType) updateGlobalTableWithNewLibrary(TYPE_LIBRARY_TYPE, CATEGORY_TYPE_LIBRARY));
			type.setNamespace(NAMESPACE);
			type.setVersion("1.2.3");
			int initialCount = m_soaTypeRegistry.getAllTypes().size();
			m_soaTypeRegistry.addTypeToRegistry(type);
			int finalCount = m_soaTypeRegistry.getAllTypes().size();
			assertTrue("Invalid working of addTypeToRegistry(LibraryType libraryType)", (initialCount != finalCount));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:" + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of addTypeToRegistry(LibraryType libraryType) for 
	 * type name as empty string.
	 */
	@Test
	public void testAddTypeToRegistryLTTypeNameEmptyStr() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			LibraryType type = new LibraryType();
			type.setName("");
			type.setLibraryInfo((TypeLibraryType) updateGlobalTableWithNewLibrary(TYPE_LIBRARY_TYPE, CATEGORY_TYPE_LIBRARY));
			type.setNamespace(NAMESPACE);
			type.setVersion("1.2.3");
			m_soaTypeRegistry.addTypeToRegistry(type);
			assertTrue("Invalid working of addTypeToRegistry(LibraryType libraryType)", false);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input params \"libraryType\" cannot have a null value for name.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().equals(exceptionMessage));
		}
	}

	/**
	 * Validate the working of addTypeToRegistry(LibraryType libraryType) for 
	 * type name as null.
	 */
	@Test
	public void testAddTypeToRegistryLTTypeNameNull() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			LibraryType type = new LibraryType();
			type.setName(null);
			type.setLibraryInfo((TypeLibraryType) updateGlobalTableWithNewLibrary(TYPE_LIBRARY_TYPE, CATEGORY_TYPE_LIBRARY));
			type.setNamespace(NAMESPACE);
			type.setVersion("1.2.3");
			m_soaTypeRegistry.addTypeToRegistry(type);
			assertTrue("Invalid working of addTypeToRegistry(LibraryType libraryType)", false);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input params \"libraryType\" cannot have a null value for name.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().equals(exceptionMessage));
		}
	}

	/**
	 * Validate the working of addTypeToRegistry(LibraryType libraryType) for 
	 * LibraryInfo as new TypeLibraryType().
	 */
	@Test
	public void testAddTypeToRegistryLTNewLibraryObj() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			LibraryType type = new LibraryType();
			type.setName("TestType1");
			type.setLibraryInfo(new TypeLibraryType());
			type.setNamespace(NAMESPACE);
			type.setVersion("1.2.3");
			m_soaTypeRegistry.addTypeToRegistry(type);
			assertTrue("Invalid working of addTypeToRegistry(LibraryType libraryType)", false);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input params \"libraryType\" cannot have a null value for the Library Name.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().equals(exceptionMessage));
		}
	}

	/**
	 * Validate the working of addTypeToRegistry(LibraryType libraryType) for 
	 * LibraryInfo as null.
	 */
	@Test
	public void testAddTypeToRegistryLTWithLibraryAsNull() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			LibraryType type = new LibraryType();
			type.setName("TestType1");
			type.setLibraryInfo(null);
			type.setNamespace(NAMESPACE);
			type.setVersion("1.2.3");
			m_soaTypeRegistry.addTypeToRegistry(type);
			assertTrue("Invalid working of addTypeToRegistry(LibraryType libraryType)", false);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input params \"libraryType\" cannot have a null value for the LibraryInfo.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().equals(exceptionMessage));
		}
	}

	/**
	 * Validate the working of addTypeToRegistry(LibraryType libraryType) for 
	 * Namespace different than the namespace of Library.
	 */
	@Test
	public void testAddTypeToRegistryLTWithDiffNamespace() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			
			LibraryType type = new LibraryType();
			type.setName("TestType1");
			type.setLibraryInfo((TypeLibraryType) updateGlobalTableWithNewLibrary(TYPE_LIBRARY_TYPE, CATEGORY_TYPE_LIBRARY));
			type.setNamespace("http://wrong/namespace");
			type.setVersion("1.2.3");
			m_soaTypeRegistry.addTypeToRegistry(type);
			assertTrue("Invalid working of addTypeToRegistry(LibraryType libraryType)", false);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "The namespace of the type and the library to which it belongs to do not match.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	/**
	 * Validate the working of addTypeToRegistry(LibraryType libraryType) for 
	 * Namespace as Null.
	 */
	@Test
	public void testAddTypeToRegistryLTWithNullNamespace() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			LibraryType type = new LibraryType();
			type.setName("TestType1");
			type.setLibraryInfo((TypeLibraryType) updateGlobalTableWithNewLibrary(TYPE_LIBRARY_TYPE, CATEGORY_TYPE_LIBRARY));
			type.setNamespace(null);
			type.setVersion("1.2.3");
			m_soaTypeRegistry.addTypeToRegistry(type);
			//assertTrue("Invalid working of addTypeToRegistry(LibraryType libraryType)", false);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "The namespace of the type and the library to which it belongs to do not match.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	/**
	 * Validate the working of addTypeToRegistry(LibraryType libraryType) for 
	 * version of format a.b.c.d
	 */
	@Test
	public void testAddTypeToRegistryLTWrongVersion() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			LibraryType type = new LibraryType();
			type.setName("TestType1");
			type.setLibraryInfo((TypeLibraryType) updateGlobalTableWithNewLibrary(TYPE_LIBRARY_TYPE, CATEGORY_TYPE_LIBRARY));
			type.setNamespace(NAMESPACE);
			type.setVersion("1.2.3.4");
			boolean value = m_soaTypeRegistry.addTypeToRegistry(type);
			System.out.println(value);
			assertTrue("Invalid working of addTypeToRegistry(LibraryType libraryType)", value);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "The namespace of the type and the library to which it belongs to do not match.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	/**
	 * Validate the working of addTypeToRegistry(LibraryType libraryType) for 
	 * version ""
	 */
	@Test
	public void testAddTypeToRegistryLTEmptyVersion() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			LibraryType type = new LibraryType();
			type.setName("TestType1");
			type.setLibraryInfo((TypeLibraryType) updateGlobalTableWithNewLibrary(TYPE_LIBRARY_TYPE, CATEGORY_TYPE_LIBRARY));
			type.setNamespace(NAMESPACE);
			type.setVersion("");
			m_soaTypeRegistry.addTypeToRegistry(type);
			//assertTrue("Invalid working of addTypeToRegistry(LibraryType libraryType)", false);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "The namespace of the type and the library to which it belongs to do not match.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	/**
	 * Validate the working of addTypeToRegistry(LibraryType libraryType) for 
	 * version ""
	 */
	@Test
	public void testAddTypeToRegistryLTNullVersion() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			LibraryType type = new LibraryType();
			type.setName("TestType1");
			type.setLibraryInfo((TypeLibraryType) updateGlobalTableWithNewLibrary(TYPE_LIBRARY_TYPE, CATEGORY_TYPE_LIBRARY));
			type.setNamespace(NAMESPACE);
			type.setVersion(null);
			m_soaTypeRegistry.addTypeToRegistry(type);
			//assertTrue("Invalid working of addTypeToRegistry(LibraryType libraryType)", false);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "The namespace of the type and the library to which it belongs to do not match.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	/**
	 * Validate the working of addTypeToRegistry(LibraryType libraryType) for 
	 * type null.
	 */
	@Test
	public void testAddTypeToRegistryLTTypeNull() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		//Set the environment.
		setEnvironment(folderConstant);
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			LibraryType type = null;
			m_soaTypeRegistry.addTypeToRegistry(type);
			assertTrue("Invalid working of addTypeToRegistry(LibraryType libraryType)", false);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input param \"libraryType\" cannot be null.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	//*********--- boolean addDependencyToRegistry(TypeDependencyType typeDependencyType , String libraryName) ---************//

	@Test
	public void testAddDependencyToRegistry() {
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		utility.setClassPath(testingdir.getDir().getAbsolutePath() );
		String typeDependenciesFilePath = getTestResrcDir()+"\\TypeLibraryCodegen\\CategoryTypeLibrary\\gen-meta-src\\META-INF\\CategoryTypeLibrary\\populateRegistryWithTypeLibraries\\1\\NewTypeDependencies.xml";
		File f = new File(typeDependenciesFilePath);
		String path = f.getAbsolutePath();
		boolean flag = f.exists();
		try {
			ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
			InputStream inStream = myClassLoader.getResourceAsStream(path);
			inStream = new FileInputStream(f);
			TypeLibraryDependencyType typeLibraryDependencyType = null;
			String typeName = "CategoryProduct";

			LibraryType libraryType = new LibraryType();
			libraryType.setName(typeName);

			int initialSize = m_soaTypeRegistry.getDependentParentTypeFiles(libraryType).size();
			System.out.println("Start:" + initialSize);
			typeLibraryDependencyType = JAXB.unmarshal(inStream, TypeLibraryDependencyType.class);

			for (TypeDependencyType type : typeLibraryDependencyType.getType()) {
				System.out.println("Type : " + type.getName());
				if (type.getName().equals(typeName)) {
					m_soaTypeRegistry.addDependencyToRegistry(type, CATEGORY_TYPE_LIBRARY);
				}
			}

			List<LibraryType> tlt = m_soaTypeRegistry.getDependentParentTypeFiles(libraryType);
			List<LibraryType> tlt1 = m_soaTypeRegistry.getDependentChildTypeFiles(libraryType);
			//System.out.println("End:"+finalSize );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showTypeInformation() throws Exception {
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		List<LibraryType> typeList = m_soaTypeRegistry.getAllTypes();
		for (int i = 0; i < typeList.size(); i++) {
			LibraryType type1 = typeList.get(i);
			System.out.println(type1.getName());
		}
		TypeLibraryType typeLibraryInfo = m_soaTypeRegistry.getTypeLibrary(PRODUCT_TYPE_LIBRARY);
		List<TypeInformationType> libType = typeLibraryInfo.getType();
		System.out.println("Library Name : " + typeLibraryInfo.getLibraryName()
				+ "  |  "
				+ "Library Version: "
				+ typeLibraryInfo.getLibraryNamespace());
		for (int j = 0; j < libType.size(); j++) {
			TypeInformationType typeInfo = libType.get(j);
			System.out.println("JavaTypeName: " + typeInfo.getJavaTypeName()
					+ "  |  "
					+ "Type Version: "
					+ typeInfo.getVersion()
					+ "  |  "
					+ "xml Type name: "
					+ typeInfo.getXmlTypeName());
		}
	}

	//*********--- boolean updateGlobalRegistry() ---************//
	/**
	 * Validate the working of the updateGlobalRegistry() method.
	 */
	@Test
	//@Ignore("failing")
	public void testUpdateGlobalRegistryValidInputs() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			setEnvironment(folderConstant);
			createTempLibrary(testingdir.getDir().getAbsolutePath() +"/LibraryTest", "LibraryTest");
			
			m_soaTypeRegistry.addTypeLibraryToRegistry(PRODUCT_TYPE_LIBRARY);

			//Create ProductTypeLibrary object.
			TypeLibraryType library = (TypeLibraryType) updateGlobalTableWithNewLibrary(TYPE_LIBRARY_TYPE,
					PRODUCT_TYPE_LIBRARY);
			createTypeLibrary(PROJECT_ROOT_TEST, "Test", "1.2.3");
			setEnvironment(folderConstant);
			//Create type object.
			LibraryType type = new LibraryType();
			type.setName("testUpdateGlobalRegistry");
			type.setLibraryInfo(library);
			type.setVersion("1.2.3");
			m_soaTypeRegistry.addTypeToRegistry(type);
			TypeLibraryType tlt = m_soaTypeRegistry.getTypeLibrary(PRODUCT_TYPE_LIBRARY);
			int initialCount = tlt.getType().size();
			
			//Type name should get updated to testUpdateGlobalRegistryChanged
			boolean flag1 = m_soaTypeRegistry.updateGlobalRegistry();
			tlt = m_soaTypeRegistry.getTypeLibrary(PRODUCT_TYPE_LIBRARY);
			int finalCount = tlt.getType().size();
			assertTrue("Updating to global registry failed.", (flag1 || (initialCount != finalCount)));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e.getMessage(), false);
		}

	}

	//*********---LibraryType getType(String typeName) ---************//
	/**
	 * Validate the working of getType(String typeName) for valid type name.
	 */
	@Test
	//@Ignore("failing")
	public void testGetTypeValidTypeName() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		String PRODUCT_TYPE = "CategoryName";
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		setEnvironment(folderConstant);
		try {
			
			
			LibraryType type = m_soaTypeRegistry.getType(PRODUCT_TYPE, CATEGORY_TYPE_LIBRARY);
			boolean flag = false;
			if (type != null && PRODUCT_TYPE.equals(type.getName()))
				flag = true;
			assertTrue("Invalid working of getType(String typeName)", flag);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of getType(String typeName) for non-existing type name.
	 */
	@Test
	public void testGetTypeNonExistingTypeName() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		String PRODUCT_TYPE = "CategoryName123";
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		setEnvironment(folderConstant);
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			LibraryType type = m_soaTypeRegistry.getType(PRODUCT_TYPE);
			boolean flag = false;
			if (type != null && PRODUCT_TYPE.equals(type.getName()))
				flag = true;
			assertFalse("Invalid working of getType(String typeName)", flag);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of getType(String typeName) for null type name.
	 */
	@Test
	public void testGetTypeNullTypeName() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		String PRODUCT_TYPE = null;
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		setEnvironment(folderConstant);
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			LibraryType type = m_soaTypeRegistry.getType(PRODUCT_TYPE);
			boolean flag = false;
			if (type != null && PRODUCT_TYPE.equals(type.getName()))
				flag = true;
			assertFalse("Invalid working of getType(String typeName)", flag);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input param \"typeName\" cannot be null.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	//*********--- List<LibraryType> getAllTypes() ---************//
	/**
	 * Validate the working of the List<LibraryType> getAllTypes() for valid library.
	 */
	@Test
	public void testGetAllTypesValidLib() {
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		try {
			List<LibraryType> typeList = m_soaTypeRegistry.getAllTypes();
			assertTrue("Invalid working of getAllTypes().", (typeList != null));
			int initialCount = typeList.size();
			TypeLibraryType productLibrary = m_soaTypeRegistry.getTypeLibrary(PRODUCT_TYPE_LIBRARY);
			LibraryType type1 = new LibraryType();
			type1.setLibraryInfo(productLibrary);
			type1.setName("Type1");
			m_soaTypeRegistry.addTypeToRegistry(type1);
			
			typeList = m_soaTypeRegistry.getAllTypes();
			assertTrue("Invalid working of getAllTypes().", (typeList != null));
			int interMidCount = typeList.size();
			//m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			
			LibraryType type2 = new LibraryType();
			type2.setLibraryInfo(productLibrary);
			type2.setName("Type2");
			m_soaTypeRegistry.addTypeToRegistry(type2);
			
			typeList = m_soaTypeRegistry.getAllTypes();
			assertTrue("Invalid working of getAllTypes().", (typeList != null));
			int finalCount = typeList.size();
			boolean flag = ((initialCount < interMidCount) && (interMidCount < finalCount));
			assertTrue("Invalid working of getAllTypes().", flag);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e, false);
		}
	}

	//*********--- Map<String,String> getTypeLibrariesVersion(List<String> typeLibraryNames) ---************//
	/**
	 * Validate the working of getTypeLibrariesVersion(List<String> typeLibraryNames) for valid LibraryName list.
	 */
	@Test
	public void testGetTypeLibrariesVersion() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		List<String> typeNames = new ArrayList();
		try {
			

			TypeLibraryType productTypeLibrary = m_soaTypeRegistry.getTypeLibrary(PRODUCT_TYPE_LIBRARY);
			TypeLibraryType categoryTypeLibrary = m_soaTypeRegistry.getTypeLibrary(CATEGORY_TYPE_LIBRARY);
			productTypeLibrary.setVersion("1.2.3");
			categoryTypeLibrary.setVersion("3.2.1");
			m_soaTypeRegistry.addTypeLibraryToRegistry(productTypeLibrary);
			m_soaTypeRegistry.addTypeLibraryToRegistry(categoryTypeLibrary);
			typeNames.add(PRODUCT_TYPE_LIBRARY);
			typeNames.add(CATEGORY_TYPE_LIBRARY);

			Map<String, String> typeVersions = m_soaTypeRegistry.getTypeLibrariesVersion(typeNames);
			boolean flag1 = ("1.2.3".equals(typeVersions.get(PRODUCT_TYPE_LIBRARY)));
			boolean flag2 = ("3.2.1".equals(typeVersions.get(CATEGORY_TYPE_LIBRARY)));
			assertTrue("Invalid working of getTypeLibrariesVersion(List<String> typeLibraryNames)", (flag1 || flag2));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured:" + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of getTypeLibrariesVersion(List<String> typeLibraryNames) for valid LibraryName list
	 * along with non existent libraryName.
	 */
	@Test
	public void testGetTypeLibrariesVersionInvalidName() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		List<String> typeNames = new ArrayList();
		try {
			

			TypeLibraryType productTypeLibrary = m_soaTypeRegistry.getTypeLibrary(PRODUCT_TYPE_LIBRARY);
			TypeLibraryType categoryTypeLibrary = m_soaTypeRegistry.getTypeLibrary(CATEGORY_TYPE_LIBRARY);
			productTypeLibrary.setVersion("1.2.3");
			categoryTypeLibrary.setVersion("3.2.1");
			m_soaTypeRegistry.addTypeLibraryToRegistry(productTypeLibrary);
			m_soaTypeRegistry.addTypeLibraryToRegistry(categoryTypeLibrary);
			typeNames.add(PRODUCT_TYPE_LIBRARY);
			typeNames.add(CATEGORY_TYPE_LIBRARY);
			typeNames.add("InvalidLibraryName");

			Map<String, String> typeVersions = m_soaTypeRegistry.getTypeLibrariesVersion(typeNames);
			boolean flag1 = ("1.2.3".equals(typeVersions.get(PRODUCT_TYPE_LIBRARY)));
			boolean flag2 = ("3.2.1".equals(typeVersions.get(CATEGORY_TYPE_LIBRARY)));
			assertTrue("Invalid working of getTypeLibrariesVersion(List<String> typeLibraryNames)", (flag1 || flag2));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured:" + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of getTypeLibrariesVersion(List<String> typeLibraryNames) for valid LibraryName list
	 * along with null libraryName.
	 */
	@Test
	public void testGetTypeLibrariesVersionNull() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		List<String> typeNames = new ArrayList();
		try {
			
			TypeLibraryType productTypeLibrary = m_soaTypeRegistry.getTypeLibrary(PRODUCT_TYPE_LIBRARY);
			TypeLibraryType categoryTypeLibrary = m_soaTypeRegistry.getTypeLibrary(CATEGORY_TYPE_LIBRARY);
			productTypeLibrary.setVersion("1.2.3");
			categoryTypeLibrary.setVersion("3.2.1");
			m_soaTypeRegistry.addTypeLibraryToRegistry(productTypeLibrary);
			m_soaTypeRegistry.addTypeLibraryToRegistry(categoryTypeLibrary);
			typeNames.add(PRODUCT_TYPE_LIBRARY);
			typeNames.add(null);
			typeNames.add(CATEGORY_TYPE_LIBRARY);

			Map<String, String> typeVersions = m_soaTypeRegistry.getTypeLibrariesVersion(typeNames);
			boolean flag1 = ("1.2.3".equals(typeVersions.get(PRODUCT_TYPE_LIBRARY)));
			boolean flag2 = ("3.2.1".equals(typeVersions.get(CATEGORY_TYPE_LIBRARY)));
			assertTrue("Invalid working of getTypeLibrariesVersion(List<String> typeLibraryNames)", (flag1 || flag2));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured:" + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of getTypeLibrariesVersion(List<String> typeLibraryNames) for valid LibraryName list
	 * along with duplicate libraryName.
	 */
	@Test
	public void testGetTypeLibrariesVersionDuplicateNames() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		List<String> typeNames = new ArrayList();
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(PRODUCT_TYPE_LIBRARY);
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);

			TypeLibraryType productTypeLibrary = m_soaTypeRegistry.getTypeLibrary(PRODUCT_TYPE_LIBRARY);
			TypeLibraryType categoryTypeLibrary = m_soaTypeRegistry.getTypeLibrary(CATEGORY_TYPE_LIBRARY);
			productTypeLibrary.setVersion("1.2.3");
			categoryTypeLibrary.setVersion("3.2.1");
			m_soaTypeRegistry.addTypeLibraryToRegistry(productTypeLibrary);
			m_soaTypeRegistry.addTypeLibraryToRegistry(categoryTypeLibrary);
			typeNames.add(CATEGORY_TYPE_LIBRARY);
			typeNames.add(CATEGORY_TYPE_LIBRARY);
			typeNames.add(CATEGORY_TYPE_LIBRARY);

			Map<String, String> typeVersions = m_soaTypeRegistry.getTypeLibrariesVersion(typeNames);
			boolean flag1 = (typeVersions.size() == 1);
			boolean flag2 = ("3.2.1".equals(typeVersions.get(CATEGORY_TYPE_LIBRARY)));
			assertTrue("Invalid working of getTypeLibrariesVersion(List<String> typeLibraryNames)", (flag1 || flag2));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured:" + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of getTypeLibrariesVersion(List<String> typeLibraryNames) for valid LibraryName list
	 * along with null parameter to method.
	 */
	@Test
	public void testGetTypeLibrariesVersionNullParam() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
		

			Map<String, String> typeVersions = m_soaTypeRegistry.getTypeLibrariesVersion(null);
			boolean flag1 = (typeVersions.size() == 1);
			boolean flag2 = ("3.2.1".equals(typeVersions.get(CATEGORY_TYPE_LIBRARY)));
			assertTrue("Invalid working of getTypeLibrariesVersion(List<String> typeLibraryNames)", (flag1 || flag2));
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "nput param \"typeLibraryNames\" cannot be null.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	//*********--- List<TypeLibraryType> getAllTypeLibraries() ---************//
	/**
	 * Validate the working of List<TypeLibraryType> getAllTypeLibraries().
	 */
	@Test
	public void testGetAllTypeLibraries() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			

			List<TypeLibraryType> libraryType = m_soaTypeRegistry.getAllTypeLibraries();
			boolean libName = false;
			boolean libNamespace = false;
			boolean libVersion = false;
			for (TypeLibraryType library : libraryType) {
				if (PRODUCT_TYPE_LIBRARY.equals(library.getLibraryName()))
					libName = true;

				if ("http://www.ebayopensource.org/soaframework/examples/config".equals(library.getLibraryNamespace()))
					libNamespace = true;

				if ("1.2.3".equals(library.getVersion()))
					libVersion = true;
			}
			assertTrue("Invalid working of List<TypeLibraryType> getAllTypeLibraries()",
					(libName || libVersion || libNamespace));

			boolean check = true;
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			libraryType = m_soaTypeRegistry.getAllTypeLibraries();
			for (TypeLibraryType library : libraryType) {
				if (PRODUCT_TYPE_LIBRARY.equals(library.getLibraryName())) {
					check = ("http://www.ebayopensource.org/soaframework/examples/config".equals(library.getLibraryNamespace())) || ("1.2.3".equals(library.getVersion()));
				} else if (CATEGORY_TYPE_LIBRARY.equals(library.getLibraryName())) {
					check = ("http://www.ebayopensource.org/soaframework/examples/config".equals(library.getLibraryNamespace())) || ("1.2.3".equals(library.getVersion()));
				} 
				assertTrue("Invalid working of List<TypeLibraryType> getAllTypeLibraries()", check);
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e.getMessage(), false);
		}
	}

	//*********--- TypeLibraryType getTypeLibrary(String typeLibName) ---************//
	/**
	 * Validate the working of TypeLibraryType getTypeLibrary(String typeLibName) for
	 * valid library name.
	 */
	@Test
	public void testGetTypeLibrary() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
		
			TypeLibraryType library = m_soaTypeRegistry.getTypeLibrary(PRODUCT_TYPE_LIBRARY);
			boolean libName = false;
			boolean libNamespace = false;
			boolean libVersion = false;

			if (PRODUCT_TYPE_LIBRARY.equals(library.getLibraryName()))
				libName = true;

			if ("http://www.ebayopensource.org/soaframework/examples/config".equals(library.getLibraryNamespace()))
				libNamespace = true;

			if ("1.2.3".equals(library.getVersion()))
				libVersion = true;

			assertTrue("Invalid working of List<TypeLibraryType> getAllTypeLibraries()",
					(libName || libVersion || libNamespace));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured :" + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of TypeLibraryType getTypeLibrary(String typeLibName) for
	 * invalid typeLibraryName.
	 */
	@Test
	public void testGetTypeLibraryInvalidLibrary() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			TypeLibraryType library = m_soaTypeRegistry.getTypeLibrary(PRODUCT_TYPE_LIBRARY + "Changed");
			assertTrue("Invalid working of List<TypeLibraryType> getAllTypeLibraries()", (library == null));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured :" + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of TypeLibraryType getTypeLibrary(String typeLibName) for
	 *  null typeLibraryName.
	 */
	@Test
	public void testGetTypeLibraryNullLibraryName() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		String libName = null;
		try {
			TypeLibraryType library = m_soaTypeRegistry.getTypeLibrary(libName);
			assertTrue("Invalid working of List<TypeLibraryType> getAllTypeLibraries()", (library == null));
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input param \"typeLibName\" cannot be null.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	/**
	 * Validate the working of TypeLibraryType getTypeLibrary(String typeLibName) for
	 *  empty typeLibraryName.
	 */
	@Test
	public void testGetTypeLibraryEmptyLibraryName() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			TypeLibraryType library = m_soaTypeRegistry.getTypeLibrary("");
			assertTrue("Invalid working of List<TypeLibraryType> getAllTypeLibraries()", (library == null));
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input param \"typeLibName\" cannot be null.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	//*********--- Set<String> getLibrariesReferredByType(String typeName) ---************//
	/**
	 * Validate the working of getLibrariesReferredByType(String typeName) for valid type name.
	 */
	@Test
	//@Ignore("failing")
	public void testGetLibrariesReferredByTypeST() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			QName typeQ = new QName(NAMESPACE,"CategoryName");
			Set<String> librariesRefered = m_soaTypeRegistry.getLibrariesReferredByType(typeQ);
			boolean flag = librariesRefered.contains(CATEGORY_TYPE_LIBRARY);
			assertTrue("Invalid working of getLibrariesReferredByType(String typeName)", flag);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of getLibrariesReferredByType(String typeName) for invalid
	 * type name.
	 */
	@Test
	public void testGetLibrariesReferredByTypeSTInvalidTypeName() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			

			QName typeQ = new QName(NAMESPACE,"CategoryName12345");	
			Set<String> librariesRefered = m_soaTypeRegistry.getLibrariesReferredByType(typeQ);
			boolean flag = librariesRefered.isEmpty();
			assertTrue("Invalid working of getLibrariesReferredByType(String typeName)", flag);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e.getMessage(), false);
		}
	}

	

	//*********--- Set<String> getLibrariesReferredByType(LibraryType typeName) ---************//
	/**
	 * Validate the working of getLibrariesReferredByType(LibraryType typeName) for valid
	 * LibraryType object.
	 */
	@Test
	//@Ignore("failing")
	public void testGetLibrariesReferredByTypeLT() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			LibraryType type = m_soaTypeRegistry.getType("ProductName", PRODUCT_TYPE_LIBRARY);
			Set<String> librariesRefered = m_soaTypeRegistry.getLibrariesReferredByType(type);
			boolean flag = librariesRefered.contains(PRODUCT_TYPE_LIBRARY);
			assertTrue("Invalid working of getLibrariesReferredByType(LibraryType typeName)", flag);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of getLibrariesReferredByType(LibraryType typeName) for new
	 * LibraryType object.
	 */
	@Test
	public void testGetLibrariesReferredByTypeLTNewObj() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			LibraryType type = new LibraryType();
			Set<String> librariesRefered = m_soaTypeRegistry.getLibrariesReferredByType(type);
			boolean flag = librariesRefered.isEmpty();
			assertTrue("Invalid working of getLibrariesReferredByType(LibraryType typeName)", flag);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input LibraryType's name is either null or empty.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	/**
	 * Validate the working of getLibrariesReferredByType(LibraryType typeName) for null
	 * LibraryType object.
	 */
	@Test
	public void testGetLibrariesReferredByTypeLTNull() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			LibraryType type = null;
			Set<String> librariesRefered = m_soaTypeRegistry.getLibrariesReferredByType(type);
			boolean flag = librariesRefered.isEmpty();
			assertTrue("Invalid working of getLibrariesReferredByType(LibraryType typeName)", flag);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input param \"LibraryType\" cannot be null.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	//*********--- List<LibraryType> getDependentParentTypeFiles(LibraryType libraryType) ---************//
	/**
	 * Validate the working of getDependentParentTypeFiles(LibraryType libraryType) for valid LibraryType 
	 * object.
	 */
	@Test
	//@Ignore("failing")
	public void testGetDependentParentTypeFiles() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			LibraryType libraryType = m_soaTypeRegistry.getType("CategoryName", CATEGORY_TYPE_LIBRARY);
			List<LibraryType> actualList = m_soaTypeRegistry.getDependentParentTypeFiles(libraryType);
			assertTrue("Invalid working of getDependentParentTypeFiles(LibraryType libraryType).",
					(actualList != null || actualList.size() != 0));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of getDependentParentTypeFiles(LibraryType libraryType) for invalid LibraryType 
	 * object.
	 */
	@Test
	public void testGetDependentParentTypeFilesInvalid() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			LibraryType libraryType = new LibraryType();
			libraryType.setName("XYZ");
			List<LibraryType> actualList = m_soaTypeRegistry.getDependentParentTypeFiles(libraryType);
			assertTrue("Invalid working of getDependentParentTypeFiles(LibraryType libraryType).", (actualList.size() == 0));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of getDependentParentTypeFiles(LibraryType libraryType) for null LibraryType 
	 * object.
	 */
	@Test
	public void testGetDependentParentTypeFilesNull() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			LibraryType libraryType = null;
			List<LibraryType> actualList = m_soaTypeRegistry.getDependentParentTypeFiles(libraryType);
			assertTrue("Invalid working of getDependentParentTypeFiles(LibraryType libraryType).", false);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input param \"LibraryType\" cannot be null.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	//*********--- List<LibraryType> getDependentChildTypeFiles(LibraryType libraryType) ---************//
	/**
	 * Validate the working of getDependentChildTypeFiles(LibraryType libraryType) for valid 
	 * LibraryType object.
	 */
	@Test
	//@Ignore("failing")
	public void testGetDependentChildTypeFiles() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			LibraryType libraryType = m_soaTypeRegistry.getType("ProductName",PRODUCT_TYPE_LIBRARY);
			List<LibraryType> actualList = m_soaTypeRegistry.getDependentChildTypeFiles(libraryType);
			assertTrue("Invalid working of getDependentParentTypeFiles(LibraryType libraryType).",
					(actualList != null || actualList.size() != 0));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured :" + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of getDependentChildTypeFiles(LibraryType libraryType) for invalid 
	 * LibraryType object.
	 */
	@Test
	public void testGetDependentChildTypeFilesInvalidType() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			LibraryType libraryType = new LibraryType();
			libraryType.setName("XYZ");
			List<LibraryType> actualList = m_soaTypeRegistry.getDependentChildTypeFiles(libraryType);
			assertTrue("Invalid working of getDependentParentTypeFiles(LibraryType libraryType).", (actualList.size() == 0));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured :" + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of getDependentChildTypeFiles(LibraryType libraryType) for null 
	 * LibraryType object.
	 */
	@Test
	public void testGetDependentChildTypeFilesNull() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			LibraryType libraryType = null;
			List<LibraryType> actualList = m_soaTypeRegistry.getDependentChildTypeFiles(libraryType);
			assertTrue("Invalid working of getDependentParentTypeFiles(LibraryType libraryType).", (actualList.size() == 0));
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input param \"LibraryType\" cannot be null.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	//*********--- boolean removeTypeFromRegistry(String typeName) ---************//
	/**
	 * Validate the working of removeTypeFromRegistry(String typeName) for valid type name.
	 */
	@Test
	public void testRemoveTypeFromRegistryST() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			List<LibraryType> types = m_soaTypeRegistry.getAllTypes();
			int initialCount = types.size();
			boolean check = true;
			QName typeQ = new QName(NAMESPACE,"CategoryName");
			boolean flag = m_soaTypeRegistry.removeTypeFromRegistry(typeQ);
			types = m_soaTypeRegistry.getAllTypes();
			for (LibraryType type : types) {
				if ("CategoryName".equals(type.getName())) {
					check = false;
				}
			}
			assertTrue("Invalid working of removeTypeFromRegistry(String typeName)", (check || flag));

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured : " + e.getMessage(), false);
		}
	}

	//*********--- boolean removeTypeFromRegistry(LibraryType typeName) ---************//
	/**
	 * Validate the working of removeTypeFromRegistry(LibraryType typeName) for valid LibraryType 
	 * Object.
	 */
	@Test
	public void testRemoveTypeFromRegistryLT() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			LibraryType productNameType = m_soaTypeRegistry.getType("CategoryName",CATEGORY_TYPE_LIBRARY);
			m_soaTypeRegistry.removeTypeFromRegistry(productNameType);
			List<LibraryType> types = m_soaTypeRegistry.getAllTypes();
			for (LibraryType type : types) {
				if ("CategoryName".equals(type.getName())) {
					//check = false;
				}
			}
			//assertTrue("Invalid working of removeTypeFromRegistry(String typeName)", (check || flag));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//*********--- boolean doesTypeNameExist(String typeName) ---************//
	/**
	 * Validate the working of doesTypeNameExist(String typeName) for valid type name.
	 */
	@Test
	//@Ignore("failing")
	public void testDoesTypeNameExistST() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			QName typeQ = new QName(NAMESPACE,"CategoryName");
			boolean type = m_soaTypeRegistry.doesTypeNameExist(typeQ);
			assertTrue("Invalid working of doesTypeNameExist(String typeName).", type);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of doesTypeNameExist(String typeName) for empty type name.
	 */
	@Test
	public void testDoesTypeNameExistSTEmpty() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			QName typeQ = new QName(NAMESPACE,"");
			boolean type = m_soaTypeRegistry.doesTypeNameExist(typeQ);
			assertFalse("Invalid working of doesTypeNameExist(String typeName).", type);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input LibraryType's name is either null or empty";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	/**
	 * Validate the working of doesTypeNameExist(String typeName) for invalid type name.
	 */
	@Test
	public void testDoesTypeNameExistSTInvalidTypeName() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			m_soaTypeRegistry.addTypeLibraryToRegistry(CATEGORY_TYPE_LIBRARY);
			QName typeQ = new QName(NAMESPACE,"XYZ");
			boolean type = m_soaTypeRegistry.doesTypeNameExist(typeQ);
			assertFalse("Invalid working of doesTypeNameExist(String typeName).", type);
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue("Following exception occured: " + ex.getMessage(), false);
		}
	}

	

	//*********--- boolean doesTypeNameExist(LibraryType typeName) ---************//
	/**
	 * Validate the working of doesTypeNameExist(LibraryType typeName) for valid libraryType object.
	 */
	@Test
	//@Ignore("failing")
	public void testDoesTypeNameExistLT() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			QName typeQ = new QName(NAMESPACE,"CategoryName");
			LibraryType type = m_soaTypeRegistry.getType(typeQ);
			boolean catType = m_soaTypeRegistry.doesTypeNameExist(type);
			assertTrue("Invalid working of doesTypeNameExist(String typeName).", catType);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of doesTypeNameExist(LibraryType typeName) for new libraryType object.
	 */
	@Test
	public void testDoesTypeNameExistLTNewObj() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			LibraryType type = new LibraryType();
			type.setName("XYZ");
			boolean catType = m_soaTypeRegistry.doesTypeNameExist(type);
			assertFalse("Invalid working of doesTypeNameExist(String typeName).", catType);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Following exception occured: " + e.getMessage(), false);
		}
	}

	/**
	 * Validate the working of doesTypeNameExist(LibraryType typeName) for null.
	 */
	@Test
	public void testDoesTypeNameExistLTNull() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
		
			LibraryType type = null;
			boolean catType = m_soaTypeRegistry.doesTypeNameExist(type);
			assertFalse("Invalid working of doesTypeNameExist(String typeName).", catType);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input param \"LibraryType\" cannot be null.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	/**
	 * Validate the working of doesTypeNameExist(LibraryType typeName) for LibraryType object without name.
	 */
	@Test
	public void testDoesTypeNameExistLTWithoutName() {
		String folderConstant = "populateRegistryWithTypeLibraries/1";
		setEnvironment(folderConstant);
		m_soaTypeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		try {
			
			LibraryType type = new LibraryType();
			boolean catType = m_soaTypeRegistry.doesTypeNameExist(type);
			assertFalse("Invalid working of doesTypeNameExist(String typeName).", catType);
		} catch (Exception ex) {
			ex.printStackTrace();
			String exceptionMessage = "Input LibraryType's name is either null or empty.";
			String exceptionClassName = "org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException";
			assertTrue("Exception class is invalid. \n Actual:" + ex.getClass().getName()
					+ " \n Expected:"
					+ exceptionClassName, ex.getClass().getName().equals(exceptionClassName));
			assertTrue("Exception message is Invalid. \n Actual:" + ex.getMessage() + "\n Expected:" + exceptionMessage,
					ex.getMessage().contains(exceptionMessage));
		}
	}

	public static void main(String[] args) {
		SOAGlobalRegistryQETest test = new SOAGlobalRegistryQETest();
		test.testDoesTypeNameExistLTWithoutName();
	}

	private Object updateGlobalTableWithNewLibrary(String objectName, String typeLibraryName) throws Exception {
		String projectRoot = PROJECT_ROOT +"/" + typeLibraryName;
		String libraryName = typeLibraryName;
		TypeLibraryType typeLibraryType = null;
		Object globalObject = null;
		String typeInfoXMLFilePath = projectRoot +File.separator +"gen-meta-src/META-INF/" +typeLibraryName+
				 "/TypeInformation.xml";
		File file = new File(typeInfoXMLFilePath);
		try {
			typeLibraryType = JAXB.unmarshal(file, TypeLibraryType.class);
		} catch (Throwable t) {
			throw new Exception(t);
		} finally {
		}

		if (objectName.equals("TypeLibraryType")) {
			globalObject = typeLibraryType;
		}
		return globalObject;
	}

	private Object updateGlobalTableWithDependentLibrary(String objectName, String typeLibraryName) throws Exception {
		String projectRoot = PROJECT_ROOT +"/" + typeLibraryName;
		String libraryName = typeLibraryName;
		TypeLibraryType typeDependencyType = null;
		Object globalObject = null;
		String typeInfoXMLFilePath = projectRoot +File.separator +"gen-meta-src/META-INF" +typeLibraryName+
		 "/TypeInformation.xml";
		File file = new File(typeInfoXMLFilePath);
		try {
			typeDependencyType = JAXB.unmarshal(file, TypeLibraryType.class);
		} catch (Throwable t) {
			throw new Exception(t);
		} finally {
		}

		if (objectName.equals("TypeDependentType")) {
			globalObject = typeDependencyType;
		}
		return globalObject;
	}

	private boolean deleteTypeLibrary(String projectRoot) {
		File projectDir = new File(projectRoot);
		if (projectDir.isDirectory()) {
			String[] childFiles = projectDir.list();
			for (int i = 0; i < childFiles.length; i++) {
				deleteTypeLibrary(projectRoot + "\\" + childFiles[i]);
			}
		}
		boolean flag = projectDir.delete();
		System.out.println(flag);
		return flag;
	}

	private String getTypeInformationXMLPath(String projectRoot, String libraryName, String aditionalPath) {
		String path = null;
		if (aditionalPath != null && aditionalPath.trim().length() != 0) {
			path = projectRoot + "/gen-meta-src/META-INF/" + libraryName + "/" + aditionalPath + "/TypeInformation.xml";
		} else {
			path = projectRoot + "/gen-meta-src/META-INF/" + libraryName + "/TypeInformation.xml";
		}
		return path;
	}

	private boolean createTypeLibrary(String projectRoot, String libraryName, String version) {
		boolean flag = false;
		ServiceGenerator sGenerator = new ServiceGenerator();
		String[] pluginParameter = { "-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				projectRoot,
				"-libname",
				libraryName,
				"-libVersion",
				version,
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

	private boolean addTypeToLibrary(String projectRoot, String libraryName, String xsdName) {
		boolean flag = false;
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
		}
		return flag;
	}

	/**
	 * This method will set environment. Following tasks are to be done by this method.
	 * 1. Delete ProductTypeLibrary and CategoryTypeLibrary.
	 * 2. Create ProductTypeLibrary and CategoryTypeLibrary.
	 * 3. Update the TypeInformation.xml and TypeDependencies.xml.
	 * 4. Add these projects into the class path.
	 * @param folderConstant
	 */
	private void setEnvironment(String folderConstant) {
		

		//Create CategoryTypeLibrary
		boolean createCategoryLib = createTypeLibrary(PROJECT_ROOT_CATEGORY, CATEGORY_TYPE_LIBRARY, "1.2.3");
		assertTrue(CATEGORY_TYPE_LIBRARY + " creation failed.", createCategoryLib);
		
		try {
			TestResourceUtil.copyResource("types/CategoryTypeLibrary/CategoryName.xsd", testingdir, "CategoryTypeLibrary/meta-src");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		addTypeToLibrary(PROJECT_ROOT_CATEGORY,CATEGORY_TYPE_LIBRARY,"CategoryName.xsd");
		
		//Create CategoryTypeLibrary
		boolean createProductLib = createTypeLibrary(PROJECT_ROOT_PRODUCT, PRODUCT_TYPE_LIBRARY, "3.2.1");
		assertTrue(PRODUCT_TYPE_LIBRARY + " creation failed.", createProductLib);
		try {
			TestResourceUtil.copyResource("types/ProductTypeLibrary/ProductName.xsd", testingdir, "ProductTypeLibrary/meta-src");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addTypeToLibrary(PROJECT_ROOT_PRODUCT,PRODUCT_TYPE_LIBRARY,"ProductName.xsd");
		
		
		//Copy TypeInformation.xml of CategoryTypeLibrary from Vanilla Copies to AntTests.
		String categoryTIXmlCodegenPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_CATEGORY,
				CATEGORY_TYPE_LIBRARY,
				null);
		String categoryTIXmlVanillaPath =  getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/gen-meta-src/META-INF/CategoryTypeLibrary/"+folderConstant+"/TypeInformation.xml";
		boolean updateTIXmlCat = utility.updateSourceFile(categoryTIXmlVanillaPath, categoryTIXmlCodegenPath);
		assertTrue("TypeInformation.xml updation failed for Library" + CATEGORY_TYPE_LIBRARY, updateTIXmlCat);

		//Copy TypeInformation.xml of ProductTypeLibrary from Vanilla Copies to AntTests.
		String productTIXmlCodegenPath = utility.getTypeInformationXMLPath(PROJECT_ROOT_PRODUCT, PRODUCT_TYPE_LIBRARY, null);
		String productTIXmlVanillaPath = getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/ProductTypeLibrary/gen-meta-src/META-INF/ProductTypeLibrary/"+folderConstant+"/TypeInformation.xml";
		boolean updateTIXmlProd = utility.updateSourceFile(productTIXmlVanillaPath, productTIXmlCodegenPath);
		assertTrue("TypeInformation.xml updation failed for Library" + PRODUCT_TYPE_LIBRARY, updateTIXmlProd);

		String categoryTDXmlCodegenPath = testingdir.getDir().getAbsolutePath() + "/CategoryTypeLibrary/meta-src/META-INF/CategoryTypeLibrary/TypeDependencies.xml";
		String categoryTDXmlVanillaPath =getTestResrcDir().getAbsolutePath() + "/TypeLibraryCodegen/CategoryTypeLibrary/meta-src/META-INF/CategoryTypeLibrary/TypeDependencies.xml";
		boolean updateTDXmlCat = utility.updateSourceFile(categoryTDXmlVanillaPath, categoryTDXmlCodegenPath);
		assertTrue("TypeDependencies.xml updation failed for Library", updateTDXmlCat);

		//Set the class path
		boolean classPath = utility.setClassPath(testingdir.getDir().getAbsolutePath() );
		assertTrue("Class path is not set properly.", classPath);
	}

	private int getAllLibrariesFromWorkspace() {
		int count = 0;
		List<TypeLibraryType> allTypeLibrary = null;
		try {
			allTypeLibrary = m_soaTypeRegistry.getAllTypeLibraries();
			count = allTypeLibrary.size();
			for (int i = 0; i < allTypeLibrary.size(); i++) {
				TypeLibraryType library = allTypeLibrary.get(i);
				System.out.println((i + 1) + "." + library.getLibraryName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("No exception should be thrown. Exception:" + e.getMessage(), false);
		}
		return count;
	}
	
	private void createTempLibrary(String projectRoot,String libraryName){
		String[] pluginParameter = {
				"-gentype",
				"genTypeCreateTypeLibrary",
				"-pr",
				projectRoot,
				"-libname", libraryName, "-libVersion", "1.2.3" ,"-libNamespace",NAMESPACE};
		try{
			ServiceGenerator sGenerator = new ServiceGenerator();
			sGenerator.startCodeGen(pluginParameter);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
}
