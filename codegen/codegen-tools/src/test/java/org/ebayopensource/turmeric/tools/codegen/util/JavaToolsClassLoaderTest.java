/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.JavaToolsClassLoader;
import org.junit.Assert;
import org.junit.Test;


public class JavaToolsClassLoaderTest {

	@Test
	public void testCreate() throws CodeGenFailedException {
		/* it should be safe to assume that if you are running
		 * this test case, you are running within a JDK.
		 */
		ClassLoader cl = JavaToolsClassLoader.createIfNeeded();
		Assert.assertNotNull("JavaToolsClassLoader should not be null", cl);
		
		assertClassPresent(cl, "com.sun.tools.javac.Main");
		assertClassPresent(cl, "com.sun.tools.apt.Main");
		assertClassPresent(cl, "com.sun.javadoc.Doclet");
		assertClassPresent(cl, "com.sun.javadoc.Type");
	}

	private void assertClassPresent(ClassLoader cl, String name) {
		try {
			Class<?> clazz = cl.loadClass(name);
			Assert.assertNotNull("Should have found class: " + name, clazz);
		} catch (ClassNotFoundException e) {
			Assert.fail("Counld not load class: " + name + " from " + cl.getClass().getName());
		}
	}
}
