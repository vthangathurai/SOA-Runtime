/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package com.ebay.lnptest.soaframework.binding.jaxb;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import com.ebay.soaframework.common.binding.DataBindingDesc;
import com.ebay.soaframework.common.binding.Deserializer;
import com.ebay.soaframework.common.binding.DeserializerFactory;
import com.ebay.soaframework.common.binding.SerializerFactory;
import com.ebay.soaframework.common.impl.binding.jaxb.fastinfoset.JAXBFastInfosetDeserializerFactory;
import com.ebay.soaframework.common.impl.binding.jaxb.fastinfoset.JAXBFastInfosetSerializerFactory;
import com.ebay.soaframework.common.impl.binding.jaxb.json.JAXBJSONDeserializerFactory;
import com.ebay.soaframework.common.impl.binding.jaxb.json.JAXBJSONSerializerFactory;
import com.ebay.soaframework.common.impl.binding.jaxb.nv.JAXBNVDeserializerFactory;
import com.ebay.soaframework.common.impl.binding.jaxb.nv.JAXBNVSerializerFactory;
import com.ebay.soaframework.common.impl.binding.jaxb.xml.JAXBXMLDeserializerFactory;
import com.ebay.soaframework.common.impl.binding.jaxb.xml.JAXBXMLSerializerFactory;
import com.ebay.soaframework.common.pipeline.InboundMessage;
import com.ebay.soaframework.common.pipeline.Message;
import com.ebay.soaframework.common.pipeline.MessageContext;
import com.ebay.soaframework.common.types.SOAHeaders;
import com.ebay.test.soaframework.binding.jaxb.BaseSerDeserTests;
import com.ebay.test.soaframework.binding.jaxb.JAXBTestHelper;
import com.ebay.test.soaframework.sample.types1.MyMessage;
import com.ebay.test.soaframework.util.TestUtils;

public class JAXBDataBindingTimingTests extends BaseSerDeserTests {

	private static final SerializerFactory fiSerFactory = new JAXBFastInfosetSerializerFactory();
	private static final DeserializerFactory fiDeserFactory = new JAXBFastInfosetDeserializerFactory();

	private static final SerializerFactory xmlSerFactory = new JAXBXMLSerializerFactory();
	private static final DeserializerFactory xmlDeserFactory = new JAXBXMLDeserializerFactory();

	private static final SerializerFactory jsonStreamSerFactory = new JAXBJSONSerializerFactory();
	private static final DeserializerFactory jsonStreamDeserFactory = new JAXBJSONDeserializerFactory();

	private static final SerializerFactory nvSerFactory = new JAXBNVSerializerFactory();
	private static final DeserializerFactory nvDeserFactory = new JAXBNVDeserializerFactory();

	public JAXBDataBindingTimingTests(String testName) {
		super();
		//UnitTestContext.getInstance().addModules(new ModuleInterface[] {com.ebay.kernel.Module.getInstance()});
	}

	public void testJAXBBindingTiming() throws Exception {
		System.out.println("**** Starting testJAXBBindingTiming");
		MyMessage msg = TestUtils.createTestMessage(3);

		// Warn up
		doTimingTest(msg, false, fiDeserFactory, fiSerFactory, 1, null);
		doTimingTest(msg, false, xmlDeserFactory, xmlSerFactory, 1, null);
		doTimingTest(msg, false, jsonStreamDeserFactory, jsonStreamSerFactory, 1, null);
		doTimingTest(msg, false, nvDeserFactory, nvSerFactory, 1, null);
		doTimingTest(msg, true,  nvDeserFactory, nvSerFactory, 1, null);

		// Time it.
		doTimingTest(msg, false, fiDeserFactory, fiSerFactory, 100, "Fast Infoset");
		doTimingTest(msg, false, xmlDeserFactory, xmlSerFactory, 100, "Wstx XML");
		doTimingTest(msg, false, jsonStreamDeserFactory, jsonStreamSerFactory, 100, "Streaming JSON");
		doTimingTest(msg, false, nvDeserFactory, nvSerFactory, 100, "NV");
		doTimingTest(msg, true,  nvDeserFactory, nvSerFactory, 100, "Ordered NV");
		System.out.println("**** Ending testJAXBBindingTiming");
	}
	
	public void testTimingWithDifferentPayloadSize() throws Throwable {
		jaxbBindingTimingSubStructRepeatnTimes(3);
		jaxbBindingTimingSubStructRepeatnTimes(50);
		jaxbBindingTimingSubStructRepeatnTimes(100);
		jaxbBindingTimingSubStructRepeatnTimes(150);
	}

