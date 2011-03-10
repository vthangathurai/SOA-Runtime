/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.DomParseUtils;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MessageProcessorConfigMapper;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MetadataPropertyConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.OptionList;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ServiceNameUtils;
import org.ebayopensource.turmeric.runtime.common.monitoring.ErrorStatusOptions;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import org.ebayopensource.turmeric.runtime.common.impl.internal.config.NameValue;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.RequestParamsDescriptor;


// Copy non-null source data into the destination holder.
// The very first time the ServiceInvokerOptions is initialized, 
public class ServiceConfigMapper {
	public static void map(String filename, Element serviceInstanceConfig, ServiceConfigHolder dst) throws ServiceCreationException {
		String errorClass = null;
		String errorDataProviderClass = null;
		NodeList customSerializers = null;
		Element providerOptions = DomParseUtils.getSingleElement(filename, serviceInstanceConfig, "provider-options");
		if (providerOptions != null) {
			mapProviderOptions(filename, providerOptions, dst);
			errorClass = DomParseUtils.getElementText(filename, providerOptions, "error-mapping-handler-class-name");
			errorDataProviderClass = DomParseUtils.getElementText(filename, providerOptions, "error-data-provider-class-name");
			customSerializers = DomParseUtils.getImmediateChildrenByTagName(providerOptions, "custom-serializers");
		}
		Element g11nOptions = DomParseUtils.getSingleElement(filename, serviceInstanceConfig, "G11N-options");
		mapG11NOptions(filename, g11nOptions, dst); 
		Element pipelineConfig = DomParseUtils.getSingleElement(filename, serviceInstanceConfig, "pipeline-config");
		NodeList protocolProcessors = DomParseUtils.getImmediateChildrenByTagName(serviceInstanceConfig, "protocol-processor");
		NodeList transports = DomParseUtils.getImmediateChildrenByTagName(serviceInstanceConfig, "transport");
		Element dataBindingConfig = DomParseUtils.getSingleElement(filename, serviceInstanceConfig, "data-binding-config");
		MessageProcessorConfigMapper.map(filename, customSerializers, errorClass, errorDataProviderClass, pipelineConfig, protocolProcessors, transports, dataBindingConfig, dst.getMessageProcessorConfig());
		String serviceLayer = DomParseUtils.getElementText(filename, serviceInstanceConfig, "service-layer", false);
		if (serviceLayer != null) {
			dst.setServiceLayer(serviceLayer);		
		}
	}

	private static void mapProviderOptions(String filename, Element providerOptions, ServiceConfigHolder dst) throws ServiceCreationException {
		if (providerOptions == null) {
			return;
		}
		// We're merging all the lists - supported global IDs, supported data bindings, 
		// unsupported operation - as a complete replace; list values in the service config
		// (when specified) override the group config compltely instead of being merged.
		List<String> unsupportedOperationsValues = DomParseUtils.getStringList(filename, providerOptions, "unsupported-operation");
		if (unsupportedOperationsValues != null && !unsupportedOperationsValues.isEmpty()) {
			dst.setUnsupportedOperation(unsupportedOperationsValues);
		}
		
		List<String> supportedDataBindingsValues = DomParseUtils.getStringList(filename, providerOptions, "supported-data-bindings");
		if (supportedDataBindingsValues != null && !supportedDataBindingsValues.isEmpty()) {
			dst.setSupportedDataBindings(supportedDataBindingsValues);
		}		
		String monitoringLevelStr = DomParseUtils.getElementText(filename, providerOptions, "monitoring-level");
		if (monitoringLevelStr != null) {
			MonitoringLevel level = DomParseUtils.mapMonitoringLevel(filename, monitoringLevelStr);
			dst.setMonitoringLevel(level);
		}
		String versionCheckHandler = DomParseUtils.getElementText(filename, providerOptions, "version-check-handler");
		if (versionCheckHandler != null) {
			dst.setVersionCheckHandlerClassName(versionCheckHandler);
		}
		OptionList headerMappingOptions = DomParseUtils.getOptionList(filename, providerOptions, "header-mapping-options");
		if (headerMappingOptions != null) {
			dst.setHeaderMappingOptions(headerMappingOptions);
		}
		OptionList requestHeaderMappingOptions = DomParseUtils.getOptionList(filename, providerOptions, "request-header-mapping-options");
		if (requestHeaderMappingOptions != null) {
			dst.setRequestHeaderMappingOptions(requestHeaderMappingOptions);
		}
		OptionList responseHeaderMappingOptions = DomParseUtils.getOptionList(filename, providerOptions, "response-header-mapping-options");
		if (responseHeaderMappingOptions != null) {
			dst.setResponseHeaderMappingOptions(responseHeaderMappingOptions);
		}
		String defaultRequestDataBinding = DomParseUtils.getElementText(filename, providerOptions, "default-request-data-binding");
		if (defaultRequestDataBinding != null) {
			dst.setDefaultRequestDataBinding(defaultRequestDataBinding);
		}
		String defaultResponseDataBinding = DomParseUtils.getElementText(filename, providerOptions, "default-response-data-binding");
		if (defaultResponseDataBinding != null) {
			dst.setDefaultResponseDataBinding(defaultResponseDataBinding);
		}
		Element errorStatusOptions = DomParseUtils.getSingleElement(filename, providerOptions, "error-status-options");
		if (errorStatusOptions != null) {
			ErrorStatusOptions errorOptions = mapErrorStatusOptions(filename, errorStatusOptions);
			dst.setErrorStatusOptions(errorOptions);
		}
		
		Element operationMappingOptions = DomParseUtils.getSingleElement(filename, providerOptions, "operation-mapping-options");
		if( operationMappingOptions != null ) {
			OperationMappings omo = mapOperationMappingOptions(filename, operationMappingOptions);
			dst.setOperationMappings(omo);
		}
		Element reqParamsMapping = DomParseUtils.getSingleElement(filename, providerOptions, "request-params-mapping");
		if( reqParamsMapping != null ) {			 
			dst.setRequestParamsDescriptor(mapOperationReqParamsMappings(filename, reqParamsMapping));
			verifyPathIndicesUniqueness(dst, filename);
		}
	}
	
