/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.lang.StringUtils;
import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorLibraryBaseErrors;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.BaseMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.InboundMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.attachment.OutboundMessageAttachments;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.OutboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceDesc;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageProcessingStage;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageContextImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDescFactory;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageContextImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageProcessor;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDescFactory;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;
import org.ebayopensource.turmeric.runtime.tests.common.sample.transports.TestTransport;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorClassificationCodeType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorParameterType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.ErrorType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.errors.SeverityCodeType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.Address;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;

import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;

import org.junit.Assert;





/**
 * @author wdeng
 */
public class TestUtils {
	public static final String PAYLOAD_UNORDERED_NV = "Unordered NV";

	private static final HashMap<String, String> s_mimeTypes = new HashMap<String, String>();
	static {
		s_mimeTypes.put(BindingConstants.PAYLOAD_JSON, SOAConstants.MIME_JSON);
		s_mimeTypes.put(BindingConstants.PAYLOAD_NV, SOAConstants.MIME_NV);
		s_mimeTypes.put(PAYLOAD_UNORDERED_NV, SOAConstants.MIME_NV);
		s_mimeTypes.put(BindingConstants.PAYLOAD_XML, SOAConstants.MIME_XML);
		s_mimeTypes.put(BindingConstants.PAYLOAD_FAST_INFOSET, SOAConstants.MIME_FAST_INFOSET);

	}

	public static final String TEST1_SERVICE_NAME = "test1";
	public static final QName TEST1_SERVICE_QNAME = new QName(SOAConstants.DEFAULT_SERVICE_NAMESPACE, TEST1_SERVICE_NAME);

	public static final String ELEMENT_ORDERING_PRESERVE_VALUE = "true";
//TODO: when package annotation is implemented use these right ones
	public static final String XML_INPUT_BODY = "<?xml version='1.0' encoding='UTF-8'?><ns2:MyMessage xmlns:ns2=\"http://www.ebay.com/test/soaframework/sample/service/message\"><ns2:body>SOA SOA, SOS.</ns2:body><ns2:recipients><entry><key>soa@ebay.com</key><value><ns2:city>San Jose</ns2:city><ns2:emailAddress>soa@ebay.com</ns2:emailAddress><ns2:postCode>95125</ns2:postCode><ns2:state>CA</ns2:state><ns2:streetNumber>2145</ns2:streetNumber></value></entry></ns2:recipients><ns2:subject>Test SOA JAXB XML ser/deser</ns2:subject></ns2:MyMessage>";
	public static final String JSON_INPUT_BODY = "{\"jsonns.ns\":\"http://www.ebay.com/test/soaframework/sample/service/message\",\"ns.MyMessage\":{\"ns.body\":\"SOA SOA, SOS.\",\"ns.recipients\":{\"entry\":{\"key\":\"soa@ebay.com\",\"value\":{\"ns.city\":\"San Jose\",\"ns.emailAddress\":\"soa@ebay.com\",\"ns.postCode\":\"95125\",\"ns.state\":\"CA\",\"ns.streetNumber\":\"2145\"}}},\"ns.subject\":\"Test SOA JAXB XML ser/deser\"}}";
	public static final String NV_INPUT_BODY = "nvns:ns2=\"http://www.ebay.com/test/soaframework/sample/service/message\"&ns2:MyMessage.ns2:body=\"SOA+SOA%2C+SOS.\"&ns2:MyMessage.ns2:recipients.entry.key=\"soa%40ebay.com\"&ns2:MyMessage.ns2:recipients.entry.value.ns2:city=\"San+Jose\"&ns2:MyMessage.ns2:recipients.entry.value.ns2:emailAddress=\"soa%40ebay.com\"&ns2:MyMessage.ns2:recipients.entry.value.ns2:postCode=\"95125\"&ns2:MyMessage.ns2:recipients.entry.value.ns2:state=\"CA\"&ns2:MyMessage.ns2:recipients.entry.value.ns2:streetNumber=\"2145\"&ns2:MyMessage.ns2:subject=\"Test+SOA+JAXB+XML+ser%2Fdeser\"";

	public static final String SOA_MESSAGE_PROTOCOL_VALUE = "TEST_CTX_CREATE";

