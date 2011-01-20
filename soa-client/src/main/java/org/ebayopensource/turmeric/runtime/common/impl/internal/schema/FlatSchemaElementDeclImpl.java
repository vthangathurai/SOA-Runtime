/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.schema;

import javax.xml.namespace.QName;

/**
 * @author ichernyshev
 */
public class FlatSchemaElementDeclImpl {

	public final static int UNBOUNDED = -1;

	private final QName m_name;
	private final FlatSchemaComplexTypeImpl m_complexType;
	private final int m_maxOccurs;
	private final boolean m_isAttribute;

	FlatSchemaElementDeclImpl(QName name,
		FlatSchemaComplexTypeImpl complexType, int maxOccurs)
	{
		if (name == null || complexType == null) {
			throw new NullPointerException();
		}

		m_name = name;
		m_complexType = complexType;
		m_maxOccurs = maxOccurs;
		m_isAttribute = false;
	}

	FlatSchemaElementDeclImpl(QName name, int maxOccurs, boolean isAttribute)
	{
		if (name == null) {
			throw new NullPointerException();
		}

		m_name = name;
		m_complexType = null;
		m_maxOccurs = maxOccurs;
		m_isAttribute = isAttribute;
	}

	public QName getName() {
		return m_name;
	}

	public boolean isComplexType() {
		return m_complexType != null;
	}

	public boolean isAttribute() {
		return m_isAttribute;
	}

	public FlatSchemaComplexTypeImpl getComplexType() {
		return m_complexType;
	}

	public int getMaxOccurs() {
		return m_maxOccurs;
	}

	public static FlatSchemaElementDeclImpl createRootSimpleElement(QName name) {
		return new FlatSchemaElementDeclImpl(name, 1, false);
	}

	public static FlatSchemaElementDeclImpl createRootAnyElement(QName name) {
		return new FlatSchemaElementDeclImpl(name, 1, false);
	}

	public static FlatSchemaElementDeclImpl createRootComplexElement(
		QName name, FlatSchemaComplexTypeImpl complexType)
	{
		return new FlatSchemaElementDeclImpl(name, complexType, 1);
	}
}
