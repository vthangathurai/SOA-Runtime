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
package org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.validation;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ebayopensource.turmeric.runtime.common.exceptions.SchemaExtractionException;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.validation.util.SchemaExtractorUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.ibm.wsdl.factory.WSDLFactoryImpl;

import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;

/**
 * @author arajmony
 *
 */
public class SchemaExtractor {

	 
	private static final Logger s_logLogger = Logger.getInstance(SchemaExtractor.class.getName());
	private  ClassLoader m_ClassLoader;
	private String m_serviceAdminName;
	private String m_basePathForSchema = "META-INF/soa/services/schema/";
	private String m_wsdlPath;
	private InputSource m_wsdlsInputSource;
	private boolean isENS;
	private static final String XML_URI= "http://www.w3.org/2001/XMLSchema";
	private static final String XMLNS= "xmlns";
	private static final String UPA_REMOVED_PREFIX = "UPA_REMOVED_";
	
	
	Map<String, String> m_tnsAndFileNameMapForNormal = new HashMap<String, String>();
	Map<String, String> m_tnsAndFileNameMapForUPAFree = new HashMap<String, String>();
	Map<String, String> m_currTnsAndFileNameMap= null;
	
	private enum NodeTypeEnum{
		NORMAL, UPA_FREE;
	}
	
	
	public SchemaExtractor(String serviceAdminName){
		m_ClassLoader = Thread.currentThread().getContextClassLoader();
		m_serviceAdminName = serviceAdminName;
		m_basePathForSchema += m_serviceAdminName + "/";
		
		File file = new File(m_basePathForSchema);
		boolean mkdirStatus = file.mkdirs();
		
		if(!mkdirStatus)
			s_logLogger.log(LogLevel.WARN,"status of mkdir im schemaextractor for service : " + m_serviceAdminName+  " is "+ mkdirStatus);
	}
	
	
	
	public SchemaBaseDetails getMasterSchemaFilesDetails  ()
		throws SchemaExtractionException{
		
		SchemaBaseDetails schemaBaseDetails = new SchemaBaseDetails();
		
		m_wsdlPath = getWSDLPathToBeUsed(m_serviceAdminName);
		 		
		Definition wsdlDefinition = getWSDLsDefintion(m_wsdlPath);

		//call the method twice one each for each node type
		buildXMLFilesForSchemas(wsdlDefinition,NodeTypeEnum.NORMAL);
		buildXMLFilesForSchemas(wsdlDefinition,NodeTypeEnum.UPA_FREE);
		
		cleanTheGeneratedUPAFreeSchemas();
		
		
		/*
		 * create the response
		 */
		schemaBaseDetails.setServiceAdminName(m_serviceAdminName);
		schemaBaseDetails.setFilePathForStrictValidation(m_basePathForSchema + UPA_REMOVED_PREFIX + "master.xsd");
		schemaBaseDetails.setIsEnableNSWSDL(isENS);
		
		
		return schemaBaseDetails;
	}
	
	
	
	
	
	
	
	/**
	 * This method is used to clean the genearted UPA schemas. This it does by the following
	 * 
	 *	1. sax parse the WSDL and identify those types which have xs:any. this list should have namespace 
	 *	   aware elements
	 *	2. sax parse the doc and identify if any type depends on the types derived from step # 1.
	 *	3. remove xs:any only from those elements which are in the "being-referred" list
	 * @throws SchemaExtractionException
	 */
	private void cleanTheGeneratedUPAFreeSchemas() throws SchemaExtractionException{
		
		List<QName> listOfTypesHavingXSAny;
		List<QName> listOfInterestedTypesHavingXSAny;
		Set<QName> setOfTypesReferringToXSAnyTypes;
		
		try {
			listOfTypesHavingXSAny = SchemaExtractorUtil.getListOFTypesHavingXSAny(m_wsdlPath);
			
			listOfInterestedTypesHavingXSAny = removeNonSoaFwkBaseTypesFromList(listOfTypesHavingXSAny);
			
			setOfTypesReferringToXSAnyTypes = SchemaExtractorUtil.getListOfTypesReferringTheXSAnyTypes(listOfInterestedTypesHavingXSAny,m_wsdlPath);
			
			if(s_logLogger.isLogEnabled(LogLevel.INFO))
				s_logLogger.log(LogLevel.INFO,"listOfTypesReferringToXSAnyTypes : " + setOfTypesReferringToXSAnyTypes);
			
			Map<String,List<String>> mapOfTNSAndTypeName = new HashMap<String, List<String>>(setOfTypesReferringToXSAnyTypes.size());
			
			for(QName currQName : setOfTypesReferringToXSAnyTypes){
				String ns = currQName.getNamespaceURI();
				String typeName = currQName.getLocalPart();
				
				if(mapOfTNSAndTypeName.get(ns)== null){
					ArrayList<String> list = new ArrayList<String>(3);
					mapOfTNSAndTypeName.put(ns, list);
				}
					
				mapOfTNSAndTypeName.get(ns).add(typeName);
			}
			
			
			//types to be removed from the respective schema have now been identified and they reside inside the 
			// map mapOfTNSAndTypeName, where the key is the namespace and the value is a list of type names
			
			Set<Entry<String, List<String>>> set =    mapOfTNSAndTypeName.entrySet();
			for(Entry<String, List<String>> currEntry : set){
				String currSchemasTNS = currEntry.getKey();
				String fileNameOfSchemaTobeUPAfreed = m_tnsAndFileNameMapForUPAFree.get(currSchemasTNS);
				
				String xsdFileRelativePath = m_basePathForSchema + fileNameOfSchemaTobeUPAfreed;
				
				SchemaExtractorUtil.removeTheTypesFromTheFile(currEntry.getValue(),xsdFileRelativePath);
				
			}
			
			
			
			
		} catch (Exception e) {
			throw new SchemaExtractionException(e.getMessage());
		}
		
	}



