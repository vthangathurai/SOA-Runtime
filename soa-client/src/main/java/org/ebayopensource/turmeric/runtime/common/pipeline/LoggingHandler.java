/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;


/**
 * Logging handlers provide call request/dispatch/response logging capability.  Logging handlers are called at 
 * various points in service processing.  These handlers are distinct from normal pipeline handlers.
 * 
 * Logging handlers are registered in service or group pipeline configuration.  Multiple logging handlers can be defined.
 * 
 * Logging handlers are not the same as the SOA Framework's use of JDK logging (nor the SOA LogManager which obtains
 * JDK loggers).  A logging handler might use an underlying JDK logger, or might use a completely different method 
 * of logging.
 * 
 * @author ichernyshev
 */
public interface LoggingHandler {

	/**
	 * Initialize the logging handler after construction
	 * 
	 * Construction and initialization is managed automatically by the framework.
	 * @param ctx The InitContext carrying initialization options.
	 * @throws ServiceException Exception thrown when initialization fails.
	 */
	public void init(InitContext ctx) throws ServiceException;

	/**
	 * Called at various stages during message processing in order to provide statusing information on the progress
	 * of the invocation.
	 * 
	 * @param ctx the message context for the current invocation.
	 * @param stage the stage of message processing, from a logging standpoint.
	 * @throws ServiceException Exception thrown when processing fails.
	 */
	public void logProcessingStage(MessageContext ctx, LoggingHandlerStage stage) throws ServiceException;

	/**
	 * Allows response resident errors (RRE's) to be logged.
	 * @param ctx the message context for the current invocation.
	 * @param errorData the error exception to be logged.
	 * @throws ServiceException Exception thrown when processing fails.
	 */
	public void logResponseResidentError(MessageContext ctx, ErrorData errorData) throws ServiceException;

	/**
	 * Allows errors (unrecovered processing exceptions) to be logged.
	 * @param ctx the message context for the current invocation.
	 * @param e the error exception to be logged.
	 * @throws ServiceException Exception thrown when processing fails.
	 */
	public void logError(MessageContext ctx, Throwable e) throws ServiceException;

	/**
	 * Allows warnings to be logged.  Warnings are any exceptions that are added as warnings into the MessageContext using 
	 * addWarning.  These include, but are not limited to, handler exceptions that are recovered using the continue-on-error
	 * feature.
	 * @param ctx the message context for the current invocation.
	 * @param e the warning exception to be logged.
	 * @throws ServiceException Exception thrown when processing fails.
	 */
	public void logWarning(MessageContext ctx, Throwable e) throws ServiceException;

	
	/**
	 * This interface is used to pass initialization options when initializing 
	 * the logging handler.
	 *
	 */
	public static interface InitContext {
		/**
		 * 
		 * @return The service's ServiceId.
		 */
		public ServiceId getServiceId();

		/**
		 * @return A map of key value options.
		 */
		public Map<String,String> getOptions();

		/**
		 * Enables error logging.
		 */
		public void setSupportsErrorLogging();
	}
}
