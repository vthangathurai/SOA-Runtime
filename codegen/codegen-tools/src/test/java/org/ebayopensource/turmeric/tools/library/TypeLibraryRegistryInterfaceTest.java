/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.ebayopensource.turmeric.common.config.LibraryType;
import org.ebayopensource.turmeric.common.config.TypeDependencyType;
import org.ebayopensource.turmeric.common.config.TypeLibraryDependencyType;
import org.ebayopensource.turmeric.common.config.TypeLibraryType;

/**
 * @author arajmony
 * 
 */
public class TypeLibraryRegistryInterfaceTest extends AbstractServiceGeneratorTestCase {
	private static SOATypeRegistry m_soaTypeRegistry;
	static String m_libraryName = "HardwareTypeLibraryTest";
	static  String m_libraryNS ;
	
	@BeforeClass
	public static void initRegistry() throws Exception {
		m_soaTypeRegistry = SOAGlobalRegistryImpl.getInstance();

		List<String> list = new ArrayList<String>();
		list.add(m_libraryName);
		
		m_soaTypeRegistry.populateRegistryWithTypeLibraries(list);
	}
	
	public static TypeLibraryType getTypeLibrary(String libraryName) throws Exception {
		TypeLibraryType typeLibrary = m_soaTypeRegistry.getTypeLibrary(m_libraryName);
		assertNotNull("Unable to get TypeLibraryType for [" + libraryName + "]", typeLibrary);
		return typeLibrary;
	}

	@Test
	public void populateRegistry() throws Exception {
		String message = "The number of expected types in the registry is 22 , but it is ";

		int size = m_soaTypeRegistry.getAllTypes().size();

		assertEquals(message + size,23, size);
	}

	@Test
	public void getType_FindCase()  throws Exception {
		/*
		 * fetch  a type which exists
		 */
		String type = "hardwareType";
		LibraryType libraryType = null;

		String libNS = getTypeLibrary(m_libraryName).getLibraryNamespace();
		QName typesQName = new QName(libNS,type);

		try{
			libraryType = m_soaTypeRegistry.getType(typesQName);
		}catch(Exception e){e.printStackTrace(); throw e;}

		assertEquals(type, libraryType.getName());
	}

	@Test
	public void getType_FailCase()  throws Exception {
		/*
		 * fetch a type which does not exist
		 */
		String type = "hardwareTypeNotExist";
		LibraryType libraryType = null;

		String libNS = getTypeLibrary(m_libraryName).getLibraryNamespace();
		QName typesQName = new QName(libNS,type);

		libraryType = m_soaTypeRegistry.getType(typesQName);
		//Expected behavior - libraryType should be null
		Assert.assertNull(libraryType);
		   
	}

	@Test
	public void getAllTypeLibraries_forSize()  throws Exception {
			List<TypeLibraryType> list = m_soaTypeRegistry.getAllTypeLibraries();

			assertTrue(list.size() >= 1); //since the type library could have already some libraries

	}

	@Test
	public void getAllTypeLibraries_forContent()  throws Exception {
		boolean libraryFound= false;
			List<TypeLibraryType> list = m_soaTypeRegistry.getAllTypeLibraries();
			//assertEquals(m_libraryName, list.get(0).getLibraryName());
			for(TypeLibraryType typeLibraryType : list){
				if(typeLibraryType.getLibraryName().equals(m_libraryName))
					libraryFound = true;
			}

			assertTrue(libraryFound);
	}


	@Test
	public void getAllTypeLibrariesNames()  throws Exception {
			Set<String> set= m_soaTypeRegistry.getAllTypeLibrariesNames();
			assertTrue(set.contains(m_libraryName));
	}

	@Test
	public void getTypeLibrary_ForValidLibrary()  throws Exception {
		getTypeLibrary(m_libraryName);
	}

	@Test
	public void getTypeLibrary_ForInValidLibrary()  throws Exception {
		getTypeLibrary(m_libraryName + "Invalid");
	}

