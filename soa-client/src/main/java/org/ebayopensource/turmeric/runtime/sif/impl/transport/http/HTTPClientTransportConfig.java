/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.transport.http;

import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ReflectionUtils;
import org.ebayopensource.turmeric.runtime.common.pipeline.TransportOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;

import com.ebay.kernel.bean.configuration.BaseConfigBean;
import com.ebay.kernel.bean.configuration.BeanConfigCategoryInfo;
import com.ebay.kernel.bean.configuration.BeanPropertyInfo;
import com.ebay.kernel.bean.configuration.ConfigCategoryCreateException;
import com.ebay.kernel.initialization.InitializationException;
import com.ebay.kernel.service.invocation.HttpConfig;
import com.ebay.kernel.service.invocation.SocketConfig;
import com.ebay.kernel.service.invocation.SslConfig;
import com.ebay.kernel.service.invocation.SvcChannelStatus;
import com.ebay.kernel.service.invocation.SvcInvocationConfig;
import com.ebay.kernel.service.invocation.client.http.nio.NioHttpConfig;
import com.ebay.kernel.service.invocation.client.http.nio.NioSvcInvocationConfig;

public class HTTPClientTransportConfig extends BaseConfigBean {

    // Static labels for file config properties;
    // these match the config bean names

    public static final String USE_HTTPS = "USE_HTTPS";
    public static final String VERIFY_TRUST_FOR_HTTPS = "VERIFY_TRUST_FOR_HTTPS";

    public static final String PROXY_HOST = "PROXY_HOST";
    public static final String PROXY_PASSWORD = "PROXY_PASSWORD";
    public static final String PROXY_PORT = "PROXY_PORT";
    public static final String PROXY_USER = "PROXY_USER";
    public static final String NON_PROXY_HOSTS = "NON_PROXY_HOSTS";
    public static final String PROXY_ENABLED = "PROXY_ENABLED";

    // Only used by Async Transport
    public static final String KEEP_ALIVE = "KEEP_ALIVE";
    public static final String CONSECUTIVE_FAILURE_THRESHOLD = "CONSECUTIVE_FAILURE_THRESHOLD";

    // defaults - first class SOA config options.
    public static final boolean DEFAULT_KEEP_ALIVE = false;
    public static final int DEFAULT_CONSECUTIVE_FAILURE_THRESHOLD = Integer.MAX_VALUE;
    // ConnectionConfig NUM_RETRY for SocketConnector.getSocket().
    public static final int DEFAULT_MAX_CONNECT_RETRY = 1;
    
    protected static final String DEFAULT_SSL_CONFIG_FACTORY = "org.ebayopensource.turmeric.runtime.sif.impl.transport.http.FileSystemSslConfigFactory";

    // Applies to socket connect *only*.

    // CONNECT_TIMEOUT (msec)
    public static final int DEFAULT_HTTP_CONNECTION_TIMEOUT = 36000; // ConnectionConfig:

    // SO_TIMEOUT (msec)
    public static final int DEFAULT_SOCKET_RECV_TIMEOUT = 50000; // SocketConfig:

    // defaults - options set via name-value 'other-options' transport config
    // properties

    public static final int DEFAULT_INVOCATION_TIMEOUT = 0; // LimitedDurationInvocationConfig:
    // MAX_INVOCATION_DURATION : Used by Sync only

    private static final boolean DEFAULT_USE_HTTPS = false; // this bean
    private static final boolean DEFAULT_VERIFY_TRUST_FOR_HTTPS = true; // this
    // bean
	private static final boolean DEFAULT_CLIENT_STREAMING = false;

    private static final String CONFIG_ID_PREFIX = SOAConstants.CONFIG_BEAN_PREFIX_CLIENT
	    + "http.";

    public static final String SSL_CONFIG_FACTORY = "SSL_CONFIG_FACTORY"; 
    
