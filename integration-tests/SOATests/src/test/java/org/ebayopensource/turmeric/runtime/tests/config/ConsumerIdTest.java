/**
 * 
 */
package org.ebayopensource.turmeric.runtime.tests.config;

/**
 * @author rpallikonda
 * 
 */
public class ConsumerIdTest {

	@Ignore
	// Ignored because of the removal of the getConsumerID from
	// ClientServerContext
	@Test
	public void consumerIdTest_ConsumerIdAppNameMismatch() throws Exception {
		Test1Driver driver = createDriver("configtest5", "consumerid2",
				"consumerid2");
		driver.setTransportHeader(SOAHeaders.AUTH_APPNAME, "appName");
		driver.setExpectedError(
				SystemErrorTypes.SVC_RT_CONSUMERID_SECURITY_APPNAME_MISMATCH
						.getId(), ServiceInvocationException.class,
				ServiceInvocationRuntimeException.class,
				"ConsumerId testConsumerId does not match ");
		driver.doCall();
	}

	@Test
	public void consumerIdTest_ConsumerIdUseCaseMismatch() throws Exception {
		Test1Driver driver = createDriver("configtest5", "consumerid5",
				"consumerid5");
		driver.setTransportHeader(SOAHeaders.USECASE_NAME, "useCase");
		driver.setTransportHeader(SOAHeaders.CONSUMER_ID, "consumerId");
		driver.doCall();
	}

	@Test
	public void consumerIdTest_Exception() throws Exception {
		Test1Driver driver = createDriver("configtest5", "consumerid3",
				"consumerid3");
		driver.setExpectedError(SystemErrorTypes.SVC_CLIENT_MISSING_CONSUMER_ID
				.getId(), ServiceInvocationException.class,
				ServiceInvocationRuntimeException.class,
				"No consumer-id present in");
		driver.doCall();
	}

	@Test
	public void consumerIdTest_ProviderSuccess() throws Exception {
		Test1Driver driver = createDriver("configtest5", "consumerid4",
				"consumerid4");
		driver.doCall();
	}

	@Ignore
	// Ignored because of the removal of the getConsumerID from
	// ClientServerContext
	@Test
	public void consumerIdTest_Success() throws Exception {
		Test1Driver driver = createDriver("configtest5", "consumerid2",
				"consumerid2");
		driver.doCall();
	}

	protected Test1Driver createDriver(String config) throws Exception {
		return createDriver(config, Test1Driver.TEST1_ADMIN_NAME);
	}

	protected Test1Driver createDriver(String config, String clientName)
			throws Exception {
		Test1Driver driver = new Test1Driver(Test1Driver.TEST1_ADMIN_NAME,
				clientName, config, null);
		setupDriver(driver);
		return driver;
	}

	protected Test1Driver createDriver(String config, String adminName,
			String clientName) throws Exception {
		Test1Driver driver = new Test1Driver(adminName, clientName, config,
				null);
		setupDriver(driver);
		return driver;
	}

	protected void setupDriver(Test1Driver driver) {
		driver.setSkipSerialization(true);
	}

	@After
	public void tearDown() throws Exception {
		ServiceConfigManager.getInstance().setConfigTestCase("config");
	}
}
