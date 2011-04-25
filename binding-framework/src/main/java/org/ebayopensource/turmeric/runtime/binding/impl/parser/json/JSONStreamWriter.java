/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.json;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.DataBindingOptions;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.BaseStreamWriter;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.IndexedQName;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.binding.utils.BufferedCharWriter;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;


/**
 * 
 * @author wdeng
 * 
 */
public class JSONStreamWriter extends BaseStreamWriter {
	public static final String KEY_USE_SCHEMA_INFO = "useSchemaInfo";
	public static final String KEY_FORMAT_OUTPUT = "formatOutput";
	public static final String KEY_VALUE_KEY = "valueKey";
	private static final String KEY_NO_ROOT = DataBindingOptions.NoRoot.getOptionName();

	private boolean m_useSchemaInfo = true;
	private boolean m_formatOutput = false;
	private String m_valueKey = BindingConstants.JSON_VALUE_KEY;
	private boolean m_noRoot = false; //true;

	private String m_lastWritten=null;
		
	private int m_nestedLevel = 0;
	private final BufferedCharWriter m_os;
	private boolean m_hasNSDefinition = false;
	private boolean m_shouldOutputNamespaceDefs = true;
	private String m_defaultNamespace = null;
	private boolean m_singleNamespace = false;

	private NodeInfo m_currentNodeInfo;   // This is the key structure of the writer. See notes on NodeInfo.
	private DataElementSchema m_rootEleSchema;
	
	private boolean m_justSkippedRoot = false;

	public JSONStreamWriter(NamespaceConvention convention, Charset charset, OutputStream os) 
		throws XMLStreamException {
		this(convention, null, charset, os, CollectionUtils.EMPTY_STRING_MAP);
	}

	public JSONStreamWriter(NamespaceConvention convention, DataElementSchema rootEleSchema,
			Charset charset, OutputStream os, Map<String, String> options)
		throws XMLStreamException
	{
		super(convention);
		m_os = new BufferedCharWriter(os, charset, 2048);
		m_defaultNamespace = convention.getSingleNamespace();
		m_singleNamespace = convention.isSingleNamespace();
		setupOptions(options);
		if (null == rootEleSchema) {
			m_useSchemaInfo = false;
		}
		if (m_useSchemaInfo) {
			m_rootEleSchema = rootEleSchema;
		}
	}
	
	@Override
	public void close() throws XMLStreamException {
		try {
			m_os.close();
		} catch (IOException e) {
			throw new XMLStreamException(e);
		}
	}

	@Override
	public void flush() throws XMLStreamException {
		try {
			m_os.flush();
		} catch (IOException e) {
			throw new XMLStreamException(e);
		}
	}

	@Override
	public void writeCharacters(String value) throws XMLStreamException {

		try {
			m_currentNodeInfo.setHasCharacters(true);
			boolean isArray = true;
			if (m_useSchemaInfo) {
				DataElementSchema elementSchema = m_currentNodeInfo.getElementSchema();
				isArray =  elementSchema.getMaxOccurs() == -1;
			}
			if (m_currentNodeInfo.hasAttribute()) {
				writeToStream(",");
				if (m_formatOutput) {
					writeIndentation(m_nestedLevel);
				}
				writeToStream("\"");
				writeToStream(m_valueKey);
				writeToStream("\":");
			} else if (isArray) {
				NodeInfo parent = m_currentNodeInfo.getParent();
				if (null == parent || parent.isLastKnownChildWithNewName()) {
					writeToStream("[");
				}
			}
			if (value != null) {
				writeToStream("\"");
				writeToStream(encodeValue(value)); 
				writeToStream("\"");
			} else {
				writeToStream(BindingConstants.NULL_VALUE_STR);
			}
		} catch (IOException ioe) {
			throw new XMLStreamException(ioe);
		}
	}

	@Override
	public void writeAttribute(String prefix, String nsURI, String localName,
			String value) throws XMLStreamException {
		writeElementName(prefix, localName, nsURI, true);
		try {
			writeToStream("\"");
			writeToStream(encodeValue(value)); 
			writeToStream("\"");
		} catch (IOException ioe) {
			throw new XMLStreamException(ioe);
		}
		m_currentNodeInfo.setHasAttribute(true);
	}

