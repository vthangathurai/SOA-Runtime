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
 * This class provides a way for the service implementations, and its extended call path (e.g. metrics, clients
 * used by the service), to obtain the calling message context.
 *
 * @author ichernyshev
 */
public abstract class MessageContextAccessor {

	private static MessageContextAccessor s_instance;

	/**
	 * Concrete implementations of MessageContextAccessor use this method to access the static instance of the MessageContextAccessor.
	 * @return The MessageContextAccessor singleton.
	 */
	protected static MessageContextAccessor getInstance() {
		return s_instance;
	}

	/**
	 * Concrete implementations of MessageContextAccessor use this method to set a static MessageContextAccessor
	 * instance at initialization.
	 * @param value the accessor
	 */
	protected static void setInstance(MessageContextAccessor value) {
		if (s_instance != null) {
			throw new IllegalStateException();
		}

		if (value == null) {
			throw new NullPointerException();
		}

		s_instance = value;
	}

	/**
	 * Returns the current message context for this invocation.
	 * @return the message context
	 */
	public static MessageContext getContext() {
		if (s_instance == null) {
			return null;
		}

		return s_instance.getContextInternal();
	}

	/**
	 * Concrete implementations of MessageContextAccessor implement this method to get the context.
	 * @return the message context
	 */
	protected abstract MessageContext getContextInternal();
}
