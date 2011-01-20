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

import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.turmeric.tools.codegen.InputOptions;




/**
 * @author arajmony
 *
 */
public class TypeLibraryInputOptions {
	
	public static final String OPT_HELP = "-help";
	public static final String DEFAULT_TYPE_VERSION = "1.0.0";
	//Option to specify Code generation type, to indicate what files need to be generated
	public static final String OPT_CODE_GEN_TYPE = "-gentype";
	// Option to specify the root path for the creation of the typelibrary project
	public static final String OPT_PROJECT_ROOT = "-pr";
	// Option to specify where to place generated java source files
	public static final String OPT_JAVA_SRC_GEN_DIR = InputOptions.OPT_JAVA_SRC_GEN_DIR;
	// Option to specify where to place generated meta source files
	public static final String OPT_META_SRC_GEN_DIR = InputOptions.OPT_META_SRC_GEN_DIR;
	// Option to specify where to find meta-src files
	public static final String OPT_META_SRC_DIR = "-metasrc";
	// Option to specify the name of the typelibrary project
	public static final String OPT_LIBRARY_NAME = "-libname";
	// Option to specify the library version
	public static final String OPT_LIBRARY_VERSION = "-libVersion";
	// Option to specify the library namespace
	public static final String OPT_LIBRARY_NAMESPACE = "-libNamespace";
	// Option to specify the library category
	public static final String OPT_LIBRARY_CATEGORY = "-libCategory";
	// Option to specify to output  more debug messages
	public static final String OPT_VERBOSE = "-verbose";
	// Option to specify to suppress any prompt messages
	public static final String OPT_DONT_PROMPT = "-dontprompt";
	// Option to specify the codegen logging config file
	public static final String OPT_LOG_CONFIG_FILE ="-lcf";
    // Option to specify the xsd type
	public static final String OPT_XSD_TYPE ="-type";
    // Option to specify the staging area
	public static final String OPT_STAGING_AREA ="-staging";
	// Option to specify additional classpath to XJC
	public static final String OPT_ADD_CP_TO_XJC ="-classPathToXJC";
	// Option to specify that additional build classpath is automatically added to xjc.
	public static final String OPT_ADD_BUILD_CP_TO_XJC ="-addBuildClassPathToXJC";

	// Option to specify the dependent Libraries
	public static final String OPT_DEPENDENT_LIBS ="-dependenttypelibs"; 
	
	
	/**   options for the V4 gentype             ***/
	// Option to specify the wsdl file 
	public static final String OPT_V4_WSDL_LOCATION = "-wsdl";
	// Option to specify the destination location for the generated beans
	public static final String OPT_V4_DEST_LOCATION = "-dest";
	// Option to specify the package for the generated beans  TODO don't expose this until it should be more like -ns2pkg 
	public static final String OPT_V4_NS_2_PKG = "-pkg";
	// Option to specify the catalog file 
	public static final String OPT_V4_CATALOG_FILE = "-catalog";
	
	
	private TypeLibraryGenType m_typeLibraryGenType;	
	private String m_projectRoot;
	private String m_javaSrcDestLocation;	
	private String m_metaSrcDestLocation;
	private String m_metaSrcLocation;
	private String m_typeLibraryName;
	private List<String> m_xsdFiles;
	private boolean m_verbose = false;	
	private boolean m_help = false;	
	private boolean m_isDontPrompt = false;
	private boolean m_addBuildClassPathToXJC = true;
	private String m_logConfigFile;
	private String m_stagingLocation;
	private String m_dependentTypeLibs;
	private String m_libraryVersion;
	private String m_libraryNamespace;
	private String m_libraryCategory;
	private String m_additionalClassPathToXJC;
	
	private String m_v4WsdlLocation;
	private String m_v4DestLocation;
	private String m_v4NS2Pkg;
	private String m_v4Catalog;

	public static enum TypeLibraryGenType {
		genTypeCleanBuildTypeLibrary(1),
		genTypeDeleteType(2),
		genTypeIncrBuildTypeLibrary(3),
		genTypeAddType(4),
		genTypeCreateTypeLibrary(5) ,
		V4(6)
		;

		private final int TYPE_VALUE;

		private TypeLibraryGenType(int value) {
			TYPE_VALUE = value;
		}

		public int value() {
			return TYPE_VALUE;
		}


