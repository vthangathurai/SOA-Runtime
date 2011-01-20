/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;
import org.mortbay.util.MultiMap;
import org.mortbay.util.UrlEncoded;

public final class NVAssert {
	private NVAssert() {
		/* prevent instantiation */
	}

	/**
	 * Compare two NV strings and report failure in a human readable form.
	 * <p>
	 * Key order is irrelevant, if all of the keys exist in both sides it is considered equivalent.
	 * 
	 * @param expected
	 *            the control to check against.
	 * @param actual
	 *            the actual NV to check.
	 */
	public static void assertEquals(String expected, String actual) {
		// Speedy check first.
		if (expected.equals(actual)) {
			// It's the same.
			return;
		}

		Map<String, String> expectedMap = parseNV(expected);
		Map<String, String> actualMap = parseNV(actual);

		Assert.assertEquals(humanReadable(expectedMap),
				humanReadable(actualMap));
	}

	/**
	 * Simply makes the map readable by humans, which in turn makes the test results more meaningful and easier to read.
	 * With an added benefit that the Eclipse Junit views will highlight the differences in a much clearer fashion.
	 * 
	 * @param nvmap
	 *            the nvmap to represent in human readable form
	 * @return the human readable form.
	 */
	private static String humanReadable(Map<String, String> nvmap) {
		StringBuilder str = new StringBuilder();
		boolean needsDelim = false;
		for (Entry<String, String> entry : nvmap.entrySet()) {
			if (needsDelim) {
				str.append("@").append(SystemUtils.LINE_SEPARATOR);
			}
			str.append('&');
			str.append(entry.getKey()).append("=");
			str.append(entry.getValue());
			needsDelim = true;
		}
		return str.toString();
	}

	/**
	 * Using Jetty internal utility methods, parse an NV map using industry standard mechanisms.
	 * <p>
	 * Resulting map utilizes TreeMap to gain benefit of key sorting.
	 * 
	 * @param rawnv
	 *            the raw NV string
	 * @return the TreeMap of String pairs
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> parseNV(String rawnv) {
		MultiMap mm = new MultiMap();
		UrlEncoded.decodeTo(rawnv, mm, "UTF-8");
		Map<String, String> nvmap = new TreeMap<String, String>();
		Iterator<String> keyIter = mm.keySet().iterator();
		while(keyIter.hasNext()) {
			String key = keyIter.next();
			String value = mm.getString(key);
			nvmap.put(key, value);
		}
		return nvmap;
	}
}
