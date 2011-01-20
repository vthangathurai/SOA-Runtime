/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.StringUtils;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;


/**
 * Simple command line generation and validation class.
 */
public class CodegenCommands {
	private Log log;
	private Map<String, String> commands = new HashMap<String, String>();

	public void add(String key, String value) throws MojoFailureException {
		if (StringUtils.isBlank(key)) {
			throw new MojoFailureException(
					"Unable to set empty Codegen command line argument (key)");
		}
		if (StringUtils.isBlank(value)) {
			throw new MojoFailureException(
					"Unable to set empty Codegen command line argument (value)");
		}

		commands.put(key, value);
	}

	public void addClassesOutputDir(String dir) throws MojoFailureException {
		addKnownParameter(TurmericMavenConstants.PARAM_BIN, dir);
	}

	public void addFreeformArgs(String[] args) throws MojoFailureException {
		if (args == null) {
			return;
		}

		// List of forbidden Key Value arguments.
		List<String> forbiddenKV = new ArrayList<String>();
		forbiddenKV.add(InputOptions.OPT_PROJECT_ROOT);
		forbiddenKV.add(InputOptions.OPT_SRC_DIR);
		forbiddenKV.add(InputOptions.OPT_DEST_DIR);
		forbiddenKV.add(InputOptions.OPT_JAVA_SRC_GEN_DIR);
		forbiddenKV.add(InputOptions.OPT_META_SRC_GEN_DIR);
		forbiddenKV.add(InputOptions.OPT_BIN_DIR);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg == null) {
				continue; // skip arg
			}

			if (forbiddenKV.contains(arg)) {
				log.error("Forbidden arg ["
						+ arg
						+ "] found in turmeric-maven-plugin configuration. "
						+ "This indicates that you should use the Maven Plugin's own "
						+ "configuration and non-arg parameters to configure this option.");
				i++;
				continue;
			}

			addSingle(arg);
		}
	}

	public void addGeneratedJavaOutputDir(File dir) throws MojoFailureException {
		addGeneratedJavaOutputDir(dir.getAbsolutePath());
	}

	public void addGeneratedJavaOutputDir(String dir)
			throws MojoFailureException {
		addKnownParameter(InputOptions.OPT_JAVA_SRC_GEN_DIR, dir);
	}

	public void addGeneratedResourcesOutputDir(File dir)
			throws MojoFailureException {
		addGeneratedResourcesOutputDir(dir.getAbsolutePath());
	}

	public void addGeneratedResourcesOutputDir(String dir)
			throws MojoFailureException {
		addKnownParameter(InputOptions.OPT_META_SRC_GEN_DIR, dir);
	}

	public void addGenType(String genType) throws MojoFailureException {
		addKnownParameter(InputOptions.OPT_CODE_GEN_TYPE, genType);
	}

	private void addKnownParameter(String knownKey, String value)
			throws MojoFailureException {
		if (StringUtils.isBlank(value)) {
			throw new MojoFailureException(
					"Unable to set empty Codegen command line argument for "
							+ knownKey);
		}
		commands.put(knownKey, value);
	}

	public void addSingle(String key) throws MojoFailureException {
		if (StringUtils.isBlank(key)) {
			throw new MojoFailureException(
					"Unable to set empty Codegen command line argument (key w/no value)");
		}

		commands.put(key, null);
	}

	public void addSourceDir(String sourceDirectory)
			throws MojoFailureException {
		addKnownParameter(InputOptions.OPT_SRC_DIR, sourceDirectory);
	}

    public void addResourceDir(String directory) throws MojoFailureException {
        addKnownParameter(InputOptions.OPT_META_SRC_DIR, directory);
    }

    public String[] getCommandArray() {
		String arr[] = new String[commands.size() * 2];
		String val;
		int i = 0;
		for (Entry<String, String> entry : commands.entrySet()) {
			arr[i++] = entry.getKey();
			val = entry.getValue();
			if(val != null) {
				arr[i++] = entry.getValue();
			}
		}
		
		// We encountered a command with no value.
		if (arr.length > i) {
			String shortened[] = new String[i];
			System.arraycopy(arr, 0, shortened, 0, i);
			return shortened;
		}
		
		return arr;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public void setServiceName(String serviceName) throws MojoFailureException {
		addKnownParameter(InputOptions.OPT_SRVC_NAME, serviceName);
	}

    public void setAdminName(String adminName) throws MojoFailureException {
        addKnownParameter(InputOptions.OPT_ADMIN_NAME, adminName);
    }

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		for (Entry<String, String> entry : commands.entrySet()) {
			buf.append("\n   \"").append(entry.getKey()).append("\"");
			if (entry.getValue() == null) {
				buf.append(", <null>");
			} else {
				buf.append(", \"").append(entry.getValue()).append("\"");
			}
		}
		return buf.toString();
	}

	public void removeOptionPair(String optionKey) {
		commands.remove(optionKey);
	}

}
