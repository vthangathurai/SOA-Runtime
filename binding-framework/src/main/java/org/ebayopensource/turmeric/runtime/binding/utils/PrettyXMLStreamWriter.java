/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.utils;

import java.util.Stack;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * The XMLStreamWriter to write formated XML output.
 * 
 * @author mpoplacenel wdeng
 *
 */
public class PrettyXMLStreamWriter extends WrapperXMLStreamWriterSupport {
	/**
	 * Default intentation.
	 */
	public static final String DEFAULT_INDENT = "    ";
	
	private static final String NL = System.getProperty("line.separator");
	
	private final String m_indent;
	
	private int m_level;
	
	/**
	 * Stack keeping track of the kids status of the previous node, to avoid 
	 * indenting the leaves. 
	 * @see #m_hasKidsCurrent for more info on the state
	 */
	protected final Stack<Boolean> m_hasKidsStack = new Stack<Boolean>();
	
	/**
	 * Keeps track of the kid (=sub-element) status of the current node. Status is one of: 
	 * <ul>
	 * <li><code>null</code> - nothing under this node has been processed [just yet]
	 * <li><code>true</code> - there was at least one sub-element under this node.
	 * <li><code>false</code> - there were characters and/or CDATA, but no sub-element. 
	 * </ul> 
	 */
	protected Boolean m_hasKidsCurrent;
	
	/**
	 * Constructor with {@link #DEFAULT_INDENT default} indentation string.
	 *  
	 * @param xmlStreamWriter the stream writer to delegate to. 
	 */
	public PrettyXMLStreamWriter(XMLStreamWriter xmlStreamWriter) {
		this(xmlStreamWriter, DEFAULT_INDENT);
	}

	/**
	 * Full constructor.
	 *  
	 * @param xmlStreamWriter the stream writer to delegate to. 
	 * @param indent the indentation string.
	 */
	public PrettyXMLStreamWriter(XMLStreamWriter xmlStreamWriter, String indent) {
		super(xmlStreamWriter);
		m_indent = indent;
	}

	@Override
	public void writeCData(String data) throws XMLStreamException {
		preWriteChars();
		super.writeCData(data);
	}

	@Override
	public void writeCharacters(char[] text, int start, int len)
			throws XMLStreamException {
		// size is level or level + 1, if any write within parent already occurred 
		preWriteChars();
		super.writeCharacters(text, start, len);
	}

	@Override
	public void writeCharacters(String text) throws XMLStreamException {
		preWriteChars();
		super.writeCharacters(text);
	}

	/**
	 * Sets the parent node kid status to "none", if no status is yet set.
	 */
	private void preWriteChars() {
		assertCorrectLevel();
		if (m_hasKidsCurrent == null) m_hasKidsCurrent = false;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.utils.WrapperXMLStreamWriterSupport#writeEmptyElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void writeEmptyElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
		super.writeEmptyElement(prefix, localName, namespaceURI);
		indent();
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.utils.WrapperXMLStreamWriterSupport#writeEmptyElement(java.lang.String, java.lang.String)
	 */
	@Override
	public void writeEmptyElement(String namespaceURI, String localName)
			throws XMLStreamException {
		super.writeEmptyElement(namespaceURI, localName);
		indent();
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.utils.WrapperXMLStreamWriterSupport#writeEmptyElement(java.lang.String)
	 */
	@Override
	public void writeEmptyElement(String localName)
			throws XMLStreamException {
		super.writeEmptyElement(localName);
		m_hasKidsCurrent = true;
		indent();
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.utils.WrapperXMLStreamWriterSupport#writeEndDocument()
	 */
	@Override
	public void writeEndDocument() throws XMLStreamException {
		indent();
		super.writeEndDocument();
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.utils.WrapperXMLStreamWriterSupport#writeEndElement()
	 */
	@Override
	public void writeEndElement() throws XMLStreamException {
		preWriteEndElement();
		super.writeEndElement();
	}

	/**
	 * Processing to be performed before actually writing the end element.
	 * @throws XMLStreamException
	 */
	private void preWriteEndElement() throws XMLStreamException {
		assertCorrectLevel();
		boolean hasKids = false;
		if (m_hasKidsCurrent != null) hasKids = m_hasKidsCurrent.booleanValue();
		m_hasKidsCurrent = m_hasKidsStack.pop();
		m_level--;
		if (hasKids) {
			indent();
		}
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.utils.WrapperXMLStreamWriterSupport#writeStartDocument()
	 */
	@Override
	public void writeStartDocument() throws XMLStreamException {
		super.writeStartDocument();
		indent();
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.utils.WrapperXMLStreamWriterSupport#writeStartDocument(java.lang.String, java.lang.String)
	 */
	@Override
	public void writeStartDocument(String encoding, String version)
			throws XMLStreamException {
		super.writeStartDocument(encoding, version);
		indent();
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.utils.WrapperXMLStreamWriterSupport#writeStartDocument(java.lang.String)
	 */
	@Override
	public void writeStartDocument(String version)
			throws XMLStreamException {
		super.writeStartDocument(version);
		indent();
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.utils.WrapperXMLStreamWriterSupport#writeStartElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void writeStartElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
		preWriteStartElement();
		super.writeStartElement(prefix, localName, namespaceURI);
	}

	/**
	 * @throws XMLStreamException
	 */
	private void preWriteStartElement() throws XMLStreamException {
		assertCorrectLevel();
		
		if (m_level > 0) indent();
		
		m_hasKidsStack.push(true);
		m_hasKidsCurrent = null;
		m_level++;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.utils.WrapperXMLStreamWriterSupport#writeStartElement(java.lang.String, java.lang.String)
	 */
	@Override
	public void writeStartElement(String namespaceURI, String localName)
			throws XMLStreamException {
		preWriteStartElement();
		super.writeStartElement(namespaceURI, localName);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.binding.utils.WrapperXMLStreamWriterSupport#writeStartElement(java.lang.String)
	 */
	@Override
	public void writeStartElement(String localName)
			throws XMLStreamException {
		preWriteStartElement();
		super.writeStartElement(localName);
	}

	/**
	 * @param m_level
	 */
	private void assertCorrectLevel() {
		assert m_hasKidsStack.size() >= m_level && m_hasKidsStack.size() <= m_level + 1
			: "hasKids size " + m_hasKidsStack.size() + " should be between levels " 
			+ (m_level - 1) + " and " + m_level;
	}

	private void indent() throws XMLStreamException {
		super.writeCharacters(NL);
		doIndent();
	}

	private void doIndent() throws XMLStreamException {
		for (int j = 0; j < m_level; j++) {
			super.writeCharacters(m_indent);
		}
	}

}
