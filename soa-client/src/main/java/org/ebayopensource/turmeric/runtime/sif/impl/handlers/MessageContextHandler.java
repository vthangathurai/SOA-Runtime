/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.handlers;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ebayopensource.turmeric.runtime.binding.utils.BindingUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.handler.HandlerPreconditions;
import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;


import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;

/**
 * If service is chained, this handler will copy some headers to the chained service.
 *
 * @author kmatsumoto, ichernyshev
 */
public class MessageContextHandler extends BaseHandler {

	private static final List<String> s_securityHeaders = new ArrayList<String>();

	private Set<String> m_transportHeaderNames;
	private Set<String> m_cookieNames;
	private boolean m_shouldCopyUseCaseName;
	private boolean m_shouldCopyGlobalId;
	private boolean m_shouldCopyLocales;
	private boolean m_shouldProcessG11nOptions;
	private boolean m_shouldCopySecurityHeaders;

	@Override
	public void init(InitContext ctx)
		throws ServiceException
	{
		super.init(ctx);
		HandlerPreconditions.checkClientSide(ctx, this.getClass()); // Client Side Only

		Map<String,String> options = ctx.getOptions();
		//m_contextPropertyNames = parseNameList(options, "context-properties");

		List<String> cookieNames = BindingUtils.parseNameList(options, "cookies");
		if (cookieNames != null) {
			m_cookieNames = new HashSet<String>();
			for (String cookieName: cookieNames) {
				m_cookieNames.add(cookieName.toUpperCase());
			}
		}

		m_transportHeaderNames = new HashSet<String>();

		List<String> transportHeaderNames = BindingUtils.parseNameList(options, "transport-headers");
		if (transportHeaderNames != null) {
			for (String headerName: transportHeaderNames) {
				headerName = SOAHeaders.normalizeName(headerName, true);

				if (SOAHeaders.isSOAHeader(headerName)) {
					// do not allow arbitrary system headers

					if (SOAHeaders.USECASE_NAME.equals(headerName)) {
						m_shouldCopyUseCaseName = true;
						continue;
					}

					if (SOAHeaders.GLOBAL_ID.equals(headerName)) {
						m_shouldCopyGlobalId = true;
						continue;
					}

					if (SOAHeaders.LOCALE_LIST.equals(headerName)) {
						m_shouldCopyLocales = true;
						continue;
					}
					throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_HEADER_NAME,
							ErrorConstants.ERRORDOMAIN, new Object[] {ctx.getServiceId().toString(), headerName}));
				}
				m_transportHeaderNames.add(headerName);
			}
		}

		m_shouldCopySecurityHeaders = ("true".equalsIgnoreCase(options.get("copy-security-headers")));

		// copy use case name anyways
		m_shouldCopyUseCaseName = true;

		m_shouldProcessG11nOptions = m_shouldCopyGlobalId || m_shouldCopyLocales;
	}

	@Override
	public void invoke(MessageContext ctx) throws ServiceException {

		MessageContext callerCtx = ((ClientMessageContext)ctx).getCallerMessageContext();
		if (callerCtx == null) {
			return; //service is not chained
		}

		if (callerCtx instanceof ClientMessageContext) {
			return; // the caller should be not be a client message context.
		}
		boolean isRequestDirection = ctx.getProcessingStage().isRequestDirection();

		MessageContext srcCtx = isRequestDirection ? callerCtx : ctx;
		MessageContext dstCtx = isRequestDirection ? ctx : callerCtx;

		OutboundMessage dstMessage = (OutboundMessage) (isRequestDirection ? dstCtx.getRequestMessage() : dstCtx.getResponseMessage());
		InboundMessage srcMessage = (InboundMessage) (isRequestDirection ? srcCtx.getRequestMessage() : srcCtx.getResponseMessage());

		// copy transport headers
		for (String name: m_transportHeaderNames) {
			String value = srcMessage.getTransportHeader(name);
			if (value != null) {
				dstMessage.setTransportHeader(name, value);
			}
		}

		//copy security headers
		if (m_shouldCopySecurityHeaders) {
			Map<String,String> headers= srcMessage.getTransportHeaders();
			if (headers != null && !headers.isEmpty())
			{
				for (Entry<String, String>entry : headers.entrySet())
				{
					String name = entry.getKey().toUpperCase();
					if (name.contains("-SECURITY-"))
					{
						String value = entry.getValue();
						if (value != null && !value.isEmpty())
						{
							dstMessage.setTransportHeader(name, value);
						}
					}
				}
			}
		}


		// copy cookies
		if (m_cookieNames != null) {
			for (String name: m_cookieNames) {
				Cookie cookie = srcMessage.getCookie(name);
				if (cookie != null) {
					dstMessage.setCookie(cookie);
				}
			}
		}

		// copy request ID
		if (dstCtx.getRequestId() == null) {
			String currentRequestGuid = srcCtx.getRequestGuid();
			String currentRequestId = srcCtx.getRequestId();

			if (currentRequestId != null || currentRequestGuid != null) {
				dstCtx.setRequestId(currentRequestId, currentRequestGuid);
			}
		}

		// copy use case info
		if (m_shouldCopyUseCaseName) {
			String useCase = srcMessage.getTransportHeader(SOAHeaders.USECASE_NAME);
			dstMessage.setTransportHeader(SOAHeaders.USECASE_NAME, useCase);
		}

		// copy g11sOptions
		if (m_shouldProcessG11nOptions) {
			G11nOptions srcOptions = srcMessage.getG11nOptions();

			String globalId = dstMessage.getG11nOptions().getGlobalId();
			if (m_shouldCopyGlobalId) {
				globalId = srcOptions.getGlobalId();
			}

			List<String> locales = dstMessage.getG11nOptions().getLocales();
			if (m_shouldCopyLocales) {
				locales = srcOptions.getLocales();
			}

			Charset charset = dstMessage.getG11nOptions().getCharset();

			dstMessage.setG11nOptions(new G11nOptions(charset, locales, globalId));
		}

		// copy forwarded for data
		Object forwardedFor = srcCtx.getProperty(SOAConstants.CTX_PROP_TRANSPORT_FORWARDED_FOR);
		if (forwardedFor != null) {
			dstCtx.setProperty(SOAConstants.CTX_PROP_TRANSPORT_FORWARDED_FOR, forwardedFor);
		}

		// copy client pool name
		Object clientPoolName = srcCtx.getProperty(SOAConstants.CTX_PROP_CLIENT_POOL_NAME);
		if (clientPoolName != null) {
			dstCtx.setProperty(SOAConstants.CTX_PROP_CLIENT_POOL_NAME, clientPoolName);
		}
	}


	
}
