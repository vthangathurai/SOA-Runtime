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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;

import org.ebayopensource.turmeric.common.config.LibraryType;
import org.ebayopensource.turmeric.common.config.ReferredType;
import org.ebayopensource.turmeric.common.config.ReferredTypeLibraryType;
import org.ebayopensource.turmeric.common.config.TypeDependencyType;
import org.ebayopensource.turmeric.common.config.TypeInformationType;
import org.ebayopensource.turmeric.common.config.TypeLibraryDependencyType;
import org.ebayopensource.turmeric.common.config.TypeLibraryType;

/**
 * @author arajmony
 * 
 */
public class TypeDependencyParser  {

	private static TypeDependencyParser typeDependencyParser;

	/*
	 *  performance related variables
	 */
	private static int typeTableInitialVectorSize = 2;

	private Map<LibraryTypeWrapper,Set<LibraryTypeWrapper>> refToTypeDepChildToParentMap;
	private Map<LibraryTypeWrapper,Set<LibraryTypeWrapper>> refToTypeDepParentToChildMap;
	private Map<QName,LibraryTypeWrapper> refToMasterTypeInfoTable;
	private Map<String,TypeLibraryType> refToGlobalLibraryMap;
	
	
	private static final String TYPE_DEPENDENCY_FILE_NAME = "TypeDependencies.xml";
	
	private static CallTrackingLogger logger = LogManager.getInstance(TypeDependencyParser.class);
	
	private static CallTrackingLogger getLogger(){
		return logger;
	}
	
	
	
	/**
	 * 
	 * @return An instance of TypeDependencyParser
	 */
	public static synchronized TypeDependencyParser getInstance() {
		if (typeDependencyParser == null) {
			typeDependencyParser = new TypeDependencyParser();
		}

		return typeDependencyParser;
	}
	
	
	
    /**
     * 
     * @param typeLibraryName
     */
	public void processTypeDepXMLFile(String typeLibraryName) throws Exception{
		
		refToMasterTypeInfoTable = SOAGlobalRegistryImpl.getInstance().getMastertypeInfoTable();
		refToTypeDepParentToChildMap = SOAGlobalRegistryImpl.getInstance().getParentToChildMap();
		refToTypeDepChildToParentMap = SOAGlobalRegistryImpl.getInstance().getChildToParentMap();
		
		
		String typeDependenciesFilePath   = TypeLibraryConstants.META_INF_FOLDER + "/" + typeLibraryName + "/" + TYPE_DEPENDENCY_FILE_NAME; 
		ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
		InputStream	inStream = null;
  	    TypeLibraryDependencyType typeLibraryDependencyType = null;
  	    //TODO : log a info message here
  	    
  	    try{
  	  	    inStream   = myClassLoader.getResourceAsStream(typeDependenciesFilePath);
  	  	    if(inStream == null) return; // A project may not have the TypeDependency.xml file and it is valid
  	    	typeLibraryDependencyType = JAXB.unmarshal(inStream,TypeLibraryDependencyType.class );
  	    }catch(Exception e){
  	    	getLogger().log(Level.SEVERE, "Unable to parse the TypeDepedencies.xml file, of library " + typeLibraryName  + " its content could be invalid", e);
  	    	throw e;
  	    }finally{
  	    	CodeGenUtil.closeQuietly(inStream);
		}
  	    
  	    processTypeLibraryDependencyType(typeLibraryDependencyType);

	}

	
	
