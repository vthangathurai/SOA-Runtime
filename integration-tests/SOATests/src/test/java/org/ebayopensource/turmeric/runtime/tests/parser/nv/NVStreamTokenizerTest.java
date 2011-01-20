/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.parser.nv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVLine;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVPathPart;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVStreamParser;
import org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb.DataBindingFacade;
import org.junit.Test;

import com.ebay.kernel.util.FastURLEncoder;
import com.ebay.kernel.util.URLDecoder;

/**
 * @author wdeng
 */
public class NVStreamTokenizerTest{

	private void doTest(String input, String goldResult) throws Exception {
		doTest(input, goldResult, null);
	}

	private void doTest(String input, String goldResult, QName impliedRoot) throws Exception {
		byte[] inputData = input.getBytes();
		InputStream is = new ByteArrayInputStream(inputData);
		NamespaceConvention convention = DataBindingFacade.createDeserializationNSConvention(null);
		NVStreamParser lexer = new NVStreamParser(is, Charset.forName("ASCII"), convention);

		NVLine prevLine = null;
		NVPathPart impliedRoot2 = (impliedRoot != null ? new NVPathPart(impliedRoot, 0, false) : null);
		StringBuffer result = new StringBuffer();
		while (lexer.parseLine()) {
			NVLine line = NVLine.createNext(lexer, prevLine, null, impliedRoot2);

			if (result.length() > 0) {
				result.append("\n");
			}

			result.append("depth=");
			result.append(line.getDepth());

			if (line.isAttribute()) {
				if (line.isAttributeAtSameLevel()) {
					result.append(";attr_same_level");
				} else {
					result.append(";attr");
				}
			}

			result.append(";valley=");
			result.append(line.getValleyLevel());
			result.append(";value=");
			result.append(line.getValue());

			result.append(";path=");
			for (int i=0; i<line.getDepth(); i++) {
				NVPathPart part = line.getPathPart(i);

				if (i > 0) {
					result.append('.');
				}

				String prefix = convention.getPrefix(part.getNamespaceURI());
				if (prefix.length() != 0) {
					result.append(prefix);
					result.append(':');
				}

				result.append(part.getLocalPart());
				result.append('[');
				result.append(part.getIndex());
				result.append(']');
			}

			prevLine = line;
		}

		System.out.println("Gold result = '" + goldResult + "'");
		System.out.println("Actual Result = '" + result + "'");		

		assertEquals(goldResult, result.toString());
	}

	@Test
	public void tokenizer() throws Exception {
		System.out.println("**** Starting testTokenizer");

		String input =
			"nvns:tm=http://mynamespace/tm&nvns:c=http://mynamespace/c&" +
			"tm:message.c:body=\"SOA+SOA%2C+SOS.\"&" +
			"tm:message.recipient.city=\"San+Jose\"&" +
			"tm:message.recipient(0).c:emailAddress=\"soa%40ebay.com\"&" +
			"tm:message.recipient(0).postCode=\"95125\"&" +
			"tm:message.recipient(0).state=\"CA\"&" +
			"tm:message.recipient(0).streetNumber=\"0000\"&" +
			"tm:message.recipient(0).city=\"San+Jose\"&" +
			"tm:message.recipient(1).c:emailAddress=\"soa%40ebay.com\"&" +
			"tm:message.recipient(1).postCode=\"95125\"&" +
			"tm:message.recipient(1).state=\"CA\"&" +
			"tm:message.recipient(1).streetNumber=\"1111\"&" +
			"tm:message.subject=\"Test+SOA+JAXB+XML+ser%2Fdeser\"&";

		String goldResult =
			"depth=2;valley=-1;value=SOA SOA, SOS.;path=tm:message[0].c:body[0]\n" +
			"depth=3;valley=0;value=San Jose;path=tm:message[0].recipient[0].city[0]\n" +
			"depth=3;valley=1;value=soa@ebay.com;path=tm:message[0].recipient[0].c:emailAddress[0]\n" +
			"depth=3;valley=1;value=95125;path=tm:message[0].recipient[0].postCode[0]\n" +
			"depth=3;valley=1;value=CA;path=tm:message[0].recipient[0].state[0]\n" +
			"depth=3;valley=1;value=0000;path=tm:message[0].recipient[0].streetNumber[0]\n" +
			"depth=3;valley=1;value=San Jose;path=tm:message[0].recipient[0].city[0]\n" +
			"depth=3;valley=0;value=soa@ebay.com;path=tm:message[0].recipient[1].c:emailAddress[0]\n" +
			"depth=3;valley=1;value=95125;path=tm:message[0].recipient[1].postCode[0]\n" +
			"depth=3;valley=1;value=CA;path=tm:message[0].recipient[1].state[0]\n" +
			"depth=3;valley=1;value=1111;path=tm:message[0].recipient[1].streetNumber[0]\n" +
			"depth=2;valley=0;value=Test SOA JAXB XML ser/deser;path=tm:message[0].subject[0]";

		doTest(input, goldResult, null);
		System.out.println("**** Ending testTokenizer");
	}

