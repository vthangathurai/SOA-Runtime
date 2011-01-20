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
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDescFactory;

import com.ebay.kernel.bean.configuration.DynamicConfigBean;
import com.ebay.kernel.bean.configuration.PropertyChangeHistory;

abstract public class ClientServiceConfigBeanListener implements
		VetoableChangeListener, PropertyChangeListener {

	protected ClientServiceConfigBean m_bean;
	protected DynamicConfigBean m_dynamicBean; // If the constructor is called with a DynamicConfigBean
	protected boolean m_isDynamic = false; // will be set to true by the dynamic constructor

	private static Logger m_logger = null;

	private static Logger getLogger() {
		if (null == m_logger) {
			m_logger = LogManager
					.getInstance(ClientServiceTransportConfigBeanListener.class);
		}
		return m_logger;
	}

	public ClientServiceConfigBeanListener(ClientServiceConfigBean bean) {
		m_bean = bean;
	}

	public ClientServiceConfigBeanListener(DynamicConfigBean bean) {
		m_dynamicBean = bean;
		m_isDynamic = true;
	}

	public void vetoableChange(PropertyChangeEvent evt)
			throws PropertyVetoException {

		ClientConfigManager configMgr = null;
		ClientConfigHolder configGolden = null;
		ClientConfigHolder config = null;
		final String adminName = getAdminName();
		final String clientName = getClientName();
		try {
			configMgr = ClientConfigManager.getInstance();
			config = getConfigForUpdate(configMgr);
			configGolden = config.copy();

			setValuesForVeto(config, evt);
			reloadServiceDesc(configMgr, config, adminName, clientName);
		} catch (PropertyVetoException e) {
			if (getLogger().isLoggable(Level.WARNING)) {
				String msg = "Failed to update config bean for admin.clientName="
						+ adminName + "." + clientName
						+ ": " + e + (e.getMessage() == null ? "" : "('" + e.getMessage() + "')") 
						+ " ";
				getLogger().log(Level.WARNING, msg, e);
			}
			throw e;
		} catch (Throwable e) {
			String msg = null;
			if (getLogger().isLoggable(Level.WARNING)) {
				msg = "Failed to update config bean for admin.clientName="
						+ adminName + "." + clientName
						+ ": " + e + (e.getMessage() == null ? "" : "('" + e.getMessage() + "')") 
						+ " ";
				getLogger().log(Level.WARNING, msg, e);
			}
			try {
				if (configGolden != null) {
					reloadServiceDesc(configMgr, configGolden, adminName, clientName);
				}
			} catch (ServiceException e1) {
				if (getLogger().isLoggable(Level.WARNING)) {
					getLogger().log(Level.WARNING, msg + ": " + e1.toString());
				}
			}
			throw new PropertyVetoException(msg, evt);
		}
	}

	/**
	 * Defines the client name of the bean; please override for dynamic subclasses.
	 *  
	 * @return bean's client name. 
	 */
	protected String getClientName() {
		if (!m_isDynamic) {
			return m_bean.getClientName();
		}
		throw new IllegalStateException("Your dynamic bean listener doesn't override getAdminName()");
	}

	/**
	 * Defines the admin name of the bean; please override for dynamic subclasses.
	 *  
	 * @return bean's admin name. 
	 */
	protected String getAdminName() {
		if (!m_isDynamic) {
			return m_bean.getAdminName();
		}
		throw new IllegalStateException("Your dynamic bean listener doesn't override getAdminName()");
	}

	/**
	 * Defines the env name for the served config bean. Override for dynamic bean listeners.  
	 * @return the env name
	 */
	protected String getEnvName() {
		if (!m_isDynamic) {
			return m_bean.getEnvName();
		}
		throw new IllegalStateException("Your dynamic bean listener doesn't override getEnvName()");
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// TODO: how can we group multiple changes, so that we reload
		// ServiceDesc only once
		PropertyChangeHistory changeHistory = m_bean.createChangeHistory(evt);
		changeHistory.updateStart();

		try {
			setValuesForUpdate(evt);
			changeHistory.updateSuccess();
		} catch (Throwable e) {
			logFailure(changeHistory, e);
			throw new IllegalArgumentException(e.toString());
		}
	}

	ClientConfigHolder getConfigForUpdate(ClientConfigManager configMgr)
			throws Exception {
		String adminName = getAdminName();
		String clientName = getClientName();
		String envName = getEnvName();

		ClientConfigHolder configHolder = configMgr.getConfigForUpdate(
				adminName, clientName, envName);
		return configHolder;
	}

	void reloadServiceDesc(ClientConfigManager configMgr,
			ClientConfigHolder config, String adminName, String clientName) 
	throws ServiceException {
		String envName = getEnvName();
		configMgr.updateConfig(adminName, clientName, config);

		ClientServiceDescFactory descFactory = ClientServiceDescFactory
				.getInstance();
		descFactory.reloadServiceDesc(adminName, clientName, envName);
	}

	void logFailure(PropertyChangeHistory changeHistory, Throwable e) {
		if (changeHistory != null) {
			changeHistory.updateFailed(e);
		}

		String msg = "Failed to update config bean for admin.clientName="
				+ getAdminName() + "." + getClientName() + " "
				+ e.toString();
		getLogger().log(Level.WARNING, msg);
	}

	public void validateBooleanValue(PropertyChangeEvent evt, String name,
			String value) throws PropertyVetoException {
		if (("true".equalsIgnoreCase(value)) || "false".equalsIgnoreCase(value)) {
			return;
		}

		String msg = "Encountered invalid boolean property.value=" + name + "."
				+ value + " ";
		throw new PropertyVetoException(msg, evt);
	}

	public void validateIntegerValue(PropertyChangeEvent evt, String name,
			String value) throws PropertyVetoException {
		try {
			Integer.valueOf(value);
		} catch (NumberFormatException e) {
			String msg = "Encountered invalid numeric property.value=" + name
					+ "." + value + " ";
			throw new PropertyVetoException(msg + e.toString(), evt);
		}
	}

	public void validateStringValue(PropertyChangeEvent evt, String name, Object value) throws PropertyVetoException {
		if (!(value instanceof String)) {
			String msg = "Property '" + name + "' has a " 
				+ (value == null ? "null value"  
					: "value '" + value + "' of type " + value.getClass().getName()) 
				+ " ";
			throw new PropertyVetoException(msg, evt);
		}
	}
	protected abstract void setValuesForVeto(ClientConfigHolder configHolder,
			PropertyChangeEvent evt) throws PropertyVetoException;

	protected abstract void setValuesForUpdate(PropertyChangeEvent evt)
			throws Exception;

}