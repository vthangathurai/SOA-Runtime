/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Header;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.axis2.util.JavaUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.AuthenticatingProxyWSDLLocatorImpl;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.WSDLParserFactory;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.schema.ElementType;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.schema.Parser;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions;
import org.ebayopensource.turmeric.tools.library.codegen.TypeLibraryCodeGenContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.ebayopensource.turmeric.runtime.codegen.common.PkgNSMappingType;
import org.ebayopensource.turmeric.runtime.codegen.common.PkgToNSMappingList;
import com.sun.xml.bind.api.impl.NameConverter;

public class WSDLUtil {
	
	
	private static Map<String,QName> elementQNameMap = new HashMap<String,QName>();
	
	private static CallTrackingLogger s_logger = LogManager.getInstance(WSDLUtil.class);


	private static CallTrackingLogger getLogger() {
		return s_logger;
	}


	
	public static Map<String, WSDLOperationType> getWSDLOparations(String wsdlLoc,CodeGenContext codeGenContext) {
		
		Map<String, WSDLOperationType> wsdlOperations = 
					new HashMap<String, WSDLOperationType>();
		
		if (CodeGenUtil.isEmptyString(wsdlLoc)) {
			return wsdlOperations;
		}
		
		try {
			
			Definition wsdlDef = codeGenContext.getWsdlDefinition();
			if(wsdlDef == null)
				wsdlDef = getWSDLDefinition(wsdlLoc);
			
			Map<String, String> element2SchemaTypeMap = 
					getSchemaType2ElementMap(wsdlLoc, wsdlDef);
			
			wsdlOperations = internalGetWSDLOperations(wsdlDef, element2SchemaTypeMap);
			
		} catch (Exception ex) {
			//NOPMD
		}
		
		return wsdlOperations;
	}
	
