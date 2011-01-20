/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.nv;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.impl.jaxb.nv.NVSerializerFactory;
import org.ebayopensource.turmeric.runtime.common.binding.SerializerFactory;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.BaseSerializerFactory;


public class JAXBNVSerializerFactory  extends BaseSerializerFactory implements SerializerFactory {

	public JAXBNVSerializerFactory() {
		m_factory = new NVSerializerFactory();
	}

	@Override
	public String getPayloadType() {
		return BindingConstants.PAYLOAD_NV;
	}
}
