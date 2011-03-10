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
import java.util.Set;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceNotFoundException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.GlobalConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ParseUtils;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigHolder;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.RequestParamsDescriptor;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.runtime.tests.common.util.ExceptionUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.AfterClass;


public class ServiceConfigTest  extends AbstractTurmericTestCase {
	public ServiceConfigTest() {
		System.setProperty(ParseUtils.SYS_PROP_CONFIG_SCHEMA_CHECK, "ERROR");
	}

	@After
	public void tearDown() throws Exception {
		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
		configManager.setConfigTestCase("config");
	}

    @AfterClass
    public static void setPropertyToNone() throws Exception {
		System.setProperty(ParseUtils.SYS_PROP_CONFIG_SCHEMA_CHECK, "NONE");
	}
    
    @Test 
	public void testRequestParamMapping() throws Exception {
		
		System.setProperty(ParseUtils.SYS_PROP_CONFIG_SCHEMA_CHECK, "WARNING");
		ParseUtils.reloadSchemaCheckLevel();
		
		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
		configManager.setConfigTestCase("request-param", "testconfig");
		String goodService = "good-service";
		
		try {
			configManager.getConfig(goodService);
		} catch (Throwable e) {
			fail("Failed to find valid service: " + goodService.toString() + " " + e.getMessage());
		}	
		
		try {
			ServiceConfigHolder cch = configManager.getConfigForUpdate(goodService);
			RequestParamsDescriptor rpDesc = cch.getRequestParamsDescriptor();			
			
			// aliases check
			System.out.println(rpDesc.getRequestParams("swim").aliases());
			System.out.println(rpDesc.getRequestParams("dive").aliases());
			Map<String, String> aliases = rpDesc.getRequestParams("swim").aliases();
			
			assertTrue(aliases.containsKey("dist"));
			assertTrue(aliases.containsKey("doI"));
			
			assertTrue(aliases.containsValue("howlong"));
			assertTrue(aliases.containsValue("do.i.know.swimming"));
			
	
			// duplicates check
			System.out.println(rpDesc.getPathIndices());

			Set<String> pathIndices = rpDesc.getPathIndices();
			assertTrue(pathIndices.contains("2"));
			assertTrue(pathIndices.contains("3"));
			assertTrue(pathIndices.contains("5"));
			assertTrue(pathIndices.contains("6")); 
			
			// Parameter mapping count check
			assertEquals(rpDesc.getRequestParams("swim").count(), 4);
			assertEquals(rpDesc.getRequestParams("dive").count(), 2);
			
			assertEquals(rpDesc.getRequestParams("swim").get("2"), "howlong");
			assertEquals(rpDesc.getRequestParams("swim").get("3"), "do.i.know.swimming");
			assertEquals(rpDesc.getRequestParams("swim").get("5"), "do.i.know.swimming.2");
			assertEquals(rpDesc.getRequestParams("swim").get("6"), "do.i.know.swimming.3");
			assertEquals(rpDesc.getRequestParams("dive").get("2"), "height");
			assertEquals(rpDesc.getRequestParams("dive").get("3"), "howlong");
		}
		catch (Throwable e) {
			fail("Test failed " + goodService.toString() + " " +  e.getMessage());
		}	
	}


    
	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public void serviceConfig() throws Exception {
		
		System.setProperty(ParseUtils.SYS_PROP_CONFIG_SCHEMA_CHECK, "ERROR");
		ParseUtils.reloadSchemaCheckLevel();
		
		StringBuffer output = new StringBuffer();
		ServiceConfigManager configManager = ServiceConfigManager.getInstance();

		configManager.setConfigTestCase("configtest2", "testconfig");
		try {
			String fakeService = "unknown";
			configManager.getConfig(fakeService);
			fail("Service should not be found: " + fakeService);
		} catch (Throwable e) {
			ExceptionUtils.checkException(e, ServiceNotFoundException.class, "No configuration defined for service: unknown");
		}
		
		String goodService = "test2";
		try {
			configManager.getConfig(goodService);
		} catch (Throwable e) {
			fail("Failed to find valid service: " + goodService.toString());
		}
		
		for (int i = 1; i <= 7; i++) {  // test 6 is not really valid - namespace must be declared
			String testcase = "configtest" + String.valueOf(i);
			configManager.setConfigTestCase(testcase, "testconfig");
			Collection<String> allConfig = configManager.getAllServiceAdminNames();
			output.append("Test case: " + testcase + '\n');
			for (String svcName : allConfig) {
				try {
					ServiceConfigHolder cch = configManager.getConfigForUpdate(svcName);
					cch.dump(output);
				} catch (Exception e) {
					output.append("Exception: " + e.toString() + '\n');
				}

				try {
					GlobalConfigHolder gch = configManager.getGlobalConfigForUpdate();
					gch.dump(output);
				} catch (Exception e) {
					output.append("Exception: " + e.toString() + '\n');
				}
			}
		}

		String absPath = CompareUtils.writeOutputFile(this.getClass(), output, "server");
		String compareString = CompareUtils.getCompareString(this.getClass(), "server.compare.txt");
		assertEquals(compareString, output.toString());

		configManager.setConfigTestCase("config");
	}
}