	@Test
	public void decodeNegativeChar1() throws Exception {
		System.out.println("**** Starting testDecodeNegativeChar1");
		String input = "nvns:tm=http://mynamespace/tm&nvns:c=http://mynamespace/c&tm:message.c:body=\"SOA+SOA%XC+SOS.\"";
		String goldResult = "depth=2;valley=-1;value=SOA SOA%XC SOS.;path=tm:message[0].c:body[0]";
		doTest(input, goldResult);
		System.out.println("**** Ending testDecodeNegativeChar1");
	}
	
	@Test
	public void decodeNegativeChar2() throws Exception {
		System.out.println("**** Starting testDecodeNegativeChar2");
		String input = "nvns:tm=http://mynamespace/tm&nvns:c=http://mynamespace/c&tm:message.c:body=\"SOA+SOA%3XC+SOS.\"";
		String goldResult = "depth=2;valley=-1;value=SOA SOA%3XC SOS.;path=tm:message[0].c:body[0]";
		doTest(input, goldResult);
		System.out.println("**** Ending testDecodeNegativeChar2");
	}
	
	@Test
	public void decodeNegativeUnreadBehaviorDoublePercentageSign() throws Exception {
		System.out.println("**** Starting testDecodeNegativeUnreadBehaviorDoublePercentageSign");
		String input = "nvns:tm=http://mynamespace/tm&nvns:c=http://mynamespace/c&tm:message.c:body=\"SOA+SOA%%%3XC+SOS.\"";
		String goldResult = "depth=2;valley=-1;value=SOA SOA%%%3XC SOS.;path=tm:message[0].c:body[0]";
		doTest(input, goldResult);
		System.out.println("**** Ending testDecodeNegativeUnreadBehaviorDoublePercentageSign");
	}
	
	@Test
	public void decodeNegativeUnreadBehaviorDoublePercentageSignFollowByGoodEncode() throws Exception {
		System.out.println("**** Starting testDecodeNegativeUnreadBehaviorDoublePercentageSignFollowByGoodEncode");
		String input = "body=\"A%%%2C+S\"";
		String goldResult = "depth=1;valley=-1;value=A%%, S;path=body[0]";
		doTest(input, goldResult);
		System.out.println("**** Ending testDecodeNegativeUnreadBehaviorDoublePercentageSignFollowByGoodEncode");
	}

	
	@Test
	public void decodeParenthesisFollowsByEqualSign() throws Exception {
		System.out.println("**** Starting testDecodeParenthesisFollowsByEqualSign");
		String input = "nvns:tm=http://mynamespace/tm&nvns:c=http://mynamespace/c&tm:message.c:body(0)=\"SOA+SOA.\"";
		String goldResult = "depth=2;valley=-1;value=SOA SOA.;path=tm:message[0].c:body[0]";
		doTest(input, goldResult);
		System.out.println("**** Ending testDecodeParenthesisFollowsByEqualSign");
	}
	
	@Test
	public void decodeParenthesisFollowsByDot() throws Exception {
		System.out.println("**** Starting testDecodeParenthesisFollowsByDot");
		String input = "nvns:tm=http://mynamespace/tm&nvns:c=http://mynamespace/c&tm:message(0).c:body=\"SOA+SOA.\"";
		String goldResult = "depth=2;valley=-1;value=SOA SOA.;path=tm:message[0].c:body[0]";
		doTest(input, goldResult);
		System.out.println("**** Ending testDecodeParenthesisFollowsByDot");
	}
	
