/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.ISerializerFactory;
import org.ebayopensource.turmeric.runtime.binding.exception.BindingException;
import org.ebayopensource.turmeric.runtime.binding.exception.WriterCreationException;
import org.ebayopensource.turmeric.runtime.common.binding.Serializer;
import org.ebayopensource.turmeric.runtime.common.binding.SerializerFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * @author wdeng
 */
public abstract class BaseSerializerFactory implements SerializerFactory {

	protected Map<String, String> m_options;
	private Serializer m_serializer;
	protected ISerializerFactory m_factory;
	private Class[] m_rootClasses;

	public void init(ISerializerFactory.InitContext ctx) throws ServiceException {
		m_options = ctx.getOptions();
		m_rootClasses = ctx.getRootClasses();
		try {
			m_factory.init(ctx);
		} catch (BindingException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_XML_STREAM_WRITER_CREATION_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[] {getPayloadType(), e.toString()}), e);	
		}
	}

	public abstract String getPayloadType();

	public XMLStreamWriter createXMLStreamWriter(OutboundMessage msg, List<Class> paramTypes,
		OutputStream out) throws ServiceException
	{
		MessageContext ctxt = msg.getContext();
        try {
        	return m_factory.getXMLStreamWriter(ctxt, out);
        } catch (WriterCreationException wce) {
        	throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_XML_STREAM_WRITER_CREATION_ERROR, 
        			ErrorConstants.ERRORDOMAIN, new Object[] {getPayloadType(), wce.toString()}), wce);
        }
	}

	public XMLStreamWriter getXMLStreamWriter(OutboundMessage msg,
		List<Class> paramTypes, OutputStream out) throws ServiceException {
		try {
			return createXMLStreamWriter(msg, paramTypes, out);
		} catch (Exception e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_XML_STREAM_WRITER_CREATION_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[] {getPayloadType(), e.toString()}), e);
	}
	}

	public final Serializer getSerializer() {
		if (null == m_serializer) {
			m_serializer = new JAXBBasedSerializer(false, m_options, m_rootClasses);
		}
		return m_serializer;
	}
	
	public Map<String, String> getOptions() {
		return m_options;
	}
}
