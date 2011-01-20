/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.types1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ebayopensource.turmeric.runtime.common.binding.TypeConverter;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;


/**
 * @author wdeng
 */
public class RecipientMapConverter implements TypeConverter<AddressList, HashMap> {

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.binding.TypeConverter#getBoundType()
	 */
	public Class<HashMap> getBoundType() {
		return HashMap.class;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.binding.TypeConverter#getValueType()
	 */
	public Class<AddressList> getValueType() {
		return AddressList.class;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.binding.TypeConverter#postDeserializationConvert(java.lang.Object)
	 */
	public HashMap postDeserializationConvert(MessageContext ctx, AddressList value)
			throws ServiceException {
		AddressList addrList = (AddressList)value;
		List addrs = addrList.getAddress();
		HashMap<String, Address> addrMap = new HashMap<String, Address>();
		for (int i = 0; i<addrs.size(); i++) {
			Address addr = (Address) addrs.get(i);
			addrMap.put(addr.getEmailAddress(), addr);
		}
		return addrMap;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.binding.TypeConverter#preSerializationConvert(java.lang.Object)
	 */
	public AddressList preSerializationConvert(MessageContext ctx, HashMap value)
			throws ServiceException {

		@SuppressWarnings("unchecked")
		HashMap<String, Address> addrMap = (HashMap<String, Address>)value;

		Collection<Address> addrCollection = addrMap.values();
		Iterator<Address> iter = addrCollection.iterator();
		ArrayList<Address> addrs = new ArrayList<Address>(addrCollection.size());
		while (iter.hasNext()) {
			Address addr = iter.next();
			addrs.add(addr);
		}
		AddressList addrList = new AddressList();
		addrList.setAddress(addrs);
		return addrList;
	}

}
