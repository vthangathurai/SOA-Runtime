/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library.codegen;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.tools.codegen.handler.UserResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions.TypeLibraryGenType;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;


public class TypeLibraryCodeGenContext {
	
	private TypeLibraryInputOptions m_typeLibraryInputOptions;
	private boolean m_typeLibraryGenType = false;
	
	private String m_serviceName;
	private String m_interfacePkg;
	private String m_serviceNamespace;

	
	private Map<String, Set<String>> m_typesAndDependentTypesMap;
	private Map<String, String> m_librariesNamespace;
	
	private Map<String,Set<String>> m_libraryName_SimpleType_Java_map;


	public Map<String,Set<String>> getLibraryNameSimpleTypeJavaMap() {
		if(m_libraryName_SimpleType_Java_map == null)
			m_libraryName_SimpleType_Java_map = new HashMap<String, Set<String>>();
		
		return m_libraryName_SimpleType_Java_map;
	}



	public TypeLibraryCodeGenContext(TypeLibraryInputOptions options,
			UserResponseHandler userResponseHandler) {
		m_typeLibraryInputOptions = options;
	}
	
	
	
	public void setTypeLibraryInputOptions(TypeLibraryInputOptions typeLibraryOptions) {
		m_typeLibraryInputOptions = typeLibraryOptions;
	}
	
	public TypeLibraryInputOptions getTypeLibraryInputOptions() {
		return m_typeLibraryInputOptions;
	}	
		
	
	public void setTypeLibraryGenType(boolean typeLibraryGenType ){
		m_typeLibraryGenType = typeLibraryGenType ;
	}
	
	public boolean getTypeLibraryGenType (){
		return m_typeLibraryGenType ;
	}
	
	public String getProjectRoot(){
		String path = getTypeLibraryInputOptions().getProjectRoot();
		if(CodeGenUtil.isEmptyString(path)) {
			return null;
		}
		path = TypeLibraryUtilities.normalizePath(path);
		return CodeGenUtil.toOSFilePath(path);
	}
	
	public boolean isProjectRootBlank() {
		return CodeGenUtil.isEmptyString(getTypeLibraryInputOptions().getProjectRoot());
	}
	
	public String getLibraryName(){
		return getTypeLibraryInputOptions().getTypeLibraryName();
	}
	
	
	public boolean isTypeLibraryGenType() {
		if(m_typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeAddType ||
				m_typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeDeleteType ||
				m_typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeCleanBuildTypeLibrary ||
				m_typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeIncrBuildTypeLibrary ||
				m_typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeCreateTypeLibrary) {
	
			m_typeLibraryGenType = true;
		}
				
		return m_typeLibraryGenType;
	
	}

	
	public Map<String, Set<String>> getTypesAndDependentTypesMap() {
		if(m_typesAndDependentTypesMap == null)
			m_typesAndDependentTypesMap = new HashMap<String, Set<String>>();
		
		return m_typesAndDependentTypesMap;
	}
	
	
	public Map<String, String> getLibrariesNamespace() {
		if(m_librariesNamespace == null)
			m_librariesNamespace = new HashMap<String, String>();
		return m_librariesNamespace;
	}

	public String getServiceName() {
		return m_serviceName;
	}

	public void setServiceName(String serviceName) {
		m_serviceName = serviceName;
	}

	public String getInterfacePkg() {
		return m_interfacePkg;
	}

	public void setInterfacePkg(String pkg) {
		m_interfacePkg = pkg;
	}

	public String getServiceNamespace() {
		return m_serviceNamespace;
	}

	public void setServiceNamespace(String namespace) {
		m_serviceNamespace = namespace;
	}

	/**
	 * The folder for the 'meta-src' content. Can be overridden with use of
	 * {@link TypeLibraryInputOptions#OPT_META_SRC_DIR}
	 * 
	 * @return the overridden meta-src folder, or the legacy defaulted meta-src folder based on
	 *         {@link #getProjectRoot()}/meta-src
	 */
	public String getMetaSrcFolder() {
		String path = getTypeLibraryInputOptions().getMetaSrcLocation();
		if (CodeGenUtil.isEmptyString(path)) {
			// Fall back to legacy behavior.
            String root = getProjectRoot();
            if (CodeGenUtil.isEmptyString(root)) {
                root = ".";
            }
            path = root + "/meta-src";
		}
		path = TypeLibraryUtilities.normalizePath(path);
		return CodeGenUtil.toOSFilePath(path);
	}

	/**
	 * The folder for the 'gen-meta-src' content. Can be overridden with use of
	 * {@link TypeLibraryInputOptions#OPT_META_SRC_GEN_DIR}
	 * 
	 * @return the overridden gen-meta-src folder, or the legacy defaulted gen-meta-src folder based on
	 *         {@link #getProjectRoot()}/gen-meta-src
	 */
	public String getGenMetaSrcDestFolder() {
		String path = getTypeLibraryInputOptions().getMetaSrcDestLocation();
		if (CodeGenUtil.isEmptyString(path)) {
			// Fall back to legacy behavior.
			path = getProjectRoot() + "/gen-meta-src";
		}
		return CodeGenUtil.toOSFilePath(path);
	}
	
	/**
	 * The folder for the 'gen-src' content. Can be overridden with use of
	 * {@link TypeLibraryInputOptions#OPT_JAVA_SRC_GEN_DIR}
	 * 
	 * @return the overridden gen-src folder, or the legacy defaulted gen-src folder based on
	 *         {@link #getProjectRoot()}/gen-src
	 */
	public String getGenJavaSrcDestFolder() {
		String path = getTypeLibraryInputOptions().getJavaSrcDestLocation();
		if (CodeGenUtil.isEmptyString(path)) {
			// Fall back to legacy behavior.
			path = getProjectRoot() + "/gen-src";
		}
		return CodeGenUtil.toOSFilePath(path);
	}
}
