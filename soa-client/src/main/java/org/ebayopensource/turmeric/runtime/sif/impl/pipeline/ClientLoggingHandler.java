/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.pipeline;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.utils.ObjectNodeUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.handler.HandlerPreconditions;
import org.ebayopensource.turmeric.runtime.common.impl.pipeline.BaseLoggingHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandlerStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientServiceConfigBeanManager;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;



/**
 * @author ichernyshev
 */
public class ClientLoggingHandler extends BaseLoggingHandler {

	@Override
	public void init(InitContext ctx)
		throws ServiceException
	{
		HandlerPreconditions.checkClientSide(ctx, this.getClass()); // Client Side Only
		super.init(ctx);
	}

	@Override
	protected ClientLoggingHandlerUtils getHandlerUtils() {
		return new ClientLoggingHandlerUtils();
	}

	@Override
	public void logProcessingStage(MessageContext ctx, LoggingHandlerStage stage)
		throws ServiceException
	{
		long startTime = System.nanoTime();
		
		try {
			super.logProcessingStage(ctx, stage);
	
			if (stage == LoggingHandlerStage.RESPONSE_COMPLETE) {
				Level appErrorLogLevel = getAppErrorLogLevel();
				if (getLogger().isLoggable(appErrorLogLevel)) {
					Message responseMsg = ctx.getResponseMessage();
					if (responseMsg.isErrorMessage()) {
						Object errorResponse = null;
						if (ctx.isOutboundRawMode()) {
							// We are not guaranteed to have TypeMappings and therefore might not be able
							// to deserialize the message. so the safe thing to do is log the raw message.
							ByteBuffer bb = responseMsg.getByteBuffer();
							if (bb != null)
								try {
									errorResponse = new String(bb.array(), Charset.defaultCharset().name());
								} catch (UnsupportedEncodingException e) {
									// e.printStackTrace();
									// FIXME: exception to be handled
								}
						} else {
							errorResponse = responseMsg.getErrorResponse();
						}
						logErrorResponse(ctx, errorResponse, appErrorLogLevel);
					}
				}
			}
		}
		finally {
			long duration = System.nanoTime() - startTime; 
			logMetrics(ctx, stage, duration);
		}
	}

	@Override
	protected void addCallInfo(MessageContext ctx, LoggingHandlerStage stage, String separator, StringBuilder sb) throws ServiceException {
		if (sb.length() != 0) {
			sb.append(separator);
		}
		sb.append(stage);
		if (stage.equals(LoggingHandlerStage.RESPONSE_STARTED) || stage.equals(LoggingHandlerStage.RESPONSE_COMPLETE)) {
			String guid = ctx.getRequestGuid();
			if (guid != null) {
				sb.append(separator);
				sb.append("GUID=");
				sb.append(guid);
			}
		}
	}

	protected void logErrorResponse(MessageContext ctx, Object errorResponse, Level logLevel) {
		if (!getLogger().isLoggable(logLevel)) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		Throwable thrown = null;
		if (errorResponse instanceof ErrorMessage) {
			List<CommonErrorData> errors = ((ErrorMessage)errorResponse).getError();
			if (!errors.isEmpty()) {
				CommonErrorData error = errors.get(0);
				String domain = error.getDomain();
				sb.append("Error Response '");
				if (domain != null) {
					sb.append(domain);
					sb.append('.');
				}
				sb.append(error.getErrorId());
				sb.append("' with message '");
				sb.append(error.getMessage());
				sb.append('\'');
			} else {
				sb.append("Empty Error Response");
			}
		} else {
			if (errorResponse != null) {
				sb.append("Error Response ");

				String customText = getHandlerUtils().getCustomResponseText((ClientMessageContext)ctx, errorResponse);
				if (customText == null) {
					if (errorResponse instanceof ObjectNode) {
						try {
				        	ObjectNode objNode = (ObjectNode) errorResponse;
				        	StringWriter sw = new StringWriter();
				        	ObjectNodeUtils.writeAsXML(objNode, sw, true, "UTF-8");
				        	sb.append(sw.toString());
						} catch (Throwable thr) {
							sb.append("Could not compose error response: " + thr 
									+ ((thr.getMessage() == null) ? "" : ": " + thr.getMessage()));
							thrown = thr;
						}
					} else {
					// JAXB toString is not good at all, but we have nothing else
					customText = errorResponse.toString();
				sb.append(customText);
					}
				}

			} else {
				sb.append("Null Error Response");
			}
		}

		String prefix = getErrorPrefixText(ctx);
		getLogger().log(logLevel, prefix + sb.toString(), thrown);
	}
	
	@Override
	protected String getRequestPayloadLog(MessageContext ctx)
	{
		ServiceId id = ctx.getServiceId();
		String adminName = id.getAdminName();
		String clientName = ((ClientServiceId)id).getClientName();
		String envName = ((ClientServiceId)id).getEnvName();
		
		return ClientServiceConfigBeanManager.
			getPayloadLogInstance(adminName, clientName,envName).getRequestPayloadLog();
	}
	@Override
	protected String getResponsePayloadLog(MessageContext ctx)
	{
		ServiceId id = ctx.getServiceId();
		String adminName = id.getAdminName();
		String clientName = ((ClientServiceId)id).getClientName();
		String envName = ((ClientServiceId)id).getEnvName();

		return ClientServiceConfigBeanManager.
			getPayloadLogInstance(adminName, clientName,envName).getResponsePayloadLog();
	}
	
}
