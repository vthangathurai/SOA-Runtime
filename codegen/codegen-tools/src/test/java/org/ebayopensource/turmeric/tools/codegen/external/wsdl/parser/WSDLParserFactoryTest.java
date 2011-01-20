/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser;

import javax.wsdl.factory.WSDLFactory;

import org.ebayopensource.turmeric.junit.AbstractTurmericTestCase;
import org.junit.Assert;
import org.junit.Test;

public class WSDLParserFactoryTest extends AbstractTurmericTestCase {
	@Test
	public void testGetInstance() throws Exception {
		WSDLFactory factory = WSDLParserFactory.getInstance();
		Assert.assertNotNull("factory", factory);
	}
}
