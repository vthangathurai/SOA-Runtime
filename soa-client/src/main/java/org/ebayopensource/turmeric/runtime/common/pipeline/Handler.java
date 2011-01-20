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


/**
 * Each handler is expected to implement this interface. Handlers may update the
 * MessageContext that is passed to them. If the processing fails, they are expected to throw
 * only HandlerInvocationException. Anything else thrown is indicated as an Error (i.e, abnormal)
 * and the stack pops out. Upon throwing HandlerInvocationException, depending on whether the handler
 * has indicated to comtinue or errors or not, the processing may either continue or an error response
 * is generated.
 * 
 * @author smalladi
 */
public interface Handler {
	
	/**
	 * Initializes the handler with an init context.
	 * 
	 * @param ctx the context used to initilize the handler
	 * @throws ServiceException throws when error happens
	 */
	public void init(InitContext ctx) throws ServiceException;

	/**
	 * Invoke the handler in order to perform work such as storing state, validating request data, or modifying requests or 
	 * responses.
	 * @param ctx the message context of the currently executing operation
	 * @throws ServiceException throws when error happens
	 */
	public void invoke(MessageContext ctx) throws ServiceException;

	/**
	 * InitContext is an interface to pass parameters for handler initialization. A handler
	 * InitContext contains 
	 * <UL>
	 * <LI> svcId the service identifier (administrative name and any sub-identifier such as client configuration name) 
	 * of the associated service.
	 * <LI> name the name of the handler as given in the configuration
	 * <LI> options the property map of handler options as given in the configuration
	 * </UL>
	 * 
	 * @author wdeng
	 *
	 */
	public static interface InitContext {
		/**
		 * Get the service ID.
		 * @return service ID
		 */
		public ServiceId getServiceId();

		/**
		 * Get the name.
		 * @return the name
		 */
		public String getName(); 

		/**
		 * Get the options.
		 * @return the options
		 */
		public Map<String,String> getOptions();
	}
}
