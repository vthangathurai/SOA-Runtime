package org.ebayopensource.turmeric.runtime.spf.pipeline;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;

public interface ServiceImplFactory<T> {
	public T createServiceImpl(final MessageContext context) throws ServiceException; 
}
