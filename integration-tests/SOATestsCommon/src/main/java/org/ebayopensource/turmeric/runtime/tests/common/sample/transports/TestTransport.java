/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sample.transports;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;

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

/**
 * @author wdeng
 */
public class TestTransport implements Transport {

	public static String result;

	public void init(InitContext ctx) throws ServiceException {
	}

	public Object preInvoke(MessageContext ctx) throws ServiceException {
		OutboundMessage serverResponse = (OutboundMessage)ctx.getResponseMessage();

		DataBindingDesc binding = serverResponse.getDataBindingDesc();
		String mimeType = binding.getMimeType();
		Charset charset = serverResponse.getG11nOptions().getCharset();
		String contentType = HTTPCommonUtils.formatContentType(mimeType, charset);
		serverResponse.setTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE, contentType);
		return null;
	}

	public void invoke(Message msg, TransportOptions options) throws ServiceException {
		OutboundMessage responseMsg = (OutboundMessage)msg;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os);
		OutboundMessage serverResponse = (OutboundMessage)msg;

		Map<String,String> transportHeaders = serverResponse.buildOutputHeaders();
		try {

			if (transportHeaders != null) {
				for (Iterator<Map.Entry<String,String>> it=transportHeaders.entrySet().iterator(); it.hasNext(); ) {
					Map.Entry<String,String> e = it.next();
					writer.write(e.getKey() + "=" + e.getValue() + "\n");
				}
			}

			String contentType = serverResponse.getTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE);
			writer.write(SOAConstants.HTTP_HEADER_CONTENT_TYPE + "=" + contentType + "\n");

			Cookie[] cookies = serverResponse.getCookies();
			for (int i = 0; i < cookies.length; i++) {
				writer.write("Cookie: " + cookies[i] + "\n");
			}
			responseMsg.serialize(os);
			result = os.toString();
		} catch (IOException e) {
			throw new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_OUTBOUND_IO_EXCEPTION,
							ErrorConstants.ERRORDOMAIN, new Object[] {msg.getContext().getAdminName(), e.toString()}), e);			
		}
	}

	public Future<?> invokeAsync(Message msg, TransportOptions transportOptions)
			throws ServiceException {
		throw new UnsupportedOperationException();
	}

	public void retrieve(MessageContext context, Future<?> futureResp)
			throws ServiceException {
		throw new UnsupportedOperationException();		
	}
	
	public boolean supportsPoll(){
		throw new UnsupportedOperationException(
				"supportsPoll is not supported on TestTransport");

	}
}
