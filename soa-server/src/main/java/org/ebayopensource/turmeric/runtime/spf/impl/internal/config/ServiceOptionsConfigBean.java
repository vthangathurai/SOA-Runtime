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
import java.util.List;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.common.monitoring.ErrorStatusOptions;

import com.ebay.kernel.bean.configuration.BeanPropertyInfo;
import com.ebay.kernel.bean.configuration.ConfigCategoryCreateException;

/**
 * @author idralyuk
 */
public class ServiceOptionsConfigBean extends ServiceConfigBean {

	public static final String PROP_UNSUPPORTED_OPERATIONS = "UNSUPPORTED_OPERATIONS";
	public static final String PROP_SUPPORTED_DATA_BINDINGS = "SUPPORTED_DATA_BINDINGS";
	public static final String PROP_DEFAULT_REQUEST_BINDING = "DEFAULT_REQUEST_BINDING";
	public static final String PROP_DEFAULT_RESPONSE_BINDING = "DEFAULT_RESPONSE_BINDING";
	public static final String PROP_ERROR_STATUS_METRIC = "ERROR_STATUS_METRIC";
	public static final String PROP_ERROR_STATUS_THRESHOLD = "ERROR_STATUS_THRESHOLD";
	public static final String PROP_ERROR_STATUS_SAMPLE_SIZE = "ERROR_STATUS_SAMPLE_SIZE";

	// @see org.ebayopensource.turmeric.runtime.common.impl.internal.service.BaseServiceDescFactory.addDefaultDataBindings()
	public static final List<String> DEFAULT_SUPPORTED_DATA_BINDINGS = new ArrayList<String>();
	
	static {
		DEFAULT_SUPPORTED_DATA_BINDINGS.add(BindingConstants.PAYLOAD_XML);
		DEFAULT_SUPPORTED_DATA_BINDINGS.add(BindingConstants.PAYLOAD_NV);
		DEFAULT_SUPPORTED_DATA_BINDINGS.add(BindingConstants.PAYLOAD_JSON);
		DEFAULT_SUPPORTED_DATA_BINDINGS.add(BindingConstants.PAYLOAD_FAST_INFOSET);
	}
	
	public static final BeanPropertyInfo UNSUPPORTED_OPERATIONS = createBeanPropertyInfo(
			"m_unsupportedOperations", PROP_UNSUPPORTED_OPERATIONS, true);

	public static final BeanPropertyInfo SUPPORTED_DATA_BINDINGS = createBeanPropertyInfo(
			"m_supportedDataBindings", PROP_SUPPORTED_DATA_BINDINGS, true);
	
	public static final BeanPropertyInfo DEFAULT_REQUEST_BINDING = createBeanPropertyInfo(
			"m_defaultRequestBinding", PROP_DEFAULT_REQUEST_BINDING, true);

	public static final BeanPropertyInfo DEFAULT_RESPONSE_BINDING = createBeanPropertyInfo(
			"m_defaultResponseBinding", PROP_DEFAULT_RESPONSE_BINDING, true);
	
	public static final BeanPropertyInfo ERROR_STATUS_METRIC = createBeanPropertyInfo(
			"m_errorStatusMetric", PROP_ERROR_STATUS_METRIC, true);
	
	public static final BeanPropertyInfo ERROR_STATUS_THRESHOLD = createBeanPropertyInfo(
			"m_errorStatusThreshold", PROP_ERROR_STATUS_THRESHOLD, true);

	public static final BeanPropertyInfo ERROR_STATUS_SAMPLE_SIZE = createBeanPropertyInfo(
			"m_errorStatusSampleSize", PROP_ERROR_STATUS_SAMPLE_SIZE, true);

	private StringList m_unsupportedOperations;
	private StringList m_supportedDataBindings;
	private String m_defaultRequestBinding;
	private String m_defaultResponseBinding;
	private String m_errorStatusMetric;
	private String m_errorStatusThreshold;
	private Integer m_errorStatusSampleSize;
	
