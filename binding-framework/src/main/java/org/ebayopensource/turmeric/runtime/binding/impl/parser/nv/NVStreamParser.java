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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.impl.parser.BaseXMLStreamReader;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.utils.URLDecoderInputStream;


/**
 * @author ichernyshev
 */
public final class NVStreamParser {

	private final static char NO_CHAR = (char)-1;
	private final static char[] DELIMITERS = new char[128];
	private final static char[] VALID_CHARS = new char[128];

	private final static char[] NSPREFIX_DEF;
	private final static int NSPREFIX_DEF_CHECKSUM;

	private final Reader m_reader;
	private final char[] m_buf;
	private int m_position;
	private int m_size;

	private final NamespaceConvention m_convention;

	private int m_pathLen = -1;
	private NVElementHolder[] m_path = new NVElementHolder[8];
	private char[] m_pathDataBuffer = new char[256];
	private StringBuilder m_value = new StringBuilder();
	private int m_nsCount;
	private String m_singleNamespace;
	private int m_valueStart;
	private int m_valueEnd;
	private char[] m_unreadBuffer = {NO_CHAR, NO_CHAR};
	
	private boolean m_decoded = false;
	private boolean m_doubleQuoteDelimited = true;
	
	public NVStreamParser(InputStream is, Charset charset, NamespaceConvention convention) {
		m_buf = new char[10* 1024];
		m_reader = new InputStreamReader(new URLDecoderInputStream(is), charset);
		m_convention = convention;
	}

	public NVStreamParser(InputStream is, Charset charset, NamespaceConvention convention,
			boolean doubleQuoteDelimited) {
		this(is, charset, convention);
		m_doubleQuoteDelimited = doubleQuoteDelimited;
	}

	public NVStreamParser(InputStream is, Charset charset, NamespaceConvention convention, Map<String, String> options) {
		this(is, charset, convention);
		
		setupOptions(options);
	}
	
	private void setupOptions(Map<String, String> options) {
		if(options == null) {
			return;
		}
		
		String s = options.get(BaseXMLStreamReader.KEY_DOUBLE_QUOTE_DELIMITED);
		if( s != null ) {
			m_doubleQuoteDelimited = Boolean.parseBoolean(s); 
		}
		
	}
	
	private void fillBuffer() throws IOException {
		if (m_reader == null) {
			// payload was provided as string
			m_position = -1;
			m_size = -1;
			return;
		}

		m_position = 0;
		m_size = m_reader.read(m_buf);

		if (m_size == 0 || m_size == -1) {
			m_position = -1;
			m_size = -1;
		}
	}

	private char readPlainCharacter() throws IOException {
		char ch = m_unreadBuffer[0];
		if (m_unreadBuffer[0] != NO_CHAR) {
			m_unreadBuffer[0] = m_unreadBuffer[1];
			m_unreadBuffer[1] = NO_CHAR;
			return ch;
		}

		if (m_position == m_size) {
			fillBuffer();
			if (m_size == -1) {
				return NO_CHAR;
			}
		}

		return m_buf[m_position++];
	}

	private char read() throws IOException {
		char c = readPlainCharacter();

		// Decoder '&' 
		if (c != '%') {
			return c;
		}

		char ch1 = readPlainCharacter();
		char ch2 = readPlainCharacter();

		if (ch1 == '2' && ch2 == '6') {

			m_decoded = true;
			return '&';
		}

		if (ch1 == '2' && ch2 == '5') {

			m_decoded = true;
			return '%';
		}

		m_unreadBuffer[0] = ch1;
		m_unreadBuffer[1] = ch2;
		return c;
	}

