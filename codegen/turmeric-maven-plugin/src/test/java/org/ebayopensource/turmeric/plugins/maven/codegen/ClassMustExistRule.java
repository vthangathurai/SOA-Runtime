/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.codegen;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;

public class ClassMustExistRule implements Rule {
	private File baseDir;
	private String basePath;
	/**
	 * The classes to check. Map.Key is raw className, Map.Value is the os relative path to the java file.
	 */
	private Map<String, String> sources = new TreeMap<String, String>();
	private Interpolator interpolator;

	@Override
	public void setInterpolator(Interpolator interpolator) {
		this.interpolator = interpolator;
	}

	@Override
	public void setArguments(String... args) {
		if (args == null) {
			throw new IllegalArgumentException(
					"Expected 1 argument, but got a null");
		}
		if (args.length != 1) {
			throw new IllegalArgumentException(
					"Expected only 1 argument, but got " + args.length);
		}
		basePath = args[0];
		try {
			baseDir = new File(interpolator.interpolate(basePath));
		} catch (InterpolationException e) {
			throw new IllegalArgumentException("Unable to expand basePath: " + basePath);
		}
	}

	@Override
	public void addCheck(String line) {
		String className = line.trim();
		String rawPath = className.replace('.', '/') + ".java";
		sources.put(className, rawPath);
	}

	@Override
	public List<String> verify() {
		LinkedList<String> failures = new LinkedList<String>();
		
		if (!baseDir.exists()) {
			failures.add("  Base Directory Not Found");
		}

		for (Entry<String, String> entry : sources.entrySet()) {
			String checkPath = FilenameUtils.separatorsToSystem(entry
					.getValue());
			File testPath = new File(baseDir, checkPath);
			if (!testPath.exists()) {
				failures.add(String.format("  %s : Expected Class not found",
						entry.getKey()));
			}
		}
		
		if (failures.size() > 0) {
			failures.add(0, String.format("[%s: %s] Failures - %s",
					this.getClass().getSimpleName(), basePath, baseDir));
		}

		return failures;
	}

}
