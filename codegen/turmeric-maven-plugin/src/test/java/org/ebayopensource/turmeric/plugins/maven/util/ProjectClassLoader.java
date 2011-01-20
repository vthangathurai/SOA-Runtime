/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
import org.ebayopensource.turmeric.plugins.maven.AbstractTurmericMojo;

/**
 * Simple ClassLoader to use when testing Mojo's that need information from the project.
 * <p>
 * Such as the classpath information suitable for compiling classes, that some of the codegen utilities require.
 */
public class ProjectClassLoader extends URLClassLoader {

	public static ProjectClassLoader create(AbstractTurmericMojo mojo, ClassLoader parent) throws MalformedURLException {
		MavenProject project = mojo.getProject();
		URL urls[] = getMavenProjectClassLoaderURLS(project);
		return new ProjectClassLoader(urls, parent, project);
	}
	
	private static URL[] getMavenProjectClassLoaderURLS(MavenProject project) throws MalformedURLException {
		List<File> searchPaths = new ArrayList<File>();
		
		// Project Compile Artifacts
		@SuppressWarnings("unchecked")
		final List<Artifact> arts = project.getCompileArtifacts();
		if (arts != null) {
			for (Artifact arti : arts) {
				File artiFile = arti.getFile();
				if ((artiFile != null) && (artiFile.exists())) {
					searchPaths.add(artiFile);
				}
			}
		}
		
		// Project Resources
		@SuppressWarnings("unchecked")
		final List<Resource> resources = project.getBuild().getResources();
		
		for (Resource resource : resources) {
			String resDir = resource.getDirectory();
			File dir = new File(resDir);
			if (!dir.isAbsolute()) {
				dir = new File(project.getBasedir(), resDir);
			}
			searchPaths.add(dir);
		}

		// The Classes Dir
		File classesDir = new File(project.getBuild().getOutputDirectory());
		if (!classesDir.isAbsolute()) {
			classesDir = new File(project.getBasedir(), project.getBuild().getOutputDirectory());
		}

		searchPaths.add(classesDir);
		
		// Compile Source Roots - (needed for codegen javac)
		@SuppressWarnings("unchecked")
		List<String> sourceRoots = project.getCompileSourceRoots();
		if (sourceRoots != null) {
			for (String srcRoot : sourceRoots) {
				if (StringUtils.isBlank(srcRoot)) {
					// skip
					continue;
				}
				File src = new File(srcRoot);
				if (src.exists()) {
					searchPaths.add(new File(srcRoot));
				}
			}
		}

		int count = searchPaths.size();
		URL urls[] = new URL[count];
		for(int i=0; i<count; i++ ) {
			urls[i] = searchPaths.get(i).toURI().toURL();
			System.out.printf("### ProjectClassLoader[%d]: %s%n", i, urls[i].toExternalForm());
		}
		
		return urls;
	}
	
	private MavenProject project;
	
	public ProjectClassLoader(URL[] urls, ClassLoader parent, MavenProject project) {
		super(urls, parent);
		this.project = project;
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		// System.out.println("### " + name);
		if("com.sun.tools.javac.Main".equals(name)) {
			System.out.println("### JavaC being loaded. updating ProjectClassLoader urls.");
			/* The codegen is attempting to compile some stuff.
			 * 
			 * Time to reload the search Paths (because we might have 
			 * some more paths showing up as a result of being attached
			 * to the build)
			 */ 
			try {
				URL urls[] = getMavenProjectClassLoaderURLS(project);
				// Now, since we can't super.setUrls(urls), we have to add
				// what's missing instead.

				URL orig[] = getURLs();
				for (URL url : urls) {
					if (!contains(orig, url)) {
						System.out.println("### Adding New URL: " + url);
						super.addURL(url);
					}
				}

			} catch (MalformedURLException e) {
				e.printStackTrace(System.out);
			}
		}
		return super.loadClass(name);
	}

	private boolean contains(URL[] urls, URL testUrl) {
		for (URL url : urls) {
			if (url.equals(testUrl)) {
				return true;
			}
		}
		return false;
	}
}
