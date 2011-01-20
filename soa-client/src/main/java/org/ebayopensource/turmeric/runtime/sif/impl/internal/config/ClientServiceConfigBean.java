/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.config;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.service.Service;

import com.ebay.kernel.bean.configuration.BaseConfigBean;
import com.ebay.kernel.bean.configuration.BeanConfigCategoryInfo;
import com.ebay.kernel.bean.configuration.ConfigCategoryCreateException;
import com.ebay.kernel.bean.configuration.DynamicConfigBean;

public abstract class ClientServiceConfigBean extends BaseConfigBean {
	
	private static String getCategoryId(String category, String adminName, String clientName, String envName) {
		// categoryId needs to be unique. Use envName if it is set.
		String categoryId =  SOAConstants.CONFIG_BEAN_PREFIX_CLIENT + adminName + "."
			+ clientName + ".";
		return envName == null ? categoryId + category : categoryId + envName + "." + category;
	}

	private static String getDescription(String category, String adminName, String clientName, String envName) {
		String description =  "SOA Client Service " + category + " Config for " + adminName;
		return envName == null ? description + " (" + clientName + ")" : description + " (" + clientName + "." + envName + ")";
	}

	static DynamicConfigBean createDynamicConfigBean(ClientConfigHolder config, String category)
	throws ConfigCategoryCreateException {

		final BeanConfigCategoryInfo beanInfo = BeanConfigCategoryInfo
					.createBeanConfigCategoryInfo(
							ClientServiceConfigBean.getCategoryId(category, config.getAdminName(), config.getClientName(), config.getEnvName()),
							null,
							SOAConstants.CONFIG_BEAN_GROUP,
							false, // persistent
							true, // ops managable
							null, // persistent file
							ClientServiceConfigBean.getDescription(category, config.getAdminName(), config.getClientName(), config.getEnvName()), // description
							true // return an existing one
		);
	
		DynamicConfigBean configBean = new DynamicConfigBean(beanInfo);
		configBean.setExternalMutable();
	
		return configBean;
	}
	
	// identification
	protected final String m_adminName;
	protected final String m_clientName;
	protected final String m_envName;

	protected ClientServiceConfigBean(ClientConfigHolder config) {

		m_adminName = config.getAdminName();
		m_clientName = config.getClientName();
		m_envName = config.getEnvName();

	}

	/**
	 * Initialize the internal state of the config bean. This method is to be called from the
	 * constructor in a subclass, following whatever pre-initializations the constructor needs to
	 * perform.
	 *   
	 * @param config the config to initialize from. 
	 * @param category the category the bean will be listed under. 
	 * 
	 * @throws ConfigCategoryCreateException for problems attaching to the given category.
	 * @throws ServiceException whatever other evil
	 */
	protected void init(ClientConfigHolder config, String category) throws ConfigCategoryCreateException, ServiceException {
		// get defaults from config
		setDefaultsFromConfig(config);

		final BeanConfigCategoryInfo beanInfo = BeanConfigCategoryInfo
				.createBeanConfigCategoryInfo(
						getCategoryId(category),
						null,
						SOAConstants.CONFIG_BEAN_GROUP,
						false, // persistent
						true, // ops managable
						null, // persistent file
						getDescription(category), // description
						true // return an existing one
				);

		/***********************************************************************
		 * load the config file ClientServiceTransportXXXConfig.xml if it
		 * exists, to override the predefined properties
		 **********************************************************************/
		loadDefaultOverrides(beanInfo, Service.class,
				getDefConfigFile(category));
		loadDefaultOverrides(beanInfo, Service.class,
				getConfigFile(category));
		

		/**
		 * loadInitValueFromCM should be "true" so that the persisted (Ops
		 * choice) values are loaded at restart to solve a bug in which the
		 * conf/hard-coded attribute values are used to replace the "persisted"
		 * ones.
		 */
		init(beanInfo, true);


		// if any values were persistently overriden, we need to apply
		// changes to 'config' object
		updateConfigHolder(config);
	}

	public String getAdminName() {
		return m_adminName;
	}

	public String getClientName() {
		return m_clientName;
	}
	public String getEnvName() {
		return m_envName;
	}

	private String getCategoryId(String category) {
		return ClientServiceConfigBean.getCategoryId(category, m_adminName, m_clientName, m_envName);
	}

	private String getDescription(String category) {
		return ClientServiceConfigBean.getDescription(category, m_adminName, m_clientName, m_envName);
	}

	private String getDefConfigFile(String category) {
		return "ClientService" + category + m_adminName
				+ m_clientName + "DefConfig.xml";
	}

	private String getConfigFile(String category) {
		
		String fileName = "ClientService" + category + m_adminName
		+ m_clientName;
		return  m_envName==null ? fileName+ "Config.xml" : fileName+m_envName + "Config.xml";
	}

	abstract protected void setDefaultsFromConfig(ClientConfigHolder config) throws ServiceException;

	abstract protected void updateConfigHolder(ClientConfigHolder config) throws ServiceException;

}
