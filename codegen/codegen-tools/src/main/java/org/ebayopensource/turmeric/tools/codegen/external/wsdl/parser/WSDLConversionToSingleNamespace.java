/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser;

/**
 * A single Namespace wsdl conversion to MultiNamespace Wsdl.
 * @author aupadhay
 *
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.ClonedSchemaNodeInfo;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WSDLConversionToSingleNamespace {
	private static final String XML_SCHEMA = "schema";
	private static final String XML_BASE = "base";
	private static final String VALUE_YES = "yes";
	private static final String SOURCE_TAG = "typeLibrarySource";
	private static final String SOURCE_NAMESPACE = "namespace";
	private static final String XML_TARGETNAMESPACE = "targetNamespace";
	private static final String XML_COMPLEXTYPE = "complexType";
	private static final String XML_SIMPLETYPE = "simpleType";
	private static final String TYPELIBRARY_TAG = "library";
	private static final String XMLNS_TAG = "xmlns:";
	private static final String NAMESPACECOLON = ":";
	private static final String TYPE_TAG = "type";
	private static final String NAME_TAG = "name";
	private static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	private static final String IMPORT_TAG = "import";
	private static final String APPINFO_TAG = "appinfo";
	private static final String ANNOTATION_TAG = "annotation";
	private static String XML_DEFINITION = "definitions";
	private static Logger s_logger = LogManager.getInstance(WSDLConversionToSingleNamespace.class);

	private Set<Node> m_AllClonedNodes = new HashSet<Node>();
	private Map<Element, Set<String>> m_ElementToAddedImportsMap = new HashMap<Element, Set<String>>();
	private Set<String> m_prefixAdded = new HashSet<String>();
	private Set<String> m_allNamespaces = new HashSet<String>();
	private List<ClonedSchemaNodeInfo> m_AllClonedNodeswithAdditionalInfo = new ArrayList<ClonedSchemaNodeInfo>();
	private Set<String> m_AllTypes = new HashSet<String>();
	private Map<String, String> m_TypeWithNamespaceMapOrig = new HashMap<String, String>();
	private Map<String, String> m_TypeWithNamespaceMapFinal = new HashMap<String, String>();
	private Map<String, Set<Node>> m_typeToNodeMap = new HashMap<String, Set<Node>>();
	private Map<String, Node> m_prefixToSchemaNodeMap = new HashMap<String, Node>();
	private Map<String, String> m_prefixToNamespaceMap = new HashMap<String, String>();
	private Set<Node> m_SchemaNodesSet = new HashSet<Node>();
	private Document m_Document;
	private Node m_schemaNode;
	private String m_WsdlNamespace;
	private Node mWsdlDefNode;

	public WSDLConversionToSingleNamespace() {

	}

	/**
	 * This method creates a new wsdl with multipleNamespaces in the specified
	 * location
	 * 
	 * @param originalWsdlFileLocation -
	 *            the original wsdlfile location
	 * @param newWsdlLocation
	 *            -new location where converted wsdl needs to be created.
	 * @throws CodeGenFailedException
	 */
	public void convertWSDL(String originalWsdlFileLocation,
			String newWsdlLocation) throws CodeGenFailedException {

		//check if the wsdl passed has only one namespace
		try {
			boolean isValid = WSDLConversionToSingleNsHelper.isValidWsdl(originalWsdlFileLocation);
			if(!isValid)
			{
				s_logger.log(Level.SEVERE,"INVALID INPUT:    WSDL does not have single Namespace");
				throw new CodeGenFailedException();
			}
		}catch (Exception e) {
			throw new CodeGenFailedException(e.getMessage());
		}


		s_logger.log(Level.INFO,
		"BEGIN:WSDL ConversionToMultiPleNamespace......");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;

		try {
			s_logger.log(Level.FINE, "Creating a new Document builder..");
			builder = factory.newDocumentBuilder();
			m_Document = builder.parse(originalWsdlFileLocation);
			s_logger.log(Level.FINE, "Parsed the original wsdl file...");
			NodeList nodelist = m_Document.getElementsByTagName("*");
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);
				if (node.getNodeName().contains(XML_DEFINITION)) {
					m_WsdlNamespace = node.getAttributes().getNamedItem(
							XML_TARGETNAMESPACE).getNodeValue();
					mWsdlDefNode = node;
				}
				if (node.getNodeName().contains(XML_SCHEMA)) {
					m_schemaNode = node;
					s_logger.log(Level.FINE,
					"Traversing  schema Section of  the wsdl");
					// only schema section inside a wsdl needs to be visited.
					break;
				}
			}
			s_logger.log(Level.INFO, "recursivelyVisiting Schema......");
			recurseSchemSection(m_schemaNode);
		
			//Need to add the additionalSchemas 
			s_logger.log(Level.INFO, "addingAdditional Schema......");
			addAdditionalSchemas();
			//prefix to be added to <wsdl:definitions> tag
			addPrefix();
			s_logger.log(Level.INFO, "changing prefixesForAdded Schema......");
			//Some prefix needs to be changed and some needs to be added in the new wsdl.
			checkPrefixForNewWsdl();
			//	 need to add import seperately for the original xsd
			// Currently imports are added for each schema type created.
			//addImports((Element)m_schemaNode, m_WsdlNamespace);
			writeNewwsdl(newWsdlLocation);
			s_logger.log(Level.INFO,
			"END:WSDL ConversionToMultiPleNamespace......");

		} catch (Exception e) {
			s_logger.log(Level.SEVERE, e.getMessage());
			throw new CodeGenFailedException(e.getMessage());
		}
	}



	private void recurseSchemSection(Node node) throws CodeGenFailedException, DOMException {
		for(int i=0;i<node.getChildNodes().getLength();i++)
			recursivelyVisitNode(node);
		
	}

	/**
	 * add the prefix for new namespaces.
	 */
	private void addPrefix() {
		Iterator<String> newNamespacesAdded = m_allNamespaces.iterator();
		while (newNamespacesAdded.hasNext()) {
			String namespace = newNamespacesAdded.next();
			String prefix = namespace.substring(namespace.lastIndexOf("/") + 1)
			.toLowerCase();
			String actualPrefix = prefix;
			int i=1;
			while(m_prefixAdded.contains(prefix))
			{
				
				prefix = actualPrefix.concat(String.valueOf(i));
				i++;
			}
			m_prefixAdded.add(prefix);
			
			m_prefixToNamespaceMap.put(prefix, namespace);
			String attribute = XMLNS_TAG + prefix;
			((Element) mWsdlDefNode).setAttribute(attribute, namespace);
		}

	}
	/**
	 * check if the earlier prefix needs to be changed after schema section is changed.
	 */
	private void checkPrefixForNewWsdl() {

		Iterator<String> allTypes = m_AllTypes.iterator();
		while (allTypes.hasNext()) {
			String typeName = allTypes.next();
			// check if this type has been moved to new namespace
			if(typeName.contains(NAMESPACECOLON))
			{
				typeName = typeName.substring(typeName.lastIndexOf(NAMESPACECOLON)+1);
			}
			if (m_TypeWithNamespaceMapFinal.containsKey(typeName)) {
				// get the proper set of nodea whose prefix needs to be changed
				// now
				Set<Node> tobeChangedNodes = m_typeToNodeMap.get(typeName);

				Iterator<Node> itr = tobeChangedNodes.iterator();

				while (itr.hasNext()) {
					Node node = itr.next();
					String newNamespace = m_TypeWithNamespaceMapFinal
					.get(typeName);
					// prefix used for a particular namespace
					String prefix = getprefixUsedForAddedNamespace(newNamespace);
					String TypeValue = prefix + NAMESPACECOLON + typeName;
					Node schemaNode = getProperSchemaNode(node);
					
					//import statement to be added if there is a prefix change and targetNamespace of schema is diff.
					if(isTargetNamespaceDifferent(schemaNode,newNamespace))
						addImports((Element)schemaNode, newNamespace);

					addProperType(node, TypeValue);
					m_prefixToSchemaNodeMap.put(prefix, schemaNode);
				}
			}
		}
	}

	private String getprefixUsedForAddedNamespace(String newNamespace) {
		
		String associatedPrefix = null;
		Iterator<Entry<String, String>> itr= m_prefixToNamespaceMap.entrySet().iterator();
		while(itr.hasNext())
		{
		Entry<String, String> currentEntry = itr.next();
		if(currentEntry.getValue().equals(newNamespace))
			associatedPrefix = currentEntry.getKey();
		}
		return associatedPrefix;
	}

	private void addProperType(Node node, String TypeValue) {
		if ((!((Element) node).getAttribute(XML_BASE).equals(""))
				&& ((Element) node).getAttribute(TYPE_TAG).equals("")) {
			((Element) node).setAttribute(XML_BASE, TypeValue);
			return;
		}
		if (((Element) node).getAttribute(XML_BASE).equals("")
				&& (!((Element) node).getAttribute(TYPE_TAG).equals(""))) {
			((Element) node).setAttribute(TYPE_TAG, TypeValue);
			return;
		}
		if (((Element) node).getAttribute(XML_BASE).equals("")
				&& ((Element) node).getAttribute(TYPE_TAG).equals("")) {
			((Element) node).setAttribute(XML_BASE, TypeValue);
			((Element) node).setAttribute(TYPE_TAG, TypeValue);
			return;

		}
	}
	/**
	 * this method adds <import> tag inside the root passed.
	 * @param schemaElement- root 
	 * @param Namespace-Namespace attribute
	 */
	private void addImports(Element schemaElement, String Namespace) {
		s_logger.log(Level.FINE, "adding importStatements.....");
		NamedNodeMap attrMap = schemaElement.getAttributes();
		String prefix = null;
		String importElementName = null;
		for (int i = 0; i < attrMap.getLength(); i++) {
			if (attrMap.item(i).getNodeValue().equals(XML_SCHEMA_NAMESPACE)) {
				prefix = attrMap.item(i).getNodeName();

			}
		}
		if(prefix==null)
		{
			String nodeName = schemaElement.getNodeName();
			prefix = nodeName.substring(0, nodeName.indexOf(NAMESPACECOLON));
		}
		//if prefix extracted from above logic is xmlns thn import should not have any prefix.
		if( prefix.equals("xmlns"))
			importElementName = IMPORT_TAG;
		else 
			importElementName = prefix.substring(prefix
					.indexOf(NAMESPACECOLON) + 1)
					+ NAMESPACECOLON + IMPORT_TAG;
		
		//check needs to be done if a particulat <import namespace=""> has already been added for a particular schema.
		boolean isImportWrittenBefore = m_ElementToAddedImportsMap.containsKey(schemaElement);
		if(!isImportWrittenBefore)
		{
			//add import
			writeImportstatement(importElementName,Namespace,schemaElement);
		}
		else
		{
			boolean isImportWrittenForPrefix = m_ElementToAddedImportsMap.get(schemaElement).contains(Namespace);
			if(!isImportWrittenForPrefix)
				writeImportstatement(importElementName,Namespace,schemaElement);
		}
	}

	/**
	 * This methods adds an import statement at the start of a schema passed to it.
	 * @param importElementName - importElement statement
	 * @param namespace - Nmaespace that is to be added inside <import>
	 * @param schemaElement - import will be added to this schemaElement
	 */
	
	private void writeImportstatement(String importElementName,
			String namespace, Element schemaElement) {
		Element importElement = m_Document.createElement(importElementName);
		importElement.setAttribute(SOURCE_NAMESPACE, namespace);
		schemaElement.insertBefore(importElement, schemaElement.getFirstChild());

		if(m_ElementToAddedImportsMap.containsKey(schemaElement))
		{
			m_ElementToAddedImportsMap.get(schemaElement).add(namespace);
		}
		else
		{
			Set<String> newSet = new HashSet<String>();
			newSet.add(namespace);
			m_ElementToAddedImportsMap.put(schemaElement, newSet);
		}

	}

	/**
	 * 
	 * @param node - CurrentNode in the treee
	 * @return - the schema node to which currentNode belongs to.
	 */
	private Node getProperSchemaNode(Node node) {
		if (node == null)
			return null;
		if (node.getNodeName().contains(XML_SCHEMA))
			return node;
		else
			return getProperSchemaNode(node.getParentNode());
	}

	/**
	 * 
	 * @param string-
	 *            the location of the converted wsdl. this method uses a
	 *            transformer and writes the modified wsdl in to destination
	 *            location.
	 * @throws CodeGenFailedException
	 */
	private void writeNewwsdl(String fileLocation)
	throws CodeGenFailedException {
		s_logger.log(Level.FINE, "BEGIN writeNewwsdl()....");
		TransformerFactory transferFact = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transferFact.newTransformer();
			// Bug in java5 transformer, indentation does not work.
			// refer to
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6296446
			transformer.setOutputProperty(OutputKeys.INDENT, VALUE_YES);

		} catch (TransformerConfigurationException e) {
			s_logger.log(Level.SEVERE, e.getMessageAndLocation());
			throw new CodeGenFailedException(e.getMessage());
		}
		DOMSource sourcewsdl = new DOMSource(m_Document);

		try {
			File file = new File(fileLocation);
			try {
				s_logger.log(Level.FINE, "Creating new File...");
				boolean created = file.createNewFile();
				if (created) {
					s_logger.log(Level.FINE, "File created: " + fileLocation);
				}
			} catch (IOException e) {
				s_logger.log(Level.SEVERE, e.getMessage());
				throw new CodeGenFailedException(e.getMessage());
			}
			FileOutputStream output = new FileOutputStream(file);
			StreamResult newWsdl = new StreamResult(output);
			sourcewsdl.setNode(m_Document);
			transformer.transform(sourcewsdl, newWsdl);
		} catch (FileNotFoundException e) {
			s_logger.log(Level.SEVERE, e.getMessage());
			throw new CodeGenFailedException(e.getMessage());
		} catch (TransformerException e) {
			s_logger.log(Level.SEVERE, e.getMessage());
			throw new CodeGenFailedException(e.getMessage());
		}
	}

	/**
	 * 
	 * @param node-
	 *            the node being visited currently. This method recursively
	 *            visits each node inside <xsd:schema> and checks for <source>
	 *            tag
	 * @throws DOMException 
	 * @throws CodeGenFailedException 
	 */
	private void recursivelyVisitNode(Node currentNode) throws CodeGenFailedException, DOMException {

		if (currentNode == null)
			return;
		//for SimpleTypes refering to other sinpleTypes with restriction "base"
		if(currentNode.getAttributes()!=null && 
				currentNode.getAttributes().getNamedItem("base")!= null)
		{
			String originalType = currentNode.getAttributes().getNamedItem(
					"base").getNodeValue();	
			if (originalType.contains(NAMESPACECOLON)) {
				originalType = originalType.substring(originalType
						.indexOf(NAMESPACECOLON) + 1);
			}
			m_AllTypes.add(originalType);
			m_TypeWithNamespaceMapOrig.put(originalType, m_WsdlNamespace);
			// all types and related nodes
			if (m_typeToNodeMap.containsKey(originalType)) {
				m_typeToNodeMap.get(originalType).add(currentNode);
			} else {
				Set<Node> newSet = new HashSet<Node>();
				newSet.add(currentNode);
				m_typeToNodeMap.put(originalType, newSet);
			}
		}
		if (currentNode.getAttributes() != null
				&& currentNode.getAttributes().getNamedItem(TYPE_TAG) != null) {
			//populate details about originalType and the namespace it belongs to.
			//also populate details about type and the node.
			String originalType = currentNode.getAttributes().getNamedItem(
					TYPE_TAG).getNodeValue();
			// in case type already has a prefix which might needs to be changed
			// later.
			if (originalType.contains(NAMESPACECOLON)) {
				originalType = originalType.substring(originalType
						.indexOf(NAMESPACECOLON) + 1);
			}

			// set of all types present in schema
			m_AllTypes.add(originalType);
			m_TypeWithNamespaceMapOrig.put(originalType, m_WsdlNamespace);
			// all types and related nodes
			if (m_typeToNodeMap.containsKey(originalType)) {
				m_typeToNodeMap.get(originalType).add(currentNode);
			} else {
				Set<Node> newSet = new HashSet<Node>();
				newSet.add(currentNode);
				m_typeToNodeMap.put(originalType, newSet);
			}

		}
		if (currentNode.getNodeName().contains(SOURCE_TAG)) {
			if (checkIfvalid(currentNode) && isNamespaceDifferent(currentNode)) { 

				String associatedNamespace = currentNode.getAttributes()
				.getNamedItem(SOURCE_NAMESPACE).getNodeValue();
				m_allNamespaces.add(associatedNamespace);

				String libraryName = currentNode.getAttributes().getNamedItem(
						TYPELIBRARY_TAG).getNodeValue();
				
				Node toBeclonedNode = getproperParentNode(currentNode);
				if(m_SchemaNodesSet.contains(toBeclonedNode))
				{
					// multiple source tag inside a particular complexType/simpleType
					String errorMsg = "Input wsdl is invalid..Multiple <source> inside "+toBeclonedNode.getNodeName();
					String nameOftype = null;
					if(toBeclonedNode.hasAttributes() && toBeclonedNode.getAttributes().getNamedItem(NAME_TAG)!=null)
					{
						nameOftype = toBeclonedNode.getAttributes().getNamedItem(NAME_TAG).getNodeValue();
						errorMsg = errorMsg + " with name = " + nameOftype;
					}
					
					throw new CodeGenFailedException(errorMsg);
				}
				else
				{
				m_SchemaNodesSet.add(toBeclonedNode);
				}
				m_AllClonedNodes.add(toBeclonedNode);
				s_logger.log(Level.FINE, "Node" + toBeclonedNode.getNodeValue()
						+ " cloned...");

				Node clonedNode = toBeclonedNode.cloneNode(true);
				ClonedSchemaNodeInfo clonedNodewithAdditionalInfo = new ClonedSchemaNodeInfo(
						toBeclonedNode, associatedNamespace, libraryName);
				m_AllClonedNodeswithAdditionalInfo
				.add(clonedNodewithAdditionalInfo);

				toBeclonedNode.getParentNode().removeChild(toBeclonedNode);

				m_AllClonedNodes.add(clonedNode);
				s_logger.log(Level.FINE,
						"Adding additional information for the cloned node+"
						+ clonedNode.getNodeValue());

			}
		}
		for (int i = 0; i < currentNode.getChildNodes().getLength(); i++) {
			s_logger.log(Level.FINE, "Calling recursivelyVisitNode()...");
			recursivelyVisitNode(currentNode.getChildNodes().item(i));
		}
	}

	/**
	 * 
	 * @param node
	 *            -the current node of the DOM
	 * @return if the namespace for this node is similar to that of wsdl.
	 */
	private boolean isNamespaceDifferent(Node node) {
		s_logger.log(Level.FINE, "BEGIN isNamespaceDifferent()...");

		if (node.getAttributes().getNamedItem(SOURCE_NAMESPACE).getNodeValue().equals(
				m_WsdlNamespace))
			return false;
		else
			return true;
	}

	/**
	 * 
	 * @param node
	 *            -current node of DOM
	 * @return if the <source> tag found is in correct position.
	 * @throws CodeGenFailedException 
	 */
	private boolean checkIfvalid(Node node) throws CodeGenFailedException {
		// TODO and condition
		
		checkIfSourceTagHasProperAttributes(node);
		if (node.getParentNode().getNodeName().contains(APPINFO_TAG)
				&& node.getParentNode().getParentNode().getNodeName().contains(
						ANNOTATION_TAG))
			return true;
		else
			return false;

	}
