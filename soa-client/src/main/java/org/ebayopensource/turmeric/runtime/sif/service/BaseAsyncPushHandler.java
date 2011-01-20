/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.service;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

/**
 * Base class for all asynchronous pushing callback handler.
 * @param <T> The type of the detailed response
 */
public abstract class BaseAsyncPushHandler<T> implements AsyncHandler<T> {
	private T m_request = null;

	/**
	 * Sets the request to be sent to the invoker.
	 * @param request the request
	 */
	void setRequest(T request) {
		m_request = request;
	}

	/**
	 * Gets the request.
	 * @return the request
	 */
	public T getRequest() {
		return m_request;
	}

	/* (non-Javadoc)
	 * @see javax.xml.ws.AsyncHandler#handleResponse(javax.xml.ws.Response)
	 */
	@Override
	public abstract void handleResponse(Response<T> response);
}
