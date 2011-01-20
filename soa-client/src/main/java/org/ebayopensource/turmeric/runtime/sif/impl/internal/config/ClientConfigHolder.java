/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.CommonConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.ConfigUtils;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MessageProcessorConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MetadataPropertyConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.OperationPropertyConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.OptionList;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.TypeMappingConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.monitoring.ErrorStatusOptions;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;



public class ClientConfigHolder extends CommonConfigHolder {
	// Invocation options
	// When adding properties, please ensure that copy(), dump(), and getters/setters
	// are all covered for the new properties.  Make sure setters call checkReadOnly().
	private Integer m_appLevelNumRetries;
	private Boolean m_useREST;
	private Integer m_maxURLLengthForREST;
	private String m_retryHandlerClass;
	private Boolean m_markdownEnabled;
	private Integer m_markdownErrCountThreshold;
	private String m_markdownStateFactoryClass;
	private String m_preferredEncoding;
	private String m_preferredLocale;
	private String m_requestDataBinding;
	private String m_responseDataBinding;
	private String m_preferredTransport;
	private String m_serviceLocation;
	private Map<String, String> m_serviceLocationMap = CollectionUtils.EMPTY_STRING_MAP;
	private String m_wsdlLocation;
	private String m_requestPayloadLog;
	private String m_requestPayloadCalLog;
	private String m_responsePayloadLog;
	private String m_responsePayloadCalLog;
	private TransportOptions m_transportOverrideOptions = new TransportOptions();
	private Map<String, String> m_transportOverrideHeaderOptions = new HashMap<String, String>();
	private String m_serviceVersion;
	private String m_invocationUseCase;
	private String m_consumerId;
	private String m_preferredGlobalId;
	private String m_responseTransport;
	private String m_messageProtocol;
	private String m_customErrorResponseAdapter;
	private String m_errorDataProviderClass;	
	private String m_cacheProviderClass; 
	private Boolean m_disableCacheOnLocal = Boolean.TRUE; // Default is to skip cache
	private Boolean m_skipCacheOnError =  Boolean.FALSE; // Default to continue on error
	private Collection<String> m_retryTransportStatusCodes = CollectionUtils.EMPTY_STRING_SET;
	private Collection<String> m_retryExceptionClasses = CollectionUtils.EMPTY_STRING_SET;
	private Collection<String> m_retryErrorIds = CollectionUtils.EMPTY_STRING_SET;
	private Collection<String> m_markdownTransportStatusCodes = CollectionUtils.EMPTY_STRING_SET;
	private Collection<String> m_markdownExceptionClasses = CollectionUtils.EMPTY_STRING_SET;
	private Collection<String> m_markdownErrorIds = CollectionUtils.EMPTY_STRING_SET;
	private String m_clientName;	// The name of the client, which was used as the folder name when looking up the config file.
	private String m_envName;       // the name of environemnt , which was used as a folder name for cc.xml. null value for old cc.xml
	private String m_urlPathInfo;
	private static final char NL = '\n';
	private boolean m_ignoreServiceVersion = false;
	

