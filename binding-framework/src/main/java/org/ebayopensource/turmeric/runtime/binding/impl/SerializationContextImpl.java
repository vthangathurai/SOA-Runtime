/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;
import org.ebayopensource.turmeric.runtime.binding.ITypeConversionContext;
import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;


/**
 * Context object to pass information to Ser/Deser 
 * @author wdeng
 *
 */
public class SerializationContextImpl implements ISerializationContext {
	private Map<String, String> m_prefix2NS;
	
	private final static String NO_NAMESPACE = "__no_namespace__";
	private String m_defaultNamespace;
	private Map<String, List<String>> m_ns2Prefix;
	private Charset m_charset;
	private boolean m_supportObjectNode;
	private String m_payloadType;  // = dbDesc.getPayloadType();
	private QName m_rootXMLName;
	private Class m_rootClass; 
	private DataElementSchema m_rootElementSchema;
	private Map<String, String> m_pkg2NS;
	private Map<String, String> m_javaType2NS = new HashMap<String, String>();
	private List<Throwable> m_warningList;
	private List<Throwable> m_errorList;
	private ITypeConversionContext m_typeConversion;
	private boolean m_isREST;

	private String m_singleNamespace;
	
	public SerializationContextImpl (
								String payloadType,
								String defaultNamespace,
								Map<String, List<String>> ns2Prefix,
								Map<String, String> prefix2NS,
								Charset charset,
								QName rootXMLName,
								Class rootClass,
								DataElementSchema rootElementSchema,
								boolean supportObjectNode,
								Map<String, String> pkg2NS,
								ITypeConversionContext typeConversion,
								boolean isREST) {
		m_payloadType = payloadType;
		m_defaultNamespace = defaultNamespace;
		m_ns2Prefix = ns2Prefix;
		m_charset = charset;
		m_rootXMLName = rootXMLName;
		m_rootClass = rootClass;
		m_rootXMLName = rootXMLName;
		m_supportObjectNode = supportObjectNode;
		m_typeConversion = typeConversion;
		m_warningList = new ArrayList<Throwable>();
		m_errorList = new ArrayList<Throwable>();
		m_pkg2NS = pkg2NS;
		m_prefix2NS = prefix2NS;
		m_isREST = isREST;
		m_rootElementSchema = rootElementSchema;
	}
	
	public SerializationContextImpl(String payloadType,
			String defaultNamespace, Map<String, List<String>> ns2Prefix,
			Map<String, String> prefix2NS, Charset charset, QName rootXMLName,
			Class rootClass, DataElementSchema rootElementSchema,
			boolean supportObjectNode, Map<String, String> pkg2NS,
			ITypeConversionContext typeConversion, boolean isREST,
			String singleNamespace) {
		m_payloadType = payloadType;
		m_defaultNamespace = defaultNamespace;
		m_ns2Prefix = ns2Prefix;
		m_charset = charset;
		m_rootXMLName = rootXMLName;
		m_rootClass = rootClass;
		m_rootXMLName = rootXMLName;
		m_supportObjectNode = supportObjectNode;
		m_typeConversion = typeConversion;
		m_warningList = new ArrayList<Throwable>();
		m_errorList = new ArrayList<Throwable>();
		m_pkg2NS = pkg2NS;
		m_prefix2NS = prefix2NS;
		m_isREST = isREST;
		m_rootElementSchema = rootElementSchema;
		m_singleNamespace = singleNamespace;
	}


	public Map<String, String> getPrefixToNamespaceMap() {
		return m_prefix2NS;
	}
	public String getDefaultNamespace() {
		return m_defaultNamespace;
	}

	public String getSingleNamespace() {
		return m_singleNamespace;
	}

	public Map<String, List<String>> getNamespaceToPrefixMap() {
		return m_ns2Prefix;
	}
	public Charset getCharset() {
		return m_charset;
	}
	public boolean supportObjectNode() {
		return m_supportObjectNode;
	}
	public String getPayloadType() {
		return m_payloadType;
	}
	public Class getRootClass() {
		return m_rootClass;
	}
	public QName getRootXMLName() {
		return m_rootXMLName;
	}
	public DataElementSchema getRootElementSchema() {
		return m_rootElementSchema;
	}

	public void setIsREST(boolean flag) {
		m_isREST = flag;
	}
	
	public boolean isREST() {
		return m_isREST;
	}
	
	public void addError(Throwable t) {
		m_errorList.add(t);
	}

	public List<Throwable> getErrorList() {
		return m_errorList;
	}
	
	public void addWarning(Throwable t) {
		m_warningList.add(t);
	}

	public List<Throwable> getWarningList() {
		return m_warningList;
	}

	public boolean hasErrors() {
		return !m_errorList.isEmpty();
	}

	public boolean hasWarnings() {
		return !m_warningList.isEmpty();
	}

	public ITypeConversionContext getTypeConversionContext() {
		return m_typeConversion;
	}


	public String getNsForJavaType(Class javaType) {
		String className = javaType.getName();
		String result = m_javaType2NS.get(className);

		if (result != null) {
			if (result == NO_NAMESPACE) {
				return null;
			}
			return result;
		}

		String ns = getNsForJavaType(className, m_pkg2NS);
		if (ns != null) {
			m_javaType2NS.put(className, ns);
		} else {
			m_javaType2NS.put(className, NO_NAMESPACE);
		}

		return ns;
	}

	public static String getNsForJavaType(String className, Map<String,String> packageToNamespace) {
		String pkgName = className;
		while (pkgName.length() != 0) {
			String pkgName2;
			int p = pkgName.lastIndexOf('.');
			if (p == -1) {
				pkgName2 = "";
			} else {
				pkgName2 = pkgName.substring(0, p);
			}

			String ns = packageToNamespace.get(pkgName2);
			if (ns != null) {
				return ns;
			}

			pkgName = pkgName2;
		}

		return null;
	}
}
