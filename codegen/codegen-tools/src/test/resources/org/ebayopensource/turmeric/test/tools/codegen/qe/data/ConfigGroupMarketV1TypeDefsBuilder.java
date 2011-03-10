package com.ebayopensource.turmeric.services.gen;
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
public class ConfigGroupMarketTypeDefsBuilder
extends BaseTypeDefsBuilder
{
private final static String NS1 = "http://www.ebayopensource.com/turmeric/services";
public void build() {
ArrayList<FlatSchemaComplexTypeImpl> complexTypes = new ArrayList<FlatSchemaComplexTypeImpl>();
addComplexTypes0(complexTypes);

addComplexTypeElements0(complexTypes);

HashMap<QName, FlatSchemaElementDeclImpl> rootElements = new HashMap<QName, FlatSchemaElementDeclImpl>();
addRootElements0(complexTypes, rootElements);

SOAFrameworkCommonTypeDefsBuilder.includeTypeDefs(complexTypes, rootElements);

m_complexTypes = complexTypes;
m_rootElements = rootElements;
}
private void addComplexTypes0(ArrayList<FlatSchemaComplexTypeImpl> complexTypes) {
// Type #0 (ExtensionType)
complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "ExtensionType")));
// Type #1 (ErrorParameter)
complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "ErrorParameter")));
// Type #2 (ErrorMessage)
complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "ErrorMessage")));
// Type #3 (BaseResponse)
complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "BaseResponse")));
// Type #4 (BaseRequest)
complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "BaseRequest")));
// Type #5 (GetVersionResponse)
complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "GetVersionResponse")));
// Type #6 (GetVersionRequest)
complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "GetVersionRequest")));
// Type #7 (ErrorData)
complexTypes.add(new FlatSchemaComplexTypeImpl(new QName(NS1, "ErrorData")));
// Type #8 (<Anonymous>)
complexTypes.add(new FlatSchemaComplexTypeImpl());
}
private void addComplexTypeElements0(ArrayList<FlatSchemaComplexTypeImpl> complexTypes) {
FlatSchemaComplexTypeImpl currType;

// Type #0 (ExtensionType)
currType = complexTypes.get(0);
currType.addSimpleElement(new QName(NS1, "id"), 1);
currType.addSimpleElement(new QName(NS1, "version"), 1);
currType.addSimpleElement(new QName(NS1, "contentType"), 1);
currType.addSimpleElement(new QName(NS1, "value"), 1);

// Type #1 (ErrorParameter)
currType = complexTypes.get(1);
currType.addAttribute(new QName(NS1, "name"));

// Type #2 (ErrorMessage)
currType = complexTypes.get(2);
currType.addComplexElement(new QName(NS1, "error"), complexTypes.get(7), -1);

// Type #3 (BaseResponse)
currType = complexTypes.get(3);
currType.addSimpleElement(new QName(NS1, "ack"), 1);
currType.addComplexElement(new QName(NS1, "errorMessage"), complexTypes.get(2), 1);
currType.addSimpleElement(new QName(NS1, "version"), 1);
currType.addSimpleElement(new QName(NS1, "timestamp"), 1);
currType.addComplexElement(new QName(NS1, "extension"), complexTypes.get(0), -1);

// Type #4 (BaseRequest)
currType = complexTypes.get(4);
currType.addComplexElement(new QName(NS1, "extension"), complexTypes.get(0), -1);

// Type #5 (GetVersionResponse)
currType = complexTypes.get(5);
currType.addSimpleElement(new QName(NS1, "ack"), 1);
currType.addComplexElement(new QName(NS1, "errorMessage"), complexTypes.get(2), 1);
currType.addSimpleElement(new QName(NS1, "version"), 1);
currType.addSimpleElement(new QName(NS1, "timestamp"), 1);
currType.addComplexElement(new QName(NS1, "extension"), complexTypes.get(0), -1);
currType.addSimpleElement(new QName(NS1, "version"), 1);

// Type #6 (GetVersionRequest)
currType = complexTypes.get(6);
currType.addComplexElement(new QName(NS1, "extension"), complexTypes.get(0), -1);

// Type #7 (ErrorData)
currType = complexTypes.get(7);
currType.addSimpleElement(new QName(NS1, "errorId"), 1);
currType.addSimpleElement(new QName(NS1, "domain"), 1);
currType.addSimpleElement(new QName(NS1, "subdomain"), 1);
currType.addSimpleElement(new QName(NS1, "severity"), 1);
currType.addSimpleElement(new QName(NS1, "category"), 1);
currType.addSimpleElement(new QName(NS1, "message"), 1);
currType.addSimpleElement(new QName(NS1, "exceptionId"), 1);
currType.addComplexElement(new QName(NS1, "parameter"), complexTypes.get(1), -1);

// Type #8 (<Anonymous>)
currType = complexTypes.get(8);
currType.addSimpleElement(new QName(NS1, "firstname"), 1);
currType.addSimpleElement(new QName(NS1, "lastname"), 1);
}
private void addRootElements0(ArrayList<FlatSchemaComplexTypeImpl> complexTypes, HashMap<QName, FlatSchemaElementDeclImpl> rootElements) {
rootElements.put(new QName(NS1, "getVersionResponse"), FlatSchemaElementDeclImpl.createRootComplexElement(new QName(NS1, "getVersionResponse"), complexTypes.get(5)));
rootElements.put(new QName(NS1, "getVersionRequest"), FlatSchemaElementDeclImpl.createRootComplexElement(new QName(NS1, "getVersionRequest"), complexTypes.get(6)));
rootElements.put(new QName(NS1, "person"), FlatSchemaElementDeclImpl.createRootComplexElement(new QName(NS1, "person"), complexTypes.get(8)));
}
}