/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.security;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;

/**
 * ServerSecurityStatus is class that indicates the status of the 
 * security check. 
 *
 * @author gyue
 */
public interface ServerSecurityStatus {

	/**
	 * Is the security check performed.
	 * @return True if the security check performed
	 * @throws ServiceException Exception if the operation fails.
	 */
	public boolean isDone() throws ServiceException;

	/**
	 * Is the security check successful.
	 * @return True if the security check successful.
	 * @throws ServiceException Exception if the operation fails.
	 */
	public boolean isSuccess() throws ServiceException;
	
	/**
	 * Get the status reason.
	 * @return the status reason.
	 * @throws ServiceException Exception if the operation fails.
	 */
	public String getStatusReason() throws ServiceException;

	/**
	 * Get the status code.
	 * @return the status code.
	 * @throws ServiceException Exception if the operation fails.
	 */
	public String getStatusCode() throws ServiceException;
	
	/**
	 * Get the status vendor code.
	 * @return the status vendor code.
	 * @throws ServiceException Exception if the operation fails.
	 */
	public String getStatusVendorCode() throws ServiceException;

	/**
	 * Get the failure cause (exception).
	 * @return the failure cause (exception).
	 * @throws ServiceException Exception if the operation fails.
	 */
	public Throwable getFailureCause() throws ServiceException;
	




	







	
}
