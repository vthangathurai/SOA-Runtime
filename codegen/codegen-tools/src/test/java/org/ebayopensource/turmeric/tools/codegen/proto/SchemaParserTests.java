/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen.proto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ebay.soaframework.tools.codegen.external.WSDLUtil;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.WSDLParserConstants;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.Attribute;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.AttributeGroup;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.AttributeGroupType;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.ComplexContent;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.ComplexType;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.ElementType;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.GroupType;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.RestrictionEnumeration;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.Schema;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.SchemaType;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.Sequence;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.SimpleContent;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.SimpleType;
import com.ebay.test.TestAnnotate;
import com.ibm.wsdl.util.xml.QNameUtils;

/**
 * @author rkulandaivel
 * 
 */
public class SchemaParserTests extends CodeGenBaseTestCase {
	
	private static final String XSD_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	
	private static final QName STRING_TYPE = new QName(XSD_SCHEMA_NAMESPACE, "string");
	private static final QName INT_TYPE = new QName(XSD_SCHEMA_NAMESPACE, "int");
	private static final QName DOUBLE_TYPE = new QName(XSD_SCHEMA_NAMESPACE, "double");
	private static final QName FLOAT_TYPE = new QName(XSD_SCHEMA_NAMESPACE, "float");
	private static final QName BOOLEAN_TYPE = new QName(XSD_SCHEMA_NAMESPACE, "boolean");
	private static final QName DATE_TYPE = new QName(XSD_SCHEMA_NAMESPACE, "date");
	private static final QName LONG_TYPE = new QName(XSD_SCHEMA_NAMESPACE, "long");

    private static final QName schema1999 =
        new QName(WSDLParserConstants.NS_URI_1999_SCHEMA_XSD, "schema");
    private static final QName schema2000 =
        new QName(WSDLParserConstants.NS_URI_2000_SCHEMA_XSD, "schema");
    private static final QName schema2001 =
        new QName(WSDLParserConstants.NS_URI_2001_SCHEMA_XSD, "schema");