		public static TypeLibraryGenType getTypeLibraryGenType(String typeLibraryGenTyepName) {
			TypeLibraryGenType typeLibraryGenType = null;
			for( TypeLibraryGenType typeGenType : TypeLibraryGenType.values() ) {
				if(typeGenType.name().equalsIgnoreCase(typeLibraryGenTyepName)) {
					typeLibraryGenType = typeGenType;
					break;
				}
			}
			return typeLibraryGenType;
		}


	}


	public TypeLibraryGenType getCodeGenType() {		
		return m_typeLibraryGenType;
	}

	public void setCodeGenType(TypeLibraryGenType genType) {
		m_typeLibraryGenType = genType;
	}

	public void setProjectRoot(String projectRoot){
		m_projectRoot = projectRoot;
	}

	public String getProjectRoot(){
		return m_projectRoot;
	}

	public String getJavaSrcDestLocation() {
		return m_javaSrcDestLocation;
	}

	public void setJavaSrcDestLocation(String srcDestLocation) {
		m_javaSrcDestLocation = srcDestLocation;
	}

	public String getMetaSrcDestLocation() {
		return m_metaSrcDestLocation;
	}

	public void setMetaSrcDestLocation(String srcDestLocation) {
		m_metaSrcDestLocation = srcDestLocation;
	}

	public String getMetaSrcLocation() {
		return m_metaSrcLocation;
	}

	public void setMetaSrcLocation(String metaSrcLocation) {
		m_metaSrcLocation = metaSrcLocation;
	}

	public void setTypeLibraryName(String libraryName){
		m_typeLibraryName = libraryName;
	}

	public String getTypeLibraryName(){
		return m_typeLibraryName;
	}
	
	public boolean isVerbose() {
		return m_verbose;
	}

	public void setVerbose(boolean verbose) {
		this.m_verbose = verbose;
	}
	
	public boolean isDontPrompt() {
		return m_isDontPrompt;
	}


	public void setIsDontPrompt(boolean dontPrompt) {
		m_isDontPrompt = dontPrompt;
	}

	public boolean isHelp() {
		return m_help;
	}

	public void setHelp(boolean help) {
		this.m_help = help;
	}
	
	public void setLogConfigFile(String filePath){
		m_logConfigFile = filePath;
	}
	
	public String getLogConfigFile() {
		return m_logConfigFile;
	}


	public List<String> getXsdTypes() {
		if(m_xsdFiles == null)
			m_xsdFiles = new ArrayList<String>();
		return m_xsdFiles;
	}


	
	public static boolean isGenTypeTypeLibrary(TypeLibraryInputOptions typeLibraryInputOptions) {
		boolean isTypeLibrary = false;
		
		if(typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeAddType ||
				typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeDeleteType ||
				typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeCleanBuildTypeLibrary ||
				typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeIncrBuildTypeLibrary ||
				typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.genTypeCreateTypeLibrary || 
				typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.V4 ) {
	
			isTypeLibrary = true;
		}
				
		return isTypeLibrary;
	
	}
	
	
	
	public static boolean isPureTypeLibraryGenType(TypeLibraryInputOptions typeLibraryInputOptions){
		boolean isPureTypeLibraryGenType = true; 
		if(typeLibraryInputOptions.getCodeGenType() == TypeLibraryGenType.V4)
			isPureTypeLibraryGenType = false;
		
		return isPureTypeLibraryGenType;
	}
	
	
	
	public void setStagingArea(String stagingLocation){
		m_stagingLocation = stagingLocation;
	}
	
	public String getStaging() {
		return m_stagingLocation;
	}
	public void setDependentTypeLibs(String dependentTypeLibs){
		m_dependentTypeLibs = dependentTypeLibs;
	}
	
	public String getDependentTypeLibs() {
		return m_dependentTypeLibs;
	}

	/**
	 * @return the m_libraryNamespace
	 */
	public String getLibraryNamespace() {
		return m_libraryNamespace;
	}

	/**
	 * @param namespace the m_libraryNamespace to set
	 */
	public void setLibraryNamespace(String namespace) {
		m_libraryNamespace = namespace;
	}

	/**
	 * @return the m_libraryVersion
	 */
	public String getLibraryVersion() {
		return m_libraryVersion;
	}

	/**
	 * @param version the m_libraryVersion to set
	 */
	public void setLibraryVersion(String version) {
		m_libraryVersion = version;
	}

	/**
	 * @return the m_libraryCategory
	 */
	public String getLibraryCategory() {
		return m_libraryCategory;
	}

