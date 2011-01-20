/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.g11n.GlobalRegistryConfigManager;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceDesc;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.BaseSOAPProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandlerStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageProcessingStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Pipeline;
import org.ebayopensource.turmeric.runtime.common.pipeline.ProtocolProcessor;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;


/**
 * Defines base class for both Client and Server Message Processors (CMP and
 * SMP)
 * 
 * This class is responsible for invoking protocol processors, handlers and
 * request/response dispatchers. CMP and SMP are expected to modify the behavior
 * as necessary to prepare MessageContext or useAsync flag.
 * 
 * On the receiving side, Servlet will construct MessageContext from its
 * HttpservletRequest, and then pass MessageContext to SMP. In the case of
 * client, Service will take arguments from the user Proxy construct
 * MessageContext and then call CMP.
 * 
 * A message processor primarily has 4 stages of processing: -- running the
 * message through an input pipeline -- giving it to a request dispatcher --
 * running the message through an output pipeline -- Giving the response to a
 * Response dispatcher
 * 
 * A message processor also invokes protocol processor to perform protocol
 * specific tasks. For example, Axis2 will be plugged in as SOAP message
 * processor to process soap requests.
 * 
 * @author ichernyshev, smalladi
 */
public abstract class BaseMessageProcessorImpl {

	/**
	 * Invoke handlers, protocol processor and dispatchers for the given context
	 * NOTE: async processing logic has been removed for SOA 2.0. Async
	 * processing logic will be reintroduced post SOA 2.0.
	 */
	@SuppressWarnings("unchecked")
	public final void processMessageInternal(BaseMessageContextImpl ctx,
			boolean useAsync) {
		MessageContextAccessorImpl.blockPreviousContext();
		try {
			long processingStartTime = System.nanoTime();
			// Pass the start time into ctx for later reference
			recordMessageProcessingStartTime(ctx, processingStartTime);

			// run request pipeline
			runRequestSequence(ctx);

			// give it to request dispatcher
			if (!ctx.hasErrors() && !ctx.hasResponseResidentErrors()) {
				try {
					dispatchInternal(ctx, useAsync);					
				} catch (Throwable e) {
					handleRequestDispatchException(ctx, e);
					handleAbortedRequestDispatch(ctx);
				}
			} else {
				handleAbortedRequestDispatch(ctx);
			}

			if (!useAsync) {
				// run response pipeline in case of sync executions or pipeline
				// errors
				runResponseSequence(ctx);
				updateMonitoringAfterProcessing(ctx, processingStartTime);
			}
		} finally {
			MessageContextAccessorImpl.resetContext();
		}
	}
	
	public abstract void dispatchInternal(BaseMessageContextImpl ctx,
			boolean useAsync) throws Throwable;

	public final void processResponseInternal(BaseMessageContextImpl ctx) {
		MessageContextAccessorImpl.blockPreviousContext();
		try {
			ServiceDesc serviceDesc = ctx.getServiceDesc();
			try {
				Dispatcher requestDispatcher = serviceDesc
						.getRequestDispatcher();
				requestDispatcher.retrieve(ctx, ctx.getFutureResponse());
				
			} catch (Throwable e) {
				handleRequestDispatchException(ctx, e);
				handleAbortedRequestDispatch(ctx);
			}

			runResponseSequence(ctx);
			Long processingStartTime = (Long) ctx
					.getProperty(SystemMetricDefs.CTX_KEY_MSG_PROCESSING_STARTED);
			updateMonitoringAfterProcessing(ctx, processingStartTime
					.longValue());
		} finally {
			MessageContextAccessorImpl.resetContext();
		}

	}

	private void recordMessageProcessingStartTime(BaseMessageContextImpl ctx,
			long processingStartTime) {
		try {
			ctx.setProperty(SystemMetricDefs.CTX_KEY_MSG_PROCESSING_STARTED,
					new Long(processingStartTime));
		} catch (ServiceException e) {
			handleRequestProcessingException(ctx, e);
		}
	}

