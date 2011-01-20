/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.schema;

/**
 * The Payload schema validation level enum.
 * 
 * @author wdeng
 *
 */
public enum PayloadSchemaValidationLevelConfig {
    /**
     * Schema validation is disabled.
     */
    DISABLED,
    /**
     * Schema validation is not performed.
     */
    NONE,
    /**
     * Perform relaxed schema validation.
     */
    RELAX,
    /**
     * Perform strict schema validation.
     */
    STRICT;
}
