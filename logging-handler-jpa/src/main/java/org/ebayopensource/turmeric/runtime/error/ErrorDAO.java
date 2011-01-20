/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.error;

import java.util.List;

import org.ebayopensource.turmeric.runtime.error.model.ErrorValue;

public interface ErrorDAO {
    org.ebayopensource.turmeric.runtime.error.model.Error persistErrorIfAbsent(org.ebayopensource.turmeric.runtime.error.model.Error error);

    void persistErrorValues(List<ErrorValue> errorValues);

    org.ebayopensource.turmeric.runtime.error.model.Error findErrorByErrorId(long id);
}
