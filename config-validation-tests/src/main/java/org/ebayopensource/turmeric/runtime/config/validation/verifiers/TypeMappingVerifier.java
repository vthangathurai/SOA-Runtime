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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.config.validation.AbstractVerifier;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class TypeMappingVerifier extends AbstractVerifier {
    private static final Logger LOG = Logger.getLogger(TypeMappingVerifier.class.getName());
    private Map<String, String> packageMapping;

    @Override
    public String getFileRegex() {
        return "META-INF/soa/common/([^/]+)/([^/]+)/TypeMappings\\.xml";
    }

    @Override
    public void verifyHit(File hit) {
        LOG.fine("Verifying: " + hit);
        try {
            Document doc = xmlParse(hit);

            if (!validRootElement(hit, doc, "service", "http://www.ebayopensource.org/turmeric/common/config")) {
                return;
            }

            packageMapping = collectPackageMappings(doc);

            // Validate URL namespaces
            assertPackageNamespace(hit, "org.ebayopensource.turmeric.runtime.types",
                            "http://www.ebayopensource.org/turmeric/common/v1/types");
            assertPackageNamespace(hit, "java.lang", "http://www.w3.org/2001/XMLSchema");

            // @formatter:off
            String xpaths[] = {
                "//c:java-type-name/text()"
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

    private void assertPackageNamespace(File hit, String name, String expectedNamespace) {
        if (!packageMapping.containsKey(name)) {
            return; // not part of this type mapping, ignore.
        }
        String actualNamespace = packageMapping.get(name);
        if (!actualNamespace.equals(expectedNamespace)) {
            report.violation("/service/package-map/package[@name='" + name + "']",
                            "Expected namespace of \"%s\" for package name \"%s\", but found namespaces \"%s\" instead",
                            expectedNamespace, name, actualNamespace);
        }
    }

    protected void assertClassExists(File hit, Element elemContext, String classname) {
        super.assertClassExists(hit, elemContext, classname);

        int idx = classname.lastIndexOf('.');
        if (idx > 0) {
            String packageName = classname.substring(0, idx);
            if (!packageMapping.containsKey(packageName)) {
                report.violation(calculateXpathRefish(elemContext),
                                "Class \"%s\" does not have corresponding <package-map name=\"%s\">", classname,
                                packageName);
            }
        }
        else {
            report.violation(calculateXpathRefish(elemContext),
                            "Invalid classname (must have package to map it against): %s", classname);
        }
    }

    /**
     * Get the /package-map/package entries as a {@link Map} of the <code>name</code> to <code>xml-namespace</code>
     * 
     * @param doc
     *            the doc to fetch from.
     * @return
     * @throws Exception
     */
    private Map<String, String> collectPackageMappings(Document doc) throws Exception {
        Namespace ns = doc.getRootElement().getNamespace();
        XPath expr = new JDOMXPath("/c:service/c:package-map/c:package");
        expr.addNamespace("c", ns.getURI());

        Map<String, String> pmap = new HashMap<String, String>();

        @SuppressWarnings("unchecked")
        List<Element> nodes = expr.selectNodes(doc);
        String name, xmlNamespace;
        for (Element elem : nodes) {
            name = elem.getAttributeValue("name");
            xmlNamespace = elem.getAttributeValue("xml-namespace");
            pmap.put(name, xmlNamespace);
        }

        return pmap;
    }
}
