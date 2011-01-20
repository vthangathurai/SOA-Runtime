/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.types2;



public class  AddressType {

	private String m_address1;
	private String m_address2;

	private String m_city;
	private String m_state;
	private String m_country;
	private int m_zip;

	public String getAddress1() {
	  return m_address1;
	}

	public void setAddress1(String address1) {
		  m_address1 = address1;
	}


	public String getAddress2() {
	  return m_address2;
	}

	public void setAddress2(String address2) {
		  m_address2 = address2;
	}


	public String getCity() {
	  return m_city;
	}

	public void setCity(String city) {
		  m_city = city;
	}


	public String getState() {
	  return m_state;
	}

	public void setState(String state) {
		  m_state = state;
	}


	public String getCountry() {
	  return m_country;
	}

	public void setCountry(String country) {
		  m_country = country;
	}


	public int getZip() {
	  return m_zip;
	}

	public void setZip(int zip) {
		  m_zip = zip;
	}

}
