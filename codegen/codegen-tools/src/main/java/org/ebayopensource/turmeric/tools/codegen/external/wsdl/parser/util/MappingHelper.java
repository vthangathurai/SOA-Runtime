/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.util;



import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.WSDLParserConstants;


/**
 * Helper class for use by WSDL Parser implementations
 * 
 * @author Owen Burroughs <owenb@apache.org>
 */
public class MappingHelper {
    /**
     * Populate a map with the standard xml type -> Java class name mappings
     * @param map A map to popultate with mappings
     * @param xsdSchemaURI The xsd schema URI for xsd simple types being mapped
     * @param addNonXSDTypes Flag to indicate whether or not to include mappings
     * for SOAP-ENC simple types and Apache SOAP defined, Java collection class mappings
     * 
     */
	public static void populateWithStandardXMLJavaMappings(
        Map<QName,String> map,
        String xsdSchemaURI,
        boolean addNonXSDTypes) {

        map.put(new QName(xsdSchemaURI, "string"), "java.lang.String");
        map.put(new QName(xsdSchemaURI, "integer"), "java.math.BigInteger");
        map.put(new QName(xsdSchemaURI, "boolean"), "boolean");
        map.put(new QName(xsdSchemaURI, "float"), "float");
        map.put(new QName(xsdSchemaURI, "double"), "double");
        map.put(new QName(xsdSchemaURI, "base64Binary"), "[B");
        map.put(new QName(xsdSchemaURI, "hexBinary"), "[B");
        map.put(new QName(xsdSchemaURI, "long"), "long");
        map.put(new QName(xsdSchemaURI, "int"), "int");
        map.put(new QName(xsdSchemaURI, "short"), "short");
        map.put(new QName(xsdSchemaURI, "decimal"), "java.math.BigDecimal");
        map.put(new QName(xsdSchemaURI, "byte"), "byte");
        map.put(new QName(xsdSchemaURI, "QName"), "javax.xml.namespace.QName");
        map.put(new QName(xsdSchemaURI, "date"), "java.util.Calendar");        
        map.put(new QName(xsdSchemaURI, "time"), "java.util.Calendar");
        map.put(new QName(xsdSchemaURI, "unsignedInt"), "long");
        map.put(new QName(xsdSchemaURI, "unsignedShort"), "int");        
        map.put(new QName(xsdSchemaURI, "unsignedByte"), "short");        
        map.put(new QName(xsdSchemaURI, "anySimpleType"), "java.lang.String");
        map.put(new QName(xsdSchemaURI, "anyURI"), "java.lang.String");                

        // Register dateTime or timeInstant depending on schema
        if (xsdSchemaURI.equals(WSDLParserConstants.NS_URI_2001_SCHEMA_XSD)) {
            map.put(new QName(xsdSchemaURI, "dateTime"), "java.util.Calendar");
        } else {
            map.put(new QName(xsdSchemaURI, "timeInstant"), "java.util.Calendar");
        }

        // Only add the SOAP-ENC simple types and soap collection class mappings if
        // requested to do so
        if (addNonXSDTypes) {
            // SOAP encoding simple types
            map.put(
                new QName(WSDLParserConstants.NS_URI_SOAP_ENC, "string"),
                "java.lang.String");
            map.put(
                new QName(WSDLParserConstants.NS_URI_SOAP_ENC, "boolean"),
                "java.lang.Boolean");
            map.put(
                new QName(WSDLParserConstants.NS_URI_SOAP_ENC, "float"),
                "java.lang.Float");
            map.put(
                new QName(WSDLParserConstants.NS_URI_SOAP_ENC, "double"),
                "java.lang.Double");
            map.put(
                new QName(WSDLParserConstants.NS_URI_SOAP_ENC, "decimal"),
                "java.math.BigDecimal");
            map.put(
                new QName(WSDLParserConstants.NS_URI_SOAP_ENC, "int"),
                "java.lang.Integer");
            map.put(
                new QName(WSDLParserConstants.NS_URI_SOAP_ENC, "short"),
                "java.lang.Short");
            map.put(
                new QName(WSDLParserConstants.NS_URI_SOAP_ENC, "byte"),
                "java.lang.Byte");
            map.put(new QName(WSDLParserConstants.NS_URI_SOAP_ENC, "base64"), "[B");

            // soap Java collection mappings
            map.put(
                new QName(WSDLParserConstants.NS_URI_APACHE_SOAP, "Map"),
                "java.util.Map");
            map.put(
                new QName(WSDLParserConstants.NS_URI_APACHE_SOAP, "Vector"),
                "java.util.Vector");
            map.put(
                new QName(WSDLParserConstants.NS_URI_APACHE_SOAP, "Hashtable"),
                "java.util.Hashtable");
        }
    }
}
