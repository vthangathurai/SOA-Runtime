/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.nv;

import javax.xml.stream.XMLStreamException;

/**
 * @author ichernyshev, wdeng
 */
public final class NVLine {
	private final NVPathPart[] m_line;
	private final int m_lineLen;
	private final String m_value;
	private final int m_valleyLevel;
	private final boolean m_isSameLevelAttribute;

	public NVLine(NVPathPart[] line, int lineLen, String value,
		int valleyLevel, boolean isSameLevelAttribute)
	{
		m_line = line;
		m_lineLen = lineLen;
		m_value = value;
		m_valleyLevel = valleyLevel;
		m_isSameLevelAttribute = isSameLevelAttribute;
	}

	public NVPathPart peek() {
		return getPathPart(m_lineLen-1);
	}

	public String getValue() {
		return m_value;
	}

	public int getDepth() {
		return m_lineLen;
	}

	public NVPathPart getPathPart(int i) {
		if (i < 0 || i >= m_lineLen) {
			throw new IllegalArgumentException("Line element index " + i +
				" is out of range within length of " + m_lineLen);
		}

		return m_line[i];
	}

	public int getValleyLevel() {
		return m_valleyLevel;
	}

	public boolean isAttribute() {
		if (m_lineLen == 0) {
			return false;
		}

		NVPathPart part = peek();
		return part.isAttribute();
	}

	public boolean isAttributeAtSameLevel() {
		return m_isSameLevelAttribute;
	}

	public static NVLine createEmpty() {
		return new NVLine(null, 0, null, -1, false);
	}

	public static NVLine createNext(NVStreamParser parser,
		NVLine aboveLine, NVLine reuse, NVPathPart impliedRoot)
		throws XMLStreamException
	{
		int pathLen = parser.getElementPathLen();
		NVStreamParser.NVElementHolder[] holders = parser.getElementPath();
		String value = parser.getValue();

		// try to reuse or create an array
		NVPathPart[] line;
		int minCapacity = pathLen + 1;
		if (reuse != null && reuse.m_line != null && reuse.m_line.length >= minCapacity) {
			line = reuse.m_line;
		} else {
			line = new NVPathPart[minCapacity + 10];
		}

		int aboveLineDepth = 0;
		if (aboveLine != null) {
			aboveLineDepth = aboveLine.getDepth();
		}

		int lineLen = 0;
		int valleyIndex = -1;

		// add implied root if necessary
		if (impliedRoot != null) {
			line[0] = impliedRoot;
			valleyIndex = 0;
			lineLen = 1;
		}

		boolean hasDifferenNames = false;
		for (int i=0; i<pathLen; i++) {
			NVStreamParser.NVElementHolder holder = holders[i];

			if (i == 0 && impliedRoot != null &&
				isSameElementNameAndFlags(holder, impliedRoot))
			{
				String nsUri = parser.getNsUriForElementHolder(holder,
					true, impliedRoot.getNamespaceURI());
				if (nsUri.equals(impliedRoot.getNamespaceURI()))
				{
					// we've added implied root already
					continue;
				}
			}

			// TODO: try to avoid comparing strings if we already had a difference

			// check whether we have any previous data in the line above
			if (lineLen >= aboveLineDepth) {
				line[lineLen++] = parser.buildPathPart(holder, lineLen == 0, null);
				hasDifferenNames = true;
				continue;
			}

			// check whether name looks similar and we're at the same index
			NVPathPart abovePathPart = aboveLine.getPathPart(lineLen);
			if (!isSameElementNameAndFlags(holder, abovePathPart)) {
				line[lineLen++] = parser.buildPathPart(holder, lineLen == 0, null);
				hasDifferenNames = true;
				continue;
			}

			// now check that the namespaces match;
			// by now, we're pretty confident that it's the same name

			String nsUri = parser.getNsUriForElementHolder(holder, lineLen == 0, null);
			if (!nsUri.equals(abovePathPart.getNamespaceURI())) {
				line[lineLen++] = new NVPathPart(nsUri, abovePathPart.getLocalPart(),
					holder.m_index, holder.m_isAttribute, holder.m_elemNameChecksum);
				hasDifferenNames = true;
				continue;
			}

			// everything mathces, reuse the path part and update valley
			if (!hasDifferenNames) {
				valleyIndex = lineLen;
			}

			line[lineLen++] = abovePathPart;
		}

		boolean isSameLevelAttribute = line[lineLen-1].isAttribute() && valleyIndex >= lineLen-2;

		return new NVLine(line, lineLen, value, valleyIndex, isSameLevelAttribute);
	}

	private static boolean isSameElementNameAndFlags(NVStreamParser.NVElementHolder holder,
		NVPathPart otherPathPart)
	{
		String otherPathPartLocalName = otherPathPart.getLocalPart();
		int elemNameLength = holder.m_elemNameLength;
		if (otherPathPart.getLocalPartChecksum() != holder.m_elemNameChecksum ||
			otherPathPartLocalName.length() != elemNameLength ||
			otherPathPart.getIndex() != holder.m_index ||
			otherPathPart.isAttribute() != holder.m_isAttribute)
		{
			return false;
		}

		// check whether name is a actually the same
		char[] elemNameData = holder.m_elemName;
		int elemNameStart = holder.m_elemNameStart;
		for (int i=0; i<elemNameLength; i++) {
			if (elemNameData[elemNameStart + i] != otherPathPartLocalName.charAt(i)) {
				return false;
			}
		}

		return true;
	}
}
