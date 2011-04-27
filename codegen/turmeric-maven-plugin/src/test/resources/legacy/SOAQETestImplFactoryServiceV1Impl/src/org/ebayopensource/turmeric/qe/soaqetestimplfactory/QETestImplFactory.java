/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package org.ebayopensource.turmeric.qe.soaqetestimplfactory;

import org.ebayopensource.turmeric.qe.soaqetestimplfactoryservice.soaqetestimplfactoryservice.impl.QETestErrorImplFactory1;
import org.ebayopensource.turmeric.qe.soaqetestimplfactoryservice.soaqetestimplfactoryservice.impl.QETestImpl1;
import org.ebayopensource.turmeric.qe.soaqetestimplfactoryservice.soaqetestimplfactoryservice.impl.QETestImpl2;
import org.ebayopensource.turmeric.qe.soaqetestimplfactoryservice.soaqetestimplfactoryservice.impl.QETestImpl3;
import org.ebayopensource.turmeric.qe.soaqetestimplfactoryservice.soaqetestimplfactoryservice.impl.QETestImplError;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ServiceImplFactory;


public class QETestImplFactory implements ServiceImplFactory<Object> {
	@Override
	public Object createServiceImpl(MessageContext context)
			throws ServiceException {
		String i = context.getRequestMessage().getTransportHeader("Impl-Class");
		if (i.contentEquals("1")) {
			return new QETestImpl1();
		} else if (i.contentEquals("2")) {
			return new QETestImpl2();
		} else if (i.contentEquals("3")) {
			return new QETestImpl3();
		} else if (i.contentEquals("4")) {
			return new QETestErrorImplFactory1();	
		} else {
			return new QETestImplError();
		}
		
	}

}

