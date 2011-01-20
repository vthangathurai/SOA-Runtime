/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers;

import java.util.Collection;
import java.util.Iterator;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.JavaObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;



public class ServerReadInsertMessageHeaderHandler extends BaseHandler {

	public void invoke(MessageContext ctx) throws ServiceException {

		InboundMessage requestMsg = (InboundMessage) ctx.getRequestMessage();
		OutboundMessage responseMsg = (OutboundMessage) ctx.getResponseMessage();

		// read message header from request msg and insert the same header back to response msg
		Collection<Object> c = requestMsg.getMessageHeadersAsJavaObject();
		if (c != null) {
			Iterator i = c.iterator();
			while (i.hasNext()) {
				Object obj = i.next();
				System.out.println("message header read: " + obj.getClass().toString());
				ObjectNode node = new JavaObjectNodeImpl(null, obj);
				responseMsg.addMessageHeader(node);
			}
		}

	}
}
