/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.error.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.ebayopensource.turmeric.utils.jpa.model.Persistent;

@Entity
public class ErrorValue extends Persistent {
    @ManyToOne
    private org.ebayopensource.turmeric.runtime.error.model.Error error;
    private String errorMessage;
    private String serviceAdminName;
    private String operationName;
    private String consumerName;
    private long timeStamp;
    private boolean serverSide;
    private int aggregationPeriod;

    protected ErrorValue()
    {
    }

    public ErrorValue(org.ebayopensource.turmeric.runtime.error.model.Error error, String errorMessage, String serviceAdminName, String operationName, String consumerName, long timeStamp, boolean serverSide, int aggregationPeriod) {
        this.error = error;
        this.errorMessage = errorMessage;
        this.serviceAdminName = serviceAdminName;
        this.operationName = operationName;
        this.consumerName = consumerName;
        this.timeStamp = timeStamp;
        this.serverSide = serverSide;
        this.aggregationPeriod = aggregationPeriod;
    }

    public org.ebayopensource.turmeric.runtime.error.model.Error getError()
    {
        return error;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public String getServiceAdminName()
    {
        return serviceAdminName;
    }

    public String getOperationName()
    {
        return operationName;
    }

    public String getConsumerName()
    {
        return consumerName;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public boolean isServerSide()
    {
        return serverSide;
    }

    public int getAggregationPeriod()
    {
        return aggregationPeriod;
    }
}
