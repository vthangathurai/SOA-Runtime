/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.security.SecurityContext;


/**
 * BaseSecurityContextImpl is the base implementation class to SecurityContext interface.
 * This encapsulates all spf-sif common security related information assiocated to a service invocation
 *
 * @author gyue, ichernyshev
 */
public class BaseSecurityContextImpl implements SecurityContext {

	private final BaseMessageContextImpl m_msgCtx;

	private Map<String,Object> m_authnCustomData;
	private Map<String,String> m_authnSubjects;
	private Map<String,String> m_credentials;
	private List<String> m_resolvedSubjectGroups;
 
	public BaseSecurityContextImpl(BaseMessageContextImpl msgCtx) {
		m_msgCtx = msgCtx;
	}

	protected final BaseMessageContextImpl getMessageContext() {
		return m_msgCtx;
	}

	public final Object getAuthnCustomData(String key) throws ServiceException {
		if (m_authnCustomData == null) {
			return null;
		}
		return m_authnCustomData.get(key);
	}

	public final String getAuthnSubject(String type) throws ServiceException {
		if (m_authnSubjects == null) {
			return null;
		}
		return m_authnSubjects.get(type);
	}

	public final Collection<String> getAuthnSubjectTypes() throws ServiceException {
		if (m_authnSubjects == null) {
			return CollectionUtils.EMPTY_STRING_SET;
		}

		return Collections.unmodifiableCollection(
			new ArrayList<String>(m_authnSubjects.keySet()));
	}

	public final Map<String,String> getAuthnSubjects() throws ServiceException {
		if (m_authnSubjects == null) {
			return CollectionUtils.EMPTY_STRING_MAP;
		}

		return Collections.unmodifiableMap(
			new HashMap<String,String>(m_authnSubjects));
	}
	
	public final String getCredential(String name) throws ServiceException {
		if (m_credentials == null) {
			return null;
		}
		return m_credentials.get(name);
	}

	public final Collection<String> getCredentialNames() throws ServiceException {
		if (m_credentials == null) {
			return CollectionUtils.EMPTY_STRING_SET;
		}

		return Collections.unmodifiableCollection(
			new ArrayList<String>(m_credentials.keySet()));
	}

	public final Map<String,String> getCredentials() throws ServiceException {
		if (m_credentials == null) {
			return CollectionUtils.EMPTY_STRING_MAP;
		}

		return Collections.unmodifiableMap(
			new HashMap<String,String>(m_credentials));
	}
	

	protected void checkAuthnDataChange() throws ServiceException {
		// noop here, allow subclass to implement
	}
	
	protected void checkAuthzDataChange() throws ServiceException {
		// noop here, allow subclass to implement
	}
	
	protected void checkBlacklistDataChange() throws ServiceException {
		// noop here, allow subclass to implement
	}
	
	protected void checkWhitelistDataChange() throws ServiceException {
		// noop here, allow subclass to implement
	}

	public final void setAuthnCustomData(String key, Object value)
		throws ServiceException
	{
		checkAuthnDataChange();

		if (key == null || value == null) {
			throw new NullPointerException();
		}

		if (m_authnCustomData == null) {
			m_authnCustomData = new HashMap<String,Object>();
		}

		m_authnCustomData.put(key, value);
	}

	public final void setAuthnSubject(String type, String name)
		throws ServiceException
	{
		checkAuthnDataChange();

		if (type == null || name == null) {
			throw new NullPointerException();
		}

		if (m_authnSubjects == null) {
			m_authnSubjects = new HashMap<String,String>();
		}

		m_authnSubjects.put(type, name);
	}

	public void setAuthnSubjects(Map<String,String> subjects)
		throws ServiceException
	{
		checkAuthnDataChange();

		if (subjects.containsKey(null) || subjects.containsValue(null)) {
			throw new NullPointerException();
		}

		if (m_authnSubjects == null) {
			m_authnSubjects = new HashMap<String,String>();
		}

		m_authnSubjects.putAll(subjects);
	}
	
	public final void setCredential(String name, String value)
		throws ServiceException
	{
		checkAuthnDataChange();
	
		if (name == null || value == null) {
			throw new NullPointerException();
		}
	
		if (m_credentials == null) {
			m_credentials = new HashMap<String,String>();
		}
	
		m_credentials.put(name, value);
	}
	
	public void setCredentials(Map<String,String> credentials)
		throws ServiceException
	{
		checkAuthnDataChange();
	
		if (credentials.containsKey(null) || credentials.containsValue(null)) {
			throw new NullPointerException();
		}
	
		if (m_authnSubjects == null) {
			m_authnSubjects = new HashMap<String,String>();
		}
	
		m_authnSubjects.putAll(credentials);
	}
	
	public final void setResolvedSubjectGroup(String groupName)
		throws ServiceException
	{
		if (groupName == null) {
			throw new NullPointerException();
		}

		if (m_resolvedSubjectGroups == null) {
			m_resolvedSubjectGroups = new ArrayList<String>();
		}

		m_resolvedSubjectGroups.add(groupName);
	}
	
	public final void setResolvedSubjectGroups(List<String> groupList)
		throws ServiceException
	{
		if (groupList == null) {
			throw new NullPointerException();
		}
	
		if (m_resolvedSubjectGroups == null) {
			m_resolvedSubjectGroups = new ArrayList<String>();
		}
	
		m_resolvedSubjectGroups.addAll(groupList);		
	}
	
	public final List<String> getResolvedSubjectGroups() throws ServiceException {
		if (m_resolvedSubjectGroups == null) {
			return CollectionUtils.EMPTY_STRING_LIST;
		}

		return Collections.unmodifiableList(
			new ArrayList<String>(m_resolvedSubjectGroups));
	}
	
	
	
}
