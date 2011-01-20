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
package org.ebayopensource.turmeric.runtime.common.cachepolicy;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.cachepolicy.CachePolicyMapper;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.BaseConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.SchemaValidationLevel;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ParseUtils;
import org.w3c.dom.Document;


/**
 * 
 * Class to store the parsed CachePolicy definition. Both Client & Server
 * side load the cache policy. The server side loads the cache policy for validation 
 * purpose only.
 * @author rpallikonda
 *
 */
public class CachePolicyHolder extends BaseConfigHolder {
	
	private static final char NL = '\n';
	private String m_serviceName;
	/**
	 * Map for the operation level cache policies, 
	 * The key is the operation name
	 */
	private Map<String, OperationCachePolicy> m_opCachePolicys;
	
	/**
	 * 
	 * @return Map<String, OperationCachePolicy> map of operation and their associated 
	 *          cache policies
	 */
	public Map<String, OperationCachePolicy> getOperationCachePolicies() {
		if (m_opCachePolicys == null)
			m_opCachePolicys = 	new HashMap<String, OperationCachePolicy>();
		
		if (isReadOnly())
			return copyOperationPolicyMap(m_opCachePolicys);
		
		return m_opCachePolicys;
	}
	
	/**
	 * @return the service name.
	 */
	public String getServiceName() {
		return m_serviceName;
	}

	/**
	 * Sets the service name.
	 * @param name  the service name
	 */
	public void setServiceName(String name) {
		m_serviceName = name;
	}

	/**
	 * Deep copy method.
	 * @return a new object with a deep copy of the original data 
	 */
	public CachePolicyHolder copy() {
		CachePolicyHolder newCH = new CachePolicyHolder();
		newCH.m_readOnly = false;
		newCH.m_serviceName = m_serviceName;
		newCH.m_opCachePolicys = copyOperationPolicyMap(m_opCachePolicys);
		return newCH;
	}

	private Map<String, OperationCachePolicy> copyOperationPolicyMap(
			Map<String, OperationCachePolicy> cachePolicys) {
		
		if (cachePolicys == null)
			return null;
		
		Map<String, OperationCachePolicy> outOpPolicys = new HashMap<String, OperationCachePolicy>();
		
		for(String opName: cachePolicys.keySet()) {
			OperationCachePolicy outOpPolicy = new OperationCachePolicy();
			OperationCachePolicy.copyOpPolicy(outOpPolicy, cachePolicys.get(opName));
			outOpPolicys.put(opName, outOpPolicy);
		}		
		return outOpPolicys;
	}


	
	/**
	 * Provide a user-readable description of the configuration into a StringBuffer.
	 * @param sb the StringBuffer into which to write the description
	 */
	public void dump(StringBuffer sb) {
		if (m_serviceName != null) {
			sb.append("***** Cache policy for service: " + m_serviceName + NL);
		}

		if (m_opCachePolicys != null) {
			sb.append("========== Operation Policies ==========" + NL);
			dumpOperationPolicyMap(sb, m_opCachePolicys);
		}
		

	}

	private void dumpOperationPolicyMap(StringBuffer sb, Map<String, OperationCachePolicy> operations) {
		for (Map.Entry<String, OperationCachePolicy> entry : operations.entrySet()) {
			String opName = entry.getKey();
			OperationCachePolicy opPolicy = entry.getValue();
			sb.append("  Operation: " + opName + NL);
			OperationCachePolicy.dumpOperationPolicy(sb, opPolicy, "    ", NL);
		}
	}
	
	/**
	 * 
	 * Loads the cache policy from the input filename.
	 * 
	 * 
	 * @param adminName The admin name of a service.
	 * @param filename The file name to load the CachePolicy xml file.
	 * @param schemaname The file name pointing to XML Schema for cache policy.
	 * @return A CachePolicyHolder - a holder class to store CachePolicy for a service.
	 * @throws ServiceCreationException Exception when failed to load the cache policy.
	 */
	
	public static CachePolicyHolder loadCachePolicy(String adminName, String filename, String schemaname)  throws ServiceCreationException {	
		CachePolicyHolder dst = null;
		Document cachePolicyDoc = ParseUtils.parseConfig(filename, schemaname, true, "serviceCachePolicy", SchemaValidationLevel.ERROR);
		if (cachePolicyDoc == null) {
			// No cachePolicy file exists.. so null
			return dst;
		}
		dst = new CachePolicyHolder();
		CachePolicyMapper.map(filename, cachePolicyDoc.getDocumentElement(), dst);
		return dst;
	}
	
	/**
	 *  Loads the cache policy from the inputstream provided.
	 * @param in  An InputStream for reading the CachePolicy xml file.
	 * @param assocURL  The CachePolicy access end point URL.
	 * @param schemaname The CachePolicy XML Schema file name.
	 * @return A CachePolicyHolder - a holder class to store CachePolicy for a service.
	 * @throws ServiceCreationException Exception when failed to load the cache policy.
	 */
	
	public static CachePolicyHolder loadCachePolicy(InputStream in, String assocURL, String schemaname) throws ServiceCreationException  {
		Document cachePolicyDoc;
		CachePolicyHolder dst = null;
		cachePolicyDoc = ParseUtils.parseConfig(in, assocURL, schemaname,
				"serviceCachePolicy", SchemaValidationLevel.ERROR);
		if (cachePolicyDoc == null) {
			return null;
		}
		dst = new CachePolicyHolder();
		CachePolicyMapper.map(assocURL, cachePolicyDoc.getDocumentElement(),
				dst);
		return dst;
	}

}
