/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring;

import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.GlobalConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.StorageProviderConfig;
import org.ebayopensource.turmeric.runtime.common.impl.monitoring.storage.DiffBasedSnapshotFileLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ReflectionUtils;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsRegistry;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsStorageProvider;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.monitoring.ClientServiceMonitoringCompStatus;


/**
 * @author wdeng
 */
public final class MonitoringSystem {

	private static final int DEFAULT_SNAPSHOT_INTERVAL = 60;
	public final static String COLLECTION_LOCATION = "collectionLocation";
	public final static String COLLECTION_LOCATION_SERVER = "server";
	public final static String COLLECTION_LOCATION_CLIENT = "client";

	private static boolean s_clientInitialized;
	private static boolean s_serverInitialized;
	private static MonitoringDesc s_clientMonDesc;
	private static MonitoringDesc s_serverMonDesc;
	private static MetricsSnapshotScheduler s_serverScheduler;
	private static MetricsSnapshotScheduler s_clientScheduler;

	private MonitoringSystem() {
		// no instances
	}

	public synchronized static void initializeClient(
			GlobalConfigHolder globalConfig) throws ServiceException {
		if (s_clientInitialized) {
			return;
		}

		s_clientInitialized = true;

		MetricsRegistryImpl.createClientInstance();

		MetricsRegistry.getClientInstance().registerAllMetricsForClass(
				SystemMetricDefs.class);

		ClientServiceMonitoringCompStatus.initializeCompStatus();

		MetricsCollector collector = MetricsCollector.getClientInstance();

		s_clientMonDesc = startSnapshotScheduler(globalConfig, collector,
				COLLECTION_LOCATION_CLIENT, false);
	}

	public synchronized static void initializeServer(
			GlobalConfigHolder globalConfig) throws ServiceException {
		if (s_serverInitialized) {
			return;
		}

		s_serverInitialized = true;

		MetricsRegistryImpl.createServerInstance();

		MetricsRegistry.getServerInstance().registerAllMetricsForClass(
				SystemMetricDefs.class);

		MetricsCollector collector = MetricsCollector.getServerInstance();

		s_serverMonDesc = startSnapshotScheduler(globalConfig, collector,
				COLLECTION_LOCATION_SERVER, true);
	}

	public static MonitoringDesc getServerMonitoringDesc() {
		return s_serverMonDesc;
	}

	public static MonitoringDesc getClientMonitoringDesc() {
		return s_clientMonDesc;
	}

	private static MonitoringDesc startSnapshotScheduler(
			GlobalConfigHolder globalConfig, MetricsCollector collector,
			String collectionLocation, boolean isServer) throws ServiceException {
		MonitoringDesc monDesc = createMonitoringDesc(globalConfig, collectionLocation);

		MetricsSnapshotScheduler scheduler = new MetricsSnapshotScheduler(
				monDesc, collector);
		if(isServer)
		{
			s_serverScheduler = scheduler;
		}
		else
		{
			s_clientScheduler = scheduler;
		}
		scheduler.start();

		return monDesc;
	}
	
	public static void persistMetricsSnapSnapshot(String adminName, boolean isServer)
	{
		if(isServer && s_serverScheduler != null)
		{
			s_serverScheduler.persistSnapshot(adminName);
		}
		if(!isServer && s_clientScheduler != null)
		{
			s_clientScheduler.persistSnapshot(adminName);
		}
		
	}

	private static MonitoringDesc createDefaultMonitoringDesc(String collectionLocation) {
		String name = "DiffBasedFileLogger";
		DiffBasedSnapshotFileLogger provider = new DiffBasedSnapshotFileLogger();
		provider.init(null, name, collectionLocation, DEFAULT_SNAPSHOT_INTERVAL);

		StorageProviderDesc defaultProviderDesc = new StorageProviderDesc(name, provider, null, null);
		Map<String,StorageProviderDesc> providerDescs = new HashMap<String, StorageProviderDesc>(1);
		providerDescs.put(name, defaultProviderDesc);

		addInternalProviders(providerDescs, collectionLocation, DEFAULT_SNAPSHOT_INTERVAL);
		return new MonitoringDesc(DEFAULT_SNAPSHOT_INTERVAL, providerDescs);
	}

	private static MonitoringDesc createMonitoringDesc(GlobalConfigHolder globalConfig,
		String collectionLocation) throws ServiceException
	{
		if (null == globalConfig) {
			return createDefaultMonitoringDesc(collectionLocation);
		}

		Integer snapshotInterval = globalConfig.getMonitorSnapshotInterval();
		if (null == snapshotInterval) {
			return createDefaultMonitoringDesc(collectionLocation);
		}

		Map<String, StorageProviderDesc> providerDescs = createStorageProviderDescs(
				globalConfig, collectionLocation);
		addInternalProviders(providerDescs, collectionLocation, snapshotInterval);
		MonitoringDesc desc = new MonitoringDesc(snapshotInterval.longValue(), providerDescs);
		return desc;
	}

	private static Map<String, StorageProviderDesc> createStorageProviderDescs(
			GlobalConfigHolder globalConfig, String collectionLocation)
		throws ServiceException
	{
		Map<String, StorageProviderConfig> providerConfigs = globalConfig.getStorageProviders();
		if (null == providerConfigs || providerConfigs.isEmpty()) {
			return new HashMap<String, StorageProviderDesc>();
		}

		int size = providerConfigs.size();
		HashMap<String, StorageProviderDesc> descs = new HashMap<String, StorageProviderDesc>(size);

		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		Integer snapshotInterval = globalConfig.getMonitorSnapshotInterval();
		for (StorageProviderConfig config: providerConfigs.values()) {
			String name = config.getName();
			String clzName = config.getClassname();
			Map<String,String> options = config.getOptions();

			MetricsStorageProvider provider = ReflectionUtils.createInstance(
				clzName, MetricsStorageProvider.class, cl);

			provider.init(options, name, collectionLocation, snapshotInterval);

			StorageProviderDesc desc = new StorageProviderDesc(
				name, provider, options, collectionLocation);
			descs.put(name, desc);
		}

		return descs;
	}

	private static void addInternalProviders(Map<String, StorageProviderDesc> providerDescs,
		String collectionLocation, Integer snapshotInterval)
	{
		boolean hasDiffView = false;
		for (StorageProviderDesc provider: providerDescs.values()) {
			if (provider.getProvider() instanceof DiffSnapshotView) {
				hasDiffView = true;
			}
		}

		if (!hasDiffView) {
			String name = "__sys_DiffSnapshotView_";
			DiffSnapshotView provider = new DiffSnapshotView();
			Map<String,String> options = new HashMap<String,String>();
			
			MetricsConfigManager configManager = null;
			
			if (COLLECTION_LOCATION_CLIENT.equalsIgnoreCase(collectionLocation)) {
				configManager = MetricsConfigManager.getClientInstance();
			} else {
				configManager = MetricsConfigManager.getServerInstance();
			}
			
			provider.init(options, name, collectionLocation, snapshotInterval);
			provider.setConfigManager(configManager);
			
			StorageProviderDesc desc = new StorageProviderDesc(name, provider,
				options, collectionLocation);
			providerDescs.put(name, desc);
		}
	}
}
