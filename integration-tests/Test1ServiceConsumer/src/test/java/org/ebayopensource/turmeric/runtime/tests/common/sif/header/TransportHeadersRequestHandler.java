/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.header;

import java.util.Map;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ReducedClientMessageContext;

import junit.framework.Assert;


public class TransportHeadersRequestHandler extends org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler {
	private static final Logger LOG = Logger.getLogger(TransportHeadersRequestHandler.class.getName());
	
	/**
	 * Reference to the options argument, to verify the transport header against it.  
	 */
	private Map<String, String> m_options;

	public void invoke(MessageContext ctx) throws ServiceException {
		LOG.info("**************** TransportHeadersRequestHandler-Start ****************");
		final String soaHeaderName = "X-TURMERIC-Routing-Profile-Name";
		final String soaHeaderValue = ctx.getRequestMessage().getTransportHeader(soaHeaderName);
		final String esbHeaderName = "X-TURMERIC-ESB-Routing-Profile-Name";
		final String esbHeaderValue = ctx.getRequestMessage().getTransportHeader(esbHeaderName);
		ReducedClientMessageContext ctxReduction = (ReducedClientMessageContext) ctx;
		String testValueSOA = (String) ctxReduction.getRequestProperty("testValueSOA");
		if (testValueSOA == null) {
			testValueSOA = m_options.get("testValueSOA");
		}
		Assert.assertEquals("Value not as expected for transport header " + soaHeaderName, 
				testValueSOA, soaHeaderValue);
		String testValueESB = (String) ctxReduction.getRequestProperty("testValueESB");
		if (testValueESB == null) {
			testValueESB = m_options.get("testValueESB");
		}
		Assert.assertEquals("Value not as expected for transport header " + esbHeaderName, 
				testValueESB, esbHeaderValue);
		LOG.info("**************** TransportHeadersRequestHandler-End ****************");
	}
	
	/**
	 * Stores the reference to the options holder locally, for invocation-time use. 
	 *  
	 * @see org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler#init(org.ebayopensource.turmeric.runtime.common.pipeline.Handler.InitContext)
	 */
	@Override
	public void init(InitContext ctx) throws ServiceException {
		super.init(ctx);
		m_options = ctx.getOptions();
	}

}

