/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.List;

import org.ebayopensource.turmeric.runtime.common.monitoring.ErrorStatusOptions;


/**
 * @author idralyuk
 */
public class ServiceOptionsConfigBeanListener extends ServiceConfigBeanListener {

	ServiceOptionsConfigBeanListener(ServiceOptionsConfigBean bean) {
		super(bean);
	}
	
	@Override
	protected void setValuesForUpdate(PropertyChangeEvent evt) throws Exception {
		String name = evt.getPropertyName();

		ServiceOptionsConfigBean bean = (ServiceOptionsConfigBean) m_serviceBean;

		if (ServiceOptionsConfigBean.PROP_UNSUPPORTED_OPERATIONS.equalsIgnoreCase(name)) {
			bean.setUnsupportedOperations((ServiceConfigBean.StringList)evt.getNewValue());
		} else if (ServiceOptionsConfigBean.PROP_SUPPORTED_DATA_BINDINGS.equalsIgnoreCase(name)) {
			bean.setSupportedDataBindings((ServiceConfigBean.StringList)evt.getNewValue());
		} else if (ServiceOptionsConfigBean.PROP_DEFAULT_REQUEST_BINDING.equalsIgnoreCase(name)) {
			bean.setDefaultRequestBinding((String)evt.getNewValue());
		} else if (ServiceOptionsConfigBean.PROP_DEFAULT_RESPONSE_BINDING.equalsIgnoreCase(name)) {
			bean.setDefaultResponseBinding((String)evt.getNewValue());
		} else if (ServiceOptionsConfigBean.PROP_ERROR_STATUS_METRIC.equalsIgnoreCase(name)) {
			bean.setErrorStatusMetric((String)evt.getNewValue());
		} else if (ServiceOptionsConfigBean.PROP_ERROR_STATUS_THRESHOLD.equalsIgnoreCase(name)) {
			bean.setErrorStatusThreshold((String)evt.getNewValue());
 		} else if (ServiceOptionsConfigBean.PROP_ERROR_STATUS_SAMPLE_SIZE.equalsIgnoreCase(name)) {
			bean.setErrorStatusSampleSize((Integer)evt.getNewValue());
		} else {
			String msg = "Property(" + name + ") not supported";
			throw new IllegalArgumentException(msg);
		}
	}

	@Override
	protected void setValuesForVeto(ServiceConfigHolder configHolder,
			PropertyChangeEvent evt) throws PropertyVetoException {
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();

		if (ServiceOptionsConfigBean.PROP_UNSUPPORTED_OPERATIONS.equalsIgnoreCase(name)) {
			validateStringListValue(evt, name, value);
			configHolder.setUnsupportedOperation((ServiceConfigBean.StringList)value);
		} else if (ServiceOptionsConfigBean.PROP_SUPPORTED_DATA_BINDINGS.equalsIgnoreCase(name)) {
			validateStringListValue(evt, name, value);
			if (value != null && ((ServiceConfigBean.StringList)value).isEmpty()) {
				String msg = "At least one supported binding must be provided!";
				throw new PropertyVetoException(msg, evt);
			}
			configHolder.setSupportedDataBindings((ServiceConfigBean.StringList)value);
		} else if (ServiceOptionsConfigBean.PROP_DEFAULT_REQUEST_BINDING.equalsIgnoreCase(name)) {
			if (!isSupportedBinding(configHolder, (String)value))
				throw new PropertyVetoException("Binding " + value + " is unsupported", evt);
			configHolder.setDefaultRequestDataBinding((String)value);
		} else if (ServiceOptionsConfigBean.PROP_DEFAULT_RESPONSE_BINDING.equalsIgnoreCase(name)) {
			if (!isSupportedBinding(configHolder, (String)value))
				throw new PropertyVetoException("Binding " + value + " is unsupported", evt);
			configHolder.setDefaultResponseDataBinding((String)value);
		} else if (ServiceOptionsConfigBean.PROP_ERROR_STATUS_METRIC.equalsIgnoreCase(name)) {
			validateStringValue(evt, name, value);
			getErrorStatusOptions(configHolder).setMetric((String)value);
		} else if (ServiceOptionsConfigBean.PROP_ERROR_STATUS_THRESHOLD.equalsIgnoreCase(name)) {
			validateStringValue(evt, name, value);
			getErrorStatusOptions(configHolder).setThreshold((String)value);
		} else if (ServiceOptionsConfigBean.PROP_ERROR_STATUS_SAMPLE_SIZE.equalsIgnoreCase(name)) {
			validateIntegerValue(evt, name, value);
			getErrorStatusOptions(configHolder).setSampleSize((Integer)value);
		} else {
			String msg = "Property(" + name + ") not supported";
			throw new PropertyVetoException(msg, evt);
		}
	}
	
	private boolean isSupportedBinding(ServiceConfigHolder configHolder, String binding) {
		if (binding == null || "".equals(binding.trim())) return false;
		boolean isSupported = false;
		List<String> supportedBindings = configHolder.getSupportedDataBindings();
		if (supportedBindings != null) {
			for (String s : supportedBindings) {
				if (binding.equals(s)) isSupported = true;
			}
		}
		return isSupported;
	}
	
	private ErrorStatusOptions getErrorStatusOptions(ServiceConfigHolder configHolder) {
		ErrorStatusOptions errorStatusOptions = configHolder.getErrorStatusOptions();
		if (errorStatusOptions == null) {
			errorStatusOptions = new ErrorStatusOptions();
			configHolder.setErrorStatusOptions(errorStatusOptions);
		}
		return errorStatusOptions;
	}
}