	@Test
	public void decodeParenthesisFollowsByColonInValue() throws Exception {
		System.out.println("**** Starting testDecodeParenthesisFollowsByColonInValue");
		String input = "nvns:tm=http://mynamespace/tm&nvns:c=http://mynamespace/c&tm:message(0).c:body=\"message(0):message(1)\"";
		String goldResult = "depth=2;valley=-1;value=message(0):message(1);path=tm:message[0].c:body[0]";
		doTest(input, goldResult);
		System.out.println("**** Ending testDecodeParenthesisFollowsByColonInValue");
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void decodeParenthesisFollowsByAmp() throws Exception {
		System.out.println("**** Starting testDecodeParenthesisFollowsByAmp");
		String input = "tm:message(0)&c:body=\"SOA+SOA.\"";
		try {
			doTest(input, "");
			fail("Expecting XMLStreamException but get nothing");
		} catch (XMLStreamException e) {
			if (!"Name data has terminated unexpectedly with the end of line".equals(e.getMessage())) {
				throw e;
			}
			System.out.println("Caught expected: " + e.toString());
		}
		System.out.println("**** Ending testDecodeParenthesisFollowsByAmp");
	}
	
	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void decodeParenthesisFollowsByColon() throws Exception {
		System.out.println("**** Starting testDecodeParenthesisFollowsByColon");
		String input = "tm:message(0):c:body=\"SOA+SOA.\"";
		try {
			doTest(input, "");
			fail("Expecting XMLStreamException but get nothing");
		} catch (XMLStreamException e) {
			if (!"Namespace prefix is not expected after element name".equals(e.getMessage())) {
				throw e;
			}
			System.out.println("Caught expected: " + e.toString());
		}
		System.out.println("**** Ending testDecodeParenthesisFollowsByColon");
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void decodeAmpInValueNotAllowed() throws Exception {
		System.out.println("**** Starting testDecodeAmpInValueNotAllowed");
		String input = "tm:message(0).c:body=\"SOA&SOA.\"";
		try {
			doTest(input, "");
			fail("Expecting XMLStreamException but get nothing");
		} catch (XMLStreamException e) {
			if (!"Value starts with quotation mark, but ends without the same".equals(e.getMessage())) {
				throw e;
			}
			System.out.println("Caught expected: " + e.toString());
		}
		System.out.println("**** Ending testDecodeAmpInValueNotAllowed");
	}
	
	@Test
	public void qNameEquals() {
		System.out.println("**** Starting testQNameEquals");
		QName name = new QName("http://my.test/namespace", "AElementName", "ns");
		QName nameWDiffPrefix = new QName("http://my.test/namespace", "AElementName", "ns1");
		assertEquals(name, nameWDiffPrefix);
		System.out.println("**** Ending testQNameEquals");
	}

	@Test
	public void fastURLEncoder() {
		System.out.println("**** Starting testFastURLEncoder");
		String str = "test url encode:.()&   \\:\\.\\(\\)\\&";
		String encoded = FastURLEncoder.encode(str);
		System.out.println(encoded);
		String decoded = URLDecoder.decode(encoded);
		assertEquals(str, decoded);
		System.out.println("**** Ending testFastURLEncoder");
	}
	
	@Test
	public void ampersandValueParsing1() throws Exception {
		String paramValue1 = "b%26b";
		String paramValue2 = "e";
		
		byte[] inputData = ("a=" + paramValue1 +  "&d=" + paramValue2).getBytes();
		InputStream is = new ByteArrayInputStream(inputData);
		NamespaceConvention convention = DataBindingFacade.createDeserializationNSConvention(null);
		NVStreamParser parser = new NVStreamParser(is, Charset.forName("ASCII"), convention);
		
		parser.parseLine();
		String actualParam1 = parser.getValue();
		assertEquals("b&b", actualParam1);
		
		parser.parseLine();
		String actualParam2 = parser.getValue();
		assertEquals("e", actualParam2);
		
	}

	@Test
	public void ampersandValueParsing2() throws Exception {
		String paramValue1 = "%26%26%26";
		
		byte[] inputData = ("a=" + paramValue1).getBytes();
		InputStream is = new ByteArrayInputStream(inputData);
		NamespaceConvention convention = DataBindingFacade.createDeserializationNSConvention(null);
		NVStreamParser parser = new NVStreamParser(is, Charset.forName("ASCII"), convention);
		
		parser.parseLine();
		String actualParam1 = parser.getValue();
		assertEquals("&&&", actualParam1);
	
	}

	@Test
	public void ampersandValueParsing3() throws Exception {
		String paramValue1 = "";
		
		byte[] inputData = ("a=" + paramValue1 + "&" + "x=y").getBytes();
		InputStream is = new ByteArrayInputStream(inputData);
		NamespaceConvention convention = DataBindingFacade.createDeserializationNSConvention(null);
		NVStreamParser parser = new NVStreamParser(is, Charset.forName("ASCII"), convention);
		
		parser.parseLine();
		String actualParam1 = parser.getValue();
		assertEquals("", actualParam1);
		parser.parseLine();
	
	}


	@Test
	public void percentageSignValueParsing4() throws Exception {
		String paramValue1 = "b%25c";
		
		byte[] inputData = ("a=" + paramValue1 + "&" + "x=y").getBytes();
		InputStream is = new ByteArrayInputStream(inputData);
		NamespaceConvention convention = DataBindingFacade.createDeserializationNSConvention(null);
		NVStreamParser parser = new NVStreamParser(is, Charset.forName("ASCII"), convention);
		
		parser.parseLine();
		String actualParam1 = parser.getValue();
		assertEquals("b%c", actualParam1);
	
	}

	
	@Test
	public void ampersandValueParsing5() throws Exception {
		String paramValue1 = "a=b&c=d%26&e=f";
		
		byte[] inputData = (paramValue1).getBytes();
		InputStream is = new ByteArrayInputStream(inputData);
		NamespaceConvention convention = DataBindingFacade.createDeserializationNSConvention(null);
		NVStreamParser parser = new NVStreamParser(is, Charset.forName("ASCII"), convention);
		
		parser.parseLine();
		String actualParam1 = parser.getValue();
		assertEquals("b", actualParam1);

		parser.parseLine();
		String actualParam2 = parser.getValue();
		assertEquals("d&", actualParam2);

		parser.parseLine();
		String actualParam3 = parser.getValue();
		assertEquals("f", actualParam3);

	}

	

}
