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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MessageProcessorConfigHolder;

import com.ebay.kernel.bean.configuration.BeanPropertyInfo;
import com.ebay.kernel.bean.configuration.DynamicBeanPropertyCreationException;
import com.ebay.kernel.bean.configuration.DynamicConfigBean;
import com.ebay.kernel.exception.BaseRuntimeException;
import com.ebay.kernel.initialization.InitializationException;

public class ClientServiceConfigBeanManager {
	private static Map<String, ClientServiceInvokerConfigBean> s_invokerConfigBeans = Collections
			.synchronizedMap(new HashMap<String, ClientServiceInvokerConfigBean>());

	private static Map<String, ClientServiceTransportConfigBean> s_transportConfigBeans = Collections
			.synchronizedMap(new HashMap<String, ClientServiceTransportConfigBean>());

	private static Map<String, DynamicConfigBean> s_transportDynaConfigBeans = Collections
		.synchronizedMap(new HashMap<String, DynamicConfigBean>());

	private static Map<String, ClientServicePayloadLogConfigBean> s_payloadLogConfigBeans = Collections
	.synchronizedMap(new HashMap<String, ClientServicePayloadLogConfigBean>());

	public static ClientServiceInvokerConfigBean getInvokerInstance(
			String adminName, String clientName,String envName) {
		ClientServiceInvokerConfigBean sBean = null;
		if (s_invokerConfigBeans == null) {
			throw new BaseRuntimeException(
					"ClientServiceInvokerConfigBean is not initialized");
		}
		String svcClientName = adminName + "." + clientName;
		String lookupName = envName==null ? svcClientName : svcClientName + "." +envName;
		sBean = s_invokerConfigBeans.get(lookupName);
		if (sBean == null) {
			throw new BaseRuntimeException(
					"ClientServiceInvokerConfigBean is not initialized for " + lookupName);
		}

		return sBean;
	}
	//for backward compatibilty
	public static ClientServiceInvokerConfigBean getInvokerInstance(
			String adminName, String clientName) {
		return getInvokerInstance(adminName, clientName,null);
	}

	public static ClientServiceTransportConfigBean getTransportInstance(
			String adminName, String clientName, String envName, String transportName) {
		ClientServiceTransportConfigBean sBean = null;
		if (s_transportConfigBeans == null || s_transportDynaConfigBeans == null) {
			throw new BaseRuntimeException(
					"ClientServiceInvokerConfigBean is not initialized");
		}

		String lookupName = getLookupTransportName(adminName, clientName,envName, transportName);
		sBean = s_transportConfigBeans.get(lookupName);
		if (sBean == null) {
			throw new BaseRuntimeException(
					"ClientServiceInvokerConfigBean is not initialized");
		}

		return sBean;
	}
	//for backward compatibilty
	public static ClientServiceTransportConfigBean getTransportInstance(
			String adminName, String clientName,  String transportName) {
		return getTransportInstance(adminName, clientName, null,transportName);
	}


	public static ClientServicePayloadLogConfigBean getPayloadLogInstance(
			String adminName, String clientName,String envName) {
		ClientServicePayloadLogConfigBean sBean = null;
		if (s_payloadLogConfigBeans == null) {
			throw new BaseRuntimeException(
					"ClientServicePayloadLogConfigBean is not initialized");
		}

		String svcClientName = adminName + "." + clientName;
		String lookupName = envName==null ? svcClientName : svcClientName + "." +envName;
		sBean = s_payloadLogConfigBeans.get(lookupName);
		if (sBean == null) {
			throw new BaseRuntimeException(
					"ClientServicePayloadLogConfigBean is not initialized");
		}

		return sBean;
	}
	//for backward compatibilty
	public static ClientServicePayloadLogConfigBean getPayloadLogInstance(
			String adminName, String clientName) {
	return getPayloadLogInstance(adminName, clientName,null);

	}
	public static void initConfigBean(ClientConfigHolder config) {
		initInvokerConfigBean(config);
		initTransportConfigBeans(config);
		initPayloadLogConfigBean(config);
	}

