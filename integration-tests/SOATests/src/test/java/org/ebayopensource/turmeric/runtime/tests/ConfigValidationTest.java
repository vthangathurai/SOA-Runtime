/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests;

import org.ebayopensource.turmeric.junit.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.runtime.config.validation.ConfigAsserts;
import org.ebayopensource.turmeric.runtime.config.validation.RuntimeConfigValidator;
import org.junit.Test;

/**
 * Test to verify the sanity of the Configurations found in the test resources.
 */
public class ConfigValidationTest extends AbstractTurmericTestCase {
    @Test
    public void testProjectConfigs() {
        RuntimeConfigValidator validator = new RuntimeConfigValidator();
        
        // The following class names are known to be bad
        // Usually due to a testing scenario.
        validator.addClassExclude("com.ebay.blah.blah");
        validator.addClassExclude("com.ebay.my.transport.http_1_0");
        validator.addClassExclude("com.ebay.my.transport.http_1_1");
        validator.addClassExclude("com.ebay.my.transport.extra");
        validator.addClassExclude("com.ebay.myhandlers.mysearchhandler");
        validator.addClassExclude("com.ebay.myhandlers.dosomething1handler");
        validator.addClassExclude("com.ebay.myhandlers.affiliatetrackinghandler");
        validator.addClassExclude("com.ebay.mypackage.classname1");
        validator.addClassExclude("com.ebay.mypackage.classname2");
        validator.addClassExclude("com.ebay.mypackage.classname3");
        validator.addClassExclude("com.ebay.mypackage2.classname1");
        validator.addClassExclude("com.ebay.mypackage2.classname2");
        validator.addClassExclude("com.ebay.mypackage3.classname1");
        validator.addClassExclude("com.ebay.mypackage4.classname1");
        validator.addClassExclude("com.ebay.mytypes.value1");
        validator.addClassExclude("com.ebay.mytypes.value2");
        validator.addClassExclude("com.ebay.soa.blah.converters.converter1");
        validator.addClassExclude("com.ebay.soa.blah.converters.converter2");
        validator.addClassExclude("com.ebay.mytypes.bound1");
        validator.addClassExclude("com.ebay.mytypes.bound2");
        validator.addClassExclude("com.ebay.kernel.soap.blah.blah");
        validator.addClassExclude("com.ebay.myhandlers.blah.blah");
        validator.addClassExclude("com.ebay.soa.my.interface");
        validator.addClassExclude("myclassname");
        validator.addClassExclude("myintfcname");
        validator.addClassExclude("versioncheck");
        validator.addClassExclude("foo");
        
        // Classes that should exist, but don't (at least not from this context)
        validator.addClassExclude("org.ebayopensource.turmeric.runtime.spf.impl.handlers.RateLimiterHandler");
        validator.addClassExclude("org.ebayopensource.turmeric.runtime.common.impl.pipeline.NVSerializerImpl");
        validator.addClassExclude("org.ebayopensource.turmeric.runtime.common.impl.pipeline.NVDeserializerImpl");
        validator.addClassExclude("org.ebayopensource.turmeric.runtime.extended.sif.impl.handlers.ClientConsumerIdentificationHandler");
        validator.addClassExclude("org.ebayopensource.turmeric.runtime.tests.sample.errors.TestAutoMarkdownStateFactory");
        validator.addClassExclude("org.ebayopensource.turmeric.runtime.tests.config.ClientConsumerIdProviderTestHandler");
        validator.addClassExclude("org.ebayopensource.turmeric.runtime.tests.config.ClientConsumerIdTestHandler");

        // The following configs are known to be bad.
        // Intentionally setup to be behave bad.
        
        // Empty XMLs
        validator.addConfigExclude("META-INF/soa/services/confignegative2/test2/ServiceConfig.xml");
        validator.addConfigExclude("META-INF/soa/client/confignegative2/default/ClientConfig.xml");
        
        // Intentionally BAD XML (badly formed open/close elements)
        validator.addConfigExclude("META-INF/soa/services/configtest7/test2/ServiceConfig.xml");
        
        // Intentionally BAD XML (bad namespace)
        validator.addConfigExclude("META-INF/soa/services/configtest3/test2/ServiceConfig.xml");
        
        ConfigAsserts.assertConfigsValid(validator);
    }
}