	void updateMonitoringAfterProcessing(BaseMessageContextImpl ctx,
			long processingStartTime) {
		long duration = System.nanoTime() - processingStartTime;
		ctx.updateSvcAndOpMetric(SystemMetricDefs.OP_TIME_TOTAL, duration);

		if (shouldCountAsFailedRequest(ctx)) {
			ctx.incrementSvcAndOpMetric(SystemMetricDefs.OP_ERR_FAILED_CALLS);
		}
	}

	protected boolean shouldCountAsFailedRequest(BaseMessageContextImpl ctx) {
		
		if ( ctx.hasErrors() ) return true;
		if ( !ctx.hasResponseResidentErrors() ) return false;
		
		/**
		 * RRE's only count as failed requests if the severity was severe enough
		 */
		List<ErrorData> errorDataList = ctx.getResponseResidentErrorList();
		for ( ErrorData errorData : errorDataList )
			if ( isError( errorData ) )
				return true;
		
		return false;
	}
	
	private boolean isError( ErrorData errorData ) {
		return errorData.getSeverity() == ErrorSeverity.ERROR;
	}

	private void runRequestSequence(BaseMessageContextImpl ctx) {
		ServiceDesc serviceDesc = ctx.getServiceDesc();

		// flip the processing stage
		ctx.changeProcessingStage(MessageProcessingStage.REQUEST_PIPELINE);

		// first set up logging handler
		ctx.runLoggingHandlerStage(LoggingHandlerStage.REQUEST_STARTED);

		long startTime = System.nanoTime();

		// run the protocol processor
		try {
			ProtocolProcessor protocol = ctx.getProtocolProcessor();
			protocol.beforeRequestPipeline(ctx);
		} catch (Throwable e) {
			handleRequestProtocolProcessingException(ctx, e);
		}

		// run any logic that has to run before pipeline (e.g. version checks)
		try {
			beforeRequestPipeline(ctx);
		} catch (Throwable e) {
			handleRequestProcessingException(ctx, e);
		}

		// run the pipeline
		try {
			Pipeline requestPipeline = serviceDesc.getRequestPipeline();
			requestPipeline.invoke(ctx);
		} catch (Throwable e) {
			handleRequestProcessingException(ctx, e);
		}

		// run the protocol processor
		try {
			ProtocolProcessor protocol = ctx.getProtocolProcessor();
			protocol.beforeRequestDispatch(ctx);
		} catch (Throwable e) {
			handleRequestProtocolProcessingException(ctx, e);
		}

		// flip the processing stage
		ctx.changeProcessingStage(MessageProcessingStage.REQUEST_DISPATCH);

		ctx.runLoggingHandlerStage(LoggingHandlerStage.BEFORE_REQUEST_DISPATCH);

		long duration = System.nanoTime() - startTime;
		ctx.updateSvcAndOpMetric(SystemMetricDefs.OP_TIME_PIPELINE_REQUEST,
				startTime, duration);
	}

