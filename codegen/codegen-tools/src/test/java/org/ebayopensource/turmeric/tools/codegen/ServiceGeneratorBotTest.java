/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;

import org.ebayopensource.turmeric.junit.asserts.JavaSourceAssert;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.GeneratedAssert;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.junit.Test;

import com.thoughtworks.qdox.model.JavaMethod;

public class ServiceGeneratorBotTest extends AbstractServiceGeneratorTestCase {
    @SuppressWarnings("unused")
    private class GenDirs {
        File destDir;
        File binDir;
        File genSrcDir;
        File genMetaSrcDir;
    }

	private GenDirs generateBotService() throws Exception {
		// Initialize testing paths
		testingdir.ensureEmpty();
		File wsdl = TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/BotService.wsdl");
		File destDir = testingdir.getDir();
		File binDir = testingdir.getFile("bin");
		File metaDir = testingdir.getFile("meta-src");
		
		MavenTestingUtils.ensureDirExists(metaDir);

		// @formatter:off
		String args[] = {
			"-servicename", "BotService",
			"-cn", "BotService",
			"-namespace","http://www.virtuoz.fr/",
			"-wsdl", wsdl.getAbsolutePath(),
			"-gentype", "All",
			"-dest", destDir.getAbsolutePath(),
			"-bin", binDir.getAbsolutePath(),
		};
		// @formatter:on

		performDirectCodeGen(args, binDir);

        GenDirs dirs = new GenDirs();
        dirs.destDir = destDir;
        dirs.binDir = binDir;
        dirs.genSrcDir = testingdir.getFile("gen-src");
        dirs.genMetaSrcDir = testingdir.getFile("gen-meta-src");

        return dirs;
	}

	@Test
	public void generatedConsumerForGetProxyContents() throws Exception {
        GenDirs dirs = generateBotService();

        File actualJava = GeneratedAssert.assertJavaExists(dirs.genSrcDir, "fr.virtuoz.gen.BaseBotServiceConsumer");
        File expectedJava = getExpectedSource("BaseBotServiceConsumer");

        String signature = "protected AsyncBotService getProxy() throws ServiceException";
        JavaSourceAssert.assertMethodsEqual(expectedJava, actualJava, signature);

//        assertGeneratedContainsSnippet(
//				"gen-src/fr/virtuoz/gen/BaseBotServiceConsumer.java",
//				"SnippetGetProxyInConsumer.txt", "BotService", "BotService",
//				null);
	}

	@Test
	public void generatedConsumerForGetServiceInvokerOptionsMethodContents()
			throws Exception {
        GenDirs dirs = generateBotService();

        File actualJava = GeneratedAssert.assertJavaExists(dirs.genSrcDir, "fr.virtuoz.gen.BaseBotServiceConsumer");
        File expectedJava = getExpectedSource("BaseBotServiceConsumer");
        
        String signature = "public ServiceInvokerOptions getServiceInvokerOptions() throws ServiceException";
        JavaSourceAssert.assertMethodsEqual(expectedJava, actualJava, signature);
	}

	@Test
	public void generatedConsumerForGetServiceMethodContents() throws Exception {
        GenDirs dirs = generateBotService();

        File actualJava = GeneratedAssert.assertJavaExists(dirs.genSrcDir, "fr.virtuoz.gen.BaseBotServiceConsumer");
        File expectedJava = getExpectedSource("BaseBotServiceConsumer");
        
        String signature = "public Service getService() throws ServiceException";
        JavaSourceAssert.assertMethodsEqual(expectedJava, actualJava, signature);
	}

	@Test
	public void generatedConsumerForPollMethodContents() throws Exception {
        GenDirs dirs = generateBotService();
        
        File actualJava = GeneratedAssert.assertJavaExists(dirs.genSrcDir, "fr.virtuoz.gen.BotServiceProxy");
        File expectedJava = getExpectedSource("BotServiceProxy");

        String signature = "public List<Response<?>> poll(boolean block, boolean partial) throws InterruptedException";
        JavaSourceAssert.assertMethodsEqual(expectedJava, actualJava, signature);
	}

