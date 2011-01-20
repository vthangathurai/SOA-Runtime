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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyDesc;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.g11n.GlobalIdEntry;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.FeatureIndicatorConfig;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MessageProcessorConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.NameValue;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.OptionList;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.ProtocolProcessorConfig;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.TypeMappingConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.g11n.GlobalRegistryConfigManager;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.PipelineInitContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.BaseServiceDescFactory;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.RequestPatternMatcher;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceOperationDescImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ServiceNameUtils;
import org.ebayopensource.turmeric.runtime.common.impl.pipeline.PipelineImpl;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ReflectionUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.Pipeline;
import org.ebayopensource.turmeric.runtime.common.pipeline.PipelineMode;
import org.ebayopensource.turmeric.runtime.common.service.HeaderMappingsDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceTypeMappings;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.OperationMappings;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.OperationSecurityConfig;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.SecurityPolicyConfigHolder;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigHolder;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.markdown.SOAServerMarkdownStateManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.BaseServiceRequestDispatcher;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ErrorMapperInitContextImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.VersionCheckHandlerInitContextImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.DefaultErrorMapperImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.NullVersionCheckHandler;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.ServerLoggingHandler;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.SimpleInvokerDispatcher;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.SimpleServerResponseDispatcher;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.SimpleVersionCheckHandler;
import org.ebayopensource.turmeric.runtime.spf.impl.service.GlobalIdDesc;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ErrorMapper;
import org.ebayopensource.turmeric.runtime.spf.pipeline.VersionCheckHandler;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;

import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;

/**
 * @author ichernyshev
 */
public final class ServerServiceDescFactory extends BaseServiceDescFactory<ServerServiceDesc> {

	private static Logger LOGGER = Logger.getInstance( ServerServiceDescFactory.class );
	private boolean m_isCompStatusInitialized;

	private static ServerServiceDescFactory s_instance = new ServerServiceDescFactory();

	private ServerServiceDescFactory() {
		super("ServerServiceDescFactory", false, true);
	}

	public static ServerServiceDescFactory getInstance() {
		return s_instance;
	}

	/**
	 * Returns a ServiceDesc, attempting to load if necessary
	 */
	public final ServerServiceDesc getServiceDesc(String adminName) throws ServiceException {
		return getServiceDesc(ServerServiceId.newInstance(adminName));
	}

	/**
	 * Reloads ServiceDesc to reflect configuration change
	 */
	public final void reloadServiceDesc(String adminName) throws ServiceException {
		reloadServiceDesc(ServerServiceId.newInstance(adminName));
	}

	@Override
	protected void postServiceDescLoad(ServerServiceDesc svcDesc) {
		SOAServerMarkdownStateManager.getInstance().addServiceStates(svcDesc);
	}

	@Override
	protected ServerServiceDesc createServiceDesc(ServiceId id, boolean rawMode) throws ServiceException {
		return createServiceDesc(id);
	}

