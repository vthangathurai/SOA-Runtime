/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.types;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;


/**
 * 
 * Various string constants defined/processed by the framework. Mostly these are transport
 * headers that are specific SOA framework.
 * @author ichernyshev
 */
public final class SOAHeaders {

	/**
	 * Prefix used by all SOA headers.
	 */
	public static final String SYS_PREFIX = "X-TURMERIC-";

	/**
	 * Request data format.
	 */
	public static final String REQUEST_DATA_FORMAT = SYS_PREFIX + "REQUEST-DATA-FORMAT";

	/**
	 * Response data format.
	 */
	public static final String RESPONSE_DATA_FORMAT = SYS_PREFIX + "RESPONSE-DATA-FORMAT";

	/**
	 * Message protocol (SOAP, etc.).
	 */
	public static final String MESSAGE_PROTOCOL = SYS_PREFIX + "MESSAGE-PROTOCOL";

	/**
	 * Whether to preserve element ordering (a REST property).
	 */
	public static final String ELEMENT_ORDERING_PRESERVE = SYS_PREFIX + "ELEMENT-ORDERING-PRESERVE";

	/**
	 * Service operation name.
	 */
	public static final String SERVICE_OPERATION_NAME = SYS_PREFIX + "OPERATION-NAME";

	/**
	 * Service qname.
	 */
	public static final String SERVICE_NAME = SYS_PREFIX + "SERVICE-NAME";

	/**
	 * Message encoding (character set).
	 */
	public static final String MESSAGE_ENCODING = SYS_PREFIX + "MESSAGE-ENCODING";

	/**
	 * Indicates that the current response message is an error.
	 */
	public static final String ERROR_RESPONSE = SYS_PREFIX + "ERROR-RESPONSE";

	/**
	 * List of globalization locale choices (in client request) or single globalization
	 * locale selection (in server response).
	 */
	public static final String LOCALE_LIST = SYS_PREFIX + "LOCALE-LIST";

	/**
	 * Global ID for this request/response.
	 */
	public static final String GLOBAL_ID = SYS_PREFIX + "GLOBAL-ID";

	/**
	 * Service version in which client (in requests) or server (in responses) is operating.
	 */
	public static final String VERSION = SYS_PREFIX + "SERVICE-VERSION";

	/**
	 * Full request identifier including chaining path information.
	 */
	public static final String REQUEST_ID = SYS_PREFIX + "REQUEST-ID";

	/**
	 * Globally unique ID for the request - the uniqueness portion of the request ID.
	 */
	public static final String REQUEST_GUID = SYS_PREFIX + "REQUEST-GUID";

	/**
	 * Use case - sent by clients to identify a particular client business use case (e.g. SYI) for
	 * reporting purposes
	 */
	public static final String USECASE_NAME = SYS_PREFIX + "USECASE-NAME";
	
	/**
	 * ConsumerId - sent by consumers to identify a particular client business for
	 * reporting purposes.
	 */
	public static final String CONSUMER_ID = SYS_PREFIX + "CONSUMER-ID";


	/**
	 * Used in client requests to select a particular response transport to be used
	 * by the server.
	 */
	public static final String RESPONSE_TRANSPORT = SYS_PREFIX + "RESPONSE-TRANSPORT";
	

	/**
	 * Used in client requests to set the schema validation level to be used
	 * by the server.
	 */
	public static final String REQ_PAYLOAD_VALIDATION_LEVEL = SYS_PREFIX + "REQ-PAYLOAD-VALIDATION-LEVEL";

	/**
	 * Used to abbreviate the naming path in name-value requests; indicates that
	 * all names should be considered to begin with the specified prefix.
	 */
	public static final String NV_IMPLIED_ROOT = SYS_PREFIX + "NV-IMPLIED-ROOT";
	 

	/**
	 * Marks end of headers and beginning of the payload in REST requests.
	 *
	 * This is NOT used as a real transport header.
	 */
	public static final String REST_PAYLOAD = SYS_PREFIX + "REST-PAYLOAD";

	/**
	 * Http header name for the alternate fault status. By default, when reporting Turmeric
	 * runtime error, we set the HTTP response status code to 500.  This header is sent
	 * by client to indicate that response code server should use to report error. 
	 */
	public static final String ALTERNATE_FAULT_STATUS = SYS_PREFIX + "ALTERNATE-FAULT-STATUS";
	
	/**
	 * Http header name for client side to provide pool information the client is calling
	 * from.
	 */
	public static final String CALLING_POOL  = SYS_PREFIX + "CALLING-POOL";