	/*  A sample TypeDependencies.xml file for understanding the code a little better
	 *  
	 *  <typeLibraryDependencyType libraryName="HardwareLibrary" version="1.0.0" xmlns="http://www.ebayopensource.org/turmeric/common/config">
  	 *	  	<type name="hardwareutilitiesType" version="1.0.0">
  	 *			<referredTypeLibrary name="HardwareLibrary" version="1.0.0">
     *				<referredType name="hardwareType" version="1.0.0"/>
     *				<referredType name="writerdetailsType" version="1.0.0"/>
     *				<referredType name="powerdetailsType" version="1.0.0"/>
     * 				<referredType name="monitordetailsType" version="1.0.0"/>
     *			</referredTypeLibrary>
     *		</type>
	 */
	
	
	/**
	 * 
	 * @param typeLibraryDependencyType
	 */
	public void processTypeLibraryDependencyType(TypeLibraryDependencyType typeLibraryDependencyType) throws Exception{
		
		refToGlobalLibraryMap = SOAGlobalRegistryImpl.getInstance().getGlobalLibraryMap();
		List<TypeDependencyType> listTypeDependencyType = typeLibraryDependencyType.getType();
		String currLibraryName = typeLibraryDependencyType.getLibraryName();
		String nameSpaceCurrLibrary = refToGlobalLibraryMap.get(currLibraryName).getLibraryNamespace();
		
		/*
		 * Before procssing the current TypeLibraryDependencyType , try and get the details of the types of the corresponding type library.
		 * This is needed for removing the dependency from the global tables for the following scenario
 		 *
		 *  1. TypeA currently refers only to TypeB. Therefore TypeDependencies.xml has an entry for this.
		 *  2. user modifies TypeA to remove the dependency with TypeB. Now since TypeA is not dependent on any Type there would be 
		 *  	no entry for TypeA in the TypeDependencies.xml any more. i.e in such cases the input param to this method TypeLibraryDependencyType
		 *  	will not have this piece of deleted information.
		 */
		
		Map<LibraryType, List<LibraryType>> mapOfTypeAndItsParentsAsPerTheOldTypeDependencies = getTypesAndTheirDirectParents(currLibraryName);
		
		/*
		 * process each of the TypeDependencyType
		 */
		List<LibraryType> listOfProcessedLibraryTypes = new ArrayList<LibraryType>();
		Iterator<TypeDependencyType> iterator = listTypeDependencyType.iterator();
		//processing element <type  ...>
		while(iterator.hasNext()){
			TypeDependencyType currTypeDependencyType = iterator.next();
			listOfProcessedLibraryTypes.add(getLibraryTypeFromTypeDependencyType(currTypeDependencyType,nameSpaceCurrLibrary));
			
			processTypeDependencyType(currTypeDependencyType,currLibraryName);
		}
		
		
		processAnyDeletedDependencies(
				mapOfTypeAndItsParentsAsPerTheOldTypeDependencies,
				listOfProcessedLibraryTypes);
		
	}

	
	
	private Map<LibraryType, List<LibraryType>> getTypesAndTheirDirectParents(String currLibraryName) {
		refToGlobalLibraryMap = SOAGlobalRegistryImpl.getInstance().getGlobalLibraryMap();
		Map<LibraryType,List<LibraryType>> mapOfTypeAndItsParents = new HashMap<LibraryType, List<LibraryType>>();
		try{
			SOATypeRegistry typeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
			TypeLibraryType typeLibraryType = typeRegistry.getTypeLibrary(currLibraryName);
			String nameSpaceCurrLibrary = refToGlobalLibraryMap.get(currLibraryName).getLibraryNamespace();
		
			for(TypeInformationType typeInformationType : typeLibraryType.getType()){
				LibraryType libraryType = getLibraryTypeFromTypeInformationType(typeInformationType,nameSpaceCurrLibrary);
				
				List<LibraryType> listOfParents = typeRegistry.getDependentParentTypeFiles(libraryType, 1);
				if(listOfParents.size() > 0)
					mapOfTypeAndItsParents.put(libraryType, listOfParents);
			}
		}catch(Exception e){
			getLogger().log(Level.INFO, "Exception while trying to get parents for each of the types in the library : " + currLibraryName);
		}
	
		return mapOfTypeAndItsParents;
	}



