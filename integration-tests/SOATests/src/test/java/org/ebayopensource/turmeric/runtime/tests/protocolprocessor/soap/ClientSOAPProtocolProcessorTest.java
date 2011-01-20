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

import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.ProtocolProcessorInitContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.BaseSOAPProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageProcessor;
import org.ebayopensource.turmeric.runtime.sif.impl.protocolprocessor.soap.ClientSOAPProtocolProcessor;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.runtime.tests.common.junit.NeedsConfig;
import org.ebayopensource.turmeric.runtime.tests.common.util.SOAPTestUtils;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


/**
 * Unittest for ClientSOAPProtocolProcessor class
 * @author gyue
 *
 */
public class ClientSOAPProtocolProcessorTest extends AbstractTurmericTestCase  {
	@Rule
	public NeedsConfig needsconfig = new NeedsConfig("testconfig");

	@Before
	public void initMessageProcessor() throws Exception {
		ClientMessageProcessor.getInstance();
	}

	public static ClientSOAPProtocolProcessor createClientProtocolProcessor(String protocolName, String version) throws ServiceException {
		ClientSOAPProtocolProcessor protocolProcessor = new ClientSOAPProtocolProcessor();
		ServiceId svcId = ServerServiceId.createFallbackServiceId("test_admin_name");
		ProtocolProcessorInitContextImpl initCtx =
			new ProtocolProcessorInitContextImpl(svcId, protocolName, version);
		protocolProcessor.init(initCtx);
		initCtx.kill();
		return protocolProcessor;
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void clientBeforeRequestDispatchPositive() throws Exception {
		// create client pp
		ClientSOAPProtocolProcessor protocolProcessor = createClientProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		// create context
		MessageContext ctx = SOAPTestUtils.createClientMessageContextForTest1Service(TestUtils.createTestMessage());

		// invoke beforeRequestPipeline
		protocolProcessor.beforeRequestDispatch(ctx);

		// expects Axis context is created
		Object obj = ctx.getProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT);
		Assert.assertNotNull("Axis2 OUT message context is not set.", obj);

		Assert.assertThat("Unknown context object encountered", obj, instanceOf(org.apache.axis2.context.MessageContext.class));
		// validate the axis context
		Assert.assertTrue("axis2 OUT message context validation failed", 
		                SOAPTestUtils.validateAxis2Context((org.apache.axis2.context.MessageContext)obj));
		// make sure there's no fault in the body
		Assert.assertFalse("SOAP Body has fault", ((org.apache.axis2.context.MessageContext)obj).getEnvelope().getBody().hasFault());
	}

	@Test
	public  void clientBeforeRequestDispatchPositive_SendSOAP11() throws Exception {
		// create client pp
		ClientSOAPProtocolProcessor protocolProcessor = createClientProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		// create context
		MessageContext ctx =
			SOAPTestUtils.createClientMessageContextForTest1Service(TestUtils.createTestMessage());

		// invoke beforeRequestPipeline
		protocolProcessor.beforeRequestDispatch(ctx);
		
		org.apache.axis2.context.MessageContext msgcontext = SOAPAssert.assertMessageValidOUTContext(ctx);
		SOAPAssert.assertIsSOAP11Envelope(msgcontext);
	}

	@Test
	public  void clientBeforeRequestDispatchPositive_SendSOAP12() throws Exception {
		// create client pp
		ClientSOAPProtocolProcessor protocolProcessor = createClientProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_12, "1.2");

		// create context
		MessageContext ctx =
			SOAPTestUtils.createClientMessageContextForTest1Service(TestUtils.createTestMessage());

		// invoke beforeRequestPipeline
		protocolProcessor.beforeRequestDispatch(ctx);

