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
package org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.validation.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.ebayopensource.turmeric.runtime.common.exceptions.SchemaExtractionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;


/**
 * @author arajmony
 *
 */
public class SchemaExtractorUtil {

	private static final Logger s_logger = Logger.getInstance(SchemaExtractorUtil.class.getName());
	

	
	/**
	 * Given the WSDL's relative path, this method returns a list of QNames of those types 
	 * which have a xs:any element inside it.
	 * @param inputSource
	 * @return
	 * @throws Exception
	 */
	public static List<QName> getListOFTypesHavingXSAny(String wsdlRelativePath) 
	throws Exception{
		
		List<QName> listOfQNames = new ArrayList<QName>();
		
		class MyHandler extends DefaultHandler{
			
			private List<QName> refListOfQNames;
			private String currTNS;
			private String currTypeName;
			private String lastElementName;
			
			public MyHandler(List<QName> list) {
				refListOfQNames = list; 
				
			}
			
			 @Override
	            public void startElement(String uri, String localName,
	                    String qName, Attributes attributes) throws SAXException {
	                String elementName = ("".equals(localName)) ? qName : localName;

	                if (elementName.equals("schema")
	                        || elementName.contains(":schema")) {
	                	currTNS = attributes.getValue("targetNamespace");
	                	
	                }

	                //search for element's name also. This will come in handy for anonymous complex types. 
	                if (elementName.equals("element")
	                        || elementName.contains(":element")) {
	                	lastElementName = attributes.getValue("name");
	                }
	                
	                
	                if (elementName.equals("complexType")
	                        || elementName.contains(":complexType")) {
	                	currTypeName = attributes.getValue("name");
	                	
	                	//support for anonymous types
	                	if(currTypeName == null)
	                		currTypeName = lastElementName;
	                }
	                
	                if (elementName.equals("any")
	                        || elementName.contains(":any")) {
	                	QName currTypesQName = new QName(currTNS,currTypeName);
	                	refListOfQNames.add(currTypesQName);
	                }
	                
	            }
			
		}
		
		
		
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			
			InputStream inputStream=null;
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(wsdlRelativePath);
			if(inputStream == null){
				String msg = "InputStream could not be created for WSDL @ " + wsdlRelativePath;
				s_logger.log(LogLevel.ERROR, msg);
				throw new SchemaExtractionException(msg);
			}
			
			parser.parse(inputStream, new MyHandler(listOfQNames));
			
			
			
		} catch (ParserConfigurationException e) {
			String msg = "ParserConfigurationException at getListOFTypesHavingXSAny : " + e;
			s_logger.log(LogLevel.ERROR, msg);
			throw new SchemaExtractionException(msg,e);
		} catch (SAXException e) {
			String msg = "SAXException at getListOFTypesHavingXSAny : " + e;
			s_logger.log(LogLevel.ERROR, msg);
			throw new SchemaExtractionException(msg,e);
		}
		
		if(s_logger.isLogEnabled(LogLevel.INFO))
			s_logger.log(LogLevel.INFO, "list of types having xs:any for WSDL : " + wsdlRelativePath + 
				"  is " + listOfQNames);
		
