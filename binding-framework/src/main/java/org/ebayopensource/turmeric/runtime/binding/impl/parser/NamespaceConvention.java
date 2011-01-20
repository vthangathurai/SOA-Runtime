/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.IDeserializationContext;
import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;


public class NamespaceConvention implements NamespaceContext {

	private static Map<String,List<String>> s_standardNSToPrefixesMap;
	private static Map<String,String> s_standardPrefixToNSMap;

	private static Map<String,List<String>> s_standardNSToPrefixesMapExcludeSOACommon;
	private static Map<String,String> s_standardPrefixToNSMapExcludeSOACommon;

	private final Map<String,List<String>> m_namespaceToPrefixes;
	private final Map<String,String> m_prefixToNamespace;
	private String m_singleNamespace;
	private final boolean m_isModifiable;


	static {
		Map<String, List<String>> namespaceToPrefixes = new HashMap<String, List<String>>(3);
		Map<String, String> prefixToNamespace = new HashMap<String, String>(3);

		// Adds the XMLSchema-instance namespace by default.
		addToNsPrefixMap(BindingConstants.XMLSCHEMA_INSTANCE_URI,
			BindingConstants.XMLSCHEMA_INSTANCE_PREFIX,
			namespaceToPrefixes, prefixToNamespace, false);

		// Adds the XMLSchema namespace by default.
		addToNsPrefixMap(BindingConstants.XMLSCHEMA_URI,
			BindingConstants.XMLSCHEMA_PREFIX,
			namespaceToPrefixes, prefixToNamespace, false);

		// Adds SOA common type namespace by default.
		addToNsPrefixMap(BindingConstants.SOA_TYPES_NAMESPACE,
			BindingConstants.SOA_TYPES_PREFIX,
			namespaceToPrefixes, prefixToNamespace, false);

		s_standardNSToPrefixesMap = buildUnmodifiableNsToPrefixMap(namespaceToPrefixes);
		s_standardPrefixToNSMap = Collections.unmodifiableMap(prefixToNamespace);

		namespaceToPrefixes = new HashMap<String, List<String>>(2);
		prefixToNamespace = new HashMap<String, String>(2);

		// Adds the XMLSchema-instance namespace by default.
		addToNsPrefixMap(BindingConstants.XMLSCHEMA_INSTANCE_URI,
			BindingConstants.XMLSCHEMA_INSTANCE_PREFIX,
			namespaceToPrefixes, prefixToNamespace, false);

		// Adds the XMLSchema namespace by default.
		addToNsPrefixMap(BindingConstants.XMLSCHEMA_URI,
			BindingConstants.XMLSCHEMA_PREFIX,
			namespaceToPrefixes, prefixToNamespace, false);

		s_standardNSToPrefixesMapExcludeSOACommon = buildUnmodifiableNsToPrefixMap(namespaceToPrefixes);
		s_standardPrefixToNSMapExcludeSOACommon = Collections.unmodifiableMap(prefixToNamespace);
	}


	public static Map<String,List<String>> buildUnmodifiableNsToPrefixMap(Map<String,List<String>> map) {
		Map<String,List<String>> result = new HashMap<String,List<String>>(map.size());
		for (Map.Entry<String,List<String>> e: map.entrySet()) {
			String ns = e.getKey();
			List<String> prefixes = e.getValue();
			result.put(ns, Collections.unmodifiableList(prefixes));
		}
		return Collections.unmodifiableMap(result);
	}

	public static void buildNsPrefixes(Collection<String> namespaces,
			Map<String,List<String>> namespaceToPrefixes,
			Map<String,String> prefixToNamespace, boolean standard) {

		if(standard) {
			buildNsPrefixes0(namespaces, namespaceToPrefixes, prefixToNamespace, s_standardPrefixToNSMap);
		} else {
			buildNsPrefixes0(namespaces, namespaceToPrefixes, prefixToNamespace, s_standardPrefixToNSMapExcludeSOACommon);
		}
	}


	@Deprecated
	public static void buildNsPrefixes(Collection<String> namespaces,
			Map<String,List<String>> namespaceToPrefixes,
			Map<String,String> prefixToNamespace) {
		buildNsPrefixes0(namespaces, namespaceToPrefixes, prefixToNamespace, s_standardPrefixToNSMap);
	}

	private static void buildNsPrefixes0(Collection<String> namespaces,
		Map<String,List<String>> namespaceToPrefixes,
		Map<String,String> prefixToNamespace, Map<String, String> standardPrefixMap)
	{
		if (namespaces == null || namespaces.isEmpty()) {
			return;
		}

		// add standard prefixes first
		for (Map.Entry<String,String> e: standardPrefixMap.entrySet()) {
			String prefix = e.getKey();
			String ns = e.getValue();

			addToNsPrefixMap(ns, prefix, namespaceToPrefixes,
				prefixToNamespace, false);
		}

		int nsIndex = 0;
		for (String ns: namespaces) {
			if (namespaceToPrefixes.containsKey(ns)) {
				continue;
			}

			String prefix = "ns" + ++nsIndex;
			addToNsPrefixMap(ns, prefix, namespaceToPrefixes,
				prefixToNamespace, false);
		}
	}

	public static void addToNsPrefixMap(String ns, String prefix,
		Map<String,List<String>> namespaceToPrefixes,
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
	}

	public static Map<String,List<String>> getStandardNSToPrefixesMap(boolean soaCommonIncluded) {
		return soaCommonIncluded ? s_standardNSToPrefixesMap :  s_standardNSToPrefixesMapExcludeSOACommon;
	}

