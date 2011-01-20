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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.lang.SystemUtils;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;
import org.ebayopensource.turmeric.tools.AbstractCodegenTestCase;
import org.ebayopensource.turmeric.tools.GeneratedAssert;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.JavacHelper;
import org.junit.Assert;
import org.junit.Test;


public class ErrorLibraryGeneratorTest extends AbstractCodegenTestCase {

	@SuppressWarnings("unchecked")
	@Test
	public void missingInputOptions() throws Exception{
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeErrorLibAll"
		};
		// @formatter:on
		
		try {
			performDirectCodeGen(pluginParameters);
			Assert.fail("Expected exception of type: " + MissingInputOptionException.class.getName());
		} catch (MissingInputOptionException ex) {
			Assert.assertThat(ex.getMessage(), allOf(
					containsString("Project Root is missing"),
					containsString("Error Library name is missing"),
					containsString("Project Generated Content Destination is missing"),
					containsString("Project Meta Src Dir is missing")));
		}
	}
	
	@Test
	public void faultyInputOptionsGenType() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = testingdir.getDir();
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeFaultyErrorLibAll", 
			"-pr", rootDir.getAbsolutePath()
		};
		// @formatter:on
		
		try {
		    performDirectCodeGen(pluginParameters);
			Assert.fail("Expected exception of type: " + BadInputOptionException.class.getName());
		} catch (BadInputOptionException ex) {
			Assert.assertThat(ex.getMessage(), 
					containsString("Invalid code gen type specified"));
		}
	}
	

	@Test
	public void emptyDomainListFailureCase() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeErrorLibAll", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", ""
		};
		// @formatter:on
		
		try {
			performDirectCodeGen(pluginParameters);
			Assert.fail("Expected exception of type: " + BadInputValueException.class.getName());
		} catch (BadInputValueException ex) {
			Assert.assertThat(ex.getMessage(), 
					containsString("Please provide a proper value for the option -domain"));
		}
	}
	
	@Test
	public void nonExistingDomainFailureCase() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeErrorLibAll", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "testDomain", 
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		try {
			performDirectCodeGen(pluginParameters);
			Assert.fail("Expected exception of type: " + CodeGenFailedException.class.getName());
		} catch (CodeGenFailedException ex) {
			Assert.assertThat(ex.getMessage(), containsString("domain [testDomain] not found"));
		}
	}

	@Test
	public void faultyErrorNamesFailureCase() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/ValidateErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeErrorLibAll", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "FaultyErrorName",
			"-errorlibname", "ValidateErrorLibrary"
		};
		// @formatter:on
		
		try {
			performDirectCodeGen(pluginParameters);
			Assert.fail("Expected exception of type: " + CodeGenFailedException.class.getName());
		} catch (CodeGenFailedException ex) {
			Assert.assertThat(ex.getMessage(), containsString("svc_factory_inst_ illegal_access"));
		}
	}
	
	@Test
	public void entryNotAvailableInErrorPropertiesFileFailureCase() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/ValidateErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeErrorLibAll", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "PropertiesInconsistent",
			"-errorlibname", "ValidateErrorLibrary"
		};
		// @formatter:on
		
		try {
			performDirectCodeGen(pluginParameters);
			Assert.fail("Expected exception of type: " + CodeGenFailedException.class.getName());
		} catch (CodeGenFailedException ex) {
			Assert.assertThat(ex.getMessage(), containsString("Errors.properties does not have all the errors"));
		}
	}
	
	@Test
	public void schemaValidationFailureCase() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/ValidateErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeErrorLibAll", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "SchemaValidationFailure",
			"-errorlibname", "ValidateErrorLibrary"
		};
		// @formatter:on
		
		try {
			performDirectCodeGen(pluginParameters);
			Assert.fail("Expected exception of type: " + CodeGenFailedException.class.getName());
		} catch (CodeGenFailedException ex) {
			Assert.assertThat(ex.getMessage(), containsString("Metadata Validation failed : XML validation against"));
		}
	}
	
	@Test
	public void duplicatesValidationFailureCase() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/ValidateErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeErrorLibAll", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "Duplicates",
			"-errorlibname", "ValidateErrorLibrary"
		};
		// @formatter:on
		
		try {
			performDirectCodeGen(pluginParameters);
			Assert.fail("Expected exception of type: " + CodeGenFailedException.class.getName());
		} catch (CodeGenFailedException ex) {
			Assert.assertThat(ex.getMessage(), containsString("Duplicates found"));
		}
	}
	
	@Test
	public void generateErrorConstantsSingleDomain() throws Exception {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeConstants", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime",
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);
		
		File genSrcDir = new File(rootDir, "gen-src");
		
		GeneratedAssert.assertJavaExists(genSrcDir, "org.ebayopensource.turmeric.test.errorlibrary.turmericruntime.ErrorConstants");
	}
	
	@Test
	public void generateErrorConstantsSingleDomainwithDest() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		File destDir = new File(rootDir, "tmp");
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeConstants", 
			"-pr", rootDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-domain", "TurmericRuntime",
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);
	}

	@Test
	public void generateErrorConstantsSingleDomainDest() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		File destDir = new File(rootDir, "tmp");
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeConstants", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime",
			"-dest", destDir.getAbsolutePath(),
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);
	}
	

	@Test
	public void generateErrorDataCollectionwithDest() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		File destDir = new File(rootDir, "tmp");
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeDataCollection", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime,Security",
			"-dest", destDir.getAbsolutePath(),
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);
	}
	
	@Test
	public void generateErrorConstantsMultipleDomain() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeConstants", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime, Security",
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);
	}

	@Test
	public void generateErrorConstantsMultipleDomainwithDest() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		File destDir = new File(rootDir, "tmp");
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeConstants", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime, Security",
			"-dest", destDir.getAbsolutePath(),
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);
	}
	
	@Test
	public void generateErrorDataCollection() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeDataCollection", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime,Security",
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);
	}
	
	@Test
	public void generateErrorConstantsAndDataCollectionSingleDomain() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeErrorLibAll", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime",
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);
	}
	
	@Test
	public void generateErrorConstantsAndDataCollectionMultipleDomain() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeErrorLibAll", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime,Security",
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);
	}
	
	@Test
	public void genTypeCommandLineAllWithDomains() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeCommandLineAll", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime,Security",
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);
	}

	@Test
	public void genTypeCommandLineAll() throws Exception{
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		
		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeCommandLineAll", 
			"-pr", rootDir.getAbsolutePath(),
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);
	}	
	
	@Test
	public void validateGeneratedErrorConstants() throws Exception{
		String errorConstantsClassName = "org.ebayopensource.turmeric.test.errorlibrary.turmericruntime.ErrorConstants";
		
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		File binDir = new File(rootDir, "bin");
		File gensrcDir = new File(rootDir, "gen-src");
		
		MavenTestingUtils.ensureDirExists(binDir);
		MavenTestingUtils.ensureDirExists(gensrcDir);

		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeConstants", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime",
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);

		Class<?> errConstant = compileGeneratedFile(errorConstantsClassName, gensrcDir, binDir);
		Assert.assertThat("errConstant", errConstant, notNullValue());
		Assert.assertThat(errConstant.getName(), is(errorConstantsClassName));
	}

	@Test
	public void validateGeneratedErrorDataCollections() throws Exception{
		String errorDataCollectionClassName = "org.ebayopensource.turmeric.test.errorlibrary.turmericruntime.ErrorDataCollection";
		
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		File binDir = new File(rootDir, "bin");
		File gensrcDir = new File(rootDir, "gen-src");
		
		MavenTestingUtils.ensureDirExists(binDir);
		MavenTestingUtils.ensureDirExists(gensrcDir);

		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeDataCollection", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime",
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);

		Class<?> errDataCollection = compileGeneratedFile(errorDataCollectionClassName, gensrcDir, binDir);
		Assert.assertThat("errDataCollection", errDataCollection, notNullValue());
		Assert.assertThat(errDataCollection.getName(), is(errorDataCollectionClassName));
	}
	

	@Test
	public void validateContentOfErrorConstants() throws Exception {
		String errorConstantsClassName = "org.ebayopensource.turmeric.test.errorlibrary.turmericruntime.ErrorConstants";
		String sampleErrorName = "svc_factory_custom_ser_no_bound_type";
		
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		File binDir = new File(rootDir, "bin");
		File gensrcDir = new File(rootDir, "gen-src");
		
		MavenTestingUtils.ensureDirExists(binDir);
		MavenTestingUtils.ensureDirExists(gensrcDir);

		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeConstants", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime",
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on
		
		performDirectCodeGen(pluginParameters);

		Class<?> errConstant = compileGeneratedFile(errorConstantsClassName, gensrcDir, binDir);
		Assert.assertThat("errConstant", errConstant, notNullValue());
		Assert.assertThat(errConstant.getName(), is(errorConstantsClassName));

		Field member = errConstant.getField(sampleErrorName.toUpperCase());
		Assert.assertThat("member", member, notNullValue());
		Assert.assertThat("member.type", member.getType().getName(), is(String.class.getName()));
		Assert.assertThat("member.isFinal", Modifier.isFinal(member.getModifiers()), is(true));
		Assert.assertThat("member.isPublic", Modifier.isPublic(member.getModifiers()), is(true));
		Assert.assertThat("member.isStatic", Modifier.isStatic(member.getModifiers()), is(true));
		Assert.assertThat("member.get(null)", (String) member.get(null), is(sampleErrorName));
	}

	@Test
	public void validateContentOfErrorDataCollections() throws Exception {
		String errorDataCOllectionClassName = "org.ebayopensource.turmeric.test.errorlibrary.turmericruntime.ErrorDataCollection";
		String sampleErrorName = "svc_factory_custom_ser_no_bound_type";
		
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File rootDir = TestResourceUtil.copyResourceRootDir("errorLibrary/TestErrorLibrary", testingdir);
		File binDir = new File(rootDir, "bin");
		File gensrcDir = new File(rootDir, "gen-src");
		
		MavenTestingUtils.ensureDirExists(binDir);
		MavenTestingUtils.ensureDirExists(gensrcDir);

		// @formatter:off
		String pluginParameters[] = {
			"-gentype", "genTypeDataCollection", 
			"-pr", rootDir.getAbsolutePath(),
			"-domain", "TurmericRuntime",
			"-errorlibname", "TestErrorLibrary"
		};
		// @formatter:on

		performDirectCodeGen(pluginParameters);

		Class<?> errDataCollection = compileGeneratedFile(errorDataCOllectionClassName, gensrcDir, binDir);
		Assert.assertThat("errDataCollection", errDataCollection, notNullValue());
		Assert.assertThat(errDataCollection.getName(), is(errorDataCOllectionClassName));

		Field member = errDataCollection.getField(sampleErrorName);
		Assert.assertThat("member", member, notNullValue());
		
		Assert.assertThat("member.type", member.getType().getName(), is(CommonErrorData.class.getName()));
		Assert.assertThat("member.isFinal", Modifier.isFinal(member.getModifiers()), is(true));
		Assert.assertThat("member.isPublic", Modifier.isPublic(member.getModifiers()), is(true));
		Assert.assertThat("member.isStatic", Modifier.isStatic(member.getModifiers()), is(true));
		CommonErrorData edata = (CommonErrorData) member.get(null);
		Assert.assertThat("CommonErrorData", edata, notNullValue());
		Assert.assertThat("CommonErrorData.category", edata.getCategory(), is(ErrorCategory.SYSTEM));
		Assert.assertThat("CommonErrorData.severity", edata.getSeverity(), is(ErrorSeverity.ERROR));
		Assert.assertThat("CommonErrorData.subdommain", edata.getSubdomain(), is("Config"));
	}
	
	private Class<?> compileGeneratedFile(String className, File srcDir, File destDir) throws Exception{
		addToClasspath(srcDir, destDir);
		String javaSrcFilePath = CodeGenUtil.toJavaSrcFilePath(srcDir.getAbsolutePath(), className);
		JavacHelper javacHelper = new JavacHelper(System.out);
	    javacHelper.compileJavaSource(javaSrcFilePath, srcDir.getAbsolutePath(), destDir.getAbsolutePath());
		return Class.forName(className);
	}
	
	private void addToClasspath(File source, File dest) throws Exception {
		String javaHomeStr = System.getProperty("java.home");
		File jreHome = new File(javaHomeStr);
		File toolsJar = new File(jreHome.getParent(), "lib/tools.jar");
		if(SystemUtils.IS_OS_MAC_OSX) {
			toolsJar = new File(jreHome.getParent(), "Classes/classes.jar");
		} 
		if (!toolsJar.exists()) {
			if (javaHomeStr.indexOf("jre") > 0
					|| javaHomeStr.indexOf("JRE") > 0) {
				if (javaHomeStr.endsWith("/")) {
					javaHomeStr = javaHomeStr + "../";
				} else {
					javaHomeStr = javaHomeStr + "/../";
				}
				jreHome = new File(javaHomeStr);
				toolsJar = new File(jreHome.getParent(), "lib/tools.jar");
			}
			
		}
		
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;
		Class<?>[] parameters = { URL.class };
		URL u = toolsJar.toURI().toURL();
		Method method = sysclass.getDeclaredMethod("addURL", parameters);
		method.setAccessible(true);
		method.invoke(sysloader, new Object[] { u });
		method.invoke(sysloader, new Object[] { source.toURI().toURL() });
		method.invoke(sysloader, new Object[] { dest.toURI().toURL() });
	}
}
