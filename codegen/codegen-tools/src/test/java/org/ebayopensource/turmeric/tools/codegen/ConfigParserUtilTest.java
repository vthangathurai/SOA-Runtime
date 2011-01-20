/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import static org.hamcrest.Matchers.*;

import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.ConfigHelper;
import org.junit.Assert;
import org.junit.Test;

import org.ebayopensource.turmeric.common.config.ClientConfigList;
import org.ebayopensource.turmeric.common.config.ServiceConfig;

public class ConfigParserUtilTest extends AbstractServiceGeneratorTestCase {
	@Test
	public void parseClientConfig() throws Exception {
		String clientCfgXml = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/ClientConfig.xml").getAbsolutePath();

		ClientConfigList clientCfgList = ConfigHelper.parseClientConfig(clientCfgXml);
		Assert.assertThat(clientCfgList, notNullValue());
	}

	@Test
	public void parseServiceConfig() throws Exception {
		String clientCfgXml = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/ServiceConfig.xml").getAbsolutePath();

		ServiceConfig serviceConfig = ConfigHelper.parseServiceConfig(clientCfgXml);		
		Assert.assertThat(serviceConfig, notNullValue());
	}
}