	public ClientConfigHolder(ClientConfigHolder configToCopy, String adminName, String clientName,String envName,QName serviceQName,
			MessageProcessorConfigHolder messageProcessorConfig,
			MetadataPropertyConfigHolder metadata,
			TypeMappingConfigHolder typeMappings,
			OperationPropertyConfigHolder operationProperties,
			String serviceInterfaceClassName,
			MonitoringLevel monitoringLevel,
			OptionList requestHeaderMappingOptions,
			OptionList responseHeaderMappingOptions,
			ErrorStatusOptions errorStatusOptions) {
		super(configToCopy, adminName, serviceQName, messageProcessorConfig,
				metadata, typeMappings, operationProperties,
				serviceInterfaceClassName, monitoringLevel,
				requestHeaderMappingOptions, responseHeaderMappingOptions,
				errorStatusOptions);
		m_clientName = clientName == null ? configToCopy.m_clientName
				: clientName;
		m_envName = envName == null ? configToCopy.m_envName : envName;

		m_readOnly = false;
		m_appLevelNumRetries = configToCopy.m_appLevelNumRetries;
		m_useREST = configToCopy.m_useREST;
		m_maxURLLengthForREST = configToCopy.m_maxURLLengthForREST;
		m_retryHandlerClass = configToCopy.m_retryHandlerClass;
		m_markdownEnabled = configToCopy.m_markdownEnabled;
		m_markdownErrCountThreshold = configToCopy.m_markdownErrCountThreshold;
		m_markdownStateFactoryClass = configToCopy.m_markdownStateFactoryClass;
		m_preferredEncoding = configToCopy.m_preferredEncoding;
		m_preferredLocale = configToCopy.m_preferredLocale;
		m_requestDataBinding = configToCopy.m_requestDataBinding;
		m_responseDataBinding = configToCopy.m_responseDataBinding;
		m_preferredTransport = configToCopy.m_preferredTransport;
		m_serviceLocation = configToCopy.m_serviceLocation;
		m_serviceLocationMap = new HashMap<String, String>(configToCopy.m_serviceLocationMap);
		m_wsdlLocation = configToCopy.m_wsdlLocation;
		m_requestPayloadLog = configToCopy.m_requestPayloadLog;
		m_requestPayloadCalLog = configToCopy.m_requestPayloadCalLog;
		m_responsePayloadLog = configToCopy.m_responsePayloadLog;
		m_responsePayloadCalLog = configToCopy.m_responsePayloadCalLog;
		m_transportOverrideOptions = ConfigUtils
				.copyTransportOptions(configToCopy.m_transportOverrideOptions);
		m_serviceVersion = configToCopy.m_serviceVersion;
		m_invocationUseCase = configToCopy.m_invocationUseCase;
		m_consumerId = configToCopy.m_consumerId;
		m_preferredGlobalId = configToCopy.m_preferredGlobalId;
		m_responseTransport = configToCopy.m_responseTransport;
		m_messageProtocol = configToCopy.m_messageProtocol;
		m_customErrorResponseAdapter = configToCopy.m_customErrorResponseAdapter;
		m_retryTransportStatusCodes = new HashSet<String>(
				configToCopy.m_retryTransportStatusCodes);
		m_retryExceptionClasses = new HashSet<String>(configToCopy.m_retryExceptionClasses);
		m_retryErrorIds = new HashSet<String>(configToCopy.m_retryErrorIds);
		m_markdownTransportStatusCodes = new HashSet<String>(
				configToCopy.m_markdownTransportStatusCodes);
		m_markdownExceptionClasses = new HashSet<String>(
				configToCopy.m_markdownExceptionClasses);
		m_markdownErrorIds = new HashSet<String>(configToCopy.m_markdownErrorIds);
		m_urlPathInfo = configToCopy.m_urlPathInfo;
		m_cacheProviderClass = configToCopy.m_cacheProviderClass;
		m_disableCacheOnLocal = configToCopy.m_disableCacheOnLocal;
		m_skipCacheOnError = configToCopy.m_skipCacheOnError;
		m_ignoreServiceVersion = true;
	}

	
	public ClientConfigHolder(String adminName, String clientName,String envName) {
		super(adminName);
		m_clientName = clientName;
		m_envName = envName;

	}

