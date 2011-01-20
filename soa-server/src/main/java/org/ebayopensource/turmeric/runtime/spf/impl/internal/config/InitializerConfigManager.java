/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ParseUtils;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.Initializer.InitializerException;


/**
 * Utility class to load the initializers.
 * 
 * @author mpoplacenel
 */
public class InitializerConfigManager {

	private static final String INITIALIZERS_FILE_NAME = "serviceInitializers.txt";
	
	private static final InitializerConfigManager INSTANCE = new InitializerConfigManager();
	
	private static final Logger LOGGER = LogManager.getInstance(InitializerConfigManager.class);

	public static InitializerConfigManager getInstance() {
		return INSTANCE;
	}
	
	private static String[] loadInitializerClassNames() throws InitializerException {
		String initializersFilename = null;
		InputStream inStream;
		try {
			initializersFilename = 
				ServiceConfigManager.getInstance().getGlobalConfigPath() + INITIALIZERS_FILE_NAME;
			inStream = ParseUtils.getFileStream(initializersFilename);
		} catch (ServiceCreationException e) {
			throw new InitializerException("Unable to locate initializer file" 
						+ (initializersFilename == null ? "" : " " + initializersFilename),
					e);
		}
    	if (inStream == null) {
    		return null;
    	}

    	BufferedReader br = new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset()));
    	List<String> initializerClassNames = new ArrayList<String>();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String initializerClassName = line.trim();
				if (initializerClassName.length() > 0 && initializerClassName.charAt(0) != '#') {
					initializerClassNames.add(initializerClassName);
				}
			}
			
			return initializerClassNames.toArray(new String[initializerClassNames.size()]);
		} catch (IOException e) {
			throw new InitializerException("Error loading " + initializersFilename, e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// ignore
			}
		}	
	}

	private static List<Initializer> loadInitializers(String[] initializerClassNames) throws InitializerException {
		if (initializerClassNames == null) {
			return null;
		}
		List<Initializer> initializers = new ArrayList<Initializer>(initializerClassNames.length);
		for (String initializerClassName : initializerClassNames) {
			Initializer initializer = createInstance(initializerClassName);
			initializers.add(initializer);
		}
		
		return initializers;
	}
	
	private static Initializer createInstance(String initializerClassName) 
	throws InitializerException {
		try {
			Class<?> clazz = Class.forName(initializerClassName);
			return (Initializer) clazz.newInstance();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new InitializerException("Unable to instantiate class " + initializerClassName, e);
		}
	}

	private List<Initializer> m_initializers;
	
	private String[] m_initializerClassNames;

	private InitializerConfigManager() {
		// singleton
	}

	public void callInitializers(String serviceName) throws InitializerException {
		List<Initializer> initializers = getInitializers();
		if (initializers != null) {
			for (Initializer initializer : initializers) {
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine("======== START INITIALIZER: " + initializer + ".initialize(" + serviceName + ")");
				}
				initializer.initialize(serviceName);
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine("======== END INITIALIZER: " + initializer + ".initialize(" + serviceName + ")");
				}
			}
		}
	}

	public synchronized List<Initializer> getInitializers() throws InitializerException {
		if (m_initializers == null) {
			m_initializers = loadInitializers(getInitializerClassNames());
		}
		
		return m_initializers;
	}
	
	public synchronized String[] getInitializerClassNames() throws InitializerException {
		return getInitializerClassNames(true);
	}
	
	public synchronized String[] getInitializerClassNames(boolean load) throws InitializerException {
		if (m_initializerClassNames == null && load) {
			m_initializerClassNames = loadInitializerClassNames();
		}
		
		return m_initializerClassNames;
	}
	
	public synchronized void setInitializerClassNames(String[] initializerClassNames) {
		m_initializerClassNames = initializerClassNames;
		m_initializers = null;
	}
	
}