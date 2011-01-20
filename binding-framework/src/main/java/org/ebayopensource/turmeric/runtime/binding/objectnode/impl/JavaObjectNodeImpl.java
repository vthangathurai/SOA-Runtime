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
package org.ebayopensource.turmeric.runtime.binding.objectnode.impl;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNodeType;


/**
 * @author wdeng
 *
 */
public class JavaObjectNodeImpl implements ObjectNode {

	private QName m_nodeName;
	private Object m_javaObject;
	
	public JavaObjectNodeImpl(QName nodeName, Object javaObject) {
		m_nodeName = nodeName;
		m_javaObject = javaObject;
	}
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#cloneNode()
	 */
	public ObjectNode cloneNode() throws XMLStreamException {
		return new JavaObjectNodeImpl(m_nodeName, m_javaObject);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#getAttribute(int)
	 */
	public ObjectNode getAttribute(int n) {
		throw new UnsupportedOperationException(JavaObjectNodeImpl.class.getName() + ".getAttribute(int n).");
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#getAttributeCount()
	 */
	public int getAttributeCount() {
		throw new UnsupportedOperationException(JavaObjectNodeImpl.class.getName() + ".getAttributeCount().");
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#getAttributes()
	 */
	public List<ObjectNode> getAttributes() {
		throw new UnsupportedOperationException(JavaObjectNodeImpl.class.getName() + ".getAttributes().");
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#getChildNodes()
	 */
	public List<ObjectNode> getChildNodes() throws XMLStreamException {
		throw new UnsupportedOperationException(JavaObjectNodeImpl.class.getName() + ".getChildNodes().");
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#getChildNodesSize()
	 */
	public int getChildNodesSize() throws XMLStreamException {
		throw new UnsupportedOperationException(JavaObjectNodeImpl.class.getName() + ".getChildNodesSize().");
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#getChildrenIterator()
	 */
	public Iterator<ObjectNode> getChildrenIterator() throws XMLStreamException {
		throw new UnsupportedOperationException(JavaObjectNodeImpl.class.getName() + ".getChildrenIterator().");
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#getIsNull()
	 */
	public boolean getIsNull() {
		return null == m_javaObject;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#getNodeName()
	 */
	public QName getNodeName() {
		return m_nodeName;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#getNodeType()
	 */
	public ObjectNodeType getNodeType() {
		return ObjectNodeType.JAVA;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#getNodeValue()
	 */
	public Object getNodeValue() {
		return m_javaObject;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#getParentNode()
	 */
	public ObjectNode getParentNode() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#getUnderlyingRawNode()
	 */
	public Object getUnderlyingRawNode() {
		throw new UnsupportedOperationException(JavaObjectNodeImpl.class.getName() + ".getUnderlyingRawNode().");
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#hasAttributes()
	 */
	public boolean hasAttributes() {
		throw new UnsupportedOperationException(JavaObjectNodeImpl.class.getName() + ".hasAttributes().");
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#hasChildNodes()
	 */
	public boolean hasChildNodes() throws XMLStreamException {
		throw new UnsupportedOperationException(JavaObjectNodeImpl.class.getName() + ".hasChildNodes().");
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#insertChildAt(org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode, int)
	 */
	public void insertChildAt(ObjectNode node, int index)
			throws IndexOutOfBoundsException {
		throw new UnsupportedOperationException(JavaObjectNodeImpl.class.getName() + ".insertChildAt(ObjectNode node, int index).");
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#isAttribute()
	 */
	public boolean isAttribute() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode#replaceChildAt(org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode, int)
	 */
	public void replaceChildAt(ObjectNode node, int index)
			throws IndexOutOfBoundsException {
		throw new UnsupportedOperationException(JavaObjectNodeImpl.class.getName() + ".replaceChildAt(ObjectNode node, int index).");

	}

	/* (non-Javadoc)
	 * @see s#setNodeValue(java.lang.Object)
	 */
	public void setNodeValue(Object value) {
		m_javaObject = value;

	}

}