	public boolean parseLine() throws XMLStreamException {
		m_pathLen = -1;

		// parse line
		// if it's end of file, return false
		// if it's namespace prefix, remember that prefix and parse next line
		// if it's normal line, parse it completely

		try {
			int pathLen = 0;
			int pathDataBuffPos = 0;
			boolean isInName = true;
			boolean isInIndex = false;
			boolean isInValue = false;
			char indexLeftBracket = 0;
			int lastDataStart = 0;
			int lastDataChecksum = 0;
			boolean hasElemData = false;
			NVElementHolder currElem = getPathElem(0);
			char[] pathDataBuffer = m_pathDataBuffer;
			int pathDataBufferLen = pathDataBuffer.length;

			m_value.setLength(0);

			while (true) {
				char c = (char) read();

				// keep this most-executed block close to the beginning to shorten jumps
				if (isInValue) {
					if ( (m_decoded || c != '&') && c != NO_CHAR) {
						m_decoded = false;
						// this is not a delimiter, add to buffer
						m_value.append(c);
						continue;
					}
				} else {
					m_decoded = false;
					
					if (DELIMITERS[c & 0x7F] != c) {
						// this is not a delimiter, add to buffer
						if (pathDataBuffPos >= pathDataBufferLen) {
							expandPathDataBuffer();
							pathDataBuffer = m_pathDataBuffer;
							pathDataBufferLen = pathDataBuffer.length;
						}

						pathDataBuffer[pathDataBuffPos++] = c;
						lastDataChecksum ^= c;
						hasElemData = true;
						continue;
					}
				}

				if (isInValue) {
					// this is the end of the value
					NVElementHolder firstElem = m_path[0];
					if (firstElem.m_prefixChecksum == NSPREFIX_DEF_CHECKSUM &&
						isSameText(firstElem.m_prefix, firstElem.m_prefixStart,
							firstElem.m_prefixLength, NSPREFIX_DEF))
					{
						// this was NS prefix definition
						if (pathLen != 1) {
							throw new XMLStreamException(
								"NS Prefix cannot have multple names in the path");
						}

/* Now that, extra NV pair for header and other usage can get in front of the real NV payload 
 * because we don't honor the REST_PAYLOAD delimiter anymore.  We should not throw this exception.
 * 						if (m_hadDataLines) {
							throw new XMLStreamException(
								"NS Prefix cannot be added after data elements");
						}
*/
						updateValuePos();
						addNsPrefixDef(firstElem);
					} else {
						// finished reading a line

						updateValuePos();

//						m_hadDataLines = true;
						m_pathLen = pathLen;
						validateLine();
						return true;
					}

					// continue as if there was no this line
					pathLen = 0;
					pathDataBuffPos = 0;
					lastDataStart = 0;
					lastDataChecksum = 0;
					isInValue = false;
					isInName = true;
					hasElemData = false;
					currElem = getPathElem(0);
					m_value.setLength(0);
					continue;
				}

				if (c == '=' || c == '.') {
					if (isInIndex) {
						throw new XMLStreamException("Unexpected symbol '" + c +
							"' found inside element indexing data");
					}

					if (isInName) {
						// end of path element name
						if (lastDataStart >= pathDataBuffPos) {
							throw new XMLStreamException("Unexpected empty element name");
						}

						currElem.m_elemName = pathDataBuffer;
						currElem.m_elemNameStart = lastDataStart;
						currElem.m_elemNameLength = pathDataBuffPos - lastDataStart;
						currElem.m_elemNameChecksum = lastDataChecksum;
						lastDataStart = pathDataBuffPos;
						lastDataChecksum = 0;

						adjustAttributeName(currElem);
					}

					pathLen++;

					if (c == '.') {
						isInName = true;
						currElem = getPathElem(pathLen);
						continue;
					}

					// move into the value mode
					isInName = false;
					isInValue = true;
					continue;
				}

				if (c == NO_CHAR || c == '&') {
					if (isInIndex) {
						throw new XMLStreamException(
							"Indexing data has terminated unexpectedly with the end of stream");
					}

					if (hasElemData) {
						throw new XMLStreamException(
							"Name data has terminated unexpectedly with the end of line");
					}

					if (c == NO_CHAR) {
						return false;
					}

					// continue as if there was no this line
					pathLen = 0;
					pathDataBuffPos = 0;
					lastDataStart = 0;
					lastDataChecksum = 0;
					isInValue = false;
					isInName = true;
					hasElemData = false;
					currElem = getPathElem(0);
					m_value.setLength(0);
					continue;
				}

				if (c == ':') {
					if (isInIndex) {
						throw new XMLStreamException("Unexpected symbol '" + c +
							"' found inside element indexing data");
					}

					if (!isInName) {
						throw new XMLStreamException("Namespace prefix is not expected after element name");
					}

					if (lastDataStart >= pathDataBuffPos) {
						throw new XMLStreamException("Unexpected empty namespace prefix data");
					}

					currElem.m_prefix = pathDataBuffer;
					currElem.m_prefixStart = lastDataStart;
					currElem.m_prefixLength = pathDataBuffPos - lastDataStart;
					currElem.m_prefixChecksum = lastDataChecksum;
					lastDataStart = pathDataBuffPos;
					lastDataChecksum = 0;
					continue;
				}

				if (c == '[' || c == '(') {
					if (isInIndex) {
						throw new XMLStreamException("Unexpected symbol '" + c +
							"' found inside element indexing data");
					}

					if (!isInName) {
						throw new XMLStreamException("Unexpected symbol '" + c +
							"' found outside of element name");
					}

					if (lastDataStart >= pathDataBuffPos) {
						throw new XMLStreamException("Unexpected empty element name");
					}

					currElem.m_elemName = pathDataBuffer;
					currElem.m_elemNameStart = lastDataStart;
					currElem.m_elemNameLength = pathDataBuffPos - lastDataStart;
					currElem.m_elemNameChecksum = lastDataChecksum;
					lastDataStart = pathDataBuffPos;
					lastDataChecksum = 0;

					adjustAttributeName(currElem);

					isInName = false;
					isInIndex = true;
					indexLeftBracket = c;
					continue;
				}

				if (c == ']' || c == ')') {
					if (!isInIndex ||
						(c == ']' && indexLeftBracket != '[') ||
						(c == ')' && indexLeftBracket != '('))
					{
						throw new XMLStreamException("Unexpected symbol '" + c +
							"' found outside element indexing data");
					}

					if (isInName) {
						throw new XMLStreamException("Unexpected symbol '" + c +
							"' found inside of element name");
					}

					if (lastDataStart >= pathDataBuffPos) {
						throw new XMLStreamException("Unexpected empty indexing data");
					}

					isInIndex = false;
					currElem.m_index = parseIndex(lastDataStart, pathDataBuffPos);
					lastDataStart = pathDataBuffPos;
					lastDataChecksum = 0;
					continue;
				}

				throw new XMLStreamException("Unexpected delimiter '" + c + "' found");
			}
		} catch (IOException ioe) {
			throw new XMLStreamException(ioe);
		}
	}

