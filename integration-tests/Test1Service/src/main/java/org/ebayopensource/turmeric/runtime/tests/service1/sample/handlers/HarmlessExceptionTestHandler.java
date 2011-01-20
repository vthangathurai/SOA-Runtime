/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers;

import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.tests.service1.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.Test1Constants;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.TestErrorTypes;



public class HarmlessExceptionTestHandler extends BaseHandler {
	public void invoke(MessageContext ctx) throws org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException {
		boolean needException = ctx.getRequestMessage().hasTransportHeader(
			Test1Constants.TR_HDR_TEST1_HARMLESS_EXCEPTION);

		if (needException) {
			throw new ServiceException(TestErrorTypes.HANDLER_HARMLESS_EXCEPTION_TEST);
		}
	}
}
