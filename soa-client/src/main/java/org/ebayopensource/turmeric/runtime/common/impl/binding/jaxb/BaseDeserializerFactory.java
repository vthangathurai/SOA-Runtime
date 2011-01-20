/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;

import org.ebayopensource.turmeric.runtime.binding.IDeserializationContext;
import org.ebayopensource.turmeric.runtime.binding.IDeserializerFactory;
import org.ebayopensource.turmeric.runtime.binding.IDeserializerFactory.InitContext;
import org.ebayopensource.turmeric.runtime.binding.exception.BindingException;
import org.ebayopensource.turmeric.runtime.binding.exception.ReaderCreationException;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.ObjectNodeToXMLStreamReader;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode.ObjectNodeStreamReader;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.binding.Deserializer;
import org.ebayopensource.turmeric.runtime.common.binding.DeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceTypeMappings;


import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * @author wdeng
 */
public abstract class BaseDeserializerFactory implements DeserializerFactory {

	protected Map<String, String> m_options;
	private Deserializer m_deserializer;
	protected IDeserializerFactory m_factory;
	private Class[] m_rootClasses;
	private Schema m_upaAwareMasterSchema;

	public void init(InitContext ctx) throws ServiceException {
		m_options = ctx.getOptions();
		m_rootClasses = ctx.getRootClasses();
		m_upaAwareMasterSchema = ctx.getUpaAwareMasterSchema();
		try {
			m_factory.init(ctx);
		} catch (BindingException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_XML_STREAM_WRITER_CREATION_ERROR,
					ErrorConstants.ERRORDOMAIN), e);
		}
	}

	public abstract String getPayloadType();

	public XMLStreamReader createXMLStreamReader(InboundMessage msg,
		List<Class> paramTypes, InputStream in)
		throws Exception {
		IDeserializationContext ctxt = msg.getContext();
        try {
        	return m_factory.getXMLStreamReader(ctxt, in);
        } catch (ReaderCreationException rce) {
        	throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_XML_STREAM_READER_CREATION_ERROR,
        			ErrorConstants.ERRORDOMAIN, new Object[] {getPayloadType(), rce.toString()}), rce);
        }
	}


	public XMLStreamReader getXMLStreamReader(InboundMessage msg,
		List<Class> paramTypes, InputStream in) throws ServiceException {
		try {
			return createXMLStreamReader(msg, paramTypes, in);
		} catch (Exception e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_XML_STREAM_READER_CREATION_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {getPayloadType()}), e);
		}
	}

	public final Deserializer getDeserializer() {
		if (null == m_deserializer) {
			m_deserializer = new JAXBBasedDeserializer(m_options, m_rootClasses,
					m_upaAwareMasterSchema);
		}
		return m_deserializer;
	}

	public XMLStreamReader getXMLStreamReader(InboundMessage msg, ObjectNode objNode) throws ServiceException {
		if (! (objNode instanceof ObjectNodeImpl)) {
			// throw deserialize JavaObjectNodeImpl is not supported
		}
		MessageContext ctx = msg.getContext();
		ServiceTypeMappings typeMappings = ctx.getServiceContext().getTypeMappings();
		NamespaceConvention convention = DataBindingFacade.createDeserializationNSConvention(typeMappings);
		ObjectNodeImpl root = ObjectNodeImpl.createEmptyRootNode();
		root.addChild((ObjectNodeImpl)objNode);
		return new ObjectNodeToXMLStreamReader(root,
				null, convention, m_options);
	}

	protected XMLStreamReader wrapWithObjectNodeReader(XMLStreamReader reader)
		throws ServiceException {
		try {
			return new ObjectNodeStreamReader(reader);
		} catch (XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_READ_ERROR, ErrorConstants.ERRORDOMAIN), e);
		}
	}

	public Map<String, String> getOptions() {
		return m_options;
	}
}