	private static void initInvokerConfigBean(ClientConfigHolder config) {
		ClientServiceInvokerConfigBean sBean = null;
		try {
			String lookupName = getLookupName(config);

			synchronized (s_invokerConfigBeans) {
				sBean = s_invokerConfigBeans.get(lookupName);
				if (sBean == null) {
					sBean = new ClientServiceInvokerConfigBean(config);

					ClientServiceInvokerConfigBeanListener listener = new ClientServiceInvokerConfigBeanListener(
							sBean);
					sBean.addVetoableChangeListener(listener);
					sBean.addPropertyChangeListener(listener);

					s_invokerConfigBeans.put(lookupName, sBean);
				}
			}
		} catch (Throwable e) {
			throw new InitializationException(e);
		}
	}

	private static void initTransportConfigBeans(ClientConfigHolder config) {
		ClientServiceTransportConfigBean sBean = null;
		final MessageProcessorConfigHolder msgProcConfig = config.getMessageProcessorConfig();
		try {
			synchronized (s_transportConfigBeans) {
				for (Map.Entry<String,String> e : msgProcConfig.getTransportClasses().entrySet()) {
					String lookupName = getLookupTransportName(config.getAdminName(), config.getClientName(), config.getEnvName(), e.getKey());

					sBean = s_transportConfigBeans.get(lookupName);
					if (sBean == null) {
						sBean = new ClientServiceTransportConfigBean(config, e.getKey());

						ClientServiceTransportConfigBeanListener listener = new ClientServiceTransportConfigBeanListener(
								sBean);
						sBean.addVetoableChangeListener(listener);
						sBean.addPropertyChangeListener(listener);

						s_transportConfigBeans.put(lookupName, sBean);
					}
				}
			}

			synchronized (s_transportDynaConfigBeans) {
				for (String transportName : msgProcConfig.getTransportClasses().keySet()) {
					DynamicConfigBean dBean = null;
					dBean = s_transportDynaConfigBeans.get(transportName);
					if (dBean == null) {
						dBean = ClientServiceConfigBean.createDynamicConfigBean(config, transportName + ".TransportHeaders");
						TransportHeadersConfigBeanListener listener =
							new TransportHeadersConfigBeanListener(dBean, config.getAdminName(), config.getClientName(), config.getEnvName(), transportName);
						listener.setNameValueMap(msgProcConfig.getTransportHeaderOptions().get(transportName));
						dBean.addVetoableChangeListener(listener);
						dBean.addPropertyChangeListener(listener);

						s_transportDynaConfigBeans.put(transportName, dBean);
					}
				}
			}

		} catch (Throwable e) {
			throw new InitializationException(e);
		}
	}

	static class TransportHeadersConfigBeanListener extends DynamicConfigBeanListener {

		protected final String m_transportName;

		TransportHeadersConfigBeanListener(DynamicConfigBean bean,
				String adminName, String clientName, String envName,
				String transportName) {
			super(bean, adminName, clientName, envName);

			m_transportName = transportName;
		}

		@Override
		protected Map<String, String> extractMapFromConfig(ClientConfigHolder configHolder) {
			final Map<String, Map<String, String>> allTransportHeaderOptions = configHolder.getMessageProcessorConfig().getTransportHeaderOptions();
			Map<String, String> transportHeaderOptions = allTransportHeaderOptions.get(m_transportName);
			if (transportHeaderOptions == null) {
				transportHeaderOptions = new HashMap<String, String>();
				allTransportHeaderOptions.put(getAdminName(), transportHeaderOptions);
			}

			return transportHeaderOptions;
		}

	}

