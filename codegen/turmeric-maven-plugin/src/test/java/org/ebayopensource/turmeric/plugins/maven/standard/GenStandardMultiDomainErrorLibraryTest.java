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
import org.ebayopensource.turmeric.plugins.maven.GenErrorLibraryMojo;
import org.junit.Test;

/**
 * Test generation of a multi-domain error library.
 */
public class GenStandardMultiDomainErrorLibraryTest extends
		BaseTurmericMojoTestCase<GenErrorLibraryMojo> {
	@Override
	public String getTestMojoDirName() {
		return "standard/multiDomainErrorLibrary";
	}

	@Override
	public String getTestMojoGoal() {
		return "gen-errorlibrary";
	}

	@Test
	public void testStandardGenErrorLibrary() throws Exception {
		GenErrorLibraryMojo mojo = createMojo();
		testProjectExecuteMojo(mojo);
		assertPostCodegenRules(mojo);
	}

}
