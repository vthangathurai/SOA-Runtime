/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.config;

import static org.junit.Assert.assertTrue;

import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.junit.Test;


// This unit test will only work when run on its own - the regular AllTests initialization doesn't have this property set.
public class ServiceConfigJVMOverrideTest extends AbstractTurmericTestCase {
	public ServiceConfigJVMOverrideTest() {
		// Checked in path is: /v3buildconfig/deliverables/SOATests/ApplicationConfigurationFiles/resources/config/soa/services/config
		System.setProperty(ServiceConfigManager.SYS_PROP_SOA_GLOBAL_CONFIG_ROOT, "/WEB-INF/config/resources/config/soa/services/config");
	}

	@Test
	public void emptyTest(){
		assertTrue(true); // this is to avoid the "no runnable tests" error junit throws. We want to keep a record
		// of the test below but not run it till the feature is implemented fully.
	}
	/**
	 * @check  Exceptions need to be handled
	 */
	/*@Test
	 TODO: Cannot run as part of unit testing (yet)
	 *       Missing jvm.compare.txt file, and the need to have a JVM
	 *       System property set are both preventing success here.
	 *
	public void serviceConfig() throws Exception {
		StringBuffer output = new StringBuffer();
		ServiceConfigManager configManager = ServiceConfigManager.getInstance();

		Collection<String> allConfig = configManager.getAllServiceAdminNames();
		for (String s : allConfig) {
			output.append("Services.txt file: ").append(s).append("\n");
		}
		
		try {
			GlobalConfigHolder gch = configManager.getGlobalConfigForUpdate();
			gch.dump(output);
		} catch (Exception e) {
			output.append("Exception: " + e.toString() + '\n');
		}
		
		System.out.println("output=[" + output + "]");
		
		CompareUtils.writeOutputFile(this.getClass(), output, "jvm");
		String compareString = CompareUtils.getCompareString(this.getClass(), "jvm.compare.txt");
		assertEquals(compareString, output.toString());
	}*/
}