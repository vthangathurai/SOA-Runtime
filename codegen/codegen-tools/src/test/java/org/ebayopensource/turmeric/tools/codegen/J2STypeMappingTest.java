/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.tools.codegen.util.J2STypeMappingUtil;
import org.junit.Assert;
import org.junit.Test;


public class J2STypeMappingTest extends AbstractServiceGeneratorTestCase {

	@Test
	public void getXmlTypeName1() throws Exception {
		Class<?> stringClass = String.class;
		String stringClassSimpleName = stringClass.getSimpleName();

		QName qName = J2STypeMappingUtil.getXmlTypeName(stringClass);

		Assert.assertNotNull("QName should not be null", qName);
		Assert.assertEquals(stringClassSimpleName.toLowerCase(), qName.getLocalPart());
	}


	@Test
	public void getXmlTypeName2() throws Exception {
		Class<?> stringClass = this.getClass();

		QName qName = J2STypeMappingUtil.getXmlTypeName(stringClass);
		
		Assert.assertNull("QName should be null", qName);
	}
}
