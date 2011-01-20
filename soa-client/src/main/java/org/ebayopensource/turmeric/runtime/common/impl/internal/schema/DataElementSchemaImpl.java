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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;


/**
 * @author ichernyshev
 */
public final class DataElementSchemaImpl implements DataElementSchema {

	private final QName m_elementName;
	private final int m_maxOccurs;
	private Map<QName, DataElementSchema> m_children;
	private Map<String, DataElementSchema[]> m_childrenByLocalName = new HashMap<String, DataElementSchema[]>();

	public DataElementSchemaImpl(QName elementName, int maxOccurs) {
		m_elementName = elementName;
		m_maxOccurs = maxOccurs;
	}

	public DataElementSchemaImpl(QName elementName, int maxOccurs, List<DataElementSchema> children)
	{
		this(elementName, maxOccurs);
		setChildren(children);
	}

	public void setChildren(List<DataElementSchema> children) {
		if (children == null || children.isEmpty()) {
			m_children = null;
			return;
		}

		int size = children.size();
		// Creates m_children and hashmap of local name to DataElementSchema
		// ArrayList.
		HashMap<String, ArrayList<DataElementSchema>> childrenByLocalName = new HashMap<String, ArrayList<DataElementSchema>>(
				size);
		m_children = new HashMap<QName, DataElementSchema>(size);
		for (DataElementSchema child : children) {
			QName elementName = child.getElementName();
			m_children.put(elementName, child);

			String localName = elementName.getLocalPart();
			ArrayList<DataElementSchema> elementSchemas = childrenByLocalName
					.get(localName);
			if (null == elementSchemas) {
				elementSchemas = new ArrayList<DataElementSchema>(1);
				childrenByLocalName.put(localName, elementSchemas);
			}
			elementSchemas.add(child);
		}

		// Creates haspmap of local name to DataElementSchema element array.
		m_childrenByLocalName = new HashMap<String, DataElementSchema[]>(
				childrenByLocalName.size());
		for (String localName : childrenByLocalName.keySet()) {
			ArrayList<DataElementSchema> elementSchemaList = childrenByLocalName
					.get(localName);
			int listSize = elementSchemaList.size();
			DataElementSchema[] elementSchemsArray = new DataElementSchema[listSize];
			for (int i = 0; i < listSize; i++) {
				elementSchemsArray[i] = elementSchemaList.get(i);
			}
			m_childrenByLocalName.put(localName, elementSchemsArray);
		}

		m_children = Collections.unmodifiableMap(m_children);
		m_childrenByLocalName = Collections
				.unmodifiableMap(m_childrenByLocalName);
	}

	DataElementSchemaImpl copyElement(QName elementName, int maxOccurs) {
		// make a new children map
		List<DataElementSchema> children = null;
		if (m_children != null) {
			children = new ArrayList<DataElementSchema>();
			for (DataElementSchema child : m_children.values()) {
				children.add(child);
			}
		}

		// create root element with a different name, but the same children
		DataElementSchemaImpl result = new DataElementSchemaImpl(elementName,
				maxOccurs);
		result.setChildren(children);
		return result;
	}

	public QName getElementName() {
		return m_elementName;
	}

	public int getMaxOccurs() {
		return m_maxOccurs;
	}

	public boolean hasChildren() {
		return m_children != null;
	}

	public Collection<QName> getChildrenNames() {
		if (m_children == null) {
			return CollectionUtils.EMPTY_QNAME_SET;
		}

		return m_children.keySet();
	}

	public DataElementSchema getChild(QName name) {
		if (m_children == null) {
			return null;
		}

		DataElementSchema childSchema = m_children.get(name);
		if (null != childSchema) {
			return childSchema;
		}

		return getChild(name.getNamespaceURI(), name.getLocalPart());
	}

	public DataElementSchema getChild(String namespaceURI, String localName) {
		DataElementSchema[] elementSchemsArray = m_childrenByLocalName
				.get(localName);
		if (null == elementSchemsArray) {
			return null;
		}

		if (namespaceURI == null || namespaceURI.length() == 0) {
			if (elementSchemsArray.length == 1) {
				return elementSchemsArray[0];
			}
			// If the name comes with a empty nsURI, we will assume that it has
			// the same ns as its parent. So, we search the schema info by
			// local part.
			namespaceURI = m_elementName.getNamespaceURI();
		}
		DataElementSchema elementSchemaFound = null;

		for (DataElementSchema elementSchema : elementSchemsArray) {
			if (elementSchema.getElementName().getNamespaceURI().equals(
					namespaceURI)) {
				elementSchemaFound = elementSchema;
				break;
			}
		}

		// if still not found or namespaces didn't match use the local part.
		// Fix added for backward compatability with Codegen bug related to JSON
		// and useSchemaInfo=true
		// to avoid creation of arrays by default in a JSON type response.
		if (elementSchemaFound == null) {
			if (elementSchemsArray.length == 1) {
				return elementSchemsArray[0];
			}
		}

		return elementSchemaFound;
	}
}
