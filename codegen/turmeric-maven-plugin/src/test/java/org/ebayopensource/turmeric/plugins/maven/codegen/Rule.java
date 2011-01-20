/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.codegen;

import java.util.List;

import org.codehaus.plexus.interpolation.Interpolator;

/**
 * Basic Assertion Rule.
 */
public interface Rule {
	/**
	 * Values after the rule name, splie by "|" symbols by {@link PostCodegenAssertions} class and handed off to Rule as
	 * separate arguments.
	 * 
	 * @param args
	 *            the arguments
	 */
	public void setArguments(String... args);

	/**
	 * Adds a single line of what to check on the rule.
	 * 
	 * @param line
	 *            the check line.
	 */
	public void addCheck(String line);

	/**
	 * Perform the assertion against the rules.
	 * 
	 * @return the list of failure reasons (if any), empty list if no failures.
	 */
	public List<String> verify();

	/**
	 * Interpolator that the rule can use to expand expressions.
	 * 
	 * @param interpolator
	 *            to use for any expressions.
	 */
	public void setInterpolator(Interpolator interpolator);
}
