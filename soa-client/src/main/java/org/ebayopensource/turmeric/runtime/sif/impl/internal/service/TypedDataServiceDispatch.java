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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.IAsyncResponsePoller;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.service.InvokerExchange;
import org.ebayopensource.turmeric.runtime.sif.service.RequestContext;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;

public class TypedDataServiceDispatch extends BaseServiceDispatchImpl<Object> {

	public TypedDataServiceDispatch(String opName, URL serviceLocation,
			ClientServiceDesc serviceDesc, URL wsdlLocation,
			ServiceInvokerOptions invokerOptions, String serviceVersion,
			Map<String, Cookie> cookies, Map<String, String> transportHeaders,
			Collection<ObjectNode> messageHeaders, G11nOptions g11Options,
			RequestContext requestContext, Executor executor,
			IAsyncResponsePoller servicePoller) throws ServiceException {
		super(opName, serviceLocation, serviceDesc, wsdlLocation,
				invokerOptions, serviceVersion, cookies, transportHeaders,
				messageHeaders, g11Options, requestContext, executor,
				servicePoller);
	}
	
	@Override
	public Object invoke(Object inArg) {
		checkType(inArg);
		return super.invoke(inArg);
	}

	@Override
	public Future<?> invokeAsync(Object inArg, AsyncHandler<Object> handler) {
		checkType(inArg);
		return super.invokeAsync(inArg, handler);
	}

	@Override
	public Response<Object> invokeAsync(Object inArg) {
		checkType(inArg);
		return super.invokeAsync(inArg);
	}

	@Override
	public void invokeOneWay(Object inArg) {
		super.invokeOneWay(inArg);
	}


	@Override
	protected Object invoke(String opName, Object inArg) {
		Object[] inParams = getInParam(inArg);
		List<Object> outParams = new ArrayList<Object>();

		try {
			invokeWithRetry(opName, false, false, inParams, outParams, null,
					null);
		} catch (ServiceInvocationException e) {
			throw new WebServiceException(e);
		}

		if (outParams.size() == 0) {
			throw new WebServiceException(new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_RT_MSG_ACCESS_VOID_DATA, ErrorConstants.ERRORDOMAIN)));
		}

		return outParams.size() > 0 ? outParams.get(0) : null;
	}

	@Override
	protected InboundMessageImpl preProcessMessageForDispatch(String opName,
			Object inArg) throws ServiceException {

		Object[] inParams = getInParam(inArg);

		return preProcessMessage(opName, false, false, inParams, null, null,
				null, true);
	}

	private Object[] getInParam(Object inArg) {
		return inArg == null ? new Object[0] : new Object[] { inArg };
	}
	
	private void checkType(Object inArg) {
		if (inArg instanceof InvokerExchange)
			throw new RuntimeException(
					"incompatible type org.ebayopensource.turmeric.runtime.sif.service.InvokerExchange passed");
	}


}
