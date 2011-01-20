/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.monitoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.monitoring.MetricCategory;
import org.ebayopensource.turmeric.runtime.common.monitoring.MetricDef;
import org.ebayopensource.turmeric.runtime.common.monitoring.MonitoringLevel;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.AverageMetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.LongSumMetricValue;
import org.ebayopensource.turmeric.runtime.common.monitoring.value.MetricValue;


/**
 * This class captures the different metrics defined in the SOA Framework.
 * Some predefined metrics are statically defined in this class.
 *
 * @author ichernyshev
 */
public final class SystemMetricDefs {

    public final static String CTX_KEY_MSG_PROCESSING_STARTED = "MessageProcessingStarted";

    public final static SvcLevelMetricDef SVC_TIME_TOTAL = new SvcLevelTimingMetricDef("Total");
    public final static SvcLevelMetricDef SVC_TIME_CALL = new SvcLevelTimingMetricDef("Call");
    public final static SvcLevelMetricDef SVC_TIME_RESP_DISPATCH = new SvcLevelTimingMetricDef("RespDispatch");
    public final static SvcLevelMetricDef SVC_TIME_PIPELINE_REQUEST = new SvcLevelTimingMetricDef("Pipeline_Request");
    public final static SvcLevelMetricDef SVC_TIME_PIPELINE_RESPONSE = new SvcLevelTimingMetricDef("Pipeline_Response");
    public final static SvcLevelMetricDef SVC_TIME_SERIALIZATION = new SvcLevelTimingMetricDef("Serialization");
    public final static SvcLevelMetricDef SVC_TIME_DESERIALIZATION = new SvcLevelTimingMetricDef("Deserialization");
    public final static SvcLevelMetricDef SVC_TIME_TRAFFICLIMITER = new SvcLevelTimingMetricDef("RateLimiter");
    public final static SvcLevelMetricDef SVC_TIME_AUTHENTICATION = new SvcLevelTimingMetricDef("Authentication");
    public final static SvcLevelMetricDef SVC_TIME_AUTHORIZATION = new SvcLevelTimingMetricDef("Authorization");
    public final static SvcLevelMetricDef SVC_TIME_BLACKLIST = new SvcLevelTimingMetricDef("Blacklist");
    public final static SvcLevelMetricDef SVC_TIME_WHITELIST = new SvcLevelTimingMetricDef("Whitelist");
    public final static SvcLevelMetricDef SVC_TIME_POLICYENFORCEMENT = new SvcLevelTimingMetricDef("PolicyEnforcement");
    public final static SvcLevelMetricDef SVC_TIME_CAL_LOGGING = new SvcLevelTimingMetricDef("CalLogging");
    public final static SvcLevelMetricDef SVC_TIME_LOGGING = new SvcLevelTimingMetricDef("Logging");
    // This section of metrics are added for diagnose times missing in action
    // (Time spend outside
    // Pipeline_Request, Deserialization, Call, Pipeline_Response, and
    // Serialization. They don't add up to total
    public final static SvcLevelMetricDef SVC_TIME_START_DESER = new SvcLevelTimingMetricDef("StartDeserialization",
            MonitoringLevel.FINEST);
    public final static SvcLevelMetricDef SVC_TIME_START_REQ_PIPE = new SvcLevelTimingMetricDef("StartReqPipeline",
            MonitoringLevel.FINEST);
    public final static SvcLevelMetricDef SVC_TIME_START_CALL = new SvcLevelTimingMetricDef("StartCall", MonitoringLevel.FINEST);
    public final static SvcLevelMetricDef SVC_TIME_START_RESP_PIPE = new SvcLevelTimingMetricDef("StartRespPipeline",
            MonitoringLevel.FINEST);
    public final static SvcLevelMetricDef SVC_TIME_START_SESER = new SvcLevelTimingMetricDef("StartSerialization",
            MonitoringLevel.FINEST);

