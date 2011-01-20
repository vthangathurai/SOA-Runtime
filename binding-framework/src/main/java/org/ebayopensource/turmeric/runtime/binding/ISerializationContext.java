/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;


/**
 * The base context object for serialization.  It contains common information needed for
 * serialize a given object to the specified wire format.  
 * 
 * @author wdeng
 *
 */
public interface ISerializationContext {	
	/**
	 * Get the type of input payload.  Currently,  
	 * framework supported payload types are:  FAST_INFOSET, XML, JSON, and NV (Name-value pair)
	 * @return type of input payload.
	 */
	public String getPayloadType(); 
	
	/**
	 * Get the default namespace. elements defined inside this namespace don't require a prefix in payload. 
	 * If null, no default namespace defined.
	 * @return default namespace or null.
	 */
	public String getDefaultNamespace();
	
	/**
	 * Return the namespace, only all the types in the context 
	 * belongs to a single namespace, null if multiple 
	 * namespaces are involved.	  
	 * @return value of single namespace or null
	 */
	public String getSingleNamespace();
	

	/**
	 * Get the namespace to prefix map used in the serialization process. Each namespace known to the
	 * context must have an entry in this map and the prefix must be unique in the map.
	 * @return the namespace to prefix map.
	 */
	public Map<String, List<String>> getNamespaceToPrefixMap();

	/**
	 * Get the prefix to namespace map used in the serialization process.  One namespace would have multiple
	 * prefix, but each prefix can map to only one namespace.
	 * @return the prefix to namespace map.
	 */
	public Map<String, String> getPrefixToNamespaceMap();
	 
	/**
	 * Get the namespace for a java type that the context known. 
	 * @param type A Java bean class object.
	 * @return the namespace for a java type.
	 */
	public String getNsForJavaType(Class type);

	/**
	 * Get the charset of the payload.
	 * @return charset of the payload.
	 */
	public Charset getCharset();
	
	/**
	 * Get the expected java Class object of the root element.
	 * @return java Class object of the root element
	 */
	public Class getRootClass(); 
	
	/**
	 * Get the ITypeConversionContext interface.  non-null value indicates 
	 * type conversion is enabled
	 * @return the ITypeConversionContext interface.
	 */
	public ITypeConversionContext getTypeConversionContext();

	/**
	 * Returns true if the context contains terminating exceptions.
	 * @return true if the context contains terminating exceptions
	 */
	public boolean hasErrors();

	/**
	 * Returns the list of terminating exceptions for this invocation, in order thrown.
	 * @return the exception list
	 */
	public List<Throwable> getErrorList();

	/**
	 * Returns the list of recoverable (continue-on-error) exceptions for this invocation, in order thrown.
	 * @return the warning list
	 */
	public List<Throwable> getWarningList();

	/**
	 * Add a new exception to the end of the list of terminating exceptions.
	 * @param t an Throwable representing an error.
	 */
	public void addError(Throwable t);
	
	/**
	 * Adds a new exception to the end of the list of recovered exceptions.
	 * @param t an Throwable representing a warning.
	 */
	public void addWarning(Throwable t);
	
	/**
	 * Get the root element name of the message.
	 * @return the name of the root element.
	 */
	public QName getRootXMLName();
	
	/**
	 * Get the data schema for the root element. 
	 * @return data schema for the root element.
	 */
	public DataElementSchema getRootElementSchema(); 
	
	/**
	 * Returns true if the we are binding with a REST format. 
	 * @return true if the we are binding with a REST format. 
	 */
	public boolean isREST();
}

