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
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb.xml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.stax2.XMLOutputFactory2;
import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;
import org.ebayopensource.turmeric.runtime.binding.ISerializerFactory;
import org.ebayopensource.turmeric.runtime.binding.impl.AbstractSerializerFactory;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.xml.XMLTextEscapingWriterFactory;

import com.ctc.wstx.stax.WstxOutputFactory;

/**
 * XMLSerializerFactory provides a Data Binding client an entry point to the 
 * Data Binding components serialization process. It provides an abstraction 
 * of the binding information for encoding method (payload type = XML) 
 * as needed to implement the serialization operation. 
 *
 * @author wdeng
 */
public class XMLSerializerFactory extends AbstractSerializerFactory
		implements ISerializerFactory {

	private XMLOutputFactory m_streamWriterFactory;

	/**
	 * Constructor
	 */
	public XMLSerializerFactory() {
		super(false);
		m_streamWriterFactory = new WstxOutputFactory();
		XMLTextEscapingWriterFactory factory = new XMLTextEscapingWriterFactory();
		m_streamWriterFactory.setProperty(XMLOutputFactory2.P_TEXT_ESCAPER, factory);
	}
	
	/**
	 * Given a serialization context and an output stream, 
	 * creates and return an XMLStreamWriter for the payload type XML. 
	 *
	 * @param ctxt - instance of ISerializationContext
	 * @param out - instace of OutputStream.
	 * @return Returns a XMLStreamWriter object for Payload type XML.
	 */
	@Override
	public XMLStreamWriter createXMLStreamWriter(ISerializationContext ctxt,
			OutputStream out) throws Exception {
		Charset charset = ctxt.getCharset();
		OutputStreamWriter osWriter = new OutputStreamWriter(out, charset);
		XMLStreamWriter xmlStreamWriter = 
			m_streamWriterFactory.createXMLStreamWriter(osWriter);
		return xmlStreamWriter;
	}
	
	/**
	 * This method returns the PayloadType supported by this factory 
	 * @return XML as payload type.
	 */
	@Override
	public String getPayloadType() {
		return BindingConstants.PAYLOAD_XML;
	}
}
