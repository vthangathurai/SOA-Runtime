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
package org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.impl.parser.ChildNodeStreamingIterator;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.StreamableObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.binding.utils.XMLStreamReaderUtils;


/**
 * @author wdeng
 *
 */
public class StreamableObjectNodeImpl extends ObjectNodeImpl 
	implements StreamableObjectNode {

	private ObjectNodeStreamReader m_context;
	private boolean m_built;
	private int m_childIndex;   // last index of child returned by next().
	private String[] m_namespacePairs;  // List of prefix1, nsURI1, prefix2, nsURI2...
	

	public StreamableObjectNodeImpl(ObjectNodeStreamReader context) {
		this(ROOT_NODE_QNAME, null, context);
	}

	public StreamableObjectNodeImpl(QName name, ObjectNode parent, ObjectNodeStreamReader context) {
		super(name, parent);
		m_context = context;
		m_built = false;
		m_childIndex = -1;
	}

	public StreamableObjectNode nextChild() throws XMLStreamException {
		// Finishes the current child if it is not in done stage.
		// Read the next start tag and fill in the name and attributes.
		// But leaves the childrens and end tags to be processed later.
		// 
		if (m_childIndex + 1 < m_children.size()) {
			return (StreamableObjectNodeImpl) m_children.get(m_childIndex++);
		}
		
		if (!m_context.allowNodeBuilding()) {
			return null;
		}
		
		StreamableObjectNodeImpl currentChild = null;
		if (m_childIndex >= 0 && m_childIndex < m_children.size()) {
			currentChild = (StreamableObjectNodeImpl)m_children.get(m_childIndex);
		}
		boolean currentChildBuilt = null == currentChild || currentChild.m_built;
		int event = m_context.getCurrentRawEvent();
		if (event == XMLStreamConstants.START_DOCUMENT) {
			event = m_context.nextRawEvent();
			if (event == XMLStreamConstants.END_DOCUMENT) {
				completeNodeBuilding();
				return null;
			}
		}
		if (event == XMLStreamConstants.CHARACTERS && currentChildBuilt) {
			buildNodeValue();
		}
		if (!currentChildBuilt) {
			fullyBuildNode(currentChild);
		}
		event = m_context.getCurrentRawEvent();
		if (event == XMLStreamConstants.END_ELEMENT) {
			completeNodeBuilding();
			return null;
		}
		StreamableObjectNodeImpl child = null;
		event = m_context.getCurrentRawEvent();
		if (!m_built && event == XMLStreamConstants.START_ELEMENT) {
			child = createNewChild();
			addChild(child);
			m_childIndex++;
		}
		return child;
	}

	@Override
	public ObjectNode cloneNode() throws XMLStreamException {
		if (!m_built) {
			if (m_context.allowNodeBuilding()) {
				fullyBuildNode(this);
			} else {
				throw new IllegalStateException("Node building is not allowed");
			}
		}
		return super.cloneNode();
	}

	@Override
	public ObjectNode getChildNode(int index) throws XMLStreamException {
		if (!m_built) {
			ObjectNode node = this;
			while (node != null && m_childIndex != index) {
				node = nextChild();
			}
		}
		return super.getChildNode(index);
	}

	@Override
	public ObjectNode getChildNode(QName name, int index) throws XMLStreamException {
		if (!m_built) {
			if (!m_context.allowNodeBuilding()) {
				throw new IllegalStateException("Node building is not allowed");
			}
			fullyBuildNode(this);
		}
		return super.getChildNode(name, index);
	}

	@Override
	public boolean hasChildNodes() throws XMLStreamException {
		if (m_children.size() > 0) {
			return true;
		}
		if (!m_built) {
			if (!m_context.allowNodeBuilding()) {
				throw new IllegalStateException("Node building is not allowed");
			}
			nextChild();
		}
		return super.hasChildNodes();
	}

	@Override
	public List<ObjectNode> getChildNodes() throws XMLStreamException {
		if (!m_built) {
			if (!m_context.allowNodeBuilding()) {
				throw new IllegalStateException("Node building is not allowed");
			}
			fullyBuildNode(this);
		}
		return super.getChildNodes();
	}

	@Override
	public List<ObjectNode> getChildNodes(QName name) throws XMLStreamException {
		if (!m_built) {
			if (!m_context.allowNodeBuilding()) {
				throw new IllegalStateException("Node building is not allowed");
			}
			fullyBuildNode(this);
		}
		return super.getChildNodes(name);
	}

	@Override
	public Iterator<ObjectNode> getChildrenIterator() throws XMLStreamException {
		if (m_children.size() <= 0 && !m_built) {
			nextChild();
		}
		return new ChildNodeStreamingIterator(this);
	}

	@Override
	public String getNodeValue() {
		return super.getNodeValue();
	}
	
	@Override
	public int getChildNodesSize() throws XMLStreamException {
		if (!m_built) {
			if (!m_context.allowNodeBuilding()) {
				throw new IllegalStateException("Node building is not allowed");
			}
			fullyBuildNode(this);
		}
		return super.getChildNodesSize();
	}
	
	public int getNamespaceCount() {
		if (null == m_namespacePairs) {
			return 0;
		}
		return m_namespacePairs.length/2;
	}
	
	public String getNamespacePrefix(int index) {
		if (null == m_namespacePairs) {
			return null;
		}
		return m_namespacePairs[2*index];
	}
	
	public String getNamespaceURI(int index) {
		if (null == m_namespacePairs) {
			return null;
		}
		return m_namespacePairs[2*index + 1];
	}
	
	public String getNamespaceURI(String prefix) {
		if (null == m_namespacePairs) {
			return null;
		}
		for (int i=0; i<m_namespacePairs.length/2; i++) {
			if (m_namespacePairs[2*i].equals(prefix)) {
				return m_namespacePairs[2*i + 1];
			}
		}
		return null;
	}

	Iterator<ObjectNode> getIteratorForBuiltChildren() throws XMLStreamException {
		return super.getChildrenIterator();
	}

	boolean isBuilding() {
		return false == m_built;
	}

	private void buildNodeValue() throws XMLStreamException {
		StringBuilder buf = new StringBuilder();
		do {
			buf.append(m_context.getRawText());
			m_location = m_context.getRawLocation();
		} while (m_context.nextRawEvent() == XMLStreamConstants.CHARACTERS);
		setNodeValue(buf.toString());
	}
	
	private void fullyBuildNode(StreamableObjectNodeImpl child) throws XMLStreamException {
		StreamableObjectNodeImpl node = (StreamableObjectNodeImpl)child.nextChild();
		while (node != null && !node.m_built) {
			m_location = m_context.getRawLocation();
			node = (StreamableObjectNodeImpl)child.nextChild();
		}
	}
	
	/**
	 * Completes the building of the callee node by complete the end tag.
	 *
	 */
	private void completeNodeBuilding() throws XMLStreamException {
		int event = m_context.getCurrentRawEvent();
		while (!m_built) {
			switch (event) {
			case XMLStreamConstants.CHARACTERS:
				buildNodeValue();
				break;
			case XMLStreamConstants.END_DOCUMENT:
				m_built = true;
				m_location = m_context.getRawLocation();
				break;
			case XMLStreamConstants.END_ELEMENT:
				m_built = true;
				m_location = m_context.getRawLocation();
				event = m_context.nextRawEvent();
				break;
			case XMLStreamConstants.START_ELEMENT:
				createNewChild();
				break;
			default:
				throw new XMLStreamException("Unexpected xml stream event '"
						+ XMLStreamReaderUtils.xmlStreamReaderEventName(event)
						+ "'.");
			}
		}
	}
	
	private StreamableObjectNodeImpl createNewChild() throws XMLStreamException {
		// create a new child.
		m_location = m_context.getRawLocation();
		int event = m_context.getCurrentRawEvent();
		if (event != XMLStreamConstants.START_ELEMENT) {
			throw new XMLStreamException("Unexpected xml stream event. expecting '" 
					+ XMLStreamReaderUtils.xmlStreamReaderEventName(XMLStreamConstants.START_ELEMENT)
					+ "', but got '"
					+ XMLStreamReaderUtils.xmlStreamReaderEventName(event)
					+ "'.");
		}
		QName name = m_context.getRawName();
		StreamableObjectNodeImpl newChild = new StreamableObjectNodeImpl(name, this, m_context);
		int nsCnt = m_context.getRawNamespaceCount();
		if (nsCnt > 0) {
			newChild.m_namespacePairs = new String[nsCnt * 2];
			String[] msPairs = newChild.m_namespacePairs;
			for (int i=0; i<nsCnt; i++) {
				String prefix = fixNull(m_context.getRawNamespacePrefix(i));
				msPairs[2*i] = prefix;
				String uri = fixNull(m_context.getRawNamespaceURI(i));
				msPairs[2*i + 1] = uri;
			}
		}
		int attrCnt = m_context.getRawAttributeCount();
		for (int i=0; i<attrCnt; i++) {
			QName attrName = new QName(m_context.getRawAttributeNamespace(i), 
							m_context.getRawAttributeLocalName(i), m_context.getRawAttributePrefix(i));
			String attrValue = m_context.getRawAttributeValue(i);
			ObjectNodeImpl attrNode = new ObjectNodeImpl(attrName, newChild, true);
			attrNode.setNodeValue(attrValue);
			newChild.addAttribute(attrNode);
		}
		m_location = m_context.getRawLocation();
		event = m_context.nextRawEvent();
		if (event == XMLStreamConstants.CHARACTERS) {
			newChild.buildNodeValue();
		}
		return newChild;
	}


	private static String fixNull(String s) {
        if (s == null) {
        	return "";
        }

        return s;
    }
}
