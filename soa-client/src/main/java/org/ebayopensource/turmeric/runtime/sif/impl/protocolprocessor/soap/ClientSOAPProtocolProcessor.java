/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.protocolprocessor.soap;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axis2.AxisFault;
import org.apache.axis2.engine.AxisEngine;
import org.ebayopensource.turmeric.runtime.binding.utils.XMLStreamReaderUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.Axis2Utils;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.BaseSOAPProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.SOAP11Fault;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.SOAP12Fault;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.SOAPUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * Title:			ClientSOAPProtocolProcessor.java
 * Description:
 * Copyright:		Copyright (c) 2007
 * Company:			eBay
 * @author 			Gary Yue
 * @version			1.0
 *
 * Axis2 Protocol Processor implementation for client
 */
public class ClientSOAPProtocolProcessor extends BaseSOAPProtocolProcessor {

	public void beforeRequestPipeline(MessageContext ctx) throws ServiceException
	{
		// NOOP
	}

	/**
	 * For client: Execute Axis2 outbound pipeline here
	 */
	public void beforeRequestDispatch(MessageContext ctx) throws ServiceException
	{
		//System.out.println("ClientSOAPProtocolProcessor: beforeRequestDispatch -- calling Axis Outbound pipeline!");
		org.apache.axis2.context.MessageContext axis2OutContext;
		try {
			// create Axis outbound context
			axis2OutContext = Axis2Utils.createOutboundAxis2Context(ctx, m_configContext);

			// create SOAP envelope
			SOAPEnvelope env = null;
			if (getMessageProtocol().equals(SOAConstants.MSG_PROTOCOL_SOAP_12)) {
				env = SOAPUtils.createSOAP12Envelope(null);
			} else if (getMessageProtocol().equals(SOAConstants.MSG_PROTOCOL_SOAP_11)) {
				env = SOAPUtils.createSOAP11Envelope(null);
			} else {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_OUTBOUND_SYSTEM_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), "ERROR: Unknown message protocol name specified"}));
			}
			// after setting envelope to context, context will now know whether this is SOAP11 or SOAP12
			axis2OutContext.setEnvelope(env);

			// overwrite the content-type here if it's not SOAP1.1 (which means it's SOAP1.2)
			if (!axis2OutContext.isSOAP11()) {
				setSOAP12ContentType( ctx.getRequestMessage());
			}

			// send via axis2 outbound pipeline
			AxisEngine.send(axis2OutContext);

			// add AxisOutContext to our message context, to be accessed on the client inbound flow
			ctx.setProperty(AXIS_OUT_CONTEXT, axis2OutContext);

		} catch (AxisFault e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_OUTBOUND_SYSTEM_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), e.getMessage()}), e);
		} catch (Exception e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_OUTBOUND_SYSTEM_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), e.getMessage()}), e);
		}
	}

	/**
	 * For client: Execute Axis2 inbound pipeline here
	 */
	public void beforeResponsePipeline(MessageContext ctx) throws ServiceException
	{
		//System.out.println("ClientSOAPProtocolProcessor: beforeResponsePipeline -- calling Axis Inbound pipeline!");

		// error checking: if already contain error, just return
		if (ctx.hasErrors()) {
			return;
		}

		org.apache.axis2.context.MessageContext axis2OutContext =
			(org.apache.axis2.context.MessageContext) ctx.getProperty(AXIS_OUT_CONTEXT);

		if (axis2OutContext == null ) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_INBOUND_SYSTEM_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {getMessageProtocol(), "Outbound axis2 context not found from message context"}));
		}

		org.apache.axis2.context.MessageContext axis2InContext = null;

		InboundMessage respMsg = (InboundMessage) ctx.getResponseMessage();
		try {
			
			// create Axis Inbound context from outbound
			axis2InContext = Axis2Utils.createInboundAxis2ContextFromOutbound(axis2OutContext);
			ctx.setProperty(AXIS_IN_CONTEXT, axis2InContext);
			SOAPEnvelope resEnvelope = null;

			// create response envelope
			if (axis2InContext.getEnvelope() == null) {

				resEnvelope = SOAPUtils.createSOAPEnvelope(respMsg.getXMLStreamReader(), axis2OutContext.getEnvelope().getNamespace().getNamespaceURI());
				if (resEnvelope != null) {
					axis2InContext.setEnvelope(resEnvelope);
				} else {
					throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_INBOUND_SYSTEM_ERROR,
							ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), "Error creating envelope for inbound message!"}));
				}
			}
		    // this is called to trigger the deserialization of the headers, and stored them in memory, prior to axis deserialization
			try {
				respMsg.getMessageHeadersAsJavaObject();
				respMsg.getMessageBody();
			} catch(Exception e) {
				// consume all exception here.
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_INBOUND_SYSTEM_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), "Payload Body or Header could not be parsed."}), e);
			}

			if (resEnvelope != null) {
	        	// pass to axis engine
	        	AxisEngine.receive(axis2InContext);
	        	
	        	// advance to the next start element
	        	//XMLStreamReaderUtils.advanceToNextStartElement(respMsg.getXMLStreamReader());

	        	// error flow case
		        if (respMsg.getXMLStreamReader().getName().getLocalPart().equals(SOAP_FAULT_NAME)) {
		        	
		        	respMsg.setTransportHeader(SOAHeaders.ERROR_RESPONSE, "true");
		        	// SOA 2.1 Addition
		        	if (axis2InContext.isFault()) {
		        		SOAPFault soapFault = axis2InContext.getEnvelope().getBody().getFault();
		        		
		        		SOAPFaultCode faultCode = soapFault.getCode();
		        		SOAPFaultReason faultReason = soapFault.getReason();
		        		
		        		SOAPFaultRole faultRole = null; // This element is optional, so we need to make sure we don't step too far
		        		
		        		String curNodeName = respMsg.getXMLStreamReader().getName().getLocalPart(); 
		        		if (SOAP_FAULT_ACTOR_NAME.equalsIgnoreCase(curNodeName) || SOAP_FAULT_ROLE_NAME.equalsIgnoreCase(curNodeName)) {
		        			faultRole = soapFault.getRole();				        	
				        }
		        		
		        		String faultCodeStr = (faultCode != null) ? faultCode.getText() : null;
		        		String faultReasonStr = (faultReason != null) ? faultReason.getText() : null;
		        		String faultRoleStr = (faultRole != null) ? faultRole.getText() : null;
		        		
		        		if (axis2InContext.isSOAP11()) {
		        			SOAP11Fault soap11Fault = new SOAP11Fault(faultCodeStr, faultReasonStr, faultRoleStr);
		        			ctx.setProperty(BaseSOAPProtocolProcessor.SOAP_FAULT_OBJECT, soap11Fault);
		        		} else {
		        			SOAP12Fault soap12Fault = new SOAP12Fault(faultCodeStr, faultReasonStr, faultRoleStr);
		        			ctx.setProperty(BaseSOAPProtocolProcessor.SOAP_FAULT_OBJECT, soap12Fault);
		        		}
		        	}

	                String faultTag = (SOAPUtils.isSOAP12Envelope(resEnvelope) ?
    						SOAP12Constants.SOAP_FAULT_DETAIL_LOCAL_NAME :
    						SOAP11Constants.SOAP_FAULT_DETAIL_LOCAL_NAME);
	                // In the case of fault, need to consume advance the XML Reader to the start of the fault detail element (ErrorMessage object)
	                if (!(XMLStreamReaderUtils.advanceToAfterStartElement(respMsg.getXMLStreamReader(), faultTag))) {
	                	throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_DATA_DESERIALIZATION_ERROR,
	                			ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), "Failed to advance XMLReader to fault <" + faultTag + "> tag!"}));
	                }
	            }
	        }
			
		} catch(XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_DATA_READ_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), e.getMessage()}), e);
		} catch(ServiceException e) {
			throw e;
		} catch(Exception e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_INBOUND_SYSTEM_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), e.getMessage()}), e);
		}
	}

	/**
	 * Execute Axis2 outbound pipeline here
	 */
	public void beforeResponseDispatch(MessageContext ctx) throws ServiceException
	{
		// NOOP
	}
}
