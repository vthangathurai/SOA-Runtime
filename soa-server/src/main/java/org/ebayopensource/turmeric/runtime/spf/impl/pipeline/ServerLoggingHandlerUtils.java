/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.pipeline;

import org.ebayopensource.turmeric.runtime.common.impl.pipeline.LoggingHandlerUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.spf.exceptions.AppErrorWrapperException;
import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;


/**
 * @author ichernyshev
 */
public class ServerLoggingHandlerUtils extends LoggingHandlerUtils {

	@Override
	public ErrorCategory getErrorCategory(MessageContext ctx, Throwable e) {
		if (e instanceof AppErrorWrapperException) {
			return ErrorCategory.APPLICATION;
		}

		return super.getErrorCategory(ctx, e);
	}
}
