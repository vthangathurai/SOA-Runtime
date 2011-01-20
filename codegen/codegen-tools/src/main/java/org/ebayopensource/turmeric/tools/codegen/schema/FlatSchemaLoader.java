/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.schema;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.FlatSchemaComplexTypeImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.FlatSchemaElementDeclImpl;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.exception.BrokenSchemaException;
import org.ebayopensource.turmeric.tools.codegen.exception.UnsupportedSchemaException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.tools.xjc.ErrorReceiver;

/**
 * @author ichernyshev
 */
public abstract class FlatSchemaLoader {

	protected static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	protected static final String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
	protected static final Object XML_SCHEMA_DATATYPES = "http://www.w3.org/2001/XMLSchema-datatypes";
	protected static final String XML_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace";

	private final ErrorReceiverImpl m_errorReceiver = new ErrorReceiverImpl();
	private String m_schemaName;

	public static FlatSchemaLoader createInstanceFromWsdl(String uri)
		throws UnsupportedSchemaException
	{
		FlatSchemaLoader result = new XSOMFlatSchemaLoader();
		result.m_schemaName = uri;
		result.init(uri);

		Document wsdl = result.parseXml(uri);
		
		
		/*   If the WSDL has import element for importing external XSDs using the "schemLocation" attribute and if the value for the attribute
		 *   is not a absolute/relative path , then it has to be manually set so that the DOMForestScanner.scan method does not throw an error.
		 *   This is more of a work-around fix , since the DOMForestScanner is not calling the call back method provided thru the setEntityResolver method
		 *   (for the underlying parser instance)   
		 */
		NodeList imports = wsdl.getElementsByTagNameNS(XML_SCHEMA, "import");
		if(imports.getLength() > 0)
			processWSDLImportsAndIncludes(wsdl,uri,imports);

		NodeList includes = wsdl.getElementsByTagNameNS(XML_SCHEMA, "include");
		if(includes.getLength() > 0)
			processWSDLImportsAndIncludes(wsdl,uri,includes);
		

		NodeList schemas = wsdl.getElementsByTagNameNS(XML_SCHEMA, "schema");
		List<Element> schemas2 = new ArrayList<Element>();
		for (int i=0; i<schemas.getLength(); i++) {
			Element schema = (Element)schemas.item(i);
			schemas2.add(schema);
		}

		result.parseSchemaRootElements(schemas2,imports);
		result.checkForErrors();

		result.load();
		result.checkForErrors();

		return result;
	}

	private static void processWSDLImportsAndIncludes(Document wsdl,String uri,NodeList imports){
		URL wsdlURL = null;
		File wsdlFile = null;
		String wsdlBasePath = null;
		String wsdlBaseAbsolutePath = null;

		try {
			wsdlFile = new File(uri);
			if (wsdlFile.isFile() || uri.indexOf(':') == -1) {
				wsdlURL = new URL("file", null, 0, uri);
				
				wsdlBaseAbsolutePath = wsdlFile.getAbsolutePath();
				wsdlBaseAbsolutePath = wsdlBaseAbsolutePath.replace("\\", "/");
	            int lastPos = wsdlBaseAbsolutePath.lastIndexOf("/");
	            if(lastPos != -1)
	            	wsdlBaseAbsolutePath = wsdlBaseAbsolutePath.substring(0,lastPos + 1);
				
			} else {
				wsdlURL = new URL(uri);
			}
			
			wsdlBasePath = wsdlURL.toString();
			wsdlBasePath = wsdlBasePath.replace("\\","/");
			
            int lastPos = wsdlBasePath.lastIndexOf("/");
            if(lastPos != -1)
            	wsdlBasePath = wsdlBasePath.substring(0,lastPos + 1);
			
		} catch (MalformedURLException e) {}
		
		
	
		
		
		for (int i=0; i<imports.getLength(); i++) {
			Element impElem = (Element)imports.item(i);
			String importFileLocation = impElem.getAttribute("schemaLocation");
			
			if(! isEmptyString(importFileLocation)){
				try {
						new URL(importFileLocation);
					} catch (MalformedURLException e) {
						//If a malformed URL comes then figure out whether that could be a file in the local system
						File importFile = new File(importFileLocation);	
						if(!importFile.isAbsolute()){
							
							String wsdlBasePathToBeUsed = wsdlBaseAbsolutePath;
							if(CodeGenUtil.isEmptyString(wsdlBasePathToBeUsed))
								wsdlBasePathToBeUsed = wsdlBasePath;
							
							if(!CodeGenUtil.isEmptyString(wsdlBasePathToBeUsed)){
								importFileLocation =  importFileLocation.replace("\\","/");
								if(wsdlBasePathToBeUsed.endsWith("/") && importFileLocation.startsWith("/"))
									wsdlBasePathToBeUsed = wsdlBasePathToBeUsed.substring(0,wsdlBasePathToBeUsed.length() - 1 );
									
								importFileLocation =  wsdlBasePathToBeUsed + importFileLocation;
								
							}
							importFileLocation =  importFileLocation.replace("\\","/");
							importFile = new File(importFileLocation);
							if(importFile.exists())
								importFileLocation = importFile.toURI().toString();
							
							impElem.setAttribute("schemaLocation",importFileLocation);
						}
				}
			}
		}		
		
	}
	
