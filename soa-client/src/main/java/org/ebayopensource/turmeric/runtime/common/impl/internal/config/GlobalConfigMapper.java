/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class GlobalConfigMapper {

	public static void map(String filename, Element topLevel, GlobalConfigHolder dst) throws ServiceCreationException {
	
		if (topLevel == null) {
			return;
		}
		Element monitorConfig = DomParseUtils.getSingleElement(filename, topLevel, "monitor-config");
		if (monitorConfig != null) {
			mapMonitorConfig(filename, monitorConfig, dst);
		}
		Element localBindingThreadPool =
			DomParseUtils.getSingleElement(filename, topLevel, "local-binding-thread-pool");
		if (localBindingThreadPool != null) {
			mapLocalBindingThreadPool(filename, localBindingThreadPool, dst);
		}
		Element serviceLayerConfig = DomParseUtils.getSingleElement(filename, topLevel, "service-layer-config");
		mapServiceLayerConfig(filename, serviceLayerConfig, dst);
	}
	
	public static void mapMonitorConfig(String filename, Element monitorConfig, GlobalConfigHolder dst) throws ServiceCreationException {
		NodeList storageProviders = DomParseUtils.getImmediateChildrenByTagName(monitorConfig, "storage-provider");
		Map<String, StorageProviderConfig> providersMapOut = new HashMap<String, StorageProviderConfig>();
		for (int i = 0; i < storageProviders.getLength(); i++) {
			Element storageProvider = (Element) storageProviders.item(i);
			String name = DomParseUtils.getRequiredAttribute(filename, storageProvider, "name");
			String classname = DomParseUtils.getElementText(filename, storageProvider, "class-name");
			StorageProviderConfig providerOut = new StorageProviderConfig();
			providerOut.setClassname(classname);
			providerOut.setName(name);
			Map<String, String> optionsMap = providerOut.getOptions();
			OptionList options = DomParseUtils.getOptionList(filename, storageProvider, "storage-options");
			DomParseUtils.storeNVListToHashMap(filename, options, optionsMap);

//			DomParseUtils.mapOptions(filename, storageProvider, "storage-options", optionsMap);
			providersMapOut.put(name, providerOut);
		}
		dst.setStorageProviders(providersMapOut);
		Integer snapshotInterval = DomParseUtils.getElementInteger(filename, monitorConfig, "snapshot-interval");
		dst.setMonitorSnapshotInterval(snapshotInterval);
		
	}
	
	public static void mapServiceLayerConfig(String filename, Element serviceLayerConfig, GlobalConfigHolder dst) throws ServiceCreationException {
		if (serviceLayerConfig != null) {
			List<String> serviceLayers = DomParseUtils.getStringList(filename, serviceLayerConfig, "layer-name");
			dst.setServiceLayerNames(serviceLayers);
		}
	}
	
	private static void mapLocalBindingThreadPool(final String filename,
												final Element localBindingThreadPool,
												final GlobalConfigHolder dst)
		throws ServiceCreationException
	{
		if (localBindingThreadPool != null) {
			Long keepAliveTimeInSecs =
				DomParseUtils.getElementLong(filename, localBindingThreadPool, "keep-alive-time-in-secs");
			dst.setThreadKeepAliveTimeInSec(keepAliveTimeInSecs);
		}
	}
}
