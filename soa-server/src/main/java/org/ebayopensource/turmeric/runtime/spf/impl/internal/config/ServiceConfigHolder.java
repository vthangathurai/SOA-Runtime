/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.CommonConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.ConfigUtils;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.OptionList;


public class ServiceConfigHolder extends CommonConfigHolder {
	// When adding properties, please ensure that copy(), dump(), and getters/setters
	// are all covered for the new properties.  Make sure setters call checkReadOnly().

	// Provider options
	private String m_serviceImplClassName;
	private String m_defaultEncoding;
	private Set<String> m_supportedGlobalId = new HashSet<String>();
	private Set<String> m_supportedLocales = new HashSet<String>();
	private List<String> m_supportedDataBindings = new ArrayList<String>();
	private List<String> m_unsupportedOperation = new ArrayList<String>();
	private List<String> m_supportedVersions = new ArrayList<String>();
	private String m_versionCheckHandlerClassName;
	private OptionList m_headerMappingOptions;
	private SecurityPolicyConfigHolder m_securityPolicy;
	private CachePolicyHolder m_cachePolicy;
	private String m_serviceLayer;
	private String m_defaultRequestDataBinding;
	private String m_defaultResponseDataBinding;
	private String m_RequestPayloadLog;
	private String m_RequestPayloadCalLog;
	private String m_ResponsePayloadLog;
	private String m_ResponsePayloadCalLog;
	private OperationMappings m_operationMappings;
	private static final char NL = '\n';

	
	public ServiceConfigHolder(String adminName) {
		super(adminName);
	}

	/*
	 */
	public ServiceConfigHolder copy() {
		ServiceConfigHolder newCH = new ServiceConfigHolder(getAdminName());
		newCH.m_readOnly = false;
		newCH.copyMemberData(this);
		newCH.m_serviceImplClassName = m_serviceImplClassName;
		newCH.m_versionCheckHandlerClassName = m_versionCheckHandlerClassName;
		newCH.m_defaultEncoding = m_defaultEncoding;
		if (m_supportedGlobalId != null) {
			newCH.m_supportedGlobalId = new HashSet<String>(m_supportedGlobalId);
		}
		if (m_supportedLocales != null) {
			newCH.m_supportedLocales = new HashSet<String>(m_supportedLocales);
		}
		if (m_supportedDataBindings != null) {
			newCH.m_supportedDataBindings = new ArrayList<String>(m_supportedDataBindings);
		}
		if (m_unsupportedOperation != null) {
			newCH.m_unsupportedOperation = new ArrayList<String>(m_unsupportedOperation);
		}
		if (m_supportedVersions != null) {
			newCH.m_supportedVersions = new ArrayList<String>(m_supportedVersions);
		}
		if (m_headerMappingOptions != null) {
			newCH.m_headerMappingOptions = ConfigUtils.copyOptionList(m_headerMappingOptions);
		}
		newCH.m_defaultRequestDataBinding = m_defaultRequestDataBinding;
		newCH.m_defaultResponseDataBinding = m_defaultResponseDataBinding;
		
		newCH.m_RequestPayloadLog = m_RequestPayloadLog;
		newCH.m_RequestPayloadCalLog = m_RequestPayloadCalLog;
		
		newCH.m_ResponsePayloadLog = m_ResponsePayloadLog;
		newCH.m_ResponsePayloadCalLog = m_ResponsePayloadCalLog;
	
		if (m_securityPolicy != null) {
			newCH.m_securityPolicy = m_securityPolicy.copy();
		}
		
		if (m_cachePolicy != null) {
			newCH.m_cachePolicy = m_cachePolicy.copy();
		}
		newCH.m_serviceLayer = m_serviceLayer;
		return newCH;
	}

	/**
	 * set read-only property to true.
	 */
	@Override
	public void lockReadOnly() {
		if (m_securityPolicy != null) {
			m_securityPolicy.lockReadOnly();
		}
		if (m_cachePolicy != null) {
			m_cachePolicy.lockReadOnly();
		}
		super.lockReadOnly();
	}

	/**
	 * @return the m_defaultEncoding
	 */
	public String getDefaultEncoding() {
		return m_defaultEncoding;
	}

	/**
	 * @param defaultEncoding the m_defaultEncoding to set
	 */
	public void setDefaultEncoding(String encoding) {
		checkReadOnly();
		m_defaultEncoding = encoding;
	}

	/**
	 * @return the m_serviceImplClassName
	 */
	public String getServiceImplClassName() {
		return m_serviceImplClassName;
	}

	/**
	 * @param serviceImplClassName the m_serviceImplClassName to set
	 */
	public void setServiceImplClassName(String className) {
		checkReadOnly();
		m_serviceImplClassName = className;
	}

