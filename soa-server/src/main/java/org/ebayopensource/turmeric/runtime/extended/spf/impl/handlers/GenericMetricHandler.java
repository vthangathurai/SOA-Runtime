/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.extended.spf.impl.handlers;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.handler.HandlerPreconditions;
import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;

public class GenericMetricHandler extends BaseHandler {

	private final String END_POINT_URL = "ws/spf";
	@Override
	public void init(InitContext ctx) throws ServiceException {
		

	    HandlerPreconditions.checkClientSide(ctx, this.getClass());
	    super.init(ctx);
	}

	@Override
	public void invoke(MessageContext msgCtx) throws ServiceException {
		String reqUri = ((BaseMessageContextImpl)msgCtx).getRequestUri();
		if(reqUri != null && reqUri.contains(END_POINT_URL))
			((BaseMessageContextImpl)msgCtx).updateSvcMetric(SystemMetricDefs.SVC_WS_SPF_CALLS, 1);		
		else if(msgCtx.getServiceAddress() != null && msgCtx.getServiceAddress().getServiceLocationUrl() != null && 
				msgCtx.getServiceAddress().getServiceLocationUrl().toString().contains(END_POINT_URL))
			((BaseMessageContextImpl)msgCtx).updateSvcMetric(SystemMetricDefs.SVC_WS_SPF_CALLS, 1);		
	}

}