	public void getLibrariesReferredByType()  throws Exception {
		String ns = getTypeLibrary(m_libraryName).getLibraryNamespace();
		Assert.assertThat("Namespace should not be null", ns, notNullValue());

		@SuppressWarnings("deprecation")
        Set<String> set = m_soaTypeRegistry.getLibrariesReferredByType("harddiskType222");
		Assert.assertThat(set, hasItem(m_libraryName));
	}

	@Test
	public void getLibrariesReferredByType_overRiddenMethod()  throws Exception {
			LibraryType libraryType = new LibraryType();
			libraryType.setName("harddiskType222");

			String ns = getTypeLibrary(m_libraryName).getLibraryNamespace();
			libraryType.setNamespace(ns);
			Set<String> set = m_soaTypeRegistry.getLibrariesReferredByType(libraryType);
			Assert.assertThat(set, hasItem(m_libraryName));
	}

	@Test
	public void getDependentParentTypeFiles()  throws Exception {
			List<LibraryType> expectedList = new ArrayList<LibraryType>();
			LibraryType type = new LibraryType();

			String ns = getTypeLibrary(m_libraryName).getLibraryNamespace();


			type.setName("speeddetailsharddiskType");
			expectedList.add(type);
			type.setName("harddiskdetailsType");
			expectedList.add(type);
			type.setName("hardwareType");
			expectedList.add(type);



			LibraryType libraryType = new LibraryType();
			libraryType.setName("harddiskType");
			libraryType.setNamespace(ns);

			List<LibraryType> actualList = m_soaTypeRegistry.getDependentParentTypeFiles(libraryType);
			System.out.println(actualList);

			boolean result = assertLibraryTypeLists(expectedList,actualList);
			assertTrue(result);
	}

	@Test
	public void getDependentChildTypeFiles()  throws Exception {
			List<LibraryType> expectedList = new ArrayList<LibraryType>();
			LibraryType type = new LibraryType();

			type.setName("secondarymemoryType");
			expectedList.add(type);

			String ns = getTypeLibrary(m_libraryName).getLibraryNamespace();
			LibraryType libraryType = new LibraryType();
			libraryType.setName("harddiskType");
			libraryType.setNamespace(ns);

			List<LibraryType> actualList = m_soaTypeRegistry.getDependentChildTypeFiles(libraryType);
			boolean result = assertLibraryTypeLists(expectedList,actualList);
			assertTrue(result);
	}

	@SuppressWarnings("deprecation")
    @Test
	public void doesTypeNameExist_StringInput()  throws Exception {
	    Assert.assertThat(m_soaTypeRegistry.doesTypeNameExist("harddiskType"), is(true));
	}

	@Test
	public void doesTypeNameExist_TypeInput()  throws Exception {
			String ns = getTypeLibrary(m_libraryName).getLibraryNamespace();
			LibraryType libraryType = new LibraryType();
			libraryType.setName("harddiskType");
			libraryType.setNamespace(ns);
			assertTrue(m_soaTypeRegistry.doesTypeNameExist(libraryType));
	}

	@Test
	public void addTypeToRegistry_TypeInput()  throws Exception {
			String newType = "harddiskTypeNewType";
			LibraryType libraryType = new LibraryType();
			libraryType.setName(newType);


			TypeLibraryType typeLibraryType = new TypeLibraryType();
			typeLibraryType.setLibraryName(m_libraryName);
			libraryType.setLibraryInfo(typeLibraryType);

			int countBefore = m_soaTypeRegistry.getAllTypes().size();

			assertTrue(m_soaTypeRegistry.addTypeToRegistry(libraryType));

			String libNS = getTypeLibrary(m_libraryName).getLibraryNamespace();
			QName typesQName = new QName(libNS,newType);
			assertEquals(newType, m_soaTypeRegistry.getType(typesQName).getName());

			assertEquals(countBefore+1, m_soaTypeRegistry.getAllTypes().size() );
	}

