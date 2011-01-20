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
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;
import org.ebayopensource.turmeric.plugins.maven.resources.ResourceLocator;
import org.ebayopensource.turmeric.plugins.maven.resources.ResourceLocator.Location;
import org.ebayopensource.turmeric.plugins.maven.utils.CodegenCommands;
import org.ebayopensource.turmeric.plugins.maven.utils.LegacyProperties;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions;
import org.jdom.Document;
import org.jdom.Element;


/**
 * Perform codegen of the error library classes.
 * Typically provided as a set of XML files.
 * 
 * @goal gen-errorlibrary
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @requiresProject true
 */
public class GenErrorLibraryMojo extends AbstractTurmericCodegenMojo {
	private static final String LEGACY_PROP_REF = "${project.basedir}/error_library_project.properties";

	/**
	 * CodeGen Type.
	 * 
	 * @parameter expression="${codegen.generator.type}"
	 * 		 default-value="genTypeCommandLineAll"
	 * @required
	 */
	protected String genType = "genTypeCommandLineAll";
	
	/**
	 * Error Library Name.
	 * 
	 * @parameter expression="${codegen.errorlibrary.name}"
	 * 		 default-value="${project.artifactId}"
	 * @required
	 */
	protected String errorLibraryName = "${project.artifactId}";
	
	/**
	 * Comma separated list of domains for this error library.
	 * <p>
	 * If unspecified, the list of domains will be identified by
	 * looking for any ErrorData.xml files within the project
	 * resources directories, and reading the domain value out of them.
	 * Looks for a path pattern of "META-INF/errorlibrary/([^/]+)/ErrorData.xml"
	 * <p>
	 * <b>[LEGACY] Value ignored when in legacy mode, the value
	 * present in error_library_project.properties is used</b>
	 * 
	 * @parameter expression="${codegen.domains}"
	 * @optional
	 */
	protected String domains;
	
	/**
	 * [LEGACY] Domain List Properties Path Reference for this project.
	 * <p>
	 * Use syntax similar to how {@link ClassLoader#getResource(String)} expects.
	 * <p>
	 * Actual Domain List File location is looked up via {@link ResourceLocator#findResource(String)}
	 * 
	 * @parameter expression="${codegen.wsdl.path}"
	 *            default-value="META-INF/errorlibrary/$${mojo.errorLibraryName}/domain_list.properties"
	 * @optional
	 */
	protected String domainListPropPathRef = "META-INF/errorlibrary/${mojo.errorLibraryName}/domain_list.properties";
	
	@Override
	protected String getGoalName() {
		return "gen-errorlibrary";
	}
	
	@Override
	protected void addCodegenCommands(CodegenCommands commands)
			throws MojoExecutionException, MojoFailureException {
		// Remove these unused, defaulted, commands to support legacy Error Library codegen 
		commands.removeOptionPair(InputOptions.OPT_META_SRC_GEN_DIR);
		commands.removeOptionPair(InputOptions.OPT_JAVA_SRC_GEN_DIR);
		commands.removeOptionPair(InputOptions.OPT_BIN_DIR);
		commands.removeOptionPair(InputOptions.OPT_SRC_DIR);
		
		// Legacy mode is the only one that can use project root
		if(isLegacyMode()) {
			commands.add(ErrorLibraryInputOptions.OPT_PROJECT_ROOT, project.getBasedir().getAbsolutePath());
		}

		commands.add(ErrorLibraryInputOptions.OPT_DEST_LOCATION, outputDirectory.getAbsolutePath());
		commands.add(ErrorLibraryInputOptions.OPT_LIST_OF_DOMAIN, domains);
		commands.add(ErrorLibraryInputOptions.OPT_ERRORLIBRARY_NAME, errorLibraryName);
		
		if(isStandardsMode()) {
			@SuppressWarnings("unchecked")
			List<Resource> resources = project.getBuild().getResources();
			Resource firstResource = resources.get(0);
			commands.add(ErrorLibraryInputOptions.OPT_META_SRC_DIR, firstResource.getDirectory());
		}
	}
	
