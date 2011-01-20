/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.CommonConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MetadataPropertyConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.SystemMetricDefs;
import org.ebayopensource.turmeric.runtime.common.impl.pipeline.NullProtocolProcessor;
import org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.Pipeline;
import org.ebayopensource.turmeric.runtime.common.service.HeaderMappingsDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceTypeMappings;


/**
 * Service Description (ServiceDesc) is an object loaded at service initialization
 * (startup time or lazy init).  ServiceDesc contains all pre-built data
 * structures necessary for the service execution. Most of the SOA framework and extension implementation
 * will not need direct access to configuration (e.g. Common/Client/ServiceConfigHolder), since the most relevant
 * information is stored and accessible in ServiceDesc as fully validated and processed information.
 * ServiceDesc has two subclasses: ClientServiceDesc and ServerServiceDesc, which extend the base class in order to
 * provide data applicable to client-side or server-side operation only.
 *
 * ServiceDesc is considered an implementation detail and should generally not be used by service writers or consumers.
 * Refer to ServiceContext for a more public representation of the most important client/service configuration and state.
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
 * <LI> List of request header mappings (map of name-to-name)
 * <LI> List of response header mappings (map of name-to-name)
 * <UL>
 *
 * @author ichernyshev
 */
public abstract class ServiceDesc {
	private final ServiceId m_id;
	private final QName m_serviceQName;
	private final CommonConfigHolder m_config;
	private final Pipeline m_requestPipeline;
	private final Pipeline m_responsePipeline;
	private final Dispatcher m_requestDispatcher;
	private final Dispatcher m_responseDispatcher;
	private final Map<String,ServiceOperationDesc> m_operations;
	private final Map<String,ProtocolProcessorDesc> m_protocols;
	private final Map<String,DataBindingDesc> m_bindings;
	private final ServiceTypeMappings m_typeMappings;
	private final ClassLoader m_classLoader;
	private final List<LoggingHandler> m_loggingHandlers;
	private final Class m_serviceInterfaceClass;
	private final ProtocolProcessorDesc m_nullProtocolProcessor;
	private final List<String> m_serviceLayers;
	private final ServiceMetricHolder m_metrics;
	private final HeaderMappingsDesc m_requestHeaderMappings;
	private final HeaderMappingsDesc m_responseHeaderMappings;
	private final ErrorDataProvider m_errorDataProviderClass;
	
	protected ServiceDesc(ServiceDesc descToCopy, ServiceId id,
		QName serviceQName,
		CommonConfigHolder config,
		Pipeline requestPipeline,
		Pipeline responsePipeline,
		Dispatcher requestDispatcher,
		Dispatcher responseDispatcher,
		Map<String,ServiceOperationDesc> operations,
		Map<String,ProtocolProcessorDesc> protocols,
		Map<String,DataBindingDesc> bindings,
		ServiceTypeMappings typeMappings,
		ClassLoader classLoader,
		List<LoggingHandler> loggingHandlers,
		Class serviceInterfaceClass,
		List<String> serviceLayers,
		HeaderMappingsDesc requestHeaderMappings,
		HeaderMappingsDesc responseHeaderMappings,
		ErrorDataProvider errorDataProviderClas){
		
		this(
				id = id == null ? descToCopy.m_id : id,
				serviceQName = serviceQName == null ? descToCopy.m_serviceQName
						: serviceQName,
				config = config == null ? descToCopy.m_config : config,
				requestPipeline = requestPipeline == null ? descToCopy.m_requestPipeline
						: requestPipeline,
				responsePipeline = responsePipeline == null ? descToCopy.m_responsePipeline
						: responsePipeline,
				requestDispatcher = requestDispatcher == null ? descToCopy.m_requestDispatcher
						: requestDispatcher,
				responseDispatcher = responseDispatcher == null ? descToCopy.m_responseDispatcher
						: responseDispatcher,
				operations = operations == null ? descToCopy.m_operations
						: operations,
				protocols = protocols == null ? descToCopy.m_protocols
						: protocols,
				bindings = bindings == null ? descToCopy.m_bindings : bindings,
				typeMappings = typeMappings == null ? descToCopy.m_typeMappings
						: typeMappings,
				classLoader = classLoader == null ? descToCopy.m_classLoader
						: classLoader,
				loggingHandlers = loggingHandlers == null ? descToCopy.m_loggingHandlers
						: loggingHandlers,
				serviceInterfaceClass = serviceInterfaceClass == null ? descToCopy.m_serviceInterfaceClass
						: serviceInterfaceClass,
				serviceLayers = serviceLayers == null ? descToCopy.m_serviceLayers
						: serviceLayers,
				requestHeaderMappings = requestHeaderMappings == null ? descToCopy.m_requestHeaderMappings
						: requestHeaderMappings,
				responseHeaderMappings = responseHeaderMappings == null ? descToCopy.m_responseHeaderMappings
						: responseHeaderMappings,
				errorDataProviderClas = errorDataProviderClas == null ? descToCopy.m_errorDataProviderClass
						: errorDataProviderClas);		
	}

