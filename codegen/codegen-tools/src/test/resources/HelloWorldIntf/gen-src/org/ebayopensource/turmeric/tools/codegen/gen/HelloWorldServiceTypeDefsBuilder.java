
package org.ebayopensource.turmeric.tools.codegen.gen;

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
public class HelloWorldServiceTypeDefsBuilder
    extends BaseTypeDefsBuilder
{


    public void build() {
        ArrayList<FlatSchemaComplexTypeImpl> complexTypes = new ArrayList<FlatSchemaComplexTypeImpl>();
         
        HashMap<QName, FlatSchemaElementDeclImpl> rootElements = new HashMap<QName, FlatSchemaElementDeclImpl>();
         
        SOAFrameworkCommonTypeDefsBuilder.includeTypeDefs(complexTypes, rootElements);
         
        m_complexTypes = complexTypes;
        m_rootElements = rootElements;
    }

}
