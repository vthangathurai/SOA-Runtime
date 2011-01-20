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
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.junit.Test;


public class ValidateServiceSchemas extends AbstractTurmericTestCase  {
	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void clientConfig() throws Exception {
		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
		configManager.setConfigTestCase("configremote", "testconfig");
		configManager.setConfigTestCase("config-no-g11n", "testconfig");
		configManager.setConfigTestCase("config-nested-service", "config-nested-service");
		configManager.setConfigTestCase("configtest1", "testconfig");
		configManager.setConfigTestCase("configtest2", "testconfig");
		configManager.setConfigTestCase("configtest3", "testconfig");
		configManager.setConfigTestCase("configtest4", "testconfig");
		configManager.setConfigTestCase("configtest5", "testconfig");
		configManager.setConfigTestCase("confignegative3", "testconfig");
		configManager.setConfigTestCase("configtypeconvert", "configtypeconvert");
		try {
			configManager.setConfigTestCase("confignegative5", "testconfig");
		} catch (ServiceCreationException e) {
			// expected
		}
		
		try {
			configManager.setConfigTestCase("confignegative6", "testconfig");
//			configManager.setConfigTestCase("confignegative7", "testconfig");
		} catch (ServiceCreationException e) {
			// expected
		} finally {
			configManager.setConfigTestCase("config", "config");
		}
	}

}