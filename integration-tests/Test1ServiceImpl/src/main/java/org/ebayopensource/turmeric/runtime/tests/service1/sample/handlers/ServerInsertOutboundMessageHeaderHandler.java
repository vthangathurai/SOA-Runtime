/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.JavaObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers.RequestHeader;



public class ServerInsertOutboundMessageHeaderHandler extends BaseHandler {

	public void invoke(MessageContext ctx) throws ServiceException {
		OutboundMessage response = (OutboundMessage) ctx.getResponseMessage();
		ObjectNode node = null;
		RequestHeader header = new RequestHeader();
		header.setPartnerID("sdf");
		header.setPartnerTransactionDate("sdf");
		header.setPassword("sdfffff");
		node = new JavaObjectNodeImpl(new QName("http://iop.pb.com/", "RequestHeader", ""),header);
		response.addMessageHeader(node);
	}
}
