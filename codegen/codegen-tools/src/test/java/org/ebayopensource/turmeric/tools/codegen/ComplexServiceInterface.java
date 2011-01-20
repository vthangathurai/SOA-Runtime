/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.util.List;

import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.ItemType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types2.AddressType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types2.SearchResultType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types2.UserType;

public interface ComplexServiceInterface {

	public List<ItemType> getMyStuff(UserType user, String userName)
			throws Exception;

	//public Map<UserType, List<SearchResultType>> getItems(List<UserType> users);

	public List<ItemType> getSellerItems(String sellerName);

	public UserType getUser(String userName);

	public String getEbayOfficialTime();

	public List<SearchResultType> getSearchResults(String searcQry);

	public List<AddressType> getAddresses(String userName);

	public long getUserId(String userName);

	//public Map<String, Map<UserType, List<ItemType>>> getMyStuffMap(
	//		List<UserType> user, String userName) throws Exception;

	public void doSomething(String msg);

	public void doNothing();

	public long[] getLongArray();

	public ItemType[] getItemArray(String[] sellerNames);

}

