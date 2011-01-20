/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.exceptions;

/**
 * This is a data structure to hold error name and error domain in pair. 
 * an error domain plus an error name uniquely identify an Error defined in an
 * Error Library.  This structure acts as a key for an CommonErrorData.
 *  
 * @author ana, wdeng
 *
 */
class ErrorNameDomain{
	
	private String errorName;
	private String domain;
	
	/**
	 * 
	 * @param errorName The name of an CommonErrorData.
	 * @param domain The domain of an CommonErrorData.
	 */
	ErrorNameDomain(String errorName, String domain){
		this.errorName = errorName;
		this.domain = domain;
	}
	
	/**
	 * 
	 * @return an error name.
	 */
	public String getErrorName() {
		return errorName;
	}

	/**
	 * 
	 * @return an error domain.
	 */
	public String getDomain() {
		return domain;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ErrorNameDomain [domain=");
        builder.append(domain);
        builder.append(", errorName=");
        builder.append(errorName);
        builder.append("]");
        return builder.toString();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result
				+ ((errorName == null) ? 0 : errorName.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ErrorNameDomain other = (ErrorNameDomain) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (errorName == null) {
			if (other.errorName != null)
				return false;
		} else if (!errorName.equals(other.errorName))
			return false;
		return true;
	}
}