/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.config;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceNotFoundException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.ConfigManager;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.DomParseUtils;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.GlobalConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.GlobalConfigMapper;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MessageProcessorConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MetadataPropertyConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.TypeMappingConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.BaseServiceDescFactory;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ParseUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.utils.Preconditions;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDescFactory;
import org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPSyncAsyncClientTransport;
import org.ebayopensource.turmeric.runtime.sif.service.ClientServiceId;

import com.ebay.kernel.context.RuntimeContext;
import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;

public class ClientConfigManager extends ConfigManager {

	private static Logger LOGGER = Logger.getInstance( ClientConfigManager.class );
	private static final String SYS_PROP_SOA_CLIENT_CONFIG_ROOT =  "SOA_CLIENT_CONFIG_ROOT";

	private static final String BASE_PATH = "META-INF/soa/client/";
	private static final String BASE_PATH2 = "META-INF/soa/common/";
	private static final String GLOBAL_FILENAME = "GlobalClientConfig.xml";
	private static final String CLIENT_FILENAME = "ClientConfig.xml";
	private static final String CLIENT_SCHEMA = "client/ClientConfig.xsd";
	private static final String GLOBAL_SCHEMA = "client/GlobalClientConfig.xsd";
	private static final QName DEFAULT_SERVICE = new QName("", "");
	private static ConfigManager s_instance = null;
	private static final char FILE_SEPERATOR = '/';
	private static  final String DEFAULT_BASE_PATH = BASE_PATH + "config/";
	private static final String s_clientConfigRoot;
	private Element m_globalData = null;
	private Element m_groupData = null;
	private Map<String, Map<String, ClientConfigHolder>> m_clientData = new HashMap<String, Map<String, ClientConfigHolder>>();
	private GlobalConfigHolder m_globalConfig;
	private boolean m_configLoaded = false;
	private String m_configPath =  s_clientConfigRoot;
	private String m_commonPath = BASE_PATH2 + "config/";
	private boolean isInTestMode = false;

	static {
		String configRoot = System.getProperty(SYS_PROP_SOA_CLIENT_CONFIG_ROOT);
		StringBuffer buf = new StringBuffer();

		if (configRoot != null) {
			configRoot = configRoot.trim();
			if (configRoot.startsWith("$", 0)) {
				int var = configRoot.indexOf("/",0);
				URL url = RuntimeContext.getConfigRoot();
				if (url!=null)
					buf.append(url.getPath());
				configRoot = configRoot.substring(var+1);
			}

			buf.append(BASE_PATH);
			buf.append(configRoot);
			if (!buf.toString().endsWith("/")) {
				buf.append('/');
			}
			s_clientConfigRoot = buf.toString();
		} else {
			s_clientConfigRoot = DEFAULT_BASE_PATH;
		}
	}


	public static ClientConfigManager getInstance() throws ServiceCreationException {
    	if (s_instance == null)
    		s_instance = new ClientConfigManager();
    	return (ClientConfigManager)s_instance;
    }

	public ClientConfigHolder getConfig(String serviceAdminName, String clientName) throws ServiceCreationException, ServiceNotFoundException {
		return getConfig(serviceAdminName, clientName, false);
	}

	public synchronized ClientConfigHolder getConfig(String serviceAdminName, String clientName, boolean rawMode) throws ServiceCreationException, ServiceNotFoundException {
		return getConfig(serviceAdminName, clientName, null, rawMode);
	}

