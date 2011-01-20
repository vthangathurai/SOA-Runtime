/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;


public class ClientIpTestHandler extends ReadHeaderHandler {

	private static final String RETURN_CLIENT_IP = "RETURN_CLIENT_IP";

	public void invoke(MessageContext ctx) throws ServiceException {
		ServerMessageContext serverCtx = (ServerMessageContext) ctx;
		String sourceIpAddr = (String)serverCtx.getProperty(SOAConstants.CTX_PROP_TRANSPORT_CLIENT_SOURCE_IP);

		//Boolean isInternal = (Boolean)serverCtx.getProperty("IS_INTERNAL_CLIENT");
		//serverCtx.setProperty("IS_INTERNAL_CLIENT", Boolean.valueOf("false"));

		if(sourceIpAddr != null) {
			Message response = ctx.getResponseMessage();
			response.setTransportHeader(RETURN_CLIENT_IP , sourceIpAddr);
		}
	}

}
