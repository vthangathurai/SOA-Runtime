/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.tools.codegen.handler.DefaultResponseHandler;
import org.junit.Before;
import org.junit.Test;


/**
 * @author rpallikonda
 *
 */
public final class DefaultResponseHandlerTest extends AbstractServiceGeneratorTestCase {

	private DefaultResponseHandler defRespHandler = null;

	@Before
	public void setUp() throws Exception {
		defRespHandler = new DefaultResponseHandler();
	}

	@Test
	public void defaultResponse() {
		assertEquals("the default response should be same as isResponse()", 
				defRespHandler.getBooleanResponse(null), 
				defRespHandler.isDefaultResponse());

		defRespHandler.setResponseMap(null);

		assertEquals("the default response should be same as isResponse()", 
				defRespHandler.getBooleanResponse(null), 
				defRespHandler.isDefaultResponse());

		defRespHandler.setDefaultResponse(false);

		assertEquals("the default response should be same as isResponse()", 
				defRespHandler.getBooleanResponse(null), 
				defRespHandler.isDefaultResponse());
	}

	@Test
	public void responseMap() {
		Map< String, Boolean > newResponseMap = new HashMap< String, Boolean >();
		newResponseMap.put("key1", null);
		newResponseMap.put("key2", false);

		defRespHandler.setResponseMap(newResponseMap);

		assertEquals("null value for a key should return false", 
				defRespHandler.getBooleanResponse("key1"), 
				false);

		assertEquals("non-existent key should return defaultResponse", 
				defRespHandler.getBooleanResponse("non-existent"), 
				defRespHandler.isDefaultResponse());

		assertEquals("value returned is not the one set", 
				defRespHandler.getBooleanResponse("key2"), 
				false);
	}
}
