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

import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.StreamableObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ChildNodeIterator;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;


/**
 * @author wdeng
 */
public class ChildNodeStreamingIterator extends ChildNodeIterator implements Iterator<ObjectNode> {

	public ChildNodeStreamingIterator(ObjectNodeImpl node) {
		super(node);
		assert(m_myFriend instanceof StreamableObjectNode);
	}

	@Override
	public boolean hasNext() {
		if (super.hasNext()) {
			return true;
		}
		try {
			((StreamableObjectNode)m_myFriend).nextChild();
		} catch (XMLStreamException e) {
			throw new RuntimeException("No such Element.", e);
		}
		return super.hasNext();
	}

	@Override
	public ObjectNode next() {
		if (super.hasNext()) {
			return super.next();
		}
		try {
			return ((StreamableObjectNode)m_myFriend).nextChild();
		} catch (XMLStreamException e) {
			throw new RuntimeException("No such Element.", e);
		}
	}
}
