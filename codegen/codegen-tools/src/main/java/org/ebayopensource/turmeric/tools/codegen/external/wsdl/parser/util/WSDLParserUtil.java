/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.util;


import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

import org.apache.axis2.util.JavaUtils;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.AuthenticatingProxyWSDLLocatorImpl;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.WSDLParserConstants;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.WSDLParserException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.ibm.wsdl.Constants;

/**
 * This class provides utilities for WSDL Parser
 *
 * 
 */
public class WSDLParserUtil {
	
    private static final String DOT = ".";
    private static final String XMLSEPARATORS =
        "\u002D\u002E\u003A\u00B7\u0387\u06DD\u06DE\u30FB";
    private static final String XMLSEPARATORS_NODOT =
        "\u002D\u003A\u00B7\u0387\u06DD\u06DE";
    private static final String UNDERSCORE = "_";
     
    private static Boolean providersInitialized = Boolean.FALSE;
    private static boolean simpleTypesMapCreated = false;
    private static HashMap<QName,String> simpleTypesMap = new HashMap<QName,String>();
    
 
    public static Service selectService(
        Definition def,
        String serviceNS,
        String serviceName)
        throws WSDLParserException {
        Map<QName, Object> services = getAllItems(def, "Service");
        QName serviceQName =
            ((serviceNS != null && serviceName != null)
                ? new QName(serviceNS, serviceName)
                : null);
        Service service =
            (Service) getNamedItem(services, serviceQName, "Service");

        return service;
    }

