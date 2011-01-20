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
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDescFactory;

import com.ebay.kernel.bean.configuration.BaseConfigBean;
import com.ebay.kernel.bean.configuration.DynamicConfigBean;
import com.ebay.kernel.bean.configuration.PropertyChangeHistory;

/**
 * @author idralyuk
 */
public abstract class ServiceConfigBeanListener implements VetoableChangeListener, PropertyChangeListener {

	protected ServiceConfigBean m_serviceBean; // Only one of these is going to be used at runtime
	protected DynamicConfigBean m_dynamicBean; // If the constructor is called with a DynamicConfigBean
	protected boolean m_isDynamic = false;     // this boolean will be set to true
	
	private static Logger m_logger = null;

	private static Logger getLogger() {
		if (null == m_logger) {
			m_logger = LogManager.getInstance(ServiceConfigBeanListener.class);
		}
		return m_logger;
	}

	public ServiceConfigBeanListener(ServiceConfigBean bean) {
		m_serviceBean = bean;
	}
	
	public ServiceConfigBeanListener(DynamicConfigBean bean) {
		m_dynamicBean = bean;
		m_isDynamic = true;
	}

	protected BaseConfigBean getBean() {
		if (m_isDynamic) return m_dynamicBean;
		else return m_serviceBean;
	}
	
	protected String getAdminName() {
		if (!m_isDynamic) {
			return m_serviceBean.getAdminName();
		}
		return "*** adminName ***"; // This will be overriden by child classes
	}
	
	public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {

		ServiceConfigManager configMgr = null;
		ServiceConfigHolder configGolden = null;
		ServiceConfigHolder config = null;
		String adminName = getAdminName();
		try {
			configMgr = ServiceConfigManager.getInstance();
			config = getConfigForUpdate(configMgr, adminName);
			configGolden = config.copy();

			setValuesForVeto(config, evt);
			reloadServiceDesc(configMgr, config, adminName);

		} catch (Throwable e) {
			e.printStackTrace(); // temporarily
			String msg= "Failed to update config bean for admin=" + adminName + " ";
			
			try {
				reloadServiceDesc(configMgr, configGolden, adminName);
			} catch (ServiceException e1) {
				getLogger().log(Level.WARNING, msg + e1.toString());
				throw new PropertyVetoException(msg, evt);
			}
			getLogger().log(Level.WARNING, msg + e.toString());
			throw new PropertyVetoException(msg, evt);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// TODO: how can we group multiple changes, so that we reload
		// ServiceDesc only once
		PropertyChangeHistory changeHistory = getBean().createChangeHistory(evt);
		changeHistory.updateStart();

		try {
			setValuesForUpdate(evt);
			changeHistory.updateSuccess();
		} catch (Throwable e) {
			logFailure(changeHistory, getAdminName(), e);
			throw new IllegalArgumentException(e.toString());
		}
	}

	ServiceConfigHolder getConfigForUpdate(ServiceConfigManager configMgr, String adminName) throws Exception {
		ServiceConfigHolder configHolder = configMgr.getConfigForUpdate(adminName);
		return configHolder;
	}

	void reloadServiceDesc(ServiceConfigManager configMgr, ServiceConfigHolder config, String adminName) throws ServiceException {
		configMgr.updateConfig(adminName, config);

		ServerServiceDescFactory descFactory = ServerServiceDescFactory.getInstance();
		descFactory.reloadServiceDesc(adminName);
	}

	void logFailure(PropertyChangeHistory changeHistory, String adminName, Throwable e) {
		if (changeHistory != null) {
			changeHistory.updateFailed(e);
		}

		String msg = "Failed to update config bean for admin=" + adminName + " " + e.toString();
		getLogger().log(Level.WARNING, msg);
	}

	public void validateBooleanValue(PropertyChangeEvent evt, String name, String value) throws PropertyVetoException {
		if (("true".equalsIgnoreCase(value)) || "false".equalsIgnoreCase(value)) {
			return;
		}

		String msg = "Encountered invalid boolean property.value=" + name + "." + value + " ";
		throw new PropertyVetoException(msg, evt);
	}

	public void validateIntegerValue(PropertyChangeEvent evt, String name, Object value) throws PropertyVetoException {
		if (! (value instanceof Integer)) {
			String msg = "Encountered invalid numeric property.value=" + name + "." + value + " ";
			throw new PropertyVetoException(msg, evt);
		}
	}

	public void validateStringValue(PropertyChangeEvent evt, String name, Object value) throws PropertyVetoException {
		if (! (value instanceof String)) {
			String msg = "Encountered invalid String property.value=" + name + "." + value + " ";
			throw new PropertyVetoException(msg, evt);
		}
	}
	public void validateStringListValue(PropertyChangeEvent evt, String name, Object value) throws PropertyVetoException {
		if (! (value instanceof ServiceConfigBean.StringList)) {
			String msg = "Encountered invalid StringList property.value=" + name + "." + value + " ";
			throw new PropertyVetoException(msg, evt);
		}
	}

	protected abstract void setValuesForVeto(ServiceConfigHolder configHolder,
			PropertyChangeEvent evt) throws PropertyVetoException;

	protected abstract void setValuesForUpdate(PropertyChangeEvent evt)
			throws Exception;
}
