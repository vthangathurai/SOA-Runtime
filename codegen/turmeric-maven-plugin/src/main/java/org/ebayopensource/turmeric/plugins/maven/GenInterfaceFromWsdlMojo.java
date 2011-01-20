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
import org.codehaus.plexus.util.StringUtils;
import org.ebayopensource.turmeric.plugins.maven.resources.ResourceLocator;
import org.ebayopensource.turmeric.plugins.maven.resources.ResourceLocator.Location;
import org.ebayopensource.turmeric.plugins.maven.utils.CodegenCommands;
import org.ebayopensource.turmeric.plugins.maven.utils.LegacyProperties;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;

/**
 * Generate an interface from a WSDL file.
 * 
 * @goal gen-interface-wsdl
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @requiresProject true
 */
public class GenInterfaceFromWsdlMojo extends AbstractGenInterfaceMojo {
	private static final String LEGACY_PROP_REF = "${project.basedir}/service_intf_project.properties";
	
	/**
	 * WSDL Location
	 * 
	 * @parameter default-value="META-INF/soa/services/wsdl/$${mojo.serviceName}/$${mojo.serviceName}.wsdl"
	 * @required
	 */
	protected String wsdlLocation = "META-INF/soa/services/wsdl/${mojo.serviceName}/${mojo.serviceName}.wsdl";
	
    /**
     * TypeDependencies.xml Location
     * 
     * @parameter default-value="META-INF/$${mojo.serviceName}/TypeDependencies.xml"
     * @required
     */
    protected String typeDependenciesRef = "META-INF/${mojo.serviceName}/TypeDependencies.xml";
	
	/**
	 * Internal full path reference to the {@link File} referenced in wsdlLocation,
	 * after lookup within the project's defined build resource directories.
	 */
	private File wsdlFile;
	
	/**
	 * Type Mapping File Location
	 * 
	 * @parameter expression="${codegen.type.mapping.location}"
	 *            default-value="META-INF/soa/common/config/$${mojo.serviceName}"
	 * @required
	 */
	private String typeMappingLocation = "META-INF/soa/common/config/${mojo.serviceName}";
	
	/**
	 * Type Mapping Generated Package Namespace
	 * 
	 * @parameter expression="${codegen.type.mapping.packageName}"
	 * @optional
	 * @deprecated should use more robust mapping structure
	 */
	private String typeMappingPackageName;
	
	/**
	 * Enable Namespace Folding
	 * 
	 * @parameter expression="${codegen.namespace.folding}"
	 *            default-value="false"
	 * @optional
	 * @see org.ebayopensource.turmeric.tools.codegen.InputOptions#OPT_ENABLEDNAMESPACE_FOLDING
	 */
	private boolean enableNamespaceFolding = false;
	
	/**
	 * Allow codegen to compile
	 * 
	 * @parameter expression="${codegen.compile}"
	 *            default-value="true"
	 * @optional
	 * @see org.ebayopensource.turmeric.tools.codegen.InputOptions#OPT_NO_COMPILE
	 */
	private boolean compile = true;
	
	/**
     * Generated shared consumer.
     * 
     * @parameter expression="${codegen.shared.consumer}"
     *            default-value="false"
     * @optional
     * @see org.ebayopensource.turmeric.tools.codegen.InputOptions#OPT_NO_COMPILE
     */
	private boolean sharedConsumer = false;
	
	/**
	 * The package for the shared consumer.
	 * <p>
	 * Note: only used if {@link #sharedConsumer} is set to true.
	 * 
	 * @parameter expression="${codegen.shared.consumer.package}
	 *            default-value="$${mojo.packageName}.$${mojo.adminNameLower}.gen"
	 * @optional
	 */
	private String sharedConsumerPackage = "${mojo.packageName}.${mojo.adminNameLower}.gen";
	
	@Override
	protected String getGoalName() {
		return "gen-interface-wsdl";
	}

