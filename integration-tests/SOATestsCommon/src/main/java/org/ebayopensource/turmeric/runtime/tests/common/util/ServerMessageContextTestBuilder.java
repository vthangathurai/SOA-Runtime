/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.util;

import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis2.transport.http.HTTPConstants;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.BaseMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.InboundMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.OutboundMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.OutboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceDesc;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageContextImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageProcessor;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDescFactory;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.SimpleJettyServer;
import org.ebayopensource.turmeric.runtime.tests.common.sample.transports.TestTransport;
import org.junit.Assert;


/**
 * A bit more flexible option to the various 
 * TestUtils.createClientMessageContext() methods, along with
 * providing support for running with an embedded {@link SimpleJettyServer}
 */
public class ServerMessageContextTestBuilder {
	public static final String SOA_MESSAGE_PROTOCOL_VALUE = "TEST_CTX_CREATE";
	public static final String ELEMENT_ORDERING_PRESERVE_VALUE = "true";
	private static final String XML_INPUT_BODY;
	private static final String JSON_INPUT_BODY;
	private static final String NV_INPUT_BODY;
	public static final String TEST1_SERVICE_NAME = "test1";
	public static final String TEST1_SUBJECT = "Test SOA JAXB XML ser/deser";
	public static final String TEST1_EMAIL_ADDRESS = "soa@ebay.com";
	
	private String bindingName;
	private String serviceName = TEST1_SERVICE_NAME;
	private String messageProtocol;
	private String opName = "myTestOperation";
	private byte payload[];
	private String contentType;
	private URI serverUri;
	
	static {
	    /* Initializing complex body strings here with some formatting
	     * so its easier to see the structure and content
	     */
	    
        // @formatter:off
	    XML_INPUT_BODY = "<?xml version='1.0' encoding='UTF-8'?>" +
        "<ns2:MyMessage xmlns:ns2=\"http://www.ebay.com/test/soaframework/sample/types1\">" +
          "<ns2:body>SOA SOA, SOS.</ns2:body>" +
          "<ns2:recipients>" +
            "<entry>" +
              "<key>soa@ebay.com</key>" +
              "<value>" +
                "<ns2:city>San Jose</ns2:city>" +
                "<ns2:emailAddress>soa@ebay.com</ns2:emailAddress>" +
                "<ns2:postCode>95125</ns2:postCode>" +
                "<ns2:state>CA</ns2:state>" +
                "<ns2:streetNumber>2145</ns2:streetNumber>" +
              "</value>" +
            "</entry>" +
          "</ns2:recipients>" +
          "<ns2:subject>Test SOA JAXB XML ser/deser</ns2:subject>" +
        "</ns2:MyMessage>"; 
        // @formatter:on
	    
        // @formatter:off
	    JSON_INPUT_BODY = 
        "{" +
          "\"jsonns.ns\":\"http://www.ebay.com/test/soaframework/sample/types1\"," +
          "\"ns.MyMessage\":{" +
            "\"ns.body\":\"SOA SOA, SOS.\"," +
            "\"ns.recipients\":{" +
              "\"entry\":{" +
                "\"key\":\"soa@ebay.com\"," +
                "\"value\":{" +
                  "\"ns.city\":\"San Jose\"," +
                  "\"ns.emailAddress\":\"soa@ebay.com\"," +
                  "\"ns.postCode\":\"95125\"," +
                  "\"ns.state\":\"CA\"," +
                  "\"ns.streetNumber\":\"2145\"" +
                "}" +
              "}" +
            "}," +
            "\"ns.subject\":\"Test SOA JAXB XML ser/deser\"" +
          "}" +
        "}";
        // @formatter:on

        // @formatter:off
	    NV_INPUT_BODY = 
	    "nvns:ns2=\"http://www.ebay.com/test/soaframework/sample/types1\"" +
	    "&ns2:MyMessage.ns2:body=\"SOA+SOA%2C+SOS.\"" +
	    "&ns2:MyMessage.ns2:recipients.entry.key=\"soa%40ebay.com\"" +
	    "&ns2:MyMessage.ns2:recipients.entry.value.ns2:city=\"San+Jose\"" +
	    "&ns2:MyMessage.ns2:recipients.entry.value.ns2:emailAddress=\"soa%40ebay.com\"" +
	    "&ns2:MyMessage.ns2:recipients.entry.value.ns2:postCode=\"95125\"" +
	    "&ns2:MyMessage.ns2:recipients.entry.value.ns2:state=\"CA\"" +
	    "&ns2:MyMessage.ns2:recipients.entry.value.ns2:streetNumber=\"2145\"" +
	    "&ns2:MyMessage.ns2:subject=\"Test+SOA+JAXB+XML+ser%2Fdeser\"";
        // @formatter:on
	}
	
