/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.util.HashMap;

import junit.framework.Assert;

import org.ebayopensource.turmeric.tools.codegen.external.WSDLUtil;
import org.junit.Test;


public class WSDLUtilTest extends AbstractServiceGeneratorTestCase 
{
    private HashMap<Integer, String>m_InputMap = new HashMap<Integer, String>();
    private HashMap<Integer, String>m_EpectedOutputMap = new HashMap<Integer, String>();
    public WSDLUtilTest(){
    	m_InputMap.put(1,"_valid");
		m_InputMap.put(2,"___valid");
		m_InputMap.put(3,"validName");
		m_InputMap.put(4,"ValidName");
		m_InputMap.put(5, "valid_isvalid");
		m_InputMap.put(6, "aaant4paramsList");
		m_EpectedOutputMap.put(1,"Valid");
		m_EpectedOutputMap.put(2,"Valid");
		m_EpectedOutputMap.put(3,"ValidName");
		m_EpectedOutputMap.put(4,"ValidName");
		m_EpectedOutputMap.put(5, "ValidIsvalid");
		m_EpectedOutputMap.put(6,"Aaant4ParamsList");
    	
    };
	
	@Test
	public void getXMLIdentifiersClassNameforEmptyString()
	{
		boolean isException = false;
		try
		{
		String ClassName = WSDLUtil.getXMLIdentifiersClassName("  ");
		Assert.assertNull(ClassName);
		}
		catch(IllegalArgumentException e)
		{
			isException = true;
		}
		Assert.assertTrue(isException);
	}
	
	@Test
	public void getXMLIdentifiersClassNameforStartingDigit()
	{
		boolean isException = false;
		try
		{
		String ClassName = WSDLUtil.getXMLIdentifiersClassName("123nvalid");
		Assert.assertNull(ClassName);
		}
		catch(IllegalArgumentException e)
		{
			isException = true;
		}
		Assert.assertTrue(isException);
	}
	
	@Test
	public void getXMLIdentifiersClassNameforStartingSpecialCharacter()
	{
		boolean isException = false;
		try
		{
		String ClassName = WSDLUtil.getXMLIdentifiersClassName("&*Invalid");
		Assert.assertNull(ClassName);
		}
		catch(IllegalArgumentException e)
		{
			isException = true;
		}
		Assert.assertTrue(isException);
	}
	
	@Test
	public void getXMLIdentifiersClassNameforValidString()
	{
		for(int i=1;i<=m_InputMap.size();i++)
		{
		String className = WSDLUtil.getXMLIdentifiersClassName(m_InputMap.get(i));
		Assert.assertEquals(m_EpectedOutputMap.get(i), className);
		}
	}   
}
