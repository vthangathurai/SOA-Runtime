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

import static org.ebayopensource.turmeric.tools.library.TypeLibraryConstants.*;


import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.ebayopensource.turmeric.tools.codegen.util.ContextClassLoaderUtil;
import org.ebayopensource.turmeric.tools.library.codegen.TypeLibraryCodeGenContext;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;

import org.ebayopensource.turmeric.common.config.LibraryType;
import org.ebayopensource.turmeric.common.config.TypeInformationType;
import org.ebayopensource.turmeric.common.config.TypeLibraryType;


/**
 * @author arajmony
 * 
 */
public class TypeInformationParser  {

	private static TypeInformationParser typeInformationParser;
	private  volatile TypeLibraryType m_currentTypeLibraryType;
	
	//TODO bad design : change this to either static or use local references in each of the methods where they are used. The problem is that otherwise in every method
	// where these references are used they should be updated
	private Map<QName, LibraryTypeWrapper> refOfMasterTypeInfoTable ;
	private Map<String,TypeLibraryType> refOfGlobalLibraryMap;
	
	private static CallTrackingLogger logger = LogManager.getInstance(TypeInformationParser.class);
	
	private static CallTrackingLogger getLogger(){
		return logger;
	}
	

	public static synchronized TypeInformationParser getInstance() {
		if (typeInformationParser == null) {
			typeInformationParser = new TypeInformationParser();
		}

		return typeInformationParser;
	}


	/**
	 * This method is called when flow comes thru global registry
	 * @param typeLibraryName
	 * @throws Exception
	 */
	public void processTypeInfoXMLFile( String typeLibraryName) throws Exception {
		String typeInfoXMLFilePath   = TypeLibraryConstants.META_INF_FOLDER + "/" + typeLibraryName + "/"  + TYPE_INFORMATION_FILE_NAME;
		
		refOfMasterTypeInfoTable = SOAGlobalRegistryImpl.getInstance().getMastertypeInfoTable();
		refOfGlobalLibraryMap    = SOAGlobalRegistryImpl.getInstance().getGlobalLibraryMap();
		

		InputStream	inStream = null;
        
		try{
	  	    inStream   = ContextClassLoaderUtil.getResourceAsStream(typeInfoXMLFilePath);
	  	    if(inStream == null){
	  	    	throw new Exception("Could not find the TypeInformation.xml file for library " + typeLibraryName + "  at location : "+typeInfoXMLFilePath); 
	  	    }
			//after successfully validating the file, unmarshall the TypeInfo content and store the same in global tables for future reference
			//TODO validating the file
			TypeLibraryType typeLibraryType = JAXB.unmarshal(inStream, TypeLibraryType.class);
			m_currentTypeLibraryType = typeLibraryType;
			populateTypeInfoGlobalTable(typeLibraryType,typeLibraryName);
		
		}catch (Throwable t) {
			getLogger().log(Level.SEVERE, "Unable to parse the TypeInformation.xml file, of library " + typeLibraryName  + " its content could be invalid", t);
			throw new Exception(t);
		}
		finally{
			CodeGenUtil.closeQuietly(inStream);
		}
	}
	
