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
package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author aupadhay
 *
 */
public class WSDLConversionTestHelper 
{

	private static final String XML_SCHEMA = "schema";
	private static final String XML_IMPORTS = "xsd:import";
	private static final String XML_NAMESPACE = "namespace";
	private static final Logger s_Logger = LogManager.getInstance(WSDLConversionTestHelper.class);

	public static int getNumberOfschemaFromWSDL(File wsdlFile)
	{
		int no_Schema =0;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(wsdlFile);
			NodeList nodelist = document.getElementsByTagName("*");
			for(int i=0;i<nodelist.getLength();i++)
			{
				Node node = nodelist.item(i);
				if(node.getNodeName().contains(XML_SCHEMA))
				{
					no_Schema++;

				}
			}
			return no_Schema;
		} catch (Exception e) {
			s_Logger.log(Level.SEVERE, e.getMessage());
		}
		return 0;
	}
	public static ArrayList<String> getAllNewNamespaceAddedInImports(File wsdlFile)
	{
		ArrayList<String> allnamespaces = new ArrayList<String>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(wsdlFile);
			NodeList nodelist = document.getElementsByTagName("*");
			for(int i=0;i<nodelist.getLength();i++)
			{
				Node node = nodelist.item(i);
				if(node.getNodeName().contains(XML_IMPORTS))
				{
					allnamespaces.add(node.getAttributes().getNamedItem(XML_NAMESPACE).getNodeValue());
				}
			}
			return allnamespaces;

		} catch (Exception e) {
			s_Logger.log(Level.SEVERE, e.getMessage());
		}
		return null;
	}


}







