/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen.proto;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.ebay.soaframework.tools.codegen.CodeGenContext;
import com.ebay.soaframework.tools.codegen.ServiceGenerator;
import com.ebay.soaframework.tools.codegen.exception.CodeGenFailedException;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.WSDLParserConstants;
import com.ebay.soaframework.tools.codegen.fastserformat.FastSerFormatCodegenBuilder;
import com.ebay.soaframework.tools.codegen.fastserformat.FastSerFormatCodegenValidator;
import com.ebay.soaframework.tools.codegen.fastserformat.FastSerFormatValidationError;
import com.ebay.soaframework.tools.codegen.fastserformat.ValidationRule;
import com.ebay.soaframework.tools.codegen.fastserformat.validator.FastSerFormatNotSupportedException;
import com.ebay.soaframework.tools.codegen.fastserformat.validator.FastSerFormatValidationHandler;
import com.ebay.test.TestAnnotate;

/**
 * @author rkulandaivel
 *
 */
public class FastSerFormatValidationTests extends CodeGenBaseTestCase {

	public static String[] getTestInvalidArgs() {
		String testArgs[] = new String[] {
				"-servicename",
				"CalcService",
				"-wsdl",
				"UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/CalcServiceProtobufInvalid.wsdl",
				"-genType", "All", "-src", ".\\UnitTests\\src",
				"-dest", ".\\tmp", "-scv", "1.0.0", "-bin", ".\\bin",
				//"-enabledNamespaceFolding",
				"-nonXSDFormats", "protobuf" };
		return testArgs;
	}

	public static String[] getTestValidArgs() {
		String testArgs[] = new String[] {
				"-servicename",
				"CalcService",
				"-wsdl",
				"UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/CalcServiceProtobufInvalid.wsdl",
				"-genType", "All", "-src", ".\\UnitTests\\src",
				"-dest", ".\\tmp", "-scv", "1.0.0", "-bin", ".\\bin",
				"-enabledNamespaceFolding",
				"-nonXSDFormats", "protobuf" };
		return testArgs;
	}
	
	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void validateService() throws Exception {
		CodeGenContext context = ProtobufSchemaMapperTestUtils.getCodeGenContext( getTestInvalidArgs() );
		
		//commented the test case bacause the check for enable namespace folding is removed.
//		try {
//			FastSerFormatCodegenBuilder.getInstance().validateServiceIfApplicable(context);
//		} catch (CodeGenFailedException e) {
//			if( !e.getMessage().contains( FastSerFormatCodegenValidator.NAMESPACE_FOLDING_MESSAGE )){
//				Assert.fail("The Test failed. Expected to fail for namespace folding false");
//			}
//		}
		
		context.getInputOptions().setInputFile(null);
		context.getInputOptions().setEnabledNamespaceFolding(true);
		try {
			FastSerFormatCodegenBuilder.getInstance().validateServiceIfApplicable(context);
		} catch (CodeGenFailedException e) {
			if( !e.getMessage().contains( FastSerFormatValidationHandler.EMPTY_WSDL_PATH )){
				e.printStackTrace();
				Assert.fail("The Test failed. Expected to fail for empty wsdl path");
			}
		}

		context.getInputOptions().setInputFile("UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/CalcServiceProtobufInvalidNS.wsdl");
		try {
			FastSerFormatCodegenBuilder.getInstance().validateServiceIfApplicable(context);
		} catch (CodeGenFailedException e) {
			if( !e.getMessage().contains( WSDLParserConstants.NS_URI_1999_SCHEMA_XSD )){
				e.printStackTrace();
				Assert.fail("The Test failed. Expected to fail for invalid xsd namespace");
			}
		}

		context = ProtobufSchemaMapperTestUtils.getCodeGenContext( getTestValidArgs() );
		try {
			FastSerFormatCodegenBuilder.getInstance().validateServiceIfApplicable(context);
		} catch (CodeGenFailedException e) {
			e.printStackTrace();
			if( !( e instanceof FastSerFormatNotSupportedException ) ){
				Assert.fail("The Test failed. Expected exception FastSerFormatNotSupportedException");
			}

			FastSerFormatNotSupportedException excep = (FastSerFormatNotSupportedException)e;
			if( excep.getErrors().size() == 0 ){
				Assert.fail("The test failed. Expected errors around 10.");
			}

			Set<ValidationRule> rules = new HashSet<ValidationRule>();
			
			for( FastSerFormatValidationError error : excep.getErrors() ){
				if(error.getDescription().contains("{0}") ||  error.getDescription().contains("{1}") || error.getDescription().contains("{2}") ){
					Assert.fail("The test failed. Message formation failed");
				}
				rules.add(error.getError());
			}
			
			for( ValidationRule rule : ValidationRule.values() ){
				if( rule == ValidationRule.REDEFINE_NOT_SUPPORTED 
						|| rule == ValidationRule.OLD_SCHEMAS_NOT_SUPPORTED 
						){
					continue;
				}
				if( !rules.contains(rule) ){
					Assert.fail("The rule "+rule.value() +" is not detected");
				}
			}
			Assert.assertTrue(excep.getMessage(), true);
			
			String message = excep.getMessage();
			if(message.endsWith(";")){
				Assert.fail("Message not formatted properly. It ends with semi colan");
			}
			return ;
			
		}
		Assert.fail("The validation succeede so the test case failed.");
		
	}

	public static String[] getTestArgsForXsdValidation() {
		String testArgs[] = new String[] {
				"-servicename",
				"CalcService",
				"-wsdl",
				"UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/CalcServiceProtobufInvalid.wsdl",
				"-genType", "ValidateXSDsForNonXSDFormats", 
				"-enabledNamespaceFolding",
				"-nonXSDFormats", "protobuf",
				"-xsdPathsForValidation", "types/InValidTypeLibrary/ComplexTypeA.xsd,types/InValidTypeLibrary/ComplexTypeB.xsd"};
		return testArgs;
	}
	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testParsingXSds() {

		ServiceGenerator serviceGenerator = ServiceGeneratorTestUtils
		.createServiceGenerator();
		
		Set<ValidationRule> expectedRules = new HashSet<ValidationRule>();
		expectedRules.add(ValidationRule.ANONYMOUS_TYPE_NOT_SUPPORTED );
		expectedRules.add(ValidationRule.POLYMORPHISM_NOT_SUPPORTED );
		try {
			serviceGenerator.startCodeGen(getTestArgsForXsdValidation());
		} catch( Exception e ){
			String message = e.getMessage();
			for( ValidationRule rule : expectedRules ){
				if( message.indexOf(rule.value() ) < 0 ){
					Assert.fail("The xsd is supposed to fail for rule '"+rule+"'. But id did not. ");
				}
			}
		}

	}
}
