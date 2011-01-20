/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.sif.service;

/**
 * @author aupadhay
 * This interface is implemented by hosting environemnt
 * to specify the deployed environment.
 *
 */
public interface EnvironmentMapper 
{
	/**
	 * @return The current deployed environment, for example, Production,
	 *     Testing, or Development.
	 */
	String getDeploymentEnvironment();

}
