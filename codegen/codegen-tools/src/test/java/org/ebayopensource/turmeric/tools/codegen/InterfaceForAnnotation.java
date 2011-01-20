/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

/**
 * comments for the Interface at the Interface level
 * @author arajmony
 *
 */
public interface InterfaceForAnnotation {

	/**
	 * This method calculates the interest for the given principal amount and period at the specified rate of interest.
	 * @param period  period for which the interest has to be calculated
	 * @param rate    rate of interest
	 * @param principal the prinicpal amount
	 * @return The interest amount in USD
	 */
public double calculateInterest(int period,float rate,float principal);

}