	private void processAnyDeletedDependencies(Map<LibraryType, List<LibraryType>> oldMappingOfTypeAndItsParents, List<LibraryType> listOfProcessedLibraryTypes) {
		
		refToMasterTypeInfoTable = SOAGlobalRegistryImpl.getInstance().getMastertypeInfoTable();
		refToTypeDepParentToChildMap = SOAGlobalRegistryImpl.getInstance().getParentToChildMap();
		refToTypeDepChildToParentMap = SOAGlobalRegistryImpl.getInstance().getChildToParentMap();

		
		// converting LibraryTypes to LibraryTypeWrapper's since we are going to use removeAll method and onnly the wrapper class has an 
		// over-ridden equals method . LibraryType class is a generated one and hence we cannot over-ride equals method
		Set<LibraryType> setOfTypesInOldDependenciesFile = oldMappingOfTypeAndItsParents.keySet();
		Set<LibraryTypeWrapper> setOfWrapperTypesInOldDependenciesFile = new HashSet<LibraryTypeWrapper>();
		for(LibraryType libraryType: setOfTypesInOldDependenciesFile)
			setOfWrapperTypesInOldDependenciesFile.add(new LibraryTypeWrapper(libraryType));
		
		
		List<LibraryTypeWrapper> listOfProcessedLibraryTypeWrappers = new ArrayList<LibraryTypeWrapper>();
		for(LibraryType libraryType: listOfProcessedLibraryTypes)
			listOfProcessedLibraryTypeWrappers.add(new LibraryTypeWrapper(libraryType));
		
		
		setOfWrapperTypesInOldDependenciesFile.removeAll(listOfProcessedLibraryTypeWrappers);
		
		// Now setOfTypesInOldDependenciesFile contains only those Types for which dependency existed earlier but has now been removed totally from the
		//  TypeDependencies.xml . For each such type in the set 
		//  a) get the list of the old parents and for each of the parent types remove the current type as one of its childs. (refToTypeDepParentToChildMap)
		//  b) since the current type does not have any parents any more , remove the current type from refToTypeDepChildToParentMap
		
		Iterator<LibraryTypeWrapper> libraryTypeIter = setOfWrapperTypesInOldDependenciesFile.iterator();
		while(libraryTypeIter.hasNext()){
			LibraryTypeWrapper currLibraryTypeWrapper = libraryTypeIter.next(); 
			LibraryType currLibraryType 			  = currLibraryTypeWrapper.getLibraryType();
			
			try {
					for(LibraryType parentLibraryType :   oldMappingOfTypeAndItsParents.get(currLibraryType) ){
						LibraryTypeWrapper parenTypeWrapper = new LibraryTypeWrapper(parentLibraryType);
						Set<LibraryTypeWrapper> listOfChilds = refToTypeDepParentToChildMap.get(parenTypeWrapper);
						if(listOfChilds != null){
							listOfChilds.remove(currLibraryTypeWrapper);
							refToTypeDepParentToChildMap.put(parenTypeWrapper, listOfChilds);
						}
					}
					refToTypeDepChildToParentMap.remove(currLibraryTypeWrapper);
				
			} catch (Exception e) {
				getLogger().log(Level.INFO, "Exception while trying to get dependent childs for type : "+ currLibraryType.getName());
			}
		}
	}



	private LibraryType getLibraryTypeFromTypeInformationType(TypeInformationType typeInformationType, String nameSpaceCurrLibrary) {
		LibraryType libraryType = new LibraryType();
		libraryType.setName(typeInformationType.getXmlTypeName());
		libraryType.setVersion(typeInformationType.getVersion());
		libraryType.setNamespace(nameSpaceCurrLibrary);
		return libraryType;
	}


	private LibraryType getLibraryTypeFromTypeDependencyType(TypeDependencyType currTypeDependencyType, String nameSpaceCurrLibrary) {
		LibraryType libraryType = new LibraryType();
		libraryType.setName(currTypeDependencyType.getName());
		libraryType.setVersion(currTypeDependencyType.getVersion());
		libraryType.setNamespace(nameSpaceCurrLibrary);
		return libraryType;
	}