	/*
	 * Create a deep copy of the object.
	 */
	public ClientConfigHolder copy() {
		ClientConfigHolder newCH = new ClientConfigHolder(getAdminName(), m_clientName,m_envName);
		newCH.m_readOnly = false;
		newCH.copyMemberData(this);
		newCH.m_appLevelNumRetries = m_appLevelNumRetries;
		newCH.m_useREST = m_useREST;
		newCH.m_maxURLLengthForREST = m_maxURLLengthForREST;
		newCH.m_retryHandlerClass = m_retryHandlerClass;
		newCH.m_markdownEnabled = m_markdownEnabled;
		newCH.m_markdownErrCountThreshold = m_markdownErrCountThreshold;
		newCH.m_markdownStateFactoryClass = m_markdownStateFactoryClass;
		newCH.m_preferredEncoding = m_preferredEncoding;
		newCH.m_preferredLocale = m_preferredLocale;
		newCH.m_requestDataBinding = m_requestDataBinding;
		newCH.m_responseDataBinding = m_responseDataBinding;
		newCH.m_preferredTransport = m_preferredTransport;
		newCH.m_serviceLocation = m_serviceLocation;
		newCH.m_serviceLocationMap = new HashMap<String, String>(m_serviceLocationMap);
		newCH.m_wsdlLocation = m_wsdlLocation;
		newCH.m_requestPayloadLog = m_requestPayloadLog;
		newCH.m_requestPayloadCalLog = m_requestPayloadCalLog;
		newCH.m_responsePayloadLog = m_responsePayloadLog;
		newCH.m_responsePayloadCalLog = m_responsePayloadCalLog;
		newCH.m_transportOverrideOptions = ConfigUtils.copyTransportOptions(m_transportOverrideOptions);
		newCH.m_transportOverrideHeaderOptions = new HashMap<String, String>(m_transportOverrideHeaderOptions);
		newCH.m_serviceVersion = m_serviceVersion;
		newCH.m_invocationUseCase = m_invocationUseCase;
		newCH.m_consumerId = m_consumerId;
		newCH.m_preferredGlobalId = m_preferredGlobalId;
		newCH.m_responseTransport = m_responseTransport;
		newCH.m_messageProtocol = m_messageProtocol;
		newCH.m_customErrorResponseAdapter = m_customErrorResponseAdapter;
		newCH.m_retryTransportStatusCodes = new HashSet<String>(m_retryTransportStatusCodes);
		newCH.m_retryExceptionClasses = new HashSet<String>(m_retryExceptionClasses);
		newCH.m_retryErrorIds = new HashSet<String>(m_retryErrorIds);
		newCH.m_markdownTransportStatusCodes = new HashSet<String>(m_markdownTransportStatusCodes);
		newCH.m_markdownExceptionClasses = new HashSet<String>(m_markdownExceptionClasses);
		newCH.m_markdownErrorIds = new HashSet<String>(m_markdownErrorIds);
		newCH.m_urlPathInfo = m_urlPathInfo;
		newCH.m_cacheProviderClass = m_cacheProviderClass;
		newCH.m_disableCacheOnLocal = m_disableCacheOnLocal;
		newCH.m_skipCacheOnError = m_skipCacheOnError;
		return newCH;
	}
	
	/**
	 * Returns the application level number of retries configured.
	 */
	public Integer getAppLevelNumRetries() {
		return m_appLevelNumRetries;
	}

	/**
	 * Sets the application level number of retries to attempt (per failed, retryable invocation)
	 * @param numRetries  the m_numAppLevelRetries to set
	 */
	public void setAppLevelNumRetries(Integer numRetries) {
		checkReadOnly();
		m_appLevelNumRetries = numRetries;
	}

	/**
	 * Returns the m_useREST
	 */
	public Boolean getUseREST() {
		return m_useREST;
	}

	/**
	 * @param useREST the m_useREST to set
	 */
	public void setUseREST(Boolean useREST) {
		checkReadOnly();
		m_useREST = useREST;
	}

	/**
	 * Returns the m_maxURLLengthForREST
	 */
	public Integer getMaxURLLengthForREST() {
		return m_maxURLLengthForREST;
	}

	/**
	 * @param maxURLLengthForREST the m_maxURLLengthForREST to set
	 */
	public void setMaxURLLengthForREST(Integer maxURLLengthForREST) {
		checkReadOnly();
		m_maxURLLengthForREST = maxURLLengthForREST;
	}

	/**
	 * Returns the class name of the application retry handler
	 */
	public String getRetryHandlerClass() {
		return m_retryHandlerClass;
	}

	/**
	 * Sets the class name of the application retry handler
	 * @param m_retryHandlerClass  the name of the retry handler class to set
	 */
	public void setRetryHandlerClass(String retryHandlerClass) {
		checkReadOnly();
		m_retryHandlerClass = retryHandlerClass;
	}

	/**
	 * Returns the auto markdown error count threshold configured.
	 */
	public Boolean getMarkdownEnabled() {
		return m_markdownEnabled;
	}

	/**
	 * Sets the auto markdown error count threshold
	 */
	public void setMarkdownEnabled(Boolean value) {
		checkReadOnly();
		m_markdownEnabled = value;
	}

	/**
	 * Returns the auto markdown error count threshold configured.
	 */
	public Integer getMarkdownErrCountThreshold() {
		return m_markdownErrCountThreshold;
	}

	/**
	 * Sets the auto markdown error count threshold
	 */
	public void setMarkdownErrCountThreshold(Integer value) {
		checkReadOnly();
		m_markdownErrCountThreshold = value;
	}

	/**
	 * Returns the class name of the auto markdown state factory
	 */
	public String getMarkdownStateFactoryClass() {
		return m_markdownStateFactoryClass;
	}

	/**
	 * Sets the class name of the auto markdown state factory
	 */
	public void setMarkdownStateFactoryClass(String value) {
		checkReadOnly();
		m_markdownStateFactoryClass = value;
	}

