/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.axis2;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.junit.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.runtime.config.validation.AbstractVerifier;
import org.ebayopensource.turmeric.runtime.config.validation.RuntimeConfigValidator;
import org.ebayopensource.turmeric.runtime.config.validation.Violations;
import org.jdom.Document;
import org.junit.Test;


public class Axis2ConfigTest extends AbstractTurmericTestCase {
    private static final Logger LOG = Logger.getLogger(Axis2ConfigTest.class.getName());

    class Axis2Verifier extends AbstractVerifier {
        @Override
        public String getFileRegex() {
            return "META-INF/soa/protocol-processors/SOAP/.*axis2.xml";
        }

        @Override
        public void verifyHit(File hit) {
            LOG.info("Verifying: " + hit);
            try {
                Document doc = xmlParse(hit);

                String expectedRootName = "axisconfig";
                if (!validRootElement(hit, doc, expectedRootName)) {
                    return;
                }

                // @formatter:off
                String xpaths[] = {
                    "//messageReceiver[@class]",
                    "//transportSender[@class]",
                    "//transportReceiver[@class]",
                    "//listener[@class]",
                    "//phase[@class]"
                };
                // @formatter:on

                for (String xpath : xpaths) {
                    assertClassExists(hit, doc, xpath);
                }
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unable to parse XML: " + hit, e);
            }
        }
    }
    
    @Test
    public void testAxis2Config() {
        File testDir = MavenTestingUtils.getMainResourcesDir();
        File testXml = MavenTestingUtils.getMainResourceFile("META-INF/soa/protocol-processors/SOAP/ebay-axis2.xml");
        
        Axis2Verifier verifier = new Axis2Verifier();
        Violations violations = new Violations();
        verifier.setReport(violations);
        verifier.setBaseDir(testDir);
        verifier.verifyHit(testXml);
        
        RuntimeConfigValidator.assertNoViolations(violations);
    }
}
