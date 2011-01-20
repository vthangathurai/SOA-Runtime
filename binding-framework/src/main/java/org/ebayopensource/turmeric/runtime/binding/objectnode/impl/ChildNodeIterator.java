/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.objectnode.impl;

import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;


public class ChildNodeIterator implements Iterator<ObjectNode> {

	protected ObjectNodeImpl m_myFriend = null;
	protected int m_index = 0;	// Index of next child.

	public ChildNodeIterator(ObjectNodeImpl node) {
		super();
		m_myFriend = node;
	}

	public boolean hasNext() {
		return m_myFriend.m_children.size() > m_index;
	}

	public ObjectNode next() {
		try {
			if (!hasNext()) {
				return null;
			}
			return m_myFriend.getChildNode(m_index++);
		} catch (XMLStreamException e) {
			throw new RuntimeException("Not able to find element", e);
		}
	}

	public ObjectNodeImpl peek() {
		try {
			if (m_index < 1) {
				return null;
			}
			return (ObjectNodeImpl)m_myFriend.getChildNode(m_index - 1);
		} catch (XMLStreamException e) {
			throw new RuntimeException("Not able to find element", e);
		}
	}

	public void remove() {
		throw new UnsupportedOperationException(this.getClass().getName()
				+ ".remove()");
	}
}