	/**
	 * Returns the preferred character set encoding to be used by this client
	 */
	public String getPreferredEncoding() {
		return m_preferredEncoding;
	}
	/**
	 * Sets the preferred character set encoding to be used by this client
	 * @param encoding  the encoding to set
	 */
	public void setPreferredEncoding(String encoding) {
		checkReadOnly();
		m_preferredEncoding = encoding;
	}

	/**
	 * Returns the preferred locale to be used by this client
	 */
	public String getPreferredLocale() {
		return m_preferredLocale;
	}

	/**
	 * Sets the preferred locale to be used by this client
	 * @param locale  the preferred locale
	 */
	public void setPreferredLocale(String locale) {
		checkReadOnly();
		m_preferredLocale = locale;		
	}
	
	/**
	 * Returns the request data binding to be used by this client
	 */
	public String getRequestDataBinding() {
		return m_requestDataBinding;
	}
	
	/**
	 * Sets the request data binding to be used by this client
	 * @param prefBinding  the data binding
	 */
	public void setRequestDataBinding(String prefBinding) {
		checkReadOnly();
		m_requestDataBinding = prefBinding;
	}

	/**
	 * Returns the response data binding to be used by this client
	 */
	public String getResponseDataBinding() {
		return m_responseDataBinding;
	}
	
	/**
	 * Sets the response data binding to be used by this client
	 * @param prefBinding  the data binding
	 */
	public void setResponseDataBinding(String prefBinding) {
		checkReadOnly();
		m_responseDataBinding = prefBinding;
	}

	/**
	 * Returns the preferred transport to be used by this client
	 */
	public String getPreferredTransport() {
		return m_preferredTransport;
	}
	
	/**
	 * Sets the preferred transport to be used by this client
	 * @param prefBinding  the preferred transport
	 */
	public void setPreferredTransport(String prefTransport) {
		checkReadOnly();
		m_preferredTransport = prefTransport;
	}

	/**
	 * Returns the transport override options associated with 
	 */
	public TransportOptions getTransportOverrideOptions() {
		if (isReadOnly()) {
			return ConfigUtils.copyTransportOptions(m_transportOverrideOptions);
		}
		return m_transportOverrideOptions;
	}
	
	public Map<String, String> getTransportOverrideHeaderOptions() {
		if (isReadOnly()) {
			return new HashMap<String, String>(m_transportOverrideHeaderOptions);
		}
		
		return m_transportOverrideHeaderOptions;
	}

	/**
	 * @param options the m_transportOverrideOptions to set
	 */
	public void setTransportOverrideOptions(TransportOptions options) {
		m_transportOverrideOptions = options;
	}

	public String getServiceVersion() {
		return m_serviceVersion;
	}

	public void setServiceVersion(String version) {
		checkReadOnly();
		m_serviceVersion = version;
	}
	
	public Collection<String> getRetryTransportStatusCodes() {
		if (isReadOnly()) {
			return new HashSet<String>(m_retryTransportStatusCodes);
		}
		return m_retryTransportStatusCodes;
	}

	public void setRetryTransportStatusCodes(Collection<String> retryTransportStatusCodes) {
		checkReadOnly();
		m_retryTransportStatusCodes = retryTransportStatusCodes;
	}
	
	public Collection<String> getRetryExceptionClasses() {
		if (isReadOnly()) {
			return new HashSet<String>(m_retryExceptionClasses);
		}
		return m_retryExceptionClasses;
	}

	public void setRetryExceptionClasses(Collection<String> retryExceptionClasses) {
		checkReadOnly();
		m_retryExceptionClasses = retryExceptionClasses;
	}
	
	public Collection<String> getRetryErrorIds() {
		if (isReadOnly()) {
			return new HashSet<String>(m_retryErrorIds);
		}
		return m_retryErrorIds;
	}

	public void setRetryErrorIds(Collection<String> errorIds) {
		checkReadOnly();
		m_retryErrorIds = errorIds;
	}
	
	public Collection<String> getMarkdownTransportStatusCodes() {
		if (isReadOnly()) {
			return new HashSet<String>(m_markdownTransportStatusCodes);
		}
		return m_markdownTransportStatusCodes;
	}

	public void setMarkdownTransportStatusCodes(Collection<String> value) {
		checkReadOnly();
		m_markdownTransportStatusCodes = value;
	}
	
