/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.parser.json;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.ebayopensource.turmeric.runtime.binding.impl.jaxb.json.JSONFilterInputStream;
import org.junit.Test;


public class JSONFilterInputStreamTest {

	private static final String JSON_INPUT = 
		"{\"emailAddresses\":[{\"email\":\"abc@abc.com\",\"id\":\"1693916\"},{\"email\":\"abc@abc.com\",\"id\":\"1693916\"},{\"email\":\"abc@abc.com\",\"id\":\"1693916\"}],\"phoneNumbers\":[{\"activated\":\"true\",\"countryCode\":\"1\",\"id\":\"2583906\"},{\"activated\":\"false\",\"countryCode\":\"1\",\"id\":\"123345\"}],\"streetAddresses\":[{\"city\":\"san jose\",\"country\":\"US\",\"id\":\"12345\",\"postalCode\":\"98765\",\"province\":\"CA\",\"recipientName\":\"rec name\",\"street1\":\"12 aaaaa\"}]}";
	
	private static final String XML_ROOT_ELEMENT = "GetAccountDetailsResponse";
		
	private static final String EXPECTED_OUTPUT = 
		"{" + "\"" + XML_ROOT_ELEMENT + "\"" + ":" +	JSON_INPUT + "}";

	private static final byte[] EXPECTED_OUTPUT_BYTES = EXPECTED_OUTPUT.getBytes();
	
	
	private static final String MSG = "\"This is to test with simple message string\",";
	private static final String JSON_INPUT2 = "{" + MSG + "}";
	private static final String XML_ROOT_ELEMENT2 = "Message";
	private static final String EXPECTED_OUTPUT2 = 
		"{" + "\"" + XML_ROOT_ELEMENT2 + "\"" + ":" +	MSG + "}";
	private static final byte[] EXPECTED_OUTPUT_BYTES2 = EXPECTED_OUTPUT2.getBytes();
	
	
	private static final String PREFIX = "b";
	private static final String JSON_INPUT3 = "{\"body\":[{\"email\":\"abc@abc.com\",\"id\":\"1693916\"},{\"email\":\"abc@abc.com\",\"id\":\"1693916\"},{\"email\":\"abc@abc.com\",\"id\":\"1693916\"}],\"phoneNumbers\":[{\"activated\":\"true\",\"countryCode\":\"1\",\"id\":\"2583906\"},{\"activated\":\"false\",\"countryCode\":\"1\",\"id\":\"123345\"}],\"streetAddresses\":[{\"city\":\"san jose\",\"country\":\"US\",\"id\":\"12345\",\"postalCode\":\"98765\",\"province\":\"CA\",\"recipientName\":\"rec name\",\"street1\":\"12 aaaaa\"}]}";
	private static final String XML_ROOT_ELEMENT3 = PREFIX;
	private static final String EXPECTED_OUTPUT3 = 
		"{" + "\"" + XML_ROOT_ELEMENT3 + "\"" + ":" + JSON_INPUT3 + "}";
	private static final byte[] EXPECTED_OUTPUT_BYTES3 = EXPECTED_OUTPUT3.getBytes();

	private static final String MULTI_NS_INPUT = 
		"{\"jsonns.xsi\":\"http://www.w3.org/2001/XMLSchema-instance\",\"jsonns.xs\":\"http://www.w3.org/2001/XMLSchema\",\"jsonns.ms\":\"http://www.ebayopensource.org/turmeric/common/v1/types\",\"jsonns.ns2\":\"http://www.paypal.com/paypalOne\",\"ms.getName\":[{\"ms.in\":[{\"ns2.name\":[\"whatever\"]}]}]}";
	private static final byte[] MULTI_NS_INPUT_BYTES = MULTI_NS_INPUT.getBytes();

	private static final String MULTI_NS_INPUT_NO_ROOT = 
		"{\"jsonns.xsi\":\"http://www.w3.org/2001/XMLSchema-instance\",\"jsonns.xs\":\"http://www.w3.org/2001/XMLSchema\",\"jsonns.ms\":\"http://www.ebayopensource.org/turmeric/common/v1/types\",\"jsonns.ns2\":\"http://www.paypal.com/paypalOne\",\"ms.in\":[{\"ns2.name\":[\"whatever\"]}]}";
	private static final String MULTI_NS_INPUT_ROOT = "ms.getName";
	private static final String MULTI_NS_NO_ROOT_EXPECTED_OUTPUT = 
		"{\"jsonns.xsi\":\"http://www.w3.org/2001/XMLSchema-instance\"," +
		"\"jsonns.xs\":\"http://www.w3.org/2001/XMLSchema\"," +
		"\"jsonns.ms\":\"http://www.ebayopensource.org/turmeric/common/v1/types\"," +
		"\"jsonns.ns2\":\"http://www.paypal.com/paypalOne\"," +
		"\"ms.getName\":{" +
		"\"ms.in\":[{\"ns2.name\":[\"whatever\"]}]}" +
		"}";
	private static final byte[] MULTI_NS_NO_ROOT_EXPECTED_OUTPUT_BYTES =
		MULTI_NS_NO_ROOT_EXPECTED_OUTPUT.getBytes();
	
