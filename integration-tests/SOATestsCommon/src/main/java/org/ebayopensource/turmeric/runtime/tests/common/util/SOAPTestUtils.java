/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.context.ServiceGroupContext;
import org.apache.axis2.description.AxisServiceGroup;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.InboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.OutboundMessageImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.Axis2Utils;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.BaseSOAPProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.SOAPUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageContextImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDescFactory;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServerMessageContext;
import org.ebayopensource.turmeric.runtime.tests.common.sample.transports.TestTransport;


/**
 * @author gyue
 */
public class SOAPTestUtils {

	public static final String SOA_MESSAGE_PROTOCOL_VALUE = "TEST_CTX_CREATE";
	public static final String SOAP_BINDING_NAME = "XML";
	public static final String TEST1_URL_STRING = "http://localhost:8080/ws/spf/Test1Service";

	public final static String DEFAULT_EMPTY_ENVELOPE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
											"<soapenv:Body>" +
											"</soapenv:Body>" +
											"</soapenv:Envelope>";

	public final static String GOOD_ENVELOPE_WITH_NEWLINE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
											"<soapenv:Body>" +
											"	\n </soapenv:Body>" +
											"	\n	\n \n	\n</soapenv:Envelope>	\n";

	public final static String BAD_ENVELOPE_MISSING_ENDBODY = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
											"<soapenv:Body>" +
											"	\\n	</soapenv:Envelope>";

	public final static String BAD_ENVELOPE_MISSING_ENDENVELOPE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
											"<soapenv:Body>" +
											"	\\n </soapenv:Body>";

	public final static String GOOD_XML_REQUEST =
											"<ns1:MyMessage xmlns:ns1=\"http://www.ebay.com/test/soaframework/sample/types1\">" +
												"<ns1:body>SOA SOA, SOS.</ns1:body>" +
												"<ns1:recipients><entry><key>soa@ebay.com</key><value><city>San Jose</city><emailAddress>soa@ebay.com</emailAddress><postCode>95125</postCode><state>CA</state><streetNumber>2145</streetNumber></value></entry></ns1:recipients>" +
												"<ns1:subject>Test SOA JAXB XML ser/deser</ns1:subject>" +
											"</ns1:MyMessage>";

	public final static String START_XML_BODY_ELEMENT = "MyMessage";
	public final static String START_XML_BODY_ERROR_ELEMENT = SOAConstants.ERROR_MESSAGE_ELEMENT_NAME.getLocalPart();

	public final static String BAD_XML_REQUEST =
											"<ns1:BADMyMessage xmlns:ns1=\"http://www.ebay.com/test/soaframework/sample/service/message\">" +
												"<BADbody>SOA SOA, SOS.</body>" +
												"<recipients><entry><key>soa@ebay.com</key><value><city>San Jose</city><emailAddress>soa@ebay.com</emailAddress><postCode>95125</postCode><state>CA</state><streetNumber>2145</streetNumber></value></entry></recipients>" +
												"<subject>Test SOA JAXB XML ser/deser</subject>" +
											"</ns1:BADMyMessage>";

	// Default SOAP Envelope (SOAP1.1)
	public final static String GOOD_SOAP_REQUEST = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
											"<soapenv:Body>" +
												GOOD_XML_REQUEST +
											"</soapenv:Body>" +
											"</soapenv:Envelope>";

	// SOAP SOAP1.1 Envelope w/ space
	public final static String GOOD_SOAP_REQUEST_WITH_SPACE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
											"	<soapenv:Body>			" +
												"\n" +
												GOOD_XML_REQUEST +
												"\n" +
											"			</soapenv:Body>			" +
											"</soapenv:Envelope>";

	// SOAP SOAP1.1 Envelope w/ comments
	public final static String GOOD_SOAP_REQUEST_WITH_COMMENTS = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
											"	<soapenv:Body>			" +
												"\n <!-- comments --> \n" +
												GOOD_XML_REQUEST +
												"\n" +
											"<!-- comments -->" +
											"			</soapenv:Body>			" +
											"</soapenv:Envelope>";

