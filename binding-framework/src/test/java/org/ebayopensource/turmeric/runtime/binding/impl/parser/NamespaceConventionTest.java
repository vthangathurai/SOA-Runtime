/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser;

import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.junit.Assert;
import org.junit.Test;


public class NamespaceConventionTest {

	private static final String NS_COMMON_TYPES = "http://www.ebayopensource.org/turmeric/common/v1/types";

    @Test
	public void buildingPrefixes() {	
		Collection<String> namespaces = new ArrayList<String>();
		namespaces.add("abc");
		
		Map<String, List<String>> namespaceToPrefixes = new HashMap<String, List<String>>();
		Map<String, String> prefixToNamespace = new HashMap<String, String>();
		
		NamespaceConvention.buildNsPrefixes(namespaces , namespaceToPrefixes , prefixToNamespace , true);

		Assert.assertThat("Namespace to Prefixes", namespaceToPrefixes.keySet(), hasItem(NS_COMMON_TYPES));
		Assert.assertThat("Prefix to Namespaces", prefixToNamespace.values(), hasItem(NS_COMMON_TYPES));
	}
	
	@Test
	public void buildingPrefixes2() {	
		Collection<String> namespaces = new ArrayList<String>();
		namespaces.add("abc");
		
		Map<String, List<String>> namespaceToPrefixes = new HashMap<String, List<String>>();
		Map<String, String> prefixToNamespace = new HashMap<String, String>();
		
		NamespaceConvention.buildNsPrefixes(namespaces , namespaceToPrefixes , prefixToNamespace , false);

		Assert.assertThat("Namespace to Prefixes", namespaceToPrefixes.keySet(), not(hasItem(NS_COMMON_TYPES)));
        Assert.assertThat("Prefix to Namespaces", prefixToNamespace.values(), not(hasItem(NS_COMMON_TYPES)));
	}
}
