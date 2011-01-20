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

/**
 * Interface representing the processed low-level type mapping configuration information.  Serializer/deserializer writers
 * should normally use the higher level information in ServiceOperationParamDesc instead. 
 * @author ichernyshev
 */
public interface ServiceTypeMappings {

	/**
	 * Returns the XML namespace corresponding to a particular Java type.
	 * @param javaType the class of the Java type for which an XML namespace should be found
	 * @return the corresponding XML namespace
	 */
	public String getNsForJavaType(Class javaType);

	/**
	 * @return the java package to namespace mapping.
	 */
	public Map<String, String> getPackageToNamespaceMap();

	/**
	 * Returns a mapping between all XML namespaces of top-level XML wire elements, and all corresponding Java
	 * package name prefixes.
	 * @return the XML namespace to Java package prefix map
	 */
	public Map<String,String> getNamespaceToPrefixMap();

	/**
	 * Returns a mapping between all XML namespaces of top-level XML wire elements, and all corresponding Java
	 * package name prefixes.
	 * @return the XML namespace to Java package prefix map
	 */
	public Map<String,List<String>> getNamespaceToPrefixesMap();

	/**
	 * Returns a mapping between all Java package name prefixes of top-level serializable Java objects, and all corresponding
	 * XML namespaces.
	 * @return the Java package prefix to XML namespace map
	 */
	public Map<String,String> getPrefixToNamespaceMap();

	/**
	 * Returns a particular namespace corresponding to a specified Java package name prefix.
	 * @param prefix the Java package prefix for which an XML namespace should be found
	 * @return the corresponding XML namespace
	 */
	public String getNamespaceByPrefix(String prefix);

	/**
	 * Returns a particular Java package name prefix corresponding to a specified XML namespace.
	 * @param ns the XML namespace for which a package prefix should be found
	 * @return the corresponding Java package name prefix
	 */
	public String getPrefixByNamespace(String ns);
	
	/**
	 * Returns namespace URI if the type mapping contains only one user defined namespace.
	 * 
	 * Otherwise return null
	 * 
	 * @return the namespace URI if the type mapping contains only one user defined namespace. null otherwise.
	 */
	public String getSingleNamespace();
	
	/**
	 * Returns true of namespace folding is enabled for the service.
	 * @return true of namespace folding is enabled for the service.
	 */
	public boolean getNamespaceFoldingEnabled();
}
