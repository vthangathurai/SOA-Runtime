/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.monitoring.ErrorStatusOptions;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;



/**
 * This class represents processed configuration that is common between client and server.
 * <p>
 * Note: Most ConfigHolder data is available in higher-level structures. Refer to ServiceDesc and related structures
 * as the primary configuration in the public API for SOA framework.
 * @author rmurphy
 *
 */
public abstract class CommonConfigHolder extends BaseConfigHolder {
	private final String m_adminName;
	private QName m_serviceQName;
	private MessageProcessorConfigHolder m_messageProcessorConfig = new MessageProcessorConfigHolder();
	private MetadataPropertyConfigHolder m_metadata = new  MetadataPropertyConfigHolder(); 
	private TypeMappingConfigHolder m_typeMappings = new TypeMappingConfigHolder();
	private OperationPropertyConfigHolder m_operationProperties = new OperationPropertyConfigHolder();
	private String m_serviceInterfaceClassName;
	private MonitoringLevel m_monitoringLevel;
	private OptionList m_requestHeaderMappingOptions;
	private OptionList m_responseHeaderMappingOptions;

	private String m_configFilename;
	private String m_groupFilename;
	private ErrorStatusOptions m_errorStatusOptions;
	
	public CommonConfigHolder(CommonConfigHolder configToCopy,
			String adminName, QName serviceQName,
			MessageProcessorConfigHolder messageProcessorConfig,
			MetadataPropertyConfigHolder metadata,
			TypeMappingConfigHolder typeMappings,
			OperationPropertyConfigHolder operationProperties,
			String serviceInterfaceClassName,
			MonitoringLevel monitoringLevel,
			OptionList requestHeaderMappingOptions,
			OptionList responseHeaderMappingOptions,
			ErrorStatusOptions errorStatusOptions) {
		
		// create copies
		adminName = adminName == null ? configToCopy.m_adminName : adminName;
		serviceQName = serviceQName == null ? configToCopy.m_serviceQName
				: serviceQName;
		messageProcessorConfig = messageProcessorConfig == null ? configToCopy.m_messageProcessorConfig
				.copy()
				: messageProcessorConfig;
		metadata = metadata == null ? configToCopy.m_metadata.copy() : metadata;
		typeMappings = typeMappings == null ? configToCopy.m_typeMappings
				.copy() : typeMappings;
		operationProperties = operationProperties == null ? configToCopy.m_operationProperties == null ? null
				: configToCopy.m_operationProperties.copy()
				: operationProperties;
		serviceInterfaceClassName = serviceInterfaceClassName == null ? configToCopy.m_serviceInterfaceClassName
				: serviceInterfaceClassName;
		monitoringLevel = monitoringLevel == null ? configToCopy.m_monitoringLevel
				: monitoringLevel;
		requestHeaderMappingOptions = requestHeaderMappingOptions == null ? configToCopy.m_requestHeaderMappingOptions == null ? null
				: ConfigUtils
						.copyOptionList(configToCopy.m_requestHeaderMappingOptions)
				: requestHeaderMappingOptions;
		responseHeaderMappingOptions = responseHeaderMappingOptions == null ? configToCopy.m_responseHeaderMappingOptions == null ? null
				: ConfigUtils
						.copyOptionList(configToCopy.m_responseHeaderMappingOptions)
				: responseHeaderMappingOptions;
		errorStatusOptions = errorStatusOptions == null ? configToCopy.m_errorStatusOptions : errorStatusOptions;
		
		// copy to member vars
		m_adminName = adminName;
		m_serviceQName = serviceQName; 
		m_messageProcessorConfig = messageProcessorConfig; 
		m_metadata = metadata; 
		m_typeMappings = typeMappings; 
		m_operationProperties = operationProperties; 
		m_serviceInterfaceClassName = serviceInterfaceClassName; 
		m_monitoringLevel = monitoringLevel;
		m_requestHeaderMappingOptions = requestHeaderMappingOptions;
		m_responseHeaderMappingOptions = responseHeaderMappingOptions;
		m_errorStatusOptions =errorStatusOptions;
	}

	public CommonConfigHolder(String adminName) {
		m_adminName = adminName;
	}

