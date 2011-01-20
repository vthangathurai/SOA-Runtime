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
import java.lang.reflect.Modifier;

import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.junit.Assert;

public final class CodeGenAssert {
	private CodeGenAssert() {
		/* prevent instantiation */
	}
	
	public static File assertJavaSourceExists(File destDir, String classname) {
		File javaFile = new File(destDir, classname.replace('.', File.separatorChar) + ".java");
		PathAssert.assertFileExists("Java Source File", javaFile);
		return javaFile;
	}
	
	public static void assertClassPackage(Class<?> actualClazz,
			String expectedPackageName) {
		Assert.assertThat("Package Name", actualClazz.getPackage().getName(),
				is(expectedPackageName));
	}
	
	public static void assertClassIsPublic(Class<?> actualClazz) {
		Assert.assertTrue("Class name should be " + actualClazz.getName(),
				Modifier.isPublic(actualClazz.getModifiers()));
	}
	
	public static void assertClassName(Class<?> actualClazz, String expectedName) {
		Assert.assertThat("Class Name", actualClazz.getSimpleName(), is(expectedName));
	}
	
	public static void assertFieldIsPublicStaticFinal(Field actualField) {
		Assert.assertTrue(
				"Field access should be public: "
						+ actualField.toGenericString(),
				Modifier.isPublic(actualField.getModifiers()));
		Assert.assertTrue(
				"Field access should be static: "
						+ actualField.toGenericString(),
				Modifier.isStatic(actualField.getModifiers()));
		Assert.assertTrue(
				"Field access should be final: "
						+ actualField.toGenericString(),
				Modifier.isFinal(actualField.getModifiers()));
	}

	public static void assertFieldIsPrivateStaticFinal(Field actualField) {
		Assert.assertTrue(
				"Field access should be private: "
						+ actualField.toGenericString(),
				Modifier.isPrivate(actualField.getModifiers()));
		Assert.assertTrue(
				"Field access should be static: "
						+ actualField.toGenericString(),
				Modifier.isStatic(actualField.getModifiers()));
		Assert.assertTrue(
				"Field access should be final: "
						+ actualField.toGenericString(),
				Modifier.isFinal(actualField.getModifiers()));
	}

	public static void assertFieldType(Field actualField,
			Class<String> expectedClass) {
		Assert.assertThat("Field type: " + actualField.toGenericString(),
				actualField.getType().getName(), is(expectedClass.getName()));
	}
}
