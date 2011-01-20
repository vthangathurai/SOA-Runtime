/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.errors;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.DefaultErrorMapperImpl;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.Test1Constants;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.CustomErrorMessage;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.util.TestUtils;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;


/**
 * @author wdeng
 */
public class TestServerErrorMapper extends DefaultErrorMapperImpl {
	public void init(InitContext ctx) throws ServiceException {
		super.init(ctx);
	}

	public Object mapErrors(List<Throwable> errors, ServerMessageContext ctx) throws ServiceException
	{
		if (ctx.getRequestMessage().hasTransportHeader(Test1Constants.TR_HDR_ERROR_MAPPER_EXCEPTION)) {
			throw new RuntimeException("Test error mapper exception");
		}

		ErrorMessage errorMessage =  (ErrorMessage) super.mapErrors(errors, ctx);
		if (ctx.getOperationName().equals("customError1")) {
			List<ErrorType> errorTypeList = TestUtils.errorMessageToErrorTypeList(errorMessage);
			CustomErrorMessage customMessage = new CustomErrorMessage();
			customMessage.getError().addAll(errorTypeList);
			return customMessage;
		} else if (ctx.getOperationName().equals("customError2")) {
			List<ErrorType> errorTypeList2 = TestUtils.errorMessageToErrorTypeList(errorMessage);
			MyMessage result = new MyMessage();
			result.getError().addAll(errorTypeList2);
			return result;
		}

		return errorMessage;
	}
}
