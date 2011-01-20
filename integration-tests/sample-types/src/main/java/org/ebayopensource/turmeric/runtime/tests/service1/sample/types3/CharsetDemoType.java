/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.types3;

public class CharsetDemoType {
	protected String myString;
	protected char myChar;
	protected byte[] myByteArray;
	
	public byte[] getMyByteArray() {
		return myByteArray;
	}
	public void setMyByteArray(byte[] myByteArray) {
		this.myByteArray = myByteArray;
	}
	public char getMyChar() {
		return myChar;
	}
	public void setMyChar(char myChar) {
		this.myChar = myChar;
	}
	public String getMyString() {
		return myString;
	}
	public void setMyString(String myString) {
		this.myString = myString;
	}
}
