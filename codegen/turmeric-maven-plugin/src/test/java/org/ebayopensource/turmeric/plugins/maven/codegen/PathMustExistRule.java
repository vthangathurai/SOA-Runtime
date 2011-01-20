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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;

public class PathMustExistRule implements Rule {
	private File baseDir;
	private String basePath;
	private List<String> checks = new ArrayList<String>();
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
		checks.add(line.trim());
	}

	@Override
	public List<String> verify() {
		LinkedList<String> failures = new LinkedList<String>();
		
		if (!baseDir.exists()) {
			failures.add("  Base Directory Not Found");
		}

		for (String checkPath: checks) {
			checkPath = FilenameUtils.separatorsToSystem(checkPath);
			File testPath = new File(baseDir, checkPath);
			if (!testPath.exists()) {
				failures.add(String.format(
						"  %s : Expected Path not found", checkPath));
			}
		}
		
		if (failures.size() > 0) {
			failures.add(0, String.format("[%s: %s] Failures - %s",
					this.getClass().getSimpleName(), basePath, baseDir));
		}

		return failures;
	}

}