	// SOAP1.2 Envelope
	public final static String GOOD_SOAP_12_REQUEST = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
											"<soapenv:Body>" +
												GOOD_XML_REQUEST +
											"</soapenv:Body>" +
											"</soapenv:Envelope>";

	// SOAP1.2 Envelope w/ space
	public final static String GOOD_SOAP_12_REQUEST_WITH_SPACE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
											"	<soapenv:Body>				" +
												"\n" +
												GOOD_XML_REQUEST +
												"\n" +
											"			</soapenv:Body>			" +
											"</soapenv:Envelope>";

	// SOAP1.2 Envelope w/ comments
	public final static String GOOD_SOAP_12_REQUEST_WITH_COMMENTS = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
											"	<soapenv:Body>				" +
												"\n <!-- comments --> \n" +
												GOOD_XML_REQUEST +
												"\n" +
												"<!-- comments -->" +
											"			</soapenv:Body>			" +
											"</soapenv:Envelope>";

	// SOAP1.2 Envelope with Header
	public final static String GOOD_SOAP_12_REQUEST_WITH_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
											"<soapenv:Header>" +
												"<ns1:Token xmlns:ns1=\"http://www.ebay.com/test/soaframework/sample/service/token\">" +
												"</ns1:Token>" +
											"</soapenv:Header>" +
											"<soapenv:Body>" +
												GOOD_XML_REQUEST +
											"</soapenv:Body>" +
											"</soapenv:Envelope>";


	// SOAP envelope with bad body element
	public final static String BAD_SOAP_REQUEST_BADXMLBODY = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
											"<soapenv:Body>" +
												BAD_XML_REQUEST +
											"</soapenv:Body>" +
											"</soapenv:Envelope>";
	public final static String BAD_SOAP_12_REQUEST_BADXMLBODY = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
											"<soapenv:Body>" +
												BAD_XML_REQUEST +
											"</soapenv:Body>" +
											"</soapenv:Envelope>";

	// invalid start body tag
	public final static String BAD_SOAP_REQUEST_INVALIDSTARTBODYTAG = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
											"<soapenv:BodyBAD>" +
												GOOD_XML_REQUEST +
											"</soapenv:Body>" +
											"</soapenv:Envelope>";
	public final static String BAD_SOAP_12_REQUEST_INVALIDSTARTBODYTAG = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
											"<soapenv:BodyBAD>" +
												GOOD_XML_REQUEST +
											"</soapenv:Body>" +
											"</soapenv:Envelope>";

	// invalid start envelope tag
	public final static String BAD_SOAP_REQUEST_INVALIDSTARTENVELOPETAG = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:EnvelopeBAD xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
											"<soapenv:Body>" +
												GOOD_XML_REQUEST +
											"</soapenv:Body>" +
											"</soapenv:Envelope>";
	public final static String BAD_SOAP_12_REQUEST_INVALIDSTARTENVELOPETAG = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
											"<soapenv:Body>" +
												GOOD_XML_REQUEST +
											"</soapenv:Body>" +
											"</soapenv:Envelope>";


	// invalid end body tag
	public final static String BAD_SOAP_REQUEST_INVALIDENDBODYTAG = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
											"<soapenv:Body>" +
												GOOD_XML_REQUEST +
											"</soapenv:BADBody>" +
											"</soapenv:Envelope>";
	public final static String BAD_SOAP_12_REQUEST_INVALIDENDBODYTAG = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
											"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
											"<soapenv:Body>" +
												GOOD_XML_REQUEST +
											"</soapenv:BADBody>" +
											"</soapenv:Envelope>";