	@Test
	public void generatedDispatcherForUpperCaseOperationNameInDispatcherConstructor()
			throws Exception {
        GenDirs dirs = generateBotService();
        
        File java = GeneratedAssert.assertJavaExists(dirs.genSrcDir, "fr.virtuoz.impl.gen.BotServiceRequestDispatcher");
        
        String expectedline = "addSupportedOperation(\"TalkXml\", new Class[] {TalkXml.class }, new Class[] {TalkXmlResponse.class });";
        String expectedconstructor = "public BotServiceRequestDispatcher()";
        
        JavaMethod constructor = JavaSourceAssert.assertConstructorExists(java, expectedconstructor);
        JavaSourceAssert.assertBodyContains(constructor, expectedline);
	}

	@Test
	public void generatedDispatcherForUpperCaseOperationNameInDispatchersDispatchMethod()
			throws Exception {
        GenDirs dirs = generateBotService();
        
        File java = GeneratedAssert.assertJavaExists(dirs.genSrcDir, "fr.virtuoz.impl.gen.BotServiceRequestDispatcher");

        String expectedline = "if (\"TalkXml\".equals(operationName)) {";
        String expectedmethod = "public boolean dispatch(MessageContext param0, BotService param1) throws ServiceException";

        JavaMethod method = JavaSourceAssert.assertMethodExists(java, expectedmethod);
        JavaSourceAssert.assertBodyContains(method, expectedline);
	}

	@Test
	public void generatedInterfaceForPollMethod() throws Exception {
        GenDirs dirs = generateBotService();

        File java = GeneratedAssert.assertJavaExists(dirs.genSrcDir, "fr.virtuoz.AsyncBotService");
        String expected = "public List<Response<?>> poll(boolean block, boolean partial) throws InterruptedException";
        JavaSourceAssert.assertMethodExists(java, expected);
	}

	@Test
	public void generatedProxyForPollMethodContents() throws Exception {
        GenDirs dirs = generateBotService();

        File actualJava = GeneratedAssert.assertJavaExists(dirs.genSrcDir, "fr.virtuoz.gen.BotServiceProxy");
        File expectedJava = getExpectedSource("BotServiceProxy");

        String signature = "public List<Response<?>> poll(boolean block, boolean partial) throws InterruptedException";
        JavaSourceAssert.assertMethodsEqual(expectedJava, actualJava, signature);
		generateBotService();

//		assertGeneratedContainsSnippet(
//				"gen-src/fr/virtuoz/gen/BotServiceProxy.java",
//				"SnippetPollMethodInProxy.txt", "BotService", "BotService",
//				null);
	}

	@Test
	public void generatedProxyForUpperCaseOperationNameInAsyncMethod()
			throws Exception {
        GenDirs dirs = generateBotService();
        
        File java = GeneratedAssert.assertJavaExists(dirs.genSrcDir, "fr.virtuoz.gen.BotServiceProxy");
        
        String expectedline = "Dispatch dispatch = m_service.createDispatch(\"TalkXml\");";
        String expectedmethods[] = {
          "public Future<?> talkXmlAsync(TalkXml param0, AsyncHandler<TalkXmlResponse> param1)",
          "public Response<TalkXmlResponse> talkXmlAsync(TalkXml param0)"
        };

        for(String expectedmethod: expectedmethods) {
            JavaMethod method = JavaSourceAssert.assertMethodExists(java, expectedmethod);
            JavaSourceAssert.assertBodyContains(method, expectedline);
        }
	}

	@Test
	public void generatedProxyForUpperCaseOperationNameInSyncMethod()
			throws Exception {
        GenDirs dirs = generateBotService();
        
        File java = GeneratedAssert.assertJavaExists(dirs.genSrcDir, "fr.virtuoz.gen.BotServiceProxy");
        
        String expectedline = "m_service.invoke(\"TalkXml\", params, returnParamList);";
        String expectedmethod = "public TalkXmlResponse talkXml(TalkXml param0)";
        
        JavaMethod method = JavaSourceAssert.assertMethodExists(java, expectedmethod);
        JavaSourceAssert.assertBodyContains(method, expectedline);
	}

	@Test
	public void generatedTypeMappingsForUpperCaseOperationName()
			throws Exception {
        GenDirs dirs = generateBotService();
        
        File typemapping = GeneratedAssert.assertFileExists(dirs.genMetaSrcDir, "META-INF/soa/common/config/BotService/TypeMappings.xml");

        assertGeneratedContainsSnippet(typemapping,
                        "SnippetUpperCaseOperationNameInTypeMappings.txt", "BotService", "BotService", "TalkXml");
	}

    private File getExpectedSource(String classname) {
        return MavenTestingUtils.getTestResourceFile("gen/example/botservice/" + classname + ".java");
    }
}
