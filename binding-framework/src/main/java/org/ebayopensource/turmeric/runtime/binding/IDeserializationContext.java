/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding;


/**
 * The context object used for deserialization.  Contains information needed for
 * deserialize the payload.
 * 
 * @author wdeng
 *
 */
public interface IDeserializationContext extends ISerializationContext { 
	/**
	 * This method defines whether the incoming NV request have the element listed in alphabet ascending order.
	 * This method is only appliable to NV format.
	 * 
	 * @return true if element order is preserved.  
	 */
	public boolean isElementOrderPreserved();
}
