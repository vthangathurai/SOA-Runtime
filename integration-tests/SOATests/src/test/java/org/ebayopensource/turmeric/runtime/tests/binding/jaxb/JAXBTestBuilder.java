/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.binding.jaxb;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.binding.DeserializerFactory;
import org.ebayopensource.turmeric.runtime.common.binding.SerializerFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageProcessingStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.SimpleJettyServer;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;


/**
 * TODO: change to JAXBMessageTestBuilder
 * TODO: change variable use 'jaxbtest' to 'jaxbtxt'?
 * TODO: add method to create DataBindingDesc object? (new builder?) 
 */
public class JAXBTestBuilder {
	private String bindingName;
	private String contentType;
	private DeserializerFactory deserializerFactory;
	private DataBindingDesc inboundDBDesc;
	private String messageProtocol;
	private String opName = "myTestOperation";
	private boolean ordered = false;
	private DataBindingDesc outboundDBDesc;
	private byte payload[];
	private SerializerFactory serializerFactory;
	private URL serviceAddressUrl;
	private String serviceName = TestUtils.TEST1_SERVICE_NAME;

	public JAXBTestBuilder clearPayload() {
		this.payload = null;
		return this;
	}

	public String createOnWireString(MyMessage msg) throws Exception {
		MessageContext ctx = createTestMessageContext();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		serialize(ctx, out, msg);
		return out.toString("UTF-8");
	}

	public String createOnWireStringWithExtraEndTag(MyMessage msg)
			throws Exception {
		String xml1 = createOnWireString(msg);
		int lastEndTag = xml1.lastIndexOf("</");
		xml1 = xml1.substring(0, lastEndTag) + "</ExtraEndTag>"
				+ xml1.substring(lastEndTag);
		return xml1;
	}

	public String createOnWireStringWithMissingEndTag(MyMessage msg)
			throws Exception {
		String xml1 = createOnWireString(msg);
		int firstGTSign = xml1.indexOf('>');
		xml1 = xml1.substring(0, firstGTSign + 1) + "<ExtraStartTag>"
				+ xml1.substring(firstGTSign + 1);
		return xml1;
	}
	
	/**
	 * TODO: make default mechanism.
	 * 
	 * @param payload
	 * @return
	 * @throws Exception
	 */
	public MessageContext createTestMessageContext(byte payload[]) throws Exception {
		setPayload(payload);
		return createTestMessageContext();
	}
	
	/**
	 * TODO: make default mechanism.
	 * 
	 * @param payload
	 * @return
	 * @throws Exception
	 */
	public MessageContext createTestMessageContext(String payload) throws Exception {
		setPayload(payload);
		return createTestMessageContext();
	}

	public MessageContext createTestMessageContext() throws Exception {
		@SuppressWarnings("rawtypes")
		BaseMessageContextImpl ctx = (BaseMessageContextImpl) TestUtils
				.createServerMessageContext(serializerFactory.getPayloadType(),
						serviceName, opName, messageProtocol, payload,
						serviceAddressUrl, contentType);
		ctx.changeProcessingStage(MessageProcessingStage.RESPONSE_DISPATCH);
		InboundMessageImpl iMsg = (InboundMessageImpl) ctx.getRequestMessage();
		if (null != contentType) {
			iMsg.setTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE,
					contentType);
		}

		iMsg.setTransportHeader(SOAHeaders.ELEMENT_ORDERING_PRESERVE,
				Boolean.toString(ordered));

		return ctx;
	}

	public void serialize(MessageContext ctx, ByteArrayOutputStream out,
			Object msg1) throws ServiceException {
		OutboundMessage outMsg;
		outMsg = (OutboundMessage) ctx.getResponseMessage();
		outMsg.setParam(0, msg1);
		outMsg.serialize(out);
	}

	public JAXBTestBuilder setBindingName(String bindingName) {
		this.bindingName = bindingName;
		return this;
	}

	public JAXBTestBuilder setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public JAXBTestBuilder setDeserializerFactory(
			DeserializerFactory deserializerFactory) {
		this.deserializerFactory = deserializerFactory;
		return this;
	}

	public JAXBTestBuilder setInboundDBDesc(DataBindingDesc inboundDBDesc) {
		this.inboundDBDesc = inboundDBDesc;
		return this;
	}

	public JAXBTestBuilder setMessageProtocol(String messageProtocol) {
		this.messageProtocol = messageProtocol;
		return this;
	}

	public JAXBTestBuilder setOpName(String opName) {
		this.opName = opName;
		return this;
	}

	public JAXBTestBuilder setOrdered(boolean ordered) {
		this.ordered = ordered;
		return this;
	}
	
	public JAXBTestBuilder setOutboundDBDesc(DataBindingDesc outboundDBDesc) {
		this.outboundDBDesc = outboundDBDesc;
		return this;
	}

	public JAXBTestBuilder setPayload(byte[] payload) {
		this.payload = payload;
		return this;
	}

	public JAXBTestBuilder setPayload(ByteArrayOutputStream out) {
		this.payload = out.toByteArray();
		return this;
	}

	public JAXBTestBuilder setPayload(String data) {
		return setPayload(data, Charset.forName("UTF-8"));
	}

	public JAXBTestBuilder setPayload(String data, Charset charset) {
		this.payload = data.getBytes(charset);
		return this;
	}

	public JAXBTestBuilder setSerializerFactory(
			SerializerFactory serializerFactory) {
		this.serializerFactory = serializerFactory;
		return this;
	}

	public JAXBTestBuilder setServiceAddressUrl(URL serviceAddressUrl) {
		this.serviceAddressUrl = serviceAddressUrl;
		return this;
	}

	public JAXBTestBuilder setServiceName(String serviceName) {
		this.serviceName = serviceName;
		return this;
	}

	public JAXBTestBuilder setSymmetricDBDesc(DataBindingDesc dbDesc) {
		this.inboundDBDesc = dbDesc;
		this.outboundDBDesc = dbDesc;
		return this;
	}

	public JAXBTestBuilder setTestServer(SimpleJettyServer jetty)
			throws MalformedURLException {
		this.serviceAddressUrl = jetty.getSPFURI().toURL();
		return this;
	}
}
