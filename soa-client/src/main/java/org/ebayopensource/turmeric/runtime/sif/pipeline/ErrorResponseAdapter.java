/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.pipeline;

import org.ebayopensource.turmeric.runtime.common.service.ServiceId;

/**
 * Defines a client-side interface for mapping custom error types.
 * 
 * Methods from this interface can be called at various points and
 * should NOT throw any exception, even if the data looks totally bogus
 * 
 * @author ichernyshev
 */
public interface ErrorResponseAdapter {
	/**
	 * Initialize the custom exception handler after automatic construction.
	 * @param ctx the context used to initialize the adapter
	 */
	public void init(InitContext ctx);

	/**
	 * Returns the text of this custom error response.
	 * 
	 * @param errorResponse the custom error response
	 * @return  NULL is it cannot recognize error response
	 */
	public String getErrorText(Object errorResponse);

	/**
	 * Returns the appropriate exception class name.
	 *  
	 * @param errorResponse the custom error response
	 * @return NULL is it cannot recognize error response or
	 * there is no appropriate exception
	 */
	public String getExceptionClassName(Object errorResponse);

	/**
	 * Returns the appropriate error id or null.
	 * 
	 */
	/**
	 * @param errorResponse the custom error response
	 * @return NULL is it cannot recognize error response or there is no appropriate error id
	 * 
	 * NOTE: this function (as well as others) shoult NOT throw exceptions
	 * even if it cannot parse error id
	 */
	public Long getErrorId(Object errorResponse);

	/**
	 * Checks whether error response refers any system errors or it's app only.
	 * 
	 * @param errorResponse the custom error response
	 * @return  NULL is it cannot recognize error response
	 */
	public Boolean hasSystemErrors(Object errorResponse);

	/**
	 * InitContext is the interface to provide parameters for ErrorResponseAdapter initialization. 
	 * It provides the following information
	 * <UL>
	 * <LI> The Service Id the service identifier (administrative name and any sub-identifier such as client configuration name) 
	 * of the associated service.	
	 * </UL>
	 *
	 */
	public static interface InitContext {
		/**
		 * Get the service ID.
		 * @return the service ID
		 */
		public ServiceId getServiceId();
	}
}