	@Override
	public void writeStartElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException
	{
		if (null == localName) {
			throw new XMLStreamException(
					"writeStartElement expects non-null local name.");
		}
		
		String prefix2 = m_convention.getPrefix(namespaceURI);
		writeElementName(prefix2, localName, namespaceURI, false);
		createNodeInfo(localName, namespaceURI);
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
		try {
			closeChildArrayElementsWithBlanket();
			// The case that root element is reduced. 
			if (m_noRoot && m_currentNodeInfo.getParent() == null) {
				if (!m_useSchemaInfo && m_currentNodeInfo.getLastKnownChild() == null && (m_currentNodeInfo.hasAttribute() || m_currentNodeInfo.hasCharacters())) {
					writeToStream("]");
				}
				return;
			}

			NodeInfo lastKnownSibling = getLastKnownChildOfCurrentNode();
			if (lastKnownSibling != null) {
				writeIndentation(m_nestedLevel-1);
				writeToStream("}");
					
				// Break the reference to grand child to release memory
				lastKnownSibling.addChild(null);
				
				// Handling case that the element has attribute.
			}  else if (m_currentNodeInfo.hasAttribute()) {
				writeIndentation(m_nestedLevel-1);
				writeToStream("}");
			} 
			
		} catch (IOException ioe) {
			throw new XMLStreamException(ioe);
		} finally {
			// Move m_currentNodeInfo up one level.
			m_currentNodeInfo = m_currentNodeInfo.getParent();
			m_nestedLevel--;
		}
	}

	public void writeStartDocument() throws XMLStreamException {
		try {
			writeToStream("{");
			writeIndentation(0);

			if (m_shouldOutputNamespaceDefs) {
				m_shouldOutputNamespaceDefs = false;

				if (m_singleNamespace) {
					return;
				}
				
				// Write the namespace prefix definitions.
				Map<String, String> prefixToNSMap = m_convention.getPrefixToNamespaceMap();
				Iterator<Entry<String, String>> iter = prefixToNSMap.entrySet().iterator();
				while (iter.hasNext()) {
					m_hasNSDefinition = true;
					Entry<String, String> entry = iter.next();
					String prefix = (String)entry.getKey();
					String ns = (String)entry.getValue();
					writeToStream("\"");
					writeToStream(JSONConstants.JSON_NAMESPACE_DEF_PREFIX);
					if (!(prefix == null) && !("".equals(prefix))) {
						writeToStream(".");
						writeToStream(prefix);
					}
					writeToStream("\":\"");
					writeToStream(ns);
					writeToStream("\"");
					if (iter.hasNext()) {
						writeToStream(",");
						writeIndentation(0);
					}
				}
			}
		} catch (IOException ioe) {
			throw new XMLStreamException(ioe);
		}
	}

	@Override
	public void writeEndDocument() throws XMLStreamException {
		try {
			// When schema info is not available, we already need to close the blankets when there is an attribute.
			if (!m_useSchemaInfo && !m_noRoot) {
				writeToStream("]");
			}
			if (m_formatOutput) {
				writeToStream("\n}\n");
			} else {
				writeToStream("}");
			}
		} catch (IOException ioe) {
			throw new XMLStreamException(ioe);
		}
	}

