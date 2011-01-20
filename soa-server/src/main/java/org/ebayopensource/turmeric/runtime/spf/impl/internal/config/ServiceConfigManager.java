/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyHolder;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceNotFoundException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.ConfigManager;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.DomParseUtils;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.GlobalConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.GlobalConfigMapper;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MetadataPropertyConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.OperationPropertyConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.OperationPropertyConfigMapper;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.TypeMappingConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ParseUtils;
import org.ebayopensource.turmeric.runtime.common.utils.Preconditions;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDescFactory;

public class ServiceConfigManager extends ConfigManager {
	private static final Logger LOG = Logger.getLogger(ServiceConfigManager.class.getName());
	
	public static final String SYS_PROP_SOA_GLOBAL_CONFIG_ROOT = "SOA_GLOBAL_CONFIG_ROOT";

	private static final String CONFIG = "config/";
	private static final String BASE_PATH = "META-INF/soa/services/";
	private static final String BASE_PATH2 = "META-INF/soa/common/";
	private static final String COMMON_PATH = BASE_PATH2 + CONFIG;
	private static final String SERVICE_FILE_NAME = "services.txt";
	private static final String GLOBAL_FILENAME = "GlobalServiceConfig.xml";
	private static final String SERVICE_FILENAME = "ServiceConfig.xml";
	private static final String SERVICE_SCHEMA = "server/ServiceConfig.xsd";
	private static final String SECURITY_SCHEMA = "server/SecurityPolicy.xsd";
	private static final String CACHE_SCHEMA = "server/CachePolicy.xsd";
	private static final String GLOBAL_SCHEMA = "server/GlobalServiceConfig.xsd";
	private static ConfigManager s_instance = null;
	private static final String s_globalConfigRoot;


	private Element m_globalData = null;
	private Element m_groupData = null;
	private HashMap<String, ServiceConfigHolder> m_configData = new HashMap<String, ServiceConfigHolder>();
	private GlobalConfigHolder m_globalConfig;
	private boolean m_namesLoaded = false;
	private boolean m_configLoaded = false;

	private String m_configPath = BASE_PATH + CONFIG;
	private String m_commonPath = COMMON_PATH;
	private String m_globalConfigPath = s_globalConfigRoot;
    private Set<String> m_svcNames = new HashSet<String>();

	static {
		String configRoot = System.getProperty(SYS_PROP_SOA_GLOBAL_CONFIG_ROOT);
		StringBuffer buf = new StringBuffer();
		if (configRoot != null) {
			buf.append(configRoot);
			if (!configRoot.endsWith("/")) {
				buf.append('/');
			}
			s_globalConfigRoot = buf.toString();
		} else {
			s_globalConfigRoot = BASE_PATH + "config/";
		}
	}

	public static ServiceConfigManager getInstance() throws ServiceCreationException {
    	if (s_instance == null) {
    		s_instance = new ServiceConfigManager();
    	}
    	return (ServiceConfigManager)s_instance;
    }

