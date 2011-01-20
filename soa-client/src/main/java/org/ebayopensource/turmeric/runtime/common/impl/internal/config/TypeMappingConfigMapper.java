/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

public class TypeMappingConfigMapper {
	private static final String TRUE = "TRUE";
	private static final String ATTR_PACKAGE_NAME = "name";
	private static final String ATTR_PACKAGE_XML_NS = "xml-namespace";
	private static final String BASIC_XML_TYPE_JAVA_PACKAGE = "java.lang";
	public static void map(String filename, Element topLevel, TypeMappingConfigHolder dst) throws ServiceCreationException {
		String enforceSingleNS = topLevel.getAttribute("enable-namespace-folding");
		dst.setEnableNamespaceFolding((enforceSingleNS != null && TRUE.equals(enforceSingleNS)) ? true : false);
		Element operationList = (Element) DomParseUtils.getSingleNode(filename, topLevel, SOAConstants.XML_NODE_OPERATIONLIST);
		NodeList operations = DomParseUtils.getImmediateChildrenByTagName(operationList, SOAConstants.XML_NODE_OPERATION);
		List<String> operationsInWsdl = new ArrayList<String>();
		for (int i = 0; i < operations.getLength(); i++) {
			Element inOperation = (Element) operations.item(i);
			String name = inOperation.getAttribute("name");
			operationsInWsdl.add(name);
		}
		if(!operationsInWsdl.contains(SOAConstants.OP_GET_VERSION)){
			addOperation(filename, topLevel);
			dst.setOperationAdded(true);
		}

		addCachePolicyOperation(filename, topLevel);
		// dst.setOperationAdded(true); // only needed for getVersion calls

		Element packageMap = (Element) DomParseUtils.getSingleNode(filename, topLevel, SOAConstants.XML_NODE_PACKAGEMAP);
		NodeList packages = DomParseUtils.getImmediateChildrenByTagName(packageMap, SOAConstants.XML_NODE_PACKAGE);
		Map<String,String> pkgToNs = new HashMap<String,String>();
		for (int i = 0; i < packages.getLength(); i++) {
			Element onePackage = (Element) packages.item(i);
			String javaPackage = onePackage.getAttribute("name");
			String xmlNamespace = onePackage.getAttribute("xml-namespace");
			// TODO - temporary hack for backward compatibility - need to find all instances of the old typemappings and fix them.
//			if (SOAConstants.OLD_DEFAULT_SERVICE_NAMESPACE.equals(xmlNamespace)) {
//				xmlNamespace = SOAConstants.DEFAULT_SERVICE_NAMESPACE;
//			}
			pkgToNs.put(javaPackage, xmlNamespace);
			dst.setXmlNamespaceFromJavaPackage(javaPackage, xmlNamespace);
		}
		operations = DomParseUtils.getImmediateChildrenByTagName(operationList, SOAConstants.XML_NODE_OPERATION);
		for (int i = 0; i < operations.getLength(); i++) {
			Element inOperation = (Element) operations.item(i);
			String name = inOperation.getAttribute("name");
			OperationConfig outOperation = new OperationConfig();
			outOperation.setName(name);
			MessageTypeConfig messageTypeConfig = parseMessageTypeConfig(filename, pkgToNs, inOperation, SOAConstants.XML_NODE_REQUEST_MESSAGE);
			if (messageTypeConfig != null) {
				outOperation.setRequestMessage(messageTypeConfig);
			}
			messageTypeConfig = parseMessageTypeConfig(filename, pkgToNs, inOperation, SOAConstants.XML_NODE_RESPONSE_MESSAGE);
			if (messageTypeConfig != null) {
				outOperation.setResponseMessage(messageTypeConfig);
			}
			messageTypeConfig = parseMessageTypeConfig(filename, pkgToNs, inOperation, SOAConstants.XML_NODE_ERROR_MESSAGE);
			if (messageTypeConfig != null) {
				outOperation.setErrorMessage(messageTypeConfig);
			}
			parseMessageHeaderList(filename, pkgToNs, outOperation.getRequestHeader(), inOperation, SOAConstants.XML_NODE_REQUEST_HEADER);
			parseMessageHeaderList(filename, pkgToNs, outOperation.getResponseHeader(), inOperation, SOAConstants.XML_NODE_RESPONSE_HEADER);
			dst.setOperation(name, outOperation);
		}

		Element javaTypeList = (Element) DomParseUtils.getSingleNode(filename, topLevel, "java-type-list");
		if ( javaTypeList != null ) {
			NodeList javaTypes = DomParseUtils.getImmediateChildrenByTagName(javaTypeList, SOAConstants.XML_NODE_JAVA_TYPE_NAME);
			if ( javaTypes != null ) {
				for (int i = 0; i < javaTypes.getLength(); i++) {
					Element javaClass = (Element) javaTypes.item(i);
					String name = javaClass.getTextContent();
					dst.addJavaTypes( name );
				}
			}
		}
	}

