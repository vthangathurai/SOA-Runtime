/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.pipeline;

import java.util.Collection;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;


/**
 * The VersionCheckHandler provides versioning information for a service.  This information 
 * is used by the framework for two purposes:
 * <UL>
 * <LI>To test whether an incoming request version is supported by the service; the version
 * check handler throws a ServiceException otherwise.
 * <LI>To support the built-in isServiceVersionSupported and getServiceVersion operations.
 * </UL>
 * 
 * Within the architecture, versions are maintained as strings at interface level.  Version check
 * handlers can implement further syntactic assumptions.  For eBay Marketplace, a handler
 * is implemented enforcing three-part numeric form; refer to <code>NumericVersionCheckHandler</code>.
 * 
 * The base VersionCheckHandler implementation will return current version and supported version 
 * based on configured values.
 * @author ichernyshev
 */
public interface VersionCheckHandler {

	/**
	 * Called by the framework to initialize this version check handler after initialization.
	 * @param ctx an InitContext.
	 * @throws ServiceException Exception when initialization fails.
	 */
	public void init(InitContext ctx) throws ServiceException;

	/**
	 * Returns the current service version.
	 * @return the current service version
	 */
	public String getVersion();

	/**
	 * Returns true if the specified version is supported by this service.  This method
	 * supports the isServiceVersionSupported operation.
	 * @param version the version to be checked
	 * @return true if the version is supported
	 * @throws ServiceException Exception when the isVersionSupported check encounters error.
	 */
	public boolean isVersionSupported(String version) throws ServiceException;

	/**
	 * Returns normally if the version in the client request (as carried in the message context)
	 * is supported by this service.  This method supports validation of incoming requests
	 * for compatibility vs. the service.
	 * @param ctx the message context of the current invocation
	 * @throws ServiceException if the version is not supported by this service.
	 */
	public void checkRequestVersion(MessageContext ctx) throws ServiceException;

	/**
	 * An Context to carry initialization parameters.
	 * 
	 * @author wdeng
	 *
	 */
	public static interface InitContext {
		/**
		 * Returns the ServiceId.
		 * @return the ServiceId.
		 */
		public ServerServiceId getServiceId();

		/**
		 * Returns the current version.
		 * @return the current version.
		 */
		public String getVersion(); 

		/**
		 * Returns the list of supported version.
		 * @return the list of supported version.
		 */
		public Collection<String> getSupportedVersion();
	}
}
