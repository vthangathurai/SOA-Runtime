/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.error;

import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.turmeric.common.v1.types.BaseResponse;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.handlers.ResponseResidentErrorHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;

public class ResponseResidentErrorLoggingHandler extends ResponseResidentErrorHandler {
    @Override
    protected List<CommonErrorData> getErrorDataList(MessageContext ctx) throws IllegalArgumentException {
        try {
            List<CommonErrorData> result = new ArrayList<CommonErrorData>();

            Message response = ctx.getResponseMessage();
            for (int i = 0; i < response.getParamCount(); ++i) {
                Object param = response.getParam(i);
                if (param instanceof BaseResponse)
                {
                    ErrorMessage errorMessage = ((BaseResponse)param).getErrorMessage();
                    if (errorMessage != null)
                        result.addAll(errorMessage.getError());
                }
            }

            return result;
        } catch (ServiceException x) {
            throw new IllegalArgumentException(x);
        }
    }
}
