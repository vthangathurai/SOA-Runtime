/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.errorlibrary.codegen;

import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.errorlibrary.ELDomainInfoHolder;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorLibraryInputOptions;


public class ErrorLibraryCodeGenContext {
	
	private ErrorLibraryInputOptions m_errorLibraryInputOptions;
	
	private String m_organization;
	private String m_version;	

	private Map<String, ELDomainInfoHolder> m_domainInfoMap;
	
	public ErrorLibraryCodeGenContext(ErrorLibraryInputOptions options) {
		m_errorLibraryInputOptions = options;
	}	
	
	public void setInputOptions(ErrorLibraryInputOptions errorLibraryOptions) {
		m_errorLibraryInputOptions = errorLibraryOptions;
	}
	
	public ErrorLibraryInputOptions getInputOptions() {
		return m_errorLibraryInputOptions;
	}	
	
	public String getOrganization() {
		return m_organization;
	}

	public void setOrganization(String m_organization) {
		this.m_organization = m_organization;
	}

	public String getVersion() {
		return m_version;
	}

	public void setVersion(String m_version) {
		this.m_version = m_version;
	}

	public Map<String, ELDomainInfoHolder> getDomainInfoMap() {
		if(m_domainInfoMap == null)
			 m_domainInfoMap = new HashMap<String, ELDomainInfoHolder>();
		return m_domainInfoMap;
	}

	public void setDomainInfoMap(Map<String, ELDomainInfoHolder> domainInfoMap) {
		this.m_domainInfoMap = domainInfoMap;
	}

	/**
	 * The folder for the 'meta-src' content. Can be overridden with use of
	 * {@link ErrorLibraryInputOptions#OPT_META_SRC_DIR}
	 * 
	 * @return the overridden meta-src folder, or the legacy defaulted meta-src folder based on
	 *         {@link ErrorLibraryInputOptions#getProjectRoot()}/meta-src
	 */
	public String getMetaSrcFolder() {
		String path = getInputOptions().getMetaSrcDir();
		if (CodeGenUtil.isEmptyString(path)) {
			// Fall back to legacy behavior.
			path = getInputOptions().getProjectRoot() + "/meta-src";
		}
		return CodeGenUtil.toOSFilePath(path);
	}

	/**
	 * The folder for the 'gen-src' content. Can be overridden with use of
	 * {@link ErrorLibraryInputOptions#OPT_DEST_LOCATION}
	 * 
	 * @return the overridden gen-src folder, or the legacy defaulted gen-src folder based on
	 *         {@link ErrorLibraryInputOptions#getProjectRoot()}/gen-src
	 */
	public String getGenJavaSrcDestFolder() {
		String path = getInputOptions().getDestLocation();
		if (CodeGenUtil.isEmptyString(path)) {
			// Fall back to legacy behavior.
			path = getInputOptions().getProjectRoot() + "/gen-src";
		}
		return CodeGenUtil.toOSFilePath(path);
	}
}