	@Override
	protected ServerServiceDesc createServiceDesc(ServiceId id) throws ServiceException {
		ServerServiceId serverSvcId = (ServerServiceId)id;
		String adminName = id.getAdminName();
		ServiceConfigHolder config = ServiceConfigManager.getInstance().getConfig(adminName);
		QName serviceQName = config.getServiceQName();

		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		MessageProcessorConfigHolder processorConfig = config.getMessageProcessorConfig();
		TypeMappingConfigHolder typeMappingsCfg = config.getTypeMappings();

		Pipeline requestPipeline = createPipeline(id, PipelineMode.REQUEST, processorConfig, cl);
		Pipeline responsePipeline = createPipeline(id, PipelineMode.RESPONSE, processorConfig, cl);

		Class serviceInterfaceClass = loadServiceInterfaceClass(config, true, cl);

		ServiceTypeMappings typeMappings = createTypeMappings(id, typeMappingsCfg, serviceInterfaceClass, cl);

		Collection<String> unsupportedOperations = config.getUnsupportedOperation();
		Map<String,ServiceOperationDesc> operations = createOperations(id,
			typeMappingsCfg, unsupportedOperations, typeMappings, serviceQName, cl, config.getOperationProperties());
		RequestPatternMatcher<ServiceOperationDesc> operationMatcher =
			createOperationsMatcher(operations.values());

		Map<String,ProtocolProcessorDesc> protocols = createProtocolProcessors(serverSvcId,
			processorConfig.getProtocolProcessors(), cl);
		RequestPatternMatcher<ProtocolProcessorDesc> protocolMatcher = createProtocolProcessorsMatcher(
			processorConfig.getProtocolProcessors(), protocols);

		Collection<String> supportedDataBindings = config.getSupportedDataBindings();
		Set<Class> rootClasses = getRootClassesFromOperations(operations.values());
		for ( String className : typeMappingsCfg.getJavaTypes() ) {
			Class clazz = ReflectionUtils.loadClass(className, null, true, cl);
			if ( clazz == null ) {
				if ( LOGGER.isLogEnabled( LogLevel.WARN ) ) {
					LOGGER.log( LogLevel.WARN, "Unable to load type mapping class: " + className );
				}
			} else {
				rootClasses.add( clazz );
			}
		}
		Map<String,DataBindingDesc> bindings = createDataBindings(serverSvcId, processorConfig,
			supportedDataBindings,  rootClasses, true, cl, true);

		DataBindingDesc defaultRequestBinding = getDefaultRequestDataBinding(config, supportedDataBindings, bindings, adminName);
		DataBindingDesc defaultResponseBinding = getDefaultResponseDataBinding(config, supportedDataBindings, bindings, adminName);

		RequestPatternMatcher<DataBindingDesc> bindingMatcherForRequest =
			new RequestPatternMatcher<DataBindingDesc>(true);
		RequestPatternMatcher<DataBindingDesc> bindingMatcherForResponse =
			new RequestPatternMatcher<DataBindingDesc>(true);
		createDataBindingsMatchers(bindings.values(),
			bindingMatcherForRequest, bindingMatcherForResponse);

		String serviceImplClassName = config.getServiceImplClassName();
		if (serviceImplClassName == null) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_UNDEFINED_IMPL_CLASS_NAME,
					ErrorConstants.ERRORDOMAIN));
		}

		ErrorMapper errorMapper = createErrorMapper(processorConfig, serverSvcId, cl);
		ErrorDataProvider errorDataProviderClass = getErrorDataProviderClass(processorConfig, cl);
		List<LoggingHandler> loggingHandlers = createLoggingHandlers(id, processorConfig, cl);

		// Dispatcher class name is based on the admin name.
		String serviceDispatcherClassName = ServiceNameUtils.getServiceDispatcherClassName(
				serverSvcId.getAdminName(), serviceImplClassName);

		BaseServiceRequestDispatcher<?> serviceDispatcher = null;
		try {
			serviceDispatcher = ReflectionUtils.createInstance(
					serviceDispatcherClassName, BaseServiceRequestDispatcher.class, cl);
		}
		catch (ServiceException se) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_MISSING_DISPATCHER_IMPL,
					ErrorConstants.ERRORDOMAIN, new Object[]{serviceDispatcherClassName}), se.getCause());
		}


		VersionCheckHandler versionCheckHandler = createVersionCheckHandler(serverSvcId, config, cl);

		serviceDispatcher.init(serverSvcId, serviceInterfaceClass,
			serviceImplClassName, cl, operations.values(), versionCheckHandler);

		Dispatcher requestDispatcher = new SimpleInvokerDispatcher(serviceDispatcher);
		Dispatcher responseDispatcher = new SimpleServerResponseDispatcher(true);

		Collection<GlobalIdEntry> registryEntries = GlobalRegistryConfigManager.getInstance().getAllEntries();
		Map<String, GlobalIdDesc> globalIdMap = createGlobalIdMap(registryEntries, config);

		Charset serviceCharset = null;	// default is to use request charset
		String serviceCharsetName = config.getDefaultEncoding();
		if (serviceCharsetName != null) {
			try {
				serviceCharset = Charset.forName(serviceCharsetName);
			} catch (Exception e) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_UNSUPPORTED_CHARSET,
						ErrorConstants.ERRORDOMAIN, new Object[] {serviceCharsetName, adminName}), e);
			}
		}

		UrlMappingsDesc urlMappings = loadUrlMappings(adminName, config);
		OperationMappings operationMappings = config.getOperationMappings();
		if( operationMappings == null ) {
			operationMappings = new OperationMappings();
		}
		HeaderMappingsDesc requestHeaderMappings = loadHeaderMappings(adminName, config.getRequestHeaderMappingOptions(), true);
		HeaderMappingsDesc responseHeaderMappings = loadHeaderMappings(adminName, config.getResponseHeaderMappingOptions(), false);

		SecurityPolicyConfigHolder securityPolicy = config.getSecurityPolicy();
		Map<String, Map<String, String>> authentOperationsMap = new HashMap<String, Map<String, String>>();
		loadSecurityPolicy(adminName, securityPolicy, operations, authentOperationsMap);

		CachePolicyDesc cachePolicyDesc = null;
		if (config.getCachePolicy() != null) {
				cachePolicyDesc = CachePolicyDesc.create(config.getCachePolicy(), createOpRequestTypeDesc(operations));
		}


		List<String> serviceLayers = ServiceConfigManager.getInstance().getGlobalConfig().getServiceLayerNames();

		ServerServiceDesc result = new ServerServiceDesc(
			serverSvcId, serviceQName, config,
			requestPipeline, responsePipeline,
			requestDispatcher, responseDispatcher,
			operations, protocols, bindings,
			typeMappings,
			cl,
			loggingHandlers,
			serviceInterfaceClass,
			operationMatcher,
			protocolMatcher,
			bindingMatcherForRequest,
			bindingMatcherForResponse,
			serviceImplClassName,
			errorMapper,
			errorDataProviderClass,
			globalIdMap,
			versionCheckHandler,
			serviceCharset,
			urlMappings,
			operationMappings,
			requestHeaderMappings,
			responseHeaderMappings,
			authentOperationsMap,
			defaultRequestBinding,
			defaultResponseBinding,
			serviceLayers, cachePolicyDesc);

		return result;
	}

	private DataBindingDesc getDefaultRequestDataBinding(ServiceConfigHolder config, Collection<String> supportedDataBindings,
			Map<String,DataBindingDesc> bindings, String adminName) throws ServiceCreationException
	{
		String defaultBinding = config.getDefaultRequestDataBinding();
		if (defaultBinding == null) {
			defaultBinding = BindingConstants.PAYLOAD_XML;
		}
		if (supportedDataBindings != null && !supportedDataBindings.isEmpty()) {
			if (! supportedDataBindings.contains(defaultBinding)) {
				throw new ServiceCreationException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_DEFAULT_DATA_BINDING,
							ErrorConstants.ERRORDOMAIN, new Object[] { adminName, defaultBinding, supportedDataBindings.toString()}));
			}
		}


		return bindings.get(defaultBinding);
	}

	private DataBindingDesc getDefaultResponseDataBinding(ServiceConfigHolder config, Collection<String> supportedDataBindings,
			Map<String,DataBindingDesc> bindings, String adminName) throws ServiceCreationException
	{
		String defaultBinding = config.getDefaultResponseDataBinding();
		if (defaultBinding == null) {
			return null;
		}

		if (supportedDataBindings != null && !supportedDataBindings.isEmpty()) {
			if (! supportedDataBindings.contains(defaultBinding)) {
				throw new ServiceCreationException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_DEFAULT_DATA_BINDING,
							ErrorConstants.ERRORDOMAIN, new Object[] { adminName, defaultBinding, supportedDataBindings.toString()}));
			}
		}

		return bindings.get(defaultBinding);
	}

	private void loadSecurityPolicy(String adminName, SecurityPolicyConfigHolder securityPolicy,
			Map<String, ServiceOperationDesc> operations,
			Map<String, Map<String, String>> authentOperationsMap) throws ServiceCreationException {
		if (securityPolicy != null) {
			Map<String, OperationSecurityConfig> authentOperationsConfig = securityPolicy.getAuthenticationOperations();
			for (Map.Entry<String, OperationSecurityConfig> entry : authentOperationsConfig.entrySet()) {
				String opName = entry.getKey();
				if (!opName.equals("*") && !operations.containsKey(opName)) {
					throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_OPERATION,
							ErrorConstants.ERRORDOMAIN, new Object[] {adminName, opName}));
				}
				Map<String, String> opConfigMap = new HashMap<String, String>();
				OperationSecurityConfig opConfig = entry.getValue();
				List<NameValue> nvList = opConfig.getOption();
				for (NameValue nv : nvList) {
					String name = nv.getName();
					String value = nv.getValue();
					opConfigMap.put(name, value);
				}
				authentOperationsMap.put(opName, opConfigMap);
			}
		}
	}

	private UrlMappingsDesc loadUrlMappings(String adminName, ServiceConfigHolder config) throws ServiceCreationException {
		OptionList options = config.getHeaderMappingOptions();
		if (options == null) {
			return UrlMappingsDesc.EMPTY_MAPPINGS;
		}
		List<NameValue> nameValueList = options.getOption();
		Map<Integer,String> pathMap = new HashMap<Integer,String>();
		Map<String,String> queryMap = new HashMap<String,String>();
		Set<String> rejectSet = new HashSet<String>();
		Set<String> nameSet = new HashSet<String>();

		String queryOpMapping = null;
		for (NameValue nv : nameValueList) {
			String rawname = nv.getName();
			String name = SOAHeaders.normalizeName(rawname, true);
			if (!SOAHeaders.isSOAHeader(name)) {
				throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_HEADER_NAME,
						ErrorConstants.ERRORDOMAIN, new Object[] {adminName, name}));
			}

			if(nameSet.contains(name)) {
				throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_DUPLICATE_HEADER_KEY,
						ErrorConstants.ERRORDOMAIN, new Object[] {adminName, name}));
			}
			nameSet.add(name);

			String value = nv.getValue();
			if (value.startsWith("query[")) {
				if (!value.endsWith("]")) {
					throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_MAPPING_VALUE,
							ErrorConstants.ERRORDOMAIN, new Object[] {adminName, value}));
				}
				String indexval = value.substring(6, value.length()-1);
				queryMap.put(indexval, name);
			} else if (value.equals("queryop")) {
				queryOpMapping = name;
			} else if (value.startsWith("path[")) {
				if (!value.endsWith("]")) {
					throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_MAPPING_VALUE,
							ErrorConstants.ERRORDOMAIN, new Object[] {adminName, value}));
				}
				String indexval = value.substring(5, value.length()-1);
				Integer indexnum = null;
				try {
					indexnum = Integer.valueOf(indexval);
				} catch (NumberFormatException e) {
					throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_MAPPING_VALUE,
							ErrorConstants.ERRORDOMAIN, new Object[] {adminName, value}), e);
				}
				pathMap.put(indexnum, name);
			} else if( value.trim().equalsIgnoreCase("reject") ) {
				rejectSet.add(name);
			} else {
				throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_MAPPING_VALUE,
						ErrorConstants.ERRORDOMAIN, new Object[] {adminName, value}));
			}

		}
		UrlMappingsDesc result = new UrlMappingsDesc(pathMap, queryMap, queryOpMapping, rejectSet);
		return result;
	}

	public ServerServiceDesc createFallbackServiceDesc(QName serviceQName) throws ServiceException {
		ServerServiceId serverSvcId = ServerServiceId.createFallbackServiceId(serviceQName.getLocalPart());
		ClassLoader cl = ServiceDesc.class.getClassLoader();

		// create empty pipelines
		Pipeline requestPipeline = createFallbackPipeline(serverSvcId, PipelineMode.REQUEST);
		Pipeline responsePipeline = createFallbackPipeline(serverSvcId, PipelineMode.RESPONSE);

		// create empty operation lists
		Map<String,ServiceOperationDesc> operations = new HashMap<String,ServiceOperationDesc>();
		RequestPatternMatcher<ServiceOperationDesc> operationMatcher =
			new RequestPatternMatcher<ServiceOperationDesc>(true);

		// create empty protocol processor lists
		Map<String,ProtocolProcessorDesc> protocols = new HashMap<String,ProtocolProcessorDesc>();
		RequestPatternMatcher<ProtocolProcessorDesc> protocolMatcher =
			new RequestPatternMatcher<ProtocolProcessorDesc>(true);

		Set<Class> rootClasses = getRootClassesFromOperations(operations.values());

		// create default bindings
		Map<String,DataBindingDesc> bindings = new HashMap<String,DataBindingDesc>();
		addDefaultDataBindings(serverSvcId, bindings, null, rootClasses, null);

		// create matchers for bindings
		RequestPatternMatcher<DataBindingDesc> bindingMatcherForRequest =
			new RequestPatternMatcher<DataBindingDesc>(true);
		RequestPatternMatcher<DataBindingDesc> bindingMatcherForResponse =
			new RequestPatternMatcher<DataBindingDesc>(true);
		createDataBindingsMatchers(bindings.values(),
			bindingMatcherForRequest, bindingMatcherForResponse);

		ServiceTypeMappings typeMappings = createFallbackTypeMappings();

		ErrorMapper errorMapper = new DefaultErrorMapperImpl();
		ErrorDataProvider errorDataProviderClass = getDefaultErrorDataProviderClass();

		ErrorMapperInitContextImpl errorMapperInitCtx = new ErrorMapperInitContextImpl(serverSvcId);
		errorMapper.init(errorMapperInitCtx);
		errorMapperInitCtx.kill();

		List<LoggingHandler> loggingHandlers = new ArrayList<LoggingHandler>();
		addDefaultLoggingHandler(serverSvcId, cl, loggingHandlers);

		Dispatcher requestDispatcher = new FallbackRequestDispatcher();
		Dispatcher responseDispatcher = new SimpleServerResponseDispatcher(true);

		VersionCheckHandler versionCheckHandler = new NullVersionCheckHandler();

		VersionCheckHandlerInitContextImpl versionInitCtx =
			new VersionCheckHandlerInitContextImpl(serverSvcId, "1.0", null);
		versionCheckHandler.init(versionInitCtx);
		versionInitCtx.kill();

		Collection<GlobalIdEntry> registryEntries = GlobalRegistryConfigManager.getInstance().getAllEntries();
		Map<String, GlobalIdDesc> globalIdMap = createGlobalIdMap(registryEntries, null);

		List<String> serviceLayers = ServiceConfigManager.getInstance().getGlobalConfig().getServiceLayerNames();

		ServerServiceDesc result = new ServerServiceDesc(
			serverSvcId,
			serviceQName,
			null, // config
			requestPipeline, responsePipeline,
			requestDispatcher, responseDispatcher,
			operations, protocols, bindings,
			typeMappings,
			cl,
			loggingHandlers,
			null, // service interface class
			operationMatcher,
			protocolMatcher,
			bindingMatcherForRequest,
			bindingMatcherForResponse,
			null, // service impl class name
			errorMapper,
			errorDataProviderClass,
			globalIdMap,
			versionCheckHandler,
			null,
			UrlMappingsDesc.EMPTY_MAPPINGS,
			new OperationMappings(),
			HeaderMappingsDesc.EMPTY_MAPPINGS,
			HeaderMappingsDesc.EMPTY_MAPPINGS,
			Collections.unmodifiableMap(new HashMap<String, Map<String, String>>()),
			bindings.get(BindingConstants.PAYLOAD_XML),
			bindings.get(BindingConstants.PAYLOAD_XML),
			serviceLayers, null
		);

		return result;
	}

	public ServiceOperationDesc createFallbackOperationDesc(ServerServiceDesc svcDesc, String opName)
		throws ServiceException
	{
		ServiceId svcId = svcDesc.getServiceId();
		QName svcQName = svcDesc.getServiceQName();
		ServiceTypeMappings typeMappings = svcDesc.getTypeMappings();
		ClassLoader cl = svcDesc.getClassLoader();

		String svcNamespace = svcQName.getNamespaceURI();
		ServiceOperationParamDesc requestParamDesc = createOperationParamDesc(svcId,
			null, svcNamespace, typeMappings, opName, false, cl);
		ServiceOperationParamDesc responseParamDesc = createOperationParamDesc(svcId,
			null, svcNamespace, typeMappings, opName, false, cl);
		ServiceOperationParamDesc errorParamDesc = createOperationParamDesc(svcId,
			null, null, typeMappings, opName, true, cl);

		return new ServiceOperationDescImpl(svcId, opName,
			requestParamDesc, responseParamDesc, errorParamDesc,
			null, null, null, false, false);
	}

	private Pipeline createFallbackPipeline(ServiceId svcId, PipelineMode pipelineMode)
		throws ServiceException
	{
		ClassLoader cl = Pipeline.class.getClassLoader();
		Pipeline result = new PipelineImpl();

		PipelineInitContextImpl initCtx = new PipelineInitContextImpl(svcId, pipelineMode, cl, null);
		result.init(initCtx);
		initCtx.kill();

		return result;
	}

	@Override
	protected String getDefaultLoggingHandlerClassName() {
		return ServerLoggingHandler.class.getName();
	}

	private RequestPatternMatcher<ProtocolProcessorDesc> createProtocolProcessorsMatcher(
		List<ProtocolProcessorConfig> processorConfigs, Map<String,ProtocolProcessorDesc> protocols)
		throws ServiceException
	{
		RequestPatternMatcher<ProtocolProcessorDesc> result =
			new RequestPatternMatcher<ProtocolProcessorDesc>(true);

		for (int i=0; i<processorConfigs.size(); i++) {
			ProtocolProcessorConfig config = processorConfigs.get(i);

			String name = config.getName();
			name = name.toUpperCase();

			ProtocolProcessorDesc processor = protocols.get(name);

			FeatureIndicatorConfig indicator = config.getIndicator();
			String uriPattern = indicator.getURLPattern();
			if (uriPattern != null) {
				result.addUriPattern(uriPattern, processor);
			}

			NameValue headerValue = indicator.getTransportHeader();
			if (headerValue != null) {
				String headerName = headerValue.getName();
				headerName = SOAHeaders.normalizeName(headerName, true);

				String value = headerValue.getValue();
				value = SOAHeaders.normalizeValue(headerName, value);

				result.addHeaderPattern(headerName, value, processor);
			}
		}

		return result;
	}

	private RequestPatternMatcher<ServiceOperationDesc> createOperationsMatcher(
		Collection<ServiceOperationDesc> operations)
		throws ServiceException
	{
		RequestPatternMatcher<ServiceOperationDesc> result =
			new RequestPatternMatcher<ServiceOperationDesc>(true);

		for (ServiceOperationDesc op : operations) {
			result.addHeaderPattern(SOAHeaders.SERVICE_OPERATION_NAME, op.getName(), op);
		}

		return result;
	}

	private void createDataBindingsMatchers(Collection<DataBindingDesc> bindings,
		RequestPatternMatcher<DataBindingDesc> bindingMatcherForRequest,
		RequestPatternMatcher<DataBindingDesc> bindingMatcherForResponse)
		throws ServiceException
	{
		for (DataBindingDesc bindingDesc : bindings) {
			String payloadType = bindingDesc.getPayloadType();

			if (bindingMatcherForRequest != null) {
				bindingMatcherForRequest.addHeaderPattern(
					SOAHeaders.REQUEST_DATA_FORMAT,
					payloadType, bindingDesc);
			}

			if (bindingMatcherForResponse != null) {
				bindingMatcherForResponse.addHeaderPattern(
					SOAHeaders.RESPONSE_DATA_FORMAT,
					payloadType, bindingDesc);
			}
		}
	}

	private ErrorMapper createErrorMapper(MessageProcessorConfigHolder config,
		ServerServiceId svcId, ClassLoader cl)
		throws ServiceException
	{
		ErrorMapper result;
		String className = config.getErrorMappingClass();
		if (className == null) {
			// if error mapper not configured, use the system default one
			result = new DefaultErrorMapperImpl();
		} else {
			result = ReflectionUtils.createInstance(className, ErrorMapper.class, cl);
		}

		ErrorMapperInitContextImpl initCtx = new ErrorMapperInitContextImpl(svcId);
		result.init(initCtx);
		initCtx.kill();

		return result;
	}

	private ErrorDataProvider getErrorDataProviderClass(MessageProcessorConfigHolder config, ClassLoader cl) throws ServiceException
	{
		ErrorDataProvider result =  null;
		String className = config.getErrorDataProviderClass();
		if (className == null) {
			// if error data provider not configured, for now set it to null

		} else {
			result = ReflectionUtils.createInstance(className, ErrorDataProvider.class, cl);
		}

		return result;
	}

	private ErrorDataProvider getDefaultErrorDataProviderClass() throws ServiceException
	{
		// For now set it to null as default
		ErrorDataProvider result =  null;
		return result;
	}

	private VersionCheckHandler createVersionCheckHandler(ServerServiceId serverSvcId,
		ServiceConfigHolder config, ClassLoader cl) throws ServiceException
	{
		String currentVersion = config.getMetaData().getVersion();
		if (currentVersion == null || currentVersion.length() == 0) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_MISSING_SERVICE_VERSION,
					ErrorConstants.ERRORDOMAIN, new Object[] {serverSvcId.getAdminName()}));
		}

		Collection<String> supportedVersions = config.getSupportedVersions();
		if (supportedVersions != null) {
			supportedVersions = new ArrayList<String>(supportedVersions);
		} else {
			supportedVersions = new ArrayList<String>();
		}
		if (!supportedVersions.contains(currentVersion)) {
			supportedVersions.add(currentVersion);
		}
		supportedVersions = Collections.unmodifiableCollection(supportedVersions);

		String versionCheckClassName = config.getVersionCheckHandlerClassName();
		if (versionCheckClassName == null) {
			versionCheckClassName = SimpleVersionCheckHandler.class.getName();
		}

		VersionCheckHandler versionCheckHandler = ReflectionUtils.createInstance(
			versionCheckClassName, VersionCheckHandler.class, cl);

		VersionCheckHandlerInitContextImpl initCtx = new VersionCheckHandlerInitContextImpl(
			serverSvcId, currentVersion, supportedVersions);
		versionCheckHandler.init(initCtx);
		initCtx.kill();

		return versionCheckHandler;
	}

	@Override
	protected Collection<ServiceId> loadAllServiceNames() throws ServiceException {
		Collection<ServiceId> result = new ArrayList<ServiceId>();
		Collection<String> names = ServiceConfigManager.getInstance().getAllServiceAdminNames();
		if (names != null) {
			for (String name: names) {
				result.add(ServerServiceId.newInstance(name));
			}
		}
		return result;
	}

	private Map<String, GlobalIdDesc> createGlobalIdMap(Collection<GlobalIdEntry> registryEntries, ServiceConfigHolder config) {
		Map<String, GlobalIdDesc> result = new HashMap<String, GlobalIdDesc>();
		Set<String> supportedGlobalId = null;
		Set<String> supportedLocales = null;
		if (config != null) {
			supportedGlobalId = config.getSupportedGlobalId();
			supportedLocales = config.getSupportedLocales();
		}
		for (GlobalIdEntry entry : registryEntries) {
			String id = entry.getId();
			boolean isDefaultRegistryEntry = entry.isDefaultGlobalId();
			boolean supported;
			if (supportedGlobalId == null || supportedGlobalId.isEmpty()) {
				// No configured list implies all IDs are supported.
				supported = true;
			} else if (isDefaultRegistryEntry) {
				supported = true;
			} else {
				supported = (supportedGlobalId.contains(id));
			}
			GlobalIdDesc idDesc = new GlobalIdDesc(entry, supported, supportedLocales);
			if (isDefaultRegistryEntry) {
				result.put(SOAConstants.DEFAULT_GLOBAL_ID, idDesc);
			} else {
				result.put(id, idDesc);
			}
		}
		return result;
	}

	private Map<String, ServiceOperationParamDesc> createOpRequestTypeDesc(
			Map<String, ServiceOperationDesc> operations) {
		Map<String, ServiceOperationParamDesc> opReqTypeMap = new HashMap<String, ServiceOperationParamDesc>();
		for(Map.Entry<String, ServiceOperationDesc> entry: operations.entrySet()) {
			opReqTypeMap.put(entry.getKey(), entry.getValue().getRequestType());
		}
		return opReqTypeMap;
	}

	@Override
	public ServiceContext getServiceContext(ServerServiceDesc desc) throws ServiceException {
		return new ServerServiceContextImpl(desc);
	}

	public synchronized void initializeCompStatus() {
		if (!m_isCompStatusInitialized) {
			m_isCompStatusInitialized = true;
			initializeCompStatus(new ServerServiceBrowserCompStatus());
		}
	}

	static class FallbackRequestDispatcher implements Dispatcher {
		public void dispatchSynchronously(MessageContext ctx) throws ServiceException {
			throw new IllegalStateException("FallbcakRequestDispatcher should not be invoked");
		}

		public Future<?> dispatch(MessageContext ctx)	throws ServiceException {
			throw new UnsupportedOperationException("FallbcakRequestDispatcher should not be invoked");
		}

		public void retrieve(MessageContext ctx, Future<?> name) throws ServiceException {
			throw new UnsupportedOperationException("FallbcakRequestDispatcher does not support retrieve");
		}
	}

	static {
		setServerInstance(s_instance);
	}

	@Override
	protected ServerServiceDesc createServiceDesc(ServiceDesc commonDesc,
			ServiceId newServiceId, QName srvQName, Definition definition,
			boolean rawMode) throws ServiceException {
		throw new UnsupportedOperationException(
				"Creating ServiceDesc from wsdlURL is not supported");
	}

}