	public Collection<String> getMarkdownExceptionClasses() {
		if (isReadOnly()) {
			return new HashSet<String>(m_markdownExceptionClasses);
		}
		return m_markdownExceptionClasses;
	}

	public void setMarkdownExceptionClasses(Collection<String> value) {
		checkReadOnly();
		m_markdownExceptionClasses = value;
	}
	
	public Collection<String> getMarkdownErrorIds() {
		if (isReadOnly()) {
			return new HashSet<String>(m_markdownErrorIds);
		}
		return m_markdownErrorIds;
	}

	public void setMarkdownErrorIds(Set<String> value) {
		checkReadOnly();
		m_markdownErrorIds = value;
	}
	
	/**
	 * Returns the consumerId
	 */
	public String getConsumerId() {
		return m_consumerId;
	}

	/**
	 * @param consumerId the consumer id to set
	 */
	public void setConsumerId(String consumerId) {
		checkReadOnly();
		m_consumerId = consumerId;
	}
	
	/**
	 * Returns the invocation use case
	 */
	public String getInvocationUseCase() {
		return m_invocationUseCase;
	}

	/**
	 * @param useCase the invocation use case to set
	 */
	public void setInvocationUseCase(String useCase) {
		checkReadOnly();
		m_invocationUseCase = useCase;
	}

	/**
	 * Returns the invocation use case
	 */
	public String getCustomErrorResponseAdapter() {
		return m_customErrorResponseAdapter;
	}

	/**
	 * @param useCase the invocation use case to set
	 */
	public void setCustomErrorResponseAdapter(String value) {
		checkReadOnly();
		m_customErrorResponseAdapter = value;
	}

	/**
	 * Returns the error data provider class name
	 */
	public String getErrorDataProviderClass() {
		return m_errorDataProviderClass;
	}

	/**
	 * @param value the error data provider class name to set
	 */
	public void setErrorDataProviderClass(String value) {
		checkReadOnly();
		m_errorDataProviderClass = value;
	}
	
	/**
	 * Returns the cache provider class name
	 */
	public String getCacheProviderClass() {
		return m_cacheProviderClass;
	}

	/**
	 * @param value the cache provider class name to set
	 */
	public void setCacheProviderClass(String value) {
		checkReadOnly();
		m_cacheProviderClass = value;
	}
	
	/**
	 * Returns if cache is to be skipped on local transport
	 */
	public Boolean isCacheDisabledOnLocal() {
		return m_disableCacheOnLocal;
	}

	/**
	 * @param value set cache skip on local flag
	 */
	public void setCacheDisabledOnLocal(Boolean value) {
		checkReadOnly();
		m_disableCacheOnLocal = value;
	}
	
	/**
	 * Returns if cache is to be skipped on errors during provider init
	 */
	public Boolean isSkipCacheOnError() {
		return m_skipCacheOnError;
	}

	/**
	 * @param value set cache skip on local flag
	 */
	public void setSkipCacheOnError(Boolean value) {
		checkReadOnly();
		m_skipCacheOnError = value;
	}


	/**
	 * Returns the m_preferredGlobalId
	 */
	public String getPreferredGlobalId() {
		return m_preferredGlobalId;
	}

	/**
	 * @param globalId the m_preferredGlobalId to set
	 */
	public void setPreferredGlobalId(String globalId) {
		checkReadOnly();
		m_preferredGlobalId = globalId;
	}

	public String getMessageProtocol() {
		return m_messageProtocol;
	}

	public void setMessageProtocol(String protocol) {
		checkReadOnly();
		m_messageProtocol = protocol;
	}

	public String getResponseTransport() {
		return m_responseTransport;
	}

	public void setResponseTransport(String transport) {
		checkReadOnly();
		m_responseTransport = transport;
	}

	/**
	 * Returns the m_serviceLocation
	 */
	public String getServiceLocation() {
		return m_serviceLocation;
	}

	/**
	 * @param location the m_serviceLocation to set
	 */
	public void setServiceLocation(String location) {
		checkReadOnly();
		m_serviceLocation = location;
	}
	
	/**
	 * Returns the m_serviceLocationMap
	 */
	public Map<String, String> getServiceLocationMap() {
		if (isReadOnly()) 
			return new HashMap<String,String>(m_serviceLocationMap);
		return m_serviceLocationMap;
	}
	