	private static void parseMessageHeaderList(String filename, Map<String, String> pkgToNs, List<MessageHeaderConfig> headerList, Element inOperation, String headerName) throws ServiceCreationException {
		NodeList headerElements = DomParseUtils.getImmediateChildrenByTagName(inOperation, headerName);
		for (int i = 0; i < headerElements.getLength(); i++) {
			Element headerElement = (Element) headerElements.item(i);
			MessageHeaderConfig messageHeaderConfig = parseMessageHeaderConfig(filename, pkgToNs, headerElement);
			headerList.add(messageHeaderConfig);
		}
	}

	private static MessageHeaderConfig parseMessageHeaderConfig(String filename, Map<String, String> pkgToNs,
			Element headerElement) throws ServiceCreationException {
		MessageHeaderConfig messageHeaderConfig = new MessageHeaderConfig();
		String javaType = DomParseUtils.getElementText(filename, headerElement, SOAConstants.XML_NODE_JAVA_TYPE_NAME);
		messageHeaderConfig.setJavaTypeName(javaType);
		String xmlLocalTypeName = DomParseUtils.getElementText(filename, headerElement, SOAConstants.XML_NODE_XML_TYPE_NAME);
		messageHeaderConfig.setXmlTypeName(qualifyXmlName(pkgToNs, javaType, xmlLocalTypeName));
		String xmlLocalElementName = DomParseUtils.getElementText(filename, headerElement, SOAConstants.XML_NODE_XML_ELEMENT_NAME);
		QName xmlElementName = QName.valueOf(xmlLocalElementName);
		messageHeaderConfig.setXmlElementName(xmlElementName);
		return messageHeaderConfig;
	}

	private static MessageTypeConfig parseMessageTypeConfig(String filename, Map<String, String> pkgToNs, Element operation, String name) throws ServiceCreationException {
		Element oneMessage = (Element) DomParseUtils.getSingleNode(filename, operation, name);
		if (oneMessage == null) {
			return null;
		}
		MessageTypeConfig messageTypeConfig = new MessageTypeConfig();
		String javaType = DomParseUtils.getElementText(filename, oneMessage, SOAConstants.XML_NODE_JAVA_TYPE_NAME);
		messageTypeConfig.setJavaTypeName(javaType);
		String xmlLocalTypeName = DomParseUtils.getElementText(filename, oneMessage, SOAConstants.XML_NODE_XML_TYPE_NAME);
		messageTypeConfig.setXmlTypeName(qualifyXmlName(pkgToNs, javaType, xmlLocalTypeName));
		String xmlLocalElementName = DomParseUtils.getElementText(filename, oneMessage, SOAConstants.XML_NODE_XML_ELEMENT_NAME);
		QName xmlElementName = QName.valueOf(xmlLocalElementName);
		if( XMLConstants.NULL_NS_URI.equals(xmlElementName.getNamespaceURI()) ) {
			xmlElementName = qualifyXmlName(pkgToNs, javaType, xmlLocalElementName);
		}
		messageTypeConfig.setXmlElementName(xmlElementName);
		Boolean hasAttachment = DomParseUtils.getElementBoolean(filename, oneMessage, SOAConstants.XML_NODE_HAS_ATTACHMENT);
		if (hasAttachment != null && hasAttachment.booleanValue()) {
			messageTypeConfig.setHasAttachment(true);
		} else {
			messageTypeConfig.setHasAttachment(false);
		}

		return messageTypeConfig;
	}