	/**
	 * @return the m_supportedGlobalId
	 */
	public Set<String> getSupportedGlobalId() {
		if (m_readOnly) {
			return new HashSet<String>(m_supportedGlobalId);
		}

		return m_supportedGlobalId;
	}

	/**
	 * @param supportedGlobalId the m_supportedGlobalId to set
	 */
	public void setSupportedGlobalId(Set<String> supportedGlobalId) {
		checkReadOnly();
		this.m_supportedGlobalId = supportedGlobalId;
	}

	/**
	 * @return the m_supportedLocales
	 */
	public Set<String> getSupportedLocales() {
		if (m_readOnly) {
			return new HashSet<String>(m_supportedLocales);
		}

		return m_supportedLocales;
	}

	/**
	 * @param supportedLocale the m_supportedLocales to set
	 */
	public void setSupportedLocale(Set<String> supportedLocales) {
		checkReadOnly();
		this.m_supportedLocales = supportedLocales;
	}

	/**
	 * @return the m_supportedDataBindings
	 */
	public List<String> getSupportedDataBindings() {
		if (m_readOnly) {
			return new ArrayList<String>(m_supportedDataBindings);
		}

		return m_supportedDataBindings;
	}

	/**
	 * @param supportedDataBindings the m_supportedDataBindings to set
	 */
	public void setSupportedDataBindings(List<String> supportedDataBindings) {
		checkReadOnly();
		this.m_supportedDataBindings = supportedDataBindings;
	}

	/**
	 * @return the m_unsupportedOperation
	 */
	public List<String> getUnsupportedOperation() {
		if (m_readOnly) {
			return new ArrayList<String>(m_unsupportedOperation);
		}

		return m_unsupportedOperation;
	}

	/**
	 * @param unsupportedOperation the m_unsupportedOperation to set
	 */
	public void setUnsupportedOperation(List<String> unsupportedOperation) {
		checkReadOnly();
		this.m_unsupportedOperation = unsupportedOperation;
	}

	/**
	 * @return the m_supportedVersions
	 */
	public List<String> getSupportedVersions() {
		if (m_readOnly) {
			return new ArrayList<String>(m_supportedVersions);
		}

		return m_supportedVersions;
	}

	/**
	 * @param supportedVersions the m_supportedVersions to set
	 */
	public void setSupportedVersions(List<String> supportedVersions) {
		checkReadOnly();
		this.m_supportedVersions = supportedVersions;
	}

	/**
	 * @return the m_versionCheckHandlerClassName
	 */
	public String getVersionCheckHandlerClassName() {
		return m_versionCheckHandlerClassName;
	}

	/**
	 * @param checkHandlerClassName the m_versionCheckHandlerClassName to set
	 */
	public void setVersionCheckHandlerClassName(String className) {
		checkReadOnly();
		m_versionCheckHandlerClassName = className;
	}

	/**
	 * @return the m_headerMappingOptions
	 */
	public OptionList getHeaderMappingOptions() {
		if (m_readOnly) {
			return ConfigUtils.copyOptionList(m_headerMappingOptions);
		}
		return m_headerMappingOptions;
	}

	/**
	 * @param options the m_headerMappingOptions to set
	 */
	public void setHeaderMappingOptions(OptionList options) {
		checkReadOnly();

		m_headerMappingOptions = options;
	}


	public void setOperationMappings(OperationMappings omo) {
		
		checkReadOnly();
		
		m_operationMappings = omo;	
	}
	
	public OperationMappings getOperationMappings() {
		if( m_operationMappings == null ) {
			return null;
		}
		
		if( m_readOnly) {
			return m_operationMappings.clone();
		}
		
		return m_operationMappings;
		
	}


	/**
	 * @return the m_securityPolicy
	 */
	public SecurityPolicyConfigHolder getSecurityPolicy() {
		// This object has a read-only state that shadows the ConfigHolder itself, so no need
		// to manage cloning at this level - object will take care of cloning its internal values.
		return m_securityPolicy;
	}

	/**
	 * @param securityPolicy the m_securityPolicy to set
	 */
	public void setSecurityPolicy(SecurityPolicyConfigHolder securityPolicy) {
		checkReadOnly();
		m_securityPolicy = securityPolicy;
	}
	
	/**
	 * @return the m_cachePolicy
	 */
	public CachePolicyHolder getCachePolicy() {
		// This object has a read-only state that shadows the ConfigHolder itself, so no need
		// to manage cloning at this level - object will take care of cloning its internal values.
		return m_cachePolicy;
	}

	/**
	 * @param cachePolicy the m_cachePolicy to set
	 */
	public void setCachePolicy(CachePolicyHolder cachePolicy) {
		checkReadOnly();
		m_cachePolicy = cachePolicy;
	}