	public static Map<String,String> getStandardPrefixToNSMap(boolean soaCommonIncluded) {
		return soaCommonIncluded ? s_standardPrefixToNSMap :  s_standardPrefixToNSMapExcludeSOACommon;
	}

	// TODO: move StandardNSPrefixes from internal ServiceTypeMappingsImpl to a shared place
	public static NamespaceConvention createDeserializationNSConvention(IDeserializationContext ctxt) {
		return createDeserializationNSConvention(
				ctxt.getSingleNamespace(),
				ctxt.getPrefixToNamespaceMap(),
				ctxt.getNamespaceToPrefixMap());
	}
	public static NamespaceConvention createDeserializationNSConvention(
			String singleNamespace, Map<String, String> prefixToNS, Map<String,List<String>>nsToPrefix)
	{
		Map<String,List<String>> namespaceToPrefixes = new HashMap<String,List<String>>();
		Map<String,String> prefixToNamespace = new HashMap<String,String>();
		if (singleNamespace != null) {
			namespaceToPrefixes.putAll(nsToPrefix);
			prefixToNamespace.putAll(prefixToNS);
		}

		return new NamespaceConvention(namespaceToPrefixes, prefixToNamespace, singleNamespace, true);
	}

	public static NamespaceConvention createSerializationNSConvention(ISerializationContext ctxt)
	{
		return createSerializationNSConvention(
				ctxt.getSingleNamespace(),
				ctxt.getPrefixToNamespaceMap(),
				ctxt.getNamespaceToPrefixMap());
	}

	public static NamespaceConvention createSerializationNSConvention(String singleNamespace, Map<String, String> prefixToNS, Map<String,List<String>>nsToPrefix)
	{
		return new NamespaceConvention(nsToPrefix, prefixToNS, singleNamespace, false);
	}

	/**
	 * This factory method is for use in standalone (de)ser case to create single namespace convention.
	 */
	public static NamespaceConvention createSingleNamespaceSerializationConvention(
		String prefix, String nsURI)
	{
		Map<String,String> standardPrefixToNamespace = getStandardPrefixToNSMap(false);
		Map<String,List<String>> standardNamespaceToPrefixes = getStandardNSToPrefixesMap(false);

		int size = standardPrefixToNamespace.size() + 1;
		Map<String,List<String>> namespaceToPrefixes = new HashMap<String,List<String>>(size + 1);
		Map<String,String> prefixToNamespace = new HashMap<String,String>(size + 1);

		namespaceToPrefixes.putAll(standardNamespaceToPrefixes);
		prefixToNamespace.putAll(standardPrefixToNamespace);

		addToNsPrefixMap(nsURI, prefix,
			namespaceToPrefixes, prefixToNamespace, true);

		return new NamespaceConvention(namespaceToPrefixes, prefixToNamespace, nsURI, false);
	}

	private NamespaceConvention(
		Map<String,List<String>> namespaceToPrefixes,
		Map<String,String> prefixToNamespace,
		String singleNamespace, boolean isModifiable)
	{
		m_prefixToNamespace = prefixToNamespace;
		m_namespaceToPrefixes = namespaceToPrefixes;
		m_singleNamespace = singleNamespace;
		m_isModifiable = isModifiable;
	}

	public boolean isSingleNamespace() {
		return m_singleNamespace != null;
	}

	public String getSingleNamespace() {
		return m_singleNamespace;
	}

	public void setSingleNamespace(String ns) {
		m_singleNamespace = ns;
	}

	public void addMapping(String prefix, String namespace) throws XMLStreamException {
		if (!m_isModifiable) {
			throw new IllegalStateException(
				"This NamingConvention instance does not allow modifications");
		}

		String oldNs = m_prefixToNamespace.get(prefix);
		if (oldNs != null) {
			if (!oldNs.equals(namespace)) {
				throw new XMLStreamException("NS Prefix '" + prefix +
					"' for '" + oldNs + "' has been redefined to '" + namespace + "'");
			}

			// already defined
			return;
		}

		if (m_singleNamespace != null && !m_singleNamespace.equals(namespace)) {
			throw new XMLStreamException("NS '" + namespace +
				"' specified in the stream does not match declared single namespace'" +
				namespace + "'");
		}

		addToNsPrefixMap(namespace, prefix,
			m_namespaceToPrefixes, m_prefixToNamespace, true);
	}

	private List<String> getPrefixesInt(String ns) {
		return m_namespaceToPrefixes.get(ns);
	}

	public String getPrefix(String nsUri) {
		if (nsUri == null || nsUri.length() == 0) {
			return "";
		}

		List<String> list = getPrefixesInt(nsUri);
		if (list == null) {
			// Just return null, instead of throwing exception
			// throw new IllegalStateException("Unknown namespace URI '" + nsUri + "'");
			return null;
		}

		return list.get(0);
	}

	public Iterator getPrefixes(String nsUri) {
		List<String> list = getPrefixesInt(nsUri);
		return (list != null ? list.iterator() : null);
	}

	public String getNamespaceURI(String prefix) {
		if (prefix == null || prefix.length() == 0) {
			return "";
		}

		String nsURI = m_prefixToNamespace.get(prefix);
		if (nsURI == null) {
			throw new IllegalStateException("Unknown namespace prefix '" + prefix + "'");
		}

		return nsURI;
	}

	public String getNamespaceUriNoChecks(String prefix) {
		return m_prefixToNamespace.get(prefix);
	}

	public Map<String, String> getPrefixToNamespaceMap() {
		return Collections.unmodifiableMap(m_prefixToNamespace);
	}
}
