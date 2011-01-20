/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.pipeline;

import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;

/**
 * ClientMessageContext is an interface through which all client side handlers
 * get access to the message that is being processed. It extends the common
 * MessageContext interface. It provides access to ServiceInvokerOptions and
 * ClientConfig.
 * 
 * @author wdeng, ichernyshev
 */
public interface ClientMessageContext extends ReducedClientMessageContext {

	/**
	 * A property key to store the time when the request is sent.  This is 
	 * used to calculate client side call time.
	 */
	static String REQUEST_SENT_TIME = "request_sent_time";

	/**
	 * Returns the service invoker options that were supplied for this specific
	 * invocation. This value is never null. Any service invoker options will
	 * override their corresponding values in configuration.
	 * 
	 * @return the service invoker options
	 */
	public ServiceInvokerOptions getInvokerOptions();

}
