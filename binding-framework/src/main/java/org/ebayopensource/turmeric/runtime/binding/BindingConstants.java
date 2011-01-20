/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding;

import javax.xml.namespace.QName;

/**
 * This class defines the common constants used by the Binding Framework.
 * 
 * @author wdeng
 */
public final class BindingConstants {

	/**
	 * Refers to the namespace for SOA common types, such as ErrorMessage,
	 * BaseAttachmentType, etc.
	 */
	public final static String SOA_TYPES_NAMESPACE = "http://www.ebayopensource.org/turmeric/common/v1/types";

	/**
	 * Refers to the attribute mark symbol.
	 */
	public static final String ATTRIBUTE_MARK = "@";

	/**
	 * Refers to the namespace prefix for XML schema instance.
	 */
	public final static String XMLSCHEMA_INSTANCE_PREFIX = "xsi";

	/**
	 * Refers to the URI for XML schema instance.
	 */
	public final static String XMLSCHEMA_INSTANCE_URI = "http://www.w3.org/2001/XMLSchema-instance";

	/**
	 * Refers to the namespace prefix for XML schema.
	 */
	public final static String XMLSCHEMA_PREFIX = "xs";

	/**
	 * Refers to the URI for XML schema.
	 */
	public final static String XMLSCHEMA_URI = "http://www.w3.org/2001/XMLSchema";

	/**
	 * Refers to the namespace prefix for SOA Types.
	 */
	public final static String SOA_TYPES_PREFIX = "ms";

	/**
	 * Refers to the default namespace prefix.
	 */
	public final static String DEFAULT_PREFIX = "ns";

	/**
	 * Refers to the Second Prefix.
	 */
	public final static String SECOND_PREFIX = DEFAULT_PREFIX + "2";
	
	/**
	 * Refers to the name of XML Schema's nillable attribute.
	 */
	public final static String NILLABLE_ATTRIBUTE_NAME = "nil";

	/**
	 * Refers to the QName for nillable attribute.
	 */
	public final static QName NILLABLE_ATTRIBUTE_QNAME = 
		new QName(XMLSCHEMA_INSTANCE_URI, NILLABLE_ATTRIBUTE_NAME, XMLSCHEMA_INSTANCE_PREFIX);
	
	/**
	 * Refers to the String of null value.
	 */
	public final static String NULL_VALUE_STR = "null";
	
	/**
	 * Refers to the String with JSON value key. This key is used to represent in JSON 
	 * the xml element that contains contents with attribute. For example,
	 * <code>
	 *     <Size unit="meter">100</Size>
	 * </code>
	 * is represented by 
	 * <code>
	 *     {Size : {@unit="meter",
	 *              __value__=100}
	 *     }
	 * </code>
	 */
	public final static String JSON_VALUE_KEY = "__value__";

	/**
	 * Refers to the Unbounded value = -1.  This is use to represend XML Schema element 
	 * definition's unbounded attribute value in momory.
	 */
	public final static int UNBOUNDED = -1;

	/**
	 * Refers to the payload type (data binding name) for XML.
	 */
	public static final String PAYLOAD_XML = "XML";

	/**
	 * Refers to the payload type (data binding name) for name-value pair.
	 */
	public static final String PAYLOAD_NV = "NV";

	/**
	 * Refers to the payload type (data binding name) for JSON (JavaScript Object Notation).
	 */
	public static final String PAYLOAD_JSON = "JSON";

	/**
	 * Refers to the payload type (data binding name) for ASN.1 Fast Infoset (binary XML).
	 */
	public static final String PAYLOAD_FAST_INFOSET = "FAST_INFOSET";

	/**
	 * Refers to the payload validation level. Can take the following values
	 * disabled, none, relax, strict
	 */
	public static final String VALIDATE_PAYLOAD = "validatePayLoad";

	/**
	 * Refers to the Listener class to be used in the event of any validation failures.
	 */
	public static final String SCHEMA_VALIDATION_LISTENER_CLASS = "schemaValidationListenerClass";

	/**
	 * Private Constructor.
	 */
	private BindingConstants() {
		// no instances
	}
}
