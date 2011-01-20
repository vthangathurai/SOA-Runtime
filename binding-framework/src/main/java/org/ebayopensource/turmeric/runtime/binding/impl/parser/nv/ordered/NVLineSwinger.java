/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.ordered;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVLine;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVPathPart;


/**
 * This class swings from a NV line full name's right most name down to the left
 * most name that is different from that of the next NV line full name in the
 * incoming NV payload. Then it swings up this next full name up to its right
 * most name. It is responsible for generating XML stream events alone the way.
 * 
 * @author wdeng, ichernyshev
 */
final class NVLineSwinger {
	private final NVLine m_currentLine;
	private final NVLine m_previousLine;
	private final int m_valleyLevel;
	private int m_currentLevel;
	private boolean m_passedValley;
	private NVPathPart m_currentName;

	/**
	 * Both currentLine and the previousLine must be non-null.
	 * 
	 * @param currentLine
	 * @param previousLine
	 */
	public NVLineSwinger(NVLine currentLine, NVLine previousLine) {
		m_currentLine = currentLine;
		m_previousLine = previousLine;
		m_valleyLevel = m_currentLine.getValleyLevel();

		m_currentLevel = previousLine.getDepth();
		m_passedValley = false;
	}

	public NVLine getCurrentLine() {
		return m_currentLine;
	}

	public int getCurrentLevel() {
		return m_currentLevel;
	}

	public NVLine getPreviousLine() {
		return m_previousLine;
	}

	public String getCurrentValue() {
		return m_currentLine.getValue();
	}

	public boolean passedValley() {
		return m_passedValley;
	}

	/**
	 * Returns the next name along the swinging path and records where we are
	 * along the swing path. Returns null when it gets to the next top.
	 * 
	 * @return
	 */
	public QName getNextName() {
		if (!m_passedValley) {
			if (m_currentLevel == 0) {
				m_currentLevel--;
				m_passedValley = true;
			} else {
				m_currentLevel--;
				if (m_valleyLevel != -1) {
					m_passedValley = m_currentLevel == m_valleyLevel;
				}
			}
		}

		if (m_passedValley) {
			m_currentLevel++;
			if (m_currentLevel == m_currentLine.getDepth()) {
				return null;
			}
		}

		m_currentName = m_passedValley ? m_currentLine.getPathPart(m_currentLevel) :
			m_previousLine.getPathPart(m_currentLevel);
		return m_currentName;
	}

	public QName getQName() {
		return m_currentName;
	}

	/**
	 * Moves the swinger all the way to the up swing.
	 * 
	 * @return the QName at the top.
	 */
	public QName swingToLevel(int level) {
		m_passedValley = true;
		m_currentName = m_currentLine.getPathPart(level);
		m_currentLevel = level;
		return m_currentName;
	}

	public boolean isAttributeLevelElement() {
		if (!m_passedValley) {
			// backward direction has not attributes
			return false;
		}

		if (m_currentLevel + 1 != m_currentLine.getDepth()) {
			return false;
		}

		return !m_currentLine.isAttribute();
	}

	public boolean shouldAddAsAttribute() {
		if (!m_passedValley) {
			// backward direction has not attributes
			return false;
		}

		if (m_currentLevel + 2 != m_currentLine.getDepth()) {
			return false;
		}

		return m_currentLine.isAttribute();
	}
}
