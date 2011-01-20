/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.security;

import java.util.Collection;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.security.SecurityContext;


/**
 * ServerSecurityContext is an interface through which all server side handlers can access security
 * information about the outgoing request and incoming response.
 *
 * @author gyue, ichernyshev
 */
public interface ServerSecurityContext extends SecurityContext {
	/**
	 * Returns the authentication status.
	 * @return the authentication status.
	 * @throws ServiceException Exception when failed to return the 
	 * authentication status.
	 */
	public ServerSecurityStatus getAuthnStatus() throws ServiceException;

	/**
	 * Returns the authorization status.
	 * @return the authorization status.
	 * @throws ServiceException Exception when failed to return the 
	 * authorization status.
	 */
	public ServerSecurityStatus getAuthzStatus() throws ServiceException;


	/**
	 * Returns the blacklist status.
	 * @return the blacklist status.
	 * @throws ServiceException Exception when failed to return the 
	 * blacklist status.
	 */
	public ServerSecurityStatus getBlacklistStatus() throws ServiceException;


	/**
	 * Returns the whitelist status.
	 * @return the whitelist status.
	 * @throws ServiceException Exception when failed to return the 
	 * whitelist status.
	 */
	public ServerSecurityStatus getWhitelistStatus() throws ServiceException;

	/**
	 * Set the authentication success status.
	 * @param statusReason  A string with reason of the returning status.
	 * @param statusCode A status code
	 * @param statusVendorCode A status vendor code.
	 * @throws ServiceException Exception when the set operation fails.
	 */
	public void setAuthnSuccess(String statusReason,
		String statusCode, String statusVendorCode) throws ServiceException;

	/**
	 * Set the authentication failure status.
	 * @param statusReason  A string with reason of the returning status.
	 * @param statusCode A status code
	 * @param statusVendorCode A status vendor code.
	 * @param cause the Throwable which causes the failure.
	 * @throws ServiceException Exception when the set operation fails.
	 */
	public void setAuthnFailure(String statusReason,
		String statusCode, String statusVendorCode, Throwable cause)
		throws ServiceException;
	
	/**
	 * Set the authorization success status.
	 * @param statusReason  A string with reason of the returning status.
	 * @param statusCode A status code
	 * @param statusVendorCode A status vendor code.
	 * @throws ServiceException Exception when the set operation fails.
	 */	
	public void setAuthzSuccess(String statusReason,
		String statusCode, String statusVendorCode) throws ServiceException;

	/**
	 * Set the authorization failure status.
	 * @param statusReason  A string with reason of the returning status.
	 * @param statusCode A status code
	 * @param statusVendorCode A status vendor code.
	 * @param cause the Throwable which causes the failure.
	 * @throws ServiceException Exception when the set operation fails.
	 */
	public void setAuthzFailure(String statusReason,
		String statusCode, String statusVendorCode, Throwable cause)
		throws ServiceException;
	
	/**
	 * Set the blacklist success status.
	 * @param statusReason  A string with reason of the returning status.
	 * @param statusCode A status code
	 * @param statusVendorCode A status vendor code.
	 * @throws ServiceException Exception when the set operation fails.
	 */	
	public void setBlacklistSuccess(String statusReason,
		String statusCode, String statusVendorCode) throws ServiceException;

	/**
	 * Set the blacklist failure status.
	 * @param statusReason  A string with reason of the returning status.
	 * @param statusCode A status code
	 * @param statusVendorCode A status vendor code.
	 * @param cause the Throwable which causes the failure.
	 * @throws ServiceException Exception when the set operation fails.
	 */
	public void setBlacklistFailure(String statusReason,
		String statusCode, String statusVendorCode, Throwable cause)
		throws ServiceException;
	
	/**
	 * Set the whitelist success status.
	 * @param statusReason  A string with reason of the returning status.
	 * @param statusCode A status code
	 * @param statusVendorCode A status vendor code.
	 * @throws ServiceException Exception when the set operation fails.
	 */	
	public void setWhitelistSuccess(String statusReason,
		String statusCode, String statusVendorCode) throws ServiceException;

	/**
	 * Set the whitelist failure status.
	 * @param statusReason  A string with reason of the returning status.
	 * @param statusCode A status code
	 * @param statusVendorCode A status vendor code.
	 * @param cause the Throwable which causes the failure.
	 * @throws ServiceException Exception when the set operation fails.
	 */
	public void setWhitelistFailure(String statusReason,
		String statusCode, String statusVendorCode, Throwable cause)
		throws ServiceException;

	/**
	 * Set authentication method.
	 * @param name the authentication method name.
	 * @throws ServiceException Exception when the set operation fails.
	 */
	public void setAuthnMethodName(String name) throws ServiceException;

	/**
	 * Get authentication method.
	 * @return authentication method.
	 * @throws ServiceException Exception when the get operation fails.
	 */
	public String getAuthnMethodName() throws ServiceException;

	/**
	 * Get the authentication methods defined in the configuration for this particular service/operation.
	 * @return authentication method collection.
	 */
	public Collection<String> getAuthnMethods();
}
