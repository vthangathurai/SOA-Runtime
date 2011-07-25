package com.ebay.qajunittests.advertisinguniqueidservicev2consumer;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({
	com.ebay.qajunittests.advertisinguniqueidservicev2consumer.sif.caching.AllTests.class,
	com.ebay.qajunittests.advertisinguniqueidservicev2consumer.spf.AllTests.class
	
})

public class AllTests extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		return suite;
	}

}
