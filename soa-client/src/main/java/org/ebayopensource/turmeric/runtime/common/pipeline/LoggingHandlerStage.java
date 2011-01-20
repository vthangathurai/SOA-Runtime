/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

/**
 * @author ichernyshev
 */
public enum LoggingHandlerStage {

	/**
	 * Called just before beginning of request pipeline processing.
	 */
	REQUEST_STARTED,

	/**
	 * Called just before request dispatch (at the end of request pipeline
	 * processing).
	 */
	BEFORE_REQUEST_DISPATCH,

	/**
	 * Called when request dispatch starts (just before call to transport or
	 * IMPL).
	 */
	REQUEST_DISPATCH_START,

	/**
	 * Called when request dispatch starts (just before call to transport or
	 * IMPL) for Async Request.
	 */
	REQUEST_DISPATCH_ASYNC_SEND_START,

	/**
	 * Called when request dispatch ends (just before call to transport or
	 * IMPL) for Async Request.
	 */
	REQUEST_DISPATCH_ASYNC_SEND_COMPLETE,

	/**
	 * Called when response dispatch-processing starts (just before call to transport or
	 * IMPL) for Async Request.
	 */
	RESPONSE_DISPATCH_ASYNC_RECEIVE_START,

	/**
	 * Called when response dispatch-processing ends (just before call to transport or
	 * IMPL) for Async Request.
	 */
	RESPONSE_DISPATCH_ASYNC_RECEIVE_COMPLETE,

	/**
	 * Called when request dispatch completed (right fater call to transport or
	 * IMPL).
	 */
	REQUEST_DISPATCH_COMPLETE,

	/**
	 * Called just after dispatch (before beginning of response pipeline
	 * processing).
	 */
	RESPONSE_STARTED,

	/**
	 * Called after response dispatch.
	 */
	RESPONSE_COMPLETE;
}
