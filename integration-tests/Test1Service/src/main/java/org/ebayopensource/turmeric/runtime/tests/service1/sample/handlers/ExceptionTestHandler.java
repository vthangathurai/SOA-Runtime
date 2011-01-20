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
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.TestErrorTypes;


public class ExceptionTestHandler extends BaseHandler {
	public final static String KEY_THROW_EXCEPTION = "ThrowException";
	public final static String KEY_HANDLER_NAME = "HandlerName";

	public final static String NAME_CONTINUE_ON_ERROR_HANDLER = "TestExceptionContinueOnError";
	public final static String NAME_STOP_AT_ERROR_HANDLER = "TestExceptionStopOnError";

	public void invoke(MessageContext ctx) throws ServiceException {
		String nameProp = (String)ctx.getProperty(KEY_HANDLER_NAME);
		if (!getName().equals(nameProp)) {
			return;
		}
		Object throwException = ctx.getProperty(KEY_THROW_EXCEPTION);
		if (null == throwException || Boolean.FALSE == throwException) {
			return;
		}	
		throw new ServiceException(TestErrorTypes.HANDLER_EXCEPTION_TEST,
			new Object[] {"MyTestData"});
	}
}