    public static final String SSL_TRUSTSTORE_CHANNELNAME = "SSL_TRUSTSTORE_CHANNELNAME";
	public static final String SSL_KEYPAIR_CHANNELNAME = "SSL_KEYPAIR_CHANNELNAME";

    
    // Member fields for this bean - can't be final
    private boolean useHttps;
    private boolean verifyTrustForHttps;
    private long maxInvocationDuration;

    // Contained beans
    private SvcInvocationConfig svcIvcConfig;
    private NioSvcInvocationConfig nioSvcIvcConfig;
    private SslConfig sslConfig;
     
    // member variables - final
    private final String configName;
    private final String configId;
    private final TransportOptions transportOptions;
    
    protected static final BeanPropertyInfo PROP_USE_HTTPS = createBeanPropertyInfo(
	    "useHttps", USE_HTTPS, true);
    protected static final BeanPropertyInfo PROP_VERIFY_TRUST_FOR_HTTPS = createBeanPropertyInfo(
	    "verifyTrustForHttps", VERIFY_TRUST_FOR_HTTPS, true);
    protected static final BeanPropertyInfo MAX_INVOCATION_DURATION = createBeanPropertyInfo(
	    "maxInvocationDuration", "MAX_INVOCATION_DURATION", true);
    
    /**
     * Constructor that requires full set of parameters to be specified
     */
    public HTTPClientTransportConfig(String configName, TransportOptions options) {
	
	this.configName = configName;
	this.configId = CONFIG_ID_PREFIX + configName;
	this.transportOptions = options;
	
	BeanConfigCategoryInfo categoryInfo;
	try {	    
	    categoryInfo = BeanConfigCategoryInfo.createBeanConfigCategoryInfo(
		    configId, null, SOAConstants.CONFIG_BEAN_GROUP, false,
		    true, null, "SOA HttpClient Configuration", true);	    
	     // Parameters in order 
	     // categoryId, alias, group, isPersistent, opsManagable, persistFileUri, description, returnExistingOne

	} catch (ConfigCategoryCreateException e) {
	    throw new InitializationException(e);
	}	
	
	this.useHttps = isSecure(); 
	this.verifyTrustForHttps = verifyTrust();
	createServiceConfigs(categoryInfo);	
	init(categoryInfo, true);
    }

    

    private boolean isSecure() {
	boolean isSecure = DEFAULT_USE_HTTPS;
	Boolean b = getBoolean(transportOptions.getProperties(), USE_HTTPS);
	if (b != null) {
	    isSecure = b.booleanValue();
	}
	return isSecure;
    }
    
    private boolean verifyTrust() {
	boolean verifyTrust = DEFAULT_VERIFY_TRUST_FOR_HTTPS;
	Boolean b = getBoolean(transportOptions.getProperties(), VERIFY_TRUST_FOR_HTTPS);
	if (b != null) {
	    verifyTrust = b.booleanValue();
	}
	return verifyTrust;
    }
    
