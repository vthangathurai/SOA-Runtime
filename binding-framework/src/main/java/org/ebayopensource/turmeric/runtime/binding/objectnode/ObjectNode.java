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
package org.ebayopensource.turmeric.runtime.binding.objectnode;


import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

/**
 * This class is very similar to org.w3c.doc.Node,  except that
 * it is vastly simplified and the elements of this don't necessarily 
 * have to be text/strings.
 * This is a generic object tree which can be used to represent any arbitrary
 * raw message structure. Most of the methods are derived/copied 
 * from the Node interface.
 * 
 * @author smalladi
 * @author wdeng
 *
 */
public interface ObjectNode {
	
	/**
	 * Get the name of the node.
	 * @return the node name
	 */
	public QName getNodeName();
	
	/**
	 * Get the node type (XML or Java).
	 * @return the node type
	 */
	public ObjectNodeType getNodeType();
	
	/**
	 * If the NodeType is XML, getUnderlyingRawNode return the corresponding 
	 * org.w3c.dom.Node object. Otherwise, this method throws IlleagalAccessError. 
	 * @return an Object representing the raw node
	 */
	public Object getUnderlyingRawNode();
	
	/**
	 * Return the node value information.
	 * This method can be called only if its a LEAF node - i.e, no children.
	 * @return an Object with the value
	 */
	public Object getNodeValue();
	
	/**
	 * Set the node value information.
	 * Similar to getNodeValue, setNodeValue can only be called on a LEAF node.
	 * If its called on a non leaf node, unsupportedoperation exception is 
	 * is thrown.
	 * @param value the new node value
	 */
	public void setNodeValue(Object value);
	
	/**
	 * Returns the parent node, if there is one. Otherwise, returns null.
	 * @return the parent node, or null if none
	 */
	public ObjectNode getParentNode();
	
	/**
	 * This method checks if there are child nodes to this object.
	 * @return true if there are child nodes
	 * @throws XMLStreamException 
	 */
	public boolean hasChildNodes() throws XMLStreamException;
	
	/**
	 * Returns true if the node is null.  Null is an encoded value in some representations e.g. JSON null,
	 * xsi:nil=true.
	 * @return true if the node is null.
	 */
	public boolean getIsNull();
	
	/**
	 * Returns the child nodes. If there are no child nodes, it returns null.
	 * @return List of child nodes, or null if none
	 * @throws XMLStreamException 
	 */
	public List<ObjectNode> getChildNodes() throws XMLStreamException;
	
	/**
	 * Returns an iterator over the collection of child nodes.  
	 * @return the child node iterator
	 * @throws XMLStreamException Exception when fails to read from stream.
	 */
	public Iterator<ObjectNode> getChildrenIterator() throws XMLStreamException;

	/**
	 * Returns the number of child nodes.
	 * @return number of child nodes
	 * @throws XMLStreamException Exception when fails to read from stream.
	 */
	public int getChildNodesSize() throws XMLStreamException;
	
	/**
	 * Inserts a new child node before child position <code>index</code>.
	 * @param node the child node
	 * @param index the zero-offset position at which to insert the child
	 * @throws IndexOutOfBoundsException Exception when index out of bound.
	 */
	public void insertChildAt(ObjectNode node, int index) throws IndexOutOfBoundsException;
	
	/**
	 * Replaces the child node occupying child position <code>index</code>.
	 * @param node the child node
	 * @param index the zero-offset position of the child to be replaced
	 * @throws IndexOutOfBoundsException Exception when index out of bound.
	 */
	public void replaceChildAt(ObjectNode node, int index) throws IndexOutOfBoundsException;

	/**
	 * Returns a shallow copy of this node (with the same children reference).
	 * @return a copy of the node
	 * @throws XMLStreamException Exception when fails to read from stream.
	 */
	public ObjectNode cloneNode() throws XMLStreamException;
	
	/**
	 * Returns true if the node has attribute information.
	 * @return true if the node has attributes
	 */
	public boolean hasAttributes();
	
	/**
	 * Returns the number of attributes on the node.
	 * @return the number of attributes
	 */
	public int getAttributeCount();
	
	/**
	 * Returns true if this node is an attribute.
	 * @return true if this node is an attribute.
	 */
	public boolean isAttribute();
	
	/**
	 * Returns a list with all the attributes of this node.
	 * @return all the attributes of this node.
	 */
	public List<ObjectNode> getAttributes();
	
	/**
	 * Returns the attribute at position <code>n</code> on the node.
	 * @param n the zero-offset position
	 * @return the attribute
	 */
	public ObjectNode getAttribute(int n);
}
