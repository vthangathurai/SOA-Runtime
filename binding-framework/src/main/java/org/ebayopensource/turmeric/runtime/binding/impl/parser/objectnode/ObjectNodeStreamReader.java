/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.utils.Stack;


/**
 * @author wdeng
 */
public class ObjectNodeStreamReader 
		implements ObjectNodeBuilder, XMLStreamReader {

	private static final String DEFAULT_ATTR_TYPE = "CDATA"; 
	private Stack<Iterator<ObjectNode>> m_nodeStack;

	private XMLStreamReader m_reader;
	private int m_event;
	private StreamableObjectNodeImpl m_rootNode;
	protected StreamableObjectNodeImpl m_currentNode;
	private int m_rawEvent;
	private boolean m_allowNodeBuilding = true;
	private String m_encodingScheme;
	private String m_version;

	public ObjectNodeStreamReader(XMLStreamReader reader) throws XMLStreamException {
		m_nodeStack = new Stack<Iterator<ObjectNode>>();
		m_reader = reader;
		m_event = START_DOCUMENT;
		m_encodingScheme = m_reader.getCharacterEncodingScheme();
		m_version = m_reader.getVersion();
		nextRawEvent();
		m_rootNode = new StreamableObjectNodeImpl(this);
		m_currentNode = m_rootNode;
	}
	
	public ObjectNode getObjectNode() throws XMLStreamException {
		return m_rootNode;
	}
	
	public boolean allowNodeBuilding() {
		return m_allowNodeBuilding;
	}
	
	public void stopNodeBuilding() {
		m_allowNodeBuilding = false;
	}
	public void close() throws XMLStreamException {
		m_reader.close();
	}
	
	public int getAttributeCount() {
		if (m_currentNode == null) {
			int cnt = m_reader.getAttributeCount();
			return cnt;
		}
		return m_currentNode.getAttributeCount();
	}
	
	public String getAttributeLocalName(int index) {
		if (m_currentNode == null) {
			String name = m_reader.getAttributeLocalName(index);
			return name;
		}
		return getAttributeName(index).getLocalPart();
	}
	
	public QName getAttributeName(int index) {
		if (m_currentNode == null) {
			QName qName = m_reader.getAttributeName(index);
			return qName;
		}
		return m_currentNode.getAttribute(index).getNodeName();
	}
	
	public String getAttributeNamespace(int index) {
		if (m_currentNode == null) {
			String ns = m_reader.getAttributeNamespace(index);
			return ns;
		}
		return getAttributeName(index).getNamespaceURI();
	}
	
	public String getAttributePrefix(int index) {
		if (m_currentNode == null) {
			return m_reader.getAttributePrefix(index);
		}
		return getAttributeName(index).getPrefix();
	}
	
	public String getAttributeType(int index) {
		if (m_currentNode == null) {
			String type = m_reader.getAttributeType(index);
			return type;
		}
		
		// hard coded since we are not able to get the attribute type from anywhere
		return DEFAULT_ATTR_TYPE; 
		
		//throw new UnsupportedOperationException(ObjectNodeStreamReader.class.getName() + ".getAttributeType(int index)");
	}
	
	public String getAttributeValue(int index) {
		if (m_currentNode == null) {
			String value = m_reader.getAttributeValue(index);
			return value;
		}
		return (String)m_currentNode.getAttribute(index).getNodeValue();
	}
	
	public String getAttributeValue(String nsURI, String localName) {
		throw new UnsupportedOperationException(ObjectNodeStreamReader.class.getName() + ".getAttributeValue(String nsURI, String localName)");
	}
	
	public String getCharacterEncodingScheme() {
		return m_encodingScheme;
	}
	
	public String getElementText() throws XMLStreamException {
		return getText();
	}
	
	public String getEncoding() {
		return m_reader.getEncoding();
	}
	
	public int getEventType() {
		if (m_currentNode == null) {
			return m_reader.getEventType();
		}
		return m_event;
	}
	
	public String getLocalName() {
		return getName().getLocalPart();
	}
	
	public Location getLocation() {
		if (m_currentNode == null) {
			return m_reader.getLocation();
		}
		return m_currentNode.getLocation();
	}
	
	public QName getName() {
		if (m_currentNode == null) {
			QName qName =  m_reader.getName();
			return qName;
		}
		return m_currentNode.getNodeName();
	}
	
	public NamespaceContext getNamespaceContext() {
		return m_reader.getNamespaceContext();
	}
	
	public int getNamespaceCount() {
		if (m_currentNode == null) {
			return m_reader.getNamespaceCount();
		}
		return m_currentNode.getNamespaceCount();
	}
	
	public String getNamespacePrefix(int index) {
		if (m_currentNode == null) {
			return m_reader.getNamespacePrefix(index);
		}
		return m_currentNode.getNamespacePrefix(index);
	}
	
	public String getNamespaceURI() {
		if (null == m_currentNode) {
			return m_reader.getNamespaceURI();
		}
		return m_currentNode.getNodeName().getNamespaceURI();
	}
	
	public String getNamespaceURI(int index) {
		if (m_currentNode == null) {
			return m_reader.getNamespaceURI(index);
		}
		return m_currentNode.getNamespaceURI(index);
	}
	
	public String getNamespaceURI(String prefix) {
		if (m_currentNode == null) {
			return m_reader.getNamespaceURI(prefix);
		}
		return m_currentNode.getNamespaceURI(prefix);
	}
	
	public String getPIData() {
		return m_reader.getPIData();
	}
	
	public String getPITarget() {
		return m_reader.getPITarget();
	}
	
	public String getPrefix() {
		return getName().getPrefix();
	}
	
	public Object getProperty(String arg0) throws IllegalArgumentException {
		return m_reader.getProperty(arg0);
	}
	
	public String getText() {
		if (null == m_currentNode) {
			return m_reader.getText();
		}
		return m_currentNode.getNodeValue();
	}
	
	public char[] getTextCharacters() {
		if (null == m_currentNode) {
			return m_reader.getTextCharacters();
		}
		return getText().toCharArray();
	}
	
	public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
		char[] textChars = getTextCharacters();
		System.arraycopy(textChars, sourceStart, target, targetStart, length);
		int availableSpace = target.length - targetStart - 1;
		int remainingChars = textChars.length - sourceStart - 1;
		return Math.min(Math.min(availableSpace, remainingChars), length);
	}
	
	public int getTextLength() {
		if (null == m_currentNode) {
			return m_reader.getTextLength();
		}
		return getText().length();
	}
	
	public int getTextStart() {
		if (null == m_currentNode) {
			return m_reader.getTextStart();
		}
		return 0;
	}
	
	public String getVersion() {
		return m_version;
	}
	
	public boolean hasName() {
		return null != getName();
	}
	
	public boolean hasNext() throws XMLStreamException {
		if (m_event == END_DOCUMENT) {
			return false;
		}
		if (null == m_currentNode) {
			return m_reader.hasNext();
		}
		return m_event != END_DOCUMENT ? true : false;
	}
	
	public boolean hasText() {
		throw new UnsupportedOperationException(ObjectNodeStreamReader.class.getName() + ".hasText()");
	}
	
	public boolean isAttributeSpecified(int index) {
		throw new UnsupportedOperationException(ObjectNodeStreamReader.class.getName() + ".isAttributeSpecified(int index)");
	}
	
	public boolean isCharacters() {
		if (null == m_currentNode) {
			return m_reader.isCharacters();
		}
		return m_event == CHARACTERS;
	}
	
	public boolean isEndElement() {
		if (null == m_currentNode) {
			return m_reader.isEndElement();
		}
		return m_event == END_ELEMENT;
	}
	
	public boolean isStandalone() {
	    return false;
		// throw new UnsupportedOperationException(ObjectNodeStreamReader.class.getName() + ".isStandalone()");
	}
	
	public boolean isStartElement() {
		if (null == m_currentNode) {
			return m_reader.isStartElement();
		}
		return m_event == START_ELEMENT;
	}
	
	public boolean isWhiteSpace() {
		throw new UnsupportedOperationException(ObjectNodeStreamReader.class.getName() + ".isWhiteSpace()");
	}

	public int next() throws XMLStreamException {
		if (m_event == END_DOCUMENT) {
			return m_event;
		}
		if (null == m_currentNode) {
			int event = nextRawEvent();
			return event;
		}
		if (m_event == START_DOCUMENT) {
			// Gets the top level childrens of the tree.
			return startNextChildNode(END_DOCUMENT);
		}

		if (m_event == START_ELEMENT) {
			String value = m_currentNode.getNodeValue();
			if (value != null) {
				m_event = CHARACTERS;
				return m_event;
			}
			return startNextChildNode(END_ELEMENT);
		}

		if (m_event == CHARACTERS) {
			return startNextChildNode(END_ELEMENT);
		}

		if (m_event == END_ELEMENT) {
			m_currentNode = (StreamableObjectNodeImpl)m_currentNode.getParentNode();
			Iterator<ObjectNode> children = m_nodeStack.peek();
			if (children.hasNext()) {
				StreamableObjectNodeImpl node = (StreamableObjectNodeImpl)children.next();
//				LogManager.getInstance(ObjectNodeStreamReader.class).log(Level.FINE, "Event: " + m_event + " Child: " + node);
				m_currentNode = node;
				m_event = START_ELEMENT;
				return m_event;
			}
			if (m_currentNode.isBuilding()) {
				// This is the end of built object nodes. from this point
				// on, read from the underneath xml stream reader.
				m_currentNode = null;
				return m_rawEvent;
			}
			if (m_nodeStack.size() > 0) {
				m_nodeStack.pop();
				m_event = END_ELEMENT;
				return m_event;
			}
			m_event = END_DOCUMENT;
			return m_event;
		}

		throw new XMLStreamException("Wrong-formatted input, "
				+ "found unrecognized event id: " + m_event);
	}
	
	public int nextTag() throws XMLStreamException {
		throw new UnsupportedOperationException(ObjectNodeStreamReader.class.getName() + ".nextTag()");
	}
	
	public void require(int type, String nsURI, String localName) throws XMLStreamException {
		throw new UnsupportedOperationException(ObjectNodeStreamReader.class.getName() + ".require(int type, String nsURI, String localName)");
	}
	
	public boolean standaloneSet() {
		return m_reader.standaloneSet();
	}
	
	int getCurrentRawEvent() {
		return m_rawEvent;
	}
	
	int nextRawEvent() throws XMLStreamException {
		int currentEvent = getCurrentRawEvent();
		m_rawEvent = m_reader.next();
		while (SPACE == m_rawEvent 
			|| COMMENT == m_rawEvent
			|| (END_ELEMENT == currentEvent && CHARACTERS == m_rawEvent)) {
			m_rawEvent = m_reader.next();
		}
		return m_rawEvent;
	}
	
	Location getRawLocation() {
		return m_reader.getLocation();
	}
	
	String getRawText() {
		return m_reader.getText();
	}
	
	QName getRawName() {
		return m_reader.getName();
	}
	
	int getRawNamespaceCount() {
		return m_reader.getNamespaceCount();
	}
	
	String getRawNamespacePrefix(int index) {
		return m_reader.getNamespacePrefix(index);
	}
	
	String getRawNamespaceURI(int index) {
		return m_reader.getNamespaceURI(index);
	}
	
	int getRawAttributeCount() {
		return m_reader.getAttributeCount();
	}
	
	QName getRawAttributeName(int index) {
		return m_reader.getAttributeName(index);
	}
	
	String getRawAttributeLocalName(int index) {
		return m_reader.getAttributeLocalName(index);
	}
	
	String getRawAttributeNamespace(int index) {
		String nsURI = m_reader.getAttributeNamespace(index);
		if (null == nsURI) {
			return "";
		}
		return nsURI;
	}
	
	String getRawAttributePrefix(int index) {
		String prefix = m_reader.getAttributePrefix(index);
		if (null == prefix) {
			return "";
		}
		return prefix;
	}
	
	String getRawAttributeValue(int index) {
		return m_reader.getAttributeValue(index);
	}
	
	private int startNextChildNode(int eventWhenNoMoreChild) throws XMLStreamException {
		StreamableObjectNodeImpl nodeImpl = m_currentNode;
		Iterator<ObjectNode> children = 
			m_allowNodeBuilding ? nodeImpl.getChildrenIterator() : nodeImpl.getIteratorForBuiltChildren();
		StreamableObjectNodeImpl node = null;
		if (children.hasNext()) {
			m_nodeStack.push(children);
			node = (StreamableObjectNodeImpl) children.next();
		}
//		LogManager.getInstance(ObjectNodeStreamReader.class).log(Level.INFO, "Event: " + m_event + " Node: " + m_currentNode + " Child: " + node);
		if (null != node) {
			m_currentNode = node;
			m_event = START_ELEMENT;
			return m_event;
		}
		if (m_currentNode.isBuilding()) {
			// This is the end of built object nodes. from this point
			// on, read from the underneath xml stream reader.
			m_currentNode = null;
			return m_rawEvent;
		}
		m_event = eventWhenNoMoreChild;
		return m_event;
	}
}
