/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.stubs;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;

public class SelfProjectStub extends MavenProjectStub
{
	private List<Dependency> dependencies = new ArrayList<Dependency>();

	@SuppressWarnings("unchecked")
	public SelfProjectStub() {
		File pom = new File(getBasedir(), "pom.xml");
		MavenXpp3Reader pomReader = new MavenXpp3Reader();
		Model model = null;

		try {
			model = pomReader.read(new FileReader(pom));
			setModel(model);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		setGroupId(model.getGroupId());
		setArtifactId(model.getArtifactId());
		setVersion(model.getVersion());
		setName(model.getName());
		setUrl(model.getUrl());
		setPackaging(model.getPackaging());
		setFile(pom);

		if (model.getDependencies() != null) {
			dependencies.addAll(model.getDependencies());
		}
		
		if (model.getBuild() != null) {
			setBuild(model.getBuild());
		} else {
			setBuild(new Build());
		}

		File srcDir = new File(getBasedir(), toOS("src/main/java"));
		getBuild().setSourceDirectory(srcDir.getAbsolutePath());
		
		File targetDir = new File(getBasedir(), "target");
		getBuild().setDirectory(targetDir.getAbsolutePath());

		File outputDir = new File(targetDir, "classes");
		getBuild().setOutputDirectory(outputDir.getAbsolutePath());

		List<Resource> resources = new ArrayList<Resource>();
		resources.addAll(getBuild().getResources());
		
		// Only add resource dir if none are defined.
		if (resources.isEmpty()) {
			resources = new ArrayList<Resource>();
			Resource resource = new Resource();
			File resourceDir = new File(getBasedir(), toOS("src/main/resources"));
			resource.setDirectory(resourceDir.getAbsolutePath());
			resources.add(resource);
		} else {
			// Fix any relative resource paths.
			for(Resource resource: resources) {
				String resDir = toOS(resource.getDirectory());
				File dir = new File(resDir);
				if(!dir.isAbsolute()) {
					dir = new File(getBasedir(), resDir);
					resource.setDirectory(dir.getAbsolutePath());
				}
			}
		}
		getBuild().setResources(resources);
	}
	
	@Override
	public List<Dependency> getDependencies() {
		return dependencies;
	}
	
	private String toOS(String path) {
		return FilenameUtils.separatorsToSystem(path);
	}

	@Override
	public File getBasedir() {
		return MavenTestingUtils.getBasedir();
	}
}