	public synchronized ServiceConfigHolder getConfig(String adminName) throws ServiceException {
		loadConfig();

		// In case service is not preinitialized via services.txt, we need to initialize it now.
		initService(adminName);
		ServiceConfigHolder outData = m_configData.get(adminName);
		if (outData == null) {
			throw new ServiceNotFoundException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_NO_SUCH_SERVICE,
					ErrorConstants.ERRORDOMAIN, new Object[] {adminName}));
		}
		return outData;
	}

	/*
	 * Get all loaded service names.
	 * This will do a full configuration load each time it is called.  This allows
	 * multiple servlets to initialize, each adding some services and ServiceDescs.
	 */
	public synchronized Collection<String> getAllServiceAdminNames() throws ServiceException {
		loadNames();
		return Collections.unmodifiableCollection(m_svcNames);
	}

	public synchronized ServiceConfigHolder getConfigForUpdate(String adminName) throws ServiceException {
		ServiceConfigHolder outData = getConfig(adminName);
		if (outData == null) {
			return null;
		}
		return outData.copy();
	}

	public synchronized void updateConfig(String serviceAdminName, ServiceConfigHolder holder) {
		holder.lockReadOnly();
		m_configData.put(serviceAdminName, holder);
	}

	public synchronized GlobalConfigHolder getGlobalConfig() throws ServiceException {
		loadGlobalConfig();
		return m_globalConfig;
	}

	public synchronized GlobalConfigHolder getGlobalConfigForUpdate() throws ServiceException {
		loadGlobalConfig();
		if (m_globalConfig == null) {
			return null;
		}
		return m_globalConfig.copy();
	}

	private synchronized void loadGlobalConfig() throws ServiceException {
		if (m_globalData != null) {
			return;
		}
		String globalFileName = getGlobalFilePath();
   	 	loadGlobalData(globalFileName);
   	 	m_globalConfig = new GlobalConfigHolder();
   	 	GlobalConfigMapper.map(globalFileName, m_globalData, m_globalConfig);

   	 	// Get group element but delay looking into it until we see group references present (findGroup).
   	 	if (m_globalData != null) {
   	 		m_groupData = DomParseUtils.getSingleElement(globalFileName, m_globalData, "service-config-groups");
   	 	}
	}

	private synchronized void loadConfig() throws ServiceException {
		if (m_configLoaded) {
			return;
		}

		loadGlobalConfig();

		m_configLoaded = true;
	}

	public String getGlobalConfigPath() {
		return m_globalConfigPath;
	}

	private synchronized void loadNames() throws ServiceException {
		if (m_namesLoaded) {
			return;
		}

		String serviceFilename = m_globalConfigPath + SERVICE_FILE_NAME;
		InputStream inStream=ParseUtils.getFileStream(serviceFilename);

    	if (inStream == null) {
    		return;
    	}

    	BufferedReader br = null;
   		br = new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset()));

		String line;
		try {
			while ((line = br.readLine()) != null) {
				String serviceName = line.trim();
				if (serviceName.length() > 0 && serviceName.charAt(0) != '#') {
					m_svcNames.add(serviceName);
				}
			}
		} catch (Exception e) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_IO_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {serviceFilename}), e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// ignore
			}
		}

		m_namesLoaded = true;
    }

    private synchronized void initService(String serviceName) throws ServiceCreationException, ServiceNotFoundException {
		String globalFileName = getGlobalFilePath();
		loadConfig(serviceName, globalFileName);
    }

	private void loadGlobalData(String globalFileName) throws ServiceCreationException {
		if (m_globalData != null)
			return;
		String globalSchemaName = s_schemaPath + GLOBAL_SCHEMA;
		Document globalDoc = ParseUtils.parseConfig(globalFileName, globalSchemaName, true, "global-service-config", ParseUtils.getSchemaCheckLevel());
		if (globalDoc != null) {
			m_globalData = globalDoc.getDocumentElement();
		}
	}

	private synchronized ServiceConfigHolder loadConfig(String adminName, String globalFileName)
		throws ServiceCreationException, ServiceNotFoundException {
		try {
			ServiceConfigHolder outData = m_configData.get(adminName);
			if (outData != null) {
				return outData;
			}
			String serviceConfigFileName = m_configPath + adminName + "/" + SERVICE_FILENAME;
			String serviceConfigSchemaName = s_schemaPath + SERVICE_SCHEMA;
	        Document configDoc = ParseUtils.parseConfig(serviceConfigFileName, serviceConfigSchemaName, true, "service-config", ParseUtils.getSchemaCheckLevel());
	        if (configDoc == null) {
	        	LOG.severe("Unable to find Service configuration: file=\"" + serviceConfigFileName + "\" schema=\"" + serviceConfigSchemaName + "\"");
	        	throw new ServiceNotFoundException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_NO_SUCH_SERVICE,
	        			ErrorConstants.ERRORDOMAIN, new Object[] {adminName}));
	        }
	        Element serviceConfig = configDoc.getDocumentElement();
	        String groupName = serviceConfig.getAttribute("group");
			Element serviceGroup = null;
	        if (groupName != null && groupName.trim().length() != 0 ) {
	        	serviceGroup = findGroup(globalFileName, groupName);
	        }

	        ServiceConfigHolder sch = ServiceConfigMapper.applyConfigs(adminName,
	        	serviceConfigFileName, globalFileName, serviceGroup, serviceConfig);

	        applyConfigBeanOverrides(sch);

	        TypeMappingConfigHolder typeMappings = loadTypeMappingData(
	        	m_commonPath + adminName + "/TypeMappings.xml");
	        sch.setTypeMappings(typeMappings);
	        OperationPropertyConfigHolder operationProperties = loadOperationProperties(
	        	m_configPath + adminName + "/service_operations.properties");
	        sch.setOperationProperties(operationProperties);
	        SecurityPolicyConfigHolder securityPolicy = loadSecurityPolicy(
	        	adminName,
	        	m_configPath + adminName + "/SecurityPolicy.xml",
	        	s_schemaPath + SECURITY_SCHEMA);
	        sch.setSecurityPolicy(securityPolicy);
	        String cachePolicyFileName = getCachePolicyFilePath(adminName);
			CachePolicyHolder cachePolicy = CachePolicyHolder.loadCachePolicy(
		        	adminName,
		        	cachePolicyFileName,
		        	s_schemaPath + CACHE_SCHEMA);
			sch.setCachePolicy(cachePolicy);
	        sch.lockReadOnly();
	        m_configData.put(adminName, sch);
	        return sch;
		} catch (ServiceCreationException se) {
			throw se;
		} catch (ServiceNotFoundException snfe) {
			throw snfe;
		} catch (Exception e) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_GENERIC_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {e.toString()}), e);
		}
	}

	private String getCachePolicyFilePath(String adminName) {
		String cachePolicyFileName = m_configPath + adminName + "/CachePolicy.xml";
		return cachePolicyFileName;
	}


	private OperationPropertyConfigHolder loadOperationProperties(String filename) throws ServiceCreationException {
		OperationPropertyConfigHolder holder = new OperationPropertyConfigHolder();
		if (!OperationPropertyConfigMapper.map(filename, getClassLoader(), holder, false)) {
			return null;
		}
		return holder;
	}

	private SecurityPolicyConfigHolder loadSecurityPolicy(String adminName, String filename, String schemaname) throws ServiceCreationException {
		Document securityDoc = ParseUtils.parseConfig(filename, schemaname, true, "security-policy", ParseUtils.getSchemaCheckLevel());
		if (securityDoc == null) {
			return null;
		}
		SecurityPolicyConfigHolder dst = new SecurityPolicyConfigHolder(adminName);
		SecurityPolicyMapper.map(filename, securityDoc.getDocumentElement(), dst);
		return dst;
	}

	private void applyConfigBeanOverrides(ServiceConfigHolder sch) {
		ServiceConfigBeanManager.initConfigBeans(sch);
	}

	private Element findGroup(String filename, String groupName) throws ServiceCreationException {
		if (m_groupData == null) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {filename, "Group '" + groupName + "' is referenced but no groups are defined"}));
		}
        NodeList nodes = DomParseUtils.getImmediateChildrenByTagName(m_groupData, "service-group");
        for (int i=0; i < nodes.getLength(); i++) {
        	Element serviceGroup = (Element)nodes.item(i);
        	String nameAttr = serviceGroup.getAttribute("name");
			if (nameAttr != null && nameAttr.equals(groupName)) {
				return serviceGroup;
			}
		}

        throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR,
        		ErrorConstants.ERRORDOMAIN, new Object[] {filename, "Cannot find group: " + groupName + ", global file=" + getGlobalFileUrl()}));
	}

	private String getGlobalFilePath() {
		return m_globalConfigPath + GLOBAL_FILENAME;
	}

	private String getGlobalFileUrl() {
		URL globalFileURL = getClassLoader().getResource(getGlobalFilePath());
		return (globalFileURL != null ? globalFileURL.getPath() : " ");
	}

	// For test usage only - to be deprecated
	private synchronized void setConfigPath(String path) {
		m_configPath = path;
		m_globalConfigPath = path;
	}

	// For test usage only - to be deprecated
	private synchronized void setCommonPath(String path) {
		m_commonPath = path;
	}

	// For test usage only - to be deprecated
	public synchronized void setConfigTestCase(String relativePath, String commonPath, boolean force) throws ServiceException {
		String newPath = BASE_PATH + relativePath + "/";
		if (!force && m_configPath != null && m_configPath.equals(newPath)) {
			return;
		}

		m_configLoaded = false;
		m_namesLoaded = false;
		m_configData.clear();
		m_svcNames.clear();
		m_globalData = null;
		m_groupData = null;
		m_globalConfig = null;

		setConfigPath(newPath);
		String newPath2 = BASE_PATH2 + commonPath + "/";
		setCommonPath(newPath2);

		ServerServiceDescFactory.getInstance().resetFactoryForUnitTest();
	}
	public void setConfigTestCase(String relativePath) throws ServiceException {
		setConfigTestCase(relativePath, relativePath, false);
	}
	public void setConfigTestCase(String relativePath, boolean force) throws ServiceException {
		setConfigTestCase(relativePath, relativePath, force);
	}
	public void setConfigTestCase(String relativePath, String commonPath) throws ServiceException {
		setConfigTestCase(relativePath, commonPath, false);
	}

	/**
	 * Returns the Service Meta data Properties
	 *
	 * @param adminName
	 * @return MetadataPropertyConfigHolder a non-null ConfigHolder
	 * @throws ServiceCreationException
	 * @throws ServiceException
	 */
	public MetadataPropertyConfigHolder getMetadataPropertyConfigHolder(String adminName)
	throws ServiceCreationException {
		Preconditions.checkNotNull(adminName);
		StringBuilder fileName = new StringBuilder().append(m_commonPath);
		fileName.append(adminName).append("/service_metadata.properties");
		try {
			return loadMetadataPropertyData(fileName.toString(), getGlobalConfig().getServiceLayerNames());
		}
		catch(ServiceException e) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_GENERIC_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {e.toString()}), e);
		}
	}

	public InputStream getCachePolicy(String adminName) throws ServiceCreationException {
		Preconditions.checkNotNull(adminName);
		String cachePolicyFilePath = getCachePolicyFilePath(adminName);
		InputStream cachePolicyStream = ParseUtils.getFileStream(cachePolicyFilePath);
		return cachePolicyStream;
	}
}