	private List<QName> removeNonSoaFwkBaseTypesFromList(
			List<QName> listOfTypesHavingXSAny) {
		List<QName> response = new ArrayList<QName>(2);
		QName baseSvcReq = new QName("http://www.ebayopensource.org/turmeric/common/v1/types","BaseRequest");
		QName baseSvcRes = new QName("http://www.ebayopensource.org/turmeric/common/v1/types","BaseResponse");
		
		for(QName curr : listOfTypesHavingXSAny){
			if(curr.equals(baseSvcReq) || curr.equals(baseSvcRes))
				response.add(curr);
		}
		
		return response;
	}



	private Definition getWSDLsDefintion(String wsdlRelativePath) 
		throws SchemaExtractionException{
		
		WSDLFactory factory = null;
		try {
			factory = WSDLFactoryImpl.newInstance();
		} catch (WSDLException e) {
			String errMsg = "Exception while trying to create WSDL factory : "+ e;
			s_logLogger.log(LogLevel.ERROR, errMsg);
			throw new SchemaExtractionException(errMsg,e);
		}
		
		WSDLReader reader = factory.newWSDLReader();
		
		InputStream inputStream=null;
		inputStream = m_ClassLoader.getResourceAsStream(wsdlRelativePath);
		if(inputStream == null){
			String msg = "InputStream could not be created for WSDL @ " + wsdlRelativePath;
			s_logLogger.log(LogLevel.ERROR, msg);
			throw new SchemaExtractionException(msg);
		}
		
		
		Definition definition = null;
		
		m_wsdlsInputSource = new InputSource(inputStream);
		try {
			 definition = reader.readWSDL(null,m_wsdlsInputSource);
		} catch (WSDLException e) {
			String errMsg = "Exception while trying to create WSDL Definition : "+ e;
			s_logLogger.log(LogLevel.ERROR, errMsg);
			throw new SchemaExtractionException(errMsg,e);
		}
		
		return definition;
	}








	private String getWSDLPathToBeUsed(String serviceAdminName) 
		throws SchemaExtractionException{
		String wsdlRelativePath= "soa/services/wsdl/" + serviceAdminName + "_mns.wsdl";
		
		URL url = m_ClassLoader.getResource(wsdlRelativePath);
		if(url == null){
			s_logLogger.log(LogLevel.WARN, "mns wsdl not found for WSDL with serviceAdminName " + serviceAdminName + ". Trying for normal WSDL...");
			wsdlRelativePath = "META-INF/soa/services/wsdl/"+serviceAdminName+"/" + serviceAdminName + ".wsdl";
			url = m_ClassLoader.getResource(wsdlRelativePath);
			if(url == null){
				String msg = "both normal and mns wsdl not found for WSDL with serviceAdminName " + serviceAdminName;
				s_logLogger.log(LogLevel.ERROR,msg );
				throw new SchemaExtractionException(msg);
			}
		}else
			isENS = true;
		
		if(s_logLogger.isLogEnabled(LogLevel.INFO))
			s_logLogger.log(LogLevel.INFO, "WSDL for service admin name " + serviceAdminName + " found in path " + wsdlRelativePath);
		
		return wsdlRelativePath;
	}


