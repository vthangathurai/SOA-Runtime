/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.tools.codegen.InputOptions.CodeGenType;
import org.ebayopensource.turmeric.tools.codegen.InputOptions.InputType;
import org.ebayopensource.turmeric.tools.codegen.handler.ConsoleResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.handler.UserResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;



/**
 * A context class for holding code generation context information.
 *
 *
 * @author rmandapati
 */

public class CodeGenContext {

	private InputOptions m_inputOptions;
	private UserResponseHandler m_userResponseHandler;
	private JTypeTable m_jTypeTable;

	private QName m_serviceQName = null;

	private String m_serviceInterfaceClassName;
	private String m_serviceImplSkeletonName;

	private String m_javaSrcDestLocation;
	private String m_metaSrcDestLocation;


	private List<String> m_generatedJavaFiles;

	private String m_wsdlURI = null;

	private boolean m_isBinLocAddedToClasspath;
	
	private String m_asyncInterfaceInterfaceClassName;
	private String m_javaAsyncSrcDestLocation;
	private boolean asyncType = false;
	
	private Set<String> m_operationNamesInWSDL;
	private Map<String,String> m_javaMethodOperationNameMap;
	
	private boolean m_generatePollMethod = true; 
	private Definition m_wsdlDefinition;
	
	private String m_schemaTypesJavaFileLocation;



	public CodeGenContext(InputOptions options,
				UserResponseHandler userResponseHandler) {
		m_inputOptions = options;
		m_userResponseHandler = userResponseHandler;
	}


	public InputOptions getInputOptions() {
		return m_inputOptions;
	}

	public void setInputOptions(InputOptions options) {
		m_inputOptions = options;
	}


	public String getServiceAdminName() {
		return getInputOptions().getServiceAdminName();
	}


	public QName getServiceQName() {
		if (m_serviceQName == null) {
			m_serviceQName = new QName(getNamespace(), getServiceAdminName());
		}

		return m_serviceQName;
	}

	public void setServiceQName(QName qName) {
		m_serviceQName = qName;
		m_inputOptions.setServiceAdminName(qName.getLocalPart());
		m_inputOptions.setNamespace(qName.getNamespaceURI());
		m_inputOptions.setServiceQName(qName);
	}

	public String getNamespace() {
		if (CodeGenUtil.isEmptyString(m_inputOptions.getNamespace())) {
			m_inputOptions.setNamespace(SOAConstants.DEFAULT_SERVICE_NAMESPACE);
		}

		return m_inputOptions.getNamespace();
	}


	public JTypeTable getJTypeTable() {
		return m_jTypeTable;
	}

	public void setJTypeTable(JTypeTable typeTable) {
		m_jTypeTable = typeTable;
	}


	public String getServiceImplClassName() {
		if (!CodeGenUtil.isEmptyString(
				getInputOptions().getServiceImplClassName())) {
			return getInputOptions().getServiceImplClassName();
		} else {
			return getServiceImplSkeletonName();
		}
	}


	public String getServiceImplSkeletonName() {
		return m_serviceImplSkeletonName;
	}

	public void setServiceImplSkeletonName(String serviceImplSkeletonName) {
		this.m_serviceImplSkeletonName = serviceImplSkeletonName;
	}



	public UserResponseHandler getUserResponseHandler() {
		if (m_userResponseHandler == null) {
			m_userResponseHandler = new ConsoleResponseHandler();
		}
		return m_userResponseHandler;
	}

	public void setUserResponseHandler(UserResponseHandler userResponseHandler) {
		m_userResponseHandler = userResponseHandler;
	}

	public String getServiceInterfaceClassName() {
		if (m_serviceInterfaceClassName == null &&
				 getInputOptions().getInputType() !=  InputType.WSDL  ) {
			m_serviceInterfaceClassName = getInputOptions().getInputFile();
		}

		return m_serviceInterfaceClassName;
	}

	public void setServiceInterfaceClassName(String interfaceName) {
		m_serviceInterfaceClassName = interfaceName;
	}

	public String getSrcLocation() {
		return getInputOptions().getSrcLocation();
	}

	public String getDestLocation() {
		return getInputOptions().getDestLocation();
	}
	
	/**
	 * [LEGACY] Method to get a Java Src Dest Location.
	 */
	public String getJavaSrcDestLocation() {
		// Maintain backward compatibility
		return getJavaSrcDestLocation(true);
	}

	/**
	 * Get Java Src Dest Location.
	 * <p>
	 * You have the ability to get the Java Src Dest Location with fallback to the {@link #getDestLocation()} or
	 * without.
	 * <p>
	 * This behavior is useful in eliminating the need for additional and tacked on directories after the location as
	 * seen in legacy (CLI) based codegen.
	 * 
	 * @param fallback
	 *            true to use {@link #getDestLocation()} if the actual Java Src Dest Location is unspecified.
	 * @return Java Src Destination Location, (or null if fallback set to false, and the Java Src Destination is
	 *         unspecified)
	 */
	public String getJavaSrcDestLocation(boolean fallback) {
		if (CodeGenUtil.isEmptyString(m_javaSrcDestLocation) && fallback) {
			// Just return the fallback, don't SET it to m_javaSrcDestLocation
			return getInputOptions().getDestLocation();
		}
		return m_javaSrcDestLocation;
	}

	public void setJavaSrcDestLocation(String destLocation) {
		m_javaSrcDestLocation = destLocation;
	}

