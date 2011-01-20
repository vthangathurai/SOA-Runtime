/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.pipeline;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;


/**
 * @author ichernyshev
 */
public class PayloadAccessHelper {

	public static final int DEFAULT_PAYLOAD_BYTES_TO_LOG = 4096;

	private final boolean m_isClientSide;
	private final int m_maxPayloadBytesToLog;
	private final boolean m_shouldLogRequests;
	private final boolean m_shouldLogResponses;

	public PayloadAccessHelper(ServiceId id, Map<String,String> options,
		boolean shouldLogRequests, boolean shouldLogResponses)
		throws ServiceException
	{
		m_isClientSide = id.isClientSide();
		m_shouldLogRequests = shouldLogRequests;
		m_shouldLogResponses = shouldLogResponses;

		int maxPayloadBytesToLog;
		String value = options.get("payload-max-bytes");
		if (value != null && value.length() != 0) {
			try {
				maxPayloadBytesToLog = Integer.parseInt(value);
			} catch (Exception e) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_GENERIC_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[] {"Invalid payload-max-bytes: " + value}), e);
			}
		} else {
			maxPayloadBytesToLog = DEFAULT_PAYLOAD_BYTES_TO_LOG;
		}

		m_maxPayloadBytesToLog = maxPayloadBytesToLog;
	}

	private void startInboundRecording(Message msg) throws ServiceException {
		InboundMessage inboundMsg = (InboundMessage)msg;
		inboundMsg.recordPayload(m_maxPayloadBytesToLog);
	}

	private void startOutboundRecording(Message msg) throws ServiceException {
		OutboundMessage outboundMsg = (OutboundMessage)msg;
		outboundMsg.recordPayload(m_maxPayloadBytesToLog);
	}

	private String getInboundData(Message msg)
		throws ServiceException, UnsupportedEncodingException
	{
		InboundMessage inboundMsg = (InboundMessage)msg;
		byte[] data = inboundMsg.getRecordedData();
		if (data == null) {
			return null;
		}

		return new String(data, msg.getG11nOptions().getCharset().name());
	}

	private String getOutboundData(Message msg)
		throws ServiceException, UnsupportedEncodingException
	{
		OutboundMessage outboundMsg = (OutboundMessage)msg;
		byte[] data = outboundMsg.getRecordedData();
		if (data == null) {
			return null;
		}

		return new String(data, msg.getG11nOptions().getCharset().name());
	}

	public void startRequestRecording(MessageContext ctx) {
		if (!m_shouldLogRequests) {
			return;
		}
		
		try {
			Message msg = ctx.getRequestMessage();
			if (m_isClientSide) {
				startOutboundRecording(msg);
			} else {
				startInboundRecording(msg);
			}
		} catch (Throwable e) {
			LogManager.getInstance(PayloadAccessHelper.class).log(Level.WARNING,
				"Unable to start Request Payload recording due to: " + e.toString(), e);
		}
	}

	public void startResponseRecording(MessageContext ctx) {
		if (!m_shouldLogResponses) {
			return;
		}

		try {
			Message msg = ctx.getResponseMessage();
			if (m_isClientSide) {
				startInboundRecording(msg);
			} else {
				startOutboundRecording(msg);
			}
		} catch (Throwable e) {
			LogManager.getInstance(PayloadAccessHelper.class).log(Level.WARNING,
				"Unable to start Response Payload recording due to: " + e.toString(), e);
		}
	}

	public String getRequestPayload(MessageContext ctx) {
		
		if (!m_shouldLogRequests) {
			return null;
		}

		String result;
		try {
			Message msg = ctx.getRequestMessage();

			if (m_isClientSide) {
				result = getOutboundData(msg);
			} else {
				result = getInboundData(msg);
			}
		} catch (Throwable e) {
			LogManager.getInstance(PayloadAccessHelper.class).log(Level.WARNING,
				"Unable to access Request Payload data due to: " + e.toString(), e);
			result = null;
		}

		return result;
	}

	public String getResponsePayload(MessageContext ctx) {
		
		if (!m_shouldLogResponses) {
			return null;
		}

		String result;
		try {
			Message msg = ctx.getResponseMessage();

			if (m_isClientSide) {
				result = getInboundData(msg);
			} else {
				result = getOutboundData(msg);
			}
		} catch (Throwable e) {
			LogManager.getInstance(PayloadAccessHelper.class).log(Level.WARNING,
				"Unable to access Response Payload data due to: " + e.toString(), e);
			result = null;
		}

		return result;
	}

	public int getMaxPayloadBytesToLog() {
		return m_maxPayloadBytesToLog;
	}
}
