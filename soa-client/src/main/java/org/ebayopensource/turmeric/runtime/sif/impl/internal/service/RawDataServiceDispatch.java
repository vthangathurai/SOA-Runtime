/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.service;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.IAsyncResponsePoller;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.sif.service.InvokerExchange;
import org.ebayopensource.turmeric.runtime.sif.service.RequestContext;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;


public class RawDataServiceDispatch extends
		BaseServiceDispatchImpl<InvokerExchange> {

	public RawDataServiceDispatch(String opName, URL serviceLocation,
			ClientServiceDesc serviceDesc, URL wsdlLocation,
			ServiceInvokerOptions invokerOptions, String serviceVersion,
			Map<String, Cookie> cookies, Map<String, String> transportHeaders,
			Collection<ObjectNode> messageHeaders, G11nOptions g11nOptions,
			RequestContext requestContext, Executor executor,
			IAsyncResponsePoller servicePoller) throws ServiceException {
		super(opName, serviceLocation, serviceDesc, wsdlLocation,
				invokerOptions, serviceVersion, cookies, transportHeaders,
				messageHeaders, g11nOptions, requestContext, executor,
				servicePoller);
	}

	public InvokerExchange invoke(RawDispatchData inArg) {
		return invoke(m_opName, inArg);
	}

	public InvokerExchange invoke(String opName, RawDispatchData dispatchData) {
		try {
			invokeWithRetry(opName, dispatchData.isInboundRawMode(),
					dispatchData.isOutboundRawMode(), dispatchData
							.getInParams(), dispatchData.getOutParams(),
					dispatchData.getInWrapper(), dispatchData.getOutWrapper());
		} catch (ServiceInvocationException e) {
			throw new WebServiceException(e);
		}
		return dispatchData;
	}

	@Override
	protected InboundMessageImpl preProcessMessageForDispatch(String opName,
			InvokerExchange ex) throws ServiceException {
		RawDispatchData inArg = (RawDispatchData) ex;

		setTransportHeaderIfApplicable(inArg);

		return preProcessMessage(opName, inArg.isInboundRawMode(), inArg
				.isOutboundRawMode(), inArg.getInParams(),
				inArg.getOutParams(), inArg.getInWrapper(), inArg
						.getOutWrapper(), true);
	}

	private void setTransportHeaderIfApplicable(RawDispatchData inArg) {
		Map<String, String> headerMap = inArg.getHeaderMap();

		if (inArg.isInboundRawMode() && headerMap == null) {
			throw new IllegalArgumentException("Header map cannot be null");
		}

		if (headerMap == null)
			return;

		RequestContext ctx = getDispatchRequestContext();
		for (Map.Entry<String, String> e : headerMap.entrySet()) {
			ctx.setTransportHeader(e.getKey(), e.getValue());
		}
	}

	@Override
	protected Future<?> invokeAsync(String opName, InvokerExchange inArg,
			AsyncHandler<InvokerExchange> handler) {
		return super.invokeAsync(opName, new RawDispatchData(inArg), handler);
	}

	@Override
	protected Response<InvokerExchange> invokeAsync(String opName,
			InvokerExchange inArg) {
		return super.invokeAsync(opName, new RawDispatchData(inArg));
	}

	@Override
	protected InvokerExchange invoke(String opName, InvokerExchange inArg) {
		return this.invoke(opName, new RawDispatchData(inArg));
	}

}