	/**
	 * [LEGACY] Method to get a Meta Src Dest Location.
	 */
	public String getMetaSrcDestLocation() {
		// Maintain backward compatibility
		return getMetaSrcDestLocation(true);
	}
	
	/**
	 * Get Meta Src Dest Location.
	 * <p>
	 * You have the ability to get the Meta Src Dest Location with fallback to the {@link #getDestLocation()} or
	 * without.
	 * <p>
	 * This behavior is useful in eliminating the need for additional and tacked on directories after the location as
	 * seen in legacy (CLI) based codegen.
	 * 
	 * @param fallback
	 *            true to use {@link #getDestLocation()} if the actual Meta Src Dest Location is unspecified.
	 * @return Meta Src Destination Location, (or null if fallback set to false, and the Meta Src Destination is
	 *         unspecified)
	 */
	public String getMetaSrcDestLocation(boolean fallback) {
		if (CodeGenUtil.isEmptyString(m_metaSrcDestLocation) && fallback) {
			// Just return the fallback, don't SET it to m_metaSrcDestLocation
			return getInputOptions().getDestLocation();
		}
		return m_metaSrcDestLocation;
	}

	public void setMetaSrcDestLocation(String destLocation) {
		m_metaSrcDestLocation = destLocation;
	}

	public String getBinLocation() {
		if (CodeGenUtil.isEmptyString(getInputOptions().getBinLocation())) {
			return getInputOptions().getDestLocation();
		}
		return getInputOptions().getBinLocation();
	}


	public List<String> getGeneratedJavaFiles() {
		if (m_generatedJavaFiles == null) {
			m_generatedJavaFiles = new ArrayList<String>();
		}
		return m_generatedJavaFiles;
	}

	public void addGeneratedJavaSrcFile(String javaSourceFileName) {
		getGeneratedJavaFiles().add(javaSourceFileName);
	}

	public void addGeneratedJavaSrcFiles(List<String> javaSourceFileName) {
		getGeneratedJavaFiles().addAll(javaSourceFileName);
	}


	public String getClientName() {
		return getInputOptions().getClientName();
	}


	public String getWSDLURI() {
		if (CodeGenUtil.isEmptyString(m_wsdlURI)) {
			return getInputOptions().getWSDLURI();
		}
		return m_wsdlURI;
	}


	public void setWSDLURI(String wsdlURI) {
		m_wsdlURI = wsdlURI;
		getInputOptions().setWSDLURI(wsdlURI);
	}


	public boolean isBinLocAddedToClasspath() {
		return m_isBinLocAddedToClasspath;
	}


	public void setIsBinLocAddedToClasspath(boolean binLocAddedToClasspath) {
		m_isBinLocAddedToClasspath = binLocAddedToClasspath;
	}

	public String getProjectRoot(){
		return getInputOptions().getProjectRoot();
	}
	
	public String getServiceAsyncInterfaceClassName() {

		return m_asyncInterfaceInterfaceClassName;

	}

	public void setServiceAsyncInterfaceClassName(String asyncInterfaceName) {

		m_asyncInterfaceInterfaceClassName = asyncInterfaceName;

	}
	
	public boolean isAsyncInterfaceRequired() {

		CodeGenType codeGenType = m_inputOptions.getCodeGenType();
		return ((codeGenType == CodeGenType.All) || 
				(codeGenType == CodeGenType.Client) ||
				(codeGenType == CodeGenType.ClientNoConfig) ||
				(codeGenType == CodeGenType.Proxy) ||
				(codeGenType == CodeGenType.ServiceFromWSDLIntf)||
				(codeGenType == CodeGenType.Consumer));

		}
	
		
	public boolean isAsyncInputTypeWSDL() {

		return asyncType;

	}

	public void setAsyncInputType(boolean asyncType) {

		this.asyncType = asyncType;

	}

	public String getAsyncJavaSrcDestLocation() {
		if (CodeGenUtil.isEmptyString(m_javaAsyncSrcDestLocation)) {
			m_javaAsyncSrcDestLocation = m_javaSrcDestLocation;
		}
		return m_javaAsyncSrcDestLocation;
	}

	public void setAsyncJavaSrcDestLocation(String destLocation) {
		m_javaAsyncSrcDestLocation = destLocation;
	}

	public Set<String> getOperationNamesInWSDL() {
		if(m_operationNamesInWSDL == null)
			m_operationNamesInWSDL = new HashSet<String>();
		return m_operationNamesInWSDL;
	}
	

	public Map<String, String> getJavaMethodOperationNameMap() {
		if(m_javaMethodOperationNameMap == null)
			m_javaMethodOperationNameMap = new HashMap<String, String>();
		return m_javaMethodOperationNameMap;
	}

	
	public boolean isGeneratePollMethod() {
		return m_generatePollMethod;
	}


	public void setGeneratePollMethod(boolean pollMethod) {
		m_generatePollMethod = pollMethod;
	}


	public Definition getWsdlDefinition() {
		return m_wsdlDefinition;
	}


	public void setWsdlDefinition(Definition definition) {
		m_wsdlDefinition = definition;
	}


	public String getSchemaTypesJavaFileLocation() {
		return m_schemaTypesJavaFileLocation;
	}


	public void setSchemaTypesJavaFileLocation(String typesJavaFileLocation) {
		m_schemaTypesJavaFileLocation = typesJavaFileLocation;
	}

	
}
