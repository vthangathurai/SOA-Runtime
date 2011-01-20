/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.monitoring;

import java.util.Locale;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.MetricsRegistrationHelper;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;


public class TestErrorDataContentProvider implements ErrorDataProvider {

	public CommonErrorData getCommonErrorData(ErrorDataKey arg0, Object[] arg1, Locale arg2) {
		return null;
	}

	public <T extends CommonErrorData> T getCustomErrorData(ErrorDataKey arg0, Object[] arg1, Class<T> arg2, Locale arg3) {
		return null;
	}

	@SuppressWarnings("deprecation")
	public CommonErrorData getErrorData(ErrorDataKey key, Object[] arg1, Locale arg2) {
		CommonErrorData errorData = new CommonErrorData();
		if (key.getId() == 1) {
			errorData.setDomain("SOA");
			errorData.setErrorId(1000L);
			errorData.setMessage("BLAH");
			errorData.setSeverity(ErrorSeverity.ERROR);
			errorData.setSubdomain("TESTING");
			
			MetricsRegistrationHelper.registerMetricsForErrorDataGrps(errorData, "MPLACE", "errtestgrp1 errtestgrp2");
		} else {
			errorData.setDomain("SOA");
			errorData.setErrorId(2000L);
			errorData.setMessage("BLAH");
			errorData.setSeverity(ErrorSeverity.ERROR);
			errorData.setSubdomain("TESTING");
			
			MetricsRegistrationHelper.registerMetricsForErrorDataGrps(errorData, "MPLACE", "errtestgrp2 errtestgrp3");
		}
		return errorData;
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

}
