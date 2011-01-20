/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.pipeline;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;


/**
 * The error mapper provides server-side mapping from the list of exceptions in the MessageContext
 * (<code>getErrorList()</code> method), to the error response that will go out over the wire.
 * 
 * The default implementation maps the MessageContext exceptions to an ErrorMessage with one 
 * or more Errordata
 * 
 * @author ichernyshev
 */
public interface ErrorMapper {
	/**
	 * Called by the framework after the error mapper instance is constructed.
	 * for this service at service initialization
	 * 
	 * @param ctx An init context.
	 * @throws ServiceException Exception when init fails.
	 */
	public void init(InitContext ctx) throws ServiceException;

	/**
	 * Performs error mapping for the current outbound error flow.
	 * 
	 * Please note that non-ServiceExceptionInterface-compliant IMPL errors
	 * will be wrapped into AppErrorWrapperException
	 * 
	 * @param errors the list of errors from the MessageContext.
	 * @param ctx the server-side message context for the current invocation.
	 * @return the error response message.  This will be set by the framework as a parameter
	 * into the outbound response message.
	 * @throws ServiceException Exception when failed to map errors.
	 */
	public Object mapErrors(List<Throwable> errors, ServerMessageContext ctx) throws ServiceException;

	/**
	 * Interface to pass init parameter for ErrorMapper creation.
	 * @author wdeng
	 *
	 */
	public static interface InitContext {
		
		/**
		 * Returns the service id.
		 * @return the service id.
		 */
		public ServerServiceId getServiceId();
	}
}
