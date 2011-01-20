/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.standard;

import org.ebayopensource.turmeric.plugins.maven.BaseTurmericMojoTestCase;
import org.ebayopensource.turmeric.plugins.maven.GenTypeLibraryMojo;
import org.junit.Test;

/**
 * Test basic generation of the type library.
 */
public class GenStandardSampleTypeLibraryTest extends
		BaseTurmericMojoTestCase<GenTypeLibraryMojo> {
	@Override
	public String getTestMojoDirName() {
		return "standard/sampleTypeLibrary";
	}

	@Override
	public String getTestMojoGoal() {
		return "gen-typelibrary";
	}

	@Test
	public void testLegacyGenTypeLibrary() throws Exception {
		GenTypeLibraryMojo mojo = createMojo();
		testProjectExecuteMojo(mojo);
		assertPostCodegenRules(mojo);
	}
}
