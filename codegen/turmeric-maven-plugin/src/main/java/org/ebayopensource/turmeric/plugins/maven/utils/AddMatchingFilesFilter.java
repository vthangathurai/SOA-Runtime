/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.logging.Log;

public class AddMatchingFilesFilter implements FileFilter {
	private Log log;
	private File basedir;
	private String basedirname;
	private int basedirnamelength;
	private List<File> hits;
	private Pattern pattern;

	public AddMatchingFilesFilter(Log log, File basedir, Pattern pattern,
			List<File> hits) {
		this.log = log;
		this.basedir = basedir;
		this.basedirname = this.basedir.getAbsolutePath() + File.separator;
		this.basedirnamelength = this.basedirname.length();
		this.pattern = pattern;
		this.hits = hits;
	}

	@Override
	public boolean accept(File pathname) {
		String relativePath = pathname.getAbsolutePath().substring(basedirnamelength);
		relativePath = FilenameUtils.separatorsToUnix(relativePath);
		String dbg = "miss";
		if (pattern.matcher(relativePath).matches()) {
			dbg = "HIT ";
			hits.add(pathname);
		}

		log.debug(dbg + " on pattern [" + pattern.pattern() + "]: " + relativePath);
		return true;
	}

}