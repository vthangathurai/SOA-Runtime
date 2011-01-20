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

import com.ebay.kernel.bean.configuration.BeanConfigCategoryInfo;
import com.ebay.kernel.service.invocation.SslConfig;

/**
 * 
 * AbstractFactory to create the SslConfig
 * 
 * @author pkaliyamurthy
 *
 */
public interface AbstractSslConfigFactory<T extends SslConfig> {
    
    /**
     * The factory method for creating the SSL Config.
     * @return
     */
    public T createSslConfig(BeanConfigCategoryInfo beanInfo, Map<String, String> options) throws SslConfigCreationException;    
  
}
