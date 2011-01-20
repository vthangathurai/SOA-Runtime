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
 * @author ichernyshev
 */
public interface AutoMarkdownStateFactory {

	/**
	 * Initialize the custom exception handler after automatic construction.
	 * 
	 * @param ctx the context used to initialize the factory
	 * @throws ServiceException throws when error happens
	 */
	public void init(InitContext ctx) throws ServiceException;

	/**
	 * Checks whether we support service level auto markdown only.
	 * 
	 * @return true if supported
	 */
	public boolean isSvcLevelAutoMarkdown();

	/**
	 * Creates automarkdown state if applicable, or returns null.
	 * 
	 * @param adminName The service administrative name
	 * @param opName The operation name
	 * @param clientName The Client name
	 * @return An AutoMarkdownState for the given operation of a service called by
	 *      a client of the service. 
	 */
	public AutoMarkdownState createAutoMarkdownState(String adminName,
		String opName, String clientName);

	/**
	 * InitContext is the interface to provide parameters for ErrorResponseAdapter initialization.
	 */
	public static interface InitContext {
		/**
		 * Gets the service Id.
		 * @return the service Id
		 */
		public ClientServiceId getServiceId();

		/**
		 * Returns list of transport-level codes (such as HTTP status codes),
		 * which should be counted towards markdown.
		 * 
		 * @return a collection of the transport code in String
		 */
		public Collection<String> getTransportCodes();

		/**
		 * Returns list of names of exceptions, either local or remote
		 * [encoded in the response ErrorData name], which should be counted.
		 * 
		 * @return a collection of the name of the exceptions
		 */
		public Collection<String> getExceptions();

		/**
		 * Returns list of error IDs.
		 * @return a collection of the error IDs
		 */
		public Collection<String> getErrorIds();

		/**
		 * Returns number of consecutive errors after which we should markdown automatically.
		 *  
		 * @return the error count threshold, zero if number of errors is not configured
		 */
		public int getErrorCountThreshold();
	}
}
