/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl;

import java.io.OutputStream;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;
import org.ebayopensource.turmeric.runtime.binding.ISerializer;
import org.ebayopensource.turmeric.runtime.binding.ISerializerFactory;
import org.ebayopensource.turmeric.runtime.binding.exception.SerializtionInitializationException;
import org.ebayopensource.turmeric.runtime.binding.exception.WriterCreationException;
import org.ebayopensource.turmeric.runtime.binding.impl.jaxb.JAXBSerializer;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;


/**
 * @author wdeng
 */
public abstract class AbstractSerializerFactory implements ISerializerFactory {

	private final boolean m_shouldOverrideNullObjectMarshalling;
	private ISerializer m_serializer;
	protected Map<String, String> m_options = CollectionUtils.EMPTY_STRING_MAP;
	
	public AbstractSerializerFactory(boolean shouldOverrideNullObjectMarshalling) {
		m_shouldOverrideNullObjectMarshalling = shouldOverrideNullObjectMarshalling;
	}

	public void init(InitContext ctx) throws SerializtionInitializationException {
		m_options = ctx.getOptions();
		m_serializer = new JAXBSerializer(m_shouldOverrideNullObjectMarshalling, 
				m_options, ctx.getRootClasses());
	}

	public abstract String getPayloadType();

	public abstract XMLStreamWriter createXMLStreamWriter(ISerializationContext ctxt,
		OutputStream out) throws Exception;

	public XMLStreamWriter getXMLStreamWriter(ISerializationContext ctxt,
		OutputStream out) throws WriterCreationException {
		try {
			return createXMLStreamWriter(ctxt, out);
		} catch (Exception e) {
			throw new WriterCreationException(getPayloadType(), e);
		}
	}

	public final ISerializer getSerializer() {
		return m_serializer;
	}
	
	public Map<String, String> getOptions() {
		return m_options;
	}
}
