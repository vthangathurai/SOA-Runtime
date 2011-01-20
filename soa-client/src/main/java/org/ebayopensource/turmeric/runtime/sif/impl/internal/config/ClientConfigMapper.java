/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.config;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.DomParseUtils;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MessageProcessorConfigMapper;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MetadataPropertyConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.OptionList;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.TypeMappingConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ServiceNameUtils;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ebay.kernel.variable.VariableResolver;

// Copy non-null source data into the destination holder.
// The very first time the ServiceInvokerOptions is initialized, 
public class ClientConfigMapper {
	// Called to store the shared data between a group definition and an instance definition.  This shared data is represented by
	// clientInstanceConfig.  When called for an instance definition, there is extra info in "clientConfig" (the parent element) that
	// we need to also store.
	public static void map(String adminName, String filename, Element clientConfig, Element clientInstanceConfig, ClientConfigHolder dst) throws ServiceCreationException {
		if (clientConfig != null) {
			mapClientConfig(adminName, filename, clientConfig, dst);
		}
		
		if (clientInstanceConfig == null) {
			return;
			// we allow client instance config to be absent - this would be a trivial config with no overridden parameter values.
		}

		NodeList customSerializers = null;
		
		Element invocationOptions = DomParseUtils.getSingleElement(filename, clientInstanceConfig, "invocation-options");
		if (invocationOptions != null) {
			mapInvocationOptions(filename, invocationOptions, dst);
			customSerializers = DomParseUtils.getImmediateChildrenByTagName(invocationOptions, "custom-serializers");
		}
		Element cachePolicyConfig = DomParseUtils.getSingleElement(filename, clientInstanceConfig, "cache-policy");
		if (cachePolicyConfig != null) {
			mapCachePolicyConfig(filename, cachePolicyConfig, dst);
		}
		Element pipelineConfig = DomParseUtils.getSingleElement(filename, clientInstanceConfig, "pipeline-config");
		NodeList protocolProcessors = DomParseUtils.getImmediateChildrenByTagName(clientInstanceConfig, "protocol-processor");
		NodeList transports = DomParseUtils.getImmediateChildrenByTagName(clientInstanceConfig, "transport");
		Element dataBindingConfig = DomParseUtils.getSingleElement(filename, clientInstanceConfig, "data-binding-config");

		OptionList requestHeaderMappingOptions = DomParseUtils.getOptionList(filename, clientInstanceConfig, "request-header-mapping-options");
		if (requestHeaderMappingOptions != null) {
			dst.setRequestHeaderMappingOptions(requestHeaderMappingOptions);
		}
		OptionList responseHeaderMappingOptions = DomParseUtils.getOptionList(filename, clientInstanceConfig, "response-header-mapping-options");
		if (responseHeaderMappingOptions != null) {
			dst.setResponseHeaderMappingOptions(responseHeaderMappingOptions);
		}

		MessageProcessorConfigMapper.map(filename, customSerializers, null, dst.getErrorDataProviderClass(), pipelineConfig, protocolProcessors, transports, dataBindingConfig, dst.getMessageProcessorConfig());
	}

	private static void mapClientConfig(String adminName, String filename, Element clientConfig, ClientConfigHolder dst) 
		throws ServiceCreationException {		
		MetadataPropertyConfigHolder metadataProps = ClientConfigManager.getInstance().getMetadataPropertyConfigHolder(adminName);
		dst.setMetaData(metadataProps);
		String serviceName;
		if (metadataProps.getSmpVersion() >= 1.1d) {
			StringBuilder serviceqname = new StringBuilder();
			serviceqname.append('{').append(metadataProps.getServiceNamespace()).append('}');
			serviceqname.append(metadataProps.getServiceName());
			serviceName = serviceqname.toString();					
		}
		else {
			serviceName = clientConfig.getAttribute("service-name");
			if(serviceName.isEmpty())
				serviceName = metadataProps.getServiceName();
		}
		QName serviceQname = ServiceNameUtils.normalizeQName(QName.valueOf(serviceName));		
		dst.setServiceQName(serviceQname);		
		String serviceLocation = DomParseUtils.getElementText(filename, clientConfig, "service-location");
		if (serviceLocation != null) {
			dst.setServiceLocation(serviceLocation);
		}		
		Element locationMappings = DomParseUtils.getSingleElement(filename, clientConfig, "service-location-mappings");
		if (locationMappings != null) {
			mapLocationMappings(filename, locationMappings, dst);
		}
		String serviceIntfcClassName = DomParseUtils.getElementText(filename, clientConfig, "service-interface-class-name");
		if (serviceIntfcClassName != null) {
			dst.setServiceInterfaceClassName(serviceIntfcClassName);
		}
		String wsdlLocation = DomParseUtils.getElementText(filename, clientConfig, "wsdl-location");
		if (wsdlLocation != null) {
			dst.setWsdlLocation(wsdlLocation);
		}		
	}
	