	/**
	 * 
	 * @param typeLibraryType
	 */
	public void populateTypeInfoGlobalTable(TypeLibraryType typeLibraryType,String libraryName) throws Exception{
		
		m_currentTypeLibraryType = typeLibraryType;
		refOfMasterTypeInfoTable = SOAGlobalRegistryImpl.getInstance().getMastertypeInfoTable();
		refOfGlobalLibraryMap = SOAGlobalRegistryImpl.getInstance().getGlobalLibraryMap();
		String currLibraryNS = typeLibraryType.getLibraryNamespace();
		
		if(TypeLibraryUtilities.isEmptyString(currLibraryNS)){
			String errMsg = "The " + TypeLibraryConstants.TYPE_INFORMATION_FILE_NAME + "has not defined a value for the attribute libraryNamespace . pls define it.";
			getLogger().log(Level.SEVERE, errMsg);
			throw new  BadInputValueException(errMsg);
		}
		
		refOfGlobalLibraryMap.put(libraryName, typeLibraryType);

		List<TypeInformationType> list = typeLibraryType.getType();
		Iterator<TypeInformationType> iterator = list.iterator();
		while(iterator.hasNext()){
			TypeInformationType typeInformationType = iterator.next();
			
			/*
			 * If the global table already has such an entry then we should just modify the same and not create a new wrapper object.
			 */ 
			QName currTypesQName = new QName(currLibraryNS,typeInformationType.getXmlTypeName());
			LibraryTypeWrapper libraryTypeWrapper = refOfMasterTypeInfoTable.get(currTypesQName);
			if(libraryTypeWrapper == null)
				libraryTypeWrapper = new LibraryTypeWrapper();
			
			
			LibraryType libraryType = new LibraryType();
			libraryType.setLibraryInfo(m_currentTypeLibraryType);
			libraryType.setName(typeInformationType.getXmlTypeName());
			libraryType.setPackage(typeInformationType.getJavaTypeName());
			libraryType.setNamespace(currLibraryNS);
			libraryType.setVersion(typeInformationType.getVersion());
			libraryTypeWrapper.setLibraryType(libraryType);
			
			refOfMasterTypeInfoTable.put(TypeLibraryUtilities.getQNameOfLibraryType(libraryType), libraryTypeWrapper);
		}
	}

	
	


    /**
     *  This method is to be called whenever new types are added  thru gentype
     * @param projectRoot
     * @param libraryName
     * @param modifiedTypeMap
     */
	public void updateTypeInformationXMLFile(TypeLibraryCodeGenContext ctx, String libraryName, Map<String, TypeInformationType> modifiedTypeMap) throws Exception{
		/*
		 * a. using jaxb build the objects for the typeinformation.xml
		 * b. update this objects using the modified list
		 * c. convert the objects back to xml file again
		 * d. update the global tables based on the new TypeInformation.xml file
		 */
		refOfMasterTypeInfoTable = SOAGlobalRegistryImpl.getInstance().getMastertypeInfoTable();
		refOfGlobalLibraryMap = SOAGlobalRegistryImpl.getInstance().getGlobalLibraryMap();

		
		String typeInfoXMLFilePath   = TypeLibraryUtilities.getTypeInfoFolder(ctx, libraryName) + File.separatorChar + TYPE_INFORMATION_FILE_NAME; 		
		File typeInfoFile = new File(typeInfoXMLFilePath);
		
		TypeLibraryType typeLibraryTypeNew = JAXB.unmarshal(typeInfoFile , TypeLibraryType.class);
		List<TypeInformationType> originalList = typeLibraryTypeNew.getType();
		String currLibrarysNS = typeLibraryTypeNew.getLibraryNamespace();
		
		for(int i=0; i < originalList.size() ; i++){
			TypeInformationType oldInformationType = originalList.get(i);
			String typeName = oldInformationType.getXmlTypeName();
			
			TypeInformationType newInformationType = modifiedTypeMap.get(typeName);
			
			if(newInformationType != null){
				originalList.remove(i);
				originalList.add(i, newInformationType);
				
				QName currTypesQName = new QName(currLibrarysNS,typeName);
				LibraryTypeWrapper libraryTypeWrapper =  refOfMasterTypeInfoTable.get(currTypesQName);
				if(libraryTypeWrapper != null){
					LibraryType libraryType = libraryTypeWrapper.getLibraryType();
					if(libraryType == null)
						libraryType = new LibraryType();
					
					libraryType.setPackage(newInformationType.getJavaTypeName());
					libraryType.setVersion(newInformationType.getVersion());
					libraryType.setNamespace(currLibrarysNS);
				}
				
				//removing the types which are processed 
				modifiedTypeMap.remove(typeName);
			}
			
		}
		
		//processing the types which are new 
		Set<String> typesPending = modifiedTypeMap.keySet();
		Iterator<String> iterator = typesPending.iterator();
		while(iterator.hasNext()){
			String typeName = iterator.next();
			typeLibraryTypeNew.getType().add(modifiedTypeMap.get(typeName));
		}
		
		JAXB.marshal(typeLibraryTypeNew, typeInfoFile);
		
		
		/*
		 * updating the global tables since the TypeInformatiom.xml file has now been modified
		 */
		TypeLibraryType typeLibraryTypeAfterUpdate = JAXB.unmarshal(typeInfoFile , TypeLibraryType.class);
		populateTypeInfoGlobalTable(typeLibraryTypeAfterUpdate,libraryName);
		
		
	}


