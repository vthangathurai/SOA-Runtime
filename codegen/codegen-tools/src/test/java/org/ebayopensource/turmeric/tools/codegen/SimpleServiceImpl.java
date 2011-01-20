/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import javax.activation.DataHandler;

public class SimpleServiceImpl implements SimpleServiceInterface {
   
	public String greetUser(String userName) {
        return "Hello " + userName;
    }
	
    public boolean sendMessage(String message)throws Exception {
    	System.out.println("Message received : " + message);
    	return true;
    }
    
	public double pi() {
		return Math.PI;
	}
	
	public void doSomething(String someThing) {
		System.out.println("Doing some thing ....:-)");
	}
	
	public void doNothing() {
		System.out.println("Sitting idle ...:-(");
	}
    
	
	public DataHandler echoAttachment(DataHandler dl) {
		return null;
	}
	
}
