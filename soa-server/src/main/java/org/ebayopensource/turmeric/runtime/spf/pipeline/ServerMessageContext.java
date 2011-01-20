/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.pipeline;

import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceContext;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;


/**
 * ServerMessageContext is an interface through which all client side handlers
 * get access to the message that is being processed. It extends the common
 * MessageContext interface. It provides access to ServiceProviderOptions and
 * ServiceConfig.
 * 
 * @author wdeng
 */
public interface ServerMessageContext extends MessageContext {
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext#getServiceId()
	 */
	@Override
	public ServerServiceId getServiceId();
	
	/**
	 * Returns the name of the server being accessed by the web service query, as specified in the request URL.
	 * Can be null if the transport does not have the concept of server name.  Returns "(local binding)" if the
	 * transport is LocalTransport.
	 * @return the request URL server name
	 */
	public String getTargetServerName();

	/**
	 * Returns the service port number being accessed by the web service query, as specified in the request URL.
	 * @return the request URL server port
	 */
	public int getTargetServerPort();
	
	/**
	 * Returns a read-only view of the query parameter map, as parsed from the request URL.
	 * @return the query parameter map
	 */
	public Map<String,String> getQueryParams();

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext#getServiceContext()
	 */
	@Override
	public ServerServiceContext getServiceContext();
}
