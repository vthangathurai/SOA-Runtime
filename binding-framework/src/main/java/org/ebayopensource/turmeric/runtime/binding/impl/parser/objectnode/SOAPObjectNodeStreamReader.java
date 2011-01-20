/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class SOAPObjectNodeStreamReader extends ObjectNodeStreamReader {

	private List<String> m_defaultNSPrefix = new ArrayList<String>();
	private List<String> m_defaultNSUri = new ArrayList<String>();

	private static final String SOAP_ENV_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String BODY_LOCAL_NAME = "body";
	private static final String SOAPENVELOPE_LOCAL_NAME = "envelope";

	private boolean m_defaultNSSet = false;

	public SOAPObjectNodeStreamReader(XMLStreamReader reader)
			throws XMLStreamException {
		super(reader);
	}

	@Override
	public int getNamespaceCount() {
		int nsc = super.getNamespaceCount();
		return nsc + m_defaultNSPrefix.size();
	}

	@Override
	public String getNamespacePrefix(int index) {
		// the outer namespace goes first to the list
		// the caller should access the list from the end so the most recently added
		// namespace will be returned first
		int defaultSize = m_defaultNSPrefix.size();
		if (index < defaultSize) {
			return m_defaultNSPrefix.get(index);
		}
		return super.getNamespacePrefix(index - defaultSize);
	}

	@Override
	public String getNamespaceURI(int index) {
		// the outer namespace goes first to the list
		// the caller should access the list from the end so the most recently added
		// namespace will be returned first
		int defaultSize = m_defaultNSUri.size();
		if (index < defaultSize) {
			return m_defaultNSUri.get(index);
		}
		return super.getNamespaceURI(index - defaultSize);
	}

	@Override
	public String getNamespaceURI(String prefix) {
		int defaultSize = m_defaultNSUri.size();
		for (int i = 0; i < defaultSize; i++) {
			if (m_defaultNSPrefix.get(i).equals(prefix)) {
				return m_defaultNSUri.get(i);
			}
		}
		return super.getNamespaceURI(prefix);
	}

	@Override
	public int next() throws XMLStreamException {
		int ret = super.next();

		if (!m_defaultNSSet && m_currentNode != null) {
			QName bodyName = m_currentNode.getNodeName();
			QName envelopeName = m_currentNode.getParentNode()
					.getNodeName();
			if (BODY_LOCAL_NAME.equalsIgnoreCase(bodyName.getLocalPart())
					&& SOAP_ENV_NAMESPACE.equals(bodyName.getNamespaceURI())
					&& SOAPENVELOPE_LOCAL_NAME.equalsIgnoreCase(envelopeName.getLocalPart())
					&& SOAP_ENV_NAMESPACE.equals(envelopeName.getNamespaceURI())) {
				StreamableObjectNodeImpl node = (StreamableObjectNodeImpl) m_currentNode.getParentNode();
				int nsc = node.getNamespaceCount();
				for (int i = 0; i < nsc; i++) {
					m_defaultNSPrefix.add(node.getNamespacePrefix(i));
					m_defaultNSUri.add(node.getNamespaceURI(i));
				}
				node = m_currentNode;
				nsc = node.getNamespaceCount();
				for (int i = 0; i < nsc; i++) {
					m_defaultNSPrefix.add(node.getNamespacePrefix(i));
					m_defaultNSUri.add(node.getNamespaceURI(i));
				}
				m_defaultNSSet = true;
			}
		}
		return ret;

	}
}