	private void buildXMLFilesForSchemas(Definition wsdlDefinition,NodeTypeEnum nodeType) 
		throws SchemaExtractionException{
		if(nodeType == NodeTypeEnum.NORMAL)
			m_currTnsAndFileNameMap = m_tnsAndFileNameMapForNormal;
		else
			m_currTnsAndFileNameMap = m_tnsAndFileNameMapForUPAFree;
		
		
		Map<String, String> globalPrefixNSMap = wsdlDefinition.getNamespaces();

		Types types = wsdlDefinition.getTypes();

		List<Object> listOfElements = types.getExtensibilityElements();

		if(s_logLogger.isLogEnabled(LogLevel.INFO))
			s_logLogger.log(LogLevel.INFO, "# of schemas = " + listOfElements.size());
		

		/*
		 * get the target namespace of each of the schemas
		 */
		List<String> allTNSList = new ArrayList<String>();
		for (int k = 0; k < listOfElements.size(); k++) {
			Schema schema = (Schema) listOfElements.get(k);
			Element element = schema.getElement();
			Attr attribute = element.getAttributeNode("targetNamespace");
			allTNSList.add(attribute.getNodeValue());
		}
		
		
		for(int k = 0 ; k< allTNSList.size() ; k++){
			String fileName = "schema_" + k + ".xsd";
			
			if(nodeType == NodeTypeEnum.UPA_FREE)
				 fileName = UPA_REMOVED_PREFIX + fileName;
				
			m_currTnsAndFileNameMap.put(allTNSList.get(k), fileName);	
		}
		
		if(s_logLogger.isLogEnabled(LogLevel.INFO))
			s_logLogger.log(LogLevel.INFO, "Target namespaces are " + allTNSList);
		
		
		
		/*
		 * Creating one file each for each schema
		 */
		for (int k = 0; k < listOfElements.size(); k++) {
			
			Schema schema = (Schema) listOfElements.get(k);
			Element element = schema.getElement();
			
			Attr attribute = element.getAttributeNode("targetNamespace");
			String currTNS = attribute.getNodeValue();
			
			if(s_logLogger.isLogEnabled(LogLevel.INFO))
				s_logLogger.log(LogLevel.INFO, "processing schema section with target namespace as " + currTNS);
			
			/*
			 * get all attributes in the current schema element
			 */
			NamedNodeMap schemaAttributes = element.getAttributes();
			Map<String, String> currSchemasAttrMap = new HashMap<String, String>();
			for(int l = 0 ; l < schemaAttributes.getLength(); l++){
				Node node = schemaAttributes.item(l);
				currSchemasAttrMap.put(node.getNodeName(),node.getNodeValue());
			}
			
			
			/*
			 * identify the attributes/prefixes which are defined in wsdl:definition but not in the schema.
			 * And then add the delta prefix-ns mappings to the new schema node
			 */
			HashMap<String, String> additonalAttrs= new HashMap<String, String>();
			Set<Entry<String, String>> entrySet =  globalPrefixNSMap.entrySet();
			for(Entry<String, String> currEntry : entrySet){
				if(currSchemasAttrMap.containsKey(currEntry.getKey())){
					s_logLogger.log(LogLevel.FINE, "attribute already present in local schema so not overwriting  : "+ currEntry.getKey());
				}else{
					additonalAttrs.put(currEntry.getKey(), currEntry.getValue());
				}
			}
			
			
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
				s_logLogger.log(LogLevel.ERROR, msg);
				throw new SchemaExtractionException(msg,e);
			}
			// create an instance of DOM
			Document document = db.newDocument();
			
			//clone the node
			Node clonedNode = document.importNode(element,true);
			
			
			//add the proper prefixes and additional attributes in the schema element
			addAdditionalPrefixesToClonedNode(clonedNode,additonalAttrs);
			
			
			//add all the import stmts to the cloned schema element
			addAdditionalImportStmtsToClonedNode(clonedNode,currTNS,m_currTnsAndFileNameMap,document,false);
			
			
			//append the schema element to the document 
			document.appendChild(clonedNode);
			
			
			/*
			 * Schema node is now created and stand alone. so lets write it to a file
			 */
			File fileOutput = new File(m_basePathForSchema + m_currTnsAndFileNameMap.get(currTNS));
			if(s_logLogger.isLogEnabled(LogLevel.INFO))
				s_logLogger.log(LogLevel.INFO, "schema file path : " + fileOutput.getAbsolutePath());
			
			SchemaExtractorUtil.writeSchemaDocumentToFile(document,fileOutput);
			
			
		}
		
