/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.logging;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

public class SimpleConsoleHandler extends Handler {
	public static class Init {
		public Init() {
			URL url = this.getClass().getResource("test-logging.properties");
			if (url == null) {
				configureDefaults();
			} else {
				// Configurable Setup
				InputStream in = null;
				try {
					in = url.openStream();
					LogManager.getLogManager().readConfiguration(in);
				} catch (IOException e) {
					e.printStackTrace(System.err);
					configureDefaults();
				} finally {
					IOUtils.closeQuietly(in);
				}
			}
		}

		public void configureDefaults() {
			// Default Setup
			Logger root = Logger.getLogger("");

			boolean ourHandlerExists = false;
			Handler handlers[] = root.getHandlers();
			for (Handler handler : handlers) {
				if (handler instanceof SimpleConsoleHandler) {
					ourHandlerExists = true;
					continue;
				}
				if (handler instanceof ConsoleHandler) {
					System.out.println("Removing default logging handler: "
							+ handler);
					root.removeHandler(handler);
					continue;
				}

				System.out.println("Other handler present: " + handler);
			}

			if (!ourHandlerExists) {
				SimpleConsoleHandler logger = new SimpleConsoleHandler();
				System.out.println("Adding our logging handler: " + logger);
				root.addHandler(logger);
			}
		}
	}

	public static void init() {
		System.setProperty("java.util.logging.config.class",
				SimpleConsoleHandler.Init.class.getName());
	}

	@Override
	public void close() throws SecurityException {
		/* nothing to do here */
	}

	@Override
	public void flush() {
		/* nothing to do here */
	}

	@Override
	public void publish(LogRecord record) {
		StringBuilder buf = new StringBuilder();
		buf.append("[").append(record.getLevel().getName());
		buf.append("] ").append(record.getLoggerName());
		buf.append(" (").append(record.getSourceMethodName());
		buf.append("): ").append(record.getMessage());

		System.out.println(buf.toString());
		if (record.getThrown() != null) {
			record.getThrown().printStackTrace(System.out);
		}
	}

}
