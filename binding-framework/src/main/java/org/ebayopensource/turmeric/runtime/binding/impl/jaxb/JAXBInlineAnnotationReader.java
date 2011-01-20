/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;
import org.ebayopensource.turmeric.runtime.binding.ITypeConversionContext;
import org.ebayopensource.turmeric.runtime.binding.exception.ElementFormMismatchException;

import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.bind.v2.model.core.ErrorHandler;

/**
 * @author wdeng
 *
 */
public class JAXBInlineAnnotationReader implements RuntimeAnnotationReader{

	private static final RuntimeInlineAnnotationReader DEFAULT_READER
				= new RuntimeInlineAnnotationReader();
	private XmlJavaTypeAdapter m_adapterAnnotation = null;
	private Collection<String> m_boundTypes;
	private XmlSchemaAnnotationBuilder m_xmlSchemaAnnoBuilder;

	public JAXBInlineAnnotationReader(ISerializationContext ctxt, Map<String, String> options) {
		ITypeConversionContext typeConvCtxt = ctxt.getTypeConversionContext();
		if (null != typeConvCtxt && !typeConvCtxt.isEmpty()) {
			m_adapterAnnotation = new XmlJavaTypeAdapterBean(typeConvCtxt.getTypeConversionAdapterClass(), null);
			m_boundTypes = typeConvCtxt.getBoundTypes();
		} else {
			m_boundTypes = new ArrayList<String>();
		}

		//TODO: we need to find a place to hold this object. it is shared by
		// all JAXB based data binding of the same service, one way is to have
		// a setProperty/getProperty() on ServiceDesc to carry custom property
		// objects.
		m_xmlSchemaAnnoBuilder = new XmlSchemaAnnotationBuilder(ctxt, options);
	}

	public Annotation[] getAllFieldAnnotations(Field field, Locatable srcPos) {
		Annotation[] annotations = DEFAULT_READER.getAllFieldAnnotations(field, srcPos);
		return annotations;
	}

	public Annotation[] getAllMethodAnnotations(Method method, Locatable srcPos) {

		Class propertyClass = getPropertyClass(method);
		Annotation[] annotations = DEFAULT_READER.getAllMethodAnnotations(method, srcPos);
		if (null != propertyClass && m_boundTypes.contains(propertyClass.getName())) {
			return addTypeConverterAdapter(annotations, false);
		}
		return annotations;
	}

	public <A extends Annotation> A getClassAnnotation(Class<A> a, Class clazz, Locatable srcPos) {
		if (a == XmlSeeAlso.class) {
			return null;
		}
		return DEFAULT_READER.getClassAnnotation(a, clazz, srcPos);
	}

	public Class[] getClassArrayValue(Annotation a, String name) {
		return DEFAULT_READER.getClassArrayValue(a, name);
	}

	public Class getClassValue(Annotation a, String name) {
		return DEFAULT_READER.getClassValue(a, name);
	}

	public <A extends Annotation> A getFieldAnnotation(Class<A> annotation, Field field, Locatable srcPos) {
		A a = DEFAULT_READER.getFieldAnnotation(annotation, field, srcPos);
		return a;
	}

	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getMethodAnnotation(Class<A> annotation, Method method, Locatable srcPos) {
		if (null == m_adapterAnnotation) {
			return DEFAULT_READER.getMethodAnnotation(annotation, method, srcPos);
		}
		if (annotation != XmlJavaTypeAdapter.class) {
			return DEFAULT_READER.getMethodAnnotation(annotation, method, srcPos);
		}
		Class propertyClass = getPropertyClass(method);
		if (null == propertyClass || !m_boundTypes.contains(propertyClass.getName())) {
			return DEFAULT_READER.getMethodAnnotation(annotation, method, srcPos);
		}
		return (A)m_adapterAnnotation;
	}

	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getMethodAnnotation(Class<A> annotation, Method getter, Method setter, Locatable srcpos) {
		if (null == m_adapterAnnotation) {
			return DEFAULT_READER.getMethodAnnotation(annotation, getter, setter, srcpos);
		}
		if (annotation != XmlJavaTypeAdapter.class) {
			return DEFAULT_READER.getMethodAnnotation(annotation, getter, setter, srcpos);
		}
		Class propertyClass = getPropertyClass(getter);
		if (null == propertyClass || !m_boundTypes.contains(propertyClass.getName())) {
			return DEFAULT_READER.getMethodAnnotation(annotation, getter, setter, srcpos);
		}
		return (A)m_adapterAnnotation;
	}

