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
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author aupadhay This class generates a wsdl at
 *         gen-meta-src/META-INF/soa/services/wsdl/<ServiceName>/<ServiceName>_public.wsdl
 *         with modified serviceName and removes <appinfo> tags
 *         <publicserviceName> is obtained from <service_metadata>.properties
 *         file.
 */
public class WsdlWithPublicServiceGenerator implements SourceGenerator

{

	private static Logger s_logger = LogManager
			.getInstance(WsdlWithPublicServiceGenerator.class);
	private static final String WSDL_SERVICE_TAG = "wsdl:service";
	private static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	private static final String NAME_TAG = "name";
	private static final String PUBLIC_WSDL = "_public.wsdl";
	private static final String WSDL_LOC = "soa\\services\\wsdl\\";
	private static final String GENERATED_WSDL_LOC = CodeGenConstants.META_INF_FOLDER
			+ File.separator + WSDL_LOC;
	private static final String XML_SCHEMA = "schema";
	private static final String APPINFO_TAG = "appinfo";
	private static final String ANNOTATION_TAG = "annotation";
	private static final String WSDL_DEF = "wsdl:definitions";
	private static final String TNS = "targetNamespace";
	private Node m_wsdlDefNode = null;
	private String m_service = null;
	private static WsdlWithPublicServiceGenerator s_wsdlwithPublicService = new WsdlWithPublicServiceGenerator();

	public boolean continueOnError() {

		return true;
	}

	public static WsdlWithPublicServiceGenerator getInstance() {
		return s_wsdlwithPublicService;
	}

	public void generate(CodeGenContext codeGenCtx)
			throws CodeGenFailedException {
		if (codeGenCtx.getInputOptions().getPublicServiceName() == null)
			return;
		s_logger.log(Level.INFO, "BEGIN WsdlWithPublicServiceGenerator()....");
		createNewWsdlwithProperServiceName(codeGenCtx);
		s_logger.log(Level.INFO, "END WsdlWithPublicServiceGenerator()....");

	}

	private void createNewWsdlwithProperServiceName(CodeGenContext codegenCtx)
			throws CodeGenFailedException {

		String serviceName = null;
		m_service = codegenCtx.getInputOptions().getSvcCurrVersion();
		String actualServiceName = codegenCtx.getInputOptions()
				.getServiceAdminName();

		if (codegenCtx.getInputOptions().getPublicServiceName() == null)
			serviceName = codegenCtx.getServiceAdminName();
		else
			serviceName = codegenCtx.getInputOptions().getPublicServiceName();

		if (codegenCtx.getProjectRoot() == null)
			throw new CodeGenFailedException("Project root can not be null..");

		String wsdlPath = codegenCtx.getProjectRoot() + File.separator
				+ CodeGenConstants.GEN_META_SRC_FOLDER + File.separator
				+ GENERATED_WSDL_LOC + actualServiceName + File.separator
				+ actualServiceName + PUBLIC_WSDL;
		wsdlPath = CodeGenUtil.toOSFilePath(wsdlPath);

		try {
			String folderStructurePath = codegenCtx.getProjectRoot()
					+ File.separator + CodeGenConstants.GEN_META_SRC_FOLDER
					+ File.separator + CodeGenConstants.META_INF_FOLDER
					+ File.separator + WSDL_LOC + actualServiceName;
			folderStructurePath = CodeGenUtil
					.toOSFilePath(folderStructurePath);
			CodeGenUtil.createDir(folderStructurePath);
			writeNewWsdl(codegenCtx.getInputOptions().getInputFile(), wsdlPath,
					serviceName);
		} catch (IOException e) {
			s_logger.log(Level.SEVERE,
					"Could not create wsdl with PublicServiceName.....");
			throw new CodeGenFailedException(
					"wsdl with publicServiceName could not be created..");
		}

	}

