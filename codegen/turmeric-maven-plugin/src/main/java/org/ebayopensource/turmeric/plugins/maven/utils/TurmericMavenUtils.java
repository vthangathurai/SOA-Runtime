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

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.ebayopensource.turmeric.plugins.maven.utils.TurmericMavenConstants.ProjectType;

/**
 * Common utilities for Turmer Maven Plugin
 * @author yayu
 */
public final class TurmericMavenUtils {
	private TurmericMavenUtils() {
		super();
	}
	
	public static boolean invokedFromEclipse() {
		final String javaClasspath = System.getProperty("java.class.path");
		if(StringUtils.isBlank(javaClasspath)) {
			return false;
		}
		
		return (javaClasspath.contains("org.eclipse.equinox.launcher"));
	}
	
	public static boolean isValidInterfaceProject(String groupID, String projectType) {
		return ProjectType.INTERFACE.name().equalsIgnoreCase(projectType);
	}
	
	public static boolean isValidImplementationProject(String groupID, String projectType) {
		return ProjectType.IMPLEMENTATION.name().equalsIgnoreCase(projectType);
	}
	
	/* DISABLED - Assumes arbitrary and hardcoded directory paths
	public static boolean isFoldersAvailable(MavenProject project,  
			Log logger, String... folders) {
		if (project != null) {
			final File projectDir = getProjectFile(project);
			for (String folder: folders) {
				final File dir = new File(projectDir, folder);
				if (dir.exists() == false) {
					logger.warn("Folder '" + dir + "' is missing");
					return false;
				} else if (dir.listFiles().length == 0) {
					logger.warn("Folder '" + dir + "' is empty");
					return false;
				}
			}
			return true;
		}
		return false;
	}
	*/
	
	public static File getProjectFile(MavenProject project) {
		File projectDir = project.getFile();
		if (projectDir.isFile())
			projectDir = projectDir.getParentFile();
		return projectDir;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
