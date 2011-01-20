/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.config;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.runtime.tests.common.util.ExceptionUtils;
import org.junit.Test;


public class ClientConfigNegativeTest extends AbstractTurmericTestCase {
	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void clientConfig() throws Exception {
		ClientConfigManager configManager = ClientConfigManager.getInstance();
		try {
			configManager.setConfigTestCase("confignegative1", "testconfig");
		} catch (ServiceCreationException e) {
			ExceptionUtils.checkException(e, ServiceCreationException.class, "Unable to load file: META-INF/soa/client/confignegative1/default/ClientConfig.xml");
		}
		try {
			configManager.setConfigTestCase("confignegative2", "testconfig");
		} catch (ServiceCreationException e) {
			ExceptionUtils.checkException(e, ServiceCreationException.class, "Error parsing configuration file META-INF/soa/client/confignegative2/default/ClientConfig.xml: org.xml.sax.SAXParseException");
		}
		try {
			configManager.setConfigTestCase("confignegative3", "testconfig");
		} catch (ServiceCreationException e) {
			ExceptionUtils.checkException(e, ServiceCreationException.class, "client-instance-config line is missing in the config file: META-INF/soa/client/confignegative3/default/ClientConfig.xml");
		}
		// We no longer error out missing client-instance-config.
//		try {
//			configManager.setConfigTestCase("confignegative4");
//		} catch (ServiceCreationException e) {
//			ExceptionUtils.checkException(e, ServiceCreationException.class, "client-instance-config line is missing in the config file: META-INF/soa/client/confignegative4/default/ClientConfigGroups.xml");
//		}
		try {
			configManager.setConfigTestCase("confignegative5", "testconfig");
		} catch (ServiceCreationException e) {
			ExceptionUtils.checkException(e, ServiceCreationException.class, "Cannot find group: NoSuchGroup");
		}
		try {
			configManager.setConfigTestCase("confignegative6", "testconfig");
		} catch (ServiceCreationException e) {
			ExceptionUtils.checkException(e, ServiceCreationException.class, "Can't find chain: NoSuchChain");
		}
		try {
			configManager.setConfigTestCase("confignegative7", "testconfig");
		} catch (ServiceCreationException e) {
			ExceptionUtils.checkException(e, ServiceCreationException.class, "handler 'Logging' is missing classname in the pipeline section: request-handlers");
		} finally {
			configManager.setConfigTestCase("config");
		}
	}

}