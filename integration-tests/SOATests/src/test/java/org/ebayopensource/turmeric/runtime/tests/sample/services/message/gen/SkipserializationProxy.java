/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.tests.sample.services.message.gen;

import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.gen.Test1Proxy;


/**
 * @author wdeng
 *
 */
public class SkipserializationProxy extends Test1Proxy {
	public SkipserializationProxy(Service service) {
		super(service);
	}

}
