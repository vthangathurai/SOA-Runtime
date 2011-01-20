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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;

/**
 * Rule to check that the specified paths do not exist.
 */
public class PathMustNotExistRule implements Rule {
	private File baseDir;
	private String basePath;
	private Map<String,String> checks = new HashMap<String, String>();
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
		int delim = line.indexOf('|');
		String path, msg;
		
		if(delim > 0) {
			path = line.substring(0,delim).trim();
			msg = line.substring(delim+1).trim();
		} else {
			path = line.trim();
			msg = "Indicates codegen bug";
		}
		
		checks.put(path, msg);
	}

	@Override
	public List<String> verify() {
		LinkedList<String> failures = new LinkedList<String>();
		
		for (Entry<String, String> entry : checks.entrySet()) {
			String checkPath = FilenameUtils.separatorsToSystem(entry.getKey());
			File testPath = new File(baseDir, checkPath);
			if (testPath.exists()) {
				failures.add(String.format("  %s : Path should not exist (%s)",
						checkPath, entry.getValue()));
			}
		}
		
		if (failures.size() > 0) {
			failures.add(0, String.format("[%s: %s] Failures - %s",
					this.getClass().getSimpleName(), basePath, baseDir));
		}

		return failures;
	}

}
