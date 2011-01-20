/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Some usefull methods for reading URLs or streams and returning the data as
 * Direct Buffers.
 * 
 * @author Ivan Z. Ganza
 * @author Robert Schuster
 * @author Bart LEBOEUF
 */
public class BufferUtil {

	// byte 8 bits
	// short 16 bits
	// int 32 bits
	// long 64 bits
	// char 16 bits
	// float 32 bits
	// double 64 bits

	private static final int BUFFER_SIZE = 1024;

	/**
	 * Tries to open the given URL, get its input stream, returns the data in a
	 * <I><B>direct</B></I> ByteBuffer.
	 * 
	 * @param url
	 *            an <code>URL</code> value
	 * @return a <code>ByteBuffer</code> value with the contacts of the data
	 *         present at URL
	 * @exception IOException
	 *                if an error occurs
	 * @exception MalformedURLException
	 *                if an error occurs
	 */
	public static ByteBuffer readURL(URL url) throws IOException,
			MalformedURLException {
		URLConnection connection = null;
		try {
			connection = url.openConnection();
			return readInputStream(new BufferedInputStream(connection
					.getInputStream()));
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Fully reads the given InputStream, returning its contents as a ByteBuffer.
	 * 
	 * @param in
	 *            an <code>InputStream</code> value
	 * @return a <code>ByteBuffer</code> value
	 * @exception IOException
	 *                if an error occurs
	 */
	public static ByteBuffer readInputStream(InputStream in) throws IOException {
		/*
		 * Converts the InputStream into a channels which allows us to read into
		 * a ByteBuffer.
		 */
		ReadableByteChannel ch = Channels.newChannel(in);

		// Creates a list that stores the intermediate buffers.
		List<ByteBuffer> list = new LinkedList<ByteBuffer>();

		// A variable storing the accumulated size of the data.
		int sum = 0, read = 0;

		/*
		 * Reads all the bytes from the channel and stores them in various
		 * buffer objects.
		 */
		do {
			ByteBuffer b = createByteBuffer(BUFFER_SIZE);
			read = ch.read(b);

			if (read > 0) {
				b.flip(); // make ready for reading later
				list.add(b);
				sum += read;
			}
		} while (read != -1);

		ByteBuffer bb = createByteBuffer(sum);

		/* Merges all buffers into the Buffer bb */
		Iterator<ByteBuffer> ite = list.iterator();
		while (ite.hasNext()) {
			bb.put(ite.next());
		}

		list.clear();

		return bb;
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param capacity initial capacity
	 * @return newly created byte buffer
	 */
	public static ByteBuffer createByteBuffer(int capacity) {
		// direct byte buffer would have been efficient, but
		// the client code uses ByteBuffer.array() without calling hasArray()
		//return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
		return ByteBuffer.allocate(capacity).order(ByteOrder.nativeOrder());
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param values initial values to be copied to the buffer
	 * @return newly created byte buffer
	 */
	public static ByteBuffer createByteBuffer(byte[] values) {
		return createByteBuffer(values.length).put(values);
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param capacity initial capacity
	 * @return newly created float buffer
	 */
	public static FloatBuffer createFloatBuffer(int capacity) {
		return createByteBuffer(capacity * 4).asFloatBuffer();
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param values initial values to be copied to the buffer
	 * @return newly created float buffer
	 */
	public static FloatBuffer createFloatBuffer(float[] values) {
		return createFloatBuffer(values.length).put(values);
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param capacity initial capacity
	 * @return newly created integer buffer
	 */
	public static IntBuffer createIntBuffer(int capacity) {
		return createByteBuffer(capacity * 4).asIntBuffer();
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param values initial values to be copied to the buffer
	 * @return newly created integer buffer
	 */
	public static IntBuffer createIntBuffer(int[] values) {
		return createIntBuffer(values.length).put(values);
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param capacity initial capacity
	 * @return newly created double buffer
	 */
	public static DoubleBuffer createDoubleBuffer(int capacity) {
		return createByteBuffer(capacity * 8).asDoubleBuffer();
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param values initial values to be copied to the buffer
	 * @return newly created double buffer
	 */
	public static DoubleBuffer createDoubleBuffer(double[] values) {
		return createDoubleBuffer(values.length).put(values);
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param capacity initial capacity
	 * @return newly created long buffer
	 */
	public static LongBuffer createLongBuffer(int capacity) {
		return createByteBuffer(capacity * 8).asLongBuffer();
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param values initial values to be copied to the buffer
	 * @return newly created long buffer
	 */
	public static LongBuffer createLongBuffer(long[] values) {
		return createLongBuffer(values.length).put(values);
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param capacity initial capacity
	 * @return newly created short buffer
	 */
	public static ShortBuffer createShortBuffer(int capacity) {
		return createByteBuffer(capacity * 2).asShortBuffer();
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param values initial values to be copied to the buffer
	 * @return newly created short buffer
	 */
	public static ShortBuffer createShortBuffer(short[] values) {
		return createShortBuffer(values.length).put(values);
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param capacity initial capacity
	 * @return newly created character buffer
	 */
	public static CharBuffer createCharBuffer(int capacity) {
		return createByteBuffer(capacity * 2).asCharBuffer();
	}

	/**
	 * Create direct(?) native type buffers.
	 * @param values initial values to be copied to the buffer
	 * @return newly created capacity buffer
	 */
	public static CharBuffer createCharBuffer(char[] values) {
		return createCharBuffer(values.length).put(values);
	}
}