	private void writeElementName(String prefix, String localName, String namespaceURI, boolean isAttribute) throws XMLStreamException {
		try {
			NodeInfo lastKnownSibling = getLastKnownChildOfCurrentNode();
			String prevNS = lastKnownSibling == null ? "" : lastKnownSibling.getNamespaceURI();
			String prevLocalName = lastKnownSibling == null ? "" : lastKnownSibling.getLocalPart();
			if (m_currentNodeInfo == null) {
				// Handles the first top level element
				
				if (m_hasNSDefinition) {
					writeToStream(",");
				}
				
				if( ! m_noRoot ) {
					writeCurrentElement(prefix, localName, namespaceURI, isAttribute);
				} else {
					m_justSkippedRoot = true;
				}
				
			} else {
				if (!(localName.equals(prevLocalName) && namespaceURI.equals(prevNS)))
				{
					if( m_justSkippedRoot ) {
						m_justSkippedRoot = false;
					} else {
						closeSiblingArrayElementsWithBlanket();
						prepareToStartNewElement();
					}
				
					writeCurrentElement(prefix, localName, namespaceURI, isAttribute);
				} else { // Array element continuing with the same QName
					writeToStream(",");
					writeIndentation(m_nestedLevel);
				}
			}
		} catch (IOException ioe) {
			throw new XMLStreamException(ioe);
		}
	}

	private void createNodeInfo(String localName, String namespaceURI) throws XMLStreamException {
		DataElementSchema eleSchema = getElementSchema(namespaceURI, localName, m_currentNodeInfo);

		String prefix;
		prefix = m_convention.getPrefix(namespaceURI);

		int index = 0;
		QName nodeName = createQName(m_convention, prefix, localName);
		if (null != m_currentNodeInfo && m_currentNodeInfo.sameQName(nodeName)) {
			index = m_currentNodeInfo.getIndex() + 1;
		}

		NodeInfo currentNodeInfo = new NodeInfo(nodeName, index, eleSchema, m_currentNodeInfo);
		m_nestedLevel++;
		if (null != m_currentNodeInfo) {
			m_currentNodeInfo.addChild(currentNodeInfo);
		} 
		m_currentNodeInfo = currentNodeInfo;
	}

	private void writeIndentation(int level) throws IOException {
		if (m_formatOutput) {
			writeToStream("\n");
			for (int i = 0; i <= level; i++) {
				writeToStream("\t");
			}
		}
	}

	private void prepareToStartNewElement() throws IOException, XMLStreamException {		
		if (m_currentNodeInfo == null || m_currentNodeInfo.getLastKnownChild() == null) {
			// Processing elements at one level higher.
			// For element that has attribute;
			if (m_currentNodeInfo != null && m_currentNodeInfo.hasAttribute() ) {
				writeToStream(",");
				return;
			} 
			
			// For elements without attribute;
			NodeInfo parent = m_currentNodeInfo == null ? null : m_currentNodeInfo.getParent();
			boolean isFirstArrayElement = parent == null ? true : parent.isLastKnownChildWithNewName();
			if (m_useSchemaInfo) {
				// Use Schema Info to reduce the use of blankets
				DataElementSchema currentNodeSchema = m_currentNodeInfo.getElementSchema();
				isFirstArrayElement = isFirstArrayElement && (currentNodeSchema == null ? false : (currentNodeSchema.getMaxOccurs() == -1));
			} 
			if (isFirstArrayElement && !m_currentNodeInfo.hasAttribute()){
				writeToStream("[{");
			} else {
				writeToStream("{");
			}
			return;
		} 
		// For elements at the same level
		writeToStream(",");
	}
	
	private NodeInfo getLastKnownChildOfCurrentNode() {
		if (m_currentNodeInfo == null) {
			return null;
		}
		return m_currentNodeInfo.getLastKnownChild();
	}

	private void writeCurrentElement(String prefix, String localName, String namespaceURI, boolean isAttribute) throws IOException {
		writeIndentation(m_nestedLevel);
		writeToStream("\"");
		if ((!m_singleNamespace || !m_defaultNamespace.equals(namespaceURI) || isAttribute)
				&& prefix != null && prefix.length() != 0) {
			writeToStream(prefix);
			writeToStream(".");
		}
		if (isAttribute) {
			writeToStream(BindingConstants.ATTRIBUTE_MARK);
		}
		writeToStream(localName);
		writeToStream("\":");
	}

	static QName createQName(NamespaceConvention convention, String prefix, String name) {
		QName qname = null;
		if (prefix != null) {
			// TODO: use function that checks for no namespace
			String xns = convention.getNamespaceUriNoChecks(prefix);

			if (xns == null) {
				qname = new QName("", name, prefix);
			} else {
				qname = new QName(xns, name, prefix);
			}
		} else {
			qname = new QName(name);
		}
		return qname;
	}

