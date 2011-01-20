/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.attachment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

/**
 * @author wdeng
 */
//TODO: we should move this to the right place.  Is there a similar UUID gen in kernel that we can use?
public class UUIDGenerator {
	/**
	 * This class will give UUIDs.
	 */
	private static String s_baseUUID = null;
	private static long s_incrementingValue = 0;


	private static Random s_myRand = null;

	/**
	 * MD5 a random string with localhost/date etc will return 128 bits
	 * construct a string of 18 characters from those bits.
	 *
	 * @return string
	 */
	public static String getUUID() {
		if (s_baseUUID == null) {
			s_baseUUID = getInitialUUID();
		}
		if (++s_incrementingValue >= Long.MAX_VALUE) {
			s_incrementingValue = 0;
		}
		return "urn:uuid:" + s_baseUUID + new Date().getTime() + s_incrementingValue;
	}

	private static String getInitialUUID() {
		if (s_myRand == null) {
			s_myRand = new Random();
		}

		long rand = s_myRand.nextLong();

		String sid;
		try {
			sid = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			sid = Thread.currentThread().getName();
		}

		StringBuffer sb = new StringBuffer();
		sb.append(sid);
		sb.append(":");
		sb.append(Long.toString(rand));

		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException(
				"Unable to obtain MD5 digest algorithm: " + e.toString(), e);
		}

		md5.update(sb.toString().getBytes());
		byte[] array = md5.digest();

		StringBuffer sb2 = new StringBuffer();
		for (int j = 0; j < array.length; ++j) {
			int b = array[j] & 0xFF;
			sb2.append(Integer.toHexString(b));
		}

		int begin = s_myRand.nextInt();
		if (begin < 0) {
			begin = begin * -1;
		}
		begin = begin % 8;

		return sb2.toString().substring(begin, begin + 18).toUpperCase();
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100000; i++) {
			UUIDGenerator.getInitialUUID();
		}

		for (int i = 0; i < 100000; i++) {
			UUIDGenerator.getUUID();
		}
	}
}
