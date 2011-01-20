/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.service;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyDesc;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.RequestPatternMatcher;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceDesc;
import org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.Pipeline;
import org.ebayopensource.turmeric.runtime.common.service.HeaderMappingsDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceTypeMappings;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.OperationMappings;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigHolder;
import org.ebayopensource.turmeric.runtime.spf.impl.service.GlobalIdDesc;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ErrorMapper;
import org.ebayopensource.turmeric.runtime.spf.pipeline.VersionCheckHandler;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;


/**
 * @author ichernyshev
 */
public final class ServerServiceDesc extends ServiceDesc {

	private final RequestPatternMatcher<ServiceOperationDesc> m_operationMatcher;
	private final RequestPatternMatcher<ProtocolProcessorDesc> m_protocolMatcher;
	private final RequestPatternMatcher<DataBindingDesc> m_bindingMatcherRequest;
	private final RequestPatternMatcher<DataBindingDesc> m_bindingMatcherResponse;
	private final String m_serviceImplClassName;
	private final ErrorMapper m_errorMapper;
	private final Map<String, GlobalIdDesc> m_globalIdMap;
	private final VersionCheckHandler m_versionCheckHandler;
	private final Charset m_serviceCharset;
	private final UrlMappingsDesc m_urlMappings;
	private final Map<String, Map<String, String>> m_authenticationOperationMap;
	private final DataBindingDesc m_defaultRequestBinding;
	private final DataBindingDesc m_defaultResponseBinding;
	private final CachePolicyDesc m_cachePolicyDesc;
	private OperationMappings m_operationMappings;

	public ServerServiceDesc(ServerServiceId id,
		QName serviceQName,
		ServiceConfigHolder config,
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
		RequestPatternMatcher<ServiceOperationDesc> operationMatcher,
		RequestPatternMatcher<ProtocolProcessorDesc> protocolMatcher,
		RequestPatternMatcher<DataBindingDesc> bindingMatcherRequest,
		RequestPatternMatcher<DataBindingDesc> bindingMatcherResponse,
		String serviceImplClassName,
		ErrorMapper errorMapper,
		ErrorDataProvider errorDataProviderClass,
		Map<String, GlobalIdDesc> globalIdMap,
		VersionCheckHandler versionCheckHandler,
		Charset serviceCharset,
		UrlMappingsDesc urlMappings,
		OperationMappings operationMappings,
		HeaderMappingsDesc requestHeaderMappings,
		HeaderMappingsDesc responseHeaderMappings,
		Map<String, Map<String, String>> authenticationOperations,
		DataBindingDesc defaultRequestBinding,
		DataBindingDesc defaultResponseBinding,
		List<String> serviceLayers,
		CachePolicyDesc cachePolicyDesc
		)
	{
		super(id, serviceQName, config, requestPipeline, responsePipeline,
			requestDispatcher, responseDispatcher,
			operations, protocols, bindings,
			typeMappings, classLoader,
			loggingHandlers,
			serviceInterfaceClass,
			serviceLayers,
			requestHeaderMappings,
			responseHeaderMappings,
			errorDataProviderClass);

		if (operationMatcher == null || protocolMatcher == null ||
			bindingMatcherRequest == null || bindingMatcherResponse == null ||
			errorMapper == null || globalIdMap == null ||
			versionCheckHandler == null ||
			authenticationOperations == null)
		{
			throw new NullPointerException();
		}

		if (config != null) {
			// check properties that can be null in fallback scenario
			if (serviceImplClassName == null) {
				throw new NullPointerException();
			}
		}

		m_operationMatcher = operationMatcher;
		m_protocolMatcher = protocolMatcher;
		m_bindingMatcherRequest = bindingMatcherRequest;
		m_bindingMatcherResponse = bindingMatcherResponse;
		m_serviceImplClassName = serviceImplClassName;
		m_errorMapper = errorMapper;
		m_globalIdMap = Collections.unmodifiableMap(globalIdMap);
		m_versionCheckHandler = versionCheckHandler;
		m_serviceCharset = serviceCharset;
		m_urlMappings = urlMappings;
		m_operationMappings = operationMappings;
		m_authenticationOperationMap = authenticationOperations;
		m_defaultRequestBinding = defaultRequestBinding;
		m_defaultResponseBinding = defaultResponseBinding;
		m_cachePolicyDesc = cachePolicyDesc;

	}

