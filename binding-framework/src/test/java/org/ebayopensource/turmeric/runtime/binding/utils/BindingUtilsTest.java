/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.utils;

import static org.hamcrest.Matchers.*;

import org.ebayopensource.turmeric.junit.AbstractTurmericTestCase;
import org.junit.Assert;
import org.junit.Test;

public class BindingUtilsTest extends AbstractTurmericTestCase {

	@Test
	public void testSameObjectDiffStrings() {
		String s1 = "a1";
		String s2 = "b2";
		
		Assert.assertFalse(BindingUtils.sameObject(s1, s2));
	}

	@Test
	public void testSameObjectSameStrings() {
		String s1, s2;
		s1 = s2 = "a1";
		
		Assert.assertTrue(BindingUtils.sameObject(s1, s2));
	}

	@Test
	public void testSameObjectSimilarStrings() {
		String s1 = "str";
		String s2 = "str";
		
		Assert.assertTrue(BindingUtils.sameObject(s1, s2));
	}
	
	@Test
	public void testGetPackageName() {
		String fq = "org.ebayopensource.turmeric.runtime.SampleObject";
		Assert.assertThat(BindingUtils.getPackageName(fq), is("org.ebayopensource.turmeric.runtime"));
	}
	
	@Test
	public void tesGetPackgeNameFromClass() {
		Class<?> clazz = this.getClass();
		String expectedPackageName = clazz.getPackage().getName();
		Assert.assertThat(BindingUtils.getPackageName(clazz), is(expectedPackageName));
	}
}
