/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.nv;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.BaseStreamWriter;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.IndexedQName;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.binding.utils.BufferedCharWriter;
import org.ebayopensource.turmeric.runtime.binding.utils.CollectionUtils;

import com.ebay.kernel.util.FastURLEncoder;

/**
 * @author wdeng
 */
public class NVStreamWriter extends BaseStreamWriter {

	public static final String KEY_USE_BRACKETS = "useBrackets";
	public static final String KEY_QUOTE_VALUE = "quoteValue";
	public static final String KEY_ENCODE_VALUE = "encodeValue";
	public static final String KEY_FULL_URL_ENCODED = "fullURLEncoded";
	public static final String KEY_ALWAYS_ADD_PREFIX = "alwaysAddPrefix";
	public static final String KEY_USE_SCHEMA_INFO = "useSchemaInfo";

	private static final String[] INTEGER_STRINGS = new String[] { "0",
			"1", "2", "3", "4", "5", "6", "7", "8", "9" };

	private final ArrayList<IndexedQName> m_names;
	private final BufferedCharWriter m_os;
	private boolean m_shouldAddAmpersand = false;
	private IndexedQName m_prevQName;
	private boolean m_shouldOutputNamespaceDefs = true;
	private QName m_rootXmlName;
	private char[] m_indexChars = new char[] {'(',')'};
	private boolean m_quoteValue = true;
	private boolean m_encodeValue = true;
	private boolean m_alwaysAddPrefix = false;
	private boolean m_skipPrefix = false;
	private boolean m_fullURLEncoding = false;
	private boolean m_useSchemaInfo = false;
	private String  m_singleNamespacePrefix;
	private Charset m_charset;
	private DataElementSchema m_rootEleSchema;

	private ArrayList<DataElementSchema> m_elementPath;

	public NVStreamWriter(NamespaceConvention conv, QName rootXmlName, OutputStream os)
			throws XMLStreamException {
		this(conv, os, Charset.forName("UTF-8"), rootXmlName, null, CollectionUtils.EMPTY_STRING_MAP, false);
	}

	public NVStreamWriter(NamespaceConvention convention, OutputStream os,
		Charset charset, QName rootXmlName, DataElementSchema rootEleSchema,
		Map<String, String>options, boolean fullURLEncoding)
		throws XMLStreamException
	{
		super(convention);

		m_names = new ArrayList<IndexedQName>(32);
		m_os = new BufferedCharWriter(os, charset, 2048);
		m_charset = charset;
		m_rootXmlName = rootXmlName;
		setupOptions(options);
		m_fullURLEncoding = fullURLEncoding;
		m_skipPrefix = !m_alwaysAddPrefix && convention.isSingleNamespace();
		if (m_skipPrefix && convention.isSingleNamespace()) {
			m_singleNamespacePrefix = convention.getPrefix(convention.getSingleNamespace());
		}
		if (m_useSchemaInfo) {
			m_elementPath = new ArrayList<DataElementSchema>();
			m_rootEleSchema = rootEleSchema;
		}
	}

