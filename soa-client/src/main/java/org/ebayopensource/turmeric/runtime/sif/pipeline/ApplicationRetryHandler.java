/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.pipeline;

import java.util.Collection;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;


/**
 * The application retry handler analyzes exceptions and error responses to determine if they should be retried
 * 
 * The handler class is configured using the &lt;retry-options&gt;&lt;app-level-retry-handler&gt; element within
 * &lt;invocation-options&gt;.  The framework will create and initialize a retry handler instance, and use it to test 
 * any service invocation errors for retryability.
 */
public interface ApplicationRetryHandler {
	/**
	 * Initialize the retry handler after automatic construction.
	 * 
	 * @param ctx the context used to initialize the handler
	 * @throws ServiceException throws when error happens
	 */
	public void init(InitContext ctx) throws ServiceException;

	/**
	 * Tests whether a given exception (local or remote) is retryable.
	 * 
	 * @param ctx 
	 * @param exception an exception from the service invocation error,
	 *        can be NULL if context is not currently available
	 * @return true if the exception is retryable
	 */
	public boolean isRetryable(ClientMessageContext ctx, Throwable exception);

	/**
	 * InitContext is the interface to provide parameters for ErrorResponseAdapter initialization.
	 */
	public static interface InitContext {
		/**
		 * Gets the service ID.
		 * @return the service ID
		 */
		public ClientServiceId getServiceId();

		/**
		 * Returns list of transport-level codes (such as HTTP status codes) which should be retried.
		 * 		
		 * @return a collection of retry transport codes
		 */
		public Collection<String> getRetryTransportCodes();

		/**
		 * Returns list of names of exceptions, either local or remote [encoded in the response ErrorData name],
		 * which should be retried.
		 * 
		 * @return a collection of retry exceptions
		 */
		public Collection<String> getRetryExceptions();

		/**
		 * Returns retry error Ids.
		 * @return a collection of error Ids.
		 */
		public Collection<String> getRetryErrorIds();
	}
}
