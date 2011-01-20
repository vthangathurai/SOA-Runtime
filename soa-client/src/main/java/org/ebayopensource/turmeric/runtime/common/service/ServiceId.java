/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.service;

import org.ebayopensource.turmeric.runtime.binding.utils.BindingUtils;
import org.ebayopensource.turmeric.runtime.common.utils.Preconditions;


/**
 * Represents a concise identifier for a specific instance of a client or service, 
 * as distinguished primarily by having a distinct configuration.  
 * The information for ServiceId is retrieved from the service_metadata.properties file
 * packaged with the service interface project.
 * 
 * The <code>adminName</code> is the unique id for a service instance. It represents the 
 * service triplet <namespace, serviceName, version> 
 *   
 * Examples of administrative purposes include:
 * <UL>
 *  <LI> Reporting and logging.
 *  <LI> Management operations such as marking down, viewing, or configuring client and service instances.
 * </UL>
 * 
 * @author ichernyshev
 */
public abstract class ServiceId {

	private final String adminName;
	private final String serviceName;
	private final String version;
	private final String namespace;
	private final boolean isClientSide;	
	private int hashCode;
	
	/**
	 * The comma.
	 */
	protected static final String COMMA = ",";

	/**
	 * Constructor; called by the derived classes.
	 * @param adminName the administrative name of the service; see <code>getAdminName()</code>.
	 * @param serviceName the service name
	 * @param version the service version
	 * @param namespace the service namespace 
	 * @param isClientSide true if this is a client-side service ID.
	 * 
	 * @since SOA 2.6
	 */
	protected ServiceId(String adminName, String serviceName, String version, String namespace, boolean isClientSide) {
		Preconditions.checkNotNull(adminName);
		this.adminName = adminName;
		this.serviceName = serviceName;
		this.version = version;
		this.namespace = namespace;
		this.isClientSide = isClientSide;
	}
	
	/**
	 * Returns the administrative name of the service.
	 * On the client side, the administrative name is the local part of the service qualified name configured in ClientConfig.xml. 
	 * On the server side, the administrative name matches the folder name holding the ServiceConfig.xml file.
	 * @return the administrative name
	 */
	public final String getAdminName() {
		return adminName;
	}

	/**
	 * Returns true, if this is a Client ServiceId.
	 *         false, if Server ServiceId
	 * @return true if client side ServiceId
	 */
	public final boolean isClientSide() {
		return isClientSide;
	}

	/**
	 * Abstract function; clients return the client name as a subname.  Services currently return empty string and may be
	 * extended in future SOA Framework phase to return version number.
	 * @return the service sub-name
	 */
	public abstract String getServiceSubname();

	/**
	 * Returns the Service Name.
	 * @return service name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Returns the service version.
	 * @return version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Returns the Service Namespace.
	 * @return namespace
	 */
	public String getNamespace() {
		return namespace;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		// Return true if it the same object
		if(object == this) {
			return true;
		}		
		if (object instanceof ServiceId) {
			ServiceId other = (ServiceId) object;
			int hash = hashCode; // Single read on hashCodes as they may change
			int otherHash = other.hashCode;	
			// Return false if the hash codes do not match.
			if(hash != otherHash && hash != 0 && otherHash != 0) {
				return false;
			}
			return adminName.equals(other.adminName) && 
				BindingUtils.sameObject(serviceName, other.serviceName) && 
			 	BindingUtils.sameObject(version, other.version)	&& 
			 	BindingUtils.sameObject(namespace, other.namespace);			
		}		
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {		
		if (hashCode == 0) {
			StringBuilder hash = new StringBuilder(); 
			hash.append(adminName);
			if (serviceName != null) 
				hash.append(serviceName);
			if (version != null) 
				hash.append(version);
			if (namespace != null) 
				hash.append(namespace);	
			hashCode = hash.toString().hashCode();
		}		
		return hashCode;
	}
	
	/**
	 * Returns the string representation of Service Id.
	 * @return the service Id in String form 
	 */
	@Override
	public String toString() {		
		StringBuilder sb = new StringBuilder().append('[');
		sb.append(adminName).append(COMMA);
		sb.append(namespace).append(COMMA);
		sb.append(serviceName).append(COMMA);
		sb.append(version).append(']');		
		return sb.toString();
	}	
	
	/**
	 * @return The canonical service id.
	 */
	public String getCanonicalServiceName() {
		return "(" + namespace + COMMA+ serviceName + COMMA + version + ")";
	}
}