	private void setupOptions(Map<String, String>options) {
		if (null == options) {
			return;
		}
		String useBracketsOption = options.get(KEY_USE_BRACKETS);
		if (useBracketsOption != null && Boolean.parseBoolean(useBracketsOption)) {
				m_indexChars[0] = '[';
				m_indexChars[1] = ']';
		}

		String quoteValueOption = options.get(KEY_QUOTE_VALUE);
		if (null != quoteValueOption) {
			m_quoteValue = Boolean.parseBoolean(quoteValueOption);
		}

		String encodeValueOption = options.get(KEY_ENCODE_VALUE);
		if (null != encodeValueOption) {
			m_encodeValue = Boolean.parseBoolean(encodeValueOption);
		}

		String alwaysAddPrefixOption = options.get(KEY_ALWAYS_ADD_PREFIX);
		if (null != alwaysAddPrefixOption) {
			m_alwaysAddPrefix = Boolean.parseBoolean(alwaysAddPrefixOption);
		}

		String useSchemaInfoOption = options.get(KEY_USE_SCHEMA_INFO);
		if (null != useSchemaInfoOption) {
			m_useSchemaInfo = Boolean.parseBoolean(useSchemaInfoOption);
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
		writeNVPair(null, null, value);
	}

	private void writeNVPair(String nsURI, String localName, String value) throws XMLStreamException {
		try {
			if (m_shouldAddAmpersand) {
				m_os.write('&');
			} else {
				m_shouldAddAmpersand = true;
			}

			int len = m_names.size();
			// This is to handle simple argument type.
			boolean isSimpleType = (len == 0);
			if (isSimpleType) {
				m_names.add(new IndexedQName(m_rootXmlName, 0));
				len = 1;
			}
			if (m_useSchemaInfo) {
				writeNVPathWithSchemaInfo(nsURI, localName, value, isSimpleType);
			} else {
				writeNVPathWithoutSchemaInfo(nsURI, localName, value);
			}

			if (localName != null) {
				m_os.write('.');
				writeOneName(nsURI, localName);
			}

			if (value != null) {
				m_os.write("=");
				if (m_quoteValue) {
					m_os.write('\"');
				}
				if (m_fullURLEncoding) {
					writeURLEncodedValue(value);
				} else {
					if (m_encodeValue) {
						writeURLEncodedValue(value);
					} else if (m_quoteValue) {
						writeDoubleQuoteEncodedValue(value);
					} else {
						writeNVEncodedValue(value);
					}
				}
				if (m_quoteValue) {
					m_os.write('\"');
				}
			} else {
				m_os.write("=null");
			}
		} catch (IOException ioe) {
			throw new XMLStreamException(ioe);
		}
	}

	private void writeNVPathWithSchemaInfo(String nsURI, String localName, String value, boolean isSimpleType)
		throws XMLStreamException, IOException {
		DataElementSchema currentElement = null;
		Iterator<DataElementSchema> elementIter = m_elementPath.iterator();
		// NV format always skips the root element if it is not simple type object.
		if (!isSimpleType) {
			elementIter.next();
		}
		for (int i = 0; i < m_names.size(); i++) {
			IndexedQName name = m_names.get(i);
			if (elementIter.hasNext()) {
				currentElement = elementIter.next();
			}

			if (i > 0) {
				m_os.write('.');
			}

			writeOneName(name.getNamespaceURI(), name.getLocalPart());
			int maxOccurs = currentElement == null ? BindingConstants.UNBOUNDED : currentElement.getMaxOccurs();
			if (maxOccurs > 1 || maxOccurs == BindingConstants.UNBOUNDED) {
				writeIndex(name.getIndex());
			}
		}
	}

	private void writeNVPathWithoutSchemaInfo(String nsURI, String localName, String value)
		throws XMLStreamException, IOException {
		int len = m_names.size();
		for (int i = 0; i < len; i++) {
			IndexedQName name = m_names.get(i);

			if (i > 0) {
				m_os.write('.');
			}

			writeOneName(name.getNamespaceURI(), name.getLocalPart());
			writeIndex(name.getIndex());
		}
	}

	private void writeIndex(int index) throws IOException {
		m_os.write(m_indexChars[0]);
		if (index <= 9) {
			// index cannot be negative in this writer
			m_os.write(INTEGER_STRINGS[index]);
		} else {
			m_os.write(Integer.toString(index));
		}
		m_os.write(m_indexChars[1]);
	}

	private String writeOneName(String nsURI, String localName) throws IOException {
		String prefix = m_convention.getPrefix(nsURI);
		if ((!m_skipPrefix || !prefix.equals(m_singleNamespacePrefix)) && !"".equals(prefix)) {
			m_os.write(prefix);
			m_os.write(':');
		}

		m_os.write(localName);
		return prefix;
	}

	@Override
	public void writeAttribute(String prefix, String nsURI, String localName,
			String value) throws XMLStreamException {
		writeNVPair(nsURI, BindingConstants.ATTRIBUTE_MARK + localName, value);
	}

	@Override
	public void writeStartElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException
	{
		if (null == localName) {
			throw new XMLStreamException(
					"writeStartElement expects non-null local name.");
		}

		int index = 0;
		if (null != m_prevQName
				&& m_prevQName.sameQName(namespaceURI, localName, prefix)) {
			index = m_prevQName.getIndex() + 1;
		}
		IndexedQName elementName = new IndexedQName(namespaceURI, localName, prefix, index);

		// Find the DataElementSchema and add it to the stack.
		if (m_useSchemaInfo) {
			setupSchemaInfo(elementName);
		}
		if (elementName.getQName().equals(m_rootXmlName)) {
			return;
		}
		m_names.add(elementName);
	}

	private void setupSchemaInfo(IndexedQName elementName) throws XMLStreamException {
		DataElementSchema eleSchema = null;
		QName qName = elementName.getQName();
		if (m_elementPath.isEmpty()) {
			eleSchema = m_rootEleSchema;
		} else {
			DataElementSchema parentEleSchema = m_elementPath.get(m_elementPath.size() - 1);
			if(!parentEleSchema.hasChildren()) {
				throw new XMLStreamException(
					String.format("Unable to load schema \"%s\" - parent schema \"%s\" has no children", 
							qName, parentEleSchema.getElementName()));
			}
			eleSchema = parentEleSchema.getChild(qName);
			if (eleSchema == null) {
				throw new XMLStreamException(
						String.format("Unable to load schema \"%s\" - Not a child of parent schema \"%s\"", 
								qName, parentEleSchema.getElementName()));
			}
		}
		if (eleSchema == null) {
			throw new XMLStreamException("Unable to load schema information for: " + qName);
		}
		m_elementPath.add(eleSchema);
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
		if (m_names.size() < 1) {
			return;
		}
		if (m_useSchemaInfo) {
			m_elementPath.remove(m_elementPath.size() - 1);
		}
		m_prevQName = m_names.remove(m_names.size() - 1);
	}

	public void writeStartDocument() throws XMLStreamException {
		if (!m_shouldOutputNamespaceDefs) {
			return;
		}
		m_shouldOutputNamespaceDefs = false;

		// If it is single namespace message, don't output NS definition.
		if (m_skipPrefix) {
			return;
		}
		Map<String, String> prefixToNSMap = m_convention
				.getPrefixToNamespaceMap();
		for (Map.Entry<String,String> e: prefixToNSMap.entrySet()) {
			String prefix = e.getKey();
			String ns = e.getValue();
			outputNamespace(prefix, ns);
		}
	}

	@Override
	public void writeDefaultNamespace(String arg0) throws XMLStreamException {
		m_convention.setSingleNamespace(arg0);
	}

	private void outputNamespace(String prefix, String ns)
			throws XMLStreamException
	{
		try {
			if (m_shouldAddAmpersand) {
				m_os.write('&');
			} else {
				m_shouldAddAmpersand = true;
			}

			m_os.write(NVConstants.NV_NAMESPACE_DEF_PREFIX);
			if (!(null == prefix) && !"".equals(prefix)) {
				m_os.write(':');
				m_os.write(prefix);
			}
			m_os.write("=");
			if( m_quoteValue ) {
				m_os.write("\"");
			}
			m_os.write(ns);
			if( m_quoteValue ) {
				m_os.write('\"');
			}
		} catch (IOException ioe) {
			throw new XMLStreamException(ioe);
		}
	}

	private static final String[] CHAR_ENCODE = new String[256];
	private static final String[] URL_CHAR_ENCODE = new String[256];

	static {
		for (int i=0; i<CHAR_ENCODE.length; i++) {
			CHAR_ENCODE[i] = "" + (char)i;
		}
		CHAR_ENCODE[' '] = "+";		// Only need to encode the
		CHAR_ENCODE['&'] = "%26";   // special character that
		CHAR_ENCODE['='] = "%3d";   // NV format is using.
		CHAR_ENCODE['.'] = "%2e";
		CHAR_ENCODE[':'] = "%3a";
		CHAR_ENCODE['"'] = "%22";

		for (int i = -128; i < 128; i++) {
			char c = (char)i;
			if (   (c >= 'a' && c <= 'z')
				|| (c >= 'A' && c <= 'Z')
				|| (c >= '0' && c <= '9')
				|| c == '.'
				|| c == '-'
				|| c == '_'
				|| c == '*'
				) {

				URL_CHAR_ENCODE[0xff & i] = "" + c;
			}
			else if (c == ' ') {
				URL_CHAR_ENCODE[0xff & i] = "" + '+';
			}
			else {
				URL_CHAR_ENCODE[0xff & i] = getStandardEncodedChar(c);
			}
		}
	}

	private static String getStandardEncodedChar(char c) {
		String encodedStr = Integer.toHexString(c);
		int size = encodedStr.length();

		if (size >= 2) {
			encodedStr = encodedStr.substring(size - 2, size);
			return "%" + encodedStr.toUpperCase();
		}

		return "%0" + encodedStr.toUpperCase();
	}

	// This method try to minimize the URLencoding that we are doing based on 
	// m_charset. we URL encode any character whose character value < 256.  
	// any character > 256 we give them to the java writer to handle the 
	// encoding.  
	// This will not work for services that uses non-SOA client.
	private void writeNVEncodedValue(String value) throws IOException {
		if (null == value) {
			return;
		}
		for (int i=0; i<value.length(); i++) {
			char code = value.charAt(i);
			if (code < 256) {
				m_os.write(CHAR_ENCODE[code]);
			} else {
				m_os.write(code);
			}
		}
	}

	private void writeURLEncodedValue(String value) throws IOException {
		if (null == value) {
			return;
		}
		String encodedValue = FastURLEncoder.encode(value, m_charset.name());
		m_os.write(encodedValue);
	}

	private void writeDoubleQuoteEncodedValue(String value) throws IOException {
		if (null == value) {
			return;
		}
		for (int i=0; i<value.length(); i++) {
			char code = value.charAt(i);
			if (code == '"') {
				m_os.write("%22");
			} else {
				m_os.write(code);
			}
		}
	}

	public void setFullURLEncoding(boolean fullURLEncoded) {
		this.m_fullURLEncoding = fullURLEncoded;
	}
}