	public static Map<String, String> getSchemaType2ElementMap(String wsdlLoc) {
		
		Map<String, String> element2SchemaTypeMap = new HashMap<String, String>();
		
		if (CodeGenUtil.isEmptyString(wsdlLoc)) {
			return element2SchemaTypeMap;
		}
		
		try {
			Definition wsdlDef = getWSDLDefinition(wsdlLoc);
			element2SchemaTypeMap = getSchemaType2ElementMap(wsdlLoc, wsdlDef);
		} catch (Exception ex) {
			//NOPMD
		}
		
		return element2SchemaTypeMap;
	}
	
	
	private static Map<String, String> getSchemaType2ElementMap(String wsdlLoc, Definition wsdlDef) {
		
		Map<String, String> element2SchemaTypeMap = new HashMap<String, String>();
		elementQNameMap.clear();
		
		try {				

			List types = new ArrayList();
		    AuthenticatingProxyWSDLLocatorImpl wsdlLocator = 
		    		new AuthenticatingProxyWSDLLocatorImpl(wsdlLoc, "", "");
		    Parser.getAllSchemaTypes(wsdlDef, types, wsdlLocator);
		    
		    for (int i = 0; i < types.size(); i++) {
		    	Object obj = types.get(i);
		    	if (obj instanceof ElementType) {
		    		ElementType schemaElement = (ElementType) obj;
		    		QName typeQName = schemaElement.getElementType();
		    		QName elementQName = schemaElement.getTypeName();
		    		if (typeQName == null) {
		    			typeQName = elementQName;
		    		}
		    		
		    		if (typeQName != null && elementQName != null) {
		    			
		    			String elementLocalPart = elementQName.getLocalPart();
		    			String typeLocalPart = typeQName.getLocalPart();
		    			element2SchemaTypeMap.put(		    						 
		    					elementLocalPart,
		    					typeLocalPart);
		    			
		    			elementQNameMap.put(elementLocalPart, elementQName);
		    			elementQNameMap.put(typeLocalPart, typeQName);
		    			
		    		}
		    	}
		    }				    
		    
		} catch (Exception ex) {
			//NOPMD
		}		
		
		return element2SchemaTypeMap;
	}
	
	
	private static Map<String, WSDLOperationType> internalGetWSDLOperations(
				Definition wsdlDef, 
				Map<String, String> element2SchemaTypeMap) {
		
		Map portTypeMap = wsdlDef.getPortTypes();		
		Iterator mapValuesItr = portTypeMap.values().iterator();
		
		Map<String, WSDLOperationType> wsdlOperations = 
				new HashMap<String, WSDLOperationType>();
		
		//get the Binding for the WSDL file
		Binding binding = getBindingForWSDL(wsdlDef);
		
		while (mapValuesItr.hasNext()) {
			
			PortType portType = (PortType) mapValuesItr.next();
			
			List operations = portType.getOperations();
			
			for (int i = 0; i < operations.size(); i++) {
				Operation operation  = (Operation) operations.get(i);

			 try{	
				WSDLOperationType wsdlOpType = new WSDLOperationType();
				
				wsdlOpType.setOperationName(operation.getName());
				
				if (operation.getInput() != null) {
					Input input = operation.getInput();	
					Message inputMsg = input.getMessage();
					Part msgPart = getFirstPart(inputMsg.getParts().values());
					
					WSDLMessageType wsdlMsg = 
							getWSDLMsgType(
									inputMsg.getQName().getLocalPart(), 
									msgPart, 
									element2SchemaTypeMap);				
					wsdlOpType.setInMessage(wsdlMsg);
				}
				
				if (operation.getOutput() != null) {
					Output output = operation.getOutput();	
					Message outputMsg = output.getMessage();
					Part msgPart = getFirstPart(outputMsg.getParts().values());
					
					WSDLMessageType wsdlMsg = 
							getWSDLMsgType(
										outputMsg.getQName().getLocalPart(), 
										msgPart, 
										element2SchemaTypeMap);				
					wsdlOpType.setOutMessage(wsdlMsg);
				}
				
				if(operation.getFaults() != null){
					Map<String, Fault> map = new HashMap<String, Fault>();
					map = operation.getFaults();
					for(String faultName  : map.keySet()){
						Fault currFault = map.get(faultName);
						
						Message faultMessage = currFault.getMessage();
						Part msgPart = getFirstPart(faultMessage.getParts().values());
						
						WSDLMessageType wsdlMsg = new WSDLMessageType();
						
						String faultElementName = msgPart.getElementName().getLocalPart();
						String faultTypeName  = element2SchemaTypeMap.get(faultElementName);
						
						QName faultTypeElement = elementQNameMap.get(faultTypeName);
						
						wsdlMsg.setName(faultTypeElement.getNamespaceURI());
						wsdlMsg.setElementName(faultElementName);
						wsdlMsg.setElementQname(faultTypeElement);
						wsdlMsg.setSchemaTypeName(faultTypeName);
						
						wsdlOpType.getFaults().add(wsdlMsg);
						
					}
				}
				
				wsdlOperations.put(wsdlOpType.getOperationName(), wsdlOpType);
				
                //header-support  {
				if(binding != null){
                	BindingOperation bindOper = binding.getBindingOperation(operation.getName(), null, null);
                	if(bindOper != null){
                		
        				
        				
        				
        				//processing the BindingInput to get RequestHeader
        				BindingInput bindIp = bindOper.getBindingInput();

        				for(Object obj : bindIp.getExtensibilityElements()){
        					if( obj instanceof SOAPHeader ) {
        						SOAPHeader wsdlSoapReqHeader = null;
        						WSDLMessageType wsdlMsg = new WSDLMessageType();
        						
        						wsdlSoapReqHeader = (SOAPHeader) obj;

        						QName headerRequestElementName = getHeaderElementName(wsdlDef,wsdlSoapReqHeader);
        						String headerRequestType  = element2SchemaTypeMap.get(headerRequestElementName.getLocalPart());
        						
        						QName headerRequestTypeElement = elementQNameMap.get(headerRequestType);
        						
        						wsdlMsg.setName(headerRequestTypeElement.getNamespaceURI());
        						wsdlMsg.setElementName(headerRequestElementName.toString());
        						wsdlMsg.setSchemaTypeName(headerRequestType);
        						
        						wsdlOpType.getRequestHeader().add(wsdlMsg);
        					}
        					else if( obj instanceof SOAP12Header ) {
        						SOAP12Header wsdlSoap12ReqHeader = null;
        						WSDLMessageType wsdlMsg = new WSDLMessageType();
        						
        						wsdlSoap12ReqHeader = (SOAP12Header) obj;

        						QName headerRequestElementName = getHeaderElementName(wsdlDef,wsdlSoap12ReqHeader);
        						String headerRequestType  = element2SchemaTypeMap.get(headerRequestElementName.getLocalPart());
        						
        						QName headerRequestTypeElement = elementQNameMap.get(headerRequestType);
        						
        						wsdlMsg.setName(headerRequestTypeElement.getNamespaceURI());
        						wsdlMsg.setElementName(headerRequestElementName.toString());
        						wsdlMsg.setSchemaTypeName(headerRequestType);
        						
        						wsdlOpType.getRequestHeader().add(wsdlMsg);
        					}

        				}
        				
        				//processing the BindingOutput to get ResponseHeader
        				BindingOutput bindOp = bindOper.getBindingOutput();

        				for(Object obj : bindOp.getExtensibilityElements()){
        					if( obj instanceof SOAPHeader ) {
        						SOAPHeader wsdlSoapResHeader = null;
        						WSDLMessageType wsdlMsg = new WSDLMessageType();
        						
        						wsdlSoapResHeader = (SOAPHeader) obj;
        						
        						QName headerResponseElementName = getHeaderElementName(wsdlDef,wsdlSoapResHeader);
        						String headerResponseType  = element2SchemaTypeMap.get(headerResponseElementName.getLocalPart());
        						
        						QName headerResponseTypeElement = elementQNameMap.get(headerResponseType);

        						wsdlMsg.setName(headerResponseTypeElement.getNamespaceURI());
        						wsdlMsg.setElementName(headerResponseElementName.toString());
        						wsdlMsg.setSchemaTypeName(headerResponseType);
        						
        						wsdlOpType.getResponseHeader().add(wsdlMsg);
        					}
        					else if( obj instanceof SOAP12Header ) {
        						SOAP12Header wsdlSoap112ResHeader = null;
        						WSDLMessageType wsdlMsg = new WSDLMessageType();
        						
        						wsdlSoap112ResHeader = (SOAP12Header) obj;
        						
        						QName headerResponseElementName = getHeaderElementName(wsdlDef,wsdlSoap112ResHeader);
        						String headerResponseType  = element2SchemaTypeMap.get(headerResponseElementName.getLocalPart());
        						
        						QName headerResponseTypeElement = elementQNameMap.get(headerResponseType);

        						wsdlMsg.setName(headerResponseTypeElement.getNamespaceURI());
        						wsdlMsg.setElementName(headerResponseElementName.toString());
        						wsdlMsg.setSchemaTypeName(headerResponseType);
        						
        						wsdlOpType.getResponseHeader().add(wsdlMsg);
        					} 
        				}

                	}
                }//header-support }

			}catch(Exception e){
				getLogger().log(Level.SEVERE, "Exception while parsing WSDL operation : " +operation.getName());
				continue;
			}
			}
		}
		
		return wsdlOperations;
	}
	
	
	/**
	 * returns the Element name of the SoapHeader 
	 * @param wsdlDef WSDL Definition
	 * @param soapHeader SOAP Header (Request/Response)
	 * @return XML Element name of the SoapHeader
	 */
	private static QName getHeaderElementName(Definition wsdlDef, SOAPHeader soapHeader) {
		Message message = wsdlDef.getMessage(soapHeader.getMessage());
		Part part = message.getPart(soapHeader.getPart());
		
		return part.getElementName();
	}
	
