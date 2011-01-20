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
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.StringObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;



public class ClientInsertMessageHeaderHandler extends BaseHandler {

	public void invoke(MessageContext ctx) throws ServiceException {
		OutboundMessage request = (OutboundMessage) ctx.getRequestMessage();
		ObjectNode node = null;
		boolean isObject = true;
		if (!isObject) {
			node = new StringObjectNodeImpl(new QName("http://iop.pb.com", "RequestHeader", ""), 
				     "	<PartnerID>1</PartnerID> \n" +
				     "  <Password>Paypal</Password> \n" +
				     "  <PartnerTransactionID>1234</PartnerTransactionID> \n" +
				     "  <WSVersionNumber>IOP_V0201</WSVersionNumber> \n" +
				     "  <PartnerTransactionDate>2007-08-04  00:00:00</PartnerTransactionDate>"); 
		} else {
			RequestHeader header = new RequestHeader();
			header.setPartnerID("sdf");
			header.setPartnerTransactionDate("sdf");
			header.setPassword("sdfffff");
			node = new JavaObjectNodeImpl(new QName("http://iop.pb.com/", "RequestHeader", ""),header);
		}
		request.addMessageHeader(node);
	}
}
