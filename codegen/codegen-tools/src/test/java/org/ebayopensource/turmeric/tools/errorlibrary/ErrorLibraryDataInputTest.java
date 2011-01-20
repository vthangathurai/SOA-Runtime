/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.errorlibrary;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.AbstractCodegenTestCase;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class ErrorLibraryDataInputTest extends AbstractCodegenTestCase {
	/**
	 * Null input for -pr
	 */
	@Test
	public void testNullInputForProjectRoot() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");
		
        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", null, 
			"-domain", "runtime", 
			"-errorlibname","TestErrorLibrary"
		};
        // @formatter:on
		
		try {
			performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "provide a proper value for the option -pr";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * Null input for -Domain
	 */
	@Test
	public void testNullInputForDomain() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", projDir.getAbsolutePath(), 
			"-domain", null, 
			"-errorlibname","TestErrorLibrary",
			"-dest", projDir.getAbsolutePath() 
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "provide a proper value for the option -domain";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * Null input for -dest
	 */
	@Test
	public void testNullInputForDestination() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", projDir.getAbsolutePath(), 
			"-domain", "runtime",
			"-errorlibname","TestErrorLibrary",
			"-dest", null 
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "provide a proper value for the option -dest";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * Empty string passed as gentype
	 */
	@Test
	public void testEmptyStringInputToGentype() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", " ", 
			"-pr", projDir.getAbsolutePath(), 
			"-domain", "runtime", 
			"-errorlibname","TestErrorLibrary",
			"-dest", projDir.getAbsolutePath() 
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "Please provide a proper value for the option -gentype";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * Empty string passed as projectRoot
	 */
	@Test
	public void testEmptyStringInputToProjectRoot() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", " ",
			"-domain", "runtime",
			"-errorlibname","TestErrorLibrary",
			"-dest", projDir.getAbsolutePath() 
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "Please provide a proper value for the option -pr";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * Empty string passed as Domain
	 */
	@Test
	public void testEmptyStringInputToDomain() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");

        // @formatter:off
		String[] inputArgs = {
			"-gentype", "genTypeDataCollection",
			"-pr", projDir.getAbsolutePath(), 
			"-domain", " ",
			"-errorlibname","TestErrorLibrary",
			"-dest", projDir.getAbsolutePath()
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "Please provide a proper value for the option -domain";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * Empty string passed as destination
	 */
	@Test
	public void testEmptyStringInputToDest() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", projDir.getAbsolutePath(), 
			"-domain", "runtime",
			"-errorlibname","TestErrorLibrary",
			"-dest", " " 
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "Please provide a proper value for the option -dest";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * Pass no input for gentype
	 */
	@Test
	public void testPassingNoInputToGentype() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", /* no input */
			"-pr", projDir.getAbsolutePath(), 
			"-domain", "runtime",
			"-errorlibname","TestErrorLibrary",
			"-dest", projDir.getAbsolutePath() 
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "Please provide a value for the option -gentype";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * Pass no input for project root
	 */
	@Test
	public void testPassingNoInputToProjectRoot() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", /* no input */
			"-domain", "runtime",
			"-errorlibname","TestErrorLibrary",
			"-dest", projDir.getAbsolutePath() 
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "Please provide a value for the option -pr";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * Pass no input for domain
	 */
	@Test
	public void testPassingNoInputToDomain() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", projDir.getAbsolutePath(), 
			"-domain", /* no input */ 
			"-errorlibname","TestErrorLibrary",
			"-dest", projDir.getAbsolutePath() 
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "Please provide a value for the option -domain";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * Pass no input for destination
	 */
	@Test
	public void testPassingNoInputToDest() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", projDir.getAbsolutePath(), 
			"-domain", "runtime", 
			"-errorlibname","TestErrorLibrary",
			"-dest" /* no input */ 
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "Missing parameter for '-dest' option.";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/** 
	 * invalid input to gentype
	 */
	@Test
	public void testInvalidInputToGentype() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "invalid", 
			"-pr", projDir.getAbsolutePath(),
			"-domain", "runtime", 
			"-errorlibname","TestErrorLibrary",
			"-dest", projDir.getAbsolutePath()
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "Invalid code gen type specified : invalid";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * invalid input to project root
	 */
	@Test
	public void testInvalidInputToProjectRoot() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		File propFile = createDomainPropertiesFile(projDir, "TestErrorLibrary");
		Properties props = new Properties();
		props.setProperty("listOfDomains", "runtime");
		storeProps(propFile, props);
		
		File invalidDir = new File(projDir, "invalid");
		MavenTestingUtils.ensureDeleted(invalidDir);

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", invalidDir.getAbsolutePath(), 
			"-dest", projDir.getAbsolutePath(), 
			"-errorlibname","TestErrorLibrary",
			"-domain", "runtime",
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "Project Root directory does not exist";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * invalid input to destination
	 */
	@Test
	public void testInvalidInputToDestination() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		File propFile = createDomainPropertiesFile(projDir, "TestErrorLibrary");
		Properties props = new Properties();
		props.setProperty("listOfDomains", "runtime");
		storeProps(propFile, props);
		
		File invalidDir = new File(projDir, "invalid");
		MavenTestingUtils.ensureDeleted(invalidDir);

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", projDir.getAbsolutePath(), 
			"-domain", "runtime",
			"-errorlibname","TestErrorLibrary",
			"-dest", invalidDir.getAbsolutePath() 
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "provide a  valid value for -dest option";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	// pass Different input to -pr and - dest
	@Test
	public void testPassingDiffInputForPrAndDest() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		File propFile = createDomainPropertiesFile(projDir, "TestErrorLibrary");
		File destDir = testingdir.getFile("dest/gen-src");
		MavenTestingUtils.ensureEmpty(destDir);

		// @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", projDir.getAbsolutePath(), 
			"-domain", "runtime",
			"-errorlibname","TestErrorLibrary",
			"-dest", destDir.getAbsolutePath()
		};
        // @formatter:on

		Properties props = new Properties();
		props.setProperty("listOfDomains", "runtime");
		storeProps(propFile, props);

		performDirectCodeGen(inputArgs);
		
		CodeGenAssert.assertJavaSourceExists(destDir,
				"org.suhua.errorlibrary.runtime.ErrorDataCollection");
	}

	/**
	 * Tests that pr overides dest option
	 */
	@Test
	public void testPrOverridesDestOption() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		File propFile = createDomainPropertiesFile(projDir, "TestErrorLibrary");
		Properties props = new Properties();
		props.setProperty("listOfDomains", "runtime");
		storeProps(propFile, props);

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", projDir.getAbsolutePath(), 
			"-domain", "runtime",
			"-errorlibname","TestErrorLibrary",
		};
        // @formatter:on

		performDirectCodeGen(inputArgs);

		File destDir = new File(projDir, "gen-src");
		CodeGenAssert.assertJavaSourceExists(destDir,
				"org.suhua.errorlibrary.runtime.ErrorDataCollection");
	}

	/**
	 * Test for missing gentype option
	 */
	@Test
	public void testMissingGentypeOption() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		createDomainPropertiesFile(projDir, "TestErrorLibrary");
		
		File tempDir = new File(projDir, "temp2");
		MavenTestingUtils.ensureEmpty(tempDir);

		// @formatter:off
		String[] inputArgs = { 
			"-pr", projDir.getAbsolutePath(), 
			"-domain", "runtime",
			"-errorlibname","TestErrorLibrary",
			"-dest", tempDir.getAbsolutePath()
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "provide a value for -gentype option";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * Test for missing pr option
	 */
	@Test
	public void testMissingProjectRoot() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		File propFile = createDomainPropertiesFile(projDir, "TestErrorLibrary");
		Properties props = new Properties();
		props.setProperty("listOfDomains", "runtime");
		storeProps(propFile, props);
		
		File tempDir = new File(projDir, "temp2");
		MavenTestingUtils.ensureEmpty(tempDir);

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-domain", "runtime",
			"-errorlibname","TestErrorLibrary",
			"-dest", tempDir.getAbsolutePath()
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "Project Meta Src Dir is missing. "+
			"Please provide the value for this option -metasrc";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}
	
	
	@Test
	public void testMissingProjectRootProvidedMetasrc() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		File propFile = createDomainPropertiesFile(projDir, "TestErrorLibrary");
		Properties props = new Properties();
		props.setProperty("listOfDomains", "runtime");
		storeProps(propFile, props);

		File tempDir = new File(projDir, "temp2");
		MavenTestingUtils.ensureEmpty(tempDir);

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-dest", projDir.getAbsolutePath(), 
			"-domain", "runtime",
			"-errorlibname","TestErrorLibrary",
			"-metasrc",new File(projDir,"meta-src").getAbsolutePath()
		};
        // @formatter:on

		performDirectCodeGen(inputArgs);

		//File destDir = new File(projDir, "gen-src");
		CodeGenAssert.assertJavaSourceExists(projDir,
				"org.suhua.errorlibrary.runtime.ErrorDataCollection");
	}
	

	/**
	 * Test for missing domain option
	 */
	@Test
	public void testMissingDomain() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		File propFile = createDomainPropertiesFile(projDir, "TestErrorLibrary");
		Properties props = new Properties();
		props.setProperty("listOfDomains", "runtime");
		storeProps(propFile, props);

		File tempDir = new File(projDir, "temp2");
		MavenTestingUtils.ensureEmpty(tempDir);

        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", projDir.getAbsolutePath(), 
			"-dest", tempDir.getAbsolutePath(),
			"-errorlibname","TestErrorLibrary"
		};
        // @formatter:on

		try {
		    performDirectCodeGen(inputArgs);
		} catch (Exception e) {
			String expected = "List of domains is missing which is mandatory. "
					+ "Pls provide the value for this option -domain";
			Assert.assertThat(e.getMessage(), containsString(expected));
		}
	}

	/**
	 * Test for missing dest option - Artifact to be generated in project root
	 */
	@Test
	public void testMissingDest() throws Exception {
		testingdir.ensureEmpty();
		File projDir = testingdir.getDir();
		File propFile =  createDomainPropertiesFile(projDir, "TestErrorLibrary");

		Properties props = new Properties();
		props.setProperty("listOfDomains", "runtime");
		storeProps(propFile, props);
		
        // @formatter:off
		String[] inputArgs = { 
			"-gentype", "genTypeDataCollection", 
			"-pr", projDir.getAbsolutePath(), 
			"-domain", "runtime",
			"-errorlibname","TestErrorLibrary"
		};
        // @formatter:on

		performDirectCodeGen(inputArgs);

		File destDir = new File(projDir, "gen-src"); // the legacy dest directory
		CodeGenAssert.assertJavaSourceExists(destDir,
				"org.suhua.errorlibrary.runtime.ErrorDataCollection");
	}

	private void storeProps(File propsFile, Properties props) throws IOException {
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(propsFile);
			props.store(stream, "---stored---");
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	private File createDomainPropertiesFile(File projRoot, String errorLibName) throws Exception
	{
		String dirname = FilenameUtils.separatorsToSystem("meta-src/META-INF/errorlibrary/" + errorLibName);
		File testDir = new File(projRoot, dirname);

		// Ensure that we don't accidentally benefit from past or other tests.
		MavenTestingUtils.ensureDirExists(testDir);

		// Create an empty properties file
		File testProp = new File(testDir, "error_library_project.properties");
		Assert.assertTrue("Creating empty file: " + testProp, testProp.createNewFile());

		// Create required support files.
		ErrorLibraryFileGenerationTest.copyErrorPropertiesToProjectRoot(
				"QAErrors.properties", projRoot, "runtime");
		ErrorLibraryFileGenerationTest.copyErrorXmlToProjectRoot(
				"ErrorData_QA.xml", projRoot, "runtime");

		return testProp;
	}
}
