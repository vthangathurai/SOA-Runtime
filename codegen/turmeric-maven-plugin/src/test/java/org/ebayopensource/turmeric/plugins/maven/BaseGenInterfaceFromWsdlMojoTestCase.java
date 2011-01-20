/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven;

import java.io.File;

import org.ebayopensource.turmeric.junit.asserts.PathAssert;

public abstract class BaseGenInterfaceFromWsdlMojoTestCase extends
		BaseTurmericMojoTestCase<GenInterfaceFromWsdlMojo> {

	@Override
	public final String getTestMojoGoal() {
		return "gen-interface-wsdl";
	}

	/**
	 * Validate that all of the expected Service Interface files were generated.
	 * 
	 * @param mojo
	 *            the mojo that hold the configuration we are interested in
	 * @param genDir
	 *            the generated-sources directory we are checking in
	 */
	public void assertGeneratedServiceFiles(GenInterfaceFromWsdlMojo mojo, File genDir) {
		File packageDir = new File(genDir, mojo.getPackageName().replace('.', File.separatorChar));
		PathAssert.assertDirExists(packageDir);

		String serviceName = mojo.getServiceName();
		
		PathAssert.assertFileExists(packageDir, (serviceName + ".java"));
		PathAssert.assertFileExists(packageDir, ("Async" + serviceName + ".java"));
		
		File packageGenDir = new File(packageDir, "gen");
		PathAssert.assertDirExists(packageGenDir);
		
		PathAssert.assertFileExists(packageGenDir, (serviceName + "Proxy.java"));
		PathAssert.assertFileExists(packageGenDir, (serviceName + "TypeDefsBuilder.java"));
	}
}
