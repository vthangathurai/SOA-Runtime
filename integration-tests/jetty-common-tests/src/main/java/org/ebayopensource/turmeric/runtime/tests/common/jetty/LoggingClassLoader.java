/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.jetty;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingClassLoader extends ClassLoader {
	private Logger logger;
	
	public LoggingClassLoader(Logger logger, ClassLoader parent) {
		super(parent);
		this.logger = logger;
	}
	
	@Override
	public URL getResource(String name) {
		URL url = super.getResource(name);
		
		Level lvl = Level.INFO;
		if (url == null) {
			lvl = Level.WARNING;
		}
		logger.log(lvl, String.format("### Resource : %s -> %s", name, url));

		return url;
	}
	
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		logger.info(String.format("### Resource(s): %s", name ));
		return super.getResources(name);
	}
}
