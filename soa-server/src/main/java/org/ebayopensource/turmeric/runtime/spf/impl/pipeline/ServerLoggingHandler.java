/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.pipeline;

import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.pipeline.BaseLoggingHandler;
import org.ebayopensource.turmeric.runtime.common.impl.pipeline.LoggingHandlerUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandlerStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageProcessingStage;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.exceptions.AppErrorWrapperException;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigBeanManager;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;

public class ServerLoggingHandler extends BaseLoggingHandler {
	
	
	@Override
	public void init(InitContext ctx)
		throws ServiceException
	{
		ServiceId svcId = ctx.getServiceId();
		if (svcId.isClientSide()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_CANNOT_USE_ON_CLIENT,
					ErrorConstants.ERRORDOMAIN, new Object[] {this.getClass().getName(), svcId.getAdminName()}));
		}

		super.init(ctx);
	}

	@Override
	protected LoggingHandlerUtils getHandlerUtils() {
		return new ServerLoggingHandlerUtils();
	}
	
	@Override
	public void logProcessingStage(MessageContext ctx, LoggingHandlerStage stage) throws ServiceException {
		long startTime = System.nanoTime();
		try {
			super.logProcessingStage(ctx, stage);
		} finally {
			long duration = System.nanoTime() - startTime; 
			logMetrics(ctx, stage, duration);
		}
	}

	@Override
	protected Level getErrorLogLevel(MessageContext ctx, Throwable e)
		throws ServiceException
	{
		MessageProcessingStage stage = ctx.getProcessingStage();
		if (stage == MessageProcessingStage.RESPONSE_DISPATCH ||
			stage == MessageProcessingStage.RESPONSE_COMPLETE)
		{
			// log any dispatch errors as "severe" since
			// they cannot be delivered to the client
			return Level.SEVERE;
		}

		return super.getErrorLogLevel(ctx, e);
	}

	@Override
	protected Throwable unwrapException(Throwable e) {
		if (e instanceof AppErrorWrapperException) {
			AppErrorWrapperException e2 = (AppErrorWrapperException)e;
			e = e2.getCause();
		}

		return super.unwrapException(e);
	}

	@Override
	protected void addFullCallInfo(MessageContext ctx, LoggingHandlerStage stage, String separator, StringBuilder sb) throws ServiceException {
		super.addFullCallInfo(ctx, stage, separator, sb);

		ServerMessageContext serverCtx = (ServerMessageContext)ctx;

		String version = serverCtx.getServiceVersion();
		sb.append(separator);
		sb.append("SvcVer=");
		sb.append(version);

		String clientIP = serverCtx.getClientAddress().getIpAddress();
		sb.append(separator);
		sb.append("ClientIP=");
		sb.append(clientIP);

		String sourceIP = (String)ctx.getProperty(SOAConstants.CTX_PROP_TRANSPORT_CLIENT_SOURCE_IP);
		if (sourceIP != null) {
			sb.append(separator);
			sb.append("SourceIP=");
			sb.append(sourceIP);
		}

		String forwardedFor = (String)ctx.getProperty(SOAConstants.CTX_PROP_TRANSPORT_FORWARDED_FOR);
		if (forwardedFor != null) {
			sb.append(separator);
			sb.append("ForwardedFor=");
			sb.append(forwardedFor);
		}
	}
//	@Override
	protected String getRequestPayloadLog(MessageContext ctx)
	{
		return ServiceConfigBeanManager.
			getPayloadLogInstance(ctx.getServiceId().getAdminName()).getRequestPayloadLog();
	}
	
//	@Override
	protected String getResponsePayloadLog(MessageContext ctx)
	{
		return ServiceConfigBeanManager.
			getPayloadLogInstance(ctx.getServiceId().getAdminName()).getResponsePayloadLog();
	}
	


}
