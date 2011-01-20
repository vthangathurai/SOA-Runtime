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
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb.nv;

import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;
import org.ebayopensource.turmeric.runtime.binding.ISerializerFactory;
import org.ebayopensource.turmeric.runtime.binding.impl.AbstractSerializerFactory;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVStreamWriter;
import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;


/**
 * NVSerializerFactory provides a Data Binding client an entry point to the 
 * Data Binding components serialization process. It provides an abstraction 
 * of the binding information for encoding method (payload type = NameValue) 
 * as needed to implement the serialization operation. 
 *
 * @author wdeng
 */
public class NVSerializerFactory extends AbstractSerializerFactory
		implements ISerializerFactory {

	/**
	 * Initializes NVSerializerFactory instance
	 */
	public NVSerializerFactory() {
		super(true);
	}
	

	/**
	 * Given a serialization context and an output stream, 
	 * creates and return an XMLStreamWriter for the payload type NV. 
	 *
	 * @param ctxt - instance of ISerializationContext
	 * @param out - instace of OutputStream.
	 * @return Returns a XMLStreamWriter object for Payload type NV.
	 */
	@Override
	public XMLStreamWriter createXMLStreamWriter(ISerializationContext ctxt,
			OutputStream out) throws Exception {

		QName rootXmlName = ctxt.getRootXMLName();
		DataElementSchema rootEleSchema = ctxt.getRootElementSchema();

		NamespaceConvention convention = NamespaceConvention.createSerializationNSConvention(ctxt);
		Charset charset = ctxt.getCharset();
		return new NVStreamWriter(convention, out, charset, rootXmlName, rootEleSchema, m_options, ctxt.isREST());
	}
	

	/**
	 * This method returns the PayloadType supported by this factory 
	 * @return NV as payload type.
	 */
	@Override
	public String getPayloadType() {
		return BindingConstants.PAYLOAD_NV;
	}
}