	private static void mapLocationMappings(String filename, Element locationMappings, ClientConfigHolder dstConfig) throws ServiceCreationException {
		if (locationMappings == null) {
			return;
		}
		NodeList locationMappingList = DomParseUtils.getImmediateChildrenByTagName(locationMappings, "service-location-mapping");
		Map<String, String> locationMap = new HashMap<String, String>(locationMappingList.getLength());
		
		for (int i = 0; i < locationMappingList.getLength(); i++) {
			Element element = (Element)locationMappingList.item(i);
			String env = DomParseUtils.getElementText(filename, element, "name", true);
			if (env == null || env.length() == 0) {
				DomParseUtils.throwError(filename, "Missing required value on element 'name'" );
			}
			String url = DomParseUtils.getElementText(filename, element, "url", true);
			if (url == null || url.length() == 0) {
				DomParseUtils.throwError(filename, "Missing required value on element 'url'" );
			}
			
			try {
				// Invoke the variableResolver as done in the ClientServiceDescFactory.createDesc()
				url = VariableResolver.processString(url);
				new URL(url);
			} catch (MalformedURLException e) {
				DomParseUtils.throwError(filename, "Invalid URL: " + url + " String specified for " + env);
			}
						
			locationMap.put(env, url);
		}
		
		if (locationMap.size() > 0)
			dstConfig.setServiceLocationMap(locationMap);
		
	}

	private static void mapInvocationOptions(String filename, Element invocationOptions, ClientConfigHolder dstConfig) throws ServiceCreationException {
		if (invocationOptions == null) {
			return;
		}
		Element preferredTransport = DomParseUtils.getSingleElement(filename, invocationOptions, "preferred-transport");
		if (preferredTransport != null) {
			mapPreferredTransport(filename, preferredTransport, dstConfig);
		}
		Element g11nOptions = DomParseUtils.getSingleElement(filename, invocationOptions, "G11N-options");
		if (g11nOptions != null) {
			mapG11NOptions(filename, g11nOptions, dstConfig);
		}
		String useServiceVersion = DomParseUtils.getElementText(filename, invocationOptions, "use-service-version");
		if (useServiceVersion != null) {
			dstConfig.setServiceVersion(useServiceVersion);
		}
		String monitoringLevelStr = DomParseUtils.getElementText(filename, invocationOptions, "monitoring-level");
		if (monitoringLevelStr != null) {
			MonitoringLevel level = DomParseUtils.mapMonitoringLevel(filename, monitoringLevelStr);
			dstConfig.setMonitoringLevel(level);
		}
		String invocationUseCase = DomParseUtils.getElementText(filename, invocationOptions, "invocation-use-case");
		if (invocationUseCase != null) {
			dstConfig.setInvocationUseCase(invocationUseCase);
		}
		String consumerId = DomParseUtils.getElementText(filename, invocationOptions, "consumer-id");
		if (consumerId != null && !consumerId.isEmpty()) {
			dstConfig.setConsumerId(consumerId);
			// Also setting the use-case to the consumerId, as this supersedes the other
			dstConfig.setInvocationUseCase(SOAConstants.APPNAME_PREFIX + consumerId);
		}
		String requestDataBinding = DomParseUtils.getElementText(filename, invocationOptions, "request-data-binding");
		if (requestDataBinding != null) {
			dstConfig.setRequestDataBinding(requestDataBinding);
		}
		String responseDataBinding = DomParseUtils.getElementText(filename, invocationOptions, "response-data-binding");
		if (responseDataBinding != null) {
			dstConfig.setResponseDataBinding(responseDataBinding);
		}
		String messageProtocol = DomParseUtils.getElementText(filename, invocationOptions, "message-protocol");
		if (messageProtocol != null) {
			dstConfig.setMessageProtocol(messageProtocol);
		}
		String responseTransport = DomParseUtils.getElementText(filename, invocationOptions, "response-transport");
		if (responseTransport != null) {
			dstConfig.setResponseTransport(responseTransport);
		}
		Element retryOptions = DomParseUtils.getSingleElement(filename, invocationOptions, "retry-options");
		if (retryOptions != null) {
			mapRetryOptions(filename, retryOptions, dstConfig);
		}
		String customErrorResponseAdapter = DomParseUtils.getElementText(filename, invocationOptions, "custom-error-response-adapter");
		if (customErrorResponseAdapter != null) {
			dstConfig.setCustomErrorResponseAdapter(customErrorResponseAdapter);
		}
		String errorDataProviderClass = DomParseUtils.getElementText(filename, invocationOptions, "error-data-provider-class-name");
		if (errorDataProviderClass != null) {
			dstConfig.setErrorDataProviderClass(errorDataProviderClass);
		}
		Element markdownOptions = DomParseUtils.getSingleElement(filename, invocationOptions, "markdown-options");
		if (markdownOptions != null) {
			mapMarkdownOptions(filename, markdownOptions, dstConfig);
		}
		Element useHttpGet = DomParseUtils.getSingleElement(filename, invocationOptions, "use-rest");
		if (useHttpGet != null) {
			String useHttpGetStr = DomParseUtils.getText(useHttpGet);
			if (useHttpGetStr != null && !useHttpGetStr.equalsIgnoreCase("false")) {
				dstConfig.setUseREST(Boolean.TRUE);
			} else {
				dstConfig.setUseREST(Boolean.FALSE);
			}
			Integer maxHttpGetUrlByteLength = DomParseUtils.getAttributeInteger(filename, useHttpGet, "max-url-byte-length");
			if (maxHttpGetUrlByteLength != null) {
				dstConfig.setMaxURLLengthForREST(maxHttpGetUrlByteLength);
			}
		}
		//
		String urlPathInfo = DomParseUtils.getElementText(filename, invocationOptions, "url-path-info");
		if (urlPathInfo != null) {
			dstConfig.setUrlPathInfo(urlPathInfo);
		}
		
	}
	
