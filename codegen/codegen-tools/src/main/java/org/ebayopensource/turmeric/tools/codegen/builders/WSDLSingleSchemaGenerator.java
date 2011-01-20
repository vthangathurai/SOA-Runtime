/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class WSDLSingleSchemaGenerator extends BaseCodeGenerator implements SourceGenerator{
private static final String WSDL_GEN_DIR = "META-INF/soa/services/wsdl";
	

	private final String TARGET_NAMESPACE = "targetNamespace";

	private final String TYPES_TAG = "wsdl:types";

	private final String SCHEMA_TAG = "schema";

	private final String IMPORT_TAG = "import";

	private final String NAMESPACE = "namespace";

	private final String XMLnamespace = "http://www.w3.org/2001/XMLSchema";
	
	private static Logger s_logger = LogManager.getInstance(WSDLSingleSchemaGenerator.class);
	
	private static WSDLSingleSchemaGenerator s_wsdlGenerator = new WSDLSingleSchemaGenerator();
	
	private WSDLSingleSchemaGenerator() {}
	
	
	public static WSDLSingleSchemaGenerator getInstance() {
		return s_wsdlGenerator;
	}
	

	@SuppressWarnings("unused")
    private Logger getLogger() {
		return s_logger;
	}
	
	
	public boolean continueOnError() {
		return true;
	}
	
	public void generate(CodeGenContext codeGenCtx)  
			throws CodeGenFailedException  {
//		String metaSrcdestLoc = codeGenCtx.getMetaSrcDestLocation();
//		String svcName = codeGenCtx.getServiceName();
//		String wsdlFilePath =  CodeGenUtil.toOSFilePath(destFolderPath(metaSrcdestLoc, svcName)) +
//		getWSDLFileName(codeGenCtx.getServiceName());
//		parseDocument(codeGenCtx.getInputOptions().getInputFile(), wsdlFilePath);
	}
	
	private void parseDocument(String srcFileName, String destFileName) throws CodeGenFailedException {
		
		// parse the wsdl
		
		Document wsdlDocument = null;
		DocumentBuilderFactory wsdlDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder wsdlDocumentBuilder = wsdlDocumentBuilderFactory.newDocumentBuilder();
			wsdlDocument = wsdlDocumentBuilder.parse(new File(srcFileName));
		} catch (ParserConfigurationException parserConfigurationException) {
			String errMsg = "WSDL parsing failed!";
			s_logger.log(Level.SEVERE, errMsg, parserConfigurationException);
			throw new CodeGenFailedException(errMsg, parserConfigurationException);
		} catch (SAXException saxException) {
			String errMsg = "SAXException!";
			s_logger.log(Level.SEVERE, errMsg, saxException);
			throw new CodeGenFailedException(errMsg, saxException);
		} catch (IOException e) {
			String errMsg = "SAXException!";
			s_logger.log(Level.SEVERE, errMsg, e);
		}
		Element wsdlDefinition = null;
		String wsdlNamespace = null;
		NodeList wsdlTypes = null;
		NamedNodeMap namespacePrefixes = null;
		
		if(wsdlDocument != null){
			wsdlDefinition = wsdlDocument.getDocumentElement();
			wsdlNamespace = wsdlDefinition.getAttribute(TARGET_NAMESPACE);
			wsdlTypes = wsdlDefinition.getElementsByTagName(TYPES_TAG);
			namespacePrefixes = wsdlDefinition.getAttributes();
		}
		
		
		Node targetSingleSchema = null;		
		List<Node> schemaNodes = new ArrayList<Node>(); 		
 		List<Node> importNodes = new ArrayList<Node>();		
 		
 		//getting all the schemas available in the wsdl and populating them into a list
		
		NodeList schemaTypes = null;
		if(wsdlTypes != null && wsdlTypes.getLength() > 0)
			schemaTypes = wsdlTypes.item(0).getChildNodes();
		for (int j = 0; j < schemaTypes.getLength(); j++) {
			if(schemaTypes.item(j).getNodeName().contains(SCHEMA_TAG)){
				schemaNodes.add(schemaTypes.item(j));
			}
		}
		
		List<Node> namespaceNodes = new ArrayList<Node>();
				
		/*
		 * Adding the import tags in each schema into a list
		 */
		List<String> importNamespace = new ArrayList<String>();		
		for (Node schemaNode : schemaNodes) {
			if(schemaNode.getAttributes() != null)
				if(schemaNode.getAttributes().getNamedItem(TARGET_NAMESPACE) != null)
					importNamespace.add(schemaNode.getAttributes().getNamedItem(TARGET_NAMESPACE).getNodeValue());
			
			NamedNodeMap xmlnsNamespace = schemaNode.getAttributes();
			for(int j=0; j < xmlnsNamespace.getLength() ; j++){				
				Node currAttribute = xmlnsNamespace.item(j);
				String prefix = currAttribute.getNodeName();
				if(currAttribute.getNodeValue().equalsIgnoreCase(XMLnamespace) && 
						prefix != null && prefix.indexOf(":") != -1)
					namespaceNodes.add(currAttribute);
			}
			
			NodeList tempImportList = schemaNode.getChildNodes();
			for(int k = 0; k < tempImportList.getLength(); k++){
				if(tempImportList.item(k).getNodeName().contains(IMPORT_TAG)){
					importNodes.add(tempImportList.item(k));
				}
			}
		}		
		
		/*
		 * Creating a map of namespace and their prefix, which is made use of while replacing the prefixes 
		 */
		
		Map<String, String> nameSpacePrefixMap = new HashMap<String, String>();		
		List<String> prefixList = new ArrayList<String>();
		
		for(int j=0; j < namespacePrefixes.getLength() ; j++){
			Node currAttribute = namespacePrefixes.item(j);
			nameSpacePrefixMap.put(currAttribute.getNodeValue(), currAttribute.getNodeName());			
		}
		
		int numberOfImports = 0;
		if(importNodes != null)
			numberOfImports = importNodes.size();
		
		List<Node> requiredImportList = new ArrayList<Node>();
		
		/*
		 * Creating a list of required namespace that are to be imported
		 */
		
		for(int k = 0; k < numberOfImports; k++){
			Node parent = importNodes.get(0).getParentNode();
			if(importNamespace.contains(importNodes.get(0).getAttributes().getNamedItem(NAMESPACE).getNodeValue()))
				parent.removeChild(importNodes.remove(0));
			else{
				requiredImportList.add(importNodes.get(0).cloneNode(true));
				parent.removeChild(importNodes.remove(0));
			}
		}
		
		if (schemaNodes != null && schemaNodes.size() > 0) {
			
			/*
			 * Cloning the schema node which has the same targetnamespace as that of the wsdl
			 */
			
			for (Node schemaNode : schemaNodes) {
				String schemaNamespace = null;
				if(schemaNode.getAttributes().getNamedItem(TARGET_NAMESPACE) != null)
					schemaNamespace = schemaNode.getAttributes().getNamedItem(TARGET_NAMESPACE).getNodeValue();
				if(schemaNamespace != null && wsdlNamespace != null && wsdlNamespace.equalsIgnoreCase(schemaNamespace)){
					targetSingleSchema = schemaNode.cloneNode(true);
					wsdlTypes.item(0).removeChild(schemaNode);
					schemaNodes.remove(schemaNode);
					break;
				}
			}
			/*
			 * Adding the child nodes of each schema to the cloned schema and removing the current schema
			 */
			for (Node schemaNode : schemaNodes) {
				String currSchemaNS = schemaNode.getAttributes().getNamedItem(TARGET_NAMESPACE).getNodeValue();
				NodeList currLeftOverSchema = schemaNode.getChildNodes();
				String prefix = nameSpacePrefixMap.get(currSchemaNS);
				for (int j = 0; j < currLeftOverSchema.getLength(); j++) {
					Node newDescendants = currLeftOverSchema.item(j).cloneNode(true);
					targetSingleSchema.appendChild(newDescendants);
				}
				if(nameSpacePrefixMap.get(currSchemaNS)!= null && !(nameSpacePrefixMap.get(currSchemaNS).equals("xmlns")))
					if(prefix != null && prefix.indexOf(":") != -1)
						prefixList.add(prefix.substring(prefix.indexOf(":") + 1));
				wsdlTypes.item(0).removeChild(schemaNode);
			}			
		}
		
		/*
		 * If each schema uses seperate prefix then that prefix must be added to the wsdl definition as an attribute.
		 */		
		for (Node namespaceNode : namespaceNodes) {
			Attr attributeNode = wsdlDocument.createAttribute(namespaceNode.getNodeName());
			attributeNode.setNodeValue(namespaceNode.getNodeValue());
			if(wsdlDefinition != null)
				wsdlDefinition.setAttributeNode(attributeNode);
		}
		
		if(requiredImportList != null && requiredImportList.size() > 0)
			for (Node node : requiredImportList) {
				targetSingleSchema.insertBefore(node, targetSingleSchema.getFirstChild());
			}
			
		/*
		 * After all the schemas are removed, the cloned schema node is added to wsdl element
		 * and then the prefixes are modified in order not to get any issues and then written into another file 
		 */
		if(wsdlTypes != null && wsdlTypes.getLength() > 0)
			wsdlTypes.item(0).appendChild(targetSingleSchema);		
		writeToFile(wsdlDocument, destFileName);		
		replacePrefix(prefixList, wsdlNamespace, nameSpacePrefixMap, destFileName);
			
	}		
	
	/**
	 * 
	 * @param document
	 * @param fileName
	 * @throws CodeGenFailedException 
	 */
	private void writeToFile(Document document, String fileName) throws CodeGenFailedException{
		/*
		 * Commenting this method .Using restricted apis.
		 * Not being used currently anywhere else.
		 * Should not be used.
		 */
//		OutputFormat outputFormat = new OutputFormat();
//		outputFormat.setIndenting(true);
//		
//		FileWriter fileWriter = null;
//		try {
//			 fileWriter = new FileWriter(fileName);
//			 DOMSerializer wsdlSerializer = new XMLSerializer(fileWriter, outputFormat);
//			 wsdlSerializer.serialize(document);
//		}catch (Exception exception) {
//			String errMsg = "Unknown Exception!";
//			s_logger.log(Level.SEVERE, errMsg, exception);
//			throw new CodeGenFailedException(errMsg, exception);
//		}finally{
// 			CodeGenUtil.closeQuietly(fileWriter);
//		}
	}
	
	/**
	 * 
	 * @param prefixList
	 * @param wsdlNamespace
	 * @param nameSpacePrefixMap
	 * @param fileName
	 * @throws CodeGenFailedException 
	 */
	private void replacePrefix(List<String> prefixList, String wsdlNamespace, 
			Map<String, String> nameSpacePrefixMap, String fileName) throws CodeGenFailedException{
		BufferedReader fileReader = null;
		FileWriter fileWriter = null;
		StringBuilder wsdlString = new StringBuilder();
		String newPrefix = nameSpacePrefixMap.get(wsdlNamespace).substring(nameSpacePrefixMap.get(wsdlNamespace).indexOf(":") + 1)+":";
		try{
			fileReader = new BufferedReader(new FileReader(fileName));
			String currLine;
			while((currLine = fileReader.readLine())!= null){							
				for (String currPrefix : prefixList)
					currLine = currLine.replaceAll(currPrefix+":", newPrefix);
				wsdlString.append(currLine).append("\n");
				}
			fileWriter = new FileWriter(fileName);
			fileWriter.write(wsdlString.toString());
		}catch (IOException exception) {
			String errMsg = "IOException!";
			s_logger.log(Level.SEVERE, errMsg, exception);
			throw new CodeGenFailedException(errMsg, exception);
		}finally{
			CodeGenUtil.closeQuietly(fileReader);
			CodeGenUtil.closeQuietly(fileWriter);
		}
	}
	
	private String getWSDLFileName(String svcName) {
		return svcName + "SNS.wsdl";

	}

	
	private String destFolderPath(String destLoc, String serviceName)
			throws CodeGenFailedException {

		String destFolderPath = CodeGenUtil.genDestFolderPath(destLoc,
				serviceName, WSDL_GEN_DIR);

		try {
			CodeGenUtil.createDir(destFolderPath);
		} catch (IOException ioEx) {
			throw new CodeGenFailedException(ioEx.getMessage(), ioEx);
		}

		return destFolderPath;

	}
	
	public String getFilePath(String serviceAdminName, String interfaceName) {

		String filePath = CodeGenUtil.toOSFilePath(WSDL_GEN_DIR) + serviceAdminName + File.separatorChar +serviceAdminName + ".wsdl" ;
		return filePath;
	}
}
