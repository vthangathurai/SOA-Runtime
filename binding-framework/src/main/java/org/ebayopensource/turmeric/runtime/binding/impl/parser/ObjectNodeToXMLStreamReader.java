/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser;

import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.binding.utils.Stack;


/**
 * @author wdeng
 */
public class ObjectNodeToXMLStreamReader extends BaseXMLStreamReader {

	private final String m_nsDefinitionPrefix;
	private Stack<Iterator<ObjectNode>> m_nodeStack;
	private String m_currentValue;

	public ObjectNodeToXMLStreamReader(String nsDefinitionPrefix, NamespaceConvention convention, Map<String, String> options)
	{
		this(null, nsDefinitionPrefix, convention, options);
	}

	public ObjectNodeToXMLStreamReader(ObjectNodeImpl root,
		String nsDefinitionPrefix, NamespaceConvention convention, Map<String, String> options)
	{
		super(convention, options);
		m_nsDefinitionPrefix = nsDefinitionPrefix;
		m_nodeStack = new Stack<Iterator<ObjectNode>>();
		m_node = root;
		m_event = START_DOCUMENT;
	}

	public void close() throws XMLStreamException {
		// noop
	}

	public String getElementText() throws XMLStreamException {
		return m_currentValue;
	}

	public NamespaceContext getNamespaceContext() {
		return m_convention;
	}

	public String getText() {
		return m_currentValue;
	}

	@Override
	public int getAttributeCount() {
		int count = 0;
		if (m_node.hasAttributes()) {
			count = m_node.getAttributes().size();
		}
		return count;
	}

	@Override
	public String getAttributeLocalName(int n) {
		ObjectNode node = m_node.getAttribute(n);
		QName name = node.getNodeName();
		return name.getLocalPart();
	}

	@Override
	public QName getAttributeName(int n) {
		   ObjectNode node = m_node.getAttribute(n);
		QName name = node.getNodeName();
		return name;
	}

	@Override
	public String getAttributeNamespace(int n) {
		ObjectNode node = m_node.getAttribute(n);
		QName name = node.getNodeName();
		return name.getNamespaceURI();
	}

	@Override
	public String getAttributePrefix(int n) {
		ObjectNode node = m_node.getAttribute(n);
		QName name = node.getNodeName();
		return name.getPrefix();
	}

	@Override
	public String getAttributeValue(int n) {
		ObjectNode node = m_node.getAttribute(n);
		return node.getNodeValue().toString();
	}

	public int next() throws XMLStreamException {
		if (m_event == START_DOCUMENT) {
			// Gets the top level childrens of the tree.
			return startNextChildNode(END_DOCUMENT);
		}

		if (m_event == START_ELEMENT) {
			String value = (String)m_node.getNodeValue();
			if (value != null) {
				m_currentValue = value;
				m_event = CHARACTERS;
				return m_event;
			}
			return startNextChildNode(END_ELEMENT);
		}

		if (m_event == CHARACTERS) {
			m_currentValue = null;
			return startNextChildNode(END_ELEMENT);
		}

		if (m_event == END_ELEMENT) {
			Iterator<ObjectNode> children = m_nodeStack.peek();
			if (children.hasNext()) {
				ObjectNodeImpl node = (ObjectNodeImpl)children.next();
//				LogManager.getInstance(ObjectNodeToXMLStreamReader.class).log(Level.FINE, "Event: " + m_event + " Child: " + node);
				m_node = node;
				m_event = START_ELEMENT;
				return m_event;
			}
			m_nodeStack.pop();
			if (m_nodeStack.size() > 0) {
				m_event = END_ELEMENT;
				return m_event;
			}
			m_event = END_DOCUMENT;
			return m_event;
		}

		throw new XMLStreamException("Wrong-formatted input, "
				+ "found unrecognized event id: " + m_event);
	}

	private int startNextChildNode(int eventWhenNoMoreChild) throws XMLStreamException {
		Iterator<ObjectNode> children = ((ObjectNodeImpl)m_node).getChildrenIterator();
		ObjectNode node = null;
		if (children.hasNext()) {
			m_nodeStack.push(children);
			node = children.next();
			while (null != node && addMappingIfApplied(node.getNodeName(), (String)node.getNodeValue())) {
				node = children.hasNext()? children.next() : null;
			}
		}

//		LogManager.getInstance(ObjectNodeToXMLStreamReader.class).log(Level.INFO, "Event: " + m_event + " Node: " + m_node + " Child: " + node);
		if (null != node) {
			m_node = node;
			m_event = START_ELEMENT;
			return m_event;
		}
		m_event = eventWhenNoMoreChild;
		return m_event;
	}

	/**
	 * Adds the namespace mapping if it is a namespace definition
	 *
	 * @param nsDefinition
	 * @return true if it is a namespace definiton.
	 */
	private boolean addMappingIfApplied(QName name, String value) throws XMLStreamException {
		if (m_nsDefinitionPrefix == null) {
			return false;
		}

		String prefix = name.getPrefix();
		String localPart = name.getLocalPart();
		if (m_nsDefinitionPrefix.equals(prefix)) {
			m_convention.addMapping(localPart, value);
			return true;
		}

		if ((prefix == null || "".equals(prefix)) && m_nsDefinitionPrefix.equals(localPart)) {
			m_convention.addMapping(localPart, value);
			return true;
		}

		return false;
	}
}