	/**
	 * Constructor. Intended to be called only by the internal subclasses.
	 * 
	 * @param id
	 * @param serviceQName
	 * @param config
	 * @param requestPipeline
	 * @param responsePipeline
	 * @param requestDispatcher
	 * @param responseDispatcher
	 * @param operations
	 * @param protocols
	 * @param bindings
	 * @param typeMappings
	 * @param classLoader
	 * @param loggingHandlers
	 * @param serviceInterfaceClass
	 */
	protected ServiceDesc(ServiceId id,
		QName serviceQName,
		CommonConfigHolder config,
		Pipeline requestPipeline,
		Pipeline responsePipeline,
		Dispatcher requestDispatcher,
		Dispatcher responseDispatcher,
		Map<String,ServiceOperationDesc> operations,
		Map<String,ProtocolProcessorDesc> protocols,
		Map<String,DataBindingDesc> bindings,
		ServiceTypeMappings typeMappings,
		ClassLoader classLoader,
		List<LoggingHandler> loggingHandlers,
		Class serviceInterfaceClass,
		List<String> serviceLayers,
		HeaderMappingsDesc requestHeaderMappings,
		HeaderMappingsDesc responseHeaderMappings,
		ErrorDataProvider errorDataProviderClass)
	{
		if (id == null || serviceQName == null ||
			requestPipeline == null || responsePipeline == null ||
			requestDispatcher == null || responseDispatcher == null ||
			protocols == null || bindings == null || operations == null ||
			loggingHandlers == null || classLoader == null ||
			typeMappings == null) 
		{
			throw new NullPointerException();
		}

		m_id = id;
		m_serviceQName = serviceQName;
		m_config = config;
		m_requestPipeline = requestPipeline;
		m_responsePipeline = responsePipeline;
		m_requestDispatcher = requestDispatcher;
		m_responseDispatcher = responseDispatcher;
		m_operations = operations;
		m_protocols = Collections.unmodifiableMap(protocols);
		m_bindings = bindings;
		m_typeMappings = typeMappings;
		m_classLoader = classLoader;
		m_loggingHandlers = Collections.unmodifiableList(
			new ArrayList<LoggingHandler>(loggingHandlers));
		m_serviceInterfaceClass = serviceInterfaceClass;
		m_serviceLayers = serviceLayers;

		m_nullProtocolProcessor = new ProtocolProcessorDesc("{null}", new NullProtocolProcessor(), null);

		((ServiceTypeMappingsImpl)typeMappings).cleanTypeDefsBuilder();

		m_metrics = new ServiceMetricHolder(id, null);
		m_requestHeaderMappings = requestHeaderMappings;
		m_responseHeaderMappings = responseHeaderMappings;
		m_errorDataProviderClass = errorDataProviderClass; 
	}

	/**
	 * Returns the administrative name of the service whose invocation is currently in process for this message context.
	 * On the client side, the administrative name is the local part of the service qualified name configured in ClientConfig.xml.
	 * On the server side, the administrative name matches the folder name holding the ServiceConfig.xml file.
	 * @return the administrative name
	 */
	public final String getAdminName() {
		return m_id.getAdminName();
	}

	/**
	 * Returns the fully qualified name of the service which is currently being invoked; clients and services mutually associate
	 * this value in order to uniquely identify the service to be invoked.
	 * @return the service qualified name
	 */
	public final QName getServiceQName() {
		return m_serviceQName;
	}

	/**
	 * Returns a client/service identifier associated with a local configuration instance.  This identifier consists of
	 * the administrative name of the service being consumed or provided, plus any sub-identification such as the consuming
	 * client (configuration) instance.
	 * @return the service identifier
	 */
	public ServiceId getServiceId() {
		return m_id;
	}

	/**
	 * Returns true if the configuration reflects the default fallback configuration used in case of error (e.g. if no configuration
	 * matching the requests's service name can be found).
	 * @return true if the configuration reflects the default fallback configuration
	 */
	public final boolean isFallback() {
		return (m_config == null);
	}

	/**
	 * Throw an exception if there is no underlying configuration, i.e. if any stored configuration reflects the
	 * default fallback configuration used in case of error.
	 */
	protected final void checkNotFallback() {
		if (m_config == null) {
			throw new IllegalStateException(
				"This is a fallback ServiceDesc and Config is not available here");
		}
	}

	/**
	 * Returns a reference to an object representing the low-level source configuration for this client or service.
	 * @return the configuration reference
	 */
	public final CommonConfigHolder getConfig() {
		checkNotFallback();
		return m_config;
	}

	/**
	 * Returns a reference to the applicable class loader for the currently executing client or service.
	 * @return the class loader
	 */
	public final ClassLoader getClassLoader() {
		return m_classLoader;
	}

	/**
	 * Returns the current request pipeline instance.
	 * @return the request pipeline
	 */
	public final Pipeline getRequestPipeline() {
		return m_requestPipeline;
	}