	public String getNsUriByPrefix(String prefix) {
		return m_convention.getNamespaceURI(prefix);
	}

	public String getNsUriForElementHolder(NVStreamParser.NVElementHolder holder,
		boolean allowNsDefaults, String impliedRootNs)
		throws XMLStreamException
	{
		if (holder.m_prefixLength == 0) {
			if (allowNsDefaults) {
				if (m_singleNamespace != null) {
					return m_singleNamespace;
				}

				String singleNamespace = m_convention.getSingleNamespace();
				if (singleNamespace != null) {
					return singleNamespace;
				}

				if (impliedRootNs != null) {
					return impliedRootNs;
				}
			}

			return "";
		}

		String prefix = new String(holder.m_prefix,
			holder.m_prefixStart, holder.m_prefixLength);

		String nsUri = getNsUriByPrefix(prefix);
		if (nsUri == null) {
			throw new XMLStreamException("Undefined namespace prefix " + prefix);
		}

		return nsUri;
	}

	public NVPathPart buildPathPart(NVStreamParser.NVElementHolder holder,
		boolean allowNsDefaults, String impliedRootNs)
		throws XMLStreamException
	{
		String nsUri = getNsUriForElementHolder(holder, allowNsDefaults, impliedRootNs);

		String elemName = new String(holder.m_elemName,
			holder.m_elemNameStart, holder.m_elemNameLength);

		return new NVPathPart(nsUri, elemName, holder.m_index,
			holder.m_isAttribute, holder.m_elemNameChecksum);
	}

