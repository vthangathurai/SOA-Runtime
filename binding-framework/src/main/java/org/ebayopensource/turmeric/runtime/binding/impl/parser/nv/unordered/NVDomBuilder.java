/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.unordered;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVLine;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVPathPart;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVStreamParser;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;


/**
 * @author wdeng
 */
public class NVDomBuilder {
	
	private NamespaceConvention m_convention;
	private NVPathPart m_rootXmlName;
	private Map<String, String> m_options;
	
	public NVDomBuilder(NamespaceConvention namespaceConvertion, QName rootXmlName) {
		if (null == rootXmlName) {
			throw new NullPointerException("NVDomBuilder constructor requires rootXmlName.");
		}
		if (null == namespaceConvertion) {
			throw new NullPointerException("NVDomBuilder constructor requires namespaceConvertion.");
		}
		m_convention = namespaceConvertion;
		m_rootXmlName = new NVPathPart(rootXmlName, 0, false);
	}

	public NVDomBuilder(NamespaceConvention namespaceConvention, QName rootXmlName, Map<String, String> options) {
		this(namespaceConvention, rootXmlName);
		
		this.m_options = options;
	}
	
	public ObjectNodeImpl createDom(QName rootName, InputStream inputStream, Charset charset)
		throws XMLStreamException
	{
		ObjectNodeImpl root = new ObjectNodeImpl(rootName, null);

		if (null == inputStream) {
			throw new NullPointerException("NVDomBuilder.createDom() requires non-null inputStream.");
		}

		if (null == charset) {
			throw new NullPointerException("NVDomBuilder.createDom() requires charset.");
		}
		
		ObjectNodeImpl topMostElement = new ObjectNodeImpl(m_rootXmlName, root);
		root.addChild(topMostElement);

		NVStreamParser lineParser = new NVStreamParser(inputStream, charset, m_convention, m_options);

		NVLine line = readLine(lineParser, null, null);
		while (line != null) {
			// processing one line
			ObjectNodeImpl currentNode = topMostElement;
			boolean isAttribute = line.isAttribute();

			int depth = line.getDepth();
			for (int i=0; i<depth; i++) {
				NVPathPart part = line.getPathPart(i);
				String value = line.getValue();

				ObjectNodeImpl child;
				if (i == 0) {
					child = topMostElement;
				} else {
					if (isAttribute && i == depth-1) {
						child = new ObjectNodeImpl(part, currentNode);
						currentNode.addAttribute(child);
					} else {
						child = (ObjectNodeImpl)currentNode.getChildNode(part, part.getIndex());

						if (null == child) {
							child = new ObjectNodeImpl(part, currentNode);
							currentNode.setChild(part, part.getIndex(), child);
						}
					}
				}

				if (i == depth-1) {
					boolean isNull = "null".equals(value);

					child.setNodeValue(value);
					child.setIsNull(isNull);

					if (isNull && !isAttribute) {
			    		ObjectNode nilAttr = new ObjectNodeImpl(
			    			BindingConstants.NILLABLE_ATTRIBUTE_QNAME, child);
			    		nilAttr.setNodeValue("true");
			    		child.addAttribute(nilAttr);
			    	}
				}

				currentNode = child;
			}

			line = readLine(lineParser, null, null);
		}

		return root;
	}

	private NVLine readLine(NVStreamParser parser, NVLine prevLine, NVLine reuseLine) throws XMLStreamException {
		boolean hasLine = parser.parseLine();
		if (!hasLine) {
			// end of stream
			return null;
		}

		return NVLine.createNext(parser, prevLine, reuseLine, m_rootXmlName);
	}
}
