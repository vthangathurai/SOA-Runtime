/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ebayopensource.turmeric.tools.codegen.ServiceGenerator;
import org.ebayopensource.turmeric.tools.codegen.handler.UserResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.validator.MessageObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class WSDLSingleSchemaValidator {
	
	public boolean containsSingleSchema(String fileName) {

		final String TYPES_TAG = "wsdl:types";
		final String SCHEMA_TAG = "schema";
		boolean returnValue = false;
		
		List<MessageObject> errMsgList = null;

		Document wsdlDocument = null;
		DocumentBuilderFactory wsdlDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder wsdlDocumentBuilder = wsdlDocumentBuilderFactory.newDocumentBuilder();
			wsdlDocument = wsdlDocumentBuilder.parse(new File(fileName));
		} catch (Exception e) {
			errMsgList = new ArrayList<MessageObject>();
		}

		Element wsdlDefinition = wsdlDocument.getDocumentElement();
		NodeList wsdlTypes = wsdlDefinition.getElementsByTagName(TYPES_TAG);
		List<Node> schemaNodes = new ArrayList<Node>();

		NodeList schemaTypes = wsdlTypes.item(0).getChildNodes();
		for (int j = 0; j < schemaTypes.getLength(); j++) {
			if (schemaTypes.item(j).getNodeName().contains(SCHEMA_TAG))
				schemaNodes.add(schemaTypes.item(j));
		}

		if (schemaNodes.size() == 1 && errMsgList == null){
			returnValue = true;
		}

		return returnValue;
	}
	
	public boolean createServiceGenerator(String fileName) {
		boolean returnValue = false;
		UserResponseHandler testResponseHandler = new TestUserResponseHandler();
		ServiceGenerator serviceGenerator = new ServiceGenerator(testResponseHandler);
		List<MessageObject> errMsgList = null;


		try {
			serviceGenerator.startCodeGen(getTestServiceGenerator(fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (errMsgList == null){
			returnValue = true;
		}
		return returnValue;
	}
	
	private String[] getTestServiceGenerator(String fileName) {

		String testArgs[] =  new String[] {
		"-servicename", "CalcService",
		"-wsdl", fileName,
		"-gentype", "Interface",
		"-src", ".\\UnitTests\\src",
		"-dest", ".\\UnitTests\\tmp\\wsdl",
		"-scv", "1.0.0",
		"-gip", "org.ebayopensource.test.soaframework.tools.codegen",
		"-bin", ".\\bin"
		};

		return testArgs;
    }


}
