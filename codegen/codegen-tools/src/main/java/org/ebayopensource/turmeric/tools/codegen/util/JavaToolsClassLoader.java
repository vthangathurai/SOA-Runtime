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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.io.FilenameUtils;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;


/**
 * Work in Progress.
 * Potential replacement for {@link CodeGenClassLoader}
 */
public class JavaToolsClassLoader extends URLClassLoader {
	public static ClassLoader createIfNeeded() throws CodeGenFailedException {
		ClassLoader parent = Thread.currentThread().getContextClassLoader();
		
		if(!needToolsJar(parent)) {
			return parent;
		}
		
		File toolsJar = findToolsJar();
		
		try {
			// Convert the tools.jar location to a URL and use it!
			URL urls[] = { toolsJar.toURI().toURL() };
			return new JavaToolsClassLoader(urls, parent);
		} catch (MalformedURLException e) {
			throw new CodeGenFailedException(
					"MalformedURLException from the tools.jar location: "
							+ toolsJar.getAbsolutePath(), e);
		}
	}
	
	public static boolean needToolsJar(ClassLoader classloader) {
		
		// @formatter:off
		String expectedClasses[] = {
			"com.sun.tools.javac.Main",
			"com.sun.tools.apt.Main",
			"com.sun.javadoc.Doclet",
			"com.sun.javadoc.Type"
		};
		// @formatter:on
		
		// If all of the expected classes are present already
		// then just return the active classloader.
		boolean foundExpected = true;
		
		for (String expectedClass : expectedClasses) {
			try {
				classloader.loadClass(expectedClass);
			} catch (ClassNotFoundException e) {
				foundExpected = false;
				break;
			}
		}
		
		return !foundExpected;
	}
	
	public static File findToolsJar() throws CodeGenFailedException {
		// Attempt to find the tools.jar near the java.home
		
		// The search paths for the tools.jar
		// @formatter:off
		String searchPaths[] = {
			"tools.jar",
			"lib/tools.jar",
			"../lib/tools.jar",
			"../Classes/classes.jar"
		};
		// @formatter:on
		
		// The search process
		File jdkHome = new File(System.getProperty("java.home"));
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
		
		return toolsJar;
	}
	
	public JavaToolsClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
}
