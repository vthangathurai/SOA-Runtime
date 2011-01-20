/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Representation of Legacy Properties.
 * <p>
 * The properties from both a properties file, and a project.properties
 * are loaded and tracked separately (for logging purposes).
 * <p>
 * NOTE: Do not use LegacyProperties from within standard maven flows!
 */
public class LegacyProperties {
	private Log log;
	private Properties projectProps;
	private Properties legacyFileProps;
	private File legacyPropertiesFile;
	
	/**
	 * Create a LegacyProperties object for dealing with Legacy Properties.
	 * 
	 * @param log
	 *            the log to write to
	 * @param project
	 *            the maven project to get maven properties from
	 * @param legacyPropertiesFile
	 *            the (potential) legacy properties file to load from. if this file does not exist, no properties are
	 *            loaded, and no exception is thrown.
	 * @throws MojoFailureException
	 *             if the legacyPropertiesFile exists, but there is a problem reading / parsing / loading the properties
	 *             from the file.
	 */
	public LegacyProperties(Log log, MavenProject project, File legacyPropertiesFile)
	throws MojoFailureException
	{
		this.log = log;
		this.legacyPropertiesFile = legacyPropertiesFile;
		
		projectProps = project.getProperties();
		if (projectProps == null) {
			projectProps = new Properties();
		}
		
		if (legacyPropertiesFile.exists()) {
			FileReader reader = null;
			try {
				reader = new FileReader(legacyPropertiesFile);
				legacyFileProps = new Properties();
				legacyFileProps.load(reader);
			} catch (IOException e) {
				throw new MojoFailureException("Unable to load properties file: " + legacyPropertiesFile, e);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
	}
	
	/**
	 * Asserts that the required legacyPropertiesFile exists.
	 * 
	 * @throws MojoFailureException
	 *             if unable to find the legacy properties file
	 */
	public void assertLegacyFileRequired() throws MojoFailureException {
		if (legacyPropertiesFile.exists() == false) {
			throw new MojoFailureException("Missing Required Legacy File: " + legacyPropertiesFile);
		}
	}
	
	/**
	 * Get a property.
	 * <p>
	 * Search order is: Project Properties, then Legacy File Properties, finally Default Value.
	 * 
	 * @param key
	 *            the property key to look for
	 * @param defaultValue
	 *            the default value to return if not found in the primary two properties sources.
	 * @return the value
	 */
	public String getProperty(String key, String defaultValue) {
		String ret = projectProps.getProperty(key);
		if (ret != null) {
			log.debug("[LEGACY] Using project property value [" + key + "]: \"" + ret + "\"");
			return ret;
		}

		ret = legacyFileProps.getProperty(key);
		if (ret != null) {
			log.debug("[LEGACY] Using file property value [" + key + "]: \"" + ret + "\"");
			return ret;
		}

		log.debug("[LEGACY] Property not found, using default [" + key + "]: \"" + defaultValue + "\"");
		return defaultValue;
	}

	public boolean exists() {
		return legacyPropertiesFile.exists();
	}
}
