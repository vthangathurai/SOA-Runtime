/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external;

import javax.xml.namespace.QName;

public class WSDLMessageType {
	
	private String m_name;
	private String m_schemaTypeName;
	private String m_elementName;
	private QName  m_elementQname;
	
	
	
	public QName getElementQname() {
		return m_elementQname;
	}

	public void setElementQname(QName qname) {
		m_elementQname = qname;
	}

	public String getName() {
		return m_name;
	}
	
	public void setName(String m_name) {
		this.m_name = m_name;
	}
	
	public String getSchemaTypeName() {
		return m_schemaTypeName;
	}
	
	public void setSchemaTypeName(String typeName) {
		m_schemaTypeName = typeName;
	}
	
	public String getElementName() {
		return m_elementName;
	}
	public void setElementName(String elementName) {
		m_elementName = elementName;
	}
	
	
}
