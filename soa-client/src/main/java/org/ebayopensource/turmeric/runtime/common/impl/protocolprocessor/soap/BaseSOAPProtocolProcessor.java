/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap;

import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.utils.XMLStreamReaderUtils;
import org.ebayopensource.turmeric.runtime.binding.utils.XMLStreamReaderUtilsException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.axis2.SOAConfigurator;
import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.ProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * Title:			BaseSOAPProtocolProcessor.java
 * Description:
 * Copyright:		Copyright (c) 2007
 * Company:			eBay
 * @author 			Gary Yue
 * @version			1.0
 *
 * Abstract class for SOAP Protocol Processor. Contains shared codes that can be used by child classes
 */
public abstract class BaseSOAPProtocolProcessor implements ProtocolProcessor {
    private static final Logger LOG = Logger.getLogger(BaseSOAPProtocolProcessor.class.getName());
	protected ConfigurationContext m_configContext;
	protected String m_messageProtocolName = null;

    // Default Error Response Indicator Code for SOAP is HTTP_INTERNAL_ERROR (500)
    private static int m_defaultTransportErrorResponseIndicationCode = HttpURLConnection.HTTP_INTERNAL_ERROR;

    // Alternate Error Response Indicator Code for SOAP is HTTP_OK (200)
    private static int m_alternateTransportErrorResponseIndicationCode = HttpURLConnection.HTTP_OK;

    public static final String AXIS_IN_CONTEXT = "AXIS_IN_CONTEXT";
    public static final String AXIS_OUT_CONTEXT = "AXIS_OUT_CONTEXT";
    public static final String SOAP_FAULT_OBJECT = "SOAP_FAULT_OBJECT";

    private static Collection<String> s_supportedDataFormats;

    static {
		// initialize the static supported data format list
    	s_supportedDataFormats = new ArrayList<String>();
		s_supportedDataFormats.add(BindingConstants.PAYLOAD_XML);
    }

