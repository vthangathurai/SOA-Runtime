/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.tester;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

class GenericAsyncHandler<T> implements AsyncHandler<T> {
    private volatile Response<T> m_resp;

    private volatile Exception m_error;

    private volatile T m_message;

    private volatile boolean m_done = false;

    public void handleResponse(Response<T> resp) {
        try {
            m_resp = resp;
            m_message = m_resp.get();
        }
        catch (Exception e) {
            m_error = e;
        }
        finally {
            m_done = true;
        }
    }

    public T get() {
        return m_message;
    }

    public Exception getError() {
        return m_error;
    }

    public boolean isDone() {
        return m_done;
    }

    public boolean hasError() {
        return m_error != null;
    }

    public Response<T> getResponse() {
        return m_resp;
    }
}