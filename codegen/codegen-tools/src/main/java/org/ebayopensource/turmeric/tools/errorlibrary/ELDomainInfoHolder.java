/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.errorlibrary;

import org.ebayopensource.turmeric.common.config.ErrorBundle;

public class ELDomainInfoHolder {

	private ErrorBundle m_errorBundle;
	
	private String m_packageName;

	public ErrorBundle getErrorBundle() {
		return m_errorBundle;
	}

	public void setErrorBundle(ErrorBundle bundle) {
		m_errorBundle = bundle;
	}

	public String getPackageName() {
		return m_packageName;
	}

	public void setPackageName(String name) {
		m_packageName = name;
	}
	
}
