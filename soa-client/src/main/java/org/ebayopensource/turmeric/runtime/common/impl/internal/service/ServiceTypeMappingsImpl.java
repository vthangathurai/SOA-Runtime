/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.impl.SerializationContextImpl;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.BaseTypeDefsBuilder;
import org.ebayopensource.turmeric.runtime.common.service.ServiceTypeMappings;

import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;

/**
 * @author ichernyshev
 */
public final class ServiceTypeMappingsImpl implements ServiceTypeMappings {
	
	private static Logger LOGGER = Logger.getInstance( ServiceTypeMappingsImpl.class );
	private final static String NO_NAMESPACE = "__no_namespace__";
	private final Map<String,String> m_packageToNamespace;
	private final Map<String,String> m_classnameToNamespace;
	private final Map<String,String> m_namespaceToPrefix;
	private final Map<String,List<String>> m_namespaceToPrefixes;
	private final Map<String,String> m_prefixToNamespace;
	private String m_singleNamespace;
	private BaseTypeDefsBuilder m_typeDefsBuilder;
	private boolean m_typeDefsBuilderCleaned;
	private boolean m_namespaceFoldingEnabled = false;

	public ServiceTypeMappingsImpl(Map<String,String> packageToNamespace,
		BaseTypeDefsBuilder typeDefsBuilder)
	{
		this(packageToNamespace, typeDefsBuilder, false);
	}
	
	private boolean validateSingleNamespace(Map<String,String> packageToNamespace) {
		Collection<String> namespaces = packageToNamespace.values();
		HashSet<String> uniqueNamespaces = new HashSet<String>(namespaces);
		return uniqueNamespaces.size() == 1;
	}
	
	public ServiceTypeMappingsImpl(Map<String,String> packageToNamespace,
		BaseTypeDefsBuilder typeDefsBuilder, boolean enableNamespaceFolding)
	{
		m_namespaceFoldingEnabled = enableNamespaceFolding;
		if (enableNamespaceFolding && !validateSingleNamespace(packageToNamespace)) {
			throw new IllegalStateException("TypeMappings is not compliant with single namespace constraint. it contains " 
							+ packageToNamespace.values());
		}
		if (packageToNamespace == null) {
			packageToNamespace = new HashMap<String,String>();
		}
		m_packageToNamespace = Collections.unmodifiableMap(packageToNamespace);

		m_classnameToNamespace = Collections.synchronizedMap(new HashMap<String,String>());

		Map<String,List<String>> namespaceToPrefixes = new HashMap<String,List<String>>();
		Map<String,String> namespaceToPrefix = new HashMap<String,String>();
		Map<String,String> prefixToNamespace = new HashMap<String,String>();
		buildNsPrefixes(m_packageToNamespace.values(), namespaceToPrefixes,
			namespaceToPrefix, prefixToNamespace);

		m_namespaceToPrefixes = NamespaceConvention.buildUnmodifiableNsToPrefixMap(namespaceToPrefixes);
		m_namespaceToPrefix = Collections.unmodifiableMap(namespaceToPrefix);
		m_prefixToNamespace = Collections.unmodifiableMap(prefixToNamespace);

		m_typeDefsBuilder = typeDefsBuilder;
	}

	public String getNsForJavaType(Class javaType) {
		String className = javaType.getName();
		String result = m_classnameToNamespace.get(className);

		if (result != null) {
			if (result == NO_NAMESPACE) {
				return null;
			}
			return result;
		}

		String ns = getNsForJavaType(className, m_packageToNamespace);
		if (ns != null) {
			m_classnameToNamespace.put(className, ns);
		} else {
			m_classnameToNamespace.put(className, NO_NAMESPACE);
		}

		return ns;
	}

	public static String getNsForJavaType(String className, Map<String,String> packageToNamespace) {
		return SerializationContextImpl.getNsForJavaType(className, packageToNamespace);
	}

	public Map<String, String> getPackageToNamespaceMap() {
		return m_packageToNamespace;
	}
	
	public Map<String,String> getNamespaceToPrefixMap() {
		return m_namespaceToPrefix;
	}

	public Map<String,List<String>> getNamespaceToPrefixesMap() {
		return m_namespaceToPrefixes;
	}

	public Map<String,String> getPrefixToNamespaceMap() {
		return m_prefixToNamespace;
	}

	public String getNamespaceByPrefix(String prefix) {
		return m_prefixToNamespace.get(prefix);
	}

	public String getPrefixByNamespace(String ns) {
		return m_namespaceToPrefix.get(ns);
	}

	public String getSingleNamespace() {
		return m_singleNamespace;
	}

	BaseTypeDefsBuilder getTypeDefsBuilder() {
		if (m_typeDefsBuilderCleaned) {
			throw new IllegalStateException("TypeDefsBuilder has been cleaned");
		}

		return m_typeDefsBuilder;
	}

	void cleanTypeDefsBuilder() {
		// save some memory here
		m_typeDefsBuilder = null;
		m_typeDefsBuilderCleaned = true;
	}

