/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.apache.commons.io.FilenameUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.ServiceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;


public final class CodeGenClassLoader extends URLClassLoader {

	private static CallTrackingLogger s_logger = LogManager
			.getInstance(CodeGenClassLoader.class);

	private static CallTrackingLogger getLogger() {
		return s_logger;
	}

	/**
	 * List of package prefixes we want to mask the parent classLoader from
	 * loading
	 */
	private final String[] m_inclPackagePrefixes;

	/**
	 * List of package prefixes we want parent classLoader to load
	 */
	private final String[] m_exclPackagePrefixes;

	/**
	 * List of classes we want parent classLoader to load
	 */
	private final String[] m_exclClasses;

	private ArrayList<URL> m_jarURLs = new ArrayList<URL>();

	private ArrayList<URL> m_dirURLs = new ArrayList<URL>();

	private ArrayList<URL> m_classPathURLs = new ArrayList<URL>();

	private ClassLoader peerClassLoader = null;

	private ClassLoader parentClassLoader = null;

	public CodeGenClassLoader(ClassLoader parentClassLoader,
			ClassLoader peerClassLoader, String[] inclPackagePrefixes,
			String[] exclPackagePrefixes, String[] exclClasses)
			throws CodeGenFailedException {

		super(new URL[0], parentClassLoader);
		this.peerClassLoader = peerClassLoader;
		this.parentClassLoader = parentClassLoader;

		addUrlsFromClassLoader(parentClassLoader);
		addUrlsFromClassLoader(peerClassLoader);
		for (URL dirURL : m_dirURLs) {
			addURL(dirURL);
		}
		m_classPathURLs.addAll(m_jarURLs);

		URL[] toolsJarURLs = getToolsJar(parentClassLoader);

		if (toolsJarURLs.length == 0) {
			// if tools.jar was found in our classloader, no need to create
			// a parallel classloader
			m_inclPackagePrefixes = new String[0];
			m_exclPackagePrefixes = new String[0];
			m_exclClasses = new String[0];
		} else {
			m_inclPackagePrefixes = inclPackagePrefixes;
			m_exclPackagePrefixes = exclPackagePrefixes;
			m_exclClasses = exclClasses;
		}

		logDebugMessageForClassLoader(inclPackagePrefixes, exclPackagePrefixes,
				m_classPathURLs, toolsJarURLs.length);

	}

	public void addURL(URL url) {
		try {
			super.addURL(url.toURI().toURL());
			m_classPathURLs.add(url);
		} catch (Exception ex) {
		}
	}

	public Class<?> loadClass(String className) throws ClassNotFoundException {
		Class<?> clazz = null;
		try {
			if (startsWithPrefix(m_exclClasses, className)
					|| startsWithPrefix(m_exclPackagePrefixes, className)) {
				// we need to load those classes parent class loader
				// by delegation.
				clazz = super.loadClass(className);
			} else if (startsWithPrefix(m_inclPackagePrefixes, className)) {
				// we need to load those classes in this class loader
				// without delegation.
				clazz = findClass(className);

			} else {
				// Deletage class loading to parent classloader

				clazz = super.loadClass(className);
			}
		} catch (ClassNotFoundException classNotFoundException) {

		}


		if (clazz == null) {
			String msg = "Codegen Failed to resolve it, peerClassLoader is "
					+ peerClassLoader + " and parent classLoader is "
					+ parentClassLoader;
			throw new ClassNotFoundException(className + " \n " + msg);
		}
		return clazz;
	}

