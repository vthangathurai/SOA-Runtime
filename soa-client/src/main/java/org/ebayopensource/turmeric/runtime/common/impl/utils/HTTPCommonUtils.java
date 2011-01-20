/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import com.ebay.kernel.util.StringUtils;
import com.ebay.kernel.util.guid.Guid;

public final class HTTPCommonUtils {
	private static Guid s_guid;
	private static boolean s_guidCreated;

	public static String formatContentType(String mimeType, Charset charset) {
		if (charset == null) {
			return mimeType;
		}
		return mimeType + "; charset=" + charset;
	}

	public static void addServiceAndOperationHeaders(QName serviceName, String operationName, Map<String, String> headerMap) {
		if (operationName != null) {
			headerMap.put(SOAHeaders.SERVICE_OPERATION_NAME, operationName);
		}
		if (serviceName != null) {
			headerMap.put(SOAHeaders.SERVICE_NAME, serviceName.toString());
		}

	}

	public static void addG11nHeaders(G11nOptions g11nOptions, Map<String, String> headerMap) {
		if (g11nOptions == null) {
			return;
		}
		Charset charset = g11nOptions.getCharset();
		if (!charset.equals(G11nOptions.DEFAULT_CHARSET)) {
			headerMap.put(SOAHeaders.MESSAGE_ENCODING, charset.name());
		}

		// build global id header
		String globalId = g11nOptions.getGlobalId();
		if (globalId != null && !globalId.equals(SOAConstants.DEFAULT_GLOBAL_ID)) {
			headerMap.put(SOAHeaders.GLOBAL_ID, globalId);
		}

		// build locales header
		List<String> locales = g11nOptions.getLocales();
		addValueListHeader(SOAHeaders.LOCALE_LIST, locales, headerMap);
	}

	public static void addValueListHeader(String name, Collection<?> list, Map<String,String> headerMap) {
		if (list == null || list.isEmpty()) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (Iterator<?> it=list.iterator(); it.hasNext(); ) {
			if (sb.length() > 0) {
				sb.append(',');
			}
			Object value = it.next();
			sb.append(value);
		}
		headerMap.put(name, sb.toString());
	}

	public static void encodeCookieValue(StringBuffer buf, Cookie[] cookies) {
		if (cookies == null || cookies.length == 0) {
			return;
		}

		buf.append("$Version=\"");
		buf.append(0);
		//buf.append(cookies[0].getVersion());
		buf.append("\"; ");

		for (int i = 0; i < cookies.length; i++) {
			if (i > 0) {
				buf.append("; ");
			}
			encodeCookieValue(buf, cookies[i]);
		}
	}

	public static String encodeCookieValue(StringBuffer buf, Cookie cookie) {
		String name = cookie.getName();
		validateCookieName(name);

		buf.append(name);
		buf.append("=\"");
		buf.append(cookie.getValue());
		buf.append("\"");

		/*if (cookie.getDomain() != null) {
			buf.append("; $Domain=\"");
			buf.append(cookie.getDomain());
			buf.append("\"");
		}
		if (cookie.getPath() != null) {
			buf.append("; $Path=\"");
			buf.append(cookie.getPath());
			buf.append("\"");
		}*/

		return (buf.toString());

	}

