/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.service;

import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationRuntimeException;
import org.ebayopensource.turmeric.runtime.common.service.CommonServiceOperations;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.service.Service;


/**
 * Abstract base class for all client-side proxies
 *
 * Provides basic exception handling and implements common operations
 *
 * @author ichernyshev
 */
public abstract class BaseServiceProxy<T> implements CommonServiceOperations {

	/**
	 * Provides a reference to the Service (dynamic invocation interface) instance being wrapped/represented by thie proxy.
	 * Type-specific proxies reference this instance in order to make service invocations.
	 */
	protected final Service m_service;

	/**
	 * Constructor.  To be called by the derived class.
	 * @param service
	 */
	public BaseServiceProxy(Service service) {
		if (service == null) {
			throw new NullPointerException();
		}

		m_service = service;
	}

	/**
	 * Internal function to construct a ServiceInvocationRuntimeException with an inner ServiceInvocationException.
	 * Proxy operation methods have <code>throws</code> clauses which generally specify particular application-specific exceptions.
	 * In order for the SOA Framework to throw system exceptions, these must be created in a runtime exception form
	 * or otherwise they would violate the restrictions of the <code>throws</code> clause of the method.
	 * @param e the ServiceInvocationException to be wrapped
	 * @return the ServiceInvocationRuntimeException
	 */
	protected final ServiceInvocationRuntimeException wrapInvocationException(
		ServiceInvocationException e)
	{
		Throwable cause = (e.getCause() == null) ? e : e.getCause();
		return new ServiceInvocationRuntimeException(e, cause);
	}

	/*
	 * Base class implementation of the standard client-side operation, "getServiceVersion".
	 * @see org.ebayopensource.turmeric.runtime.common.service.CommonServiceOperations#getServiceVersion()
	 */
	public String getServiceVersion() {
		Object[] params = new Object[0];
		List<Object> returnParamList = new ArrayList<Object>();

		// no try-catch since interface does not declare any exceptions

		try {
			m_service.invoke(SOAConstants.OP_GET_VERSION, params, returnParamList);
		} catch (ServiceInvocationException th) {
			throw wrapInvocationException(th);
		}

		String result = (String)returnParamList.get(0);
		return result;
	}

	/* (non-Javadoc)
	 * Base class implementation of the standard client-side operation, "isServiceVersionSupported".
	 * @see org.ebayopensource.turmeric.runtime.common.service.CommonServiceOperations#isServiceVersionSupported(java.lang.String)
	 */
	public boolean isServiceVersionSupported(String version) {
		Object[] params = new Object[] {version};
		List<Object> returnParamList = new ArrayList<Object>();

		try {
			m_service.invoke(SOAConstants.OP_IS_SERVICE_VERSION_SUPPORTED, params, returnParamList);
		} catch (ServiceInvocationException th) {
			throw wrapInvocationException(th);
		}

		Boolean result = (Boolean) returnParamList.get(0);

		return result.booleanValue();
	}
}
