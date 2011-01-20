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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.XmlAdjuster;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Assert;
import org.junit.Test;

public class ServiceGeneratorTypeMappingTest extends AbstractServiceGeneratorTestCase {

	@Test
	public void testTypeMappingsForElementName() throws Exception {
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = getCodegenDataFileInput("AccountService.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] = new String[] {
			"-servicename", "AccountService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "TypeMappings", 
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(), 
			"-scv", "1.0.0", 
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on

		performDirectCodeGen(args, binDir);
		
		File typemappings = getTestDestPath("gen-meta-src/META-INF/soa/common/config/AccountService/TypeMappings.xml");
        PathAssert.assertFileExists(typemappings);

        ExpectedMessage request = new ExpectedMessage();
        request.javaTypeName = "org.ebayopensource.turmeric.common.v1.services.GetAccountDetailsRequest";
        request.xmlTypeName = "getAccountDetailsRequest";
        request.xmlElementName = "{http://www.ebayopensource.org/turmeric/common/v1/services}getAccountDetails";
        request.hasAttachment = Boolean.FALSE;

        ExpectedMessage response = new ExpectedMessage();
        response.javaTypeName = "org.ebayopensource.turmeric.common.v1.services.GetAccountDetailsResponse";
        response.xmlTypeName = "getAccountDetailsResponse";
        response.xmlElementName = "{http://www.ebayopensource.org/turmeric/common/v1/services}getAccountDetailsResponse";
        response.hasAttachment = Boolean.FALSE;

        ExpectedMessage error = new ExpectedMessage();
        error.javaTypeName = "org.ebayopensource.turmeric.common.v1.types.ErrorMessage";
        error.xmlTypeName = "ErrorMessage";
        error.xmlElementName = "{http://www.ebayopensource.org/turmeric/common/v1/types}ErrorMessage";

        ExpectedOperation oper = new ExpectedOperation("getAccountDetails");
        oper.request = request;
        oper.response = response;
        oper.error = error;
 
        assertHasOperation(typemappings, oper);
	}

	@Test
	public void commonTypesNSInputOption() throws Exception {
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/AccountService.wsdl");
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		
		MavenTestingUtils.ensureDirExists(getTestDestPath("meta-src"));

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "AccountService",
			"-cn","AccountService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "TypeMappings",
			"-dest", destDir.getAbsolutePath(),
			"-bin", binDir.getAbsolutePath(),
			"-ctns", "http://www.paypal.com/work/play"
		};
		// @formatter:on
		
		performDirectCodeGen(args, binDir);
		
		File typemappings = getTestDestPath("gen-meta-src/META-INF/soa/common/config/AccountService/TypeMappings.xml");
        PathAssert.assertFileExists(typemappings);

        List<PackageMapping> expectedMapping = new ArrayList<PackageMapping>();
        expectedMapping.add(new PackageMapping("http://www.paypal.com/work/play","org.ebayopensource.turmeric.common.v1.types"));
        expectedMapping.add(new PackageMapping("http://www.ebayopensource.org/turmeric/common/v1/services","org.ebayopensource.turmeric.common.v1.services"));

        assertHasPackageMap(typemappings, expectedMapping);
	}
	
	@Test
	public void commonTypesNSInputOptionFailureCase_BadNS() throws Exception {
		MavenTestingUtils.ensureEmpty(testingdir.getDir());
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/AccountService.wsdl");
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String args[] =  new String[] {
			"-servicename", "AccountService",
			"-cn","AccountService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "TypeMappings",
			"-dest", destDir.getAbsolutePath(),
			"-bin", binDir.getAbsolutePath(),
			"-ctns", ".file|abc"
		};
		// @formatter:on

		try {
			performDirectCodeGen(args);
			Assert.fail("Should have thrown a " + BadInputValueException.class.getName());
		} catch (BadInputValueException e) {
			Assert.assertThat(e.getMessage(),
					containsString("option \"-ctns\" is not a valid URI"));
		}
	}
	
	@Test
    public void testTypemappingsWithCorrectNamespace() throws Exception {
        testingdir.ensureEmpty();
        File wsdl = getCodegenDataFileInput("PayPalAPIInterfaceService.wsdl");
        File srcDir = getTestSrcDir();
        File destDir = getTestDestDir();
        File binDir = testingdir.getFile("bin");

        // @formatter:off
		String args[] = new String[] {
			"-servicename", "AccountService",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "TypeMappings", 
			"-src", srcDir.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(), 
			"-scv", "1.0.0", 
			"-bin", binDir.getAbsolutePath()
		};
		// @formatter:on

        performDirectCodeGen(args);

        File typemappings = getTestDestPath("gen-meta-src/META-INF/soa/common/config/AccountService/TypeMappings.xml");
        PathAssert.assertFileExists(typemappings);

        List<PackageMapping> expectedMapping = new ArrayList<PackageMapping>();
        expectedMapping.add(new PackageMapping("urn:ebay:api:PayPalAPI","org.ebayopensource.turmeric.common.v1.services"));
        expectedMapping.add(new PackageMapping("urn:ebay:apis:CoreComponentTypes","ebay.apis.corecomponenttypes"));
        expectedMapping.add(new PackageMapping("urn:ebay:apis:eBLBaseComponents","ebay.apis.eblbasecomponents"));
        expectedMapping.add(new PackageMapping("urn:ebay:api:PayPalAPI","ebay.api.paypalapi"));

        assertHasPackageMap(typemappings, expectedMapping);
    }
	
