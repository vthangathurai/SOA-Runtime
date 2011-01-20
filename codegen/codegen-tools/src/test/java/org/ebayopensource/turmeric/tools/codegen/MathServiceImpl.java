/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

public class MathServiceImpl implements MathServiceInterface {

	public int add(int num1, int num2) {
		return num1 +  num2;
	}

	public int muliply(int num1, int num2) {
		return num1 *  num2;
	}

	public double pi() {
		return Math.PI;
	}
	
	public float factor() {
		return 1.25f;
	}
	

}
