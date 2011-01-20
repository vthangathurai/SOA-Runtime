/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.transport.http;

import java.io.File;
import java.net.URL;
import java.util.Map;

import com.ebay.kernel.bean.configuration.BeanConfigCategoryInfo;
import com.ebay.kernel.context.AppBuildConfig;
import com.ebay.kernel.context.RuntimeContext;
import com.ebay.kernel.initialization.InitializationException;
import com.ebay.kernel.resource.ResolverFactory;
import com.ebay.kernel.service.invocation.FileSystemSslConfig;
import com.ebay.kernel.service.invocation.SslConfig;
import com.ebay.kernel.service.invocation.transport.socket.SslProviderType;

public class FileSystemSslConfigFactory implements AbstractSslConfigFactory<SslConfig> {
    
    private static final boolean DEFAULT_VERIFY_TRUST_FOR_HTTPS = true; 
    
    @Override
    public SslConfig createSslConfig(BeanConfigCategoryInfo beanInfo,
	    Map<String, String> options) throws SslConfigCreationException {	
	
	final String truststorePath = options.get(FileSystemSslConfig.TRUSTSTORE.getName());	
	boolean verifyTrust = getVerifyTrustForHttps(options);
	
	if (truststorePath == null || truststorePath.isEmpty()) {
	    String cerDir = new File("certificates").getAbsolutePath();
	    SslConfig sslConfig = new FileSystemSslConfig(beanInfo, cerDir);	    
	    sslConfig.setVerifyTrust(verifyTrust);
	    sslConfig.setSslProviderType(SslProviderType.DEFAULT_JVM_JSSE_SSLV3);
	    return sslConfig;
	}
	try
        {
            URL configRoot = RuntimeContext.getExternalConfigRoot();
            String cerDir = AppBuildConfig.getInstance().isProduction() ?
                configRoot.getPath() + truststorePath : ResolverFactory.getInstance().getResource(HTTPClientTransportConfig.class, truststorePath).getPath();
            FileSystemSslConfig sslConfig = new FileSystemSslConfig(beanInfo, cerDir);
            sslConfig.setVerifyTrust(verifyTrust);
            sslConfig.setSslProviderType(SslProviderType.DEFAULT_JVM_JSSE_SSLV3);          
            return sslConfig;
        }
        catch(Exception x)
        {
            throw new InitializationException("Trouble loading certificates from truststore or directory! Path="+truststorePath, x);
        }
    }

    private static Boolean getVerifyTrustForHttps(Map<String, String> properties) {
	String s = properties.get(HTTPClientTransportConfig.VERIFY_TRUST_FOR_HTTPS);
	if (s == null) {
	    return DEFAULT_VERIFY_TRUST_FOR_HTTPS;
	}
	return Boolean.valueOf(Boolean.parseBoolean(s));
    }
}
