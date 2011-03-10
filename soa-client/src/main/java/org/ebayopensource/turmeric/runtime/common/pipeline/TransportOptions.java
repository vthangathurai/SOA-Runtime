/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * This class represents usage options that are configurable against a transport.  The same class is used to hold both
 * file-based configuration for transports, and options that are dynamically sent inside the ServiceInvokerOptions for a 
 * particular client invocation.
 * 
 * Not all options are applicable to every transport, e.g., LocalTransport does not use timeout or retry logic.  Also,
 * some transports may not support dynamic changing of some or all options.
 * 
 * @author rmurphy
 *
 */
public class TransportOptions {
	private String m_httpTransportClassName;
	private Integer m_numConnectRetries;
	private Integer m_connectTimeout;
	private Integer m_receiveTimeout;
	private Integer m_invocationTimeout;
	private Boolean m_skipSerialization;
	private Map<String,String> m_properties;
	// if true, the call to the server will be handled in a separate thread
	private Boolean m_useDetachedLocalBinding;
	private Boolean m_clientStreaming;

	/**
	 * The default constructor.
	 */
	public TransportOptions() {
		m_properties = new HashMap<String,String>();
	}

	/**
	 * Copy constructor.  Makes a safe copy of the source TransportOptions structure.
	 * @param src the structure from which to copy
	 */
	public TransportOptions(TransportOptions src) {
		m_httpTransportClassName = src.m_httpTransportClassName;
		m_numConnectRetries = src.m_numConnectRetries;
		m_connectTimeout = src.m_connectTimeout;
		m_receiveTimeout = src.m_receiveTimeout;
		m_invocationTimeout = src.m_invocationTimeout;
		m_skipSerialization = src.m_skipSerialization;
		m_useDetachedLocalBinding = src.m_useDetachedLocalBinding;
		m_clientStreaming = src.m_clientStreaming;

		m_properties = new HashMap<String,String>(src.m_properties);
	}

	/**
	 * Creates a new TransportOptions that is a merge of self and the supplied structure.  First, a copy of the
	 * current instance's values is made; then, any non-null values from the supplied override structure are 
	 * set into the copied structure to complete the merge.
	 * @param overrides the structure whose options are merged (overridden) into self's values.
	 * @return the merged TransportOptions
	 */
	public TransportOptions getMergedCopy(TransportOptions overrides) {
		TransportOptions result = new TransportOptions(this);

		if (overrides == null) {
			return result;
		}
		
		if (overrides.m_httpTransportClassName != null) {
			result.m_httpTransportClassName = overrides.m_httpTransportClassName;
		}

		if (overrides.m_numConnectRetries != null) {
			result.m_numConnectRetries = overrides.m_numConnectRetries;
		}

		if (overrides.m_connectTimeout != null) {
			result.m_connectTimeout = overrides.m_connectTimeout;
		}

		if (overrides.m_receiveTimeout != null) {
			result.m_receiveTimeout = overrides.m_receiveTimeout;
		}

		if (overrides.m_invocationTimeout != null) {
			result.m_invocationTimeout = overrides.m_invocationTimeout;
		}

		if (overrides.m_skipSerialization != null) {
			result.m_skipSerialization = overrides.m_skipSerialization;
		}
		
		if (overrides.m_useDetachedLocalBinding != null) {
			result.m_useDetachedLocalBinding = overrides.m_useDetachedLocalBinding;			
		}
		
		if (overrides.m_clientStreaming != null) {
			result.m_clientStreaming = overrides.m_clientStreaming;
		}
		
		result.m_properties.putAll(overrides.m_properties);

		return result;
	}
	
	/**
	 * @return The class name of the trransport.
	 */
	public String getHttpTransportClassName() {
		if (m_httpTransportClassName == null)
			m_httpTransportClassName = "org.ebayopensource.turmeric.runtime.sif.impl.transport.http.HTTPSyncAsyncClientTransport";
		return m_httpTransportClassName;
	}
	
	/**
	 * Sets the transport class name.
	 * @param value A Transport class name.
	 */
	public void setHttpTransportClassName(String value) {
		m_httpTransportClassName = value;
	}

	/**
	 * Returns the number of retries the transport should attempt upon communication (transport-level) errors.  The
	 * transport should implement this so that the maximum number of attempts will be the number of retries, plus one
	 * (the initial try).
	 * @return  the number of transport-level retries
	 */
	public Integer getNumConnectRetries() {
		return m_numConnectRetries;
	}

	/**
	 * Sets the number of transport-level retries.
	 * @param numConnectRetries number of transport-level retries (total number will be this number, plus one).
	 */
	public void setNumConnectRetries(Integer numConnectRetries) {
		m_numConnectRetries = numConnectRetries;
	}

	/**
	 * Returns the timeout on connection attempts e.g. socket connect operation.
	 * @return the connection timeout
	 */
	public Integer getConnectTimeout() {
		return m_connectTimeout;
	}

