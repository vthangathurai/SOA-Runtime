/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.utils;

import java.util.List;
import java.util.Map;

/**
 * BindingUtils class provides utility methods for Binding Framework needs.
 * 
 * @author ichernyshev
 * @author wdeng
 */
public final class BindingUtils {

	private final static int[] HEX_DIGITS = new int[128];

	/**
	 * Checks whether object are equal, supporting null as object reference.
	 * 
	 * @param obj1 first object
	 * @param obj2 second object
	 * @return true if obj1 and obj2 are the same.
	 */
	public static boolean sameObject(final Object obj1, final Object obj2) {
		if (obj1 == obj2) {
			// both are the same object or nulls
			return true;
		}

		if (obj1 == null || obj2 == null) {
			// another one is not null
			return false;
		}

		return obj1.equals(obj2);
	}

	/**
	 * Get the name of the package given the class name.
	 * @param className - Name of the class
	 * @return String - Name of the package
	 */
	public static String getPackageName(final String className) {
		int p = className.lastIndexOf('.');
		return (p != -1 ? className.substring(0, p) : "");
	}

	/**
	 * Get the name of the package given the instance of the class. 
	 * @param clazz - Instance of the class
	 * @return String - Name of the package
	 */
	public static String getPackageName(final Class clazz) {
		return getPackageName(clazz.getName());
	}

	/**
	 * Parse the delimiter (comma) seperated name list and return the list of names. 
	 * @param options - Map containing delimiter separated paramNames list
	 * @param paramName - Name of the parameter
	 * @return List of names
	 *
	 */
	public static List<String> parseNameList(final Map<String,String> options, 
			final String paramName) {
		String paramStr = options.get(paramName);

		if (paramStr == null) {
			return null;
		}

		List<String> result = com.ebay.kernel.util.StringUtils.splitStr(paramStr, ',', true);

		if (result != null && result.isEmpty()) {
			return null;
		}

		return result;
	}

	/**
	 * Returns the Hex representation for the given integer digit.
	 * @param c - integer digit
	 * @return Hex representation of the digit
	 */
	public static int getHexDigitValue(int c) {
		if (c >= '0' && c <= 127) {
			return HEX_DIGITS[c];
		}

		return -1;
	}

	/**
	 * Default construction.
	 */
	private BindingUtils() {
		// no instances
	}

	static {
		for (int i=0; i<HEX_DIGITS.length; i++) {
			HEX_DIGITS[i] = -1;
		}

		HEX_DIGITS['0'] = 0;
		HEX_DIGITS['1'] = 1;
		HEX_DIGITS['2'] = 2;
		HEX_DIGITS['3'] = 3;
		HEX_DIGITS['4'] = 4;
		HEX_DIGITS['5'] = 5;
		HEX_DIGITS['6'] = 6;
		HEX_DIGITS['7'] = 7;
		HEX_DIGITS['8'] = 8;
		HEX_DIGITS['9'] = 9;
		HEX_DIGITS['A'] = 10;
		HEX_DIGITS['B'] = 11;
		HEX_DIGITS['C'] = 12;
		HEX_DIGITS['D'] = 13;
		HEX_DIGITS['E'] = 14;
		HEX_DIGITS['F'] = 15;
		HEX_DIGITS['a'] = 10;
		HEX_DIGITS['b'] = 11;
		HEX_DIGITS['c'] = 12;
		HEX_DIGITS['d'] = 13;
		HEX_DIGITS['e'] = 14;
		HEX_DIGITS['f'] = 15;
	}
}
