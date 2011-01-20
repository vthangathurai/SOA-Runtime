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
import java.util.List;

import com.ebay.kernel.context.ServerContext;
import com.ebay.kernel.util.StringUtils;
import com.ebay.kernel.variable.provider.DefaultVariableHolder;
import com.ebay.kernel.variable.provider.IVariableHolder;
import com.ebay.kernel.variable.provider.IVariableProvider;
import com.ebay.kernel.variable.provider.VariableProviderRegistry;

/**
 * @author ichernyshev
 */
final class SOAVariableProvider implements IVariableProvider {

	private static final String NET_DOMAIN = "net_domain";
	private static final String NET_DOMAIN_POS = "net_domain.";
	private static final String NET_DOMAIN_NEG = "net_domain.-";

	private static boolean s_initialized;
	private static String m_hostName;

	public IVariableHolder getValue(String name) {
		int p = name.indexOf('.');

		String firstName;
		String lastName;
		if (p != -1) {
			firstName = name.substring(0, p);
			lastName = name.substring(p + 1);
		} else {
			firstName = name;
			lastName = null;
		}

		if (firstName.equals(NET_DOMAIN)) {
			int id;
			if (lastName != null) {
				// exceptions will be caught and cached by the caller
				id = Integer.parseInt(lastName);
			} else {
				id = 0;
			}

			String domainStr = getNetDomain(id);
			if (domainStr == null) {
				return null;
			}

			return new DefaultVariableHolder(domainStr);
		}

		return null;
	}

	public Collection<String> getAllNames() {
		Collection<String> result = new ArrayList<String>();

		String hostName = getHostName();
		if (hostName != null) {
			int dotCount = getCharCount(hostName, '.');
			//for (int i=1; i<=dotCount; i++) {
			for (int i=dotCount; i<=dotCount; i++) {
				result.add(NET_DOMAIN_POS + i);
			}
			//for (int i=1; i<=dotCount-1; i++) {
			for (int i=1; i<=1; i++) {
				result.add(NET_DOMAIN_NEG + i);
			}
			result.add(NET_DOMAIN);
		}

		return result;
	}

	private String getNetDomain(int id) {
		String hostName = getHostName();
		if (hostName == null) {
			return null;
		}

		List<String> parts = StringUtils.splitStr(hostName, '.');
		if (parts.isEmpty()) {
			return null;
		}

		int startIdx;
		if (id > 0) {
			startIdx = parts.size() - id;
			if (startIdx < 0) {
				startIdx = 0;
			}
		} else {
			startIdx = 1 - id;
			if (startIdx >= parts.size()) {
				startIdx = parts.size()-1;
			}
		}

		StringBuilder sb = new StringBuilder();
		for (int i=startIdx; i<parts.size(); i++) {
			if (sb.length() > 0) {
				sb.append('.');
			}
			sb.append(parts.get(i));
		}
		return sb.toString();
	}

	private String getHostName() {
		if (m_hostName == null) {
			m_hostName = ServerContext.getFullHostName();
		}

		return m_hostName;
	}

	private int getCharCount(String str, char c) {
		int result = 0;
		for (int i=0; i<str.length(); i++) {
			if (str.charAt(i) == c) {
				result++;
			}
		}
		return result;
	}

	static synchronized void init() {
		if (s_initialized) {
			return;
		}

		s_initialized = true;

		VariableProviderRegistry.registerProvider("turmeric", new SOAVariableProvider());
	}
}
