/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.config;

import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.impl.internal.config.BaseConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.ConfigUtils;


/**
 * This class represents processed server-side security policy configuration for a service.
 * <p>
 * Note: Most ConfigHolder data is available in higher-level structures. Refer to ServiceDesc and related structures
 * as the primary configuration in the public API for SOA framework.
 * @author rmurphy
 *
 */
public class SecurityPolicyConfigHolder extends BaseConfigHolder {
	private static final char NL = '\n';

	private final String m_adminName;
	private Map<String, OperationSecurityConfig> m_authenticationOperations;
	private Map<String, OperationSecurityConfig> m_authorizationOperations;

    public SecurityPolicyConfigHolder(String adminName) {
		m_adminName = adminName;
	}
    
	/**
	 * Returns the admin name of the service associated with this policy holder. 
	 * @return the admin name
	 */
	public String getAdminName() {
		return m_adminName;
	}
	
    /**
     * Get the complete list of per-operation option lists for authentication.
     * @return the list of option lists.
     */
    public Map<String, OperationSecurityConfig> getAuthenticationOperations() {
        if (m_authenticationOperations == null) {
        	m_authenticationOperations = new HashMap<String, OperationSecurityConfig>();
        }
        if (isReadOnly()) {
        	return copyOperationMap(m_authenticationOperations);
        }
        return m_authenticationOperations;
    }

    /**
     * Get the complete list of per-operation option lists for authorization.
     * @return the list of option lists.
     */
    public Map<String, OperationSecurityConfig> getAuthorizationOperations() {
        if (m_authorizationOperations == null) {
        	m_authorizationOperations = new HashMap<String, OperationSecurityConfig>();
        }
        if (isReadOnly()) {
        	return copyOperationMap(m_authorizationOperations);
        }
        return m_authorizationOperations;
    }

	/**
	 * Safe copy method.
	 * @return a new object with a safe copy of the original data 
	 */
	public SecurityPolicyConfigHolder copy() {
		SecurityPolicyConfigHolder newCH = new SecurityPolicyConfigHolder(m_adminName);
		newCH.m_readOnly = false;
		newCH.m_authenticationOperations = copyOperationMap(m_authenticationOperations);
		newCH.m_authorizationOperations = copyOperationMap(m_authorizationOperations);

		return newCH;
	}

	private Map<String, OperationSecurityConfig> copyOperationMap(Map<String, OperationSecurityConfig> inOperations) {
		if (inOperations == null) {
			return null;
		}
		Map<String, OperationSecurityConfig> outOperations = new HashMap<String, OperationSecurityConfig>();
		for (Map.Entry<String, OperationSecurityConfig> entry : inOperations.entrySet()) {
			OperationSecurityConfig inOperation = entry.getValue();
			OperationSecurityConfig outOperation = new OperationSecurityConfig();
			ConfigUtils.putNameValueList(inOperation.getOption(), outOperation.getOption());
			outOperation.setName(inOperation.getName());
			outOperations.put(outOperation.getName(), outOperation);
		}
		return outOperations;
	}

	/*
	 * Provide a user-readable description of the configuration into a StringBuffer.
	 * @param sb the StringBuffer into which to write the description
	 */
	public void dump(StringBuffer sb) {
		if (m_adminName != null) {
			sb.append("***** Security policy for service: " + m_adminName + NL);
		}

		if (m_authenticationOperations != null) {
			sb.append("========== Authentication options ==========" + NL);
			dumpOperationMap(sb, m_authenticationOperations);
		}
		
		if (m_authorizationOperations != null) {
			sb.append("========== Authorization options ==========" + NL);
			dumpOperationMap(sb, m_authorizationOperations);
			
		}
	}

	private void dumpOperationMap(StringBuffer sb, Map<String, OperationSecurityConfig> operations) {
		for (Map.Entry<String, OperationSecurityConfig> entry : operations.entrySet()) {
			String opName = entry.getKey();
			OperationSecurityConfig opConfig = entry.getValue();
			sb.append("  Operation: " + opName + NL);
			ConfigUtils.dumpOptionList(sb, opConfig, "    ");
		}
	}
}