	public String getAdminName() {
		return m_adminName;
	}

	protected void copyMemberData(CommonConfigHolder holder) {
		this.m_serviceQName = holder.m_serviceQName;
		this.m_configFilename = holder.m_configFilename;
		this.m_groupFilename = holder.m_groupFilename;
		this.m_messageProcessorConfig = holder.m_messageProcessorConfig.copy();
		this.m_typeMappings = holder.getTypeMappings().copy();
		this.m_metadata = holder.getMetaData().copy();
		if (holder.m_operationProperties != null) {
			this.m_operationProperties = holder.m_operationProperties.copy();
		}
		this.m_serviceInterfaceClassName = holder.m_serviceInterfaceClassName;
		this.m_monitoringLevel = holder.m_monitoringLevel;
		if (holder.m_requestHeaderMappingOptions != null) {
			this.m_requestHeaderMappingOptions = ConfigUtils.copyOptionList(holder.m_requestHeaderMappingOptions);
		}
		if (holder.m_responseHeaderMappingOptions != null) {
			this.m_responseHeaderMappingOptions = ConfigUtils.copyOptionList(holder.m_responseHeaderMappingOptions);
		}
		this.m_errorStatusOptions = holder.m_errorStatusOptions;
	}
	
	/**
	 * set read-only property to true.
	 */
	@Override
	public void lockReadOnly() {
		m_messageProcessorConfig.lockReadOnly();
		m_metadata.lockReadOnly();
		m_typeMappings.lockReadOnly();
		super.lockReadOnly();
	}
	/**
	 * @return the contained message processor configuration.
	 */
	public MessageProcessorConfigHolder getMessageProcessorConfig() {
		// This object has a read-only state that shadows the ConfigHolder itself, so no need
		// to manage cloning at this level - object will take care of cloning its internal values.
		return m_messageProcessorConfig;
	}

	/**
	 * Set the message processor configuration object.
	 * @param m_messageProcessorConfig the message processor configuration to set
	 */
	public void setMessageProcessorConfig(MessageProcessorConfigHolder mpConfig) {
		checkReadOnly();
		m_messageProcessorConfig = mpConfig;
	}

	/**
	 * @return the fully qualified service name to which this configuration applies
	 */
	public QName getServiceQName() {
		return m_serviceQName;
	}

	/**
	 * Set the fully qualified service name for this configuration
	 * @param serviceQName the qualified name to set
	 */
	public void setServiceQName(QName serviceQName) {
		checkReadOnly();
		this.m_serviceQName = serviceQName;
	}
	
	/**
	 * @return the type mappings configuration (serialization type information) for this service
	 */
	public TypeMappingConfigHolder getTypeMappings() {
		return m_typeMappings;
	}

	/**
	 * @param typeMappings the type mappings configuration object
	 */
	public void setTypeMappings(TypeMappingConfigHolder typeMappings) {
		checkReadOnly();
		this.m_typeMappings = typeMappings;
	}
	
	/**
	 * @return the meta data configuration for this service
	 */
	public MetadataPropertyConfigHolder getMetaData() {
		return m_metadata;
	}

	/**
	 * @param metaData the meta data configuration object
	 */
	public void setMetaData(MetadataPropertyConfigHolder metaData) {
		checkReadOnly();
		this.m_metadata = metaData;
	}
	

	/**
	 * @return the operation property configuration for this service
	 */
	public OperationPropertyConfigHolder getOperationProperties() {
		return m_operationProperties;
	}

	/**
	 * @param operationProperties the operation property configuration object
	 */
	public void setOperationProperties(OperationPropertyConfigHolder operationProperties) {
		checkReadOnly();
		this.m_operationProperties = operationProperties;
	}

	/**
	 * @return the service interface class name
	 */
	public String getServiceInterfaceClassName() {
		return m_serviceInterfaceClassName;
	}

	/**
	 * Set the service interface class name for this configuration
	 * @param serviceInterfaceClassName the class name to set
	 */
	public void setServiceInterfaceClassName(String className) {
		checkReadOnly();
		m_serviceInterfaceClassName = className;
	}

