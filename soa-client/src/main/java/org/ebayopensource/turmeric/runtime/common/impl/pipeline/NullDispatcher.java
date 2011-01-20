/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.pipeline;

import java.util.concurrent.Future;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;


/**
 * Dispatcher that does not perform any work
 * 
 * @author ichernyshev
 */
public class NullDispatcher implements Dispatcher {

	public void dispatchSynchronously(MessageContext ctx) {
		// noop
	}

	public Future<?> dispatch(MessageContext ctx) throws ServiceException {
		return null;
	}

	public void retrieve(MessageContext ctx, Future<?> name) {
		// noop
	}
}
