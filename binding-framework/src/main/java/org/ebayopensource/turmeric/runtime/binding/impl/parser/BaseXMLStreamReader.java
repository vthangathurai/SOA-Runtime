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
package org.ebayopensource.turmeric.runtime.binding.impl.parser;

import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;


public abstract class BaseXMLStreamReader implements XMLStreamReader {
	private static final String KEY_ELEMENT_FORM_DEFAULT = "elementFormDefault";
	public static final String KEY_DOUBLE_QUOTE_DELIMITED = "quoteValue";
	
    protected int m_event;
    protected ObjectNode m_node;
    protected QName m_normalizedNodeName = null;
    private QName m_prevQName = null;
    protected NamespaceConvention m_convention;
    protected boolean m_isNull;
    protected boolean m_elementFormDefaultQualified = true;

    protected boolean m_doubleQuoteDelimited = true;
    
    public BaseXMLStreamReader(NamespaceConvention convention, Map<String, String> options) {
		if (null == convention) {
			throw new NullPointerException("OrderedNVStreamReader requires namespaceConvention");
		}
    	m_convention = convention;
		setupOptions(options);
    }

    public abstract int getAttributeCount();
    public abstract String getAttributeLocalName(int n);
    public abstract String getAttributeNamespace(int n);
    public abstract String getAttributePrefix(int n);
    public abstract String getAttributeValue(int n);

    public QName getName() {
    	QName nodeName = internalGetName();
    	if (nodeName.equals(m_prevQName)) {
    		return m_normalizedNodeName;
    	}
    	m_prevQName = nodeName;
     	String nsURI = nodeName.getNamespaceURI();
    	String newURI = nsURI;
		if (m_elementFormDefaultQualified
				&& (null == nsURI || nsURI.length() == 0)) {
			newURI = m_convention.getSingleNamespace();
		}
		if (newURI == null) {
			newURI = "";
		}
		if(!newURI.equals(nsURI)) {
			m_normalizedNodeName = new QName(newURI, nodeName.getLocalPart(), nodeName
				.getPrefix());
		}
		else {
			m_normalizedNodeName = nodeName;
		}
		return m_normalizedNodeName;
    }
    
    protected QName internalGetName() {
    	return m_node.getNodeName();
    }

    public boolean isAttributeSpecified(int arg0) {
        return false;
    }

    public boolean isCharacters() {
        return m_event == CHARACTERS;
    }

    public boolean isEndElement() {
        return m_event == END_ELEMENT;
    }

    public boolean isStandalone() {
        return false;
    }

    public boolean isStartElement() {
        return m_event == START_ELEMENT;
    }

    public boolean isWhiteSpace() {
        return false;
    }

    public int nextTag() throws XMLStreamException {
        int event = next();
        while (event != START_ELEMENT && event != END_DOCUMENT) {
            event = next();
        }
        return event;
    }

    public int getEventType() {
        return m_event;
    }

    public void require(int arg0, String arg1, String arg2) throws XMLStreamException {
		// noop
    }

    public QName getAttributeName(int n) {
        throw new UnsupportedOperationException(this.getClass().getName() + ".getAttributeName");
    }

    public String getAttributeValue(String ns, String local) {
        throw new UnsupportedOperationException(this.getClass().getName() + ".getAttributeValue");
    }

    public String getAttributeType(int arg0) {
        throw new UnsupportedOperationException(this.getClass().getName() + ".getAttributeType");
    }

    public String getLocalName() {
        return getName().getLocalPart();
    }

    public String getNamespaceURI() {
    	QName name = getName();
        return name.getNamespaceURI();
    }

    public int getNamespaceCount() {
        return 0;
    }

    public String getNamespacePrefix(int n) {
    	return null;
    }

    public String getNamespaceURI(int n) {
    	return null;
    }

    public String getNamespaceURI(String prefix) {
    	return m_convention.getNamespaceURI(prefix);
    }

    public boolean hasName() {
        return false;
    }

    public boolean hasNext() throws XMLStreamException {
        return m_event != END_DOCUMENT;
    }

    public boolean hasText() {
        return m_event == CHARACTERS;
    }

    public boolean standaloneSet() {
        return false;
    }

    public String getCharacterEncodingScheme() {
    	return null;
    }

    public String getEncoding() {
        throw new UnsupportedOperationException(this.getClass().getName() + ".getEncoding");
    }

    public Location getLocation() {
        return new Location() {

            public int getCharacterOffset() {
                return 0;
            }

            public int getColumnNumber() {
                return 0;
            }

            public int getLineNumber() {
                return -1;
            }

            public String getPublicId() {
                return null;
            }

            public String getSystemId() {
                return null;
            }
            
        };
    }

    public String getPIData() {
        throw new UnsupportedOperationException(this.getClass().getName() + ".getPIData");
    }

    public String getPITarget() {
        throw new UnsupportedOperationException(this.getClass().getName() + ".getPITarget");
    }

    public String getPrefix() {
        return getName().getPrefix();
    }

    public Object getProperty(String arg0) throws IllegalArgumentException {
        throw new UnsupportedOperationException(this.getClass().getName() + ".getProperty");
    }

    public String getVersion() {
        return null;
    }

    public char[] getTextCharacters() {
        return getText().toCharArray();
    }

    public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3) throws XMLStreamException {
       throw new UnsupportedOperationException(this.getClass().getName() + ".getTextCharacters");
    }

    public int getTextLength() {
        return getText().length();
    }

    public int getTextStart() {
        return 0;
    }

	private void setupOptions(Map<String, String>options) {
		if (null == options) {
			return;
		}
		String elementFormDefault = options.get(KEY_ELEMENT_FORM_DEFAULT);
		if (null != elementFormDefault && !"Qualified".equalsIgnoreCase(elementFormDefault)) {
			m_elementFormDefaultQualified = false;
		}
		
		String doubleQuoteDelimited = options.get(KEY_DOUBLE_QUOTE_DELIMITED);
		if( doubleQuoteDelimited != null ) {
			m_doubleQuoteDelimited = Boolean.parseBoolean(doubleQuoteDelimited); 
		}
	}
}
