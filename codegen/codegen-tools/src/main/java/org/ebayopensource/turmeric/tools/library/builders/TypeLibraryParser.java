/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library.builders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.axis2.wsdl.codegen.CodeGenerationException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.ContextClassLoaderUtil;
import org.ebayopensource.turmeric.tools.library.TypeLibraryConstants;
import org.ebayopensource.turmeric.tools.library.codegen.TypeLibraryCodeGenContext;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class TypeLibraryParser extends DefaultHandler {


	
	/*
	 * Constants representing element name and attribute names
	 */

	private  final String ELEMENT_TYPE_REF_TYPE_LIB = "referredTypeLibrary";
	private  final String ATTRIBUTE_NAME = "name";

	private  volatile String m_currentReferredLibraryName;

	private  final Set<String> m_referedTypeLibraries = new HashSet<String>();

	private  final String TYPE_DEPENDENCY_FILE_NAME = "TypeDependencies.xml";
	
	
	private static CallTrackingLogger logger = LogManager.getInstance(TypeLibraryParser.class);
	
	private static CallTrackingLogger getLogger(){
		return logger;
	}
	
	
	
	/**
	 * 
	 * @return An instance of TypeDependencyParser
	 */
	public static synchronized TypeLibraryParser getInstance() {
		return new TypeLibraryParser();
	}
	
	/**
	 * @deprecated use {@link #processTypeDepXMLFileForGen(TypeLibraryCodeGenContext, String)} instead.
	 */
	@Deprecated
	public void processTypeDepXMLFileForGen(String projectRoot, String typeLibraryName) throws Exception{
		throw new CodeGenerationException("Unable to processTypeDefXMLFileForGen without a proper TypeLibraryCodeGenContext.");
	}
	
	public void processTypeDepXMLFileForGen(TypeLibraryCodeGenContext codeGenCtx, String typeLibraryName) throws Exception{
		
		String typeDefsFolder = TypeLibraryUtilities.getTypeDepFolder(codeGenCtx, typeLibraryName );
		String defaultSvcLayerFilePath   = TypeLibraryConstants.META_INF_FOLDER + "/" + typeLibraryName + "/" + TYPE_DEPENDENCY_FILE_NAME; 
  	    InputStream	inStream = null;
  	    
		try {
			inStream   = ContextClassLoaderUtil.getResourceAsStream(defaultSvcLayerFilePath);
			if(inStream == null){
	   	    	File typeDepFile = new File(typeDefsFolder + File.separator + TYPE_DEPENDENCY_FILE_NAME);
	   	    	if(!typeDepFile.exists()){
	   	    		getLogger().log(
							Level.WARNING,
							TYPE_DEPENDENCY_FILE_NAME
									+ " could not be found for library "
									+ typeLibraryName + " under the path  "
									+ typeDepFile.getPath());
	   	    		return; //Its not mandatory for a project to have TypeDependencies.xml file
	   	    	}
	   	    	
	   	    	inStream = new FileInputStream(typeDepFile);
			}
  	    
			DefaultHandler handler = this;
			SAXParserFactory factory = SAXParserFactory.newInstance();

			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(inStream, handler);

		} catch (Throwable t) {
			getLogger().log(Level.SEVERE, "Unable to parse the TypeDepedencies.xml file, of library " + typeLibraryName  + " its content could be invalid", t);
			throw new Exception(t);
		}
		finally{
			CodeGenUtil.closeQuietly(inStream);
		}

	}

	
    /**
     * 
     * @param typeLibraryName
     */
	public void processTypeDepXMLFile(String typeLibraryName) throws Exception{
		
			
		String defaultSvcLayerFilePath   = TypeLibraryConstants.META_INF_FOLDER + "/" + typeLibraryName + "/" + TYPE_DEPENDENCY_FILE_NAME; 
		ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
  	    InputStream	inStream = null;
  	    
		try {
	  	    inStream = myClassLoader.getResourceAsStream(defaultSvcLayerFilePath);
	  	    
	  	    if(inStream == null){
	  	    	getLogger().log(
					Level.WARNING,
					TYPE_DEPENDENCY_FILE_NAME
							+ " could not be found for library "
							+ typeLibraryName + " under the class path  "
							+ defaultSvcLayerFilePath);
	   	
	  	    	return;
	  	    }
	  	  
			DefaultHandler handler = this;
			SAXParserFactory factory = SAXParserFactory.newInstance();

			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(inStream, handler);

		} catch (Throwable t) {
			getLogger().log(Level.SEVERE, "Unable to parse the TypeDepedencies.xml file, of library " + typeLibraryName  + " its content could be invalid", t);
			throw new Exception(t);
		}
		finally{
			CodeGenUtil.closeQuietly(inStream);
		}

	}

	
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		String eName = localName; // element name
		if ("".equals(eName)) eName = qName; // not namespace-aware
		
		 if(eName.contains(ELEMENT_TYPE_REF_TYPE_LIB))
			processElement_refTypeLib(uri, eName, attributes);
				
	}



	private void processElement_refTypeLib(String uri, String name, Attributes attributes)  throws SAXException {
		if(attributes != null){
			m_currentReferredLibraryName = attributes.getValue(ATTRIBUTE_NAME);
			
		}
		
	}


	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String eName = localName; // element name
		if ("".equals(eName)) eName = qName; // not namespace-aware
		
		if(eName.contains(ELEMENT_TYPE_REF_TYPE_LIB)) {
			m_referedTypeLibraries.add(m_currentReferredLibraryName);
			m_currentReferredLibraryName = null;
			
		}
	}

	
	public Set<String> getReferredTypeLibraries(){
		return m_referedTypeLibraries;
	}

}
