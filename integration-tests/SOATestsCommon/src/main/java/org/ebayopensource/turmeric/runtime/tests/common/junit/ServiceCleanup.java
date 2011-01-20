/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.junit;

import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.tests.common.sif.error.MarkdownTestHelper;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class ServiceCleanup implements MethodRule {
    private static final Logger LOG = Logger.getLogger("test.servicecleanup");
    private String serviceName;
    private String clientName;
    private String operationName;

    public ServiceCleanup(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getClientName() {
        return clientName;
    }

    public ServiceCleanup setClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    public String getOperationName() {
        return operationName;
    }

    public ServiceCleanup setOperationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    @Override
    public Statement apply(final Statement statement, final FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                }
                finally {
                    LOG.info(String.format("Cleaning up after service [%s] testing", serviceName));
                    MarkdownTestHelper.markupClientManually(serviceName, clientName, operationName);
                }
            }
        };
    }
}
