/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsStorageProvider;


/**
 * @author wdeng
 */
public final class MonitoringDesc {
	private final long m_snapshotInterval;
	private final Map<String, StorageProviderDesc> m_providerDescs;
	private final List<MetricsStorageProvider> m_providers;

	public MonitoringDesc(long snapshotInterval, Map<String, StorageProviderDesc> providerDescs) {
		m_snapshotInterval = snapshotInterval;
		m_providerDescs = Collections.unmodifiableMap(providerDescs);
		m_providers = Collections.unmodifiableList(buildProviderList());
	}

	public long getSnapshotInterval() {
		return m_snapshotInterval;
	}

	public Collection<StorageProviderDesc> getAllProviderDescs() {
		return m_providerDescs.values();
	}

	/**
	 * Returns the list of all the registered service metrics storage provider.
	 */
	public Collection<MetricsStorageProvider> getAllProviders() {
		return m_providers;
	}

	private ArrayList<MetricsStorageProvider> buildProviderList() {
		ArrayList<MetricsStorageProvider> providers = new ArrayList<MetricsStorageProvider>(m_providerDescs.size());
		for (Iterator<StorageProviderDesc> it=m_providerDescs.values().iterator(); it.hasNext();) {
			StorageProviderDesc desc = it.next();
			providers.add(desc.getProvider());
		}
		return providers;
	}
}
