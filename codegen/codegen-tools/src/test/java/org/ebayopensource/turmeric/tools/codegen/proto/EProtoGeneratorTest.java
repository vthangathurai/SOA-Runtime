package org.ebayopensource.turmeric.tools.codegen.proto;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.junit.Test;

import com.ebay.soaframework.tools.codegen.CodeGenContext;
import com.ebay.soaframework.tools.codegen.exception.CodeGenFailedException;
import com.ebay.soaframework.tools.codegen.external.WSDLUtil;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.WSDLParserException;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.SchemaType;
import com.ebay.soaframework.tools.codegen.fastserformat.FastSerFormatCodegenBuilder;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.ProtoBufCompiler;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.ProtobufSchemaMapper;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.eproto.EProtoGenerator;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.exception.ProtobufModelGenerationFailedException;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufSchema;
import com.ebay.soaframework.tools.codegen.util.CodeGenConstants;
import com.ebay.soaframework.tools.codegen.util.CodeGenUtil;
import com.ebay.test.TestAnnotate;
import com.ebay.test.soaframework.tools.codegen.CodegenTestUtils;

public class EProtoGeneratorTest extends AbstractServiceGeneratorTestCase{
	
	
	File wsdl = getCodegenDataFileInput("CalcService.wsdl");
	
	private static String destLoc = ".";
	private static String[] getFindItemsServiceArgs() {
		String testArgs[] = new String[] {
				"-servicename",
				"FindItemsService",
				"-wsdl",
				"./UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/SearchFindItemServiceV2.wsdl",
				"-genType", "ClientNoConfig", 
				"-src", ".\\UnitTests\\src",
				"-dest", destLoc, 
				"-scv", "1.0.0", 
				"-bin", ".\\bin",
				 "-enabledNamespaceFolding",
				"-nonXSDFormats", "protobuf" };
		return testArgs;
	}
	

	private static String[] getAllComplexTypeWsdlArgs() {
		String testArgs[] = new String[] {
				"-servicename",
				"CalculatorService",
				"-wsdl",
				"./UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/TestAllComplexTypeWsdl.wsdl",
				"-genType", "ClientNoConfig", 
				"-envmapper", "com.ebay.soaframework.extended.sif.MarketplaceEnvironmentMapperImpl",
				"-src", "./UnitTests/src",
//				"-mdest", "./meta-src",
				"-dest", destLoc, 
				"-scv", "1.0.0", 
//				"-bin", "./bin",
				 "-enabledNamespaceFolding",
				"-nonXSDFormats", "protobuf" };
		return testArgs;
	}
	

	private static String[] getComplexTypeWsdlFailure() {
		String testArgs[] = new String[] {
				"-servicename",
				"CalculatorServiceFailure",
				"-wsdl",
				"./UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/TestAllComplexTypeWsdl.wsdl",
				"-genType", "ClientNoConfig", 
				"-src", ".\\UnitTests\\src",
				"-dest", destLoc, 
				"-scv", "1.0.0", 
				"-bin", ".\\bin",
				 "-enabledNamespaceFolding",
				"-nonXSDFormats", "protobuf" };
		return testArgs;
	}

