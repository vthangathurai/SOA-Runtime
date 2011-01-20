/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class NodeListAdaptor implements NodeList {
	ArrayList<Node> m_nodeList;
	public NodeListAdaptor() {
		m_nodeList = new ArrayList<Node>();
	}

	public void add(Node node) {
		m_nodeList.add(node);
	}

	public int getLength() {
		return m_nodeList.size();
	}

	public Node item(int i) {
		return m_nodeList.get(i);
	}
}
