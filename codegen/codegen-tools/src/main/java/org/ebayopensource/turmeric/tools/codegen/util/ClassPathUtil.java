/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;

import com.ebay.kernel.util.StringUtils;

public class ClassPathUtil {
	private static final Logger LOG = Logger.getLogger(ClassPathUtil.class
			.getName());
	
	private static AtomicLong cacheHits = new AtomicLong(0);
	private static AtomicLong cacheMisses = new AtomicLong(0);

	/**
	 * The {@link #getJarClassPathRefs(File)} call is expensive, so lets
	 * cache its results.
	 */
	private static ConcurrentMap<File, List<File>> jarClassPathCache = new ConcurrentHashMap<File, List<File>>();

	/**
	 * Append to a StringBuilder the classpath obtained via the {@link #getClassPath()} method.
	 * 
	 * @param builder
	 *            the builder to append to.
	 * @param classpath
	 *            the classpath obtained.
	 */
	public static void appendClasspath(StringBuilder builder,
			List<File> classpath) {
		boolean delim = false;
		for (File file : classpath) {
			if (delim) {
				builder.append(File.pathSeparator);
			}
			builder.append(file.getAbsolutePath());
			delim = true;
		}
	}

	/**
	 * Walk the {@link ClassLoader} heirarchy, starting with {@link Thread#getContextClassLoader()} to obtain the list
	 * of active file paths present, for later use by various tooling such as JavaC, Axis2, Jaxb, etc.
	 * <p>
	 * Method is smart enough to see <code>Class-Path</code> attribute entries that may be present inside of a Jar
	 * file's <code>META-INF/MANIFEST.MF</code> resource.
	 * 
	 * @return the List of File resources detected within the ClassLoader heirarchy
	 */
	public static LinkedList<File> getClassPath() {
		LinkedList<File> classpath = new LinkedList<File>();

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();

		getClassPathFromClassLoader(classpath, classLoader);

		return classpath;
	}
	
	public static void dumpStats() {
		LOG.finer("Cache Size: " + jarClassPathCache.size());
		LOG.finer("Cache Hits: " + cacheHits.get());
		LOG.finer("Cache Misses: " + cacheMisses.get());
	}

	private static void getClassPathFromClassLoader(LinkedList<File> classpath,
			ClassLoader classLoader) {
		if (classLoader == null) {
			return; // no classloader
		}

		if (!(classLoader instanceof URLClassLoader)) {
			// unable to get anything from this classloader.
			// Try parent instead.
			getClassPathFromClassLoader(classpath, classLoader.getParent());
			return;
		}

		URLClassLoader ucl = (URLClassLoader) classLoader;

		URL[] urls = null;
		if (ucl instanceof CodeGenClassLoader) {
			CodeGenClassLoader cgcl = (CodeGenClassLoader) ucl;
			urls = cgcl.getAllURLs();
		} else {
			urls = ucl.getURLs();
		}

		// Add the urls
		File file;
		String path;
		for (URL url : urls) {
			// Normalize the path
			try {
				file = new File(url.toURI());
			} catch (URISyntaxException e) {
				LOG.warning("Unable to identify file from invalid URI: "
						+ url.toExternalForm());
				path = url.toExternalForm();
				if (path.startsWith("file:")) {
					path = path.substring("file:".length());
					path = FilenameUtils.normalize(path);
				}
				file = new File(path);
			}
			addFilePath(classpath, file);
		}

		getClassPathFromClassLoader(classpath, classLoader.getParent());
		dumpStats();
	}

	private static void addFilePath(LinkedList<File> classpath, File file) {
		if (classpath.contains(file)) {
			// Already seen, skip it.
			return;
		}
		classpath.add(file);

		if (isArchive(file)) {
			List<File> refs = jarClassPathCache.get(file);
			if(refs == null) {
				cacheMisses.incrementAndGet();
				refs = getJarClassPathRefs(file);
				jarClassPathCache.put(file, refs);
			} else {
				cacheHits.incrementAndGet();
			}
			
			for(File ref: refs) {
				addFilePath(classpath, ref);
			}
		}
	}

	private static List<File> getJarClassPathRefs(File file) {
		List<File> refs = new ArrayList<File>();
		
		JarFile jar = null;
		try {
			jar = new JarFile(file);
			Manifest manifest = jar.getManifest();
			if (manifest == null) {
				// No manifest, no classpath.
				return refs;
			}

			Attributes attrs = manifest.getMainAttributes();
			if (attrs == null) {
				/*
				 * No main attributes. (not sure how that's possible, but we can skip this jar)
				 */
				return refs;
			}
			String classPath = attrs.getValue(Attributes.Name.CLASS_PATH);
			if (CodeGenUtil.isEmptyString(classPath)) {
				return refs;
			}

			String parentDir = FilenameUtils.getFullPath(file.getAbsolutePath());
			File possible;
			for (String path : StringUtils.splitStr(classPath, ' ')) {
				possible = new File(path);

				if (!possible.isAbsolute()) {
					// relative path?
					possible = new File(FilenameUtils.normalize(parentDir
							+ path));
				}
				
				if(!refs.contains(possible)) {
					refs.add(possible);
				}
			}
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Unable to load/read/parse Jar File: "
					+ file.getAbsolutePath(), e);
		} finally {
			CodeGenUtil.closeQuietly(jar);
		}
		
		return refs;
	}

	private static boolean isArchive(File file) {
		if (!file.isFile()) {
			return false;
		}

		String filename = file.getName();
		if (CodeGenUtil.isEmptyString(filename)) {
			return false;
		}

		return (filename.toLowerCase().endsWith(".jar"));
	}
}
