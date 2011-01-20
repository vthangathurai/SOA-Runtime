/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.handlers;

import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

public class RequestObjectNodeAccessHandler extends BaseHandler {
	public static final String H_REQUEST_TEST_OBJECT_NODE = "DoObjectNodeTest";
	public static final String H_BODY_1 = "TestHeaderMessageBody1";
	public static final String H_REQUEST_MSG_STRING = "TestHeaderReequestMSGString";
	public static final String H_REQUEST_BODY_NODE_CLASS_TYPE = "TestHeaderReequestBodyClassType";
	public static final String H_REQUEST_BODY_NODE_TYPE_PRE_DESER = "TestHeaderReequestBodyNodeTypePreDeser";
	public static final String H_REQUEST_BODY_NODE_TYPE_POST_DESER = "TestHeaderReequestBodyNodeTypePostDeser";
	
	public void invoke(MessageContext ctx) throws ServiceException {
		InboundMessage request = (InboundMessage)ctx.getRequestMessage();
		// If inbound message is empty skip this test.
		if (skipObjectNodeTests(request)) {
			return;
		}
		Message response = ctx.getResponseMessage();
		ObjectNode body = request.getMessageBody();
		try {
			if (null != body) {
				response.setTransportHeader(H_REQUEST_BODY_NODE_TYPE_PRE_DESER, body.getNodeType().name());
				Iterator<ObjectNode> children = body.getChildrenIterator();
				if (null != children) {
					while (children.hasNext()) {
						ObjectNode msg = children.next();
						if (!"MyMessage".equals(msg.getNodeName().getLocalPart())) {
							continue;
						}
						children = msg.getChildrenIterator();
						if (null != children) {
							while (children.hasNext()) {
								ObjectNode msgBody = children.next();
								if ("body".equals(msgBody.getNodeName().getLocalPart())) {
									response.setTransportHeader(H_BODY_1, msgBody.getNodeValue().toString());
								}
							}
						}
					}
				}
			}
			request.getParamCount();
			body = request.getMessageBody();
			if (null != body) {
				response.setTransportHeader(H_REQUEST_BODY_NODE_TYPE_POST_DESER, body.getNodeType().name());
				response.setTransportHeader(H_REQUEST_BODY_NODE_CLASS_TYPE, body.getNodeValue().getClass().getSimpleName());
				response.setTransportHeader(H_REQUEST_MSG_STRING, body.getNodeValue().toString());	
			}
		} catch (XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_READ_ERROR, ErrorConstants.ERRORDOMAIN,  new Object[] {e.getMessage()}), e);
		}
	}
	
	private boolean skipObjectNodeTests(InboundMessage msg) {
		try {
			if (msg.getTransportHeader(H_REQUEST_TEST_OBJECT_NODE) == null) {
				return true;
			}
			ServiceOperationParamDesc opDesc = msg.getParamDesc();
			if (opDesc == null ) {
				return true;
			}
			msg.getXMLStreamReader();
			return false;
		} catch (Exception e) {
			return true;
		}
	}
}
