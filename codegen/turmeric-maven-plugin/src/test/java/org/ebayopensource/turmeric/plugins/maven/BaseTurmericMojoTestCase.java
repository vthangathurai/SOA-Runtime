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

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.util.FileUtils;
import org.ebayopensource.turmeric.plugins.maven.codegen.PostCodegenAssertions;
import org.ebayopensource.turmeric.plugins.maven.stubs.DebugEnabledLog;
import org.ebayopensource.turmeric.plugins.maven.stubs.TurmericProjectStub;
import org.ebayopensource.turmeric.plugins.maven.util.ProjectClassLoader;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;


public abstract class BaseTurmericMojoTestCase<T extends AbstractTurmericMojo>
		extends AbstractMojoTestCase {
	private File projectBaseDir;
	private boolean mojoLoggingDebug = false;

	public abstract String getTestMojoDirName();

	public abstract String getTestMojoGoal();
	
	protected String toOS(String path) {
		return FilenameUtils.separatorsToSystem(path);
	}
	
	protected String asOSPath(String classname) {
		return FilenameUtils.separatorsToSystem(classname.replace('.', '/'));
	}

	protected void setUp() throws Exception {
		super.setUp();

		projectBaseDir = new File(getBasedir(), toOS("target/tests/" + getTestMojoDirName()));
		FileUtils.deleteDirectory(projectBaseDir);

		// copy src/test/resources/${dirname} into target dir for working with it in a test case
		// Don't want to mess up the source tree with accidents and bugs.
		File srcProjectDir = new File(getBasedir(), toOS("src/test/resources/" + getTestMojoDirName()));
		FileUtils.copyDirectoryStructure(srcProjectDir, projectBaseDir);
	}
	
	protected void setMojoLoggingDebug(boolean enabled) {
		mojoLoggingDebug = enabled;
	}

	protected void setPlexusLoggingLevel(int threshold) {
		try {
			LoggerManager loggerManager = (LoggerManager) lookup(LoggerManager.ROLE);
			loggerManager.setThreshold(threshold);
		} catch (Exception e) {
			// Not a fatal error
			e.printStackTrace(System.err);
		}
	}
	
	protected void testProjectExecuteMojo(T mojo) throws Exception {
		ServiceInvocationException.class.getName();
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			ProjectClassLoader cl = ProjectClassLoader.create(mojo, super.getClassLoader());
			Thread.currentThread().setContextClassLoader(cl);
			mojo.execute();
		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}

	/**
	 * Create and configure a Mojo from a pom in the src/test/resources tree.
	 * 
	 * @return a Mojo
	 * @exception Exception
	 *                if an error occurs
	 */
	@SuppressWarnings("unchecked")
	protected T createMojo() throws Exception {
		File pom = new File(projectBaseDir, "plugin-config.xml");
		T mojo = (T) lookupMojo(getTestMojoGoal(), pom);
		assertNotNull("Mojo should not be null", mojo);
		
		TurmericProjectStub stub = new TurmericProjectStub(getTestMojoDirName());
		setVariableValueToObject(mojo, "project", stub);

		// Configure logging
		if(mojoLoggingDebug || mojo.isVerbose()) {
			setVariableValueToObject(mojo, "log", new DebugEnabledLog());
		}
		
		// Setup default for timestampFile
		File targetDir = new File(mojo.getProject().getBuild().getDirectory());
		File timestampFile = new File(targetDir, "turmeric-timestamp.properties");
		setVariableValueToObject(mojo, "timestampFile", timestampFile);
		
		return mojo;
	}

	protected void assertPostCodegenRules(AbstractTurmericCodegenMojo mojo) throws Exception {
		PostCodegenAssertions pca = new PostCodegenAssertions(mojo);
		pca.assertGenerated();
	}
}