	public ServerMessageContext createServerMessageContext() throws Exception
	{
		ServerMessageProcessor.getInstance();
		ServerServiceDesc serverDesc = ServerServiceDescFactory.getInstance().getServiceDesc(serviceName);

		ProtocolProcessorDesc protocolProcessor;
		if (messageProtocol != null) {
			protocolProcessor = serverDesc.getProtocolProcessor(messageProtocol);
		} else {
			protocolProcessor = serverDesc.getNullProtocolProcessor();
		}

		Assert.assertThat("opName", opName, is(notNullValue()));
		ServiceOperationDesc operation = serverDesc.getOperation(opName);
		Assert.assertThat("operation", operation, is(notNullValue()));

		DataBindingDesc dbDesc = createTestDataBinding(bindingName, serverDesc);
		Assert.assertThat("dbDesc", dbDesc, is(notNullValue()));

		G11nOptions g11nOptions = new G11nOptions();
		Assert.assertThat("g11nOptions", g11nOptions, is(notNullValue()));

		ServiceAddress clientAddress = new ServiceAddress(null);
		ServiceAddress serviceAddress = new ServiceAddress(serverUri.toURL());
		Map<String,String> headers = new HashMap<String, String>();

		headers.put(SOAHeaders.ELEMENT_ORDERING_PRESERVE, ELEMENT_ORDERING_PRESERVE_VALUE);
		headers.put("Connection", "Keep-Alive");
		headers.put("Host", "localhost");
		if (contentType == null) {
			if (messageProtocol != null && messageProtocol.equals(SOAConstants.MSG_PROTOCOL_SOAP_12)) {
				headers.put("Content-Type".toUpperCase(), "application/soap+xml");
			} else {
				headers.put("Content-Type".toUpperCase(), "text/xml");
			}
		} else{
			headers.put("Content-Type", contentType);
		}
		headers.put("SOAPAction", opName);

		String payloadType = dbDesc.getPayloadType();
		headers.put(SOAHeaders.REQUEST_DATA_FORMAT, payloadType);
		headers.put(SOAHeaders.RESPONSE_DATA_FORMAT, payloadType);

		if (null == payload && contentType != SOAPTestUtils.ATTACHMENT_CONTENT_TYPE_STRING) {
			payload = XML_INPUT_BODY.getBytes();
			if (payloadType.equals("NV")) {
				payload = NV_INPUT_BODY.getBytes();
			} else if (payloadType.equals("JSON")) {
				payload = JSON_INPUT_BODY.getBytes();
			}
		}

		InputStream inputStream = null;
		if (null != payload) {
			inputStream = new ByteArrayInputStream(payload);
		}

		boolean isAttachment = (contentType != null && contentType.contains(HTTPConstants.MEDIA_TYPE_MULTIPART_RELATED));

		// Attachment handling
/*		BaseMessageAttachments attachments = null;
		if (isAttachment) {
			try {
				attachments = new InboundMessageAttachments(inputStream, contentType);
			} catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
*/
		Transport transport = new TestTransport();
		
		
		InboundMessageImpl requestMsg = new InboundMessageImpl(true,
			SOA_MESSAGE_PROTOCOL_VALUE, dbDesc, g11nOptions, headers, null, null, null, operation);
		
/*		if (isAttachment && null != inputStream) {
			inputStream= attachments.getInputStreamForMasterMessage();
		}
*/		
		if (null != inputStream)
			requestMsg.setInputStream(inputStream);


		BaseMessageAttachments outAttachments = null;
		if (isAttachment) {
			outAttachments = new OutboundMessageAttachments(messageProtocol);
		}
		OutboundMessageImpl responseMsg = new OutboundMessageImpl(false,
			SOA_MESSAGE_PROTOCOL_VALUE, dbDesc, g11nOptions, null, null, null, outAttachments, operation, false, 0);

		Charset effectiveCharset = serverDesc.getServiceCharset();
		if (effectiveCharset == null) {
			effectiveCharset = g11nOptions.getCharset();
		}

		Map<String,Object> systemProperties = null;
		String requestVersion = null;
		String serviceVersion = null;
		String targetServerName = "(none)";
		int targetServerPort = 0;
		String requestUri = serverUri.toASCIIString();
		// String requestUri = "none";
		Map<String, String> queryParams = null;

		ServerMessageContextImpl ctx = new ServerMessageContextImpl(
			serverDesc, operation, protocolProcessor, transport,
			requestMsg, responseMsg, serviceAddress, systemProperties, 
			clientAddress, requestVersion, serviceVersion,
			effectiveCharset, requestUri, 
			targetServerName, targetServerPort, queryParams);

		return ctx;
	}
	