	ServiceOptionsConfigBean(ServiceConfigHolder config) throws ConfigCategoryCreateException {
		super(config, "Options");
		setDefaultsFromConfig(config);
		// why fire changes here?
//		addPropertyToConfigMng(UNSUPPORTED_OPERATIONS, m_unsupportedOperations);
//		addPropertyToConfigMng(SUPPORTED_DATA_BINDINGS, m_supportedDataBindings);
//		addPropertyToConfigMng(DEFAULT_REQUEST_BINDING, m_defaultRequestBinding);
//		addPropertyToConfigMng(DEFAULT_RESPONSE_BINDING, m_defaultResponseBinding);
//		addPropertyToConfigMng(ERROR_STATUS_METRIC, m_errorStatusMetric);
//		addPropertyToConfigMng(ERROR_STATUS_THRESHOLD, m_errorStatusThreshold);
//		addPropertyToConfigMng(ERROR_STATUS_SAMPLE_SIZE, m_errorStatusSampleSize);
	}
	
	public List<String> getUnsupportedOperations() {
		return m_unsupportedOperations;
	}
	
	public List<String> getSupportedDataBindings() {
		return m_supportedDataBindings;
	}

	public String getDefaultRequestBinding() {
		return m_defaultRequestBinding;
	}
	
	public String getDefaultResponseBinding() {
		return m_defaultResponseBinding;
	}

	public String getErrorStatusMetric() {
		return m_errorStatusMetric;
	}
	
	public String getErrorStatusThreshold() {
		return m_errorStatusThreshold;
	}

	public Integer getErrorStatusSampleSize() {
		return m_errorStatusSampleSize;
	}

	public void setUnsupportedOperations(List<String> unsupportedOperations) {
		changeProperty(UNSUPPORTED_OPERATIONS, m_unsupportedOperations, unsupportedOperations);
	}
	
	public void setSupportedDataBindings(List<String> supportedDataBindings) {
		changeProperty(SUPPORTED_DATA_BINDINGS, m_supportedDataBindings, supportedDataBindings);
	}
	
	public void setDefaultRequestBinding(String binding) {
		changeProperty(DEFAULT_REQUEST_BINDING, m_defaultRequestBinding, binding);
	}

	public void setDefaultResponseBinding(String binding) {
		changeProperty(DEFAULT_RESPONSE_BINDING, m_defaultResponseBinding, binding);
	}
	
	public void setErrorStatusMetric(String metric) {
		changeProperty(ERROR_STATUS_METRIC, m_errorStatusMetric, metric);
	}
	
	public void setErrorStatusThreshold(String threshold) {
		changeProperty(ERROR_STATUS_THRESHOLD, m_errorStatusThreshold, threshold);
	}

	public void setErrorStatusSampleSize(Integer size) {
		changeProperty(ERROR_STATUS_SAMPLE_SIZE, m_errorStatusSampleSize, size);
	}

	@Override
	protected void setDefaultsFromConfig(ServiceConfigHolder config) {
		List<String> supportedDataBindings = config.getSupportedDataBindings();
		if (supportedDataBindings == null || supportedDataBindings.size() == 0) {
			supportedDataBindings = DEFAULT_SUPPORTED_DATA_BINDINGS;
		}
		m_supportedDataBindings = new StringList(supportedDataBindings);
		
		m_unsupportedOperations = new StringList(config.getUnsupportedOperation());
		m_defaultRequestBinding = config.getDefaultRequestDataBinding();
		m_defaultResponseBinding = config.getDefaultResponseDataBinding();
		
		ErrorStatusOptions errorStatusOptions = config.getErrorStatusOptions();
		if (errorStatusOptions != null) {
			m_errorStatusMetric = errorStatusOptions.getMetric();
			m_errorStatusThreshold = errorStatusOptions.getThreshold();
			m_errorStatusSampleSize = errorStatusOptions.getSampleSize();
		}
	}

	@Override
	protected void updateConfigHolder(ServiceConfigHolder config) {
		config.setSupportedDataBindings(m_supportedDataBindings);
		config.setUnsupportedOperation(m_unsupportedOperations);
		config.setDefaultRequestDataBinding(m_defaultRequestBinding);
		config.setDefaultResponseDataBinding(m_defaultResponseBinding);

		ErrorStatusOptions errorStatusOptions = config.getErrorStatusOptions();
		if (errorStatusOptions == null) {
			errorStatusOptions = new ErrorStatusOptions();
			config.setErrorStatusOptions(errorStatusOptions);
		}
		errorStatusOptions.setMetric(m_errorStatusMetric);
		errorStatusOptions.setThreshold(m_errorStatusThreshold);
		if (m_errorStatusSampleSize != null) {
			errorStatusOptions.setSampleSize(m_errorStatusSampleSize.intValue());
		}
	}
}

