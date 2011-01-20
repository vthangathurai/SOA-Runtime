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
 * Defines stages of pipeline processing.
 * @author ichernyshev, smalladi
 */
public enum MessageProcessingStage {
	/**
	 * The message context has been created, but the message processor has not started running the request pipeline.
	 */
	REQUEST_INIT(true), 
	/**
	 * This state is entered just before invoking the request pipeline.
	 */
	REQUEST_PIPELINE(true), 
	/**
	 * This state is entered just before invoking the request dispatcher.
	 */
	REQUEST_DISPATCH(true),
	/**
	 * This state is entered just before invoking the response pipeline.
	 */
	RESPONSE_PIPELINE(false), 
	/**
	 * This state is entered just before invoking the response dispatcher.
	 */
	RESPONSE_DISPATCH(false), 
	/**
	 * This state is entered just after invoking the response dispatcher.
	 */
	RESPONSE_COMPLETE(false);

	private final boolean m_isRequestDirection;

	private MessageProcessingStage(boolean isRequestDirection) {
		m_isRequestDirection = isRequestDirection;
	}

	/**
	 * Returns true if the invocation is currently undergoing request processing (stages from REQUEST_INIT through REQUEST_DISPATCH). 
	 * @return true if the invocation is currently undergoing request processing
	 */
	public boolean isRequestDirection() {
		return m_isRequestDirection;
	}

	/**
	 * Returns true if the invocation is currently undergoing response processing (stages from RESPONSE_DISPATCH through RESPONSE_COMPLETE). 
	 * @return true if the invocation is currently undergoing response processing
	 */
	public boolean isResponseDirection() {
		return !m_isRequestDirection;
	}
}
