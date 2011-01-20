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
import org.apache.axis2.context.MessageContext;

/**
*  Dummy message receiver for SOA, which does nothing and return.
*/

public class SOADummyMessageReceiver extends org.apache.axis2.receivers.AbstractMessageReceiver {


	@Override
    public final void receive(MessageContext msgContext) throws AxisFault {
		// NOOP
    }
    
	@Override
    protected void invokeBusinessLogic(MessageContext messageCtx) throws AxisFault {
    	// NOOP
    }
}

