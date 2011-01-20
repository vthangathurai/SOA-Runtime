/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline;

import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.xml.ws.AsyncHandler;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.GlobalConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.MonitoringSystem;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageProcessorImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.AsyncCallBack;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.markdown.SOAClientMarkdownStateManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDescFactory;


/**
 * Implements MessageProcessor on the client side (CMP).
 * 
 * This implementation receives MessageContext created by the calling party
 * (Service) and processes it by running appropriate protocol processor,
 * handlers and request dispatcher.
 * 
 * @author ichernyshev, smalladi
 */
public final class ClientMessageProcessor extends BaseMessageProcessorImpl {

	private static ClientMessageProcessor s_instance;

	private ClientMessageProcessor() {
		// local instance
	}

	public static synchronized ClientMessageProcessor getInstance()
			throws ServiceException {
		if (s_instance == null) {
			s_instance = new ClientMessageProcessor();
			s_instance.initialize();
		}

		return s_instance;
	}

	public void processMessage(ClientMessageContextImpl ctx, boolean useAsync) {
		processMessageInternal(ctx, useAsync);
	}

	public void processResponse(ClientMessageContextImpl ctx) {
		processResponseInternal(ctx);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dispatchInternal(BaseMessageContextImpl ctx, boolean useAsync)
			throws Throwable {
		Dispatcher requestDispatcher = ctx.getServiceDesc()
				.getRequestDispatcher();
		if (useAsync) {
			setPushPropertiesIfRequired((ClientMessageContextImpl) ctx);
			Future<?> futureResponse = requestDispatcher.dispatch(ctx);
			ctx.setFutureResponse(futureResponse);
		} else {
			requestDispatcher.dispatchSynchronously(ctx);
		}
	}

	private void setPushPropertiesIfRequired(ClientMessageContextImpl ctx)
			throws ServiceException {
		AsyncHandler<?> handler = ctx.getClientAsyncHandler();
		if (handler != null) {
			AsyncCallBack callback = new AsyncCallBackImpl(ctx);
			ctx.setServiceAsyncCallback(callback);
		}
	}

	@Override
	protected boolean shouldCountAsFailedRequest(BaseMessageContextImpl ctx) {
		if (super.shouldCountAsFailedRequest(ctx)) {
			return true;
		}

		try {
			return ctx.getResponseMessage().isErrorMessage();
		} catch (ServiceException e) {
			// this should not happen
			LogManager.getInstance(this.getClass()).log(
					Level.SEVERE,
					"Unexpected error in isErrorMessage for '"
							+ ctx.getAdminName() + "." + ctx.getOperationName()
							+ "': " + e.toString(), e);
			return true;
		}
	}

	@Override
	protected void handleAbortedRequestDispatch(BaseMessageContextImpl ctx) {
		super.handleAbortedRequestDispatch(ctx);

		InboundMessage response = (InboundMessage) ctx.getResponseMessage();
		response.unableToProvideStream();
	}

	private void initialize() throws ServiceException {
		BaseMessageProcessorImpl.initializeCommonSubsystems();

		GlobalConfigHolder globalConfig = ClientConfigManager.getInstance()
				.getGlobalConfig();
		MonitoringSystem.initializeClient(globalConfig);

		SOAClientMarkdownStateManager.getInstance();

		ClientServiceDescFactory.getInstance().initializeCompStatus();
	}
}