	private static void verifyPathIndicesUniqueness(ServiceConfigHolder dst, String filename) 
	  throws ServiceCreationException {
		RequestParamsDescriptor requestParamDesc = dst.getRequestParamsDescriptor();
		if(requestParamDesc == null) {
			return;
		}
		Set<String> paramIndices = requestParamDesc.getPathIndices();
		OptionList headerMappings = dst.getHeaderMappingOptions();
		if(headerMappings == null) {
			return;
		}
		List<NameValue> nvList = headerMappings.getOption();
		if(nvList == null || nvList.isEmpty()) {
			return;
		}
		for(NameValue nv : nvList) {
			String index = nv.getValue();			
			index = index.substring(index.indexOf("path[") + 5, index.indexOf(']'));
			if(index.startsWith("+")) {
				index = index.replace('+', '-');
			}
			if(paramIndices.contains(index)) {
				throwError(filename, "Duplicates indices for url path elements");
			}
		}		
	}

	private static RequestParamsDescriptor mapOperationReqParamsMappings(String filename, Element operationReqParamsMapping) 
		throws ServiceCreationException {		
		RequestParamsDescriptor requestParams = new RequestParamsDescriptor();		
		NodeList operations = DomParseUtils.getImmediateChildrenByTagName(operationReqParamsMapping, "operation");
		final Set<String> knownOperations = new HashSet<String>();
		int numOperations = operations.getLength();
		for(int i=0; i < numOperations; ++i) {			
			Element operation = (Element) operations.item(i);
			String operationName = operation.getAttribute("name");			
			if( operationName == null ) {
				DomParseUtils.throwError(filename, "Missing operation name in operation element: '" + operation + "'");
			}
			if(knownOperations.contains(operationName)) {
				throwError(filename, "Duplicate definition for request params for operation '" + operationName + "'");
			}					
			NodeList params = DomParseUtils.getImmediateChildrenByTagName(operation, "option");			
			int paramsLen = params.getLength();
			for(int j=0; j < paramsLen; ++j) {
				Element option = (Element) params.item(j);
				String name = option.getAttribute("name");
				String alias = option.getAttribute("alias");
				if (name == null || name.isEmpty()) {
					throwError(filename, "Missing option name in option list \"operation\" ");
				}
				String value = DomParseUtils.getText(option);
				if (value == null) {
					throwError(filename, "Missing option value for option list \"operation\" ");
				}				
				try {
					String index = value.substring(value.indexOf("path[") + 5, value.indexOf(']'));					
					if(index.startsWith("+")) {
						index = index.replace('+', '-');
					}
					try {
						Integer.parseInt(index);
					}
					catch(NumberFormatException e) {					
						throwError(filename, "Invalid value for request uri path index. An integer is expected. Given :" + value);					
					}	
					if(!requestParams.map(operationName, index, name, alias)) {
						throwError(filename, "Invalid configuration for request param mapping. " +
								"Check for duplicate path indices or duplicate aliases or duplicate request param names");	
					}
				}
				catch(StringIndexOutOfBoundsException exception) {					
					throwError(filename, "Invalid value for request uri index. It must of form 'path[i]'");					
				}				
			}
			knownOperations.add(operationName);
		}		
		return requestParams;
	}


	
	private static OperationMappings mapOperationMappingOptions(String filename, Element operationMappingOptions) 
		throws ServiceCreationException {
		
		OperationMappings omOptions = new OperationMappings();
		NodeList operations = DomParseUtils.getImmediateChildrenByTagName(operationMappingOptions, "operation");
		for(int i=0; i<operations.getLength(); i++) {
			Element operation = (Element)operations.item(i);
			String name = operation.getAttribute("name");
			if( name == null ) {
				DomParseUtils.throwError(filename, "Missing operation name in operation: '" + operation + "'");
			}
			
			String value = DomParseUtils.getText(operation);
			if (value == null) {
				throwError(filename, "Missing option value for option list: '" + operation + "'");
			}
			
			OperationMapping old = omOptions.getOperationMapping(value);
			if( old != null ) {
				throwError(filename, "Duplicate options specified with key: " + value);
				
			}
			OperationMapping om = new OperationMapping(name, value);
			omOptions.add(om);
		}
		
		return omOptions;
	
	}
	
