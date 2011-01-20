/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap;


import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.TransportUtils;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;



/**
 * Title:			SOAPUtils.java
 * Description:
 * Copyright:		Copyright (c) 2007
 * Company:			eBay
 * @author 			Gary Yue
 * @version			1.0
 *
 * Utility class that provides some basic SOAP functionalites
 * like SOAP envelope creation, SOA Message creation, etc..
 */
public class SOAPUtils {


	/**
	 * Create SOAP envelope from XML Stream reader
	 */
    public static SOAPEnvelope createSOAPEnvelope(XMLStreamReader streamReader, String envNamespaceURI)
            throws AxisFault {
        StAXBuilder builder =  new StAXSOAPModelBuilder(streamReader, envNamespaceURI);
        return TransportUtils.createSOAPEnvelope(builder.getDocumentElement());
    }


    /**
     * Create a SOAP envelope based on namespaceURI
     * @param namespaceURI
     * @param xmlPayload
     * @return
     */
    public static SOAPEnvelope createSOAPEnvelope(OMElement xmlPayload, String envNamespaceURI) {

		if (envNamespaceURI != null) {
			if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(envNamespaceURI)) {
				return createSOAP12Envelope(xmlPayload);
			}
		}
		// default is SOAP1.1
		return createSOAP11Envelope(xmlPayload);
	}

    /**
     * Create a SOAP envelope based on SOA protocol name (SOAP11 vs SOAP12)
     * @param protocolName
     * @param xmlPayload
     * @return
     */
	public static SOAPEnvelope createSOAPEnvelopeByProtocolName(String protocolName, OMElement xmlPayload) {

		if (protocolName != null) {
			if (SOAConstants.MSG_PROTOCOL_SOAP_12.equals(protocolName)) {
				return createSOAP12Envelope(xmlPayload);
			}
		}
		// default is SOAP1.1
		return createSOAP11Envelope(xmlPayload);
	}

	/**
	 * Create a SOAP12 envelope
	 * @param xmlPayload
	 * @return
	 */
	public static SOAPEnvelope createSOAP12Envelope(OMElement xmlPayload) {
		SOAPFactory soapFactory = OMAbstractFactory.getSOAP12Factory();
		SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();
		if (xmlPayload != null) {
		    envelope.getBody().addChild(xmlPayload);
		}
		return envelope;
	}

	/**
	 * Create a SOAP11 envelope
	 * @param xmlPayload
	 * @return
	 */
	public static SOAPEnvelope createSOAP11Envelope(OMElement xmlPayload) {
		SOAPFactory soapFactory = OMAbstractFactory.getSOAP11Factory();
		SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();
		if (xmlPayload != null) {
		    envelope.getBody().addChild(xmlPayload);
		}
		return envelope;
	}

	/**
	 * Check if this is a SOAP11 envelope
	 * @param env
	 * @return
	 */
	public static boolean isSOAP11Envelope(SOAPEnvelope env) {
		if (env.getNamespace().getNamespaceURI().
				equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
			return true;
		}

		return false;
	}

	/**
	 * Check if this is a SOAP12 envelope
	 * @param env
	 * @return
	 */
	public static boolean isSOAP12Envelope(SOAPEnvelope env) {
		if (env.getNamespace().getNamespaceURI().
				equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
			return true;
		}

		return false;
	}


	/**
	 * Serialize the SOAP fault to the specified xml stream writer
	 * @param fault
	 * @param writer
	 * @throws XMLStreamException
	 */
	public static void serializeSOAPFault(SOAPFault fault, XMLStreamWriter writer) throws XMLStreamException {

		OMSerializerUtil.serializeStartpart(fault, writer);
		OMElement e = null;

		// based on the namespace (SOAP1.1 vs SOAP1.2) to serialize accordingly
		if (fault.getNamespace().getNamespaceURI().equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
			// SOAP1.2
			e = fault.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_CODE);
			e.serialize(writer);

			e = fault.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_REASON);
			e.serialize(writer);

			e = fault.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_DETAIL);
			OMSerializerUtil.serializeStartpart(e, writer);

		} else {
			// SOAP1.1
			e = fault.getFirstChildWithName(SOAP11Constants.QNAME_FAULT_CODE);
			e.serialize(writer);

			e = fault.getFirstChildWithName(SOAP11Constants.QNAME_FAULT_REASON);
			e.serialize(writer);

			e = fault.getFirstChildWithName(SOAP11Constants.QNAME_FAULT_DETAIL);
			OMSerializerUtil.serializeStartpart(e, writer);
		}
	}

	/**
	 * Create a SOAP Fault code
	 * @param namespaceURI
	 * @param faultCodeStr
	 * @return
	 */
	public static SOAPFaultCode createSOAPFaultCode(String namespaceURI, String faultCodeStr) {

		SOAPFactory soapFactory = null;
		if (namespaceURI != null) {
			if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(namespaceURI)) {
				soapFactory = OMAbstractFactory.getSOAP12Factory();
			}
		}

		// default is SOAP1.1
		if (soapFactory == null)
			soapFactory = OMAbstractFactory.getSOAP11Factory();

		SOAPFaultCode code = soapFactory.createSOAPFaultCode();
		SOAPFaultValue value = soapFactory.createSOAPFaultValue(code);
		value.setText(faultCodeStr);
		code.setValue(value);
		return code;
	}



}