	private static Set<String> s_caseSensitiveHeaders = new HashSet<String>();

	private static Set<String> s_soaHeaders = new HashSet<String>();

	/**
	 * Http header name for authentication token header.
	 */
	public static final String AUTH_TOKEN = SYS_PREFIX + "SECURITY-TOKEN";
	/**
	 * Http header name for user id token header.
	 */
	public static final String AUTH_USERID = SYS_PREFIX + "SECURITY-USERID";
	/**
	 * Http header name for user password token header.
	 */
	public static final String AUTH_PASSWORD = SYS_PREFIX + "SECURITY-PASSWORD";
	
	/**
	 * Http header name for security cookie.
	 */
	public static final String AUTH_COOKIE = SYS_PREFIX + "SECURITY-COOKIE";

	static {
		Field[] fields = SOAHeaders.class.getFields();
		for (int i=0; i<fields.length; i++) {
			Field field = fields[i];
			int modifier = field.getModifiers();
			if (!String.class.equals(field.getType()) ||
				!Modifier.isFinal(modifier) ||
				!Modifier.isStatic(modifier) ||
				!Modifier.isPublic(modifier))
			{
				continue;
			}

			String fieldName = field.getName();
			if (fieldName.equals("SYS_PREFIX")) {
				continue;
			}

			String fieldValue;
			try {
				fieldValue = (String) field.get(null);
				if (fieldValue.startsWith(SYS_PREFIX)) {
					s_soaHeaders.add(fieldValue);
				}
			} catch (Throwable e) {
				LogManager.getInstance(SOAHeaders.class).log(Level.SEVERE,
					"Unale access field " + fieldName + " in SOAHeaders: " + e.toString(), e);
			}
		}
	}

	private SOAHeaders() {
		// no instances
	}

	/**
	 * Returns true if this header is an SOA header.  (SOA headers start with the X-TURMERIC prefix.)
	 * @param header the header name to check
	 * @return true if this is an SOA header
	 */
	public static boolean isSOAHeader(String header) {
		return s_soaHeaders.contains(normalizeName(header, true));
	}

	/**
	 * Normalize the name of headers that are considered case-insensitive within the SOA architecture.
	 * Only service name, operation name, request ID, use case name, locale list, and global ID are
	 * considered case sensitive.  [Note: HTTP headers are already considered case insensitive according
	 * to the HTTP standard.]
	 * @param name the header name
	 * @param isCaseInsensitive whether this header is case insensitive (always true for SOA headers)
	 * @return the normalized name
	 */
	public static String normalizeName(String name, boolean isCaseInsensitive) {
		String nameUC = name.toUpperCase();
		if (isCaseInsensitive || isSOAHeader(name)) {
			return nameUC;
		}

		return name;
	}

	/**
	 * Normalize the case of header values that are considered case-insensitive within the SOA architecture.
	 * All headers are considered case insensitive except service name, operation name, request ID,
	 * use case name, locale list, and global ID.
	 * @param name the name of the header
	 * @param value the value to be adjusted
	 * @return the normalized value
	 */
	public static String normalizeValue(String name, String value) {
		if (value == null) {
			return value;
		}

		if (name.toUpperCase().contains("-SECURITY-"))
			return value;
		
		if (!isSOAHeader(name)) {
			// do not normalize unknown headers
			return value;
		}

		if (s_caseSensitiveHeaders.contains(name)) {
			// do not normalize case-sensitive headers
			return value;
		}

		return value.toUpperCase();
	}

	/**
	 * Returns true if this SOA header is case sensitive.
	 * @param name name of the header
	 * @return true if case sensitive
	 */
	public static boolean isCaseSensitive(String name) {
		return s_caseSensitiveHeaders.contains(name);
	}

	static {
		s_caseSensitiveHeaders.add(SERVICE_NAME);
		s_caseSensitiveHeaders.add(SERVICE_OPERATION_NAME);
		s_caseSensitiveHeaders.add(REQUEST_ID);
		s_caseSensitiveHeaders.add(USECASE_NAME);
		s_caseSensitiveHeaders.add(LOCALE_LIST);
		s_caseSensitiveHeaders.add(GLOBAL_ID);
		s_caseSensitiveHeaders.add(REQUEST_GUID);
	
		s_caseSensitiveHeaders.add(CALLING_POOL);
		s_caseSensitiveHeaders.add(CONSUMER_ID);
	}
}