	/**
	 * @deprecated Does not support embedded jetty, Use {@link ServerMessageContextTestBuilder}
	 */
	@Deprecated
	public static ServerMessageContext createServerMessageContextForTest1Service(String bindingName) throws Exception {
		return createServerMessageContext(bindingName, TEST1_SERVICE_NAME, null, null);
	}

	/**
	 * @deprecated Does not support embedded jetty, Use {@link ServerMessageContextTestBuilder}
	 */
	@Deprecated
	public static ServerMessageContext createServerMessageContext(String bindingName, String serviceName, String messageProtocol,
			String payload, URL serviceAddressUrl, String contentType) throws Exception {
		return createServerMessageContext(bindingName, serviceName, "myTestOperation", messageProtocol,
				payload, serviceAddressUrl, contentType);
	}

	/**
	 * @deprecated Does not support embedded jetty, Use {@link ServerMessageContextTestBuilder}
	 */
	@Deprecated
	public static ServerMessageContext createServerMessageContext(String bindingName, String serviceName, String opName, String messageProtocol,
		String payload, URL serviceAddressUrl, String contentType) throws Exception
	{
		return createServerMessageContext(bindingName, serviceName, opName, messageProtocol,
			(payload != null ? payload.getBytes() : null), serviceAddressUrl, contentType);
	}

	/**
	 * @deprecated Does not support embedded jetty, Use {@link ServerMessageContextTestBuilder}
	 */
	@Deprecated
	public static ServerMessageContext createServerMessageContext(String bindingName, String serviceName, String opName, String messageProtocol,
		byte[] payload, URL serviceAddressUrl, String contentType) throws Exception
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
		ServiceAddress serviceAddress = new ServiceAddress(serviceAddressUrl);
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
*/		if (null != inputStream)
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

		ServerMessageContextImpl ctx = new ServerMessageContextImpl(
			serverDesc, operation, protocolProcessor, transport,
			requestMsg, responseMsg, serviceAddress, null, clientAddress, null, null,
			effectiveCharset, serviceAddressUrl == null ? "none" : serviceAddressUrl.toString(),
			"(none)", 0, null);

