/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package org.ebayopensource.turmeric.advertisinguniqueidservicev1.handlers;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;

public class GUIDHandler extends BaseHandler {

	@SuppressWarnings("unchecked")
	@Override
	public void invoke(MessageContext ctx) throws ServiceException {
		BaseMessageContextImpl basemessageCtxImpl =  (BaseMessageContextImpl) ctx;
		basemessageCtxImpl.setRequestGuid("1234abcd");
	}

}
