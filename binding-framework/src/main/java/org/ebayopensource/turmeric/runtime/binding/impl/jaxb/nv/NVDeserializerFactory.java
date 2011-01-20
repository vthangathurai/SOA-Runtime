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

import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.IDeserializationContext;
import org.ebayopensource.turmeric.runtime.binding.IDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.exception.ReaderCreationException;
import org.ebayopensource.turmeric.runtime.binding.impl.AbstractDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.ObjectNodeToXMLStreamReader;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.ordered.OrderedNVStreamReader;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.unordered.NVDomBuilder;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;


/**
 * NVDeserializerFactory provides a Data Binding client an entry point to the 
 * Data Binding components deserialization process. It provides an abstraction 
 * of the binding information for encoding method (payload type = NameValue) 
 * as needed to implement the deserialization operation. 
 * @author wdeng
 *
 */
public class NVDeserializerFactory extends AbstractDeserializerFactory
		implements IDeserializerFactory {
	

	/**
	 * Given a deserialization context and an input stream, 
	 * creates and return an XMLStreamReader for the payload type NV. 
	 *
	 * @param ctxt - instance of IDeserializationContext
	 * @param in - instace of inputstream.
	 * @return Returns a XMLStreamReader object for Payload type NV.
	 */
	@Override
	public XMLStreamReader createXMLStreamReader(IDeserializationContext ctxt,
			InputStream in) throws Exception {
		boolean ordered = ctxt.isElementOrderPreserved();
		QName rootXmlName = ctxt.getRootXMLName();
		Charset charset = ctxt.getCharset();
		XMLStreamReader xmlStreamReader = createNVXMLStreamReader(
			in, ordered, rootXmlName, charset, ctxt);
		return xmlStreamReader;
	}

	private XMLStreamReader createNVXMLStreamReader(InputStream is,
		boolean ordered, QName rootXmlName, Charset charset, IDeserializationContext ctxt) 
		throws ReaderCreationException
	{
		NamespaceConvention convention = NamespaceConvention.createDeserializationNSConvention(ctxt);

		if (ordered) {
			XMLStreamReader reader = new OrderedNVStreamReader(is, convention, charset, rootXmlName, m_options); 
			return reader;
		}

		try {
			
			
			// TODO: how come we pass XmlType without namespace?
			ObjectNodeImpl root = ObjectNodeImpl.createEmptyRootNode();
			if (rootXmlName != null) {
				NVDomBuilder builder = new NVDomBuilder(convention, rootXmlName, m_options);
				root = builder.createDom(ObjectNodeImpl.ROOT_NODE_QNAME, is, charset);
			}
			XMLStreamReader reader = new ObjectNodeToXMLStreamReader(root, null, convention, m_options);
			return reader;
		} catch (XMLStreamException e) {
			throw new ReaderCreationException(getPayloadType(), e);
		}
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
