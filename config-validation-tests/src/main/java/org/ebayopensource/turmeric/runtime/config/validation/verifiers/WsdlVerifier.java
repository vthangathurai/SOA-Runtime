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

import org.apache.commons.lang.StringUtils;
import org.ebayopensource.turmeric.runtime.config.validation.AbstractVerifier;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class WsdlVerifier extends AbstractVerifier {
    private static final Logger LOG = Logger.getLogger(WsdlVerifier.class.getName());
    private static final String LEGACY_MARKETPLACE_NAMESPACE = "http://www.ebay.com/marketplace/services";
    private static final String MARKETPLACE_NAMESPACE = "http://www.ebayopensource.org/turmeric/common/v1/types";

    @Override
    public String getFileRegex() {
        return "META-INF/soa/services/wsdl/([^/]+)/([^|\\\\/:\\?\\*]+)\\.wsdl";
    }

    @Override
    public void verifyHit(File hit) {
        LOG.fine("Verifying: " + hit);
        try {
            Document doc = xmlParse(hit);

            if (!validRootElement(hit, doc, "definitions", "http://schemas.xmlsoap.org/wsdl/")) {
                return;
            }

            Element root = doc.getRootElement();
            
            Namespace defNs = root.getNamespace();
            Namespace xsNs = findNamespace(root, "http://www.w3.org/2001/XMLSchema");
            
            // Validate TypeLibrary namespace declarations
            if (xsNs != null) {
                Map<String, String> expectedTypes = new HashMap<String, String>();
                expectedTypes.put("common-type-library", "http://www.ebayopensource.org/turmeric/common/v1/types");
                validateTypeLibrarySources(hit, root, defNs, xsNs, expectedTypes);
            }
            
            // Validate <xs:schema> marketplace namespace declarations
            validateSchemaMarketplaceNamespace(hit, root, defNs, xsNs);
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "Unable to parse XML: " + hit, e);
        }
    }

    private void validateSchemaMarketplaceNamespace(File hit, Element root, Namespace defNs, Namespace xsNs) throws JaxenException {
        XPath expr = new JDOMXPath("//xs:schema");
        expr.addNamespace("xs", xsNs.getURI());
        expr.addNamespace("d", defNs.getURI());

        @SuppressWarnings("unchecked")
        List<Element> nodes = expr.selectNodes(root);
        for (Element elem : nodes) {
            @SuppressWarnings("unchecked")
            List<Namespace> nslist = elem.getAdditionalNamespaces();
            for (Namespace ns : nslist) {
                if (ns.getURI().equals(LEGACY_MARKETPLACE_NAMESPACE)) {
                    report.violation(calculateXpathRefish(elem) + "[@xmlns:" + ns.getPrefix() + "]",
                                    "Bad marketplace namespace declaration \"%s\" use \"%s\" instead.",
                                    LEGACY_MARKETPLACE_NAMESPACE, MARKETPLACE_NAMESPACE);
                }
            }
        }
    }

    private void validateTypeLibrarySources(File hit, Element root, Namespace defNs, Namespace xsNs,
                    Map<String, String> expectedTypes) throws JaxenException {
        XPath expr = new JDOMXPath("//xs:appinfo/d:typeLibrarySource");
        expr.addNamespace("xs", xsNs.getURI());
        expr.addNamespace("d", defNs.getURI());

        @SuppressWarnings("unchecked")
        List<Element> nodes = expr.selectNodes(root);
        String library, libNamespace, expectedNamespace;
        for (Element elem : nodes) {
            library = elem.getAttributeValue("library");
            libNamespace = elem.getAttributeValue("namespace");
            expectedNamespace = expectedTypes.get(library);

            if (StringUtils.isNotBlank(expectedNamespace)) {
                // Perform specific validation
                report.violation(calculateXpathRefish(elem),
                                "Bad typeLibrarySource namespace \"%s\" expected \"%s\" for library \"%s\"",
                                libNamespace, expectedNamespace, library);
                continue;
            }
            
            // Perform basic marketplace validation on other unspecified libraries
            if(libNamespace.equals(LEGACY_MARKETPLACE_NAMESPACE)) {
                report.violation(calculateXpathRefish(elem),
                                "Bad typeLibrarySource namespace \"%s\" expected \"%s\" for library \"%s\"",
                                libNamespace, MARKETPLACE_NAMESPACE, library);
            }
        }
    }

    private Namespace findNamespace(Element elem, String uri) {
        @SuppressWarnings("unchecked")
        List<Namespace> nslist = elem.getAdditionalNamespaces();
        for (Namespace ns : nslist) {
            if (ns.getURI().equals(uri)) {
                return ns;
            }
        }
        return null;
    }

}