	public void jaxbBindingTimingSubStructRepeatnTimes(int n) throws Throwable {
		//System.setProperty("com.ebay.jni.JniDirectory", "D:/views/ichernyshev_soa/BuildOutput/modules50/jni");
		//com.ebay.jni.memtrace.NativeMemTrace.initialize();
		//com.ebay.jni.memtrace.NativeMemTrace.setThreadExecSamplingInterval(50);

		System.out.println("**** Starting jaxbBindingTimingSubStructRepeatnTimes n=" + n);
		MyMessage msg = TestUtils.createTestMessage(n);

		int warmupLength = 5;
		for (int i=0; i<warmupLength; i++) {
			long memUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			memUsage /= 1024;
			System.out.println("Warming up: cycle " + (i+1) + " of " + warmupLength +
				". Mem usage = " + memUsage + "KB");
			for (int j=0; j<200; j++) {
				doTimingTest(msg, false, fiDeserFactory, fiSerFactory, 1, null);
				doTimingTest(msg, false, xmlDeserFactory, xmlSerFactory, 1, null);
				doTimingTest(msg, false, jsonStreamDeserFactory, jsonStreamSerFactory, 1, null);
				doTimingTest(msg, false, nvDeserFactory, nvSerFactory, 1, null);
				doTimingTest(msg, true,  nvDeserFactory, nvSerFactory, 1, null);
			}
		}

		runGc();

		long memUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		memUsage /= 1024;
		System.out.println("Measuring time. Mem usage = " + memUsage + "KB");

		doTimingTest(msg, false, fiDeserFactory, fiSerFactory, 1000, "Fast Infoset");
		doTimingTest(msg, false, xmlDeserFactory, xmlSerFactory, 1000, "Wstx XML");
		doTimingTest(msg, false, jsonStreamDeserFactory, jsonStreamSerFactory, 1000, "Streaming JSON");
		doTimingTest(msg, false, nvDeserFactory, nvSerFactory, 1000, "NV");
		doTimingTest(msg, true,  nvDeserFactory, nvSerFactory, 1000, "Ordered NV");
		doTimingTest(msg, false, nvDeserFactory, nvSerFactory, 1000, "NV IR", true);
		doTimingTest(msg, true,  nvDeserFactory, nvSerFactory, 1000, "Ordered NV IR", true);

/*		com.ebay.util.MemTraceTestUtils.enableCpuThreadSampling();
		System.out.println("Sampling CPU");
		//doTimingTest(msg, true,  xmlDeserFactory, xmlSerFactory, 3500, "Wstx XML", true);
		doTimingTest(msg, true,  nvDeserFactory, nvSerFactory, 3500, "Ordered NV IR", true);
		com.ebay.util.MemTraceTestUtils.disableThreadSamplingAndPrintResults();*/

		System.out.println("**** Ending jaxbBindingTimingSubStructRepeatnTimes");
	}

	private void runGc() throws Exception{
		System.gc();
		System.gc();
		Thread.sleep(3000);
		System.gc();
	}

	private void doTimingTest(Object msg, boolean ordered,
		DeserializerFactory deserF, SerializerFactory serF,
		int times, String title)
		throws Exception
	{
		doTimingTest(msg, ordered, deserF, serF, times, title, false);
	}

	private void doTimingTest(Object msg, boolean ordered,
		DeserializerFactory deserF, SerializerFactory serF,
		int times, String title, boolean useImpliedRoot)
		throws Exception
	{
		this.m_deserFactory = deserF;
		this.m_serFactory = serF;
		super.setUp();
		DataBindingDesc xmlDbDesc = new DataBindingDesc(deserF.getPayloadType(), TestUtils.getMimeType(deserF.getPayloadType()), serF, deserF, null, null, null, null);

		if (title != null) {
			runGc();
		}

		long serStart = System.nanoTime();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (int i=0; i< times; i++) {
			MessageContext ctx = JAXBTestHelper.createTestMessageContext(
				ordered, serF, deserF, xmlDbDesc, xmlDbDesc, null);
			ctx.getRequestMessage();
			out.reset();
			JAXBTestHelper.serialize(ctx, out, msg);
		}
		double serTime = System.nanoTime() - serStart;

		byte[] data = out.toByteArray();
		//Object resultMsg = null;

		if (title != null) {
			runGc();
		}

		long deserStart = System.nanoTime();
		for (int i=0; i< times; i++) {
			MessageContext ctx = JAXBTestHelper.createTestMessageContext(
				ordered, serF, deserF, xmlDbDesc, xmlDbDesc, data, null, "myTestOperation");
			Message inMsg = ctx.getRequestMessage();
			inMsg.setTransportHeader(SOAHeaders.NV_IMPLIED_ROOT, String.valueOf(useImpliedRoot));

			Deserializer deser = deserF.getDeserializer();
			deser.deserialize((InboundMessage)ctx.getRequestMessage(), MyMessage.class);
		}
		double deserTime = System.nanoTime() - deserStart;

//		assertEquals(msg, resultMsg);

		//if (loggingOn) {
		//	String xml1 = out.toString();
		//	System.out.println(xml1);
		//}

		if (title != null) {
			serTime /= (double)times;
			deserTime /= (double)times;
			serTime /= 1000000.0;
			deserTime /= 1000000.0;

			System.out.format("%1$20s -- \tser: %2$10f  \tdeser: %3$10f \tmsg size: %4$6d\n",
				title, new Double(serTime), new Double(deserTime), new Integer(data.length));
		}
	}

	public static void main (String[] argv) throws Exception {
		JAXBDataBindingTimingTests testObject = new JAXBDataBindingTimingTests("LnP test");
		MyMessage msg = TestUtils.createTestMessage(1);
		for (int i=0; i<10000; i++) {
			testObject.doTimingTest(msg, true,  nvDeserFactory, nvSerFactory, 1, null);
		}
		System.out.println("Done");
		try {
			Thread.sleep(1000000000);
		} catch (Exception e) {}
	}
}
