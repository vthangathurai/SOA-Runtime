/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen.proto;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;

import com.ebay.soaframework.tools.codegen.CodeGenContext;
import com.ebay.soaframework.tools.codegen.exception.CodeGenFailedException;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.WSDLParserException;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.SchemaType;
import com.ebay.soaframework.tools.codegen.fastserformat.FastSerFormatCodegenBuilder;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.ProtobufSchemaMapper;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.dotproto.DotProtoGenerator;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufEnumMessage;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufField;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufMessage;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufOption;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufOptionType;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufSchema;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.SchemaTypeName;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.tag.DefaultProtobufTagGenerator;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.tag.ProtobufTagGenerator;
import com.ebay.test.TestAnnotate;

/**
 * @author rkulandaivel
 * 
 */
public class ProtobufSchemaMapperTests extends CodeGenBaseTestCase {

	public static String[] getTestAWsdlArgs() {
		String testArgs[] = new String[] {
				"-servicename",
				"FindItemService",
				"-wsdl",
				"UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/FindItemServiceAdjustedV3.wsdl",
				"-genType", "ClientNoConfig", "-src", ".\\UnitTests\\src",
				"-dest", ".\\tmp", "-scv", "1.0.0", "-bin", ".\\bin",
				// "-enabledNamespaceFolding",
				"-nonXSDFormats", "protobuf" };
		return testArgs;
	}

	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testDePolymorphizedFindItemServiceWsdl() throws Exception {
		CodeGenContext context = ProtobufSchemaMapperTestUtils.getCodeGenContext( getTestAWsdlArgs() );
		
		FastSerFormatCodegenBuilder.getInstance().validateServiceIfApplicable(context);
		
		List<SchemaType> listOfSchemaTypes;
		try {
			listOfSchemaTypes = FastSerFormatCodegenBuilder.getInstance().generateSchema( context );
		} catch (WSDLParserException e) {
			throw new CodeGenFailedException( "Generate Schema Failed.", e );
		}
		
		int i = 0;
		for(SchemaType schemaType : listOfSchemaTypes){
			//System.out.println(i+"======"+ schemaType.getTypeName() + "===" + schemaType.getClass().getName());
			i++;
		}
		ProtobufSchema schema = ProtobufSchemaMapper.getInstance().createProtobufSchema(listOfSchemaTypes, context);
		//System.out.println(schema);
		String dotprotofilepath = "UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/FindItemServiceAdjustedV3.proto";

		List<ProtobufMessage> messagesFromFile = ProtobufSchemaMapperTestUtils.loadFindItemServiceManuallyWrittenProtoFile( dotprotofilepath );
		updateMessagesLoadedFromFile( messagesFromFile, context, "com.ebay.marketplace.search.v1.services" );
		System.out.println( "messagesFromFile==" +messagesFromFile );
		
		validateTheSchema(schema, context, messagesFromFile);
		ProtobufSchemaMapperTestUtils.validateTagNumberGeneration( context, schema );
	}

	private void updateMessagesLoadedFromFile(List<ProtobufMessage> messagesFromFile, CodeGenContext context, String basePackage ){
		for( ProtobufMessage message : messagesFromFile ){
			String msgName = message.getMessageName();
			String typeName = msgName;
			if( message.isEnumType() ){
				typeName = ((ProtobufEnumMessage)message).getEnumMessageName();
			}
			message.setJaxbClassName(basePackage + "." + msgName);
			message.setEprotoClassName(basePackage + ".proto.extended.E" + msgName);
			
			if( message.isEnumType() ){
				message.setJprotoClassName(basePackage + ".proto.FindItemService$" + typeName + "$"+msgName);
			}else{
				message.setJprotoClassName(basePackage + ".proto.FindItemService$" + typeName);
			}
			for(ProtobufField field : message.getFields()){
				field.setTypeOfField( ProtobufSchemaMapperTestUtils.getFieldType(field) );
			}

			
			String serviceNamespace = context.getNamespace();


			//Test type FieldValue
			QName fieldValName = new QName(serviceNamespace, msgName);
			SchemaTypeName fieldValueTypeName = new SchemaTypeName( fieldValName );
			message.setSchemaTypeName(fieldValueTypeName);
		}
	}
	