	public ServerMessageContext createServerMessageContextForSojServiceTest()
	throws Exception
	{
		ServerMessageProcessor.getInstance();
		ServerServiceDesc serverDesc = ServerServiceDescFactory.getInstance().getServiceDesc(serviceName);

		ProtocolProcessorDesc protocolProcessor;
		if (messageProtocol != null) {
			protocolProcessor = serverDesc.getProtocolProcessor(messageProtocol);
		} else {
			protocolProcessor = serverDesc.getNullProtocolProcessor();
		}

		ServiceOperationDesc operation = serverDesc.getOperation(opName);

		DataBindingDesc dbDesc = createTestDataBinding(bindingName, serverDesc);

		G11nOptions g11nOptions = new G11nOptions();

		ServiceAddress clientAddress = new ServiceAddress(null);
		ServiceAddress serviceAddress = new ServiceAddress(serverUri.toURL());
		Map<String,String> headers = new HashMap<String, String>();

		headers.put(SOAHeaders.ELEMENT_ORDERING_PRESERVE, ELEMENT_ORDERING_PRESERVE_VALUE);
		headers.put("Connection", "Keep-Alive");
		headers.put("Host", "localhost");
		if (contentType == null) {
			if (messageProtocol != null && messageProtocol.equals(SOAConstants.MSG_PROTOCOL_SOAP_12)) {
				headers.put("Content-Type".toUpperCase(), "application/soap+xml");
			} else {
				headers.put("Content-Type".toUpperCase(), "text/xml");
			}
		} else{
			headers.put("Content-Type", contentType);
		}
		headers.put("SOAPAction", opName);

		String payloadType = dbDesc.getPayloadType();
		headers.put(SOAHeaders.REQUEST_DATA_FORMAT, payloadType);
		headers.put(SOAHeaders.RESPONSE_DATA_FORMAT, payloadType);

		if (null == payload && contentType != SOAPTestUtils.ATTACHMENT_CONTENT_TYPE_STRING) {
			payload = XML_INPUT_BODY.getBytes();
			if (payloadType.equals("NV")) {
				payload = NV_INPUT_BODY.getBytes();
			} else if (payloadType.equals("JSON")) {
				payload = JSON_INPUT_BODY.getBytes();
			}
		}

		InputStream inputStream = null;
		if (null != payload) {
			inputStream = new ByteArrayInputStream(payload);
		}

		boolean isAttachment = (contentType != null && contentType.contains(HTTPConstants.MEDIA_TYPE_MULTIPART_RELATED));
		// Attachment handling
		BaseMessageAttachments attachments = null;
		if (isAttachment) {
			attachments = new InboundMessageAttachments(inputStream, contentType);
		}

		Transport transport = new TestTransport();

		InboundMessageImpl requestMsg = new InboundMessageImpl(true,
			SOA_MESSAGE_PROTOCOL_VALUE, dbDesc, g11nOptions, headers, null, null, attachments, operation);
		if (isAttachment && null != inputStream) {
			inputStream= attachments.getInputStreamForMasterMessage();
		}
		if (null != inputStream)
			requestMsg.setInputStream(inputStream);


		BaseMessageAttachments outAttachments = null;
		if (isAttachment) {
			outAttachments = new OutboundMessageAttachments(messageProtocol);
		}
		OutboundMessageImpl responseMsg = new OutboundMessageImpl(false,
			SOA_MESSAGE_PROTOCOL_VALUE, dbDesc, g11nOptions, null, null, null, outAttachments, operation, false, 0);

		Charset effectiveCharset = serverDesc.getServiceCharset();
		if (effectiveCharset == null) {
			effectiveCharset = g11nOptions.getCharset();
		}
		
		Map<String,Object> systemProperties = null;
		String requestVersion = null;
		String serviceVersion = null;
		String targetServerName = "(none)";
		int targetServerPort = 0;
		String requestUri = serverUri.toASCIIString();
		Map<String, String> queryParams = null;

		ServerMessageContextImpl ctx = new ServerMessageContextImpl(
			serverDesc, operation, protocolProcessor, transport,
			requestMsg, responseMsg, serviceAddress, systemProperties, 
			clientAddress, requestVersion, serviceVersion,
			effectiveCharset, requestUri,
			targetServerName, targetServerPort, queryParams);

		return ctx;
	}
	
