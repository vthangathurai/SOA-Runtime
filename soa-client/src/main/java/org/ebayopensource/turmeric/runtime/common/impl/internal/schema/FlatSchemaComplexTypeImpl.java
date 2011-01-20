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
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * @author ichernyshev
 */
public class FlatSchemaComplexTypeImpl {
	private final QName m_typeName;
	private List<FlatSchemaElementDeclImpl> m_elements = new ArrayList<FlatSchemaElementDeclImpl>();

	public FlatSchemaComplexTypeImpl() {
		m_typeName = null;
	}

	public FlatSchemaComplexTypeImpl(QName typeName) {
		if (typeName == null) {
			throw new NullPointerException();
		}

		m_typeName = typeName;
	}

	public QName getTypeName() {
		return m_typeName;
	}

	public List<FlatSchemaElementDeclImpl> getElements() {
		return Collections.unmodifiableList(m_elements);
	}

	public void addElements(List<FlatSchemaElementDeclImpl> elements) {
		m_elements.addAll(elements);
	}

	public FlatSchemaElementDeclImpl addComplexElement(QName name,
		FlatSchemaComplexTypeImpl complexType, int maxOccurs)
	{
		FlatSchemaElementDeclImpl result = new FlatSchemaElementDeclImpl(name, complexType, maxOccurs);
		m_elements.add(result);
		return result;
	}

	public FlatSchemaElementDeclImpl addSimpleElement(QName name, int maxOccurs)
	{
		FlatSchemaElementDeclImpl result = new FlatSchemaElementDeclImpl(name, maxOccurs, false);
		m_elements.add(result);
		return result;
	}

	public FlatSchemaElementDeclImpl addAnyElement(QName name, int maxOccurs)
	{
		FlatSchemaElementDeclImpl result = new FlatSchemaElementDeclImpl(name, maxOccurs, false);
		m_elements.add(result);
		return result;
	}

	public FlatSchemaElementDeclImpl addAttribute(QName name)
	{
		FlatSchemaElementDeclImpl result = new FlatSchemaElementDeclImpl(name, 1, true);
		m_elements.add(result);
		return result;
	}
}