		return listOfQNames;
	}

	
	
	
	/**
	 * Given the WSDLs relative path and the list of QNames of the types which has xs:any this method would return
	 * a set of QNames of those types which cause UPA issue bcos of xs:any.This is identified by the logic 
	 *   1. If a type using xs:any is being extended by some other type or being referred as a composite element, then 
	 *   	UPA is true for this case. 
	 * @param listOFTypesHavingXSAny
	 * @param wsdlRelativePath
	 * @return
	 */
	public static Set<QName> getListOfTypesReferringTheXSAnyTypes(List<QName> listOFTypesHavingXSAny,String wsdlRelativePath)
		throws Exception{
		Set<QName> setOfQNames = new HashSet<QName>();
		Map<String, QName> mapOfNamesAndQNames = new HashMap<String, QName>();
		
		
		/*
		 * get the local name only from the list of QNames as 
		 *  1. it is easier while parsing the xml doc to identify such elements
		 *  2. it is also legal, since as of today (SOA 2.7) the fwk does not support two types of the same name 
		 *  	even if they belong to a different namespace //TODO correct this once this stmt # 2 becomes invalid.
		 */
		List<String> onlyTypeNamesOfTypesHavingXSAny = new ArrayList<String>(listOFTypesHavingXSAny.size());
		for(QName currQName : listOFTypesHavingXSAny){
			onlyTypeNamesOfTypesHavingXSAny.add(currQName.getLocalPart());
			mapOfNamesAndQNames.put(currQName.getLocalPart(), currQName);
		}
		
		
		
		class MyHandler extends DefaultHandler{
			Set<QName> refQNameSet;
			Map<String, QName> refMap;
			private String currTypeName;
			private String lastElementName;
			
			MyHandler(Set<QName> arg1,Map<String, QName> arg2){
				refQNameSet = arg1;
				refMap = arg2;
				
			}
			
			 @Override
	            public void startElement(String uri, String localName,
	                    String qName, Attributes attributes) throws SAXException {
	                String elementName = ("".equals(localName)) ? qName : localName;
  
	                
	                //search for element's name also. This will come in handy for anonymous complex types. 
	                if (elementName.equals("element")
	                        || elementName.contains(":element")) {
	                	lastElementName = attributes.getValue("name");//this is recorded for supporting anonymous types
	                	
	                	String referredTypeName = attributes.getValue("type");
	                	if(referredTypeName == null)
	                		return;
	                		
	                	int index = referredTypeName.indexOf(":");
	                	if(index > 0){
	                		referredTypeName = referredTypeName.substring(index+1);
	                	}
	                	
	                	if(refMap.get(referredTypeName) != null){
	                		refQNameSet.add(refMap.get(referredTypeName));
	                	}
	                }
	                
	                
	                if (elementName.equals("complexType")
	                        || elementName.contains(":complexType")) {
	                	if(attributes != null)
	                		currTypeName = attributes.getValue("name");
	                	
	                	//support for anonymous types
	                	if(currTypeName == null)
	                		currTypeName = lastElementName;
	                }
	                
	                
	                //<xs:extension base="tns:BaseServiceRequest">
	                
	                if (elementName.equals("extension")
	                        || elementName.contains(":extension")) {
	                	
	                	String base = attributes.getValue("base");
	                	int index = base.indexOf(":");
	                	if(index > 0){
	                		base = base.substring(index+1);
	                	}
	                	
	                	if(refMap.get(base) != null){
	                		refQNameSet.add(refMap.get(base));
	                	}
	                }
	                
	                
	            }
			
		}
		
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			
			InputStream inputStream=null;
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(wsdlRelativePath);
			if(inputStream == null){
				String msg = "InputStream could not be created for WSDL @ " + wsdlRelativePath;
				s_logger.log(LogLevel.ERROR, msg);
				throw new SchemaExtractionException(msg);
			}
			
			parser.parse(inputStream, new MyHandler(setOfQNames,mapOfNamesAndQNames));
			
			
			
		} catch (ParserConfigurationException e) {
			String msg = "ParserConfigurationException at getListOFTypesHavingXSAny : " + e;
			s_logger.log(LogLevel.ERROR, msg);
			throw new SchemaExtractionException(msg,e);
		} catch (SAXException e) {
			String msg = "SAXException at getListOFTypesHavingXSAny : " + e;
			s_logger.log(LogLevel.ERROR, msg);
			throw new SchemaExtractionException(msg,e);
		}
		
		if(s_logger.isLogEnabled(LogLevel.INFO))
			s_logger.log(LogLevel.INFO, "list of types referring to types having xs:any for WSDL : " + wsdlRelativePath + 
				"  is " + setOfQNames);
		
		return setOfQNames;

	}




	
	
	
	
	public static void removeTheTypesFromTheFile(List<String> value,
			String xsdFileRelativePath) throws Exception {
		
		/*
		 * creating a new schema file
		 */
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// get an instance of builder
		DocumentBuilder db=null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			String msg = "Parser configuration exception : " + e;
			s_logger.log(LogLevel.ERROR, msg);
			throw new SchemaExtractionException(msg,e);
		}
		// create an instance of DOM from the schema file
		Document document = db.parse(xsdFileRelativePath);
		
		NodeList nodeList = document.getChildNodes();
		Element schemaElement = null;
		
		
		for(int i=0; i < nodeList.getLength(); i++){
			Element currElement = (Element)nodeList.item(i); 
			String nodeName = currElement.getNodeName();
			if(nodeName.equals("schema") || nodeName.endsWith(":schema")){
				schemaElement = currElement;
				break;
			}
		}
		
		boolean nodeDeleted = false;
		
		if(schemaElement != null){
			NodeList childNodesOfSchemaElement = schemaElement.getChildNodes();
		
			for(int i=0; i < childNodesOfSchemaElement.getLength() ; i++){
				Node currNode = childNodesOfSchemaElement.item(i);
				
				if( ! (nodeNameEquals("complexType",currNode) || nodeNameEquals("element",currNode))){
					continue;
				}
				
				//find the name of the complex type or element, iff the name matches with one of those in the input param value
				//the code should try and remove the xs:any from such types/elements(anonymous types) only.
				NamedNodeMap attributes = currNode.getAttributes();
				if(attributes == null)
					continue;
				
				String typeOrAnyonymousElementName = attributes.getNamedItem("name").getNodeValue();
				if(value.contains(typeOrAnyonymousElementName)){
					removeXSAnyNodeFromThisNode(currNode);
					nodeDeleted = true;
				}
			}
		}
		
		
		/*
		 * re-write the file, since xs:any if present would have been removed
		 * but do it only if file has been modified
		 */
		if(nodeDeleted){
			if(s_logger.isLogEnabled(LogLevel.INFO))
				s_logger.log(LogLevel.INFO, "deletable xs:any was found and removed from " + xsdFileRelativePath);
			
			File outputFile = new File(xsdFileRelativePath);
			writeSchemaDocumentToFile(document,outputFile);
		}
		
	}



	
	public static boolean nodeNameEquals(String nameToMatch, Node currNode) {
		if(currNode == null)
			return false; 
		
		String nodeName = currNode.getNodeName();
		if(nodeName == null)
			return false;
		
		if(nodeName.equals(nameToMatch) || nodeName.endsWith(":"+nameToMatch))
			return true;
		
		
		return false;
	}




	private static void removeXSAnyNodeFromThisNode(
			Node currNode) {
		
		Node parentNode = currNode.getParentNode();
		String nodeName = currNode.getNodeName();
		
		if(nodeName.equals("any") || nodeName.endsWith(":any")){
			parentNode.removeChild(currNode);
			s_logger.log(LogLevel.FINE, "removed xs:any from node "+ nodeName);
			
		}else{
			NodeList nodesChNodeList = currNode.getChildNodes();
			if(nodesChNodeList != null){
				for(int i =0; i<nodesChNodeList.getLength() ; i++){
					removeXSAnyNodeFromThisNode(nodesChNodeList.item(i));
				}
			}
		}
		
		
	}
	
	
	public static void writeSchemaDocumentToFile(Document document, File fileOutput) throws SchemaExtractionException{

		
		
		FileOutputStream output = null;
		try {
			 output = new FileOutputStream(fileOutput);
		} catch (FileNotFoundException e) {
			closeOutputStreamSilently(output);
			String msg = "Error while trying to write to schema file : " + fileOutput.getAbsolutePath();
			s_logger.log(LogLevel.ERROR,msg );
			throw new SchemaExtractionException(msg,e);
		}

		 // Use a Transformer for output
		TransformerFactory tFactory =  TransformerFactory.newInstance();
		Transformer transformer = null;
		
		try {
			transformer = tFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			closeOutputStreamSilently(output);
			String msg = "Error while trying to create a transformer : "+ e;
			s_logger.log(LogLevel.ERROR,msg );
			throw new SchemaExtractionException(msg,e);
		}


		
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(output);
		try {
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
		} catch (TransformerException e) {
			String msg = "Error while trying to transform the schema to a file  : " + e;
			s_logger.log(LogLevel.ERROR,msg );
			throw new SchemaExtractionException(msg,e);
		}finally{
			closeOutputStreamSilently(output);
		}
		
		if(s_logger.isLogEnabled(LogLevel.INFO))
			s_logger.log(LogLevel.INFO, "file " + fileOutput.getAbsolutePath() + "  successfully written ");
		
	}

	
	
	public static void closeOutputStreamSilently(FileOutputStream output) {
		
		try {
			if(output != null)
				output.close();
		} catch (IOException e) {
			s_logger.log(LogLevel.WARN, "could not close output stream " + output);
		}
		
	}
	
}
