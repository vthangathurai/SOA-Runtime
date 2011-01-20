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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Internal representation of the operation level cache policy information.
 * The operation level policy has the following information:
 *  TTL
 *  KeyExpression set
 * @author rpallikonda
 *
 */
public class OperationCachePolicy {
	
	/**
	 *  TTL value 
	 */
	private long m_TTL;
	
	/**
	 * key expressions
	 */
	private List<String> m_keyExpressions;

	/**
	 * Get TTL value for the operation.
	 * @return   TTL value for the operation.
	 */
	public long getTTL() {
		return m_TTL;
	}
	
	/**
	 * Sets the TTL value for the operation.
	 * @param TTL  the TTL value for the operation.
	 */

	public void setTTL(long TTL) {
		this.m_TTL = TTL;
	}

	/**
	 * Returns the keyExpressions associated with the operation.
	 * @return List<String> key expressions for the operation.
	 */
	public List<String> getKeyExpressions() {
		if (m_keyExpressions == null)
			m_keyExpressions = new ArrayList<String>();
		return m_keyExpressions;
	}

	/**
	 * Sets the keyExpressions for the operation.
	 * Clears any existing keyExpressions
	 * @param keyExpressions the keyExpressions for the operation
	 */
	public void setKeyExpressions(List<String> keyExpressions) {
		if (keyExpressions == null)
			throw new NullPointerException();
		
		if (m_keyExpressions == null)
			m_keyExpressions =  new ArrayList<String>();
		
		m_keyExpressions.clear();
		m_keyExpressions.addAll(keyExpressions);
	}
	
	/**
	 * Adds the keyExpression passed to the keyExpressions.
	 * @param keyExpression a key expression.
	 */
	public void addKeyExpression(String keyExpression) {
		if (m_keyExpressions == null)
			m_keyExpressions =  new ArrayList<String>();
		
		if (keyExpression == null)
			return;
		
		m_keyExpressions.add(keyExpression);
	}
	
	/**
	 * Copies the internal properties from one OperationCachePolicy to another.
	 * @param outOpPolicy  A target OperationCachePolicy
	 * @param inOpPolicy  A source OperationCachePolicy
	 */
	public static void copyOpPolicy(OperationCachePolicy outOpPolicy,
			OperationCachePolicy inOpPolicy) {
		outOpPolicy.setTTL(inOpPolicy.getTTL());
		outOpPolicy.setKeyExpressions(inOpPolicy.getKeyExpressions());
	}
	
	/**
	 * Create a description of an OperationCachePolicy to the given StringBuffer.
	 * 
	 * @param sb A StringBuffer to hold the description.
	 * @param opPolicy An OperationCachePolicy to be dumpped.
	 * @param delim The delimiter to use.
	 * @param newLine The newline character to use.
	 */
	public static void dumpOperationPolicy(StringBuffer sb,
			OperationCachePolicy opPolicy, String delim, char newLine) {
		sb.append("  TTL: " + opPolicy.getTTL() + newLine);
		sb.append("  KeyExpressions:  ");
		for(String keyExpression : opPolicy.getKeyExpressions()) {
			sb.append(keyExpression).append(delim);
		}
		
	}

}
