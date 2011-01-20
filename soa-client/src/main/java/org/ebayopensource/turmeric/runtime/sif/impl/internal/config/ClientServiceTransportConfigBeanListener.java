/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ReflectionUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.Transport;


public class ClientServiceTransportConfigBeanListener extends
		ClientServiceConfigBeanListener {

	ClientServiceTransportConfigBeanListener(
			ClientServiceTransportConfigBean bean) {
		super(bean);
	}

	@Override
	protected void setValuesForUpdate(PropertyChangeEvent evt) throws Exception {
		String name = evt.getPropertyName();

		ClientServiceTransportConfigBean bean = (ClientServiceTransportConfigBean) m_bean;

		if (ClientServiceTransportConfigBean.PROP_HTTP_TRANSPORT_CLASS_NAME
				.equalsIgnoreCase(name)) {
			bean.SetHttpTransportClassName((String)evt.getNewValue());
		} else if (ClientServiceTransportConfigBean.PROP_NUM_CONNECT_RETRIES
				.equalsIgnoreCase(name)) {
			bean.setNumConnectRetries((Integer)evt.getNewValue());
		} else if (ClientServiceTransportConfigBean.PROP_CONNECTION_TIMEOUT
				.equalsIgnoreCase(name)) {
			bean.setConnectionTimeout((Integer)evt.getNewValue());
		} else if (ClientServiceTransportConfigBean.PROP_RECEIVE_TIMEOUT
				.equalsIgnoreCase(name)) {
			bean.setReceiveTimeout((Integer)evt.getNewValue());
		} else if (ClientServiceTransportConfigBean.PROP_INVOCATION_TIMEOUT
				.equalsIgnoreCase(name)) {
			bean.setInvocationTimeout((Integer)evt.getNewValue());
		} else if (ClientServiceTransportConfigBean.PROP_SKIP_SERIALIZATION
				.equalsIgnoreCase(name)) {
			bean.setSkipSerialization((Boolean)evt.getNewValue());
		} else if (ClientServiceTransportConfigBean.PROP_USE_DETACHED_LOCAL_BINDING
				.equalsIgnoreCase(name)) {
			bean.setUseDetachedLocalBinding((Boolean)evt.getNewValue());
		} else {
			String msg = "Property(" + name + ") not supported";
			throw new IllegalArgumentException(msg);
		}
	}

	@Override
	protected void setValuesForVeto(ClientConfigHolder configHolder,
			PropertyChangeEvent evt) throws PropertyVetoException {
		String name = evt.getPropertyName();
		Object newValue = evt.getNewValue();
		String value = null;
		
		if (newValue != null) {
			value = newValue.toString();
		}
		
		ClientServiceTransportConfigBean bean = (ClientServiceTransportConfigBean) m_bean;


		try {
			if (ClientServiceTransportConfigBean.PROP_HTTP_TRANSPORT_CLASS_NAME
					.equalsIgnoreCase(name)) {
				validateHttpTransportClassNameValue(evt, name, value);
				bean.updateConfigHolder(configHolder, name, value);
			} else if (ClientServiceTransportConfigBean.PROP_NUM_CONNECT_RETRIES
					.equalsIgnoreCase(name)) {
				validateIntegerValue(evt, name, value);
				bean.updateConfigHolder(configHolder, name, value);
			} else if (ClientServiceTransportConfigBean.PROP_CONNECTION_TIMEOUT
					.equalsIgnoreCase(name)) {
				validateIntegerValue(evt, name, value);
				bean.updateConfigHolder(configHolder, name, value);
			} else if (ClientServiceTransportConfigBean.PROP_RECEIVE_TIMEOUT
					.equalsIgnoreCase(name)) {
				validateIntegerValue(evt, name, value);
				bean.updateConfigHolder(configHolder, name, value);
			} else if (ClientServiceTransportConfigBean.PROP_INVOCATION_TIMEOUT
					.equalsIgnoreCase(name)) {
				validateIntegerValue(evt, name, value);
				bean.updateConfigHolder(configHolder, name, value);
			} else if (ClientServiceTransportConfigBean.PROP_SKIP_SERIALIZATION
					.equalsIgnoreCase(name)) {
				validateBooleanValue(evt, name, value);
				bean.updateConfigHolder(configHolder, name, value);
			} else if (ClientServiceTransportConfigBean.PROP_USE_DETACHED_LOCAL_BINDING
					.equalsIgnoreCase(name)) {
				validateBooleanValue(evt, name, value);
				bean.updateConfigHolder(configHolder, name, value);
			} else {
				String msg = "Property(" + name + ") not supported";
				throw new PropertyVetoException(msg, evt);
			}
		} catch (ServiceException e) {
			String msg = "Updating Property(" + name + ") caused errors: " + e.getMessage();
			throw new PropertyVetoException(msg, evt);
		}
	}

	private void validateHttpTransportClassNameValue(PropertyChangeEvent evt, String name,
			String value) throws PropertyVetoException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			ReflectionUtils.loadClass(value,Transport.class, cl);
		} catch (ServiceException e) {
			String msg = "Encountered invalid property.value=" + name + "."
			+ value + "; The class specified should implement org.ebayopensource.turmeric.runtime.common.pipeline.Transport ";
			throw new PropertyVetoException(msg, evt);
		}
	}
}