	private static void generateArtifacts(CodeGenContext codeGenContext) throws Exception{
		String fastSerFormatStr = codeGenContext.getInputOptions().getSupportedFastSerFormats();
		//Is service enabled for fast ser format
		if( null == fastSerFormatStr || "".equals(fastSerFormatStr) ){
			//fast ser format not enabled. Hence return.
			return;
		}
		
		List<SchemaType> listOfSchemaTypes;
		try {
			listOfSchemaTypes = FastSerFormatCodegenBuilder.getInstance().generateSchema( codeGenContext );
		} catch (WSDLParserException e) {
			throw new CodeGenFailedException( "Generate Schema Failed.", e );
		} catch (WSDLException e) {
			throw new CodeGenFailedException( "Generate Schema Failed. Unable to created wsdl definition.", e );
		}
			
		ProtobufSchema schema;
		try {
			schema = ProtobufSchemaMapper.getInstance().createProtobufSchema(listOfSchemaTypes, codeGenContext);
		} catch (ProtobufModelGenerationFailedException e) {
			throw new CodeGenFailedException("Proto buf model generation failed.", e);
		}

		try {
			ProtoBufCompiler.getInstance().compileProtoFile(schema, codeGenContext);
		} catch (CodeGenFailedException codeGenFailedException) {
			throw codeGenFailedException;
		} catch (Exception exception) {
			throw new CodeGenFailedException(exception.getMessage(), exception);
		}
		
		try {
			EProtoGenerator.getInstance().generate(schema, codeGenContext);
		} catch (CodeGenFailedException codeGenFailedException) {
			throw codeGenFailedException;
		} catch (Exception exception) {
			throw new CodeGenFailedException(exception.getMessage(), exception);
		}
	}
	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, 
			feature = TestAnnotate.Feature.Codegen, 
			subFeature = "", 
			description = "", 
			bugID = "", 
			trainID = "", 
			projectID = "", 
			authorDev = "", 
			authorQE = "")
	public void testDePolymorphizedFindItemServiceWsdl() throws Exception {
		
		CodeGenContext context = getCodeGenContext(getFindItemsServiceArgs());

		String wsdlFileLoc = context.getInputOptions().getInputFile();
		
		if(context.getWsdlDefinition() == null){
			Definition definition = WSDLUtil.getWSDLDefinition(wsdlFileLoc);
			context.setWsdlDefinition(definition);
		}
		context.setMetaSrcDestLocation(CodeGenUtil.genDestFolderPath(destLoc, 
				CodeGenConstants.META_SRC_FOLDER));
		WSDLUtil.populateCodegenCtxWithWSDLDetails(wsdlFileLoc, context);
		

//		FastSerFormatCodegenBuilder.getInstance().buildFastSerFormatArtifacts(context);
		generateArtifacts(context);
//		compileMultipleGeneratedEProtos(context);
		
//		Class eprotoClass = IntrospectUtil.loadClass(eprotoName);
//		System.out.println(eprotoClass.getCanonicalName());

		CodeGenUtil.deleteContentsOfDir(new File(context.getJavaSrcDestLocation()));
	}
	
	
	@Test	
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, 
			feature = TestAnnotate.Feature.Codegen, 
			subFeature = "", 
			description = "", 
			bugID = "", 
			trainID = "", 
			projectID = "", 
			authorDev = "", 
			authorQE = "")
	public void testAllComplexTypeWsdlAWsdl() throws Exception {
		CodeGenContext context = getCodeGenContext(getAllComplexTypeWsdlArgs());

		String wsdlFileLoc = context.getInputOptions().getInputFile();
		
		if(context.getWsdlDefinition() == null){
			Definition definition = WSDLUtil.getWSDLDefinition(wsdlFileLoc);
			context.setWsdlDefinition(definition);
		}
		context.setMetaSrcDestLocation(CodeGenUtil.genDestFolderPath(destLoc, 
						CodeGenConstants.META_SRC_FOLDER));

		WSDLUtil.populateCodegenCtxWithWSDLDetails(wsdlFileLoc, context);

//		FastSerFormatCodegenBuilder.getInstance().buildFastSerFormatArtifacts(context);
		generateArtifacts(context);
		

		String targetArtifactSnippet = ".\\UnitTests\\src\\com\\ebay\\test\\soaframework\\tools\\codegen\\data\\SnippetEProtoXMLCal.txt";
		String generatedFileName = context.getJavaSrcDestLocation() + 
		"com/ebay/test/soaframework/tools/codegen/proto/extended/ETestAllPossibleComplexType.java";

		boolean isValid = CodegenTestUtils.validateGeneratedContent(generatedFileName,targetArtifactSnippet
				,"BotService","BotService",null);

		assertTrue(isValid);
		String targetArtifactSnippet2 = ".\\UnitTests\\src\\com\\ebay\\test\\soaframework\\tools\\codegen\\data\\SnippetEProtoNewInstance.txt";
		isValid = CodegenTestUtils.validateGeneratedContent(generatedFileName,targetArtifactSnippet2
				,"BotService","BotService",null);
		assertTrue(isValid);
		String eprotoName = "com.ebay.test.soaframework.tools.codegen.proto.extended.ETestComplexType.java";
		compileGeneratedEProtos(context, eprotoName);
		
		CodeGenUtil.deleteContentsOfDir(new File(context.getJavaSrcDestLocation()));
	}
	
	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, 
			feature = TestAnnotate.Feature.Codegen, 
			subFeature = "", 
			description = "", 
			bugID = "", 
			trainID = "", 
			projectID = "", 
			authorDev = "", 
			authorQE = "")
	public void testComplexTypeWsdlFailure() throws Exception {
		CodeGenContext context = getCodeGenContext(getComplexTypeWsdlFailure());

		String wsdlFileLoc = context.getInputOptions().getInputFile();
		
		if(context.getWsdlDefinition() == null){
			Definition definition = WSDLUtil.getWSDLDefinition(wsdlFileLoc);
			context.setWsdlDefinition(definition);
		}

		try {
//			FastSerFormatCodegenBuilder.getInstance().buildFastSerFormatArtifacts(context);
			generateArtifacts(context);
		} catch (CodeGenFailedException e) {
			
		}
		
		
//		Class eprotoClass = IntrospectUtil.loadClass(eprotoName);
//		System.out.println(eprotoClass.getCanonicalName());

		CodeGenUtil.deleteContentsOfDir(new File(context.getJavaSrcDestLocation()));
		
	}
	

}
