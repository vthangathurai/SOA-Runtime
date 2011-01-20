/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.types2;

import java.util.List;

public class  UserType {

	private long m_userId;
	private String m_name;
	private String m_emailId;
	private List<AddressType> m_addresses;


	public long getUserId() {
	  return m_userId;
	}

	public void setUserId(long value) {
	  m_userId = value;
	}



	public String getName() {
	  return m_name;
	}

	public void setName(String value) {
	  m_name = value;
	}


	public String getEmailId() {
	  return m_emailId;
	}

	public void setEmailId(String value) {
	  m_emailId = value;
	}



	public List<AddressType> getAddressList() {
	  return m_addresses;
	}

	public void setAddressList(List<AddressType> value) {
	  m_addresses = value;
	}





}