    public final static OpLevelMetricDef OP_TIME_START_DESER = new OpLevelTimingMetricDef(SVC_TIME_START_DESER,
            MonitoringLevel.FINEST);
    public final static OpLevelMetricDef OP_TIME_START_REQ_PIPE = new OpLevelTimingMetricDef(SVC_TIME_START_REQ_PIPE,
            MonitoringLevel.FINEST);
    public final static OpLevelMetricDef OP_TIME_START_CALL = new OpLevelTimingMetricDef(SVC_TIME_START_CALL,
            MonitoringLevel.FINEST);
    public final static OpLevelMetricDef OP_TIME_START_RESP_PIPE = new OpLevelTimingMetricDef(SVC_TIME_START_RESP_PIPE,
            MonitoringLevel.FINEST);
    public final static OpLevelMetricDef OP_TIME_START_SER = new OpLevelTimingMetricDef(SVC_TIME_START_SESER,
            MonitoringLevel.FINEST);
    public final static OpLevelMetricDef OP_TIME_TOTAL = new OpLevelTimingMetricDef(SVC_TIME_TOTAL);
    public final static OpLevelMetricDef OP_TIME_CALL = new OpLevelTimingMetricDef(SVC_TIME_CALL);
    public final static OpLevelMetricDef OP_TIME_RESP_DISPATCH = new OpLevelTimingMetricDef(SVC_TIME_RESP_DISPATCH);
    public final static OpLevelMetricDef OP_TIME_PIPELINE_REQUEST = new OpLevelTimingMetricDef(SVC_TIME_PIPELINE_REQUEST);
    public final static OpLevelMetricDef OP_TIME_PIPELINE_RESPONSE = new OpLevelTimingMetricDef(SVC_TIME_PIPELINE_RESPONSE);
    public final static OpLevelMetricDef OP_TIME_SERIALIZATION = new OpLevelTimingMetricDef(SVC_TIME_SERIALIZATION);
    public final static OpLevelMetricDef OP_TIME_DESERIALIZATION = new OpLevelTimingMetricDef(SVC_TIME_DESERIALIZATION);
    public final static OpLevelMetricDef OP_TIME_TRAFFICLIMITER = new OpLevelTimingMetricDef(SVC_TIME_TRAFFICLIMITER);
    public final static OpLevelMetricDef OP_TIME_AUTHENTICATION = new OpLevelTimingMetricDef(SVC_TIME_AUTHENTICATION);
    public final static OpLevelMetricDef OP_TIME_AUTHORIZATION = new OpLevelTimingMetricDef(SVC_TIME_AUTHORIZATION);
    public final static OpLevelMetricDef OP_TIME_BLACKLIST = new OpLevelTimingMetricDef(SVC_TIME_BLACKLIST);
    public final static OpLevelMetricDef OP_TIME_WHITELIST = new OpLevelTimingMetricDef(SVC_TIME_WHITELIST);
    public final static OpLevelMetricDef OP_TIME_POLICYENFORCEMENT = new OpLevelTimingMetricDef(SVC_TIME_POLICYENFORCEMENT);
    public final static OpLevelMetricDef OP_TIME_CAL_LOGGING = new OpLevelTimingMetricDef(SVC_TIME_CAL_LOGGING);
    public final static OpLevelMetricDef OP_TIME_LOGGING = new OpLevelTimingMetricDef(SVC_TIME_LOGGING);
    

    // Total Error Metrics
    public final static SvcLevelMetricDef SVC_ERR_TOTAL = new SvcLevelErrorMetricDef("Total");

    // Category Error Metrics
    public final static SvcLevelMetricDef SVC_ERR_CAT_SYSTEM = new SvcLevelErrorMetricDef("Category.System");
    public final static SvcLevelMetricDef SVC_ERR_CAT_APPLICATION = new SvcLevelErrorMetricDef("Category.Application");
    public final static SvcLevelMetricDef SVC_ERR_CAT_REQUEST = new SvcLevelErrorMetricDef("Category.Request");
    public final static SvcLevelMetricDef SVC_ERR_UNEXPECTED = new SvcLevelErrorMetricDef("Unexpected");