	private Document parseXSDOrWsdl(String wsdlOrXsdPath) throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        
		builder = factory.newDocumentBuilder();
		return builder.parse(wsdlOrXsdPath);
	}
	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testEnumType() throws Exception {
		String enumTypeXsdPath = "meta-src/types/TypeLibrarySupportForProtoBuf/MyEnumType.xsd";
		
		Document document = parseXSDOrWsdl(enumTypeXsdPath);
		
		Schema schema = new Schema(document.getDocumentElement());
		
		List<SchemaType> types = schema.getTypes();
		for(Object type : types){
			if(type instanceof SimpleType){
				SimpleType simpleType= (SimpleType)type;
				
				if( !("MyEnumType".equals(simpleType.getName()) ) ){
					Assert.fail("The simple type is not correct");
				}else if( !( STRING_TYPE.equals(simpleType.getRestriction().getBase()) )){
					Assert.fail("The restriction base is not correct");
				}else if( simpleType.getRestriction().getEnumerations().size() != 2 ){
					Assert.fail("The restriction enumeration size is not correct");
				}
				
				List<String> enumValueList = new ArrayList<String>();
				enumValueList.add("OptA");
				enumValueList.add("OptB");
				
				for(RestrictionEnumeration resEnum : simpleType.getRestriction().getEnumerations() ){
					if( !(enumValueList.contains(resEnum.getEnumValue())) ){
						Assert.fail("The restriction enum value is not correct");
					}
				}
			}
		}
	}
	
	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testSimpleType() throws Exception {
		String enumTypeXsdPath = "meta-src/types/TypeLibrarySupportForProtoBuf/MySimpleTypeIntRest.xsd";
		
		Document document = parseXSDOrWsdl(enumTypeXsdPath);
		
		Schema schema = new Schema(document.getDocumentElement());
		
		List<SchemaType> types = schema.getTypes();
		for(Object type : types){
			if(type instanceof SimpleType){
				SimpleType simpleType= (SimpleType)type;
				
				if( !("MySimpleTypeIntRest".equals(simpleType.getName()) ) ){
					Assert.fail("The simple type name is not correct");
				}else if( !( INT_TYPE.equals(simpleType.getRestriction().getBase()) )){
					Assert.fail("The restriction base is not correct");
				}else if( !simpleType.getRestriction().hasTotalDigits() ){
					Assert.fail("The total digits is defined");
				}else if( simpleType.getRestriction().getTotalDigits() != 3){
					Assert.fail("The total digits defined is 3");
				}
				
			}
		}
	}

	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testSimpleTypeWithList() throws Exception {
		String enumTypeXsdPath = "meta-src/types/TypeLibrarySupportForProtoBuf/MySimpleTypeList.xsd";
		
		Document document = parseXSDOrWsdl(enumTypeXsdPath);
		
		Schema schema = new Schema(document.getDocumentElement());
		
		List<SchemaType> types = schema.getTypes();
		for(Object type : types){
			if(type instanceof SimpleType){
				SimpleType simpleType= (SimpleType)type;
				
				if( !("MySimpleTypeList".equals(simpleType.getName()) ) ){
					Assert.fail("The simple type name is not correct");
				}else if( !( simpleType.getList().getItemType().equals(DOUBLE_TYPE) )){
					Assert.fail("The list type is not correct");
				}
				
			}
		}
	}
	
	
	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testComplexType() throws Exception {
		String enumTypeXsdPath = "meta-src/types/TypeLibrarySupportForProtoBuf/MyComplexType.xsd";
		
		Document document = parseXSDOrWsdl(enumTypeXsdPath);
		
		Schema schema = new Schema(document.getDocumentElement());
		
		List<SchemaType> types = schema.getTypes();
		for(Object type : types){
			if(type instanceof ComplexType){
				ComplexType complexType= (ComplexType)type;
				
				if( !("MyComplexType".equals(complexType.getTypeName().getLocalPart() ) ) ){
					Assert.fail("The complex type name is not correct");
				}else if( !complexType.hasSequence()){
					Assert.fail("The complex type does not have sequence");
				}else if( complexType.getSequence().getEntries().size() != 2){
					Assert.fail("The sequence entry size is not correct");
				}

				int i = 0;
				for( Sequence.SequenceEntry entry : complexType.getSequence().getEntries()){
					if( !(entry.isElement())){
						Assert.fail("The entry should be element type");
					}
					ElementType elementType = entry.getElement();
					if( i == 0 ){
						if( !( "elemB".equals( elementType.getName()) ) ){
							Assert.fail("The element name isnot correct");
						}else if( (elementType.getMaxOccurs() != 1) || (elementType.getMinOccurs() != 0)){
							Assert.fail("The min occurs and max occurs values are wrong");
						}else if( !elementType.getElementType().equals( new QName(schema.getTargetNamespace(), "MySimpleTypeIntRest") ) ){
							Assert.fail("The element type is wrong");
						}
					}else if( i == 1){
						if( !( "elemA".equals( elementType.getName()) ) ){
							Assert.fail("The element name isnot correct");
						}else if( (elementType.getMaxOccurs() != 1) || (elementType.getMinOccurs() != 0)){
							Assert.fail("The min occurs and max occurs values are wrong");
						}else if( !elementType.getElementType().equals( FLOAT_TYPE ) ){
							Assert.fail("The element type is wrong");
						}						
					}
					i++;
				}
			}
		}
	}
	
	
	
	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testComplexTypeWithAttribute() throws Exception {
		String enumTypeXsdPath = "meta-src/types/TypeLibrarySupportForProtoBuf/ComplexTypeWithAttr.xsd";
		
		Document document = parseXSDOrWsdl(enumTypeXsdPath);
		
		Schema schema = new Schema(document.getDocumentElement());
		
		List<SchemaType> types = schema.getTypes();
		for(Object type : types){
			if(type instanceof ComplexType){
				ComplexType complexType= (ComplexType)type;
				
				if( !("ComplexTypeWithAttr".equals(complexType.getTypeName().getLocalPart() ) ) ){
					Assert.fail("The complex type name is not correct");
				}else if( !complexType.hasSequence()){
					Assert.fail("The complex type does not have sequence");
				}else if( complexType.getSequence().getEntries().size() != 1){
					Assert.fail("The sequence entry size is not correct");
				}else if( complexType.getAttributes().size() != 1 ){
					Assert.fail("The type should have one attribute");
				}else if(!complexType.isAbstract()){
					Assert.fail("The type is abstract");
				}
					
				Attribute attr = complexType.getAttributes().get(0);
					
				if( !"attrZ".equals( attr.getAttributeName() ) ){
					Assert.fail("The name of attribute defined is attrZ");
				}else if(attr.getUse() != Attribute.AttributeUse.OPTIONAL){
					Assert.fail("Attribute use is not correct");
				}else if(!FLOAT_TYPE.equals(attr.getValueType())){
					Assert.fail("The type is float type");
				}
			}
		}
	}

	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testComplexTypeWithAttributeGroup() throws Exception {
		String enumTypeXsdPath = "meta-src/types/TypeLibrarySupportForProtoBuf/ComplexTypeWithAttrGp.xsd";
		
		Document document = parseXSDOrWsdl(enumTypeXsdPath);
		
		Schema schema = new Schema(document.getDocumentElement());
		
		QName groupName = new QName(schema.getTargetNamespace(), "MyAttrGpA");
		
		List<SchemaType> types = schema.getTypes();
		for(Object type : types){
			if(type instanceof ComplexType){
				ComplexType complexType= (ComplexType)type;
				
				if( !("ComplexTypeWithAttrGp".equals(complexType.getTypeName().getLocalPart() ) ) ){
					Assert.fail("The complex type name is not correct");
				}else if( !complexType.hasSequence()){
					Assert.fail("The complex type does not have sequence");
				}else if( complexType.getSequence().getEntries().size() != 2){
					Assert.fail("The sequence entry size is not correct");
				}else if( complexType.getAttributeGroup().size() != 1 ){
					Assert.fail("The type should have one attribute");
				}else if(complexType.isAbstract()){
					Assert.fail("The type is abstract");
				}
					
				AttributeGroup attrGroup = complexType.getAttributeGroup().get(0);
				
				if( !groupName.equals( attrGroup.getGroupRef() ) ){
					Assert.fail("The group ref is not correct");
				}

			}
			if(type instanceof AttributeGroupType){
				AttributeGroupType groupType = (AttributeGroupType)type;

				if(!groupName.equals(groupType.getTypeName())){
					Assert.fail("The group name is not correct");
				}else if(groupType.getAttributes().size() != 2){
					Assert.fail("The number of attributes is 2");
				}
				
				List<String> attrNames = Arrays.asList("attrB", "attrA");
				
				for(Attribute attr : groupType.getAttributes()){
					if( !attrNames.contains(attr.getAttributeName())){
						Assert.fail("The attribute name is not correct");
					}
				}
				
			}
		}
	}
	
	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testComplexTypeWithChoice() throws Exception {
		String enumTypeXsdPath = "meta-src/types/TypeLibrarySupportForProtoBuf/ComplexTypeUsingChoice.xsd";
		
		Document document = parseXSDOrWsdl(enumTypeXsdPath);
		
		Schema schema = new Schema(document.getDocumentElement());
		
		List<SchemaType> types = schema.getTypes();
		for(Object type : types){
			if(type instanceof ComplexType){
				ComplexType complexType= (ComplexType)type;
				
				if( !("ComplexTypeUsingChoice".equals(complexType.getTypeName().getLocalPart() ) ) ){
					Assert.fail("The complex type name is not correct");
				}else if( !complexType.hasChoice()){
					Assert.fail("The complex type does not have Choice");
				}else if( complexType.getChoice().getElements().size() != 2){
					Assert.fail("The choice elements size is not correct");
				}else if(complexType.isAbstract()){
					Assert.fail("The type is not abstract");
				}

				List<QName> elemNames = Arrays.asList(new QName(schema.getTargetNamespace(),"argE"), new QName(schema.getTargetNamespace(), "argF") );
				for(ElementType elemType : complexType.getChoice().getElements() ){
					if( !elemNames.contains(elemType.getTypeName())) {
						Assert.fail("The type name is not correct");
					}else if( !STRING_TYPE.equals( elemType.getElementType() ) ){
						Assert.fail("The element type is string");
					}
				}
					
			}
		}
	}

	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testComplexTypeUsingGroup() throws Exception {
		String enumTypeXsdPath = "meta-src/types/TypeLibrarySupportForProtoBuf/ComplexTypeusingGrp.xsd";
		
		Document document = parseXSDOrWsdl(enumTypeXsdPath);
		
		Schema schema = new Schema(document.getDocumentElement());
		
		QName group = new QName(schema.getTargetNamespace(), "Literal");
		List<SchemaType> types = schema.getTypes();
		for(Object type : types){
			if(type instanceof ComplexType){
				ComplexType complexType= (ComplexType)type;
				
				if( !("ComplexTypeusingGrp".equals(complexType.getTypeName().getLocalPart() ) ) ){
					Assert.fail("The complex type name is not correct");
				}else if( !complexType.hasGroup()){
					Assert.fail("The complex type does not have Group");
				}else if( !group.equals( complexType.getGroup().getGroupRef() ) ){
					Assert.fail("The group ref is wrong");
				}else if(complexType.isAbstract()){
					Assert.fail("The type is not abstract");
				}
			}
			if( type instanceof GroupType){
				GroupType gpType = (GroupType)type;
				
				if( !group.equals(gpType.getTypeName())){
					Assert.fail("The group name is not correct");
				}
				if( !gpType.hasChoice()){
					Assert.fail("The group has choice");
				}
				if( gpType.getChoice().getElements().size() != 3 ){
					Assert.fail("number of elements is 3");
				}
				
				String ts = schema.getTargetNamespace();
				List<QName> elemNames = Arrays.asList(new QName(ts,"xInt"), new QName(ts,"xBoolean"), new QName(ts,"xString"));
				List<QName> typeNames = Arrays.asList(BOOLEAN_TYPE, INT_TYPE, STRING_TYPE);

				for(ElementType elemType : gpType.getChoice().getElements()){
					if( !elemNames.contains(elemType.getTypeName())){
						Assert.fail("The element names are not correct");
					}
					if( !typeNames.contains(elemType.getElementType())){
						Assert.fail("The element types are not correct");
					}
				}
			}
		}
	}
	
	
	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testComplexTypeUsingSimpleContent() throws Exception {
		String enumTypeXsdPath = "meta-src/types/TypeLibrarySupportForProtoBuf/ComplexTypeSimpleContentWithAttrGp.xsd";
		
		Document document = parseXSDOrWsdl(enumTypeXsdPath);
		
		Schema schema = new Schema(document.getDocumentElement());
		
		QName group = new QName(schema.getTargetNamespace(), "MyAttrGpA");
		List<SchemaType> types = schema.getTypes();
		for(Object type : types){
			if(type instanceof ComplexType){
				ComplexType complexType= (ComplexType)type;
				
				if( !("ComplexTypeSimpleContentWithAttrGp".equals(complexType.getTypeName().getLocalPart() ) ) ){
					Assert.fail("The complex type name is not correct");
				}else if( !complexType.hasSimpleContent()){
					Assert.fail("The complex type does not have SimpleContent");
				}
				
				SimpleContent sc = complexType.getSimpleContent();
				
				if(sc.getExtension() == null){
					Assert.fail("The extension is null");
				}
				if(!STRING_TYPE.equals(sc.getExtension().getBase())){
					Assert.fail("The extsnion base is string type");
				}

				if(sc.getExtension().getAttributeGroups().size() != 1){
					Assert.fail("The extsnion has one attribute group");
				}
				if(sc.getExtension().getAttributes().size() != 2){
					Assert.fail("The extsnion has one attributes");
				}
				AttributeGroup attrGp= sc.getExtension().getAttributeGroups().get(0);
				if( !group.equals(attrGp.getGroupRef())){
					Assert.fail("The group ref is not correct");
				}


				List<QName> elemNames = Arrays.asList(new QName(schema.getTargetNamespace(),"argA"), new QName(schema.getTargetNamespace(),"argB"));
				List<QName> typeNames = Arrays.asList(BOOLEAN_TYPE, DATE_TYPE);
				
				for( Attribute attr : sc.getExtension().getAttributes() ){
					if( !elemNames.contains(attr.getAttributeQName() )){
						Assert.fail("The attribute names are not correct");
					}
					if( !typeNames.contains(attr.getValueType() ) ){
						Assert.fail("The attribute value type is not correct");
					}
				}
			}
		}
	}
	
	
	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testComplexTypeUsingComplexContent() throws Exception {
		String enumTypeXsdPath = "meta-src/types/TypeLibrarySupportForProtoBuf/MyComplexTypeComplexContentType.xsd";
		
		Document document = parseXSDOrWsdl(enumTypeXsdPath);
		
		Schema schema = new Schema(document.getDocumentElement());
		
		QName baseType = new QName(schema.getTargetNamespace(), "MyComplexType");

		List<SchemaType> types = schema.getTypes();
		for(Object type : types){
			if(type instanceof ComplexType){
				ComplexType complexType= (ComplexType)type;
				
				if( !("MyComplexTypeComplexContentType".equals(complexType.getTypeName().getLocalPart() ) ) ){
					Assert.fail("The complex type name is not correct");
				}else if( !complexType.hasComplexContent()){
					Assert.fail("The complex type does not have Complex content");
				}
				
				ComplexContent sc = complexType.getComplexContent();
				if(sc.getExtension() == null){
					Assert.fail("The extension is null");
				}
				if(sc.getExtension().getSequence() == null){
					Assert.fail("The extension sequence is null");
				}
				if(!baseType.equals(sc.getExtension().getBase())){
					Assert.fail("The extsnion base is MyComplexType type");
				}
				if(sc.getExtension().getAttributeList().size() != 1){
					Assert.fail("The extsnion has one attributes");
				}
				Attribute attr = sc.getExtension().getAttributeList().get(0);
				if( !new QName(schema.getTargetNamespace(), "attrZ").equals(attr.getAttributeQName()) ){
					Assert.fail("The attribute name is attrZ");
				}
				Sequence seq = sc.getExtension().getSequence();
				if( seq.getEntries().size() != 2){
					Assert.fail( "The size of sequence entries is 2" );
				}
				

				int i = 0;
				for( Sequence.SequenceEntry entry : seq.getEntries()){
					if( !(entry.isElement())){
						Assert.fail("The entry should be element type");
					}
					ElementType elementType = entry.getElement();
					if( i == 0 ){
						if( !( "inputB".equals( elementType.getName()) ) ){
							Assert.fail("The element name isnot correct");
						}else if( (elementType.getMaxOccurs() != 1) || (elementType.getMinOccurs() != 0)){
							Assert.fail("The min occurs and max occurs values are wrong");
						}else if( !elementType.getElementType().equals( LONG_TYPE ) ){
							Assert.fail("The element type is wrong");
						}
					}else if( i == 1){
						if( !( "inputA".equals( elementType.getName()) ) ){
							Assert.fail("The element name isnot correct");
						}else if( (elementType.getMaxOccurs() != 1) || (elementType.getMinOccurs() != 0)){
							Assert.fail("The min occurs and max occurs values are wrong");
						}else if( !elementType.getElementType().equals( new QName(schema.getTargetNamespace(), "MySimpleTypeIntRest") ) ){
							Assert.fail("The element type is wrong");
						}						
					}
					i++;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testCurrenyRateServiceWsdl() throws Exception {
		String wsdlPath = "UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/CurrencyRateService.wsdl";
		
		Definition def = WSDLUtil.getWSDLDefinition( wsdlPath );
		List<Schema> schemas = new ArrayList<Schema>();
		
		Types types = def.getTypes();
        if (types != null) {
            Iterator extEleIt = types.getExtensibilityElements().iterator();

            while (extEleIt.hasNext()) {
                Object nextEl = extEleIt.next();

                Element schemaEl;

                if(nextEl instanceof javax.wsdl.extensions.schema.Schema) {
                    javax.wsdl.extensions.schema.Schema typesElement = (javax.wsdl.extensions.schema.Schema)nextEl;
                    schemaEl = typesElement.getElement();
                } else if (nextEl instanceof UnknownExtensibilityElement) {
                    UnknownExtensibilityElement typesElement = (UnknownExtensibilityElement) nextEl;
                    schemaEl = typesElement.getElement();
                } else {
                    continue;
                }

                if (QNameUtils.matches(schema2001, schemaEl)
                    || QNameUtils.matches(schema2000, schemaEl)
                    || QNameUtils.matches(schema1999, schemaEl)) {
                    Schema sc = new Schema(schemaEl);
                    schemas.add(sc);
                }
            }
        }
	}
}
