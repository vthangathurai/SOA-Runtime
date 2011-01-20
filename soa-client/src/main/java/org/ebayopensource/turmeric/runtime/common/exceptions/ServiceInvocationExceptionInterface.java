/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.exceptions;

import java.util.List;

/**
 * Interface for the wrapped service exceptions that are propagated to
 * the clients during the response processing.
 * @author ichernyshev
 */
public interface ServiceInvocationExceptionInterface extends ServiceExceptionInterface {

	
	/**
	 * @return List of Throwable representing client side errors.
	 */
	public List<Throwable> getClientErrors();

	/**
	 * @return The ErrorResponse returned by the service impl.
	 */
	public Object getErrorResponse();

	/**
	 * @return The server side exception is an exception thrown by service implementation.
	 */
	public boolean isAppOnlyException();

	/**
	 * @return The Request GUID.
	 */
	public String getRequestGuid();

	/**
	 * @return The application exception thrown by service implementation.
	 */
	public Throwable getApplicationException();
}
