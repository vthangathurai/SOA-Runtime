/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Abstract base class for source generation only. Not for use with mojos that generate source and resources at the same
 * time.
 */
public abstract class AbstractSourceGenOnlyMojo extends AbstractTurmericMojo {
	/**
	 * Location of generated java code
	 * 
	 * @parameter expression="${codegen.output.directory}"
	 *            default-value="${project.build.directory}/generated-sources/codegen"
	 * @required
	 */
	protected File outputDirectory;

	public File getOutputDirectory() {
		return outputDirectory;
	}

	@Override
	public boolean needsGeneration() throws MojoExecutionException {
		if (this.project == null) {
			throw new MojoExecutionException("Cannot execute with null project");
		}

		// Both interface and impl use an output directory
		if (outputDirectory.exists() == false) {
			getLog().info(
					"Must Generate: No output directory present: "
							+ outputDirectory);
			return true;
		}

		return false;
	}

	@Override
	protected void onAttachGeneratedDirectories() {
		// Attach the generated source directory to the maven project
		project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
	}
}
