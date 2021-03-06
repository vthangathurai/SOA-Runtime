/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.schema;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.WSDLParserConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class to represent a &lt;complexType&gt; element in a schema
 * 
 * @author Owen Burroughs <owenb@apache.org>
 */
public class ComplexType extends SchemaType implements Serializable {

	static final long serialVersionUID = 1L;

    private boolean isAnArray = false;
    private String name = "";
    private QName typeName = null;
    private QName arrayType = null;
    private int arrayDim = 0;
    private ComplexContent complexContent = null;
    private static final QName soapEncArray =
        new QName(WSDLParserConstants.NS_URI_SOAP_ENC, "Array");
    private static final QName soapEncArrayType =
        new QName(WSDLParserConstants.NS_URI_SOAP_ENC, "arrayType");
    private static final QName wsdlArrayType =
        new QName(WSDLParserConstants.NS_URI_WSDL, "arrayType");
    ArrayList sequenceElements = new ArrayList();        

	/**
	 * Constructor
	 * @param el The dom element for this complexType
	 */
    ComplexType(Element el, String tns) {
        typeName = getAttributeQName(el, "name", tns);
        if (typeName != null) {
        	name = typeName.getLocalPart();
        }
        
        process(el, tns);
        
        if (name.startsWith("ArrayOf")) {
            if (complexContent != null) {
                Restriction res = complexContent.getRestriction();
                if (res != null) {
                    QName base = res.getBase();
                    if (soapEncArray.equals(base)) {
                        Attribute[] atts = res.getAttributes();
                        if (atts != null && atts.length > 0) {
                            for (int i = 0; i < atts.length; i++) {
                                Attribute a = atts[i];
                                if (a != null) {
                                    QName ref = a.getXMLAttribute("ref");
                                    if (soapEncArrayType.equals(ref)) {
                                        QName tempType =
                                            a.getXMLAttribute(wsdlArrayType);
                                        if (tempType != null) {
                                            String ns =
                                                tempType.getNamespaceURI();
                                            String lp = tempType.getLocalPart();
                                            // Work out array dimension
                                            int index = lp.lastIndexOf("[]");
                                            while(index != -1) {
                                            	lp = lp.substring(0, index);
                                            	arrayDim++;
                                            	index = lp.lastIndexOf("[]");
                                            }                                           
                                            arrayType = new QName(ns, lp);
                                        }
                                        break;
                                    }
                                }
                            }
                        } else {
                            SequenceElement[] sels = res.getSequenceElements();
                            if (sels != null && sels.length == 1) {
								SequenceElement sel = sels[0];
								QName tempType = sel.getXMLAttribute("type");
								if (tempType != null) {
									String ns = tempType.getNamespaceURI();
                                    String lp = tempType.getLocalPart();
                                    arrayType = new QName(ns, lp);
								}
                            }
                        }
                    }
                }
                isAnArray = true;
            }
        } else {
        }
    }

	/**
	 * @see SchemaType#isComplex()
	 */ 
    public boolean isComplex() {
        return true;
    }

	/**
	 * @see SchemaType#isArray()
	 */ 
    public boolean isArray() {
        return isAnArray;
    }

	/**
	 * @see SchemaType#getArrayType()
	 */ 
    public QName getArrayType() {
        return arrayType;
    }

	/**
	 * @see SchemaType#getArrayDimension()
	 */ 
    public int getArrayDimension() {
        return arrayDim;
    }

	/**
	 * @see SchemaType#getTypeName()
	 */ 
    public QName getTypeName() {
        return typeName;
    }

	/**
	 * Get all the &lt;element&gt; elements within a sequence nested in this complexType
	 * @return The &lt;element&gt; elements within the sequnce
	 */
    @SuppressWarnings("unchecked")
	public SequenceElement[] getSequenceElements() {
        return (SequenceElement[]) sequenceElements.toArray(
            new SequenceElement[sequenceElements.size()]);
    }

    private void process(Element el, String tns) {
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element subEl = (Element) child;
                String elType = subEl.getLocalName();
                if (elType.equals("complexContent")) {
                    complexContent = new ComplexContent(subEl, tns);
                } else if (elType.equals("sequence")) {
                    parseSequenceElements(subEl, tns);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
	private void parseSequenceElements(Element el, String tns) {
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element subEl = (Element) child;
                String elType = subEl.getLocalName();
                if (elType.equals("element")) {
                    sequenceElements.add(new SequenceElement(subEl, tns));
                }
            }
        }
    }    
    
    /**
     * @author arajmony
     */
    public ComplexContent getComplexContent(){
    	return complexContent;
    }
}
