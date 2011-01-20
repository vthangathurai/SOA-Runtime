/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.config.validation.verifiers;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ebayopensource.turmeric.runtime.config.validation.AbstractVerifier;
import org.jdom.Document;

public class ServiceConfigVerifier extends AbstractVerifier {
    private static final Logger LOG = Logger.getLogger(ServiceConfigVerifier.class.getName());

    @Override
    public String getFileRegex() {
        return "META-INF/soa/services/([^/]+)/([^/]+)/ServiceConfig.xml";
    }

    @Override
    public void verifyHit(File hit) {
        LOG.fine("Verifying: " + hit);
        try {
            Document doc = xmlParse(hit);

            String expectedRootName = "service-config";
            String expectedDefaultNamespace = "http://www.ebayopensource.org/turmeric/common/config";

            if (!validRootElement(hit, doc, expectedRootName, expectedDefaultNamespace)) {
                return;
            }

            Matcher matcher = getHitRegexMatcher(hit);
            // The matcher.find should always be successful, as we wouldn't
            // be here if it failed to find or match.
            if (matcher.find()) {
                String xmlServiceName = doc.getRootElement().getAttributeValue("service-name");
                Pattern serviceNameWithNamespace = Pattern.compile("^\\{([^\\}]+)\\}([A-Za-z0-9]+)$");
                Matcher mat = serviceNameWithNamespace.matcher(xmlServiceName);
                if (mat.find()) {
                    String namespace = mat.group(1);
                    validateNamespaceSyntax(hit, "//service-config[@service-name]", namespace);
                    xmlServiceName = mat.group(2);
                }
                String pathServiceName = matcher.group(2);
                if (!xmlServiceName.equals(pathServiceName)) {
                    report.violation("//service-config[@service-name]",
                                    "Service name declared in xml [%s] must match the directory name in the path to the xml [%s] (case is important!)",
                                    xmlServiceName, pathServiceName);
                }
            }

            // @formatter:off
            String xpaths[] = {
                "//c:class-name/text()",
                "//c:service-impl-class-name/text()",
                "//c:service-interface-class-name/text()",
                "//c:error-mapping-handler-class-name/text()",
                "//c:version-check-handler/text()",
                "//c:state-factory/text()"
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

    private void validateNamespaceSyntax(File hit, String context, String namespace) {
        if(namespace.startsWith("http://")) {
            return; // ok namespace
        }
        
        if(namespace.startsWith("https://")) {
            return; // ok namespace
        }
        
        report.violation(context, "Invalid namespace syntax \"%s\"", namespace);
    }
}
