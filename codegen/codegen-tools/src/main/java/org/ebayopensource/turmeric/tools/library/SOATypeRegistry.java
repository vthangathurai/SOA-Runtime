/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.tools.library;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.common.config.LibraryType;
import org.ebayopensource.turmeric.common.config.TypeDependencyType;
import org.ebayopensource.turmeric.common.config.TypeLibraryType;


/**
 * @author arajmony
 *
 * This interface holds the methods related to the Type Library.
 */
public interface SOATypeRegistry {

	
	
	/**
	 * Given a list of library names, the method updates the global registry for all the tables.
	 * 
	 * @param typeLibraryNames
	 * @return
	 * @throws Exception
	 */
	boolean populateRegistryWithTypeLibraries(List<String> typeLibraryNames)  throws Exception;
	
	
	/**
     * Given a list of library names, the method updates the global registry for all the libraries mentioned.
     * 
     * @param typeLibraryNames
     * @return list of RegistryUpdateDetails details of the libraries for which the initialization has failed.
     * @throws Exception
     */
	List<RegistryUpdateDetails> populateRegistryWithTypeLibrariesDetailed(List<String> typeLibraryNames)  throws Exception;
	
	
	/**
	 * Given the details of a type library in an object of type TypeLibraryType , the method adds
	 * the detail to the GlobalRegistry
	 * @param library type of TypeLibraryType which contains the basic info of a type library
	 * @return
	 * @throws Exception
	 */
	boolean addTypeLibraryToRegistry(TypeLibraryType library) throws Exception;
	
	
	
	/**
	 * Given the name of a library ,this method will populate/update the GlobalRegistry for the same.
	 * @param library Name of the library
	 * @return
	 * @throws Exception
	 */
	boolean addTypeLibraryToRegistry(String library) throws Exception;

	
	/** -- use the overloade method addTypeToRegistry(LibraryType libraryType)
	 * Given a type name, the methods adds the details of the same into the GlobalRegistry
     * @param typeName Type name of the type to be added
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	boolean addTypeToRegistry(String typeName) throws Exception;
	
	
	
	/**
	 * Given the details of a type , the method adds the details of the same into the GlobalRegistry
	 * @param libraryType An instance of org.ebayopensource.turmeric.runtime.common.config.LibraryType
	 * @return operation status.
	 * @throws Exception
	 */
	boolean addTypeToRegistry(LibraryType libraryType) throws Exception;
	

	
	/** 
	 * Given the dependency mapping for a type , the method updates the GlobalRegistry with that information.
	 * @param typeDependencyType A file which contains the section in the TypeDependencies.xml which
	 * 						     corrrespond to this newly added type.		
	 * @param libraryName   Name of the library to which the other input param TypeDependencyType belongs to  
	 * @return
	 * @throws Exception
	 */
	boolean addDependencyToRegistry(TypeDependencyType typeDependencyType , String libraryName) throws Exception;

	
	
	/**
	 *  This method when called would update the GlobalRegistry for the exisiting libraries in it.
	 *  
	 *  This is a kind of sync-up method.
	 * 
	 * @return
	 * @throws Exception
	 */
	boolean updateGlobalRegistry() throws Exception;
	
	
	
	/**
	 *  Given a type name returns the details of the type
	 * @param typeName
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	LibraryType getType(String typeName) throws Exception; 
	
	
	/**
	 *  Given a type's QName returns the details of the type
	 * @param typeQName
	 * @return
	 * @throws Exception
	 */
	LibraryType getType(QName typeQName) throws Exception;

	/**
	 *  Given a type's QName returns the details of the type
	 * @param typeName     name of the Type (without namespace just localpart)
	 * @param libraryName  name of the library to which the type belongs to.The namespace of the type would be derived from the given library 
	 * @return
	 * @throws Exception
	 */
	LibraryType getType(String typeName,String libraryName) throws Exception;
	
	
	/**
	 * Lists all the types currently available in the global registry.
	 * @return  A List of names of the Types recorded in the Global Registry
	 * @throws Exception
	 */
	List<LibraryType> getAllTypes() throws Exception;
	
	
	
	
	/**
	 * Given a list of library names, the method returns a map where the keys are library names and their values being their version.
	 * @param typeLibraryNames List of type library names
	 * @return a map in which the key is the Library Name and the value is the version 
	 * @throws Exception
	 */
	Map<String,String> getTypeLibrariesVersion(List<String> typeLibraryNames) throws Exception;
	
	
	
	
	/**
	 * Returns the details of all the type libraries in the GlobalRegistry
	 * 
	 * @return A list of Library details
	 * @throws Exception
	 */
	List<TypeLibraryType> getAllTypeLibraries() throws Exception;

	/**
	 * Returns the names of all the type libraries in the GlobalRegistry
	 * 
	 * @return A list of Library details
	 * @throws Exception
	 */
	Set<String> getAllTypeLibrariesNames() throws Exception;
	
	
	
