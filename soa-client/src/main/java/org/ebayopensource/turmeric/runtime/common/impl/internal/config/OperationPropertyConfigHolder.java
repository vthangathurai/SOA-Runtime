/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class OperationPropertyConfigHolder extends BaseConfigHolder {
	private Map<String, Map<String, String>> m_operations = new HashMap<String, Map<String, String>>();
	private static final char NL = '\n';

	/**
	 * Set the property map of a particular operation by name.
	 * @param opname the name of the operation
	 * @param operation the operation-specific configuration to set
	 */
	public void setOperationPropertyMap(String opname, Map<String, String> propertyMap) {
		checkReadOnly();
		m_operations.put(opname, propertyMap);
	}
	
	public Map<String, String> getOperationPropertyMap(String opname) {
		if (isReadOnly()) {
			return new HashMap<String, String>(m_operations.get(opname));
		}
		return m_operations.get(opname);
	}

	/**
	 * Safe copy method.
	 * @return a new object with a safe copy of the original data 
	 */
	public OperationPropertyConfigHolder copy() {
		OperationPropertyConfigHolder newCH = new OperationPropertyConfigHolder();
		newCH.m_readOnly = false;
		newCH.m_operations = copyOperations(m_operations);

		return newCH;
	}

	private Map<String, Map<String, String>> copyOperations(Map<String, Map<String, String>> inOpMap) {
		if (inOpMap == null) {
			return null;
		}
		Map<String, Map<String, String>> outOpMap = new HashMap<String, Map<String, String>>();
		for (Map.Entry<String, Map<String, String>> entry : inOpMap.entrySet()) {
			String key = entry.getKey();
			Map<String, String> inOp = entry.getValue();
			Map<String, String> outOp = new HashMap<String, String>(inOp);
			outOpMap.put(key, outOp);
		}
		return outOpMap;
	}

	/*
	 * Provide a user-readable description of the operation maps into a StringBuffer.
	 * @param sb the StringBuffer into which to write the description
	 */
	public void dump(StringBuffer sb) {
		if (m_operations == null || m_operations.isEmpty()) {
			return;
		}
		sb.append("========== Operation Properties ==========" + NL);
		for (Map.Entry<String, Map<String, String>> entry : m_operations.entrySet()) {
			String opname = entry.getKey();
			sb.append("Operation: " + opname + NL);
			Map<String, String> opMap = entry.getValue();
			ConfigUtils.dumpStringMap(sb, opMap, "  ");
		}
	}
}