	/**
	 * This method is to be called whenever new types are deleted  thru gentype
	 * @param projectRoot
	 * @param libraryName
	 * @param xsdTypesToBeDeleted
	 * @return
	 * @throws Exception
	 */
	public boolean deleteTypesFromInformationXMLFile(TypeLibraryCodeGenContext ctx, String libraryName,List<String> xsdTypesToBeDeleted) throws Exception {
		
		refOfMasterTypeInfoTable = SOAGlobalRegistryImpl.getInstance().getMastertypeInfoTable();
		refOfGlobalLibraryMap = SOAGlobalRegistryImpl.getInstance().getGlobalLibraryMap();
		
		boolean status = true;
		List<String> xsdTypeNamesWithoutExtensions = new ArrayList<String>(xsdTypesToBeDeleted.size());
		
		String typeInfoXMLFilePath   = TypeLibraryUtilities.getTypeInfoFolder(ctx, libraryName) + File.separatorChar + TYPE_INFORMATION_FILE_NAME; 		
		File typeInfoFile = new File(typeInfoXMLFilePath);
		
		TypeLibraryType typeLibraryType = JAXB.unmarshal(typeInfoFile , TypeLibraryType.class);
        
		Iterator<String> iterator =   xsdTypesToBeDeleted.iterator();
		while(iterator.hasNext()){
			String xsdFileName = iterator.next();
			String xsdTypeName = getTypeNameFromFileName(xsdFileName);
			xsdTypeNamesWithoutExtensions.add(xsdTypeName);
		}
		
		
		List<TypeInformationType> listOfTypes =    typeLibraryType.getType();
		
		// code to delete the type from the parent type
		Iterator<TypeInformationType> typeIterator = listOfTypes.iterator();
		while(typeIterator.hasNext()){
			String currTypeName = typeIterator.next().getXmlTypeName();
			if(xsdTypeNamesWithoutExtensions.contains(currTypeName)){
				typeIterator.remove();
			}
		}
		
		
		
		JAXB.marshal(typeLibraryType, typeInfoFile);
		
		/*
		 * updating the global tables since the TypeInformatiom.xml file has now been modified
		 */
		TypeLibraryType typeLibraryTypeAfterUpdate = JAXB.unmarshal(typeInfoFile , TypeLibraryType.class);
		populateTypeInfoGlobalTable(typeLibraryTypeAfterUpdate,libraryName);
		
		
		/*
		 * Call to global registry to delete this type . such calls are needed only for delete
		 */
		SOATypeRegistry registry = SOAGlobalRegistryFactory.getSOATypeRegistryInstance();
		Iterator<String> iterator2 = xsdTypeNamesWithoutExtensions.iterator();
		while(iterator2.hasNext()){
			registry.removeTypeFromRegistry(iterator2.next());	
		}
		
		return status;
	}


	private String getTypeNameFromFileName(String xsdFileName) {
        int index = xsdFileName.indexOf(".xsd");
        if(index < 0)
        	index = xsdFileName.indexOf(".XSD");
        
        if(index < 0 )
        	return xsdFileName;
        else 
        	return xsdFileName.substring(0, index);
	}
	
}
