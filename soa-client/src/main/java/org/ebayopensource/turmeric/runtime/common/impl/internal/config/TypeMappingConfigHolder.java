/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents processed type mapping (serialization) configuration, which is common between client and server.
 * <p>
 * Note: Most ConfigHolder data is available in higher-level structures. Refer to ServiceDesc and related structures
 * as the primary configuration in the public API for SOA framework.
 * @author rmurphy
 *
 */
public class TypeMappingConfigHolder extends BaseConfigHolder {
	private HashMap<String, OperationConfig> m_operations = new HashMap<String, OperationConfig>();
	private HashMap<String, List<String>> m_xmlNSFromJavaPkg = new HashMap<String, List<String>>();
	private static final char NL = '\n';
	private HashSet<String> m_javaTypes = new HashSet<String>();
	private boolean m_enableNamespaceFolding = false;
	private boolean m_operationAdded = false;
	
	/**
	 * Introduces additional fully qualified Java class names to store
	 * These class names can be important for serialization purposes
	 * 
	 * @param type fully qualified Java class name
	 */
	public void addJavaTypes( String type ) {
		m_javaTypes.add( type );
	}
	
	/**
	 * 
	 * @return a collection of stored Java class names 
	 */
	public Collection<String> getJavaTypes() {
		return Collections.unmodifiableCollection( m_javaTypes );
	}

	/**
	 * @return the collection of all per-operation configuration.
	 */
	public Collection<OperationConfig> getOperations() {
		return Collections.unmodifiableCollection(m_operations.values());
	}
	
	/**
	 * @return the Set of operation names in configuration.
	 */
	public Set<String> getOperationNames() {
		return Collections.unmodifiableSet(m_operations.keySet());
	}

	/**
	 * Set the configuration of a particular operation by name.
	 * @param opname the name of the operation
	 * @param operation the operation-specific configuration to set
	 */
	void setOperation(String opname, OperationConfig operation) {
		checkReadOnly();
		m_operations.put(opname, operation);
	}

	/**
	 * @return the Java package-to-XML namespace mappings (used to drive JAXB serialization/deserialization)
	 */
	public Map<String,List<String>> getPackageToNsMap() {
		// TODO: how do we make inner list unmodifiable ?
		return Collections.unmodifiableMap(m_xmlNSFromJavaPkg);
	}

	/**
	 * Set a package-to-namespace mapping.
	 * @param javaType the Java classname from which to obtain a package name
	 * @param xmlNs the associated XML namespace
	 */
	void setXmlNamespaceFromJavaPackage(String javaType, String xmlNs) {
		checkReadOnly();
		List<String> nsList = m_xmlNSFromJavaPkg.get(javaType);
		if (nsList == null) {
			nsList = new ArrayList<String>();
			m_xmlNSFromJavaPkg.put(javaType, nsList);
		}
		nsList.add(xmlNs);
	}

	public boolean getEnableNamespaceFolding() {
		return m_enableNamespaceFolding;
	}
	
	public void setEnableNamespaceFolding(boolean flag) {
		m_enableNamespaceFolding = flag;
	}
	
	public boolean getOperationAdded() {
		return m_operationAdded;
	}
	
	public void setOperationAdded(boolean flag) {
		m_operationAdded = flag;
	}
	
	/**
	 * Safe copy method.
	 * @return a new object with a safe copy of the original data
	 */
	public TypeMappingConfigHolder copy() {
		TypeMappingConfigHolder newCH = new TypeMappingConfigHolder();
		newCH.m_readOnly = false;
		newCH.m_enableNamespaceFolding = m_enableNamespaceFolding;
		newCH.m_operations = copyOperations(m_operations);

		newCH.m_xmlNSFromJavaPkg = new HashMap<String,List<String>>();
		for (String javaName : m_xmlNSFromJavaPkg.keySet()) {
			List<String> xmlNsList = m_xmlNSFromJavaPkg.get(javaName);
			for (String xmlNs: xmlNsList) {
				newCH.setXmlNamespaceFromJavaPackage(javaName, xmlNs);
			}
		}

		return newCH;
	}

	private HashMap<String, OperationConfig> copyOperations(HashMap<String, OperationConfig> inOpMap) {
		if (inOpMap == null) {
			return null;
		}
		HashMap<String, OperationConfig> outOpMap = new HashMap<String, OperationConfig>();
		for (Map.Entry<String, OperationConfig> entry : inOpMap.entrySet()) {
			String key = entry.getKey();
			OperationConfig inOp = entry.getValue();
			OperationConfig outOp = ConfigUtils.copyOperationConfig(inOp);
			outOpMap.put(key, outOp);
		}
		return outOpMap;
	}

	/*
	 * Provide a user-readable description of the configuration into a StringBuffer.
	 * @param sb the StringBuffer into which to write the description
	 */
	public void dump(StringBuffer sb) {
		sb.append("========== Type Mappings ==========" + NL);
		sb.append("nsFolding: " + m_enableNamespaceFolding + "\n");
		List<String> operations = new ArrayList<String> (m_operations.keySet()) ;
		Collections.sort(operations);
		for (String opname : operations) {
		//for (String opname : m_operations.keySet()) {
			OperationConfig cfg = m_operations.get(opname);
			sb.append("Operation: " + opname + NL);
			if (cfg.getRequestMessage() != null)
				dumpMessageType(sb, "  Request", cfg.getRequestMessage());
			if (cfg.getResponseMessage() != null)
				dumpMessageType(sb, "  Response", cfg.getResponseMessage());
			if (cfg.getErrorMessage() != null)
				dumpMessageType(sb, "  Error", cfg.getErrorMessage());
			if (cfg.getRequestHeader() != null && !cfg.getRequestHeader().isEmpty()) {
				dumpHeaderList(sb, "  Request headers:", "    ", cfg.getRequestHeader());
			}
			if (cfg.getResponseHeader() != null && !cfg.getResponseHeader().isEmpty()) {
				dumpHeaderList(sb, "  Response headers:", "    ", cfg.getResponseHeader());
			}
		}
		sb.append("Package Info:" + NL);
		List<String> javaNames = new ArrayList<String> (m_xmlNSFromJavaPkg.keySet()) ;
		Collections.sort(javaNames);
		for (String javaName : javaNames) {
		//for (String javaName : m_xmlNSFromJavaPkg.keySet()) {
			List<String> xmlNsList = m_xmlNSFromJavaPkg.get(javaName);
			for (String xmlNs: xmlNsList) {
				sb.append("  java="+javaName+" xml="+xmlNs+NL);
			}
		}
	}

	private void dumpHeaderList(StringBuffer sb, String heading, String prefix, List<MessageHeaderConfig> mhcList) {
		sb.append(heading + NL);
		for (MessageHeaderConfig mhc : mhcList) {
			dumpMessageHeader(sb, prefix, mhc);
		}
	}

	private void dumpMessageHeader(StringBuffer sb, String prefix, MessageHeaderConfig mhc) {
		sb.append(prefix + "element: " + mhc.getXmlElementName() + ", java=" + mhc.getJavaTypeName() + " xml=" + mhc.getXmlTypeName() + NL);
	}

	private void dumpMessageType(StringBuffer sb, String type, MessageTypeConfig msg) {
		sb.append(type + ": " + "java=" + msg.getJavaTypeName() + " xml=" + msg.getXmlTypeName());
		if (msg.hasAttachment) {
			sb.append(" has-attachment");
		}
		sb.append(NL);
	}
}
