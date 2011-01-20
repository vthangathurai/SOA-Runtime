/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;



/**
 * <p>Java class for PresenceConfig.
 * 
 * <p>
 * 
 */
public enum PresenceConfig {

    MANDATORY("Mandatory"),
    REPLACEABLE("Replaceable"),
    REMOVED("Removed");
    private final String value;

    PresenceConfig(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PresenceConfig fromValue(String v) {
        for (PresenceConfig c: PresenceConfig.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