    /**
     * returns the Element name of the Soap12Header instance
     * @param wsdlDef WSDL Definition
     * @param soap12Header SOAP12 Header (Request/Response)
     * @return	XML Element name of the Soap12Header
     */
	private static QName getHeaderElementName(Definition wsdlDef, SOAP12Header soap12Header) {
		Message message = wsdlDef.getMessage(soap12Header.getMessage());
		Part part = message.getPart(soap12Header.getPart());
		
		return part.getElementName();
	}
	
	
	/**
	 * gets the Type of the HeaderRequest and HeaderResponse , this method always assumes that the 
	 * <code>element</code> tag is a direct child of the <code>schema</code> tag
	 * @param wsdlDef WSDL Defintion
	 * @param soapHeader SOAPHeader representing the Header Request / Header Response
	 * @return the type name of the input soapHeader 
	 */
	
	private static String getHeaderType(Definition wsdlDef, SOAPHeader soapHeader ){
		
		Message message       = wsdlDef.getMessage(soapHeader.getMessage());
		Part part             = message.getPart(soapHeader.getPart());
		String headerTypeName = part.getElementName().getLocalPart();
		String headerType     = null;
		Schema schema = null;
		
		/*
		 *  finding the <schema> element
		 */
		List exItems = wsdlDef.getTypes().getExtensibilityElements();
		for (Object obj : exItems)
		{
			ExtensibilityElement exItem = (ExtensibilityElement)obj;
			if(! (exItem instanceof Schema ))
				continue;
		    schema           = (Schema)obj;
		}
		
		if(schema == null)
		   return null;
	
		Element      element = schema.getElement();
		Node         node    = element.getFirstChild();
		NamedNodeMap nameNode;
		
		/*
		 * Scan the element nodes sequentially
		 * 
		 * phase -1 logic (till the first continue statement)
		 * 1. If its not an <element> node , move to the next sibling node
		 * 2. If the node does not have any attributes, move to the next sibling node
		 * 
		 * phase -2 logic (btw the first and the second "continue" statement)
		 * 1. Move to the next sibling node If the current node's name attribute's value is different than the one which 
		 *    we are looking for.
		 *    
		 * phase -3 ( remainder of the while loop)   
		 * 1. Exit from the loop once we get the Header's Type.   
		 */
		while((node =  node.getNextSibling())  != null) {
			
			if( !("element".equals(node.getLocalName())) 
				 || !node.hasAttributes())
				continue;
						
			nameNode                  = node.getAttributes();
			String currHeaderTypeName = nameNode.getNamedItem("name").toString().trim();
			currHeaderTypeName        = currHeaderTypeName.substring(currHeaderTypeName.indexOf("\"")+1, currHeaderTypeName.length() - 1);
				
			if (!currHeaderTypeName.equalsIgnoreCase(headerTypeName))
			   continue;
				
			headerType = nameNode.getNamedItem("type").toString().trim();
			headerType = headerType.substring(headerType.indexOf(":") +1 ,headerType.length() - 1 );
				
			break;
			
		}
				
		return headerType;
	}
	
	
	/**
	 *   This procedure assumes that
	 *   1. the input WSDL file will have only one service defined 
	 *   2. the service will be have only one port defined
	 *   3. the port will have only one or zero binding defined  
	 */
	private static Binding getBindingForWSDL(Definition wsdlDef) {
	    Binding binding = null;
	    QName serviceQName =  null;   
		String portName =  null;   
		
		Map servicesMap = wsdlDef.getServices();
      	 
		//get the first service
	  	Iterator keySetItr = servicesMap.keySet().iterator();
		while (keySetItr.hasNext()) {
			serviceQName = (QName) keySetItr.next();
			break;
		}
		Service service = wsdlDef.getService(serviceQName);
		
		//get the first port of the service
		Map portsMap = service.getPorts();
		keySetItr = portsMap.keySet().iterator();
		while (keySetItr.hasNext()) {
			portName = (String) keySetItr.next();
			break;
		}
		
		binding   = service.getPort(portName).getBinding() ;
	  
	  return binding;
		
	}
	
	
	private static WSDLMessageType getWSDLMsgType(
				String msgName, 
				Part msgPart, 
				Map<String, String> element2SchemaTypeMap) {
		
		WSDLMessageType wsdlMsg = new WSDLMessageType();
		
		wsdlMsg.setName(msgName);
		wsdlMsg.setElementName(msgPart.getElementName().getLocalPart());
		wsdlMsg.setElementQname(msgPart.getElementName());
		if (msgPart.getTypeName() != null) {
			wsdlMsg.setSchemaTypeName(msgPart.getTypeName().getLocalPart());
		} else {
			String typeName = element2SchemaTypeMap.get(msgPart.getElementName().getLocalPart());
			wsdlMsg.setSchemaTypeName(typeName);
		}

		
		return wsdlMsg;
	}
	