        org.apache.axis2.context.MessageContext msgcontext = SOAPAssert.assertMessageValidOUTContext(ctx);
        SOAPAssert.assertIsSOAP12Envelope(msgcontext);
	}

	@Test
	public  void clientBeforeResponsePipelinePositive() throws Exception {
		// create client pp
		ClientSOAPProtocolProcessor protocolProcessor = createClientProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		// create context
		MessageContext ctx =
			SOAPTestUtils.createClientMessageContextForTest1Service(null, SOAPTestUtils.GOOD_SOAP_REQUEST);

		// crate axis2 context and add to ebay context
		org.apache.axis2.context.MessageContext axisContext = SOAPTestUtils.createTestAxis2OutboundMessageContext(protocolProcessor, ctx);
		ctx.setProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT, axisContext);

		// invoke beforeResponseDispatch
		protocolProcessor.beforeResponsePipeline(ctx);

		// expects Axis in context is created
        org.apache.axis2.context.MessageContext msgcontext = SOAPAssert.assertMessageValidINContext(ctx);
        SOAPAssert.assertBodyHasNoFault(msgcontext);

		// expects XMLReader pointing to the beginning of body
		// DO NOT GET THE READER FROM SOAP ENVELOPE - it's a different one! (why?)
		//XMLStreamReader reader = axisContext.getEnvelope().getBody().getXMLStreamReader();
		XMLStreamReader reader = ((InboundMessage)ctx.getResponseMessage()).getXMLStreamReader();
		if (!SOAPTestUtils.validateXMLReaderAtStartElement(reader, SOAPTestUtils.START_XML_BODY_ELEMENT)) {
			assertTrue(false);
		}
	}

	@Test
	public  void clientBeforeResponsePipelinePositive_SOAP12() throws Exception {
		// create client pp
		ClientSOAPProtocolProcessor protocolProcessor = createClientProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_12, "1.2");

		// create context
		MessageContext ctx =
			SOAPTestUtils.createClientMessageContextForTest1Service(null, SOAPTestUtils.GOOD_SOAP_12_REQUEST);

		// crate axis2 context and add to ebay context
		org.apache.axis2.context.MessageContext axisContext = SOAPTestUtils.createTestAxis2OutboundMessageContext(protocolProcessor, ctx);
		ctx.setProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT, axisContext);

		// invoke beforeResponseDispatch
		protocolProcessor.beforeResponsePipeline(ctx);

		// expects Axis in context is created
        org.apache.axis2.context.MessageContext msgcontext = SOAPAssert.assertMessageValidINContext(ctx);
        SOAPAssert.assertBodyHasNoFault(msgcontext);

		// expects XMLReader pointing to the beginning of body
		// DO NOT GET THE READER FROM SOAP ENVELOPE - it's a different one! (why?)
		//XMLStreamReader reader = axisContext.getEnvelope().getBody().getXMLStreamReader();
		XMLStreamReader reader = ((InboundMessage)ctx.getResponseMessage()).getXMLStreamReader();
		if (!SOAPTestUtils.validateXMLReaderAtStartElement(reader, SOAPTestUtils.START_XML_BODY_ELEMENT)) {
			assertTrue(false);
		}
	}

	@Test
	public  void clientBeforeResponsePipelinePositive_ReceiveFault() throws Exception {
		// create client pp
		ClientSOAPProtocolProcessor protocolProcessor = createClientProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		// create context
		MessageContext ctx =
			SOAPTestUtils.createClientMessageContextForTest1Service(null, SOAPTestUtils.GOOD_SOAP_FAULT);

		// crate axis2 context and add to ebay context
		org.apache.axis2.context.MessageContext axisContext = SOAPTestUtils.createTestAxis2OutboundMessageContext(protocolProcessor, ctx);
		ctx.setProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT, axisContext);

		// invoke beforeResponsePipeline
		protocolProcessor.beforeResponsePipeline(ctx);

		// expects Axis in context is created
        org.apache.axis2.context.MessageContext msgcontext = SOAPAssert.assertMessageValidINContext(ctx);
        SOAPAssert.assertBodyHasFault(msgcontext);

		// expects XMLReader pointing to the beginning of fault element
		// DO NOT GET THE READER FROM SOAP ENVELOPE - it's a different one! (why?)
		//XMLStreamReader reader = axisContext.getEnvelope().getBody().getXMLStreamReader();
		XMLStreamReader reader = ((InboundMessage)ctx.getResponseMessage()).getXMLStreamReader();
		if (!SOAPTestUtils.validateXMLReaderAtStartElement(reader, SOAPTestUtils.START_XML_BODY_ERROR_ELEMENT)) {
			assertTrue(false);
		}
	}

	@Test
	public  void clientBeforeResponsePipelinePositiveWithActor_ReceiveFault() throws Exception {
		// create client pp
		ClientSOAPProtocolProcessor protocolProcessor = createClientProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

		// create context
		MessageContext ctx =
			SOAPTestUtils.createClientMessageContextForTest1Service(null, SOAPTestUtils.GOOD_SOAP_FAULT_WITH_ACTOR);

		// crate axis2 context and add to ebay context
		org.apache.axis2.context.MessageContext axisContext = SOAPTestUtils.createTestAxis2OutboundMessageContext(protocolProcessor, ctx);
		ctx.setProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT, axisContext);

		// invoke beforeResponsePipeline
		protocolProcessor.beforeResponsePipeline(ctx);

		// expects Axis in context is created
        org.apache.axis2.context.MessageContext msgcontext = SOAPAssert.assertMessageValidINContext(ctx);
        SOAPAssert.assertBodyHasFault(msgcontext);

		// expects XMLReader pointing to the beginning of fault element
		// DO NOT GET THE READER FROM SOAP ENVELOPE - it's a different one! (why?)
		//XMLStreamReader reader = axisContext.getEnvelope().getBody().getXMLStreamReader();
		XMLStreamReader reader = ((InboundMessage)ctx.getResponseMessage()).getXMLStreamReader();
		if (!SOAPTestUtils.validateXMLReaderAtStartElement(reader, SOAPTestUtils.START_XML_BODY_ERROR_ELEMENT)) {
			assertTrue(false);
		}
	}

	@Test
	public  void clientBeforeResponsePipelineNegative_InvalidStartBodyTag() throws Exception {
		try {
			// create client pp
			ClientSOAPProtocolProcessor protocolProcessor = createClientProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

			// create context
			MessageContext ctx =
					SOAPTestUtils.createClientMessageContextForTest1Service(null, SOAPTestUtils.BAD_SOAP_REQUEST_INVALIDSTARTBODYTAG);

			// crate axis2 context and add to ebay context
			org.apache.axis2.context.MessageContext axisContext = SOAPTestUtils.createTestAxis2OutboundMessageContext(protocolProcessor, ctx);
			ctx.setProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT, axisContext);

			// invoke beforeResponsePipeline
			protocolProcessor.beforeResponsePipeline(ctx);
            Assert.fail("Expected an Exception of type: " + ServiceException.class.getName());
        } catch (ServiceException e) {
            String expected = "Payload Body or Header could not be parsed";
            Assert.assertThat(e.getMessage(), containsString(expected));
        }
	}

	@SuppressWarnings("unchecked")
    @Test
	public  void clientBeforeResponsePipelineNegative_InvalidStartEnvelopeTag() throws Exception {
		try {
			// create client pp
			ClientSOAPProtocolProcessor protocolProcessor = createClientProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");

			// create context
			MessageContext ctx =
				SOAPTestUtils.createClientMessageContextForTest1Service(null, SOAPTestUtils.BAD_SOAP_REQUEST_INVALIDSTARTENVELOPETAG);

			// crate axis2 context and add to ebay context
			org.apache.axis2.context.MessageContext axisContext = SOAPTestUtils.createTestAxis2OutboundMessageContext(protocolProcessor, ctx);
			ctx.setProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT, axisContext);

			// invoke beforeResponsePipeline
			protocolProcessor.beforeResponsePipeline(ctx);
            Assert.fail("Expected an Exception of type: " + ServiceException.class.getName());
        } catch (ServiceException e) {
            String expected1 = "First Element must contain the local name, Envelope";
            String expected2 = "but found EnvelopeBAD";
            Assert.assertThat(e.getMessage(),
                            allOf(containsString(expected1), containsString(expected2)));
        }
	}

	@Test
	public  void clientBeforeResponsePipelineNegative_EnvelopeNamespaceMistach() throws Exception {
		// create client pp
		ClientSOAPProtocolProcessor protocolProcessor = createClientProtocolProcessor(SOAConstants.MSG_PROTOCOL_SOAP_12, "1.2");

		// TESET1: SOAP12 pp receiving a SOAP11 envelope
		try {
			// create context: receive a soap11 envelope
			MessageContext ctx =
				SOAPTestUtils.createClientMessageContextForTest1Service(null, SOAPTestUtils.GOOD_SOAP_REQUEST);

			// crate axis2 context and add to ebay context
			org.apache.axis2.context.MessageContext axisContext = SOAPTestUtils.createTestAxis2OutboundMessageContext(protocolProcessor, ctx);
			ctx.setProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT, axisContext);

			// invoke beforeResponseDispatch
			protocolProcessor.beforeResponsePipeline(ctx);
            Assert.fail("Expected an Exception of type: " + ServiceException.class.getName());
        } catch (ServiceException e) {
            String expected = "Transport level information does not match with SOAP Message namespace URI";
            Assert.assertThat(e.getMessage(), containsString(expected));
        }

		// TESET2: SOAP11 pp receiving a SOAP12 envelope
		try {
			// initialize SOAP11 SOAP processor
			ServiceId svcId = ServerServiceId.createFallbackServiceId("test_admin_name");
			ProtocolProcessorInitContextImpl initCtx =
				new ProtocolProcessorInitContextImpl(svcId, SOAConstants.MSG_PROTOCOL_SOAP_11, "1.1");
			protocolProcessor.init(initCtx);
			initCtx.kill();

			// create context: receive a soap11 envelope
			MessageContext ctx =
				SOAPTestUtils.createClientMessageContextForTest1Service(null, SOAPTestUtils.GOOD_SOAP_12_REQUEST);

			// crate axis2 context and add to ebay context
			org.apache.axis2.context.MessageContext axisContext = SOAPTestUtils.createTestAxis2OutboundMessageContext(protocolProcessor, ctx);
			ctx.setProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT, axisContext);

			// invoke beforeResponseDispatch
			protocolProcessor.beforeResponsePipeline(ctx);
            Assert.fail("Expected an Exception of type: " + ServiceException.class.getName());
        } catch (ServiceException e) {
            String expected = "Transport level information does not match with SOAP Message namespace URI";
            Assert.assertThat(e.getMessage(), containsString(expected));
        }
	}
}
