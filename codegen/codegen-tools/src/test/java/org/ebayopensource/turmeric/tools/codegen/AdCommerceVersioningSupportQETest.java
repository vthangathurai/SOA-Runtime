/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.codegen.external.WSDLUtil;
import org.ebayopensource.turmeric.tools.codegen.handler.UserResponseHandler;
import org.junit.Before;
import org.junit.Test;
/**
 * @author rmohagaonkar
 *
 */
public class AdCommerceVersioningSupportQETest  extends AbstractServiceGeneratorTestCase{
/*The test cases are made for the AdComerce Versioning Support*/
	
	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	NamespaceContextImpl nsc;

	@Before
	public void init() throws Exception{
		
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		prDir = testingdir.getFile("tmp");
		nsc = new NamespaceContextImpl();
		
		
		}
	
	@Test
	public void CheckWsdlWithPublicServiceName() {
		
		File wsdl = getCodegenQEDataFileInput("Testing1.wsdl");
			System.out.println("*****************CheckWsdlWithPublicServiceName Starts*************************");
			String publicServiceWsdlPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/services/wsdl/MyServiceV1/MyServiceV1_public.wsdl";

			boolean isException = false;
			ServiceGenerator servicegenerator = createServiceGenerator();
			try {
				
				String[] testArgs = new String[] {
						"-servicename","MyServiceV1",
						"-wsdl",wsdl.getAbsolutePath(),
						"-genType", "ServiceMetadataProps", 
						"-publicservicename","MyService",
						 "-pr", destDir.getAbsolutePath() };
				servicegenerator.startCodeGen(testArgs);
				
			} catch (Exception e) {
				isException = true;
				assertFalse(true);
			}
			assertFalse(isException);
			File modifiedWsdlPath = new File(publicServiceWsdlPath);
			/*Quick Bug 6570  fir the wsdl not created at the required wsl location with Ad Commerce support .*/
		assertTrue(modifiedWsdlPath.exists());
			String serviceName = null;
			/*Test case # 1 and 2 Validate that the service wsdl  is created at required location  and consist of service name tag value as public service name*/
			try {
				
				serviceName = WSDLUtil.getFirstServiceQName(publicServiceWsdlPath)
						.getLocalPart();
			} catch (PreProcessFailedException e) {
				assertTrue(false);
			}
			
			/*Bug Id :: 6567 for the wsdl not updated with the public service name*/
			assertEquals("MyService", serviceName);
			
			System.out.println("*************************CheckWsdlWithPublicServiceName Ends*****************************");
}
	@Test
	public void CheckServiceAndCLientConfigWithPublicServiceName() {
		
		File wsdl = getCodegenQEDataFileInput("Testing1.wsdl");
			System.out.println("*****************CheckWsdlWithPublicServiceName Starts*************************");
			String publicServiceWsdlPath = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/services/wsdl/MyServiceV1/MyServiceV1_public.wsdl";

			boolean isException = false;
			ServiceGenerator servicegenerator = createServiceGenerator();
			try {
				
				/*String[] testArgs = new String[] {
						"-servicename","MyServiceV1",
						"-wsdl","Vanilla-Codegen\\ServiceInputFiles\\Testing1.wsdl",
						"-genType", "ServiceMetadataProps", 
						"-publicservicename","MyService",
						 "-pr", "AntTests" };
				servicegenerator.startCodeGen(testArgs);*/
				String[] testArgs1 = new String[] {
						"-servicename",
						"MyServiceV1",
						"-wsdl",
						wsdl.getAbsolutePath(),
						"-genType", "ServerConfig",
						"-dest", destDir.getAbsolutePath() ,
						"-src", destDir.getAbsolutePath() ,
						"-bin", binDir.getAbsolutePath() ,
						"-publicservicename","MyService",
						
						 "-pr",  destDir.getAbsolutePath()  };
				
				servicegenerator
						.startCodeGen(testArgs1);
				String[] testArgs2 = new String[] {
						"-servicename",
						"MyServiceV1",
						"-wsdl",
						wsdl.getAbsolutePath(),
						"-genType", "ClientConfig",
						"-dest",destDir.getAbsolutePath(),
						"-src",destDir.getAbsolutePath(),
						"-bin",binDir.getAbsolutePath(),
						"-publicservicename","MyService",
						
						 "-pr", destDir.getAbsolutePath() };
				
				servicegenerator
						.startCodeGen(testArgs2);
			} catch (Exception e) {
				isException = true;
				assertFalse(true);
			}
			assertFalse(isException);
			File modifiedWsdlPath = new File(publicServiceWsdlPath);
			/*Quick Bug 6570  fir the wsdl not created at the required wsl location with Ad Commerce support .*/
		assertTrue(modifiedWsdlPath.exists());
			String serviceName = null;
			/*Test case # 1 and 2 Validate that the service wsdl  is created at required location  and consist of service name tag value as public service name*/
			try {
				
				serviceName = WSDLUtil.getFirstServiceQName(publicServiceWsdlPath)
						.getLocalPart();
			} catch (PreProcessFailedException e) {
				assertTrue(false);
			}
			
			/*Bug Id :: 6567 for the wsdl not updated with the public service name*/
			assertEquals("MyService", serviceName);
			
			System.out.println("*************************CheckWsdlWithPublicServiceName Ends*****************************");
}
	
	private ServiceGenerator createServiceGenerator() {
		UserResponseHandler testResponseHandler = new TestUserResponseHandler();
		ServiceGenerator serviceGenerator = new ServiceGenerator(testResponseHandler);

		return serviceGenerator;
	}
	
	
}