	public synchronized ClientConfigHolder getConfig(String serviceAdminName, String clientName, String envName,boolean rawMode) throws ServiceCreationException, ServiceNotFoundException {
		init();
		Map<String, ClientConfigHolder> clientConfigMap = loadConfig(serviceAdminName, clientName,envName, false, rawMode);

		ClientConfigHolder outData = clientConfigMap.get(serviceAdminName);
		if (outData == null) {
			outData = clientConfigMap.get(DEFAULT_SERVICE.getLocalPart());
		}
		if (outData == null) {
			throw new ServiceNotFoundException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_NO_SUCH_SERVICE,
					ErrorConstants.ERRORDOMAIN, new Object[] {serviceAdminName}));
		}
		return outData;
	}

	// TODO: Get the typemapping and construct CCH
	public synchronized ClientConfigHolder getConfig(
			ClientConfigHolder baseConfig, String serviceAdminName, String clientName,
			String envName, QName svcQName,
			Definition wsdlDefinition, boolean rawMode)
			throws ServiceCreationException, ServiceNotFoundException {
		init();
		Map<String, ClientConfigHolder> clientConfigMap = loadConfig(
				baseConfig, serviceAdminName, clientName, envName, svcQName,
				wsdlDefinition, rawMode);

		ClientConfigHolder outData = clientConfigMap.get(serviceAdminName);

		if (outData == null) {
			outData = clientConfigMap.get(DEFAULT_SERVICE.getLocalPart());
		}
		if (outData == null) {
			throw new ServiceNotFoundException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_NO_SUCH_SERVICE,
					ErrorConstants.ERRORDOMAIN, new Object[] {serviceAdminName}));
		}
		return outData;
	}

	public synchronized Map<String, ClientConfigHolder> loadConfig(
			ClientConfigHolder baseConfig, String serviceName, String clientName,
			String envName, QName svcQName,
			Definition wsdlDefinition, boolean rawMode)
			throws ServiceCreationException {

		String key = (envName == null) ? clientName : clientName + "."
				+ serviceName + "." + envName;
		Map<String, ClientConfigHolder> clientConfigMap = m_clientData.get(key);
		if (clientConfigMap != null) {
			return clientConfigMap;

		}

		TypeMappingConfigHolder typeMappingsCfg = ClientConfigManager
				.getInstance().getTypeMappingConfigWithModifiedNS(
						baseConfig.getAdminName(), baseConfig.getServiceQName(), svcQName, wsdlDefinition, rawMode);

		clientConfigMap = new HashMap<String, ClientConfigHolder>();
		ClientConfigHolder cch = ClientConfigMapper.getConfigFromBaseConfig(
				baseConfig, serviceName, clientName, envName, svcQName,
				typeMappingsCfg);
		applyConfigBeanOverrides(cch);

		clientConfigMap.put(serviceName, cch);

		m_clientData.put(key, clientConfigMap);

		return clientConfigMap;
	}

	private synchronized TypeMappingConfigHolder getTypeMappingConfigWithModifiedNS(
			String adminName, QName oldSvcQName, QName svcQName, Definition wsdlDefinition, boolean rawMode)
	throws ServiceCreationException {
		return loadTypeMappingDataWithModifiedNS(m_commonPath + adminName
				+ "/TypeMappings.xml", oldSvcQName, svcQName, wsdlDefinition, rawMode);
	}


	/**
	 *   SOA2.4 onwards, Multiple ClientConfig (MCC) feature mandates env. Please see
	 *   @link getConfigForUpdate(String serviceAdminName, String clientName, String envName)
	 */
	@Deprecated
	public synchronized ClientConfigHolder getConfigForUpdate(String serviceAdminName, String clientName) throws ServiceCreationException, ServiceNotFoundException {
		ClientConfigHolder outData = getConfig(serviceAdminName, clientName, BaseServiceDescFactory.getClientInstance().isInRawMode(serviceAdminName));
		return outData.copy();
	}

	public synchronized ClientConfigHolder getConfigForUpdate(String serviceAdminName, String clientName, String envName) throws ServiceCreationException, ServiceNotFoundException {
		ClientConfigHolder outData = getConfig(serviceAdminName, clientName, envName, BaseServiceDescFactory.getClientInstance().isInRawMode(serviceAdminName));
		return outData.copy();
	}

	// ClientServiceId id = new ClientServiceId(serviceAdminName, clientName);
	// This function is called by the bean manager, which must call ClientServiceDescFactory.reloadServiceDesc(id);
	public synchronized void updateConfig(String serviceAdminName,
			String clientName, ClientConfigHolder holder) {

		// put the holder into the hashmap
		holder.lockReadOnly();
		Map<String, ClientConfigHolder> clientConfig = null;
		if (holder.getEnvName() == null)
			//key used in such cases is clientName
			clientConfig = m_clientData.get(clientName);
		else {
			//Key would be based on <clientName>.<AdminName>.<envName>
			String key = holder.getClientName() + "." + holder.getAdminName()
					+ "." + holder.getEnvName();
			clientConfig = m_clientData.get(key);
		}
		clientConfig.put(serviceAdminName, holder);

	}

	public synchronized GlobalConfigHolder getGlobalConfig() throws ServiceCreationException {
		init();
		return m_globalConfig;
	}

	public synchronized GlobalConfigHolder getGlobalConfigForUpdate() throws ServiceCreationException {
		init();
		if (m_globalConfig == null) {
			return null;
		}
		return m_globalConfig.copy();
	}

	private synchronized void init() throws ServiceCreationException {
		if (m_configLoaded)
			return;
		String globalFileName = getBasePath()+ GLOBAL_FILENAME;
   	 	loadGlobalData(globalFileName);
   	 	m_globalConfig = new GlobalConfigHolder();
   	 	GlobalConfigMapper.map(globalFileName, m_globalData, m_globalConfig);

   	 	// Get group element but delay looking into it until we see group references present (findGroup).
   	 	if (m_globalData != null) {
   	 		m_groupData = DomParseUtils.getSingleElement(globalFileName, m_globalData, "client-config-groups");
   	 	}

   	 	// No pre-initialization even of the default client.  Too confusing to pre-init a service that is not
   	 	// being created by the service factory.
        //loadConfig(SOAConstants.DEFAULT_CLIENT_NAME, true);
        m_configLoaded = true;
    }

	private synchronized Map<String, ClientConfigHolder> loadConfig(String serviceName, String clientName, boolean isOptional, boolean rawMode) throws ServiceCreationException {
		return loadConfig(serviceName, clientName,null, isOptional, rawMode);
	}



	private synchronized Map<String, ClientConfigHolder> loadConfig(
			String serviceName, String clientName, String envName,
			boolean isOptional, boolean rawMode)
			throws ServiceCreationException {
		String clientConfigFileName = null;
		if (clientName == null) {
			clientName = SOAConstants.DEFAULT_CLIENT_NAME;
		}
		//key needs to be unique.
		//key= <ClientName>.<AdminName>.<envName> we need to support switching between cc.xml for a service as well as
		//calling diff services with same cc.xml
		String key = (envName == null) ? clientName : clientName + "." + serviceName + "." + envName;
		Map<String, ClientConfigHolder> clientConfigMap = m_clientData.get(key);
		if (clientConfigMap != null) {
			return clientConfigMap;
		}
		clientConfigMap = new HashMap<String, ClientConfigHolder>();
		clientConfigFileName = this.getConfigFilePath(getBasePath(), clientName, envName, serviceName);
		String clientConfigSchemaName = s_schemaPath + CLIENT_SCHEMA;
		String globalFileName = getBasePath() + GLOBAL_FILENAME;
		Document configDoc = null;
		try{
			configDoc = ParseUtils.parseConfig(clientConfigFileName, clientConfigSchemaName, isOptional, "client-config-list", ParseUtils.getSchemaCheckLevel());
		}catch(ServiceCreationException servexc)  {
	        // try to load from default now
			// need to log this
			if ( LOGGER.isLogEnabled( LogLevel.WARN ) ) {
				LOGGER.log( LogLevel.WARN, "Unable to load ClientConfig.xml from config root (will use default): " + m_configPath );
			}
			clientConfigFileName = this.getConfigFilePath(DEFAULT_BASE_PATH, clientName, envName, serviceName);
        	configDoc = ParseUtils.parseConfig(clientConfigFileName, clientConfigSchemaName, isOptional, "client-config-list", ParseUtils.getSchemaCheckLevel());
		}
        if (configDoc == null) {
        	// if still null optional to load it; don't need the return value
        	return null;
        }

        Element clientConfigs = configDoc.getDocumentElement();
        NodeList nodes = DomParseUtils.getImmediateChildrenByTagName(clientConfigs, "client-config");
        MetadataPropertyConfigHolder metaDataHolder = null;
        double metaDataVersion = -1d;
        if(serviceName != null) {
            metaDataHolder = getMetadataPropertyConfigHolder(serviceName);
            metaDataVersion = metaDataHolder.getSmpVersion();
        }
        for (int i = 0; i < nodes.getLength(); i++) {
            Element clientConfig = (Element)nodes.item(i);
            String adminName;
            QName serviceQName = DEFAULT_SERVICE;
            // smp_version >= 1.1 implies metaDataHolder has admin name.
            if (metaDataVersion >= 1.1d) {
            	adminName = metaDataHolder.getAdminName();
		if (rawMode) {
		    if(i > 0) {
		        throwRawModeException(clientConfigFileName);
		    }
		    serviceQName = new QName(SOAConstants.DEFAULT_SERVICE_NAMESPACE, metaDataHolder.getServiceName());
            	}
            }
            else if (rawMode) {
            	// There should only be a single client-config when operating in raw mode
            	if (i > 0) {
		    throwRawModeException(clientConfigFileName);
            	}
            	adminName = serviceName;
            	serviceQName = new QName(SOAConstants.DEFAULT_SERVICE_NAMESPACE, serviceName);
            }
            else {
            	String serviceNameInCC = clientConfig.getAttribute("service-name");
                if (serviceNameInCC != null && !serviceNameInCC.isEmpty()) {
                	serviceQName = DomParseUtils.getQName(clientConfigFileName, serviceNameInCC, SOAConstants.DEFAULT_SERVICE_NAMESPACE, "service-name");
                	adminName = serviceQName.getLocalPart();
                }
                else {
                	adminName = serviceName;
                }
                metaDataHolder = getMetadataPropertyConfigHolder(adminName);
            }
            String groupName = clientConfig.getAttribute("group");
    		Element clientGroup = null;
            if (groupName != null && groupName.length() != 0) {
            	 clientGroup = findGroup(clientConfigFileName, groupName);
            }
            ClientConfigHolder cch = ClientConfigMapper.applyConfigs(adminName, clientName, envName,
            		clientConfigFileName, globalFileName, clientGroup, clientConfig);

             //Process the transports defined in Config and add the default transports
            processDefaultTransportsInConfig(cch);
            applyConfigBeanOverrides(cch);
            TypeMappingConfigHolder typeMappings = loadTypeMappingData(m_commonPath + adminName + "/TypeMappings.xml", rawMode);
            cch.setTypeMappings(typeMappings);
            cch.setMetaData(metaDataHolder);
            if (rawMode) {
            	// Default config is generic, so we update the holder to use the service name passed by the caller
            	cch.setServiceQName(serviceQName);
            }
            cch.lockReadOnly();
            if (clientConfigMap.get(adminName) != null) {
            	throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR,
            			ErrorConstants.ERRORDOMAIN, new Object[] {clientConfigFileName, "Duplicate definition of service: '" + adminName + "'"}));
	        }
	        clientConfigMap.put(adminName, cch);
        }
        m_clientData.put(key, clientConfigMap);
        return clientConfigMap;
	}

	private void throwRawModeException(String fileName) throws ServiceCreationException {
		throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR,
            				ErrorConstants.ERRORDOMAIN, new Object[] {fileName, "Only a single default client-config section is allowed when operating in Raw Mode"}));
	}

	private String getConfigFilePath(String configLoc,String clientName,String envName,String serviceName){
		StringBuilder ccfilename = new StringBuilder();
		ccfilename.append(configLoc).append(clientName).append(FILE_SEPERATOR);
		if (envName != null) {
			ccfilename.append(envName).append(FILE_SEPERATOR).append(serviceName).append(FILE_SEPERATOR);
		}
		ccfilename.append(CLIENT_FILENAME);
		String clientConfigFileName = ccfilename.toString();
		return clientConfigFileName;
	}

	private void applyConfigBeanOverrides(ClientConfigHolder cch) {
		ClientServiceConfigBeanManager.initConfigBean(cch);
	}

	private Element findGroup(String filename, String groupName) throws ServiceCreationException {
		if (m_groupData == null) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR,
					ErrorConstants.ERRORDOMAIN, new Object[] {filename, "Group '" + groupName + " 'is referenced but no groups are defined"}));
		}
        NodeList nodes = DomParseUtils.getImmediateChildrenByTagName(m_groupData, "client-group");
        for (int i=0; i < nodes.getLength(); i++) {
        	Element clientGroup = (Element)nodes.item(i);
        	String nameAttr = clientGroup.getAttribute("name");
			if (nameAttr != null && nameAttr.equals(groupName)) {
				return clientGroup;
			}
		}
        throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR,
        		ErrorConstants.ERRORDOMAIN, new Object[] {filename, "Cannot find group: " + groupName + ", global file=" + getGlobalFilePath()}));
	}

	/*
	 *  The runtime by default creates HTTP11, HTTP10 transports with default options unless provided
	 *  in the config. Similarly it attempts to create LOCAL transport if possible. Here we take care of
	 *  merging the override options (if any) with provided transport options (if any).
	 */
	private void processDefaultTransportsInConfig(ClientConfigHolder cch) throws ServiceCreationException {
		//
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		MessageProcessorConfigHolder processorConfig = cch.getMessageProcessorConfig();
		String defTransportName = cch.getPreferredTransport();
		TransportOptions transportOverrideOptions = cch.getTransportOverrideOptions();

		if (defTransportName == null) {
			defTransportName = SOAConstants.TRANSPORT_HTTP_11;
		}
		Map<String,String> transportClassesCfg = processorConfig.getTransportClasses();
		Map<String,TransportOptions> transportOptionsCfg = processorConfig.getTransportOptions();

		// make validate config classnames
		for (Map.Entry<String,String> e: transportClassesCfg.entrySet()) {
			String name = e.getKey();
			validateTransportName(name);
			String className = e.getValue();
			if (className == null) {
				throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_TRANSPORT_CONFIG,
						ErrorConstants.ERRORDOMAIN, new Object[] { name }));
			}
		}

		// add client transport names before creating transports to allow proper overrides
		if (!transportClassesCfg.containsKey(SOAConstants.TRANSPORT_HTTP_10)) {
			transportClassesCfg.put(SOAConstants.TRANSPORT_HTTP_10,
				HTTPSyncAsyncClientTransport.class.getName());
		}

		if (!transportClassesCfg.containsKey(SOAConstants.TRANSPORT_HTTP_11)) {
			transportClassesCfg.put(SOAConstants.TRANSPORT_HTTP_11,
				HTTPSyncAsyncClientTransport.class.getName());
		}

		if (!transportClassesCfg.containsKey(SOAConstants.TRANSPORT_LOCAL)) {
			String className = "org.ebayopensource.turmeric.runtime.spf.impl.transport.local.LocalTransport";
			try {
				Class.forName(className, true, cl);

				// only add if the class can be successfully loaded
				transportClassesCfg.put(SOAConstants.TRANSPORT_LOCAL, className);
			} catch (ClassNotFoundException e) {
				// ignore, do not add this class by default
			} catch (NoClassDefFoundError e) {
				// ignore, do not add this class by default
			}
		}

		for (Map.Entry<String,String> e: transportClassesCfg.entrySet()) {
			String name = e.getKey();
			boolean isUpdated = false;
			// get a non-null copy of the options
			TransportOptions options =	transportOptionsCfg.get(name);
			if (options == null) {
				options = new TransportOptions();
				options.setHttpTransportClassName(e.getValue()); // set the classname
				isUpdated = true;
			}
			if (defTransportName != null && transportOverrideOptions != null && defTransportName.equals(name)) {
				options = options.getMergedCopy(transportOverrideOptions);
				isUpdated = true;
			}
			if (name.equals(SOAConstants.TRANSPORT_HTTP_11)) {
				options.getProperties().put(SOAConstants.HTTP_VERSION, SOAConstants.TRANSPORT_HTTP_11);
			} else if (name.equals(SOAConstants.TRANSPORT_HTTP_10)) {
				options.getProperties().put(SOAConstants.HTTP_VERSION, SOAConstants.TRANSPORT_HTTP_10);
			}
			if (isUpdated)
				transportOptionsCfg.put(name, options);
		}
	}


	private void validateTransportName(String name) throws ServiceCreationException {
		if (name == null) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_INVALID_TRANSPORT_NAME,
					ErrorConstants.ERRORDOMAIN, new Object[] { "***null***" }));
		}
	}

	private String getBasePath(){
		return isInTestMode?m_configPath:DEFAULT_BASE_PATH;
	}

	private String getGlobalFilePath() {
		URL glabalFileURL = getClassLoader().getResource(getBasePath() + GLOBAL_FILENAME);
		return (glabalFileURL != null ? glabalFileURL.getPath() : " ");
	}

	private void loadGlobalData(String globalFileName) throws ServiceCreationException {
		if (m_globalData != null)
			return;
		String globalSchemaName = s_schemaPath + GLOBAL_SCHEMA;
		Document globalDoc = ParseUtils.parseConfig(globalFileName, globalSchemaName, true, "global-client-config", ParseUtils.getSchemaCheckLevel());
		if (globalDoc != null) {
			m_globalData = globalDoc.getDocumentElement();
		}
	}

	private synchronized void setConfigPath(String path) {
		m_configPath = path;
	}

	private synchronized void setCommonPath(String path) {
		m_commonPath = path;
	}

	public synchronized void setConfigTestCase(String relativePath, String commonPath, boolean force) throws ServiceException {
		String newPath = BASE_PATH + relativePath + "/";
		if (!force && m_configPath != null && m_configPath.equals(newPath)) {
			LOGGER.log(LogLevel.WARN, "ClientConfigManager TestCase path of \"" + newPath + "\" already set.");
			return;
		}

		m_configLoaded = false;
		m_globalConfig = null;
		m_clientData = new HashMap<String, Map<String,ClientConfigHolder>>();
		m_globalData = null;
		m_groupData = null;
		isInTestMode = true;
		setConfigPath(newPath);
		String newPath2 = BASE_PATH2 + commonPath + "/";
		setCommonPath(newPath2);
		init();

		ClientServiceDescFactory.getInstance().resetFactoryForUnitTest();
	}

	public void setConfigTestCase(String relativePath) throws ServiceException {
		setConfigTestCase(relativePath, relativePath);
	}

	public void setConfigTestCase(String relativePath, boolean force) throws ServiceException {
		setConfigTestCase(relativePath, relativePath, force);
	}

	public void setConfigTestCase(String relativePath, String commonPath) throws ServiceException {
		setConfigTestCase(relativePath, commonPath, false);
	}
	// This returns all known client names, i.e. names of configurations that have either been pre-initialized
	// (currently only the configuration "default"), or accessed by a getConfig() call.  We expect this
	// to be called at system init (e.g. by BaseServiceDescFactory), and only the value "default" will be
	// available at that time.
	//
	// Continued calls to this function after init will return the set of all accessed client names
	// (configurations).
	//
	// Since ClientConfigManager is a singleton per appserver, the registration process is global to the
	// appserver.  This means that if multiple clients are deployed as multiple wars, each with their own
	// config, such configs will have to have unique client names in order to be registered unambiguously
	// into the ClientConfigManager.
	//
	public Collection<String> getAllClientNames() throws ServiceCreationException {
		init();
		//since m_clientdata also contains adminName and envName in the key hence need to seperate the client keySet
		//"." is not a valid character for envName,clientname or AdminName given by user.
		Set<String> clientkeySet = new HashSet<String>();
		Set<String> keySet = m_clientData.keySet();
		for (String currentkey : keySet) {
			String clientName = currentkey;
			if (currentkey.contains("."))
				clientName = currentkey.substring(0,
						currentkey.indexOf('.'));

			clientkeySet.add(clientName);
		}
		return Collections.unmodifiableCollection(clientkeySet);
	}



	public Collection<String> getAllServiceAdminNames(String clientName) throws ServiceCreationException {
		return getAllServiceAdminNames(clientName, false);
	}

	public Collection<String> getAllServiceAdminNames(String clientName, boolean rawMode) throws ServiceCreationException {
		Map<String, ClientConfigHolder> clientConfigMap = loadConfig(null, clientName, false, rawMode);
		return Collections.unmodifiableCollection(clientConfigMap.keySet());
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
		return loadMetadataPropertyData(fileName.toString(), getGlobalConfig().getServiceLayerNames());
	}

	/**
	 * Creates a new ClientServiceId instance.
	 *
	 * @param adminName
	 * @param clientName
	 * @param env
	 * @return
	 * @throws ServiceCreationException
	 * @throws ServiceNotFoundException
	 */
	public static ServiceId createClientServiceId(String adminName, String clientName, String env, boolean rawMode) {
		ClientServiceId clientServiceId = null;
		boolean configFound = true;
		try {
			ClientConfigHolder config = ClientConfigManager.getInstance().getConfig(adminName, clientName, env, rawMode);
			MetadataPropertyConfigHolder metadata = config.getMetaData();
			if (metadata != null) {
				String serviceName = metadata.getServiceName();
				String version = metadata.getVersion();
				String namespace = metadata.getServiceNamespace();
				clientServiceId =
					new ClientServiceId(adminName, serviceName, version, namespace, clientName, env);
			}
		} catch (Exception e) {
			configFound = false;
		}

		// Fallback code for backward compatibility. If config is not
		// found, the client is prehistoric or it is test case related
		if(!configFound) {
			clientServiceId = new ClientServiceId(adminName, clientName);
		}
		return clientServiceId;
	}
}