	/**
	 * This method creates wsdl with modified serviceName at
	 * gen-meta-src/META-INF/soa/services/wsdl/<ServiceName>/<ServiceName>_public.wsdl
	 * 
	 * @param inputFile-
	 *            input Wsdl location
	 * @param wsdlPath -
	 *            modified wsdl Location
	 * @throws CodeGenFailedException
	 * @throws IOException
	 */
	private void writeNewWsdl(String inputFile, String wsdlPath,
			String serviceName) throws CodeGenFailedException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document document = null;

		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(inputFile);
			String prefix = getprefixFortargetNamespace(document);
			NodeList nodeList = document.getElementsByTagName(WSDL_SERVICE_TAG);
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i) instanceof Element) {
					m_wsdlDefNode = nodeList.item(i);
					Element serviceTagElement = (Element) nodeList.item(i);
					serviceTagElement.setAttribute(NAME_TAG, serviceName);
				}
			}
			// Need to remove <appinfo> tags coming from typelibrary in the wsdl

			removeAppinfoTags(document);
			String tagName = prefix + ":" + "version";

			Element versionElement = document.createElement(tagName);
			versionElement.setTextContent(m_service);

			String annotationPrefix = getPrefixForSchema(document);
			String annotationTag = annotationPrefix + ":" + ANNOTATION_TAG;
			Element annotationElement = document.createElement(annotationTag);

			NodeList list = m_wsdlDefNode.getChildNodes();
			//need to insert at right position
			Node portNode =null;
			for(int j=0;j<list.getLength();j++)
			{
				if (list.item(j).getNodeName().contains("port")) {
					portNode = list.item(j);
					break;
				}
			}
			m_wsdlDefNode.insertBefore(annotationElement, portNode);
			String appinfoTag = annotationPrefix + ":" + APPINFO_TAG;
			Element appinfoElement = document.createElement(appinfoTag);

			annotationElement.appendChild(appinfoElement);
			appinfoElement.appendChild(versionElement);

		} catch (Exception e) {
			s_logger.log(Level.SEVERE,
					"Could not modify serviceName in the wsdl...");
			throw new CodeGenFailedException(e.getMessage());
		}
		writeNewwsdl(document, wsdlPath, serviceName);
	}

	private String getprefixFortargetNamespace(Document document) {

		String prefix = null;
		String prefixForTNS = "prefix";
		NodeList nodelist = document.getElementsByTagName(WSDL_DEF);
		String namespace = nodelist.item(0).getAttributes().getNamedItem(TNS)
				.getNodeValue();
		NamedNodeMap nodeMap = nodelist.item(0).getAttributes();
		for (int i = 0; i < nodeMap.getLength(); i++) {
			String value = nodeMap.item(i).getNodeValue();

			if (value.equals(namespace)) {
				if (!nodeMap.item(i).getNodeName().equals(TNS))
					prefix = nodeMap.item(i).getNodeName();
			}
		}
		if (prefix != null)
			prefixForTNS = prefix.substring(prefix.indexOf(':') + 1);

		return prefixForTNS;
	}

	private String getPrefixForSchema(Document document) {

		String prefix = null;
		NodeList childNodes = document.getElementsByTagName("*");
		NamedNodeMap map = null;
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeName().contains(XML_SCHEMA)) {
				Node schemaNode = childNodes.item(i);
				map = schemaNode.getAttributes();
				break;
			}
		}
		if(map != null)
			prefix = getdefinedprefixInsidewsdl(map);
		// in case prefix for XML_SCHEMA is not defined under <schema> section,
		// it must be declared at the root of the wsdl.

		if (prefix == null) {
			map = m_wsdlDefNode.getParentNode().getAttributes();
			prefix = getdefinedprefixInsidewsdl(map);
		}
		return prefix;
	}

	private String getdefinedprefixInsidewsdl(NamedNodeMap map) {
		String prefix = null;
		for (int i = 0; i < map.getLength(); i++) {
			if (map.item(i).getNodeValue().equals(XML_SCHEMA_NAMESPACE))
				prefix = map.item(i).getNodeName();

		}
		return prefix == null ? null : prefix
				.substring(prefix.indexOf(':') + 1);
	}

	private void removeAppinfoTags(Document document)
			throws CodeGenFailedException, DOMException {
		NodeList nodeList = document.getElementsByTagName("*");
		// visit schema section inside the wsdl
		// Wsdl created by plugin having elements from Typelibrary is supposed
		// to have single Namespace.
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeName().contains(XML_SCHEMA)) {
				traverseNode(nodeList.item(i));
				break;
			}
		}
	}

	private void traverseNode(Node currentNode) throws CodeGenFailedException,
			DOMException {
		if (currentNode == null)
			return;
		if (currentNode.getNodeName().contains(APPINFO_TAG)) {
			// need to remove the <appinfo> tags

			Node properParentNode = getproperParentNode(currentNode);
			properParentNode.removeChild(currentNode);

		}
		for (int i = 0; i < currentNode.getChildNodes().getLength(); i++) {
			s_logger.log(Level.FINE, "Calling recursivelyVisitNode()...");
			traverseNode(currentNode.getChildNodes().item(i));
		}

	}

	/**
	 * This method returns the proper parent (complextype or SimpleType)
	 * 
	 * @param node
	 *            --curretnNode in the schema
	 * @return --proper parent
	 */
	private Node getproperParentNode(Node node) {
		s_logger.log(Level.FINE, "BEGIN getproperParentNode()...");
		if (node.getParentNode().getNodeName().contains(ANNOTATION_TAG))
			return node.getParentNode();
		else
			return getproperParentNode(node.getParentNode());
	}

	private void writeNewwsdl(Document document, String wsdlPath,
			String serviceName) throws CodeGenFailedException, IOException {

		TransformerFactory transferFact = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transferFact.newTransformer();
			// Bug in java5 transformer, indentation does not work.
			// refer to
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6296446
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		} catch (TransformerConfigurationException e) {
			s_logger.log(Level.SEVERE,
					"Could not remove <appifo> tags from the wsdl");
			throw new CodeGenFailedException(
					"wsdl with modified publicserviceName could not be created");
		}
		DOMSource sourcewsdl = new DOMSource(document);

		try {

			File file = new File(wsdlPath);
			try {
				boolean created = file.createNewFile();
				if (created) {
					s_logger.log(Level.INFO, serviceName + PUBLIC_WSDL
							+ "created successfully...");
				}
			} catch (IOException e) {
				s_logger.log(Level.INFO,
						"Could not create publicservice wsdl....");
				throw new CodeGenFailedException(e.getMessage());
			}
			FileOutputStream output = new FileOutputStream(file);
			StreamResult newWsdl = new StreamResult(output);
			sourcewsdl.setNode(document);
			transformer.transform(sourcewsdl, newWsdl);
		} catch (FileNotFoundException e) {
			throw new CodeGenFailedException(e.getMessage());
		} catch (TransformerException e) {
			throw new CodeGenFailedException(e.getMessage());
		}
	}

	public String getFilePath(String serviceAdminName, String interfaceName) {

		String wsdlPath = CodeGenConstants.GEN_META_SRC_FOLDER + File.separator
				+ GENERATED_WSDL_LOC + serviceAdminName + File.separator
				+ serviceAdminName + PUBLIC_WSDL;
		return CodeGenUtil.toOSFilePath(wsdlPath);
	}

}