    public void init(InitContext ctx) throws ServiceException {
		LOG.fine("Initializing SOAPProtocolProcessor..."); //KEEPME
    	try {
    		m_messageProtocolName = ctx.getName(); // SOAP11 or SOAP12
    		m_configContext =
			    ConfigurationContextFactory.createConfigurationContext(new SOAConfigurator());
		} catch (AxisFault e) {
		    LOG.log(Level.WARNING, "Unable to initialize " + BaseSOAPProtocolProcessor.class.getName(), e);
			e.printStackTrace();
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_INIT_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), e.toString()}));
		}

		m_configContext.setProperty(Constants.CONTAINER_MANAGED, Constants.VALUE_TRUE);
	}

	public void postDeserialize(InboundMessage msg) throws ServiceException {
		//System.out.println("BasedSOAPProtocolProcessor: postDeserialize() -- consume ending Envelope tag!");
		XMLStreamReader reader = msg.getXMLStreamReader();

		try {

			// get env from context
			org.apache.axis2.context.MessageContext axis2InContext =
				(org.apache.axis2.context.MessageContext) msg.getContext().getProperty(AXIS_IN_CONTEXT);

			if (axis2InContext == null) {
				throw new IllegalStateException("Attempt to post-deserialize without going " +
					"through before-pipeline event first");
			}

			SOAPEnvelope env = axis2InContext.getEnvelope();

			// handler fault case on inbound response
			if (!msg.getContext().getProcessingStage().isRequestDirection() && msg.isErrorMessage()) {
				// consume the end detail tag
				String endFaultDetailTag =
						(SOAPUtils.isSOAP12Envelope(env) ? SOAP12Constants.SOAP_FAULT_DETAIL_LOCAL_NAME
														: SOAP11Constants.SOAP_FAULT_DETAIL_LOCAL_NAME);
				XMLStreamReaderUtils.consumeEndElement(reader, endFaultDetailTag);

				// consume the end Fault tag
				XMLStreamReaderUtils.consumeEndElement(reader, SOAPConstants.SOAPFAULT_LOCAL_NAME);

			}
			// consume the end Body tag
			XMLStreamReaderUtils.consumeEndElement(reader, SOAPConstants.BODY_LOCAL_NAME);

			// consume end Envelope tag
			XMLStreamReaderUtils.consumeEndElement(reader, SOAPConstants.SOAPENVELOPE_LOCAL_NAME);

			// should be at the end of the doc
			int type = reader.getEventType();
			if (type == XMLStreamConstants.END_DOCUMENT) {
				try {
					reader.close();
				} catch (XMLStreamException e) {
					throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_DATA_READ_ERROR,
							ErrorConstants.ERRORDOMAIN, new Object[] {getMessageProtocol(), e.toString()}), e);
				}
			} else {
				//System.out.println("current pos: " + reader.getLocation().getCharacterOffset());
				// ERROR: SHOULD NOT REACH HERE!!!
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_DATA_DESERIALIZATION_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[] {getMessageProtocol(), "Error: Contain XML element after ending SOAP Envelope tag"}));
			}
			
			// SOA 2.1 Addition
			MessageContext ctx = msg.getContext();
			Object soapFault = ctx.getProperty(SOAP_FAULT_OBJECT);
			if (soapFault != null) {
				Object detail = msg.getErrorResponseInternal();
				if (SOAPUtils.isSOAP12Envelope(env)) {
					((SOAP12Fault)soapFault).setDetail(detail);
				} else {
					((SOAP11Fault)soapFault).setDetail(detail);
				}
				msg.setErrorResponse(soapFault);
			}
		} catch(XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_DATA_READ_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {getMessageProtocol(), e.toString()}), e);
		} catch(ServiceException e) {
			throw e;
		}catch(XMLStreamReaderUtilsException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_DATA_DESERIALIZATION_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {getMessageProtocol(), e.toString()}), e);
		} catch (Exception e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_INBOUND_SYSTEM_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {getMessageProtocol(), e.toString()}), e);
		}
	}

	public void preSerialize(OutboundMessage msg, XMLStreamWriter writer) throws ServiceException {
		//System.out.println("BasedSOAPProtocolProcessor: preSerialize -- write start envelope/header/body!");

		// get axis2 out Context
		org.apache.axis2.context.MessageContext axis2OutContext =
			(org.apache.axis2.context.MessageContext) msg.getContext().getProperty(AXIS_OUT_CONTEXT);

		SOAPEnvelope env = axis2OutContext.getEnvelope();

		try {
			// write start xml declaration
			writer.writeStartDocument(OMConstants.DEFAULT_CHAR_SET_ENCODING,
	                    				"1.0");

			// write the start envelope tag
			OMSerializerUtil.serializeStartpart(env, writer);

			// write header block
//			SOAPHeader header = env.getHeader();
//			header.serialize(writer);

			// write header block
			SOAPHeader header = env.getHeader();
			//header.serialize(writer);
			// write the start header tag
			OMSerializerUtil.serializeStartpart(header, writer);
			// write the header content
			msg.serializeHeader(writer);
			// write the end header tag
			OMSerializerUtil.serializeEndpart(writer);


			// write start body
			SOAPBody body = env.getBody();
			// normal response: just serialize the start body tag. JAXB will handler the body serialization
			OMSerializerUtil.serializeStartpart(body, writer);

			// check if the body has fault. If so, emit the fault information up to the detail tag
			if (body.hasFault()) {
				// fault case: serialize the fault info up to the detail tag
				SOAPUtils.serializeSOAPFault(body.getFault(), writer);
			}
		} catch (XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_DATA_WRITE_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {getMessageProtocol(), e.toString()}), e);
		} catch (Exception e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_OUTBOUND_SYSTEM_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {getMessageProtocol(), e.toString()}), e);
		}
	}


	public void postSerialize(OutboundMessage msg, XMLStreamWriter writer) throws ServiceException {
		//System.out.println("BasedSOAPProtocolProcessor: postSerialize -- write end envelope/body tag!");

        try {
        	// complete the end tags
			writer.writeEndDocument();
		} catch (XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_DATA_WRITE_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {getMessageProtocol(), e.toString()}), e);
		} catch (Exception e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_OUTBOUND_SYSTEM_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {getMessageProtocol(), e.toString()}), e);
		}
	}


	public String getMessageProtocol() {
		return m_messageProtocolName;
	}

	public boolean supportsHeaders() {
		return true;
	}

	@SuppressWarnings("unused")
    private static final String SOAP_ENVELOPE_NAME = "Envelope";
	private static final String SOAP_HEADER_NAME = "Header";
	private static final String SOAP_BODY_NAME = "Body";
	public static final String SOAP_FAULT_NAME = "Fault";
	public static final String SOAP_FAULT_ACTOR_NAME = "faultactor";
	public static final String SOAP_FAULT_ROLE_NAME = "Role";

	public Collection<ObjectNode> getMessageHeaders(ObjectNode root) throws ServiceException {
		try {
			ObjectNode envelope = getSoapEnvelope(root);
			if (null == envelope) {
				return null;
			}
			Iterator<ObjectNode> children = envelope.getChildrenIterator();
			if (!children.hasNext()) {
				return null;
			}
			ObjectNode header = (ObjectNode) children.next();
			if (SOAP_HEADER_NAME.equalsIgnoreCase(header.getNodeName().getLocalPart())) {
				return header.getChildNodes();
			}
			return null;
		} catch (XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_READ_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[] {e.toString()}), e);
		}
	}

	public ObjectNode getMessageBody(ObjectNode root) throws ServiceException {
		try {
			ObjectNode envelope = getSoapEnvelope(root);
			if (null == envelope) {
				return null;
			}
			Iterator<ObjectNode> children = envelope.getChildrenIterator();
			if (!children.hasNext()) {
				return null;
			}
			ObjectNode body = children.next();
			if (SOAP_BODY_NAME.equalsIgnoreCase(body.getNodeName().getLocalPart())) {
				return body;
			}
			if (!children.hasNext()) {
				return null;
			}
			body = children.next();
			if (SOAP_BODY_NAME.equalsIgnoreCase(body.getNodeName().getLocalPart())) {
				return body;
			}
			return null;
		} catch (XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_READ_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[] {e.toString()}), e);
		}
	}

	public Collection<String> getSupportedDataFormats() {
		return s_supportedDataFormats;
	}

	private ObjectNode getSoapEnvelope(ObjectNode root) throws XMLStreamException {
		Iterator<ObjectNode> childNodes = root.getChildrenIterator();
		if (childNodes.hasNext()) {
			return childNodes.next();
		}
		return null;
	}

	public ConfigurationContext getConfigurationContext() {
		return m_configContext;
	}

    public void setSOAP12ContentType(Message msg) throws ServiceException {
    	String mimeType = SOAP12Constants.SOAP_12_CONTENT_TYPE;
    	Charset charset = msg.getG11nOptions().getCharset();
    	String contentType = HTTPCommonUtils.formatContentType(mimeType, charset);
    	msg.setTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE, contentType);
    }

    public int getTransportErrorResponseIndicationCode() {
		return m_defaultTransportErrorResponseIndicationCode;
	}

	public int getAlternateTransportErrorResponseIndicationCode() {
		return m_alternateTransportErrorResponseIndicationCode;
	}

	public boolean isExpectedMessageProtocol(Message msg)
			throws ServiceException {
		String contentType = msg
				.getTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE);

		if (contentType == null)
			return false;

		// Content-Type is not case sensitive according to rfc:
		// http://www.w3.org/Protocols/rfc1341/4_Content-Type.html
		contentType = contentType.toLowerCase();

		String soapAction = msg
				.getTransportHeader(SOAConstants.HTTP_HEADER_SOAP11_SOAPACTION);

		// Check for SOAP11
		if (contentType.indexOf(SOAP11Constants.SOAP_11_CONTENT_TYPE
				.toLowerCase()) > -1
				&& soapAction != null
				&& SOAConstants.MSG_PROTOCOL_SOAP_11
						.equals(m_messageProtocolName)) {
			return true;
		}// Check for SOAP12
		else if (contentType.indexOf(SOAP12Constants.SOAP_12_CONTENT_TYPE
				.toLowerCase()) > -1
				&& SOAConstants.MSG_PROTOCOL_SOAP_12
						.equals(m_messageProtocolName)) {
			return true;
		}
		return false;
	}

/*	public boolean isExpectedMessageProtocol(ObjectNode root) throws ServiceException {
		if (root != null) {
			try {
				ObjectNode envelope = getSoapEnvelope(root);

				if (envelope != null && SOAP_ENVELOPE_NAME.equalsIgnoreCase(envelope.getNodeName().getLocalPart())) {
					String nsURI = envelope.getNodeName().getNamespaceURI();

					if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(nsURI) &&
							SOAConstants.MSG_PROTOCOL_SOAP_11.equals(m_messageProtocolName)) {
						return true;
					} else if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(nsURI) &&
							SOAConstants.MSG_PROTOCOL_SOAP_12.equals(m_messageProtocolName)) {
						return true;
					} 
				}
			} catch (Exception e) {
				throw new ServiceException(
				ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_READ_ERROR,
					ErrorConstants.ERRORDOMAIN),e);
				
			}
		}
		return false;
	}*/
	
	
}
