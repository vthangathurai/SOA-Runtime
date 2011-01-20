/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.resources;

import static org.hamcrest.Matchers.*;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.plugins.maven.resources.ResourceLocator.Location;
import org.ebayopensource.turmeric.plugins.maven.stubs.DebugEnabledLog;
import org.ebayopensource.turmeric.plugins.maven.stubs.SelfProjectStub;
import org.junit.Assert;
import org.junit.Test;

public class ResourceLocatorTest {
	private String toOS(String path) {
		return FilenameUtils.separatorsToSystem(path);
	}
	
	@Test
	public void testFindProjectResource() throws Exception {
		Log log = new DebugEnabledLog();
		MavenProject project = new SelfProjectStub();
		ResourceLocator locator = new ResourceLocator(log, project);
		
		String resourcePath = "org/ebayopensource/turmeric/plugins/maven/resources/messages.properties";
		
		Location location = locator.findResource(resourcePath);
		Assert.assertThat("location", location, notNullValue());
		Assert.assertThat("location.isLocalToProject", location.isLocalToProject(), is(true));
		Assert.assertThat("location.isLocalLiveFile", location.isLiveFile(), is(true));
		
		File expected = new File(MavenTestingUtils.getBasedir(), toOS("src/main/resources/" + resourcePath));
		PathAssert.assertFileExists(expected);
		File actual = location.getFile();
		Assert.assertThat("location.file", actual, notNullValue());
		Assert.assertThat("location.file", actual.getAbsolutePath(), is(expected.getAbsolutePath()));
	}
	
	@Test
	public void testFindOutputDirectoryResource() throws Exception {
		Log log = new DebugEnabledLog();
		MavenProject project = new SelfProjectStub();
		ResourceLocator locator = new ResourceLocator(log, project);
		
		String resourcePath = "org/ebayopensource/turmeric/plugins/maven/resources/ResourceLocator.class";
		
		Location location = locator.findResource(resourcePath);
		Assert.assertThat("location", location, notNullValue());
		Assert.assertThat("location.isLocalToProject", location.isLocalToProject(), is(true));
		Assert.assertThat("location.isLocalLiveFile", location.isLiveFile(), is(true));
		
		File expected = new File(MavenTestingUtils.getBasedir(), toOS("target/classes/" + resourcePath));
		PathAssert.assertFileExists(expected);
		File actual = location.getFile();
		Assert.assertThat("location.file", actual, notNullValue());
		Assert.assertThat("location.file", actual.getAbsolutePath(), is(expected.getAbsolutePath()));
	}
	
	@Test
	public void testFindClasspathResource() throws Exception {
		Log log = new DebugEnabledLog();
		MavenProject project = new SelfProjectStub();
		ResourceLocator locator = new ResourceLocator(log, project);
		
		Location location = locator.findResource("META-INF/maven/org.apache.maven/maven-project/pom.properties");
		Assert.assertThat("location", location, notNullValue());
		Assert.assertThat("location.isLocalToProject", location.isLocalToProject(), is(false));
		Assert.assertThat("location.isLocalLiveFile", location.isLiveFile(), is(false));
		
		String expected = "maven-project-2.0.10.jar!/META-INF/maven/org.apache.maven/maven-project/pom.properties";
		Assert.assertThat("location.uri", location.getUri().toASCIIString(), containsString(expected));
	}
}
