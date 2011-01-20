/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.errorlibrary;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.Date;

import org.ebayopensource.turmeric.tools.GeneratedAssert;
import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorIdGenerator;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorIdGeneratorFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author arajmony
 */
public class ErrorIdGeneratorTest extends AbstractServiceGeneratorTestCase {

	private static final String MARKET_PLACES_ORG_NAME = "MarketPlaces";
	private static final int MIN_BLK_SIZE = 1000;
	
	private static final String DOMAIN_ONE = "DOMAIN_ONE";
	
	@Test(expected=IllegalArgumentException.class)
	public void errorIDGenerator_null_organization_name(){
		ErrorIdGeneratorFactory.getErrorIdGenerator(
        		testingdir.getDir().getAbsolutePath(),
        		null);
	}

	@Test
	public void buildErrorIDGenerator_test_before_firct_call_to_getNext() throws Exception {
		testingdir.ensureEmpty();
		File destDir = testingdir.getDir();

		Date date = new Date();
		long secs = date.getTime();
		
		ErrorIdGeneratorFactory.getErrorIdGenerator(
    		destDir.getAbsolutePath(),
    		MARKET_PLACES_ORG_NAME + secs,
    		100);

		GeneratedAssert.assertFileExists(destDir, (MARKET_PLACES_ORG_NAME + secs + "ErrorIDs.xml"));
	}

	@Test
	public void errorIDGenerator_subsequent_calls_with_same_param() throws Exception{
		testingdir.ensureEmpty();
		File destDir = testingdir.getDir();
		
		String orgName = "testErrorIDGenerator_subsequent_calls_with_same_param";
		
		Date date = new Date();
		
		ErrorIdGenerator  errorIdGenerator = ErrorIdGeneratorFactory.getErrorIdGenerator(
				destDir.getAbsolutePath(),
        		orgName + date.getTime());
		
		ErrorIdGenerator  errorIdGenerator2 = ErrorIdGeneratorFactory.getErrorIdGenerator(
				destDir.getAbsolutePath(),
        		orgName + date.getTime());
		
		long ID_1 = errorIdGenerator.getNextId(DOMAIN_ONE);
		long ID_2 = errorIdGenerator2.getNextId(DOMAIN_ONE);
		
		Assert.assertThat("ID_1 != ID_2", ID_1, not(ID_2));
	}

	@Test
	public void buildErrorIDGenerator_forSequenceGeneration() throws Exception {
		testingdir.ensureEmpty();
		File destDir = testingdir.getDir();

		Date date = new Date();
		long secs = date.getTime();

        ErrorIdGenerator  errorIdGenerator = ErrorIdGeneratorFactory.getErrorIdGenerator(
        		destDir.getAbsolutePath(),
        		MARKET_PLACES_ORG_NAME + secs);
        
        long id = errorIdGenerator.getNextId(DOMAIN_ONE);
        long id2 = errorIdGenerator.getNextId(DOMAIN_ONE);

        Assert.assertEquals("id2 [" + id2 + "] - id [" + id + "]", (id2-id), (long) 1);
	}

	
	@Test
	public void buildErrorIDGenerator_alwaysGreaterThanReservedRange() throws Exception {
		testingdir.ensureEmpty();
		File destDir = testingdir.getDir();
		
		Date date = new Date();
		long secs = date.getTime();
		
        ErrorIdGenerator  errorIdGenerator = ErrorIdGeneratorFactory.getErrorIdGenerator(
        		destDir.getAbsolutePath(),
        		MARKET_PLACES_ORG_NAME + secs + 1);
        
        long id = errorIdGenerator.getNextId(DOMAIN_ONE);
   

        ErrorIdGenerator  errorIdGenerator2 = ErrorIdGeneratorFactory.getErrorIdGenerator(
        		destDir.getAbsolutePath(),
        		MARKET_PLACES_ORG_NAME + secs + 2);
        
        long id2 = errorIdGenerator2.getNextId(DOMAIN_ONE);
        
        Assert.assertThat("id", id, greaterThan((long) MIN_BLK_SIZE));
        Assert.assertThat("id2", id2, greaterThan((long) MIN_BLK_SIZE));
	}	
	
	
	@Test
	public void buildErrorIDGenerator_Different_Domains_have_different_starting_points() throws Exception {
		testingdir.ensureEmpty();
		File destDir = testingdir.getDir();

		Date date = new Date();
		long secs = date.getTime();
		
        ErrorIdGenerator  errorIdGenerator = ErrorIdGeneratorFactory.getErrorIdGenerator(
        		destDir.getAbsolutePath(),
        		MARKET_PLACES_ORG_NAME + secs + 3);

        String domain = "abc";
        
        long id = errorIdGenerator.getNextId(DOMAIN_ONE + domain);
        long id2 = errorIdGenerator.getNextId(DOMAIN_ONE + domain + "2");

        Assert.assertEquals("id2 [" + id2 + "] - id [" + id + "]", (id2-id), (long) MIN_BLK_SIZE);
	}
	
	
	@Test
	public void buildErrorIDGenerator_block_size_less_than_minimum() throws Exception {
		testingdir.ensureEmpty();
		File destDir = testingdir.getDir();

		Date date = new Date();
		long secs = date.getTime();
		
		try {
			ErrorIdGeneratorFactory.getErrorIdGenerator(
					destDir.getAbsolutePath(), MARKET_PLACES_ORG_NAME + secs
							+ 4, 99);
			Assert.fail("Expected exception of type: " + IllegalArgumentException.class.getName());
		} catch (IllegalArgumentException ex) {
			Assert.assertThat(ex.getMessage(), containsString("The block specified is less then the minimum block size"));
		}
	}
	
	@Test
	public void buildErrorIDGenerator_does_id_overlap() throws Exception {
		testingdir.ensureEmpty();
		File destDir = testingdir.getDir();

		Date date = new Date();
		long secs = date.getTime();
		
		ErrorIdGenerator  errorIdGenerator = ErrorIdGeneratorFactory.getErrorIdGenerator(
				destDir.getAbsolutePath(),
    		MARKET_PLACES_ORG_NAME + secs,
    		100);
		
		String domainOne = "testBuildErrorIDGenerator_does_id_overlap_domain_one_"+ secs;
		String domainTwo = "testBuildErrorIDGenerator_does_id_overlap_domain_two_" + secs;

		long id1 = errorIdGenerator.getNextId(domainOne);
		long id2 = errorIdGenerator.getNextId(domainTwo);
		
		Assert.assertEquals("id2 [" + id2 + "] - id1 [" + id1 + "]", (id2-id1), (long) 100);
		
		for(int i=1 ;i<= 99; i++){
			errorIdGenerator.getNextId(domainOne);
		}
		
		long id3 = errorIdGenerator.getNextId(domainOne);
		
		Assert.assertEquals("id3 [" + id3 + "] - id2 [" + id2 + "]", (id3-id2), (long) 100);
	}
}