	private static void mapCachePolicyConfig(String filename,
			Element cachePolicyConfig, ClientConfigHolder dstConfig) throws ServiceCreationException {
		
		if (cachePolicyConfig == null) {
			return;
		}
		String cacheProviderClass = DomParseUtils.getElementText(filename, cachePolicyConfig, "cache-provider-class-name", true);
		if (cacheProviderClass != null && !cacheProviderClass.isEmpty()) {
			dstConfig.setCacheProviderClass(cacheProviderClass);
		}
		Boolean disableCacheOnLocal = DomParseUtils.getElementBoolean(filename, cachePolicyConfig, "disable-cache-on-local");
		if (disableCacheOnLocal != null)
			dstConfig.setCacheDisabledOnLocal(disableCacheOnLocal);
		
		Boolean skipCacheOnError = DomParseUtils.getElementBoolean(filename, cachePolicyConfig, "skip-cache-on-error");
		if (skipCacheOnError != null)
			dstConfig.setSkipCacheOnError(skipCacheOnError);
	}

	private static void mapRetryOptions(String filename, Element retryOptions, ClientConfigHolder dstConfig) throws ServiceCreationException {
		Integer appLevelNumRetries = DomParseUtils.getElementInteger(filename, retryOptions, "app-level-num-retries");
		if (appLevelNumRetries != null) {
			dstConfig.setAppLevelNumRetries(appLevelNumRetries);
		}
		String appLevelRetryHandler = DomParseUtils.getElementText(filename, retryOptions, "app-level-retry-handler");
		if (appLevelRetryHandler != null) {
			dstConfig.setRetryHandlerClass(appLevelRetryHandler);
		}
		List<String> httpRetryCodes = DomParseUtils.getStringList(filename, retryOptions, "retry-transport-status-code");
		if (httpRetryCodes != null) {
			Set<String> outCodes = new HashSet<String>(httpRetryCodes);
			dstConfig.setRetryTransportStatusCodes(outCodes);
		}
		List<String> exceptionRetryClasses = DomParseUtils.getStringList(filename, retryOptions, "retry-exception-class");
		if (exceptionRetryClasses != null) {
			Set<String> outClasses = new HashSet<String>(exceptionRetryClasses);
			dstConfig.setRetryExceptionClasses(outClasses);
		}
		List<String> retryErrorIds = DomParseUtils.getStringList(filename, retryOptions, "retry-error-id");
		if (retryErrorIds != null) {
			Set<String> outErrorIds = new HashSet<String>(retryErrorIds);
			dstConfig.setRetryErrorIds(outErrorIds);
		}
	}

