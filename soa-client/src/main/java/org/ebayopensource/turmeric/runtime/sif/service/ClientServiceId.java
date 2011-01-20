/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.service;

import org.ebayopensource.turmeric.runtime.binding.utils.BindingUtils;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;


/**
 * Client side derived class of ServiceId.  The client name forms the service sub-name.
 * @author ichernyshev
 */
public final class ClientServiceId extends ServiceId {

	private final String clientName;	
	private final String envName;
	private int hashCode;
	
	/**
	 * Constructor.
	 * @param adminName administrative name of the service within this client instance.
	 * 		  On the client side, the administrative name is the local part of the 
	 * 		  service qualified name configured in ClientConfig.xml.
	 * @param clientName the unique client name (from ServiceFactory.create() method).  
	 *        This name matches the name of the configuration directory under 
	 *        META-INF/soa/client/config. null indicates the "default" client. 
	 */
	public ClientServiceId(String adminName, String clientName) {
		this(adminName, clientName, null);
	}

	/**
	 * Constructor.
	 * @param adminName administrative name of the service within this client instance.
	 * 		  On the client side, the administrative name is the local part of the 
	 * 		  service qualified name configured in ClientConfig.xml.
	 * @param clientName the unique client name (from ServiceFactory.create() method).  
	 * 		  This name matches the name of the configuration directory under 
	 *        META-INF/soa/client/config/
	 * @param envName  the unique environment name location of cc.xml derived as
	 *        META-INF/soa/client/config/<clientname>/envname/adminName/cc.xml
	 */
	public ClientServiceId(String adminName, String clientName, String envName) {
		this(adminName, null, null, null, clientName, envName);
	}

	/**
	 * Constructor.
	 * @param adminName administrative name of the service within this client instance.
	 * 		  On the client side, the administrative name is the local part of the 
	 * 		  service qualified name configured in ClientConfig.xml.
	 * @param serviceName the service name
	 * @param version the service version
	 * @param namespace the service namespace 
	 * @param clientName the unique client name (from ServiceFactory.create() method).  
	 * 		  This name matches the name of the configuration directory under 
	 * 		  META-INF/soa/client/config/
	 * @param envName  the unique environment name location of cc.xml derived as
	 * 		  META-INF/soa/client/config/<clientname>/envname/adminName/cc.xml
	 */
	public ClientServiceId(String adminName, String serviceName,
			String version, String namespace, String clientName, String envName) {
		super(adminName, serviceName, version, namespace, true);
		this.clientName = clientName;
		this.envName = envName;
	}

	/**
	 * Implementation of the abstract method in the parent. In Client side,
	 * the client name is returned as the service subname.
	 * 
	 * @return the service sub-name
	 */
	@Override
	public String getServiceSubname() {
		return clientName;
	}

	/**
	 * Returns the client name.  This is the unique client name (from ServiceFactory.create() method).  This
	 * name matches the name of the configuration directory under META-INF/soa/client/config.
	 * null indicates the "default" client. 
	 * @return the client name, or null for default.
	 */
	public final String getClientName() {
		return clientName;
	}

	/**
	 * Returns the environMent name for the clientConfig.xml.
	 * @return  environMent name
	 */
	public String getEnvName() {
		return envName;
	}

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			StringBuilder hash = new StringBuilder();
			int superHash = super.hashCode();
			if (clientName != null) 
				hash.append(clientName);				
			if (envName != null) 
				hash.append(envName);
			hashCode = superHash ^ hash.toString().hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object object) {
		// If it is the same object, return true.
		if (object == this) {
			return true;
		}		
		if (object instanceof ClientServiceId) {
			ClientServiceId other = (ClientServiceId) object;
			int hash = hashCode;
			int objHash = other.hashCode;
			// Return false if the hash codes do not matches
			if(hash != objHash && hash != 0 && objHash != 0) {
				return false;
			}
			return super.equals(other) && BindingUtils.sameObject(envName, other.envName)
				&& BindingUtils.sameObject(clientName, other.clientName);			
		}
		return false;
	}

	/**
	 * Returns the string representation of Client Service Id.
	 * 
	 * @return the client service Id in String form
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ClientServiceId(");
		sb.append(super.toString()).append(ServiceId.COMMA);
		sb.append(clientName).append(ServiceId.COMMA);
		sb.append(envName).append(')');
		return sb.toString();
	}
}