		return ctx;
	}

	/**
	 * @deprecated Does not support embedded jetty, Use {@link ServerMessageContextTestBuilder}
	 */
	@Deprecated
	public static ServerMessageContext createServerMessageContextForSojServiceTest(String bindingName, String serviceName, String opName, String messageProtocol,
			byte[] payload, URL serviceAddressUrl, String contentType) throws Exception
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
			ServiceAddress serviceAddress = new ServiceAddress(serviceAddressUrl);
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
				try {
					attachments = new InboundMessageAttachments(inputStream, contentType);
				} catch(Exception e) {
					e.printStackTrace();
					throw e;
				}
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

			ServerMessageContextImpl ctx = new ServerMessageContextImpl(
				serverDesc, operation, protocolProcessor, transport,
				requestMsg, responseMsg, serviceAddress, null, clientAddress, null, null,
				effectiveCharset, serviceAddressUrl == null ? "none" : serviceAddressUrl.toString(),
				"(none)", 0, null);

			return ctx;
		}

	public static ServerMessageContext createServerMessageContext(
			String bindingName, String serviceName, String messageProtocol,
			URL serviceAddressUrl) throws Exception {
		ServerMessageContext ctx = createServerMessageContext(bindingName,
				serviceName, messageProtocol, "Dummy", serviceAddressUrl, null);

		OutboundMessage outMsg = (OutboundMessage)ctx.getResponseMessage();
		((ServerMessageContextImpl)ctx).changeProcessingStage(MessageProcessingStage.RESPONSE_DISPATCH);
		outMsg.setParam(0, createTestMessage());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		outMsg.serialize(out);
		String payload = out.toString("UTF-8");
		System.out.println("Payload: " + payload);
		return createServerMessageContext(bindingName, serviceName, messageProtocol,
			payload, serviceAddressUrl, null);
	}


	public static ServerMessageContext createServerMessageContext(
			String bindingName, String serviceName, String operationName, String messageProtocol,
			URL serviceAddressUrl) throws Exception {
		ServerMessageContext ctx = createServerMessageContext(bindingName,
				serviceName, operationName, messageProtocol, "Dummy", serviceAddressUrl, null);

		OutboundMessage outMsg = (OutboundMessage)ctx.getResponseMessage();
		((ServerMessageContextImpl)ctx).changeProcessingStage(MessageProcessingStage.RESPONSE_DISPATCH);
		String payload = null;

		if (operationName != null &&
				(	operationName.equalsIgnoreCase("customError2") ||
					operationName.equalsIgnoreCase("myTestOperation") ||
					operationName.equalsIgnoreCase("serviceChainingOperation"))) {
			outMsg.setParam(0, createTestMessage());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			outMsg.serialize(out);
			payload = out.toString("UTF-8");
		}
		System.out.println("Payload: " + payload);
		return createServerMessageContext(bindingName, serviceName, operationName, messageProtocol,
			payload, serviceAddressUrl, null);
	}
	
	public static ServerMessageContext createServerMessageContextWithPayload(
			String bindingName, String serviceName, String operationName, String messageProtocol,
			URL serviceAddressUrl, String payload) throws Exception {
		ServerMessageContext ctx = createServerMessageContext(bindingName,
				serviceName, operationName, messageProtocol, "Dummy", serviceAddressUrl, null);

		((ServerMessageContextImpl)ctx).changeProcessingStage(MessageProcessingStage.RESPONSE_DISPATCH);

		System.out.println("Payload: " + payload);
		return createServerMessageContext(bindingName, serviceName, operationName, messageProtocol,
			payload, serviceAddressUrl, null);
	}

	// create a client message context for Test1 service
	public static ClientMessageContext createClientMessageContextForHttpGet(Object outParam, URL serviceURL, int maxUrlLen, String clientName)
				throws Exception {
		// initialize client-side metric subsystem
		ServiceFactory.create(
				TestUtils.TEST1_SERVICE_NAME, clientName, serviceURL, null);

		return TestUtils.createClientMessageContext(
					"NV",
					TestUtils.TEST1_SERVICE_NAME,
					null, // message protocol
					outParam,
					null, // input payload message string
					serviceURL,
					null,
					true,
					maxUrlLen, clientName); // headers
	}
	public static ClientMessageContext createClientMessageContextForHttpGet(Object outParam, URL serviceURL, int maxUrlLen, String clientName, Transport transport)
	throws Exception {
		return createClientMessageContextForHttpGet(outParam, serviceURL, maxUrlLen, clientName, transport, "myTestOperation");
	}
	
	// create a client message context for Test1 service with provided transport
	public static ClientMessageContext createClientMessageContextForHttpGet(Object outParam, URL serviceURL, int maxUrlLen, String clientName, Transport transport, String opName)
				throws Exception {
		// initialize client-side metric subsystem
		ServiceFactory.create(
				TestUtils.TEST1_SERVICE_NAME, clientName, serviceURL, null);

		return TestUtils.createClientMessageContext(
					"NV",
					TestUtils.TEST1_SERVICE_NAME,
					null, // message protocol
					outParam,
					null, // input payload message string
					serviceURL,
					null,
					true,
					maxUrlLen, clientName, transport,opName); // headers
	}

	
	public static ClientMessageContext createClientMessageContext(String bindingName, String serviceName, String messageProtocol,
			Object outParam, String inPayload, URL serviceAddressUrl, Map<String,String> extraHeaders,
			boolean isREST, int maxUrlLen, String clientName) throws Exception {
		return createClientMessageContext(bindingName,serviceName,messageProtocol,
				outParam,inPayload,serviceAddressUrl,extraHeaders,
				isREST,maxUrlLen,clientName,new TestTransport(),"myTestOperation");
	}
	
	public static ClientMessageContext createClientMessageContext(String bindingName, String serviceName, String messageProtocol,
			Object outParam, String inPayload, URL serviceAddressUrl, Map<String,String> extraHeaders,
			boolean isREST, int maxUrlLen, String clientName, String consumerId) throws Exception {
		return createClientMessageContext(bindingName,serviceName,messageProtocol,
				outParam,inPayload,serviceAddressUrl,extraHeaders,
				isREST,maxUrlLen,clientName,new TestTransport(),"myTestOperation", consumerId);
	}

	public static ClientMessageContext createClientMessageContext(String bindingName, String serviceName, String messageProtocol,
			Object outParam, String inPayload, URL serviceAddressUrl, Map<String,String> extraHeaders,
			boolean isREST, int maxUrlLen, String clientName, Transport transport, String opName) throws Exception {
		return createClientMessageContext(bindingName, serviceName, messageProtocol,
			 outParam, inPayload, serviceAddressUrl, extraHeaders,
		     isREST, maxUrlLen, clientName, transport, opName, null);
	}

	public static ClientMessageContext createClientMessageContext(String bindingName, String serviceName, String messageProtocol,
																	Object outParam, String inPayload, URL serviceAddressUrl, Map<String,String> extraHeaders,
																	boolean isREST, int maxUrlLen, String clientName, Transport transport, String opName, String consumer_id) throws Exception {
		ClientServiceDesc serviceDesc = ClientServiceDescFactory.getInstance().getServiceDesc(serviceName, clientName);

		ProtocolProcessorDesc protocolProcessor;
		if (messageProtocol != null) {
			protocolProcessor = serviceDesc.getProtocolProcessor(messageProtocol);
		} else {
			protocolProcessor = serviceDesc.getNullProtocolProcessor();
		}

		ServiceOperationDesc operation = serviceDesc.getOperation(opName);
		DataBindingDesc dbDesc = createTestDataBinding(bindingName, serviceDesc);
		G11nOptions g11nOptions = new G11nOptions();

		ServiceAddress serviceAddress = new ServiceAddress(serviceAddressUrl);
		Map<String,String> headers = new HashMap<String, String>();
		String payloadType = dbDesc.getPayloadType();
		headers.put(SOAHeaders.REQUEST_DATA_FORMAT, payloadType);
		headers.put(SOAHeaders.RESPONSE_DATA_FORMAT, payloadType);
		if (extraHeaders != null) {
			headers.putAll(extraHeaders);
		}

		BaseMessageImpl requestMsg = new OutboundMessageImpl(true,
				SOA_MESSAGE_PROTOCOL_VALUE, dbDesc, g11nOptions, headers, null, null, null, operation, isREST, maxUrlLen);

		InboundMessageImpl responseMsg = new InboundMessageImpl(false,
			SOA_MESSAGE_PROTOCOL_VALUE, dbDesc, g11nOptions, null, null, null, null, operation);

		ServiceInvokerOptions invokerOptions = new ServiceInvokerOptions();

		String serviceVersion = null;
		String responseTransport = null;
		String useCase = null;
		String consumerId = consumer_id;

		ClientMessageContextImpl ctx = new ClientMessageContextImpl(
				serviceDesc, operation, protocolProcessor, transport,
				requestMsg, responseMsg, serviceAddress, null, serviceVersion, invokerOptions,
				responseTransport, useCase, consumerId, g11nOptions.getCharset(),
				(serviceAddressUrl == null ?  null : serviceAddressUrl.toString()));

		if (outParam != null) {
			// this has to happen after client message context is created and assoicated w/ requestMsg
			requestMsg.setParam(0, outParam);
		}
		if (inPayload != null) {
			InputStream inputStream = new ByteArrayInputStream(inPayload.getBytes());
			responseMsg.setInputStream(inputStream);
		}

		return ctx;
	}

	public static Map<String,String> createHeadersForSoapTest() {
		Map<String,String> headers = new HashMap<String, String>();
		headers.put(SOAHeaders.ELEMENT_ORDERING_PRESERVE, ELEMENT_ORDERING_PRESERVE_VALUE);
		headers.put("Connection", "Keep-Alive");
		headers.put("Host", "localhost");
		headers.put("Content-Type", "text/xml");
		headers.put("SOAPAction", "MyTestOperation");
		return headers;
	}

	private static DataBindingDesc createTestDataBinding(String bindingName, ServiceDesc serviceDesc) {
//		SerializerFactory serFactory = new JAXBXMLSerializerFactory();
//		DeserializerFactory deserFactory = new JAXBXMLDeserializerFactory();
//		String contextPath = "org.ebayopensource.turmeric.runtime.tests.common.sample.service.message";
//		return new DataBindingDesc("XML", contextPath, serFactory, deserFactory, null, null);
		return serviceDesc.getDataBindingDesc(bindingName);
	}

	public static boolean equals(Object obj1, Object obj2) {
		if (null == obj1) {
			return null == obj2;
		}
		return obj1.equals(obj2);
	}

	public static void doMarshalUnMarshal(JAXBElement ele, boolean assertTrue) throws Exception {
		Object obj = ele.getValue();
		JAXBContext ctx = JAXBContext.newInstance(obj.getClass());
		Marshaller m = ctx.createMarshaller();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		m.marshal(ele, os);
		String xml = os.toString("UTF-8");
		System.out.println(xml);
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		Unmarshaller u = ctx.createUnmarshaller();
		javax.xml.stream.XMLStreamReader xmlStreamReader =
		    javax.xml.stream.XMLInputFactory.newInstance().createXMLStreamReader(is);
		Object newObj = u.unmarshal(xmlStreamReader, obj.getClass());
		JAXBElement newEle = null;
		if (newObj instanceof JAXBElement) {
			newEle = (JAXBElement)newObj;
			newObj = newEle.getValue();
		}
		if (assertTrue) {
			Assert.assertEquals(obj, newObj);
			return;
		}
		Assert.assertNotSame(obj, newObj);
	}

	public static MyMessage createTestMessage() {
		return createTestMessage(1);
	}

	public static String getMimeType(String payloadType) {
		return s_mimeTypes.get(payloadType);
	}

	public static byte[] SOA_IN_CHINESE = new byte[] { (byte) 0xE9,
		(byte) 0x9D, (byte) 0xA2, (byte) 0xE5, (byte) 0x90, (byte) 0x91,
		(byte) 0xE6, (byte) 0x9C, (byte) 0x8D, (byte) 0xE5, (byte) 0x8A,
		(byte) 0xA1, (byte) 0xE7, (byte) 0x9A, (byte) 0x84, (byte) 0xE6,
		(byte) 0x9E, (byte) 0x84, (byte) 0xE6, (byte) 0x9E, (byte) 0xB6 };
	public static final String SOA_IN_CHINESE_STRING = new String(SOA_IN_CHINESE, Charset.forName("UTF-8"));
	public static final String SOA_IN_CHINESE_STRING_URL_ENCODED = 
			"%E9%9D%A2%E5%90%91%E6%9C%8D%E5%8A%A1%E7%9A%84%E6%9E%84%E6%9E%B6";
	public static final String MESSAGE_BODY_TEXT = "SOA in Chinese is '"
		+ SOA_IN_CHINESE_STRING + "'";
	public static final String MESSAGE_SUBJECT_TEXT = "SOA Framework test message";
	public static final String CITY_NAME = "San Jose";
	public static final String EMAIL_ADDRESS0 = "soa0@ebay.com";
	public static MyMessage createTestMessage(int numRecipients) {
		MyMessage msg = new MyMessage();
		msg.setBody(MESSAGE_BODY_TEXT);
		msg.setSubject(MESSAGE_SUBJECT_TEXT);
		msg.setSomething("This is from the any object type");
		Address addr;
		for (int i=0; i<numRecipients; i++) {
			addr = new Address();
			addr.setStreetNumber(2000 + i);
			addr.setState("Hamilton Ave");
			addr.setCity(CITY_NAME);
			addr.setState("CA");
			addr.setPostCode(95125 + i);
			addr.setEmailAddress("soa" + i + "@ebay.com");
			msg.addRecipient(addr);
		}
		return msg;
	}

	public static MyMessage createCustomMessage(int numRecipients, String body) {
		MyMessage msg = new MyMessage();
		msg.setBody(body);
		msg.setSubject(MESSAGE_SUBJECT_TEXT);
		msg.setSomething("This is from the any object type");
		Address addr;
		for (int i=0; i<numRecipients; i++) {
			addr = new Address();
			addr.setStreetNumber(2000 + i);
			addr.setState("Hamilton Ave");
			addr.setCity(CITY_NAME);
			addr.setState("CA");
			addr.setPostCode(95125 + i);
			addr.setEmailAddress("soa" + i + "@ebay.com");
			msg.addRecipient(addr);
		}
		return msg;
	}
	
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Charset UTF16 = Charset.forName("UTF-16");
	public static MyMessage encodeMessage(MyMessage msg, Charset charset) throws Exception {
		if (null == msg) {
			return null;
		}
		MyMessage encodedMsg = new MyMessage();
		String body = msg.getBody();
		if (!charset.contains(UTF8) && !charset.contains(UTF16)) {
			body = usAsciiEncodeString(body);
		}
		encodedMsg.setBody(body);
		encodedMsg.setCreateTime(msg.getCreateTime());
		encodedMsg.setRecipients(msg.getRecipients());
		encodedMsg.setSubject(msg.getSubject());
		encodedMsg.setSomething(msg.getSomething());
		return encodedMsg;
	}

	private static String usAsciiEncodeString(String string) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os, "US-ASCII");
		writer.write(string);
		writer.close();
		return os.toString();
	}

	public static List<ErrorType> errorMessageToErrorTypeList(ErrorMessage msg) {
		List<CommonErrorData> errorDataList = msg.getError();
		List<ErrorType> result = new ArrayList<ErrorType>();
		for (CommonErrorData errorData : errorDataList) {
			ErrorType errorType = errorDataToErrorType(errorData);
			result.add(errorType);
		}
		return result;
	}

	public static ErrorMessage errorTypeListToErrorMessage(List<ErrorType> errorTypeList) {
		List<CommonErrorData> errorDataList = new ArrayList<CommonErrorData>();
		for (ErrorType errorType : errorTypeList) {
			CommonErrorData errorData = errorTypeToErrorData(errorType);
			errorDataList.add(errorData);
		}
		ErrorMessage result = ErrorLibraryBaseErrors.getNewErrorMessage(errorDataList);
		return result;
	}

	public static CommonErrorData errorTypeToErrorData(ErrorType errorType) {
		CommonErrorData errorData = new CommonErrorData();
		ErrorCategory category;
		ErrorClassificationCodeType classification = errorType.getErrorClassification();
		if (classification != null) {
			if (classification.equals(ErrorClassificationCodeType.REQUEST_ERROR)) {
				category = ErrorCategory.REQUEST;
			} else if (classification.equals(ErrorClassificationCodeType.SYSTEM_ERROR)) {
				category =  ErrorCategory.SYSTEM;
			} else { // using CustomCode - hack
				category = ErrorCategory.APPLICATION;
			}
			errorData.setCategory(category);
		}
		if (errorType.getErrorCode() != null) {
			long errorCode = Long.valueOf(errorType.getErrorCode()).longValue();
			errorData.setErrorId(errorCode);
		}
		List<ErrorParameterType> params = errorType.getErrorParameters();
		if (params != null && !params.isEmpty()) {
			ErrorParameterType param = params.get(params.size()-1);
			if (param.getParamID().equals("Exception")) {
				errorData.setExceptionId(param.getValue());
			}
		}
		errorData.setMessage(errorType.getLongMessage());
		SeverityCodeType severityCode = errorType.getSeverityCode();
		if (severityCode != null) {
			ErrorSeverity severity = (severityCode.equals(SeverityCodeType.ERROR)? ErrorSeverity.ERROR: ErrorSeverity.WARNING);
			errorData.setSeverity(severity);
		}
		return errorData;
	}

	public static ErrorType errorDataToErrorType(CommonErrorData errorData) {
		ErrorType errorType = new ErrorType();
		ErrorClassificationCodeType classification;
		ErrorCategory category = errorData.getCategory();
		if (category.equals(ErrorCategory.REQUEST)) {
			classification = ErrorClassificationCodeType.REQUEST_ERROR;
		} else if (category.equals(ErrorCategory.SYSTEM)) {
			classification = ErrorClassificationCodeType.SYSTEM_ERROR;
		} else { // application
			classification = ErrorClassificationCodeType.CUSTOM_CODE;
		}
		errorType.setErrorClassification(classification);
		errorType.setErrorCode(String.valueOf(errorData.getErrorId()));
		errorType.setShortMessage(errorData.getMessage());
		errorType.setLongMessage(errorData.getMessage());
		SeverityCodeType severity = (errorData.getSeverity().equals(ErrorSeverity.ERROR)? SeverityCodeType.ERROR: SeverityCodeType.WARNING);
		errorType.setSeverityCode(severity);
		String exception = errorData.getExceptionId();
		if (exception != null) {
			ErrorParameterType param = new ErrorParameterType();
			param.setParamID("Exception");
			param.setValue(exception);
			errorType.getErrorParameters().add(param);
		}
		// not supported: "real" parameters
		return errorType;
	}
}

