/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;
/**
 *@author aupadhay
 *This class is to store the additional info about the cloned nodes.
 *validation can be put later.
 */
import org.w3c.dom.Node;

public  class ClonedSchemaNodeInfo
{
	private Node m_Node;
	private String m_AssociatedNamespace;
	private String m_TypeLibName;

	public ClonedSchemaNodeInfo(Node node,String namespace,String libraryName)
	{
		this.m_Node = node;
		this.m_AssociatedNamespace = namespace;
		this.m_TypeLibName=libraryName;

	}

	public Node getNode() {
		return m_Node;
	}

	public String getAssociatedNamespace() {
		return m_AssociatedNamespace;
	}

	public String getTypeLibName() {
		return m_TypeLibName;
	}
	
	
}
