/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class XmlJavaTypeAdapterBean
    implements XmlJavaTypeAdapter
{
    private final Class m_value;
    private final Class m_type;

    public XmlJavaTypeAdapterBean(Class value, Class type) {
        m_value = value;
        m_type = type;
    }

    public Class<XmlJavaTypeAdapter> annotationType() {
        return XmlJavaTypeAdapter.class;
    }

	@Override
    public boolean equals(Object that) {
        if (!(that instanceof XmlJavaTypeAdapter)) {
            return false;
        }
        XmlJavaTypeAdapterBean other = (XmlJavaTypeAdapterBean)that;
        if (!m_value.equals(other.m_value)) {
            return false;
        }
        if (!m_type.equals(other.m_type)) {
            return false;
        }
        return true;
    }

	@Override
    public int hashCode() {
        int r = 0;
        r = (r^m_value.hashCode());
        r = (r^m_type.hashCode());
        return r;
    }

    public Class value() {
        return m_value;
    }

    public Class type() {
        return m_type;
    }

}
