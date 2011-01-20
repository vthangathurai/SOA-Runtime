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
package org.ebayopensource.turmeric.runtime.tests.jaxb.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.sample.services.item.gen.HashMapWrapper;
import org.ebayopensource.turmeric.runtime.tests.sample.services.item.gen.HashSetWrapper;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.Address;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.ItemType;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types2.UserType;
import org.junit.Test;


/**
 * Test the marshalling and unmarshalling of complex object.
 * 
 * @author wdeng
 *
 */
public class ComplexObjectTest  {

	@Test
	public void marshalUnmarshalComplexObject() throws Exception {
		MyComplexObject obj = createMyComplexObject();
		JAXBElement ele = new JAXBElement(new QName("", "MyComplexObject"), obj.getClass(), obj);
		TestUtils.doMarshalUnMarshal(ele, false);
	}

	@Test
	public void marshalUnmarshalHashMapOfArray() throws Exception {
		HashMapOfArrayList obj = createHashMapOfArrayList();
		JAXBElement ele = new JAXBElement(new QName("", "HashMapOfArrayList"), obj.getClass(), obj);
		TestUtils.doMarshalUnMarshal(ele, false);
	}

	@Test
	public void marshalUnmarshalHashMapOfHashMap() throws Exception {
		HashMapOfHashMap obj = createHashMapOfHashMap();
		JAXBElement ele = new JAXBElement(new QName("", "HashMapOfHashMap"), obj.getClass(), obj);
		TestUtils.doMarshalUnMarshal(ele, false);
	}

	@Test
	public void marshalUnmarshalHashMapOfAddress() throws Exception {
		HashMapOfAddress obj = createHashMapOfAddress();
		JAXBElement ele = new JAXBElement(new QName("", "HashMapOfAddress"), obj.getClass(), obj);
		TestUtils.doMarshalUnMarshal(ele, true);
	}

	@Test
	public void marshalUnmarshalHashMapOfItem() throws Exception {
		HashMap<String, ItemType> obj = createHashMapOfItem();
		JAXBElement ele = new JAXBElement(new QName("", "HashMapOfItem"), obj.getClass(), obj);
		TestUtils.doMarshalUnMarshal(ele, false);
	}

	@Test
	public void marshalUnmarshalHashMapOfItemWithWrapper() throws Exception {
		HashMap<String, ItemType> obj = createHashMapOfItem();
		HashMapWrapper wrapper = new HashMapWrapper(obj);
		JAXBElement ele = new JAXBElement(new QName("", "HashMapWrapper"), wrapper.getClass(), wrapper);
		TestUtils.doMarshalUnMarshal(ele, false);
	}

	@Test
	public void marshalUnmarshalHashSetOfItem() throws Exception {
		HashSet<ItemType> obj = createHashSetOfItem();
		JAXBElement ele = new JAXBElement(new QName("", "HashSetOfItem"), obj.getClass(), obj);
		TestUtils.doMarshalUnMarshal(ele, false);
	}

	@Test
	public void marshalUnmarshalHashSetOfItemWithWrapper() throws Exception {
		HashSet<ItemType> obj = createHashSetOfItem();
		HashSetWrapper wrapper = new HashSetWrapper(obj);
		JAXBElement ele = new JAXBElement(new QName("", "HashSetWrapper"), wrapper.getClass(), wrapper);
		TestUtils.doMarshalUnMarshal(ele, false);
	}

