/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.GlobalConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.MonitoringSystem;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageProcessorImpl;
import org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.markdown.SOAServerMarkdownStateManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.monitoring.ServerServiceMonitoringCompStatus;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDescFactory;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ErrorMapper;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;
import org.ebayopensource.turmeric.runtime.spf.pipeline.VersionCheckHandler;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;



/**
 * Implements server-side processing of the MessageContext.
 *
 * MessageContext is created by the Servlet or other receiving subsystem,
 * such as LocalTransport, POP3 poller, etc. This implementation runs
 * appropriate handlers, protocol processor and dispatchers.
 *
 * @author ichernyshev, smalladi
 */
public class ServerMessageProcessor extends BaseMessageProcessorImpl {
	private static ServerMessageProcessor s_instance;

	private ServerMessageProcessor() {
		// local instances
	}

	public static synchronized ServerMessageProcessor getInstance() throws ServiceException {
		if (s_instance == null) {
			s_instance = new ServerMessageProcessor();
			s_instance.initialize();
		}

		return s_instance;
	}

	public void processMessage(ServerMessageContext ctx) {
		ServerMessageContextImpl ctx2 = (ServerMessageContextImpl)ctx;
		processMessageInternal(ctx2, false);
	}

	@Override
	public void dispatchInternal(BaseMessageContextImpl ctx, boolean useAsync) throws Throwable {
		Dispatcher requestDispatcher = ctx.getServiceDesc().getRequestDispatcher();
		requestDispatcher.dispatchSynchronously(ctx);
	}

	@Override
	protected void beforeRequestPipeline(BaseMessageContextImpl ctx) throws ServiceException {
		super.beforeRequestPipeline(ctx);

		ServerMessageContextImpl serverCtx = (ServerMessageContextImpl)ctx;
		ServerServiceDesc serviceDesc = serverCtx.getServiceDesc();

		VersionCheckHandler versionCheckHandler = serviceDesc.getVersionCheckHandler();
		versionCheckHandler.checkRequestVersion(ctx);
	}

	@Override
	protected void processPreResponseDispatchErrors(BaseMessageContextImpl ctx)
		throws ServiceException
	{
		@SuppressWarnings("unchecked")
		List<Throwable> errors = ctx.getErrorList();
		if (errors == null || errors.isEmpty()) {
			return;
		}

		OutboundMessage response = (OutboundMessage)ctx.getResponseMessage();
		response.setTransportHeader(SOAHeaders.ERROR_RESPONSE, "true");
		ServerMessageContextImpl serverCtx = (ServerMessageContextImpl)ctx;
		Object errorMsg;
		ServerServiceDesc serviceDesc = serverCtx.getServiceDesc();
		ErrorMapper errorMapper = serviceDesc.getErrorMapper();
		try {
			errorMsg = errorMapper.mapErrors(errors, serverCtx);
			if (errorMsg == null) {
				throw new ServiceException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_ERROR_MAPPER_RETURNED_NULL,
								ErrorConstants.ERRORDOMAIN, 
						new Object[] {errorMapper.getClass().getName()}));
			}
			response.setErrorResponse(errorMsg);
		} catch (Throwable e) {
			Throwable t = new ServiceException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_ERROR_MAPPER_FAILURE,
						ErrorConstants.ERRORDOMAIN, 
						new Object[] {errorMapper.getClass().getName(), e.toString()}), e);
			// add error, so it gets logged
			ctx.addError(t);

			Class errorMessageClass = serverCtx.getOperation().getErrorType().
				getRootJavaTypes().get(0);
			if (ErrorMessage.class.equals(errorMessageClass)) {
				errorMsg = buildDefaultErrorMessage(ctx, e);
				response.setErrorResponse(errorMsg);
			} else {
				response.setUnserializable(t.toString());
			}
		}
	}

	private Object buildDefaultErrorMessage(BaseMessageContextImpl ctx, Throwable e) {
		CommonErrorData errData = ErrorDataFactory.createErrorData(
				ErrorConstants.SVC_RT_UNABLE_TO_MAP_ERRORS, ErrorConstants.ERRORDOMAIN);
		errData.setMessage(e.toString());		

		ErrorMessage result = new ErrorMessage();
		List<CommonErrorData> errors = result.getError();
		errors.add(errData);

		return result;
	}

	private void initialize() throws ServiceException {
		BaseMessageProcessorImpl.initializeCommonSubsystems();

		GlobalConfigHolder globalConfig = ServiceConfigManager.getInstance().getGlobalConfig();

		initializeMonitoringSystem(globalConfig);

		SOAServerMarkdownStateManager.getInstance();

		ServerServiceDescFactory.getInstance().initializeCompStatus();

		ServerServiceDescFactory serviceDescFactory = ServerServiceDescFactory.getInstance();
		if (!serviceDescFactory.loadAllServices()) {
			Throwable t = serviceDescFactory.getFirstInitException();
			throw new ServiceCreationException(
					ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_INCOMPLETE_INIT,
							ErrorConstants.ERRORDOMAIN, new Object[] {
							t==null? "See log for details" : t.toString()}), t);
		}
	}

	private void initializeMonitoringSystem(GlobalConfigHolder globalConfig)
			throws ServiceException {
		MonitoringSystem.initializeServer(globalConfig);

		ServerServiceMonitoringCompStatus.initializeCompStatus();
	}
}
