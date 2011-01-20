/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.protocolprocessor.soap;

import java.util.logging.Logger;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.util.MessageContextBuilder;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.Axis2Utils;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.BaseSOAPProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.SOAPUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * Title:			ServerSOAPProtocolProcessor.java
 * Description:  	
 * Copyright:		Copyright (c) 2007
 * Company:			eBay
 * @author 			Gary Yue
 * @version			1.0
 *
 * Axis2 Protocol Processor implementation for server
 */
public class ServerSOAPProtocolProcessor extends BaseSOAPProtocolProcessor {
    private static final Logger LOG = Logger.getLogger(ServerSOAPProtocolProcessor.class.getName());

	/**
	 * Execute Axis2 inbound pipeline here
	 */
	public void beforeRequestPipeline(MessageContext ctx) throws ServiceException 
	{
		//System.out.println("ServerSOAPProtocolProcessor: beforeRequestPipeline -- calling Axis Inbound pipeline!");
		try {
			// get transport headers
			String contentType = ctx.getRequestMessage().getTransportHeader(HTTPConstants.HEADER_CONTENT_TYPE.toUpperCase());
			String soapAction = ctx.getRequestMessage().getTransportHeader(HTTPConstants.HEADER_SOAP_ACTION.toUpperCase());
			InboundMessage requestMsg = (InboundMessage) ctx.getRequestMessage();

			String requestURL = null;
			// obtain the request URL
			if (ctx.getServiceAddress().getServiceUrl() != null) {
				requestURL = ctx.getServiceAddress().getServiceUrl().toString();
			}
			
			// create Axis2 context from Ebay context, and attached it to Ebay Context
			// NOTE: this axis inbound context is REQUIRED in the system for sending proper outbound SOAP fault!!!
			org.apache.axis2.context.MessageContext axis2Context = Axis2Utils.createInboundAxis2Context(ctx, m_configContext);
			ctx.setProperty(AXIS_IN_CONTEXT, axis2Context);

			// if error has happened prior to the inbound flow, we have safely exit, since no additional processing is required.
			if (ctx.hasErrors()) {
				return;
			}

			Exception exceptionWhileProcessing = null;
			// ok.. we need to see if there was an exception while processing the header or body since a tag may be wrong
			// e.g.. <bodyBAD> (check the tests :) In the older version of axis (and code), this exception would have been ignored here and
			// would have been rethrown from the Axis2Utils and everything was peachy. But that would not be the right thing to do here
			// and in the newer version, the envelope processing goes on anyways for our purposes (getting the child), and it cleanly skips the
			// bad tag. So, this issue is not caught in the Axis2Utils codepath either. 
			// So now we catch this exception while parsing, save it and then throw it AFTER the Axis2Utils.processHTTPPostRequest call.
			// why do we need that? Well.. that does the whole soap envelope setting, namespaces etc. If that doesnt happen like this,
			// a client could make a soap12 call but get back a soap11 fault response since the Axis2Utils codepath didnt get executed.
			try {
			    // this is called to trigger the deserialization of the headers, and stored them in memory, prior to axis deserialization
				requestMsg.getMessageHeadersAsJavaObject();
				requestMsg.getMessageBody();
			} catch(Exception e) {
				exceptionWhileProcessing = e;
			}
			
			LOG.info("request msg: " + requestMsg);
			LOG.info("xml stream reader: " + requestMsg.getXMLStreamReader());
			/*** ENTERING AXIS2 PIPELINE ***/
			Axis2Utils.processHTTPPostRequest(axis2Context, requestMsg.getXMLStreamReader(), contentType, soapAction, requestURL);
			/*** EXITING AXIS2 PIPELINE ***/
			// went thru Axis2 pipline successfully...

			if(exceptionWhileProcessing!=null){
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_INBOUND_SYSTEM_ERROR,
						ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), "Payload Body or Header could not be parsed."}), exceptionWhileProcessing);

			}
		} catch (AxisFault e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_INBOUND_SYSTEM_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), e.getMessage()}), e);
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_INBOUND_SYSTEM_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), e.getMessage()}), e);
		}
	}
	
	public void beforeRequestDispatch(MessageContext ctx) throws ServiceException 
	{
			// NOOP
	}
	
	public void beforeResponsePipeline(MessageContext ctx) throws ServiceException
	{
			// NOOP
	}
	 
	/**
	 * Execute Axis2 outbound pipeline here
	 */
	public void beforeResponseDispatch(MessageContext ctx) throws ServiceException
	{
		//System.out.println("ServerSOAPProtocolProcessor: beforeResponseDispatch -- calling Axis Outbound pipeline!");

		// get axis2 Context 
		org.apache.axis2.context.MessageContext axis2InContext = 
			(org.apache.axis2.context.MessageContext) ctx.getProperty(AXIS_IN_CONTEXT);
		
		if (axis2InContext == null ) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_OUTBOUND_SYSTEM_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[] {getMessageProtocol(), "Inbound axis2 context not found from message context"}));
		}
		
		try {
        	// overwrite the content-type here if it's not SOAP1.1 (which means it's SOAP1.2)
            if (!axis2InContext.isSOAP11()) {
                setSOAP12ContentType( ctx.getResponseMessage());
            }

            if (ctx.hasErrors()) {
            	// error flow
            	// heck: to bypass a nullpointer
            	//axis2InContext.getAxisOperation().addParameter(new Parameter(Constants.Configuration.SEND_STACKTRACE_DETAILS_WITH_FAULTS, Boolean.FALSE));
            	//axis2InContext.getOperationContext().setProperty(Constants.Configuration.SEND_STACKTRACE_DETAILS_WITH_FAULTS, Boolean.FALSE);

            	// Convert the exception to a Axis fault, so that axis can create a proper SOAP Fault message from it 
            	org.apache.axis2.context.MessageContext faultContext = createSOAPFaultContext(ctx.getErrorList().get(0), axis2InContext);
            	ctx.setProperty(AXIS_OUT_CONTEXT, faultContext);
            	
            	// go thru axis2 pipeline
            	AxisEngine.sendFault(faultContext);
            } else {
            	// normal flow
            	// Create Axis2 Out context from In context
            	org.apache.axis2.context.MessageContext axis2OutMsgContext =
            			Axis2Utils.createOutboundAxis2ContextFromInbound(axis2InContext);
    			ctx.setProperty(AXIS_OUT_CONTEXT, axis2OutMsgContext);

            	// Create an empty envelope
                SOAPEnvelope envelope = SOAPUtils.createSOAPEnvelope((OMElement)null, axis2InContext.getEnvelope().getNamespace().getNamespaceURI());
                axis2OutMsgContext.setEnvelope(envelope);

	        	// go thru axis2 pipeline
                AxisEngine.send(axis2OutMsgContext);
            }
            
		} catch (AxisFault e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_OUTBOUND_SYSTEM_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), e.getMessage()}), e);
		} catch (Exception e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_PROTOCOLPROCESSOR_OUTBOUND_SYSTEM_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[]{getMessageProtocol(), e.getMessage()}), e);
		}
	}

	private org.apache.axis2.context.MessageContext createSOAPFaultContext(	Throwable throwable, 
																org.apache.axis2.context.MessageContext axis2InContext) throws AxisFault {

		// convert the exception to an Axis Fault
    	AxisFault axisFault = createAxisFaultFromException(throwable);

    	// populate axisFault (fault code, fault reason, etc...)
    	populateAxisFault(axisFault, axis2InContext);
    	
    	// Create Axis2 Fault Out Context from In context
    	org.apache.axis2.context.MessageContext faultContext = 
    						 MessageContextBuilder.createFaultMessageContext(axis2InContext, axisFault);
		
    	return faultContext;
		
	}

	// takes the exception and maps it to a soap fault
	private AxisFault createAxisFaultFromException(Throwable throwable) {
		if (throwable instanceof ServiceException) {
			// handle service exception case
			ServiceException ex = (ServiceException)throwable;
			if (ex.getCause() != null && ex.getCause() instanceof AxisFault) {
				// if there is a nested soap fault, use this as the soap fault 
				return (AxisFault) ex.getCause();
			}

			// otherwise, create a new soap fault
			return new AxisFault(ex.getMessage(), ex);
		}

		return new AxisFault(throwable.getMessage(), throwable);
	}
	
	private void populateAxisFault(AxisFault axisFault, org.apache.axis2.context.MessageContext axis2InContext) {
		if (axis2InContext.getEnvelope() == null) {
			// default: SOAP11
			if (axis2InContext.isSOAP11()) {
				axisFault.setFaultCode(SOAP11Constants.QNAME_RECEIVER_FAULTCODE); 
			} else {
				axisFault.setFaultCode(SOAP12Constants.QNAME_RECEIVER_FAULTCODE); 
			}
		} else {
			String envNamespaceURI = axis2InContext.getEnvelope().getNamespace().getNamespaceURI();
			
			// map fault code
			if (envNamespaceURI.equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
				axisFault.setFaultCode(SOAP12Constants.QNAME_RECEIVER_FAULTCODE);
			} else {
				axisFault.setFaultCode(SOAP11Constants.QNAME_RECEIVER_FAULTCODE);
			}
		}
	}

}
