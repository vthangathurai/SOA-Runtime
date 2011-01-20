/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

public class CommentDetector {

	public final static String COMMENT_DETECTOR_REGEX = getCommentDetectorRegex(); // "((//)|(/\\*)|(/\\*\\*)|(\\*)|(\\*/)).*"

	private static String getCommentDetectorRegex() {
		StringBuilder regex = new StringBuilder();
		final String ASTERISK = "\\*";
		final char OR = '|';
		final String DOC_COMM_STARTER = "/" + ASTERISK + ASTERISK;
		final String SING_LINE_COMM_STARTER = "/" + "/";
		final String COMM_STARTER = "/" + ASTERISK;
		final String COMM_END = ASTERISK + "/";
		final String ANY = ".*";
		regex.append('(').append(ASTERISK).append(OR).append(DOC_COMM_STARTER).append(OR)
				.append(COMM_STARTER).append(OR).append(COMM_END).append(OR)
				.append(SING_LINE_COMM_STARTER).append(')').append(ANY);
		return regex.toString();
	}
	
}