    private void createServiceConfigs(BeanConfigCategoryInfo categoryInfo ) {	

	int httpConnectionTimeout = getHttpConnTimeout();
	int socketRecvTimeout = getSocketRecvTimeout();
	Map<String, String> propertyMap = transportOptions.getProperties();	
	
	// Main config bean for HTTPClient
	String svcHost = null;
	String svcPort = null;
	boolean ignoreConfigedSvcHost = false;
	boolean asyncInvocation = false;	
	
	svcIvcConfig = new SvcInvocationConfig(categoryInfo,
		configName, SvcChannelStatus.MARK_UP, svcHost, svcPort,
		ignoreConfigedSvcHost, asyncInvocation);	
	
	nioSvcIvcConfig = new NioSvcInvocationConfig(categoryInfo, configName,
		SvcChannelStatus.MARK_UP);
	boolean clientStreaming = isStreaming();
	if (clientStreaming) {
		nioSvcIvcConfig.setUseResponseStreaming(clientStreaming);
	}

	int maxAvailableThreads = 1;
	int maxActiveThreads = 1;
	boolean useConnPool = false;
	svcIvcConfig.createConnectionConfig(maxAvailableThreads, maxActiveThreads,
		httpConnectionTimeout, getMaxConnectRetry(), false,
		false, useConnPool);
	nioSvcIvcConfig.createConnectionConfig(httpConnectionTimeout, getMaxConnectRetry());	
        
	if (useHttps) {

	    Integer soLinger = null;
	    Integer soRcvBuf = null;
	    Integer soSndBuf = null;
	    Boolean tcpNoDelay = Boolean.TRUE;
	    Boolean soKeepAlive = null;
	    
	    SocketConfig socketConfig = svcIvcConfig.createSocketConfig(
		    Integer.valueOf(socketRecvTimeout), soLinger, soRcvBuf,
		    soSndBuf, tcpNoDelay, soKeepAlive);
	    SocketConfig nioSocketConfig = nioSvcIvcConfig.createRequestConfig(
		    Integer.valueOf(socketRecvTimeout), soLinger, soRcvBuf,
		    soSndBuf, tcpNoDelay, soKeepAlive);
	    try {
		sslConfig = createSSLConfig(socketConfig);
	    } catch (SslConfigCreationException e) {
		throwInitializationException(e);
	    }
	    
	    socketConfig.setSslConfig(sslConfig);
	    nioSocketConfig.setSslConfig(sslConfig);
	}

	// HTTP Config
	
	boolean followHttpRedirect = true;
	boolean enableKeepAlive = false;
	boolean enableProxy = false;
	
	HttpConfig httpConfig = svcIvcConfig.createHttpConfig(followHttpRedirect,
		enableKeepAlive, enableProxy);	
	
	NioHttpConfig nioHttpConfig = nioSvcIvcConfig.createHttpConfig(followHttpRedirect,
		enableKeepAlive, enableProxy);
	
	nioHttpConfig.setKeepAliveEnabled(doIKeepAlive());	
	
	String proxyHost = propertyMap.get(PROXY_HOST);	
	
	if (proxyHost != null) {
	    String nonProxyHosts = propertyMap.get(NON_PROXY_HOSTS);
	    String proxyPassword = propertyMap.get(PROXY_PASSWORD);
	    String proxyPort = propertyMap.get(PROXY_PORT);
	    String proxyUser = propertyMap.get(PROXY_USER);
	    Boolean proxyEnabled = getBoolean(propertyMap, PROXY_ENABLED);
	    
	    httpConfig.createProxyConfig(proxyHost, nonProxyHosts, proxyPort,
		    proxyUser, proxyPassword);

	    nioHttpConfig.createProxyConfig(proxyHost, nonProxyHosts,
		    proxyPort, proxyUser, proxyPassword);
	    if (proxyEnabled != null && proxyEnabled.booleanValue()) {
		httpConfig.setProxyEnabled(true);
		nioHttpConfig.setProxyEnabled(true);
	    }
	}

	// limit the invocation duration if invocation timeout is specified
	// Default is false.
	boolean enableInvocationTimeout = false;
	int invocationTimeout = getInvocationTimeout();
	if (invocationTimeout > 0) {
	    enableInvocationTimeout = true;
	}
	
	svcIvcConfig.createLimitedDurationInvocationConfig(enableInvocationTimeout, invocationTimeout);
	nioSvcIvcConfig.setConsecutiveFailureThreshold(getConsecutiveFailureThreashold());
    }


    private int getConsecutiveFailureThreashold() {
	Integer consecutiveFailureThreshold = getInteger(transportOptions.getProperties(),
		CONSECUTIVE_FAILURE_THRESHOLD);
	consecutiveFailureThreshold = consecutiveFailureThreshold == null ? Integer
		.valueOf(DEFAULT_CONSECUTIVE_FAILURE_THRESHOLD)
		: consecutiveFailureThreshold;
	return consecutiveFailureThreshold.intValue();
    }
    