	private void setupOptions(Map<String, String>options) {
		String useSchemaInfoOption = options.get(KEY_USE_SCHEMA_INFO);
		if (null != useSchemaInfoOption) {
			m_useSchemaInfo = Boolean.parseBoolean(useSchemaInfoOption);
		}

		String formatOutput = options.get(KEY_FORMAT_OUTPUT);
		if (null != formatOutput) {
			m_formatOutput = Boolean.parseBoolean(formatOutput);
		}

		String valueKey = options.get(KEY_VALUE_KEY);
		if (null != valueKey && valueKey.length() > 0) {
			m_valueKey = valueKey;
		}
		
		String noRootValue = options.get(KEY_NO_ROOT);
		if( noRootValue != null ) {
			m_noRoot = Boolean.parseBoolean(noRootValue);
		}
		
	}

	private DataElementSchema getElementSchema(String namespaceURI, String localName, NodeInfo parentNode) throws XMLStreamException {
		DataElementSchema eleSchema = null;
		if (!m_useSchemaInfo) {
			return null;
		}
		if (parentNode == null) {
			eleSchema = m_rootEleSchema;
		} else { 
			DataElementSchema parentEleSchema = parentNode.getElementSchema();
			eleSchema = parentEleSchema.getChild(namespaceURI, localName);
		}
		if (eleSchema == null) {
			throw new XMLStreamException("Unable to load schema information for: " + localName);
		}
		return eleSchema;
	}

	public static final String encodeValue(String s) {
		if(s == null)
			return "";
		
		StringBuffer sbuf = new StringBuffer();
		for(int i=0; i<s.length(); i++) {
			char c = s.charAt(i);
			switch(c) {
			case '"':
				sbuf.append("\\\"");
				break;
			case '\\':
				sbuf.append("\\\\");
				break;
			case '/':
				sbuf.append("\\/");
				break;
			case '\b':
				sbuf.append("\\b");
				break;
			case '\t':
				sbuf.append("\\t");
				break;
			case '\n':
				sbuf.append("\\n");
				break;
			case '\f':
				sbuf.append("\\f");
				break;
			case '\r':
				sbuf.append("\\r");
				break;
				
			default:
				if( c < ' ' ||  (c >= '\u0080' && c < '\u00a0') ||
                        		(c >= '\u2000' && c < '\u2100') ) {
					String s1 = Integer.toHexString(c);
					while(s1.length()<4) {
						s1 = "0" + s1;
					}
					sbuf.append("\\u" + s1);
				} else {
					sbuf.append(c);
				}
				break;
			}
		}
		
		return sbuf.toString();
	}
	
	// This is to close the sibling element before we start with the element currently started to be written.
	private void closeSiblingArrayElementsWithBlanket() throws IOException {
		NodeInfo lastKnownSibling = getLastKnownChildOfCurrentNode();
		if (lastKnownSibling == null) {
			// This is the first node at this level. nothing to close.
			return;
		}
		if (m_useSchemaInfo) {
			if (lastKnownSibling.getElementSchema().getMaxOccurs() == -1) {
				writeToStream("]");
			}
			return;
		} 
		// When schema info is not available, we already need to close the blankets when there is an attribute.
		if (lastKnownSibling.hasAttribute()) {
			writeToStream("]");
			return;
		}

		if (lastKnownSibling.getLastKnownChild() != null || lastKnownSibling.hasCharacters() || lastKnownSibling.hasAttribute()) {
			writeToStream("]");
		}		
	}
	
