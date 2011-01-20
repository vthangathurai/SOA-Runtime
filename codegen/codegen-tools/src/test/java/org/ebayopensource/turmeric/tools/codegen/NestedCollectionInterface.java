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
package org.ebayopensource.turmeric.tools.codegen;

import java.util.HashMap;
import java.util.List;

/**
 * @author rpallikonda
 *
 */
public interface NestedCollectionInterface {
	
	public List<HashMap<String, String>> getList( List<HashMap<String, String>> arg1);
	
	// public List<HashMap<String, Integer>> getList(String arg1);

}
