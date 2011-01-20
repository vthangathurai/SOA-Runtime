/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.types;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

/**
 * This class represents globalization options requested by the client and processed, validated and finalized by the
 * service.  G11nOptions is present in both client and server side message context.  The server-side G11N handler
 * examines the requesting client globalization options, and sets the negotiated locale and desired character set and global ID,
 * into the response context.
 * @author ichernyshev, smalladi
 */
public class G11nOptions {

	/**
	 * UTF-8 is the default Charset.
	 */
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private final List<String> m_locales;
	private final String m_globalId;
	private final Charset m_charset;

	/**
	 * Default constructor; constructs globalization options with the default character set (UTF-8) and no global ID or
	 * locale information.
	 */
	public G11nOptions() {
		this(DEFAULT_CHARSET);
	}

	/**
	 * Constructs globalization options with the specified character set and no global ID or
	 * locale information.
	 * @param charset the Java representation of the desired character set
	 */
	public G11nOptions(Charset charset) {
		this(charset, null, null);
	}

	/**
	 * Constructs globalization options with the specified character set and locales (which are should be available for the
	 * default global ID as set up in the global registry).
	 * @param charset the Java representation of the desired character set
	 * @param locales the list of locale names
	 */
	public G11nOptions(Charset charset, List<String> locales) {
		this(charset, locales, null);
	}

	/**
	 * Constructs globalization options with the specified character set, locales, and global ID.  The locales should be
	 * available for the specified global ID as set up in the global registry.
	 * @param charset the Java representation of the desired character set
	 * @param locales the list of locale names
	 * @param globalId the global ID
	 */
	public G11nOptions(Charset charset, List<String> locales, String globalId) {
		if (charset == null) {
			throw new NullPointerException();
		}

		m_charset = charset;

		if (locales != null) {
			m_locales = Collections.unmodifiableList(locales);
		} else {
			m_locales = null;
		}

		m_globalId = globalId;
	}

	/**
	 * Returns the list of locales.
	 * @return the list of locales
	 */
	public List<String> getLocales() {
		return m_locales;
	}

	/**
	 * Returns the global ID.
	 * @return the global ID
	 */
	public String getGlobalId() {
		return m_globalId;
	}

	/**
	 * Returns the character set.
	 * @return the Java representation of the character set
	 */
	public Charset getCharset() {
		return m_charset;
	}

	/**
	 * Merges two G11nOptions structures, the "override" userOptions structure from a ServiceInvokerOptions, and the
	 * "fallback" configOptions structure from the configuration.  Any options that are null in the user options will defer
	 * to the configuration.
	 * @param userOptions the the "override" userOptions structure from a ServiceInvokerOptions
	 * @param configOptions the "fallback" configOptions structure from the configuration
	 * @return the new merged G11nOptions structure
	 */
	static public G11nOptions mergeFallbackOptions(G11nOptions userOptions, G11nOptions configOptions) {
		if (userOptions == null) {
			return configOptions;
		}
		// TODO - character set can never be delegated to configuration, because it can't be null in the
		// ServiceInvokerOptions copy (it can never be null)
		return new G11nOptions(userOptions.getCharset() == null? configOptions.getCharset() : userOptions.getCharset(),
								userOptions.getLocales() == null? configOptions.getLocales() : userOptions.getLocales(),
								userOptions.getGlobalId() == null? configOptions.getGlobalId() : userOptions.getGlobalId());
	}
}
