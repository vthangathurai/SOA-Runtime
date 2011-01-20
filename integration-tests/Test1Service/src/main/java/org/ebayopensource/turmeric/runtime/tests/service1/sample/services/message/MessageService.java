/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message;

import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;


/**
 * The first testing service. It supports two operations
 * 
 * AddMessage: to add a message;
 * GetAllMessages: to retrive all messages.
 * @author wdeng
 *
 */
public class MessageService {
	
	public MyMessage AddMessage(MyMessage newMessage) {
		return new MyMessage();
	}

	public List<MyMessage> getAllMessages() {
		return new ArrayList<MyMessage> ();
	}
}
