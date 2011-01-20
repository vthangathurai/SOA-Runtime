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


/**
 * Base class for :gen-interface-* mojos.
 */
public abstract class AbstractGenInterfaceMojo extends AbstractTurmericCodegenMojo {
	
	/**
	 * Service Name
	 * 
	 * @parameter expression="${codegen.serviceName}" default-value="${project.artifactId}"
	 * @required
	 * @see org.ebayopensource.turmeric.tools.codegen.InputOptions#OPT_SRVC_NAME
	 */
	protected String serviceName = "${project.artifactId}";
	
    /**
     * Admin Name
     * 
     * @parameter expression="${codegen.adminName}" default-value="$${mojo.serviceName}"
     * @optional
     * @see org.ebayopensource.turmeric.tools.codegen.InputOptions#OPT_ADMIN_NAME
     */
    protected String adminName = "${mojo.serviceName}";

    /**
	 * The java package name for the generated service interface.
	 * 
	 * @parameter expression="${codegen.packageName}" default-value="${project.groupId}"
	 * @required
	 * @see org.ebayopensource.turmeric.tools.codegen.InputOptions#OPT_GEN_INTERFACE_PACKAGE
	 */
	protected String packageName = "${project.groupId}";

    /**
     * The java class name for the generated service interface.
     * 
     * @parameter expression="${codegen.className}" default-value="$${mojo.serviceName}"
     * @optional
     * @see org.ebayopensource.turmeric.tools.codegen.InputOptions#OPT_GEN_INTERFACE_NAME
     */
    protected String className = "${mojo.serviceName}";

    /**
	 * CodeGen Type.
	 * 
	 * @parameter expression="${codegen.generator.type}" default-value="ClientNoConfig"
	 * @required
	 * @see org.ebayopensource.turmeric.tools.codegen.InputOptions#OPT_CODE_GEN_TYPE
	 */
	protected String genType = "ClientNoConfig";
	
	/**
	 * CodeGen to specify if ObjectFactory needs to be removed while code generation
	 * <p>
	 * 
	 * @parameter expression="${codegen.generator.type}" default-value="true"
	 * @see org.ebayopensource.turmeric.tools.codegen.InputOptions#OPT_OBJECTFACT_GEN 
	 */
	protected boolean generateObjectFactory = true;
	
	@Override
	public String getGenType() {
		return genType;
	}
	
	public boolean isGenerateObjectFactory() {
		return generateObjectFactory;
	}

	public String getServiceName() {
		return serviceName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public String getAdminName() {
        return adminName;
    }
	
	public String getAdminNameLower() {
        return adminName.toLowerCase();
    }
	
	@Override
	public boolean needsGeneration() throws MojoExecutionException {
		if (super.needsGeneration()) {
			return true;
		}
		
		if(isNewerThanLastTimestamp(getProjectPomFile())) {
			getLog().info("Must Generate: Detected changed file");
			return true;
		}

		getLog().info("No need to generate.");
		return false;
	}

	@Override
	protected void onAttachGeneratedDirectories() {
		// HACK to get around CodeGen adding extra path information, even
		// though we specify the full path to generate into.
		File clientDir = new File(outputDirectory, "client");
		if (clientDir.exists()) {
			super.outputDirectory = clientDir;
		}
		super.onAttachGeneratedDirectories();
	}

	@Override
	protected void onValidateParameters() throws MojoExecutionException,
			MojoFailureException {
		super.onValidateParameters();

		// Valid service name
		if (StringUtils.isBlank(serviceName)) {
			throw new MojoExecutionException("serviceName not specified");
		}

		serviceName = expandParameter(serviceName);
		adminName = expandParameter(adminName);
		packageName = expandParameter(packageName);
        className = expandParameter(className);
		genType = expandParameter(genType);
	}
}