	/**
	 * Sets the timeout on connection attempts e.g. socket connect operation.
	 * @param timeout the connection timeout
	 */
	public void setConnectTimeout(Integer timeout) {
		m_connectTimeout = timeout;
	}

	/**
	 * Returns the timeout on read attempts e.g. socket receive operation
	 * @return the receive timeout
	 */
	public Integer getReceiveTimeout() {
		return m_receiveTimeout;
	}

	/**
	 * Sets the timeout on read attempts e.g. socket receive operation
	 * @param timeout the receive timeout
	 */
	public void setReceiveTimeout(Integer timeout) {
		m_receiveTimeout = timeout;
	}

	/**
	 * Returns the timeout on overall transport invocation (start of connection, up through all receive operations needed to
	 * receive all the returned data).
	 * @return the invocation timeout
	 */
	public Integer getInvocationTimeout() {
		return m_invocationTimeout;
	}

	/**
	 * Sets the timeout on transport invocation (start of connection, up through all receive operations needed to
	 * receive all the returned data).
	 * @param timeout the invocation timeout
	 */
	public void setInvocationTimeout(Integer timeout) {
		m_invocationTimeout = timeout;
	}

	/**
	 * Returns whether the transport is configured to skip serialization; normally applicable only to locally bound 
	 * (in-process) transports.  If true, no serialization is done, rather the message's parameter object trees are
	 * passed directly to the receiving side.  All object trees contain references to the original values, so extreme 
	 * care must be taken to avoid changing values and causing inconsistencies between the client and server views of 
	 * the data.
	 * @return whether to skip serialization; null indicates no preference is set (used when merging); transports 
	 * should evaluate null as equivalent to false.
	 */
	public Boolean getSkipSerialization() {
		return m_skipSerialization;
	}

	/**
	 * Sets the transport property indicating whether to skip serialization; refer to <code>getSkipSerialization()</code>.
	 * @param skipSerialization whether to skip serialization
	 */
	public void setSkipSerialization(Boolean skipSerialization) {
		m_skipSerialization = skipSerialization;
	}
	
	/**
	 * Returns whether the transport is configured to perform the local binding in a separate thread.
	 * @return True if the transport is configured to perform the local binding in a separate thread.
	 */
	public Boolean isUseDetachedLocalBinding() {
		return m_useDetachedLocalBinding;
	}

	/**
	 * Sets the transport property indicating whether the local binding operation should be
	 * performed in a separate thread or not; refer to <code>isUseDetachedLocalBinding()</code>.
	 * @param useDetachedLocalBinding Boolean that indicates whether detached local binding
	 * should be used or not.
	 */
	public void setUseDetachedLocalBinding(Boolean useDetachedLocalBinding) {
		m_useDetachedLocalBinding = useDetachedLocalBinding;
	}

	/**
	 * Specifies if the client API is in content streaming mode. 
	 * @return the m_clientStreaming
	 */
	public Boolean isClientStreaming() {
		return m_clientStreaming;
	}

	/**
	 * Sets the client streaming mode. 
	 * @param clientStreaming the new value for the clientStreaming property. 
	 */
	public void setClientStreaming(Boolean clientStreaming) {
		m_clientStreaming = clientStreaming;
	}

	/**
	 * Returns this TransportOptions structure's property map for modification purposes.  The property map 
	 * is a generic map with string keys mapping to string values.  It can be used to hold any options that are meaningful
	 * to a specific transport, which are not represented by the "first class" properties of TransportOptions (i.e. all the 
	 * properties besides this map).
	 * @return the property map
	 */
	public Map<String, String> getProperties() {
		return m_properties;
	}

	/**
	 * Sets the TransportOptions property map.  A copy of the supplied map is made.
	 * @param options the property map
	 */
	public void setProperties(Map<String, String> options) {
		if (options != null) {
			m_properties = new HashMap<String,String>(options);
		} else {
			m_properties = new HashMap<String,String>();
		}
	}
	
	/**
	 * Returns names of all current properties as an unmodifiable collection.
	 * @return the property names
	 */
	public Collection<String> getAllPropertyNames() {
		return Collections.unmodifiableCollection(m_properties.keySet());
	}

	/**
	 * 
	 * @param name
	 * @return the property value
	 */
	public String getProperty(String name) {
		return m_properties.get(name);
	}
	
	@Override
	public String toString() {
		String superStr = super.toString();
		return superStr.substring(superStr.lastIndexOf(".") + 1) + "("
			+ "httpTransportClassName='" + getHttpTransportClassName() + "'" 
			+ ", "
			+ "connectTimeout=" + getConnectTimeout()
			+ ", "
			+ "invocationTimeout=" + getInvocationTimeout()
		    + ", "
		    + "numConnectRetries=" + getNumConnectRetries()
		    + ", "
		    + "receiveTimeout=" + getReceiveTimeout()
		    + ", "
		    + "properties=" + getProperties()
		    + ", "
		    + "skipSerialization=" + getSkipSerialization()
		    + ", "
		    + "clientScreaming=" + isClientStreaming()
			+ ")";
	}

}
