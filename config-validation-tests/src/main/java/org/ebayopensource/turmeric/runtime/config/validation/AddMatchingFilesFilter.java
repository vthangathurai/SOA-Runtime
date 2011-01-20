/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.config.validation;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

public class AddMatchingFilesFilter implements FileFilter {
	private PathRegex pathregex;
	private List<File> hits;

	public AddMatchingFilesFilter(PathRegex regex, List<File> hits) {
	    this.pathregex = regex;
		this.hits = hits;
	}

	@Override
	public boolean accept(File pathname) {
		if (pathregex.matches(pathname)) {
			hits.add(pathname);
		}
		return true;
	}
}