    public final static SvcLevelMetricDef SVC_ERR_SEVERITY_ERROR = new SvcLevelErrorMetricDef("Severity.Error");
    public final static SvcLevelMetricDef SVC_ERR_SEVERITY_WARNING = new SvcLevelErrorMetricDef("Severity.Warning");

    public final static OpLevelMetricDef OP_ERR_TOTAL = new OpLevelErrorMetricDef(SVC_ERR_TOTAL);
    public final static OpLevelMetricDef OP_ERR_CAT_SYSTEM = new OpLevelErrorMetricDef(SVC_ERR_CAT_SYSTEM);
    public final static OpLevelMetricDef OP_ERR_CAT_APPLICATION = new OpLevelErrorMetricDef(SVC_ERR_CAT_APPLICATION);
    public final static OpLevelMetricDef OP_ERR_CAT_REQUEST = new OpLevelErrorMetricDef(SVC_ERR_CAT_REQUEST);
    public final static OpLevelMetricDef OP_ERR_UNEXPECTED = new OpLevelErrorMetricDef(SVC_ERR_UNEXPECTED);
    public final static OpLevelMetricDef OP_ERR_SEVERITY_ERROR = new OpLevelErrorMetricDef(SVC_ERR_SEVERITY_ERROR);
    public final static OpLevelMetricDef OP_ERR_SEVERITY_WARNING = new OpLevelErrorMetricDef(SVC_ERR_SEVERITY_WARNING);
    public final static OpLevelMetricDef OP_ = new OpLevelErrorMetricDef(SVC_ERR_UNEXPECTED);
    public final static SvcLevelMetricDef SVC_ERR_FAILED_CALLS = new SvcLevelMetricDef("FailedCalls", MetricCategory.Error,
            LongSumMetricValue.class);

    public final static SvcLevelMetricDef SVC_WS_SPF_CALLS = new SvcLevelMetricDef("EndPointContainingGenericServlet", MetricCategory.Other,
            LongSumMetricValue.class);
    
    public final static OpLevelMetricDef OP_ERR_FAILED_CALLS = new OpLevelMetricDef("FailedCalls", SVC_ERR_FAILED_CALLS,
            MetricCategory.Error, LongSumMetricValue.class);

    private static List<SvcLevelMetricDef> s_allSvcMetrics = new ArrayList<SvcLevelMetricDef>();
    private static List<OpLevelMetricDef> s_allOpMetrics = new ArrayList<OpLevelMetricDef>();
    private static Map<OpLevelMetricDef, OpLevelMetricDef> s_matchingStartTimeOpMetrics = new HashMap<OpLevelMetricDef, OpLevelMetricDef>(5);

    public static List<SvcLevelMetricDef> getAllSvcMetrics() {
        return s_allSvcMetrics;
    }

    public static List<OpLevelMetricDef> getAllOperationMetrics() {
        return s_allOpMetrics;
    }

    public static OpLevelMetricDef getStartTimeOperationMetrics(OpLevelMetricDef OpMetric) {
        return s_matchingStartTimeOpMetrics.get(OpMetric);
    }