	public final static String SOAP11_ERROR_STRUCTURE_STR = SOAP11Constants.SOAP_FAULT_DETAIL_LOCAL_NAME + "></soapenv:Fault>";
	public final static String SOAP12_ERROR_STRUCTURE_STR = SOAP12Constants.SOAP_FAULT_DETAIL_LOCAL_NAME + "></soapenv:Fault>";

	public final static String GOOD_XML_ERRORMESSAGE = 	"<ns3:errorMessage xmlns:ns3=\"http://www.ebayopensource.org/turmeric/common/v1/types\" xmlns:ns1=\"http://www.ebay.com/test/soaframework/sample/service/message\">" +
															"<ns3:error>" +
																"<ns3:error-id>2005</ns3:error-id>" +
																"<ns3:domain>SOA</ns3:domain>" +
																"<ns3:severity>Error</ns3:severity>" +
																"<ns3:category>Application</ns3:category>" +
																"<ns3:message>Internal application error: {0}</ns3:message>" +
															"</ns3:error>" +
														"</ns3:errorMessage>";

	public final static String GOOD_SOAP_FAULT = "<?xml version='1.0' encoding='utf-8'?>" +
												"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
												"<soapenv:Header />" +
												"<soapenv:Body>" +
												"<soapenv:Fault>" +
													"<faultcode>TEST FAULTCODE</faultcode>" +
													"<faultstring>TEST FAULTSTRING</faultstring>" +
													"<detail>" +
														GOOD_XML_ERRORMESSAGE +
													"</detail>" +
												"</soapenv:Fault>" +
												"</soapenv:Body>" +
												"</soapenv:Envelope>";

	public final static String GOOD_SOAP_FAULT_WITH_ACTOR = "<?xml version='1.0' encoding='utf-8'?>" +
												"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
												"<soapenv:Header />" +
												"<soapenv:Body>" +
												"<soapenv:Fault>" +
													"<faultcode>TEST FAULTCODE</faultcode>" +
													"<faultstring>TEST FAULTSTRING</faultstring>" +
													"<faultactor>TEST FAULTACTOR</faultactor>" +
													"<detail>" +
														GOOD_XML_ERRORMESSAGE +
													"</detail>" +
												"</soapenv:Fault>" +
												"</soapenv:Body>" +
												"</soapenv:Envelope>";

	// Attachment
	public final static String ATTACHMENT_CONTENT_TYPE_STRING =
												"multipart/related;boundary=MIMEBoundaryurn_uuid_9E55D9AADCAC7C46E811592318362121; " +
												"type=\"application/xop+xml\";start=\"<0.urn:uuid:9E55D9AADCAC7C46E811592318362122.org>" +
												"\";start-info=\"text/xml\"; charset=UTF-8";

	public final static String ATTACHMENT_ROOT_MIME_BLOCK =
												"--MIMEBoundaryurn_uuid_9E55D9AADCAC7C46E811592318362121\n\n" +
												"content-type: application/xop+xml; charset=UTF-8; type=\"text/xml\";" + "\n" +
												"content-id: <0.urn:uuid:9E55D9AADCAC7C46E811592318362122.org>" + "\n" +
												"content-transfer-encoding: binary" +
												"\n\n";

	public final static String MESSAGE_BODY_TEXT = "SOA SOA, SOS.";
	public final static String ATTACHMENT_CONTENT_TYPE = "application/octet-stream";
	public final static String ATTACHMENT_BODY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n";

	public final static String ATTACHMENT_DATA_MIME_BLOCK =
												"--MIMEBoundaryurn_uuid_9E55D9AADCAC7C46E811592318362121" +
												"content-type: " + ATTACHMENT_CONTENT_TYPE +"\n" +
												"content-id: <1.urn:uuid:9E55D9AADCAC7C46E811592318363373.org>" + "\n" +
												"content-transfer-encoding: binary" + "\n" +
												"\n" + ATTACHMENT_BODY;