	private static Part getFirstPart(Collection parts) {
		Part firstPart = null;
		
		Iterator partsItr = parts.iterator();
		while (partsItr.hasNext()) {
			firstPart = (Part) partsItr.next();
			break;
		}
		return firstPart;
	}
	
	
	public static String getInterfaceName(
			String wsdlLoc,
			String pkgName) throws PreProcessFailedException {
		return getInterfaceName(wsdlLoc, pkgName, new HashMap<String, String>());
	}

	public static String getInterfaceName(
			String wsdlLoc,
			String pkgName,
			Map<String, String> ns2PkgMap) throws PreProcessFailedException {
		
		return getInterfaceName(wsdlLoc, pkgName, new HashMap<String, String>(),null);
	
	
	}
	

	//The param codeGenContext can be NULL . So pls keep that in mind while making any changes
	public static String getInterfaceName(
			String wsdlLoc,
			String pkgName,
			Map<String, String> ns2PkgMap,
			CodeGenContext codeGenContext) throws PreProcessFailedException {
		
		String interfaceName = null;
		String serviceName = null;
		String targetNamespace = null;
		
		QName serviceQName = getFirstServiceQName(wsdlLoc,codeGenContext);
		serviceName = serviceQName.getLocalPart();
		targetNamespace = serviceQName.getNamespaceURI();
		
		if (CodeGenUtil.isEmptyString(pkgName)) {
			pkgName = ns2PkgMap.get(targetNamespace);
			if (CodeGenUtil.isEmptyString(pkgName)) {
			pkgName = getPackageFromNamespace(targetNamespace);
		}
		}


		interfaceName = CodeGenUtil.makeFirstLetterUpper(serviceName);// + "SkeletonInterface";
		if (!CodeGenUtil.isEmptyString(pkgName)) {
			interfaceName = pkgName + "." + interfaceName;
		}
		
		return interfaceName;
	}
	
	
	public static Definition getWSDLDefinition(String wsdlLoc) throws WSDLException, PreProcessFailedException {
		
		WSDLFactory factory = WSDLParserFactory.getInstance();
	    WSDLReader wsdlReader = factory.newWSDLReader();
	    wsdlReader.setFeature("javax.wsdl.importDocuments", true);
	    
	    Definition wsdlDef = wsdlReader.readWSDL(wsdlLoc);
	    
	    return wsdlDef;
	}

