package org.ebayopensource.turmeric.tools.codegen.proto;

import java.io.File;

import org.junit.Test;

import com.ebay.soaframework.tools.codegen.ServiceGenerator;
import com.ebay.soaframework.tools.codegen.util.CodeGenUtil;
import com.ebay.test.TestAnnotate;
import com.ebay.test.soaframework.tools.codegen.ServiceGeneratorTestUtils;

public class ProtoBufFullRun extends BaseEprotoGeneratorTest{

	private static String destLoc = ".";
	
	private static String[] getAllComplexTypeWsdlArgs() {
		String testArgs[] = new String[] {
				"-servicename",
				"CalculatorService1",
				"-wsdl",
				"./UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/TestFullRunComplexTypeWsdl.wsdl",
				"-genType", "ClientNoConfig", 
				"-envmapper", "com.ebay.soaframework.extended.sif.MarketplaceEnvironmentMapperImpl",
				"-src", "./UnitTests/src",
				"-dest", destLoc, 
				"-mdest", "./meta-src/",
				"-scv", "1.0.0", 
//				"-bin", "./bin",
				 "-enabledNamespaceFolding",
				"-nonXSDFormats", "protobuf" };
		return testArgs;
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
	public void testComplexTypeWsdlFullFlow() throws Exception {
		
		CodeGenUtil.deleteContentsOfDir(new File(destLoc + "/gen-meta-src"));
		logDebugMessage("**** Begin testForTestingDefaultingInputTypeInterface() ****");
		String protoLocation = CodeGenUtil.genDestFolderPath(destLoc, "/meta-src/META-INF/soa/services/proto/CalculatorService1");
		File deleteProtoFileLocation = new File(protoLocation);
		if(deleteProtoFileLocation.exists())
			CodeGenUtil.deleteContentsOfDir(deleteProtoFileLocation);
		ServiceGenerator serviceGenerator = ServiceGeneratorTestUtils.createServiceGenerator();
		try {
			serviceGenerator.startCodeGen(getAllComplexTypeWsdlArgs());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

		logDebugMessage("**** End testForTestingDefaultingInputTypeInterface() ****");
	

		CodeGenUtil.deleteContentsOfDir(new File(destLoc + "/gen-src"));
	}
}
