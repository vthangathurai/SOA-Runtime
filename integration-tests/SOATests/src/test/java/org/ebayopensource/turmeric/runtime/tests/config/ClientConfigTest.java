/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.config;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.GlobalConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.SchemaValidationLevel;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ParseUtils;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigHolder;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageProcessor;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDescFactory;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.junit.Test;


public class ClientConfigTest extends AbstractTurmericTestCase {
	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void clientConfig() throws Exception {
		StringBuffer output = new StringBuffer();
		ClientConfigManager configManager = ClientConfigManager.getInstance();
		try {
			for (int i = 1; i <= 5; i++) { // test 6 is not really valid - namespace must be declared
				String testcase = "configtest" + String.valueOf(i);
				try {
					configManager.setConfigTestCase(testcase, "testconfig");
				} catch (ServiceCreationException e) {
					if (i==6 && ParseUtils.getSchemaValidationLevel().equals(SchemaValidationLevel.ERROR)) {

					} else {
						throw e;
					}
				}
				Collection<String> allConfig = configManager.getAllServiceAdminNames(SOAConstants.DEFAULT_CLIENT_NAME);
				output.append("Test case: " + testcase + '\n');
				for (String svcName : allConfig) {
					ClientConfigHolder cch = configManager.getConfigForUpdate(svcName, null);
					cch.dump(output);
					GlobalConfigHolder gch = configManager.getGlobalConfigForUpdate();
					gch.dump(output);
				}
			}

			CompareUtils.writeOutputFile(this.getClass(), output, "client");
			String compareString = CompareUtils.getCompareString(this.getClass(), "client.compare.txt");
			assertEquals(compareString, output.toString());
		} finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void locationMapping_Success() throws Exception {
		ClientConfigManager configManager = ClientConfigManager.getInstance();
		configManager.setConfigTestCase("configlocationmaptest", "testconfig");
		Collection<String> allConfig = configManager.getAllServiceAdminNames(SOAConstants.DEFAULT_CLIENT_NAME);
		ClientConfigHolder cch = configManager.getConfigForUpdate("test2", null);
		Map<String, String> serviceLocationMap = cch.getServiceLocationMap();
		assertEquals(serviceLocationMap.size(), 2);
		assertNotNull(serviceLocationMap.get("feature"));
		assertNotNull(serviceLocationMap.get("dev"));
		assertEquals(serviceLocationMap.get("feature"), "http://fp001-v3a.qa.ebay.com:8080/ws/spf");
		assertEquals(serviceLocationMap.get("dev"), "http://d-sjc-xhan2.corp.com:8080/ws/spf");
		
	}
	
	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void locationMappingNegativeCase() throws Exception {
		ClientConfigManager configManager = ClientConfigManager.getInstance();
		configManager.setConfigTestCase("configlocationmapnegativetest", "testconfig");
		try{
			Collection<String> allConfig = configManager.getAllServiceAdminNames(SOAConstants.DEFAULT_CLIENT_NAME);
			fail("Should have received ServiceCreationException");
		}
		catch (ServiceCreationException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void consumerIdCheckCCH_Success() throws Exception {
		ClientConfigManager configManager = ClientConfigManager.getInstance();
		try {
			
			configManager.setConfigTestCase("configtest5", "config");
			Collection<String> allConfig = configManager.getAllServiceAdminNames("consumerid");
			ClientConfigHolder cch = configManager.getConfig("test1", "consumerid");
			assertEquals("consumerId value should replace useCase value", "a:testConsumerId", cch.getInvocationUseCase());
			assertEquals("consumerId value is incorrect", "testConsumerId", cch.getConsumerId());
		} 
		finally {
			configManager.setConfigTestCase("config");
		}
		
	}
	
	@Test
	public void consumerIdCheckClientServiceDesc_Success() throws Exception {
		ClientMessageProcessor.getInstance();
		ClientConfigManager configManager = ClientConfigManager.getInstance();
		try {
			
			configManager.setConfigTestCase("configtest5", "config");
			ClientServiceDesc serviceDesc = ClientServiceDescFactory.getInstance().getServiceDesc("test1", "consumerid");
			assertEquals("unexpected consumer_id", "testConsumerId", serviceDesc.getConsumerId());
			assertEquals("unexpected useCase value", "a:testConsumerId", serviceDesc.getUseCase());			
		} 
		finally {
			configManager.setConfigTestCase("config");
		}
		
	}
	
}