    /**
     * Namespace 2 Package algorithm as defined by the JAXB Specification
     *
     * @param Namespace
     * @return String represeting Namespace
     */
    public static String getPackageFromNamespace(String namespace) {
    	
    	//Using the method used by JAXB directly to avoid potential conflicts with JAXB generated code
    	//Therefore commenting out the old code which is based on JAXB 2.0 spec
    	return com.sun.tools.xjc.api.XJC.getDefaultPackageName(namespace);
    	
    	/*
    	
        // The following steps correspond to steps described in the JAXB Specification

    	boolean isURNProtocol = namespace.startsWith("urn:");
    	
        // Step 1: Scan off the host name
        String hostname = null;
        String path = null;
        try {
            URL url = new URL(namespace);
            hostname = url.getHost();
            path = url.getPath();
        }
        catch (MalformedURLException e) {
            // No FFDC code needed
            if (namespace.indexOf(":") > -1) {
                // Brain-dead code to skip over the protocol
                hostname = namespace.substring(namespace.indexOf(":") + 1);
            } else {
                hostname = namespace;
            }
        }

        //deriving the Host name and path for URI's starting with "urn"
        //Host name is the component which falls btw the first : and the second :  (if second exists) 
        if(isURNProtocol){
           hostname = "";
           path = "";
           StringBuilder pathStr = new StringBuilder();
           StringTokenizer urnTokens = new StringTokenizer(namespace,":");
           if(urnTokens.countTokens() >= 2){
        	   urnTokens.nextToken();
        	   hostname = urnTokens.nextToken();
        	   while(urnTokens.hasMoreTokens()){
        		   pathStr.append(urnTokens.nextToken());
        		   if(urnTokens.hasMoreTokens())
        			   pathStr.append(":");
        	   }
        	   path = pathStr.toString();
           }
        }
        	
        
        // Step 3: Tokenize the host name using ":" and "/"
        StringTokenizer st = new StringTokenizer(hostname, ":/");

        ArrayList<String> wordList = new ArrayList<String>();

        int hostNameTokenCount = st.countTokens();
        //Read Hostname first.
        for (int i = 0; st != null && i < hostNameTokenCount; ++i) {
        		wordList.add(st.nextToken());
        }
        //Read rest Of the path now
        if (path != null) {
        	StringTokenizer pathst = null;
        	if(isURNProtocol)
        		pathst = new StringTokenizer(path, ":");
        	else 
        		pathst = new StringTokenizer(path, "/");
        	
            while (pathst != null && pathst.hasMoreTokens()) {
                wordList.add(pathst.nextToken());
            }
        }
        String[] words = wordList.toArray(new String[0]);

        // Now do step 2: Strip off the trailing "." (i.e. strip off .html)
        if (words.length > 1) {
            String lastWord = words[words.length - 1];
            int index = lastWord.lastIndexOf('.');
            if (index > 0) {
                words[words.length - 1] = lastWord.substring(0, index);
            }
        }

        // Step 4: Unescape each escape sequence
        // TODO I don't know what to do here.

        // Step 5: If protocol is urn, replace - with . in the first word 
        if (isURNProtocol) {
            words[0] = replace(words[0], "-", ".");
        }

        // Step 6: Tokenize the first word with "." and reverse the order. (the www is also removed).
        // TODO This is not exactly equivalent to the JAXB Rule.
        StringTokenizer st2 = new StringTokenizer(words[0], ".");
        ArrayList<String> list = new ArrayList<String>();
        while (st2.hasMoreTokens()) {
            // Add the strings so they are in reverse order
            list.add(0, st2.nextToken());
        }
        // Remove www
        String last = list.get(list.size() - 1);
        if (last.equals("www")) {
            list.remove(list.size() - 1);
        }
        // Now each of words is represented by list
        for (int i = 1; i < words.length; i++) {
            list.add(words[i]);
        }

        // Step 7: lowercase each word
        for (int i = 0; i < list.size(); i++) {
            String word = list.remove(i);
            word = word.toLowerCase();
            list.add(i, word);
        }

        // Step 8: make into and an appropriate java word
        for (int i = 0; i < list.size(); i++) {
            String word = list.get(i);

            // 8a: Convert special characters to underscore
            // Convert non-java words to underscore.
            // TODO: Need to do this for all chars..not just hyphens
            word = replace(word, "-", "_");

            StringBuilder wordBuilder = new StringBuilder(word);
            // 8b: Append _ to java keywords
            if (JavaUtils.isJavaKeyword(word)) {
            	wordBuilder.append("_");
            }
            // 8c: prepend _ if first character cannot be the first character of a java identifier
            if (!Character.isJavaIdentifierPart(word.charAt(0))) {
                wordBuilder = new StringBuilder("_").append(wordBuilder);
            }

            list.set(i, wordBuilder.toString());
        }

        // Step 9: Concatenate and return
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
            	nameBuilder.append(list.get(0));
            } else {
            	nameBuilder.append(".").append(list.get(i));
            }
        }
        return nameBuilder.toString();
        
        */
    }
	
    
        
    /**
     * replace: Like String.replace except that the old new items are strings.
     *
     * @param name string
     * @param oldT old text to replace
     * @param newT new text to use
     * @return replacement string
     */
    private static final String replace(String name,
                                       String oldT, String newT) {

        if (name == null) return "";

        // Create a string buffer that is twice initial length.
        // This is a good starting point.
        StringBuffer sb = new StringBuffer(name.length() * 2);

        int len = oldT.length();
        try {
            int start = 0;
            int i = name.indexOf(oldT, start);

            while (i >= 0) {
                sb.append(name.substring(start, i));
                sb.append(newT);
                start = i + len;
                i = name.indexOf(oldT, start);
            }
            if (start < name.length())
                sb.append(name.substring(start));
        } catch (NullPointerException e) {
            // No FFDC code needed
        }

        return new String(sb);
    }
    

    
	private static String getFirstServiceName(Map servicesMap) {
		QName serviceQName = null;
		
		Iterator keySetItr = servicesMap.keySet().iterator();
		while (keySetItr.hasNext()) {
			serviceQName = (QName) keySetItr.next();
			break;
		}
		
		String firstSvcName = null;		
		if (serviceQName != null) {
			firstSvcName = serviceQName.getLocalPart();
		} else {
			firstSvcName = "Empty";
		}
		
		return firstSvcName;
	}
    
	
	public static QName getFirstServiceQName(String wsdlLoc) throws PreProcessFailedException {
		try {
			Definition wsdlDef = getWSDLDefinition(wsdlLoc);
			return getFirstServiceQName(wsdlDef);
		} catch (WSDLException ex) {
			throw new PreProcessFailedException("Failed to parse WSDL", ex);
		}
	}

