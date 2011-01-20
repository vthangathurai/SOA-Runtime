/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.binding.jaxb;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.ebayopensource.turmeric.runtime.binding.common.utils.SerializationUtils;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Test;


// TODO: Move to BindingFwk unit tests.
public class SerializationUtilsTest {

	private static final String TEST_NAMESPACE_URL = "http://www.ebay.com/test/soaframework/sample/service/message";
	@Test
	public void jaxbJSONSimpleSerialization() throws Exception {
		System.out.println("**** Starting testJaxbJSONSimpleSerialization");
		MyMessage msg = TestUtils.createTestMessage(1);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		SerializationUtils.serializeSingleNamespaceJSONOutput(TEST_NAMESPACE_URL, msg, baos);
		
		String result = baos.toString("UTF-8");
		System.out.println(result);
		
		StringBuilder json = new StringBuilder();
		json.append("{\"MyMessage\":[");
		json.append("{\"body\":[\"");
		
		// The chinese in encoded raw java form (mainly to avoid java source file encoding issues.
		// Assuming UTF-8 Charset.
		json.append(new String(TestUtils.MESSAGE_BODY_TEXT));
		json.append("\"],\"recipients\":");
		json.append("[{\"entry\":[{\"key\":[\"soa0@ebay.com\"],");
		json.append("\"value\":[{\"city\":[\"San Jose\"],");
		json.append("\"emailAddress\":[\"soa0@ebay.com\"],");
		json.append("\"postCode\":[\"95125\"],\"state\":[\"CA\"],");
		json.append("\"streetNumber\":[\"2000\"]}]}]}]");
		json.append(",\"something\":");
		json.append("[{\"xsi.@type\":\"xs:string\",");
		json.append("\"__value__\":\"This is from the any object type\"}]");
		json.append(",\"subject\":[\"SOA Framework test message\"]}"); 
		json.append("]}");
		
		assertEquals("JSON Form", result, json.toString());
		System.out.println("**** Ending testJaxbJSONSimpleSerialization");
	}

	//TODO: to make it work for NV, we need to use a config that has only one namespace.
	@Test
	public void jaxbNVSimpleSerialization() throws Exception {
		System.out.println("**** Starting testJaxbNVSimpleSerialization");
		MyMessage msg = TestUtils.createTestMessage(1);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		SerializationUtils.serializeSingleNamespaceNVOutput(TEST_NAMESPACE_URL, msg, baos);
		
		String result = baos.toString("UTF-8");
		System.out.println(result);
		
		StringBuilder nv = new StringBuilder();
		nv.append("body(0)=\"SOA+in+Chinese+is+%27");
		// From Charset UTF-8 then URLencoded
		nv.append(TestUtils.SOA_IN_CHINESE_STRING_URL_ENCODED);
		nv.append("%27\"");
		nv.append("&recipients(0).entry(0).key(0)=\"soa0%40ebay.com\"");
		nv.append("&recipients(0).entry(0).value(0).city(0)=\"San+Jose\"");
		nv.append("&recipients(0).entry(0).value(0).emailAddress(0)");
		nv.append("=\"soa0%40ebay.com\"");
		nv.append("&recipients(0).entry(0).value(0).postCode(0)=\"95125\"");
		nv.append("&recipients(0).entry(0).value(0).state(0)=\"CA\"");
		nv.append("&recipients(0).entry(0).value(0).streetNumber(0)=\"2000\"");
		nv.append("&something(0).xsi:@type=\"xs%3Astring\"");
		nv.append("&something(0)=\"This+is+from+the+any+object+type\"");
		nv.append("&subject(0)=\"SOA+Framework+test+message\"");
		
		assertEquals(nv.toString(), result);
		System.out.println("**** Ending testJaxbNVSimpleSerialization");
	}
}
