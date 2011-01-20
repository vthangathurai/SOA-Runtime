/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.impl.internal.service.BaseServiceDescFactory;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;

import com.ebay.kernel.bean.configuration.BaseConfigBean;
import com.ebay.kernel.bean.configuration.BeanConfigCategoryInfo;
import com.ebay.kernel.bean.configuration.BeanPropertyInfo;
import com.ebay.kernel.bean.configuration.ConfigCategoryCreateException;
import com.ebay.kernel.bean.configuration.DynamicBeanPropertyCreationException;
import com.ebay.kernel.bean.configuration.DynamicConfigBean;
import com.ebay.kernel.initialization.InitializationException;

public class DynamicMonitoringChangeMgr implements PropertyChangeListener, VetoableChangeListener
{

	private static final String CONFIG_CATEGORY_PREFIX = SOAConstants.CONFIG_BEAN_GROUP + ".monitoring.";

	private static final String DESC = "This config bean enables monitoring level of SOA services to be updated dynamically";

	private static final String PROP_NAME_ACTION = "ACTION";

	private final boolean m_isClientSide;

	private DynamicConfigBean m_configBean;

	private Map<String, MonitoringLevel> m_configData = new HashMap<String, MonitoringLevel>();

	private static DynamicMonitoringChangeMgr s_clientInstance;

	private static DynamicMonitoringChangeMgr s_serverInstance;

	private DynamicMonitoringChangeMgr(boolean isClientSide) {
		m_isClientSide = isClientSide;
		initConfigBean();
	}

	private static Logger m_logger = null;

	private static Logger getLogger() {
		if (null == m_logger) {
			m_logger = LogManager.getInstance(DynamicMonitoringChangeMgr.class);
		}
		return m_logger;
	}

	public static synchronized DynamicMonitoringChangeMgr getClientInstance() {
		if (s_clientInstance == null) {
			s_clientInstance = new DynamicMonitoringChangeMgr(true);
		}
		return s_clientInstance;
	}

	public static synchronized DynamicMonitoringChangeMgr getServerInstance() {
		if (s_serverInstance == null) {
			s_serverInstance = new DynamicMonitoringChangeMgr(false);
		}
		return s_serverInstance;
	}

	private void initConfigBean() {
		try {
			BeanConfigCategoryInfo category = BeanConfigCategoryInfo
					.createBeanConfigCategoryInfo(
							CONFIG_CATEGORY_PREFIX + (m_isClientSide ? "Client" : "Server"),
							null, SOAConstants.CONFIG_BEAN_GROUP, false, true,
							null, DESC, true);
			m_configBean = new DynamicConfigBean(category);
			m_configBean.setExternalMutable();

			if (!m_configBean.hasProperty(PROP_NAME_ACTION)) {
				m_configBean.addProperty(PROP_NAME_ACTION, "");
			}

			m_configBean.addPropertyChangeListener(this);
			m_configBean.addVetoableChangeListener(this);
		} catch (ConfigCategoryCreateException e) {
			getLogger().log(Level.SEVERE,
					"Failed to initialize config bean: " + e.toString());
			throw new InitializationException(e);
		} catch (DynamicBeanPropertyCreationException e) {
			getLogger().log(Level.SEVERE,
					"Failed to initialize config bean: " + e.toString());
			throw new InitializationException(e);
		}
	}

	public MonitoringLevel getMonitoringLevel(String adminName) {
		MonitoringLevel result = null;
		synchronized (m_configData) {
			result = m_configData.get(adminName);
		}
		return result;
	}

	/**
	 * This method gets called when a bound property is changed.
	 *
	 * @param evt
	 *            A PropertyChangeEvent object describing the event source and
	 *            the property that has changed.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		BeanPropertyInfo info = BaseConfigBean.getBeanPropertyInfo(evt);
		String adminName = info.getName();
		String value = (String) evt.getNewValue();

		if (adminName.equals(PROP_NAME_ACTION)) {
			if (value == null || value.length() == 0
					|| "done".equalsIgnoreCase(value)) {
				return;
			}

			executeCommand(value);
			return;
		}

		// Do need to support setting a Monitoring Level for all services?
		// if (componentName.equalsIgnoreCase(ALL_COMPONENTS) == true) {
		try {
			parseServiceName(adminName, evt);
		} catch (Throwable e) {
			// exception is logged in veto already
			return;
		}

		MonitoringLevel level = MonitoringLevel.NORMAL;
		try {
			level = parseMonitoringLevel(value, adminName, evt);
		} catch (Throwable e) {
			// exception is logged in veto already
			return;
		}

		updateMonitoringLevel(adminName, level);
	}

	public void vetoableChange(PropertyChangeEvent evt)
			throws PropertyVetoException {
		BeanPropertyInfo info = BaseConfigBean.getBeanPropertyInfo(evt);
		String adminName = info.getName();
		String value = (String) evt.getNewValue();

		if (adminName.equals(PROP_NAME_ACTION)) {
			if (value == null || value.length() == 0
					|| "done".equalsIgnoreCase(value)) {
				return;
			}

			verifyCommand(value);
			return;
		}

		parseServiceName(adminName, evt);

		parseMonitoringLevel(value, adminName, evt);
	}

	private void verifyCommand(String value) {
		// TODO: verify action value
	}

	private void executeCommand(String value) {
		// perform action specified in VALUE
		if (value.startsWith("reset:")) {
			// TODO: get adminName after ":" and reset all data for that
			// admin name
			m_configBean.setPropertyValue(PROP_NAME_ACTION, "done");
		}
	}

	private void updateMonitoringLevel(String adminName, MonitoringLevel level) {
		synchronized (m_configData) {
			MonitoringLevel cachedLevel = m_configData.get(adminName);
			if (cachedLevel != null
					&& (cachedLevel.ordinal() == level.ordinal())) {
				return;
			}
			m_configData.put(adminName, level);
		}

		resetMonitoringLevel(adminName);
	}

	private void resetMonitoringLevel(String adminName) {
		MetricsConfigManager mcm = null;
		if (m_isClientSide) {
			mcm = MetricsConfigManager.getClientInstance();
		} else {
			mcm = MetricsConfigManager.getServerInstance();
		}
		mcm.resetMonitoringLevel(adminName);
	}

	private void parseServiceName(String adminName, PropertyChangeEvent evt)
			throws PropertyVetoException {
		BaseServiceDescFactory descFactory = null;

		if (m_isClientSide) {
			descFactory = BaseServiceDescFactory.getClientInstance();
		} else {
			descFactory = BaseServiceDescFactory.getServerInstance();
		}

		QName qName = descFactory.findKnownQNameByAdminName(adminName);
		if (qName == null) {
			throw new PropertyVetoException("Unknown admin name " + adminName,
					evt);
		}
	}

	private MonitoringLevel parseMonitoringLevel(String value,
			String adminName, PropertyChangeEvent evt)
			throws PropertyVetoException {
		try {
			return MonitoringLevel.valueOf(value.toUpperCase().trim());
		} catch (Throwable e) {
			throw new PropertyVetoException("Invalid monitoring level: "
					+ value + " for service " + adminName, evt);
		}
	}

}