	/**
	 * @param locationMap the m_serviceLocationMap to set
	 */
	public void setServiceLocationMap(Map<String, String> locationMap) {
		checkReadOnly();
		m_serviceLocationMap = locationMap;
	}
	/**
	 * 
	 * @return environment name used for a cc.xml
	 */
	public String getEnvName() {
		return m_envName;
	}
	
	/**
	 * Sets the serviceLocation from the map for only valid keys
	 * @param env the environment to determine the serviceLocation from LocationMappings
	 */
	public String setServiceLocationFromLocationMapping(String env) {
		String locationUrl = m_serviceLocationMap.get(env);
	
		if (locationUrl != null) {
			setServiceLocation(locationUrl);
		} else {
			LogManager.getInstance(this.getClass()).log(Level.SEVERE, 
					 "No Service location mapped for " + env );
		}
		
		return locationUrl;
	}
	

	/**
	 * Returns the m_wsdlLocation
	 */
	public String getWsdlLocation() {
		return m_wsdlLocation;
	}

	/**
	 * @param location the m_wsdlLocation to set
	 */
	public void setWsdlLocation(String location) {
		checkReadOnly();
		m_wsdlLocation = location;
	}
	
	public String getClientName() {
		return m_clientName;
	}

	public void setClientName(String name) {
		checkReadOnly();
		m_clientName = name;
	}
	
	/**
	 * Returns the URI Path
	 */
	public String getUrlPathInfo() {
		return m_urlPathInfo;
	}

	/**
	 * Sets the URI Path
	 * @param urlPathInfo  the URI Path
	 */
	public void setUrlPathInfo(String urlPathInfo) {
		checkReadOnly();
		m_urlPathInfo = urlPathInfo;
	}

	

