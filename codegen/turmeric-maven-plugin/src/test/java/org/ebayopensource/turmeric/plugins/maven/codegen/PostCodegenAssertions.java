/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.codegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.interpolation.EnvarBasedValueSource;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.codehaus.plexus.util.StringUtils;
import org.ebayopensource.turmeric.plugins.maven.AbstractTurmericCodegenMojo;
import org.junit.Assert;

/**
 * Utility to verify the results of a codegen test case.
 */
public class PostCodegenAssertions {
	private static final boolean DEBUG = false;
	private static final String ASSERTION_RULES = "assertions.txt";
	
	/**
	 * Parse Mode: what is expected.
	 */
	enum ParseMode {
		/* Looking for Rule Argument */
		RULE, 
		/* Looking for Rule Check Line (or new Rule Argument) */
		CHECK 
	};

	private AbstractTurmericCodegenMojo mojo;
	private MavenProject project;
	private Map<String, Class<? extends Rule>> availableRules = new HashMap<String, Class<? extends Rule>>();
	private List<Rule> rules = new ArrayList<Rule>();
	private Interpolator interpolator;

	public PostCodegenAssertions(AbstractTurmericCodegenMojo mojo)
			throws IOException, InterpolationException {
		this.mojo = mojo;
		this.project = mojo.getProject();

		addAvailableRule(ClassMustExistRule.class);
		addAvailableRule(ClassMustNotExistRule.class);
		addAvailableRule(PathMustNotExistRule.class);
		addAvailableRule(PathMustExistRule.class);
		
		initInterpolator();

		File assertionRules = new File(this.project.getBasedir(), ASSERTION_RULES);
		loadAssertions(assertionRules);
	}

	private void initInterpolator() throws IOException {
		interpolator = new RegexBasedInterpolator();
		interpolator.addValueSource(new PrefixedObjectValueSource("project", project));
		interpolator.addValueSource(new PrefixedObjectValueSource("mojo", mojo));
		interpolator.addValueSource(new PropertiesBasedValueSource(System.getProperties()));
		interpolator.addValueSource(new EnvarBasedValueSource());
	}

	private final void loadAssertions(File rulesFile) throws IOException, InterpolationException {
		
		FileReader reader = null;
		BufferedReader buf = null;

		try {
			reader = new FileReader(rulesFile);
			buf = new BufferedReader(reader);

			String line;
			int linenum = 0;
			ParseMode mode = ParseMode.RULE;
			Rule activeRule = null;

			while ((line = buf.readLine()) != null) {
				linenum ++;
				
				if(StringUtils.isEmpty(line)) {
					// Nothing here. skip
					continue;
				}
				
				line = line.trim();
				
				if(line.charAt(0)== '#') {
					// A comment. skip
					continue;
				}
				
				if(line.charAt(0) == '|') {
					String parts[] = StringUtils.split(line, "|");
					String ruleName = parts[0];
					activeRule = createRule(ruleName);
					activeRule.setInterpolator(this.interpolator);
					String args[] = new String[parts.length-1];
					System.arraycopy(parts, 1, args, 0, args.length);
					activeRule.setArguments(args);
					debug("Created Rule: %s", activeRule);
					rules.add(activeRule);
					
					mode = ParseMode.CHECK; // expect CHECK lines now
					continue;
				}
				
				if(mode == ParseMode.CHECK) {
					debug("Adding Check: %s", line);
					activeRule.addCheck(line);
					continue;
				}
				
				throw new IOException(
						"Parse Failure, unexpected content at line #" + linenum
								+ " (expecting " + mode + "): " + line);
			}
		} finally {
			IOUtils.closeQuietly(buf);
			IOUtils.closeQuietly(reader);
		}
	}

	private Rule createRule(String ruleName) {
		Class<? extends Rule> clazz = availableRules.get(ruleName);
		if(clazz == null){ 
			throw new IllegalStateException("No such Rule available: " + ruleName);
		}
		
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalStateException("Unable to instantiate new Rule: " + clazz, e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Unable to access new Rule: " + clazz, e);
		}
	}

	private void debug(String format, Object ... args) {
		if(DEBUG) {
			System.out.println("[PostCodegenAssertions] " + String.format(format, args));
		}
	}

	public final void addAvailableRule(Class<? extends Rule> ruleClass) {
		String name = ruleClass.getSimpleName();
		availableRules.put(name, ruleClass);
	}

	public void assertGenerated() {
		List<String> failures = new ArrayList<String>();
		
		for(Rule rule: rules) {
			failures.addAll(rule.verify());
		}
		
		if(failures.size() > 0) {
			StringBuilder err = new StringBuilder();
			err.append("ASSERTION FAILURES: Post CodeGen:");
			
			for(String failure: failures) {
				err.append("\n  ").append(failure);
			}
			
			System.out.println("--------------------------------------------------");
			System.out.println(err.toString());
			System.out.println("--------------------------------------------------");
			Assert.fail(err.toString());
		} else {
			System.out.println("[PostCodegenAssertions] PASSED");
		}
	}
}