private void checkIfSourceTagHasProperAttributes(Node node) throws CodeGenFailedException {
		
		if(!(node.hasAttributes()))
		{
			throw new CodeGenFailedException("source tag does not have attributes");
		}
		if(node.getAttributes().getNamedItem(SOURCE_NAMESPACE)==null || 
				node.getAttributes().getNamedItem(TYPELIBRARY_TAG)==null)
		{
			throw new CodeGenFailedException("Attributes for the source Tag are Invalid");
		}
	}

/**
 * Need to check if the targetNamespace of the current schema is different form the namespace being added.
 * @param node -- schemaNode
 * @param namespaceTobeAdded===the namespace which is to be added
 * @return
 */
	private boolean isTargetNamespaceDifferent(Node node,String namespaceTobeAdded) {
		s_logger.log(Level.FINE, "BEGIN isNamespaceDifferent()...");

		if (node.getAttributes().getNamedItem(XML_TARGETNAMESPACE).getNodeValue().equals(
				namespaceTobeAdded))
			return false;
		else
			return true;
	}
/**
 * This method adds the additional schemas inside the <Wsdl:types> section 
 */
	private void addAdditionalSchemas() {

		Iterator<String> alNamespaces = m_allNamespaces.iterator();
		while (alNamespaces.hasNext()) {
			String targetnamespaceForCurrentNode = (String) alNamespaces.next();
			List<Node> allNodesWithSameNamespace = getallNodesWithNamespace(targetnamespaceForCurrentNode);
			addnewSchemaInwsdl(targetnamespaceForCurrentNode,
					allNodesWithSameNamespace);

		}
	}
