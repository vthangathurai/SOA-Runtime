/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.handler.DontPromptResponseHandler;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions;
import org.ebayopensource.turmeric.tools.library.codegen.TypeLibraryCodeGenContext;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author arajmony
 */
public class TypeLibraryUtilitiesTest extends AbstractServiceGeneratorTestCase {
	public TypeLibraryUtilitiesTest(){}

	@Test
	public void method_checkVersionFormat(){
		String version = "1.0.1";
		Assert.assertTrue(TypeLibraryUtilities.checkVersionFormat(version, 3));
	}

	@Test
	public void method_checkVersionFormat_failCase_lowerLimit(){
		String version = "1.0.1";
		boolean status = TypeLibraryUtilities.checkVersionFormat(version, 2);
		assertFalse(status);
	}

	@Test
	public void method_checkVersionFormat_failCase_HigherLimit(){
		String version = "1.2.1";
		boolean status = TypeLibraryUtilities.checkVersionFormat(version, 4);
		assertFalse(status);
	}

	@Test
	public void method_checkVersionFormat_failCase_NonNumericCase_1(){
		String version = "1.ab.1";
		boolean status = TypeLibraryUtilities.checkVersionFormat(version, 3);
		assertFalse(status);
	}

	@Test
	public void method_checkVersionFormat_failCase_NonNumericCase_2(){
		String version = "1.//.1";
		boolean status = TypeLibraryUtilities.checkVersionFormat(version, 3);
		assertFalse(status);
	}

	@Test
	public void method_checkVersionFormat_failCase_NonNumericCase_3(){
		String version = "1.2.$%";
		boolean status = TypeLibraryUtilities.checkVersionFormat(version, 3);
		assertFalse(status);
	}

	@Test
	public void method_checkVersionFormat_failCase_Empty(){
		String version = "1..3";
		boolean status = TypeLibraryUtilities.checkVersionFormat(version, 3);
		assertFalse(status);
	}


	@Test
	public void method_checkVersionFormat_failCase_Empty_2(){
		String version = "..";
		boolean status = TypeLibraryUtilities.checkVersionFormat(version, 3);
		assertFalse(status);
	}

	@Test
	public void method_checkVersionFormat_manyVersions(){
		String version = "12.1.23.45.38989.90901";
		boolean status = TypeLibraryUtilities.checkVersionFormat(version, 6);
		assertTrue(status);
	}

	@Test
	public void method_findDependentLibrariesForAType__case__one_level__no_current__only_parent() throws Exception{
		Set<String> expectedLib = new HashSet<String>();
		expectedLib.add("SecondLevelLib");

		Map<String, Set<String>> dependentLibAndTypeMap = null;
		TypeLibraryCodeGenContext ctx = createTypeLibraryCodeGenContext("", "FirstLevelLib");
		dependentLibAndTypeMap = TypeLibraryUtilities.findDependentLibrariesAndTypesForAType(ctx, "FirstLevelLib", "FirstLevelType");

		assertEquals(1, dependentLibAndTypeMap.size());
		assertTrue(dependentLibAndTypeMap.containsKey("SecondLevelLib"));
	}


	@Test
	public void method_findDependentLibrariesForAType__case__one_level__current__and__parent() throws Exception{
		String[] expectedLibArray = new String[]{"CurrentAndParent","SecondLevelLib"};

		Map<String, Set<String>> dependentLibAndTypeMap = null;
		TypeLibraryCodeGenContext ctx = createTypeLibraryCodeGenContext("", "CurrentAndParent");
		dependentLibAndTypeMap = TypeLibraryUtilities.findDependentLibrariesAndTypesForAType(ctx, "CurrentAndParent", "CurrentAndParenttype");

		assertEquals(2, dependentLibAndTypeMap.size());

		for(int i=0;i<expectedLibArray.length;i++)
		{
			assertTrue(dependentLibAndTypeMap.containsKey(expectedLibArray[i]));
		}
	}

	@Test
	public void method_findDependentLibrariesForAType__case__three_level__parent__grandParent() throws Exception{
		String[] expectedLibArray = new String[]{"FirstLevelLib","SecondLevelLib"};

		Map<String, Set<String>> dependentLibAndTypeMap = null;
		TypeLibraryCodeGenContext ctx = createTypeLibraryCodeGenContext("", "ParentAndGrandParent");
		dependentLibAndTypeMap = TypeLibraryUtilities.findDependentLibrariesAndTypesForAType(ctx, "ParentAndGrandParent", "ParentAndGrandParentType");

		assertEquals(2, dependentLibAndTypeMap.size());

		for(int i=0;i<expectedLibArray.length;i++)
		{
			assertTrue(dependentLibAndTypeMap.containsKey(expectedLibArray[i]));
		}
	}

	@Test
	public void method_findDependentLibrariesForAType__case__three_level__current__parent__grandParent() throws Exception{
		String[] expectedLibArray = new String[]{"CurrentParentAndGrand","SecondLevelLib","FirstLevelLib"};

		Map<String, Set<String>> dependentLibAndTypeMap = null;
		TypeLibraryCodeGenContext ctx = createTypeLibraryCodeGenContext("", "CurrentParentAndGrand");
		dependentLibAndTypeMap = TypeLibraryUtilities.findDependentLibrariesAndTypesForAType(ctx, "CurrentParentAndGrand", "CurrentParentAndGrandType");
		
		assertEquals(3, dependentLibAndTypeMap.size());

		for(int i=0;i<expectedLibArray.length;i++)
		{
			assertTrue(dependentLibAndTypeMap.containsKey(expectedLibArray[i]));
		}
	}

