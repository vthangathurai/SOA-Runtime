/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.sif.logging;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.PrereadingRawDataRecorder;
import org.junit.Test;


public class PrereadingRawDataRecorderTest {

	@Test
	public void clientConfig() throws Exception {
		recorder(3, 10);
		recorder(10, 10);
		recorder(26, 10);
		recorder(3, 25);
		recorder(10, 25);
		recorder(26, 25);
		recorder(3, 26);
		recorder(10, 26);
		recorder(26, 26);
		recorder(3, 27);
		recorder(10, 27);
		recorder(26, 27);
		recorder(3, 28);
		recorder(10, 28);
		recorder(26, 28);
	}

	
	private void recorder(int dribbleSize, int maxBytesToRecord) throws IOException {
		String dataStr = "abcdefghijklmnopqrstuvwxyz";
		byte[] data = dataStr.getBytes();
		ByteArrayInputStream bytestream = new ByteArrayInputStream(data);
		FilterInputStream stream = new DribbleInputStream(bytestream, dribbleSize);
		PrereadingRawDataRecorder recorder = new PrereadingRawDataRecorder(stream, maxBytesToRecord);
		String compare = readOneAtATime(recorder, data);
		assertEquals(dataStr, compare);
		stream.reset();
		recorder = new PrereadingRawDataRecorder(stream, maxBytesToRecord);
		compare = readMultiple(recorder, data, data.length);
		assertEquals(dataStr, compare);
		stream.reset();
		recorder = new PrereadingRawDataRecorder(stream, maxBytesToRecord);
		compare = readMultiple(recorder, data, 5);
		assertEquals(dataStr, compare);
	}

	private String readMultiple(PrereadingRawDataRecorder recorder, byte[] data, int bytesPerRead) throws IOException {
		byte[] output = new byte[data.length*2];
		int bytes = 1;
		int ix = 0;
		while (bytes > 0) {
			bytes = recorder.read(output, ix, bytesPerRead);
			if (bytes > 0) {
				ix += bytes;
			}
		}
		return new String(output, 0, data.length);
	}

	private String readOneAtATime(PrereadingRawDataRecorder recorder, byte[] data) throws IOException {
		byte[] output = new byte[data.length];
		int ix = 0;
		int c = 1;
		while (c > 0) {
			c = recorder.read();
			if (c > 0) {
				output[ix++] = (byte) c;
			}
		}
		return new String(output);
	}
	
	private class DribbleInputStream extends FilterInputStream {
		int m_dribbleSize;
		DribbleInputStream(InputStream in, int dribbleSize) {
			super(in);
			m_dribbleSize = 3;
		}
	    public int read(byte b[], int off, int len) throws IOException {
	    	if (len > m_dribbleSize) {
	    		len = m_dribbleSize;
	    	}
	    	return in.read(b, off, len);
	    }
	}
}