/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author aupadhay
 *
 */
public class WsdlParserUtil 
{
 private static final String XML_SCHEMA = "schema";
 private static final String XML_TARGETNAMESPACE = "targetNamespace";
 private static String XML_DEFINITION = "definitions";
 public static Set<String> getAllTargetNamespces(String wsdlLoc) throws ParserConfigurationException, SAXException, IOException
 {
  Set<String> allTnsSet = new HashSet<String>();
  Logger s_logger = LogManager.getInstance(WsdlParserUtil.class);
  Document  m_Document;
  String m_WsdlNamespace = null;
  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  DocumentBuilder builder;
  s_logger.log(Level.INFO, "Creating a new Document builder..");
  builder = factory.newDocumentBuilder();
  m_Document = builder.parse(wsdlLoc);
  s_logger.log(Level.INFO, "Parsing the original wsdl file...");
  NodeList nodelist = m_Document.getElementsByTagName("*");
  for (int i = 0; i < nodelist.getLength(); i++) 
  {
   Node node = nodelist.item(i);
   if (node.getNodeName().contains(XML_DEFINITION)) {
    m_WsdlNamespace = node.getAttributes().getNamedItem(
      XML_TARGETNAMESPACE).getNodeValue();
   }
   if (node.getNodeName().contains(XML_SCHEMA)) {
    String targetNamespace = ((Element)node).getAttributes().getNamedItem(XML_TARGETNAMESPACE).getNodeValue();
    allTnsSet.add(targetNamespace);
   }
  }
  return allTnsSet;
 }
 
}