	private static void mapMarkdownOptions(String filename, Element markdownOptions, ClientConfigHolder dstConfig) throws ServiceCreationException {
		Boolean enabled = DomParseUtils.getElementBoolean(filename, markdownOptions, "enable-auto-markdown");
		if (enabled != null) {
			dstConfig.setMarkdownEnabled(enabled);
		}
		Integer errCountThreshold = DomParseUtils.getElementInteger(filename, markdownOptions, "error-count-threshold");
		if (errCountThreshold != null) {
			dstConfig.setMarkdownErrCountThreshold(errCountThreshold);
		}
		String stateFactory = DomParseUtils.getElementText(filename, markdownOptions, "markdown-state-factory");
		if (stateFactory != null) {
			dstConfig.setMarkdownStateFactoryClass(stateFactory);
		}
		List<String> httpCodes = DomParseUtils.getStringList(filename, markdownOptions, "transport-status-code");
		if (httpCodes != null) {
			Set<String> outCodes = new HashSet<String>(httpCodes);
			dstConfig.setMarkdownTransportStatusCodes(outCodes);
		}
		List<String> exceptionClasses = DomParseUtils.getStringList(filename, markdownOptions, "exception-class");
		if (exceptionClasses != null) {
			Set<String> outClasses = new HashSet<String>(exceptionClasses);
			dstConfig.setMarkdownExceptionClasses(outClasses);
		}
		List<String> errorIds = DomParseUtils.getStringList(filename, markdownOptions, "error-id");
		if (errorIds != null) {
			Set<String> outErrorIds = new HashSet<String>(errorIds);
			dstConfig.setMarkdownErrorIds(outErrorIds);
		}
	}

	private static void mapPreferredTransport(String filename, Element preferredTransport, ClientConfigHolder dstConfig) throws ServiceCreationException {
		String name = preferredTransport.getAttribute("name");
		if (name != null) {
			dstConfig.setPreferredTransport(name.toUpperCase()); // always store transport names in upper case
		}
		Element overrideOptions = DomParseUtils.getSingleElement(filename, preferredTransport, "override-options");
		if (overrideOptions != null) {
			TransportOptions outOptions = DomParseUtils.mapTransportOptions(filename, overrideOptions);
			dstConfig.setTransportOverrideOptions(outOptions);
		}
		Map<String, String> overrideHeaderOptionsMap = dstConfig.getTransportOverrideHeaderOptions();
		OptionList overrideHeaderOptions = DomParseUtils.getOptionList(filename, preferredTransport, "override-header-options");
		DomParseUtils.storeNVListToHashMap(filename, overrideHeaderOptions, overrideHeaderOptionsMap);
		
	}
		
	private static void mapG11NOptions(String filename, Element g11nOptions, ClientConfigHolder dstConfig) throws ServiceCreationException {
		String preferredEncoding = DomParseUtils.getElementText(filename, g11nOptions, "preferred-encoding");
		if (preferredEncoding != null) {
			dstConfig.setPreferredEncoding(preferredEncoding);
		}
		String preferredLocale = DomParseUtils.getElementText(filename, g11nOptions, "preferred-locale");
		if (preferredLocale != null) {
			dstConfig.setPreferredLocale(preferredLocale);
		}
		String preferredGlobalId = DomParseUtils.getElementText(filename, g11nOptions, "preferred-global-id");
		if (preferredGlobalId != null) {
			dstConfig.setPreferredGlobalId(preferredGlobalId);
		}
	}

	public static ClientConfigHolder applyConfigs(String adminName, String clientName, String envName,String configFilename,
		String groupFilename, Element clientGroup, Element clientConfig) throws ServiceCreationException
	{
		ClientConfigHolder holder = new ClientConfigHolder(adminName, clientName,envName);
		holder.setConfigFilename(configFilename);
		holder.setGroupFilename(groupFilename);
		if (clientGroup != null) { // If the instance config has referenced a group, get the group's instance data - same type as client-instance-config
			Element clientInstanceInGroup = DomParseUtils.getSingleElement(groupFilename, clientGroup, "client-config");
			map(adminName, groupFilename, null, clientInstanceInGroup, holder);
		}
        if (clientConfig != null) {
        	Element clientInstance = DomParseUtils.getSingleElement(groupFilename, clientConfig, "client-instance-config");
        	map(adminName, configFilename, clientConfig, clientInstance, holder);
        }
		return holder;
	}
	
	public static ClientConfigHolder getConfigFromBaseConfig(
			ClientConfigHolder baseConfig, String adminName, String clientName,
			String envName, QName svcQName,
			TypeMappingConfigHolder typeMappingsCfg) {
		return new ClientConfigHolder(baseConfig, adminName, clientName,
				envName, svcQName, null, null, typeMappingsCfg, null, null,
				null, null, null, null);
	}

}
