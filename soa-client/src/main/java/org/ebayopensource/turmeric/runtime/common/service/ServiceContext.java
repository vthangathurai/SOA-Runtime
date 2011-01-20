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
import java.util.List;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;


/**
 * Service context provides a summary view of the internal configuration and state data of the message context. This allows
 * implementation of configuration-aware transports, protocol processors, handlers, and other framework extensions, without 
 * exposing all implementation detail of the message context. 
 * 
 * Information stored in the ServiceContext includes:
 * <UL>
 * <LI> Service admin name and fully qualified name
 * <LI> Low-level source configuration
 * <LI> Reference to service Class Loader
 * <LI> List of service operations descriptions (map of name-to-operation)
 * <LI> List of service data binding descriptions (map of name-to-binding)
 * <LI> ServiceTypeMappings defining mapping of Java and XML type names, namespaces and packages
 * <LI> Both Request and Response pipeline instances
 * <LI> Request and Response dispatcher instances
 * <LI> List of service protocol processors instances (map of name-to-processor)
 * <LI> List of service transport instances (map of name-to-transport)
 * <LI> List of service logging handler instances
 * <LI> Service Interface class pointer
 * <LI> Cached metric value aggregators
 * </UL>
 *
 * @author ichernyshev
 */
public interface ServiceContext {
	/**
	 * Returns a client/service identifier associated with a local configuration instance.  This identifier consists of
	 * the administrative name of the service being consumed or provided, plus any sub-identification such as the consuming
	 * client (configuration) instance.
	 * @return the service ID
	 */
	public ServiceId getServiceId();

	/**
	 * Returns the administrative name of the service whose invocation is currently in process for this message context.
	 * On the client side, the administrative name is the local part of the service qualified name configured in ClientConfig.xml. 
	 * On the server side, the administrative name matches the folder name holding the ServiceConfig.xml file.
	 * @return the administrative name
	 */
	public String getAdminName();

	/**
	 * Returns the fully qualified name of the service which is currently being invoked;
	 * clients and services mutually associate this value in order to uniquely identify
	 * the service to be invoked.
	 * @return the service qualified name
	 */
	public QName getServiceQName();

	/**
	 * Returns true if it this ServiceContext represents client-side execution.
	 * @return true if client-side
	 */
	public boolean isClientSide();

	/**
	 * Returns true if the configuration reflects the default fallback configuration used in case of error (e.g. if no configuration
	 * matching the requests's service name can be found).
	 * @return true if the configuration is a fallback configuration
	 */
	public boolean isFallback();

	/**
	 * Returns a reference to the applicable class loader for the currently executing client or service.
	 * @return the class loader.
	 */
	public ClassLoader getClassLoader();

	/**
	 * Returns the configuration, such as request, response, and error message types, associated with a particular operation.
	 * @param name the name of the operation for which to return configuration 
	 * @return the operation-specific configuration
	 */
	public ServiceOperationDesc getOperation(String name);

	/**
	 * Returns the collection of all configuration associated with each operation for the currently invoked service.
	 * @return the map of configuration
	 */
	public Collection<ServiceOperationDesc> getAllOperations();

	/**
	 * Returns the data binding (serialization) configuration associated with a particular data binding name (e.g. "json").
	 * @param name the data binding name for which to return configuration
	 * @return the data binding configuration
	 */
	public DataBindingDesc getDataBindingDesc(String name);

	/**
	 * Returns the collection of all configuration associated with all data bindings configured for the currently invoked service.
	 * @return the data binding configuration 
	 */
	public Collection<DataBindingDesc> getAllDataBindings();

	/**
	 * Returns the collection of all type mapping data (e.g. maps associating XML namespaces to Java packages) for the currently
	 * invoked service.
	 * @return the type mappign data
	 */
	public ServiceTypeMappings getTypeMappings();

	/**
	 * Returns the service interface class name.
	 * @return the service interface class name
	 */
	public String getServiceIntfClassName();
	
	/**
	 * Returns the list of service layers.
	 * @return the list of service layers.
	 */
	public List<String> getServiceLayerNames();
}