	private static QName qualifyXmlName(Map<String, String> pkgToNs, String javaType, String localpart) throws ServiceCreationException {
		if (localpart == null) {
			return null;
		}
		String packageName = getPackageName(javaType);
		String namespace = null;
		if (packageName != null) {
			namespace = pkgToNs.get(packageName);
			if (namespace == null) {
				throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_TYPEMAP_JAVA_NO_PACKAGE_MATCH,
						ErrorConstants.ERRORDOMAIN, new Object[] {javaType}));
			}
		}
		return new QName(namespace, localpart);
	}

	private static String getPackageName(String javaType) throws ServiceCreationException {
		if (javaType == null) {
			return null;
		}
		int lastDot = javaType.lastIndexOf(".");
		if (lastDot == -1) {
			// Typically this is a primitive like integer.  For now, we'll assume java.lang since typemappings
			// is an auto-generated file.
			return BASIC_XML_TYPE_JAVA_PACKAGE;
		}
		return javaType.substring(0, lastDot);
	}

	private static void addOperation(String filename, Element topLevel) throws ServiceCreationException{
		Element packageMap = (Element) DomParseUtils.getSingleNode(filename, topLevel, SOAConstants.XML_NODE_PACKAGEMAP);
		NodeList packages = DomParseUtils.getImmediateChildrenByTagName(packageMap, SOAConstants.XML_NODE_PACKAGE);
		if(packages != null && packages.getLength() > 0){
			Element onePackage = (Element) packages.item(0);
			Element newPackageMapping = (Element)onePackage.cloneNode(false);
			newPackageMapping.setAttribute(ATTR_PACKAGE_NAME, BASIC_XML_TYPE_JAVA_PACKAGE);
			newPackageMapping.setAttribute(ATTR_PACKAGE_XML_NS, BindingConstants.XMLSCHEMA_URI);
			packageMap.appendChild(newPackageMapping);
		}
		Element operationList = (Element) DomParseUtils.getSingleNode(filename, topLevel, SOAConstants.XML_NODE_OPERATIONLIST);
		NodeList operations = DomParseUtils.getImmediateChildrenByTagName(operationList, SOAConstants.XML_NODE_OPERATION);

		if(operations != null && operations.getLength() > 0){
			Element firstOperation = (Element) operations.item(0);
			Element newOperation = (Element) firstOperation.cloneNode(true);
			newOperation.setAttribute("name", SOAConstants.OP_GET_VERSION);
			NodeList newOperationParams = newOperation.getChildNodes();
			int numberOfParams = newOperationParams.getLength();
			int notRemoved = 0;
			for (int i = 0; i < numberOfParams; i++) {
				Node childNode = newOperationParams.item(notRemoved);
				if(childNode.getNodeName() == SOAConstants.XML_NODE_RESPONSE_MESSAGE){
					NodeList respParams = childNode.getChildNodes();
					for (int j = 0; j < respParams.getLength(); j++) {
						Node respParam = respParams.item(j);

						if(respParam.getNodeName() == SOAConstants.XML_NODE_JAVA_TYPE_NAME && respParam.getFirstChild() != null)
							respParam.getFirstChild().setNodeValue("java.lang.String");
						else if(respParam.getNodeName() == SOAConstants.XML_NODE_XML_TYPE_NAME && respParam.getFirstChild() != null)
							respParam.getFirstChild().setNodeValue("VersionString");
						else if(respParam.getNodeName() == SOAConstants.XML_NODE_XML_ELEMENT_NAME && respParam.getFirstChild() != null)
							respParam.getFirstChild().setNodeValue("version");
						else if(respParam.getNodeName() == SOAConstants.XML_NODE_HAS_ATTACHMENT && respParam.getFirstChild() != null)
							respParam.getFirstChild().setNodeValue("false");
					}
					notRemoved++;
				}else
					newOperation.removeChild(childNode);
			}

			operationList.appendChild(newOperation);
		}

	}

	private static void addCachePolicyOperation(String filename, Element topLevel) throws ServiceCreationException{
		Element packageMap = (Element) DomParseUtils.getSingleNode(filename, topLevel, SOAConstants.XML_NODE_PACKAGEMAP);
		NodeList packages = DomParseUtils.getImmediateChildrenByTagName(packageMap, SOAConstants.XML_NODE_PACKAGE);
		if(packages != null && packages.getLength() > 0){
			Element onePackage = (Element) packages.item(0);
			Element newPackageMapping = (Element)onePackage.cloneNode(false);
			newPackageMapping.setAttribute(ATTR_PACKAGE_NAME, BASIC_XML_TYPE_JAVA_PACKAGE);
			newPackageMapping.setAttribute(ATTR_PACKAGE_XML_NS, BindingConstants.XMLSCHEMA_URI);
			packageMap.appendChild(newPackageMapping);
		}
		Element operationList = (Element) DomParseUtils.getSingleNode(filename, topLevel, SOAConstants.XML_NODE_OPERATIONLIST);
		NodeList operations = DomParseUtils.getImmediateChildrenByTagName(operationList, SOAConstants.XML_NODE_OPERATION);

		if(operations != null && operations.getLength() > 0){
			Element firstOperation = (Element) operations.item(0);
			Element newOperation = (Element) firstOperation.cloneNode(true);
			newOperation.setAttribute("name", SOAConstants.OP_GET_CACHE_POLICY);
			NodeList newOperationParams = newOperation.getChildNodes();
			int numberOfParams = newOperationParams.getLength();
			int notRemoved = 0;
			for (int i = 0; i < numberOfParams; i++) {
				Node childNode = newOperationParams.item(notRemoved);
				if(childNode.getNodeName() == SOAConstants.XML_NODE_RESPONSE_MESSAGE){
					NodeList respParams = childNode.getChildNodes();
					for (int j = 0; j < respParams.getLength(); j++) {
						Node respParam = respParams.item(j);

						if(respParam.getNodeName() == SOAConstants.XML_NODE_JAVA_TYPE_NAME && respParam.getFirstChild() != null)
							respParam.getFirstChild().setNodeValue("java.lang.String");
						else if(respParam.getNodeName() == SOAConstants.XML_NODE_XML_TYPE_NAME && respParam.getFirstChild() != null)
							respParam.getFirstChild().setNodeValue("CachePolicy");
						else if(respParam.getNodeName() == SOAConstants.XML_NODE_XML_ELEMENT_NAME && respParam.getFirstChild() != null)
							respParam.getFirstChild().setNodeValue("cachepolicy");
						else if(respParam.getNodeName() == SOAConstants.XML_NODE_HAS_ATTACHMENT && respParam.getFirstChild() != null)
							respParam.getFirstChild().setNodeValue("false");
					}
					notRemoved++;
				}else
					newOperation.removeChild(childNode);
			}

			operationList.appendChild(newOperation);
		}

	}

	public static void map(String filename, Element topLevel,
			QName oldSvcQName, QName svcQName, Definition wsdlDef,
			TypeMappingConfigHolder dst) throws ServiceCreationException {
		changeNameSpace(filename, topLevel, oldSvcQName, svcQName, wsdlDef);
		map(filename, topLevel, dst);
	}

	private static void changeNameSpace(String fileName, Element typeMapping,
			QName oldSvcQName, QName svcQName, Definition wsdlDef)
			throws ServiceCreationException {
		String targetWSDLNS = wsdlDef != null ? wsdlDef.getTargetNamespace()
				: svcQName.getNamespaceURI();
		Element operationList = (Element) DomParseUtils.getSingleNode(fileName,
				typeMapping, SOAConstants.XML_NODE_OPERATIONLIST);
		NodeList operations = DomParseUtils.getImmediateChildrenByTagName(
				operationList, SOAConstants.XML_NODE_OPERATION);

		HashMap<String, String> oldToNewNameSpace = new HashMap<String, String>();

		changeNameSpaceMessages(fileName, wsdlDef, oldSvcQName, svcQName,
				targetWSDLNS, operations, oldToNewNameSpace);

		changeNameSpaceForPackageMap(fileName, typeMapping, oldToNewNameSpace);
	}

	private static void changeNameSpaceForPackageMap(String fileName,
			Element typeMapping, HashMap<String, String> oldToNewNameSpace)
			throws ServiceCreationException {
		Element packageMapList = (Element) DomParseUtils.getSingleNode(
				fileName, typeMapping, SOAConstants.XML_NODE_PACKAGEMAP);
		NodeList packages = DomParseUtils.getImmediateChildrenByTagName(
				packageMapList, SOAConstants.XML_NODE_PACKAGE);
		HashMap<String, String> nsToResplace = new HashMap<String, String>();
		// Replace ns for packages
		for (int i = 0; i < packages.getLength(); i++) {
			// Get the package
			Element inPackage = (Element) packages.item(i);
			String javaPackage = inPackage.getAttribute(ATTR_PACKAGE_NAME);
			String newXMLNs = oldToNewNameSpace.get(javaPackage);
			if (newXMLNs != null) {
				nsToResplace.put(inPackage.getAttribute(ATTR_PACKAGE_XML_NS),
						newXMLNs);
				inPackage.setAttribute(ATTR_PACKAGE_XML_NS, newXMLNs);
			}
		}
		// Relace other NS mappings
		for (int i = 0; i < packages.getLength(); i++) {
			// Get the package
			Element inPackage = (Element) packages.item(i);
			String XMLSNs = inPackage.getAttribute(ATTR_PACKAGE_XML_NS);
			String newXMLNs = nsToResplace.get(XMLSNs);
			if (newXMLNs != null) {
				inPackage.setAttribute(ATTR_PACKAGE_XML_NS, newXMLNs);
			}
		}

	}

	private static void changeNameSpaceMessages(String fileName,
			Definition wsdlDef, QName oldSvcQName, QName svcQName,
			String targetWSDLNS, NodeList operations,
			HashMap<String, String> oldToNewNameSpace)
			throws ServiceCreationException {
		for (int i = 0; i < operations.getLength(); i++) {
			// Get the operation
			Element inOperation = (Element) operations.item(i);
			changeNameSpaceMessage(fileName, wsdlDef, oldSvcQName, svcQName,
					targetWSDLNS, oldToNewNameSpace, inOperation);
		}
	}

	private static void changeNameSpaceMessage(String fileName,
			Definition wsdlDef, QName oldSvcQName, QName svcQName,
			String targetWSDLNS, HashMap<String, String> oldToNewNameSpace,
			Element inOperation) throws ServiceCreationException {
		changeNameSpaceInnerMessage(fileName, wsdlDef, oldSvcQName, svcQName,
				targetWSDLNS, oldToNewNameSpace, inOperation,
				SOAConstants.XML_NODE_REQUEST_MESSAGE, false);
		changeNameSpaceInnerMessage(fileName, wsdlDef, oldSvcQName, svcQName,
				targetWSDLNS, oldToNewNameSpace, inOperation,
				SOAConstants.XML_NODE_RESPONSE_MESSAGE, false);
		changeNameSpaceInnerMessage(fileName, wsdlDef, oldSvcQName, svcQName,
				targetWSDLNS, oldToNewNameSpace, inOperation,
				SOAConstants.XML_NODE_ERROR_MESSAGE, true);
	}

	private static void changeNameSpaceInnerMessage(String fileName,
			Definition wsdlDef, QName oldSvcQName, QName svcQName,
			String targetWSDLNS, HashMap<String, String> oldToNewNameSpace,
			Element inOperation, String xmlMessageName, boolean ignoreMismatch)
			throws ServiceCreationException {
		// Get Request Message
		Element reqMessage = (Element) DomParseUtils.getSingleNode(fileName,
				inOperation, xmlMessageName);
		if (reqMessage == null) {
			return;
		}
		// Get XML-element-Name Element
		Element xmlTypeElement = DomParseUtils.getSingleElement(fileName,
				reqMessage, SOAConstants.XML_NODE_XML_ELEMENT_NAME);

		Element javaTypeElement = DomParseUtils.getSingleElement(fileName,
				reqMessage, SOAConstants.XML_NODE_JAVA_TYPE_NAME);

		if (xmlTypeElement == null || javaTypeElement == null) {
			return;
		}
		// Get XML Element Name content
		String xmlTypeElementStr = xmlTypeElement.getTextContent();

		// Get Java type Name
		String javaTypeName = javaTypeElement.getTextContent();

		// Get QName out of it
		QName qNameTM = QName.valueOf(xmlTypeElementStr);

		// First check if the WSDL can get this message
		//Raghu: Turning off the wsdl mismatch validation. As we need to plugin
		// wsdl schema parsing for doing the same.
	/*	if (wsdlDef != null) {
			retroFitQName(wsdlDef, svcQName, targetWSDLNS,
					oldToNewNameSpace, xmlTypeElement, javaTypeName, qNameTM,
					ignoreMismatch);
		} else {*/
			retroFitQName(oldSvcQName.getNamespaceURI(), targetWSDLNS,
					oldToNewNameSpace, xmlTypeElement, javaTypeName, qNameTM);
		/*}*/
	}

	private static void retroFitQName(String oldNS, String targetWSDLNS,
			HashMap<String, String> oldToNewNameSpace, Element xmlTypeElement,
			String javaTypeName, QName qNameTM) throws ServiceCreationException {
		if (oldNS.equals(qNameTM.getNamespaceURI()) || qNameTM.getNamespaceURI().isEmpty()) {
			QName qNameWSDLNS = new QName(targetWSDLNS, qNameTM.getLocalPart());
			// Retro-fit the element
			xmlTypeElement.setTextContent(qNameWSDLNS.toString());

			String packageName = getPackageName(javaTypeName);
			if (packageName != null) {
				oldToNewNameSpace.put(packageName, targetWSDLNS);
			}
		}
	}

/*	private static void retroFitQName(Definition wsdlDef, QName svcQName,
			String targetWSDLNS, HashMap<String, String> oldToNewNameSpace,
			Element xmlTypeElement, String javaTypeName, QName qNameTM,
			boolean ignoreMismatch) throws ServiceCreationException {
		Message message = wsdlDef.getMessage(qNameTM);
		if (message == null) {
			// Try QName with
			QName qNameWSDLNS = new QName(targetWSDLNS, qNameTM.getLocalPart());

			message = wsdlDef.getMessage(qNameWSDLNS);
			if (message == null) {
				if (!ignoreMismatch) {
					throw new ServiceCreationException(
					ErrorDataFactory.createErrorData(ErrorConstants.CFG_GENERIC_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] { qNameTM.toString(),
									svcQName.toString() }));
				}
				return;
			}
			// Retro-fit the element
			xmlTypeElement.setTextContent(qNameWSDLNS.toString());

			String packageName = getPackageName(javaTypeName);
			if (packageName != null) {
				oldToNewNameSpace.put(packageName, targetWSDLNS);
			}
		}
	}
	*/

}