		/*
		 * creating the master_schema file
		 */
		
		createMasterSchemaFile(m_currTnsAndFileNameMap,nodeType);
		
		
	}






	private void createMasterSchemaFile(Map<String, String> tnsAndFileNameMap, NodeTypeEnum nodeType) 
	throws SchemaExtractionException{
		
		if(s_logLogger.isLogEnabled(LogLevel.INFO))
			s_logLogger.log(LogLevel.INFO, "creating the master schema file : " + tnsAndFileNameMap);
		
		String tns = null;
		String ouptutFilePath = null;
		
		if(nodeType == NodeTypeEnum.NORMAL){
			tns = "http://www.ebay.com/marketplace/schema/validation/" +m_serviceAdminName;
			ouptutFilePath = m_basePathForSchema + "master.xsd";
		}
		else{
			tns = "http://www.ebay.com/marketplace/schema/validation/upa/free/" +m_serviceAdminName;
			ouptutFilePath = m_basePathForSchema + UPA_REMOVED_PREFIX + "master.xsd";
		}
		
		
		/*
		 * creating a new master schema file
		 */
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// get an instance of builder
		DocumentBuilder db=null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			String msg = "Parser configuration exception : " + e;
			s_logLogger.log(LogLevel.ERROR, msg);
			throw new SchemaExtractionException(msg,e);
		}
		// create an instance of DOM
		Document document = db.newDocument();

		

				
		
		Element root = document.createElement("schema");
		root.setAttribute("targetNamespace", tns );
		document.appendChild(root);
		root.setAttribute("xmlns", XML_URI);
		
		
		// create comment
		Comment commentNode = document.createComment("Generated File. .... ");
		root.appendChild(commentNode);
		
		addAdditionalImportStmtsToClonedNode(root,null,tnsAndFileNameMap,document,true);

		
		/*
		 * Schema node is now created and stand alone. so lets write it to a file
		 */
		File fileOutput = new File(ouptutFilePath);
		if(s_logLogger.isLogEnabled(LogLevel.INFO))
			s_logLogger.log(LogLevel.INFO, "schema file path : " + fileOutput.getAbsolutePath());
		
		SchemaExtractorUtil.writeSchemaDocumentToFile(document,fileOutput);
		
		
	}



	


	



	/**
	 * 
	 * @param clonedNode
	 * @param currTNS				THIS CAN BE NULL see the current callers
	 * @param tnsAndFileNameMap
	 * @param document
	 * @param addAll   - this is redundant , the same is already achieved by currTNs in a wy, but still to retain this as it is more clear
	 */
	private void addAdditionalImportStmtsToClonedNode(Node clonedNode,
			String currTNS, Map<String, String> tnsAndFileNameMap, Document document, boolean addAll) {
		
		// identify the first child since the import stmts should be added as the first childs
		Node firstChildNode  = clonedNode.getFirstChild();
		

		Set<Entry<String, String>> set = tnsAndFileNameMap.entrySet();
		
		for(Entry<String, String> setEntry : set){
			if((!setEntry.getKey().equals(currTNS)) || addAll){
				Element importElement = null;
				
				importElement = document.createElementNS(XML_URI,"import");
				importElement.setAttribute("namespace", setEntry.getKey());
				importElement.setAttribute("schemaLocation", setEntry.getValue());
				clonedNode.insertBefore(importElement, firstChildNode);
				//clonedNode.appendChild(importElement);
			}
		}
		
	}



	private void addAdditionalPrefixesToClonedNode(
			Node clonedNode, HashMap<String, String> additonalAttrs) {
		
		
		if(s_logLogger.isLogEnabled(LogLevel.INFO))
			s_logLogger.log(LogLevel.INFO, "additional prefixes to be added");

		Element element = (Element) clonedNode;
		Set<Entry<String, String>> entrySet =  additonalAttrs.entrySet();
		for(Entry<String, String> currEntry : entrySet){
			if(currEntry.getKey() != null && !currEntry.getKey().trim().equals(""))
				element.setAttribute(XMLNS + ":" + currEntry.getKey(), currEntry.getValue());
		}
		
		/*
		 * the xmlns attribute 
		 *   1. should always be there in a schema
		 *   2. should always point to http://www.w3.org/2001/XMLSchema
		 */
		
		element.setAttribute(XMLNS, XML_URI);
		
	}



}
