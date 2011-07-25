package com.ebay.qajunittests.advertisinguniqueidservicev2consumer.spf;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({SchemaValidationTests.class,
	EnhancedSvcConfigRestTests.class})

public class AllTests extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		return suite;
	}

}