    static {
        s_allSvcMetrics.add(SVC_TIME_TOTAL);
        s_allSvcMetrics.add(SVC_TIME_CALL);
        s_allSvcMetrics.add(SVC_TIME_RESP_DISPATCH);
        s_allSvcMetrics.add(SVC_TIME_PIPELINE_REQUEST);
        s_allSvcMetrics.add(SVC_TIME_PIPELINE_RESPONSE);
        s_allSvcMetrics.add(SVC_TIME_SERIALIZATION);
        s_allSvcMetrics.add(SVC_TIME_DESERIALIZATION);
        s_allSvcMetrics.add(SVC_TIME_TRAFFICLIMITER);
        s_allSvcMetrics.add(SVC_TIME_AUTHENTICATION);
        s_allSvcMetrics.add(SVC_TIME_AUTHORIZATION);
        s_allSvcMetrics.add(SVC_TIME_CAL_LOGGING);
        s_allSvcMetrics.add(SVC_TIME_LOGGING);
        s_allSvcMetrics.add(SVC_TIME_BLACKLIST);
        s_allSvcMetrics.add(SVC_TIME_WHITELIST);
        s_allSvcMetrics.add(SVC_ERR_TOTAL);
        s_allSvcMetrics.add(SVC_ERR_FAILED_CALLS);
        s_allSvcMetrics.add(SVC_WS_SPF_CALLS);
        s_allSvcMetrics.add(SVC_ERR_UNEXPECTED);

        s_allSvcMetrics.add(SVC_ERR_CAT_SYSTEM);
        s_allSvcMetrics.add(SVC_ERR_CAT_APPLICATION);
        s_allSvcMetrics.add(SVC_ERR_CAT_REQUEST);

        s_allSvcMetrics.add(SVC_ERR_SEVERITY_ERROR);
        s_allSvcMetrics.add(SVC_ERR_SEVERITY_WARNING);

        s_allSvcMetrics.add(SVC_TIME_START_DESER);
        s_allSvcMetrics.add(SVC_TIME_START_REQ_PIPE);
        s_allSvcMetrics.add(SVC_TIME_START_CALL);
        s_allSvcMetrics.add(SVC_TIME_START_RESP_PIPE);
        s_allSvcMetrics.add(SVC_TIME_START_SESER);

        s_allOpMetrics.add(OP_TIME_TOTAL);
        s_allOpMetrics.add(OP_TIME_CALL);
        s_allOpMetrics.add(OP_TIME_RESP_DISPATCH);
        s_allOpMetrics.add(OP_TIME_PIPELINE_REQUEST);
        s_allOpMetrics.add(OP_TIME_PIPELINE_RESPONSE);
        s_allOpMetrics.add(OP_TIME_SERIALIZATION);
        s_allOpMetrics.add(OP_TIME_DESERIALIZATION);
        s_allOpMetrics.add(OP_TIME_TRAFFICLIMITER);
        s_allOpMetrics.add(OP_TIME_AUTHENTICATION);
        s_allOpMetrics.add(OP_TIME_AUTHORIZATION);
        s_allOpMetrics.add(OP_TIME_BLACKLIST);
        s_allOpMetrics.add(OP_TIME_WHITELIST);
        s_allOpMetrics.add(OP_TIME_POLICYENFORCEMENT);
        s_allOpMetrics.add(OP_TIME_CAL_LOGGING);
        s_allOpMetrics.add(OP_TIME_LOGGING);
        
        s_allOpMetrics.add(OP_ERR_TOTAL);

        s_allOpMetrics.add(OP_ERR_CAT_APPLICATION);
        s_allOpMetrics.add(OP_ERR_CAT_SYSTEM);
        s_allOpMetrics.add(OP_ERR_CAT_REQUEST);

        s_allOpMetrics.add(OP_ERR_FAILED_CALLS);
        s_allOpMetrics.add(OP_ERR_UNEXPECTED);
        s_allOpMetrics.add(OP_ERR_SEVERITY_ERROR);
        s_allOpMetrics.add(OP_ERR_SEVERITY_WARNING);

        s_allOpMetrics.add(OP_TIME_START_DESER);
        s_allOpMetrics.add(OP_TIME_START_REQ_PIPE);
        s_allOpMetrics.add(OP_TIME_START_CALL);
        s_allOpMetrics.add(OP_TIME_START_RESP_PIPE);
        s_allOpMetrics.add(OP_TIME_START_SER);

        s_matchingStartTimeOpMetrics.put(OP_TIME_CALL, OP_TIME_START_CALL);
        s_matchingStartTimeOpMetrics.put(OP_TIME_PIPELINE_REQUEST, OP_TIME_START_REQ_PIPE);
        s_matchingStartTimeOpMetrics.put(OP_TIME_PIPELINE_RESPONSE, OP_TIME_START_RESP_PIPE);
        s_matchingStartTimeOpMetrics.put(OP_TIME_SERIALIZATION, OP_TIME_START_SER);
        s_matchingStartTimeOpMetrics.put(OP_TIME_DESERIALIZATION, OP_TIME_START_DESER);

        // make lists immutable
        s_allSvcMetrics = Collections.unmodifiableList(s_allSvcMetrics);
        s_allOpMetrics = Collections.unmodifiableList(s_allOpMetrics);

        s_matchingStartTimeOpMetrics = Collections.unmodifiableMap(s_matchingStartTimeOpMetrics);
    }

