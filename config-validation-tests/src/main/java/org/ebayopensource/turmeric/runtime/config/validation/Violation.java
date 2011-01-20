/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.config.validation;

import java.io.File;

public class Violation {
	private File file;
	private String context;
	private String msg;

	public Violation(File file, String context, String msg) {
		super();
		this.file = file;
		this.context = context;
		this.msg = msg;
	}

	public File getFile() {
		return file;
	}

	public String getContext() {
		return context;
	}

	public String getMsg() {
		return msg;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Violation ");
		builder.append(file).append(" | ");
		builder.append(context).append(" | ");
		builder.append(msg);
		return builder.toString();
	}
}