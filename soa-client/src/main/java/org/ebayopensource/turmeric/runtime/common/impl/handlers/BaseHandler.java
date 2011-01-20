/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.handlers;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.Handler;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;

/**
 * Base class for handlers, providing identification, and an implementation of the init() method.
 * @author wdeng
 */
public abstract class BaseHandler implements Handler {

	private ServiceId m_svcId;
	private String m_name;
	private String m_fullName;

	/*
	 * Initialize the handler; store the service name and administrative name.
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Handler#init(org.ebayopensource.turmeric.runtime.common.service.ServiceId, java.lang.String, java.util.Map)
	 */
	public void init(InitContext ctx)
		throws ServiceException
	{
		m_svcId = ctx.getServiceId();
		m_name = ctx.getName();
		m_fullName = m_svcId.getAdminName() + "." + m_name;
	}

	/* (non-Javadoc)
	 * Abstract invocation method to be implemented by specific handler classes.
	 * @see org.ebayopensource.turmeric.runtime.common.pipeline.Handler#invoke(org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext)
	 */
	public abstract void invoke(MessageContext ctx) throws ServiceException;

	/**
	 * Return the service identifier(administrative name and any sub-identifier such as client configuration name) 
	 * of the associated service.
	 * @return the service identifier
	 */
	protected final ServiceId getServiceId() {
		return m_svcId;
	}

	/**
	 * Returns the administrative name of the associated service.
	 * @return the administrative name
	 */
	protected final String getAdminName() {
		return m_svcId.getAdminName();
	}

	/**
	 * Returns the name of the handler as given in the configuration.
	 * @return the handler name
	 */
	protected final String getName() {
		return m_name;
	}

	/**
	 * Returns a string represnting both the service identifier and the handler name, for logging purposes.
	 * @return the full name string
	 */
	protected final String getFullName() {
		return m_fullName;
	}
}