	@SuppressWarnings("unchecked")
	@Test
	public void addTypeToRegistry_TypeInput_Fail_case_NS_mismatch()  throws Exception {
		try{
			String newType = "harddiskTypeNewTypeNs_mis";
			LibraryType libraryType = new LibraryType();
			libraryType.setName(newType);
			libraryType.setNamespace("http:\\invalid.com");


			TypeLibraryType typeLibraryType = new TypeLibraryType();
			typeLibraryType.setLibraryName(m_libraryName);
			libraryType.setLibraryInfo(typeLibraryType);

			m_soaTypeRegistry.addTypeToRegistry(libraryType);
			Assert.fail("Expected exception of type: " + BadInputValueException.class.getName());
		} catch (BadInputValueException ex) {
			Assert.assertThat(ex.getMessage(), allOf(
					containsString("The namespace of the type and the library to which it belongs to do not match"),
					containsString("http:\\invalid.com"),
					containsString("http://www.ebayopensource.org/soaframework/examples/config")));
		}
	}

	@Test
	public void addTypeToRegistry_TypeInput_NS_match()  throws Exception {
			String newType = "harddiskTypeNewTypeNS";
			LibraryType libraryType = new LibraryType();
			libraryType.setName(newType);

			TypeLibraryType typeLibraryTypePar = getTypeLibrary(m_libraryName);
			libraryType.setNamespace(typeLibraryTypePar.getLibraryNamespace());


			TypeLibraryType typeLibraryType = new TypeLibraryType();
			typeLibraryType.setLibraryName(m_libraryName);
			libraryType.setLibraryInfo(typeLibraryType);

			int countBefore = m_soaTypeRegistry.getAllTypes().size();
			assertTrue(m_soaTypeRegistry.addTypeToRegistry(libraryType));

			String libNS = getTypeLibrary(m_libraryName).getLibraryNamespace();
			QName typesQName = new QName(libNS,newType);

			assertEquals(newType, m_soaTypeRegistry.getType(typesQName).getName());
			assertEquals(countBefore+1, m_soaTypeRegistry.getAllTypes().size() );
	}

	@Test
	public void addDependencyToRegistry()  throws Exception {
			String typeDependenciesFilePath   = TypeLibraryConstants.META_INF_FOLDER + "/" + m_libraryName + "/" + "NewTypeDependencies.xml"; 
			ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
			TypeLibraryDependencyType typeLibraryDependencyType = null;
			String typeName= "harddiskType";

			String libNS = getTypeLibrary(m_libraryName).getLibraryNamespace();
			LibraryType libraryType = new LibraryType();
			libraryType.setName(typeName);
			libraryType.setNamespace(libNS);

			int initialSize = m_soaTypeRegistry.getDependentParentTypeFiles(libraryType).size();

			InputStream	inStream = null;
			try{
				inStream   = myClassLoader.getResourceAsStream(typeDependenciesFilePath);
				typeLibraryDependencyType = JAXB.unmarshal(inStream,TypeLibraryDependencyType.class );

				for(TypeDependencyType type : typeLibraryDependencyType.getType()){
					if(type.getName().equals(typeName)){
						m_soaTypeRegistry.addDependencyToRegistry(type, m_libraryName);
					}
				}

				int finalSize = m_soaTypeRegistry.getDependentParentTypeFiles(libraryType).size();
				assertTrue(finalSize > initialSize);
			}finally{
				IOUtils.closeQuietly(inStream);
			}
	}

	@Test
	public void removalOfPartialDependencyFromTheDependencyFile()  throws Exception {
			String typeDependenciesFilePath   = TypeLibraryConstants.META_INF_FOLDER + "/" + m_libraryName + "/" + "NewTypeDependencies.xml"; 
			ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
			TypeLibraryDependencyType typeLibraryDependencyType = null;
			String typeName= "speeddetailsramType";

			String libNS = getTypeLibrary(m_libraryName).getLibraryNamespace();
			LibraryType libraryType = new LibraryType();
			libraryType.setName(typeName);
			libraryType.setNamespace(libNS);

			int initialSize = m_soaTypeRegistry.getDependentChildTypeFiles(libraryType).size();
				InputStream	inStream   = null;
				try{
					inStream  = myClassLoader.getResourceAsStream(typeDependenciesFilePath);
					typeLibraryDependencyType = JAXB.unmarshal(inStream,TypeLibraryDependencyType.class );

					for(TypeDependencyType type : typeLibraryDependencyType.getType()){
						m_soaTypeRegistry.addDependencyToRegistry(type, m_libraryName);
					}

					int finalSize = m_soaTypeRegistry.getDependentChildTypeFiles(libraryType).size();
					assertTrue(initialSize > finalSize);
				}finally{
					IOUtils.closeQuietly(inStream);
				}
	}

