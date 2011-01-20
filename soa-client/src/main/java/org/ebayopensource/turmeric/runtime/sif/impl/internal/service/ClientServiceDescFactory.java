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

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheProvider;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.cachepolicy.DefaultCacheProvider;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MessageProcessorConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.TypeMappingConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.TransportInitContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.BaseServiceDescFactory;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ProtocolProcessorDesc;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceDesc;
import org.ebayopensource.turmeric.runtime.common.impl.pipeline.NullDispatcher;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ReflectionUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.Dispatcher;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.Pipeline;
import org.ebayopensource.turmeric.runtime.common.pipeline.PipelineMode;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.service.HeaderMappingsDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.service.ServiceTypeMappings;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;


import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigHolder;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.markdown.SOAClientMarkdownStateManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ApplicationRetryHandlerInitContextImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.AutoMarkdownStateFactoryInitContextImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ErrorResponseAdapterInitContextImpl;
import org.ebayopensource.turmeric.runtime.sif.impl.pipeline.ClientLoggingHandler;
import org.ebayopensource.turmeric.runtime.sif.impl.pipeline.DefaultApplicationRetryHandler;
import org.ebayopensource.turmeric.runtime.sif.impl.pipeline.DefaultAutoMarkdownStateFactory;
import org.ebayopensource.turmeric.runtime.sif.impl.pipeline.SimpleClientRequestDispatcher;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ApplicationRetryHandler;
import org.ebayopensource.turmeric.runtime.sif.pipeline.AutoMarkdownStateFactory;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ErrorResponseAdapter;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;

import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;
import com.ebay.kernel.variable.VariableResolver;

/**
 * @author ichernyshev
 */
