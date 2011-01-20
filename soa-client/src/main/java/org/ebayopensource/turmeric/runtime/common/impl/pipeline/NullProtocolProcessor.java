/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.pipeline;

import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.ProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * A simple ProtocolProcessor, which does nothing
 *
 * @author ichernyshev
 */
public class NullProtocolProcessor implements ProtocolProcessor {

	// EMPTY LIST - supporting ALL data formats
    private static Collection<String> s_supportedDataFormats = new ArrayList<String>();

    // Default Error Response Indicator Code is HTTP_INTERNAL_ERROR (500)
    private static int m_defaultTransportErrorResponseIndicationCode = HttpURLConnection.HTTP_INTERNAL_ERROR;

    // Alternate Error Response Indicator Code is HTTP_OK (200)
    private static int m_alternateTransportErrorResponseIndicationCode = HttpURLConnection.HTTP_OK;


	public void init(InitContext ctx) throws ServiceException {
		// noop
	}

	public void beforeRequestPipeline(MessageContext ctx)
		throws ServiceException
	{
		// noop
	}

	public void beforeRequestDispatch(MessageContext ctx)
		throws ServiceException
	{
		// noop
	}

	public void beforeResponsePipeline(MessageContext ctx)
		throws ServiceException
	{
		// noop
	}

	public void beforeResponseDispatch(MessageContext ctx)
		throws ServiceException
	{
		// noop
	}

	public void preSerialize(OutboundMessage msg, XMLStreamWriter xmlStream)
		throws ServiceException
	{
		if (!msg.isErrorMessage() && msg.getParamCount() == 0) {
			return;
		}
		try {
			G11nOptions g11n = msg.getG11nOptions();
			Charset charset = g11n.getCharset();
			xmlStream.writeStartDocument(charset.name(), "1.0");
		} catch (XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_START_DOCUMENT_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[]{msg.getPayloadType(), e.toString()}), e);
		}
	}

	public void postSerialize(OutboundMessage msg, XMLStreamWriter xmlStream)
		throws ServiceException
	{
		if (!msg.isErrorMessage() && msg.getParamCount() == 0) {
			return;
		}
		try {
			xmlStream.writeEndDocument();
		} catch (XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_END_DOCUMENT_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[]{msg.getPayloadType(), e.toString()}), e);
		}
	}

	public void postDeserialize(InboundMessage msg)
		throws ServiceException
	{
		// noop
	}

	public boolean isExpectedMessageProtocol(Message root) throws ServiceException {
		return true;
	}
	
	public boolean supportsHeaders() {
		return false;
	}

	public String getMessageProtocol() {
		return SOAConstants.MSG_PROTOCOL_NONE;
	}

	public Collection<ObjectNode> getMessageHeaders(ObjectNode root) throws ServiceException {
		return null;
	}

	public ObjectNode getMessageBody(ObjectNode root) throws ServiceException {
		return root;
	}

	public Collection<String> getSupportedDataFormats() {
		return s_supportedDataFormats;
	}

    public int getTransportErrorResponseIndicationCode() {
		return m_defaultTransportErrorResponseIndicationCode;
	}

	public int getAlternateTransportErrorResponseIndicationCode() {
		return m_alternateTransportErrorResponseIndicationCode;
	}
}