	public <A extends Annotation> A getMethodParameterAnnotation(Class<A> annotation, Method method, int paramIndex, Locatable srcPos) {
		return DEFAULT_READER.getMethodParameterAnnotation(annotation, method, paramIndex, srcPos);
	}

	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getPackageAnnotation(Class<A> a, Class clazz, Locatable srcPos) {
		A annoFromDefault = DEFAULT_READER.getPackageAnnotation(a, clazz, srcPos);
		if (!(a == XmlSchema.class)) {
			return annoFromDefault;
		}
		XmlSchema xsAnno = (XmlSchema)annoFromDefault;
		XmlSchema anno = m_xmlSchemaAnnoBuilder.getXmlSchemaReplacement(clazz, xsAnno);
		return (A)anno;
	}

	void checkElementFormDefault(Object annoFromDefault) { 
		XmlNsForm efd = m_xmlSchemaAnnoBuilder.getElementFormDefault();
		if (annoFromDefault instanceof XmlSchema) {
			XmlNsForm nsForm = ((XmlSchema)annoFromDefault).elementFormDefault();
			 
			if (!nsForm.equals(XmlNsForm.UNSET) && !efd.equals(nsForm)) {
				throw new ElementFormMismatchException();
			}
		}
	}
	
	public boolean hasClassAnnotation(Class clazz, Class<? extends Annotation> annotationType) {
		return DEFAULT_READER.hasClassAnnotation(clazz, annotationType);
	}

	public boolean hasFieldAnnotation(Class<? extends Annotation> annotationType, Field field) {
		return DEFAULT_READER.hasFieldAnnotation(annotationType, field);
	}

	@Override
	public int hashCode() {
		return m_xmlSchemaAnnoBuilder.hashCode() ^ m_boundTypes.hashCode();
	}

	public boolean hasMethodAnnotation(Class<? extends Annotation> annotation, Method method) {
		if (annotation != XmlJavaTypeAdapter.class) {
			return DEFAULT_READER.hasMethodAnnotation(annotation, method);
		}
		Class propertyClass = getPropertyClass(method);
		if (null == propertyClass || !m_boundTypes.contains(propertyClass.getName())) {
			return DEFAULT_READER.hasMethodAnnotation(annotation, method);
		}
		return true;
	}

	public boolean hasMethodAnnotation(Class<? extends Annotation> annotation, String propertyName, Method getter, Method setter, Locatable srcPos) {
		if (annotation != XmlJavaTypeAdapter.class) {
			return DEFAULT_READER.hasMethodAnnotation(annotation, propertyName, getter, setter, srcPos);
		}
		Class propertyClass = getPropertyClass(getter);
		if (null == propertyClass || !m_boundTypes.contains(propertyClass.getName())) {
			return DEFAULT_READER.hasMethodAnnotation(annotation, propertyName, getter, setter, srcPos);
		}
		return true;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		DEFAULT_READER.setErrorHandler(errorHandler);
	}

	private Class getPropertyClass(Method method) {
		String methodName = method.getName();
		if (methodName.indexOf("get") != 0) {
			return null;
		}
		return method.getReturnType();
	}

	private Annotation[] addTypeConverterAdapter(Annotation[] annotations, boolean isField) {
		int len = 0;
		Annotation[] newAnnotations = new Annotation[1];
		if (annotations != null) {
			len = annotations.length;
			newAnnotations = new Annotation[len + 1];
			System.arraycopy(annotations, 0, newAnnotations, 0, len);
		}
		Annotation xmlAdapter = m_adapterAnnotation;
		newAnnotations[len] = xmlAdapter;
		return newAnnotations;
	}

	@Override
	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		if (!(o instanceof JAXBInlineAnnotationReader)) {
			return false;
		}
		JAXBInlineAnnotationReader other = (JAXBInlineAnnotationReader)o;
		return m_boundTypes.equals(other.m_boundTypes)
			&& m_xmlSchemaAnnoBuilder.equals(other.m_xmlSchemaAnnoBuilder);
	}

}
