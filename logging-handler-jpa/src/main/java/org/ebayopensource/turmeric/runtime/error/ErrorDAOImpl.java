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
import org.ebayopensource.turmeric.utils.jpa.AbstractDAO;

public class ErrorDAOImpl extends AbstractDAO implements ErrorDAO {
    @Override
    public org.ebayopensource.turmeric.runtime.error.model.Error persistErrorIfAbsent(org.ebayopensource.turmeric.runtime.error.model.Error error) {
        org.ebayopensource.turmeric.runtime.error.model.Error result = findErrorByErrorId(error.getErrorId());
        if (result == null) {
            try
            {
                persistEntity(error);
            }
            catch (RuntimeException x)
            {
                // Concurrent insert failed, re-read the error
                result = findErrorByErrorId(error.getErrorId());
                if (result == null)
                    throw x;
            }
        }
        return result;
    }

    @Override
    public void persistErrorValues(List<ErrorValue> errorValues) {
        persistEntities(errorValues);
    }

    @Override
    public org.ebayopensource.turmeric.runtime.error.model.Error findErrorByErrorId(long errorId)
    {
        return getSingleResultOrNull(org.ebayopensource.turmeric.runtime.error.model.Error.class, "errorId", errorId);
    }
}
