/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMException;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.dom.soap12.SOAP12Factory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.context.ServiceGroupContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.InOutAxisOperation;
import org.apache.axis2.description.OutInAxisOperation;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.transport.http.HTTPTransportUtils;
import org.apache.axis2.util.MessageContextBuilder;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.axis2.SOAConfigurator;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.axis2.SOADummyMessageReceiver;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;


/**
 * Title:			Axis2Utils.java
 * Description:  	
 * Copyright:		Copyright (c) 2007
 * Company:			eBay
 * @author 			Gary Yue
 * @version			1.0
 *
 * Utility class that provides convenient routines calling into Axis2 framework/runtime APIs.
 * Examples include message context creation, Axis service creation etc...
 */
public class Axis2Utils {
	
    private static final int VERSION_UNKNOWN = 0;
    private static final int VERSION_SOAP11 = 1;
    private static final int VERSION_SOAP12 = 2;
    
    /**
     * Entry point to the Axis2 on incoming post request
     * @param msgContext
     * @param inReader
     * @param out
     * @param contentType
     * @param soapActionHeader
     * @param requestURI
     * @throws AxisFault
     */
    public static void processHTTPPostRequest(org.apache.axis2.context.MessageContext msgContext,
            XMLStreamReader inReader,
            String contentType,
            String soapActionHeader,
            String requestURI) throws AxisFault {
    	int soapVersion = VERSION_UNKNOWN;
    	try {
    		soapVersion = HTTPTransportUtils.initializeMessageContext(msgContext, soapActionHeader, requestURI, contentType);

    		if (soapVersion == VERSION_SOAP12) {
    			msgContext.setEnvelope(
    					SOAPUtils.createSOAPEnvelope(inReader, SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));
    		} else {
    			msgContext.setEnvelope(
    					SOAPUtils.createSOAPEnvelope(inReader, SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI));
    		}
    		
    		AxisEngine.receive(msgContext);
    	} catch (SOAPProcessingException e) {
    		throw AxisFault.makeFault(e);
    	} catch (AxisFault e) {
    		throw e;
    	} catch (OMException e) {
    		throw AxisFault.makeFault(e);
    	} catch (FactoryConfigurationError e) {
    		throw AxisFault.makeFault(e);
    	} finally {
    		if ((msgContext.getEnvelope() == null) && soapVersion != VERSION_SOAP11) {
    			msgContext.setEnvelope(new SOAP12Factory().getDefaultEnvelope());
    		}
    	}
	}

    
	/**
	 * Create an inbound Axis2 MessageContext from eBay Message Context.
	 * This is used in the server request flow
	 * @param ctx
	 * @param configContext
	 * @return
	 * @throws AxisFault
	 */
	public static org.apache.axis2.context.MessageContext createInboundAxis2Context(MessageContext ctx, ConfigurationContext configContext) throws AxisFault {
		org.apache.axis2.context.MessageContext msgContext = new org.apache.axis2.context.MessageContext();
		
		// set the config context
		msgContext.setConfigurationContext(configContext);

		// configure to use NULL transport here (as defined in ebay-axis2.xml config file)
		msgContext.setTransportOut(configContext.getAxisConfiguration().getTransportOut("null"));

		// this needs to be set, otherwise, validateTransport() in DispatchPhase will fail
		msgContext.setIncomingTransportName("test"); //Constants.TRANSPORT_HTTP);

		// lookup default service
		AxisService defaultService = configContext.getAxisConfiguration().getService(SOAConfigurator.DEFAULT_AXIS2_SERVICE);
		msgContext.setAxisService(defaultService);
        
    	AxisOperation op = new InOutAxisOperation(new QName(ctx.getOperationName()));
    	op.setMessageReceiver(new SOADummyMessageReceiver());
    	msgContext.setAxisOperation(op);
    	
        return msgContext;
	}