	private DataBindingDesc createTestDataBinding(String bindingName, ServiceDesc serviceDesc) {
//		SerializerFactory serFactory = new JAXBXMLSerializerFactory();
//		DeserializerFactory deserFactory = new JAXBXMLDeserializerFactory();
//		String contextPath = "org.ebayopensource.turmeric.runtime.tests.common.sample.service.message";
//		return new DataBindingDesc("XML", contextPath, serFactory, deserFactory, null, null);
		return serviceDesc.getDataBindingDesc(bindingName);
	}


	public String getBindingName() {
		return bindingName;
	}

	public String getContentType() {
		return contentType;
	}

	public String getMessageProtocol() {
		return messageProtocol;
	}

	public String getOpName() {
		return opName;
	}

	public byte[] getPayload() {
		return payload;
	}

	public String getServiceName() {
		return serviceName;
	}

	public ServerMessageContextTestBuilder setBindingName(String bindingName) {
		this.bindingName = bindingName;
		return this;
	}

	public ServerMessageContextTestBuilder setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public ServerMessageContextTestBuilder setMessageProtocol(String messageProtocol) {
		this.messageProtocol = messageProtocol;
		return this;
	}

	public ServerMessageContextTestBuilder setOpName(String opName) {
		this.opName = opName;
		return this;
	}

	public ServerMessageContextTestBuilder setPayload(byte[] payload) {
		this.payload = payload;
		return this;
	}

	public ServerMessageContextTestBuilder setServiceName(String serviceName) {
		this.serviceName = serviceName;
		return this;
	}

	public ServerMessageContextTestBuilder setTestServer(SimpleJettyServer jetty)
			throws MalformedURLException {
		this.serverUri = jetty.getSPFURI();
		return this;
	}
}