	private NVElementHolder getPathElem(int pathPos) {
		if (pathPos >= m_path.length) {
			NVElementHolder[] tmp = new NVElementHolder[m_path.length * 2];
			System.arraycopy(m_path, 0, tmp, 0, m_path.length);
			m_path = tmp;
		}

		NVElementHolder result = m_path[pathPos];
		if (result == null) {
			result = new NVElementHolder();
			m_path[pathPos] = result;
		} else {
			result.m_elemName = null;
			result.m_elemNameStart = 0;
			result.m_elemNameLength = 0;
			result.m_elemNameChecksum = 0;

			result.m_prefix = null;
			result.m_prefixStart = 0;
			result.m_prefixLength = 0;
			result.m_prefixChecksum = 0;

			result.m_isAttribute = false;

			result.m_index = 0;
		}

		return result;
	}

	private void adjustAttributeName(NVElementHolder elem) throws XMLStreamException {
		if (elem.m_elemName[elem.m_elemNameStart] != '@') {
			return;
		}

		elem.m_elemNameStart++;
		elem.m_elemNameLength--;
		elem.m_isAttribute = true;

		if (elem.m_elemNameLength == 0) {
			throw new XMLStreamException("Unexpected empty element attribute name");
		}
	}

	private int parseIndex(int start, int endPos) throws XMLStreamException {
		int result = 0;
		for (int i=start; i<endPos; i++) {
			char c = m_pathDataBuffer[i];
			if (c < '0' || c > '9') {
				throw new XMLStreamException("Nun-numeric index contains symbol '" + c + "'");
			}

			int currDigit = c - '0';
			result = result * 10 + currDigit;
		}
		return result;
	}

	private void expandPathDataBuffer() {
		char[] tmp = new char[m_pathDataBuffer.length * 2];
		System.arraycopy(m_pathDataBuffer, 0, tmp, 0, m_pathDataBuffer.length);
		m_pathDataBuffer = tmp;

		// replace all old pointers to free up unused memory
		for (int i=0; i<m_path.length; i++) {
			NVElementHolder holder = m_path[i];
			if (holder == null) {
				continue;
			}

			if (holder.m_prefix != null) {
				holder.m_prefix = m_pathDataBuffer;
			}

			if (holder.m_elemName != null) {
				holder.m_elemName = m_pathDataBuffer;
			}
		}
	}

	private void validateLine() throws XMLStreamException {
		boolean hasAttribute = false;
		for (int i=0; i<m_pathLen; i++) {
			if (hasAttribute) {
				throw new XMLStreamException("Attribute cannot contain child element");
			}

			NVElementHolder holder = m_path[i];
			hasAttribute = holder.m_isAttribute;

			if (holder.m_prefixLength > 0) {
				validateName(holder.m_prefix, holder.m_prefixStart, holder.m_prefixLength);
			}

			validateName(holder.m_elemName, holder.m_elemNameStart, holder.m_elemNameLength);
		}
	}

	private void validateName(char[] data, int start, int len) throws XMLStreamException {
		int endPos = start + len;
		for (int i=start; i<endPos; i++) {
			char c = data[i];
			if (VALID_CHARS[c & 0x7F] != c) {
				throw new XMLStreamException("Invalid symbol '" + c + "' found for element in NV line");
			}
		}
	}

