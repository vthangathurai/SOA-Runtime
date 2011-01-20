/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ebayopensource.turmeric.junit.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.junit.Assert;
import org.junit.Test;


public class JavaSourceParserTest extends AbstractTurmericTestCase {
	public String getTestSourcePath(Class<?> clazz) {
		File srcTestDir = MavenTestingUtils.getProjectDir("src/test/java");
		String javapath = clazz.getName().replace(".", File.separator);
		File javaFile = PathAssert.assertFileExists(srcTestDir, javapath + ".java");
		return javaFile.getAbsolutePath();
	}
	
	@Test
	public void testMethodToParamNamesMap() {
		Class<?> clazz = JavaSourceSample.class;
		String javaSrcFile = getTestSourcePath(clazz);
		Map<String, String[]> map = JavaSourceParser.methodToParamNamesMap(javaSrcFile, clazz);
		Assert.assertNotNull("Map should not be null", map);
		
		dump(map);
		
		Assert.assertTrue("Should have found 'dosomething' method", map.containsKey("dosomething"));
		Assert.assertTrue("Should have found 'getVersionId' method", map.containsKey("getVersionId"));
		
		String[] initParams = map.get("init");
		Assert.assertNotNull("Should have gotten init params", initParams);
		Assert.assertEquals("logger", initParams[0]);
		Assert.assertEquals("level", initParams[1]);
	}

	private void dump(Map<String, String[]> map) {
		System.out.printf("Found %d Map entries%n", map.size());
		for(String methodName: map.keySet()) {
			System.out.printf("Method Name \"%s\"%n", methodName);
			String[] params = map.get(methodName);
			if(params == null) {
				System.out.printf("    params: <null>%n");
			} else {
				System.out.printf("    params: [%s]%n", StringUtils.join(params, ','));
			}
		}
	}
}
