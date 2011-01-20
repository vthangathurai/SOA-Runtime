/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;

/**
 * SecurityContext is an interface through which all handlers get access to
 * security information of the incoming message.
 * 
 * @author gyue, ichernyshev
 */
public interface SecurityContext {

	/**
	 * Gets the authentication custom data from the security context.
	 * 
	 * @param key
	 *            key of the data to be retrieved
	 * @return the value of the key
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public Object getAuthnCustomData(String key) throws ServiceException;

	/**
	 * Gets the authentication subject from the security context.
	 * 
	 * @param type
	 *            the type of the subject
	 * @return the subject
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public String getAuthnSubject(String type) throws ServiceException;

	/**
	 * Gets the authentication subject types from the security context.
	 * 
	 * @return all the types in the security context
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public Collection<String> getAuthnSubjectTypes() throws ServiceException;

	/**
	 * Gets the authentication subjects from the security context.
	 * 
	 * @return the map containing the subjects. Keys are the types of the
	 *         subject and the values are the names of subjects
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public Map<String, String> getAuthnSubjects() throws ServiceException;

	/**
	 * Gets the credential from the security context.
	 * 
	 * @param name
	 *            the name of the credential
	 * @return the value of the credential
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public String getCredential(String name) throws ServiceException;

	/**
	 * Gets all the credentials from the security context.
	 * 
	 * @return the map containing the credentials. Keys are the names of the
	 *         credentials.
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public Map<String, String> getCredentials() throws ServiceException;

	/**
	 * Sets the authentication subject.
	 * 
	 * @param type
	 *            the type of the subject
	 * @param name
	 *            the name of the subject
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public void setAuthnSubject(String type, String name)
			throws ServiceException;

	/**
	 * Sets the authentication subjects.
	 * 
	 * @param subjects
	 *            map containing the subjects. Keys are the types and values are
	 *            the subject names.
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public void setAuthnSubjects(Map<String, String> subjects)
			throws ServiceException;

	/**
	 * Sets the authentication custom data.
	 * 
	 * @param key
	 *            key of the data
	 * @param value
	 *            value of the data
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public void setAuthnCustomData(String key, Object value)
			throws ServiceException;

	/**
	 * Sets the credentials.
	 * 
	 * @param name
	 *            name of the credential
	 * @param value
	 *            value of the credential
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public void setCredential(String name, String value)
			throws ServiceException;

	/**
	 * Adds the resolved subject group.
	 * 
	 * @param groupName
	 *            the group name
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public void setResolvedSubjectGroup(String groupName)
			throws ServiceException;

	/**
	 * Sets the resolved subject group list.
	 * 
	 * @param groupList
	 *            the list containing the subject group names
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public void setResolvedSubjectGroups(List<String> groupList)
			throws ServiceException;

	/**
	 * Gets the resolved subject group list.
	 * 
	 * @return the list containing the subject group names.
	 * @throws ServiceException
	 *             throw if error happens
	 */
	public List<String> getResolvedSubjectGroups() throws ServiceException;

}
