/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb.json;

import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;
import org.ebayopensource.turmeric.runtime.binding.ISerializerFactory;
import org.ebayopensource.turmeric.runtime.binding.exception.WriterCreationException;
import org.ebayopensource.turmeric.runtime.binding.impl.AbstractSerializerFactory;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.json.JSONStreamWriter;
import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;


/**
 * JSONSerializerFactory provides a Data Binding client an entry point to the 
 * Data Binding components serialization process. It provides an abstraction 
 * of the binding information for encoding method (payload type = JSON) 
 * as needed to implement the serialization operation. 
 */
public class JSONSerializerFactory extends AbstractSerializerFactory implements ISerializerFactory {
	public JSONSerializerFactory() {
		super(true);
	}


	/**
	 * Given a serialization context and an output stream, 
	 * creates and return an XMLStreamWriter for the JSON payload type. 
	 *
	 * @param ctxt - instance of ISerializationContext
	 * @param out - instace of OutputStream.
	 * @return Returns a XMLStreamWriter object for Payload type JSON.
	 */
	@Override
	public XMLStreamWriter createXMLStreamWriter(ISerializationContext ctxt, OutputStream out) 
			throws WriterCreationException {
		NamespaceConvention convention = NamespaceConvention.createSerializationNSConvention(ctxt);
		Charset charset = ctxt.getCharset();
		DataElementSchema rootEleSchema = ctxt.getRootElementSchema();
		try {
			return new JSONStreamWriter(convention, rootEleSchema, charset, out, m_options);
		}
		catch (XMLStreamException e) {
			throw new WriterCreationException(getPayloadType(), e);
		}
	}
	

	/**
	 * This method returns the PayloadType supported by this factory 
	 * @return JSON as payload type.
	 */
	@Override
	public String getPayloadType() {
		return BindingConstants.PAYLOAD_JSON;
	}
}
