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
package org.ebayopensource.turmeric.runtime.tests.sample.services.item;

import java.util.HashMap;
import java.util.HashSet;

import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.ItemType;

/**
 * @author wdeng
 * 
 */
public class ItemServiceImpl implements ItemService {

    @Override
    public HashMap<String, ItemType> findItemMap(String keyword) {
        return null;
    }

    @Override
    public HashSet<ItemType> findItemSet(String keyword) {
        return null;
    }

}