	private MyComplexObject createMyComplexObject() {
		MyComplexObject obj = new MyComplexObject();
		
		HashMap<String, String> tmpMap = new HashMap<String, String> ();
		
		tmpMap.put("Content-Type", "text/xml");
		tmpMap.put("Content-Length", "1000");
		tmpMap.put("Accept-Encoding", "cn;En-Us");
		
		ArrayList<HashMap <String, String>> tmpList = new ArrayList<HashMap <String, String>>();
		tmpList.add(tmpMap);

		tmpMap = new HashMap<String, String> ();
		tmpMap.put("Content-Type", "text/plain");
		tmpMap.put("Content-Length", "2000");
		tmpMap.put("Accept-Encoding", "Ch");
		tmpList.add(tmpMap);
		
		HashMap<String, ArrayList<HashMap<String, String>>> requests = new HashMap<String, ArrayList<HashMap<String, String>>>();
		requests.put("Headers", tmpList);

		tmpList = new ArrayList<HashMap <String, String>>();

		tmpMap = new HashMap<String, String> ();
		tmpMap.put("UserId", "Joe Doe");
		tmpMap.put("Type", "seller");
		tmpMap.put("Session", "123654905495");
		tmpList.add(tmpMap);
		
		tmpMap = new HashMap<String, String> ();
		tmpMap.put("UserId", "Joy Ching");
		tmpMap.put("Type", "buyer");
		tmpMap.put("Session", "157234554905495");
		tmpList.add(tmpMap);
		
		obj.setMyNestedMap(requests);
		
		Address addr = new Address();
		addr.setEmailAddress("wdeng@ebay.com");
		obj.setAddress(addr);
		return obj;
	}

	private HashMapOfHashMap createHashMapOfHashMap() {
		HashMapOfHashMap obj = new HashMapOfHashMap();
						
		HashMap<String, HashMap<String, String>> headers = new HashMap<String, HashMap<String, String>>();
		
		HashMap<String, String> tmpList = new HashMap<String, String>();
		tmpList.put("1", "text/xml");
		tmpList.put("2", "text/plain");
		headers.put("Content-Type", tmpList);
		
		tmpList = new HashMap<String, String>();
		tmpList.put("1", "cn");
		tmpList.put("2", "En-Us");
		headers.put("Accept-Encoding", tmpList);
		
		obj.setMyNestedMap(headers);
		return obj;
	}

	private HashMapOfArrayList createHashMapOfArrayList() {
		HashMapOfArrayList obj = new HashMapOfArrayList();
						
		HashMap<String, ArrayList<String>> headers = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> tmpList = new ArrayList<String>();
		tmpList.add("text/xml");
		tmpList.add("text/plain");
		headers.put("Content-Type", tmpList);
		
		tmpList = new ArrayList<String>();
		tmpList.add("cn");
		tmpList.add("En-Us");
		headers.put("Accept-Encoding", tmpList);
		
		obj.setMyNestedMap(headers);
		return obj;
	}

	private HashMapOfAddress createHashMapOfAddress() {
		HashMapOfAddress obj = new HashMapOfAddress();
						
		HashMap<String, Address> addresses = new HashMap<String, Address>();
		
		Address addreBay = new Address();
		addreBay.setEmailAddress("contactus@ebay.com");
		addresses.put("eBay", addreBay);

		Address addrPP = new Address();
		addrPP.setEmailAddress("contactus@paypal.com");
		addresses.put("PayPal", addrPP);

		obj.setMyNestedMap(addresses);
		return obj;
	}

	private HashMap<String, ItemType> createHashMapOfItem() throws Exception {
		HashMap<String, ItemType> obj = new HashMap<String, ItemType>();
		
		ItemType item = new ItemType();
		item.setItemId(123456789);
		item.setTitle("A nice pink ipod, a perfect gift idea.");
		item.setPrice(99.99);
		obj.put(String.valueOf(item.getItemId()), item);

		item = new ItemType();
		item.setItemId(223456789);
		item.setTitle("Harry Potter book 8.");
		item.setPrice(89.99);
		obj.put(String.valueOf(item.getItemId()), item);

		return obj;
	}

	private HashSet<ItemType> createHashSetOfItem() throws Exception {
		HashSet<ItemType> obj = new HashSet<ItemType>();
		
		ItemType item = new ItemType();
		item.setItemId(123456789);
		item.setTitle("A nice pink ipod, a perfect gift idea.");
		item.setPrice(1999.99);
		HashMap<String, UserType> users = new HashMap<String, UserType>();
		UserType user1 = new UserType();
		user1.setUserId(12345);
		user1.setName("John");
		users.put("John", user1);
		UserType user2 = new UserType();
		user2.setUserId(882345);
		user2.setName("yoshi");		
		users.put("yoshi", user2);
		item.setSeller(user1);
		obj.add(item);

		item = new ItemType();
		item.setItemId(223456789);
		item.setTitle("Harry Potter book 8.");
		item.setPrice(1999.99);
		obj.add(item);

		return obj;
	}
}