	@Override
	public void dump(StringBuffer sb) {
		super.dump(sb);
		if (m_clientName != null) {
			sb.append("clientName="+m_clientName+NL);
		}
		if(m_envName !=null)  {
			sb.append("envName="+m_envName);
		}
		sb.append("========== Invocation Options =========="+NL);
		if (m_preferredLocale != null) {
			sb.append("preferredLocale="+m_preferredLocale+NL);
		}
		if (m_serviceLocation != null) {
			sb.append("serviceLocation="+m_serviceLocation+NL);
		}
		if (m_serviceLocationMap != null && !m_serviceLocationMap.isEmpty()) {
			sb.append("serviceLocationMapEntries=");
			ConfigUtils.dumpStringMap(sb, m_serviceLocationMap, "\t");
			sb.append(NL);
		}
		if (m_wsdlLocation != null) {
			sb.append("wsdlLocation="+m_wsdlLocation+NL);
		}
		if (m_useREST != null) {
			sb.append("useREST=" + m_useREST + NL);
		}
		if (m_maxURLLengthForREST != null) {
			sb.append("maxUrlLengthForREST=" + m_maxURLLengthForREST + NL);
		}
		
		if (m_requestPayloadLog != null) {
			sb.append("requestPayloadLog="+m_requestPayloadLog+NL);
		}
		if (m_requestPayloadCalLog != null) {
			sb.append("requestPayloadLog="+m_requestPayloadCalLog+NL);
		}
		
		if (m_responsePayloadLog != null) {
			sb.append("responsePayloadLog="+m_responsePayloadLog+NL);
		}
		if (m_requestPayloadCalLog != null) {
			sb.append("responsePayloadLog="+m_responsePayloadCalLog+NL);
		}

		if (m_transportOverrideOptions != null) {
			sb.append("========== Transport Override Options =========="+NL);
			ConfigUtils.dumpTransportOptions(sb, m_transportOverrideOptions, "\t");
		}
		if (m_transportOverrideHeaderOptions != null && !m_transportOverrideHeaderOptions.isEmpty()) {
			sb.append("========== Transport Override Header Options =========="+NL);
			ConfigUtils.dumpStringMap(sb, m_transportOverrideHeaderOptions, "\t");
		}
		if (m_preferredEncoding != null) {
			sb.append("preferredEncoding="+m_preferredEncoding+NL);
		}
		sb.append("========== Retry options ==========" +NL);
		if (m_appLevelNumRetries != null) {
			sb.append("appLevelNumRetries="+m_appLevelNumRetries+NL);
		}
		if (m_retryHandlerClass != null) {
			sb.append("retryHandlerClass="+m_retryHandlerClass+NL);
		}
		if (m_retryTransportStatusCodes != null && !m_retryTransportStatusCodes.isEmpty()) {
			sb.append("retry transport status codes=");
			TreeSet<String> ss = new TreeSet<String>(m_retryTransportStatusCodes);
			ConfigUtils.<String>dumpList(sb, ss);
			sb.append(NL);
		}
		if (m_retryExceptionClasses != null && !m_retryExceptionClasses.isEmpty()) {
			sb.append("exception classes=");
			TreeSet<String> exceptionClasses = new TreeSet<String>(m_retryExceptionClasses);
			ConfigUtils.<String>dumpList(sb, exceptionClasses);
			sb.append(NL);
		}
		if (m_retryErrorIds != null && !m_retryErrorIds.isEmpty()) {
			sb.append("error ids=");
			ConfigUtils.dumpList(sb, m_retryErrorIds);
			sb.append(NL);
		}
		sb.append("========== Markdown options ==========" +NL);
		if (m_markdownEnabled != null) {
			sb.append("enabled="+m_markdownEnabled+NL);
		}
		if (m_markdownErrCountThreshold != null) {
			sb.append("errCountThreshold="+m_markdownErrCountThreshold+NL);
		}
		if (m_markdownStateFactoryClass != null) {
			sb.append("markdownStateFactoryClass="+m_markdownStateFactoryClass+NL);
		}
		if (m_markdownTransportStatusCodes != null && !m_markdownTransportStatusCodes.isEmpty()) {
			sb.append("transport status codes=");
			ConfigUtils.dumpList(sb, m_markdownTransportStatusCodes);
			sb.append(NL);
		}
		if (m_markdownExceptionClasses != null && !m_markdownExceptionClasses.isEmpty()) {
			sb.append("exception classes=");
			ConfigUtils.dumpList(sb, m_markdownExceptionClasses);
			sb.append(NL);
		}
		if (m_markdownErrorIds != null && !m_markdownErrorIds.isEmpty()) {
			sb.append("error ids=");
			ConfigUtils.dumpList(sb, m_markdownErrorIds);
			sb.append(NL);
		}
		sb.append("========== Data Bindings =========="+NL);
		if (m_requestDataBinding != null) {
			sb.append("requestDataBinding="+m_requestDataBinding+NL);
		}
		if (m_responseDataBinding != null) {
			sb.append("responseDataBinding="+m_responseDataBinding+NL);
		}
		if (m_preferredTransport != null) {
			sb.append("preferredTransport="+m_preferredTransport+NL);
		}
		if (m_serviceVersion != null) {
			sb.append("serviceVersion="+m_serviceVersion+NL);
		}
		if (m_invocationUseCase != null) {
			sb.append("invocationUseCase="+m_invocationUseCase+NL);
		}
		if (m_consumerId != null) {
			sb.append("consumerId="+m_consumerId+NL);
		}
		if (m_preferredGlobalId != null) {
			sb.append("preferredGlobalId="+m_preferredGlobalId+NL);
		}
		if (m_messageProtocol != null) {
			sb.append("messageProtocol="+m_messageProtocol+NL);
		}
		if (m_responseTransport != null) {
			sb.append("responseTransport="+m_responseTransport+NL);
		}
		if (m_customErrorResponseAdapter != null) {
			sb.append("customErrorResponseAdapter="+m_customErrorResponseAdapter+NL);
		}
		if (m_errorDataProviderClass != null) {
			sb.append("errorDataProviderClass="+m_errorDataProviderClass+NL);
		}		
		if (m_cacheProviderClass != null) {
			sb.append("cacheProviderClass="+m_cacheProviderClass+NL);
		}
		if (m_disableCacheOnLocal != null) {
			sb.append("disableCacheOnLocal="+m_disableCacheOnLocal+NL);
		}
		if (m_skipCacheOnError != null) {
			sb.append("skipCacheOnError="+m_skipCacheOnError+NL);
		}
		if (m_urlPathInfo != null) {
			sb.append("urlPathInfo=" + m_urlPathInfo + NL);
		}
		
	}

	public ArrayList<String> getPreferredLocaleSet() {
		final String preferredLocale = m_preferredLocale == null ? "" : m_preferredLocale;
		StringTokenizer tokenizer = new StringTokenizer(preferredLocale, ";");
		ArrayList<String> outList = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			outList.add(token);
		}
		return outList;
	}


	public boolean isIgnoreServiceVersion() {
		return m_ignoreServiceVersion;
	}

}
