/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring;
 
/**
 * This interface provides the ability to reset the current metrics.
 *
 */
public interface DiffBasedMetricsStorageProvider {
	
	/**
	 * Resets all the metrics for the service of the given admin name.
	 * 
	 * @param adminName The admin name of a service.
	 */
	public void resetPreviousSnapshot(String adminName);
}
