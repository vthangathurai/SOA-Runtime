/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.types.SOACommonConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;


/**
 * @author ichernyshev
 */
public final class DataElementSchemaLoader {

	private static DataElementSchemaLoader s_instance = new DataElementSchemaLoader();
	private static DataElementSchemaImpl s_errorMessageSchema;

	public static DataElementSchemaLoader getInstance() {
		return s_instance;
	}

	private DataElementSchemaLoader() {
		// singleton
	}

	public DataElementSchema loadSchema(ServiceId svcId, QName rootElementName, QName rootElementType,
		BaseTypeDefsBuilder typeDefsBuilder, ClassLoader cl)
		throws ServiceException
	{
		if (rootElementType.equals(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME)) {
			return getErrorMessageSchema(rootElementName);
		}

		if (typeDefsBuilder == null) {
			// no schema info at all
			return new DataElementSchemaImpl(rootElementName, 1);
		}

		// try to match type name to the complex type name, then to root element name
		// this is a bit of a hack, but we only have one name in type mappings...

		List<DataElementSchemaHolder> resolvedComplexTypes = new ArrayList<DataElementSchemaHolder>();

		Collection<FlatSchemaComplexTypeImpl> complexTypes = typeDefsBuilder.getComplexTypes();
		FlatSchemaComplexTypeImpl complexType = findComplexType(complexTypes, rootElementType);
		if (complexType != null) {
			return loadSchemaForComplexType(complexType, rootElementName, 1, resolvedComplexTypes);
		}

		Map<QName,FlatSchemaElementDeclImpl> rootElements = typeDefsBuilder.getRootElements();
		FlatSchemaElementDeclImpl rootElement = rootElements.get(rootElementName);
		if (rootElement != null) {
			return loadSchemaForElement(rootElement, rootElementName, resolvedComplexTypes);
		}
		return new DataElementSchemaImpl(rootElementName, 1);
	}

	public static DataElementSchema getErrorMessageSchema() {
		return getErrorMessageSchema(SOAConstants.ERROR_MESSAGE_ELEMENT_NAME);
	}

	public static DataElementSchema getErrorMessageSchema(QName rootElementName) {
		if (s_errorMessageSchema == null) {
			s_errorMessageSchema = buildErrorMessageSchema();
		}

		return s_errorMessageSchema.copyElement(rootElementName, 1);
	}

	private static DataElementSchemaImpl buildErrorMessageSchema() {
		List<DataElementSchema> errorElementChildren = new ArrayList<DataElementSchema>();
		addChildElement("errorId", 1, errorElementChildren);
		addChildElement("domain", 1, errorElementChildren);
		addChildElement("severity", 1, errorElementChildren);
		addChildElement("category", 1, errorElementChildren);
		addChildElement("message", 1, errorElementChildren);
		addChildElement("exceptionId", 1, errorElementChildren);
		addChildElement("subdomain", 1, errorElementChildren);
		addChildElement("parameter", -1, errorElementChildren);

		DataElementSchemaImpl errorElement = new DataElementSchemaImpl(
			new QName(SOACommonConstants.SOA_TYPES_NAMESPACE, "error"), -1);
		errorElement.setChildren(errorElementChildren);

		List<DataElementSchema> errorMessageChildren = new ArrayList<DataElementSchema>();
		errorMessageChildren.add(errorElement);

		DataElementSchemaImpl errorMessageType = new DataElementSchemaImpl(
			SOAConstants.ERROR_MESSAGE_ELEMENT_NAME, 1);
		errorMessageType.setChildren(errorMessageChildren);

		return errorMessageType;
	}

	private static DataElementSchema addChildElement(String name, int maxOccurs,
		List<DataElementSchema> list)
	{
		return addChildElement(new QName(SOACommonConstants.SOA_TYPES_NAMESPACE, name), maxOccurs, list);
	}

	private static DataElementSchema addChildElement(QName name, int maxOccurs,
		List<DataElementSchema> list)
	{
		DataElementSchemaImpl result = new DataElementSchemaImpl(name, maxOccurs);
		list.add(result);
		return result;
	}

	private FlatSchemaComplexTypeImpl findComplexType(
		Collection<FlatSchemaComplexTypeImpl> complexTypes, QName typeName)
	{
		for (FlatSchemaComplexTypeImpl complexType: complexTypes) {
			QName typeName2 = complexType.getTypeName();
			if (typeName2 == null) {
				// skip anonymous/embedded types
				continue;
			}

			if (typeName2.equals(typeName)) {
				return complexType;
			}
		}

		return null;
	}

	private DataElementSchemaHolder findElementHolder(
		List<DataElementSchemaHolder> holders, FlatSchemaComplexTypeImpl complexType, QName elementName)
	{
		for (DataElementSchemaHolder holder: holders) {
			if (holder.m_complexType == complexType && holder.m_element.getElementName().equals(elementName) ) {
				return holder;
			}
		}

		return null;
	}

	private DataElementSchema loadSchemaForComplexType(FlatSchemaComplexTypeImpl complexType,
		QName elementName, int maxOccurs, List<DataElementSchemaHolder> resolvedComplexTypes)
	{
		// first try to prevent infinite recursion
		DataElementSchemaHolder holder = findElementHolder(resolvedComplexTypes, complexType, elementName);
		if (holder != null) {
			// make a copy of the element with a different name, but preserving the same children
			return holder.m_element; //holder.m_element.copyElement(elementName, maxOccurs);
		}

		DataElementSchemaImpl result = new DataElementSchemaImpl(elementName, maxOccurs);
		resolvedComplexTypes.add(new DataElementSchemaHolder(result, complexType));
		List<DataElementSchema> children = loadChildren(complexType, resolvedComplexTypes);
		result.setChildren(children);
		return result;
	}

	private DataElementSchema loadSchemaForElement(FlatSchemaElementDeclImpl element,
		QName elementName, List<DataElementSchemaHolder> resolvedComplexTypes)
	{
		FlatSchemaComplexTypeImpl complexType = element.getComplexType();
		if (complexType == null) {
			return new DataElementSchemaImpl(elementName, element.getMaxOccurs());
		}

		return loadSchemaForComplexType(complexType, elementName,
			element.getMaxOccurs(), resolvedComplexTypes);
	}

	private List<DataElementSchema> loadChildren(FlatSchemaComplexTypeImpl complexType,
		List<DataElementSchemaHolder> resolvedComplexTypes)
	{
		List<FlatSchemaElementDeclImpl> elements = complexType.getElements();
		if (elements == null || elements.isEmpty()) {
			return null;
		}

		List<DataElementSchema> result = new ArrayList<DataElementSchema>(elements.size());
		for (FlatSchemaElementDeclImpl element: elements) {
			DataElementSchema child = loadSchemaForElement(element, element.getName(), resolvedComplexTypes);
			result.add(child);
		}

		return result;
	}

	private static class DataElementSchemaHolder {
		final DataElementSchemaImpl m_element;
		final FlatSchemaComplexTypeImpl m_complexType;

		DataElementSchemaHolder(DataElementSchemaImpl element, FlatSchemaComplexTypeImpl complexType) {
			if (element == null || complexType == null) {
				throw new NullPointerException();
			}

			m_element = element;
			m_complexType = complexType;
		}
	}
}
