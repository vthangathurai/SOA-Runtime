/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.service;

/**
 * Defines operations provided by all services.  These operations do not have to implemented by
 * the service implementation, because the BaseServiceRequestDispatcher handles them generically on behalf of the
 * service-specific dispatcher.
 * 
 * CommonServiceOperations is implemented by the BaseServicProxy class.  Generated proxies derive from this class, so
 * they automatically inherit an implementation that will provide these operations as proxy methods.
 * 
 * The TypeMappings file must register these operations for all services, in order for the operations to be passed as
 * valid, on both client and server side. The code generator ensures that these operations are generated into the
 * TypeMappings file.
 * 
 * @author ichernyshev
 */
public interface CommonServiceOperations {

	/**
	 * Returns the current implementation version of the service.
	 * @return the implementation version of the service
	 * 
	 */
	public String getServiceVersion();

	/**
	 * Returns whether a specific version is supported by the service.
	 * @param version the version to test for support
	 * @return true if the service implementation supports this version.
	 */
	public boolean isServiceVersionSupported(String version);
}