	void runResponseSequence(BaseMessageContextImpl ctx) {
		ServiceDesc serviceDesc = ctx.getServiceDesc();

		long startTime = System.nanoTime();

		// flip the processing stage
		ctx.changeProcessingStage(MessageProcessingStage.RESPONSE_PIPELINE);

		ctx.runLoggingHandlerStage(LoggingHandlerStage.RESPONSE_STARTED);

		// run the protocol processor
		try {
			ProtocolProcessor protocol = ctx.getProtocolProcessor();
			protocol.beforeResponsePipeline(ctx);
		} catch (Throwable e) {
			handleResponseProtocolProcessingException(ctx, e);
		}

		// run the pipeline
		try {
			Pipeline responsePipeline = serviceDesc.getResponsePipeline();
			responsePipeline.invoke(ctx);
		} catch (Throwable e) {
			handleResponseProcessingException(ctx, e);
		}

		// run the protocol processor
		try {
			ProtocolProcessor protocol = ctx.getProtocolProcessor();
			protocol.beforeResponseDispatch(ctx);
		} catch (Throwable e) {
			handleResponseProtocolProcessingException(ctx, e);
		}

		// flip the processing stage
		ctx.changeProcessingStage(MessageProcessingStage.RESPONSE_DISPATCH);

		boolean canProcessResponse = true;
		try {
			processPreResponseDispatchErrors(ctx);
		} catch (Throwable e) {
			// cannot do anything if errors are not processed
			canProcessResponse = false;
			handlePostResponseDispatchException(ctx, e);
		}

		long duration = System.nanoTime() - startTime;
		ctx.updateSvcAndOpMetric(SystemMetricDefs.OP_TIME_PIPELINE_RESPONSE,
				startTime, duration);

		if (canProcessResponse) {
			/*
			 * Returning response is a seperate step. It is also abstracted out
			 * through a dispatcher. In the case of a servlet, the
			 * responseDispatcher is simply writing the data to the
			 * HTTPResponse's outputstream. In the case of a Loaclbinding and
			 * client cases, the response dispatcher simply returns and does
			 * nothing. In the case of an asynchronous model, the response
			 * dispatcher may use a connection and send the response back.
			 */

			// TODO: add timing for response dispatcher on server (both sync and
			// async cases)
			// TO DO: fix async case - we should use callback and uplink here as
			// we cannot call logger yet
			try {
				// run through the response dispatcher.
				Dispatcher responseDispatcher = serviceDesc
						.getResponseDispatcher();
				responseDispatcher.dispatchSynchronously(ctx);
			} catch (Throwable e) {
				handlePostResponseDispatchException(ctx, e);
			}
		}

		ctx.changeProcessingStage(MessageProcessingStage.RESPONSE_COMPLETE);

		ctx.runLoggingHandlerStage(LoggingHandlerStage.RESPONSE_COMPLETE);
	}

	protected void beforeRequestPipeline(BaseMessageContextImpl ctx)
			throws ServiceException {
		// let subclasses override
	}

	protected void handleAbortedRequestDispatch(BaseMessageContextImpl ctx) {
		// let subclasses override
	}

	protected void processPreResponseDispatchErrors(BaseMessageContextImpl ctx)
			throws ServiceException {
		// let subclasses override
	}

	protected void handleRequestProcessingException(BaseMessageContextImpl ctx,
			Throwable e) {
		ctx.addError(e);
	}

	protected void handleRequestDispatchException(BaseMessageContextImpl ctx,
			Throwable e) {
		Object soapFault = ctx.getProperty(BaseSOAPProtocolProcessor.SOAP_FAULT_OBJECT);
		if(soapFault != null){
			ctx.addError(new Exception("Received Soap Fault: " + soapFault));
		}
		ctx.addError(e);
	}

	protected void handleRequestProtocolProcessingException(
			BaseMessageContextImpl ctx, Throwable e) {
		ctx.addError(e);
	}

	protected void handleResponseProcessingException(
			BaseMessageContextImpl ctx, Throwable e) {
		ctx.addError(e);
	}

	protected void handleResponseProtocolProcessingException(
			BaseMessageContextImpl ctx, Throwable e) {
		Object soapFault = ctx.getProperty(BaseSOAPProtocolProcessor.SOAP_FAULT_OBJECT);
		if(soapFault != null){
			ctx.addError(new Exception("Received Soap Fault: " + soapFault));
		}
		ctx.addError(e);
	}

	protected void handlePostResponseDispatchException(
			BaseMessageContextImpl ctx, Throwable e) {
		ctx.addError(e);
	}

	protected static void initializeCommonSubsystems() throws ServiceException {
		ErrorDataFactory.initialize(ErrorConstants.ERRORDOMAIN);
		ErrorDataFactory.initialize(org.ebayopensource.turmeric.security.errorlibrary.ErrorConstants.ERRORDOMAIN);
		GlobalRegistryConfigManager.getInstance();
	}
}
