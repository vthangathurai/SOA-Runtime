/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

public abstract class AbstractTestService {
	
	
	public static TestService getInstance() {
		return new TestService();
	}
	
	public String  greetUser(String userName) {
		return "Hello, " + userName;
	}
	
	public boolean sendMessage(String userName) throws Exception {
		String msg = "Please meet me at eBay Cafe";
		return sendSMS(userName, msg);
	}
	
	
	public boolean sendMoney(String userName, double amount) throws Exception {
		doNothing();
		return true;
	}	
		
	protected boolean sendSMS(String userName, String msg) {
		doNothing();
		boolean success = true;
		
		return success;
	}
	
	private void doNothing() {		
	}
}
