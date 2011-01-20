/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;


/**
 * @author wdeng
 */
public class XmlSchemaAnnotationBuilder {
	
	/**
	 * Refers to the default Form element
	 */
	public static final String KEY_ELEMENT_FORM_DEFAULT = "elementFormDefault";
	/**
	 * Refers to the default Form attribute
	 */
	public static final String KEY_ATTRIBUTE_FORM_DEFAULT = "attributeFormDefault";

	private Map<String, XmlSchemaBean> m_pkgNameToXmlSchemaAnnoMap = new HashMap<String, XmlSchemaBean>();
	private ISerializationContext m_context;
	private XmlNsForm m_elementFormDefault = XmlNsForm.QUALIFIED;
	private XmlNsForm m_attrFormDefault = XmlNsForm.UNQUALIFIED;

	XmlSchemaAnnotationBuilder(ISerializationContext ctxt,
			Map<String, String> options) {
		m_context = ctxt;
		setupOptions(options);
	}

	private void setupOptions(Map<String, String> options) {
		if (null == options) {
			return;
		}
		String elementFormDefault = options.get(KEY_ELEMENT_FORM_DEFAULT);
		if (null != elementFormDefault ) {
			try {
				m_elementFormDefault = XmlNsForm.valueOf(elementFormDefault);
			} catch (IllegalArgumentException iae) {
				m_elementFormDefault = XmlNsForm.QUALIFIED;
			}
		}

		String attrFormDefault = options.get(KEY_ATTRIBUTE_FORM_DEFAULT);
		if (null != attrFormDefault ) {
			try {
				m_attrFormDefault = XmlNsForm.valueOf(attrFormDefault);
			} catch (IllegalArgumentException iae) {
				m_attrFormDefault = XmlNsForm.UNQUALIFIED;
			}
		}
	}

	XmlSchemaBean getXmlSchemaAnnotation(Class clazz, String location, XmlNs[] xmlNs, XmlNsForm efd, XmlNsForm afd) {
		String pkgName = clazz.getPackage().getName();
		XmlSchemaBean annotation = m_pkgNameToXmlSchemaAnnoMap.get(pkgName);
		if (null == annotation) {
			String nsURI = m_context.getNsForJavaType(clazz);
			if (nsURI == null) {
				nsURI = "";
			}
			annotation = new XmlSchemaBean(location, nsURI, xmlNs, efd, afd);
			m_pkgNameToXmlSchemaAnnoMap.put(pkgName, annotation);
		}
		return annotation;
	}
	
	XmlSchema getXmlSchemaReplacement(Class clazz, XmlSchema xsAnno) {
		if (xsAnno == null) {
			return getXmlSchemaAnnotation(clazz, null, null, m_elementFormDefault, m_attrFormDefault);
		}
		String nsURI = m_context.getNsForJavaType(clazz);
		if (null == nsURI) {
			return xsAnno;
		}
		String pkgName = clazz.getPackage().getName();
		XmlNsForm elementFormDefault = xsAnno.elementFormDefault();
		return getXmlSchemaAnnotation(clazz, xsAnno.location(), xsAnno.xmlns(), elementFormDefault, xsAnno.attributeFormDefault());
	}
	
	XmlNsForm getElementFormDefault() {
		return m_elementFormDefault;
	}
}
