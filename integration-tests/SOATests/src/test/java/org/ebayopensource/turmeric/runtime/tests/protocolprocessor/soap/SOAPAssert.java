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

import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.BaseSOAPProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.tests.common.util.SOAPTestUtils;
import org.junit.Assert;


public class SOAPAssert {
    
    public static void assertBodyHasFault(org.apache.axis2.context.MessageContext msgcontext) {
        // make sure there is a fault in the body
        Assert.assertTrue("SOAP Body should have had a fault", msgcontext.getEnvelope()
                        .getBody().hasFault());
    }
    
    public static void assertBodyHasNoFault(org.apache.axis2.context.MessageContext msgcontext) {
        // make sure there is no fault in the body
        Assert.assertFalse("SOAP Body should have NO fault", msgcontext.getEnvelope()
                        .getBody().hasFault());
    }
    
    public static org.apache.axis2.context.MessageContext assertMessageValidINContext(MessageContext ctx)
    {
        // expects Axis context is created
        Object obj = ctx.getProperty(BaseSOAPProtocolProcessor.AXIS_IN_CONTEXT);
        Assert.assertNotNull("Axis2 IN message context is not set", obj);

        Assert.assertThat("Unknown context object encountered",
                obj, instanceOf(org.apache.axis2.context.MessageContext.class));
        
        org.apache.axis2.context.MessageContext msgcontext = (org.apache.axis2.context.MessageContext) obj;

        // validate the axis context
        Assert.assertTrue("axis2 IN message context validation failed",
                SOAPTestUtils.validateAxis2Context(msgcontext));

        return msgcontext;
    }

    public static org.apache.axis2.context.MessageContext assertMessageValidOUTContext(MessageContext ctx)
    {
        // expects Axis context is created
        Object obj = ctx.getProperty(BaseSOAPProtocolProcessor.AXIS_OUT_CONTEXT);
        Assert.assertNotNull("Axis2 OUT message context is not set", obj);

        Assert.assertThat("Axis2 OUT: Unknown context object encountered",
                obj, instanceOf(org.apache.axis2.context.MessageContext.class));
        
        org.apache.axis2.context.MessageContext msgcontext = (org.apache.axis2.context.MessageContext) obj;

        // validate the axis context
        Assert.assertTrue("Axis2 OUT message context validation failed",
                SOAPTestUtils.validateAxis2Context(msgcontext));

        return msgcontext;
    }

    public static void assertIsSOAP11Envelope(org.apache.axis2.context.MessageContext msgcontext) {
        SOAPEnvelope env = msgcontext.getEnvelope();
        Assert.assertThat("SOAPEnvelope", env, notNullValue());
        Assert.assertThat("SOAPEnvelope.namespace.uri should be for SOAP11", env.getNamespace().getNamespaceURI(),
                        is(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI));
    }
    
    public static void assertIsSOAP12Envelope(org.apache.axis2.context.MessageContext msgcontext) {
        SOAPEnvelope env = msgcontext.getEnvelope();
        Assert.assertThat("SOAPEnvelope", env, notNullValue());
        Assert.assertThat("SOAPEnvelope.namespace.uri should be for SOAP12", env.getNamespace().getNamespaceURI(),
                        is(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI));
    }
}