	public final static String ATTACHMENT_END_BOUNDARY_BLOCK =
												"--MIMEBoundaryurn_uuid_9E55D9AADCAC7C46E811592318362121--";
	// XML payload w/ binary data field
	public final static String GOOD_XML_REQUEST_WITH_BINDARY_FIELD =
												"<ns1:MyMessage xmlns:ns1=\"http://www.ebay.com/test/soaframework/sample/service/message\">" +
													"<body>" + MESSAGE_BODY_TEXT + "</body>" +
													"<recipients><entry><key>soa@ebay.com</key><value><city>San Jose</city><emailAddress>soa@ebay.com</emailAddress><postCode>95125</postCode><state>CA</state><streetNumber>2145</streetNumber></value></entry></recipients>" +
													"<subject>Test SOA JAXB XML ser/deser</subject>" +
													"<binaryData>" +
												    	"<xop:Include href=\"cid:1.urn:uuid:9E55D9AADCAC7C46E811592318363373.org\" xmlns:xop=\"http://www.w3.org/2004/08/xop/include\" />" +
												   "</binaryData>" +
												"</ns1:MyMessage>";

	public final static String GOOD_SOAP_REQUEST_WITH_BINDARY_FIELD =
													"<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
														"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
														"<soapenv:Body>" +
															GOOD_XML_REQUEST_WITH_BINDARY_FIELD +
														"</soapenv:Body>" +
													"</soapenv:Envelope>";
	// XML attachment request
	public final static String GOOD_XML_ATTACHMENT_REQUEST =
												ATTACHMENT_ROOT_MIME_BLOCK +
												GOOD_XML_REQUEST_WITH_BINDARY_FIELD + "\n" +
												ATTACHMENT_DATA_MIME_BLOCK +
												ATTACHMENT_END_BOUNDARY_BLOCK;
	// SOAP attachment request
	public final static String GOOD_SOAP_ATTACHMENT_REQUEST =
												ATTACHMENT_ROOT_MIME_BLOCK +
												GOOD_SOAP_REQUEST_WITH_BINDARY_FIELD + "\n" +
												ATTACHMENT_DATA_MIME_BLOCK +
												ATTACHMENT_END_BOUNDARY_BLOCK;


	public static InboundMessage createDummyInboundMessage(BaseSOAPProtocolProcessor protocolProcessor)
				throws ServiceException, MalformedURLException {
		ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc(TestUtils.TEST1_SERVICE_NAME);
		DataBindingDesc dbDesc = serviceDesc.getDataBindingDesc(SOAP_BINDING_NAME);
		G11nOptions g11nOptions = new G11nOptions();
		Transport transport = new TestTransport();
		ServiceOperationDesc operation = serviceDesc.getOperation("myTestOperation");
		ServiceAddress clientAddress = new ServiceAddress(null);
		ServiceAddress serviceAddress = new ServiceAddress(new URL("http://localhost:8080/ws/svc/MyService"));

		BaseMessageImpl requestMsg = new InboundMessageImpl(true,
			SOA_MESSAGE_PROTOCOL_VALUE, dbDesc, g11nOptions, null, null, null, null, operation);

		BaseMessageImpl responseMsg = new OutboundMessageImpl(false,
			SOA_MESSAGE_PROTOCOL_VALUE, dbDesc, g11nOptions, null, null, null, null, operation, false, 0);

		String serviceVersion = null;

		Charset effectiveCharset = serviceDesc.getServiceCharset();
		if (effectiveCharset == null) {
			effectiveCharset = g11nOptions.getCharset();
		}

		ProtocolProcessorDesc processorDesc = new ProtocolProcessorDesc("SOAP", protocolProcessor, null);

		new ServerMessageContextImpl(
			serviceDesc, operation, processorDesc, transport,
			requestMsg, responseMsg, serviceAddress, null, clientAddress, null, serviceVersion,
			effectiveCharset, "(none)", "(none)", 0, null);

		return (InboundMessage) requestMsg;
	}


