/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.utils;

import org.ebayopensource.turmeric.tools.codegen.InputOptions;

/**
 * @author yayu
 *
 */
public final class TurmericMavenConstants {

	public static final String POM_PROP_KEY_SERVICE_GROUP_ID = "serviceGroupID";
	public static final String POM_PROP_KEY_SERVICE_NAME = "serviceName";
	public static final String POM_PROP_KEY_PROJECT_TYPE = "projectType";
	public static enum ProjectType {
		INTERFACE, IMPLEMENTATION, CONSUMER, TYPELIBRARY, ERRORLIBRARY
	}
	
	public static final String GENTYPE_DISPATCHER_FOR_MAVEN = "DispatcherForMaven";
	public static final String GENTYPE_CLIENT_NO_CONFIG = "ClientNoConfig";
	public static final String GENTYPE_CLEAN_BUILD_TYPE_LIBRARY = "genTypeCleanBuildTypeLibrary";
	public static final String GENTYPE_COMMAND_LINE_ALL = "genTypeCommandLineAll";
	
	/* Hardcoded paths and directories are not how things are done in 
	 * a maven plugin, use Mojo parameters.  always.
	 * 
	public static final String FOLDER_GEN_META_SRC = "gen-meta-src";
	public static final String FOLDER_META_SRC = "meta-src";
	public static final String FOLDER_GEN_SRC = "gen-src";
	public static final String FOLDER_SRC = "gen-src";
	public static final String FOLDER_GEN_SRC_SERVICE = FOLDER_GEN_SRC + "/service";
	public static final String FOLDER_GEN_SRC_CLIENT = FOLDER_GEN_SRC + "/cilent";
	public static final String[] SRC_FOLDERS_INTERFACE = {FOLDER_GEN_META_SRC, FOLDER_GEN_SRC_CLIENT};
	public static final String[] SRC_FOLDERS_IMPL = {FOLDER_GEN_META_SRC, FOLDER_GEN_SRC_SERVICE};
	public static final String[] SRC_FOLDERS_TYPELIB = {FOLDER_GEN_META_SRC, FOLDER_META_SRC, FOLDER_SRC};
	public static final String[] SRC_FOLDERS_ERRORLIB = {FOLDER_META_SRC, FOLDER_SRC};
	 */
	//FIXME use the codegen-tools.InputOptions as soon as this new option available in the repo.
	public static final String PARAM_ERROR_LIBRARY_NAME = "-errorlibname"; //the name of the error library
	
	public static final String PARAM_GENTYPE = "-genType";
	public static final String PARAM_NAMESPACE = "-namespace";
	public static final String PARAM_INTERFACE = "-interface";
	public static final String PARAM_ADMIN_NAME = "-adminname";
	public static final String PARAM_SERVICE_NAME = "-serviceName";
	public static final String PARAM_LIB_NAME = "-libname";
	//public static final String PARAM_SCV = "-scv";
	public static final String PARAM_STAGING = "-staging";
	public static final String PARAM_DEPENDENT_TYPE_LIBS = "-dependentTypeLibs";
	public static final String PARAM_SICN = "-sicn";
	/** 
	 * The need to specify the project root is bad code smell.
	 * The Generators that require this concept, should be changed
	 * to to accept fully qualified paths from the project's mojo parameters,
	 * not using hardcoded paths based on project.basedir.
	 * @deprecated Fix generator to use Mojo.parameters, not hardcoded paths.
	 */
	@Deprecated
	public static final String PARAM_PR = "-pr";
	public static final String PARAM_SRC = "-src";
	public static final String PARAM_DEST = "-dest";
	public static final String PARAM_BIN = "-bin";
	public static final String PARAM_CN = "-cn";
	public static final String PARAM_SL = "-sl";
	/**
	 * @deprecated Use {@link InputOptions#OPT_META_SRC_GEN_DIR} instead
	 */
	@Deprecated
	public static final String PARAM_MDEST = InputOptions.OPT_META_SRC_GEN_DIR;
	public static final String PARAM_WSDL = "-wsdl";
	public static final String PARAM_GIP = "-gip";
	public static final String PARAM_GIN = "-gin";
	public static final String PARAM_AVI = "-avi";
	public static final String PARAM_JDEST = "-jdest";
	public static final String PARAM_UIJ = "-uij";
	public static final String PARAM_GT = "-gt"; //for generating the unit test of gentype serviceFromWSDLImpl
	public static final String PARAM_DOMAIN = "-domain"; //command separated list of error domains
	
	
	/**
	 * 
	 */
	private TurmericMavenConstants() {
		super();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

}
