/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.monitoring;

import org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring.BaseMonitoringComponentStatus;

/**
 * Client-side SOA monitoring Component Status page
 * 
 * @author ichernyshev
 */
public final class ServerServiceMonitoringCompStatus extends BaseMonitoringComponentStatus {
	private static boolean s_isCompStatusInitialized;

	private ServerServiceMonitoringCompStatus() {
		super("TurmericServerMonitoring", false, "Server");
	}

	public static synchronized void initializeCompStatus() {
		if (!s_isCompStatusInitialized) {
			s_isCompStatusInitialized = true;
			initializeCompStatus(new ServerServiceMonitoringCompStatus());
		}
	}
}