	private void closeChildArrayElementsWithBlanket() throws IOException {
		NodeInfo lastKnownChild = getLastKnownChildOfCurrentNode();  
		if (lastKnownChild == null ) {
			return;
		}
		if (m_useSchemaInfo) {
			if (lastKnownChild.getElementSchema().getMaxOccurs() == -1) {
				writeToStream("]");
			}
			return;
		} 
		writeToStream("]");
		return;
	}

	
	private void writeToStream(String str) throws IOException{
		//check for exceptional cases
		if (m_lastWritten!=null){
			if (m_lastWritten.endsWith(":") && 
					( str.startsWith(",") || str.startsWith("\n}") || str.startsWith("}")) ){
				m_os.write("null");
			} 
		}
		m_os.write(str);
		m_lastWritten = str;
	}
	/**
	 * This is the key structure for the writer.  Through parent variable, 
	 * it keep a stack of all the nested nodes.  Through lastKnownChild variable, it keeps track 
	 * of the last node that has been processed.  When the stack level retreat, we break the 
	 * lastKnownChild link, so no DOM is maintained.
	 * 
	 * @author wdeng
	 *
	 */
	private static final class NodeInfo extends IndexedQName {
		NodeInfo m_lastKnownChild;
		boolean m_isLastKnownChildWithNewName = true;
		boolean m_hasAttribute = false;
		boolean m_hasCharacters = false;
		DataElementSchema m_elementSchema;
		NodeInfo m_parent;
		NodeInfo(QName qName, int index, DataElementSchema schema, NodeInfo parent) {
			super(qName, index);
			m_lastKnownChild = null;
			m_elementSchema = schema;
			m_parent = parent;
		}
		
		NodeInfo getParent() {
			return m_parent;
		}
		
		boolean isLastKnownChildWithNewName() {
			return m_isLastKnownChildWithNewName;
		}
		NodeInfo getLastKnownChild() {
			return m_lastKnownChild;
		}
		
		DataElementSchema getElementSchema() {
			return m_elementSchema;
		}
		
		void addChild(NodeInfo child) {
			// If the newly added child has new QName than the previous child, set
			// m_isLastKnownChildWithNewName to true.
			if (null == child) {
				m_isLastKnownChildWithNewName = false;
				m_lastKnownChild = null;
				return;
			}
			if (m_lastKnownChild == null) {
				m_isLastKnownChildWithNewName = true;
			} else {
				m_isLastKnownChildWithNewName = !child.getNamespaceURI().equals(m_lastKnownChild.getNamespaceURI()) 
					|| !child.getLocalPart().equals(m_lastKnownChild.getLocalPart());
			}
			m_lastKnownChild = child;
		}

		boolean hasAttribute() {
			return m_hasAttribute;
		}

		void setHasAttribute(boolean attribute) {
			m_hasAttribute = attribute;
		}

		boolean hasCharacters() {
			return m_hasCharacters;
		}

		void setHasCharacters(boolean hasCharacters) {
			this.m_hasCharacters = hasCharacters;
		}
		

		@Override
		public int hashCode() {
			return super.hashCode() ^ m_lastKnownChild.hashCode() ^ m_elementSchema.hashCode()
				^ m_parent.hashCode() ^ (m_isLastKnownChildWithNewName ? 0 : 1) 
				^ (m_hasCharacters ? 0 : 2)
				^ (m_hasAttribute ? 0 : 8);
		}


		@Override
		public boolean equals(Object other) {
			if (other == this) {
				return true;
			}

			if (other == null || !(other instanceof NodeInfo)) {
				return false;
			}

			NodeInfo other2 = (NodeInfo)other;

			if (! equals (m_parent, other2.m_parent)) {
				return false;
			}

			if (! equals (m_elementSchema, other2.m_elementSchema)) {
				return false;
			}

			if (! equals (m_lastKnownChild, other2.m_lastKnownChild)) {
				return false;
			}
			
			if (m_isLastKnownChildWithNewName != other2.m_isLastKnownChildWithNewName) {
				return false;
			}
			
			if (m_hasAttribute != other2.m_hasAttribute) {
				return false;
			}
			
			if (m_hasCharacters != other2.m_hasCharacters) {
				return false;
			}

			return super.equals(other);
		}
		
		private boolean equals(Object o1, Object o2) {
			if (o1 == null) {
				return o2 == null;
			}
			return o1.equals(o2);
		}
	}
}


