/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.config;

import static org.junit.Assert.*;

import java.util.Collection;

import org.ebayopensource.turmeric.runtime.common.g11n.GlobalIdEntry;
import org.ebayopensource.turmeric.runtime.common.impl.internal.g11n.GlobalIdEntryImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.g11n.GlobalRegistryConfigHolder;
import org.ebayopensource.turmeric.runtime.common.impl.internal.g11n.GlobalRegistryConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.junit.Test;


public class GlobalRegistryTest extends AbstractTurmericTestCase {
	@Test
	public void clientConfig() throws Exception {
		StringBuffer output = new StringBuffer();
		
		GlobalRegistryConfigManager registry = 
			GlobalRegistryConfigManager.getInstance();
		registry.init();
		Collection<GlobalIdEntry> registryEntrySet = registry.getAllEntries();
		for (GlobalIdEntry entry : registryEntrySet) {
			((GlobalIdEntryImpl)entry).dump(output);
		}

		CompareUtils.writeOutputFile(this.getClass(), output, "registry");
		String compareString = CompareUtils.getCompareString(this.getClass(), "registry.compare.txt");
		assertEquals(compareString, output.toString());
	}
	
	@Test
	public void globalRegConfigHldr() {
		GlobalRegistryConfigManager registry = null;
		GlobalRegistryConfigHolder holder = new GlobalRegistryConfigHolder();
		StringBuffer holderSb = new StringBuffer();
		StringBuffer copyHolderSb = new StringBuffer();
		

		registry = GlobalRegistryConfigManager.getInstance();
		Collection<GlobalIdEntry> registryEntrySet = registry.getAllEntries();
		
		for(GlobalIdEntry gidEntry : registryEntrySet) {
			holder.setEntry(gidEntry.getId(), (GlobalIdEntryImpl)gidEntry);
		}
		
		assertEquals(registryEntrySet.size(), holder.getAllGlobalIds().size());
		
		GlobalRegistryConfigHolder holderCopy = holder.copy();
		holder.dump(holderSb);
		holderCopy.dump(copyHolderSb);
		
		assertEquals("The global registry dumps are to be identical", 
				holderSb.length(), copyHolderSb.length());
		
	}
}