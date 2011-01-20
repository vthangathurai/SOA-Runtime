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
import org.apache.maven.plugin.MojoFailureException;
import org.ebayopensource.turmeric.plugins.maven.utils.CodegenCommands;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;


/**
 * Generate a service interface based on a Java source file.
 * 
 * @goal gen-interface-java
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @requiresProject true
 */
public class GenInterfaceFromJavaMojo extends AbstractGenInterfaceMojo {

	private File sourceFile;
	
	@Override
	protected String getGoalName() {
		return "gen-interface-java";
	}
	
	@Override
	protected void addCodegenCommands(CodegenCommands commands)
			throws MojoExecutionException, MojoFailureException {
		commands.setServiceName(serviceName);
		commands.add(InputOptions.OPT_GEN_INTERFACE_PACKAGE, packageName);
		commands.add(InputOptions.OPT_GEN_INTERFACE_NAME, className);
		commands.add(InputOptions.OPT_INTERFACE, packageName + "." + serviceName);
	}

	@Override
	public boolean needsGeneration() throws MojoExecutionException {
		if (super.needsGeneration()) {
			return true;
		}

		if(isNewerThanLastTimestamp(sourceFile)) {
			getLog().info("Must Generate: Source java file modified recently: " + sourceFile);
			return true;
		}

		getLog().info("Source java file has not been updated recently.");
		return false;
	}

	@Override
	protected void onValidateParameters() throws MojoExecutionException,
			MojoFailureException {
		super.onValidateParameters();

		File srcDir = new File(project.getBuild().getSourceDirectory());
		sourceFile = new File(srcDir, toOS(packageName.replace('.', '/') + '/' + serviceName) + ".java");
	}
}
