/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 *
 */
package org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb;

import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceTypeMappingsImpl;
import org.ebayopensource.turmeric.runtime.common.service.ServiceTypeMappings;


/**
 * This class contains delegate methods to the data binding module.
 *
 * @author wdeng
 *
 */
public class DataBindingFacade {
	/**
	 * Creates a NamespaceConvention for deserialization from ServiceTypeMappings.
	 *
	 * @param typeMappings
	 * @return
	 */
	public static NamespaceConvention createDeserializationNSConvention(ServiceTypeMappings typeMappings) {
		String singleNamespace = (typeMappings != null ? typeMappings.getSingleNamespace() : null);
		boolean namespaceFoldingEnabled = typeMappings == null ? false : typeMappings.getNamespaceFoldingEnabled();
		return NamespaceConvention.createDeserializationNSConvention(singleNamespace,
				ServiceTypeMappingsImpl.getStandardPrefixToNSMap(namespaceFoldingEnabled),
				ServiceTypeMappingsImpl.getStandardNSToPrefixesMap(namespaceFoldingEnabled));
	}

	public static NamespaceConvention createSerializationNSConvention(ServiceTypeMappings typeMappings) {
		return NamespaceConvention.createSerializationNSConvention(typeMappings.getSingleNamespace(),

				typeMappings.getPrefixToNamespaceMap(),
				typeMappings.getNamespaceToPrefixesMap());
	}
}
