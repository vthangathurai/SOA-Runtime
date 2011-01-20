/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.errors;

import java.util.List;

import org.ebayopensource.turmeric.common.v1.types.ErrorParameter;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;

/**
 * Derived class of ErrorData (the default wire format individual error instance within the SOA Framework).
 * LocalizableErrorData holds pre-localized error information within an error message,
 * prior to the error mapping process which will perform localization and transfer to the pre-serialized error response
 * (wire) object.
 * @author ichernyshev
 * @deprecated  Replace by Error Library
 */
public class LocalizableErrorData extends CommonErrorData {

	private final ErrorDesc m_errorDesc;
	private final Object[] m_params;

	/**
	 * Constructs an ErrorData based on the classification information in the specified ErrorDesc.  This
	 * constructor form takes no dynamic parameter information.
	 * @param errorDesc the ErrorDesc with static classification information (numeric error ID, message string, severity, etc.).
	 */
	public LocalizableErrorData(ErrorDesc errorDesc) {
		this(errorDesc, null);
	}

	/**
	 * Constructs an ErrorData based on the classification information in the specified ErrorDesc, with the specified
	 * dynamic parameters.
	 * @param errorDesc the ErrorDesc with static classification information (numeric error ID, message string, severity, etc.).
	 * @param params an array of object values. These will be used later for string substitution using Java's
	 * message localization mechanism.
	 */
	public LocalizableErrorData(ErrorDesc errorDesc, Object[] params)
	{
		if (errorDesc == null) {
			throw new NullPointerException();
		}

		super.setCategory(errorDesc.getCategory());
		super.setDomain(errorDesc.getDomain());
		super.setErrorId(errorDesc.getId());
		super.setSeverity(errorDesc.getSeverity());
		super.setMessage(errorDesc.buildMessage(null, params));
		super.setSubdomain(getSubdomain(errorDesc));

		m_errorDesc = errorDesc;

		if (params != null) {
			m_params = new Object[params.length];
			System.arraycopy(params, 0, m_params, 0, params.length);
			List<ErrorParameter> myParams = getParameter();
			for (int i=0; i<params.length; i++) {
				ErrorParameter param = new ErrorParameter();
				param.setName("Param" + i);
				if(params[i] != null) {
				    param.setValue(params[i].toString());
				}
				else {
				    param.setValue("null");
				}
				myParams.add(param);
			}
		} else {
			m_params = null;
		}
	}

	private String getSubdomain(ErrorDesc errorDesc) {
	    return errorDesc.getSubcategory().getName();
    }

    /**
	 * Localizes the internally held message based on the requested locale,
	 * or using English if no translation is available.  The internally held parameters are
	 * substituted using the {0} {1} etc. string substitution mechanism.
	 * @param locale the locale for the translated message.
	 */
	public void localizeMessage(String locale) {
		String newText = m_errorDesc.buildMessage(locale, m_params);
		setMessage(newText);
	}
}
