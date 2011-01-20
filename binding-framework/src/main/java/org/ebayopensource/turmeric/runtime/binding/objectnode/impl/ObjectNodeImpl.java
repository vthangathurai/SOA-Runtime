/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.objectnode.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNodeType;


public class ObjectNodeImpl implements ObjectNode {
	public static final QName ROOT_NODE_QNAME = new QName("root");
	public static final ObjectNodeImpl EMPTY_ROOT_NODE = new ObjectNodeImpl(ROOT_NODE_QNAME, null);
	 
	private QName m_name;
	private String m_value;
	private boolean m_isNull;
	protected ArrayList<ObjectNode> m_children;
	protected ArrayList<ObjectNode> m_attributes = null;
	protected Location m_location;
	private ObjectNode m_parent;
	protected boolean m_isAttribute = false;

	public static final ObjectNodeImpl createEmptyRootNode() {
		return new ObjectNodeImpl(ROOT_NODE_QNAME, null);
	}
	
	public ObjectNodeImpl(QName name, ObjectNode parent) {
		this(name, parent, false);
	}
	
	public ObjectNodeImpl(QName name, ObjectNode parent, boolean isAttribute) {
		m_name = name;
		m_isAttribute = isAttribute;
		handleAtMarkedElementName();
		m_parent = parent;
		m_children = new ArrayList<ObjectNode>();
		m_attributes = new ArrayList<ObjectNode>();
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @throws XMLStreamException 
	 */
	public List<ObjectNode> getChildNodes(QName name) throws XMLStreamException {
		ArrayList<ObjectNode> children = new ArrayList<ObjectNode>();
		boolean found = false;
		for (Iterator<ObjectNode> it = m_children.iterator(); it.hasNext();) {
			ObjectNode child = it.next();
			if (sameQName(child.getNodeName(), name)) {
				children.add(child);
				found = true;
			} else if (found) {
				// Assumption here is that children with the same name are 
				// listed one after each other in the list. Once we found one
				// child not with the same name, we can stop.
				break;
			}
		}
		return found ? children : null;
	}
	
	/**
	 * Gets (index + 1)st child with the same name.
	 * 
	 * @param name
	 * @param index
	 * @return
	 * @throws XMLStreamException 
	 */
	public ObjectNode getChildNode(QName name, int index) throws XMLStreamException {
		int i = getChildNodeIndex(name, index);
		return i > -1 ? m_children.get(i) : null;
	}

	/**
	 * Gets child at the index position of the m_children array.
	 * 
	 * @param index
	 * @return
	 * @throws XMLStreamException 
	 */
	public ObjectNode getChildNode(int index) throws XMLStreamException {
		return m_children.get(index);
	}
	
	/**
	 * Returns the index of the child node in the m_children list.
	 * 
	 * @param name
	 * @param index
	 * @return
	 */
	private int getChildNodeIndex(QName name, int index) {
		boolean found = false;
		int count = 0;
		for (int i=0; i<m_children.size(); i++) {
			ObjectNode child = m_children.get(i);
			if (sameQName(child.getNodeName(), name)) {
				if (count == index) {
					return i;
				}
				count++;
			} else if (found) {
				// Assumption here is that children with the same name are 
				// listed one after each other in the list. Once we found one
				// child not with the same name, we can stop.
				break;
			}
		}
		return -1;
	}

	/**
	 * Set the child and returns its index in the child array;
	 * 
	 * @param name
	 * @param child
	 * @return
	 */
	public int setChild(QName name, ObjectNodeImpl child) {
		return setChild(name, Integer.MAX_VALUE, child);
	}

	public int addChild(ObjectNodeImpl child) {
		m_children.add(child);
		return m_children.size() - 1;
	}
	
	/**
	 * Set the child and returns its index in the whole children list;
	 * 
	 * @param name
	 * @param index
	 * @param child
	 * @return
	 */
	public int setChild(QName name, int index, ObjectNodeImpl child) {
		if (null == name) {
			return -1;
		}
		int i = getChildNodeIndex(name, index);
		if (i < 0) {
			m_children.add(index, child);
		} else {
			m_children.set(index, child);
		}
		return index;
	}

	public boolean hasChildNode(QName name, int index) {
		return getChildNodeIndex(name, index) > -1;
	}
	
	public boolean hasChildNodes() throws XMLStreamException {
		return m_children.size() > 0;
	}

	public void setName(QName name) {
		this.m_name = name;
	}

	public String getNodeValue() {
		return m_value;
	}

	public void setNodeValue(Object value) {
		if (value instanceof String) {
			this.m_value = (String) value;
			return;
		}
		throw new IllegalArgumentException("Expects " + String.class.getName());
	}

	public Iterator<ObjectNode> getChildrenIterator() throws XMLStreamException {
		return new ChildNodeIterator(this);
	}

	public QName getNodeName() {
		return m_name;
	}

	public ObjectNodeType getNodeType() {
		return ObjectNodeType.XML;
	}

	public Object getUnderlyingRawNode() {
		throw new UnsupportedOperationException(this.getClass().getName()
				+ ".getUnderlyingRawNode");
	}

	public ObjectNode getParentNode() {
		return m_parent;
	}

	public void setParentNode(ObjectNode parent) {
		this.m_parent = parent;
	}

	/**
	 * Returns the child nodes. If there are no child nodes, it returns null.
	 * 
	 * @return List of child nodes.
	 */
	public List<ObjectNode> getChildNodes() throws XMLStreamException {
		return Collections.unmodifiableList(m_children);
	}

	public int getChildNodesSize() throws XMLStreamException {
		return m_children.size();
	}

	public void insertChildAt(ObjectNode node, int index)
			throws IndexOutOfBoundsException {
		throw new UnsupportedOperationException(this.getClass().getName()
				+ ".insertChildAt");
	}

	public void replaceChildAt(ObjectNode node, int index)
			throws IndexOutOfBoundsException {
		throw new UnsupportedOperationException(this.getClass().getName()
				+ ".replaceChildAt");
	}

	public ObjectNode cloneNode() throws XMLStreamException {
		ObjectNodeImpl clone = new ObjectNodeImpl(this.m_name, this.m_parent);
		clone.m_value = this.m_value;
		clone.m_children = this.m_children;
		return clone;
	}
	
	public void setIsNull(boolean isNull) {
		m_isNull = isNull;
	}
	
	public boolean getIsNull() {
		return m_isNull;
	}
	
	public boolean isAttribute() {
		return m_isAttribute;
	}
	
	public boolean hasAttributes() {
		if (m_attributes == null) {
			return false;
		}
		return m_attributes.size() > 0;
	}
	
	public int getAttributeCount() {
		if (m_attributes == null) {
			return 0;
		}
		return m_attributes.size();
	}

	/**
	 * 
	 * @return all the attributes of this node.
	 */
	public List<ObjectNode> getAttributes() {
		return m_attributes;
	}
	
	public ObjectNode getAttribute(int n) {
		return m_attributes == null ? null : m_attributes.get(n);
	}

	public void addAttribute(ObjectNode o) {
		if (null == m_attributes) {
			m_attributes = new ArrayList<ObjectNode>();
		}
		m_attributes.add(o);
	}
	
	public Location getLocation() {
		return m_location;
	}
	
	public String getTree() throws XMLStreamException {
		StringBuffer msg = new StringBuffer();
		buildString(msg, 0);
		return msg.toString();
	}
	
	public void buildString(StringBuffer msg, int level) throws XMLStreamException {
		for (int i = 0; i < level; i++) {
			msg.append('\t');
		}
		QName node = getNodeName();
		String prefix = node.getPrefix();
		if (null != prefix && prefix.length() > 0) {
			msg.append(node.getPrefix());
			msg.append(".");
		}
		msg.append(node.getLocalPart());
		msg.append(":");
		String value = null;
		value = getNodeValue();
		if (null != value) {
			msg.append(value);
			msg.append("\n");
			return;
		}
		Iterator<ObjectNode> iter = getChildrenIterator();
		if (!iter.hasNext()) {
			msg.append("{}\n");
			return;
		}
		msg.append("{\n");
		while (iter.hasNext()) {
			ObjectNodeImpl child = (ObjectNodeImpl)iter.next();
			child.buildString(msg, level + 1);
		}
		for (int i = 0; i < level; i++) {
			msg.append('\t');
		}
		msg.append("}\n");
	}

	@Override
	public String toString() {
		return getNodeName().toString();
	}

	private boolean sameQName(QName n1, QName n2) {
		if (null == n1) {
			return null == n2;
		}
		return equals(n1.getLocalPart(), n2.getLocalPart()) 
			&& equals(n1.getNamespaceURI(), n2.getNamespaceURI());
	}
	
	private boolean equals(Object o1, Object o2) {
		if (null == o1) {
			return null == o2;
		}
		return o1.equals(o2);
	}
	/** 
	 * Sets m_isAttribute flag from element name.  In NV and JSON format,  we prefix "@" in element name to indicate
	 * it is an attribute.  This logic is to based on the "@" mark to set m_isAttribute and removes "@" from element name.
	 */
	private void handleAtMarkedElementName() {
		String localName = m_name.getLocalPart();
		boolean hasAtMark = (localName != null && localName.startsWith(BindingConstants.ATTRIBUTE_MARK));
		if (hasAtMark) {
			localName = localName.substring(1);
			m_name = new QName(m_name.getNamespaceURI(), localName, m_name.getPrefix());
		}
		m_isAttribute = (m_isAttribute || hasAtMark);
	}
	
}
