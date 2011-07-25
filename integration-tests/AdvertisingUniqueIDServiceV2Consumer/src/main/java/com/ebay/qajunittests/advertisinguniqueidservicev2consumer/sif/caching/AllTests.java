package com.ebay.qajunittests.advertisinguniqueidservicev2consumer.sif.caching;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	CacheFunctionalTests.class,
//	CachingCompatibilityPre24Service.class,
	CacheLocalModeNoDisableTag.class,
	DisableCacheOnLocalFalse.class,
	DisableCacheOnLocalTrue.class,
	GetCahcePolicyRESTTests.class,
	CachingOverRawMode.class,
	CacheUnitNegative_SchemaValidation.class,
	CacheUnitNegative.class,CacheUnitPositive.class
	})
public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Tests for com.ebay.qajunittests.cachefunctional ");
		return suite;
	}
}
