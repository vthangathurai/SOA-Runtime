/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.util;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;

public class ReqHandler extends org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler
{
	public String headervalue = null;

	public void invoke(MessageContext ctx) throws ServiceException {
		System.out.println("ReqHandler-Start");
		throw new RuntimeException("Testing handler exception");
		
	}

	public void setHeaderValue(String val) {
		headervalue = val;
	}
}

