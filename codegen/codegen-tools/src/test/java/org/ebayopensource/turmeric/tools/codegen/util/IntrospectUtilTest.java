/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.ebayopensource.turmeric.junit.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.junit.rules.TestingDir;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.CodeGenTestMessage;
import org.ebayopensource.turmeric.tools.codegen.JTypeTable;
import org.ebayopensource.turmeric.tools.codegen.util.IntrospectUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class IntrospectUtilTest extends AbstractTurmericTestCase {
	@Rule
	public TestingDir testingdir = new TestingDir();
	
	@Before
	public void preventBadTests() {
		mavenTestingRules.setStrictReadPaths(true);
		mavenTestingRules.setStrictWritePaths(true);
		mavenTestingRules.setFailOnViolation(true);
	}
	
	@Test
	public void isCollectionTypeString() throws Exception {
		Assert.assertFalse(IntrospectUtil.isCollectionType(String.class));
	}

	@Test
	public void isCollectionTypeList() throws Exception {
		Assert.assertTrue(IntrospectUtil.isCollectionType(List.class));
	}

	@Test
	public void isCollectionTypeInteger() throws Exception {
		Assert.assertFalse(IntrospectUtil.isCollectionType(Integer.TYPE.getClass()));
	}

	@Test
	public void isCollectionTypeHashtable() throws Exception {
		Assert.assertTrue(IntrospectUtil.isCollectionType(Hashtable.class));
	}


	@Test
	public void isCollectionTypeNull() throws Exception {
		Class<?> typeClass = null;
		Assert.assertFalse(IntrospectUtil.isCollectionType(typeClass));
	}

	@Test
	public void hasCollectionType1() throws Exception {
		Class<?>[] typeClass = new Class[] {String.class};
		Assert.assertFalse(IntrospectUtil.hasCollectionType(typeClass));
	}


	@Test
	public void hasCollectionType2() throws Exception {
		Class<?>[] typeClass = new Class[] {String.class, List.class};
		Assert.assertTrue(IntrospectUtil.hasCollectionType(typeClass));
	}



	@Test
	public void hasCollectionType3() throws Exception {
		Class<?>[] typeClass = null;
		Assert.assertFalse(IntrospectUtil.hasCollectionType(typeClass));
	}

	@Test
	public void hasAttachmentTypeRef1() throws Exception {
		Class<?> type = null;
		Set<String> typeNameSet = new HashSet<String>(); 
		Assert.assertFalse(IntrospectUtil.hasAttachmentTypeRef(type, typeNameSet));
	}


	@Test
	public void hasAttachmentTypeRef2() throws Exception {
		Class<?> type = String.class;
		Set<String> typeNameSet = new HashSet<String>(); 
		boolean hasAttachmentType = IntrospectUtil.hasAttachmentTypeRef(type, typeNameSet);

		assertTrue((hasAttachmentType == false));
	}



	@Test
	public void hasAttachmentTypeRef3() throws Exception {
		boolean hasAttachmentType = false;

		try {
			Class<?> type = CodeGenTestMessage.class;
			Set<String> typeNameSet = new HashSet<String>(); 
			hasAttachmentType = IntrospectUtil.hasAttachmentTypeRef(type, typeNameSet);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}	

		assertTrue((hasAttachmentType == true));
	}

	@Test
	public void testLoadClass() throws Exception {
		// Setup testing directory
		testingdir.ensureEmpty();
		File classes = testingdir.getFile("classes");
		MavenTestingUtils.ensureDirExists(classes);
		File srcDir = TestResourceUtil.getResourceDir("botservice-classes");
		FileUtils.copyDirectory(srcDir, classes);

		// Setup temp classloader with testing dir included
		URL urls[] = { classes.toURI().toURL() };
		URLClassLoader cl = new URLClassLoader(urls);
		ClassLoader originalCL = Thread.currentThread().getContextClassLoader();

		try {
			Thread.currentThread().setContextClassLoader(cl);
			// Execute test
			Class<?> clazz = IntrospectUtil.loadClass("fr.virtuoz.BotService");
			Assert.assertNotNull("Loaded Class should not be null", clazz);
		} finally {
			Thread.currentThread().setContextClassLoader(originalCL);
		}
	}
	
	@Test
	public void testInitializeJType() throws Exception {
		// Setup testing directory
		testingdir.ensureEmpty();
		File classes = testingdir.getFile("classes");
		MavenTestingUtils.ensureDirExists(classes);
		File srcDir = TestResourceUtil.getResourceDir("botservice-classes");
		FileUtils.copyDirectory(srcDir, classes);

		// Setup temp classloader with testing dir included
		URL urls[] = { classes.toURI().toURL() };
		URLClassLoader cl = new URLClassLoader(urls);
		ClassLoader originalCL = Thread.currentThread().getContextClassLoader();

		try {
			Thread.currentThread().setContextClassLoader(cl);
			JTypeTable jtt = IntrospectUtil.initializeJType("fr.virtuoz.BotService");
			Assert.assertNotNull("Loaded Class should not be null", jtt);
		} finally {
			Thread.currentThread().setContextClassLoader(originalCL);
		}
	}
}
