/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.axis2;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.axis2.transport.TransportSender;

/**
 * Dummy SOA Transport Sender which does not do anything
 * @author gyue
 *
 */
public class SOADummyTransportSender extends AbstractHandler implements TransportSender {

    public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {
		// NOOP
    	return null;
    }

    public void cleanup(MessageContext msgContext) throws AxisFault {
    	// NOOP
    }

    public void stop() {
    	//NOOP
    }

	public void init(ConfigurationContext confContext, TransportOutDescription transportOut) throws AxisFault {
		// NOOP
	}
}