	@Override
	protected void onAttachGeneratedDirectories() {
		// HACK to get around CodeGen adding extra path information, even
		// though we specify the full path to generate into.
		File genSrcDir = new File(outputDirectory, "gen-src");
		if (genSrcDir.exists()) {
			super.outputDirectory = genSrcDir;
		}
		super.onAttachGeneratedDirectories();
	}
	
	@Override
	protected void onValidateParameters() throws MojoExecutionException,
			MojoFailureException {
		super.onValidateParameters();
		errorLibraryName = expandParameter(errorLibraryName);
		domains = expandParameter(domains);
		
		if (isStandardsMode()) {
			if(StringUtils.isBlank(domains)) {
				domains = findDomains();
			}
		} else if (isLegacyMode()) {
			domainListPropPathRef = expandParameter(domainListPropPathRef);
			ResourceLocator locator = new ResourceLocator(getLog(), getProject());
			Location domainListPropLocation = locator.findResource(domainListPropPathRef);
			
			LegacyProperties props = getLegacyProperties(LEGACY_PROP_REF);
			if(!props.exists()) {
				if(domainListPropLocation == null) {
					StringBuilder err = new StringBuilder();
					err.append("Unable to find LEGACY mode properties files ");
					err.append(" that contain the listOfDomains needed for ");
					err.append(" successful LEGACY mode operations.\n");
					err.append("\n  NOT FOUND: ").append(LEGACY_PROP_REF);
					err.append("\n  NOT FOUND: ${project.resources}/");
					err.append(domainListPropPathRef);
					err.append("\n  NOT FOUND: ${project.build.outputDirectory}/");
					err.append(domainListPropPathRef);
					err.append("\n  NOT FOUND: ${project.dependencies}/");
					err.append(domainListPropPathRef);
					err.append("\n");
					throw new MojoFailureException(err.toString());
				}
				props = getLegacyProperties(domainListPropLocation);
			} 

			domains = props.getProperty("listOfDomains", domains);
		}
	}
	
	/**
	 * Attempt to find any "META-INF/errorlibrary/([^/]+)/ErrorData.xml" files
	 * in the project resource directories, and read the domain out of them.
	 *  
	 * @return
	 * @throws MojoExecutionException 
	 */
	private String findDomains() throws MojoExecutionException {
		List<String> domains = new ArrayList<String>();
		
		List<File> errordatas = findProjectResourceFiles("META-INF/errorlibrary/([^/]+)/ErrorData.xml"); 
		for(File file: errordatas) {
			domains.add(readDomainFromErrorData(file));
		}
		
		if(domains.isEmpty())   {
			throw new MojoExecutionException("Unable to find list of domain "
					+ "names via ErrorData.xml lookup.  You'll likely have to "
					+ "specify the domain names yourself in the plugin "
					+ "configuration, or add the missing ErrorData.xml files");
		}
		
		return StringUtils.join(domains.iterator(), ",");
	}

	private String readDomainFromErrorData(File errorDataXml) throws MojoExecutionException {
		Document doc = parseXml(errorDataXml);
		Element root = doc.getRootElement();
		return root.getAttributeValue("domain");
	}

	/**
	 * {@inheritDoc}
	 * @see org.ebayopensource.turmeric.plugins.maven.AbstractTurmericCodegenMojo#getGenType()
	 */
	@Override
	public String getGenType() {
		return this.genType;
	}
	
	public String getErrorLibraryName() {
		return errorLibraryName;
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.ebayopensource.turmeric.plugins.maven.AbstractTurmericCodegenMojo#needsGeneration()
	 */
	@Override
	public boolean needsGeneration() throws MojoExecutionException {
		// Always needs generation
		return true;
	}
}
