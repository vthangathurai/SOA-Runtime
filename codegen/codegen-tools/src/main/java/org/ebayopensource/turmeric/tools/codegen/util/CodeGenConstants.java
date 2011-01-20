/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import org.ebayopensource.turmeric.runtime.common.types.SOACommonConstants;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;


/**
 * Maintains all code generation tool constants
 *
 * @author rmandapati
 */
public interface CodeGenConstants {

	public static final String GEN_SRC_FOLDER = "gen-src";
	public static final String GEN_META_SRC_FOLDER = "gen-meta-src";
	public static final String META_SRC_FOLDER = "meta-src";
	public static final String META_INF_FOLDER = "META-INF";
	public static final String TYPES_FOLDER = "types";

	public static final String CLIENT_GEN_FOLDER = "client";
	public static final String SERVICE_GEN_FOLDER = "service";
	public static final String COMMON_GEN_FOLDER = "common";
	public static final String TEST_GEN_FOLDER = "gen-test";

	public static final String DEFAULT_CLIENT_NAME = "default";
	public static final String ASYNC_NAME = "Async";
	public static final String DOT = ".";

	public static final String ALL = "all";

	// Config related
	public static final int NUM_OF_APP_RETRIES = 1;
	public static final int NUM_OF_CONN_RETRIES = 3;

	public static final String DEFAULT_SERVICE_URL = "http://localhost:8080/";

	public static final String SOA_COMMON_TYPES_PKG = ErrorMessage.class.getPackage().getName();
	public static final String SOA_COMMON_TYPES_NS = SOACommonConstants.SOA_TYPES_NAMESPACE;

	//The following {  are the names of the properties stored in the service_metadata.properties file
	//For adding any new properties to this file, pls define a constant here
	public static final String SERVICE_NAME = "service_name";
	public static final String ADMIN_NAME = "admin_name";
	public static final String SERVICE_VERSION = "service_version";
	public static final String SERVICE_INTF_CLASS_NAME = "service_interface_class_name";
	public static final String SERVICE_LAYER = "service_layer";
	public static final String ORIGINAL_WSDL_URI = "original_wsdl_uri";
	public static final String SERVICE_NAMESPACE = "service_namespace";
	String SERVICE_NS_PART = "service_namespace_part";
	String SVC_DOMAIN_NAME = "domainName";
    // }

    /* The following are the names of the properties stored in the service_intf_project.properties file
     * For adding any new properties to this file, pls define a constant here
     */
	public static final String SERVICE_LOCATION = "service_location";
	public static final String INTERFACE_SOURCE_TYPE  = "interface_source_type";
	public static final String NS_2_PKG = "ns2pkg";
	public static final String ENABLE_NAMESPACE_FOLDING = "enabledNamespaceFolding";
	public static final String PUBLIC_SERVICE_NAME = "publicServiceName";
	String CTNS = "ctns";
	String SVC_INTF_PROJECT_PROPERTIES_FILE_VERSION = "sipp_version";
	String SVC_CONSUMER_VERSION = "smp_version";
	String PROPERTY_SHARED_CONSUMER_SHORTER_PATH = "short_path_for_shared_consumer";

	/*
	 * These constant are defined for consumerProperties file which is created by plugin
	 */
	public static final String SERVICE_CONSUMER_PROPS_FILE = "service_consumer_project.properties";
	public static final String ENVMAPPER_PROP = "envMapper";
	String CONS_PROJECT_PROPERTIES_FILE_VERSION = "scpp_version";
	String NO_BASE_CONSUMERPROP = "not_generate_base_consumer";
	
	/**
	 * This file is created by plugin and has version info
	 */
	String SERVICE_IMPL_PROPERTIES_FILE = "service_impl_project.properties";
	String CONS_SERVICE_IMPL_VERSION ="simp_version";
			

	/*
	 * for codegn logging
	 */
	public static final String CODEGEN_LOG_CONFIG = "CODEGEN_LOG_CONFIG"; // name of the environment variable

	//Constants related to poll method
	String POLL_METHOD_NAME ="poll";
	String POLL_METHOD_PARAM_BLOCK = "block";
	String POLL_METHOD_PARAM_PARTIAL = "partial";
	
	String MIN_VERSION_FOR_GENERATING_POLL_METHOD = "2.1.1";
	
    String PKG_PARAM = "@pkg@";
	String NAMESPACE_PARAM = "@ns@";
	String WSDL_2_JAVA_NS_TO_PKG_PATTERN = NAMESPACE_PARAM + "=" + PKG_PARAM;

}
