/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * @author ichernyshev
 */
public final class ServiceOperationParamDescImpl implements ServiceOperationParamDesc {

	private final List<Class> m_rootJavaTypes;
	private final List<DataElementSchema> m_rootElements;
	private final Map<QName,Class> m_xmlToJavaMappings;
	private final Map<String,QName> m_javaToXmlMappings;
	private final boolean m_hasAttachment;

	public ServiceOperationParamDescImpl(List<Class> rootJavaTypes,
		List<DataElementSchema> rootElements,
		Map<QName,Class> xmlToJavaMappings, boolean hasAttachments) throws ServiceCreationException
	{
		if (rootJavaTypes == null || rootElements == null) {
			throw new NullPointerException();
		}

		m_rootJavaTypes = Collections.unmodifiableList(rootJavaTypes);
		m_rootElements = Collections.unmodifiableList(rootElements);

		if (xmlToJavaMappings == null) {
			xmlToJavaMappings = new HashMap<QName,Class>();
		}
		m_xmlToJavaMappings = Collections.unmodifiableMap(xmlToJavaMappings);

		m_javaToXmlMappings = Collections.unmodifiableMap(
			buildJavaToXmlMappings(xmlToJavaMappings));
		m_hasAttachment = hasAttachments;
	}

	public List<Class> getRootJavaTypes() {
		return m_rootJavaTypes;
	}

	public List<DataElementSchema> getRootElements() {
		return m_rootElements;
	}

	public Class getJavaTypeForXmlName(QName xmlName) {
		return m_xmlToJavaMappings.get(xmlName);
	}
	
	public Map<String,QName>getJavaToXmlMappings() {
		return m_javaToXmlMappings;
	}

	public QName getXmlNameForJavaType(Class javaType) {
		return getXmlNameForJavaType(javaType.getName());
	}

	public QName getXmlNameForJavaType(String javaType) {
		return m_javaToXmlMappings.get(javaType);
	}

	public Map<QName,Class> getXmlToJavaMappings() {
		return m_xmlToJavaMappings;
	}

	public boolean hasAttachment() {
		return m_hasAttachment;
	}

	private static Map<String,QName> buildJavaToXmlMappings(Map<QName,Class> xmlToJavaMappings) throws ServiceCreationException
	{
		if (xmlToJavaMappings == null) {
			return new HashMap<String,QName>();
		}

		Map<String,QName> result = new HashMap<String,QName>();
		for (Iterator<Map.Entry<QName,Class>> it=xmlToJavaMappings.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<QName,Class> e = it.next();
			QName xmlName = e.getKey();
			String className = e.getValue().getName();
			if (result.containsKey(className)) {
				throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_CLASS_TO_MULTIPLE_NS_DEFINITION,
						ErrorConstants.ERRORDOMAIN, new Object[] {result.get(className), xmlName, className}));
			}
			result.put(className, xmlName);
		}

		return result;
	}
}
