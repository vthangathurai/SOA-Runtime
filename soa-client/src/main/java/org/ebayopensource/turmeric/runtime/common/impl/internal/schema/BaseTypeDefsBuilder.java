/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Base class for codegen Builders that allow SOA to learn about XML schema
 * 
 * SOA Serializers operate on XML Stream Writers, which means that we need
 * a representation of the schema, not representation of the code model
 * reflecting schema (e.g. JAXB Model class). In particular, this means
 * that we cannot use JAXB at runtime to traverse the object graph and
 * learn about schema for two reasons:
 * 1. There may be no JAXB JAR if app is not using JAXB-based serialization
 * 2. The serializer needs schema and not object structure
 * 
 * @author ichernyshev
 */
public abstract class BaseTypeDefsBuilder {

	protected Collection<FlatSchemaComplexTypeImpl> m_complexTypes;
	protected Map<QName,FlatSchemaElementDeclImpl> m_rootElements;

	public abstract void build();

	public final Collection<FlatSchemaComplexTypeImpl> getComplexTypes() {
		return Collections.unmodifiableCollection(m_complexTypes);
	}

	public final Map<QName,FlatSchemaElementDeclImpl> getRootElements() {
		return Collections.unmodifiableMap(m_rootElements);
	}
}
