/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

public class MetadataPropertyConfigMapper {

	public static void map(String metaDataConfigFileName, MetadataPropertyConfigHolder holder, List<String> serviceLayers)
	throws ServiceCreationException
	{
		map(metaDataConfigFileName, false, holder, serviceLayers);
	}
	
	public static void map(String metaDataConfigFileName, boolean isOptional, MetadataPropertyConfigHolder holder, List<String> serviceLayers)
		throws ServiceCreationException
	{
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream is = cl.getResourceAsStream(metaDataConfigFileName);
		if (is == null) {
			if (isOptional) {
				return;
			}

			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_CANNOT_LOAD_FILE, 
					ErrorConstants.ERRORDOMAIN, new Object[] {metaDataConfigFileName +" Unable to locate the config file" }));
			
		}

		Properties props = new Properties();

		try {
			try {
				props.load(is);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_CANNOT_LOAD_FILE, 
					ErrorConstants.ERRORDOMAIN, new Object[] {metaDataConfigFileName + " : " + e.toString()}));
		}

		Enumeration e = props.propertyNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			String value = props.getProperty(name);
			holder.setProperty(name, value);
		}
		
		validateLayer(metaDataConfigFileName, holder, serviceLayers);
	}
	
	private static void validateLayer(String metaDataConfigFileName, 
			MetadataPropertyConfigHolder holder, 
			List<String> serviceLayers) 
		throws ServiceCreationException
	{
		String layer = holder.getLayer();
		if (layer == null || (!serviceLayers.contains(layer))) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[] {metaDataConfigFileName, MetadataPropertyConfigHolder.KEY_LAYER 
						+ "=" + layer + ", expected=" + serviceLayers}));
		}
	}
}