	protected Class<?> findClass(String name) throws ClassNotFoundException {

		Class<?> loadedClass = findLoadedClass(name);
		if (loadedClass != null) {
			return loadedClass;
		}

		StringBuilder sb = new StringBuilder(name.length() + 6);
		sb.append(name.replace('.', '/')).append(".class");

		InputStream is = getResourceAsStream(sb.toString());
		if (is == null)
			throw new ClassNotFoundException("Class not found" + sb);

		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) >= 0)
				baos.write(buf, 0, len);

			buf = baos.toByteArray();

			// define package if not defined yet
			int i = name.lastIndexOf('.');
			if (i != -1) {
				String pkgname = name.substring(0, i);
				Package pkg = getPackage(pkgname);
				if (pkg == null)
					definePackage(pkgname, null, null, null, null, null, null,
							null);
			}

			return defineClass(name, buf, 0, buf.length);
		} catch (IOException e) {
			throw new ClassNotFoundException(name, e);
		} finally {
			CodeGenUtil.closeQuietly(is);
			CodeGenUtil.closeQuietly(baos);
		}
	}

	public URL findResource(String resourceName) {
		for (URL url : m_jarURLs) {

			JarFile jarFile = null;
			try {
				File file = CodeGenUtil.urlToFile(url);
				jarFile = new JarFile(file);
				JarEntry jarEntry = jarFile.getJarEntry(resourceName);
				if(jarEntry == null) {
					continue; // Skip, not part of this jar.
				}
				// Java supports "jar:" url references natively.
				return new URL(String.format("jar:%s!/%s", file.toURI().toASCIIString(), jarEntry.getName()));
			} catch (IOException e) {
				e.printStackTrace(); // KEEPME
			} catch (Exception e) {
				/* ignore */
			} finally {
				CodeGenUtil.closeQuietly(jarFile);
			}

		}
		return super.findResource(resourceName);
	}

	
	public URL getURLOfJarFileWithASpecifiedName(String  fileName){

		URL result = null;
		
		for(URL url : m_jarURLs){
			String jarPath = url.getFile();
			
			if(jarPath.endsWith(fileName)){
				result = url;
				break;
			}

		}
		
		return result;
	}
	
	@Override
	public URL[] getURLs() {
		return m_classPathURLs.toArray(new URL[0]);
	}

	public URL[] getAllURLs() {
		List<URL> allUrlList = new ArrayList<URL>();
		allUrlList.addAll(m_classPathURLs);
		if (parentClassLoader != null
				&& parentClassLoader instanceof URLClassLoader) {
			allUrlList.addAll(Arrays
					.asList(((URLClassLoader) parentClassLoader).getURLs()));
		}
		if (peerClassLoader != null
				&& peerClassLoader instanceof URLClassLoader) {
			allUrlList.addAll(Arrays.asList(((URLClassLoader) peerClassLoader)
					.getURLs()));
		}

		URL[] urlArray = new URL[0];
		getLogger().log(Level.FINE, "**** start of  getAllURLs *****");
		getLogger().log(Level.FINE,
				Arrays.toString(allUrlList.toArray(urlArray)));
		getLogger().log(Level.FINE, "**** end of  getAllURLs *****");

		return allUrlList.toArray(new URL[0]);
	}

	private void logDebugMessageForClassLoader(String[] inclPackagePrefixes,
			String[] exclPackagePrefixes, ArrayList<URL> paramClassPathURLs,
			int toolsJarURLsLength) {

		if (toolsJarURLsLength == 0)
			getLogger()
					.log(Level.FINE,
							"**** The included and excluded packages are NOT relevant *****");
		else {
			getLogger()
					.log(Level.FINE,
							"**** The included and excluded packages are relevant *****");

			getLogger().log(Level.FINE,
					"**** start of  included package prefixes *****");
			getLogger().log(Level.FINE, Arrays.toString(inclPackagePrefixes));
			getLogger().log(Level.FINE,
					"**** end of  included package prefixes *****");

			getLogger().log(Level.FINE,
					"**** start of  excluded package prefixes *****");
			getLogger().log(Level.FINE, Arrays.toString(exclPackagePrefixes));
			getLogger().log(Level.FINE,
					"**** end of  excluded package prefixes *****");

		}

		URL[] urlArray = new URL[0];
		getLogger().log(Level.FINE, "**** start of  m_classPathURLs *****");
		getLogger().log(Level.FINE,
				Arrays.toString(paramClassPathURLs.toArray(urlArray)));
		getLogger().log(Level.FINE, "**** end of  m_classPathURLs *****");
	}

	private static URL[] getAllURLs(ClassLoader parentClassLoader)
			throws CodeGenFailedException {
		URL[] parentClasspathURLs = new URL[0];
		if (parentClassLoader instanceof URLClassLoader) {
			parentClasspathURLs = ((URLClassLoader) parentClassLoader)
					.getURLs();
		}

		URL[] toolsJarURLs = getToolsJar(parentClassLoader);

		URL[] allURLs = new URL[parentClasspathURLs.length
				+ toolsJarURLs.length];

		System.arraycopy(toolsJarURLs, 0, allURLs, 0, toolsJarURLs.length);
		System.arraycopy(parentClasspathURLs, 0, allURLs, toolsJarURLs.length,
				parentClasspathURLs.length);

		return allURLs;
	}

	private boolean startsWithPrefix(String[] srcArray, String inputStr) {
		boolean startsWithPrefix = false;
		for (String prefix : srcArray) {
			if (inputStr.startsWith(prefix) || inputStr.equals(prefix)) {
				startsWithPrefix = true;
				break;
			}
		}

		return startsWithPrefix;
	}

	/**
	 * Returns a class loader that can load classes from JDK tools.jar.
	 * 
	 * @param parentClassLoader
	 */
	private static URL[] getToolsJar(ClassLoader parent)
			throws CodeGenFailedException {
		
		// @formatter:off
		String expectedClasses[] = {
			"com.sun.tools.javac.Main",
			"com.sun.tools.apt.Main",
			"com.sun.javadoc.Doclet"
		};
		// @formatter:on
		
		// If all of the expected classes are present already
		// then just return the active classloader.
		boolean foundExpected = true;
		
		for (String expectedClass : expectedClasses) {
			try {
				Class.forName(expectedClass, false, parent);
			} catch (ClassNotFoundException e) {
				foundExpected = false;
				break;
			}
		}
		
		if (foundExpected) {
			// we can already load them in the parent class loader.
			// so no need to look for tools.jar.
			// this happens when we are run inside IDE/Ant, or
			// in Mac OS.
			return new URL[0];
		}
		
		// Attempt to find the tools.jar near the java.home
		
		// The search paths for the tools.jar
		// @formatter:off
		String searchPaths[] = {
			"tools.jar",
			"lib/tools.jar",
			"../lib/tools.jar"
		};
		// @formatter:on

		// Start with the jdkHome
		File jdkHome = new File(System.getProperty("java.home"));
		if (!CodeGenUtil.isEmptyString(ServiceGenerator.s_JdkHome)) {
			jdkHome = new File(ServiceGenerator.s_JdkHome);
			s_logger.log(Level.INFO, "JdkHome being used for classloader is :"
					+ jdkHome);
		}
		
		// The search process
		File toolsJar = null;
		for(String searchPath: searchPaths) {
			File possible = new File(jdkHome, FilenameUtils.separatorsToSystem(searchPath));
			if(possible.exists()) {
				toolsJar = possible;
				break;
			}
		}

		// Not found. can't codegen!
		if(toolsJar == null) {
			StringBuilder msg = new StringBuilder();
			msg.append("Failed(2) to load tools.jar: Are you running with a JDK?");
			msg.append("\nSystem.ENV(JAVA_HOME)=").append(System.getenv("JAVA_HOME"));
			msg.append("\nSystem.ENV(JDK_HOME)=").append(System.getenv("JDK_HOME"));
			msg.append("\nSystem.property(java.home)=").append(System.getProperty("java.home"));
			throw new CodeGenFailedException(msg.toString());
		}

		try {
			return new URL[] { toolsJar.toURI().toURL() };
		} catch (MalformedURLException e) {
			throw new CodeGenFailedException(
					"MalformedURLException from the tools.jar location: "
							+ toolsJar.getAbsolutePath(), e);
		}
	}

	private void addUrlsFromClassLoader(ClassLoader urlClassLoader)
			throws CodeGenFailedException {
		URL allURLs[] = getAllURLs(urlClassLoader);
		for (int i = 0; i < allURLs.length; i++) {

			try {
				File file = new File(allURLs[i].toURI());
				if(file.isFile()) {
					m_jarURLs.add(allURLs[i]);
				} else {
					m_dirURLs.add(allURLs[i]);
				}
			} catch (Exception e) {
			}
		}
	}
}
