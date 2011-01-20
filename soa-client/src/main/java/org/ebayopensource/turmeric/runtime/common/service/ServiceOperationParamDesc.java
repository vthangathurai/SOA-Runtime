/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.service;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;


/**
 * Interface providing information about a specific parameter of an operation.  Serializers use this information to determine
 * what kind of information is being serialized/deserialized, to validate that received types match expectations,
 * and to format any errors as appropriate for the operation (using the right error response type).
 * @author ichernyshev
 */
public interface ServiceOperationParamDesc {

	/**
	 * Returns the set of all types that appear as top-level content Java objects in the unserialized message (request, 
	 * response or error message). In the current implementation, there is only one such "root" type for a given operation
	 * and type of operation message (request/response/error).
	 * @return the root type(s)
	 */
	public List<Class> getRootJavaTypes();

	/**
	 * Returns the set of all XML element names that appear as top level elements in a serialized message (request, response
	 * or error message). In the current implementation, there is only one such "root" type.
	 * @return the root element name(s)
	 */
	public List<DataElementSchema> getRootElements();

	/**
	 * Returns the Java type corresponding to a particular XML element name, based on the type mapping configuration.  This
	 * is used when deserializing, to create the correct root Java instance based on an incoming XML element name.
	 * @param xmlName the XML element name
	 * @return the corresponding Java type
	 */
	public Class getJavaTypeForXmlName(QName xmlName);

	/**
	 * Returns the XML element name corresponding to a particular Java type, based on the type mapping configuration.  This
	 * is used when serializing, to create the correct XML element name based on the Java instance. 
	 * @param javaType the Class representing the Java type
	 * @return the corresponding XML element name
	 */
	public QName getXmlNameForJavaType(Class javaType);

	/**
	 * Returns the XML element name corresponding to a particular Java type, based on the type mapping configuration.  This
	 * is used when serializing, to create the correct XML element name based on the Java instance. 
	 * @param javaType the string name of the Java type
	 * @return the corresponding XML element name
	 */
	public QName getXmlNameForJavaType(String javaType);

	/**
	 * Returns the complete map of XML element name to Java type mappings.
	 * @return the element name to Java type mappings
	 */
	public Map<QName,Class> getXmlToJavaMappings();

	/**
	 * Returns the complete map of Java type to XML element name mappings.
	 * @return the element name to Java type mappings
	 */
	public Map<String,QName> getJavaToXmlMappings();
	
	/**
	 * Returns true if this operation parameter (request/response) has the ability to carry attachments. 
	 * @return true if the operation parameter can carry attachments.
	 */
	public boolean hasAttachment();
}
