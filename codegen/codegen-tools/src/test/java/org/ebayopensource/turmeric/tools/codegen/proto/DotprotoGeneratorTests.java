/**
 * 
 */
package com.ebay.test.soaframework.tools.codegen;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import junit.framework.Assert;

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
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufSchema;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.SchemaTypeName;
import com.ebay.test.TestAnnotate;

/**
 * @author anav
 *
 */
public class DotprotoGeneratorTests extends CodeGenBaseTestCase{
	public static String[] getEmptyWsdlArgs() {
		String testArgs[] = new String[] {
				"-servicename",
				"FindItemServiceEmpty",
				"-wsdl",
				"UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/FindItemServiceEmpty.wsdl",
				"-genType", "ClientNoConfig", "-src", ".\\UnitTests\\src",
				"-dest", ".\\tmp", "-scv", "1.0.0", "-bin", ".\\bin",
				 "-enabledNamespaceFolding",
				"-nonXSDFormats", "protobuf" };
		return testArgs;
	}

	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testDotprotoGenerationForEmptyWSDL() throws Exception {
		String protoPath = ".\\tmp\\meta-src\\META-INF\\soa\\services\\proto\\FindItemServiceEmpty\\FindItemServiceEmpty.proto";
		File emptyProto = new File( protoPath );
		if(emptyProto.exists() ){
			emptyProto.delete();
		}

		CodeGenContext context = ProtobufSchemaMapperTestUtils.getCodeGenContext( getEmptyWsdlArgs() );
		
		FastSerFormatCodegenBuilder.getInstance().validateServiceIfApplicable(context);
		
		List<SchemaType> listOfSchemaTypes;
		try {
			listOfSchemaTypes = FastSerFormatCodegenBuilder.getInstance().generateSchema( context );
		} catch (WSDLParserException e) {
			Assert.fail( "Generate Schema Failed." + e.getMessage() );
			throw e;
		}
		
		ProtobufSchema schema = ProtobufSchemaMapper.getInstance().createProtobufSchema(listOfSchemaTypes, context);

		try {
			DotProtoGenerator.getInstance().generate(schema, context);
		} catch (Exception e1) {
			Assert.fail( "Dot Proto generation failed." + e1.getMessage() );
			throw e1;
		}
			
		File dir = new File( schema.getDotprotoTargetDir() );
		File dotproto = new File(dir, schema.getDotprotoFileName() );
		
		if( !protoPath.equals( dotproto.getPath() ) ){
			Assert.fail("The file paths are different. Expected path =" + protoPath + ". Generated path"+ dotproto.getPath());
		}
		if(dotproto.exists() ){
			ProtobufSchemaMapperTestUtils.loadFindItemServiceManuallyWrittenProtoFile( dotproto.getPath() );
		}
	}
	
	public static String[] getTestAWsdlArgs() {
		String testArgs[] = new String[] {
				"-servicename",
				"FindItemService",
				"-wsdl",
				"UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/FindItemServiceAdjustedV3.wsdl",
				"-genType", "ClientNoConfig", "-src", ".\\UnitTests\\src",
				"-dest", ".", "-scv", "1.0.0", "-bin", ".\\bin",
				// "-enabledNamespaceFolding",
				"-nonXSDFormats", "protobuf" };
		return testArgs;
	}

	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testDotprotoGeneration() throws Exception {
		CodeGenContext context = ProtobufSchemaMapperTestUtils.getCodeGenContext( getTestAWsdlArgs() );
		
		List<SchemaType> listOfSchemaTypes;
		try {
			listOfSchemaTypes = FastSerFormatCodegenBuilder.getInstance().generateSchema( context );
		} catch (WSDLParserException e) {
			throw new CodeGenFailedException( "Generate Schema Failed.", e );
		}
		
		ProtobufSchema schema = ProtobufSchemaMapper.getInstance().createProtobufSchema(listOfSchemaTypes, context);

		try {
			DotProtoGenerator.getInstance().generate(schema, context);
		} catch (Exception e1) {
			throw new CodeGenFailedException("Dot Proto generation failed.", e1);
		}
		
		String dotprotofilepath = "UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/FindItemServiceAdjustedV3.proto";
		List<ProtobufMessage> messagesFromFile = ProtobufSchemaMapperTestUtils.loadFindItemServiceManuallyWrittenProtoFile( dotprotofilepath );
		
		updateMessagesLoadedFromFile( messagesFromFile, context, "com.ebay.marketplace.search.v1.services" );
		
		File dir = new File( schema.getDotprotoTargetDir() );
		File dotproto = new File(dir, schema.getDotprotoFileName() );
		
		System.out.println(dotproto.getPath());
		List<ProtobufMessage> generatedMessages = ProtobufSchemaMapperTestUtils.loadFindItemServiceManuallyWrittenProtoFile( dotproto.getPath() );
		
		updateMessagesLoadedFromFile( generatedMessages, context, "com.ebay.marketplace.search.v1.services" );
		System.out.println(messagesFromFile);
		System.out.println(generatedMessages);
		System.out.println("done");
		validateTheSchema(generatedMessages, context, messagesFromFile);
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
	
	private void validateTheSchema(List<ProtobufMessage> generatedMessages, CodeGenContext context, List<ProtobufMessage> messagesFromFile) throws Exception{
		Map<SchemaTypeName, ProtobufMessage> schemaTypeMap = ProtobufSchemaMapperTestUtils.createMessageMapFromList( generatedMessages );

		
		for( ProtobufMessage message : messagesFromFile ){
			ProtobufMessage messageFromModel = schemaTypeMap.get(message.getSchemaTypeName());
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
		}
	}
	
}