    private boolean doIKeepAlive() {
	Boolean keepAlive = getBoolean(transportOptions.getProperties(), KEEP_ALIVE);
	keepAlive = keepAlive == null ? Boolean.valueOf(DEFAULT_KEEP_ALIVE)
		: keepAlive;
	return keepAlive.booleanValue();
    }



    private int getInvocationTimeout() {	
	int invocationTimeout = DEFAULT_INVOCATION_TIMEOUT;
	Integer i = transportOptions.getInvocationTimeout();
	if (i != null) {
	    invocationTimeout = i.intValue();
	}
	return invocationTimeout;
    }


    private boolean isStreaming() {
    	Boolean clientStreaming = transportOptions.isClientStreaming();
    	if (clientStreaming == null) 
    		return DEFAULT_CLIENT_STREAMING;
    	return clientStreaming;
    }

    private int getSocketRecvTimeout() {	
	int socketRecvTimeout = DEFAULT_SOCKET_RECV_TIMEOUT;
	Integer i = transportOptions.getReceiveTimeout();
	if (i != null) {
	    socketRecvTimeout = i.intValue();
	}
	return socketRecvTimeout;
    }



    private int getHttpConnTimeout() {	
	int httpConnectionTimeout = DEFAULT_HTTP_CONNECTION_TIMEOUT;
	Integer i = transportOptions.getConnectTimeout();
	if (i != null) {
	    httpConnectionTimeout = i.intValue();
	}
	return httpConnectionTimeout;
    }



    private int getMaxConnectRetry() {
	int maxConnectRetry = DEFAULT_MAX_CONNECT_RETRY;
	Integer i = transportOptions.getNumConnectRetries();
	if (i != null) {
	    maxConnectRetry = i.intValue();
	}
	return maxConnectRetry;
    }

    private void throwInitializationException(SslConfigCreationException cause) {
	throw new InitializationException("Trouble creating SSL Config", cause);
    }

    public long getMaxInvocationDuration() {
	return maxInvocationDuration;
    }

    public SvcInvocationConfig getSvcInvocationConfig() {
	return svcIvcConfig;
    }

    public NioSvcInvocationConfig getNioSvcInvocationConfig() {
	return nioSvcIvcConfig;
    }
    
    public boolean isVerifyTrustForHttps() {
        return verifyTrustForHttps;
    }

    private Boolean getBoolean(Map<String, String> propertyMap, String name) {
	String s = propertyMap.get(name);
	if (s == null) {
	    return null;
	}
	return Boolean.valueOf(Boolean.parseBoolean(s));
    }

    private Integer getInteger(Map<String, String> propertyMap, String name) {
	String s = propertyMap.get(name);
	if (s == null) {
	    return null;
	}
	return Integer.valueOf(Integer.parseInt(s));
    }
    
    @SuppressWarnings("unchecked")
    private SslConfig createSSLConfig(SocketConfig sc) throws SslConfigCreationException {
	
	Map<String, String> properties = transportOptions.getProperties();
	AbstractSslConfigFactory<? extends SslConfig> sslConfigFactory = null;	
	String sslFactoryName = properties.get(SSL_CONFIG_FACTORY);
	if (sslFactoryName == null) {
	    sslFactoryName = DEFAULT_SSL_CONFIG_FACTORY;
	}
	ClassLoader cl = Thread.currentThread().getContextClassLoader();
	try {
	    sslConfigFactory = ReflectionUtils.createInstance(sslFactoryName,
		    AbstractSslConfigFactory.class, cl);
	} catch (ServiceException e) {
	    throw new SslConfigCreationException("Instantiation of " + sslFactoryName + " failed", e);
	}
	return sslConfigFactory.createSslConfig(sc.getBeanConfigCategoryInfo(),
		properties);
    }
    
    
    private static final long serialVersionUID = 1L;

}
