/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.types1;

import org.ebayopensource.turmeric.runtime.tests.service1.sample.types2.UserType;


public class  ItemType {

	private long m_itemId;
	private String m_title;
	private double m_price;

	private UserType m_seller;



	public long getItemId() {
	  return m_itemId;
	}

	public void setItemId(long value) {
	  m_itemId = value;
	}



	public String getTitle() {
	  return m_title;
	}

	public void setTitle(String value) {
	  m_title = value;
	}


	public double getPrice() {
	  return m_price;
	}

	public void setPrice(double value) {
	  m_price = value;
	}




	public UserType getSeller() {
	  return m_seller;
	}

	public void setSeller(UserType value) {
	  m_seller = value;
	}


}