	private static ErrorStatusOptions mapErrorStatusOptions(String filename, Element errorStatusOptions) throws ServiceCreationException {
		
		ErrorStatusOptions errorOptions = new ErrorStatusOptions();
		
		String metric = DomParseUtils.getElementText(filename, errorStatusOptions, "metric");		
		if (metric != null) {
			errorOptions.setMetric(metric);
		}
		String threshold = DomParseUtils.getElementText(filename, errorStatusOptions, "threshold");
		if (threshold != null) {
			errorOptions.setThreshold(threshold);
		}
		Integer sampleSize = DomParseUtils.getElementInteger(filename, errorStatusOptions, "sample-size");
		if (sampleSize != null) {
			errorOptions.setSampleSize(sampleSize.intValue());
		}
		return errorOptions;
	}
	
	
	private static void mapG11NOptions(String filename, Element g11nOptions, ServiceConfigHolder dst) throws ServiceCreationException {
		if (g11nOptions == null)
			return;
		String defaultEncoding = DomParseUtils.getElementText(filename, g11nOptions, "default-encoding");
		if (defaultEncoding != null) {
			dst.setDefaultEncoding(defaultEncoding);
		}
		List<String> supportedGlobalIdList = DomParseUtils.getStringList(filename, g11nOptions, "supported-global-id");
		if (supportedGlobalIdList != null) {
			Set<String> supportedGlobalIdSet = new HashSet<String>();
			for (String supportedGlobalId : supportedGlobalIdList) {
				if (supportedGlobalId.length() > 0) {
					supportedGlobalIdSet.add(supportedGlobalId);
				}
			}
			dst.setSupportedGlobalId(supportedGlobalIdSet);
		}
		List<String> supportedLocaleValueList = DomParseUtils.getStringList(filename, g11nOptions, "supported-locale");
		if (supportedLocaleValueList != null) {
			Set<String> supportedLocaleValues = new HashSet<String>(supportedLocaleValueList);
			dst.setSupportedLocale(supportedLocaleValues);
		}
	}	
	