	private void buildNsPrefixes(Collection<String> namespaces,
		Map<String,List<String>> namespaceToPrefixes,
		Map<String,String> namespaceToPrefix,
		Map<String,String> prefixToNamespace)
	{
		if (namespaces == null || namespaces.isEmpty()) {
			return;
		}

		// add standard prefixes first
		for (Map.Entry<String,String> e: getStandardPrefixToNSMap(m_namespaceFoldingEnabled).entrySet()) {
			String prefix = e.getKey();
			String ns = e.getValue();

			addToNsPrefixMap(ns, prefix, namespaceToPrefixes,
				namespaceToPrefix, prefixToNamespace, false);
		}
		if ( LOGGER.isDebugEnabled() ) {
			LOGGER.log( LogLevel.DEBUG, "ServiceTypeMappings.buildNsPrefixes: namespaces = " + namespaces );
			LOGGER.log( LogLevel.DEBUG, "ServiceTypeMappings.buildNsPrefixes: namespaceToPrefixes = " + namespaceToPrefixes );
			LOGGER.log( LogLevel.DEBUG, "ServiceTypeMappings.buildNsPrefixes: namespaceToPrefix = " + namespaceToPrefix );
			LOGGER.log( LogLevel.DEBUG, "ServiceTypeMappings.buildNsPrefixes: prefixToNamespace = " + prefixToNamespace );
		}
		int nsIndex = 0;
		//TODO: this following loop is complicated and may need rewrite.
		boolean b = true;
		for (String ns: namespaces) {
			
			if ( LOGGER.isDebugEnabled() )
				LOGGER.log( LogLevel.DEBUG, "ns: " + ns );

			if(b && ns.equals(BindingConstants.SOA_TYPES_NAMESPACE)) {
				b = false;
				if (nsIndex == 0) {
					m_singleNamespace = ns;
					nsIndex++;
				} else {
					m_singleNamespace = null;
				}
				continue;
			}

			if (namespaceToPrefixes.containsKey(ns)) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.log( LogLevel.DEBUG, "Contains " + ns );
				continue;
			}

			if (nsIndex == 0) {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.log( LogLevel.DEBUG, "Index 0 is " + ns + " overwriting " + m_singleNamespace );
				m_singleNamespace = ns;
			} else {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.log( LogLevel.DEBUG, "nsIndex " + nsIndex + " is nullifying " + m_singleNamespace );
				m_singleNamespace = null;
			}

			String prefix = "ns" + ++nsIndex;
			addToNsPrefixMap(ns, prefix, namespaceToPrefixes,
				namespaceToPrefix, prefixToNamespace, false);
			if ( LOGGER.isDebugEnabled() ) {
				LOGGER.log( LogLevel.WARN, "Adding ns prefix " + prefix );
				LOGGER.log( LogLevel.WARN, "ServiceTypeMappings.buildNsPrefixes: namespaceToPrefixes = " + namespaceToPrefixes );
			}
		}
	}

	public static void addToNsPrefixMap(String ns, String prefix,
		Map<String,List<String>> namespaceToPrefixes,
		Map<String,String> namespaceToPrefix,
		Map<String,String> prefixToNamespace,
		boolean recreateListIfUnmodifiable)
	{
		prefixToNamespace.put(prefix, ns);

		List<String> prefixes = namespaceToPrefixes.get(ns);
		if (prefixes == null) {
			prefixes = new ArrayList<String>();
			namespaceToPrefixes.put(ns, prefixes);
		} else if (recreateListIfUnmodifiable && !(prefixes instanceof ArrayList)) {
			prefixes = new ArrayList<String>(prefixes);
			namespaceToPrefixes.put(ns, prefixes);
		}

		if (!prefixes.contains(prefix)) {
			prefixes.add(prefix);
		}

		if (namespaceToPrefix != null && !namespaceToPrefix.containsKey(ns)) {
			namespaceToPrefix.put(ns, prefix);
		}
	}

	public boolean getNamespaceFoldingEnabled() {
		return m_namespaceFoldingEnabled;
	}

	
	/**
	 * 
	 * Return the namespace to prefix mapping for standard schemas. The map contains
	 *  http://www.w3.org/2001/XMLSchema
	 *  http://www.w3.org/2001/XMLSchema-instance and
	 *  http://www.ebayopensource.org/turmeric/common/v1/types if namespace folding is not enabled for the service.
	 *  
	 *  @return the namespace to prefix mapping for standard schemas.
	 */
	public static Map<String,List<String>> getStandardNSToPrefixesMap(boolean namespaceFoldingEnabled) {
		return NamespaceConvention.getStandardNSToPrefixesMap(!namespaceFoldingEnabled);
	}

	/**
	 * 
	 * Return the prefix to namespace mapping for standard schemas. The map contains
	 *  http://www.w3.org/2001/XMLSchema
	 *  http://www.w3.org/2001/XMLSchema-instance and
	 *  http://www.ebayopensource.org/turmeric/common/v1/types if namespace folding is not enabled for the service.
	 *  
	 *  @return the prefix to namespace mapping for standard schemas.
	 */

	public static Map<String,String> getStandardPrefixToNSMap(boolean namespaceFoldingEnabled) {
		return NamespaceConvention.getStandardPrefixToNSMap(!namespaceFoldingEnabled);
	}
}
