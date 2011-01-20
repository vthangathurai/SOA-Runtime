/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.pipeline;

import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;


/**
 * Defines an interface to implement custom auto-markdown behavior.
 * 
 * The implementation of this class should not provide thread protection since
 * all methods are called under synchronization of appropriate markdown state object
 * 
 * @author ichernyshev
 */
public interface AutoMarkdownState {

	/**
	 * Resets the automarkdown state upon markup or manual markdown.
	 */
	public void reset();

	/**
	 * Returns non-null reason text if it's down automatically.
	 * 
	 * @return Must return null if there is no current automarkdown
	 */
	public String getMarkdownReason();

	/**
	 * Count the number of errors in the context which matches exception.
	 * @param ctx the context
	 * @param e the matching e
	 * @throws ServiceException throws when error happens
	 */
	public void countError(ClientMessageContext ctx, Throwable e) throws ServiceException;

	/**
	 * Count the number of success in the context.
	 * @param ctx the context
	 * @throws ServiceException throws when error happens
	 */
	public void countSuccess(ClientMessageContext ctx) throws ServiceException;

	/**
	 * Returns all attributes to be added to snapshot data, or null.
	 * 
	 * @return The map containing the attributes 
	 */
	public Map<String,String> getSnapshotAttrs();

	/**
	 * Copies data from another state during ServiceDesc re-creation.
	 * 
	 * If AutoMarkdownStateFactory has changed, this method may be called with a different
	 * concrete implementation of AutoMarkdownState
	 * 
	 * @param other from data
	 */
	public void copyStateFrom(AutoMarkdownState other);
}
