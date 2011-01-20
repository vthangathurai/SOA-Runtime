/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author wdeng
 */
public abstract class BaseStreamWriter implements XMLStreamWriter {

	protected final NamespaceConvention m_convention;

	public BaseStreamWriter(NamespaceConvention convention) {
		m_convention = convention;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeCharacters(java.lang.String)
	 */
	public abstract void writeCharacters(String value)
			throws XMLStreamException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public abstract void writeStartElement(java.lang.String prefix,
			java.lang.String localName, java.lang.String namespaceURI)
			throws XMLStreamException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeEndElement()
	 */
	public abstract void writeEndElement() throws XMLStreamException;

	public abstract void close() throws XMLStreamException;

	public abstract void flush() throws XMLStreamException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#getNamespaceContext()
	 */
	public NamespaceContext getNamespaceContext() {
		return m_convention;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#getPrefix(java.lang.String)
	 */
	public String getPrefix(String arg0) throws XMLStreamException {
		return m_convention.getPrefix(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#getProperty(java.lang.String)
	 */
	public Object getProperty(String arg0) throws IllegalArgumentException {
		throw new UnsupportedOperationException(this.getClass().getName()
				+ "getProperty");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#setDefaultNamespace(java.lang.String)
	 */
	public void setDefaultNamespace(String arg0) throws XMLStreamException {
		// noop
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#setNamespaceContext(javax.xml.namespace.NamespaceContext)
	 */
	public void setNamespaceContext(NamespaceContext arg0)
			throws XMLStreamException {
		// noop
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#setPrefix(java.lang.String,
	 *      java.lang.String)
	 */
	public void setPrefix(String arg0, String arg1) throws XMLStreamException {
		// noop
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	public void writeAttribute(String localName, String value)
			throws XMLStreamException {
		writeAttribute(null, localName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void writeAttribute(String namespaceURI, String localName, String value)
			throws XMLStreamException {
		writeAttribute("", namespaceURI, localName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public void writeAttribute(String prefix, String nsURI, String localName,
			String value) throws XMLStreamException {
		// noop
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeDefaultNamespace(java.lang.String)
	 */
	public void writeDefaultNamespace(String arg0) throws XMLStreamException {
		// noop
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeEntityRef(java.lang.String)
	 */
	public void writeEntityRef(String arg0) throws XMLStreamException {
		// noop
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeNamespace(java.lang.String,
	 *      java.lang.String)
	 */
	public void writeNamespace(String arg0, String arg1)
			throws XMLStreamException {
		// noop
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang.String)
	 */
	public void writeProcessingInstruction(String arg0)
			throws XMLStreamException {
		// noop
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang.String,
	 *      java.lang.String)
	 */
	public void writeProcessingInstruction(String arg0, String arg1)
			throws XMLStreamException {
		// noop
	}

	public void writeCData(String text) throws XMLStreamException {
		writeCharacters(text);
	}

	public void writeCharacters(char[] arg0, int arg1, int arg2)
			throws XMLStreamException {
		writeCharacters(new String(arg0, arg1, arg2));
	}

	public void writeEmptyElement(String prefix, String local, String ns)
			throws XMLStreamException {
		writeStartElement(prefix, local, ns);
		writeEndElement();
	}

	public void writeEmptyElement(String ns, String local)
			throws XMLStreamException {
		writeStartElement(local, ns);
		writeEndElement();
	}

	public void writeEmptyElement(String local) throws XMLStreamException {
		writeStartElement(local);
		writeEndElement();
	}

	public void writeStartDocument(String arg0, String arg1)
			throws XMLStreamException {
		writeStartDocument();
	}

	public void writeStartDocument(String arg0) throws XMLStreamException {
		writeStartDocument();
	}

	public void writeStartElement(String ns, String local)
			throws XMLStreamException {
		writeStartElement(null, local, ns);
	}

	public void writeStartElement(String local) throws XMLStreamException {
		writeStartElement(null, local, null);
	}

	public void writeComment(String arg0) throws XMLStreamException {
		// noop
	}

	public void writeDTD(String arg0) throws XMLStreamException {
		// noop
	}

	public void writeEndDocument() throws XMLStreamException {
		// noop
	}
}
