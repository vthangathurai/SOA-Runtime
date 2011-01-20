/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.resources;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarException;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

public class ResourceLocator {
	public static class Location {
		private URI uri;
		private File file;
		private boolean local;
		private boolean live;

		/**
		 * A reference to a resource
		 * 
		 * @param uri
		 *            the uri to the resource
		 * @param project
		 *            the maven project to work off of
		 * @throws IOException
		 */
		public Location(URI uri, MavenProject project) throws MojoExecutionException {
			this.uri = uri;
			this.live = "file".equals(uri.getScheme());
			if (this.live) {
				this.file = new File(uri);
				this.local = this.file.getAbsolutePath().startsWith(
						project.getBasedir().getAbsolutePath());
			} else {
				this.local = false;
				String filename = FilenameUtils.getName(uri.toASCIIString());
				File targetDir = new File(project.getBuild().getDirectory());
				this.file = new File(targetDir, filename);
				try {
					FileUtils.copyURLToFile(uri.toURL(), this.file);
				} catch (IOException e) {
					throw new MojoExecutionException(
							"Unable to copy resource URI [" + uri
									+ "] to temp location [" + file + "]", e);
				}
			}
		}

		/**
		 * A reference to a resource
		 * 
		 * @param file
		 *            the file reference to the resource
		 * @param project
		 *            the maven project to work off of
		 */
		public Location(File file, MavenProject project) {
			this.uri = file.toURI();
			this.live = true;
			if (this.live) {
				this.file = new File(uri);
				this.local = this.file.getAbsolutePath().startsWith(
						project.getBasedir().getAbsolutePath());
			}
		}
		
		@Override
		public String toString() {
			StringBuilder msg = new StringBuilder();
			msg.append("Location: ").append(uri.toString());
			msg.append(" [local:").append(local);
			msg.append(", live:").append(live);
			msg.append(", file:").append(file);
			msg.append("]");
			return msg.toString();
		}

		/**
		 * Get a file reference to the desired resource.
		 * <p>
		 * May or may not be the live file.
		 * <p>
		 * A requested resource that exist in the project's own tree will be referenced directly.<br>
		 * {@link #isLocalToProject()} will be true.<br>
		 * {@link #isLiveFile()} will be true.
		 * <p>
		 * A requested resource that exists outside of the project's own tree that can be referenced directly, will be.
		 * (This is the case for m2eclipse dependency references with "Workspace Resolution" enabled)
		 * {@link #isLocalToProject()} will be false.<br>
		 * {@link #isLiveFile()} will be true.
		 * <p>
		 * A requested resource found in a project dependency's artifact in the local repository, a copy of that desired
		 * resource will be copied into the project's own <code>/target/turmeric-maven-plugin-located-resources/</code>
		 * directory tree before a file reference to this copied resource is returned. {@link #isLocalToProject()} will
		 * be true.<br>
		 * {@link #isLiveFile()} will be false.<br>
		 * {@link #getUri()} will point to where the resource was fetched from.
		 * 
		 * @return the file from the live location, or from inside of the project target directory.
		 */
		public File getFile() {
			return file;
		}

		public URI getUri() {
			return uri;
		}

		public boolean isLocalToProject() {
			return local;
		}

		public boolean isLiveFile() {
			return live;
		}
	}
	
	private Log log;
	private MavenProject project;

	public ResourceLocator(Log log, MavenProject project) {
		this.log = log;
		this.project = project;
	}

	/**
	 * Attempt to find a resource in the project's configuration, and then the classpath.
	 * <p>
	 * Search Order:<br>
	 * <ol>
	 * <li>Project Defined Resource Directories</li>
	 * <li>${project.build.outputDirectory}</li>
	 * <li>Classpath</li>
	 * </ol>
	 * 
	 * @param pathref
	 *            the path to the resource desired. In the same format as you would use for
	 *            {@link ClassLoader#getResource(String)}
	 * @return the Location of the found resource, or null if not found.
	 * @throws MojoExecutionException
	 *             if unable to process resource loookup
	 */
	public Location findResource(String pathref) throws MojoExecutionException {
		if (StringUtils.isBlank(pathref)) {
			log.warn("Unable to lookup resource for null pathref: " + pathref);
			return null;
		}

		Location location = null;

		location = lookInProject(pathref);
		if (location != null) {
			return location;
		}

		location = lookInOutputDirectory(pathref);
		if (location != null) {
			return location;
		}

		location = lookInProjectCompileArtifacts(pathref);
		if (location != null) {
			return location;
		}

		location = lookInClasspath(pathref);
		if (location != null) {
			return location;
		}

		log.debug("NOT FOUND: resource: " + pathref);
		return null;
	}

	private Location lookInProjectCompileArtifacts(String pathref) throws MojoExecutionException {
		log.debug("Looking for resource in project compile artifacts: " + pathref);
		
		@SuppressWarnings("unchecked")
		List<Artifact> arts = project.getCompileArtifacts();
		if(arts == null) {
			log.debug("NOT FOUND in project compile artifacts: <no artifacts declared to look in>");
			return null;
		}
		
		if(log.isDebugEnabled()) {
			StringBuilder dbg = new StringBuilder();
		
			dbg.append("Project Compile Artifacts:");
			
			for(Artifact arti: arts) {
				dbg.append("\n  ");
				dbg.append(arti.getFile());
			}
			
			log.debug(dbg.toString());
		}
		
		URI uri = null;
		for(Artifact arti: arts) {
			uri = findFileResource(arti.getFile(), pathref);
			if(uri != null) {
				return new Location(uri, project);
			}
		}
		
		log.debug("NOT FOUND in project compile artifacts");
		return null;
	}

