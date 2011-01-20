/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.library.exception.PopulateRegistryException;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;

import org.ebayopensource.turmeric.common.config.LibraryType;
import org.ebayopensource.turmeric.common.config.TypeDependencyType;
import org.ebayopensource.turmeric.common.config.TypeInformationType;
import org.ebayopensource.turmeric.common.config.TypeLibraryDependencyType;
import org.ebayopensource.turmeric.common.config.TypeLibraryType;


/**
 * 
 * @author arajmony
 *
 */
public class SOAGlobalRegistryImpl 
	implements SOATypeRegistry,SOAErrorRegistry, SOAServiceRegistry{



	private static CallTrackingLogger s_logger = LogManager.getInstance(SOAGlobalRegistryImpl.class);
	
	private static CallTrackingLogger getLogger() {
		return s_logger;
	}
	
	private static SOAGlobalRegistryImpl soaGlobalRegistry;
	
	private Map<QName,LibraryTypeWrapper> masterTypeInfoTable =
		new HashMap<QName, LibraryTypeWrapper>(200);
	
	private Map<LibraryTypeWrapper,Set<LibraryTypeWrapper>> typeDepChildToParentMap = 
		new HashMap<LibraryTypeWrapper, Set<LibraryTypeWrapper>>(100);
	
	private Map<LibraryTypeWrapper,Set<LibraryTypeWrapper>> typeDepParentToChildMap = 
		new HashMap<LibraryTypeWrapper, Set<LibraryTypeWrapper>>(100);
	
	
	private Map<String,TypeLibraryType> globalLibraryMap =
		new HashMap<String, TypeLibraryType>();
	
	private static TypeInformationParser typeInformationParser = new TypeInformationParser();
	private static TypeDependencyParser typeDependencyParser = new TypeDependencyParser();
	
	
	private final static int PARENT_TO_CHILD = 1;
	private final static int CHILD_TO_PARENT = 2;
	
	private static final String TYPE_DEPENDENCY_FILE_NAME = "TypeDependencies.xml";
	
	public SOAGlobalRegistryImpl(){}
	
	
	public static synchronized SOAGlobalRegistryImpl getInstance(){
		if(soaGlobalRegistry == null){ //KEEPME
			soaGlobalRegistry = new SOAGlobalRegistryImpl();
		}
		return soaGlobalRegistry;
	}


	 
	public boolean addDependencyToRegistry( TypeDependencyType typeDependencyType,String libraryName) throws Exception {
		getLogger().log(Level.FINE, "method called : addDependencyToRegistry");
		getLogger().log(Level.FINE, "addDependencyToRegistry: libraryName :" + libraryName);
		
		if(typeDependencyType == null)
			throw new BadInputValueException("Input param \"typeDependencyType\" cannot be null.");
		if(libraryName == null)
			throw new BadInputValueException("Input param \"libraryName\" cannot be null.");

		
		TypeDependencyParser dependencyParser = TypeDependencyParser.getInstance();
		dependencyParser.processTypeDependencyType(typeDependencyType,libraryName);
		return true;
	}

   
	public boolean addTypeLibraryToRegistry(TypeLibraryType library) throws Exception {
		validateTypeLibraryType(library);
		
		
		String typeLibraryName = library.getLibraryName();
		globalLibraryMap.put(typeLibraryName, library);
		typeInformationParser.populateTypeInfoGlobalTable(library, typeLibraryName);
		typeDependencyParser.processTypeDepXMLFile(typeLibraryName);
		return true;
	}

	
	private void validateTypeLibraryType(TypeLibraryType typeLibraryType) throws Exception {
		getLogger().log(Level.FINE, "method called : validateTypeLibraryType");
		getLogger().log(Level.FINE, "validateTypeLibraryType: libraryName :" + typeLibraryType);

		
		if(typeLibraryType == null)
			throw new BadInputValueException("Input param \"TypeLibraryType\" cannot be null.");

		String libraryName =  typeLibraryType.getLibraryName();
		if(TypeLibraryUtilities.isEmptyString(libraryName))
			throw new BadInputValueException("Input param TypeLibraryType's library name is either null or void.");
		
		boolean doesLibraryExistInRegistry = false;
		TypeLibraryType currTypeLibraryTypeInRegistry = null;
		if(soaGlobalRegistry.getAllTypeLibrariesNames().contains(libraryName)){
			currTypeLibraryTypeInRegistry = soaGlobalRegistry.getTypeLibrary(libraryName);
			if(currTypeLibraryTypeInRegistry != null)
				doesLibraryExistInRegistry = true;
		}
		
		String libraryVersion = typeLibraryType.getVersion();
		if(TypeLibraryUtilities.isEmptyString(libraryVersion)){
			if(doesLibraryExistInRegistry)
				typeLibraryType.setVersion(currTypeLibraryTypeInRegistry.getVersion());
			else
				typeLibraryType.setVersion(TypeLibraryConstants.TYPE_LIBRARY_DEFAULT_VERSION);
		}else{
			   	boolean isValidVersion = TypeLibraryUtilities.checkVersionFormat(libraryVersion, TypeLibraryConstants.TYPE_LIBRARY_VERSION_LEVEL);
			   	if(!isValidVersion)
			   		throw new BadInputValueException("Input param TypeLibraryType's version is not in the format X.Y.Z where X,Y,Z are integers.");
		}
		
		String nameSpace = typeLibraryType.getLibraryNamespace();
		if(TypeLibraryUtilities.isEmptyString(nameSpace)){
			typeLibraryType.setLibraryNamespace(TypeLibraryConstants.TYPE_INFORMATION_NAMESPACE);
		}else {
			if(doesLibraryExistInRegistry){
				if(!nameSpace.equals(currTypeLibraryTypeInRegistry.getLibraryNamespace()))
					throw new BadInputValueException("The namespace passed in the input TypeLibraryType does not match with the name space of the instance of the same " +
							"\nlibrary currently stored in the global Registry." +
							"\n" + "Input Namespace       : " + nameSpace +
							"\n" + "Namespace in Registry : " + currTypeLibraryTypeInRegistry.getLibraryNamespace() );
			}
		}
		
		
		//validate the contents with in the TypeLibraryType i.e validating the types
		for(TypeInformationType typeInformationType : typeLibraryType.getType()){
			if(TypeLibraryUtilities.isEmptyString(typeInformationType.getJavaTypeName()))
				throw new BadInputValueException("The java type name for one of the types passed through the TypeLibraryType is null.");
			
			if(TypeLibraryUtilities.isEmptyString(typeInformationType.getXmlTypeName()))
				throw new BadInputValueException("The xml type name for one of the types passed through the TypeLibraryType is null.");

			String version = typeInformationType.getVersion();
			if(TypeLibraryUtilities.isEmptyString(version)){
				typeInformationType.setVersion(TypeLibraryConstants.TYPE_DEFAULT_VERSION);
			} else {
			   	boolean isValidVersion = TypeLibraryUtilities.checkVersionFormat(version, TypeLibraryConstants.TYPE_VERSION_LEVEL);
			   	if(!isValidVersion)
			   		throw new BadInputValueException("Input param TypeLibraryType's version is not in the format X.Y.Z where X,Y,Z are integers.");

			}
				
		}
		
		
	}


	public boolean addTypeLibraryToRegistry(String library) throws Exception {
		getLogger().log(Level.FINE, "method called : addTypeLibraryToRegistry");
		getLogger().log(Level.FINE, "addTypeLibraryToRegistry: library :" + library);

		
		if(library == null)
			throw new BadInputValueException("Input param \"library\" cannot be null.");
		
		List<String> libraryList = new ArrayList<String>(1);
		libraryList.add(library);
		populateRegistryWithTypeLibraries(libraryList);
		return true;
	}


	@Deprecated
	public boolean addTypeToRegistry(String typeName) throws Exception {
		LibraryType libraryType = new LibraryType();
		libraryType.setName(typeName);
		return addTypeToRegistry(libraryType);
	}


	public boolean addTypeToRegistry(LibraryType libraryType) throws Exception {
		getLogger().log(Level.FINE, "method called : addTypeToRegistry");
		getLogger().log(Level.FINE, "addTypeToRegistry: libraryType :" + libraryType);

		
		boolean isValidType = validateAndDefaultLibraryType(libraryType);
		if(!isValidType)
			return false;
			
		
		LibraryTypeWrapper libraryTypeWrapper = masterTypeInfoTable.get(TypeLibraryUtilities.getQNameOfLibraryType(libraryType));
		if(libraryTypeWrapper == null){
			libraryTypeWrapper = new LibraryTypeWrapper(libraryType);	
		}
		masterTypeInfoTable.put(TypeLibraryUtilities.getQNameOfLibraryType(libraryTypeWrapper.getLibraryType()),libraryTypeWrapper);
		
		//updating the contents in the global table as well
		TypeInformationType typeInformationType = getTypeInformationTypeFromLibraryType(libraryType);
		TypeLibraryType typeLibraryType = globalLibraryMap.get(libraryType.getLibraryInfo().getLibraryName());
		typeLibraryType.getType().add(typeInformationType);
		
		
		//newly added : now addTypeToRegistry will also take care of populating the dependencies for a type
		
		TypeDependencyType typeDependencyType = getTypeDependencyTypeForType(libraryType);
		if(typeDependencyType != null)
			addDependencyToRegistry(typeDependencyType, libraryType.getLibraryInfo().getLibraryName());
		
		return true;
	}

	

	private TypeDependencyType getTypeDependencyTypeForType(LibraryType libraryType) {
		s_logger.log(Level.INFO,"method called : getTypeDependencyTypeForType");
		s_logger.log(Level.FINE,"method called : getTypeDependencyTypeForType", libraryType.getName());
		
		TypeDependencyType typeDependencyType= null;
		
		String libraryName = libraryType.getLibraryInfo().getLibraryName();
		String typeName = libraryType.getName();
		String typeDependenciesFilePath   = TypeLibraryConstants.META_INF_FOLDER + "/" + libraryName + "/" + TYPE_DEPENDENCY_FILE_NAME;
		
		ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
  	    InputStream	inStream   = null;
  	    TypeLibraryDependencyType typeLibraryDependencyType = null;
  	    
  	    getLogger().log(Level.FINE, "Identifying dependency if any for the newly added type " + libraryName + "->" +typeName);

  	    try{
  	  	    inStream   = TypeLibraryUtilities.getInputStreamForAFileFromClasspath(typeDependenciesFilePath, myClassLoader);

  	  	    if(inStream == null){
  	  	    	getLogger().log(Level.INFO, "TD.xml was not found for library " + libraryName);
  	  	    	return typeDependencyType; // A project may not have the TypeDependency.xml file and it is valid
  	  	    }
  	  	    
  	  	    typeLibraryDependencyType = JAXB.unmarshal(inStream,TypeLibraryDependencyType.class );
  	    	
  	    	if(typeLibraryDependencyType.getType() != null)
	  	    	for(TypeDependencyType currTypeDependencyType : typeLibraryDependencyType.getType()){
	  	    		if(currTypeDependencyType.getName().equals(typeName)){
	  	    			typeDependencyType = currTypeDependencyType;
	  	    			getLogger().log(Level.INFO, "TD.xml entry found for the newly added type " + libraryName + "->" +typeName);
	  	    			break;
	  	    		}
	  	    	}
  	    	
  	    }catch(Exception e){
  	    	getLogger().log(Level.SEVERE, "Unable to parse the TypeDepedencies.xml file, of library " + libraryName  + " its content could be invalid", e);
  	    }finally{
  	    	CodeGenUtil.closeQuietly(inStream);
		}
		
		return typeDependencyType;
	}


	private TypeInformationType getTypeInformationTypeFromLibraryType(LibraryType libraryType) {
		TypeInformationType typeInformationType = new TypeInformationType();
		typeInformationType.setJavaTypeName(libraryType.getPackage() + "." + libraryType.getName());
		typeInformationType.setVersion(libraryType.getVersion());
		typeInformationType.setXmlTypeName(libraryType.getName());
		
		
		return typeInformationType;
	}


	private boolean validateAndDefaultLibraryType(LibraryType libraryType) throws Exception{
		getLogger().log(Level.FINE, "method called : validateAndDefaultLibraryType");
		getLogger().log(Level.FINE, "validateAndDefaultLibraryType: libraryType :" + libraryType);

		
		if(libraryType == null)
			throw new BadInputValueException("Input param \"libraryType\" cannot be null.");
		
		if(TypeLibraryUtilities.isEmptyString(libraryType.getName()))
			throw new BadInputValueException("Input params \"libraryType\" cannot have a null value for name.");


		if(libraryType.getLibraryInfo() == null)
			throw new BadInputValueException("Input params \"libraryType\" cannot have a null value for the LibraryInfo.");

		String librayName =  libraryType.getLibraryInfo().getLibraryName();
		if(TypeLibraryUtilities.isEmptyString(librayName))
			throw new BadInputValueException("Input params \"libraryType\" cannot have a null value for the Library Name.");
		
		
		if(TypeLibraryUtilities.isEmptyString(libraryType.getVersion()))
			libraryType.setVersion(TypeLibraryConstants.TYPE_DEFAULT_VERSION);
		
		TypeLibraryUtilities.checkVersionFormat(libraryType.getVersion(), TypeLibraryConstants.TYPE_VERSION_LEVEL);
		
		
		String libNameSpace = "";
		TypeLibraryType typeLibraryType = soaGlobalRegistry.getTypeLibrary(librayName);
		if(typeLibraryType != null){
			libNameSpace = typeLibraryType.getLibraryNamespace();
			if(libNameSpace == null)
				libNameSpace = "";
		}

		if(TypeLibraryUtilities.isEmptyString(libraryType.getNamespace())){
			libraryType.setNamespace(libNameSpace);
		}
		
		if(!libraryType.getNamespace().equals(libNameSpace)){
			throw new BadInputValueException("The namespace of the type and the library to which it belongs to do not match."
					+ "\n" + "Types namespace     : " + libraryType.getNamespace()
					+ "\n" + "Libraries namespace : " + libNameSpace);
		}
			
		
		if(TypeLibraryUtilities.isEmptyString(libraryType.getPackage()))
			libraryType.setPackage(TypeLibraryUtilities.getPackageFromNamespace(libraryType.getNamespace()));
		
		
		return true;
	}


	@Deprecated
	public boolean doesTypeNameExist(String typeName) throws Exception {
		if(TypeLibraryUtilities.isEmptyString(typeName))
			throw new BadInputValueException("Input param \"typeName\" cannot be null.");
		
		LibraryType libraryType = getType(typeName); 
			
		if(libraryType == null){
			libraryType =  new LibraryType();
			libraryType.setName(typeName);
			libraryType.setNamespace(TypeLibraryConstants.TURMERIC_NAME_SPACE); //default NS for backward compatibility, added after method was deprecated
		}
		return doesTypeNameExist(libraryType);
	}

	
	public boolean doesTypeNameExist(QName typeQName) throws Exception {
		getLogger().log(Level.FINE, "method called : doesTypeNameExist");
		getLogger().log(Level.FINE, "doesTypeNameExist: typeQName :" + typeQName);

		
		if(typeQName == null)
			throw new BadInputValueException("Input param \"typeQName\" cannot be null.");
		
		LibraryType libraryType = new LibraryType();
		libraryType.setName(typeQName.getLocalPart());
		libraryType.setNamespace(typeQName.getNamespaceURI()); 
		return doesTypeNameExist(libraryType);
	}


	public boolean doesTypeNameExist(LibraryType libraryType) throws Exception {
		getLogger().log(Level.FINE, "method called : doesTypeNameExist");
		getLogger().log(Level.FINE, "doesTypeNameExist: libraryType :" + libraryType);
		
		defaultNameSpaceForLibraryType(libraryType);
		validateLibraryTypeForNullAndName(libraryType);
		
		QName typeQName = new QName(libraryType.getNamespace(),libraryType.getName());
		return masterTypeInfoTable.containsKey(typeQName); 
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.tools.library.SOATypeRegistry#getType(java.lang.String)
	 * the method is deprecated, if called would return the first matching type in the global registry with this name
	 */
	@Deprecated 
	public LibraryType getType(String typeName) throws Exception {
		getLogger().log(Level.FINE, "method called : getType");
		getLogger().log(Level.FINE, "getType: typeName :" + typeName);

		if(TypeLibraryUtilities.isEmptyString(typeName))
			throw new BadInputValueException("Input param \"typeName\" cannot be null.");
		
		LibraryType result= null;
		
		Set<QName> allTypesInRegistry =   masterTypeInfoTable.keySet();
		Iterator<QName> allTypesIterator = allTypesInRegistry.iterator();
		while(allTypesIterator.hasNext()){
			QName currTypeQName = allTypesIterator.next();
			if(currTypeQName.getLocalPart().equals(typeName)){
				LibraryTypeWrapper libraryTypeWrapper = masterTypeInfoTable.get(currTypeQName);
				if(libraryTypeWrapper != null)
					result = libraryTypeWrapper.getLibraryType();
				break;
			}
		}
 		
		return result;
	}
	
	
	public LibraryType getType(String typeName, String libraryName) throws Exception {
		getLogger().log(Level.FINE, "method called : getType");
		getLogger().log(Level.FINE, "getType: typeName :" + typeName + "  :  libraryName " + libraryName);

		TypeLibraryType typeLibraryType = globalLibraryMap.get(libraryName);
		String currTypesNS = (typeLibraryType != null)?typeLibraryType.getLibraryNamespace():null;
		QName currTypesQName = new QName(currTypesNS,typeName);
		return getType(currTypesQName);
	}

	
	public LibraryType getType(QName typeQName) throws Exception {
		getLogger().log(Level.FINE, "method called : getType");
		getLogger().log(Level.FINE, "getType: typeQName :" + typeQName);
		
		LibraryTypeWrapper wrapper = masterTypeInfoTable.get(typeQName);
		if(wrapper == null)
			return null;
		
		return wrapper.getLibraryType();
	}


	public List<LibraryType> getAllTypes() throws Exception {
		getLogger().log(Level.FINE, "method called : getAllTypes");
		getLogger().log(Level.FINE, "getAllTypes:");

		int numberOfTypes = masterTypeInfoTable.size();
		List<LibraryType> listLibType = new ArrayList<LibraryType>(numberOfTypes);
		
		Collection<LibraryTypeWrapper> collection  = masterTypeInfoTable.values();
		Iterator<LibraryTypeWrapper> iterator    = collection.iterator();
		
		while(iterator.hasNext()){
			LibraryType libraryType = iterator.next().getLibraryType();
			listLibType.add(libraryType);
		}
		
		return listLibType;
	}

	
	public List<LibraryType> getDependentChildTypeFiles(LibraryType libraryType) throws Exception {
		getLogger().log(Level.FINE, "method called : getDependentChildTypeFiles");
		getLogger().log(Level.FINE, "getDependentChildTypeFiles: libraryType :" , new Object[]{libraryType});

		return getDependentChildTypeFiles(libraryType, Integer.MAX_VALUE);
	}

	
	public List<LibraryType> getDependentChildTypeFiles(LibraryType libraryType, int maxLevel) throws Exception {
		getLogger().log(Level.FINE, "method called : getDependentChildTypeFiles");
		getLogger().log(Level.FINE, "getDependentChildTypeFiles:", new Object[]{libraryType,maxLevel});

		defaultNameSpaceForLibraryType(libraryType);
		validateLibraryTypeForNullAndName(libraryType);
		
		LibraryTypeWrapper masterType = new LibraryTypeWrapper(libraryType);
		HashSet<LibraryTypeWrapper> dependentTypesSet = new HashSet<LibraryTypeWrapper>(10);

		findDependentTypesRecursively(masterType,dependentTypesSet,PARENT_TO_CHILD,maxLevel,0);
		dependentTypesSet.remove(masterType);

		return getLibraryTypeFromLibraryWrapper(dependentTypesSet);
	}
	
	

	private void validateLibraryTypeForNullAndName(LibraryType libraryType) throws BadInputValueException{
		getLogger().log(Level.FINE, "method called : validateLibraryTypeForNullAndName");
		getLogger().log(Level.FINE, "validateLibraryTypeForNullAndName:", new Object[]{libraryType});
		
		if(libraryType == null)
			throw new BadInputValueException("Input param \"LibraryType\" cannot be null.");
		if(TypeLibraryUtilities.isEmptyString(libraryType.getName()))
			throw new BadInputValueException("Input LibraryType's name is either null or empty.");
		if(TypeLibraryUtilities.isEmptyString(libraryType.getNamespace()))
			throw new BadInputValueException("Input LibraryType's NameSpace is either null or empty.");

		
	}


	public List<LibraryType> getDependentParentTypeFiles(LibraryType libraryType) throws Exception {
		getLogger().log(Level.FINE, "method called : validateLibraryTypeForNullAndName");
		getLogger().log(Level.FINE, "validateLibraryTypeForNullAndName:", new Object[]{libraryType});
		
		return getDependentParentTypeFiles(libraryType,Integer.MAX_VALUE);
	}

	
	public List<LibraryType> getDependentParentTypeFiles(LibraryType libraryType, int maxLevel) throws Exception {
		getLogger().log(Level.FINE, "method called : getDependentParentTypeFiles");
		getLogger().log(Level.FINE, "getDependentParentTypeFiles:", new Object[]{libraryType});
		
		defaultNameSpaceForLibraryType(libraryType);
		validateLibraryTypeForNullAndName(libraryType);
		
		LibraryTypeWrapper masterType = new LibraryTypeWrapper(libraryType);
		HashSet<LibraryTypeWrapper> dependentTypesSet = new HashSet<LibraryTypeWrapper>(10);

		findDependentTypesRecursively(masterType,dependentTypesSet,CHILD_TO_PARENT,maxLevel,0);
		dependentTypesSet.remove(masterType);

		return getLibraryTypeFromLibraryWrapper(dependentTypesSet);
	}

	
   
	/**
	 * Given a type it returns the list of dependent types
	 * @param masterType
	 * @param dependentTypesSet
	 * @param dependencyDirection
	 * @param maxLevel the number of levels for which you need the dependendent information , the initial type passed is at level zero
	 * @param currLevel the current level above or below which the dependency information is needed
	 * @throws Exception
	 */
	private void findDependentTypesRecursively(LibraryTypeWrapper masterType, HashSet<LibraryTypeWrapper> dependentTypesSet,
			int dependencyDirection, int maxLevel,int currLevel)
	throws Exception{
       		if(dependentTypesSet.contains(masterType))
       			return;
       		
       		if(currLevel > maxLevel)
       			return;
       		
       		dependentTypesSet.add(masterType);
       		
       		Set<LibraryTypeWrapper> dependentTypes = null;
       		if(dependencyDirection == CHILD_TO_PARENT)
       			dependentTypes =  typeDepChildToParentMap.get(masterType);
       		else if(dependencyDirection == PARENT_TO_CHILD)
       			dependentTypes =  typeDepParentToChildMap.get(masterType);
       		
		    if(dependentTypes != null){
		    	for(LibraryTypeWrapper currType:dependentTypes)
		    		findDependentTypesRecursively(currType,dependentTypesSet,dependencyDirection,maxLevel,currLevel+1);
		    }
	}


	private List<LibraryType> getLibraryTypeFromLibraryWrapper(HashSet<LibraryTypeWrapper> dependentTypesSet) {
		List<LibraryType> listType = new ArrayList<LibraryType>(dependentTypesSet.size());

		Iterator<LibraryTypeWrapper> iterator = dependentTypesSet.iterator();
		while(iterator.hasNext()){
			LibraryType libraryType = iterator.next().getLibraryType();
			listType.add(libraryType);
		}
		return listType;
	}




	public Map<String, String> getTypeLibrariesVersion(List<String> typeLibraryNames) throws Exception {
		getLogger().log(Level.FINE, "method called : getTypeLibrariesVersion");
		getLogger().log(Level.FINE, "getTypeLibrariesVersion:", new Object[]{typeLibraryNames});

		if(typeLibraryNames == null)
			throw new BadInputValueException("Input param \"typeLibraryNames\" cannot be null.");
		
		Map<String,String> libraryVersionMap = new HashMap<String, String>(typeLibraryNames.size());
		for(int i= 0; i < typeLibraryNames.size() ; i++ ){
			String libraryName = typeLibraryNames.get(i);
			TypeLibraryType typeLibraryType = globalLibraryMap.get(libraryName);
			if(typeLibraryType != null){
				String version = typeLibraryType.getVersion();
				libraryVersionMap.put(libraryName,version);
			}
		}
		
		return libraryVersionMap;
	}

	
	
	
	public List<RegistryUpdateDetails> populateRegistryWithTypeLibrariesDetailed(
			List<String> typeLibraryNames) throws Exception {
		getLogger().log(Level.FINE, "method called : populateRegistryWithTypeLibrariesDetailed");
		getLogger().log(Level.FINE, "populateRegistryWithTypeLibrariesDetailed:", new Object[]{typeLibraryNames});

		if(typeLibraryNames == null)
			throw new BadInputValueException("Input param \"typeLibraryNames\" cannot be null.");
		
		TypeInformationParser typeInformationParser = TypeInformationParser.getInstance();
		TypeDependencyParser typeDependencyParser = TypeDependencyParser.getInstance();
		
		List<RegistryUpdateDetails> listOfRegistryUpdateDetails = new ArrayList<RegistryUpdateDetails>();
	
		
		List<String> typeDependenciesToBeProcessed = new ArrayList<String>(typeLibraryNames);
		
		/*
		 * First process all the TypeInfo files and then the TypeDependency files
		 */
		String currLibraryName= null;
		for (int i = 0; i < typeLibraryNames.size(); i++) {
			try{
				currLibraryName = typeLibraryNames.get(i);

				TypeLibraryType oldTypeLibraryType = globalLibraryMap.get(currLibraryName);
				typeInformationParser.processTypeInfoXMLFile(currLibraryName);
				TypeLibraryType newTypeLibraryType = globalLibraryMap.get(currLibraryName);
			
				parseOldAndNewTypeLibraryType(oldTypeLibraryType,newTypeLibraryType);
				}catch(Exception e){ 
					RegistryUpdateDetails registryUpdateDetails = new RegistryUpdateDetails();
					registryUpdateDetails.setIsUpdateSucess(false);
					registryUpdateDetails.setLibraryName(currLibraryName);
					registryUpdateDetails.setMessage("TypeInformation.xml file of library "+ currLibraryName + " has issues. The exception is :" + e.getMessage());
					listOfRegistryUpdateDetails.add(registryUpdateDetails); 
					
					//For this library do not prcess the TD.xml for the same library even if it exists
					typeDependenciesToBeProcessed.remove(currLibraryName);
				}
			
		}
		
		for (int i = 0; i < typeDependenciesToBeProcessed.size(); i++) {
			try{
				typeDependencyParser.processTypeDepXMLFile(typeDependenciesToBeProcessed.get(i));
				}catch(Exception e){
					
					//addExceptionMessageForTypeDependencyIssue(listOfRegistryUpdateDetails,currLibraryName,e);
					RegistryUpdateDetails registryUpdateDetails = new RegistryUpdateDetails();
					registryUpdateDetails.setIsUpdateSucess(false);
					registryUpdateDetails.setLibraryName(currLibraryName);
					registryUpdateDetails.setMessage("TypeDependencies.xml file of library "+ currLibraryName + " has issues. The exception is :" + e.getMessage());
					listOfRegistryUpdateDetails.add(registryUpdateDetails);
				}
		}
		
		return listOfRegistryUpdateDetails;
	}

	

	private void addExceptionMessageForTypeDependencyIssue(
			List<RegistryUpdateDetails> listOfRegistryUpdateDetails, String libName,Exception e) {
		
		boolean registryUpdateDetailsExist = false;
		RegistryUpdateDetails reference = null;
		
		for(RegistryUpdateDetails curr: listOfRegistryUpdateDetails){
			if(curr.getLibraryName().equals(libName)){
				registryUpdateDetailsExist = true;
				reference = curr;
				break;
			}
		}
		
		if(!registryUpdateDetailsExist)
			reference = new RegistryUpdateDetails();
		
		reference.setLibraryName(libName);
		reference.setIsUpdateSucess(false);
		reference.setMessage(reference.getMessage() + "\n" + "TypeDependencies.xml file of library "+ libName + " has issues. The exception is :" + e.getMessage());
		
	}


	public boolean populateRegistryWithTypeLibraries(List<String> typeLibraryNames) throws Exception {
		getLogger().log(Level.FINE, "method called : populateRegistryWithTypeLibrariesDetailed");
		getLogger().log(Level.FINE, "populateRegistryWithTypeLibrariesDetailed:", new Object[]{typeLibraryNames});

		
		List<RegistryUpdateDetails> registryUpdateDetails = populateRegistryWithTypeLibrariesDetailed(typeLibraryNames);
		
		if(registryUpdateDetails.size() > 0){
			StringBuffer errMsg = new StringBuffer();
			for(RegistryUpdateDetails currRegistryUpdateDetails : registryUpdateDetails)
				errMsg.append(currRegistryUpdateDetails.getMessage()).append("\n");
			
			throw new PopulateRegistryException(errMsg.toString());
		}
		else
			return true;
		
		
	}



	/*
	 * This method is to check whether any types have been deleted thru Eclipse directly , since for those plugin won't be able
	 * to call removeFromRegistry
	 */
	private void parseOldAndNewTypeLibraryType(TypeLibraryType oldTypeLibraryType, TypeLibraryType newTypeLibraryType) {
		getLogger().log(Level.FINE, "method called : parseOldAndNewTypeLibraryType");
		getLogger().log(Level.FINE, "parseOldAndNewTypeLibraryType:", new Object[]{oldTypeLibraryType,newTypeLibraryType});

		if(oldTypeLibraryType == newTypeLibraryType || oldTypeLibraryType == null || newTypeLibraryType == null)
			return;
		
		
		Set<String> typesInOldLibraryType = new HashSet<String>();
		for(TypeInformationType typeInformationType : oldTypeLibraryType.getType()){
			typesInOldLibraryType.add(typeInformationType.getXmlTypeName());
		}
		
		Set<String> typesInNewLibraryType = new HashSet<String>();
		for(TypeInformationType typeInformationType : newTypeLibraryType.getType()){
			typesInNewLibraryType.add(typeInformationType.getXmlTypeName());
		}

		
		typesInOldLibraryType.removeAll(typesInNewLibraryType);
		
		Iterator<String> deletedTypesIter = typesInOldLibraryType.iterator();
		while(deletedTypesIter.hasNext()){
			String typeName = deletedTypesIter.next();
			LibraryType libraryType = new LibraryType();
			libraryType.setName(typeName);
			libraryType.setLibraryInfo(oldTypeLibraryType);
			libraryType.setNamespace(oldTypeLibraryType.getLibraryNamespace());
			
			try {
				removeTypeFromRegistry(libraryType);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Exception while trying to delete type " + typeName);
			}
			
		}
		
		
		
	}


	@Deprecated
	public boolean removeTypeFromRegistry(String typeName) throws Exception {
		getLogger().log(Level.FINE, "method called : removeTypeFromRegistry");
		getLogger().log(Level.FINE, "removeTypeFromRegistry:", new Object[]{typeName});
		
		if(TypeLibraryUtilities.isEmptyString(typeName))
			throw new BadInputValueException("Input param \"typeName\" cannot be null.");
		
		LibraryType libraryType = getType(typeName);
		
		if(libraryType == null){
			libraryType = new LibraryType();
			libraryType.setName(typeName);
		}
		
		return removeTypeFromRegistry(libraryType);
	}
	
	

	public boolean removeTypeFromRegistry(QName typeQName) throws Exception {
		getLogger().log(Level.FINE, "method called : removeTypeFromRegistry");
		getLogger().log(Level.FINE, "removeTypeFromRegistry:", new Object[]{typeQName});

		if(typeQName == null)
			throw new BadInputValueException("Input param \"typeQName\" cannot be null.");
		
		LibraryType libraryType = new LibraryType();
		libraryType.setName(typeQName.getLocalPart());
		libraryType.setNamespace(typeQName.getNamespaceURI());
		return removeTypeFromRegistry(libraryType);
	}



	

	public boolean removeTypeFromRegistry(LibraryType libraryType) throws Exception {
		getLogger().log(Level.FINE, "method called : removeTypeFromRegistry");
		getLogger().log(Level.FINE, "removeTypeFromRegistry:", new Object[]{libraryType});

		defaultNameSpaceForLibraryType(libraryType);
		validateLibraryTypeForNullAndName(libraryType);		
		
		LibraryTypeWrapper masterLibraryTypeWrapper = masterTypeInfoTable.get(TypeLibraryUtilities.getQNameOfLibraryType(libraryType));
		if(masterLibraryTypeWrapper == null)
			return false;
		
		QName typeQName = new QName(libraryType.getNamespace(),libraryType.getName());
		masterTypeInfoTable.remove(typeQName);
		
		
		/***************  new logic */
		
		if(typeDepParentToChildMap.containsKey(masterLibraryTypeWrapper) &&
				(typeDepParentToChildMap.get(masterLibraryTypeWrapper).size() > 0) ){
			// this should never be true, this can be true only if the caller is trying to delete a type which is being referred from other type(s). 
			// i.e. this type should not be a parent for any type(s)
			// it is possible that the map will have the type to be deleted as a key, but may not have any type in the value corresponding to the key, this is
			// the reason for the second condition in the above if stmt
			
			Set<LibraryTypeWrapper> referringTypes = typeDepParentToChildMap.get(masterLibraryTypeWrapper);
			StringBuffer msg = new StringBuffer();
			msg.append("The type "+ masterLibraryTypeWrapper.getLibraryType().getName() + " cannot be deleted because the following types refer to it : ");
			
			Iterator<LibraryTypeWrapper> iterator = referringTypes.iterator();
			while(iterator.hasNext()){
				msg.append(iterator.next().getLibraryType().getName()).append(" , ");
			}
			
			s_logger.log(Level.SEVERE,msg.toString());
			throw new Exception(msg.toString());
		}
		
		
		if(typeDepChildToParentMap.containsKey(masterLibraryTypeWrapper)){
			Set<LibraryTypeWrapper> referredTypes = typeDepChildToParentMap.get(masterLibraryTypeWrapper);
			Iterator<LibraryTypeWrapper> iterator = referredTypes.iterator();
			while(iterator.hasNext()){
				LibraryTypeWrapper currReferredType = iterator.next();
				if(typeDepParentToChildMap.containsKey(currReferredType)){
					typeDepParentToChildMap.get(currReferredType).remove(masterLibraryTypeWrapper);
				}
				
			}
			
			typeDepChildToParentMap.remove(masterLibraryTypeWrapper);
		}
		
		
		
		/*
		 * old logic 
		 */
		/*
		if(typeDepChildToParentMap.containsKey(masterLibraryTypeWrapper)){
			HashSet<LibraryTypeWrapper> dependentTypesSet = new HashSet<LibraryTypeWrapper>(3);
			findDependentTypesRecursively(masterLibraryTypeWrapper,dependentTypesSet,CHILD_TO_PARENT,1,0);
			
			Iterator<LibraryTypeWrapper> iterator = dependentTypesSet.iterator();
			while(iterator.hasNext()){
				Set<LibraryTypeWrapper> set   = typeDepChildToParentMap.get(iterator.next());
				if(set != null)
					set.remove(masterLibraryTypeWrapper);
			}

			typeDepChildToParentMap.remove(masterLibraryTypeWrapper);
		}
		
		if(typeDepParentToChildMap.containsKey(masterLibraryTypeWrapper)){
			HashSet<LibraryTypeWrapper> dependentTypesSet = new HashSet<LibraryTypeWrapper>(3);
			findDependentTypesRecursively(masterLibraryTypeWrapper,dependentTypesSet,PARENT_TO_CHILD,1,0);
			
			Iterator<LibraryTypeWrapper> iterator = dependentTypesSet.iterator();
			while(iterator.hasNext()){
				Set<LibraryTypeWrapper> setWrapper   = typeDepParentToChildMap.get(iterator.next());
				if(setWrapper != null)
					setWrapper.remove(masterLibraryTypeWrapper);
			}

			typeDepParentToChildMap.remove(masterLibraryTypeWrapper);
		}
		
		*/
		
		
		return true;
	}


	
	public List<TypeLibraryType> getAllTypeLibraries() throws Exception {
		getLogger().log(Level.FINE, "method called : getAllTypeLibraries");
		getLogger().log(Level.FINE, "getAllTypeLibraries:");

		Collection<TypeLibraryType> collection =globalLibraryMap.values();
		List<TypeLibraryType> list = new ArrayList<TypeLibraryType>(collection);
		return list;
	}
	

	
	public Set<String> getAllTypeLibrariesNames() throws Exception {
		getLogger().log(Level.FINE, "method called : getAllTypeLibrariesNames");
		getLogger().log(Level.FINE, "getAllTypeLibrariesNames:");

     	return globalLibraryMap.keySet();
	}
	
	
	

	public TypeLibraryType getTypeLibrary(String typeLibName) throws Exception {
		getLogger().log(Level.FINE, "method called : getTypeLibrary");
		getLogger().log(Level.FINE, "getTypeLibrary:", new Object[]{typeLibName});

		if(TypeLibraryUtilities.isEmptyString(typeLibName))
			throw new BadInputValueException("Input param \"typeLibName\" cannot be null.");
		
		return globalLibraryMap.get(typeLibName);
	}
	
	
	@Deprecated
	public Set<String> getLibrariesReferredByType(String typeName) throws Exception {
		getLogger().log(Level.FINE, "method called : getLibrariesReferredByType");
		getLogger().log(Level.FINE, "getLibrariesReferredByType:", new Object[]{typeName});

		if(TypeLibraryUtilities.isEmptyString(typeName))
			throw new BadInputValueException("Input param \"typeName\" cannot be null.");
		
		LibraryType libraryType = getType(typeName); 
		
		if(libraryType == null){
			libraryType = new LibraryType();
			libraryType.setName(typeName);
		}
		
		return getLibrariesReferredByType(libraryType);
	}

	
	
	public Set<String> getLibrariesReferredByType(QName typeQName) throws Exception {
		getLogger().log(Level.FINE, "method called : getLibrariesReferredByType");
		getLogger().log(Level.FINE, "getLibrariesReferredByType:", new Object[]{typeQName});

		if(typeQName == null)
			throw new BadInputValueException("Input param \"typeQName\" cannot be null.");
		
		LibraryType libraryType = new LibraryType();
		libraryType.setName(typeQName.getLocalPart());
		libraryType.setNamespace(typeQName.getNamespaceURI());
		return getLibrariesReferredByType(libraryType);

	}

	
	
	
	public Set<String> getLibrariesReferredByType(LibraryType libraryType) throws Exception {
		getLogger().log(Level.FINE, "method called : getLibrariesReferredByType");
		getLogger().log(Level.FINE, "getLibrariesReferredByType:", new Object[]{libraryType});

		defaultNameSpaceForLibraryType(libraryType);
		validateLibraryTypeForNullAndName(libraryType);		
		
		Set<String> referredLibrariesSet = new HashSet<String>();
		if(libraryType == null)
			return referredLibrariesSet;

		LibraryTypeWrapper wrapper =masterTypeInfoTable.get(TypeLibraryUtilities.getQNameOfLibraryType(libraryType));
		if(wrapper == null)
			return referredLibrariesSet;
		
		String parentLibraryName = wrapper.getLibraryType().getLibraryInfo().getLibraryName();
		referredLibrariesSet.add(parentLibraryName);
		
		List<LibraryType> list   = getDependentParentTypeFiles(libraryType);
		Iterator<LibraryType> iterator = list.iterator();
		while(iterator.hasNext()){
			LibraryType type = iterator.next();
			String libraryName = type.getLibraryInfo().getLibraryName();
			referredLibrariesSet.add(libraryName);
		}
		
		return referredLibrariesSet;
	}

	
	public List<LibraryType> getTypesOfLibrary(String libraryName) throws Exception {
		getLogger().log(Level.FINE, "method called : getTypesOfLibrary");
		getLogger().log(Level.FINE, "getTypesOfLibrary:", new Object[]{libraryName});

		List<LibraryType> result = new ArrayList<LibraryType>();
		
		if(TypeLibraryUtilities.isEmptyString(libraryName))
			throw new BadInputValueException("Input param Libary Name is empty or null");
		
		for(LibraryType currLibraryType : getAllTypes()){
			TypeLibraryType typeLibraryType = currLibraryType.getLibraryInfo();
			if(typeLibraryType == null)
				continue;

			if(libraryName.equals(typeLibraryType.getLibraryName()))
				result.add(currLibraryType);
		}
		
		return result;
	}


	public boolean removeLibraryFromRegistry(String libraryName) throws Exception {
		getLogger().log(Level.FINE, "method called : removeLibraryFromRegistry");
		getLogger().log(Level.FINE, "removeLibraryFromRegistry:", new Object[]{libraryName});

		List<LibraryType> listOfTypesToDelete = getTypesOfLibrary(libraryName);
		
		for(LibraryType currLibraryType : listOfTypesToDelete){
			removeTypeFromRegistry(currLibraryType);
		}
		
		globalLibraryMap.remove(libraryName);
		
		return true;
	}

	
	
	/*
	 * This method has been added for support of multiple namespae for backward compatibility.
	 * Gets the name space for the type from thr library within it.
	 * if not available, then it sets to the default turmeric name space
	 */
	private void defaultNameSpaceForLibraryType(LibraryType libraryType) {
		getLogger().log(Level.FINE, "method called : defaultNameSpaceForLibraryType");
		getLogger().log(Level.FINE, "defaultNameSpaceForLibraryType:", new Object[]{libraryType});

		if(libraryType == null)  return;
		
		TypeLibraryType typeLibraryType = libraryType.getLibraryInfo();
		if(typeLibraryType != null)
			libraryType.setNamespace(typeLibraryType.getLibraryNamespace());
		
		if(TypeLibraryUtilities.isEmptyString(libraryType.getNamespace()))
			libraryType.setNamespace(TypeLibraryConstants.TURMERIC_NAME_SPACE);
		
	}


	public boolean updateGlobalRegistry() throws Exception {
		getLogger().log(Level.FINE, "method called : updateGlobalRegistry");
		getLogger().log(Level.FINE, "updateGlobalRegistry:", new Object[]{});

		Set<String> libraryNamesSet =   globalLibraryMap.keySet();
		List<String> libraryNamesList = new ArrayList<String>(libraryNamesSet.size());
		
		Iterator<String> iterator = libraryNamesSet.iterator();
		while(iterator.hasNext()){
			libraryNamesList.add(iterator.next());
		}
		
		populateRegistryWithTypeLibraries(libraryNamesList);
		return true;
	}

	
	
	/*
	 *  Methods to get reference to the global tables 
	 *  
	 */
	
	public Map<String, TypeLibraryType> getGlobalLibraryMap() {
		return globalLibraryMap;
	}
	
	public  Map<QName, LibraryTypeWrapper> getMastertypeInfoTable() {

		return masterTypeInfoTable;
	}
	

	public Map<LibraryTypeWrapper, Set<LibraryTypeWrapper>> getParentToChildMap() {
		return typeDepParentToChildMap;
	}


	public Map<LibraryTypeWrapper, Set<LibraryTypeWrapper>> getChildToParentMap() {
		return typeDepChildToParentMap;
	}













	
}
