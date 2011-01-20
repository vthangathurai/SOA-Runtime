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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ebayopensource.turmeric.runtime.common.impl.internal.config.DomParseUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.external.WSDLUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.library.TypeLibraryConstants;
import org.ebayopensource.turmeric.tools.library.utils.AdditionalXSDInformation;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class EpisodeGenerator {

	private static CallTrackingLogger logger = LogManager
			.getInstance(EpisodeGenerator.class);

	private static CallTrackingLogger getLogger() {
		return logger;
	}

	public EpisodeGenerator() {
	}

	public static String getJavaClassName(String episodeFilePath) {

		Element topLevel = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document result = null;
		String javaClassName = null;

		// TODO: schema validation of the XML file against the
		// TypeInformation.xsd
		try {

			DocumentBuilder builder = factory.newDocumentBuilder();
			result = builder.parse(episodeFilePath);
			topLevel = result.getDocumentElement();
			// System.out.println("Top Level Element = " + topLevel);
			NodeList bindings = DomParseUtils.getImmediateChildrenByTagName(
					topLevel, "bindings");
			for (int i = 0; i < bindings.getLength(); i++) {
				Element bindingsElement = (Element) bindings.item(i);
				String bindingsNodeName = bindingsElement.getNodeName();
				// System.out.println("Child Element = " + bindingsElement);
				if (bindingsNodeName.equals("bindings")) {
					NodeList bindingsChildList = DomParseUtils
							.getImmediateChildrenByTagName(bindingsElement,
									"bindings");
					for (int j = 0; j < bindingsChildList.getLength(); j++) {
						Element bindingsChildElement = (Element) bindingsChildList
								.item(j);
						String bindingsChildNode = bindingsChildElement
								.getNodeName();
						// System.out.println("Child Element = " + topLevel);
						if (bindingsNodeName.equals("bindings")) {
							NodeList classList = DomParseUtils
									.getImmediateChildrenByTagName(
											bindingsChildElement, "class");
							for (int k = 0; k < classList.getLength(); k++) {
								Element classElement = (Element) classList
										.item(j);
								String classNode = classElement.getNodeName();
								javaClassName = classElement
										.getAttribute("ref");
								// System.out.println("Class Node = " +
								// classNode);
							}
						}
					}
				}
			}
		} catch (Exception e) {

			getLogger().log(Level.SEVERE, e.getMessage());
			// throw e;
		}
		return javaClassName;
	}

	public static void genSunJaxbEpisodeFile(String epsiodeFileLocation,
			List<String> xsdTypes, String sunJaxbEpisodeSrcpath, Map<String, String> typesVersion,
			ArrayList<AdditionalXSDInformation> simpleTypesAdditionalXSDInfo) {
		getLogger().entering();
		HashSet nameSpaceList = new HashSet();
		HashSet<String> typesAddedInMasterEpisode = new HashSet<String>();
		Document dom = null;
		boolean headerBindingAdded = false;
		Element bindingsElement = null;
		

		// remove .xsd from the type names
		for (int i = 0; i < xsdTypes.size(); i++) {
			String temp = xsdTypes.get(i);
			temp = temp.substring(0, temp.indexOf("."));
			xsdTypes.set(i, temp);
		}

		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// create an instance of DOM
			dom = db.newDocument();
			// bindings root element
			Element root = dom.createElement("bindings");
			root.setAttribute("version", "2.1");
			root.setAttribute("xmlns", "http://java.sun.com/xml/ns/jaxb");
			dom.appendChild(root);

			// create comment
			Comment commentNode = dom
					.createComment("Generated File. Any changes will lost upon regeneration");
			root.appendChild(commentNode);
			commentNode = dom.createComment(TypeLibraryConstants.MASTER_EPISODE_TURMERIC_START_COMMNENT);
			root.appendChild(commentNode);
			

			// Parsing DOM
			DocumentBuilderFactory dbfForParser = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilderForParser = dbfForParser.newDocumentBuilder();
			
			String[] episodeFiles = TypeLibraryUtilities.getFilesInDir(
					epsiodeFileLocation, ".episode");
			for (int k = 0; k < episodeFiles.length; k++) {
				String episodeFileName = episodeFiles[k];
				String episodeSrc = epsiodeFileLocation + File.separator
						+ episodeFileName;

				Document doc = null;
				FileInputStream inputStream = null;
				
				try{
					inputStream = new FileInputStream(episodeSrc);
					doc = documentBuilderForParser.parse(inputStream);
				}finally{
					CodeGenUtil.closeQuietly(inputStream);
				}
				
				// NodeList schemas = doc.getElementsByTagName("bindings");
				NodeList rootNode = doc.getDocumentElement().getChildNodes();
				// System.out.println("Child Nodes for the bindings element = "
				// + doc.getDocumentElement().getChildNodes());

				for (int j = 0; j < rootNode.getLength(); j++) {
					Node childNode = rootNode.item(j);
					String nameSpaceDoc = "";
					String childNodeName = childNode.getNodeName();

					if (childNodeName.indexOf("bindings") >= 0) {
						NodeList childNodeL1 = childNode.getChildNodes();
						// childNodeL1.getAttributes().getNamedItem("scd").getNodeValue()

						nameSpaceDoc = childNode.getAttributes().getNamedItem(
								"xmlns:tns").getNodeValue();
						if (!nameSpaceList.contains(nameSpaceDoc)) {
							// Add the namespace to the hash set
							nameSpaceList.add(nameSpaceDoc);
							headerBindingAdded = true;
							
							// create bindings element
							bindingsElement = dom.createElement("bindings");
							bindingsElement
									.setAttribute("scd", "x-schema::tns");
							bindingsElement.setAttribute("xmlns:tns",
									nameSpaceDoc);
							root.appendChild(bindingsElement);

							// create schemaBindings element
							Element schemaBindingsElement = dom
									.createElement("schemaBindings");
							schemaBindingsElement.setAttribute("map", "true");
							bindingsElement.appendChild(schemaBindingsElement);

							addChild(childNodeL1, bindingsElement, dom,
									 typesAddedInMasterEpisode,
									xsdTypes,typesVersion);

						} else {
							Node parentNode = getParentNode(dom, nameSpaceDoc);
							addChild(childNodeL1, parentNode, dom,
									 typesAddedInMasterEpisode,
									xsdTypes,typesVersion);
						}

					}
				}

			}
			
			// This a code tweak to add the binding for simple types , since XJC does not generate the binding for simple types even 
			// when it creates the corresponding java type.
			boolean areBindingsToBeAddedManuallyForSimpleTypes = false;
			if (simpleTypesAdditionalXSDInfo != null && simpleTypesAdditionalXSDInfo.size() > 0){
				for(AdditionalXSDInformation simpleInformation : simpleTypesAdditionalXSDInfo){
					if(simpleInformation.isJavaFileGenerated()){
						areBindingsToBeAddedManuallyForSimpleTypes = true;
					}
				}
			}
			
			
			if(areBindingsToBeAddedManuallyForSimpleTypes){
				String nameSpace = simpleTypesAdditionalXSDInfo.get(0).getTargetNamespace();
				
				if(!headerBindingAdded){
					// create bindings element
					
					bindingsElement = dom.createElement("bindings");
					bindingsElement
							.setAttribute("scd", "x-schema::tns");
					bindingsElement.setAttribute("xmlns:tns",
							nameSpace);
					root.appendChild(bindingsElement);

					// create schemaBindings element
					Element schemaBindingsElement = dom
							.createElement("schemaBindings");
					schemaBindingsElement.setAttribute("map", "true");
					bindingsElement.appendChild(schemaBindingsElement);
				}
				
				
				for(int i=0;i<simpleTypesAdditionalXSDInfo.size() ;i++){
					AdditionalXSDInformation currAdditionalXSDInformation = simpleTypesAdditionalXSDInfo.get(i);
					if(!currAdditionalXSDInformation.isJavaFileGenerated())
						continue;
					
					Element individualBindElement = null;
					individualBindElement = dom.createElement("bindings");
					individualBindElement.setAttribute("if-exists", "true");
					individualBindElement.setAttribute("scd", "~tns:" + currAdditionalXSDInformation.getTypeName());
					bindingsElement.appendChild(individualBindElement);
					
					Element classElement = dom.createElement("class");
					classElement.setAttribute("ref",WSDLUtil.getPackageFromNamespace(currAdditionalXSDInformation.getTargetNamespace()) + "." + currAdditionalXSDInformation.getTypeName());
					individualBindElement.appendChild(classElement);
					
				}
				

				
			}
	
			
			commentNode = dom.createComment(TypeLibraryConstants.MASTER_EPISODE_TURMERIC_END_COMMNENT);
			root.appendChild(commentNode);

			TransformerFactory transferFact = TransformerFactory.newInstance();
			Transformer transformer = null;
			try {
				transformer = transferFact.newTransformer();
				// Bug in java5 transformer, indentation does not work.
				// refer to
				// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6296446
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

			} catch (TransformerConfigurationException e) {
				getLogger().log(Level.SEVERE, e.getMessageAndLocation());
				throw new CodeGenFailedException(e.getMessage());
			}
			//following lines are commented due to use of internal classes.
			//not permitted 
			// OutputFormat outputOptions = new OutputFormat();
			// outputOptions.setIndenting(true);
			// outputOptions.setStandalone(true);
			// Writer output = new BufferedWriter(new FileWriter(fileOutput));
			// DOMSerializer serializer = new XMLSerializer(output,
			// outputOptions);
			// serializer.serialize(dom);
			File fileOutput = new File(sunJaxbEpisodeSrcpath + File.separator
					+ "sun-jaxb.episode");
			DOMSource sourcewsdl = new DOMSource(dom);
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(fileOutput);
				StreamResult newWsdl = new StreamResult(output);
				sourcewsdl.setNode(dom);
				transformer.transform(sourcewsdl, newWsdl);
			} finally {
				CodeGenUtil.closeQuietly(output);
			}
			
			getLogger().log(Level.INFO, "sun-jaxb.episode file created successfully.");

		} catch (Exception e) {
			getLogger().log(Level.SEVERE, e.getMessage());
			
		}

		getLogger().exiting();
	}

	private static Node getParentNode(Document dom, String nameSpaceDoc) {
		NodeList childNodes = dom.getDocumentElement().getChildNodes();
		Node parentNode = null;
		for (int i = 0; i < childNodes.getLength(); i++) {
			parentNode = childNodes.item(i);
			String nodeName = childNodes.item(i).getNodeName();
			// System.out.println("Document Child Name = " + nodeName);
			if (nodeName.indexOf("bindings") >= 0) {

				String nameSpaceDom = parentNode.getAttributes().getNamedItem(
						"xmlns:tns").getNodeValue();
				if (nameSpaceDom.equals(nameSpaceDoc)) {
					return parentNode;
				}
			}
		}
		return parentNode;
	}

	private static void addChild(NodeList childNodeL1, Node parentNode,
			Document dom,
			HashSet<String> typesAddedInMasterEpisode, List<String> xsdTypes, Map<String, String> typesVersion) {
		for (int i = childNodeL1.getLength() - 1; i >= 0; i--) {
			Node childNode1 = childNodeL1.item(i);
			String childNodeName1 = childNode1.getNodeName();
			if (childNodeName1.indexOf("bindings") >= 0) {

				Node tempNode = dom.importNode(childNode1, true);
				String xmlTypeName = tempNode.getAttributes().getNamedItem(
						"scd").getNodeValue();
				NamedNodeMap attributes = tempNode.getAttributes();
				Attr attributeToAdd = dom.createAttribute("if-exists");
				attributeToAdd.setNodeValue("true");
				attributes.setNamedItem(attributeToAdd);
				if (typesAddedInMasterEpisode.contains(xmlTypeName))
					continue; // the type is already added to the master
								// episode file, so skip it

				parentNode.appendChild(tempNode);
				typesAddedInMasterEpisode.add(xmlTypeName);
				xmlTypeName = TypeLibraryUtilities.removePrefix(xmlTypeName);

			
			}
		}
	}

}
