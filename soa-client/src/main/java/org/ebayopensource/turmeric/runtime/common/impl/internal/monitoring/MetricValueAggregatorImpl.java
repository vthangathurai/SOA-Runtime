/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricCategory;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricClassifier;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.BaseMetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricComponentType;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricComponentValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContextAccessor;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;


/**
 * @author wdeng, ichernyshev
 */
final class MetricValueAggregatorImpl extends BaseMetricValue implements MetricValueAggregator {

	private static final String METRIC_CLASSIFIER_PROP = "org.ebayopensource.turmeric.runtime.MetricClassifier";
	private static final MetricClassifier UNKNOWN_CLASSIFIER = new MetricClassifier("Unknown", "Unknown", "Unknown");

	/*
	 * By default, we will use the Usecase, DC based classifier. However, user can
	 * specify a custom classifier, by providing a factory for the MetricValue.
	 */

	private final MetricDef m_metricDef;
	private final boolean m_isNormalLevel;
	private final MetricsCollector m_collector;
	private HashMap<MetricClassifier, MetricValue> m_valueByClassifier;
	private MetricValue m_firstValue;

	public static MetricValueAggregatorImpl create(MetricId id,
		MetricDef metricDef, MetricsCollector collector)
	{
		if (metricDef == null || id == null) {
			throw new NullPointerException();
		}

		MetricValue sampleValue = metricDef.getValueFactory().create(id);
		List<MetricComponentType> types = sampleValue.getAllComponentsTypes();

		return new MetricValueAggregatorImpl(id, types, metricDef, collector);
	}

	private MetricValueAggregatorImpl(MetricId id, List<MetricComponentType> types,
		MetricDef metricDef, MetricsCollector collector)
	{
		super(id, types);

		m_metricDef = metricDef;
		m_isNormalLevel = (metricDef.getLevel() == MonitoringLevel.NORMAL);
		m_collector = collector;

		m_valueByClassifier = new HashMap<MetricClassifier,MetricValue>();
	}

	public final boolean isEnabled() {
		if (m_isNormalLevel) {
			return true;
		}

		MonitoringLevel effectiveLevel = m_collector.getMonitoringLevel(getMetricId().getAdminName());

		return effectiveLevel.ordinal() >= m_metricDef.getLevel().ordinal();
	}

	public MetricDef getMetricDef() {
		return m_metricDef;
	}

	public MetricCategory getCategory() {
		return m_metricDef.getCategory();
	}

	public MonitoringLevel getLevel() {
		return m_metricDef.getLevel();
	}

	@Override
	public synchronized MetricValue deepCopy(boolean isReadOnly) {
		MetricValueAggregatorImpl result = (MetricValueAggregatorImpl)super.deepCopy(isReadOnly);

		HashMap<MetricClassifier,MetricValue> valuesSrc = m_valueByClassifier;
		HashMap<MetricClassifier,MetricValue> valuesDst =
			new HashMap<MetricClassifier,MetricValue>(valuesSrc.size());
		for (Map.Entry<MetricClassifier,MetricValue> e: valuesSrc.entrySet()) {
			MetricValue value2 = e.getValue().deepCopy(isReadOnly);
			valuesDst.put(e.getKey(), value2);
		}
		result.m_valueByClassifier = valuesDst;

		return result;
	}

	@Override
	public synchronized void addOtherValue(MetricValue other, boolean isPositive) {
		checkUpdateable();

		MetricValueAggregatorImpl other2 = (MetricValueAggregatorImpl)other;

		HashMap<MetricClassifier,MetricValue> otherValues = other2.m_valueByClassifier;
		for (Map.Entry<MetricClassifier,MetricValue> e: m_valueByClassifier.entrySet()) {
			MetricClassifier classifier = e.getKey();
			BaseMetricValue newValue = (BaseMetricValue)e.getValue();
			MetricValue otherValue = otherValues.get(classifier);
			if (otherValue != null) {
				newValue.addOtherValue(otherValue, isPositive);
			}
		}
	}

	public MetricValue getTotalValue() {
		BaseMetricValue result = null;
		for (MetricValue value: m_valueByClassifier.values()) {
			if (result != null) {
				result.addOtherValue(value, true);
			} else {
				if (m_valueByClassifier.size() == 1) {
					return value.deepCopy(true);
				}

				// there is more than 1 object, we will call add,
				// so no need to create a copy here
				result = (BaseMetricValue)value.deepCopy(false);
			}
		}

		if (result != null) {
			return result.deepCopy(true);
		}

		return null;
	}

	@Override
	public MetricComponentValue[] getValues() {
		throw new UnsupportedOperationException("MetricValueAggregatorImpl does not support getValues call");
	}

	public Collection<MetricClassifier> getClassifiers() {
		// no need to synchronize as people are handed out copies only
		return m_valueByClassifier.keySet();
	}

	public MetricValue getValue(MetricClassifier classifier) {
		return m_valueByClassifier.get(classifier);
	}

	@Override
	public void update(MetricValue value) {
		if (! isEnabled()) return;

		MetricClassifier classifier = getClassifierFromContext();
		update(classifier, value);
	}

	@Override
	public void update(int value) {
		if (! isEnabled()) return;

		MetricClassifier classifier = getClassifierFromContext();
		update(classifier, value);
	}