    private SystemMetricDefs() {
        // no instances
    }

    public static class SvcLevelMetricDef extends MetricDef {
        SvcLevelMetricDef(String nameSuffix, MetricCategory category, Class<? extends MetricValue> valueClass) {
            this(nameSuffix, category, valueClass, MonitoringLevel.NORMAL);
        }

        SvcLevelMetricDef(String nameSuffix, MetricCategory category, Class<? extends MetricValue> valueClass,
                MonitoringLevel level) {
            super("SoaFwk." + nameSuffix, MetricDef.SVC_APPLY_TO_ALL, MetricDef.OP_DONT_CARE, level, category, valueClass);
        }
    }

    public static class OpLevelMetricDef extends MetricDef {
        private final SvcLevelMetricDef m_svcDef;

        OpLevelMetricDef(String nameSuffix, SvcLevelMetricDef svcDef, MetricCategory category,
                Class<? extends MetricValue> valueClass) {
            this(nameSuffix, svcDef, category, valueClass, MonitoringLevel.NORMAL);
        }

        OpLevelMetricDef(String nameSuffix, SvcLevelMetricDef svcDef, MetricCategory category,
                Class<? extends MetricValue> valueClass, MonitoringLevel level) {
            super("SoaFwk.Op." + nameSuffix, MetricDef.SVC_APPLY_TO_ALL, MetricDef.OP_APPLY_TO_ALL, level, category, valueClass);
            m_svcDef = svcDef;
        }

        public final SvcLevelMetricDef getSvcDef() {
            return m_svcDef;
        }
    }

    private static class SvcLevelTimingMetricDef extends SvcLevelMetricDef {
        final String m_nameSuffix;

        SvcLevelTimingMetricDef(String nameSuffix) {
            this(nameSuffix, MonitoringLevel.NORMAL);
        }

        SvcLevelTimingMetricDef(String nameSuffix, MonitoringLevel level) {
            super("Time." + nameSuffix, MetricCategory.Timing, AverageMetricValue.class, level);
            m_nameSuffix = nameSuffix;
        }
    }

    public static class SvcLevelErrorMetricDef extends SvcLevelMetricDef {
        final String m_nameSuffix;

        public SvcLevelErrorMetricDef(String nameSuffix) {
            super("Err." + nameSuffix, MetricCategory.Error, LongSumMetricValue.class);
            m_nameSuffix = nameSuffix;
        }
    }

    private static class OpLevelTimingMetricDef extends OpLevelMetricDef {
        OpLevelTimingMetricDef(SvcLevelMetricDef svcDef) {
            this(svcDef, MonitoringLevel.NORMAL);
        }

        OpLevelTimingMetricDef(SvcLevelMetricDef svcDef, MonitoringLevel level) {
            super("Time." + ((SvcLevelTimingMetricDef) svcDef).m_nameSuffix, svcDef, MetricCategory.Timing,
                    AverageMetricValue.class, level);
        }
    }

    public static class OpLevelErrorMetricDef extends OpLevelMetricDef {
        public OpLevelErrorMetricDef(SvcLevelMetricDef svcDef) {
            super("Err." + ((SvcLevelErrorMetricDef) svcDef).m_nameSuffix, svcDef, MetricCategory.Error,
                    LongSumMetricValue.class);
        }
    }
}
