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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.handler.UserResponseHandler;
import org.ebayopensource.turmeric.tools.codegen.util.JavaToolsClassLoader;

/**
 * Vastly simplified codegen executor (for maven plugins and unit testing)
 */
public class NonInteractiveCodeGen implements UserResponseHandler {
	public List<URL> extraClassPath = new ArrayList<URL>();

	public List<URL> getExtraClassPath() {
		return extraClassPath;
	}

	public void setExtraClassPath(List<URL> extraClassPath) {
		this.extraClassPath = extraClassPath;
	}
	
	public void addExtraClassPath(File path) throws MalformedURLException {
		if(!path.exists()) {
			return; // do not add if it doesn't exist.
		}
		URL url = path.toURI().toURL();
		if(this.extraClassPath.contains(url)) {
			return; // do not add a second copy.
		}
		this.extraClassPath.add(url);
	}
	
	public void addJavaToolsClassPath() throws CodeGenFailedException, MalformedURLException {
		File toolsJar = JavaToolsClassLoader.findToolsJar();
		if(toolsJar != null) {
			addExtraClassPath(toolsJar);
		}
	}

	public void execute(String args[]) throws Exception {
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		URL urls[] = this.extraClassPath.toArray(new URL[0]);
		URLClassLoader cl = new URLClassLoader(urls, original);
		try {
			Thread.currentThread().setContextClassLoader(cl);
			ServiceCodeGenBuilder codegen = new ServiceCodeGenBuilder();
			codegen.build(args, this);
		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}

	@Override
	public boolean getBooleanResponse(String promptMsg) {
		return true;
	}

}
