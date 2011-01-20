/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.binding.jaxb;

import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;


/**
 * @author wdeng
 *
 */
public class JAXBNVSerializerOptionsTest extends JAXBNVSerDeserTest {

	public JAXBNVSerializerOptionsTest() {
		super();
	}

	protected void setUpConfig() throws Exception {
		ServiceConfigManager.getInstance().setConfigTestCase("confignvoptions");
	}
}
