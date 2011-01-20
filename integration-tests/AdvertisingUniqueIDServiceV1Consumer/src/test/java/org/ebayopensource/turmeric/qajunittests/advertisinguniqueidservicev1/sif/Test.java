/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.sif;

import org.ebayopensource.turmeric.advertising.v1.services.EchoMessageRequest;
import org.ebayopensource.turmeric.advertisinguniqueservicev1.AdvertisingUniqueIDServiceV1SharedConsumer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;

public class Test {

	public static void main(String[] args) throws ServiceException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "ESB1");
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("vasu");
//		client.getService().getRequestContext().setTransportHeader("X-TURMERIC-OPERATION-NAME", "echoMessage");
		System.out.println(client.echoMessage(param0).getOut());
	}
}
