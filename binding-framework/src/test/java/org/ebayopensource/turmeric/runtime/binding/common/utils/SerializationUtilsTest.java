/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.common.utils;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import org.ebayopensource.turmeric.junit.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.runtime.binding.Address;
import org.ebayopensource.turmeric.runtime.binding.MyMessage;
import org.ebayopensource.turmeric.runtime.binding.common.utils.SerializationUtils;
import org.junit.Test;


public class SerializationUtilsTest extends AbstractTurmericTestCase {
	private static final String TEST_NAMESPACE_URL = "http://www.ebayopensource.org/turmeric/sample/service/message";
	
	public static byte[] SOA_IN_CHINESE = new byte[] { (byte) 0xE9,
		(byte) 0x9D, (byte) 0xA2, (byte) 0xE5, (byte) 0x90, (byte) 0x91,
		(byte) 0xE6, (byte) 0x9C, (byte) 0x8D, (byte) 0xE5, (byte) 0x8A,
		(byte) 0xA1, (byte) 0xE7, (byte) 0x9A, (byte) 0x84, (byte) 0xE6,
		(byte) 0x9E, (byte) 0x84, (byte) 0xE6, (byte) 0x9E, (byte) 0xB6 };
	public static final String SOA_IN_CHINESE_STRING = new String(SOA_IN_CHINESE, Charset.forName("UTF-8"));
	public static final String SOA_IN_CHINESE_STRING_URL_ENCODED = 
			"%E9%9D%A2%E5%90%91%E6%9C%8D%E5%8A%A1%E7%9A%84%E6%9E%84%E6%9E%B6";
	
	public static final String MESSAGE_BODY_TEXT = "SOA in Chinese is '"
		+ SOA_IN_CHINESE_STRING + "'";
	public static final String MESSAGE_SUBJECT_TEXT = "SOA Framework test message";
	public static final String CITY_NAME = "San Jose";
	public static final String EMAIL_ADDRESS0 = "soa0@ebayopensource.org.com";

	private MyMessage createTestMessage(int numRecipients)
	{
		MyMessage msg = new MyMessage();
		msg.setBody(MESSAGE_BODY_TEXT);
		msg.setSubject(MESSAGE_SUBJECT_TEXT);
		msg.setSomething("This is from the any object type");
		Address addr;
		for (int i=0; i<numRecipients; i++) {
			addr = new Address();
			addr.setStreetNumber(2000 + i);
			addr.setState("Hamilton Ave");
			addr.setCity(CITY_NAME);
			addr.setState("CA");
			addr.setPostCode(95125 + i);
			addr.setEmailAddress("soa" + i + "@ebayopensource.org");
			msg.addRecipient(addr);
		}
		return msg;
	}
	
	@Test
	public void jaxbJSONSimpleSerialization() throws Exception {
		MyMessage msg = createTestMessage(1);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		SerializationUtils.serializeSingleNamespaceJSONOutput(TEST_NAMESPACE_URL, msg, baos);
		
		String result = baos.toString("UTF-8");
		System.out.println(result);
		
		StringBuilder json = new StringBuilder();
		json.append("{\"MyMessage\":[");
		json.append("{\"body\":[\"");
		
		// The chinese in encoded raw java form (mainly to avoid java source file encoding issues.
		// Assuming UTF-8 Charset.
		json.append(new String(MESSAGE_BODY_TEXT));
		json.append("\"],\"recipients\":");
		json.append("[{\"entry\":[{\"key\":[\"soa0@ebayopensource.org\"],");
		json.append("\"value\":[{\"city\":[\"San Jose\"],");
		json.append("\"emailAddress\":[\"soa0@ebayopensource.org\"],");
		json.append("\"postCode\":[\"95125\"],\"state\":[\"CA\"],");
		json.append("\"streetNumber\":[\"2000\"]}]}]}]");
		json.append(",\"something\":");
		json.append("[{\"xsi.@type\":\"xs:string\",");
		json.append("\"__value__\":\"This is from the any object type\"}]");
		json.append(",\"subject\":[\"SOA Framework test message\"]}"); 
		json.append("]}");
		
		assertEquals("JSON Form", result, json.toString());
	}

	//TODO: to make it work for NV, we need to use a config that has only one namespace.
	@Test
	public void jaxbNVSimpleSerialization() throws Exception {
		MyMessage msg = createTestMessage(1);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		SerializationUtils.serializeSingleNamespaceNVOutput(TEST_NAMESPACE_URL, msg, baos);
		
		String result = baos.toString("UTF-8");
		System.out.println(result);
		
		StringBuilder nv = new StringBuilder();
		nv.append("body(0)=\"SOA+in+Chinese+is+%27");
		// From Charset UTF-8 then URLencoded
		nv.append(SOA_IN_CHINESE_STRING_URL_ENCODED);
		nv.append("%27\"");
		nv.append("&recipients(0).entry(0).key(0)=\"soa0%40ebayopensource.org\"");
		nv.append("&recipients(0).entry(0).value(0).city(0)=\"San+Jose\"");
		nv.append("&recipients(0).entry(0).value(0).emailAddress(0)");
		nv.append("=\"soa0%40ebayopensource.org\"");
		nv.append("&recipients(0).entry(0).value(0).postCode(0)=\"95125\"");
		nv.append("&recipients(0).entry(0).value(0).state(0)=\"CA\"");
		nv.append("&recipients(0).entry(0).value(0).streetNumber(0)=\"2000\"");
		nv.append("&something(0).xsi:@type=\"xs%3Astring\"");
		nv.append("&something(0)=\"This+is+from+the+any+object+type\"");
		nv.append("&subject(0)=\"SOA+Framework+test+message\"");
		
		assertEquals(nv.toString(), result);
	}
}
