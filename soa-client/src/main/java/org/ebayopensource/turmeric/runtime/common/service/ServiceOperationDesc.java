/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.service;

import java.util.Collection;

/**
 * Interface providing parameter type information about a given operation.  Serializers use this information to determine
 * what kind of information is being serialized/deserialized, to validate that received types match expectations,
 * and to format any errors as appropriate for the operation (using the right error response type).
 * @author ichernyshev
 */
public interface ServiceOperationDesc {
	/**
	 * Returns the name of the operation to which this description applies.
	 * @return the name of the operation
	 */
	public String getName();

	/**
	 * If false, this is a "fallback" operation description, used only to format errors based on the default ErrorMessage
	 * type.  If true, this is a "real" operation description associated with a service interface as described by the 
	 * generated TypeMappings configuration file.
	 * @return true if this is a real, as opposed to fallback, operation description
	 */
	public boolean isExisting();

	/**
	 * If false, this operation was listed in the unsupported-operation section of the service provider options configuration.
	 * The operation is specifically disabled from invocation in this deployment.  If true, the service is not listed as
	 * an unsupported-operation, so it is allowed to be invoked. 
	 * @return true if the service is allowed to be invoked
	 */
	public boolean isSupported();

	/**
	 * Returns a ServiceOperationParamDesc describing the input parameter (request) type(s) for this operation.
	 * @return the input parameter (request) type information
	 */
	public ServiceOperationParamDesc getRequestType();

	/**
	 * Returns a ServiceOperationParamDesc describing the output parameter (response) type(s) for this operation.
	 * @return the output parameter (response) type information
	 */
	public ServiceOperationParamDesc getResponseType();

	/**
	 * Returns a ServiceOperationParamDesc describing the error parameter (error response) type for this operation.
	 * @return the error parameter (error response) type information
	 */
	public ServiceOperationParamDesc getErrorType();

	/**
	 * Returns a ServiceOperationParamDesc describing the request message protocol (e.g. SOAP) headers for this operation.
	 * @return the request header type information
	 */
	public ServiceOperationParamDesc getRequestHeaders();
	
	/**
	 * Returns a ServiceOperationParamDesc describing the response message protocol (e.g. SOAP) headers for this operation.
	 * @return the request header type information
	 */
	public ServiceOperationParamDesc getResponseHeaders();
	
	/**
	 * Returns the collection of all property names (from service_operations.properties file) for this operation.
	 * 
	 * @return the collection of property names for the operation
	 */
	public Collection<String> getPropertyNames();

	/**
	 * Returns the operation property with the specified name (from service_operations.properties file).
	 * @param name the name of the property to be returned
	 * @return the property value (currently always a string)
	 */
	public Object getProperty(String name);
}