	private static void initPayloadLogConfigBean(ClientConfigHolder config) {
		ClientServicePayloadLogConfigBean sBean = null;
		try {
			String lookupName = getLookupName(config);

			synchronized (s_payloadLogConfigBeans) {
				sBean = s_payloadLogConfigBeans.get(lookupName);
				if (sBean == null) {
					sBean = new ClientServicePayloadLogConfigBean(config);

					ClientServicePayloadLogConfigBeanListener listener = new ClientServicePayloadLogConfigBeanListener(
							sBean);
					sBean.addVetoableChangeListener(listener);
					sBean.addPropertyChangeListener(listener);

					s_payloadLogConfigBeans.put(lookupName, sBean);
				}
			}
		} catch (Throwable e) {
			throw new InitializationException(e);
		}
	}

	private static String getLookupName(ClientConfigHolder config) {
		// lookUp name needs to use environment name as well if holder has it.
		String lookupName = config.getAdminName() + "."
				+ config.getClientName();
		return config.getEnvName() == null ? lookupName : lookupName + "."
				+ config.getEnvName();
	}

	private static String getLookupTransportName(String adminName,
			String clientName, String envName, String transportName) {
		String transportLookupName = adminName + "." + clientName;
		return envName == null ? transportLookupName + "." + transportName
				: transportLookupName + "." + envName + "." + transportName;
	}

	static abstract class DynamicConfigBeanListener extends ClientServiceConfigBeanListener {

		protected final String m_adminName;

		protected final String m_clientName;

		protected final String m_envName;

		DynamicConfigBeanListener(DynamicConfigBean bean,
				String adminName, String clientName, String envName) {
			super(bean);

			m_adminName = adminName;
			m_clientName = clientName;
			m_envName = envName;
		}

		@Override
		protected String getAdminName() {
			return m_adminName;
		}

		@Override
		public String getClientName() {
			return m_clientName;
		}

		@Override
		public String getEnvName() {
			return m_envName;
		}

		@Override
		protected void setValuesForUpdate(PropertyChangeEvent evt) throws Exception {
			String name = evt.getPropertyName();
			if (name == null || name.equals(DynamicConfigBean.EXTERNAL_MUTABLE)) {
				return;
			}
			Object value = evt.getNewValue();
			if (!m_dynamicBean.hasProperty(name)) m_dynamicBean.addProperty(name, value);
			else m_dynamicBean.setPropertyValue(name, value.toString());
		}

		protected void setValuesForVeto(PropertyChangeEvent evt, Map<String, String> nameValueMap)
		throws PropertyVetoException {
			try {
				String name = evt.getPropertyName();
				if (name == null || name.equals(DynamicConfigBean.EXTERNAL_MUTABLE)) {
					return;
				}
				Object value = evt.getNewValue();
				validateStringValue(evt, name, value);
				synchronized(nameValueMap) {
					nameValueMap.put(name, value.toString());
				}

			} catch (Exception e) {
				throw new PropertyVetoException(e.getMessage(), evt);
			}
		}

		protected void setNameValueMap(Map<String, String> nameValueMap)
		throws DynamicBeanPropertyCreationException {
			if (nameValueMap == null) return;
			Set<String> nameSet = nameValueMap.keySet();
			for (String name : nameSet) {
				if (!m_dynamicBean.hasProperty(name)) {
					m_dynamicBean.addProperty(name, nameValueMap.get(name));
				}
			}
		}

		protected Map<String, String> getNameValueMap() {
			Map<String, String> map = new LinkedHashMap<String, String>();

			for (Iterator iter = m_dynamicBean.getAllPropertyInfos(); iter.hasNext(); ) {
				BeanPropertyInfo p = (BeanPropertyInfo) iter.next();
				if (!DynamicConfigBean.EXTERNAL_MUTABLE.equals(p.getName())) {
					map.put(p.getName(), (String) p.getPropertyValue(m_dynamicBean));
				}
			}
			return map;
		}

		@Override
		protected void setValuesForVeto(ClientConfigHolder configHolder, PropertyChangeEvent evt)
		throws PropertyVetoException {

			Map<String, String> nameValueMap = extractMapFromConfig(configHolder);
			setValuesForVeto(evt, nameValueMap);

		}

		abstract Map<String, String> extractMapFromConfig(ClientConfigHolder configHolder);

	}

}
