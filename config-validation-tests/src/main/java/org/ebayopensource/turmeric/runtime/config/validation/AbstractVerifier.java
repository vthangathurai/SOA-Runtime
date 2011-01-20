/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.config.validation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXBuilder;

public abstract class AbstractVerifier {
    private static final Logger LOG = Logger.getLogger(AbstractVerifier.class.getName());
    protected Report report;
    protected List<String> excludedClasses;
    private List<String> excludedConfigs;
    private PathRegex pathregex;

    protected String asArrayString(List<String> entries) {
        StringBuilder str = new StringBuilder();
        str.append("[");
        boolean delim = false;
        for (String entry : entries) {
            if (delim) {
                str.append(", ");
            }
            str.append(entry);
            delim = true;
        }
        str.append("]");
        return str.toString();
    }

    protected void assertClassExists(File hit, Document doc, String xpath) {
        try {
            Namespace ns = doc.getRootElement().getNamespace();
            XPath expr = new JDOMXPath(xpath);
            expr.addNamespace("c", ns.getURI());

            List<?> nodes = expr.selectNodes(doc);
            LOG.fine("Searching for: " + xpath);
            for (Object node : nodes) {
                String classname = null;
                if (node instanceof Text) {
                    Text txt = (Text) node;
                    classname = txt.getTextNormalize();
                    assertClassExists(hit, txt.getParentElement(), classname);
                    continue;
                }

                if (node instanceof Element) {
                    Element elem = (Element) node;
                    classname = elem.getAttributeValue("class");
                    if (StringUtils.isBlank(classname)) {
                        classname = elem.getAttributeValue("class-name");
                    }
                    assertClassExists(hit, elem, classname);
                    continue;
                }
                LOG.fine("Found: " + node);
            }
        }
        catch (JaxenException e) {
            LOG.log(Level.WARNING, "Unable to apply xpath: " + xpath, e);
        }
    }

    protected void assertClassExists(File hit, Element elemContext, String classname) {
        LOG.fine("Found Text/ClassName: " + classname);
        if (isExcludedClasses(classname)) {
            return; // skip, excluded
        }
        if (!doesClassExists(classname)) {
            String context = calculateXpathRefish(elemContext);
            report.violation(context, "Class not found: %s", classname);
        }
    }

    private boolean isExcludedClasses(String classname) {
        if (this.excludedClasses == null) {
            return false;
        }
        return this.excludedClasses.contains(classname);
    }

    public void setBaseDir(File dir) {
        this.pathregex = new PathRegex(dir, getFileRegex());
    }

    public final void verifyAllConfigMatches() {
        List<File> hits = FileFinder.findFileMatches(pathregex);
        for (File hit : hits) {
            verifyFile(hit);
        }
    }

    public void verifyFile(File file) {
        try {
            report.fileStart(file);
            String relpath = this.pathregex.getRelativePath(file);
            if (isExcludedConfigs(relpath)) {
                return; // skip. excluded config
            }
            if (file.length() <= 0) {
                return; // skip. empty file
            }
            verifyHit(file);
        }
        finally {
            report.fileEnd();
        }
    }

    private boolean isExcludedConfigs(String relpath) {
        if (this.excludedConfigs == null) {
            return false;
        }
        return this.excludedConfigs.contains(relpath);
    }

    protected String calculateXpathRefish(Element elem) {
        Stack<String> ref = new Stack<String>();
        String name;
        Element p, e = elem;
        while (e != null) {
            name = e.getName();
            p = e.getParentElement();
            if (p != null) {
                name = getXmlElementRefName(e, p);
            }
            ref.push(name);
            e = p;
        }
        StringBuilder refish = new StringBuilder();
        refish.append("/");
        while (!ref.empty()) {
            refish.append("/").append(ref.pop());
        }
        return refish.toString();
    }

    private String getXmlElementRefName(Element e, Element p) {
        String name = e.getName();

        String attrid = e.getAttributeValue("id");
        if (StringUtils.isNotBlank(attrid)) {
            return name + "[@id='" + attrid + "']";
        }

        String attrname = e.getAttributeValue("name");
        if (StringUtils.isNotBlank(attrname)) {
            return name + "[@name='" + attrname + "']";
        }

        int idx = p.indexOf(e);
        if (idx >= 0) {
            name += "[" + idx + "]";
        }

        return name;
    }

    protected boolean doesClassExists(String classname) {
        try {
            Class.forName(classname, false, Thread.currentThread().getContextClassLoader());
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public List<String> getExcludedClasses() {
        return excludedClasses;
    }

    public List<String> getExcludedConfigs() {
        return excludedConfigs;
    }

    public abstract String getFileRegex();

    protected Matcher getHitRegexMatcher(File hit) {
        return this.pathregex.getMatcher(hit);
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public void setExcludedClasses(List<String> excludedClasses) {
        this.excludedClasses = excludedClasses;
    }

    public void setExcludedConfigs(List<String> excludedConfigs) {
        this.excludedConfigs = excludedConfigs;
    }

    public abstract void verifyHit(File hit);

    public Document xmlParse(File xmlFile) throws JDOMException, IOException {
        try {
            SAXBuilder builder = new SAXBuilder(false);
            return builder.build(xmlFile);
        } catch(JDOMParseException e) {
            report.violation(Integer.toString(e.getLineNumber()), "XML Parse Violation: %s",
                            e.getMessage());
            throw e;
        }
    }

    protected boolean validRootElement(File hit, Document doc, String expectedRootName) {
        Element root = doc.getRootElement();

        if (!expectedRootName.equals(root.getName())) {
            report.violation("Root XML element", "Must have root element of <%s/> but found <%s/>",
                            expectedRootName, root.getName());
            return false; // can't work with this XML :-(
        }

        return true;
    }

    protected boolean validRootElement(File hit, Document doc, String expectedRootName,
                    String expectedDefaultNamespace) {
        Element root = doc.getRootElement();

        if (!expectedRootName.equals(root.getName())) {
            report.violation("Root XML element", "Must have root element of <%s/> but found <%s/>",
                            expectedRootName, root.getName());
            return false; // can't work with this XML :-(
        }

        String actualNamespace = root.getNamespaceURI();

        if (StringUtils.isBlank(actualNamespace)) {
            report.violation(
                            "Default Namespace",
                            "Must have the default namespace declaration of <%s xmlns=\"%s\"/>, but found none",
                            expectedRootName, expectedDefaultNamespace);
            return false; // can't work with this XML :-(
        }
        else if (!expectedDefaultNamespace.equals(actualNamespace)) {
            report.violation(
                            "Default Namespace",
                            "Expected default namespace declaration <%s xmlns=\"%s\"> but actually found <%s xmlns=\"%s\"/>",
                            expectedRootName, expectedDefaultNamespace, root.getName(),
                            actualNamespace);
            // while invalid, this doesn't prevent the rest of the tests from proceeding.
        }

        return true;
    }
}
