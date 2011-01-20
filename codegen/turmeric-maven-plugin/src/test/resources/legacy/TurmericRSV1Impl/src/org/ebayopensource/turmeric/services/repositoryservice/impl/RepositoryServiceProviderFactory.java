/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.services.repositoryservice.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;
import org.ebayopensource.turmeric.services.repository.config.RepositoryServiceProviderConfig;
import org.ebayopensource.turmeric.services.repository.config.RepositoryServiceProviders;



public class RepositoryServiceProviderFactory {
	
	private static volatile Map<String, RepositoryServiceProvider>  s_serviceProviderMap = new HashMap<String, RepositoryServiceProvider>();
	private static String serviceProviderKey;
	private static Logger s_logger = Logger.getLogger(RepositoryServiceProviderFactory.class);
	
	private RepositoryServiceProviderFactory()
	{
		
	}
	
	public static RepositoryServiceProvider getInstance(String key) throws ServiceProviderException
	{
		if(s_serviceProviderMap.size()==0)
		{
			synchronized (RepositoryServiceProviderFactory.class) {
				
				if(s_serviceProviderMap.size()==0)
				{
					populateProviderMap();
				}
				
			}
		}
		return s_serviceProviderMap.get(key);
	}
	
	public static RepositoryServiceProvider getInstance() throws ServiceProviderException
	{
		if(s_serviceProviderMap.size()==0)
		{
			synchronized (RepositoryServiceProviderFactory.class) {
				
				if(s_serviceProviderMap.size()==0)
				{
					populateProviderMap();
				}
				
			}
		}
		
		return s_serviceProviderMap.get(serviceProviderKey);
	}
	
	private static void populateProviderMap() throws ServiceProviderException
	{
//		InputStream configXmlStream = ClassLoader.getSystemResourceAsStream(RepositoryServiceConstants.s_providerConfigXml);
		InputStream configXmlStream = RepositoryServiceProviderFactory.class.getClassLoader().
														getResourceAsStream(RepositoryServiceConstants.s_providerConfigXml);
//		System.err.println("getting the config file..."+configXmlStream);
		
		if(configXmlStream == null)
		{
			String message = RepositoryServiceConstants.s_providerConfigXml + " is not provided. ";
			s_logger.error(message);
			throw new ServiceProviderException(message);
		}
//		System.err.println("configXmlStream="+configXmlStream);
		RepositoryServiceProviderConfig repositoryServiceConfig = JAXB.unmarshal(configXmlStream, 
																		RepositoryServiceProviderConfig.class);
//		System.err.println("repositoryServiceConfig="+repositoryServiceConfig);
		
		InputStream serviceProvider = RepositoryServiceProviderFactory.class.getClassLoader().
														getResourceAsStream(RepositoryServiceConstants.s_serviceProviderProperty);
		
		if(serviceProvider != null)
		{
			Properties properties = new Properties();
			try {
				properties.load(serviceProvider);
				serviceProviderKey = (String)properties.get(RepositoryServiceConstants.s_providerPropKey);
			} catch (IOException e) {
				String message = "Problem occured while loading "+RepositoryServiceConstants.s_serviceProviderProperty;
				s_logger.warn(message, e);
			}
		}
		
		if(serviceProviderKey == null)
		{
			s_logger.warn("Unable to fetch service provider key from service_provider.properties, hence loading default key");
			serviceProviderKey = repositoryServiceConfig.getDefault();
		}
		
//		System.err.println("defaultKey="+defaultKey);
		List<RepositoryServiceProviders> providerConfig = repositoryServiceConfig.getProviderConfigList().getProviderConfig();
		
		for (RepositoryServiceProviders repositoryServiceProvider : providerConfig) {
			
			String className = repositoryServiceProvider.getProviderImplClassName();
			RepositoryServiceProvider repServiceProvider = null;
			try {
				repServiceProvider = (RepositoryServiceProvider) Class.forName(className).newInstance();
			} catch (IllegalAccessException e) {
				s_logger.error(e.getMessage(),e);
				String message = " IllegalAccessException occured while initialising class "+className;
				throw new ServiceProviderException(message,e);
			} catch (InstantiationException e) {
				s_logger.error(e.getMessage(),e);
				String message = " InstantiationException occured while initialising class "+className;
				throw new ServiceProviderException(message,e);
			} catch (ClassNotFoundException e) {
				s_logger.error(e.getMessage(),e);
				String message = " ClassNotFoundException occured while loading class "+className;
				throw new ServiceProviderException(message,e);
			} catch (ClassCastException e) {
				s_logger.error(e.getMessage(),e);
				String message = " ClassCastException occured while initialising class "+className;
				throw new ServiceProviderException(message,e);
			}
			s_serviceProviderMap.put(repositoryServiceProvider.getName(), repServiceProvider);
			
		}
		
	}

}