public class ClientServiceDescFactory extends
		BaseServiceDescFactory<ClientServiceDesc> {

	private static Logger LOGGER = Logger
			.getInstance(ClientServiceDescFactory.class);

	private boolean m_isCompStatusInitialized;

	private static ClientServiceDescFactory s_instance = new ClientServiceDescFactory();

	private ClientServiceDescFactory() {
		super("ClientServiceDescFactory", true, false);
	}

	public static ClientServiceDescFactory getInstance() {
		return s_instance;
	}

	/**
	 * Returns a ServiceDesc, attempting to load if necessary
	 */
	public final ClientServiceDesc getServiceDesc(String adminName,
			String clientName) throws ServiceException {
		return getServiceDesc(createClientServiceId(adminName, clientName));
	}

	public final ClientServiceDesc getServiceDesc(String adminName,
			String clientName, boolean rawMode) throws ServiceException {
		return getServiceDesc(createClientServiceId(adminName, clientName), rawMode);
	}

	public ClientServiceDesc getServiceDesc(String adminName,
			String clientName, String envName, boolean rawMode, String targetServiceAdminName, QName svcQName, URL wsdlLocation)
			throws ServiceException {

		// check if the service desc is already available, else continue
		ClientServiceId targetClientServiceId = createClientServiceId(targetServiceAdminName, targetServiceAdminName,
				envName);
		ClientServiceDesc targetServiceDesc = null;
		try {
			targetServiceDesc = getServiceDesc(targetClientServiceId);
		} catch (ServiceCreationException e) {
			// remove from the failed cache
			clearFromFailedCache(targetClientServiceId);
		}

		// if found, return it
		if (targetServiceDesc != null )
			return targetServiceDesc;

		Definition wsdlDefinition = getWSDLDefinition(wsdlLocation);
		svcQName = getSrvQName(wsdlDefinition, svcQName);

		if (svcQName == null) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_SVC_WSDL_URL,
					ErrorConstants.ERRORDOMAIN, new Object[] {wsdlLocation}));
		}
		// Assume the target&base have identical env
		// String newServiceEnv = envName == null ? svcQName.getLocalPart() : envName;


		return getServiceDesc(getServiceDesc(createClientServiceId(adminName, clientName, envName), rawMode), targetClientServiceId, svcQName,
				wsdlDefinition, rawMode);
	}

	private QName getSrvQName(Definition wsdlDefinition, QName svcQName) {
		if(svcQName != null && svcQName.getLocalPart() != null){
			return svcQName;
		}
		if (wsdlDefinition != null) {
			svcQName = wsdlDefinition.getQName();
			if (svcQName == null) {
				for (Object obj : wsdlDefinition.getServices().keySet()) {
					svcQName = (QName) obj;
					break;
				}
			}
		}
		return svcQName;
	}

	private Definition getWSDLDefinition(URL wsdlLocation)
			throws ServiceCreationException {
		Definition wsdlDefinition = null;

		if (wsdlLocation != null) {
			String wsdlStr = wsdlLocation.toString();
			try {
				wsdlDefinition = WSDLFactory.newInstance().newWSDLReader()
						.readWSDL(wsdlStr);
			} catch (WSDLException e) {
				if (LOGGER.isLogEnabled(LogLevel.WARN)) {
					LOGGER.log(LogLevel.WARN,
							"Unable to load Wsdl defintion: " + e.getMessage());
				}
			}
		}
		return wsdlDefinition;
	}


	/**
	 * *
	 *
	 * @return ClientServicedesc for clientconfig having envName if envName is
	 *         set.
	 * @throws ServiceException
	 */
	public final ClientServiceDesc getServiceDesc(String adminName,
			String clientName, String envName, boolean rawMode)
			throws ServiceException {
			return getServiceDesc(createClientServiceId(adminName, clientName, envName), rawMode);
	}

	@Deprecated
	public final void reloadServiceDesc(String adminName, String clientName)
			throws ServiceException {
		reloadServiceDesc(createClientServiceId(adminName, clientName));
	}

	/**
	 * Reloads ServiceDesc to reflect configuration change
	 */
	public final void reloadServiceDesc(String adminName, String clientName,
			String envName) throws ServiceException {
		reloadServiceDesc(createClientServiceId(adminName, clientName, envName));
	}

	@Override
	protected void postServiceDescLoad(ClientServiceDesc svcDesc) {
		SOAClientMarkdownStateManager.getInstance().addServiceStates(svcDesc);
	}

	@Override
	protected ClientServiceDesc createServiceDesc(ServiceId id)
			throws ServiceException {
		return createServiceDesc(id, m_rawModes.containsKey(id.getAdminName()));
	}

	@Override
	protected ClientServiceDesc createServiceDesc(ServiceDesc commonDesc,
			ServiceId newServiceId, QName srvQName, Definition wsdlDefinition, boolean rawMode)
			throws ServiceException {
		ClientServiceId clientSvcId = (ClientServiceId) newServiceId;
		String clientName = clientSvcId.getClientName();
		String adminName = clientSvcId.getAdminName();
		String envName = clientSvcId.getEnvName();

		ClientConfigManager.getInstance().getConfig(
				(ClientConfigHolder) commonDesc.getConfig(), adminName,
				clientName, envName, srvQName, wsdlDefinition, rawMode);

		return createServiceDesc(newServiceId);
	}

	@Override
	protected ClientServiceDesc createServiceDesc(ServiceId id, boolean rawMode)
			throws ServiceException {
		ClientServiceId clientSvcId = (ClientServiceId) id;
		String clientName = clientSvcId.getClientName();
		String adminName = id.getAdminName();
		String envName = clientSvcId.getEnvName();

		ClientConfigHolder config = ClientConfigManager.getInstance()
				.getConfig(adminName, clientName, envName, rawMode);

		QName svcQName = config.getServiceQName();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		MessageProcessorConfigHolder processorConfig = config
				.getMessageProcessorConfig();
		TypeMappingConfigHolder typeMappingsCfg = config.getTypeMappings();

		Pipeline requestPipeline = createPipeline(id, PipelineMode.REQUEST,
				processorConfig, cl);
		Pipeline responsePipeline = createPipeline(id, PipelineMode.RESPONSE,
				processorConfig, cl);

		Class serviceInterfaceClass = loadServiceInterfaceClass(config, false,
				cl);

		ServiceTypeMappings typeMappings = createTypeMappings(id,
				typeMappingsCfg, serviceInterfaceClass, cl);

		Map<String, ServiceOperationDesc> operations = createOperations(id,
				typeMappingsCfg, null, typeMappings, svcQName, cl, null);

		Map<String, ProtocolProcessorDesc> protocols = createProtocolProcessors(
				id, processorConfig.getProtocolProcessors(), cl);

		Set<Class> rootClasses = getRootClassesFromOperations(operations
				.values());
		Collection<String> additionalTypes = typeMappingsCfg.getJavaTypes();
		for (String className : additionalTypes) {
			Class clazz = ReflectionUtils.loadClass(className, null, true, cl);
			if (clazz == null) {
				if (LOGGER.isLogEnabled(LogLevel.WARN)) {
					LOGGER.log(LogLevel.WARN,
							"Unable to load type mapping class: " + className);
				}
			} else {
				rootClasses.add(clazz);
			}
		}
		Map<String, DataBindingDesc> bindings = createDataBindings(id,
				processorConfig, null, rootClasses, false, cl, false);

		String defRequestBindingName = config.getRequestDataBinding();
		if (defRequestBindingName == null
				|| defRequestBindingName.length() == 0) {
			// Use XML databinding name, which has the same name as payload type
			defRequestBindingName = BindingConstants.PAYLOAD_XML;
		}
		DataBindingDesc defRequestBinding = findDataBinding(bindings,
				defRequestBindingName);

		String defResponseBindingName = config.getResponseDataBinding();
		DataBindingDesc defResponseBinding = null;
		if (defResponseBindingName != null) {
			defResponseBinding = findDataBinding(bindings,
					defResponseBindingName);
		}

		DataBindingDesc defRestRequestDataBinding = findDataBinding(bindings,
				BindingConstants.PAYLOAD_NV);

		DataBindingDesc defRestResponseDataBinding;
		if (defResponseBinding != null) {
			defRestResponseDataBinding = defResponseBinding;
		} else {
			defRestResponseDataBinding = findDataBinding(bindings,
					BindingConstants.PAYLOAD_XML);
		}

		String defTransportName = config.getPreferredTransport();

		if (defTransportName == null) {
			defTransportName = SOAConstants.TRANSPORT_HTTP_11;
		}

		Map<String, Transport> transports = createTransports(id,
				processorConfig, cl);

		Transport defTransport = transports.get(defTransportName);
		if (defTransport == null) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_UNKNOWN_TRANSPORT_NAME,
					ErrorConstants.ERRORDOMAIN, new Object[] { defTransportName } ));
		}

		G11nOptions g11nOptions = buildG11nOptions(config);

		List<LoggingHandler> loggingHandlers = createLoggingHandlers(id,
				processorConfig, cl);

		Dispatcher requestDispatcher;
		String requestDispatcherClassName = config.getMessageProcessorConfig()
				.getRequestDispatcherClassName();
		if (requestDispatcherClassName != null) {
			requestDispatcher = ReflectionUtils.createInstance(
					requestDispatcherClassName, Dispatcher.class, cl);
		} else {
			requestDispatcher = new SimpleClientRequestDispatcher(false);
		}

		Dispatcher responseDispatcher;
		String responseDispatcherClassName = config.getMessageProcessorConfig()
				.getResponseDispatcherClassName();
		if (responseDispatcherClassName != null) {
			responseDispatcher = ReflectionUtils.createInstance(
					responseDispatcherClassName, Dispatcher.class, cl);
		} else {
			responseDispatcher = new NullDispatcher();
		}

		URL defServiceLocationURL = null;
		if (config.getServiceLocation() != null
				&& config.getServiceLocation().length() > 0) {
			String urlStr = config.getServiceLocation();
			urlStr = VariableResolver.processString(urlStr);

			try {
				defServiceLocationURL = new URL(urlStr);
			} catch (MalformedURLException e) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_BAD_REQUEST_URL,
						ErrorConstants.ERRORDOMAIN, new Object[] { config.getClientName(), config.getServiceLocation() } ));
			}
		}

		String serviceVersion = config.getServiceVersion();

		ApplicationRetryHandler retryHandler = createRetryHandler(clientSvcId,
				config, cl);

		ErrorResponseAdapter customExceptionHandler = createCustomErrorResponseHandler(
				clientSvcId, config, cl);

		ErrorDataProvider errorDataProviderClass = getErrorDataProviderClass(
				config, cl);

		CacheProvider cacheProviderClass = getCacheProviderClass(config,
				operations, adminName, defServiceLocationURL, cl);

		AutoMarkdownStateFactory autoMarkdownFactory = createAutoMarkdownStateFactory(
				clientSvcId, config, cl);

		String urlPathInfo = config.getUrlPathInfo();

		HeaderMappingsDesc requestHeaderMappings = loadHeaderMappings(
				adminName, config.getRequestHeaderMappingOptions(), true);
		HeaderMappingsDesc responseHeaderMappings = loadHeaderMappings(
				adminName, config.getResponseHeaderMappingOptions(), false);

		List<String> serviceLayers = ClientConfigManager.getInstance()
				.getGlobalConfig().getServiceLayerNames();

		ClientServiceDesc result = new ClientServiceDesc(clientSvcId, svcQName,
				config, requestPipeline, responsePipeline, requestDispatcher,
				responseDispatcher, operations, protocols, bindings,
				transports, typeMappings, cl, g11nOptions, loggingHandlers,
				serviceInterfaceClass, defRequestBinding, defResponseBinding,
				defTransportName, defTransport, defServiceLocationURL,
				serviceVersion, retryHandler, customExceptionHandler,
				errorDataProviderClass, cacheProviderClass,
				autoMarkdownFactory, defRestRequestDataBinding,
				defRestResponseDataBinding, requestHeaderMappings,
				responseHeaderMappings, serviceLayers, urlPathInfo);

		return result;
	}

	@Override
	protected String getDefaultLoggingHandlerClassName() {
		return ClientLoggingHandler.class.getName();
	}

	@Override
	protected Collection<ServiceId> loadAllServiceNames()
			throws ServiceException {
		Collection<ServiceId> result = new ArrayList<ServiceId>();

		Collection<String> clientNames = ClientConfigManager.getInstance()
				.getAllClientNames();
		if (clientNames == null) {
			return result;
		}

		for (String clientName : clientNames) {
			Collection<String> svcNames = ClientConfigManager.getInstance()
					.getAllServiceAdminNames(clientName);

			if (svcNames != null) {
				for (String svcName : svcNames) {
					result.add(createClientServiceId(svcName, clientName));
				}
			}
		}

		return result;
	}

	private Map<String, Transport> createTransports(ServiceId svcId,
			MessageProcessorConfigHolder processorConfig, ClassLoader cl)
			throws ServiceException {
		Map<String, String> transportClasses = processorConfig
				.getTransportClasses();
		Map<String, TransportOptions> transportOptionsCfg = processorConfig
				.getTransportOptions();

		Map<String, Transport> result = new HashMap<String, Transport>();
		for (Map.Entry<String, String> e : transportClasses.entrySet()) {
			String name = e.getKey();

			// get a non-null copy of the options
			TransportOptions options = transportOptionsCfg.get(name);
			if (options == null) {
				throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_TRANSPORT_CONFIG,
						ErrorConstants.ERRORDOMAIN, new Object[] { name }));
			}
			String className = options.getHttpTransportClassName();

			Transport transport = ReflectionUtils.createInstance(className,
					Transport.class, cl);

			TransportInitContextImpl initCtx = new TransportInitContextImpl(
					svcId, name, options);
			transport.init(initCtx);
			initCtx.kill();

			result.put(name, transport);
		}

		return result;
	}

	private G11nOptions buildG11nOptions(ClientConfigHolder config)
			throws ServiceException {
		Charset charset;
		String encoding = config.getPreferredEncoding();
		if (encoding != null) {
			try {
				charset = Charset.forName(encoding);
			} catch (IllegalCharsetNameException e) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_ENCODING,
						ErrorConstants.ERRORDOMAIN, new Object[] { encoding } ));
			} catch (UnsupportedCharsetException e) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_ENCODING,
						ErrorConstants.ERRORDOMAIN, new Object[] { encoding } ));
			}
		} else {
			charset = G11nOptions.DEFAULT_CHARSET;
		}

		ArrayList<String> locales = config.getPreferredLocaleSet();

		String globalId = config.getPreferredGlobalId();
		return new G11nOptions(charset, locales, globalId);
	}

	private DataBindingDesc findDataBinding(
			Map<String, DataBindingDesc> bindings, String name)
			throws ServiceException {
		if (name == null) {
			throw new NullPointerException();
		}

		if (name.length() == 0) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_DATA_BINDING_NAME,
					ErrorConstants.ERRORDOMAIN, new Object[] {"''"}));
		}

		name = name.toUpperCase();

		DataBindingDesc defBinding = bindings.get(name);
		if (defBinding == null) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_UNKNOWN_DATA_BINDING,
					ErrorConstants.ERRORDOMAIN, new Object[] { name } ));
		}

		return defBinding;
	}

	private ApplicationRetryHandler createRetryHandler(ClientServiceId svcId,
			ClientConfigHolder config, ClassLoader cl) throws ServiceException {
		ApplicationRetryHandler retryHandler;
		String className = config.getRetryHandlerClass();
		if (className == null) {
			retryHandler = new DefaultApplicationRetryHandler();
		} else {
			retryHandler = ReflectionUtils.createInstance(className,
					ApplicationRetryHandler.class, cl);
		}

		Collection<String> retryTransportStatusCodes = config
				.getRetryTransportStatusCodes();
		Collection<String> retryExceptionClassNames = config
				.getRetryExceptionClasses();
		Collection<String> retryErrorIds = config.getRetryErrorIds();

		if (retryExceptionClassNames != null) {
			validateExceptionClasses(retryExceptionClassNames, cl);
		}

		ApplicationRetryHandlerInitContextImpl initCtx = new ApplicationRetryHandlerInitContextImpl(
				svcId, retryTransportStatusCodes, retryExceptionClassNames,
				retryErrorIds);
		retryHandler.init(initCtx);
		initCtx.kill();

		return retryHandler;
	}

	private ErrorResponseAdapter createCustomErrorResponseHandler(
			ClientServiceId svcId, ClientConfigHolder config, ClassLoader cl)
			throws ServiceException {
		String className = config.getCustomErrorResponseAdapter();
		if (className == null) {
			return null;
		}

		ErrorResponseAdapter responseHandler = ReflectionUtils.createInstance(
				className, ErrorResponseAdapter.class, cl);

		ErrorResponseAdapterInitContextImpl initCtx = new ErrorResponseAdapterInitContextImpl(
				svcId);
		responseHandler.init(initCtx);
		initCtx.kill();

		return responseHandler;
	}

	private ErrorDataProvider getErrorDataProviderClass(
			ClientConfigHolder config, ClassLoader cl) throws ServiceException {
		ErrorDataProvider result = null;
		String className = config.getErrorDataProviderClass();
		if (className == null) {
			// if error data provider not configured, for now set it to null
			result = null;
		} else {
			result = ReflectionUtils.createInstance(className,
					ErrorDataProvider.class, cl);
		}

		return result;
	}

	/**
	 * Retrieves the provider info from the holder If not present, returns the
	 * instance of DefaultNoCacheProvider
	 *
	 */
	private CacheProvider getCacheProviderClass(ClientConfigHolder config,
			Map<String, ServiceOperationDesc> operations, String adminName,
			URL defUrl, ClassLoader cl) throws ServiceException {
		CacheProvider result = null;
		String className = config.getCacheProviderClass();
		if (className == null) {
			// if cache policy provider not configured, set to default
			className =	"org.ebayopensource.turmeric.runtime.common.impl.cachepolicy.DefaultCacheProvider";
			result = new DefaultCacheProvider();
			return result;
		}

		result = ReflectionUtils.createInstance(className, CacheProvider.class,
				cl);
		if (result == null) {
			// .. log exception and switch to default
			if (LOGGER.isLogEnabled(LogLevel.WARN)) {
				LOGGER.log(LogLevel.WARN,
						"Creating instance failed for cachePolicy provider: "
								+ className
								+ " switching to DefaultCacheProvider");
			}
			result = new DefaultCacheProvider();
		}
		return result;
	}

	private AutoMarkdownStateFactory createAutoMarkdownStateFactory(
			ClientServiceId svcId, ClientConfigHolder config, ClassLoader cl)
			throws ServiceException {
		Boolean enabled = config.getMarkdownEnabled();
		if (enabled == null || !enabled.booleanValue()) {
			return null;
		}

		AutoMarkdownStateFactory result;
		String className = config.getMarkdownStateFactoryClass();
		if (className == null) {
			result = new DefaultAutoMarkdownStateFactory();
		} else {
			result = ReflectionUtils.createInstance(className,
					AutoMarkdownStateFactory.class, cl);
		}

		Collection<String> transportStatusCodes = config
				.getMarkdownTransportStatusCodes();
		Collection<String> exceptionClassNames = config
				.getMarkdownExceptionClasses();
		Collection<String> errorIds = config.getMarkdownErrorIds();

		if (exceptionClassNames != null) {
			validateExceptionClasses(exceptionClassNames, cl);
		}

		Integer errCountThreshold = config.getMarkdownErrCountThreshold();
		if (errCountThreshold == null) {
			errCountThreshold = Integer.valueOf(0);
		}

		AutoMarkdownStateFactoryInitContextImpl initCtx = new AutoMarkdownStateFactoryInitContextImpl(
				svcId, transportStatusCodes, exceptionClassNames, errorIds,
				errCountThreshold.intValue());
		result.init(initCtx);
		initCtx.kill();

		return result;
	}

	private void validateExceptionClasses(
			Collection<String> retryExceptionClassNames, ClassLoader cl)
			throws ServiceException {
		for (String className : retryExceptionClassNames) {
			ReflectionUtils.loadClass(className, Throwable.class, cl);
		}
	}

	@Override
	public ServiceContext getServiceContext(ClientServiceDesc desc)
			throws ServiceException {
		return new ClientServiceContextImpl(desc);
	}

	public synchronized void initializeCompStatus() {
		if (!m_isCompStatusInitialized) {
			m_isCompStatusInitialized = true;
			initializeCompStatus(new ClientServiceBrowserCompStatus());
		}
	}

	static {
		setClientInstance(s_instance);
	}

	//
	// Start - various ways of creating ClientServiceId - Start
	// TODO
	//  	Refactoring of the ClientServiceId creation is done to clean up the
	// process of ServiceID creation. The ClientServiceId created by these constructors
	// are not conceptually complete. The parent (ServiceId) is not populated with service name,
	// version and namespace.
	//

	private ClientServiceId createClientServiceId(String adminName, String clientName) {
		return new ClientServiceId(adminName, clientName);
	}

	private ClientServiceId createClientServiceId(String adminName, String clientName, String envName) {
		if(envName != null) {
			return new ClientServiceId(adminName, clientName, envName);
		}
		return createClientServiceId(adminName, clientName);
	}

	// End - various ways of creating Client Service Id - End
}
