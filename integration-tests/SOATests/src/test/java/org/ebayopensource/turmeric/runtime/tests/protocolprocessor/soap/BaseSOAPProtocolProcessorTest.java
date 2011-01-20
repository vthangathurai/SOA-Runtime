/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.protocolprocessor.soap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.binding.SerializerFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.ProtocolProcessorInitContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.Axis2Utils;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.BaseSOAPProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.SOAPUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.protocolprocessor.soap.ServerSOAPProtocolProcessor;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;
import org.ebayopensource.turmeric.runtime.tests.common.util.SOAPTestUtils;
import org.junit.Assert;
import org.junit.Test;

import com.ctc.wstx.exc.WstxParsingException;

public class BaseSOAPProtocolProcessorTest {
	public static ServerSOAPProtocolProcessor createServerProtocolProcessor(String protocolName, String version) throws ServiceException {
		ServerSOAPProtocolProcessor protocolProcessor = new ServerSOAPProtocolProcessor();
		ServiceId svcId = ServerServiceId.createFallbackServiceId("test_admin_name");
		ProtocolProcessorInitContextImpl initCtx =
			new ProtocolProcessorInitContextImpl(svcId, protocolName, version);
		protocolProcessor.init(initCtx);
		initCtx.kill();
		return protocolProcessor;
	}

	@Test
	public  void emptySOAPEnvelopeSerialization() throws Exception {
		ServerSOAPProtocolProcessor protocolProcessor = createServerProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		// create message context
		MessageContext ctx;
		
		ctx = SOAPTestUtils.createServerMessageContextForTest1Service(null);
		OutboundMessage msg = (OutboundMessage) ctx.getResponseMessage();

		// create axis outbound context and Empty SOAP envelope
		org.apache.axis2.context.MessageContext axis2OutContext = Axis2Utils.createOutboundAxis2Context(ctx, protocolProcessor.getConfigurationContext());
		SOAPEnvelope envelope = SOAPUtils.createSOAPEnvelope((OMElement)null, null);
		axis2OutContext.setEnvelope(envelope);
		ctx.setProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT, axis2OutContext);

		// create xml stream
		SerializerFactory serFactory = msg.getDataBindingDesc().getSerializerFactory();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
		XMLStreamWriter xmlStreamWriter = serFactory.getXMLStreamWriter(msg, null, bos);

		// invoke preSerialize
		protocolProcessor.preSerialize(msg, xmlStreamWriter);
		xmlStreamWriter.flush();

		String content = bos.toString();
		System.out.println("after preSerialize >> " + content);
        Assert.assertThat(content, endsWith("<soapenv:Body"));

