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
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ebayopensource.turmeric.plugins.maven.utils.CodegenCommands;
import org.ebayopensource.turmeric.plugins.maven.utils.LegacyProperties;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions;


/**
 * Perform servicegen on an implementation.
 * 
 * @goal gen-typelibrary
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @requiresProject true
 * @author yayu
 */
public class GenTypeLibraryMojo extends AbstractTurmericCodegenMojo {
	private static final String LEGACY_PROP_REF = "${project.basedir}/type_library_project.properties";
	
	/**
	 * CodeGen Type.
	 * 
	 * @parameter expression="${codegen.generator.type}" 
	 * 			  default-value="genTypeCleanBuildTypeLibrary"
	 * @required
	 */
	protected String genType = "genTypeCleanBuildTypeLibrary";

	/**
	 * The Typelib Name
	 * 
	 * @parameter expression="${codegen.typelib.name}" 
	 * 			  default="${project.artifactId}"
	 * 
	 */
	private String typelibName = "${project.artifactId}";
	
	/**
	 * The Typelib Namespace
	 * 
	 * @parameter expression="${codegen.typelib.namespace}" 
	 *
	 */
	private String typelibNamespace = "http://www.ebayopensource.org/turmeric/services";

	/**
	 * The Typelib Category
	 * 
	 * @parameter expression="${codegen.typelib.category}" 
	 * 			  default="COMMON"
	 * 
	 */
	private String typelibCategory = "COMMON";

	/**
	 * The Typelib Version
	 * 
	 * @parameter expression="${codegen.typelib.version}" 
	 * 			  default="${project.version}"
	 * 
	 */
	private String typelibVersion = "${project.version}";

	@Override
	protected String getGoalName() {
		return "gen-typelibrary";
	}

	@Override
	protected void addCodegenCommands(CodegenCommands commands)
			throws MojoExecutionException, MojoFailureException {
		// Remove commands not relevant to legacy Type Library codegen 
		commands.removeOptionPair(InputOptions.OPT_BIN_DIR);
		commands.removeOptionPair(InputOptions.OPT_SRC_DIR);
		
		// Fix TypeLibraryCodeGenBuilder to use Mojo parameters instead of project basedir
		commands.add(TypeLibraryInputOptions.OPT_PROJECT_ROOT, project.getBasedir().getAbsolutePath());
		
		if (isLegacyMode()){
			commands.removeOptionPair(InputOptions.OPT_META_SRC_GEN_DIR);
			commands.removeOptionPair(InputOptions.OPT_JAVA_SRC_GEN_DIR);
			List<Resource> resources = project.getBuild().getResources();
			for (Resource resource : resources) {
				if (resource.getDirectory().contains("/meta-src")){
					commands.add(TypeLibraryInputOptions.OPT_META_SRC_DIR,resource.getDirectory());	
					break;
				}
			}
			
			
		}
		// Needed, both for legacy and standard
		commands.add(TypeLibraryInputOptions.OPT_LIBRARY_NAME, typelibName);
		commands.add(TypeLibraryInputOptions.OPT_LIBRARY_CATEGORY, typelibCategory);
		commands.add(TypeLibraryInputOptions.OPT_LIBRARY_VERSION, typelibVersion);
		commands.add(TypeLibraryInputOptions.OPT_LIBRARY_NAMESPACE, typelibNamespace);
		
		//if(isStandardsMode()) {
			@SuppressWarnings("unchecked")
			List<Resource> resources = project.getBuild().getResources();
			if (isStandardsMode()){
				Resource firstResource = resources.get(0);
				commands.add(TypeLibraryInputOptions.OPT_META_SRC_DIR, firstResource.getDirectory());
			}
			StringBuilder cp = new StringBuilder();
			boolean needDelim = false;
			for (Resource resource : resources) {
				if (needDelim) {
					cp.append(File.pathSeparator);
				}
				cp.append(resource.getDirectory());
				needDelim = true;
			}
			cp.append(File.pathSeparator);
			cp.append(project.getBuild().getOutputDirectory());
			commands.add(TypeLibraryInputOptions.OPT_ADD_CP_TO_XJC, cp.toString());
		//}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.ebayopensource.turmeric.plugins.maven.AbstractTurmericCodegenMojo#getGenType()
	 */
	@Override
	public String getGenType() {
		return this.genType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.ebayopensource.turmeric.plugins.maven.AbstractTurmericCodegenMojo#needsGeneration()
	 */
	@Override
	public boolean needsGeneration() throws MojoExecutionException {
		// Always needs generation?
		return true;
	}
	
	@Override
	protected void onValidateParameters() throws MojoExecutionException,
			MojoFailureException {
		super.onValidateParameters();
		genType = expandParameter(genType);
		
		if(isLegacyMode()) {
			LegacyProperties props = getLegacyProperties(LEGACY_PROP_REF);
			typelibName = props.getProperty("TYPE_LIBRARY_NAME", null);
			typelibNamespace = props.getProperty("TYPE_LIBRARY_NAMESPACE", null);
			typelibCategory = props.getProperty("TYPE_LIBRARY_CATEGORY", null);
			typelibVersion = props.getProperty("TYPE_LIBRARY_VERSION", null);
			
		} else {
			typelibName = expandParameter(typelibName);
			typelibNamespace = expandParameter(typelibNamespace);
			typelibCategory = expandParameter(typelibCategory);
			typelibVersion = expandParameter(typelibVersion);			
		}
		validateRequiredParameters();
		
	}
	

	
	private void validateRequiredParameters() throws MojoExecutionException{
		StringBuilder errorBuilder = new StringBuilder();
		if (StringUtils.isEmpty(typelibName)) 
		{
			errorBuilder.append("typelibName is missing or invalid.\n");
		}
		if (StringUtils.isEmpty(typelibNamespace)) 
		{
			errorBuilder.append("typelibNamespace is missing or invalid.\n");
		}
		if (StringUtils.isEmpty(typelibCategory)) 
		{
			errorBuilder.append("typelibCategory is missing or invalid.\n");
		}
		if (StringUtils.isEmpty(typelibVersion)) 
		{
			errorBuilder.append("typelibVersion is missing or invalid.\n");
		}
		if (!errorBuilder.toString().isEmpty()){
			throw new MojoExecutionException(errorBuilder.toString());
		}
	}
}