	/**
	 * Returns the details of the input library .
	 * @param typeLibName Name of the type library .
	 * @return
	 * @throws Exception
	 */
	TypeLibraryType getTypeLibrary(String typeLibName) throws Exception;
	
	
	/**
	 *  Returns the names of the Libraries which the given type refers to.
	 * @param typeName  Name of the type
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	Set<String> getLibrariesReferredByType(String typeName) throws Exception;
	
	
	/**
	 *  Returns the names of the Libraries which the given type refers to.
	 * @param typeName  Name of the type
	 * @return
	 * @throws Exception
	 */
	Set<String> getLibrariesReferredByType(QName typeQName) throws Exception;

	
	
	/**
	 * Returns the names of the Libraries which the input type refers to.
	 * @param libraryType 
	 * @return
	 * @throws Exception
	 */
	Set<String> getLibrariesReferredByType(LibraryType libraryType) throws Exception;
	
	
	
	/**
	 * Given the details of a Type, the function returns a list of Types which the input type refers to.
	 * i.e given a type it returns the list of its parents.
	 * @param libraryType An instance of LibraryType with the details of the type.
	 * @return A List of the dependent types.
	 * @throws Exception
	 */
	List<LibraryType> getDependentParentTypeFiles(LibraryType libraryType) throws Exception;
	

	/**
	 * Given the details of a Type, the function returns a list of Types which the input type refers to.
	 * i.e given a type it returns the list of its parents.
	 * @param libraryType An instance of LibraryType with the details of the type.
	 * @param maxLevel  the number of levels for which you need the dependendent information 
	 * @return A List of the dependent types.
	 * @throws Exception
	 */
	List<LibraryType> getDependentParentTypeFiles(LibraryType libraryType,int maxLevel) throws Exception;
	
	
	
	/**
	 * Given the details of a Type, the function returns a list of Types which depend on the input type thru inheritance.
	 * i.e given a type it returns the list of its childs.
	 * @param libraryType An instance of LibraryType with the details of the type.
	 * @return A List of the dependent types.
	 * @throws Exception
	 */
	List<LibraryType> getDependentChildTypeFiles(LibraryType libraryType) throws Exception;
	
	/**
	 * Given the details of a Type, the function returns a list of Types which depend on the input type thru inheritance.
	 * i.e given a type it returns the list of its childs.
	 * @param libraryType An instance of LibraryType with the details of the type.
	 * @param maxLevel  the number of levels for which you need the dependendent information
	 * @return A List of the dependent types.
	 * @throws Exception
	 */
	List<LibraryType> getDependentChildTypeFiles(LibraryType libraryType,int maxLevel) throws Exception;
		
	
	
	/**
	 * Given a type name, the methods deletes the details of the same from the GlobalRegistry
     * @param typeName Type name of the type to be deleted
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	boolean removeTypeFromRegistry(String typeName) throws Exception;
	
	/**
	 * Given a type name, the methods deletes the details of the same from the GlobalRegistry
     * @param typeQName QName name of the type to be deleted
	 * @return
	 * @throws Exception
	 */
	boolean removeTypeFromRegistry(QName typeQName) throws Exception;
	
	
	/**
	 * Given the details of a type , the method deletes the details of the same from the GlobalRegistry
	 * @param libraryType An instance of org.ebayopensource.turmeric.runtime.common.config.LibraryType
	 * @return operation status.
	 * @throws Exception
	 */
	boolean removeTypeFromRegistry(LibraryType libraryType) throws Exception;
	
	
	
	/** 
	 * Given a type name the method tells whether the type exisits in the GlobalRegistry
	 * @param typeName name of the type
	 * @return A boolean value indicating the presence or absence of the Type in the Global Registry. 
	 * @throws Exception
	 */
	@Deprecated
	boolean doesTypeNameExist(String typeName) throws Exception;
	
	/** 
	 * Given a type's QName  the method tells whether the type exisits in the GlobalRegistry
	 * @param typeName name of the type
	 * @return A boolean value indicating the presence or absence of the Type in the Global Registry. 
	 * @throws Exception
	 */
	boolean doesTypeNameExist(QName typeQName) throws Exception;

	
	
	/**
	 * Given the details of a type the method tells whether the type exisits in the GlobalRegistry
	 * @param libraryType  Type details of the type
	 * @return A boolean value indicating the presence or absence of the Type in the Global Registry.
	 * @throws Exception
	 */
	boolean doesTypeNameExist(LibraryType typeInfo) throws Exception;
	
	
	/**
	 * @param libraryName Name of the Library which should be removed from the Registry.
	 * @return
	 * @throws Exception
	 */
	boolean removeLibraryFromRegistry(String libraryName) throws Exception;
	
	/**
	 * 
	 * @param libraryName Name of the Library which should be removed from the Registry.
	 * @return
	 * @throws Exception
	 */
	List<LibraryType> getTypesOfLibrary(String libraryName) throws Exception;
	
	
	
}
