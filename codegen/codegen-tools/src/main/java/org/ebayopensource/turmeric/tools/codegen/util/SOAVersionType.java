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
package org.ebayopensource.turmeric.tools.codegen.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author arajmony
 *
 */
public class SOAVersionType implements Comparator<SOAVersionType>,Serializable {
	
	static final long serialVersionUID = -3265754573465635069L;
	
	String version;
	
	
	public SOAVersionType(String version){
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * 
	 *     Parameters:
        o1 - the first object to be compared.
        o2 - the second object to be compared. 
    	Returns:
        	a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second. 
    	Throws:
        	ClassCastException - if the arguments' types prevent them from being compared by this Comparator.
	 */
	@Override
	public int compare(SOAVersionType version1, SOAVersionType version2) {
		
		String[] version1Split = version1.version.split("[.]");
		String[] version2Split = version2.version.split("[.]");
		
		if(version1Split.length > version2Split.length){
			Integer integer = new Integer(version1Split[0]);
			return integer.intValue();
		}
		
		if(version2Split.length > version1Split.length){
			Integer integer = new Integer(version2Split[0]);
			return integer.intValue();
		}

		
		//at this place both arrays are of the same length
		int numberOfSplits = version1Split.length;
		for(int i=0 ; i < numberOfSplits ; i++){
			Integer integerOfVersion1 = new Integer(version1Split[i]);
			Integer integerOfVersion2 = new Integer(version2Split[i]);
			
			int intOfVersion1 = integerOfVersion1.intValue();
			int intOfVersion2 = integerOfVersion2.intValue();
			
			int diff = intOfVersion1 - intOfVersion2;
			if(diff != 0)
				return diff;
		}
		
		
		return 0;
	}

	
	/**
	 * compares the current SOAVersionType object with any other SOAVersionType object
	 * @param type
	 * @return 
	 * @author arajmony
	 */
	public int compare(SOAVersionType type){
		return compare(this, type);
	}
	
}
