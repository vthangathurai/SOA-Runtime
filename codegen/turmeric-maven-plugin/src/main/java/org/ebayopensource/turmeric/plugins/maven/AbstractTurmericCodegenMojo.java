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
import java.net.MalformedURLException;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ebayopensource.turmeric.plugins.maven.resources.ResourceLocator.Location;
import org.ebayopensource.turmeric.plugins.maven.utils.CodegenCommands;
import org.ebayopensource.turmeric.plugins.maven.utils.LegacyProperties;
import org.ebayopensource.turmeric.tools.codegen.NonInteractiveCodeGen;
import org.ebayopensource.turmeric.tools.codegen.ServiceGeneratorFacade;


/**
 * Abstract Mojo for the ServiceGeneratorFacade from codegen-tools to generate the various project level sources.
 */
public abstract class AbstractTurmericCodegenMojo extends AbstractTurmericMojo {

	/**
	 * Location of generated java code
	 * 
	 * @parameter expression="${codegen.output.directory}"
	 *            default-value="${project.build.directory}/generated-sources/codegen"
	 * @required
	 */
	protected File outputDirectory = new File("${project.build.directory}/generated-sources/codegen");

	/**
	 * Location of generated resources (not java code)
	 * 
	 * @parameter expression="${codegen.resources.output.directory}"
	 *            default-value="${project.build.directory}/generated-resources/codegen"
	 * @required
	 */
	protected File resourcesOutputDirectory = new File("${project.build.directory}/generated-resources/codegen");

	/**
	 * Optional raw command line arguments that can be passed to the Service Code Generation tooling found in codegen-tools.
	 * 
	 * @parameter
	 * @optional
	 */
	protected String[] args;

	/**
	 * Optional flag to ignore any sort of CodeGen error.
	 * 
	 * @parameter expression="${codegen.ignore.error}" default-value="false"
	 */
	private boolean ignoreCodeGenError = false;

	/**
	 * Optional flag to indicate if the mojo should operate in legacy mode
	 * or not, usually this is an indicator of the specific kind of codegen
	 * to utilize information found in various project stored files, such
	 * as service_intf_project.properties, service_metadata.properties, 
	 * service WSDL, service_impl_project.properties, ServiceConfig.xml 
	 * and ClientConfig.xml, to name a few.
	 * 
	 * @parameter expression="${codegen.legacy}" default-value="false"
	 * 
	 * @see TURMERIC-155
	 */
	private boolean legacy = false;
	
	@Override
	public boolean executeSkip() throws MojoExecutionException,
			MojoFailureException {
		if(isMojoRunningInEclipse() && isTurmericEclipsePluginEnabled() && isLegacyMode()) {
			getLog().warn("No need to generate.  " +
					"Running in legacy mode in eclipse with turmeric eclipse plugin enabled.");
			return true; // SKIP EXECUTION
		}
		
		return false;
	}
	
	/**
	 * Tests on various aspects to know if generation is needed.
	 * 
	 * @return true if need to generate, false otherwise.
	 * @throws MojoExecutionException
	 */
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

		// Both interface and impl use a resources output directory
		if (resourcesOutputDirectory.exists() == false) {
			getLog().info(
					"Must Generate: No resources output directory present: "
							+ resourcesOutputDirectory);
			return true;
		}

