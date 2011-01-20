/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl;

import java.io.InputStream;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;

import org.ebayopensource.turmeric.runtime.binding.IDeserializationContext;
import org.ebayopensource.turmeric.runtime.binding.IDeserializer;
import org.ebayopensource.turmeric.runtime.binding.IDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.exception.DeserializtionInitializationException;
import org.ebayopensource.turmeric.runtime.binding.exception.ReaderCreationException;
import org.ebayopensource.turmeric.runtime.binding.impl.jaxb.JAXBDeserializer;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.ObjectNodeToXMLStreamReader;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;


/**
 * @author wdeng
 */
public abstract class AbstractDeserializerFactory implements IDeserializerFactory {

	protected Map<String, String> m_options = CollectionUtils.EMPTY_STRING_MAP;
	protected Class[] m_rootClasses;
	private IDeserializer m_deserializer;
	private Schema m_upaAwaremasterSchema;

	public AbstractDeserializerFactory() {
		// empty
	}

	public void init(InitContext ctx) throws DeserializtionInitializationException {
		m_options = ctx.getOptions();
		m_upaAwaremasterSchema = ctx.getUpaAwareMasterSchema();
		m_deserializer = new JAXBDeserializer(m_options, ctx.getRootClasses(), 
					m_upaAwaremasterSchema);
	}

	public abstract String getPayloadType();

	public abstract XMLStreamReader createXMLStreamReader(IDeserializationContext ctxt, InputStream in)
		throws Exception;


	public XMLStreamReader getXMLStreamReader(IDeserializationContext ctxt, InputStream in) throws ReaderCreationException {
		try {
			return createXMLStreamReader(ctxt, in);
		} catch (Exception e) {
			throw new ReaderCreationException(getPayloadType(), e);
		}
	}

	public final IDeserializer getDeserializer() {
		return m_deserializer;
	}

	public XMLStreamReader getXMLStreamReader(IDeserializationContext ctxt, ObjectNode objNode) throws ReaderCreationException {
		if (! (objNode instanceof ObjectNodeImpl)) {
			// throw deserialize JavaObjectNodeImpl is not supported
		}
		NamespaceConvention convention = NamespaceConvention.createDeserializationNSConvention(ctxt);
		ObjectNodeImpl root = ObjectNodeImpl.createEmptyRootNode();
		root.addChild((ObjectNodeImpl)objNode);
		return new ObjectNodeToXMLStreamReader(root,
				null, convention, m_options);
	}
	
	public Map<String, String> getOptions() {
		return m_options;
	}
}
