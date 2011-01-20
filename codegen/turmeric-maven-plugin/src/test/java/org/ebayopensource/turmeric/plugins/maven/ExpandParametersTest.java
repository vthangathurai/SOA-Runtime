/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Basic non-mojo tests of functionality in AbstractServiceGeneratorMojo.
 * <p>
 * Mojo tests should go in tests cases for extended implementations.
 */
public class ExpandParametersTest {

	class TurmericStubMojo extends AbstractTurmericMojo {
		@Override
		protected void onAttachGeneratedDirectories() {
			/* do nothing */
		}
		
		@Override
		protected String getGoalName() {
			return "stub";
		}

		@Override
		public boolean needsGeneration() throws MojoExecutionException,
				MojoFailureException {
			return false;
		}
	}

	@Test
	public void testExpandParameters() throws MojoExecutionException {
		String raw = "mojo.verbose = ${mojo.verbose}";

		TurmericStubMojo stub = new TurmericStubMojo();

		stub.verbose = true;
		Assert.assertEquals("mojo.verbose = true", stub.expandParameter(raw));

		stub.verbose = false;
		Assert.assertEquals("mojo.verbose = false", stub.expandParameter(raw));
	}
}
