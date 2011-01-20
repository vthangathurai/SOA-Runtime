/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.transport.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import com.ebay.kernel.gzip.ConfigurableGzipOutputStream;

public class HTTPServletResponseTransport implements Transport {
	private final HttpServletResponse m_response;
	private boolean m_enable_gzip;
	
	public HTTPServletResponseTransport(HttpServletResponse response) {
		m_response = response;

		m_enable_gzip = false;;
	}
	
	public HTTPServletResponseTransport(HttpServletRequest req, HttpServletResponse response) {
		m_response = response;
		
		String ae = req.getHeader(SOAConstants.HTTP_HEADER_ACCEPT_ENCODING);
		m_enable_gzip = "gzip".equalsIgnoreCase(ae);
	}

	public void init(InitContext ctx) throws ServiceException {
		// noop
	}

	public Object preInvoke(MessageContext ctx) throws ServiceException {
		OutboundMessage serverResponse = (OutboundMessage) ctx
				.getResponseMessage();

		// Set the content-type of response only if it has not been set (in soap1.2 case,
		// this gets over-written at the Server Protocol processor.
		// So make sure we are only setting here if it has not already been set)
		if (serverResponse
				.getTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE) == null) {
			DataBindingDesc binding = serverResponse.getDataBindingDesc();
			String mimeType = binding.getMimeType();
			Charset charset = serverResponse.getG11nOptions().getCharset();
			String contentType = HTTPCommonUtils.formatContentType(mimeType,
					charset);
			serverResponse.setTransportHeader(
					SOAConstants.HTTP_HEADER_CONTENT_TYPE, contentType);
		}

		return null;
	}

	public void invoke(Message msg, TransportOptions transportOptions)
			throws ServiceException {
		OutboundMessage serverResponse = (OutboundMessage) msg;

		if (serverResponse.isUnserializable()) {
			String reason = serverResponse.getUnserializableReason();
			try {
				m_response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR, reason);
			} catch (IOException e) {
				throw new ServiceException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_OUTBOUND_IO_EXCEPTION,
								ErrorConstants.ERRORDOMAIN, new Object[] { msg.getContext().getAdminName(), e.toString()}), e);
			}
		}

		if (serverResponse.isErrorMessage()) {
			m_response.setStatus(serverResponse
					.getTransportErrorResponseIndicationCode());
		}

		Map<String, String> transportHeaders = serverResponse
				.buildOutputHeaders();

		if (transportHeaders != null) {
			for (Iterator<Map.Entry<String, String>> it = transportHeaders
					.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, String> e = it.next();
				m_response.addHeader(e.getKey(), e.getValue());
			}
		}
		String finalContentType = serverResponse
				.getTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE);
		m_response.setContentType(finalContentType);

		Cookie[] cookies = serverResponse.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			String name = cookie.getName();

			// TODO: catch IllegalArgumentException and rewrap into Service
			// exception
			javax.servlet.http.Cookie httpCookie = new javax.servlet.http.Cookie(
					name, cookie.getValue());

			m_response.addCookie(httpCookie);
		}

		if (m_enable_gzip) {
			m_response.setHeader("Content-Encoding", "gzip");
		}
		try {
			sendData(serverResponse, m_enable_gzip);
		} catch (IOException e) {
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_OUTBOUND_IO_EXCEPTION,
						ErrorConstants.ERRORDOMAIN, new Object[] { msg.getContext().getAdminName(), e.toString()}), e);
		}
	}

	private void sendData(OutboundMessage msg, boolean enable_gzip) throws IOException,
			ServiceException {
		try {
			OutputStream os = m_response.getOutputStream();
            if (enable_gzip) {
            	ConfigurableGzipOutputStream gos = new ConfigurableGzipOutputStream(os, 4096);
    			msg.serialize(gos);
    			os.flush();
    			gos.finish();
            }
            else {
            	msg.serialize(os);
            }
		} catch(IOException ioe) {
			m_response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			throw ioe;
		} catch(ServiceException se) {
			m_response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			throw se;
		}
        m_response.getOutputStream().flush();
	}

	public Future<?> invokeAsync(Message msg, TransportOptions transportOptions)
			throws ServiceException {
		throw new UnsupportedOperationException(
				"Async Operation is not supported on HTTPServletResponseTransport");
	}

	public void retrieve(MessageContext context, Future<?> futureResp) throws ServiceException {
		throw new UnsupportedOperationException(
		"Async Operation is not support on HTTPServletResponseTransport");
	}

	public boolean supportsPoll() {
		return false;
	}
}
