/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ebayopensource.turmeric.tools.GeneratedAssert;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConfigUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ibm.xtq.common.utils.Assert;

/**
 * @author rkulandaivel
 * 
 */
public class ServiceGeneratorTest8 extends AbstractServiceGeneratorTestCase {

	public static void closeQuietly(InputStream closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

	public static boolean contentEquals(int[] file1, int[] file2)
			throws IOException {

		if (file1 == null || file2 == null) {
			return false;
		}

		if (file1.length != file2.length) {
			// lengths differ, cannot be equal
			return false;
		}

		int counter = 0;
		while (counter < file1.length) {
			if (file1[counter] != file2[counter]) {
				return false;
			}
			counter++;
		}

		return true;
	}

	private static String getCommonTypesPackageFolderPath(String projectRoot) {
		return CodeGenUtil.toOSFilePath(projectRoot)
				+ CodeGenConstants.GEN_SRC_FOLDER
				+ File.separatorChar
				+ CodeGenConstants.CLIENT_GEN_FOLDER
				+ File.separatorChar
				+ CodeGenUtil
						.getFolderPathFrompackageName(SOAConstants.SOA_TYPES_NAMESPACE);
	}

	public static BufferedReader getFileReader(String fileName)
			throws IOException {

		File inFile = new File(fileName);

		Charset defaultCharset = Charset.defaultCharset();
		FileInputStream fileInStream = new FileInputStream(inFile);
		InputStreamReader bw = new InputStreamReader(fileInStream,
				defaultCharset);
		BufferedReader buffReader = new BufferedReader(bw);

		return buffReader;
	}

	private void assertFilePaths(String implClassPath,
			boolean implShouldExists, String dispatcherClassPath) {
		File destDir = getTestDestDir();
		if (implShouldExists) {
			// file does not exists but it should
			GeneratedAssert.assertFileExists(destDir, implClassPath);
		} else {
			// file should exists but it does not
			GeneratedAssert.assertPathNotExists(destDir, implClassPath);
		}
		GeneratedAssert.assertFileExists(destDir, dispatcherClassPath);
	}

	/**
	 * this test, tests whether ClientConfig.xml is generated with required
	 * default values in commented section.
	 */
	@Test
	public void createClientConfigWithCommentedValues1() throws Exception {
		System.out
				.println("**** Begin createClientConfigWithCommentedValues1() ****");
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		mavenTestingRules.setFailOnViolation(false);
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();

		// Setup arguments
		// @formatter:off
		String testArgs[] = new String[] { "-servicename", "ClientConfig",
				"-interface", "NotRequired", "-genType", "ClientConfig",
				"-src", srcDir.getAbsolutePath(), "-dest",
				destDir.getAbsolutePath(), "-namespace",
				"http://www.ebay.com/soa/MyGlobalConfig", "-cn", "MyClient1",
				"-environment", "Myenv" };
		// @formatter:on

		try {
			performDirectCodeGen(testArgs);
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.assertFalse("Code Gen Failed", true);
		}

		// check file generated
		File clientConfigDir = GeneratedAssert
				.assertFileExists(
						destDir,
						"gen-meta-src/META-INF/soa/client/config/MyClient1/Myenv/ClientConfig/ClientConfig.xml");

		final String CONFIG_GROUP_NAME = "@@ClientConfigGroupName@@";
		final String CONFIG_SERVICE_NAME = "@@ServiceName@@";
		final String PREFERRED_TRANSPORT_NAME = "@@PreferredTransport@@";
		final String SERVICE_INT_NAME = "@@ServiceInterfaceClassName@@";
		final String WSDL_LOCATION = "@@WSDLLocation@@";
		final String SERVICE_LOCATION = "@@ServiceLocation@@";
		final String REQ_DATA_BINDING = "@@RequestDataBinding@@";
		final String RESP_DATA_BINDING = "@@ResponseDataBinding@@";
		final String CONSUMER_ID = "@@ConsumerId@@";
		final String INVOCATION_USE_CASE = "@@InvocationUseCase@@";

		String contents = readFileAsString(clientConfigDir.getAbsolutePath());

		boolean templateWordsAreNotReplaced = false;
		if (contents.contains(SERVICE_INT_NAME)
				|| contents.contains(WSDL_LOCATION)
				|| contents.contains(SERVICE_LOCATION)
				|| contents.contains(REQ_DATA_BINDING)
				|| contents.contains(RESP_DATA_BINDING)
				|| contents.contains(CONSUMER_ID)
				|| contents.contains(INVOCATION_USE_CASE)
				|| contents.contains(CONFIG_GROUP_NAME)
				|| contents.contains(CONFIG_SERVICE_NAME)
				|| contents.contains(PREFERRED_TRANSPORT_NAME)) {
			templateWordsAreNotReplaced = true;
		}
		Assert.assertFalse(templateWordsAreNotReplaced);

		if (!contents
				.contains("<service-interface-class-name>NotRequired</service-interface-class-name>")) {
			Assert.assertFalse(
					"Service Interface class name 'NotRequired' missing", true);
		}

		if (!contents
				.contains("<invocation-use-case>ClientConfigClient</invocation-use-case>")) {
			Assert.assertFalse("invocation-use-case missing", true);
		}
		System.out
				.println("**** End createClientConfigWithCommentedValues1() ****");

	}

	@Test
	public void createServiceConfigWithCommentedValues() throws Exception {
		System.out
				.println("**** Begin createServiceConfigWithCommentedValues() ****");

		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		mavenTestingRules.setFailOnViolation(false);
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();

		// Setup arguments
		// @formatter:off
		String testArgs[] = new String[] { "-servicename", "ServerConfig",
				"-interface", "NotRequired", "-genType", "ServerConfig",
				"-src", srcDir.getAbsolutePath(), "-dest",
				destDir.getAbsolutePath(), "-namespace",
				"http://www.ebay.com/soa/MyGlobalConfig", "-cn", "MyClient1" };
		// @formatter:on

		try {
			performDirectCodeGen(testArgs);
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.assertFalse("Code Gen Failed", true);
		}

		// check file generated
		File serviceConfigDir = GeneratedAssert
				.assertFileExists(destDir,
						"gen-meta-src/META-INF/soa/services/config/ServerConfig/ServiceConfig.xml");

		final String SERVICE_GROUP_NAME = "@@ServiceConfigGroupName@@";
		final String SERVICE_INT_NAME = "@@ServiceInterfaceClassName@@";
		final String SERVICE_IMPL_NAME = "@@ServiceImplClassName@@";

		String contents = readFileAsString(serviceConfigDir.getAbsolutePath());
		boolean templateWordsAreNotReplaced = false;
		if (contents.contains(SERVICE_INT_NAME)
				|| contents.contains(SERVICE_GROUP_NAME)
				|| contents.contains(SERVICE_IMPL_NAME)) {
			templateWordsAreNotReplaced = true;
		}
		Assert.assertFalse(templateWordsAreNotReplaced);

		if (!contents
				.contains("<service-interface-class-name>NotRequired</service-interface-class-name>")) {
			Assert.assertFalse(
					"Service Interface class name 'NotRequired' missing", true);
		}

		System.out
				.println("**** End createServiceConfigWithCommentedValues() ****");

	}

	@Test
	public void createServiceConfigWithRestMappings() throws Exception {
		System.out
				.println("**** Begin createServiceConfigWithRestMappings() ****");

		MavenTestingUtils.ensureEmpty(testingdir);
		mavenTestingRules.setFailOnViolation(false);
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir();
		File binDir = testingdir.getFile("bin");

		// @formatter:off
		String testArgs[] = new String[] { "-servicename", "ServerConfig",
				"-interface", "NotRequired", "-genType", "ServerConfig",
				"-src", srcDir.getAbsolutePath(), "-dest",
				destDir.getAbsolutePath(), "-namespace",
				"http://www.ebay.com/soa/MyGlobalConfig", "-cn", "MyClient1",
				"-bin", binDir.getAbsolutePath() };
		// @formatter:on

		performDirectCodeGen(testArgs);

		// check file generated
		File serviceConfigDir = GeneratedAssert
				.assertFileExists(destDir,
						"gen-meta-src/META-INF/soa/services/config/ServerConfig/ServiceConfig.xml");
		Document result = CodeGenConfigUtil.parseDOMConfigFile(serviceConfigDir
				.getParent(), "ServiceConfig.xml");
		Element topLevel = result.getDocumentElement();

		NodeList headerMappingsNodes = topLevel
				.getElementsByTagName("header-mapping-options");
		if (headerMappingsNodes == null || headerMappingsNodes.getLength() == 0) {
			Assert.assertFalse("Header mapping options are not configured.",
					true);
		}

		NodeList optionsNodeList = headerMappingsNodes.item(0).getChildNodes();
		if (optionsNodeList == null || optionsNodeList.getLength() == 0) {
			Assert.assertFalse("Header mapping options are not configured.",
					true);
		}

		Map<String, String> optionsMap = new HashMap<String, String>();
		for (int i = 0; i < optionsNodeList.getLength(); i++) {
			Node headerMapping = optionsNodeList.item(i);
			if (!(headerMapping instanceof Element)) {
				continue;
			}
			String key = null;
			if (headerMapping.getAttributes().getNamedItem("name") != null) {
				key = headerMapping.getAttributes().getNamedItem("name")
						.getNodeValue();
			}
			String value = null;
			if (headerMapping.getFirstChild() != null) {
				value = headerMapping.getFirstChild().getNodeValue();
			}
			if (key != null) {
				optionsMap.put(key, value);
			}

		}

		if (!optionsMap.get(SOAHeaders.SERVICE_OPERATION_NAME).equals(
				"path[+1]")) {
			Assert.assertFalse("Wrong value configured for header option "
					+ SOAHeaders.SERVICE_OPERATION_NAME, true);
		}

		if (!optionsMap.get(SOAHeaders.RESPONSE_DATA_FORMAT).equals(
				"query[format]")) {
			Assert.assertFalse("Wrong value configured for header option "
					+ SOAHeaders.RESPONSE_DATA_FORMAT, true);
		}
		System.out
				.println("**** End createServiceConfigWithRestMappings() ****");
	}

	private int[] generateServiceAndReturnObjectFactory() throws IOException {
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File wsdl = TestResourceUtil
				.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/AdvertisingTestPluginNewServiceV1.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String testArgs[] = new String[] { "-servicename",
				"TestPluginNewService", "-wsdl", wsdl.getAbsolutePath(),
				"-genType", "All", "-pr", destDir.getAbsolutePath(), "-src",
				srcDir.getAbsolutePath(), "-dest", destDir.getAbsolutePath(),
				"-bin", binDir.getAbsolutePath() };
		// @formatter:on

		try {
			performDirectCodeGen(testArgs);
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.assertFalse("Code Gen Failed", true);
		}

		String objectFactoryFilePath = getObjectFactoryFilePath(destDir
				.getAbsolutePath());
		File objectFactoryFile = new File(objectFactoryFilePath);
		if (!objectFactoryFile.exists()) {
			Assert.assertFalse(true);
		}
		BufferedInputStream buffInputStream = null;
		try {
			FileInputStream oFFileInputStream = new FileInputStream(
					objectFactoryFile);
			buffInputStream = new BufferedInputStream(oFFileInputStream);

			int[] contents = getContentsOfStream(buffInputStream);

			return contents;
		} catch (Throwable e) {
			Assert.assertFalse(true);
			return null;
		} finally {
			IOUtils.closeQuietly(buffInputStream);
		}
	}

	private int[] getContentsOfStream(BufferedInputStream buffInputStream)
			throws Exception {
		int[] contents = new int[buffInputStream.available()];

		int ch = -1;
		int counter = 0;
		while ((ch = buffInputStream.read()) != -1) {
			contents[counter] = ch;
			counter++;
		}
		return contents;
	}

	private String getFolderPathFromImplClassName(String[] args) {
		String className = null;
		String pkgName = null;
		String folderPath = null;
		int i = 0;
		for (String arg : args) {
			if (arg.equals(InputOptions.OPT_SVC_IMPL_CLASS_NAME)) {
				className = args[i + 1];
			}
			i++;
		}
		if (className != null) {
			pkgName = className.substring(0, className.lastIndexOf("."));
			folderPath = pkgName.replace('.', '/');
		}
		return folderPath;
	}

	private String getFolderPathFromInterfacePkg(String[] args) {
		String pkgName = null;
		String folderPath = null;
		int i = 0;
		for (String arg : args) {
			if (arg.equals(InputOptions.OPT_GEN_INTERFACE_PACKAGE)) {
				pkgName = args[i + 1];
			}
			i++;
		}
		if (pkgName != null) {
			folderPath = pkgName.replace('.', '/');
		}
		return folderPath;
	}

	private String getObjectFactoryFilePath(String projectRoot) {
		return CodeGenUtil.toOSFilePath(projectRoot)
				+ CodeGenConstants.GEN_SRC_FOLDER
				+ File.separatorChar
				+ CodeGenUtil
						.getFolderPathFrompackageName("org.ebayopensource.marketplace.advertising.v1.services")
				+ "ObjectFactory.java";
	}

	public String[] getTestArgsForFactoryModeDispatacherGen1() {
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir();
		File binDir = testingdir.getFile("bin");
		File wsdl = TestResourceUtil
				.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/AdvertisingTestSOA28DevNewServiceV1.wsdl");

		String[] testArgs = new String[] {
				"-servicename",
				"NewServiceV1",
				"-wsdl",
				wsdl.getAbsolutePath(),
				"-genType",
				"All",
				"-pr",
				destDir.getAbsolutePath(),
				"-gip",
				"org.ebayopensource.marketplace.advertising.v1.services.soa28.dev.junit",
				// "-enablednamespacefolding",
				"-sicn",
				"org.ebayopensource.marketplace.advertising.v1.services.soa28.dev.junit.impl.TestSOA28DevNewServiceImpl",
				"-bin", binDir.getAbsolutePath() };
		return testArgs;
	}

	public String[] getTestArgsForFactoryModeDispatacherGen2() {
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir();
		File binDir = testingdir.getFile("bin");
		File wsdl = TestResourceUtil
				.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/AdvertisingTestSOA28DevNewServiceV1.wsdl");

		String[] testArgs = new String[] {
				"-servicename",
				"NewServiceV1",
				"-wsdl",
				wsdl.getAbsolutePath(),
				"-genType",
				"All",
				"-sicn",
				"-pr",
				destDir.getAbsolutePath(),
				"-gip",
				"org.ebayopensource.marketplace.advertising.v1.services.soa28.dev.junit",
				// "-enablednamespacefolding",
				"-bin", binDir.getAbsolutePath() };
		return testArgs;
	}

	private void performDirectCodeGen(String[] testArgs, String propValue,
			boolean remove) throws Exception {
		MavenTestingUtils.ensureEmpty(testingdir);
		updateServiceImplProjectProperties("useExternalServiceFactory",
				propValue, remove);
		performDirectCodeGen(testArgs);
	}

	private void removeServiceImplProjectProperties() {
		File destDir = getTestDestDir();
		String propsFilePath = destDir.getAbsolutePath() + File.separator
				+ "service_impl_project.properties";
		File propsFile = new File(propsFilePath);
		if (!propsFile.exists())
			return;
		try {
			CodeGenUtil.deleteFile(propsFile);
		} catch (IOException e) {
			System.out
					.println("could not delete .\\tmp\\service_intf_project.properties file");
		}
	}

	@Test
	public void testDispatcherGenerationBasedOnFactoryMode() throws Exception {

		System.out
				.println("**** Begin testDispatcherGenerationBasedOnFactoryMode() ****");
		MavenTestingUtils.ensureEmpty(testingdir);
		mavenTestingRules.setFailOnViolation(false);
		try {
			String[] testArgs1 = getTestArgsForFactoryModeDispatacherGen1();// impl
																			// name
																			// provided
			String[] testArgs2 = getTestArgsForFactoryModeDispatacherGen2();// impl
																			// name
																			// not
																			// provided
			String intfPkgFolderPath = getFolderPathFromInterfacePkg(testArgs1);
			String implPkgFolderPath = getFolderPathFromImplClassName(testArgs1);

			// factory mode is N and impl class is provided
			performDirectCodeGen(testArgs1, "false", false);

			// impl class is derived as per test args and dispatcher class is as
			// per impl class
			String implClassPath = "gen-src/service/" + implPkgFolderPath
					+ "/TestSOA28DevNewServiceImpl.java";
			String dispatcherClassPath = "gen-src/" + implPkgFolderPath
					+ "/gen/NewServiceV1RequestDispatcher.java";

			assertFilePaths(implClassPath, true, dispatcherClassPath);

			// factory mode is N and impl class is not provided
			performDirectCodeGen(testArgs2, "false", false);

			// impl class is derived as per interface and dispatcher class is as
			// per interface
			implClassPath = "gen-src/service/" + intfPkgFolderPath
					+ "/impl/NewServiceV1ImplSkeleton.java";
			dispatcherClassPath = "gen-src/" + intfPkgFolderPath
					+ "/impl/gen/NewServiceV1RequestDispatcher.java";
			assertFilePaths(implClassPath, true, dispatcherClassPath);

			// factory mode is Y and impl class is provided
			performDirectCodeGen(testArgs1, "true", false);

			// impl class is derived as per test args and dispatcher class is as
			// per interface
			implClassPath = "gen-src/service/" + implPkgFolderPath
					+ "/impl/TestSOA28DevNewServiceImpl.java";
			dispatcherClassPath = "gen-src/" + intfPkgFolderPath
					+ "/gen/NewServiceV1RequestDispatcher.java";

			// assertFilePaths(implClassPath, false, dispatcherClassPath);

			// factory mode is Y and impl class is not provided
			performDirectCodeGen(testArgs2, "true", false);

			// impl class is derived as per interface and dispatcher class is as
			// per interface
			implClassPath = "gen-src/service/" + intfPkgFolderPath
					+ "/impl/TestSOA28DevNewServiceImplSkeleton.java";
			dispatcherClassPath = "gen-src/" + intfPkgFolderPath
					+ "/gen/NewServiceV1RequestDispatcher.java";
			assertFilePaths(implClassPath, false, dispatcherClassPath);

			// factory mode is N and impl class is provided
			performDirectCodeGen(testArgs1, "false", true);

			// impl class is derived as per test args and dispatcher class is as
			// per impl class
			implClassPath = "gen-src/service/" + implPkgFolderPath
					+ "/TestSOA28DevNewServiceImpl.java";
			dispatcherClassPath = "gen-src/" + implPkgFolderPath
					+ "/gen/NewServiceV1RequestDispatcher.java";

			assertFilePaths(implClassPath, true, dispatcherClassPath);

			// factory mode is N and impl class is not provided
			performDirectCodeGen(testArgs2, "false", true);

			// impl class is derived as per interface and dispatcher class is as
			// per interface
			implClassPath = "gen-src/service/" + intfPkgFolderPath
					+ "/impl/NewServiceV1ImplSkeleton.java";
			dispatcherClassPath = "gen-src/" + intfPkgFolderPath
					+ "/impl/gen/NewServiceV1RequestDispatcher.java";
			assertFilePaths(implClassPath, true, dispatcherClassPath);
		} finally {
			removeServiceImplProjectProperties();
		}
		System.out
				.println("**** End testDispatcherGenerationBasedOnFactoryMode() ****");
	}

	/**
	 * This test, tests whether object factory and package-info gets deleted
	 * from market place library after a service is generated.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testObjectFactoryAndPackageInfoDeletion() throws Exception {

		System.out
				.println("**** Begin testObjectFactoryAndPackageInfoDeletion() ****");
		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		mavenTestingRules.setFailOnViolation(false);
		File wsdl = TestResourceUtil
				.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/Testing.wsdl");
		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String testArgs[] = new String[] { "-servicename", "MyServiceV1",
				"-wsdl", wsdl.getAbsolutePath(), "-genType", "All", "-pr",
				destDir.getAbsolutePath(), "-src", srcDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(), "-bin",
				binDir.getAbsolutePath() };
		// @formatter:on

		try {
			performDirectCodeGen(testArgs);
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.assertFalse("Code Gen Failed", true);
		}

		String projectRoot = srcDir.getAbsolutePath();
		String commonTypesPackageFolderPath = getCommonTypesPackageFolderPath(projectRoot);
		String objectFactoryFilePath = commonTypesPackageFolderPath
				+ "ObjectFactory.java";
		String packageInfoFilePath = commonTypesPackageFolderPath
				+ "package-info.java";

		System.out.println(objectFactoryFilePath);
		boolean fileExists = false;
		if (new File(objectFactoryFilePath).exists()) {
			fileExists = true;
		}
		if (new File(packageInfoFilePath).exists()) {
			fileExists = true;
		}
		Assert.assertFalse(fileExists);
		System.out
				.println("**** End testObjectFactoryAndPackageInfoDeletion() ****");
	}

	private void testServiceConfig(boolean factoryMode,
			String serviceImplFactoryClassName, String serviceImplClassName,
			File destDir) throws Exception {
		String serviceConfigLocation = ".\\tmp\\gen-meta-src\\META-INF\\soa\\services\\config\\AdvertisingTestSOA28DevNewServiceV1\\ServiceConfig.xml";
		File serviceConfigFile = new File(serviceConfigLocation);
		Assert.assertTrue(serviceConfigFile.exists());

		BufferedReader fileReader = null;
		fileReader = getFileReader(serviceConfigLocation);
		try {

			final String SERVICE_IMPL_NAME = "@@ServiceImplClassName@@";
			final String SERVICE_IMPL_FACTORY_NAME = "@@ServiceImplFactoryClassName@@";

			String contents = null;
			try {
				contents = getContents(fileReader);
			} catch (Throwable e) {
				assertFalse("Failed to parse the file", true);
			}
			final String EMPTY_SERVICE_IMPL_NAME_NODE = "<service-impl-class-name></service-impl-class-name>";
			final String EMPTY_SERVICE_IMPL_FACTORY_NAME_NODE = "<service-impl-factory-class-name></service-impl-factory-class-name>";
			if (contents.contains(SERVICE_IMPL_NAME)
					|| contents.contains(SERVICE_IMPL_FACTORY_NAME)) {
				assertFalse("Template words like '" + SERVICE_IMPL_NAME + ","
						+ SERVICE_IMPL_NAME + "' are not replaced.", true);
			}
			if (factoryMode) {
				if (!contents.contains(EMPTY_SERVICE_IMPL_NAME_NODE)
						|| contents
								.contains(EMPTY_SERVICE_IMPL_FACTORY_NAME_NODE)) {
					assertFalse(
							"The empty nodes in the commented section is not replaced properly when factoryMode="
									+ factoryMode, true);
				}
				if (!contents.contains("<service-impl-factory-class-name>"
						+ serviceImplFactoryClassName
						+ "</service-impl-factory-class-name>")) {
					assertFalse(
							"The impl factory class name is not generated in service config",
							true);
				}
			} else if (contents.contains(EMPTY_SERVICE_IMPL_NAME_NODE)
					|| !contents.contains(EMPTY_SERVICE_IMPL_FACTORY_NAME_NODE)) {
				assertFalse(
						"The empty nodes in the commented section is not replaced properly when factoryMode="
								+ factoryMode, true);
				if (!contents.contains("<service-impl-class-name>"
						+ serviceImplClassName + "</service-impl-class-name>")) {
					assertFalse(
							"The impl class name is not generated in service config",
							true);
				}
			}
		} finally {
			try {
				fileReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	@TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
	public void testServiceConfigGenerationBasedOnFactoryMode()
			throws Exception {

		File srcDir = getTestSrcDir();
		File destDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		MavenTestingUtils.ensureEmpty(testingdir);

		System.out
				.println("**** Begin testServiceConfigGenerationBasedOnFactoryMode() ****");
		String serviceImplFactoryClassName = "X.Y.Z.A.B.C";
		try {
			String[] testArgs1 = getTestArgsForFactoryModeDispatacherGen1();// impl
																			// name
																			// provided
			String[] testArgs2 = getTestArgsForFactoryModeDispatacherGen2();// impl
																			// name
																			// not
																			// provided

			try {
				updateServiceImplProjectProperties("useExternalServiceFactory",
						"false", false);
				updateServiceImplProjectProperties(
						"serviceImplFactoryClassName",
						serviceImplFactoryClassName, false);
			} catch (Throwable e) {
				Assert.assertFalse(true);
			}

			// factory mode is N and impl class is provided
			performDirectCodeGen(testArgs1);
			testServiceConfig(false, serviceImplFactoryClassName,
					testArgs1[testArgs1.length - 1], destDir);
			try {
				updateServiceImplProjectProperties("useExternalServiceFactory",
						"true", false);
				updateServiceImplProjectProperties(
						"serviceImplFactoryClassName",
						serviceImplFactoryClassName, false);
			} catch (Throwable e) {
				Assert.assertFalse(true);
			}

			// factory mode is Y
			performDirectCodeGen(testArgs2);
			testServiceConfig(true, serviceImplFactoryClassName, null, destDir);
		} finally {
			removeServiceImplProjectProperties();
		}

		System.out
				.println("**** End testServiceConfigGenerationBasedOnFactoryMode() ****");
	}

	@Test
	public void testSortingOfObjectFactory() throws Exception {
		System.out.println("**** Begin testSortingOfObjectFactory() ****");

		int noOfTestsToBeDone = 3;

		int[] compareFrom = generateServiceAndReturnObjectFactory();
		System.out
				.println("Size of Contents retrived is " + compareFrom.length);
		int counter = 1;
		while (counter < noOfTestsToBeDone) {
			int[] compareTo = generateServiceAndReturnObjectFactory();
			System.out.println("Size of Contents retrived is "
					+ compareTo.length);
			if (!(contentEquals(compareFrom, compareTo))) {
				Assert.assertFalse(true);
			}
			compareFrom = compareTo;
			counter++;
		}

		System.out.println("**** End testSortingOfObjectFactory() ****");
	}

	/**
	 * This test, tests whether TypeMappings.xml has methodName attribute.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTMGenerationForMethodNameAttribute() throws Exception {

		System.out
				.println("**** Begin testTMGenerationForMethodNameAttribute() ****");

		// Initialize testing paths
		MavenTestingUtils.ensureEmpty(testingdir);
		File wsdl = TestResourceUtil
				.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/AdvertisingTestSOA28DevNewServiceV1.wsdl");
		File destDir = getTestDestDir();
		File srcDir = getTestSrcDir();
		File binDir = testingdir.getFile("bin");

		// Setup arguments
		// @formatter:off
		String testArgs[] = new String[] { "-servicename",
				"TestPluginNewService", "-wsdl", wsdl.getAbsolutePath(),
				"-genType", "All", "-pr", destDir.getAbsolutePath(), "-src",
				srcDir.getAbsolutePath(), "-dest", destDir.getAbsolutePath(),
				"-bin", binDir.getAbsolutePath() };
		// @formatter:on

		try {
			performDirectCodeGen(testArgs);
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.assertFalse("Code Gen Failed", true);
		}

		File tmFile = GeneratedAssert.assertFileExists(destDir,
				"gen-meta-src/META-INF/soa/common/config/" + testArgs[1]
						+ "/TypeMappings.xml");

		class ParseClass extends DefaultHandler {

			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException {
				String elementName = ("".equals(localName)) ? qName : localName;

				if ("operation".equals(elementName)) {
					String methodName = attributes.getValue("methodName");
					if (methodName == null || "".equals(methodName)) {
						throw new SAXException(
								"The Attribute methodName is not defined or null");
					}

					String operationName = attributes.getValue("name");
					if ("some-Operation1".equals(operationName)
							&& !("someOperation1".equals(methodName))) {
						throw new SAXException(
								"Invalid method name generated. Expected = 'someOperation1'. Generated = '"
										+ methodName + "'");
					}

				}
			}

		}

		boolean fileGeneratedAsExpected = true;
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = parserFactory.newSAXParser();
			saxParser.parse(tmFile.getAbsolutePath(), new ParseClass());
		} catch (Throwable e) {
			fileGeneratedAsExpected = false;
		}

		assertTrue(fileGeneratedAsExpected);
		System.out
				.println("**** End testTMGenerationForMethodNameAttribute() ****");
	}

	private void updateServiceImplProjectProperties(String propertyName,
			String propertyValue, boolean remove) throws Exception {
		File destDir = getTestDestDir();
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		String propsFilePath = destDir.getAbsolutePath() + File.separator
				+ "service_impl_project.properties";

		if (!new File(propsFilePath).exists()) {
			new File(propsFilePath).createNewFile();
		}
		InputStream fis = new FileInputStream(propsFilePath);
		Properties props = new Properties();

		props.load(fis);
		fis.close();

		if (remove) {
			props.remove(propertyName);
		} else {
			props.setProperty(propertyName, propertyValue);
		}

		OutputStream fos = new FileOutputStream(propsFilePath);
		props.store(fos, "Junit test");
		fos.flush();
		fos.close();

	}
}
