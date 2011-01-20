/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.sif.impl.internal.service;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheProvider;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ServiceNameUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ReflectionUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.Pipeline;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.service.HeaderMappingsDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceTypeMappings;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigHolder;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ApplicationRetryHandler;
import org.ebayopensource.turmeric.runtime.sif.pipeline.AutoMarkdownStateFactory;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ErrorResponseAdapter;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;

public final class ClientServiceDesc extends ServiceDesc {

	private final String m_clientName;
	private final Map<String,Transport> m_transports;
	private final DataBindingDesc m_defRequestDataBinding;
	private final DataBindingDesc m_defResponseDataBinding;
	private final DataBindingDesc m_defRestRequestDataBinding;
	private final DataBindingDesc m_defRestResponseDataBinding;
	private final String m_defTransportName;
	private final Transport m_defTransport;
	private final G11nOptions m_g11nOptions;
	private final String m_messageProtocolName;
	private final String m_responseTransport;
	private final Integer m_appLevelNumRetries;
	private final String m_serviceVersion;
	private final String m_useCase;
	private final String m_consumerId;
	private final URL m_defServiceLocationURL;
	private final ApplicationRetryHandler m_retryHandler;
	private final ErrorResponseAdapter m_customErrorResponseAdapter;
	private final CacheProvider m_cacheProviderClass;
	private final Boolean m_disableCacheOnLocal;
	private final Boolean m_skipCacheOnError;
	private final AutoMarkdownStateFactory m_autoMarkdownStateFactory;
	private final String m_urlPathInfo;
	private Class m_proxyClass;
	
//	public ClientServiceDesc(ClientServiceDesc desc, ClientServiceId id, QName serviceQName, ServiceTypeMappings typeMappings){
//		
//	}
	
	public ClientServiceDesc(ClientServiceDesc descToCopy, ClientServiceId id,
			QName serviceQName,
			ClientConfigHolder config,
			Pipeline requestPipeline,
			Pipeline responsePipeline,
			Dispatcher requestDispatcher,
			Dispatcher responseDispatcher,
			Map<String,ServiceOperationDesc> operations,
			Map<String,ProtocolProcessorDesc> protocols,
			Map<String,DataBindingDesc> bindings,
			Map<String,Transport> transports,
			ServiceTypeMappings typeMappings,
			ClassLoader classLoader,
			G11nOptions g11nOptions,
			List<LoggingHandler> loggingHandlers,
			Class serviceInterfaceClass, 
			DataBindingDesc defRequestDataBinding,
			DataBindingDesc defResponseDataBinding,
			String defTransportName,
			Transport defTransport,
			URL defServiceLocationURL,
			String serviceVersion,
			ApplicationRetryHandler retryHandler,
			ErrorResponseAdapter customErrorResponseAdapter,
			ErrorDataProvider errorDataProviderClass,
			CacheProvider cacheProviderClass,
			AutoMarkdownStateFactory autoMarkdownStateFactory,
			DataBindingDesc defRestRequestDataBinding,
			DataBindingDesc defRestResponseDataBinding,
			HeaderMappingsDesc requestHeaderMappings,
			HeaderMappingsDesc responseHeaderMappings,
			List<String> serviceLayers,
			String urlPathInfo
			) {
		super(descToCopy, id, serviceQName, config, requestPipeline, responsePipeline,
				requestDispatcher, responseDispatcher,
				operations, protocols, bindings,
				typeMappings, classLoader,
				loggingHandlers,
				serviceInterfaceClass,
				serviceLayers,
				requestHeaderMappings,
				responseHeaderMappings,
				errorDataProviderClass);
		
		config = config == null ? (ClientConfigHolder) descToCopy.getConfig()
				: config;
		id = id == null ? descToCopy.getServiceId() : id;
		transports = transports == null ? descToCopy.m_transports : transports;
		defRequestDataBinding = defRequestDataBinding == null ? descToCopy.m_defRequestDataBinding
				: defRequestDataBinding;
		defResponseDataBinding = defResponseDataBinding == null ? descToCopy.m_defResponseDataBinding
				: defResponseDataBinding;
		defRestRequestDataBinding = defRestRequestDataBinding == null ? descToCopy.m_defRestRequestDataBinding
				: defRestRequestDataBinding;
		defRestResponseDataBinding = defRestResponseDataBinding == null ? descToCopy.m_defRestResponseDataBinding
				: defRestResponseDataBinding;
		defTransportName = defTransportName == null ? descToCopy.m_defTransportName
				: defTransportName;
		defTransport = defTransport == null ? descToCopy.m_defTransport
				: defTransport;
		g11nOptions = g11nOptions == null ? descToCopy.m_g11nOptions
				: g11nOptions;
		serviceVersion = serviceVersion == null ? descToCopy.m_serviceVersion
				: serviceVersion;
		defServiceLocationURL = defServiceLocationURL == null ? descToCopy.m_defServiceLocationURL
				: defServiceLocationURL;
		retryHandler = retryHandler == null ? descToCopy.m_retryHandler
				: retryHandler;
		customErrorResponseAdapter = customErrorResponseAdapter == null ? descToCopy.m_customErrorResponseAdapter
				: customErrorResponseAdapter;
		autoMarkdownStateFactory = autoMarkdownStateFactory == null ? descToCopy.m_autoMarkdownStateFactory
				: autoMarkdownStateFactory;
		urlPathInfo = urlPathInfo == null ? descToCopy.m_urlPathInfo
				: urlPathInfo;
		cacheProviderClass = cacheProviderClass == null ? descToCopy.m_cacheProviderClass
				: cacheProviderClass;
		
		
		if (config == null || defTransportName == null || defTransport == null
				|| g11nOptions == null || retryHandler == null
				|| transports == null || defRequestDataBinding == null
				|| defRestResponseDataBinding == null
				|| defRestRequestDataBinding == null) {
			// checking config, do not allow fallback ServiceDesc on the client
			// side
			throw new NullPointerException();
		}
		
		m_clientName = id.getClientName();
		m_transports = transports == null ? descToCopy.m_transports : transports;
		m_defRequestDataBinding = defRequestDataBinding;				// required
		m_defResponseDataBinding = defResponseDataBinding;				// null OK
		m_defRestRequestDataBinding = defRestRequestDataBinding;		// required
		m_defRestResponseDataBinding = defRestResponseDataBinding;		// required
		m_defTransportName = defTransportName;
		m_defTransport = defTransport;
		m_g11nOptions = g11nOptions;
		m_messageProtocolName = config.getMessageProtocol();			// null OK
		m_responseTransport = config.getResponseTransport();			// null OK
		m_appLevelNumRetries = config.getAppLevelNumRetries();			// null OK
		m_serviceVersion = serviceVersion;								// null OK
		m_useCase = config.getInvocationUseCase();						// null OK
		m_consumerId = config.getConsumerId();                          // null OK
		m_defServiceLocationURL = defServiceLocationURL;				// null OK
		m_retryHandler = retryHandler;
		m_customErrorResponseAdapter = customErrorResponseAdapter;		// null OK
		m_autoMarkdownStateFactory = autoMarkdownStateFactory;			// null OK
		m_urlPathInfo = urlPathInfo;
		m_cacheProviderClass = cacheProviderClass;
		m_disableCacheOnLocal = config.isCacheDisabledOnLocal();
		m_skipCacheOnError = config.isSkipCacheOnError();


	}

