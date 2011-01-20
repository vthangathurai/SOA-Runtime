/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.error.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;
import org.ebayopensource.turmeric.utils.jpa.model.Persistent;

@Entity
public class Error extends Persistent {
    @Column(unique = true)
    private long errorId;
    private String name;
    @Enumerated(EnumType.STRING)
    private ErrorCategory category;
    @Enumerated(EnumType.STRING)
    private ErrorSeverity severity;
    private String domain;
    private String subDomain;
    private String organization;

    protected Error()
    {
    }

    public Error(long errorId, String name, ErrorCategory category, ErrorSeverity severity, String domain, String subDomain, String organization) {
        this.errorId = errorId;
        this.name = name;
        this.category = category;
        this.severity = severity;
        this.domain = domain;
        this.subDomain = subDomain;
        this.organization = organization;
    }

    public long getErrorId()
    {
        return errorId;
    }

    public String getName()
    {
        return name;
    }

    public ErrorCategory getCategory()
    {
        return category;
    }

    public ErrorSeverity getSeverity()
    {
        return severity;
    }

    public String getDomain()
    {
        return domain;
    }

    public String getSubDomain()
    {
        return subDomain;
    }

    public String getOrganization()
    {
        return organization;
    }
}