	class ExpectedOperation {
		public String name;
		public ExpectedMessage request;
		public ExpectedMessage response;
		public ExpectedMessage error;
		
		public ExpectedOperation(String name) {
			this.name = name;
		}
	}
	
	class ExpectedMessage {
		public String javaTypeName;
		public String xmlTypeName;
		public String xmlElementName;
		public Boolean hasAttachment;
	}
	
	static class PackageMapping {
		public String xmlNamespace;
		public String name;
		
		public PackageMapping(String xmlNamespace, String name) {
			this.xmlNamespace= xmlNamespace;
			this.name = name;
		}
		
		@Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("PackageMapping [name=");
            builder.append(name);
            builder.append(", xmlNamespace=");
            builder.append(xmlNamespace);
            builder.append("]");
            return builder.toString();
        }

        @Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result
					+ ((xmlNamespace == null) ? 0 : xmlNamespace.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PackageMapping other = (PackageMapping) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (xmlNamespace == null) {
				if (other.xmlNamespace != null)
					return false;
			} else if (!xmlNamespace.equals(other.xmlNamespace))
				return false;
			return true;
		}
	}
	
	private void assertHasOperation(File typemappings, ExpectedOperation expected) throws Exception
	{
        // Find the <package> definitions
        Document doc = XmlAdjuster.readXml(typemappings);
        Namespace ns = doc.getRootElement().getNamespace();
        String xpathStr = "//c:service/c:operation-list/c:operation[@name='" + expected.name + "']";
        XPath expression = new JDOMXPath(xpathStr);
        expression.addNamespace("c", "http://www.ebayopensource.org/turmeric/common/config");

        Element actual = (Element) expression.selectSingleNode(doc);
        Assert.assertNotNull("Should have found: " + xpathStr, actual);
        
        assertMessage("Operation.requestMessage", actual.getChild("request-message", ns), expected.request);
        assertMessage("Operation.responseMessage", actual.getChild("response-message", ns), expected.response);
        assertMessage("Operation.errorMessage", actual.getChild("error-message", ns), expected.error);
	}
	
	private void assertMessage(String msg, Element msgElem, ExpectedMessage expected) {
		Assert.assertNotNull(msg, msgElem);
		Assert.assertNotNull(msg + ": ExpectedMessage should not be null", expected);
		Namespace ns = msgElem.getNamespace();
		Assert.assertThat(msg + ": java-type-name",
				msgElem.getChildTextTrim("java-type-name", ns),
				is(expected.javaTypeName));
		Assert.assertThat(msg + ": xml-type-name",
				msgElem.getChildTextTrim("xml-type-name", ns),
				is(expected.xmlTypeName));
		Assert.assertThat(msg + ": xml-element-name",
				msgElem.getChildTextTrim("xml-element-name", ns),
				is(expected.xmlElementName));
		if(expected.hasAttachment != null) {
			Assert.assertThat(msg + ": has-attachment",
					msgElem.getChildTextTrim("has-attachment", ns),
					is(expected.hasAttachment.toString()));
		}
	}

	private void assertHasPackageMap(File typemappings, List<PackageMapping> expectedMapping) throws Exception
	{
        /* @formatter:off
         * The expected Packages array.
         * <package xml-namespace="{expected[][0]}" name="{expected[][1]}"/>
         * @formatter:on
         */
		List<PackageMapping> actualMapping = new ArrayList<PackageMapping>();
		
        // Find the <package> definitions
        Document doc = XmlAdjuster.readXml(typemappings);
        XPath expression = new JDOMXPath("//c:service/c:package-map/c:package");
        expression.addNamespace("c", "http://www.ebayopensource.org/turmeric/common/config");

        @SuppressWarnings("unchecked")
        List<Element> elements = expression.selectNodes(doc);
        String xmlnamespace, name;
        for(Element element: elements) {
            xmlnamespace = element.getAttributeValue("xml-namespace");
            name = element.getAttributeValue("name");
            actualMapping.add(new PackageMapping(xmlnamespace, name));
        }
		
        // Compare actual to expected
        // Assert.assertThat(actualMapping.size(), is(expectedMapping.size()));
        for(PackageMapping expected: expectedMapping) {
            Assert.assertThat(actualMapping, hasItem(expected));
        }
	}
}
