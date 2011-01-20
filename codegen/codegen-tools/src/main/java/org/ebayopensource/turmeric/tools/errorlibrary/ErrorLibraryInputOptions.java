/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/

package org.ebayopensource.turmeric.tools.errorlibrary;

import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.turmeric.tools.errorlibrary.util.ErrorLibraryUtils;

import com.ebay.kernel.util.StringUtils;


public class ErrorLibraryInputOptions {
	
	//Option to specify Code generation type, to indicate what files need to be generated
	public static final String OPT_CODE_GEN_TYPE = "-gentype";
	// Option to specify the root path for the creation of the errorlibrary project
	public static final String OPT_PROJECT_ROOT = "-pr";
	//Option to specify the error library name
	public static final String OPT_ERRORLIBRARY_NAME = "-errorlibname";
	//Option to specify domain
	public static final String OPT_LIST_OF_DOMAIN = "-domain";
	// Option to specify the destination location for the generated artifact
	public static final String OPT_DEST_LOCATION = "-dest";
	// Option to specify the directory for the the meta-src directory
	public static final String OPT_META_SRC_DIR = "-metasrc";
	
	
	private ErrorLibraryGenType m_errorLibraryGenType;	
	private String m_projectRoot;
	private String m_errorLibraryName;
	private List<String> m_domainList;
	private String m_destLocation;
	private String m_metaSrcDir;

	public static enum ErrorLibraryGenType {
		genTypeConstants(1),
		genTypeDataCollection(2),
		genTypeErrorLibAll(3),
		genTypeCommandLineAll(4);

		private final int TYPE_VALUE;

		private ErrorLibraryGenType(int value) {
			TYPE_VALUE = value;
		}

		public int value() {
			return TYPE_VALUE;
		}


		public static ErrorLibraryGenType getErrorLibraryGenType(String errorLibraryGenTyepName) {
			ErrorLibraryGenType errorLibraryGenType = null;
			for( ErrorLibraryGenType typeGenType : ErrorLibraryGenType.values() ) {
				if(typeGenType.name().equalsIgnoreCase(errorLibraryGenTyepName)) {
					errorLibraryGenType = typeGenType;
					break;
				}
			}
			return errorLibraryGenType;
		}


	}


	public ErrorLibraryGenType getCodeGenType() {		
		return m_errorLibraryGenType;
	}

	public void setCodeGenType(ErrorLibraryGenType genType) {
		m_errorLibraryGenType = genType;
	}

	public void setProjectRoot(String projectRoot){
		m_projectRoot = projectRoot;
	}

	public String getProjectRoot(){
		return m_projectRoot;
	}
		
	public static boolean isGenTypeErrorLibrary(ErrorLibraryInputOptions errorLibraryInputOptions) {
		
		boolean isErrorLibrary = ErrorLibraryUtils.isGenTypeErrorLibrary(errorLibraryInputOptions);
				
		return isErrorLibrary;
	
	}
	

	public String toString() {
		
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append("[ ");
		strBuilder.append(OPT_CODE_GEN_TYPE    + " = " + m_errorLibraryGenType).append("\n");
		strBuilder.append(OPT_PROJECT_ROOT    + " = " + m_projectRoot ).append("\n");
		strBuilder.append(OPT_ERRORLIBRARY_NAME    + " = " + m_errorLibraryName ).append("\n");
		strBuilder.append(OPT_DEST_LOCATION    + " = " + m_destLocation ).append("\n");
		strBuilder.append(OPT_META_SRC_DIR    + " = " + m_metaSrcDir ).append("\n");
		strBuilder.append(OPT_LIST_OF_DOMAIN    + " = {" + StringUtils.join(m_domainList, ",") + "}");
		strBuilder.append("]");
		
		return strBuilder.toString();

	}

	public String getDestLocation() {
		return m_destLocation;
	}

	public void setDestLocation(String location) {
		m_destLocation = location;
	}

	public String getMetaSrcDir() {
		return m_metaSrcDir;
	}

	public void setMetaSrcDir(String location) {
		m_metaSrcDir = location;
	}

	public List<String> getDomainList() {
		if(m_domainList == null)
			m_domainList = new ArrayList<String>();
		return m_domainList;
	}
	
	public void setDomainList(List<String> domains) {
		m_domainList = domains;
	}

	public String getErrorLibraryName() {
		return m_errorLibraryName;
	}

	public void setErrorLibraryName(String libraryName) {
		m_errorLibraryName = libraryName;
	}

}