    public static PortType selectPortType(
        Definition def,
        String portTypeNS,
        String portTypeName)
        throws WSDLParserException {
        Map<QName, Object> portTypes = getAllItems(def, "PortType");
        QName portTypeQName =
            ((portTypeNS != null && portTypeName != null)
                ? new QName(portTypeNS, portTypeName)
                : null);
        PortType portType =
            (PortType) getNamedItem(portTypes, portTypeQName, "PortType");

        return portType;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void addDefinedItems(
        Map fromItems,
        String itemType,
        Map toItems) {

        if (fromItems != null) {
            Iterator entryIterator = fromItems.entrySet().iterator();

            if (itemType.equals("Message")) {
                while (entryIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) entryIterator.next();
                    Message message = (Message) entry.getValue();

                    if (!message.isUndefined()) {
                        toItems.put(entry.getKey(), message);
                    }
                }
            } else if (itemType.equals("Operation")) {
                while (entryIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) entryIterator.next();
                    Operation operation = (Operation) entry.getValue();

                    if (!operation.isUndefined()) {
                        toItems.put(entry.getKey(), operation);
                    }
                }
            } else if (itemType.equals("PortType")) {
                while (entryIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) entryIterator.next();
                    PortType portType = (PortType) entry.getValue();

                    if (!portType.isUndefined()) {
                        toItems.put(entry.getKey(), portType);
                    }
                }
            } else if (itemType.equals("Binding")) {
                while (entryIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) entryIterator.next();
                    Binding binding = (Binding) entry.getValue();

                    if (!binding.isUndefined()) {
                        toItems.put(entry.getKey(), binding);
                    }
                }
            } else if (itemType.equals("Service")) {
                while (entryIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) entryIterator.next();
                    Service service = (Service) entry.getValue();

                    toItems.put(entry.getKey(), service);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static void getAllItems(
        Definition def,
        String itemType,
        Map<QName, Object> toItems) {

    	Map items = null;

        if (itemType.equals("PortType")) {
            items = def.getPortTypes();
        } else if (itemType.equals("Service")) {
            items = def.getServices();
        } else {
            throw new IllegalArgumentException(
                "Don't know how to find all " + itemType + "s.");
        }

        addDefinedItems(items, itemType, toItems);

        Map<?,?> imports = def.getImports();

        if (imports != null) {
            Iterator<?> valueIterator = imports.values().iterator();

            while (valueIterator.hasNext()) {
                List<?> importList = (List<?>) valueIterator.next();

                if (importList != null) {
                    Iterator<?> importIterator = importList.iterator();

                    while (importIterator.hasNext()) {
                        Import tempImport = (Import) importIterator.next();

                        if (tempImport != null) {
                            Definition importedDef = tempImport.getDefinition();

                            if (importedDef != null) {
                                getAllItems(importedDef, itemType, toItems);
                            }
                        }
                    }
                }
            }
        }
    }
    

    public static Map<QName, Object> getAllItems(Definition def, String itemType) {
        Map<QName, Object> ret = new HashMap<QName, Object>();

        getAllItems(def, itemType, ret);

         return ret;
    }

    public static Object getNamedItem(Map<QName,Object> items, QName qname, String itemType)
        throws WSDLParserException {

    	if (qname != null) {
            Object item = items.get(qname);

            if (item != null) {
                 return item;
            } else {
                throw new WSDLParserException(
                    itemType
                        + " '"
                        + qname
                        + "' not found. Choices are: "
                        + getCommaListFromQNameMap(items));
            }
        } else {
            int size = items.size();

            if (size == 1) {
                Iterator<Object> valueIterator = items.values().iterator();

                Object o = valueIterator.next();
                 return o;
            } else if (size == 0) {
                throw new WSDLParserException(
                    "WSDL document contains no " + itemType + "s.");
            } else {
                throw new WSDLParserException(
                    "Please specify a "
                        + itemType
                        + ". Choices are: "
                        + getCommaListFromQNameMap(items));
            }
        }
    }

    
    private static String getCommaListFromQNameMap(Map<QName,Object> qnameMap) {
        StringBuffer strBuf = new StringBuffer("{");
        Set<QName> keySet = qnameMap.keySet();
        Iterator<QName> keyIterator = keySet.iterator();
        int index = 0;

        while (keyIterator.hasNext()) {
            QName key = keyIterator.next();

            strBuf.append((index > 0 ? ", " : "") + key);
            index++;
        }

        strBuf.append("}");

        return strBuf.toString();
    }

    /**
     * Read WSDL - it is different from a WSDLReader readWSDL method in that it
     * specifies the use of the WSIF WSDLFactory implementation which registers
     * the extensiblity elements used by the WSIF providers.
     * @param contextURL The context in which to resolve the wsdlLoc, if the wsdlLoc is 
     * relative. Can be null, in which case it will be ignored.
     * @param wsdlLoc a URI (can be a filename or URL) pointing to a WSDL XML definition.
     * @return Defintion object representing the definition in the wsdl
     * @throws WSDLException Exception thrown if wsdl cannot be read
     */
    public static Definition readWSDL(String contextURL, String wsdlLoc)
        throws WSDLException {

        initializeProviders();

        WSDLFactory factory = WSDLFactory.newInstance(
            WSDLParserConstants.WSDL_FACTORY);
        WSDLReader wsdlReader = factory.newWSDLReader();
        wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
        try {
            Definition def = wsdlReader.readWSDL(contextURL, wsdlLoc);
            return def;
        } catch (WSDLException e) {
             throw e;
        }
    }

    /**
     * Read WSDL through an authenticating proxy. It is different from a WSDLReader 
     * readWSDL method in that it
     * specifies the use of the WSIF WSDLFactory implementation which registers
     * the extensiblity elements used by the WSIF providers.
     * @param wsdlLoc a URI (must be an http or ftp URL) pointing to a WSDL file
     * @param pa A username and password for the proxy, encapsulated as a 
     * java.net.PasswordAuthentication object
     * @return Defintion object representing the definition in the wsdl
     * @throws WSDLException Exception thrown if wsdl cannot be read
     */
    public static Definition readWSDLThroughAuthProxy(String wsdlLoc, PasswordAuthentication pa)
        throws WSDLException {

        initializeProviders();

        WSDLFactory factory = WSDLFactory.newInstance(
        		WSDLParserConstants.WSDL_FACTORY);
        WSDLReader wsdlReader = factory.newWSDLReader();
        wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
        WSDLLocator lo = null;
        try {
            lo = new AuthenticatingProxyWSDLLocatorImpl(wsdlLoc, pa);
            Definition def = wsdlReader.readWSDL(lo);
            return def;
        } catch (WSDLException e) {
             throw e;
        } finally {
        	CodeGenUtil.closeQuietly(lo);
        }
    }

    /**
     * Read WSDL - it is different from a WSDLReader readWSDL method in that it
     * specifies the use of the WSIF WSDLFactory implementation which registers
     * the extensiblity elements used by the WSIF providers.
     * @param loc A WSDLLocator to use in locating the wsdl file and its imports
     * @throws WSDLException Exception thrown if wsdl cannot be read
     */
    public static Definition readWSDL(WSDLLocator loc)
        throws WSDLException {
        
        if (loc == null) {
        	throw new WSDLException(WSDLException.CONFIGURATION_ERROR,
        	   "Cannot use null WSDLLocator for reading wsdl");
        }
        
        initializeProviders();

        WSDLFactory factory = WSDLFactory.newInstance(
        		WSDLParserConstants.WSDL_FACTORY);
        WSDLReader wsdlReader = factory.newWSDLReader();
        wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
        try {
            Definition def = wsdlReader.readWSDL(loc);
            return def;
        } catch (WSDLException e) {
            throw e;
        }
    }

    
    /**
     * Read WSDL - it is different from a WSDLReader readWSDL method in that it
     * specifies the use of the WSIF WSDLFactory implementation which registers
     * the extensiblity elements used by the WSIF providers.
     * @param documentBase A URL for the document base URI for the wsdl
     * @param reader A Reader "pointing at" the wsdl file
     * @param cl A ClassLoader used to resolve relative imports when files are in
     * in the classpath
     * @return Defintion object representing the definition in the wsdl
     * @throws WSDLException Exception thrown if wsdl cannot be read
     */
    /*
    public static Definition readWSDL(
        URL documentBase,
        Reader reader,
        ClassLoader cl)
        throws WSDLException {
        String base = (documentBase == null) ? null : documentBase.toString();
        return readWSDL(base, reader, cl);
    }
    */

    /**
     * Read WSDL - it is different from a WSDLReader readWSDL method in that it
     * specifies the use of the WSIF WSDLFactory implementation which registers
     * the extensiblity elements used by the WSIF providers.
     * @param documentBase The document base URI for the wsdl
     * @param reader A Reader "pointing at" the wsdl file
     * @param cl A ClassLoader used to resolve relative imports when files are in
     * in the classpath
     * @return Defintion object representing the definition in the wsdl
     * @throws WSDLException Exception thrown if wsdl cannot be read
     */
    /*
    public static Definition readWSDL(
        String documentBase,
        Reader reader,
        ClassLoader cl)
        throws WSDLException {
 
        initializeProviders();

        WSDLFactory factory = WSDLFactory.newInstance(
        		WSDLParserConstants.WSDL_FACTORY);
        WSDLReader wsdlReader = factory.newWSDLReader();
        wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
        WSIFWSDLLocatorImpl lo = null;
        try {
            lo = new WSIFWSDLLocatorImpl(documentBase, reader, cl);
            Definition def = wsdlReader.readWSDL(lo);
            return def;
        } catch (WSDLException e) {
            throw e;
        } finally {
        	CodeGenUtil.closeQuietly(lo);
        }
    }
    */

    /**
     * Read WSDL - it is different from a WSDLReader readWSDL method in that it
     * specifies the use of the WSIF WSDLFactory implementation which registers
     * the extensiblity elements used by the WSIF providers.
     * @param contextURL The context in which to resolve the wsdlLoc, if the wsdlLoc is 
     * relative. Can be null, in which case it will be ignored.
     * @param wsdlLoc a URI (can be a filename or URL) pointing to a WSDL XML definition.
     * @param cl A ClassLoader used to resolve relative imports when files are in
     * in the classpath
     * @return Defintion object representing the definition in the wsdl
     * @throws WSDLException Exception thrown if wsdl cannot be read
     */
    /*
    public static Definition readWSDL(
        URL contextURL,
        String wsdlLoc,
        ClassLoader cl)
        throws WSDLException {

        initializeProviders();

        WSDLFactory factory = WSDLFactory.newInstance(
        		WSDLParserConstants.WSDL_FACTORY);
        WSDLReader wsdlReader = factory.newWSDLReader();
        wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
        WSIFWSDLLocatorImpl lo = null;

        try {
            String url = (contextURL == null) ? null : contextURL.toString();
            lo = new WSIFWSDLLocatorImpl(url, wsdlLoc, cl);
            Definition def = wsdlReader.readWSDL(lo);
            return def;
        } catch (WSDLException e) {
            throw e;
        } finally {
        	CodeGenUtil.closeQuietly(lo);
        }
    }
    */

    /**
     * Read WSDL - it is different from a WSDLReader readWSDL method in that it
     * specifies the use of the WSIF WSDLFactory implementation which registers
     * the extensiblity elements used by the WSIF providers.
     * @param documentBaseURI the document base URI of the WSDL definition
     * described by the element. Will be set as the documentBaseURI
     * of the returned Definition. Can be null, in which case it
     * will be ignored.
     * @param reader A Reader "pointing at" the wsdl file
     * @return Defintion object representing the definition in the wsdl
     * @throws WSDLException Exception thrown if wsdl cannot be read
     */
    public static Definition readWSDL(String documentBaseURI, Reader reader)
        throws WSDLException {

        initializeProviders();

        WSDLFactory factory = WSDLFactory.newInstance(
        		WSDLParserConstants.WSDL_FACTORY);
        WSDLReader wsdlReader = factory.newWSDLReader();
        wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
        Definition def =
            wsdlReader.readWSDL(documentBaseURI, new InputSource(reader));
        return def;
    }

    /**
     * Read WSDL - it is different from a WSDLReader readWSDL method in that it
     * specifies the use of the WSIF WSDLFactory implementation which registers
     * the extensiblity elements used by the WSIF providers.
     * @param documentBaseURI the document base URI of the WSDL definition
     * described by the element. Will be set as the documentBaseURI
     * of the returned Definition. Can be null, in which case it
     * will be ignored.
     * @param wsdlDocument The base wsdl document
     * @return Defintion object representing the definition in the wsdl
     * @throws WSDLException Exception thrown if wsdl cannot be read
     */
    public static Definition readWSDL(String documentBaseURI, Document wsdlDocument)
        throws WSDLException {

        initializeProviders();

        WSDLFactory factory = WSDLFactory.newInstance(
        		WSDLParserConstants.WSDL_FACTORY);
        WSDLReader wsdlReader = factory.newWSDLReader();
        wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
        Definition def = wsdlReader.readWSDL(documentBaseURI, wsdlDocument);

        return def;
    }

    /**
     * Read WSDL - it is different from a WSDLReader readWSDL method in that it
     * specifies the use of the WSIF WSDLFactory implementation which registers
     * the extensiblity elements used by the WSIF providers.
     * @param documentBaseURI the document base URI of the WSDL definition
     * described by the element. Will be set as the documentBaseURI
     * of the returned Definition. Can be null, in which case it
     * will be ignored.
     * @param definitionsElement the &lt;wsdl:definitions&gt; element
     * @return Defintion object representing the definition in the wsdl
     * @throws WSDLException Exception thrown if wsdl cannot be read
     */
    public static Definition readWSDL(
        String documentBaseURI,
        Element wsdlServicesElement)
        throws WSDLException {

        initializeProviders();

        WSDLFactory factory = WSDLFactory.newInstance(
        		WSDLParserConstants.WSDL_FACTORY);
        WSDLReader wsdlReader = factory.newWSDLReader();
        wsdlReader.setFeature(Constants.FEATURE_VERBOSE, false);
        Definition def = wsdlReader.readWSDL(documentBaseURI, wsdlServicesElement);

        return def;
    }

    /**
     * Write WSDL. This method will use the WSIF WSDLFactory implementation to
     * create a WSDLWriter.
     * @param wsdlDef the WSDL definition to be written.
     * @param sink the Writer to write the xml to.
     * @throws WSDLException Exception thrown if wsdl cannot be written
     */
    public static void writeWSDL(Definition def, Writer sink)
        throws WSDLException {

        WSDLFactory factory = WSDLFactory.newInstance(
        		WSDLParserConstants.WSDL_FACTORY);
        WSDLWriter wsdlWriter = factory.newWSDLWriter();
        wsdlWriter.writeWSDL(def, sink);

    }
    

    public static Definition getDefinitionFromLocation(
        String contextURL,
        String location)
        throws WSDLParserException {

        if (location == null) {
            throw new WSDLParserException("WSDL location must not be null.");
        }

        Definition def = null;
        try {
            def = WSDLParserUtil.readWSDL(contextURL, location);
        } catch (WSDLException e) {
            throw new WSDLParserException("Problem reading WSDL document.", e);
        }
        return def;
    }
    
    

    public static Definition getDefinitionFromContent(
        String contextURL,
        String content)
        throws WSDLParserException {
        if (content == null) {
            throw new WSDLParserException("WSDL content must not be null.");
        }

        Definition def = null;
        try {
            def = WSDLParserUtil.readWSDL(contextURL, new StringReader(content));
        } catch (WSDLException e) {
             throw new WSDLParserException("Problem reading WSDL document.", e);
        }
        return def;
    }

    /**
     * Initialize the WSIF providers. Each provider initializes its WSDL
     * extension registries. This has no effect if AutoLoad providers has
     * been turned off on WSIFServiceImpl ... in that case it is the
     * responsibility of the application to initialize providers.
     */
    public synchronized static void initializeProviders() {
        if (!providersInitialized.booleanValue()) {
            providersInitialized = Boolean.TRUE;
        }
    }

    /**
     * Create a map of all schema simple types and there Java equivalents.
     */
    public static void createSimpleTypesMap() {
        synchronized (simpleTypesMap) {
            if (!simpleTypesMapCreated) {
                MappingHelper.populateWithStandardXMLJavaMappings(
                    simpleTypesMap,
                    WSDLParserConstants.NS_URI_1999_SCHEMA_XSD,
                    true);
                MappingHelper.populateWithStandardXMLJavaMappings(
                    simpleTypesMap,
                    WSDLParserConstants.NS_URI_2000_SCHEMA_XSD,
                    false);
                MappingHelper.populateWithStandardXMLJavaMappings(
                    simpleTypesMap,
                    WSDLParserConstants.NS_URI_2001_SCHEMA_XSD,
                    false);
                simpleTypesMapCreated = true;
            }
        }
    }

    /**
     * Get a map of all schema simple types and there Java equivalents.
     * @return The map of simple types
     */
    public static Map<QName, String> getSimpleTypesMap() {
        if (!simpleTypesMapCreated) {
            createSimpleTypesMap();
        }
        return simpleTypesMap;
    }

 
    
	public static String getPackageNameFromNamespaceURI(String namespaceURI) {
        // Get the segments in the namespace URI
        List<String> segments = getNamespaceURISegments(namespaceURI);

        StringBuffer packageNameBuffer = new StringBuffer();
        for (int i = 0; i < segments.size(); i++) {
            String name;

            // The first segment is the host name
            if (i == 0) {

                // Turn segment into a valid package segment name
                name = getPackageNameFromXMLName(segments.get(i));

                // Reverse its components
                StringTokenizer tokenizer = new StringTokenizer(name, ".");
                List<String> host = new ArrayList<String>();
                for (; tokenizer.hasMoreTokens();) {
                    String nextT = tokenizer.nextToken();
                    host.add(0, nextT);
                }
                StringBuffer buffer = new StringBuffer();
                for (Iterator<String> hi = host.iterator(); hi.hasNext();) {
                    if (buffer.length() != 0)
                        buffer.append('.');
                    String nextSegment = hi.next();
                    if (!Character
                        .isJavaIdentifierStart(nextSegment.toCharArray()[0]))
                        nextSegment = UNDERSCORE + nextSegment;
                    if (isJavaKeyword(nextSegment))
                        nextSegment = UNDERSCORE + nextSegment;
                    buffer.append(nextSegment);
                }
                name = buffer.toString();
            } else {

                // Turn segment into a valid java name
                name = getJavaNameFromXMLName(segments.get(i));

            }

            // Concatenate segments, separated by '.'
            if (name.length() == 0)
                continue;
            if (packageNameBuffer.length() != 0)
                packageNameBuffer.append('.');
            packageNameBuffer.append(name);
        }
        return packageNameBuffer.toString();
    }
    
    

    public static String getJavaNameFromXMLName(
        String xmlName,
        String delims) {

    	StringTokenizer tokenizer = new StringTokenizer(xmlName, delims);
        StringBuffer buffer = new StringBuffer();
        while (tokenizer.hasMoreTokens()) {
            buffer.append(tokenizer.nextToken());
        }
        String result = buffer.toString();
        if (!Character.isJavaIdentifierStart(result.toCharArray()[0]))
            result = UNDERSCORE + result;
        if (isJavaKeyword(result))
            result = UNDERSCORE + result;

        return result;
    }

    
    public static String getJavaNameFromXMLName(String xmlName) {
        String s = getJavaNameFromXMLName(xmlName, XMLSEPARATORS);
        return s;
    }
    

    public static String getPackageNameFromXMLName(String xmlName) {

        // Tokenize, don't consider '.' as a delimiter here
        String name = getJavaNameFromXMLName(xmlName, XMLSEPARATORS_NODOT);

        // Tokenize using delimiter '.' and add the tokens separated by '.'
        // This is to ensure that we have no heading/trailing/dup '.' in the string
        StringTokenizer tokenizer = new StringTokenizer(name, DOT);
        StringBuffer buffer = new StringBuffer();
        for (; tokenizer.hasMoreTokens();) {
            if (buffer.length() != 0)
                buffer.append('.');
            // -->				
            String nextSegment = (String) tokenizer.nextToken();
            if (!Character.isJavaIdentifierStart(nextSegment.toCharArray()[0]))
                nextSegment = UNDERSCORE + nextSegment;
            if (isJavaKeyword(nextSegment))
                nextSegment = UNDERSCORE + nextSegment;
            buffer.append(nextSegment);

            //			buffer.append(tokenizer.nextToken());
            // <--				
        }
        return buffer.toString();
    }
    

	private static List<String> getNamespaceURISegments(String namespaceURI) {

        // Tokenize
        List<String> segments = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(namespaceURI, ":/");
        while (tokenizer.hasMoreTokens()) {
            segments.add(tokenizer.nextToken());
        }

        // Remove protocol
        if (!segments.isEmpty()) {
            try {
                URL url = new URL(namespaceURI);
                if (segments.get(0).equals(url.getProtocol()))
                    segments.remove(0);
            } catch (MalformedURLException exn) {
            }
        }
        return segments;
    }
    
    

    public static String getJavaClassNameFromXMLName(String xmlName) {
        String s = getJavaClassNameFromXMLName(xmlName, XMLSEPARATORS);
        return s;
    }

    public static String getJavaClassNameFromXMLName(
        String xmlName,
        String delims) {
        StringTokenizer tokenizer = new StringTokenizer(xmlName, delims);
        StringBuffer buffer = new StringBuffer();
        while (tokenizer.hasMoreTokens()) {
            String nextSegment = (String) tokenizer.nextToken();
            if (nextSegment.length() > 0) {
                nextSegment =
                    Character.toUpperCase((nextSegment.toCharArray())[0])
                        + nextSegment.substring(1);
            }
            buffer.append(nextSegment);
        }
        String result = buffer.toString();
        if (!Character.isJavaIdentifierStart(result.toCharArray()[0]))
            result = UNDERSCORE + result;
        if (isJavaKeyword(result))
            return UNDERSCORE + result;
        else
            return result;
    }
    

    public static String getXSDNamespaceFromPackageName(String packageName) {
    
        String result = "";
        StringTokenizer tokenizer = new java.util.StringTokenizer(packageName, ".");
        while (tokenizer.hasMoreTokens()) {
            String nextT = tokenizer.nextToken();
            result = removeUnderscores(nextT) + "." + result;
        }
        if (result.endsWith(".")) {
            return "http://" + result.substring(0, result.length() - 1) + "/";
        }
        return "http://" + result + "/";
    }
    
    /**
     * Remove any underscore (_) characters from a string
     */
    private static String removeUnderscores(String s) {
        StringBuilder strBuilder = new StringBuilder();
        StringTokenizer tokenizer = new StringTokenizer(s, UNDERSCORE);
        while (tokenizer.hasMoreTokens()) {
            String nextT = tokenizer.nextToken();
            strBuilder.append(nextT);
        }
        return strBuilder.toString();
    }

   /**
    * Get a binding operation for a portType operation.
    * 
    * @param binding the WSLD binding the operation will choosen from
    * @param portTypeOp the portType operation the binding operation 
    *         must match
    * @return the BindingOperation  
    */
   public static BindingOperation getBindingOperation(
      Binding binding,
      Operation portTypeOp) throws WSDLParserException {

       BindingOperation bop;
      if ( portTypeOp == null ) {
      	bop = null;
      } else {
      	bop = getBindingOperation( 
           binding, 
           portTypeOp.getName(),
           portTypeOp.getInput()==null ? null : portTypeOp.getInput().getName(),
           portTypeOp.getOutput()==null ? null : portTypeOp.getOutput().getName() );
      }
      return bop;
   }
   

   /**
    * Get a binding operation for a portType operation.
    * 
    * @param binding the WSLD binding the operation will choosen from
    * @param opName the portType operation name of the wanted operation
    * @param inName the portType operation input name
    * @param outName the portType operation outpur name
    * @return the BindingOperation  
    */
   @SuppressWarnings("rawtypes")
   public static BindingOperation getBindingOperation(
      Binding binding,
      String opName,
      String inName,
      String outName) throws WSDLParserException {

      BindingOperation op = null;
      if (binding != null && opName != null) {
         ArrayList<BindingOperation> matchingOps = new ArrayList<BindingOperation>();
         List bops = binding.getBindingOperations();
         if (bops != null) {
            for (Iterator i = bops.iterator(); i.hasNext();) {
               BindingOperation bop = (BindingOperation) i.next();
               if ( opName.equalsIgnoreCase(bop.getName()) ) {
                  matchingOps.add(bop);
               }
            }
            if (matchingOps.size() == 1) {
               op = matchingOps.get(0);
            } else if (matchingOps.size() > 1) {
               op = chooseBindingOperation(matchingOps, inName, outName);
            }
         }
      }
      return op;      	
   }
   

   private static BindingOperation chooseBindingOperation(
      ArrayList<BindingOperation> bindingOps,
      String inName,
      String outName) throws WSDLParserException {
      	
      BindingOperation choosenOp = null;
      for (Iterator<BindingOperation> i = bindingOps.iterator(); i.hasNext(); ) {
         BindingOperation bop = i.next();
         String binName = (bop.getBindingInput() == null) ? 
            null : 
            bop.getBindingInput().getName();
         String boutName = (bop.getBindingOutput() == null) ?
            null : 
            bop.getBindingOutput().getName();
         if ((inName == null) ? binName == null : inName.equalsIgnoreCase(binName)) {
         	boolean outNamesMatch = true;
         	if (outName == null || outName.length() < 1) {
				outNamesMatch = (boutName == null || boutName.length() < 1);
         	} else {
         		outNamesMatch= outName.equalsIgnoreCase(boutName);
         	}
            if (outNamesMatch) {
               if ( choosenOp == null ) {
                  choosenOp = bop;
               } else {
                  throw new WSDLParserException( 
                     "duplicate operation in binding: " +
                     bop.getName() +
                     ":" + inName +
                     ":" + outName );
               }
            }
         }
      }
      return choosenOp;
   }

	private static boolean isJavaKeyword(String identifier) {
		return JavaUtils.isJavaKeyword(identifier);
        

        //	abstract    default    if            private      this
        //	boolean     do         implements    protected    throw
        //	break       double     import        public       throws
        //	byte        else       instanceof    return       transient
        //	case        extends    int           short        try
        //	catch       final      interface     static       void
        //	char        finally    long          strictfp     volatile
        //	class       float      native        super        while
        //	const       for        new           switch
        //	continue    goto       package       synchronized
        //  null        true       false         assert
    }

    /**
     * Compares two strings taking acount of a wildcard.
     * The first string is compared to the second string taking 
     * account of a wildcard character in the first string. For
     * example, wildcardCompare( "*.ibm.com", "hursley.ibm.com", '*')
     * would return true.
     */
    public static boolean wildcardCompare(String s1, String s2, char wild) {
        if (s1 == null) {
            return false;
        }
        String w = wild + "";
        return cmp(new StringTokenizer(s1, w, true), s2, w);
    }
    
    private static boolean cmp(StringTokenizer st, String s, String wild) {
    	if ( s == null || s.equals( "" ) ) {
    		return !st.hasMoreTokens();
    	}
    	if ( st.hasMoreTokens() ) {
           String s2 = st.nextToken();
           if ( wild.equals( s2 ) ) {
           	  if ( !st.hasMoreTokens() ) { 
           	     return true;   // a trailing wildcard matches anything
           	  }
           	  s2 = st.nextToken();
           	  if ( s.equals( s2 ) ) { 
           	  	 return false;   //  wildcard must be at least 1 character
           	  }
           }
           int i = s.indexOf( s2 );
           if ( i < 0 ) {
              return false;  // prefix not in s
           }
           i += s2.length();
           if ( i < s.length() ) {
              return cmp( st, s.substring( i ), wild );
           } else {
              return cmp( st, "", wild );
           }
    	}
    	return false; // no more tokens but still some s
    }
}