	private void validateTheSchema(ProtobufSchema schema, CodeGenContext context, List<ProtobufMessage> messagesFromFile) throws Exception{
		Map<SchemaTypeName, ProtobufMessage> schemaTypeMap = ProtobufSchemaMapperTestUtils.createMessageMapFromList( schema.getMessages() );

		Map<SchemaTypeName, ProtobufMessage> newSchemaTypeMap = new HashMap<SchemaTypeName, ProtobufMessage>();
		//re map
		for( Map.Entry<SchemaTypeName, ProtobufMessage> entry : schemaTypeMap.entrySet() ){
			SchemaTypeName key = entry.getKey();
			QName oldQ = key.getTypeName();
			QName newQ = new QName( oldQ.getNamespaceURI(), entry.getValue().getMessageName() );

			SchemaTypeName newKey = new SchemaTypeName( newQ );
			newSchemaTypeMap.put(newKey, entry.getValue());
		}
		
		for( ProtobufMessage message : messagesFromFile ){
			ProtobufMessage messageFromModel = newSchemaTypeMap.get(message.getSchemaTypeName());
			if(messageFromModel == null){
				throw new Exception("The model does not have an message corresponding to name " + message.getSchemaTypeName());
			}
			boolean equal = false;
			if( message instanceof ProtobufEnumMessage){
				equal = ProtobufSchemaMapperTestUtils.ProtobufMessageComparator.compareEnumMessage((ProtobufEnumMessage)message, (ProtobufEnumMessage)messageFromModel);
			}else{
				equal = ProtobufSchemaMapperTestUtils.ProtobufMessageComparator.compareMessage(message, messageFromModel);				
			}

			if(!equal){
				System.out.println(message);
				System.out.println(messageFromModel);
				throw new Exception("The proto buf message generated for " + message.getSchemaTypeName() + " has some issues.");
			}
			if("FindItemsResponse".equals(messageFromModel.getMessageName())){
				if( !messageFromModel.isRootType() ){
					throw new Exception("The proto buf message FindItemsResponse should be a root type" );
				}
			}
		}

		if( !schema.getDotprotoFileName().equals( context.getServiceAdminName() + ".proto" )){
			Assert.fail("Dot proto file name is wrong");
		}

		if( !schema.getDotprotoFilePackage().equals("com.ebay.marketplace.search.v1.services") ){
			Assert.fail("Dot proto file package is wrong");
		}

		if( ! (schema.getMessagesImported().size() == 0 )){
			Assert.fail( "Message imports not supposed to be configured" );
		}
		ProtobufOption option1 = new ProtobufOption();
		option1.setOptionType( ProtobufOptionType.JAVA_OUTER_CLASS_NAME );
		option1.setOptionValue( context.getServiceAdminName() );
		
		ProtobufOption option2 = new ProtobufOption();
		option2.setOptionType( ProtobufOptionType.JAVA_PACKAGE_NAME );
		option2.setOptionValue( "com.ebay.marketplace.search.v1.services.proto" );

		ProtobufOption option3 = new ProtobufOption();
		option3.setOptionType( ProtobufOptionType.OPTIMIZE_FOR );
		option3.setOptionValue( "SPEED" );
		for( ProtobufOption opt : schema.getDotprotoOptions() ){
			if(opt.getOptionType() == ProtobufOptionType.JAVA_OUTER_CLASS_NAME ){
				if( !context.getServiceAdminName().equals(opt.getOptionValue() )  ){
					Assert.fail("The protobuf option value is configured wrong for  JAVA_OUTER_CLASS_NAME");
				}
			}else if( opt.getOptionType() == ProtobufOptionType.JAVA_PACKAGE_NAME ){
				if( !"com.ebay.marketplace.search.v1.services.proto".equals(opt.getOptionValue() )  ){
					Assert.fail("The protobuf option value is configured wrong for  JAVA_PACKAGE_NAME");
				}				
			}else if( opt.getOptionType() == ProtobufOptionType.OPTIMIZE_FOR ){
				if( !"SPEED".equals(opt.getOptionValue() )  ){
					Assert.fail("The protobuf option value is configured wrong for  OPTIMIZE_FOR");
				}
			}else {
				Assert.fail("The protobuf options are configured wrong");
			}
		}

		
		Assert.assertTrue(schema.toString(), true);
	}
}
