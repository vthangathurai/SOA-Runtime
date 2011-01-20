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

/**
 * Perform servicegen on an implementation.
 * 
 * @goal gen-interface
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @requiresProject true
 * @deprecated use {@link GenInterfaceFromWsdlMojo} or {@link GenInterfaceFromJavaMojo} instead.
 */
@Deprecated
public class GenInterfaceMojo extends GenInterfaceFromWsdlMojo {

	@Override
	protected void onRunTearDown() throws MojoExecutionException,
			MojoFailureException {
		super.onRunTearDown();
		StringBuilder d = new StringBuilder();
		
		d.append("\n");
		d.append("********************************************************\n");
		d.append("********************************************************\n");
		d.append("\n");
		d.append("     _                               _           _ \n");
		d.append("  __| | ___ _ __  _ __ ___  ___ __ _| |_ ___  __| |\n");
		d.append(" / _` |/ _ \\ '_ \\| '__/ _ \\/ __/ _` | __/ _ \\/ _` |\n");
		d.append("| (_| |  __/ |_) | | |  __/ (_| (_| | ||  __/ (_| |\n");
		d.append(" \\__,_|\\___| .__/|_|  \\___|\\___\\__,_|\\__\\___|\\__,_|\n");
		d.append("           |_|\n");                                     
		d.append("\n");
		d.append("  The :gen-interface goal has been deprecated!");
		d.append("\n");
		d.append("  Switch to using :gen-interface-wsdl\n");
		d.append("               or :gen-interface-java\n");
		d.append("  goals instead.\n");
		d.append("\n");
		d.append("********************************************************\n");
		d.append("********************************************************\n");
		
		getLog().error(d.toString());
	}
}
