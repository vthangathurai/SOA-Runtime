/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.tests.common.util;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;

public class HeaderValidation extends org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler {

	@Override
	public void invoke(MessageContext ctx) throws ServiceException {
		if(!"Available".equals(ctx.getRequestMessage().getTransportHeader("MixedCaseHeader"))) {
			throw new RuntimeException("Customer header: MixedCaseHeader was not available ");
		}
		
	}

}