	/**
	 * Returns the current response pipeline instance.
	 * @return the response pipeline
	 */
	public final Pipeline getResponsePipeline() {
		return m_responsePipeline;
	}
	
	/**
	 * Returns the current response pipeline instance.
	 * @return the response pipeline
	 */
	public final ErrorDataProvider getErrorDataProviderClass() {
		return m_errorDataProviderClass;
	}	

	/**
	 * Returns the current request dispatcher instance.
	 * @return the request dispatcher
	 */
	public final Dispatcher getRequestDispatcher() {
		return m_requestDispatcher;
	}

	/**
	 * Returns the current response dispatcher instance.
	 * @return the response dispatcher
	 */
	public final Dispatcher getResponseDispatcher() {
		return m_responseDispatcher;
	}

	/**
	 * Returns the current list of logging handlers for the service.
	 * @return the list of logging handlers
	 */
	public final List<LoggingHandler> getLoggingHandlers() {
		return m_loggingHandlers;
	}

	/**
	 * Returns the Class representing the service interface.
	 * @return the service interface class
	 */
	public final Class getServiceInterfaceClass() {
		checkNotFallback();
		return m_serviceInterfaceClass;
	}

	/**
	 * Get the configuration, such as request, response, and error message types, associated with a particular operation.
	 * @param name the name of the operation for which to return configuration
	 * @return the operation-specific configuration
	 */
	public final ServiceOperationDesc getOperation(String name) {
		return m_operations.get(name);
	}

	/**
	 * Get the collection of all configuration associated with each operation for the currently invoked service.
	 * @return the map of configuration
	 */
	public final Collection<ServiceOperationDesc> getAllOperations() {
		return Collections.unmodifiableCollection(m_operations.values());
	}

	/**
	 * Get the protocol processor configuration associated with the specified name.
	 * @param name the message protocol name (e.g. "SOAP11")
	 * @return the protocol processor configuration
	 */
	public final ProtocolProcessorDesc getProtocolProcessor(String name) {
		return m_protocols.get(name);
	}

	/**
	 * Get the protocol processors
	 */
	public final Collection<ProtocolProcessorDesc> getAllProtocolProcessors() {
		return m_protocols.values();
	}

	/**
	 * Get the data binding (serialization) configuration associated with a particular data binding name (e.g. "json").
	 * @param name the data binding name for which to return configuration
	 * @return the data binding configuration
	 */
	public final DataBindingDesc getDataBindingDesc(String name) {
		return m_bindings.get(name);
	}
	
	/**
	 * @return the list of service layers
	 */
	public List<String> getServiceLayerNames() {
		return m_serviceLayers;
	}

	/**
	 * Returnes the collection of all configuration associated with all data bindings configured for the currently invoked service.
	 * @return the data bindings
	 */
	public final Collection<DataBindingDesc> getAllDataBindings() {
		return Collections.unmodifiableCollection(m_bindings.values());
	}

	/**
	 * Returnes the configuration for the default protocol processor (which takes no action at any pipeline processing stage).
	 * @return the default ProtocolProcessorDesc
	 */
	public final ProtocolProcessorDesc getNullProtocolProcessor() {
		return m_nullProtocolProcessor;
	}

	/**
	 * Returns the collection of all type mapping data (e.g. maps associating XML namespaces to Java packages) for the currently
	 * invoked service.
	 * @return the type mappings
	 */
	public final ServiceTypeMappings getTypeMappings() {
		return m_typeMappings;
	}

	/**
	 * Updates a given metric, never throws exceptions
	 */
	public void updateMetric(MessageContext ctx, SystemMetricDefs.SvcLevelMetricDef def, long count) {
		if (!isFallback()) {
			m_metrics.update(ctx, def, count);
		}
	}

	/**
	 * @return the Request Header Mappings
	 */
	public HeaderMappingsDesc getRequestHeaderMappings() {
		return m_requestHeaderMappings;
	}

	/**
	 * @return the Response Header Mappings
	 */
	public HeaderMappingsDesc getResponseHeaderMappings() {
		return m_responseHeaderMappings;
	}
	
	/**
	 * Returns the Canonical form of the Service if the 
	 * meta data holder has been loaded, serviceQName Otherwise.
	 * @return String, the canonical service name.
	 */
	public String getCanonicalServiceName() {		
		if (m_config != null) {
			StringBuilder serviceName = new StringBuilder();
			MetadataPropertyConfigHolder metadata = m_config.getMetaData();
			if (metadata != null) {
				serviceName.append("( ");
				String namespace = metadata.getServiceNamespace();
				// Pre 2.4 service, get the namespace from serviceQname
				if (namespace == null) {
					namespace = m_serviceQName.getNamespaceURI();
				}
				serviceName.append(namespace).append(", ");
				serviceName.append(metadata.getServiceName()).append(", ");
				serviceName.append(metadata.getVersion()).append(" )");
				return serviceName.toString();
			}
		}
		// Handle Fallback ServiceDesc 
		return m_serviceQName.toString();
	}
}