		// invoke postSerialize
		protocolProcessor.postSerialize(msg, xmlStreamWriter);
		xmlStreamWriter.flush();
		content = bos.toString();
		System.out.println("after postSerialize >> " + content);
        Assert.assertThat(content, endsWith("</soapenv:Envelope>"));
	}

	@Test
	public  void emptySOAPFaultSerialization_SOAP11() throws Exception {
		ServerSOAPProtocolProcessor protocolProcessor = createServerProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		// create message context
		MessageContext ctx = SOAPTestUtils.createServerMessageContextForTest1Service(null);
		OutboundMessage msg = (OutboundMessage) ctx.getResponseMessage();

		// create axis outbound context and Empty SOAP Fault envelope
		org.apache.axis2.context.MessageContext axis2OutContext = Axis2Utils.createOutboundAxis2Context(ctx, protocolProcessor.getConfigurationContext());
		SOAPEnvelope envelope = OMAbstractFactory.getSOAP11Factory().getDefaultFaultEnvelope();
		axis2OutContext.setEnvelope(envelope);
		ctx.setProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT, axis2OutContext);

		// create xml stream
		SerializerFactory serFactory = msg.getDataBindingDesc().getSerializerFactory();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
		XMLStreamWriter xmlStreamWriter = serFactory.getXMLStreamWriter(msg, null, bos);

		// invoke preSerialize
		protocolProcessor.preSerialize(msg, xmlStreamWriter);
		xmlStreamWriter.flush();

		String content = bos.toString();
		System.out.println("after preSerialize >> " + content);
        Assert.assertThat(content, endsWith(SOAP11Constants.SOAP_FAULT_DETAIL_LOCAL_NAME));

		// invoke postSerialize
		protocolProcessor.postSerialize(msg, xmlStreamWriter);
		xmlStreamWriter.flush();
		content = bos.toString();
		System.out.println("after postSerialize >> " + content);
        Assert.assertThat(content, endsWith("</soapenv:Envelope>"));
	}

	@Test
	public  void emptySOAPFaultSerialization_SOAP12() throws Exception {
		ServerSOAPProtocolProcessor protocolProcessor = createServerProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		// create message context
		MessageContext ctx = SOAPTestUtils.createServerMessageContextForTest1Service(null);
		OutboundMessage msg = (OutboundMessage) ctx.getResponseMessage();

		// create axis outbound context and Empty SOAP1.2 Fault envelope
		org.apache.axis2.context.MessageContext axis2OutContext = Axis2Utils.createOutboundAxis2Context(ctx, protocolProcessor.getConfigurationContext());
		SOAPEnvelope envelope = OMAbstractFactory.getSOAP12Factory().getDefaultFaultEnvelope();
		axis2OutContext.setEnvelope(envelope);
		ctx.setProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT, axis2OutContext);

		// create xml stream
		SerializerFactory serFactory = msg.getDataBindingDesc().getSerializerFactory();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
		XMLStreamWriter xmlStreamWriter = serFactory.getXMLStreamWriter(msg, null, bos);

		// invoke preSerialize
		protocolProcessor.preSerialize(msg, xmlStreamWriter);
		xmlStreamWriter.flush();

		String content = bos.toString();
		System.out.println("after preSerialize >> " + content);
		Assert.assertThat(content, endsWith(SOAP12Constants.SOAP_FAULT_DETAIL_LOCAL_NAME));

		// invoke postSerialize
		protocolProcessor.postSerialize(msg, xmlStreamWriter);
		xmlStreamWriter.flush();
		content = bos.toString();
		System.out.println("after postSerialize >> " + content);
        Assert.assertThat(content, endsWith("</soapenv:Envelope>"));
	}

	@Test
	public  void postDeserializationGoodEnvelope() throws Exception {
		ServerSOAPProtocolProcessor protocolProcessor = createServerProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		InboundMessage requestMsg = SOAPTestUtils.createDummyInboundMessage(protocolProcessor);
		requestMsg.setInputStream(new ByteArrayInputStream(SOAPTestUtils.DEFAULT_EMPTY_ENVELOPE.getBytes()));
		// crate axis2 context and add to ebay context
		org.apache.axis2.context.MessageContext axisContext =
						SOAPTestUtils.createTestAxis2InboundMessageContext(protocolProcessor, requestMsg.getContext(), SOAConstants.MSG_PROTOCOL_SOAP_11);
		requestMsg.getContext().setProperty(BaseSOAPProtocolProcessor.AXIS_IN_CONTEXT, axisContext);

		XMLStreamReader reader = requestMsg.getXMLStreamReader();
		SOAPTestUtils.advanceXMLReaderToStartBody(reader);
		protocolProcessor.postDeserialize(requestMsg);
	}

	@Test
	public  void postDeserializationGoodEnvelopeWithNewline() throws Exception {
		ServerSOAPProtocolProcessor protocolProcessor = createServerProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		InboundMessage requestMsg = SOAPTestUtils.createDummyInboundMessage(protocolProcessor);
		requestMsg.setInputStream(new ByteArrayInputStream(SOAPTestUtils.GOOD_ENVELOPE_WITH_NEWLINE.getBytes()));
		// crate axis2 context and add to ebay context
		org.apache.axis2.context.MessageContext axisContext =
						SOAPTestUtils.createTestAxis2InboundMessageContext(protocolProcessor, requestMsg.getContext(), SOAConstants.MSG_PROTOCOL_SOAP_11);
		requestMsg.getContext().setProperty(BaseSOAPProtocolProcessor.AXIS_IN_CONTEXT, axisContext);

		XMLStreamReader reader = requestMsg.getXMLStreamReader();
		SOAPTestUtils.advanceXMLReaderToStartBody(reader);
		protocolProcessor.postDeserialize(requestMsg);
	}

	@Test
	public  void postDeserializationBadEnvelopeMissingEndBody() throws Exception {
		ServerSOAPProtocolProcessor protocolProcessor = createServerProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		try {
			InboundMessage requestMsg = SOAPTestUtils.createDummyInboundMessage(protocolProcessor);
			requestMsg.setInputStream(new ByteArrayInputStream(SOAPTestUtils.BAD_ENVELOPE_MISSING_ENDBODY.getBytes()));
			// crate axis2 context and add to ebay context
			org.apache.axis2.context.MessageContext axisContext =
							SOAPTestUtils.createTestAxis2InboundMessageContext(protocolProcessor, requestMsg.getContext(), SOAConstants.MSG_PROTOCOL_SOAP_11);
			requestMsg.getContext().setProperty(BaseSOAPProtocolProcessor.AXIS_IN_CONTEXT, axisContext);

			XMLStreamReader reader = requestMsg.getXMLStreamReader();
			SOAPTestUtils.advanceXMLReaderToStartBody(reader);
			protocolProcessor.postDeserialize(requestMsg);
            Assert.fail("Expected an Exception of type: " + WstxParsingException.class.getName());
        } catch (WstxParsingException e) {
            String expected = "Unexpected close tag </soapenv:Envelope>";
            Assert.assertThat(e.getMessage(), containsString(expected));
        }
	}

	@SuppressWarnings("unchecked")
    @Test
	public  void postDeserializationBadEnvelopeMissingEndEnvelope() throws Exception {
		ServerSOAPProtocolProcessor protocolProcessor = createServerProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		try {
			InboundMessage requestMsg = SOAPTestUtils.createDummyInboundMessage(protocolProcessor);
			requestMsg.setInputStream(new ByteArrayInputStream(SOAPTestUtils.BAD_ENVELOPE_MISSING_ENDENVELOPE.getBytes()));
			// crate axis2 context and add to ebay context
			org.apache.axis2.context.MessageContext axisContext =
							SOAPTestUtils.createTestAxis2InboundMessageContext(protocolProcessor, requestMsg.getContext(), SOAConstants.MSG_PROTOCOL_SOAP_11);
			requestMsg.getContext().setProperty(BaseSOAPProtocolProcessor.AXIS_IN_CONTEXT, axisContext);

			XMLStreamReader reader = requestMsg.getXMLStreamReader();
			SOAPTestUtils.advanceXMLReaderToStartBody(reader);
			protocolProcessor.postDeserialize(requestMsg);
            Assert.fail("Expected an Exception of type: " + ServiceException.class.getName());
        } catch (ServiceException e) {
            String expected1 = "Error reading from XML stream in SOAP11 protocol";
            String expected2 = "was expecting a close tag for element <soapenv:Envelope>";
            Assert.assertThat(e.getMessage(),
                            allOf(containsString(expected1), containsString(expected2)));
        }
	}

	@Test
	public  void sOAPGetMessageHeader() throws Exception {
		InboundMessageImpl reqMsgImpl = null;
		ServerSOAPProtocolProcessor protocolProcessor = createServerProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		SOAPTestUtils.createServerMessageContextForTest1Service(null);
		InboundMessage requestMsg = SOAPTestUtils.createDummyInboundMessage(protocolProcessor);
		requestMsg.setInputStream(new ByteArrayInputStream(SOAPTestUtils.GOOD_SOAP_12_REQUEST_WITH_HEADER.getBytes()));

		Assert.assertThat(requestMsg, instanceOf(InboundMessageImpl.class));
		reqMsgImpl = (InboundMessageImpl) requestMsg;

		Collection<ObjectNode> headers = reqMsgImpl.getMessageHeaders();
		Assert.assertNotNull(headers);

		Iterator<ObjectNode> i = headers.iterator();
		Assert.assertTrue("Headers should not be not empty", i.hasNext());
		ObjectNode headerNode = i.next();
		assertEquals(headerNode.getNodeName().getLocalPart(), "Token");
	}

	@Test
	public  void sOAPGetMessageBody() throws Exception {
		InboundMessageImpl reqMsgImpl = null;
		ServerSOAPProtocolProcessor protocolProcessor = createServerProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		SOAPTestUtils.createServerMessageContextForTest1Service(null);
		InboundMessage requestMsg = SOAPTestUtils.createDummyInboundMessage(protocolProcessor);
		requestMsg.setInputStream(new ByteArrayInputStream(SOAPTestUtils.GOOD_SOAP_12_REQUEST_WITH_HEADER.getBytes()));

        Assert.assertThat(requestMsg, instanceOf(InboundMessageImpl.class));
        reqMsgImpl = (InboundMessageImpl) requestMsg;

		ObjectNode bodyNode = reqMsgImpl.getMessageBody();
		assertNotNull(bodyNode);

		Iterator<ObjectNode> childIter = bodyNode.getChildrenIterator();
		assertTrue(childIter.hasNext());

		ObjectNode child = childIter.next();
		assertEquals(child.getNodeName().getLocalPart(), "MyMessage");
	}


}
