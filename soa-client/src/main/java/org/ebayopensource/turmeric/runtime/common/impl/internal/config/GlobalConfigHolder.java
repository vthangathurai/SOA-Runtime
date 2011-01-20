/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents processed global (as opposed to per-service instance) configuration, on either client or server side.
 * <p>
 * Note: Most ConfigHolder data is available in higher-level structures. Refer to ServiceDesc and related structures
 * as the primary configuration in the public API for SOA framework.
 * @author rmurphy
 *
 */
public class GlobalConfigHolder extends BaseConfigHolder {
	private Map<String, StorageProviderConfig> m_storageProviders = new HashMap<String, StorageProviderConfig>();
	private Integer m_monitorSnapshotInterval;
	private List<String> m_serviceLayers = Arrays.asList(new String[] {"COMMON", "INTERMEDIATE", "BUSINESS"});

	/** Configuration of the global thread pool shared amongst the SOA clients.
	 * Local binding communication is one of such use cases.  We may want to create
	 * a dedicated thread pool config object when the number of properties increases.
	 */
	// the maximum time that excess idle threads will wait for new tasks before terminating.
	private Long m_threadKeepAliveTimeInSec;

	/**
	 * Safe copy method.
	 * @return a new object with a safe copy of the original data
	 */
	public GlobalConfigHolder copy() {
		GlobalConfigHolder newCH = new GlobalConfigHolder();
		newCH.m_monitorSnapshotInterval = m_monitorSnapshotInterval;
		newCH.m_storageProviders = copyStorageProviders(m_storageProviders);
		newCH.m_serviceLayers = copyServiceLayers(m_serviceLayers);
		return newCH;
	}

	private Map<String, StorageProviderConfig> copyStorageProviders(Map<String, StorageProviderConfig> inProviders) {
		if (inProviders == null) {
			return null;
		}
		HashMap<String, StorageProviderConfig> outProviders = new HashMap<String, StorageProviderConfig>();
		for (StorageProviderConfig provider : inProviders.values()) {
			outProviders.put(provider.getName(), provider.copy());
		}
		return outProviders;
	}

	private List<String> copyServiceLayers(List<String> serviceLayers) {
		if (serviceLayers == null) {
			return null;
		}
		return new ArrayList<String>(serviceLayers);
	}

	/**
	 * @return the map of metric storage provider configurations, indexed by storage provider name
	 */
	public Map<String, StorageProviderConfig> getStorageProviders() {
		if (isReadOnly()) {
			return copyStorageProviders(m_storageProviders);
		}
		return m_storageProviders;
	}

	/**
	 * Set a map of metric storage provider configurations
	 * @param providers the m_storageProviders to set
	 */
	public void setStorageProviders(
			Map<String, StorageProviderConfig> providers) {
		checkReadOnly();
		m_storageProviders = providers;
	}

	/**
	 * @return the metric collection snapshot interval (interval at which all storage providers are notified of new values)
	 */
	public Integer getMonitorSnapshotInterval() {
		return m_monitorSnapshotInterval;
	}

	/**
	 * Set the the metric collection snapshot interval.
	 * @param snapshotInterval the snapshot interval to set
	 */
	public void setMonitorSnapshotInterval(Integer snapshotInterval) {
		checkReadOnly();
		m_monitorSnapshotInterval = snapshotInterval;
	}

	/**
	 * Set the service layer values
	 * @param serviceLayers the service layers to set
	 */
	public void setServiceLayerNames(List<String> serviceLayers) {
		checkReadOnly();
		m_serviceLayers = serviceLayers;
	}

	/**
	 * @return the list of service layers
	 */
	public List<String> getServiceLayerNames() {
		if (isReadOnly()) {
			return copyServiceLayers(m_serviceLayers);
		}
		return m_serviceLayers;
	}

	/**
	 * Returns the thread keep alive time in seconds
	 */
	public Long getThreadKeepAliveTimeInSec() {
		return m_threadKeepAliveTimeInSec;
	}

	/**
	 * Sets the thread keep alive time
	 * @param keepAliveTime a long that represents the thread keep alive time in seconds
	 */
	void setThreadKeepAliveTimeInSec(Long keepAliveTime) {
		checkReadOnly();
		m_threadKeepAliveTimeInSec = keepAliveTime;
	}

	/*
	 * Provide a user-readable description of the configuration into a StringBuffer.
	 * @param sb the StringBuffer into which to write the description
	 */
	public void dump(StringBuffer sb) {
		sb.append("========== Global Monitoring Config =========="+"\n");
		if (m_monitorSnapshotInterval != null) {
			sb.append("monitorSnapshotInterval="+m_monitorSnapshotInterval + "\n");
		}

		sb.append("========== Global local-binding thread pool config =========="+"\n");
		if (m_threadKeepAliveTimeInSec != null) {
			sb.append("threadKeepAliveTimeInSec="+m_threadKeepAliveTimeInSec + "\n");
		}

		List<String> storageProviders = new ArrayList<String>(m_storageProviders.keySet());
		Collections.sort(storageProviders);
		for (String name : storageProviders) {
			StorageProviderConfig provider = m_storageProviders.get(name);
			provider.dump(sb);
		}

		sb.append("\n========== Global Layer Config =========="+"\n");
		sb.append("layers=");
		if (m_serviceLayers != null) {
			for (String name : m_serviceLayers) {
				sb.append(name).append(" ");
			}
		}
		sb.append("\n");
	}
}
