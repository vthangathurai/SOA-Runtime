/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.pipeline;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.handlers.BaseHandler;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.ServiceLayerType;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ClientMessageContext;

/**
 * This handler performs the Marketplace interpretation of layer taxonomy and
 * dependency rules
 */
public class LayerCheckHandler extends BaseHandler {

	@Override
	public void init(InitContext ctx) throws ServiceException {
		super.init(ctx);

		// enfore that the handler is client side only
		ServiceId svcId = ctx.getServiceId();
		if (!svcId.isClientSide()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_FACTORY_CANNOT_USE_ON_SERVER,
					ErrorConstants.ERRORDOMAIN, new Object[] { this.getClass().getName(), svcId.getAdminName() }));
		}

	}

	@Override
	public void invoke(MessageContext msgCtx) throws ServiceException {
		// msgCtx is clientMessageContextImpl
		// srcCtx is ServerMessageContextImpl
		MessageContext srcCtx = ((ClientMessageContext) msgCtx).getCallerMessageContext();
		if (srcCtx == null) {
			return; // service is not chained
		}

		// TODO: this should happen in init, but currently we can not get to
		// serviceDesc using HandlerInitContext.
		validateLayerNames(srcCtx);
		
		checkChainingCompatibility(srcCtx, msgCtx);
	}

	private void checkChainingCompatibility(MessageContext srcCtx, MessageContext destCtx)
			throws ServiceException {
		ServiceLayerType callerLayer = getCallerLayer(srcCtx);
		ServiceLayerType destinationLayer = getDestinationLayer(destCtx);

		switch (callerLayer) {
		case BUSINESS:
			if (!(destinationLayer == ServiceLayerType.COMMON || destinationLayer == ServiceLayerType.INTERMEDIATE)) {
				// called message context must be at layer COMMON or INTERMEDIATE
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_LAYER_VIOLATION,
						ErrorConstants.ERRORDOMAIN, new Object[] { srcCtx.getAdminName(), callerLayer.name(),
								destCtx.getAdminName(), destinationLayer.name() }));
			}
			break;
		case INTERMEDIATE:
			// called message contet must be at layer COMMON or INTERMEDIATE
			// INTERMEDIATE to INTERMEDIATE callouts are PCR controlled
			if (!(destinationLayer == ServiceLayerType.COMMON || destinationLayer == ServiceLayerType.INTERMEDIATE)) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_LAYER_VIOLATION,
						ErrorConstants.ERRORDOMAIN, new Object[] { srcCtx.getAdminName(), callerLayer.name(),
								destCtx.getAdminName(), destinationLayer.name() }));
			}
			break;
		case COMMON:
			// the called context must be at COMMON
			if (!(destinationLayer == ServiceLayerType.COMMON)) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_LAYER_VIOLATION,
						ErrorConstants.ERRORDOMAIN, new Object[] { srcCtx.getAdminName(), callerLayer.name(),
								destCtx.getAdminName(), destinationLayer.name() }));
			}
			break;
		}
	}

	private void validateLayerNames(MessageContext ctx) throws ServiceCreationException,
			ServiceException {
		List<String> layers = ctx.getServiceContext().getServiceLayerNames();
		for (String layer : layers) {
			if (ServiceLayerType.fromValue(layer) == null) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_INVALID_LAYER_NAME,
						ErrorConstants.ERRORDOMAIN, new Object[] { layer, ctx.getServiceId().getAdminName() }));
			}
		}
	}

	private ServiceLayerType getCallerLayer(MessageContext ctx) throws ServiceException {
		List<String> layers = ctx.getServiceContext().getServiceLayerNames();
		return getLayerType(ctx, layers);
	}

	private ServiceLayerType getDestinationLayer(MessageContext ctx)
			throws ServiceException {
		List<String> layers = ctx.getServiceContext().getServiceLayerNames();
		return getLayerType(ctx, layers);
	}

	private ServiceLayerType getLayerType(MessageContext ctx, List<String> layers)
			throws ServiceException {
		ServiceLayerType layerType = ServiceLayerType.fromValue(ctx.getServiceLayer());
		if (layerType != null && layers.contains(layerType.name())) {
			return layerType;
		}
		throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_INVALID_LAYER_NAME,
				ErrorConstants.ERRORDOMAIN, new Object[] { ctx.getServiceLayer(), ctx.getAdminName() }));
	}

}
