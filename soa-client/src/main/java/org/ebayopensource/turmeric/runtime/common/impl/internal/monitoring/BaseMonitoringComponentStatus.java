/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.monitoring.ErrorStatusOptions;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricCategory;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricClassifier;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricId;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricThreshold;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsCollector;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricsStorageProvider;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.LongSumMetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricComponentType;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricComponentValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValueAggregator;

import com.ebay.kernel.component.IComponentStatusXml;
import com.ebay.kernel.component.Registration;
import com.ebay.kernel.util.StringUtils;
import com.ebay.kernel.util.xml.IXmlStreamWriter;

/**
 * @author ichernyshev
 */
public abstract class BaseMonitoringComponentStatus implements
		IComponentStatusXml {

	private static final String NULL_PLACEHOLDER = "NULL_PLACEHOLDER_"
			+ System.currentTimeMillis();

	private static final String SERVICE_STATUS_IMPAIRED = "IMPAIRED";

	private static final String SERVICE_STATUS_NORMAL = "NORMAL";

	private static final String SERVICE_STATUS_UNDEFINED = "UNDEFINED";

	private final String m_name;

	private final String m_prefix;

	private final boolean m_isClient;

	private DiffSnapshotView m_diffSnapshotView;

	public BaseMonitoringComponentStatus(String name, boolean isClient,
			String prefix) {
		m_name = name;
		m_isClient = isClient;
		m_prefix = prefix;
	}

	public final String getName() {
		return m_name;
	}

	public final String getAlias() {
		return null;
	}

	public final String getStatus() {
		return "created";
	}

	public final List getProperties() {
		return Collections.EMPTY_LIST;
	}

	public final void renderXml(IXmlStreamWriter xmlWriter,
			Map<String, String> props) {
		setupDiffSnapshotView();

		xmlWriter.writeStartElement(m_prefix + "ServiceMonitoring_Root");
		try {
			Collection<MetricValueAggregator> values;

			String mode = props.get("mode");
			if (mode != null) {
				xmlWriter.writeAttribute("mode", mode);
			}

			if (mode != null && mode.equalsIgnoreCase("diff")) {
				if (m_diffSnapshotView != null) {
					long interval = m_diffSnapshotView.getLastInterval();
					if (interval != -1) {
						xmlWriter.writeAttribute("interval", Long
								.toString(interval));
						values = m_diffSnapshotView.getDiffAggregator();
						if (values == null) {
							xmlWriter.writeAttribute("diff_error",
									"No diff data returned, internal error");
							values = new ArrayList<MetricValueAggregator>();
						}
					} else {
						xmlWriter.writeAttribute("diff_error",
								"No diff data available yet");
						values = new ArrayList<MetricValueAggregator>();
					}
				} else {
					xmlWriter.writeAttribute("diff_error",
							"Diff provider is not configured");
					values = new ArrayList<MetricValueAggregator>();
				}
			} else {
				MetricsCollector metricsCollector;
				if (m_isClient) {
					metricsCollector = MetricsCollector.getClientInstance();
				} else {
					metricsCollector = MetricsCollector.getServerInstance();
				}
				
				//parameters for resetting the metric of specific service
				String actionStr = props.get("action");
				if (actionStr != null && actionStr.equals("reset")) {
					String targetStr = props.get("target");
					if (targetStr != null) {						
						MonitoringSystem.persistMetricsSnapSnapshot(targetStr, !m_isClient);
						metricsCollector.reset(targetStr);
					}
				}
				
				values = metricsCollector.getAllMetricValues();
			}

			SortedMap<String, Collection<MetricValueAggregator>> metrics = buildMetricsMapForAdminNames(values);

			MetricCategory categoryFilter = null;
			String categoryStr = props.get("category");
			if (categoryStr != null && !categoryStr.equalsIgnoreCase("all")) {
				categoryFilter = MetricCategory.fromString(categoryStr);
				xmlWriter.writeAttribute("category", categoryStr);
			}

			String detailStr = props.get("detail");
			if (detailStr != null) {
				xmlWriter.writeAttribute("single-service", detailStr);

				for (Map.Entry<String, Collection<MetricValueAggregator>> e : metrics
						.entrySet()) {
					if (!e.getKey().equals(detailStr)) {
						continue;
					}

					String adminName = e.getKey();
					Collection<MetricValueAggregator> values2 = e.getValue();
					renderServiceMetrics(adminName, values2, true,
							categoryFilter, xmlWriter, props);
				}

				return;
			}

			String fullViewStr = props.get("fullview");
			boolean isFullView = Boolean.parseBoolean(fullViewStr);

			Set<String> filter = null;
			String filterStr = props.get("filter");
			if (filterStr != null) {
				List<String> names = StringUtils.splitStr(filterStr, ',', true);
				if (!names.isEmpty()) {
					filter = new HashSet<String>(names);

					if (fullViewStr == null) {
						isFullView = true;
					}
				}
			}

			for (Map.Entry<String, Collection<MetricValueAggregator>> e : metrics
					.entrySet()) {
				if (filter != null && !matchFilter(e.getKey(), filter)) {
					continue;
				}

				String adminName = e.getKey();
				Collection<MetricValueAggregator> values2 = e.getValue();
				renderServiceMetrics(adminName, values2, isFullView,
						categoryFilter, xmlWriter, props);
			}
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	private boolean matchFilter(String adminName, Set<String> filter) {
		if (filter.contains(adminName)) {
			return true;
		}

		return false;
	}

	private SortedMap<String, Collection<MetricValueAggregator>> buildMetricsMapForAdminNames(
			Collection<MetricValueAggregator> values) {
		SortedMap<String, Collection<MetricValueAggregator>> result = new TreeMap<String, Collection<MetricValueAggregator>>();
		for (MetricValueAggregator value : values) {
			String adminName = value.getMetricId().getAdminName();

			Collection<MetricValueAggregator> sublist = result.get(adminName);
			if (sublist == null) {
				sublist = new ArrayList<MetricValueAggregator>();
				result.put(adminName, sublist);
			}

			sublist.add(value);
		}

		return result;
	}

	private SortedMap<String, Collection<MetricValueAggregator>> buildMetricsMapForOperations(
			Collection<MetricValueAggregator> values, boolean includeOperations) {
		SortedMap<String, Collection<MetricValueAggregator>> result = new TreeMap<String, Collection<MetricValueAggregator>>();
		for (MetricValueAggregator value : values) {
			String opName = value.getMetricId().getOperationName();

			if (!includeOperations && opName != null) {
				continue;
			}

			if (opName == null) {
				opName = NULL_PLACEHOLDER;
			}

			Collection<MetricValueAggregator> sublist = result.get(opName);
			if (sublist == null) {
				sublist = new ArrayList<MetricValueAggregator>();
				result.put(opName, sublist);
			}

			sublist.add(value);
		}

		return result;
	}

	private void renderServiceMetrics(String adminName,
			Collection<MetricValueAggregator> values, boolean isFullView,
			MetricCategory categoryFilter, IXmlStreamWriter xmlWriter,
			Map<String, String> props) {
		xmlWriter.writeStartElement(m_prefix + "ServiceMonitoring");
		try {
			xmlWriter.writeAttribute("name", adminName);

			SortedMap<String, Collection<MetricValueAggregator>> metricsMap = buildMetricsMapForOperations(
					values, isFullView);

			Collection<MetricValueAggregator> serviceLevelMetrics = metricsMap
					.get(NULL_PLACEHOLDER);
			if (serviceLevelMetrics != null) {
				renderServiceTotals(serviceLevelMetrics, xmlWriter,
						"ServiceMetrics", null, adminName, categoryFilter);
			}

			if (isFullView) {
				for (Map.Entry<String, Collection<MetricValueAggregator>> e : metricsMap
						.entrySet()) {
					String opName = e.getKey();

					if (opName == NULL_PLACEHOLDER) {
						continue;
					}

					Collection<MetricValueAggregator> value2 = e.getValue();
					renderServiceTotals(value2, xmlWriter, "OperationMetrics",
							opName, null, categoryFilter);
				}

				renderClassifiedMetrics(values, xmlWriter, categoryFilter);
			}
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	private void renderServiceTotals(Collection<MetricValueAggregator> values,
			IXmlStreamWriter xmlWriter, String elementName, String name,
			String adminName, MetricCategory categoryFilter) {
		xmlWriter.writeStartElement(elementName);
		try {
			if (name != null) {
				xmlWriter.writeAttribute("name", name);
			}

			MetricValueAggregator[] values2 = values
					.toArray(new MetricValueAggregator[values.size()]);
			Arrays.sort(values2,
					new MetricValueAggregatorComparatorByMetricName());

			String metricName = null;
			String threshold = null;
			if (adminName != null) {
				MetricsConfigManager configManager;
				if (m_isClient) {
					configManager = MetricsConfigManager.getClientInstance();
				} else {
					configManager = MetricsConfigManager.getServerInstance();
				}
				ErrorStatusOptions errorStatusOption = configManager
						.getErrorStatusOption(adminName);
				
				if(errorStatusOption != null) {
					metricName = errorStatusOption.getMetric();
					threshold = errorStatusOption.getThreshold();
				}
			}
			
			MetricThreshold metricThreshold = null;
			MetricValue totalMetricValue = null;
			if (m_diffSnapshotView != null) {
				
				if(adminName != null) {
					totalMetricValue = m_diffSnapshotView
						.getTotalMetricValue(adminName);
				}
				
				if(totalMetricValue == null ) {
					if(metricName != null && adminName != null) {
						totalMetricValue = (new LongSumMetricValue(new MetricId(metricName, adminName, null)).fromString("0"));
					}
				}
				
				if (totalMetricValue instanceof MetricThreshold) {
					MetricThreshold mtTotal = (MetricThreshold) totalMetricValue;
					metricThreshold = (MetricThreshold) mtTotal
						.fromString(threshold);
				}
			}
			// LongSumMetricValue lsmv = (LongSumMetricValue)(new
			// LongSumMetricValue(id).fromString(threshold));

			for (int i = 0; i < values2.length; i++) {
				MetricValueAggregator value = values2[i];
				MetricId id = value.getMetricId();

				if (categoryFilter != null
						&& value.getCategory() != categoryFilter) {
					continue;
				}

				// calculate cummulative value for all classifiers
				MetricValue totalValue = value.getTotalValue();
				if (totalValue == null) {
					// no data
					
					//continue;
					if(id == null) {
						continue;
					}
					if (id.getMetricName().equals(metricName)) {
						LongSumMetricValue lsmv = (LongSumMetricValue)(new LongSumMetricValue(id).fromString("0"));
						totalValue = lsmv;
					} else {
						continue;
					}
				}

				String status = SERVICE_STATUS_UNDEFINED;
				boolean renderStatus = false;

				if(adminName != null) {
					if (id.getMetricName().equals(metricName)) {
						renderStatus = true;
						try {
							if (metricThreshold != null) {
								int result = metricThreshold
										.compare(totalMetricValue);
	
								if (result == -1) {
									status = SERVICE_STATUS_IMPAIRED;
								} else {
									status = SERVICE_STATUS_NORMAL;
								}
							}
	
						} catch (ClassCastException ce) {
							//no op						
						}
					}
				}
					
				xmlWriter.writeStartElement("metric");
				try {
					xmlWriter.writeAttribute("name", id.getMetricName());

					renderMetricValue(id, totalValue, xmlWriter);
					if (renderStatus) {
						renderMetricErrorStatus(status, id.getMetricName(),
								totalMetricValue, xmlWriter);
					}
				} finally {
					xmlWriter.writeEndElement();
				}
			}
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	private void renderClassifiedMetrics(
			Collection<MetricValueAggregator> values,
			IXmlStreamWriter xmlWriter, MetricCategory categoryFilter) {
		xmlWriter.writeStartElement("AllMetrics");
		try {
			MetricValueAggregator[] values2 = values
					.toArray(new MetricValueAggregator[values.size()]);
			Arrays.sort(values2,
					new MetricValueAggregatorComparatorByOpMetricName());

			for (int i = 0; i < values2.length; i++) {
				MetricValueAggregator aggrValue = values2[i];
				MetricId id = aggrValue.getMetricId();

				if (categoryFilter != null
						&& aggrValue.getCategory() != categoryFilter) {
					continue;
				}

				Collection<MetricClassifier> classifiers = aggrValue
						.getClassifiers();
				if (classifiers == null || classifiers.isEmpty()) {
					continue;
				}

				for (MetricClassifier classifier : classifiers) {
					MetricValue value = aggrValue.getValue(classifier);
					if (value == null) {
						continue;
					}

					xmlWriter.writeStartElement("metric");
					try {
						xmlWriter.writeAttribute("name", id.getMetricName());

						String opName = id.getOperationName();
						if (opName != null) {
							xmlWriter.writeAttribute("opname", opName);
						}

						xmlWriter.writeAttribute("usecase", classifier
								.getUseCase());
						xmlWriter.writeAttribute("sourcedc", classifier
								.getSourceDC());
						xmlWriter.writeAttribute("targetdc", classifier
								.getTargetDC());

						renderMetricValue(id, value, xmlWriter);
					} finally {
						xmlWriter.writeEndElement();
					}
				}
			}
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	private void renderMetricValue(MetricId id, MetricValue value,
			IXmlStreamWriter xmlWriter) {
		MetricComponentValue[] components = value.getValues();
		List<MetricComponentType> types = value.getAllComponentsTypes();
		for (int i = 0; i < components.length; i++) {
			MetricComponentValue component = components[i];
			MetricComponentType type = types.get(i);

			Object valueObj = component.getValue();
			String valueStr = getValueStr(id, valueObj, type);

			xmlWriter.writeStartElement("component");
			try {
				xmlWriter.writeAttribute("name", component.getName());
				xmlWriter.writeCData(valueStr);
			} finally {
				xmlWriter.writeEndElement();
			}
		}
	}

	private void renderMetricErrorStatus(String status, String metricName,
			MetricValue mv, IXmlStreamWriter xmlWriter) {
		xmlWriter.writeStartElement("component");
		try {
			xmlWriter.writeAttribute("name", "serviceStatus");
			xmlWriter.writeCData(status);
		} finally {
			xmlWriter.writeEndElement();
		}

		xmlWriter.writeStartElement("component");
		try {
			xmlWriter.writeAttribute("name", "metricName");
			xmlWriter.writeCData(metricName);
		} finally {
			xmlWriter.writeEndElement();
		}

		xmlWriter.writeStartElement("component");
		try {
			xmlWriter.writeAttribute("name", "currentvalue");
			if (mv != null) {
				MetricComponentValue[] values = mv.getValues();
				if (values[0] != null) {
					xmlWriter.writeCData(values[0].getValue().toString());
				}
			} else {
				xmlWriter.writeCData("0");
			}
			
		} finally {
			xmlWriter.writeEndElement();
		}
	}

	private String getValueStr(MetricId id, Object value,
			MetricComponentType type) {
		Class clazz = type.getType();
		if (clazz.equals(Double.class) || clazz.equals(Float.class)) {
			StringBuilder sb = new StringBuilder();
			Formatter formatter = new Formatter(sb, Locale.US);
			formatter.format("%1$10.1f", value);
			return sb.toString();
		}
		return String.valueOf(value);
	}

	private void setupDiffSnapshotView() {
		if (m_diffSnapshotView != null) {
			return;
		}

		try {
			MonitoringDesc monitoringDesc;
			if (m_isClient) {
				monitoringDesc = MonitoringSystem.getClientMonitoringDesc();
			} else {
				monitoringDesc = MonitoringSystem.getServerMonitoringDesc();
			}

			if (monitoringDesc != null) {
				Collection<StorageProviderDesc> descs = monitoringDesc
						.getAllProviderDescs();
				for (StorageProviderDesc desc : descs) {
					MetricsStorageProvider provider = desc.getProvider();
					if (provider instanceof DiffSnapshotView) {
						m_diffSnapshotView = (DiffSnapshotView) desc
								.getProvider();
						break;
					}
				}
			}
			// TODO: allow diff interval changes in config bean
			// m_diffSnapshotView.setRefreshInterval(m_diffInterval);
		} catch (Throwable e) {
			LogManager.getInstance(this.getClass()).log(
					Level.SEVERE,
					"Exception looking up diff storage provider: "
							+ e.toString(), e);
		}
	}

	static String getRelativeMetricName(MetricId id) {
		StringBuilder sb = new StringBuilder();

		String opName = id.getOperationName();
		if (opName != null) {
			sb.append(opName);
			sb.append('.');
		} else {
			sb.append("All.");
		}

		sb.append(id.getMetricName());

		return sb.toString();
	}

	protected static final void initializeCompStatus(
			BaseMonitoringComponentStatus comp) {
		URL xslTemplate = BaseMonitoringComponentStatus.class
				.getResource("ServiceMonitoringCompStatus.xsl");
		if (xslTemplate == null) {
			throw new RuntimeException(
					"Unable to find XSL template ServiceMonitoringCompStatus.xsl"
							+ " for ServiceMonitoring component status "
							+ comp.getClass().getName());
		}

		Registration.registerComponent(comp, xslTemplate);
	}

	static class MetricValueAggregatorComparatorByMetricName implements
			Comparator<MetricValueAggregator> {
		public int compare(MetricValueAggregator obj1,
				MetricValueAggregator obj2) {
			String name1 = obj1.getMetricId().getMetricName();
			String name2 = obj2.getMetricId().getMetricName();
			return name1.compareTo(name2);
		}
	}

	static class MetricValueAggregatorComparatorByOpMetricName implements
			Comparator<MetricValueAggregator> {
		public int compare(MetricValueAggregator obj1,
				MetricValueAggregator obj2) {
			String name1 = getRelativeMetricName(obj1.getMetricId());
			String name2 = getRelativeMetricName(obj2.getMetricId());
			return name1.compareTo(name2);
		}
	}
}
