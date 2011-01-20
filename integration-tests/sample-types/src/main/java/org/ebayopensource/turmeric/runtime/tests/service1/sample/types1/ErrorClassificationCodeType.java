/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/

package org.ebayopensource.turmeric.runtime.tests.service1.sample.types1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ErrorClassificationCodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ErrorClassificationCodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="RequestError"/>
 *     &lt;enumeration value="SystemError"/>
 *     &lt;enumeration value="CustomCode"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ErrorClassificationCodeType")
@XmlEnum
public enum ErrorClassificationCodeType {

    @XmlEnumValue("RequestError")
    REQUEST_ERROR("RequestError"),
    @XmlEnumValue("SystemError")
    SYSTEM_ERROR("SystemError"),
    @XmlEnumValue("CustomCode")
    CUSTOM_CODE("CustomCode");
    private final String value;

    ErrorClassificationCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ErrorClassificationCodeType fromValue(String v) {
        for (ErrorClassificationCodeType c: ErrorClassificationCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