	private static final String MULTI_NS_INPUT_NO_ROOT_2 = 
		"{  \"jsonns.xsi\"  :  \"http://www.w3.org/2001/XMLSchema-instance\",\"jsonns.xs\":\"http://www.w3.org/2001/XMLSchema\",\"jsonns.ms\":\"http://www.ebayopensource.org/turmeric/common/v1/types\",\"jsonns.ns2\":\"http://www.paypal.com/paypalOne\",\"ms.in\":[{\"ns2.name\":[\"whatever\"]}]}";
	private static final String MULTI_NS_INPUT_ROOT_2 = "ms.getName";
	private static final String MULTI_NS_NO_ROOT_EXPECTED_OUTPUT_2 = 
		"{  \"jsonns.xsi\"  :  \"http://www.w3.org/2001/XMLSchema-instance\"," +
		"\"jsonns.xs\":\"http://www.w3.org/2001/XMLSchema\"," +
		"\"jsonns.ms\":\"http://www.ebayopensource.org/turmeric/common/v1/types\"," +
		"\"jsonns.ns2\":\"http://www.paypal.com/paypalOne\"," +
		"\"ms.getName\":{" +
		"\"ms.in\":[{\"ns2.name\":[\"whatever\"]}]}" +
		"}";
	private static final byte[] MULTI_NS_NO_ROOT_EXPECTED_OUTPUT_BYTES_2 =
		MULTI_NS_NO_ROOT_EXPECTED_OUTPUT_2.getBytes();
	// Test ability of filter input stream to handle malformed inputs
	// All formed inputs should be returned by the filter input stream without
	// any modifications
	private static final String[] MALFORMED_INPUTS = {
			"",
			
			"a",
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
			
			//"{a",
			//"{aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
			"a{",
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa{",
			
			"a}",
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa}",
			"}a",
			"}aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
			
			"\"",
			"\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\"",
			
			
			"a\"a",
			"aaaaaaaaaaaaaaaaaaaaaaaaa\"aaaaaaaaaaaaaaaaaaa",
			
			//"{}",
			//"{aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa}"
		};
	
	@Test
	public void withMalformedInput() throws Exception {
		for(String s : MALFORMED_INPUTS) {
			runWithInput(s);
		}
	}
	
