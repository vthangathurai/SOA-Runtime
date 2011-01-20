/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.config.validation.verifiers;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.ebayopensource.turmeric.runtime.config.validation.AbstractVerifier;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Assert;

public class ErrorDataVerifier extends AbstractVerifier {
    private static final Logger LOG = Logger.getLogger(ErrorDataVerifier.class.getName());

    /**
     * Used to track the ref count of various error names
     */
    static class NameRef {
        private String name;
        private int refcount;
        private List<String> ids = new ArrayList<String>();

        public NameRef(String name) {
            this.name = name;
            this.refcount = 0;
        }

        public void addId(String sid) {
            this.ids.add(sid);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            NameRef other = (NameRef) obj;
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            }
            else if (!name.equals(other.name)) {
                return false;
            }
            return true;
        }

        public List<String> getIds() {
            return ids;
        }

        public String getName() {
            return name;
        }

        public int getRefcount() {
            return refcount;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        public void increment() {
            this.refcount++;
        }
    }

    protected void assertNoDuplicateIDs(File hit, Element errorlist) {
        Namespace ns = errorlist.getNamespace();

        @SuppressWarnings("unchecked")
        List<Element> errorelems = errorlist.getChildren("error", ns);

        Assert.assertThat("<error> count", errorelems.size(), greaterThanOrEqualTo(1));

        // Collect all of the id -> name mappings
        Map<Integer, List<String>> idMap = new HashMap<Integer, List<String>>();
        for (Element elem : errorelems) {
            String name = elem.getAttributeValue("name");
            Assert.assertNotNull("Element has a <null> name", name);
            String sid = elem.getAttributeValue("id");
            Assert.assertNotNull("Element [" + name + "] has a <null> id", sid);
            int id = Integer.parseInt(sid);
            List<String> names = idMap.get(id);
            if (names == null) {
                names = new ArrayList<String>();
            }
            names.add(name);
            idMap.put(id, names);
        }

        // Validate that there are no duplicate ids.
        for (int id : idMap.keySet()) {
            List<String> names = idMap.get(id);
            if (names.size() > 1) {
                report.violation("multiple-locations", "Found %d duplicates for id [%d] = names %s", names.size(),
                                id, asArrayString(names));
            }
        }
    }

    private void assertNoDuplicateNames(File hit, Element errorlist) {
        Namespace ns = errorlist.getNamespace();

        @SuppressWarnings("unchecked")
        List<Element> errorelems = errorlist.getChildren("error", ns);

        Assert.assertThat("<error> count", errorelems.size(), greaterThanOrEqualTo(1));

        // Name to Reference Count
        Map<String, NameRef> namerefs = new HashMap<String, NameRef>();
        NameRef ref;
        for (Element elem : errorelems) {
            String name = elem.getAttributeValue("name");
            Assert.assertNotNull("Element has a <null> name", name);
            String sid = elem.getAttributeValue("id");
            Assert.assertNotNull("Element [" + name + "] has a <null> id", sid);
            ref = namerefs.get(name);
            if (ref == null) {
                ref = new NameRef(name);
            }
            ref.increment();
            ref.addId(sid);
            namerefs.put(name, ref);
        }

        // Validate that there are no duplicate names.
        for (NameRef nref : namerefs.values()) {
            if (nref.getRefcount() > 1) {
                report.violation("multiple-locations", "Found %d duplicates for name [%s] = ids %s",
                                nref.getRefcount(), nref.getName(), asArrayString(nref.getIds()));
            }
        }
    }

    @Override
    public String getFileRegex() {
        return "META-INF/errorlibrary/([^/]+)/ErrorData.xml";
    }

    @Override
    public void verifyHit(File hit) {
        LOG.fine("Verifying: " + hit);
        try {
            Document doc = xmlParse(hit);
            
            String expectedRootName = "ErrorBundle";
            String expectedDefaultNamespace = "http://www.ebayopensource.org/turmeric/common/config";
            
            if (!validRootElement(hit, doc, expectedRootName, expectedDefaultNamespace)) {
                return;
            }

            Element root = doc.getRootElement();
            Namespace ns = root.getNamespace();
            
            Matcher matcher = getHitRegexMatcher(hit);
            // The matcher.find should always be successful, as we wouldn't
            // be here if it failed to find or match.
            if (matcher.find()) {
                String xmlDomain = root.getAttributeValue("domain");
                String pathDomain = matcher.group(1);
                if (!xmlDomain.equals(pathDomain)) {
                    report.violation("//ErrorBundle[@domain]",
                                    "Domain name declared in file [%s] must match the directory name in the path to the file [%s] (case is important!)",
                                    xmlDomain, pathDomain);
                }
            }

            Element errorlist = root.getChild("errorlist", ns);
            Assert.assertThat(errorlist, notNullValue());
            Assert.assertThat(errorlist.getName(), is("errorlist"));

            assertNoDuplicateIDs(hit, errorlist);
            assertNoDuplicateNames(hit, errorlist);
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "Unable to parse XML: " + hit, e);
        }
    }

}
