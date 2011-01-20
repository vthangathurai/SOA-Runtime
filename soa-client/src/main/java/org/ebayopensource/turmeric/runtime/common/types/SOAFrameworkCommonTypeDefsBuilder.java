/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.types;


import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.FlatSchemaComplexTypeImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.schema.FlatSchemaElementDeclImpl;
import org.ebayopensource.turmeric.runtime.common.types.SOACommonConstants;


/**
 * Used by TypeDefsBuilderGenerator.java to include common types.
 *
 * @author arajmony
 */
public class SOAFrameworkCommonTypeDefsBuilder
{

    private final static String NS1 = SOACommonConstants.SOA_TYPES_NAMESPACE;
    private static ArrayList<FlatSchemaComplexTypeImpl> complexTypes;
    private static HashMap<QName, FlatSchemaElementDeclImpl> rootElements;

    static {
    	complexTypes = new ArrayList<FlatSchemaComplexTypeImpl>();
        addComplexTypes0(complexTypes);
        addComplexTypeElements0(complexTypes);

        rootElements = new HashMap<QName, FlatSchemaElementDeclImpl>();
        addRootElements0(complexTypes, rootElements);
    }


    /**
     * Includes the given type definitions into type def builder.
     * @param complexTypesParam A list FlatSchemaComplexTypeImpl, each contains type definition for a complex type 
     * @param rootElementsParam A map of QName to FlatSchemaElementDeclImpl, each corresponding to an global element
     *    defined in a schema.
     */
    public static void includeTypeDefs(ArrayList<FlatSchemaComplexTypeImpl> complexTypesParam,
    		HashMap<QName, FlatSchemaElementDeclImpl> rootElementsParam){

    	complexTypesParam.addAll(complexTypes);
        rootElementsParam.putAll(rootElements);

    }

    private static void addComplexTypes0(ArrayList<FlatSchemaComplexTypeImpl> complexTypes) {
        // Type #0 (ErrorMessage)
        complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "ErrorMessage")));
        // Type #1 (ErrorData)
        complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "ErrorData")));
        // Type #2 (ErrorParameter)
        complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "ErrorParameter")));
    }

    private static void addComplexTypeElements0(ArrayList<FlatSchemaComplexTypeImpl> complexTypes) {
        FlatSchemaComplexTypeImpl currType;

        // Type #0 (ErrorMessage)
        currType = complexTypes.get(0);
        currType.addComplexElement(new QName(NS1, "error"), complexTypes.get(1), -1);

        // Type #1 (ErrorData)
        currType = complexTypes.get(1);
        currType.addSimpleElement(new QName(NS1, "errorId"), 1);
        currType.addSimpleElement(new QName(NS1, "domain"), 1);
        currType.addSimpleElement(new QName(NS1, "subdomain"), 1);
        currType.addSimpleElement(new QName(NS1, "severity"), 1);
        currType.addSimpleElement(new QName(NS1, "category"), 1);
        currType.addSimpleElement(new QName(NS1, "message"), 1);
        currType.addSimpleElement(new QName(NS1, "exceptionId"), 1);
        currType.addComplexElement(new QName(NS1, "parameter"), complexTypes.get(2), -1);

        // Type #2 (ErrorParameter)
        currType = complexTypes.get(2);
        currType.addAttribute(new QName(null, "name"));
    }

    private static void addRootElements0(ArrayList<FlatSchemaComplexTypeImpl> complexTypes, HashMap<QName, FlatSchemaElementDeclImpl> rootElements) {
        rootElements.put(new QName(NS1, "errorMessage"), FlatSchemaElementDeclImpl.createRootComplexElement(new QName(NS1, "ErrorMessage"), complexTypes.get(0)));
    }

}