	/**
	 * This method should be called with input string that are NOT expected to be modified
	 * by the JSONFilterInputStream
	 * 
	 * @param input
	 * @throws Exception
	 */
	private void runWithInput(String input) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(XML_ROOT_ELEMENT), Charset.forName("US-ASCII"));
		
		byte[] bites = new byte[8096];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read(bites, totalRead, bites.length-totalRead);
			if(numRead == -1 ) {
				break;
			}
			
			totalRead += numRead;
		}
		
		System.out.println();
		System.out.println("expected MALFORMED INPUT: " + input);
		System.out.println("actual  MALFORMED OUTPUT: " + new String(bites));
		
		Assert.assertEquals(input.length(), totalRead);
		byte[] EXP_BITES = input.getBytes();
		for(int i=0; i<totalRead; i++) {
			Assert.assertEquals("mismatch at index: " + i, (byte)EXP_BITES[i], (byte)bites[i]);
		}
		
	}
	
	@Test
	public void readByteArrayWithNoXMLRootElement() throws Exception {
		
		ByteArrayInputStream bais = new ByteArrayInputStream(JSON_INPUT.getBytes());
		
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(XML_ROOT_ELEMENT), Charset.forName("US-ASCII"));
		
		byte[] bites = new byte[8096];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read(bites, totalRead, bites.length - totalRead);
			if( numRead == -1 ) {
				break;
			}
			totalRead += numRead;
		}
		
		String actualOutput = new String(bites, "US-ASCII");
		System.out.println("act: " + actualOutput);
		System.out.println("exp: " + JSONFilterInputStreamTest.EXPECTED_OUTPUT);
		
		//Assert.assertEquals(EXPECTED_OUTPUT, actualOutput);
		//Assert.assertEquals(this.EXPECTED_OUTPUT.length(), actualOutput.length());
		for(int i=0; i<EXPECTED_OUTPUT.length(); i++) {
			//Assert.assertEquals(this.EXPECTED_OUTPUT_BYTES[i], bites[i]);
			if( JSONFilterInputStreamTest.EXPECTED_OUTPUT_BYTES[i] != bites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}
		
	}

	@Test
	public void readByteArrayWithNoXMLRootElementWithOneBraceInput() throws Exception {
		
		ByteArrayInputStream bais = new ByteArrayInputStream(JSON_INPUT2.getBytes());
		
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(XML_ROOT_ELEMENT2), Charset.forName("US-ASCII"));
		
		byte[] bites = new byte[8096];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read(bites, totalRead, bites.length - totalRead);
			if( numRead == -1 ) {
				break;
			}
			totalRead += numRead;
		}
		
		String actualOutput = new String(bites, "US-ASCII");
		System.out.println("ACT: " + actualOutput);
		System.out.println("EXP: " + JSONFilterInputStreamTest.EXPECTED_OUTPUT2);
		
		//Assert.assertEquals(EXPECTED_OUTPUT, actualOutput);
		//Assert.assertEquals(this.EXPECTED_OUTPUT.length(), actualOutput.length());
		for(int i=0; i<EXPECTED_OUTPUT2.length(); i++) {
			//Assert.assertEquals(this.EXPECTED_OUTPUT_BYTES[i], bites[i]);
			if( JSONFilterInputStreamTest.EXPECTED_OUTPUT_BYTES2[i] != bites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}
		
	}

	@Test
	public void readByteArrayWithXMLRootElement() throws Exception {
		
		ByteArrayInputStream bais = new ByteArrayInputStream(EXPECTED_OUTPUT.getBytes());
		
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(XML_ROOT_ELEMENT), Charset.forName("US-ASCII"));
		
		byte[] bites = new byte[8096];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read(bites, totalRead, bites.length - totalRead);
			if( numRead == -1 ) {
				break;
			}
			totalRead += numRead;
		}
		
		String actualOutput = new String(bites, "US-ASCII");
		
		System.out.println("EXPECTED output: " + EXPECTED_OUTPUT);
		System.out.println("ACTUAL output:   " + actualOutput);
		
		//Assert.assertEquals(EXPECTED_OUTPUT, actualOutput);
		for(int i=0; i<EXPECTED_OUTPUT.length(); i++) {
			//Assert.assertEquals(this.EXPECTED_OUTPUT_BYTES[i], bites[i]);
			if( JSONFilterInputStreamTest.EXPECTED_OUTPUT_BYTES[i] != bites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}		
	}

	@Test
	public void readByteArrayWithNoXMLRootElement2() throws Exception {
		
		ByteArrayInputStream bais = new ByteArrayInputStream(JSON_INPUT.getBytes());
		
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(XML_ROOT_ELEMENT), Charset.forName("US-ASCII"));
		
		byte[] accBites = new byte[8192];
		byte[] bites = new byte[8];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read(bites, 0, bites.length);
			if( numRead == -1 ) {
				break;
			}
			System.arraycopy(bites, 0, accBites, totalRead, numRead);
			totalRead += numRead;
		}
		
		String actualOutput = new String(accBites, "US-ASCII");
		System.out.println("act: " + actualOutput);
		System.out.println("exp: " + JSONFilterInputStreamTest.EXPECTED_OUTPUT);
		
		//Assert.assertEquals(EXPECTED_OUTPUT, actualOutput);
		//Assert.assertEquals(this.EXPECTED_OUTPUT.length(), actualOutput.length());
		for(int i=0; i<EXPECTED_OUTPUT.length(); i++) {
			//Assert.assertEquals(this.EXPECTED_OUTPUT_BYTES[i], bites[i]);
			if( JSONFilterInputStreamTest.EXPECTED_OUTPUT_BYTES[i] != accBites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}
		
	}

	@Test
	public void readByteArrayWithXMLRootElement2() throws Exception {
		
		ByteArrayInputStream bais = new ByteArrayInputStream(EXPECTED_OUTPUT.getBytes());
		
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(XML_ROOT_ELEMENT), Charset.forName("US-ASCII"));
		
		byte[] accBites = new byte[8192];
		byte[] bites = new byte[8];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read(bites, 0, bites.length);
			if( numRead == -1 ) {
				break;
			}
			System.arraycopy(bites, 0, accBites, totalRead, numRead);
			totalRead += numRead;
		}
		
		String actualOutput = new String(accBites, "US-ASCII");
		
		//Assert.assertEquals(EXPECTED_OUTPUT, actualOutput);
		for(int i=0; i<EXPECTED_OUTPUT.length(); i++) {
			//Assert.assertEquals(this.EXPECTED_OUTPUT_BYTES[i], bites[i]);
			if( JSONFilterInputStreamTest.EXPECTED_OUTPUT_BYTES[i] != accBites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}		
	}


	@Test
	public void readByteArrayWithNoXMLRootElement3() throws Exception {
		
		ByteArrayInputStream bais = new ByteArrayInputStream(JSON_INPUT3.getBytes());
		
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(XML_ROOT_ELEMENT3), Charset.forName("US-ASCII"));
		
		byte[] accBites = new byte[8192];
		byte[] bites = new byte[8];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read(bites, 0, bites.length);
			if( numRead == -1 ) {
				break;
			}
			System.arraycopy(bites, 0, accBites, totalRead, numRead);
			totalRead += numRead;
		}
		
		String actualOutput = new String(accBites, "US-ASCII");
		System.out.println("act: " + actualOutput);
		System.out.println("exp: " + JSONFilterInputStreamTest.EXPECTED_OUTPUT3);
		
		//Assert.assertEquals(EXPECTED_OUTPUT, actualOutput);
		//Assert.assertEquals(this.EXPECTED_OUTPUT.length(), actualOutput.length());
		for(int i=0; i<EXPECTED_OUTPUT3.length(); i++) {
			//Assert.assertEquals(this.EXPECTED_OUTPUT_BYTES[i], bites[i]);
			if( JSONFilterInputStreamTest.EXPECTED_OUTPUT_BYTES3[i] != accBites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}
		
	}

	@Test
	public void readWithNoXMLRootElement() throws Exception {
		
		ByteArrayInputStream bais = new ByteArrayInputStream(JSON_INPUT.getBytes());
		
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(XML_ROOT_ELEMENT), Charset.forName("US-ASCII"));
		
		byte[] bites = new byte[8096];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read();
			if( numRead == -1 ) {
				break;
			}
			bites[totalRead++] = (byte)numRead;
		}
		
		String actualOutput = new String(bites, "US-ASCII");
		
		//Assert.assertEquals(EXPECTED_OUTPUT, actualOutput);
		for(int i=0; i<EXPECTED_OUTPUT.length(); i++) {
			//Assert.assertEquals(this.EXPECTED_OUTPUT_BYTES[i], bites[i]);
			if( JSONFilterInputStreamTest.EXPECTED_OUTPUT_BYTES[i] != bites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}		
	}

	@Test
	public void readWithXMLRootElement() throws Exception {
		
		ByteArrayInputStream bais = new ByteArrayInputStream(JSONFilterInputStreamTest.EXPECTED_OUTPUT.getBytes());
		
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(XML_ROOT_ELEMENT), Charset.forName("US-ASCII"));
		
		byte[] bites = new byte[8096];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read();
			if( numRead == -1 ) {
				break;
			}
			bites[totalRead++] = (byte)numRead;
		}
		
		String actualOutput = new String(bites, "US-ASCII");
		
		//Assert.assertEquals(EXPECTED_OUTPUT, actualOutput);
		for(int i=0; i<EXPECTED_OUTPUT.length(); i++) {
			//Assert.assertEquals(this.EXPECTED_OUTPUT_BYTES[i], bites[i]);
			if( JSONFilterInputStreamTest.EXPECTED_OUTPUT_BYTES[i] != bites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}		
	}

	
	@Test
	public void readWithMultipleNamespaceWithRoot() throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(JSONFilterInputStreamTest.MULTI_NS_INPUT.getBytes());
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName("getName"), Charset.forName("US-ASCII"));

		byte[] bites = new byte[8096];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read();
			if( numRead == -1 ) {
				break;
			}
			bites[totalRead++] = (byte)numRead;
		}
		
		String actualOutput = new String(bites, "US-ASCII");
		System.out.println("exp    output: " + MULTI_NS_INPUT);
		System.out.println("actual output: " + actualOutput);
		
		for(int i=0; i<MULTI_NS_INPUT.length(); i++) {
			if( JSONFilterInputStreamTest.MULTI_NS_INPUT_BYTES[i] != bites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}		

	}

	@Test
	public void readArrayWithMultipleNamespaceWithRoot() throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(JSONFilterInputStreamTest.MULTI_NS_INPUT.getBytes());
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName("getName"), Charset.forName("US-ASCII"));

		byte[] bites = new byte[8096];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read(bites, totalRead, bites.length-totalRead);
			if( numRead == -1 ) {
				break;
			}
			totalRead += numRead;
		}
		
		String actualOutput = new String(bites, "US-ASCII");
		System.out.println("exp    output: " + MULTI_NS_INPUT);
		System.out.println("actual output: " + actualOutput);
		
		for(int i=0; i<MULTI_NS_INPUT.length(); i++) {
			if( JSONFilterInputStreamTest.MULTI_NS_INPUT_BYTES[i] != bites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}		

	}

	
	@Test
	public void readArrayWithMultipleNamespaceWithNoRoot() throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(JSONFilterInputStreamTest.MULTI_NS_INPUT_NO_ROOT.getBytes());
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(JSONFilterInputStreamTest.MULTI_NS_INPUT_ROOT), Charset.forName("US-ASCII"));

		byte[] bites = new byte[8096];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read(bites, totalRead, bites.length-totalRead);
			if( numRead == -1 ) {
				break;
			}
			totalRead += numRead;
		}
		
		String actualOutput = new String(bites, "US-ASCII");
		System.out.println("exp    output no root: " + MULTI_NS_NO_ROOT_EXPECTED_OUTPUT);
		System.out.println("actual output no root: " + actualOutput);
		
		for(int i=0; i<MULTI_NS_NO_ROOT_EXPECTED_OUTPUT.length(); i++) {
			if( JSONFilterInputStreamTest.MULTI_NS_NO_ROOT_EXPECTED_OUTPUT_BYTES[i] != bites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}		

	}

	@Test
	public void readWithMultipleNamespaceWithNoRoot() throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(JSONFilterInputStreamTest.MULTI_NS_INPUT_NO_ROOT.getBytes());
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(JSONFilterInputStreamTest.MULTI_NS_INPUT_ROOT), Charset.forName("US-ASCII"));

		byte[] bites = new byte[8096];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read();
			if( numRead == -1 ) {
				break;
			}
			bites[totalRead++] = (byte)numRead;
		}
		
		String actualOutput = new String(bites, "US-ASCII");
		System.out.println("exp    output no root: " + MULTI_NS_NO_ROOT_EXPECTED_OUTPUT);
		System.out.println("actual output no root: " + actualOutput);
		
		for(int i=0; i<MULTI_NS_NO_ROOT_EXPECTED_OUTPUT.length(); i++) {
			if( JSONFilterInputStreamTest.MULTI_NS_NO_ROOT_EXPECTED_OUTPUT_BYTES[i] != bites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}		

	}

	
	@Test
	public void readArrayWithMultipleNamespaceWithNoRoot2() throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(MULTI_NS_INPUT_NO_ROOT_2.getBytes());
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(MULTI_NS_INPUT_ROOT_2), Charset.forName("US-ASCII"));

		byte[] bites = new byte[8096];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read(bites, totalRead, bites.length-totalRead);
			if( numRead == -1 ) {
				break;
			}
			totalRead += numRead;
		}
		
		String actualOutput = new String(bites, "US-ASCII");
		System.out.println("exp    output space: " + MULTI_NS_NO_ROOT_EXPECTED_OUTPUT_2);
		System.out.println("actual output space: " + actualOutput);
		
		for(int i=0; i<MULTI_NS_NO_ROOT_EXPECTED_OUTPUT_2.length(); i++) {
			if( MULTI_NS_NO_ROOT_EXPECTED_OUTPUT_BYTES_2[i] != bites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}		

	}

	@Test
	public void readWithMultipleNamespaceWithNoRoot_2() throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(MULTI_NS_INPUT_NO_ROOT_2.getBytes());
		JSONFilterInputStream jfin = new JSONFilterInputStream(bais, new QName(MULTI_NS_INPUT_ROOT_2), Charset.forName("US-ASCII"));

		byte[] bites = new byte[8096];
		int totalRead = 0;
		
		while(true) {
			int numRead = jfin.read();
			if( numRead == -1 ) {
				break;
			}
			bites[totalRead++] = (byte)numRead;
		}
		
		String actualOutput = new String(bites, "US-ASCII");
		System.out.println("exp    output no root: " + MULTI_NS_NO_ROOT_EXPECTED_OUTPUT_2);
		System.out.println("actual output no root: " + actualOutput);
		
		for(int i=0; i<MULTI_NS_NO_ROOT_EXPECTED_OUTPUT_2.length(); i++) {
			if( JSONFilterInputStreamTest.MULTI_NS_NO_ROOT_EXPECTED_OUTPUT_BYTES_2[i] != bites[i] ) {
				System.out.println("mismatch at " + i);
				Assert.fail("mismatch at " + i);
			}
		}		

	}
	
}