	@Override
	protected void addCodegenCommands(CodegenCommands commands)
			throws MojoExecutionException, MojoFailureException {
		getLog().info("Processing WSDL based Interface Project: " + this.project.getArtifactId());
		
		// Always required command
		commands.setServiceName(serviceName);
		
		// Use specified wsdl, to get around Codegen internal use of 'meta-src'
		// which breaks the maven build.
		commands.add(InputOptions.OPT_WSDL, wsdlFile.getAbsolutePath());

		if (isLegacyMode()) {
			// use project root only
			commands.add(InputOptions.OPT_PROJECT_ROOT, getProject() .getBasedir().getAbsolutePath());
		} else if (isStandardsMode()) {
		    if(!compile) {
		        commands.addSingle(InputOptions.OPT_NO_COMPILE);
		    }
		    // Always required command
	        commands.setAdminName(serviceName);
		    commands.add(InputOptions.OPT_GEN_INTERFACE_NAME, className);
			commands.add(InputOptions.OPT_GEN_INTERFACE_PACKAGE, packageName);
			
            if (sharedConsumer) {
                commands.addSingle(InputOptions.OPT_GEN_SHARED_CONSUMER);
                commands.add(InputOptions.OPT_PACKAGE_SHARED_CONSUMER, sharedConsumerPackage);
            }

			commands.add(InputOptions.OPT_OBJECTFACT_GEN, Boolean.toString(!generateObjectFactory));
			
			getLog().debug("ENABLE NAMESPACE FOLDING: "+ enableNamespaceFolding);
			if(enableNamespaceFolding) {
			    commands.addSingle(InputOptions.OPT_ENABLEDNAMESPACE_FOLDING);
			}
	
			// TODO: The namespace should be an input argument.
	
			if (StringUtils.isNotBlank(typeMappingPackageName)) {
				commands.add(InputOptions.OPT_NS_2_PKG,
						"http://www.ebayopensource.org/turmeric/common/v1/types="
								+ typeMappingPackageName);
			}
		}
	}

	public String getWsdlLocation() {
		return wsdlLocation;
	}

	@Override
	public boolean needsGeneration() throws MojoExecutionException {
		if (super.needsGeneration()) {
			return true;
		}

		// an interface project
		final File typeMappingFile = new File(resourcesOutputDirectory, typeMappingLocation);
		if (typeMappingFile.exists() == false) {
			getLog().info("Must Generate: TypeMapping file not present: " + typeMappingFile);
			return true;
		}
		
		if(isNewerThanLastTimestamp(typeMappingFile, wsdlFile)) {
			getLog().info("Must Generate: Detectec modified files recently");
			return true;
		}
		
		getLog().info("Service no changes detected");
		return false;
	}

	@Override
	protected void onValidateParameters() throws MojoExecutionException,
			MojoFailureException {
		super.onValidateParameters();
		
		//make sure adminName is converted to lower case
		/*if (sharedConsumer){
			sharedConsumerPackage = packageName+"."+adminName.toLowerCase()+".gen";		
		}*/
		typeMappingLocation = toOS(expandParameter(typeMappingLocation));
		wsdlLocation = toOS(expandParameter(wsdlLocation));
		sharedConsumerPackage = expandParameter(sharedConsumerPackage);
		typeDependenciesRef = expandParameter(typeDependenciesRef);
		
		ResourceLocator locator = new ResourceLocator(getLog(), getProject());
		Location typeDefLocation = locator.findResource(typeDependenciesRef);
		
		if(typeDefLocation == null) {
		    getLog().warn("No TypeDependencies.xml <typeDependenciesRef/> file"
                            + " found among project resources and dependencies: " + typeDependenciesRef);
		}
		
		if (isLegacyMode()) {
			LegacyProperties props = getLegacyProperties(LEGACY_PROP_REF);
			props.assertLegacyFileRequired();

			String intfSrcType = props.getProperty("interface_source_type", "");
			if (!"WSDL".equals(intfSrcType)) {
				StringBuilder err = new StringBuilder();
				err.append("Unexpected 'interface_source_type' value of ");
				if (intfSrcType == null) {
					err.append("<null>");
				} else {
					err.append('[').append(intfSrcType).append(']');
				}
				err.append(", expected [WSDL].  ");
				err.append("Can't use :gen-interface-wsdl goal, maybe you ");
				err.append("want to use :gen-interface-java goal?");

				throw new MojoExecutionException(err.toString());
			}
			
			// Now store a few values to aid in unit testing later.
			String sicn = props.getProperty("service_interface_class_name", 
					packageName + '.' + serviceName);
			int lastDot = sicn.lastIndexOf('.');
			if (lastDot > 0) {
				packageName = sicn.substring(0, lastDot);
				serviceName = sicn.substring(lastDot + 1);
			}
		}

		// Validate that the wsdl file is present.
		wsdlFile = findResourceFile(wsdlLocation);
		if (wsdlFile == null) {
			// Fatal error.
			throw new MojoExecutionException("Unable to find required WSDL file resource: " + wsdlLocation);
		}
		
	}
	
	@Override
	protected void onRunSetup() throws MojoExecutionException,
			MojoFailureException {
		super.onRunSetup();

		File typeFile = new File(super.resourcesOutputDirectory, typeMappingLocation);
		ensureDirectoryExists("Type Mapping Output Location", typeFile.getParentFile());
	}

	public String getTypeMappingPackageName() {
		return typeMappingPackageName;
	}

	public String getTypeMappingLocation() {
		return typeMappingLocation;
	}
}