	public static QName getFirstServiceQName(String wsdlLoc,CodeGenContext codeGenContext) throws PreProcessFailedException {
		try {
			Definition wsdlDef = null;
			
			if(codeGenContext != null)
				wsdlDef = codeGenContext.getWsdlDefinition();
			if(wsdlDef == null)
				wsdlDef = getWSDLDefinition(wsdlLoc);
			
			QName serviceQname = getFirstServiceQName(wsdlDef);
			
			//Adcommerce support.If serviceName option is set, this should be used for ServiceCreation.
			if( codeGenContext != null && ! CodeGenUtil.isEmptyString(codeGenContext.getServiceAdminName())){
				 serviceQname = new QName(serviceQname.getNamespaceURI(),codeGenContext.getServiceAdminName());
				// if entry exists in service_metadata.properties for namespace,
					// it should be used.
					String namespace = (CodeGenUtil.isEmptyString(codeGenContext
							.getNamespace())) ? serviceQname.getNamespaceURI()
							: codeGenContext.getNamespace();
					serviceQname = new QName(namespace, codeGenContext
							.getServiceAdminName());
			}
			return serviceQname;
			
		} catch (WSDLException ex) {
			throw new PreProcessFailedException("Failed to parse WSDL", ex);
		}
	}

	
	public static QName getFirstServiceQName(Definition wsdlDef) throws PreProcessFailedException {
			Map servicesMap = wsdlDef.getServices();
			if (servicesMap == null || servicesMap.isEmpty()) {
				throw new PreProcessFailedException("No services defined in WSDL.");	
			}
			QName serviceQName = null;
			
			Iterator keySetItr = servicesMap.keySet().iterator();
			while (keySetItr.hasNext()) {
				serviceQName = (QName) keySetItr.next();
				break;
			}
			return serviceQName;
	}
	
	
	public static Map<String,String> getNS2PkgMappings(InputOptions inputOptions){
		
		String ns2pkgValue = inputOptions.getNS2Pkg();
		
		String nsPkgMapMasterStr="";// This is the master string, contents to this should be added based on the priority.
		                            // The priority now is -ns2pkg, -pkg2ns and others.
		                            // This ordering is required to determine the removal of multipe values of a single namespace with different packages
		PkgToNSMappingList pkgNsMapList =  inputOptions.getPkgNSMappings();
		String nsPkgMapStr = buildPkgNSMapString(pkgNsMapList, CodeGenConstants.WSDL_2_JAVA_NS_TO_PKG_PATTERN, ',');
		
		
		if(!CodeGenUtil.isEmptyString(ns2pkgValue) )
			nsPkgMapMasterStr =  ns2pkgValue;
		
		if(CodeGenUtil.isEmptyString(nsPkgMapMasterStr))
			nsPkgMapMasterStr = nsPkgMapStr;
		else
			nsPkgMapMasterStr += "," + nsPkgMapStr;
		
		
		return processDuplicateNameSpaces(nsPkgMapMasterStr);
	}
	

	private static Map<String,String> processDuplicateNameSpaces(String nsPkgMapMasterStr){
		//String derivedNS2PkgStr="";
		
		Map<String, String>  ns2PkgMap = new HashMap<String,String>();  
		
		String[] ns2PkgMappings = nsPkgMapMasterStr.split(",");
		for(String ns2Pkg:ns2PkgMappings){
			ns2Pkg = ns2Pkg.trim();
			String[] keyValue = ns2Pkg.split("=");
			if(keyValue == null || keyValue.length != 2)
				continue;
			
			String nameSpace     = keyValue[0].trim();
			String mappedPackage = keyValue[1].trim();
			
			if(!ns2PkgMap.containsKey(nameSpace)){
				ns2PkgMap.put(nameSpace, mappedPackage);
				//derivedNS2PkgStr += nameSpace + "=" + mappedPackage + ",";
			}
			
		}
	
		
		
		return ns2PkgMap;
	}
	
	
	private static String buildPkgNSMapString(
			PkgToNSMappingList pkgNsMapList, 
			String pkgNSPatternStr, 
		char seperator) {
	
	List<String> nsPkgMapList = buildPkgNSMapList(pkgNsMapList, pkgNSPatternStr);
	
	StringBuilder strBuilder = new StringBuilder();
	if (!nsPkgMapList.isEmpty()) {
		for (String nsPkgMapEntry : nsPkgMapList) {
			strBuilder.append(nsPkgMapEntry).append(seperator);
		}
		// remove extra separator char at th end
		strBuilder.setLength(strBuilder.length()-1);
	}

	return strBuilder.toString();
}
	
