/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.security;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.spf.security.ServerSecurityStatus;


/**
 * ServerSecurityStatus is class that indicates the status of the security check 
 *
 * @author gyue
 */
public class ServerSecurityStatusImpl implements ServerSecurityStatus {

	private boolean m_done = false;
	private boolean m_success;
	private String m_statusReason;
	private String m_statusCode;
	private String m_statusVendorCode;
	private Throwable m_failureCause;

	/**
	 * Is the security check performed
	 * @return
	 * @throws ServiceException
	 */
	public final boolean isDone() throws ServiceException {
		return m_done;
	}

	/**
	 * Is the security check successful
	 * @return
	 * @throws ServiceException
	 */
	public final boolean isSuccess() throws ServiceException {
		return m_success;
	}
	
	/**
	 * Get the status reason
	 * @return
	 * @throws ServiceException
	 */
	public final String getStatusReason() throws ServiceException {
		return m_statusReason;
	}

	/**
	 * Get the status code
	 * @return
	 * @throws ServiceException
	 */
	public final String getStatusCode() throws ServiceException {
		return m_statusCode;
	}
	
	/**
	 * Get the status vendor code
	 * @return
	 * @throws ServiceException
	 */
	public final String getStatusVendorCode() throws ServiceException {
		return m_statusVendorCode;
	}

	/**
	 * Get the failure cause (exception)
	 * @return
	 * @throws ServiceException
	 */
	public final Throwable getFailureCause() throws ServiceException {
		return m_failureCause;
	}
	
	// package level set methods. The idea is not to allow anyone (other then the framwork)
	// to set values here
	void setIsDone(boolean done) {
		m_done = done;
	}
	
	void setIsSuccess(boolean success) {
		m_success = success;
	}

	void setStatusReason(String reason)  {
		m_statusReason = reason;
	}

	void setStatusCode(String code) {
		m_statusCode = code;
	}
	

	void setStatusVendorCode(String vendorCode) {
		m_statusVendorCode = vendorCode;
	}

	void setFailureCause(Throwable th) {
		m_failureCause = th;
	}
}
