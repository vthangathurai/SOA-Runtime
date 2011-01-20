/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.binding.impl.parser.json;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.ChildNodeStreamingIterator;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.ParseException;
import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.StreamableObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.ObjectNodeImpl;


/**
 * @author wdeng
 * 
 */
public class JSONStreamObjectNodeImpl extends ObjectNodeImpl implements
		StreamableObjectNode {

	private JSONStreamReadContext m_context;
	private NodeBuildingStage m_stage;
	private QName m_nameOfPreviousElement = null;
	private QName m_nameOfCurrentElement = null;
	private int m_indexOfCurrentElement = -1;

	/**
	 * This is used to built the root node.
	 * 
	 * @param m_name
	 * @param lexer
	 */
	public JSONStreamObjectNodeImpl(JSONStreamReadContext ctx) {
		this(ROOT_NODE_QNAME, null, ctx);
	}

	private JSONStreamObjectNodeImpl(QName name, ObjectNode parent,
			JSONStreamReadContext ctx) {
		super(name, parent);
		m_stage = NodeBuildingStage.NameFilled;
		m_context = ctx;
		ctx.newNodeCreated(this);
	}

	@Override
	public List<ObjectNode> getChildNodes() throws XMLStreamException {
		if (m_stage != NodeBuildingStage.ValueBuilt) {
			buildValue();
		}
		return super.getChildNodes();
	}

	@Override
	public List<ObjectNode> getChildNodes(QName name) throws XMLStreamException {
		List<ObjectNode> children = super.getChildNodes(name);
		if (children != null) {
			return children;
		}
		if (m_stage != NodeBuildingStage.ValueBuilt) {
			buildValue(name, false);
		}
		return super.getChildNodes(name);
	}
	
	@Override
	public ObjectNode getChildNode(int index) throws XMLStreamException {
		while (m_children.size() <= index && m_stage != NodeBuildingStage.ValueBuilt) {
			m_context.buildValue(this, null, Integer.MAX_VALUE, true);
		}
		return super.getChildNode(index);
	}

	@Override
	public ObjectNode getChildNode(QName name, int index) throws XMLStreamException {
		ObjectNode child = super.getChildNode(name, index);
		if (null != child) {
			return child;
		}
		if (m_stage != NodeBuildingStage.ValueBuilt) {
			m_context.buildValue(this, name, index, false);
		}
		return super.getChildNode(name, index);
	}

	@Override
	public boolean hasChildNodes() throws XMLStreamException {
		if (m_stage == NodeBuildingStage.NameFilled) {
			m_context.buildNextChild(this);
		}
		return super.hasChildNodes();
	}

	@Override
	public boolean hasAttributes() {
		if (m_stage != NodeBuildingStage.ValueBuilt) {
			m_context.buildNextChild(this);
		}
		return super.hasAttributes();
	}

	@Override
	public String getNodeValue() {
		if (m_stage != NodeBuildingStage.ValueBuilt) {
			m_context.buildNextChild(this);
		}
		return super.getNodeValue();
	}

	public StreamableObjectNode nextChild() throws XMLStreamException {
		if (m_stage != NodeBuildingStage.ValueBuilt) {
			m_context.buildNextChild(this);
		}
		if (-1 == m_indexOfCurrentElement || m_indexOfCurrentElement >= m_children.size()) {
			return null;
		}
		return (StreamableObjectNode) getChildNode(m_indexOfCurrentElement);
	}

	@Override
	public Iterator<ObjectNode> getChildrenIterator() throws XMLStreamException {
		if (m_stage == NodeBuildingStage.NameFilled) {
			m_context.buildNextChild(this);
		}
		return new ChildNodeStreamingIterator(this);
	}

	@Override
	public int getChildNodesSize() throws XMLStreamException {
		if (m_stage != NodeBuildingStage.ValueBuilt) {
			buildValue();
		}
		return super.getChildNodesSize();
	}

	/**
	 * Node is allow to be cloned only after it is built.
	 */
	@Override
	public ObjectNode cloneNode() throws XMLStreamException {
		if (m_stage != NodeBuildingStage.ValueBuilt) {
			buildValue();
		}
		return super.cloneNode();
	}

	private void buildValue() {
		buildValue(null, false);
	}

	/**
	 * build all the children until elements with QName name are found.
	 * 
	 * @param name
	 */
	private void buildValue(QName name, boolean singleChildPolicyApplied) {
		m_context.buildValue(this, name, Integer.MAX_VALUE, singleChildPolicyApplied);
	}

	void buildValue(QName key, int idxTofind, boolean singleChildPolicyApplied) {
		buildValue(key, idxTofind, singleChildPolicyApplied, false);
	}
	
	/**
	 * Build children until the given 'index'th named element
	 * 
	 * @param key
	 * @param idxTofind
	 * @param singleChildPolicyApplied
	 * @param isArrayElement
	 */
	private void buildValue(QName key, int idxTofind, boolean singleChildPolicyApplied, boolean isArrayElement) {
		// Any json node can either have value or have children, not both of
		// them.
		if (buildNonCollectionValue(singleChildPolicyApplied)) {
			return;
		}
		if (m_stage != NodeBuildingStage.NameFilled
				 && !isBuilding(m_stage)) {
			return;
		}
		JSONTokenType type = m_context.getCurrentTokenType();
		
		validateChildrenBoundary(type);

		if ((JSONTokenType.COMMA == type && isBuilding(m_stage))
		 || JSONTokenType.LCURLY == type) {
			m_stage = NodeBuildingStage.Building;
			buildObject(key, idxTofind, singleChildPolicyApplied, isArrayElement);
			return;
		}
		if (JSONTokenType.LBLANKET == type) {
			JSONStreamObjectNodeImpl parent = (JSONStreamObjectNodeImpl)getParentNode();
			parent.m_stage = NodeBuildingStage.BuildingArray;
			buildArray(key, idxTofind, singleChildPolicyApplied);
			return;
		}
		if (JSONTokenType.RBLANKET == type) {
			m_context.getNextToken();
			m_indexOfCurrentElement++;
			if (m_stage == NodeBuildingStage.BuildingArray) {
				m_stage = NodeBuildingStage.Building;
				buildValue(key, idxTofind, singleChildPolicyApplied, isArrayElement);
				return;
			} 
			JSONStreamObjectNodeImpl parent = (JSONStreamObjectNodeImpl)getParentNode();
			if (null != parent) {
				parent.m_stage = NodeBuildingStage.Building;
			} 
			return;
		}
		if (JSONTokenType.RCURLY == type) {
			m_context.getNextToken();
			nodeBuildingCompleted(singleChildPolicyApplied);
			return;
		}
		throw createException(m_context.getCurrentToken(), "'{', '}', '[',']', or ',' is expected.");
	}
	
	private void validateChildrenBoundary(JSONTokenType type) {
		if (JSONTokenType.COMMA == type) {
			if (isBuilding(m_stage)) {
				m_context.getNextToken();
				return;
			}
			throw createException(m_context.getCurrentToken(),
					"',' is unexpected.");
		}
		if (JSONTokenType.LCURLY == type) {
			if (m_stage == NodeBuildingStage.NameFilled /*|| m_stage == NodeBuildingStage.BuildingArray*/) {
				m_context.getNextToken();
				return;
			}
			throw createException(m_context.getCurrentToken(),
					"'{' is unexpected.");
		}
		if (JSONTokenType.LBLANKET == type) {
			if (m_stage == NodeBuildingStage.NameFilled) {
				m_context.getNextToken();
				return;
			}

			throw createException(m_context.getCurrentToken(),
					"'[' is unexpected.");
		} 
	}

	private void buildObject(QName key, int idxTofind, boolean singleChildPolicyApplied, boolean isArrayElement) {
		boolean isAttribute = false;
		do {
			if (JSONTokenType.COMMA == m_context.getCurrentTokenType()) {
				m_context.getNextToken();
			}
			isAttribute = false;
			JSONStreamObjectNodeImpl child = buildChild(key, idxTofind, singleChildPolicyApplied);
			if (null != child) {
				QName qName = child.getNodeName();
				isAttribute = child.isAttribute();
				if (isAttribute) {
					addAttribute(child);
				} else if (BindingConstants.JSON_VALUE_KEY.equals(qName.getLocalPart())) {
					this.setNodeValue(child.getNodeValue());
				} else {
					int childIdx = addChild(child);
					m_nameOfPreviousElement = m_nameOfCurrentElement;
					m_nameOfCurrentElement = qName;
					m_indexOfCurrentElement = childIdx;
					if (!singleChildPolicyApplied) {
						if (key == null || idxTofind == Integer.MAX_VALUE) {
							child.buildValue(null, Integer.MAX_VALUE, false, false);
						}
					}
				}
			} else { // child == null
				if (JSONTokenType.RBLANKET == m_context.getCurrentTokenType()) {
					//If it is "]", it is a curly braces mismatch.  Advance the token to avoid
					// infinite loop
					m_context.getNextToken();
				}
			}
		} while ((isAttribute || !singleChildPolicyApplied) && !foundChild(key, idxTofind)
				&& JSONTokenType.RCURLY != m_context.getCurrentTokenType());
		if (JSONTokenType.RCURLY == m_context.getCurrentTokenType()) {
			m_context.getNextToken();
			nodeBuildingCompleted(singleChildPolicyApplied);
		}
	}
	
	void nodeBuildingCompleted(boolean singleChildPolicyApplied) {
		if (m_stage == NodeBuildingStage.ValueBuilt) {
			return;
		}
		m_stage = NodeBuildingStage.ValueBuilt;
		m_nameOfPreviousElement = m_nameOfCurrentElement;
		m_nameOfCurrentElement = null;
		m_context.nodeBuildingCompleted(this);
		JSONStreamObjectNodeImpl parent = (JSONStreamObjectNodeImpl)getParentNode();
		if (    singleChildPolicyApplied && null != parent && 
				parent.m_stage == NodeBuildingStage.BuildingArray && 
				m_context.getCurrentTokenType() != JSONTokenType.RBLANKET) {
			createNewArrayElementNodeIfNeeded();
		}
	}

	private void buildArray(QName key, int idxTofind, boolean singleChildPolicyApplied) {
		JSONStreamObjectNodeImpl parentNode = (JSONStreamObjectNodeImpl) getParentNode();
		JSONTokenType type;
		type = m_context.getCurrentTokenType();
		if (JSONTokenType.RBLANKET == type) {
			this.setNodeValue(null);
			parentNode.m_stage = NodeBuildingStage.Building;
			nodeBuildingCompleted(singleChildPolicyApplied);
			return;
		}
		// Handled the first array element first
		buildValue(null, Integer.MAX_VALUE, singleChildPolicyApplied, true);
		if (singleChildPolicyApplied) {
			return;
		}
		type = m_context.getCurrentTokenType();
		while (JSONTokenType.COMMA == type) {
			JSONStreamObjectNodeImpl node = createNewArrayElementNodeIfNeeded();
			if (null == node) {
				break;
			}
			node.buildValue(null, Integer.MAX_VALUE, singleChildPolicyApplied, true);
			type = m_context.getCurrentTokenType();
		}
		if (JSONTokenType.RBLANKET == type) {
			m_context.getNextToken();
			parentNode.m_stage = NodeBuildingStage.Building;
		}
	}
	
	private JSONStreamObjectNodeImpl createNewArrayElementNodeIfNeeded() {
		JSONStreamObjectNodeImpl parentNode = (JSONStreamObjectNodeImpl) getParentNode();
		JSONTokenType type = m_context.getCurrentTokenType();
		if (JSONTokenType.RBLANKET == type) {
			// Skip the right blanket;
			m_context.getNextToken();
			parentNode.m_indexOfCurrentElement++;
			return null;
		} 
		if (JSONTokenType.COMMA != type) {
			throw createException(m_context.getCurrentToken(),
			"Expecting array end");
		}
		QName nodeName = getNodeName();
		JSONStreamObjectNodeImpl node = new JSONStreamObjectNodeImpl(
				nodeName, parentNode, m_context);
		int index = parentNode.addChild(node);
		parentNode.m_nameOfPreviousElement = parentNode.m_nameOfCurrentElement;
		parentNode.m_indexOfCurrentElement = index;
		// Skip the comma; Why need to skip?
		m_context.getNextToken();
		return node;
	}
	
	/**
	 * Returns the local name of the child.
	 * 
	 * @param key
	 * @param idxTofind
	 * @return
	 */
	private JSONStreamObjectNodeImpl buildChild(QName key, int idxTofind, boolean singleChildPolicyApplied) {
		JSONToken token = m_context.getCurrentToken();
		JSONTokenType type = token.m_type;
		if (JSONTokenType.RBLANKET == type || JSONTokenType.RCURLY == type) {
			return null;
		}
		if (JSONTokenType.STRING != type) {
			throw createException(token, "QName expected.");
		}
		QName name = buildName();
		
		// buildName() always returns non-null QName otherwise exception is thrown.
		JSONStreamObjectNodeImpl child = new JSONStreamObjectNodeImpl(name,
				this, m_context);
		type = m_context.getCurrentTokenType();
		if (JSONTokenType.COLON == type) {
			m_context.getNextToken();
		}
		child.buildNonCollectionValue(singleChildPolicyApplied);
		return child;
	}

	private boolean foundChild(QName searchKey, int searchIdx) {
		// searchKey == null, we want to find all children
		if (null == searchKey) {
			return false;
		}
		// This is to find all the children with the searchKey
		if (searchIdx == Integer.MAX_VALUE
				&& searchKey.equals(m_nameOfPreviousElement)
				&& !searchKey.equals(m_nameOfCurrentElement)) {
			return true;
		}
		return hasChildNode(searchKey, searchIdx);
	}

	private QName buildName() {
		JSONToken token = m_context.getCurrentToken();
		String prefix = "";
		String name = null;
		if (token.m_prefixEnd > 0) {
			prefix = token.getPrefix();
			name = token.getName();
		} else {
			name = token.getText();
		}
		if (null == name) {
			throw createException(token, "QName expected.");
		}
		m_context.getNextToken();
		return m_context.createQName(prefix, name, getParentNode() == null);
	}

	/**
	 * builds json value: JSON value can be string, number, object, array, true,
	 * false.
	 * 
	 * @param canTakeArray
	 * @return true, if the current node is a leaf node that has value.
	 */
	private boolean buildNonCollectionValue(boolean singleChildPolicyApplied) {
		if (m_stage == NodeBuildingStage.ValueBuilt) {
			return true;
		}
		if (isBuilding(m_stage)) {
			return false;
		}
		JSONToken token = m_context.getCurrentToken();
		if (null == token) {
			throw createException(JSONToken.END_TOKEN, "Unexpected EOF during the construction of value for " + m_nameOfCurrentElement);
		}
		JSONTokenType type = token.m_type;
		boolean shouldGetNextToken = true;
		if (JSONTokenType.STRING == type) {
			this.setNodeValue(token.getText());
		} else if (JSONTokenType.NUMBER == type) {
			this.setNodeValue(token.getText());
		} else if (JSONTokenType.TRUE == type) {
			this.setNodeValue("true");
		} else if (JSONTokenType.FALSE == type) {
			this.setNodeValue("false");
		} else if (JSONTokenType.NULL == type) {
			this.setIsNull(true);
			addNilAttribute();
		} else if (JSONTokenType.RBLANKET == type || JSONTokenType.RCURLY == type || JSONTokenType.COMMA == type) {
			this.setNodeValue("");
			shouldGetNextToken = false;
		} else {
			return false;
		}
		if (shouldGetNextToken) {
			m_context.getNextToken();
		}
		nodeBuildingCompleted(singleChildPolicyApplied);
		return true;
	}

	private ParseException createException(JSONToken token, String msg) {
		return new ParseException(token.getText(), token.m_line, token
				.m_column, msg);
	}

	private boolean isBuilding(NodeBuildingStage stage) {
		return 	m_stage == NodeBuildingStage.Building
		 || m_stage == NodeBuildingStage.BuildingArray;
	}
	
	private void addNilAttribute() {
		ObjectNodeImpl attr = new ObjectNodeImpl(BindingConstants.NILLABLE_ATTRIBUTE_QNAME, this);
		attr.setNodeValue(Boolean.TRUE.toString());
		addAttribute(attr);
	}
	
	private static enum NodeBuildingStage {
		NameFilled, 		// The name of the node has been filled
		Building, 	// The node is in the middle of building the object structure (Name-Value)
		BuildingArray, 	// The node is in the middle of building as an element in an json array
		ValueBuilt 			// The node is completely built
	}
}