	public static ServiceConfigHolder applyConfigs(String adminName, String configFilename,
		String groupFilename, Element serviceGroup, Element serviceConfig) throws ServiceCreationException, IOException
	{
		ServiceConfigHolder dst = new ServiceConfigHolder(adminName);
		dst.setConfigFilename(configFilename);
		dst.setGroupFilename(groupFilename);		
		if (serviceGroup != null) {
			Element serviceInstanceInGroup = DomParseUtils.getSingleElement(groupFilename, serviceGroup, "service-config");
			if (serviceInstanceInGroup != null) {
				// we allow client instance config to be absent - this would be a trivial config with no overridden parameter values.
				map(groupFilename, serviceInstanceInGroup, dst);
			}
		}		
		if (serviceConfig == null) {
			// Shouldn't happen - this is the top-level document element.
			throwError(configFilename, "Missing service-config section");
			return null; // throws error above
		}
       	Element serviceInstance = DomParseUtils.getSingleElement(configFilename, serviceConfig, "service-instance-config");
		if (serviceInstance != null) {
        	map(configFilename, serviceInstance, dst);
		}				
		/*
		 * Create a new MetadataPropertyConfigHolder object out of service_metadata.properties file 
		 * The holder will have all the properties defined in service_metadata.properties.
		 * Populate the ServiceConfigHolder with the MetadataPropertyConfigHolder for future references.
		 */		
		MetadataPropertyConfigHolder metadataProps = null;		
		metadataProps = ServiceConfigManager.getInstance().getMetadataPropertyConfigHolder(adminName);
		dst.setMetaData(metadataProps);
		String serviceName = null;
		/*
		 * Services created on pre-1.0 of SOA Framework do not have service_metadata.properties
		 * e.g. Some QA services and ShippingCalculatorService
		 */		
		if(metadataProps == null) {
			serviceName = serviceConfig.getAttribute("service-name");
		}
		else {
			double smpVersion = metadataProps.getSmpVersion();
			if(smpVersion == -1d) {
				throwError(configFilename, "smp_version property has invalid value");
			}
			/*
			 * smp_version property greater than or equal to 1.1 implies that
			 * service name has to constructed using properties in the
			 * service_metadata.properties. Otherwise, we read the service name
			 * from service-config.xml
			 */
			if (smpVersion >= 1.1) {
				StringBuilder serviceqname = new StringBuilder();
				serviceqname.append('{').append(metadataProps.getServiceNamespace()).append('}');
				serviceqname.append(metadataProps.getServiceName());
				serviceName = serviceqname.toString();
			} 
			else {
				serviceName = serviceConfig.getAttribute("service-name");
			}

		}
		if (serviceName == null) {
			throwError(configFilename, "Missing service name");
		}		
		QName qname = ServiceNameUtils.normalizeQName(QName.valueOf(serviceName));		
		dst.setServiceQName(qname);	
		
		/*
		 * Service Implementation class must be provided in one of the two ways. 
		 * 	 1. Service Implementation class name.
		 * 	 2. Service Implementation factory class name.  
		 */
		String serviceImplClassName = DomParseUtils.getElementText(configFilename, serviceConfig, "service-impl-class-name", false);
		String serviceFactory = DomParseUtils.getElementText(configFilename, serviceConfig, "service-impl-factory-class-name", false);		
		if(serviceImplClassName != null) { 


			dst.setServiceImplClassName(serviceImplClassName);			
		}
		else { // this means post 2.8 service			
			if(serviceFactory != null) {
				dst.setServiceImplFactoryClassName(serviceFactory);				
				String cacheable = DomParseUtils.getAttribute(configFilename, serviceConfig, "service-impl-factory-class-name", "cacheable");
				boolean implCached = "true".equalsIgnoreCase(cacheable);
				dst.setImplCached(implCached);
			}
			else { 
				// Required: service-impl-class-name
				// Same error message for backward compatibility
				throwError(configFilename, "Missing required element: 'service-impl-class-name'");
			}
		}		
		if(serviceImplClassName != null && serviceFactory != null) {
			throwError(configFilename, "Specify one of 'service-impl-class-name' or 'service-impl-factory-class-name', not both.");
		}	

		// Required: service-interface-class-name
		String serviceInterfaceClassName = DomParseUtils.getElementText(configFilename, serviceConfig, "service-interface-class-name", true);
		dst.setServiceInterfaceClassName(serviceInterfaceClassName);
		
		List<String> supportedVersionsStr = DomParseUtils.getStringList(configFilename, serviceConfig, "supported-version");
		if (supportedVersionsStr != null && !supportedVersionsStr.isEmpty()) {
			dst.setSupportedVersions(supportedVersionsStr);
		}		
		return dst;
	}

	// TODO - many of the errors thrown in this way should be individual exceptions so the text can
	// be localized better.
	private static void throwError(String filename, String cause) throws ServiceCreationException {
		throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR, 
				ErrorConstants.ERRORDOMAIN, new Object[] {filename, cause}));
	}
}
