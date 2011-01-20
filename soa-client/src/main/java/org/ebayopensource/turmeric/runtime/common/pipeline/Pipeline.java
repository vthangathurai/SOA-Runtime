/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;


/**
 * A Pipeline is a driver for running a message in sequence through 
 * configured handlers for a service. The pipeline has little message manipulation logic of its own and focuses on
 * invoking the handlers, and handling any exceptions that potentially arise during this invocation.
 * 
 * The pipeline also invokes ProtocolProcessor before and after the handlers' processing.
 * 
 * Pipelines are created on a per-service basis, and shared among multiple invoking threads.
 * 
 * @author ichernyshev, smalladi
 */
public interface Pipeline {

	/**
	 * Initialize the pipeline; called by the framework during service initialization.
	 * 
	 * @param ctx the context used to initialize the 
	 * @throws ServiceException throws when error happens
	 */
	public void init(InitContext ctx)
		throws ServiceException;

	/**
	 * The framework calls this method to drive pipeline processing for a specific invocation.
	 * @param ctx the message context for the current invocation
	 * @throws ServiceException throws when error happens
	 */
	public void invoke(MessageContext ctx)
		throws ServiceException;

	/**
	 * Returns the processing direction (request or response).
	 * @return the processing direction
	 */
	public PipelineMode getMode();

	/**
	 * InitContext is the interface to provide parameters for pipeline initialization. It 
	 * provides the following information
	 * <UL>
	 * <LI> The Service Id the service identifier (administrative name and any sub-identifier such as client configuration name) 
	 * of the associated service.
	 * <LI> The PipelineMode direction (request or response)
	 * <LI> The HandlerOptions a list of handler configurations (class names and handler exception/configuration options),
	 * in sequential order
	 * <LI> The ClassLoader to be used when creating, validating, and introspecting classes
	 * </UL>
	 *
	 */
	public static interface InitContext {
		/**
		 * Retrieves the service ID.
		 * @return the service ID
		 */
		public ServiceId getServiceId();

		/**
		 * Retrieves the pipeline mode.
		 * @return the pipeline mode
		 */
		public PipelineMode getPipelineMode();

		/**
		 * Retrieves the class loader.
		 * @return the class loader
		 */
		public ClassLoader getClassLoader();

		/**
		 * Retrieves the handler configurations.
		 * @return the handler configurations
		 */
		public List<HandlerOptions> getHandlerConfigs();
	}
}