	private static boolean isEmptyString(String str) {
		return (str == null || str.trim().length() == 0);
	}

	
	
	public static FlatSchemaLoader createInstanceFromXsd(File[] files)
		throws UnsupportedSchemaException
	{
		FlatSchemaLoader result = new XSOMFlatSchemaLoader();
		result.m_schemaName = (files.length != 0 ? files[0].getParent(): "No_Files");
		result.init(result.m_schemaName);

		result.parseSchemaFromFiles(files);
		result.checkForErrors();

		result.load();
		result.checkForErrors();

		return result;
	}

	public static FlatSchemaLoader createEmptyInstance()
	{
		FlatSchemaLoader result = new EmptyFlatSchemaLoader();
		result.m_schemaName = "Empty";
		return result;
	}

	public abstract List<FlatSchemaComplexTypeImpl> getComplexTypes();

	public abstract Map<QName,FlatSchemaElementDeclImpl> getRootElements();

	protected final Document parseXml(String fileName) throws BrokenSchemaException {
		System.out.println("Loading XML document '" + fileName + "'"); //KEEPME

		InputStream is = null;
		Document result;
		try {
			URL url;
			File file = new File(fileName);
			if (file.isFile() || fileName.indexOf(':') == -1) {
				url = new URL("file", null, 0, fileName);
			} else {
				url = new URL(fileName);
			}

			is = url.openStream();
			result = parseXml(is);
		} catch (MalformedURLException e) {
			throw new BrokenSchemaException("File is not found or URL is broken for '" +
				fileName + "' : " + e.toString(), e);
		} catch (IOException e) {
			throw new BrokenSchemaException("Unable to read '" + fileName + "' : " + e.toString(), e);
		} finally {
			CodeGenUtil.closeQuietly(is);
		}

		return result;
	}

	private Document parseXml(InputStream is) throws BrokenSchemaException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		Document result;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			ErrorHandler errorHandler = m_errorReceiver;
			builder.setErrorHandler(errorHandler);
			result = builder.parse(is);
		} catch (ParserConfigurationException e) {
			throw new BrokenSchemaException(e.toString(), e);
		} catch (SAXException e) {
			throw new BrokenSchemaException(e.toString(), e);
		} catch (IOException e) {
			throw new BrokenSchemaException(e.toString(), e);
		}

		return result;
	}

	private void checkForErrors() throws UnsupportedSchemaException {
		if (!m_errorReceiver.m_errors.isEmpty()) {
			Exception e = m_errorReceiver.m_errors.get(0);
			throw new UnsupportedSchemaException("Error parsing schema in " +
				m_schemaName + ": " + e.toString(), e);
		}
	}

	protected final ErrorReceiver getErrorReceiver() {
		return m_errorReceiver;
	}

	protected abstract void init(String uri) throws UnsupportedSchemaException;

	protected abstract void load() throws UnsupportedSchemaException;

	protected abstract void parseSchemaRootElements(List<Element> schemas,NodeList imports) throws UnsupportedSchemaException;

	protected abstract void parseSchemaFromFiles(File[] files) throws UnsupportedSchemaException;

	private class ErrorReceiverImpl extends ErrorReceiver {
		List<SAXParseException> m_errors = new ArrayList<SAXParseException>();
		List<SAXParseException> m_warnings = new ArrayList<SAXParseException>();

		public void error(SAXParseException e) {
			LogManager.getInstance(FlatSchemaLoader.class).log(Level.SEVERE,
				"Error parsing schema in " + m_schemaName + ": " + e.toString(), e);
			m_errors.add(e);
		}

		public void fatalError(SAXParseException e) {
			error(e);
		}

		public void warning(SAXParseException e) {
			LogManager.getInstance(FlatSchemaLoader.class).log(Level.WARNING,
				"Warning parsing schema in " + m_schemaName + ": " + e.toString(), e);
			m_warnings.add(e);
		}

		public void info(SAXParseException exception) {
		}
	}

	private static class EmptyFlatSchemaLoader extends FlatSchemaLoader {
		@Override
		public List<FlatSchemaComplexTypeImpl> getComplexTypes() {
			return new ArrayList<FlatSchemaComplexTypeImpl>();
		}

		@Override
		public Map<QName,FlatSchemaElementDeclImpl> getRootElements() {
			return new HashMap<QName,FlatSchemaElementDeclImpl>();
		}

		@Override
		protected void init(String uri) throws UnsupportedSchemaException {
		}

		@Override
		protected void load() throws UnsupportedSchemaException {
		}

		@Override
		protected void parseSchemaRootElements(List<Element> schemas,NodeList imports) throws UnsupportedSchemaException {
		}

		@Override
		protected void parseSchemaFromFiles(File[] files) throws UnsupportedSchemaException {
		}
	}
	
	
	
	
}
