/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.utils;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Support for an XMLStreamWriter implementation that wraps another XMLStreamWriter
 * and delegates some of the behavior to it. This class delegates EVERYTYHING to it. 
 * 
 * @author mpoplacenel
 */
/**
 * @author wdeng
 *
 */
public abstract class WrapperXMLStreamWriterSupport implements XMLStreamWriter {
	
	/**
	 * The wrapped XML stream writer. 
	 */
	protected final XMLStreamWriter m_xmlStreamWriter;
	
	/**
	 * Constructor. 
	 * 
	 * @param xmlStreamWriter the stream writer to wrap. 
	 */
	public WrapperXMLStreamWriterSupport(XMLStreamWriter xmlStreamWriter) {
		m_xmlStreamWriter = xmlStreamWriter;
	}

	/**
	 * Getter for the wrapped XMLStreamWriter.
	 * @return the wrapped XMLStreamWriter.
	 */
	XMLStreamWriter getXmlStreamWriter() {
		return m_xmlStreamWriter;
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#close()
	 */
	@Override
	public void close() throws XMLStreamException {
		m_xmlStreamWriter.close();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#flush()
	 */
	@Override
	public void flush() throws XMLStreamException {
		m_xmlStreamWriter.flush();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#getNamespaceContext()
	 */
	@Override
	public NamespaceContext getNamespaceContext() {
		return m_xmlStreamWriter.getNamespaceContext();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#getPrefix(java.lang.String)
	 */
	@Override
	public String getPrefix(String uri) throws XMLStreamException {
		return m_xmlStreamWriter.getPrefix(uri);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#getProperty(java.lang.String)
	 */
	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return m_xmlStreamWriter.getProperty(name);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#setDefaultNamespace(java.lang.String)
	 */
	@Override
	public void setDefaultNamespace(String uri) throws XMLStreamException {
		m_xmlStreamWriter.setDefaultNamespace(uri);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#setNamespaceContext(javax.xml.namespace.NamespaceContext)
	 */
	@Override
	public void setNamespaceContext(NamespaceContext context)
	throws XMLStreamException {
		m_xmlStreamWriter.setNamespaceContext(context);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#setPrefix(java.lang.String, java.lang.String)
	 */
	@Override
	public void setPrefix(String prefix, String uri)
	throws XMLStreamException {
		m_xmlStreamWriter.setPrefix(prefix, uri);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void writeAttribute(String prefix, String namespaceURI,
			String localName, String value) 
	throws XMLStreamException {
		m_xmlStreamWriter.writeAttribute(prefix, namespaceURI, localName,
				value);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void writeAttribute(String namespaceURI, String localName, String value) 
	throws XMLStreamException {
		m_xmlStreamWriter.writeAttribute(namespaceURI, localName, value);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String, java.lang.String)
	 */
	@Override
	public void writeAttribute(String localName, String value)
			throws XMLStreamException {
		m_xmlStreamWriter.writeAttribute(localName, value);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeCData(java.lang.String)
	 */
	@Override
	public void writeCData(String data) throws XMLStreamException {
		m_xmlStreamWriter.writeCData(data);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeCharacters(char[], int, int)
	 */
	@Override
	public void writeCharacters(char[] text, int start, int len)
			throws XMLStreamException {
		m_xmlStreamWriter.writeCharacters(text, start, len);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeCharacters(java.lang.String)
	 */
	@Override
	public void writeCharacters(String text) throws XMLStreamException {
		m_xmlStreamWriter.writeCharacters(text);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeComment(java.lang.String)
	 */
	@Override
	public void writeComment(String data) throws XMLStreamException {
		m_xmlStreamWriter.writeComment(data);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeDefaultNamespace(java.lang.String)
	 */
	@Override
	public void writeDefaultNamespace(String namespaceURI)
			throws XMLStreamException {
		m_xmlStreamWriter.writeDefaultNamespace(namespaceURI);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeDTD(java.lang.String)
	 */
	@Override
	public void writeDTD(String dtd) throws XMLStreamException {
		m_xmlStreamWriter.writeDTD(dtd);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void writeEmptyElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
		m_xmlStreamWriter.writeEmptyElement(prefix, localName, namespaceURI);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String, java.lang.String)
	 */
	@Override
	public void writeEmptyElement(String namespaceURI, String localName)
			throws XMLStreamException {
		m_xmlStreamWriter.writeEmptyElement(namespaceURI, localName);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String)
	 */
	@Override
	public void writeEmptyElement(String localName)
			throws XMLStreamException {
		m_xmlStreamWriter.writeEmptyElement(localName);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeEndDocument()
	 */
	@Override
	public void writeEndDocument() throws XMLStreamException {
		m_xmlStreamWriter.writeEndDocument();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeEndElement()
	 */
	@Override
	public void writeEndElement() throws XMLStreamException {
		m_xmlStreamWriter.writeEndElement();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeEntityRef(java.lang.String)
	 */
	@Override
	public void writeEntityRef(String name) throws XMLStreamException {
		m_xmlStreamWriter.writeEntityRef(name);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeNamespace(java.lang.String, java.lang.String)
	 */
	@Override
	public void writeNamespace(String prefix, String namespaceURI)
			throws XMLStreamException {
		m_xmlStreamWriter.writeNamespace(prefix, namespaceURI);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang.String, java.lang.String)
	 */
	@Override
	public void writeProcessingInstruction(String target, String data)
			throws XMLStreamException {
		m_xmlStreamWriter.writeProcessingInstruction(target, data);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang.String)
	 */
	@Override
	public void writeProcessingInstruction(String target)
	throws XMLStreamException {
		m_xmlStreamWriter.writeProcessingInstruction(target);
	}

	/*(non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeStartDocument()
	 */
	@Override
	public void writeStartDocument() throws XMLStreamException {
		m_xmlStreamWriter.writeStartDocument();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeStartDocument(java.lang.String, java.lang.String)
	 */
	@Override
	public void writeStartDocument(String encoding, String version)
			throws XMLStreamException {
		m_xmlStreamWriter.writeStartDocument(encoding, version);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeStartDocument(java.lang.String)
	 */
	@Override
	public void writeStartDocument(String version)
			throws XMLStreamException {
		m_xmlStreamWriter.writeStartDocument(version);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void writeStartElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
		m_xmlStreamWriter.writeStartElement(prefix, localName, namespaceURI);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String, java.lang.String)
	 */
	@Override
	public void writeStartElement(String namespaceURI, String localName)
			throws XMLStreamException {
		m_xmlStreamWriter.writeStartElement(namespaceURI, localName);
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String)
	 */
	@Override
	public void writeStartElement(String localName)
			throws XMLStreamException {
		m_xmlStreamWriter.writeStartElement(localName);
	}

}