	private void addNsPrefixDef(NVElementHolder elem) throws XMLStreamException {
		String prefix = new String(elem.m_elemName,
			elem.m_elemNameStart, elem.m_elemNameLength);
		String nsUri = getValueInternal();

		m_convention.addMapping(prefix, nsUri);

		m_nsCount++;
		if (m_nsCount == 1) {
			m_singleNamespace = nsUri;
		} else {
			m_singleNamespace = null;
		}
	}

	private void updateValuePos() throws XMLStreamException {
		int len = m_value.length();
		if (m_doubleQuoteDelimited && len > 2 && m_value.charAt(0) == '"') {
			if (m_value.charAt(len - 1) != '"') {
				throw new XMLStreamException(
					"Value starts with quotation mark, but ends without the same");
			}

			m_valueStart = 1;
			m_valueEnd = len - 1;
		} else {
			m_valueStart = 0;
			m_valueEnd = len;
		}
	}

	private String getValueInternal() {
/*		String finalSting = m_value.substring(m_valueStart, m_valueEnd);
		try {
			
			return new String(finalSting.getBytes("8859_1"), m_charset.displayName());
		} catch (Exception e) {
			return finalSting;
		}
*/
		return m_value.substring(m_valueStart, m_valueEnd);
	}

	private boolean isSameText(char[] data, int start, int length, char[] other) {
		if (length != other.length) {
			return false;
		}

		for (int i=0; i<length; i++) {
			if (data[start + i] != other[i]) {
				return false;
			}
		}

		return true;
	}

	public int getElementPathLen() {
		if (m_pathLen == -1) {
			throw new IllegalStateException("No NV line parsed");
		}

		return m_pathLen;
	}

	public NVElementHolder[] getElementPath() {
		if (m_pathLen == -1) {
			throw new IllegalStateException("No NV line parsed");
		}

		return m_path;
	}

	public String getValue() {
		if (m_pathLen == -1) {
			throw new IllegalStateException("No NV line parsed");
		}

		return getValueInternal();
	}

	static {
		NSPREFIX_DEF = NVConstants.NV_NAMESPACE_DEF_PREFIX.toCharArray();
		NSPREFIX_DEF_CHECKSUM = NVPathPart.calcChecksum(NVConstants.NV_NAMESPACE_DEF_PREFIX);

		for (int i=0; i<DELIMITERS.length; i++) {
			DELIMITERS[i] = NO_CHAR;
		}

		DELIMITERS[':'] = ':';
		DELIMITERS['.'] = '.';
		DELIMITERS['('] = '(';
		DELIMITERS[')'] = ')';
		DELIMITERS['['] = '[';
		DELIMITERS[']'] = ']';
		DELIMITERS['='] = '=';
		DELIMITERS['&'] = '&';

		for (int i=0; i<VALID_CHARS.length; i++) {
			VALID_CHARS[i] = NO_CHAR;
		}

		for (char i='0'; i<='9'; i++) {
			VALID_CHARS[i] = i;
		}
		for (char i='a'; i<='z'; i++) {
			VALID_CHARS[i] = i;
		}
		for (char i='A'; i<='Z'; i++) {
			VALID_CHARS[i] = i;
		}
		VALID_CHARS['_'] = '_';
		VALID_CHARS['-'] = '-';
	}

	public static class NVElementHolder {
		public char[] m_elemName;
		public int m_elemNameStart;
		public int m_elemNameLength;
		public int m_elemNameChecksum;

		public char[] m_prefix;
		public int m_prefixStart;
		public int m_prefixLength;
		public int m_prefixChecksum;

		public boolean m_isAttribute;

		public int m_index;

		public String buildPrefixText() {
			if (m_prefixLength == 0) {
				return null;
			}

			return new String(m_prefix, m_prefixStart, m_prefixLength);
		}

		public String buildElemNameText() {
			return new String(m_elemName, m_elemNameStart, m_elemNameLength);
		}
	}
}