	@Override
	public void update(long value) {
		if (! isEnabled()) return;

		MetricClassifier classifier = getClassifierFromContext();
		update(classifier, value);
	}

	@Override
	public void update(float value) {
		if (! isEnabled()) return;

		MetricClassifier classifier = getClassifierFromContext();
		update(classifier, value);
	}

	@Override
	public void update(double value) {
		if (! isEnabled()) return;

		MetricClassifier classifier = getClassifierFromContext();
		update(classifier, value);
	}

	public void update(MessageContext ctx, MetricValue value) {
		if (! isEnabled()) return;

		MetricClassifier classifier = getClassifierFromContext(ctx);
		update(classifier, value);
	}

	public void update(MessageContext ctx, int value) {
		if (! isEnabled()) return;

		MetricClassifier classifier = getClassifierFromContext(ctx);
		update(classifier, value);
	}

	public void update(MessageContext ctx, long value) {
		if (! isEnabled()) return;

		MetricClassifier classifier = getClassifierFromContext(ctx);
		update(classifier, value);
	}

	public void update(MessageContext ctx, float value) {
		if (! isEnabled()) return;

		MetricClassifier classifier = getClassifierFromContext(ctx);
		update(classifier, value);
	}

	public void update(MessageContext ctx, double value) {
		if (! isEnabled()) return;

		MetricClassifier classifier = getClassifierFromContext(ctx);
		update(classifier, value);
	}

	private synchronized void update(MetricClassifier classifier, MetricValue newValue) {
		checkUpdateable();
		MetricValue classifiedValue = getMetricValueForClassifier(classifier);
		classifiedValue.update(newValue);
	}

	private synchronized void update(MetricClassifier classifier, int value) {
		checkUpdateable();
		MetricValue classifiedValue = getMetricValueForClassifier(classifier);
		classifiedValue.update(value);
	}

	private synchronized void update(MetricClassifier classifier, long value) {
		checkUpdateable();
		MetricValue classifiedValue = getMetricValueForClassifier(classifier);
		classifiedValue.update(value);
	}

	private synchronized void update(MetricClassifier classifier, float value) {
		checkUpdateable();
		MetricValue classifiedValue = getMetricValueForClassifier(classifier);
		classifiedValue.update(value);
	}

	private synchronized void update(MetricClassifier classifier, double value) {
		checkUpdateable();
		MetricValue classifiedValue = getMetricValueForClassifier(classifier);
		classifiedValue.update(value);
	}

	/**
	 * Resets the values, to be called by Ops manually
	 */
	public synchronized void reset() {
		checkUpdateable();
		m_valueByClassifier.clear();
	}

	private MetricValue getMetricValueForClassifier(MetricClassifier classifier) {
		MetricValue value = m_valueByClassifier.get(classifier);
		if (value == null) {
			value = m_metricDef.getValueFactory().create(getMetricId());
			if (value == null) {
				throw new NullPointerException();
			}

			if (m_firstValue != null) {
				if (m_firstValue.getClass() != value.getClass()) {
					throw new IllegalArgumentException(
						m_metricDef.getValueFactory().getClass().getName() +
						" returned two different value types: " + value.getClass().getName() +
						" and " + m_firstValue.getClass().getName());
				}
			} else {
				m_firstValue = value;
			}

			m_valueByClassifier.put(classifier, value);
		}

		return value;
	}

	private static MetricClassifier getClassifierFromContext() {
		BaseMessageContextImpl ctx = (BaseMessageContextImpl)MessageContextAccessor.getContext();
		return getClassifierFromContext(ctx);
	}

	private static MetricClassifier getClassifierFromContext(MessageContext ctx) {
		return getClassifierFromContext((BaseMessageContextImpl)ctx);
	}

	private static MetricClassifier getClassifierFromContext(BaseMessageContextImpl ctx) {
		/*
		 * Look in the MessageContext, find the USECASE_NAME and DC and
		 * look up in the map m_ClassifierBasedValues and
		 * update the respective MetricValue.
		 * The logic is similar for all update methods.
		 */

		if (ctx == null) {
			return UNKNOWN_CLASSIFIER;
		}


		MetricClassifier result = (MetricClassifier)ctx.getProperty(METRIC_CLASSIFIER_PROP);
		try {
			String usecase = ctx.getRequestMessage().getTransportHeader(SOAHeaders.USECASE_NAME);	
			String knownUsecase = result == null ? null : result.getUseCase();
			if (knownUsecase != null && !SOAConstants.DEFAULT_USE_CASE.equals(knownUsecase)
			 && knownUsecase.equals(usecase)) {
				return result;
			}
			
			String sourceDc = ctx.getClientAddress().getDataCenter();
			String targetDc = ctx.getServiceAddress().getDataCenter();

			if (usecase == null) {
				usecase = SOAConstants.DEFAULT_USE_CASE;
			}
			if (targetDc == null) {
				targetDc = "Unknown";
			}
			if (sourceDc == null) {
				sourceDc = "Unknown";
			}

			result = new MetricClassifier(usecase, sourceDc, targetDc);
		} catch (ServiceException e) {
			LogManager.getInstance(MetricValueAggregatorImpl.class).log(Level.WARNING,
				"Unable to build SOA Metrics Classifier: " + e.toString(), e);
			result = UNKNOWN_CLASSIFIER;
		}

		ctx.setSystemProperty(METRIC_CLASSIFIER_PROP, result);

		return result;
	}
}