	/**
	 * 
	 * @param currTypeDependencyType
	 * @param libraryName 
	 */
	public void processTypeDependencyType(TypeDependencyType currTypeDependencyType, String masterLibraryName) throws Exception{
		
		refToMasterTypeInfoTable = SOAGlobalRegistryImpl.getInstance().getMastertypeInfoTable();
		refToTypeDepParentToChildMap = SOAGlobalRegistryImpl.getInstance().getParentToChildMap();
		refToTypeDepChildToParentMap = SOAGlobalRegistryImpl.getInstance().getChildToParentMap();
		refToGlobalLibraryMap        = SOAGlobalRegistryImpl.getInstance().getGlobalLibraryMap();  
		SOATypeRegistry typeRegistry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		
		
		String currentTypeName					      = currTypeDependencyType.getName();
		String currTypesNS 							  = refToGlobalLibraryMap.get(masterLibraryName).getLibraryNamespace(); 
		
		QName currTypesQName  =  new QName(currTypesNS,currentTypeName);
		LibraryTypeWrapper currLibraryType 		      = refToMasterTypeInfoTable.get(currTypesQName);
		
		/*		The reason for this fix is debatable (return if currLibraryType is null) , this can be null only in the folowing scenario
		 *      If a type is mentioned in a TypeDependency.xml of a library but that type's information is missing in the TypeInformation.xml of the library
		 *      , which indicates that their is a mis-match which can't exist. But plugin is throwing an error and hence fixing it
		 */
		if(currLibraryType == null) 
			return;
		
		Set<LibraryTypeWrapper> referredTypeHolder = new HashSet<LibraryTypeWrapper>(typeTableInitialVectorSize);
		List<LibraryType> listOfExistingParentTypes =  typeRegistry.getDependentParentTypeFiles(currLibraryType.getLibraryType(), 1);

		List<ReferredTypeLibraryType>  listOfReferredTypeLibrary = currTypeDependencyType.getReferredTypeLibrary();
		Iterator<ReferredTypeLibraryType> iteratorRefTLT = listOfReferredTypeLibrary.iterator();
		//processing element <referredTypeLibrary  ....>
		while(iteratorRefTLT.hasNext()){
			ReferredTypeLibraryType currReferredTypeLibraryType = iteratorRefTLT.next();
			String currReferredLibraryName  = currReferredTypeLibraryType.getName();
			String currReferredLibraryNS    = refToGlobalLibraryMap.get(currReferredLibraryName).getLibraryNamespace();
			
			List<ReferredType> listOfReferredtype  = currReferredTypeLibraryType.getReferredType();
			Iterator<ReferredType> iteratorRefType = listOfReferredtype.iterator();
			//procesing element <referredType ...> 
			while(iteratorRefType.hasNext()){
				ReferredType currReferredType = iteratorRefType.next();
				String referredTypeName    = currReferredType.getName();
				QName  referredTypesQName  = new QName(currReferredLibraryNS,referredTypeName);
					
				LibraryTypeWrapper referredType = refToMasterTypeInfoTable.get(referredTypesQName);
				if(referredType == null){
					// This can be null, if 
					//  a) The corresponding library is yet to be processed/updated  or/and
					//  b) The library is processed but there is no valid type like this 
					
					
					// Try populating the referredLibrary once more, probably its updated 
					try{
						TypeInformationParser informationParser = TypeInformationParser.getInstance();
						informationParser.processTypeInfoXMLFile(currReferredLibraryName);
					}catch(Exception e){
						String msg = "As per the TypeDependencies.xml file for the library : "+ masterLibraryName +
						" , the type : " + currentTypeName + "  refers to the type : " + referredTypesQName + " of the library :" +
						currReferredLibraryName + " . But this referred library is not in the global tables and it is not yet loaded";
						getLogger().log(Level.SEVERE,msg,e );
						throw e;
					}
					
					referredType = refToMasterTypeInfoTable.get(referredTypesQName);
					if(referredType == null){
						/* if it is still null , what to do ? ()i.e The referred library is already there in the global tables
						 *   but the corresponding TypeInformation.xml does not have the details of this yet
						 * a) Should we throw an error ?   or 
						 * b) just create a type for the same and update the information in the global TypeInfo  table 
						 *   
						 *    For the time being I am following logic (a)
						 */ 
  
						String msg = "As per the TypeDependencies.xml file for the library : "+ masterLibraryName +
						" , the type : " + currentTypeName + "  refers to the type : " + referredTypesQName + " of the library :" +
						currReferredLibraryName + " . But the TypeInformation.xml of the referred library file does not have information" +
						" about this type. pls take corrective action.";
						
						getLogger().log(Level.SEVERE, msg);
						throw new Exception(msg);
						
						/*  logic for  (b)
						refToGlobalLibraryMap = SOAGlobalRegistryImpl.getInstance().getGlobalLibraryMap();
						LibraryType libraryType = new  LibraryType();
						libraryType.setName(referredTypeName);
						libraryType.setVersion(TypeLibraryConstants.TYPE_DEFAULT_VERSION);
						libraryType.setLibraryInfo(refToGlobalLibraryMap.get(currReferredLibraryName));
						referredType = new LibraryTypeWrapper(libraryType);
						refToMasterTypeInfoTable.put(referredTypeName, referredType);
						*/
						
					}
					
				}
				
				// {  child to parent
				referredTypeHolder.add(referredType);
				refToTypeDepChildToParentMap.put(currLibraryType, referredTypeHolder);
				// }  child to parent
				
				
				// { parent to child
				Set<LibraryTypeWrapper> setLibTyp = null;
				boolean instanceExists = refToTypeDepParentToChildMap.containsKey(referredType);
				if(instanceExists){
					setLibTyp =  refToTypeDepParentToChildMap.get(referredType);
					if(setLibTyp == null) // this condition can never be true ! TODO
						setLibTyp = new HashSet<LibraryTypeWrapper>(typeTableInitialVectorSize);
					
					setLibTyp.add(currLibraryType);
				}
				else {
					setLibTyp = new HashSet<LibraryTypeWrapper>(4);
					setLibTyp.add(currLibraryType);
				}
				refToTypeDepParentToChildMap.put(referredType,setLibTyp);
				// } parent to child
				
			}
		}
		
		
		/*
		 *  The following piece of code takes care of the situation where a type dependency is removed for a type
		 *  1. TypeA currently refers to TypeB and TypeC
		 *  2. user modifies TypeA to remove the dependency with TypeC i.e TypeA no longer depends on TypeC
		 *  3. So TypeC does not have TypeA as its child an more and this has to be reflected in the global tables.
		 *  4. But since TypeA is still dependent on TypeB and therefore an entry for the same would be there in the
		 *  	TypeDependencies.xml file.
		 *  
		 *  
		 */
		if(listOfExistingParentTypes.size() != referredTypeHolder.size()){
			Vector<LibraryTypeWrapper> existingParentTypeWrappers = new Vector<LibraryTypeWrapper>(listOfExistingParentTypes.size());
			for(LibraryType libraryType : listOfExistingParentTypes)
				existingParentTypeWrappers.add(new LibraryTypeWrapper(libraryType));
			
			existingParentTypeWrappers.removeAll(referredTypeHolder);
			
			for(LibraryTypeWrapper libraryTypeWrapper : existingParentTypeWrappers){
				Set<LibraryTypeWrapper> listOfParents = refToTypeDepParentToChildMap.get(libraryTypeWrapper);
				if(listOfParents != null){
					listOfParents.remove(currLibraryType);
					refToTypeDepParentToChildMap.put(libraryTypeWrapper, listOfParents);
				}
			}
			
		}
			
		
	}


}
