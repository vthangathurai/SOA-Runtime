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
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ServiceNameUtils;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

public class DomParseUtils {
	public static Element getSingleElement(String filename, Element parent, String name) throws ServiceCreationException {
		return (Element) getSingleNode(filename, parent, name);
	}

	public static Node getSingleNode(String filename, Element parent, String name) throws ServiceCreationException {
		NodeList nodes = getImmediateChildrenByTagName(parent, name);
		if (nodes.getLength() == 0) {
			return null;
		}
		if (nodes.getLength() > 1) {
			throwError(filename, "Extra element values seen for element " + name);
		}
		return nodes.item(0);
	}

	public static NodeList getImmediateChildrenByTagName(Element parent, String name) throws ServiceCreationException {
		NodeListAdaptor result = new NodeListAdaptor();
		NodeList nodes = parent.getElementsByTagName(name);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getParentNode() == parent) {
				result.add(node);
			}
		}
		return result;
	}
	public static String getElementText(String filename, Element parent, String name) throws ServiceCreationException {
		return getElementText(filename, parent, name, false);
	}


	public static String getElementText(String filename, Element parent, String name, boolean isRequired) throws ServiceCreationException {
		Element node = getSingleElement(filename, parent, name);
		if (node == null) {
			if (!isRequired) {
				return null;
			}
			throwError(filename, "Missing required element: '" + name + "'");
		}
		return getText(node);
	}

	public static String getText(Node node) {
		StringBuffer result = new StringBuffer();
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node2 = nodes.item(i);
			if (node2.getNodeType() == Node.TEXT_NODE || node2.getNodeType() == Node.CDATA_SECTION_NODE) {
				String value = node2.getNodeValue();
				result.append(value);
			}
		}
		return result.toString().trim();
	}

	public static Integer getElementInteger(String filename, Element parent, String name) throws ServiceCreationException {
		String text = getElementText(filename, parent, name);
		if (text == null) {
			return null;
		}
		return textToInteger(filename, text, name);
	}

	public static Long getElementLong(String filename, Element parent, String name) throws ServiceCreationException {
		String text = getElementText(filename, parent, name);
		if (text == null) {
			return null;
		}
		return textToLong(filename, text, name);
	}

	public static Boolean getElementBoolean(String filename, Element parent, String name) throws ServiceCreationException {
		String text = getElementText(filename, parent, name);
		if (text == null) {
			return null;
		}
		return textToBoolean(filename, text, name);
	}

	public static Integer getAttributeInteger(String filename, Element parent, String name) throws ServiceCreationException {
		String attrStr = parent.getAttribute(name);
		if (attrStr == null) {
			return null;
		}
		return textToInteger(filename, attrStr, name);
	}

	public static void storeNVListToHashMap(String filename, OptionList options, Map<String, String> dstMap) {
		if (options == null || options.getOption() == null || options.getOption().isEmpty()) {
			return;
		}
		List<NameValue> optionNvList = options.getOption();
		for (int i=0; i<optionNvList.size(); i++) {
			NameValue nv = optionNvList.get(i);
			dstMap.put(nv.getName(), nv.getValue());
		}
	}

	public static OptionList getOptionList(String filename, Element parent, String childName) throws ServiceCreationException {
		if (parent == null) {
			return null;
		}
		OptionList result = new OptionList();
		Element optionContainer = DomParseUtils.getSingleElement(filename, parent, childName);
		if (optionContainer == null) {
			return null;
		}
		List<NameValue> outList = result.getOption();
		putNVList(filename, childName, optionContainer, outList);
		return result;
	}

	public static void putNVList(String filename, String containerName, Element optionContainer, List<NameValue> outList) throws ServiceCreationException {
		NodeList childElements = DomParseUtils.getImmediateChildrenByTagName(optionContainer, "option");
		if (childElements == null) {
			return;
		}
		for (int i = 0; i < childElements.getLength(); i++) {
			Element option = (Element) childElements.item(i);
			String name = option.getAttribute("name");
			if (name == null || name.length() == 0) {
				throwError(filename, "Missing option name in option list: '" + containerName + "'");
			}
			String value = DomParseUtils.getText(option);
			if (value == null) {
				throwError(filename, "Missing option value for option list: '" + containerName + "'");
			}
			NameValue nv = new NameValue();
			nv.setName(name);
			nv.setValue(value);
			outList.add(nv);
		}
	}

	public static List<String> getStringList(String filename, Element parent, String name) throws ServiceCreationException {
		NodeList childElements = DomParseUtils.getImmediateChildrenByTagName(parent, name);
		if (childElements == null) {
			return null;
		}
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < childElements.getLength(); i++) {
			Element oneElement = (Element) childElements.item(i);
			String outValue = DomParseUtils.getText(oneElement);
			result.add(outValue);
		}
		return result;
	}

	public static List<Integer> getIntegerList(String filename, Element parent, String name) throws ServiceCreationException {
		NodeList childElements = DomParseUtils.getImmediateChildrenByTagName(parent, name);
		if (childElements == null) {
			return null;
		}
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < childElements.getLength(); i++) {
			Element oneElement = (Element) childElements.item(i);
			String text = DomParseUtils.getText(oneElement);
			Integer intValue = textToInteger(filename, text, name);
			result.add(intValue);
		}
		return result;
	}

	public static List<Long> getLongList(String filename, Element parent, String name) throws ServiceCreationException {
		NodeList childElements = DomParseUtils.getImmediateChildrenByTagName(parent, name);
		if (childElements == null) {
			return null;
		}
		ArrayList<Long> result = new ArrayList<Long>();
		for (int i = 0; i < childElements.getLength(); i++) {
			Element oneElement = (Element) childElements.item(i);
			String text = DomParseUtils.getText(oneElement);
			Long intValue = textToLong(filename, text, name);
			result.add(intValue);
		}
		return result;
	}

	public static TransportOptions mapTransportOptions(String filename, Element inOptions) throws ServiceCreationException {
		if (inOptions == null) {
			return null;
		}
		TransportOptions outOptions = new TransportOptions();
		Integer numConnectRetries = DomParseUtils.getElementInteger(filename, inOptions, "num-connect-retries");
		if (numConnectRetries != null) {
			outOptions.setNumConnectRetries(numConnectRetries);
		}
		Integer socketConnectTimeoutMsec = DomParseUtils.getElementInteger(filename, inOptions, "socket-connect-timeout-msec");
		if (socketConnectTimeoutMsec != null) {
			outOptions.setConnectTimeout(socketConnectTimeoutMsec);
		}
		Integer socketReadTimeoutMsec = DomParseUtils.getElementInteger(filename, inOptions, "socket-read-timeout-msec");
		if (socketReadTimeoutMsec != null) {
			outOptions.setReceiveTimeout(socketReadTimeoutMsec);
		}
		Integer invocationTimeoutMsec = DomParseUtils.getElementInteger(filename, inOptions, "invocation-timeout-msec");
		if (invocationTimeoutMsec != null) {
			outOptions.setInvocationTimeout(invocationTimeoutMsec);
		}
		String skipSerStr = DomParseUtils.getElementText(filename, inOptions, "skip-serialization");
		if (skipSerStr != null) {
			outOptions.setSkipSerialization(skipSerStr.equalsIgnoreCase("true")? Boolean.TRUE : Boolean.FALSE);
		}
		String useDetachedLocalBindingStr =
			DomParseUtils.getElementText(filename, inOptions, "use-detached-local-binding");
		if (useDetachedLocalBindingStr != null) {
			outOptions.setUseDetachedLocalBinding(
				useDetachedLocalBindingStr.equalsIgnoreCase("true")? Boolean.TRUE : Boolean.FALSE);
		}
		String clientStreamingStr =
			DomParseUtils.getElementText(filename, inOptions, "client-streaming");
		if (clientStreamingStr != null) {
			outOptions.setClientStreaming(
				clientStreamingStr.equalsIgnoreCase("true")? Boolean.TRUE : Boolean.FALSE);
		}
		Map<String, String> transportOptionsMap = outOptions.getProperties();
		OptionList options = getOptionList(filename, inOptions, "other-options");
		storeNVListToHashMap(filename, options, transportOptionsMap);
		return outOptions;
	}

	public static MonitoringLevel mapMonitoringLevel(String filename, String value) throws ServiceCreationException {
		if (value == null || value.equals("")) {
			return null;
		}
		try {
			return MonitoringLevel.fromValue(value);
		} catch (IllegalArgumentException e) {
			throwError(filename, "Invalid handler presence value: " + value);
		}
		return null;
	}

	public static QName getQName(String configFilename, String serviceName, String namespaceURI, String tagName) {
		QName qname = QName.valueOf(serviceName);
		return ServiceNameUtils.normalizeQName(qname);
	}

	public static String getRequiredAttribute(String filename, Element element, String name) throws ServiceCreationException {
		String value = element.getAttribute(name);
		if (value == null || value.length() == 0) {
			throwError(filename, "Missing required attribute '" + name + "' on element '" + element.getTagName() + "'");
		}
		return value;
	}

	public static String getAttribute(String filename, Element element, String name, String attribute) throws ServiceCreationException {
		Element node = getSingleElement(filename, element, name);
		if(node == null) {
			return null;
		}		
		return node.getAttribute(attribute);
	}
	public static void throwError(String filename, String cause) throws ServiceCreationException {
		throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR,
				ErrorConstants.ERRORDOMAIN, new Object[] {filename, cause}));
	}

	private static Integer textToInteger(String filename, String text, String name) throws ServiceCreationException {
		if (text == null || text.length() == 0) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_BAD_INTEGER,
					ErrorConstants.ERRORDOMAIN, new Object[] {filename, name, "(Missing value)"}));
		}
		Integer intValue = null;
		try {
			intValue = Integer.valueOf(text);
		} catch (NumberFormatException e) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_BAD_INTEGER,
					ErrorConstants.ERRORDOMAIN, new Object[] {filename, name, e.toString()}));
		}
		return intValue;
	}

	private static Long textToLong(String filename, String text, String name) throws ServiceCreationException {
		if (text == null || text.length() == 0) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_BAD_INTEGER,
					ErrorConstants.ERRORDOMAIN, new Object[] {filename, name, "(Missing value)"}));
		}
		Long intValue = null;
		try {
			intValue = Long.valueOf(text);
		} catch (NumberFormatException e) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_BAD_INTEGER,
					ErrorConstants.ERRORDOMAIN, new Object[] {filename, name, e.toString()}));
		}
		return intValue;
	}

	private static Boolean textToBoolean(String filename, String text, String name) throws ServiceCreationException {
		if (text == null || text.length() == 0) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_BAD_BOOLEAN,
					ErrorConstants.ERRORDOMAIN, new Object[] {filename, name, "(Missing value)"}));
		}
		Boolean booleanValue = Boolean.valueOf(text);
		if (booleanValue == null) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_BAD_BOOLEAN,
					ErrorConstants.ERRORDOMAIN, new Object[] {filename, name, text}));
		}
		return booleanValue;
	}



}
