/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.cachepolicy;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.ebayopensource.turmeric.runtime.common.impl.utils.ReflectionUtils;



/**
 * Internal helper class for validating cache key expression
 * Has the list of Class types that are supported. The leaf node
 * of the key expression is expected to be a either primitive type or
 * one of the supported types
 * @author rpallikonda
 *
 */
public class KeyExpressionValidator {
	
	/**
	 * Supported basic types of objects as key. 
	 */
	@SuppressWarnings("serial")
	static final Set<Class<?>> SUPPORTED_TYPES = new HashSet<Class<?>>() {
        {
            add(Date.class);
            add(String.class);
            add(Boolean.class);
            add(Byte.class);
            add(Character.class);
            add(Integer.class);
            add(Short.class);
            add(Long.class);
            add(Float.class);
            add(Double.class);
        }
	};
	
	/**
	 * Validate that the key expression given is value with the given class.
	 * 
	 * @param clazz A Java bean's Class object.
	 * @param keyExpr A key experssion.
	 * @return True if validation successes.
	 */
	public static boolean validateKeyExpression(Class clazz, String keyExpr) {
		StringTokenizer st = new StringTokenizer(keyExpr, ".");
		Class curClz = clazz;
		Method m = null;
		while (st.hasMoreElements()) {
			String xmlElement = (String) st.nextElement();			
			m = ReflectionUtils.findMatchingJavaMethod(curClz, xmlElement);
			if (m == null) {
				// We fail the validation on encountering invalid intermediate elements
				return false;
			}
			curClz = m.getReturnType();
		}
		return validateReturnType(curClz);
	}

	/**
	 * Validate the given class is of primitive type or in the SUPPORTED_TYPES list.
	 * 
	 * @param curClz A Java bean's Class object.
	 * @return True if validation successes.
	 */
	public static boolean validateReturnType(Class curClz) {
		return curClz.isPrimitive() || SUPPORTED_TYPES.contains(curClz);
	}

}
