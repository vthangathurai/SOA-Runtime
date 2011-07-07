/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common;

import java.io.File;
import java.net.URL;

import org.ebayopensource.turmeric.junit.asserts.ClassLoaderAssert;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.junit.BeforeClass;

/**
 * Top level test case for all test within Turmeric.
 */
public abstract class AbstractTurmericTestCase extends org.ebayopensource.turmeric.junit.AbstractTurmericTestCase {
	/** 
	 * A visual clue that the transport should be LOCAL, and not using
	 * the embedded jetty server.
	 */
	protected static final URL LOCAL_TRANSPORT = null;

	/**
	 * Quick sanity checks for various resources that should be
	 * present in the classpath/classloader.
	 * <p>
	 * This only exists to sanity check the Eclipse/m2eclipse state,
	 * as developers have been bit by unprocessed resources due
	 * to m2eclipse bugs.
	 * <p>
	 * Keep in mind that you can create a launcher to correct this.
	 * <p>
	 * Project &gt; Run As &gt; Maven Build ...<br>
	 * Base Directory: <code>${workspace_loc:/SOATests}/../</code><br>
	 * Goals: <code>process-test-resources</code>
	 */
	@BeforeClass
	public static void ensureRequiredConfigPresent() {
		ClassLoaderAssert.assertResourcePresent("Required Configuration",
				"META-INF/soa/client/config/GlobalClientConfig.xml");
	}
	
	/**
	 * Configure a few ukernel paths and properties so that it behaves
	 * properly while under testing conditions.
	 */
	@BeforeClass
	public static void configureUKernel() {
		File ukernel = MavenTestingUtils.getTargetFile("ukernel");
		MavenTestingUtils.ensureDirExists(ukernel);
		// Set the output directory for persist files.
		System.setProperty("PERSIST_CONFIG_BASE_DIR", ukernel.getAbsolutePath());
		// Setup old config path search
		System.setProperty("com.ebay.kernel.context.externalConfigRootUrl", ukernel.getAbsolutePath());
		
		System.setProperty("org.ebayopensource.turmeric.log.dir", ukernel.getAbsolutePath());
		
		File targetDir = MavenTestingUtils.getTargetDir();
		System.setProperty("org.ebayopensource.payload.dump.dir", targetDir.getAbsolutePath());
	}
}
