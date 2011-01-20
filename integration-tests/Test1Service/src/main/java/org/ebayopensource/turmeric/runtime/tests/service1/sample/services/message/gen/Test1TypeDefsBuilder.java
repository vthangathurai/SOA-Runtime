/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/

package org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.gen;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.BaseTypeDefsBuilder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.FlatSchemaComplexTypeImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.FlatSchemaElementDeclImpl;
import org.ebayopensource.turmeric.runtime.common.types.SOAFrameworkCommonTypeDefsBuilder;


/**
 * Note : Generated file, any changes will be lost upon regeneration.
 *
 */
public class Test1TypeDefsBuilder
    extends BaseTypeDefsBuilder
{

    private final static String NS1 = "http://www.ebayopensource.org/turmeric/common/v1/types";
    private final static String NS2 = "http://www.ebay.com/test/soaframework/sample/types1";

    public void build() {
        ArrayList<FlatSchemaComplexTypeImpl> complexTypes = new ArrayList<FlatSchemaComplexTypeImpl>();
        addComplexTypes0(complexTypes);

        addComplexTypeElements0(complexTypes);

        HashMap<QName, FlatSchemaElementDeclImpl> rootElements = new HashMap<QName, FlatSchemaElementDeclImpl>();

        SOAFrameworkCommonTypeDefsBuilder.includeTypeDefs(complexTypes, rootElements);

        m_complexTypes = complexTypes;
        m_rootElements = rootElements;
    }

    private void addComplexTypes0(ArrayList<FlatSchemaComplexTypeImpl> complexTypes) {
        // Type #0 (ErrorParameterType)
        complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "ErrorParameterType")));
        // Type #1 (ErrorType)
        complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "ErrorType")));
        // Type #2 (MyMessage)
        complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS2, "MyMessage")));
        // Type #3 (Address)
        complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS2, "Address")));
        // Type #4 (<Anonymous>)
        complexTypes.add(new FlatSchemaComplexTypeImpl());
        // Type #5 (<Anonymous>)
        complexTypes.add(new FlatSchemaComplexTypeImpl());
        // Type #6 (ErrorAndResponseMessage)
        QName qname = new QName(NS2, "ErrorAndResponseMessage");
        complexTypes.add(new FlatSchemaComplexTypeImpl(qname));
        // Type #7 (CustomErrorMessage)
        qname = new QName(NS2, "CustomErrorMessage");
        complexTypes.add(new FlatSchemaComplexTypeImpl(qname));
    }

    private void addComplexTypeElements0(ArrayList<FlatSchemaComplexTypeImpl> complexTypes) {
        FlatSchemaComplexTypeImpl currType;

        // Type #0 (ErrorParameterType)
        currType = complexTypes.get(0);
        currType.addAttribute(new QName(null, "ParamID"));
        currType.addSimpleElement(new QName(NS1, "Value"), 1);

        // Type #1 (ErrorType)
        currType = complexTypes.get(1);
        currType.addSimpleElement(new QName(NS1, "errorClassification"), 1);
        currType.addSimpleElement(new QName(NS1, "errorCode"), 1);
        currType.addComplexElement(new QName(NS1, "errorParameters"), complexTypes.get(0), -1);
        currType.addSimpleElement(new QName(NS1, "longMessage"), 1);
        currType.addSimpleElement(new QName(NS1, "severityCode"), 1);
        currType.addSimpleElement(new QName(NS1, "shortMessage"), 1);

        // Type #2 (MyMessage)
        currType = complexTypes.get(2);
        currType.addSimpleElement(new QName(NS2, "binaryData"), 1);
        currType.addSimpleElement(new QName(NS2, "body"), 1);
        currType.addSimpleElement(new QName(NS2, "createTime"), 1);
        currType.addComplexElement(new QName(NS2, "error"), complexTypes.get(1), -1);
        currType.addComplexElement(new QName(NS2, "recipients"), complexTypes.get(5), 1);
        currType.addSimpleElement(new QName(NS2, "something"), 1);
        currType.addSimpleElement(new QName(NS2, "subject"), 1);

        // Type #3 (Address)
        currType = complexTypes.get(3);
        currType.addSimpleElement(new QName(NS2, "city"), 1);
        currType.addSimpleElement(new QName(NS2, "emailAddress"), 1);
        currType.addSimpleElement(new QName(NS2, "postCode"), 1);
        currType.addSimpleElement(new QName(NS2, "state"), 1);
        currType.addSimpleElement(new QName(NS2, "streetName"), 1);
        currType.addSimpleElement(new QName(NS2, "streetNumber"), 1);

        // Type #4 (<Anonymous>)
        currType = complexTypes.get(4);
        currType.addSimpleElement(new QName(NS2, "key"), 1);
        currType.addComplexElement(new QName(NS2, "value"), complexTypes.get(3), 1);

        // Type #5 (<Anonymous>)
        currType = complexTypes.get(5);
        currType.addComplexElement(new QName(NS2, "entry"), complexTypes.get(4), -1);

        //Type #6 (ErrorAndResponseMessage)
        currType = complexTypes.get(6);
        currType.addSimpleElement(new QName(NS2, "response"), 1);
        currType.addComplexElement(new QName(NS2, "error"), complexTypes.get(1), -1);

        //Type #7 (CustomErrorMessage)
        currType = complexTypes.get(7);
        currType.addComplexElement(new QName(NS2, "error"), complexTypes.get(1), -1);
    }

}
