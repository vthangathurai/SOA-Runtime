/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.ebayopensource.turmeric.junit.rules.TestingDir;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;

/**
 * Utility for mananging content from the Test Resources Dir.
 */
public class TestResourceUtil {
	/**
	 * Get the project specified test resources dir.
	 * 
	 * @return
	 */
	public static File getDir() {
		return MavenTestingUtils.getProjectDir("src/test/resources");
	}

	/**
	 * Gets a required resource file from the test resources dir.
	 * 
	 * @param path
	 *            the path to fetch (Note: use "/" always as this argument, the internal implementation will adjust the
	 *            path accordingly on all systems)
	 * @return the resource required (will throw an assertion failure if the resource does not exist)
	 */
	public static File getResource(String path) {
		String syspath = FilenameUtils.separatorsToSystem(path);
		File resource = new File(TestResourceUtil.getDir(), syspath);
		PathAssert.assertFileExists(resource);
		return resource;
	}
	
	/**
	 * Gets a required resource dir from the test resources dir.
	 * 
	 * @param path
	 *            the path to fetch (Note: use "/" always as this argument, the internal implementation will adjust the
	 *            path accordingly on all systems)
	 * @return the resource required (will throw an assertion failure if the resource does not exist)
	 */
	public static File getResourceDir(String path) {
		String syspath = FilenameUtils.separatorsToSystem(path);
		File resource = new File(TestResourceUtil.getDir(), syspath);
		PathAssert.assertDirExists(resource);
		return resource;
	}

	/**
	 * Copy a test resource into the {@link TestingDir} managed location
	 * 
	 * @param path
	 *            the path to the resource (will be reused as the output path in the {@link TestingDir#getDir()} +
	 *            testingDirPath)
	 * @param testingdir
	 *            the test specific testing dir
	 * @param testingDirPath
	 *            the path within the test specific testing dir to use
	 * @return the destination file that was copied
	 * @throws IOException
	 */
	public static File copyResource(String path, TestingDir testingdir,
			String testingDirPath) throws IOException {
		File destBaseDir = testingdir.getFile(FilenameUtils
				.separatorsToSystem(testingDirPath));
		MavenTestingUtils.ensureDirExists(destBaseDir);
		File resource = getResource(path);
		String filename = resource.getName();
		String destRelPath = path.substring(0,
				path.length() - filename.length());
		String destPath = FilenameUtils.normalize(destRelPath + "/" + filename);
		File destFile = new File(destBaseDir,
				FilenameUtils.separatorsToSystem(destPath));
		FileUtils.copyFile(resource, destFile);
		return destFile;
	}

	/**
	 * Copy a test resource to the specified file.
	 * 
	 * @param path
	 *            the path to the resource.
	 * @param destFile
	 *            the destination file
	 * @return the destinatino file that was copied
	 * @throws IOException
	 */
	public static File copyResource(String path, File destFile) throws IOException {
		File resource = getResource(path);
		FileUtils.copyFile(resource, destFile);
		return destFile;
	}

	/**
	 * Copy a test resource directory tree into the destination {@link TestingDir#getDir()}.
	 * 
	 * @param path
	 *            the test resource directory
	 * @param testingdir
	 *            the destination directory
	 * @return the testingdir copied into
	 * @throws IOException
	 */
	public static File copyResourceRootDir(String path, TestingDir testingdir)
			throws IOException {
		File dir = getResourceDir(path);
		File destDir = testingdir.getDir();
		MavenTestingUtils.ensureEmpty(testingdir);
		FileUtils.copyDirectory(dir, destDir);
		return destDir;
	}
}
