/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.ordered;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.BaseXMLStreamReader;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVLine;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVPathPart;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVStreamParser;


/**
 * @author ichernyshev
 */
public class OrderedNVStreamReader extends BaseXMLStreamReader {

	private final InputStream m_is;
	private final NVStreamParser m_parser;
	private final NVPathPart m_rootXmlName;

	private NVLineSwinger m_swinger;
	private ArrayList<AttributeHolder> m_attributes = new ArrayList<AttributeHolder>();

	public OrderedNVStreamReader(InputStream is, NamespaceConvention convention,
		Charset charset, QName rootXmlName, Map<String, String> option)
	{
		super(convention, option);

		if (is == null || charset == null || rootXmlName == null) {
			throw new NullPointerException();
		}

		m_is = is;
		m_parser = new NVStreamParser(is, charset, convention, m_doubleQuoteDelimited);
		m_rootXmlName = new NVPathPart(rootXmlName, 0, false);

		m_event = START_DOCUMENT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#close()
	 */
	public void close() throws XMLStreamException {
		try {
			m_is.close();
		} catch (IOException e) {
			throw new XMLStreamException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#getElementText()
	 */
	public String getElementText() throws XMLStreamException {
		if (m_swinger == null) {
			throw new IllegalStateException("No NV line currently selected");
		}

		return m_swinger.getCurrentValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#getNamespaceContext()
	 */
	public NamespaceContext getNamespaceContext() {
		return m_convention;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#getText()
	 */
	public String getText() {
		if (m_swinger == null) {
			throw new IllegalStateException("No NV line currently selected");
		}

		return m_swinger.getCurrentValue();
	}

	@Override
	public int getAttributeCount() {
		return m_attributes.size();
	}

	@Override
    public QName getAttributeName(int n) {
		AttributeHolder info = m_attributes.get(n);
		return info.m_name;
    }
	@Override
    public String getAttributeLocalName(int n) {
		AttributeHolder info = m_attributes.get(n);
		return info.m_name.getLocalPart();
    }

	@Override
    public String getAttributeNamespace(int n) {
		AttributeHolder info = m_attributes.get(n);
		return info.m_name.getNamespaceURI();
    }

	@Override
    public String getAttributePrefix(int n) {
		AttributeHolder info = m_attributes.get(n);
		return info.m_name.getPrefix();
    }

	@Override
    public String getAttributeValue(int n) {
		AttributeHolder info = m_attributes.get(n);
		return info.m_value;
    }

	@Override
	protected QName internalGetName() {
		if (m_swinger == null) {
			throw new IllegalStateException("No NV line currently selected");
		}

		return m_swinger.getQName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#next()
	 */
	public int next() throws XMLStreamException {
		m_attributes.clear();

		if (m_event == START_DOCUMENT) {
			NVLine firstLine = readLine(null, null);
			if (firstLine == null) {
				// empty document
				m_event = END_DOCUMENT;
				return m_event;
			}

			m_swinger = new NVLineSwinger(firstLine, NVLine.createEmpty());

			QName name = getNextName();
			if (name == null) {
				// TODO: must an attribute only, this is probavly an error condition
				m_event = END_DOCUMENT;
				return m_event;
			}

					m_event = START_ELEMENT;
					return m_event;
				}

		if (m_event == START_ELEMENT) {
			QName name = getNextName();
			if (name == null) {
				// reached the value
				m_event = CHARACTERS;
				return m_event;
			}

			return m_event;
		}

		if (m_event == CHARACTERS) {
			// we are done with one swing, need to get ready for the next one
			NVLine previousLine = m_swinger.getCurrentLine();

			NVLine currentLine = readLine(previousLine, m_swinger.getPreviousLine());
			if (currentLine == null) {
				// end of file, use empty line
				currentLine = NVLine.createEmpty();
			}

			m_swinger = new NVLineSwinger(currentLine, previousLine);

			QName name = getNextName();
			if (name == null) {
				// TODO: must an attribute only, this is probavly an error condition
				m_event = END_DOCUMENT;
				return m_event;
			}

			m_event = m_swinger.passedValley() ? START_ELEMENT : END_ELEMENT;
			return m_event;
		}

		if (m_event == END_ELEMENT) {
			QName name = getNextName();
			if (name == null) {
				// the next line does not exist...
				m_event = END_DOCUMENT;
				return m_event;
			}

			m_event = m_swinger.passedValley() ? START_ELEMENT : END_ELEMENT;
			return m_event;
		}

		throw new XMLStreamException("Not able to find the next event, " +
			"unexpected event type " + m_event);
	}
	
	private QName getNextName() throws XMLStreamException {
		if (m_swinger == null) {
			return null;
		}

		QName result = m_swinger.getNextName();

		if (m_swinger.shouldAddAsAttribute()) {
			// found attributes, read them and switch to real element after them
			createAttributeList();
			if (m_swinger == null) {
				return null;
			}

			result = m_swinger.getQName();
		} else if (m_swinger.isAttributeLevelElement()) {
			// add nil attribute if the value is NULL
			String currentValue = m_swinger.getCurrentValue();
			if (BindingConstants.NULL_VALUE_STR.equals(currentValue)) {
				m_attributes.add(new AttributeHolder(
					BindingConstants.NILLABLE_ATTRIBUTE_QNAME, "true"));
			}
		}

		return result;
	}
	

	/**
	 * Reads all the attributes NVLines from the input until it hits
	 * the next NV Line that is not an attribute. returns the QName.
	 */
	private void createAttributeList() throws XMLStreamException {
		NVLine oldLine = m_swinger.getCurrentLine();
		NVLine newLine = oldLine;
		NVLine prevLine = newLine;
		boolean hasNilAttribute = false;
		do {
			AttributeHolder attr = new AttributeHolder(newLine.peek(), newLine.getValue());
			if (!hasNilAttribute) {
				hasNilAttribute = BindingConstants.NILLABLE_ATTRIBUTE_QNAME.equals(attr.m_name) && "true".equalsIgnoreCase(attr.m_value);
			}
			m_attributes.add(attr);
			prevLine = newLine;
			newLine = readLine(newLine, m_swinger.getPreviousLine());
		} while (newLine != null && newLine.isAttributeAtSameLevel());

		if (newLine != null) {
			m_swinger = new NVLineSwinger(newLine, oldLine);
			m_swinger.swingToLevel(oldLine.getDepth() - 2);
		} else {
			int elementPathDepth = prevLine.getDepth() - 1;
			/*
			 * In the case of nil = true, create an element with NULL value. otherwise create an empty line.
			 */
			if (hasNilAttribute){
				NVPathPart[] elementPathParts = new NVPathPart[elementPathDepth];
				for (int i=0; i< elementPathDepth; i++) {
					elementPathParts[i] = prevLine.getPathPart(i);
				}
				NVLine elementLine = new NVLine(elementPathParts, elementPathDepth, BindingConstants.NULL_VALUE_STR, elementPathDepth, false);
				m_swinger = new NVLineSwinger(elementLine, prevLine);
			} else {
				m_swinger = new NVLineSwinger(NVLine.createEmpty(), prevLine);
			}
			m_swinger.swingToLevel(elementPathDepth - 1);
		}
	}

	private NVLine readLine(NVLine prevLine, NVLine reuseLine) throws XMLStreamException {
		boolean hasLine = m_parser.parseLine();
		if (!hasLine) {
			// end of stream
			return null;
		}

		return NVLine.createNext(m_parser, prevLine, reuseLine, m_rootXmlName);
	}

	private static class AttributeHolder {
		final QName m_name;
		final String m_value;

		AttributeHolder(QName name, String value) {
			m_name = name;
			m_value = value;
		}
	}
}
