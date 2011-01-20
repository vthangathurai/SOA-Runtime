/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers;

import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.tests.service1.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.TestErrorTypes;


public class ClientReadHeaderHandler extends BaseHandler {

	public static final String COPIED_REQUEST_HEADER_PREFIX = "COPIED_FROM_REQ_";
	public static final String SERVER_NAME = "RETURN_SOA_SERVER_NAME";
	public static final String SERVER_PORT = "RETURN_SOA_SERVER_PORT";
	public static final String RETURN_QUERY_PARAMS = "RETURN_QUERY_PARAMS";
	// TODO - query parameters

	public void invoke(MessageContext ctx) throws org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException {
		Message request = ctx.getRequestMessage();
		Message response = ctx.getResponseMessage();
		
		Map<String, String> requestHeaders = request.getTransportHeaders();
		Map<String, String> responseHeaders = response.getTransportHeaders();

		boolean hasAnyHeaders = false;
		for (String name : requestHeaders.keySet()) {
			if (!name.startsWith("TEST_")) {
				continue;
			}

			String reqvalue = requestHeaders.get(name);
			String respvalue = responseHeaders.get(COPIED_REQUEST_HEADER_PREFIX + name);
			if (!reqvalue.equals(respvalue)) {
				throw new ServiceException(TestErrorTypes.HEADER_MISMATCH);
			}
			hasAnyHeaders = true;
		}

		if (!hasAnyHeaders) {
			throw new ServiceException(TestErrorTypes.NO_REQUEST_HEADERS);
		}
	}
}