	public ClientServiceDesc(ClientServiceId id,
		QName serviceQName,
		ClientConfigHolder config,
		Pipeline requestPipeline,
		Pipeline responsePipeline,
		Dispatcher requestDispatcher,
		Dispatcher responseDispatcher,
		Map<String,ServiceOperationDesc> operations,
		Map<String,ProtocolProcessorDesc> protocols,
		Map<String,DataBindingDesc> bindings,
		Map<String,Transport> transports,
		ServiceTypeMappings typeMappings,
		ClassLoader classLoader,
		G11nOptions g11nOptions,
		List<LoggingHandler> loggingHandlers,
		Class serviceInterfaceClass, 
		DataBindingDesc defRequestDataBinding,
		DataBindingDesc defResponseDataBinding,
		String defTransportName,
		Transport defTransport,
		URL defServiceLocationURL,
		String serviceVersion,
		ApplicationRetryHandler retryHandler,
		ErrorResponseAdapter customErrorResponseAdapter,
		ErrorDataProvider errorDataProviderClass,
		CacheProvider cacheProviderClass,
		AutoMarkdownStateFactory autoMarkdownStateFactory,
		DataBindingDesc defRestRequestDataBinding,
		DataBindingDesc defRestResponseDataBinding,
		HeaderMappingsDesc requestHeaderMappings,
		HeaderMappingsDesc responseHeaderMappings,
		List<String> serviceLayers,
		String urlPathInfo
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

		if (config == null || defTransportName == null || defTransport == null ||
			g11nOptions == null || retryHandler == null || transports == null ||
			defRequestDataBinding == null || defRestResponseDataBinding == null ||
			defRestRequestDataBinding == null)
		{
			// checking config, do not allow fallback ServiceDesc on the client side
			throw new NullPointerException();
		}

		m_clientName = id.getClientName();
		m_transports = transports;
		m_defRequestDataBinding = defRequestDataBinding;				// required
		m_defResponseDataBinding = defResponseDataBinding;				// null OK
		m_defRestRequestDataBinding = defRestRequestDataBinding;		// required
		m_defRestResponseDataBinding = defRestResponseDataBinding;		// required
		m_defTransportName = defTransportName;
		m_defTransport = defTransport;
		m_g11nOptions = g11nOptions;
		m_messageProtocolName = config.getMessageProtocol();			// null OK
		m_responseTransport = config.getResponseTransport();			// null OK
		m_appLevelNumRetries = config.getAppLevelNumRetries();			// null OK
		m_serviceVersion = serviceVersion;								// null OK
		m_useCase = config.getInvocationUseCase();						// null OK
		m_consumerId = config.getConsumerId();                          // null OK
		m_defServiceLocationURL = defServiceLocationURL;				// null OK
		m_retryHandler = retryHandler;
		m_customErrorResponseAdapter = customErrorResponseAdapter;		// null OK
		m_autoMarkdownStateFactory = autoMarkdownStateFactory;			// null OK
		m_urlPathInfo = urlPathInfo;
		m_cacheProviderClass = cacheProviderClass;
		m_disableCacheOnLocal = config.isCacheDisabledOnLocal();
		m_skipCacheOnError = config.isSkipCacheOnError();
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.impl.service.ServiceDesc#getServiceId()
	 */
	@Override
	public ClientServiceId getServiceId() {
		return (ClientServiceId)super.getServiceId();
	}

	/**
	 * @return
	 */
	public String getClientName() {
		return m_clientName;
	}

	/**
	 * Get the transport instance for the specified name.
	 * @param name the name of the transport
	 * @return the transport
	 */
	public Transport getTransport(String name) {
		return m_transports.get(name);
	}

	/**
	 * @return
	 */
	public DataBindingDesc getDefRequestDataBinding() {
		return m_defRequestDataBinding;
	}

	/**
	 * @return
	 */
	public DataBindingDesc getDefResponseDataBinding() {
		return m_defResponseDataBinding;
	}

	public DataBindingDesc getDefRestRequestDataBinding() {
		return m_defRestRequestDataBinding;
	}

	public DataBindingDesc getDefRestResponseDataBinding() {
		return m_defRestResponseDataBinding;
	}

	/**
	 * @return
	 */
	public String getDefTransportName() {
		return m_defTransportName;
	}

	/**
	 * @return
	 */
	public Transport getDefTransport() {
		return m_defTransport;
	}

	/**
	 * @return
	 */
	public URL getDefServiceLocationURL() {
		return m_defServiceLocationURL;
	}

	/**
	 * @return
	 */
	public G11nOptions getG11nOptions() {
		return m_g11nOptions;
	}

	/**
	 * @return
	 */
	public String getMessageProtocolName() {
		return m_messageProtocolName;
	}

	/**
	 * @return
	 */
	public String getResponseTransport() {
		return m_responseTransport;
	}

	/**
	 * The application retry feature allows selective retry of certain configured errors, a
	 * configured number of times per request.  Maximum total try count will be retry count plus one.
	 * @return the configured number of application retries to attempt; may be overridden by a value
	 * in ServiceInvokerOptions.
	 */
	public Integer getAppLevelNumRetries() {
		return m_appLevelNumRetries;
	}

	/**
	 * @return the service version in use by this client, the version that will be specified in invocation
	 * requests.
	 */
	public String getServiceVersion() {
		return m_serviceVersion;
	}

	/**
	 * @return the configured use case that will be passed in client requests.  May be overridden by a value
	 * in ServiceInvokerOptions.
	 */
	public String getUseCase() {
		return m_useCase;
	}
	
	/**
	 * @return the configured consumerId that will be passed in client requests.  May be overridden by a value
	 * in ServiceInvokerOptions.
	 */
	public String getConsumerId() {
		return m_consumerId;
	}

	/**
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public Class getProxyClass() throws ServiceException {
		if (m_proxyClass == null) {
			Class intfClass = getServiceInterfaceClass();
			if (intfClass == null) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_UNDEFINED_INTF_CLASS_NAME,
						ErrorConstants.ERRORDOMAIN));
			}
			String className = ServiceNameUtils.getServiceProxyClassName(getAdminName(), intfClass.getName());
			m_proxyClass = ReflectionUtils.loadClass(className, intfClass, getClassLoader());
		}
		return m_proxyClass;
	}

	public ApplicationRetryHandler getRetryHandler() {
		return m_retryHandler;
	}

	public ErrorResponseAdapter getCustomErrorResponseAdapter() {
		return m_customErrorResponseAdapter;
	}

	public AutoMarkdownStateFactory getAutoMarkdownStateFactory() {
		return m_autoMarkdownStateFactory;
	}

	public String getUrlPathInfo() {
		return m_urlPathInfo;
	}
	
	public CacheProvider getCacheProviderClass() {
		return m_cacheProviderClass;
	}
	
	public Boolean isCacheDisabledOnLocal() {
		return m_disableCacheOnLocal;
	}
	
	public Boolean isSkipCacheOnError() {
		return m_skipCacheOnError;
	}
}
