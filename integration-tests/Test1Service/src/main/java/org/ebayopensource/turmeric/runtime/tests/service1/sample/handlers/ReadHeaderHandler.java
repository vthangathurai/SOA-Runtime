/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers;

import java.util.Iterator;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;
import org.ebayopensource.turmeric.runtime.tests.service1.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.TestErrorTypes;


public class ReadHeaderHandler extends BaseHandler {
	public void invoke(MessageContext ctx) throws org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException {
		Message request = ctx.getRequestMessage();
		Message response = ctx.getResponseMessage();
		
		Map<String, String> requestHeaders = request.getTransportHeaders();
		if (null == requestHeaders || requestHeaders.isEmpty()) {
			throw new ServiceException(TestErrorTypes.NO_REQUEST_HEADERS);
		}

		ServerMessageContext serverCtx = (ServerMessageContext) ctx;
		String serverName = serverCtx.getTargetServerName();
		int serverPort = serverCtx.getTargetServerPort();
		Map<String,String> queryParams = serverCtx.getQueryParams();
		response.setTransportHeader(ClientReadHeaderHandler.SERVER_NAME, serverName);
		response.setTransportHeader(ClientReadHeaderHandler.SERVER_PORT, String.valueOf(serverPort));
		String t1p = queryParams.get("test1param");
		String t2p = queryParams.get("test2param");
		StringBuffer b = new StringBuffer();
		b.append("test1param=").append(t1p).append(",test2param=").append(t2p);
		response.setTransportHeader(ClientReadHeaderHandler.RETURN_QUERY_PARAMS, b.toString());

		boolean hasAnyHeaders = false;
		Iterator<String> iter = requestHeaders.keySet().iterator();
		// Echos all the request headers to response.
		while (iter.hasNext()) {
			String name = iter.next();

			String value = requestHeaders.get(name);
			response.setTransportHeader(ClientReadHeaderHandler.COPIED_REQUEST_HEADER_PREFIX + name, value);
			hasAnyHeaders = true;
		}

		if (!hasAnyHeaders) {
			throw new ServiceException(TestErrorTypes.NO_REQUEST_HEADERS);
		}
	}
}
