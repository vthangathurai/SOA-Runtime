/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.ws.Response;

public class PushResponse<T> implements Response<T> {

	private final T m_t;
	private final Throwable m_throwable;
	private final Map<String, Object> m_context;

	public PushResponse(T t, Throwable throwable, Map<String, Object> context) {
		m_t = t;
		m_throwable = throwable;
		m_context = context;
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	public T get() throws InterruptedException, ExecutionException {
		if(m_throwable != null)
			throw new ExecutionException(m_throwable);
		return m_t;
	}

	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return get();
	}

	public boolean isCancelled() {
		return false;
	}

	public boolean isDone() {
		return true;
	}

	public Map<String, Object> getContext() {
		return m_context;
	}

}
