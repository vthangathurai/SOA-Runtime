/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.ebayopensource.turmeric.plugins.maven.tasks.GenErrorDescriptorTask;

/**
 * Generate an Error Descriptor from an Error xml
 * 
 * @goal gen-errordescriptor
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @requiresProject true
 */
public class GenErrorDescriptorMojo extends AbstractSourceGenOnlyMojo {
	/**
	 * The classname (no path) of the error descriptor to generate.
	 * 
	 * @parameter expression="${errordesc.classname}" default-value="${project.artifactId}ErrorDescriptor"
	 * @required
	 */
	private String className;

	/**
	 * The java package name to generate the error descriptor for.
	 * 
	 * @parameter expression="${errordesc.packagename}" default-value="${project.groupId}.error"
	 * @required
	 */
	private String packageName;

	/**
	 * <p>
	 * Specifies the location of the error XML to use.
	 * </p>
	 * 
	 * <p>
	 * Potential values are a filesystem path, a URL, or a classpath resource. This parameter expects that the contents
	 * of the location conform to the ErrorDescriptors xml format.
	 * </p>
	 * 
	 * <p>
	 * This parameter is resolved as resource, URL, then file. If successfully resolved, the contents of the error
	 * descriptor is copied into the <code>${project.build.directory}/error-descriptor/</code> directory before being
	 * parsed by the mojo. <i>Note: this temporary directory can be configured via the <code>errorXmlTempDir</code>
	 * parameter.
	 * </p>
	 * 
	 * @parameter expression="${errordesc.xml.location}"
	 */
	private String errorXmlLocation;

	/**
	 * Temporary directory used for the error descriptor location lookup
	 * 
	 * @parameter expression="${errordesc.xml.location.temp}"
	 *            default-value="${project.build.directory}/error-descriptor"
	 * @required
	 */
	private File errorXmlTempDir;

	private File mainErrorFile;

	private File idErrorFile;

	private String mainErrorLocation;

	private String idErrorLocation;
	
	@Override
	protected String getGoalName() {
		return "gen-errordescriptor";
	}

	@Override
	protected void onValidateParameters() throws MojoExecutionException,
			MojoFailureException {
		super.onValidateParameters();

		if (errorXmlTempDir == null) {
			throw new MojoFailureException("errorXmlTempDir is null");
		}
		
		mainErrorLocation = toOS(errorXmlLocation + "/" + className + "_metadata.xml");
		idErrorLocation = toOS(errorXmlLocation + "/" + className + ".xml");

		// Always assume project local resources are potentially present.
		mainErrorFile = findResourceFile(mainErrorLocation);
		idErrorFile = findResourceFile(idErrorLocation);
	}

	@Override
	protected void onRunSetup() throws MojoExecutionException,
			MojoFailureException {
		super.onRunSetup();
		ensureDirectoryExists("Error Xml Temp Dir", errorXmlTempDir);

		// If project local resources are not present, use discovery to find them.
		if ((mainErrorFile == null) || (idErrorFile == null)) {
			try {
				ResourceManager locator = getResourceLocator();
				locator.setOutputDirectory(errorXmlTempDir);
	
				mainErrorFile = locator.getResourceAsFile(mainErrorLocation, className + "_metadata.xml");
				if (mainErrorFile == null) {
					throw new MojoFailureException( "Unable to locate main error xml file: " + mainErrorLocation);
				}
	
				idErrorFile = locator.getResourceAsFile(idErrorLocation, className + ".xml");
				if (idErrorFile == null) {
					throw new MojoFailureException( "Unable to locate id error xml file: " + idErrorLocation);
				}
			} catch (ResourceNotFoundException e) {
				throw new MojoExecutionException("Unable to locate error xmls", e);
			} catch (FileResourceCreationException e) {
				throw new MojoExecutionException( "Unable to create error xmls in temp directory", e);
			}
		}
	}

	@Override
	public void onRun() throws MojoExecutionException, MojoFailureException {
		super.onRun();
		getLog().debug("Running mojo");

		GenErrorDescriptorTask task = new GenErrorDescriptorTask();
		task.setPackageName(packageName);
		task.setClassName(className);

		// At this point there is guaranteed to be 2 files in the
		// errorXmlTempDir, for us to use to generate the java source
		task.parseMetadataErrorXml(mainErrorFile);
		task.parseIdErrorXml(idErrorFile);

		String sourcePath = packageName.replace('.', File.separatorChar) + File.separatorChar + className + ".java";
		File outputSourceFile = new File(outputDirectory, sourcePath);

		ensureDirectoryExists("Generated Source Parent Dir", outputSourceFile.getParentFile());
		task.generate(outputSourceFile);
	}

	@Override
	public boolean needsGeneration() throws MojoExecutionException {
		return isNewerThanLastTimestamp(getProjectPomFile(), mainErrorFile, idErrorFile);
	}
}