	public static void advanceXMLReaderToStartBody(XMLStreamReader reader) throws Exception {
		while (reader.hasNext()) {
			if (reader.isStartElement() && reader.getLocalName().equalsIgnoreCase("Body"))
				break;
			reader.next();
		}
		if (!(reader.getLocalName().equalsIgnoreCase("Body"))) {
			throw new Exception("Body tag not found!");
		}
	}


	public static void advanceXMLReaderToStartElement(XMLStreamReader reader) throws Exception {
		while (reader.hasNext()) {
			if (reader.isStartElement())
				break;
			reader.next();
		}
		if (!(reader.isStartElement())) {
			throw new Exception("next start element not found!");
		}
	}

	public static org.apache.axis2.context.MessageContext createTestAxis2InboundMessageContext(
				BaseSOAPProtocolProcessor protocolProcessor, MessageContext ctx, String soapProtocol)
					throws ServiceException, MalformedURLException, AxisFault {
		org.apache.axis2.context.MessageContext axisContext = Axis2Utils.createInboundAxis2Context(ctx, protocolProcessor.getConfigurationContext());

		// populate contexts: sgc, sc, oc
		ServiceGroupContext sgc = axisContext.getConfigurationContext().createServiceGroupContext(
                (AxisServiceGroup)axisContext.getAxisServiceGroup());
        ServiceContext sc = sgc.getServiceContext(axisContext.getAxisService());
        OperationContext operationContext = sc.createOperationContext(axisContext.getAxisOperation());
		axisContext.setOperationContext(operationContext);

		// need this now???
		axisContext.getConfigurationContext().fillServiceContextAndServiceGroupContext(axisContext);
		axisContext.setEnvelope(SOAPUtils.createSOAPEnvelopeByProtocolName(soapProtocol, null));
		return axisContext;
	}

	public static org.apache.axis2.context.MessageContext createTestAxis2OutboundMessageContext(
			BaseSOAPProtocolProcessor protocolProcessor, MessageContext ctx)
				throws ServiceException, MalformedURLException, AxisFault {
		org.apache.axis2.context.MessageContext axisContext = Axis2Utils.createOutboundAxis2Context(ctx, protocolProcessor.getConfigurationContext());

		// populate contexts: sgc, sc, oc
/*		ServiceGroupContext sgc = axisContext.getConfigurationContext().createServiceGroupContext(
	            (AxisServiceGroup)axisContext.getAxisServiceGroup());
	    ServiceContext sc = sgc.getServiceContext(axisContext.getAxisService());
	    OperationContext operationContext = sc.createOperationContext(axisContext.getAxisOperation());
		axisContext.setOperationContext(operationContext);
*/
		// need this now???
		axisContext.getConfigurationContext().fillServiceContextAndServiceGroupContext(axisContext);
		axisContext.setEnvelope(SOAPUtils.createSOAPEnvelopeByProtocolName(protocolProcessor.getMessageProtocol(), null));
		return axisContext;
	}

	// validate axis2 context
	public static boolean validateAxis2Context(org.apache.axis2.context.MessageContext context) {
		if (context.getAxisService() == null) {
			System.out.println("NULL Axis Service");
			return false;
		} if (context.getAxisOperation() == null) {
			System.out.println("NULL Axis Operation");
			return false;
		} if (context.getAxisServiceGroup() == null) {
			System.out.println("NULL Axis Service Group");
			return false;
		} if (context.getConfigurationContext()== null) {
			System.out.println("NULL Axis Configuration Context");
			return false;
		} if (context.getConfigurationContext().getAxisConfiguration() == null) {
			System.out.println("NULL Axis Configuration");
			return false;
		} if (context.getEnvelope() == null) {
			System.out.println("NULL Envelope");
			return false;
		} if (context.getEnvelope().getBody() == null) {
			System.out.println("NULL Body");
			return false;
		}

		return true;
	}

