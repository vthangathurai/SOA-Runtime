/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.tools.library;

/**
 * @author arajmony
 *
 */
public interface TypeLibraryConstants {
	public static final String META_INF_FOLDER = "META-INF";
	public static final String TYPES_FOLDER = "types";

	public static final String TYPE_LIBRARY_DEFAULT_VERSION = "1.0.0";
	public static final int TYPE_LIBRARY_VERSION_LEVEL = 3;
	public static final String TYPE_LIBRARY_DEFAULT_CATEGORY = "COMMON";
	public static final String TYPE_DEFAULT_VERSION = "1.0.0";
	public static final int TYPE_VERSION_LEVEL = 3;
	
	public static final String TYPE_INFORMATION_FILE_NAME = "TypeInformation.xml";
	public static final String TYPE_INFORMATION_NAMESPACE="http://www.ebayopensource.org/turmeric/common/v1/types";
	
	public static final String SOA_NAME_SPACE_INTIALS = "http://www.ebayopensource.org/turmeric";
	
	public static final String TYPE_DEPENDENCIES_FILE_NAME = "TypeDependencies.xml"; 

	public static final String TYPE_LIB_REF_PROTOCOL = "typelib://";
	public static final String MASTER_EPISODE_TURMERIC_START_COMMNENT = "Turmeric start COMMENT . Don't delete or modify this line.";
	public static final String MASTER_EPISODE_TURMERIC_END_COMMNENT = "Turmeric end COMMENT . Don't delete or modify this line.";
	public static final String SUN_JAXB_EPISODE_FILE_NAME = "sun-jaxb.episode";
	
	//constant for the type_library_project.properties file
	static final String TYPE_LIBRARY_NAME = "TYPE_LIBRARY_NAME";
	static final String TYPE_LIBRARY_VERSION = "TYPE_LIBRARY_VERSION";
	static final String TYPE_LIBRARY_NAMESPACE = "TYPE_LIBRARY_NAMESPACE";
	static final String TYPE_LIBRARY_CATEGORY = "TYPE_LIBRARY_CATEGORY";
	
	@Deprecated
	public static final String TURMERIC_NAME_SPACE=TYPE_INFORMATION_NAMESPACE;
}