	@Test
	public void method_getLibrariesNameSpace_null_PR__null_classLoader(){
		String library_1 = "HardwareTypeLibraryTest";
		String library_2 = "Library1";
		String library_3 = "Library2";

		String library_1_NS = "http://www.ebayopensource.org/soaframework/examples/config";
		String library_2_NS = "http://www.ebayopensource.org/soaframework/examples/config";
		String library_3_NS = "http://www.ebayopensource.org/turmeric/common/v1/types";


		List<String> libraryNames = new ArrayList<String>();
		libraryNames.add(library_1);
		libraryNames.add(library_2);
		libraryNames.add(library_3);

		Map<String,String> libraryNamesNSMap = TypeLibraryUtilities.getLibrariesNameSpace(libraryNames, null, null);

		Assert.assertEquals(libraryNamesNSMap.get(library_1), library_1_NS);
		Assert.assertEquals(libraryNamesNSMap.get(library_2), library_2_NS);
		Assert.assertEquals(libraryNamesNSMap.get(library_3), library_3_NS);
	}


	@Test
	public void method_getLibrariesNameSpace_null_PR__valid_classLoader(){
		String library_1 = "HardwareTypeLibraryTest";
		String library_2 = "Library1";
		String library_3 = "Library2";

		String library_1_NS = "http://www.ebayopensource.org/soaframework/examples/config";
		String library_2_NS = "http://www.ebayopensource.org/soaframework/examples/config";
		String library_3_NS = "http://www.ebayopensource.org/turmeric/common/v1/types";


		List<String> libraryNames = new ArrayList<String>();
		libraryNames.add(library_1);
		libraryNames.add(library_2);
		libraryNames.add(library_3);

		Map<String,String> libraryNamesNSMap = TypeLibraryUtilities.getLibrariesNameSpace(libraryNames,null, this.getClass().getClassLoader());

		Assert.assertEquals(libraryNamesNSMap.get(library_1), library_1_NS);
		Assert.assertEquals(libraryNamesNSMap.get(library_2), library_2_NS);
		Assert.assertEquals(libraryNamesNSMap.get(library_3), library_3_NS);
	}

	@Test
	public void method_findDependentLibrariesAndTypesForAType__case__one_level__no_current__only_parent() throws Exception{
		Set<String> expectedLib = new HashSet<String>();
		expectedLib.add("SecondLevelLib");

		Map<String, Set<String>> dependentLibAndTypeMap = null;
		TypeLibraryCodeGenContext ctx = createTypeLibraryCodeGenContext("", "FirstLevelLib");
		dependentLibAndTypeMap = TypeLibraryUtilities.findDependentLibrariesAndTypesForAType(ctx, "FirstLevelLib", "FirstLevelType");

		assertEquals(1, dependentLibAndTypeMap.size());
		Set<String> allTyeps = dependentLibAndTypeMap.get("SecondLevelLib");
		assertTrue(allTyeps.contains("SecondLevelType"));
	}

	@Test
	public void method_findDependentLibrariesAndTypesForAType__case__one_level__current__and__parent() throws Exception{
		Map<String, Set<String>> dependentLibAndTypeMap = null;
		TypeLibraryCodeGenContext ctx = createTypeLibraryCodeGenContext("", "CurrentAndParent");
		dependentLibAndTypeMap = TypeLibraryUtilities.findDependentLibrariesAndTypesForAType(ctx, "CurrentAndParent", "CurrentAndParenttype");
		
		assertEquals(2, dependentLibAndTypeMap.size());

		assertTrue(dependentLibAndTypeMap.get("CurrentAndParent").contains("existingType"));
		assertTrue(dependentLibAndTypeMap.get("SecondLevelLib").contains("SecondLevelType"));
	}

	@Test
	public void method_findDependentLibrariesAndTypesForAType__case__three_level__parent__grandParent() throws Exception{

		Map<String, Set<String>> dependentLibAndTypeMap = null;
		TypeLibraryCodeGenContext ctx = createTypeLibraryCodeGenContext("", "ParentAndGrandParent");
		dependentLibAndTypeMap = TypeLibraryUtilities.findDependentLibrariesAndTypesForAType(ctx, "ParentAndGrandParent", "ParentAndGrandParentType");
		
		assertEquals(2, dependentLibAndTypeMap.size());
		dependentLibAndTypeMap.get("FirstLevelLib").contains("FirstLevelType");
		dependentLibAndTypeMap.get("SecondLevelLib").contains("SecondLevelType");
	}

	@Test
	public void method_findDependentLibrariesAndTypesForAType__case__three_level__current__parent__grandParent() throws Exception
	{
		Map<String, Set<String>> dependentLibAndTypeMap = null;
		
		TypeLibraryCodeGenContext ctx = createTypeLibraryCodeGenContext("", "CurrentParentAndGrand");
		dependentLibAndTypeMap = TypeLibraryUtilities.findDependentLibrariesAndTypesForAType(ctx, "CurrentParentAndGrand", "CurrentParentAndGrandType");

		assertEquals(3, dependentLibAndTypeMap.size());

		assertTrue(dependentLibAndTypeMap.get("CurrentParentAndGrand").contains("someExistingType"));
		assertTrue(dependentLibAndTypeMap.get("SecondLevelLib").contains("SecondLevelType"));
		assertTrue(dependentLibAndTypeMap.get("FirstLevelLib").contains("FirstLevelType"));
	}

	private TypeLibraryCodeGenContext createTypeLibraryCodeGenContext(
			String projectRoot, String libraryName) {
		TypeLibraryInputOptions opts = new TypeLibraryInputOptions();
		opts.setProjectRoot(projectRoot);
		opts.setTypeLibraryName(libraryName);
		TypeLibraryCodeGenContext ctx = new TypeLibraryCodeGenContext(opts, new DontPromptResponseHandler());
		return ctx;
	}
}