	public static List<String> buildPkgNSMapList(
			PkgToNSMappingList pkgNsMapList, 
			String pkgNSPatternStr) {
		
	String pkgNsMapStr = null;
	boolean isCommonTypesMapped = false;		
	List<String> pkgNSMapList = new ArrayList<String>(2);
	
	if (pkgNsMapList != null && !pkgNsMapList.getPkgNsMap().isEmpty()) {			
		for (PkgNSMappingType pkgNsMapType : pkgNsMapList.getPkgNsMap()) {				
			pkgNsMapStr = pkgNSPatternStr.replace(CodeGenConstants.PKG_PARAM, pkgNsMapType.getPackage());
			pkgNsMapStr = pkgNsMapStr.replace(CodeGenConstants.NAMESPACE_PARAM, pkgNsMapType.getNamespace());
			
			pkgNSMapList.add(pkgNsMapStr);
			
			if (CodeGenConstants.SOA_COMMON_TYPES_PKG.equals(pkgNsMapType.getPackage())) {
				isCommonTypesMapped = true;
			}
		}			
	}

	// If SOA common types pkg is NOT mapped to a namespace then
	// will do default mapping here, otherwise namespace mentioned in common types
	// schema will be used which is not we want
	if (isCommonTypesMapped == false) {
		pkgNsMapStr = pkgNSPatternStr.replaceAll(CodeGenConstants.PKG_PARAM, CodeGenConstants.SOA_COMMON_TYPES_PKG);
		pkgNsMapStr = pkgNsMapStr.replaceAll(CodeGenConstants.NAMESPACE_PARAM, CodeGenConstants.SOA_COMMON_TYPES_NS);
		
		pkgNSMapList.add(pkgNsMapStr);
	}
	
	return pkgNSMapList;		
}

	
	/**
	 * 
	 * @param wsdlLocation
	 * @throws WSDLException 
	 */
	public static void populateCodegenCtxWithWSDLDetails(String wsdlLocation,CodeGenContext codeGenContext) throws PreProcessFailedException{
		
		try{
			Definition wsdlDef = codeGenContext.getWsdlDefinition();
			
			if(wsdlDef == null){
				wsdlDef = getWSDLDefinition(wsdlLocation);
				codeGenContext.setWsdlDefinition(wsdlDef);
			}
			
			populateCodegenCtxWithWSDLDetails(wsdlDef,codeGenContext);
			
		}catch (WSDLException e) {
			throw new PreProcessFailedException("Exception while preprocessing the WSDL",e);
		}
		
	}
	
	
	public static void populateCodegenCtxWithWSDLDetails(Definition wsdlDef,CodeGenContext codeGenContext) throws PreProcessFailedException{
		
		
			
		if (wsdlDef == null)
			return;

		QName serviceQName = getFirstServiceQName(wsdlDef);
		
		if ( ! CodeGenUtil.isEmptyString(codeGenContext.getServiceAdminName())) {
			
			String namespace = serviceQName.getNamespaceURI();
			serviceQName = new QName(namespace,
					codeGenContext.getServiceAdminName());
		}
		codeGenContext.setServiceQName(serviceQName);

		List<String> operationNamesList = getWSDLOperationsName(wsdlDef);
		codeGenContext.getOperationNamesInWSDL().addAll(operationNamesList);

		Map<String, String> javaMethodNameOperationNameMap = getMethodNameOperationNameMapping(operationNamesList);
		codeGenContext.getJavaMethodOperationNameMap().putAll(
				javaMethodNameOperationNameMap);
			
		
	}
	
	public static void populateTypeLibraryCodegenCtxWithWSDLDetails(TypeLibraryCodeGenContext typeLibrarycodeGenCtx) 
									throws PreProcessFailedException{
		TypeLibraryInputOptions typeLibraryInputOptions = typeLibrarycodeGenCtx.getTypeLibraryInputOptions();
		
		try{
			Definition wsdlDef = getWSDLDefinition(typeLibraryInputOptions.getV4WsdlLocation());
			
			populateTypeLibraryCodegenCtxWithWSDLDetails(wsdlDef, typeLibrarycodeGenCtx);
			
			
		}catch (WSDLException e) {
			throw new PreProcessFailedException("Exception while preprocessing the WSDL",e);
		}
		
	}
	
	public static void populateTypeLibraryCodegenCtxWithWSDLDetails(Definition wsdlDef,TypeLibraryCodeGenContext typeLibrarycodeGenCtx) 
									throws PreProcessFailedException{
		
		if(wsdlDef == null)
			return;
		
		QName serviceQName = getFirstServiceQName(wsdlDef);
		if(serviceQName != null)
			typeLibrarycodeGenCtx.setServiceName(serviceQName.getLocalPart());
		typeLibrarycodeGenCtx.setServiceNamespace(wsdlDef.getTargetNamespace());
		typeLibrarycodeGenCtx.setInterfacePkg(getPackageFromNamespace(wsdlDef.getTargetNamespace()));

	}
	
	
	private static Map<String, String> getMethodNameOperationNameMapping(List<String> operationNamesList) {
	
		Map<String,String> javaMethodNameOperationNameMap = new HashMap<String, String>(operationNamesList.size());
		
		for(String currOperationName : operationNamesList){
			String javaMethodName = JavaUtils.xmlNameToJavaIdentifier(currOperationName);
			javaMethodNameOperationNameMap.put(javaMethodName, currOperationName);
		}
		
		return javaMethodNameOperationNameMap;
	}