/**
 * All Elements  belonging to a particular namespace should go into One schema. 
 * @param newNamespace --the namespace to be checked.
 * @param NodesWithSameNamespace -- all the nodes with same namespace in the wsdl.
 */
	private void addnewSchemaInwsdl(String newNamespace,
			List<Node> NodesWithSameNamespace) {
		NamedNodeMap attributemap = m_schemaNode.getAttributes();
		int length = attributemap.getLength();
		String[] oldAttributeName = new String[length];
		String[] oldAttributeValue = new String[length];

		Element element = m_Document.createElement(m_schemaNode.getNodeName());
		for (int i = 0; i < attributemap.getLength(); i++) {
			oldAttributeName[i] = attributemap.item(i).getNodeName();
			oldAttributeValue[i] = attributemap.item(i).getNodeValue();
			if (oldAttributeName[i].equals(XML_TARGETNAMESPACE)) {
				oldAttributeValue[i] = newNamespace;
			}
			element.setAttribute(oldAttributeName[i], oldAttributeValue[i]);
		}
		for (int i = 0; i < NodesWithSameNamespace.size(); i++) {
			Node currentNode = NodesWithSameNamespace.get(i);
			if (currentNode.hasAttributes()
					&& currentNode.getAttributes().getNamedItem(NAME_TAG) != null) {
				String typeName = currentNode.getAttributes().getNamedItem(
						NAME_TAG).getNodeValue();
				m_TypeWithNamespaceMapFinal.put(typeName, newNamespace);
			}
			element.appendChild(currentNode);

		}
		m_schemaNode.getParentNode().appendChild(element);

	}
/**
 * This method returns the proper parent (complextype or SimpleType)
 * @param node --curretnNode in the schema
 * @return --proper parent
 */
	private Node getproperParentNode(Node node) {
		s_logger.log(Level.FINE, "BEGIN getproperParentNode()...");
		if (node.getParentNode().getNodeName().contains(XML_COMPLEXTYPE)
				|| node.getParentNode().getNodeName().contains(XML_SIMPLETYPE))
			return node.getParentNode();
		else
			return getproperParentNode(node.getParentNode());
	}
/**
 * This method returns all the nodes with a particlar namespace in the schema
 * @param namespace --the namespace
 * @return list of nodes
 */
	private List<Node> getallNodesWithNamespace(String namespace) {
		List<Node> allNodes = new ArrayList<Node>();
		Iterator<ClonedSchemaNodeInfo> iterator = m_AllClonedNodeswithAdditionalInfo.iterator();
		while (iterator.hasNext()) {
			ClonedSchemaNodeInfo currentNode = iterator.next();

			if (currentNode.getAssociatedNamespace().equals(namespace)) {
				allNodes.add(currentNode.getNode());
			}
		}
		return allNodes;
	}

}
