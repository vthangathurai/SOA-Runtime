/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.cachepolicy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for EchoObjectRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EchoObjectRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ebayopensource.org/turmeric/common/v1/services}MyEchoServiceRequest">
 *       &lt;sequence>
 *         &lt;element name="inString" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="in-Int" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="in_Long" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="inBoolean" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="inNestedObject" type="{http://www.ebayopensource.org/turmeric/common/v1/services}NestedObject"/>
 *         &lt;element name="inStringArrayElement" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="inIntArrayElement" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EchoObjectRequest", propOrder = {
    "inString",
    "inInt",
    "inLong",
    "inBoolean",
    "inNestedObject",
    "inStringArrayElement",
    "inIntArrayElement"
})
public class TestEchoObjectRequest extends MyEchoServiceRequest
{

    @XmlElement(required = true)
    protected String inString;
    @XmlElement(name = "in-Int")
    protected int inInt;
    @XmlElement(name = "in_Long")
    protected long inLong;
    protected boolean inBoolean;
    @XmlElement(required = true, name = "in_NestedObj")
    protected NestedObject inNestedObject;
    @XmlElement(nillable = true)
    protected List<String> inStringArrayElement;
    @XmlElement(nillable = true)
    protected List<Integer> inIntArrayElement;

    /**
     * Gets the value of the inString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @XmlElement(name = "in_String")
    public String getInString() {
        return inString;
    }

    /**
     * Sets the value of the inString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInString(String value) {
        this.inString = value;
    }

    /**
     * Gets the value of the inInt property.
     * 
     */
    public int getInInt() {
        return inInt;
    }

    /**
     * Sets the value of the inInt property.
     * 
     */
    public void setInInt(int value) {
        this.inInt = value;
    }

    /**
     * Gets the value of the inLong property.
     * 
     */
    public long getInLong() {
        return inLong;
    }

    /**
     * Sets the value of the inLong property.
     * 
     */
    public void setInLong(long value) {
        this.inLong = value;
    }

    /**
     * Gets the value of the inBoolean property.
     * 
     */
    public boolean isInBoolean() {
        return inBoolean;
    }

    /**
     * Sets the value of the inBoolean property.
     * 
     */
    public void setInBoolean(boolean value) {
        this.inBoolean = value;
    }

    /**
     * Gets the value of the inNestedObject property.
     * 
     * @return
     *     possible object is
     *     {@link NestedObject }
     *     
     */
    public NestedObject getInNestedObject() {
        return inNestedObject;
    }

    /**
     * Sets the value of the inNestedObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link NestedObject }
     *     
     */
    public void setInNestedObject(NestedObject value) {
        this.inNestedObject = value;
    }

    /**
     * Gets the value of the inStringArrayElement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inStringArrayElement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInStringArrayElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getInStringArrayElement() {
        if (inStringArrayElement == null) {
            inStringArrayElement = new ArrayList<String>();
        }
        return this.inStringArrayElement;
    }

    /**
     * Gets the value of the inIntArrayElement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inIntArrayElement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInIntArrayElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getInIntArrayElement() {
        if (inIntArrayElement == null) {
            inIntArrayElement = new ArrayList<Integer>();
        }
        return this.inIntArrayElement;
    }

}
