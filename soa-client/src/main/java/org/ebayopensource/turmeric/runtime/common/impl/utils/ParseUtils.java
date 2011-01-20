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
package org.ebayopensource.turmeric.runtime.common.impl.utils;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.SchemaValidationLevel;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import com.ebay.kernel.resource.ResourceUtil;

/**
 * @author rpallikonda
 *
 */
public class ParseUtils {
	private static final Logger LOG = Logger.getLogger(ParseUtils.class.getName());
	
	public static final String SYS_PROP_CONFIG_SCHEMA_CHECK 
	= "org.ebayopensource.turmeric.runtime.common.impl.config.schemacheck";
	protected static final SchemaValidationLevel s_schemaCheckLevel;
	static {
	   	String schemaCheckStr = System.getProperty(SYS_PROP_CONFIG_SCHEMA_CHECK);
	   	if (schemaCheckStr == null) {
	   		s_schemaCheckLevel = SchemaValidationLevel.NONE;
	   	} else {
	   		schemaCheckStr = schemaCheckStr.toUpperCase();
	   		s_schemaCheckLevel = SchemaValidationLevel.valueOf(schemaCheckStr);

    	}
	}
	
	public static SchemaValidationLevel getSchemaValidationLevel() {
		return s_schemaCheckLevel;
	}
	
	public static SchemaValidationLevel getSchemaCheckLevel() {
		return s_schemaCheckLevel;
	}

	private static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	public static InputStream getFileStream(String fileName) throws ServiceCreationException {
		ClassLoader classLoader = getClassLoader();
		InputStream inStream = null;
		if (fileName.startsWith("$config/")) {
			String relPath = fileName.substring(8);
			URL url = null;
			try {
				url = ResourceUtil.getResource("config", relPath);
				if (url != null) {
					inStream = url.openStream();
				}
			} catch (IOException ioExc) {
				throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_CANNOT_LOAD_FILE,
						ErrorConstants.ERRORDOMAIN, new Object[] {fileName}), ioExc);
			}
		} else {
			URL resource = classLoader.getResource(fileName);
			
			String logout = System.getProperty("test.log.out");
			if(logout!=null && logout.equals("true")){
				if(resource != null)
					LOG.log(Level.SEVERE, "resource loaded: "+resource.getFile()+" :: "+resource.toString());
				else
					LOG.log(Level.SEVERE, "resource: "+resource);
			}
			
			if(resource != null) {
				try {
					inStream = resource.openStream();
				} catch (IOException e) {
					LOG.log(Level.WARNING, "Unable to open stream from url: " + resource, e);
					inStream = null;
				}
			} else {
				LOG.log(Level.WARNING, "Unable to find resource: " + fileName);
				inStream = null;
			}
		}
		return inStream;
	}

	public static synchronized Document parseConfig(String fileName, String schemaName, boolean isOptional, String topLevelName, SchemaValidationLevel checkLevel) throws ServiceCreationException {
		InputStream	inStream = getFileStream(fileName);
		if (inStream == null) {
			if (isOptional) {
				return null;
			}
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_CANNOT_LOAD_FILE,
					ErrorConstants.ERRORDOMAIN, new Object[] {fileName}));
		}
		return parseConfig(inStream, fileName, schemaName, topLevelName,
				checkLevel);
    }

	public static Document parseConfig(InputStream in, String assocURL,
			String schemaName, String topLevelName,
			SchemaValidationLevel checkLevel) throws ServiceCreationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Document result = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			ErrorHandler errorHandler = new ParseErrorHandler(assocURL, checkLevel);
			builder.setErrorHandler(errorHandler);
			result = builder.parse(in);
		} catch (ParserConfigurationException e) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_PARSE_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {assocURL, e.toString()}), e);
		} catch (SAXException e) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_PARSE_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {assocURL, e.toString()}), e);
		} catch (IOException e) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_PARSE_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {assocURL, e.toString()}), e);
		}
		Element docElement = result.getDocumentElement();
        if (docElement == null) {
        	throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR, 
        			ErrorConstants.ERRORDOMAIN, new Object[] {assocURL, "Document has no top-level element"}));
        }
        validate(schemaName, assocURL, docElement, checkLevel);
        if (!docElement.getNodeName().equals(topLevelName)) {
        	throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR, 
        			ErrorConstants.ERRORDOMAIN, new Object[] {assocURL, "Top-level element name: " + docElement.getNodeName() + "; expected: " + topLevelName}));
        }
		return result;
	}
	
	private static void validate(String schemaName, String filename, Node document, SchemaValidationLevel checkLevel) throws ServiceCreationException {
		if (checkLevel.equals(SchemaValidationLevel.NONE)) {
			return;
		}
		ClassLoader classLoader = getClassLoader();
		URL url = classLoader.getResource(schemaName);
		if (url == null) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_CANNOT_LOAD_FILE,
					ErrorConstants.ERRORDOMAIN, new Object[] {schemaName}));
    	}

		SchemaFactory factory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);

		try {
			Schema schema = factory.newSchema(url);
			Validator validator = schema.newValidator();
			ErrorHandler errorHandler = new ParseErrorHandler(filename, checkLevel);
			validator.setErrorHandler(errorHandler);
			validator.validate(new DOMSource(document));
		} catch (SAXException se) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_PARSE_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {filename, se.toString()}), se);
		} catch (IOException ioe) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_IO_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[] {schemaName, ioe.toString()}), ioe);
		}
	}

	private static class ParseErrorHandler implements ErrorHandler {
		private final String m_filename;
		private final SchemaValidationLevel m_checkLevel;
		
		ParseErrorHandler(String filename, SchemaValidationLevel checkLevel) {
			m_filename = filename;
			m_checkLevel = checkLevel;
		}
		public void warning(SAXParseException e) {
			reportError(Level.WARNING, e);
		}

		public void error(SAXParseException e) throws SAXParseException {
			reportError(Level.SEVERE, e);
			if (m_checkLevel.equals(SchemaValidationLevel.ERROR)) {
				throw e;
			}
		}
		
		public void fatalError(SAXParseException e) throws SAXParseException {
			reportError(Level.SEVERE, e);
			if (m_checkLevel.equals(SchemaValidationLevel.ERROR)) {
				throw e;
			}
		}
		
		private void reportError(Level level, SAXParseException e) {
			StringBuffer b = new StringBuffer();
			b.append(m_filename);
			int line = e.getLineNumber();
			if (line != -1) {
				b.append(" line ");
				b.append(line);
			}
			b.append(": ");
			b.append(e.getMessage());
			LogManager.getInstance(this.getClass()).log(level, b.toString());
		}
	}


}
