/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.pipeline;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.pipeline.LoggingHandlerUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ErrorResponseAdapter;


/**
 * @author ichernyshev
 */
public class ClientLoggingHandlerUtils extends LoggingHandlerUtils {

	public String getCustomResponseText(ClientMessageContext ctx, Object errorResponse) {
		ErrorResponseAdapter responseAdapter = ctx.getServiceContext().getCustomErrorResponseAdapter();

		if (responseAdapter == null) {
			// we cannot process this response type, assume it's all application errors
			return null;
		}

		try {
			return responseAdapter.getErrorText(errorResponse);
		} catch (Throwable e) {
			getLogger().log(Level.SEVERE, "ErrorResponseAdapter '" + responseAdapter.getClass().getName() +
				"' threw unexpected error in getErrorText " + e.toString(), e);
		}

		return null;
	}

	private static Logger getLogger() {
		return LogManager.getInstance(ClientLoggingHandlerUtils.class);
	}
}