	@Override
	public ServerServiceId getServiceId() {
		return (ServerServiceId)super.getServiceId();
	}

	public String getServiceImplClassName() {
		checkNotFallback();
		return m_serviceImplClassName;
	}

	public ErrorMapper getErrorMapper() {
		return m_errorMapper;
	}

	public ServiceOperationDesc lookupOperation(String uri, Map<String, String> headers)
	{
		return m_operationMatcher.findTarget(uri, headers);
	}

	public ProtocolProcessorDesc lookupProtocolProcessor(String uri, Map<String, String> headers)
	{
		return m_protocolMatcher.findTarget(uri, headers);
	}

	public DataBindingDesc lookupDataBindingForRequest(String uri, Map<String, String> headers)
	{
		//return m_bindingMatcherRequest.findTarget(uri, headers);
		DataBindingDesc dataBindingDesc = m_bindingMatcherRequest.findTarget(uri, headers);
		if (dataBindingDesc == null) {
			dataBindingDesc = m_defaultRequestBinding;
		}

		return dataBindingDesc;
	}

	public DataBindingDesc lookupDataBindingForResponse(String uri, Map<String, String> headers)
	{
		// return m_bindingMatcherResponse.findTarget(uri, headers);
		DataBindingDesc dataBindingDesc = m_bindingMatcherResponse.findTarget(uri, headers);
		if (dataBindingDesc == null) {
			dataBindingDesc = m_defaultResponseBinding;
		}

		return dataBindingDesc;
	}

	public Collection<GlobalIdDesc> getGlobalIds() {
		return m_globalIdMap.values();
	}

	public GlobalIdDesc getGlobalId(String globalId) {
		return m_globalIdMap.get(globalId);
	}

	public VersionCheckHandler getVersionCheckHandler() {
		return m_versionCheckHandler;
	}

	// Can be null - no default-encoding given in config - this means we
	// use the encoding given in the client request.
	public Charset getServiceCharset() {
		return m_serviceCharset;
	}

	/**
	 * @return the m_urlMappings
	 */
	public UrlMappingsDesc getUrlMappings() {
		return m_urlMappings;
	}

	public OperationMappings getOperationMappings() {
		return m_operationMappings;
	}
	
	/**
	 * @return the m_defaultRequestBinding
	 */
	public DataBindingDesc getDefaultRequestBinding() {
		return m_defaultRequestBinding;
	}

	/**
	 * @return the m_defaultResponseBinding
	 */
	public DataBindingDesc getDefaultResponseBinding() {
		return m_defaultResponseBinding;
	}
	
	/**
	 * @return the m_cachePolicyDesc
	 */
	public CachePolicyDesc getCachePolicyDesc() {
		return m_cachePolicyDesc;
	}

	/**
	 * Returns the authentication handler option map (String name-value pairs) for the operation with the specified
	 * name. If no option map is configured for the specified name, but a map is configured for the wildcard value "*", the
	 * wildcard map is returned.  If neither the specified name nor the wildcard value is configured, null is returned.
	 * @param opName the name of the operation whose authentication option map is being queried.
	 * @return the option hashmap, or null if no map is configured either for the specific name nor the wildcard value "*"
	 */
	public Map<String, String> getAuthenticationOptions(String opName) {
		Map<String, String> authenticationOptions = m_authenticationOperationMap.get(opName);
		if (authenticationOptions == null) {
			authenticationOptions = m_authenticationOperationMap.get(SOAConstants.SECURITY_OPERATION_WILDCARD);
		}
		if (authenticationOptions == null) {
			return null;
		}
		return Collections.unmodifiableMap(authenticationOptions);
	}
}