	private URI findFileResource(File path, String pathref) {
		if(path.isFile()) {
			// Assume its an archive.
			String extn = FilenameUtils.getExtension(path.getName());
			if(StringUtils.isBlank(extn)) {
				log.debug("No extension found for file: " + path);
				return null;
			}
			
			extn = extn.toLowerCase();
			if("jar".equals(extn)) {
				log.debug("looking inside of jar file: " + path);
				JarFile jar = null;
				try {
					jar = new JarFile(path);
					JarEntry entry = jar.getJarEntry(pathref);
					if(entry == null) {
						log.debug("JarEntry not found: " + pathref);
						return null;
					}
					
					String uripath = String.format("jar:%s!/%s", path.toURI(), entry.getName());
					try {
						return new URI(uripath);
					} catch (URISyntaxException e) {
						log.debug("Unable to create URI reference: " + path, e);
						return null;
					} 
				} catch (JarException e) {
					log.debug("Unable to open archive: " + path, e);
					return null;
				} catch (IOException e) {
					log.debug("Unable to open archive: " + path, e);
					return null;
				} finally {
					if (jar != null) {
						try {
							jar.close();
						} catch (IOException e) {
							log.debug("Unable to close jar: " + path, e);
						}
					}
				}
			}
			
			log.debug("Unsupported archive file: " +  path);
			return null;
		}
		
		if (path.isDirectory()) {
			File testFile = new File(path, FilenameUtils.separatorsToSystem(pathref));
			if (testFile.exists() && testFile.isFile()) {
				return testFile.toURI();
			}

			log.debug("Not found in directory: " + testFile);
			return null;
		}
		
		log.debug("Unable to handle non-file, non-directory " + File.class.getName() + " objects: " + path);
		return null;
	}

	private Location lookInClasspath(String pathref) throws MojoExecutionException {
		log.debug("Looking for resource in project classpath: " + pathref);
		
		if (log.isDebugEnabled()) {
			StringBuilder dbg = new StringBuilder();
			dbg.append("System.getProperty('java.class.path')=");

			String rawcp = System.getProperty("java.class.path");
			for (String cp : rawcp.split(File.pathSeparator)) {
				dbg.append("\n  ").append(cp);
			}

			log.debug(dbg.toString());

			ClassLoader cl = this.getClass().getClassLoader();
			if (cl instanceof URLClassLoader) {
				dbg = new StringBuilder();
				dbg.append("URLClassLoader(");
				dbg.append(cl.getClass().getName());
				dbg.append("):");
				
				URLClassLoader ucl = (URLClassLoader) cl;

				for (URL url : ucl.getURLs()) {
					dbg.append("\n  ").append(url.toExternalForm());
				}

				log.debug(dbg.toString());
			}
		}
		
		List<URL> resources = new ArrayList<URL>();
		try {
			Enumeration<URL> enurls = ClassLoader.getSystemResources(pathref);
			if (enurls != null) {
				while (enurls.hasMoreElements()) {
					URL url = enurls.nextElement();
					if(!resources.contains(url)) {
						resources.add(url);
					}
				}
			}
	
			addFoundResource(resources, pathref, Thread.currentThread().getContextClassLoader());
			addFoundResource(resources, pathref, this.getClass().getClassLoader());
			if (resources.isEmpty()) {
				log.debug("NOT FOUND in project classpath");
				return null;
			}

			if (resources.size() > 1) {
				log.warn("Found more than 1 classpath entry for: " + pathref);
				for (URL url : resources) {
					log.warn(" + " + url.toExternalForm());
				}
			}

			URI uri = resources.get(0).toURI();
			log.debug("FOUND resource in project classpath: " + uri);
			return new Location(uri, project);
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to process resource lookup in project classpath: " + e.getMessage(), e);
		} catch (URISyntaxException e) {
			throw new MojoExecutionException("Unable to process resource lookup in project classpath: " + e.getMessage(), e);
		}
	}

	private void addFoundResource(List<URL> resources, String pathref, ClassLoader cl) throws IOException {
		log.debug("Looking in ClassLoader: " + cl);
		Enumeration<URL> enurls = cl.getResources(pathref);
		if (enurls != null) {
			while (enurls.hasMoreElements()) {
				URL url = enurls.nextElement();
				if(!resources.contains(url)) {
					resources.add(url);
				}
			}
		}
	}

	private Location lookInOutputDirectory(String pathref) {
		File outputDir = new File(project.getBuild().getOutputDirectory());
		log.debug("Looking for resource in project output directory ["
				+ outputDir + "]: " + pathref);

		File testFile = new File(outputDir, toOS(pathref));
		if (testFile.exists()) {
			log.debug("FOUND resource in project output directory: " + testFile);
			return new Location(testFile, project);
		}

		log.debug("NOT FOUND in project output directory");
		return null;
	}

	private Location lookInProject(String pathref) {
		List<?> resources = project.getBuild().getResources();
		log.debug("Looking for resource in [" + resources.size()
				+ "] directories: " + pathref);
		Iterator<?> iter = resources.iterator();
		while (iter.hasNext()) {
			Resource resource = (Resource) iter.next();
			String resDir = resource.getDirectory();
			if (StringUtils.isBlank(resDir)) {
				log.warn("Blank resources directory: " + resource);
				continue;
			}
			File dir = new File(resDir);
			log.debug("Testing for resource in dir: " + dir);
			File possiblePath = new File(dir, pathref);
			if (possiblePath.exists()) {
				log.debug("FOUND resource: " + possiblePath);
				return new Location(possiblePath, project);
			}
		}
		log.debug("NOT FOUND in project resource directories");
		return null;
	}

	private String toOS(String path) {
		return FilenameUtils.separatorsToSystem(path);
	}
}
