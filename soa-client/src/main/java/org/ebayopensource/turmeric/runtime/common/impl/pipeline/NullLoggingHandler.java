/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.pipeline;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandlerStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;


/**
 * Implements a no-op logging handler
 * 
 * @author ichernyshev
 */
public class NullLoggingHandler implements LoggingHandler {

	public void init(InitContext ctx) throws ServiceException {
		// noop
	}

	public void logProcessingStage(MessageContext ctx, LoggingHandlerStage stage) throws ServiceException {
		// noop
	}

	public void logError(MessageContext ctx, Throwable e) throws ServiceException {
		// noop
	}

	public void logWarning(MessageContext ctx, Throwable e) throws ServiceException {
		// noop
	}

	public void logResponseResidentError(MessageContext ctx, ErrorData errorData) {
		// noop
	}
}