	/**
	 * @param category the m_libraryCategory to set
	 */
	public void setLibraryCategory(String category) {
		m_libraryCategory = category;
	}

	
	
	/**
	 * @return the m_additionalClassPathToXJC
	 */
	public String getAdditionalClassPathToXJC() {
		return m_additionalClassPathToXJC;
	}

	/**
	 * @param classPathToXJC the m_additionalClassPathToXJC to set
	 */
	public void setAdditionalClassPathToXJC(String classPathToXJC) {
		m_additionalClassPathToXJC = classPathToXJC;
	}
	
	public boolean isAddBuildClassPathToXJC() {
		return m_addBuildClassPathToXJC;
	}
	
	public void setAddBuildClassPathToXJC(String flag) {
		String f = flag.toLowerCase();
		boolean enabled = ("true".equals(f) || "yes".equals(f) || "on".equals(f));
		setAddBuildClassPathToXJC(enabled);
	}
	
	public void setAddBuildClassPathToXJC(boolean enabled) {
		this.m_addBuildClassPathToXJC = enabled;
	}

	public String getV4WsdlLocation() {
		return m_v4WsdlLocation;
	}

	public void setV4WsdlLocation(String wsdlLocation) {
		m_v4WsdlLocation = wsdlLocation;
	}

	public String getV4DestLocation() {
		return m_v4DestLocation;
	}

	public void setV4DestLocation(String destLocation) {
		m_v4DestLocation = destLocation;
	}

	public String getV4Pkg() {
		return m_v4NS2Pkg;
	}

	public void setV4Pkg(String pkg) {
		m_v4NS2Pkg = pkg;
	}

	public String getV4Catalog() {
		return m_v4Catalog;
	}

	public void setV4Catalog(String catalog) {
		m_v4Catalog = catalog;
	}

	public String toString() {
		
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append("[ ");
		strBuilder.append(OPT_CODE_GEN_TYPE    + " = " + m_typeLibraryGenType).append("\n");
		strBuilder.append(OPT_PROJECT_ROOT    + " = " + m_projectRoot ).append("\n");
		strBuilder.append(OPT_JAVA_SRC_GEN_DIR    + " = " + m_javaSrcDestLocation ).append("\n");
		strBuilder.append(OPT_META_SRC_GEN_DIR    + " = " + m_metaSrcDestLocation ).append("\n");
		strBuilder.append(OPT_META_SRC_DIR    + " = " + m_metaSrcLocation ).append("\n");
		strBuilder.append(OPT_LIBRARY_NAME    + " = " + m_typeLibraryName ).append("\n");
		strBuilder.append(OPT_LIBRARY_VERSION    + " = " + m_libraryVersion ).append("\n");
		strBuilder.append(OPT_LIBRARY_NAMESPACE    + " = " + m_libraryNamespace ).append("\n");
		strBuilder.append(OPT_LIBRARY_CATEGORY    + " = " + m_libraryCategory ).append("\n");
		strBuilder.append(OPT_VERBOSE    + " = " + m_verbose ).append("\n");
		strBuilder.append(OPT_DONT_PROMPT    + " = " + m_isDontPrompt ).append("\n");
		strBuilder.append(OPT_LOG_CONFIG_FILE    + " = " + m_logConfigFile ).append("\n");
		strBuilder.append(OPT_XSD_TYPE    + " = " + m_xsdFiles ).append("\n");
		strBuilder.append(OPT_STAGING_AREA    + " = " + m_stagingLocation ).append("\n");
		strBuilder.append(OPT_DEPENDENT_LIBS    + " = " + m_dependentTypeLibs ).append("\n");
		strBuilder.append(OPT_ADD_BUILD_CP_TO_XJC    + " = " + m_addBuildClassPathToXJC ).append("\n");
		strBuilder.append(OPT_ADD_CP_TO_XJC    + " = " + m_additionalClassPathToXJC ).append("\n");
		strBuilder.append(OPT_V4_WSDL_LOCATION    + " = " + m_v4WsdlLocation ).append("\n");
		strBuilder.append(OPT_V4_DEST_LOCATION    + " = " + m_v4DestLocation ).append("\n");
		strBuilder.append(OPT_V4_NS_2_PKG    + " = " + m_v4NS2Pkg ).append("\n");
		strBuilder.append(OPT_V4_CATALOG_FILE    + " = " + m_v4Catalog ).append("\n");
		
		strBuilder.append(" ]");
		
		return strBuilder.toString();

	}
}