	@Test
	public void removalOfTotalDependencyFromTheDependencyFile()  throws Exception {
			List<String> lib = new ArrayList<String>();
			lib.add(m_libraryName);

			m_soaTypeRegistry.populateRegistryWithTypeLibraries(lib);

			String libNS = getTypeLibrary(m_libraryName).getLibraryNamespace();
			String typeName= "processorType";
			LibraryType libraryType = new LibraryType();
			libraryType.setName(typeName);
			libraryType.setNamespace(libNS);

			int initialNumberOfDirectParents = m_soaTypeRegistry.getDependentParentTypeFiles(libraryType, 1).size();


			String typeName2= "processordetailsType";
			LibraryType libraryType2 = new LibraryType();
			libraryType2.setName(typeName2);
			libraryType2.setNamespace(libNS);
			int initialNumberOfDirectChilds = m_soaTypeRegistry.getDependentChildTypeFiles(libraryType2, 1).size();



			String typeDependenciesFilePath   = TypeLibraryConstants.META_INF_FOLDER + "/" + m_libraryName + "/" + "NewTypeDependenciesDependencyTotallyRemoved.xml"; 
			ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
			TypeLibraryDependencyType typeLibraryDependencyType = null;


				InputStream	inStream = null;
				try{
					inStream   = myClassLoader.getResourceAsStream(typeDependenciesFilePath);
					typeLibraryDependencyType = JAXB.unmarshal(inStream,TypeLibraryDependencyType.class );

					TypeDependencyParser typeDependencyParser = TypeDependencyParser.getInstance();
					typeDependencyParser.processTypeLibraryDependencyType(typeLibraryDependencyType);

					int finalNumberOfParents = m_soaTypeRegistry.getDependentParentTypeFiles(libraryType, 1).size();
					int finalNumberOfDirectChilds = m_soaTypeRegistry.getDependentChildTypeFiles(libraryType2, 1).size();


					boolean areParentsGood =  initialNumberOfDirectParents >0  &&  finalNumberOfParents == 0;
					boolean areChildsGood  =  ((initialNumberOfDirectChilds - finalNumberOfDirectChilds) == 1)?true:false;

					assertTrue(areParentsGood && areChildsGood);
				}finally{
					IOUtils.closeQuietly(inStream);
				}
	}


	private boolean assertLibraryTypeLists(List<LibraryType> expectedList, List<LibraryType> actualList) {

		if(expectedList.size() != actualList.size())
			return false;

		List<String> expectedNames = new ArrayList<String>();
		List<String> actualNames = new ArrayList<String>();

		Iterator<LibraryType> iterator = expectedList.iterator();
		while(iterator.hasNext())
			expectedNames.add(iterator.next().getName());

		iterator = actualList.iterator();
		while(iterator.hasNext())
			actualNames.add(iterator.next().getName());


		Iterator<String> iterator2 = expectedNames.iterator();
		while(iterator2.hasNext()){
			if(!actualNames.contains(iterator2.next()))
				return false;
		}



		return true;
	}


	@Test
	public void addTypeLibraryToRegistry_fail_case_version()  throws Exception {
		String libraryName = "testAddTypeLibraryToRegistry_fail_case_version";
		TypeLibraryType typeLibraryType = new TypeLibraryType();
		typeLibraryType.setLibraryName(libraryName);
		typeLibraryType.setVersion("1.2.3.4");

		try{
			m_soaTypeRegistry.addTypeLibraryToRegistry(typeLibraryType);
			Assert.fail("Expected exception of type: " + BadInputValueException.class.getName());
		} catch (BadInputValueException ex) {
			Assert.assertThat(ex.getMessage(), 
					containsString("Input param TypeLibraryType's version is not in the format X.Y.Z"));
		}
	}

}
