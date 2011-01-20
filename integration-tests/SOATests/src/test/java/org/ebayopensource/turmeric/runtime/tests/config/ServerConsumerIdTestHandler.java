/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.tests.config;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * @author rpallikonda
 *
 */
public class ServerConsumerIdTestHandler extends BaseHandler {

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler#invoke(org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext)
	 */
	@Override
	public void invoke(MessageContext ctx) throws ServiceException {
		String consumerId = ctx.getRequestMessage().getTransportHeader(SOAHeaders.CONSUMER_ID);
		if (consumerId == null || !consumerId.equals("testConsumerId")) {
			throw new ServiceException(
					ErrorUtils.createErrorData(ErrorConstants.SVC_CLIENT_MISSING_CONSUMER_ID, ErrorConstants.ERRORDOMAIN, new Object[] {null }));
		}
		// USECASE header should be set to the same
		consumerId = ctx.getRequestMessage().getTransportHeader(SOAHeaders.USECASE_NAME);
		if (consumerId == null || !consumerId.equals("a:testConsumerId")) {
			throw new ServiceException(
					ErrorUtils.createErrorData(ErrorConstants.SVC_CLIENT_MISSING_CONSUMER_ID, ErrorConstants.ERRORDOMAIN, new Object[] {null }));
		}

	}

}