	/**
	 * Create an inbound Axis2 MessageContext from outbound Axis2 MessageContext
	 * This is used in the client response flow
	 * @param axis2OutContext
	 * @return
	 * @throws AxisFault
	 */
	public static org.apache.axis2.context.MessageContext createInboundAxis2ContextFromOutbound(
						org.apache.axis2.context.MessageContext axis2OutContext) throws AxisFault {
	
		// create reponse context
		org.apache.axis2.context.MessageContext axis2InContext = new org.apache.axis2.context.MessageContext();
	
		axis2InContext.setDoingREST(false);

		// copy data from the outbound context
		axis2InContext.setTransportIn(axis2OutContext.getTransportIn());
		axis2InContext.setTransportOut(axis2OutContext.getTransportOut());
		axis2InContext.setOperationContext(axis2OutContext.getOperationContext());
		axis2InContext.setServiceContext(axis2OutContext.getServiceContext());
	
		return axis2InContext;
	}
	
	/**
	 * Create an outbound Axis2 MessageContext from eBay Message Context.
	 * This is used in the client request flow
	 * @param ctx
	 * @return
	 * @throws AxisFault
	 */
	public static org.apache.axis2.context.MessageContext createOutboundAxis2Context(MessageContext ctx, ConfigurationContext configContext) throws AxisFault {
		org.apache.axis2.context.MessageContext axis2OutContext = new org.apache.axis2.context.MessageContext();
		axis2OutContext.setConfigurationContext(configContext);

		// create dummy service/operation
		AxisService axisService = createSPFAxisService("SOAService", "SOAOperation", configContext);
		axisService.setClientSide(true);
		axis2OutContext.setAxisServiceGroup((AxisServiceGroup) axisService.getParent());
		axis2OutContext.setAxisService(axisService);

		// configure NULL transport
		axis2OutContext.setTransportIn(configContext.getAxisConfiguration().getTransportIn("null"));
		axis2OutContext.setTransportOut(configContext.getAxisConfiguration().getTransportOut("null"));

        ServiceGroupContext sgc = configContext.createServiceGroupContext(
                (AxisServiceGroup)axisService.getParent());
        ServiceContext sc = sgc.getServiceContext(axisService);

		// fill contextual information: operation context, fill service/sevice group context etc...
        AxisOperation axisOperation = axis2OutContext.getAxisService().getOperation(new QName("SOAOperation"));
        OperationContext operationContext = sc.createOperationContext(axisOperation);

        axisOperation.registerMessageContext(axis2OutContext, operationContext);
	    
        // fill the service group context and service context info
		axis2OutContext.getConfigurationContext().fillServiceContextAndServiceGroupContext(axis2OutContext);
		return axis2OutContext;
	}
    
	/**
	 * Create an outbound Axis2 MessageContext from an inbound Axis2 MessageContext
	 * This is used in the server response flow
	 * @param axis2InContext
	 * @return
	 * @throws AxisFault
	 */
	public static org.apache.axis2.context.MessageContext createOutboundAxis2ContextFromInbound(
						org.apache.axis2.context.MessageContext axis2InContext) throws AxisFault {
		return MessageContextBuilder.createOutMessageContext(axis2InContext);
	}
	
	/**
	 * Create a default SPF Axis Service based on the specified service name and operation name
	 * @param serviceName
	 * @param operationName
	 * @param configContext
	 * @return
	 * @throws AxisFault 
	 */
    public static AxisService createSPFAxisService(String serviceName, String operationName, ConfigurationContext configContext) throws AxisFault {
    	// create service and operation
        AxisService axisService = new AxisService(serviceName);
        AxisOperation axisOperation = new OutInAxisOperation(new QName(operationName));
        axisService.addOperation(axisOperation);

        // create new Axis Service Group and set it to AxisService
        AxisServiceGroup serviceGroup = new AxisServiceGroup();
        serviceGroup.addService(axisService);		
        axisService.setParent(serviceGroup);
		
		// NEED THIS???
		//m_axisConfiguration.addService(axisService);
        return axisService;
    }

}
