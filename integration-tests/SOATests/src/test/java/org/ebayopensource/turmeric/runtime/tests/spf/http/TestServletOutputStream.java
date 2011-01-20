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
package org.ebayopensource.turmeric.runtime.tests.spf.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * @author wdeng
 * 
 */
public class TestServletOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream m_os;

    public TestServletOutputStream() {
        m_os = new ByteArrayOutputStream();
    }

    @Override
    public void write(int b) throws IOException {
        m_os.write(b);
    }

    @Override
    public String toString() {
        return m_os.toString();
    }
}
