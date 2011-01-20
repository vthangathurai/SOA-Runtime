/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.service;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.MetadataPropertyConfigHolder;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigHolder;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;


/**
 * The Server side implementation of ServiceId. 
 * 
 * @author ichernyshev
 * @author pkaliyamurthy
 */
public final class ServerServiceId extends ServiceId {	

	/**
	 * Static factory method to create ServerServiceId Object.
	 * 
	 * @param adminName A service admin name.
	 * @return ServerServiceId
	 * @throws ServiceException Exception when ServiceId creation fails.
	 */
	public static ServerServiceId newInstance(String adminName) throws ServiceException {		
		String version = null;
		String serviceName = null;
		String namespace = null;
		ServiceConfigHolder config = ServiceConfigManager.getInstance().getConfig(adminName);
		MetadataPropertyConfigHolder metadata = config.getMetaData();		
		if (metadata != null) {	
			serviceName = metadata.getServiceName();
			version = metadata.getVersion();			
			namespace = metadata.getServiceNamespace();					
		}
		return new ServerServiceId(adminName, serviceName, version, namespace);		
	}
	
	/**
	 * Static factory method to create ServerServiceId Object.
	 * 
	 * @param adminName A service admin name
	 * @param version service version.
	 * @return ServerServiceId
	 * @throws ServiceException Exception when ServiceId creation fails.
	 */
	public static ServerServiceId newInstance(String adminName, String version) throws ServiceException {			
		return new ServerServiceId(adminName, null, version, null);		
	}
	
	/**
	 * Static factory method to create ServerServiceId Object.
	 * 
	 * @param adminName A service admin name.
	 * @return ServerServiceId
	 * @throws ServiceException Exception when ServiceId creation fails.
	 */
	public static ServerServiceId createFallbackServiceId(String adminName) throws ServiceException {		
		return new ServerServiceId(adminName, null, null, null);		
	}	

	/**
	 * Constructor.
	 * @param adminName administrative name of the service.  
	 *        This name matches the folder name holding the ServiceConfig.xml file.
	 * @param version the configured service version.
	 * @param serviceName the name of the service.
	 * @param namespace the namespace of a service.
	 */
	public ServerServiceId(String adminName, String serviceName, String version, String namespace) {
		super(adminName, serviceName, version, namespace, false);
	}	
	
	/**
	 * Returns the service version as the service subname.
	 * @return ServiceSubname a string representating the subname, version in the case of Service side
	 * @see org.ebayopensource.turmeric.runtime.common.service.ServiceId#getServiceSubname() getServiceSubname()
	 */
	@Override
	public String getServiceSubname() {
		return super.getVersion();
	}
	
	/**
	 * Returns the string representation of Server Service Id.
	 * @return the server service Id in String form 
	 */
	@Override
	public String toString() {		
		StringBuilder sb = new StringBuilder();
		sb.append("ServerServiceID: ");
		sb.append(super.toString());
		return sb.toString();
	}	
}