	public static boolean validateXMLReaderAtStartElement(XMLStreamReader reader, String startElement) {
		if (reader == null) {
			System.out.println("ERROR>> XMLReader not found");
			return false;
		}

		reader.isStartElement();
		reader.getLocalName();
		reader.getEventType();
		if (reader.isStartElement() && reader.getLocalName().equalsIgnoreCase(startElement)) {
			// ok
			return true;
		} else {
			System.out.println(reader.getLocalName() + "ERROR>> XMLReader not pointing to start of body element");
			return false;
		}
	}

	// check if the message contains a SOAP11 fault
	public static boolean containSOAP11Fault(String message) {
		if (message == null || message.equals("")) {
			System.out.println("ERROR>> NULL/empty message");
		}
		return (message.indexOf(SOAPTestUtils.SOAP11_ERROR_STRUCTURE_STR) != -1);
	}

	// check if the message contains a SOAP12 fault
	public static boolean containSOAP12Fault(String message) {
		if (message == null || message.equals("")) {
			System.out.println("ERROR>> NULL/empty message");
		}
		return (message.indexOf(SOAPTestUtils.SOAP12_ERROR_STRUCTURE_STR) != -1);
	}

	/*
	// check if this is a SOAP11 envelope
	public static boolean isSOAP11Envelope(SOAPEnvelope env) {
		if (env.getNamespace().getNamespaceURI().
				equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
			return true;
		} else {
			return false;
		}
	}

	// check if this is a SOAP12 envelope
	public static boolean isSOAP12Envelope(SOAPEnvelope env) {
		if (env.getNamespace().getNamespaceURI().
				equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
			return true;
		} else {
			return false;
		}
	}
*/



	// create a SOAP server message context for Test1 service (default protocol: SOAP11)
	public static ServerMessageContext createServerMessageContextForTest1Service(String payload)
				throws Exception {
		return createServerMessageContextForTest1Service(payload, SOAConstants.MSG_PROTOCOL_SOAP_11);
	}


	// create a SOAP server message context for Test1 service, with specified message protocol  (SOAP11/12)
	public static ServerMessageContext createServerMessageContextForTest1Service(String payload, String messageProtocol)
				throws Exception {
		return createServerMessageContextForTest1Service(payload, null, messageProtocol);
	}

	// create a SOAP server message context for Test1 service, with specified content type (for attachment usecase) and message protocol (SOAP11/12)
	public static ServerMessageContext createServerMessageContextForTest1Service(String payload, String contentType, String messageProtocol)
				throws Exception {
		return TestUtils.createServerMessageContext(
					SOAPTestUtils.SOAP_BINDING_NAME,
					TestUtils.TEST1_SERVICE_NAME,
					messageProtocol,
					payload,
					new URL(TEST1_URL_STRING),
					contentType);
	}

	// create a SOAP client message context for Test1 service (default protocol: SOAP11)
	public static ClientMessageContext createClientMessageContextForTest1Service(Object outParam)
				throws Exception {
		return createClientMessageContextForTest1Service(outParam, null, SOAConstants.MSG_PROTOCOL_SOAP_11);
	}


	// create a SOAP client message context for Test1 service (default protocol: SOAP11)
	public static ClientMessageContext createClientMessageContextForTest1Service(Object outParam, String inPayload)
				throws Exception {
		return createClientMessageContextForTest1Service(outParam, inPayload, SOAConstants.MSG_PROTOCOL_SOAP_11);
	}


	// create a SOAP client message context for Test1 service, with specified message protocol  (SOAP11/12)
	public static ClientMessageContext createClientMessageContextForTest1Service(Object outParam, String inPayload, String messageProtocol)
				throws Exception {
		Map<String,String> headers = TestUtils.createHeadersForSoapTest();
		return TestUtils.createClientMessageContext(
					SOAPTestUtils.SOAP_BINDING_NAME,
					TestUtils.TEST1_SERVICE_NAME,
					messageProtocol,
					outParam, inPayload,
					new URL(TEST1_URL_STRING),
					headers, false, 0, null);
	}
}