	public static Cookie parseSetCookieValue(String cookieString) {
		List<String> nvpairs = splitQuotedStr(cookieString, ';', '"', true);
		StringUtils.splitStr(cookieString, ';', true);
		Cookie cookie = null;
		for (int i = 0; i < nvpairs.size(); i++) {
			String nvpair = nvpairs.get(i);
			String name  = "";
			String value = "";

			int ix = nvpair.indexOf('=');
			if( ix >= 0 ) {
				name = nvpair.substring(0, ix).trim();
				value = nvpair.substring(ix+1).trim();
				if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
					value = value.substring(1, value.length()-1);
				} else if (value.startsWith("\"") || value.endsWith("\"")){
					throw new IllegalArgumentException(cookieString);
				}
			}

			if (i == 0) {
				cookie = new Cookie(name, value);
			}/* else if (name.equalsIgnoreCase("Comment")) {
				cookie.setComment(value);
			} else if (name.equalsIgnoreCase("Domain")) {
				cookie.setDomain(value);
			} else if (name.equalsIgnoreCase("Max-Age")) {
				// TODO: throw ServiceException, or ignore, do not throw IllegalArgumentException
				try {
					int maxAge = Integer.parseInt(value);
					cookie.setMaxAge(maxAge);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(cookieString, e);
				}
			} else if (name.equalsIgnoreCase("Path")) {
				cookie.setPath(value);
			} else if (name.equalsIgnoreCase("Secure")) {
				cookie.setSecure(true);
			} else if (name.equalsIgnoreCase("Version")) {
				try {
					int version = Integer.parseInt(value);
					cookie.setVersion(version);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(cookieString, e);
				}
			}*/
		}
		return cookie;
	}

	public static void validateCookieName(String name) {
		if (!isCookieToken(name) ||
			name.equalsIgnoreCase("Comment") ||
			name.equalsIgnoreCase("Discard") ||
			name.equalsIgnoreCase("Domain") ||
			name.equalsIgnoreCase("Expires") ||
			name.equalsIgnoreCase("Max-Age") ||
			name.equalsIgnoreCase("Path") ||
			name.equalsIgnoreCase("Secure") ||
			name.equalsIgnoreCase("Version") ||
			name.startsWith("$"))
		{
			// TODO: throw ServiceRuntimeException here
			throw new IllegalArgumentException("Cookie name cannot be a token: '" + name + "'");
		}
	}

	private static boolean isCookieToken(String value) {
		int len = value.length();
		for(int i=0; i<len; i++) {
			char c = value.charAt(i);
			if (c < ' ' || c >= '\177' || ",; ".indexOf(c) != -1) {
				return false;
			}
		}

		return true;
	}

	public static List<String> splitQuotedStr(String str, char delimiter, char quote, boolean trim) {
		int startPos = 0;
		boolean insideQuotation = false;
		List<String> result = new ArrayList<String>();
		for (int i=0; i<str.length(); i++) {
			char c = str.charAt(i);
			if (c == quote) {
				insideQuotation = !insideQuotation;
			}
			if (!insideQuotation && c == delimiter) {
				String subStr = str.substring(startPos, i);
				if (trim) {
					subStr = subStr.trim();
				}
				result.add(subStr);
				startPos = i + 1;
				continue;
			}
		}
		if (startPos < str.length()) {
			String subStr = str.substring(startPos, str.length());
			if (trim) {
				subStr = subStr.trim();
			}
			result.add(subStr);
		}
		return result;
	}

	public static void applySuppressHeaderSet(Set<String> suppressHeaderSet, Map<String,String> result) throws ServiceException {
		for (String headerName : suppressHeaderSet) {
			if (headerName != null) {
				result.remove(headerName);
			}
		}
	}

	public static void applyHeaderMap(Map<String, String> headerMap, Map<String,String> result) throws ServiceException {
		applyHeaderMap(headerMap, result, null);
	}

	public static void applyHeaderMap(Map<String, String> headerMap, Map<String,String> result, List<Throwable> errors) throws ServiceException {
		String aliasName, headerName, headerValue;
		for (Map.Entry<String,String> h : headerMap.entrySet()) {
			aliasName = h.getKey();
			headerName = h.getValue();
			if (headerName != null && aliasName != null) {
				if (result.containsKey(aliasName)) {
					headerValue = result.remove(aliasName);
					addTransportHeader(headerName, headerValue, result, true, false, errors);
				}
			}
		}
	}

	public static void addTransportHeader(String name, String value, Map<String,String> target,
			boolean checkConflicts, boolean keepOriginalValue, List<Throwable> errors) throws ServiceException {
		String oldValue = target.get(name);
		if (oldValue != null) {
			if (oldValue.equals(value)) {
				return;
			}

			if (checkConflicts) {
				if (errors != null)
					errors.add(new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_PARAM_CONFLICT,
							ErrorConstants.ERRORDOMAIN, new Object[] {name, oldValue, value})));
				return;
			}

			if (keepOriginalValue) {
				return;
			}
		}

		target.put(name, value);
	}
	

	public static String generateRequestGuid() {
		Guid guid = getGuid();
		if (guid == null) {
			return null;
		}

		return guid.nextISOGUID();
	}

	private static synchronized Guid getGuid() {
		if (s_guidCreated) {
			return s_guid;
		}

		s_guidCreated = true;
		try {
			s_guid = new Guid();
			return s_guid;
		} catch (Throwable e) {
			getLogger().log(Level.SEVERE, "Unable to instantiate GUID: " + e.toString(), e);
			return null;
		}
	}


	private static Logger getLogger() {
		return LogManager.getInstance(HTTPCommonUtils.class);
	}

	private HTTPCommonUtils() {
		// no instances
	}
}
