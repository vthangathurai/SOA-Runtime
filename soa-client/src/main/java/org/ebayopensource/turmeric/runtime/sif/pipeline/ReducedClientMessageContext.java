/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.pipeline;

import java.util.Set;

import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceContext;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;


/**
 * Minimum Client side MessageContext to be kept after the request is sent.
 *
 */
public interface ReducedClientMessageContext extends MessageContext{

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext#getInvokerVersion()
	 */
	@Override
	public String getInvokerVersion();

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext#getServiceId()
	 */
	@Override
	public ClientServiceId getServiceId();

	/**
	 * Get the property setting in effect for the current request context.
	 * @param name the name of the property value to retrieve.
	 * @return the property value, or null if none is set.
	 */
	public Object getRequestProperty(String name);

	/**
	 * Sets a property applicable to the current request context.
	 * @param name the property
	 * @param value the value of the property
	 */
	public void setRequestProperty(String name, Object value);

	/**
	 * Get the property setting in effect for the current response context.
	 * @param name the name of the property value to retrieve.
	 * @return the property value, or null if none is set.
	 */
	public Object getResponseProperty(String name);

	/**
	 * Sets a property applicable to the current response context.
	 * @param name the property
	 * @param value the value of the property
	 */
	public void setResponseProperty(String name, Object value);

	/**
	 * Returns a Set of String objects containing the names of all the request properties.
	 * @return a Set of String objects specifying the names of all the request properties
	 */
	public Set<String> getRequestPropertyNames();

	/**
	 * Returns a Set of String objects containing the names of all the response properties.
	 * @return a Set of String objects specifying the names of all the response properties
	 */
	public Set<String> getResponsePropertyNames();

	/**
	 * Used in service chaining in situations where a service's implementation is a client of some other service.
	 * The client logic can obtain access to the surrounding service context using this method, so that the
	 * upstream invocation state (headers, cookies, etc.) can be obtained. 
	 * @return the message context
	 */
	public MessageContext getCallerMessageContext();

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext#getServiceContext()
	 */
	@Override
	public ClientServiceContext getServiceContext();
	
}
