/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

/**
 * @author wdeng
 */
public class XmlSchemaBean implements XmlSchema {  
	private final String location;
	private final String namespace;
	private final XmlNs[] xmlns;
	private final XmlNsForm elementFormDefault;
	private final XmlNsForm attributeFormDefault;

	public XmlSchemaBean(String location, String namespace, XmlNs[] xmlns,
			XmlNsForm elementFormDefault, XmlNsForm attributeFormDefault) {
		this.location = location;
		this.namespace = namespace;
		this.xmlns = xmlns;
		this.elementFormDefault = elementFormDefault;
		this.attributeFormDefault = attributeFormDefault;
	}

	public Class<XmlSchema> annotationType() {
		return XmlSchema.class;
	}

	public String location() {
		return location;
	}

	public String namespace() {
		return namespace;
	}

	public XmlNs[] xmlns() {
		return xmlns;
	}

	public XmlNsForm elementFormDefault() {
		return elementFormDefault;
	}

	public XmlNsForm attributeFormDefault() {
		return attributeFormDefault;
	}

	@Override
	public boolean equals(Object that) {
		if (that == this) {
			return true;
		}
		if (!(that instanceof XmlSchema)) {
			return false;
		}
		if (!location.equals(((XmlSchemaBean) that).location)) {
			return false;
		}
		if (!namespace.equals(((XmlSchemaBean) that).namespace)) {
			return false;
		}
		if (!(xmlns == ((XmlSchemaBean) that).xmlns)) {
			return false;
		}
		if (!elementFormDefault
				.equals(((XmlSchemaBean) that).elementFormDefault)) {
			return false;
		}
		if (!attributeFormDefault
				.equals(((XmlSchemaBean) that).attributeFormDefault)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int r = 0;
		r = (r ^ location.hashCode());
		r = (r ^ namespace.hashCode());
		return r;
	}
}