	/**
	 * 
	 * @param wsdlLocation
	 * @return
	 * @throws PreProcessFailedException 
	 * @throws WSDLException
	 */
	public static List<String> getWSDLOperationsName(Definition wsdlDef) throws PreProcessFailedException {
		 List<String> operationNamesList = new ArrayList<String>();

		 List<CodegenPortDetails> portDetailsInPriorityOrder = getWSDLPortsInPriority(wsdlDef);
		 
		 Port portWithHighestPriority = null;
		 if(portDetailsInPriorityOrder.size() > 0){
			 portWithHighestPriority = portDetailsInPriorityOrder.get(0).getPort();
			 PortType portType = portWithHighestPriority.getBinding().getPortType();
			 List<Operation> operList = portType.getOperations();
			 for(Operation currOperation : operList)
				 operationNamesList.add(currOperation.getName());
		 }
		 
		 if(operationNamesList.size() == 0)
			 throw new PreProcessFailedException("There are no operations defined for the port type under the Port : " + portWithHighestPriority.getName());
		 
		 return operationNamesList;
			
	}
	
	
	/**
	 * 
	 * @param wsdlDefinition
	 * @return list of ports in the WSDL in thee priority of
	 * 			SOAP12 , SOAP11 and then HTTP ports.
	 * @throws PreProcessFailedException 
	 */
	public static List<CodegenPortDetails> getWSDLPortsInPriority(Definition wsdlDefinition) throws PreProcessFailedException{
		
		 List<CodegenPortDetails> soap12BindingPorts = new ArrayList<CodegenPortDetails>();
		 List<CodegenPortDetails> soap11BindingPorts = new ArrayList<CodegenPortDetails>();
		 List<CodegenPortDetails> httpBindingPorts   = new ArrayList<CodegenPortDetails>();
		 List<CodegenPortDetails> allPortsInPriorityOrder = new ArrayList<CodegenPortDetails>();
		 
		 QName serviceQName = getFirstServiceQName(wsdlDefinition);
		 Service service = wsdlDefinition.getService(serviceQName);
		 
		 Port currPort =null;
		 for(Object portObject :service.getPorts().keySet()){
			 currPort = (Port)service.getPorts().get(portObject);
			 
			 CodegenPortDetails currCodegenPortDetails = new CodegenPortDetails();
			 currCodegenPortDetails.setPort(currPort);
			 
			 List<ExtensibilityElement>  listOfExten =  currPort.getExtensibilityElements();
		 
			 for(ExtensibilityElement extensibilityElement:listOfExten ){
				 if(extensibilityElement instanceof SOAP12Address){
					 currCodegenPortDetails.setSOAP12(true);
					 soap12BindingPorts.add(currCodegenPortDetails);
				 }
				 else if(extensibilityElement instanceof SOAPAddress){
					 currCodegenPortDetails.setSOAP11(true);
					 soap11BindingPorts.add(currCodegenPortDetails);
				 }
				 else if(extensibilityElement instanceof HTTPAddress){
					 currCodegenPortDetails.setHTTP(true);
					 httpBindingPorts.add(currCodegenPortDetails);
				 }

			 }
		 }
		 
		 
		 allPortsInPriorityOrder.addAll(soap12BindingPorts);
		 allPortsInPriorityOrder.addAll(soap11BindingPorts);
		 allPortsInPriorityOrder.addAll(httpBindingPorts);

		 return allPortsInPriorityOrder;
	}
	
	
	/**
	 * 
	 * @param xmlIdentifierName Name of the XML identifier
	 * @return The Java class name corresponding to the input XML identifier
	 * @throws java.lang.IllegalArgumentException
	 */
	public static String getXMLIdentifiersClassName(String xmlIdentifierName)
	throws java.lang.IllegalArgumentException
	{
		
		
		if(CodeGenUtil.isEmptyString(xmlIdentifierName))
			throw new IllegalArgumentException("The XML Identifier name passed is null/empty");
		
		/*
		 * XJC implementation of JAXB allows only an underscore or an alphabet to be the first character of the types name
		 * 		xjc version "JAXB 2.1.3 in JDK 1.6"
		 *   	JavaTM Architecture for XML Binding(JAXB) Reference Implementation, (build JAXB 2.1.3 in JDK 1.6)
		 */
		
		Character firstCharacter = xmlIdentifierName.charAt(0);
		if(  !(Character.isLetter(firstCharacter)
				|| firstCharacter.equals('_') ) ){
			throw new IllegalArgumentException("The first character of an XML identifier's name should either be a character or an underscore");
		}
		
		
		String derivedClassName = NameConverter.standard.toClassName(xmlIdentifierName);
		/*
		 * refer to section D.2 in  "The Java Architecture for XML Binding (JAXB) 2.0
										Final Release
										April 19, 2006"
			for rules to convert an XML Identifier to a class name								
		 */
		
		
		getLogger().log(Level.FINE, "XML Identifier Name : " + xmlIdentifierName);
		getLogger().log(Level.FINE, "JAVA class     Name : " + derivedClassName);
		
		if(CodeGenUtil.isEmptyString(derivedClassName))
			return xmlIdentifierName;
		else
			return derivedClassName;
	}

	
}