		return false;
	}
	
	@Override
	protected void onValidateParameters() throws MojoExecutionException,
			MojoFailureException {
		super.onValidateParameters();
		
		outputDirectory = expandFile(outputDirectory);
		resourcesOutputDirectory = expandFile(resourcesOutputDirectory);
	}

	public String[] getArgs() {
		return args;
	}
	
	/**
	 * Get the Gen Type as defined by the specific service gen process.
	 * 
	 * @return the Generate Type
	 */
	public abstract String getGenType();

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public File getResourcesOutputDirectory() {
		return resourcesOutputDirectory;
	}
	
	public boolean isLegacyMode() {
		return legacy;
	}
	
	public boolean isStandardsMode() {
		return !legacy;
	}

	@Override
	protected void onRunSetup() throws MojoExecutionException,
			MojoFailureException {
		super.onRunSetup();

		ensureDirectoryExists("Output Directory", outputDirectory);
		ensureDirectoryExists("Resources Output Directory", resourcesOutputDirectory);
		
		if (isLegacyMode()) {
			getLog().warn("Operating in LEGACY Mode");
		}
	}

	/**
	 * Primary execution point of the plugin
	 */
	@Override
	public final void onRun() throws MojoExecutionException, MojoFailureException {
		super.onRun();

		CodegenCommands commands = new CodegenCommands();
		commands.setLog(getLog());

		// Add Standard CodeGen arguments.
		
		@SuppressWarnings("unchecked")
        List<Resource> resources = project.getBuild().getResources();

		commands.addGenType(getGenType());
		commands.addSourceDir(project.getBuild().getSourceDirectory());
        if ((resources != null) && (resources.size() >= 1)) {
            commands.addResourceDir(resources.get(0).getDirectory());
        }
        commands.addGeneratedJavaOutputDir(outputDirectory);
		commands.addGeneratedResourcesOutputDir(resourcesOutputDirectory);
		commands.addClassesOutputDir(project.getBuild().getOutputDirectory());

		// Add the optional args from the mojo parameters
		commands.addFreeformArgs(args);

		// Add mojo specific args
		addCodegenCommands(commands);

		// Run the codegen
		runCodegen(commands);
	}

	private void runCodegen(CodegenCommands commands)
			throws MojoExecutionException {
		NonInteractiveCodeGen codegen = new NonInteractiveCodeGen();

		// Collect all of the project compile artifacts to add to a
		// URL ClassLoader later
		@SuppressWarnings("unchecked")
		List<Artifact> arts = project.getCompileArtifacts();
		if (arts != null) {
			for (Artifact arti : arts) {
				try {
					codegen.addExtraClassPath(arti.getFile());
				} catch (MalformedURLException e) {
					throw new MojoExecutionException("Unable to get URL for: "
							+ arti.getId());
				}
			}
		}
		
		File classesDir = new File(project.getBuild().getOutputDirectory());
		// Some JVM's will not add the directory to the classpath if it doesn't exist.
		// Make sure that the project.build.outputDirectory exists prior to wrapping
		// the Codegen with an active, valid, classloader.
		ensureDirectoryExists("project.build.outputDirectory", classesDir);
		try {
			codegen.addExtraClassPath(classesDir);
		} catch (MalformedURLException e1) {
			throw new MojoExecutionException("Unable to get URL for: " + classesDir);
		}
		
		try {
			codegen.execute(commands.getCommandArray());
		} catch (Exception e) {
			String err = "Exception executing service generator facade";
			if (ignoreCodeGenError) {
				getLog().error(err, e);
			} else {
				throw new MojoExecutionException(err, e);
			}
		}
	}

	/**
	 * Add more Codegen Commands.
	 * 
	 * @param commands
	 *            the CodegenCommands to set to, which will eventually be used to execute the
	 *            {@link ServiceGeneratorFacade}
	 * @throws MojoExecutionException
	 *             when wrapping other exceptions. Will cause build to go into error, resulting in a "BUILD ERROR"
	 *             message. The build will stop.
	 * @throws MojoFailureException
	 *             to throw new exceptions. Will cause build to go into error, resulting in a "BUILD FAILURE" message.
	 *             The build will stop.
	 */
	protected abstract void addCodegenCommands(CodegenCommands commands)
			throws MojoExecutionException, MojoFailureException;

	/**
	 * Attach {@link #outputDirectory} (and {@link #resourcesOutputDirectory}
	 * if being used) to the project.
	 */
	@Override
	protected void onAttachGeneratedDirectories() {
		// Attach the generated source directory to the maven project
		project.addCompileSourceRoot(outputDirectory.getAbsolutePath());

		// Attach the generated resources directory to the maven project
		if ((resourcesOutputDirectory != null)
				&& (resourcesOutputDirectory.exists())) {
			Resource resource = new Resource();
			resource.setDirectory(resourcesOutputDirectory.getAbsolutePath());
			resource.addInclude("**/*");
			project.addResource(resource);
		}
	}

	/**
	 * Create a {@link LegacyProperties} object representing the legacy properties from the project and the named file.
	 * 
	 * @param pathref
	 *            the reference to the file required. Can include expressions.
	 * @return the properties that were loaded.
	 * @throws MojoFailureException
	 *             if unable to find the required legacy properties file.
	 * @throws MojoExecutionException
	 *             if unable to expand pathref
	 */
	protected LegacyProperties getLegacyProperties(String pathref) throws MojoExecutionException, MojoFailureException {
		File file = new File(expandParameter(pathref));

		return new LegacyProperties(getLog(), getProject(), file);
	}
	
	/**
	 * Create a {@link LegacyProperties} object representing the legacy properties from the project and location.
	 * 
	 * @param location
	 *            the location to the file required.
	 * @return the properties that were loaded.
	 * @throws MojoFailureException
	 *             if unable to find the required legacy properties file.
	 */
	protected LegacyProperties getLegacyProperties(Location location) throws MojoFailureException {
		if(location == null) {
			throw new NullPointerException("Null location");
		}
		return new LegacyProperties(getLog(), getProject(), location.getFile());
	}
}
