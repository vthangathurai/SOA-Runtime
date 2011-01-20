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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ichernyshev
 */
public class RequestPatternMatcher<E> {

	private final boolean m_caseSensitive;
	private ArrayList<RequestPattern<E>> m_patterns = new ArrayList<RequestPattern<E>>();

	public RequestPatternMatcher(boolean caseSensitive) {
		m_caseSensitive = caseSensitive;
	}

	public E findTarget(String uri, Map<String,String> headers) {
		return findTarget(uri, headers, false);
	}

	public E findTarget(String uri, Map<String,String> headers, boolean allowMany) {
		E result = null;
		for (int i=0; i<m_patterns.size(); i++) {
			RequestPattern<E> pattern = m_patterns.get(i);
			E result2 = pattern.match(uri, headers);
			if (result2 != null) {
				if (allowMany) {
					result = result2;
					break;
				}

				if (result != null) {
					// this is the second pattern match, which is not allowed
					// TODO: throw an exception here
					return null;
				}

				result = result2;
			}
		}

		return result;
	}

	public void addUriPattern(String uriPattern, E target) {
		addPattern(new RequestPatternUriRegex<E>(uriPattern, target, m_caseSensitive));
	}

	public void addHeaderPattern(String headerName, String headerValue, E target)
	{
		// header name is always case-insensitive
		headerName = headerName.toUpperCase();

		if (!m_caseSensitive && headerValue != null) {
			headerValue = headerValue.toUpperCase();
		}

		int idx = findHeaderValuePattern(headerName);

		if (idx == -1) {
			Map<String,E> headerValues = new HashMap<String,E>();
			headerValues.put(headerValue, target);
			addPattern(new RequestPatternHeaderValue<E>(headerName, headerValues));
			return;
		}

		RequestPatternHeaderValue<E> oldPattern = (RequestPatternHeaderValue<E>)m_patterns.get(idx);
		RequestPatternHeaderValue<E> newPattern = new RequestPatternHeaderValue<E>(
			oldPattern, headerValue, target);

		replacePattern(idx, newPattern);
	}

	private int findHeaderValuePattern(String headerName)
	{
		for (int i=0; i<m_patterns.size(); i++) {
			RequestPattern<E> result = m_patterns.get(i);
			if (!(result instanceof RequestPatternHeaderValue)) {
				continue;
			}

			RequestPatternHeaderValue<E> result2 = (RequestPatternHeaderValue<E>)result;
			if (result2.getHeaderName().equals(headerName)) {
				return i;
			}
		}

		return -1;
	}

	private void addPattern(RequestPattern<E> pattern)
	{
		ArrayList<RequestPattern<E>> patterns = new ArrayList<RequestPattern<E>>(m_patterns.size() + 1);
		patterns.addAll(m_patterns);
		patterns.add(pattern);
		m_patterns = patterns;
	}

	private void replacePattern(int idx, RequestPattern<E> pattern)
	{
		ArrayList<RequestPattern<E>> patterns = new ArrayList<RequestPattern<E>>(m_patterns.size());
		patterns.addAll(m_patterns);
		patterns.set(idx, pattern);
		m_patterns = patterns;
	}

	static abstract class RequestPattern<E> {
		public abstract E match(String uri, Map<String,String> headers);
	}

	static class RequestPatternUriRegex<E> extends RequestPattern<E> {
		private final Pattern m_uriPattern;
		private final E m_target;

		public RequestPatternUriRegex(String uriPattern, E target, boolean caseSensitive)
		{
			if (uriPattern == null || target == null) {
				throw new NullPointerException();
			}

			int flags = 0;
			if (!caseSensitive) { 
				flags |= Pattern.CASE_INSENSITIVE;
			}

			m_uriPattern = Pattern.compile(uriPattern, flags);
			m_target = target;
		}

		@Override
		public E match(String uri, Map<String,String> headers) {
			if (uri == null) {
				return null;
			}

			Matcher matcher = m_uriPattern.matcher(uri);
			if (matcher.matches()) {
				return m_target;
			}

			return null;
		}
	}

	static class RequestPatternHeaderValue<E> extends RequestPattern<E> {
		private final String m_headerName;
		private final Map<String,E> m_headerValues;

		public RequestPatternHeaderValue(String headerName,
			Map<String,E> headerValues)
		{
			if (headerName == null || headerValues == null) {
				throw new NullPointerException();
			}

			for (Iterator<Map.Entry<String,E>> it=headerValues.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry<String,E> e = it.next();
				if (e.getKey() == null || e.getValue() == null) {
					throw new NullPointerException();
				}
			}

			m_headerName = headerName;
			m_headerValues = headerValues;
		}

		public RequestPatternHeaderValue(RequestPatternHeaderValue<E> oldPattern,
			String newHeaderValue, E newTarget)
		{
			if (newHeaderValue == null || newTarget == null) {
				throw new NullPointerException();
			}

			m_headerName = oldPattern.m_headerName;

			m_headerValues = new HashMap<String,E>(oldPattern.m_headerValues);
			m_headerValues.put(newHeaderValue, newTarget);
		}

		public String getHeaderName() {
			return m_headerName;
		}

		@Override
		public E match(String uri, Map<String,String> headers) {
			if (headers == null) {
				return null;
			}

			String otherValue = headers.get(m_headerName);
			if (otherValue == null) {
				return null;
			}

			E result = m_headerValues.get(otherValue);
			if (result == null) {
				// TODO: throw an exception indicating unknown header value
			}

			return result;
		}
	}
}