	/**
	 * @return the metric monitoring level for the service; affects how much of the registered metric data is actually collected.
	 */
	public MonitoringLevel getMonitoringLevel() {
		return m_monitoringLevel;
	}

	/**
	 * Set the metric monitoring level for the service.
	 * @param level the nonitoring level to set
	 */
	public void setMonitoringLevel(MonitoringLevel level) {
		checkReadOnly();
		m_monitoringLevel = level;
	}

	/**
	 * @return the filename from which the configuration was read
	 */
	public String getConfigFilename() {
		return m_configFilename;
	}

	/**
	 * Set the filename of the source file for the configuration
	 * @param filename the filename to set
	 */
	public void setConfigFilename(String filename) {
		m_configFilename = filename;
	}

	/**
	 * @return the filename of the group configuration referenced by this configuration, or null if no group file was provided
	 */
	public String getGroupFilename() {
		return m_groupFilename;
	}

	/**
	 * Set the filename of the group configuration referenced by this configuration.
	 * @param filename the filename to set
	 */
	public void setGroupFilename(String filename) {
		m_groupFilename = filename;
	}
	
	/**
	 * @return the m_requestHeaderMappingOptions
	 */
	public OptionList getRequestHeaderMappingOptions() {
		if (m_readOnly) {
			return ConfigUtils.copyOptionList(m_requestHeaderMappingOptions);
		}
		return m_requestHeaderMappingOptions;
	}

	/**
	 * @param options the m_requestHeaderMappingOptions to set
	 */
	public void setRequestHeaderMappingOptions(OptionList options) {
		checkReadOnly();
		
		m_requestHeaderMappingOptions = options;
	}

	/**
	 * @return the m_responseHeaderMappingOptions
	 */
	public OptionList getResponseHeaderMappingOptions() {
		if (m_readOnly) {
			return ConfigUtils.copyOptionList(m_responseHeaderMappingOptions);
		}
		return m_responseHeaderMappingOptions;
	}

	/**
	 * @param options the m_responseHeaderMappingOptions to set
	 */
	public void setResponseHeaderMappingOptions(OptionList options) {
		checkReadOnly();
		
		m_responseHeaderMappingOptions = options;
	}
	
	/**
	 * @return the m_errorStatusOptions
	 */
	public ErrorStatusOptions getErrorStatusOptions() {
		return m_errorStatusOptions;
	}

	/**
	 * @param errorStatusOptions the m_errorStatusOptions to set
	 */
	public void setErrorStatusOptions(ErrorStatusOptions errorStatusOptions) {
		checkReadOnly();
		m_errorStatusOptions = errorStatusOptions;
	}

	/**
	 * Provide a user-readable description of the configuration into a StringBuffer.
	 * @param sb the StringBuffer into which to write the description
	 */
	public void dump(StringBuffer sb) {
		if (m_serviceQName != null) {
			sb.append("***** SERVICE: " + m_serviceQName+"\n");
		}
		if (m_serviceInterfaceClassName != null) {
			sb.append("serviceInterfaceClassName=" + m_serviceInterfaceClassName+"\n");
		}
		m_messageProcessorConfig.dump(sb);
		m_typeMappings.dump(sb);
		m_metadata.dump(sb);
		sb.append("========== Monitoring Config =========="+"\n");
		if (m_monitoringLevel != null) {
			sb.append("monitoringLevel=" + m_monitoringLevel + "\n");
		}
		if (m_operationProperties != null) {
			m_operationProperties.dump(sb);
		}
		if (m_requestHeaderMappingOptions != null) {
			sb.append("requestHeaderMappingOptions:"+"\n");
			ConfigUtils.dumpOptionList(sb, m_requestHeaderMappingOptions, "  ");
		}
		if (m_responseHeaderMappingOptions != null) {
			sb.append("responseHeaderMappingOptions:"+"\n");
			ConfigUtils.dumpOptionList(sb, m_responseHeaderMappingOptions, "  ");
		}
		sb.append("========== Error Status Options =========="+"\n");
		if (m_errorStatusOptions != null) {
			ConfigUtils.dumpErrorStatusOptions(sb, m_errorStatusOptions, "\t\t");
		}
	}
}
