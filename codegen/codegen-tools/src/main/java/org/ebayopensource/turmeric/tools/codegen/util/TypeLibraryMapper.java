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
package org.ebayopensource.turmeric.tools.codegen.util;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.DomParseUtils;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




/**
 * @author arajmony
 *
 */
public class TypeLibraryMapper {

	public static void map(String fileName , List<TypeLibraryClassDetails> list)
	throws BadInputValueException{
		
		Element topLevel = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document result= null;
		
		//TODO: schema validation of the XML file against the TypeInformation.xsd
		try {

			DocumentBuilder builder = factory.newDocumentBuilder();
			result = builder.parse(fileName);
			topLevel = result.getDocumentElement();

		} catch (SAXException e) {
			throw new BadInputValueException("",e);
		} catch (IOException e) {
			throw new BadInputValueException("",e);
		} catch (ParserConfigurationException e) {
			throw new BadInputValueException("",e);
		}		
		
		
		
		try {
			
			NodeList packages = DomParseUtils.getImmediateChildrenByTagName(topLevel, "package");
			for (int i = 0; i < packages.getLength(); i++) {
				Element packageElement = (Element) packages.item(i);
				String packageName = packageElement.getAttribute("name");
				String packageNS   = packageElement.getAttribute("nameSpace");
				
				Element  classList  = (Element) DomParseUtils.getSingleNode(fileName, packageElement, "class-list");
				NodeList classes = DomParseUtils.getImmediateChildrenByTagName(classList, "class");
				
				for(int j=0; j < classes.getLength() ; j++){
					Element classElement = (Element) classes.item(j);
					String className = classElement.getTextContent().trim();

					TypeLibraryClassDetails obj = new TypeLibraryClassDetails();
					obj.setClassName(className);
					obj.setNameSpace(packageNS);
					obj.setPackageName(packageName);

					list.add(obj);

				}
				
				
				
			}
		} catch (ServiceCreationException e) {

			throw new BadInputValueException("Codegen failed at class TypeLibraryMapper ",e);
		}
 
		
		
	}
	
	
}
