/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.pipeline;

import java.util.concurrent.Future;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.BaseServiceRequestDispatcher;


/**
 * Invokes the actual service implementation
 * 
 * This implementation should provide both synchronous and asynchronous modes of execution
 * 
 * @author ichernyshev
 */
public class SimpleInvokerDispatcher implements Dispatcher {

	private final BaseServiceRequestDispatcher m_serviceDispatcher;

	public SimpleInvokerDispatcher(BaseServiceRequestDispatcher serviceDispatcher) {
		if (serviceDispatcher == null) {
			throw new NullPointerException();
		}

		m_serviceDispatcher = serviceDispatcher;
	}

	public void dispatchSynchronously(MessageContext ctx) throws ServiceException {
		m_serviceDispatcher.dispatchSynchronously(ctx);
	}

	public Future<?> dispatch(MessageContext ctx) throws ServiceException {
		throw new UnsupportedOperationException("asynchronous dispatch is not supported in this version.");
	}

	public void retrieve(MessageContext ctx, Future<?> name) throws ServiceException {
		throw new UnsupportedOperationException("asynchronous dispatch is not supported in this version.");
	}
}