	/**
	 * @return the m_serviceLayer
	 */
	public String getServiceLayer() {
		return m_serviceLayer;
	}

	/**
	 * @param layer the m_serviceLayer to set
	 */
	public void setServiceLayer(String layer) {
		checkReadOnly();
		m_serviceLayer = layer;
	}

	/**
	 * @return the m_defaultRequestDataBinding
	 */
	public String getDefaultRequestDataBinding() {
		return m_defaultRequestDataBinding;
	}

	/**
	 * @param dataBinding the m_defaultRequestDataBinding to set
	 */
	public void setDefaultRequestDataBinding(String dataBinding) {
		m_defaultRequestDataBinding = dataBinding;
	}

	/**
	 * @return the m_defaultResponseDataBinding
	 */
	public String getDefaultResponseDataBinding() {
		return m_defaultResponseDataBinding;
	}

	/**
	 * @param dataBinding the m_defaultResponseDataBinding to set
	 */
	public void setDefaultResponseDataBinding(String dataBinding) {
		m_defaultResponseDataBinding = dataBinding;
	}	

///////////////////////
// Dump methods

	@Override
	public void dump(StringBuffer sb) {
		super.dump(sb);
		if (m_serviceLayer != null) {
			sb.append("========== Service Layer: " + m_serviceLayer + NL);
		}
		sb.append("========== Provider Options =========="+NL);
		if (m_defaultEncoding != null) {
			sb.append("defaultEncoding="+m_defaultEncoding+NL);
		}
		if (m_serviceImplClassName != null) {
			sb.append("serviceImplClassName="+m_serviceImplClassName+NL);
		}
		if (m_versionCheckHandlerClassName != null) {
			sb.append("versionCheckHandlerClassName="+m_versionCheckHandlerClassName+NL);
		}
		if (m_supportedGlobalId != null && !m_supportedGlobalId.isEmpty()) {
			sb.append("supportedGlobalId=");
			TreeSet<String> supportedGlobalId = new TreeSet<String>(m_supportedGlobalId);
			ConfigUtils.dumpList(sb, supportedGlobalId);
			sb.append(NL);
		}
		if (m_supportedLocales != null && !m_supportedLocales.isEmpty()) {
			sb.append("supportedLocale=");
			TreeSet<String> supportedLocales = new TreeSet<String>(m_supportedLocales);
			ConfigUtils.dumpList(sb, supportedLocales);
			sb.append(NL);
		}
		if (m_supportedDataBindings != null && !m_supportedDataBindings.isEmpty()) {
			sb.append("supportedDataBindings=");
			TreeSet<String> supportedDataBindings = new TreeSet<String>(m_supportedDataBindings);
			ConfigUtils.dumpList(sb, supportedDataBindings);
			sb.append(NL);
		}
		if (m_unsupportedOperation != null && !m_unsupportedOperation.isEmpty()) {
			sb.append("unsupportedOperation=");
			TreeSet<String> unsupportedOperation = new TreeSet<String>(m_unsupportedOperation);
			ConfigUtils.dumpList(sb, unsupportedOperation);
			sb.append(NL);
		}
		if (m_supportedVersions != null && !m_supportedVersions.isEmpty()) {
			sb.append("supportedVersions=");
			TreeSet<String> supportedVersions = new TreeSet<String>(m_supportedVersions);
			ConfigUtils.dumpList(sb, supportedVersions);
			sb.append(NL);
		}
		if (m_headerMappingOptions != null) {
			sb.append("headerMappingOptions:"+NL);
			ConfigUtils.dumpOptionList(sb, m_headerMappingOptions, "  ");
		}
		if (m_defaultRequestDataBinding != null) {
			sb.append("defaultRequestDataBinding="+m_defaultRequestDataBinding+NL);
		}
		if (m_defaultResponseDataBinding != null) {
			sb.append("defaultResponseDataBinding="+m_defaultResponseDataBinding+NL);
		}
		
		if (m_RequestPayloadLog != null) {
			sb.append("defaultRequestPayloadLog="+m_RequestPayloadLog+NL);
		}
		
		if (m_RequestPayloadCalLog != null) {
			sb.append("defaultRequestPayloadCalLog="+m_RequestPayloadLog+NL);
		}
		
		if (m_ResponsePayloadLog != null) {
			sb.append("defaultResponsePayloadLog="+m_ResponsePayloadLog+NL);
		}
		
		if (m_RequestPayloadCalLog != null) {
			sb.append("defaultResponsePayloadLog="+m_ResponsePayloadLog+NL);
		}
		if (m_securityPolicy != null) {
			m_securityPolicy.dump(sb);
		}
		if (m_cachePolicy != null) {
			m_cachePolicy.dump(sb);
		}
	}

}
